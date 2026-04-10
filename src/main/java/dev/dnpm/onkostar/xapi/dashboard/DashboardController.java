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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    var kpa = dashboardService.findKlinikAnamneseWithCaseId();
    return kpa.stream()
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

              final var builder =
                  DashboardEntry.builder()
                      .caseId(caseId.getString())
                      .guid(Base64Utils.encodeToString(procedure.getGuid()))
                      .mtb(
                          DashboardEntry.Mtb.builder()
                              .registrationDate(date.getDate())
                              .carePlans(
                                  dashboardService.getCarePlans(patient.getId(), procedure.getId()))
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
  }
}
