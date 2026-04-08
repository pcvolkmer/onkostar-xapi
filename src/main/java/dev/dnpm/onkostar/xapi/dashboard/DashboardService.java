/*
 * This file is part of onkostar-plugin-xapi
 *
 * Copyright (C) 2026 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.dnpm.onkostar.xapi.dashboard;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.filter.DataOperator;
import de.itc.onkostar.api.filter.IProcedureFilter;
import de.itc.onkostar.api.filter.IProcedureFilterVisitor;
import de.itc.onkostar.api.filter.ProcedureDataFilter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service("xapiDashboardService")
public class DashboardService {

  private final Logger log = LoggerFactory.getLogger(DashboardService.class);

  private final IOnkostarApi onkostarApi;
  private final JdbcTemplate jdbcTemplate;

  public DashboardService(IOnkostarApi onkostarApi, final DataSource dataSource) {
    this.onkostarApi = onkostarApi;
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public List<Procedure> findKlinikAnamneseWithCaseId() {
    final var sql =
        "SELECT prozedur.id FROM dk_dnpm_kpa JOIN prozedur ON (prozedur.id = dk_dnpm_kpa.id) WHERE geloescht = 0 AND fallnummermv IS NOT NULL AND anmeldedatummtb IS NOT NULL;";
    final var ids = jdbcTemplate.queryForList(sql, Integer.class);

    return ids.stream()
        .map(onkostarApi::getProcedure)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  public DashboardEntry.MvConsent getMvConsent(int patientId) {
    try {
      final var procedures =
          onkostarApi.getProceduresForPatientByForm(patientId, "DNPM ConsentMV", null);

      if (!procedures.isEmpty()) {
        final var procedure = procedures.get(0);
        final var date = procedure.getValue("date");
        final var sequencing = procedure.getValue("sequencing");
        final var caseIdentification = procedure.getValue("caseidentification");
        final var reIdentification = procedure.getValue("reidentification");

        var builder = DashboardEntry.MvConsent.builder();
        if (null != date && null != date.getDate()) {
          builder.consentDate(date.getDate());
        } else {
          return null;
        }
        builder.sequencing("permit".equals(sequencing.getString()));
        builder.caseIdentification("permit".equals(caseIdentification.getString()));
        builder.reIdentification("permit".equals(reIdentification.getString()));

        return builder.build();
      }

    } catch (Exception e) {
      log.error("Error processing MvConsent for patient {}: {}", patientId, e.getMessage());
      return null;
    }

    log.warn("No MvConsent found for patient {}", patientId);
    return null;
  }

  public DashboardEntry.BroadConsent getBroadConsent(int patientId) {
    try {
      final var procedures =
          onkostarApi.getProceduresForPatientByForm(patientId, "DNPM ConsentMV", null);

      if (!procedures.isEmpty()) {
        final var procedure = procedures.get(0);
        final var date = procedure.getValue("ebroadconsentdate");
        final var electronicAvailable = procedure.getValue("ebroadconsentpresent");

        var builder = DashboardEntry.BroadConsent.builder();
        if (null != date && null != date.getDate()) {
          builder.consentDate(date.getDate());
        } else {
          return null;
        }
        if (null != electronicAvailable) {
          builder.electronicAvailable(electronicAvailable.getBoolean());
        }

        return builder.build();
      }

    } catch (Exception e) {
      log.error("Error processing BroadConsent for patient {}: {}", patientId, e.getMessage());
      return null;
    }

    log.warn("No BroadConsent found for patient {}", patientId);
    return null;
  }

  public List<DashboardEntry.CarePlan> getCarePlans(int patientId, int kpaId) {
    return onkostarApi
        .getProceduresForPatientByForm(
            patientId,
            "DNPM Therapieplan",
            new IProcedureFilter() {
              @Override
              public <T> T accept(IProcedureFilterVisitor<T> iProcedureFilterVisitor) {
                return iProcedureFilterVisitor.visitProcedureDataFilter(
                    new ProcedureDataFilter("refdnpmklinikanamnese", kpaId, DataOperator.EQUALS));
              }
            })
        .stream()
        .filter(Objects::nonNull)
        .filter(
            procedure ->
                procedure.getValue("datum") != null
                    && procedure.getValue("datum").getDate() != null)
        .map(
            procedure ->
                DashboardEntry.CarePlan.builder()
                    .date(procedure.getValue("datum").getDate())
                    .build())
        .sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate()))
        .collect(Collectors.toList());
  }

  public DashboardEntry.Submission getClinicalSubmission(Disease disease) {
    return getSubmission(disease, "KDK");
  }

  public DashboardEntry.Submission getGenomicSubmission(Disease disease) {
    return getSubmission(disease, "GRZ");
  }

  private DashboardEntry.Submission getSubmission(Disease disease, String formFieldSuffix) {
    try {
      final var procedure =
          onkostarApi.getLastProcedureForDiseaseByForm(
              disease.getId(),
              "DNPM Vorgangsnummern",
              new IProcedureFilter() {
                @Override
                public <T> T accept(IProcedureFilterVisitor<T> iProcedureFilterVisitor) {
                  return iProcedureFilterVisitor.visitProcedureDataFilter(
                      new ProcedureDataFilter(
                          "Datum" + formFieldSuffix, null, DataOperator.ISNOTNULLOREMPTY));
                }
              });

      if (null == procedure) {
        return null;
      }

      return DashboardEntry.Submission.builder()
          .id(procedure.getValue("ID" + formFieldSuffix).getString())
          .date(procedure.getValue("Datum" + formFieldSuffix).getDate())
          .tan(procedure.getValue("Vorgangsnummer" + formFieldSuffix).getString())
          .build();
    } catch (Exception e) {
      log.error("Error processing Submission for patient {}", disease.getPatient().getId(), e);
      return null;
    }
  }
}
