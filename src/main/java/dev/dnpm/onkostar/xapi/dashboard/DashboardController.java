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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

  private final Logger log = LoggerFactory.getLogger(DashboardController.class);

  private final DashboardService dashboardService;

  public DashboardController(final DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping("/x-api/mv-dashboard")
  public List<DashboardEntry> getDashboard() {
    var usedPids = new ArrayList<Integer>();

    final var kpa =
        dashboardService.findKlinikAnamneseWithCaseId().stream()
            .map(
                procedure -> {
                  final var caseId = procedure.getValue("FallnummerMV");
                  if (null == caseId) {
                    return null;
                  }
                  final var date = procedure.getValue("AnmeldedatumMTB");
                  if (null == date) {
                    return null;
                  }

                  final var patient = procedure.getPatient();
                  final var diseases = procedure.getDiseases();

                  usedPids.add(patient.getId());

                  final var carePlans =
                      dashboardService.getCarePlans(patient.getId(), procedure.getId());

                  final var builder =
                      DashboardEntry.builder()
                          .caseId(caseId.getString())
                          .guid(Base64Utils.encodeToString(procedure.getGuid()))
                          .deceased(null != procedure.getPatient().getDeathdate())
                          .deceasedAtFirstMtb(
                              dashboardService.patientDeceasedAtFirstMtb(patient, carePlans))
                          .mtb(
                              DashboardEntry.Mtb.builder()
                                  .registrationDate(date.getString())
                                  .carePlans(carePlans)
                                  .build())
                          .mvConsent(dashboardService.getMvConsent(patient.getId()))
                          .broadConsent(dashboardService.getBroadConsent(patient.getId()));

                  if (null != diseases && diseases.size() == 1) {
                    final var disease = diseases.get(0);
                    builder
                        .clinicalSubmission(dashboardService.getClinicalSubmission(disease))
                        .genomicSubmission(dashboardService.getGenomicSubmission(disease));
                  }

                  return builder.build();
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    kpa.addAll(
        dashboardService.findMvConsent().stream()
            .filter(procedure -> !usedPids.contains(procedure.getPatient().getId()))
            .map(
                procedure ->
                    DashboardEntry.builder()
                        .caseId(
                            String.format(
                                "!%s",
                                Sha512DigestUtils.shaHex(procedure.getGuid()).substring(0, 7)))
                        .guid(Base64Utils.encodeToString(procedure.getGuid()))
                        .deceased(procedure.getPatient().getDeathdate() != null)
                        .mvConsent(dashboardService.getMvConsent(procedure.getPatient().getId()))
                        .broadConsent(
                            dashboardService.getBroadConsent(procedure.getPatient().getId()))
                        .build())
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));

    return kpa;
  }
}
