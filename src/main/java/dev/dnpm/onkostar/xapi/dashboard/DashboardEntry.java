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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DashboardEntry {
  private final String caseId;
  private final Mtb mtb;
  private final MvConsent mvConsent;
  private final BroadConsent broadConsent;
  private final Submission clinicalSubmission;
  private final Submission genomicSubmission;

  @Data
  @Builder
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class Mtb {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final Date registrationDate;

    private final List<CarePlan> carePlans;
  }

  @Data
  @Builder
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class MvConsent {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final Date consentDate;

    private final boolean sequencing;
    private final boolean caseIdentification;
    private final boolean reIdentification;
  }

  @Data
  @Builder
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class BroadConsent {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final Date consentDate;

    private final boolean electronicAvailable;
  }

  @Data
  @Builder
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class CarePlan {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final Date date;
  }

  @Data
  @Builder
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public static class Submission {
    private final String id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final Date date;

    private final String tan;
  }
}
