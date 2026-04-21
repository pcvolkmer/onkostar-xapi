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

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DashboardEntry {
  private final String caseId;
  private final String guid;
  private final boolean deceased;
  private final boolean deceasedAtFirstMtb;
  private final Mtb mtb;
  private final MvConsent mvConsent;
  private final BroadConsent broadConsent;
  private final Submission clinicalSubmission;
  private final Submission genomicSubmission;

  private final String nextFollowUpDue;

  @Data
  @Builder
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class Mtb {
    private final String registrationDate;
    private final List<CarePlan> carePlans;
  }

  @Data
  @Builder
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class MvConsent {
    private final String consentDate;
    private final boolean sequencing;
    private final boolean caseIdentification;
    private final boolean reIdentification;
  }

  @Data
  @Builder
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class BroadConsent {
    private final String consentDate;
    private final boolean electronicAvailable;
  }

  @Data
  @Builder
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class CarePlan {
    private final String date;
  }

  @Data
  @Builder
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class Submission {
    private final String id;
    private final String date;
    private final String tan;
    private final String sequencingType;
  }
}
