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

package dev.dnpm.onkostar.xapi.molgen.model;

import com.fasterxml.jackson.annotation.*;
import java.util.Date;
import java.util.List;

public class OsMolekulargenetik {
  private List<BiomarkerElement> biomarker;
  private Date datum;
  private String einsendenummer;
  private List<Molekulargenuntersuchung> molekulargenuntersuchung;
  private String patientID;
  private String referenzgenom;

  @JsonProperty("biomarker")
  public List<BiomarkerElement> getBiomarker() {
    return biomarker;
  }

  @JsonProperty("biomarker")
  public void setBiomarker(List<BiomarkerElement> value) {
    this.biomarker = value;
  }

  @JsonProperty("datum")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  public Date getDatum() {
    return datum;
  }

  @JsonProperty("datum")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  public void setDatum(Date value) {
    this.datum = value;
  }

  @JsonProperty("einsendenummer")
  public String getEinsendenummer() {
    return einsendenummer;
  }

  @JsonProperty("einsendenummer")
  public void setEinsendenummer(String value) {
    this.einsendenummer = value;
  }

  @JsonProperty("molekulargenuntersuchung")
  public List<Molekulargenuntersuchung> getMolekulargenuntersuchung() {
    return molekulargenuntersuchung;
  }

  @JsonProperty("molekulargenuntersuchung")
  public void setMolekulargenuntersuchung(List<Molekulargenuntersuchung> value) {
    this.molekulargenuntersuchung = value;
  }

  @JsonProperty("patient_id")
  public String getPatientID() {
    return patientID;
  }

  @JsonProperty("patient_id")
  public void setPatientID(String value) {
    this.patientID = value;
  }

  @JsonProperty("referenzgenom")
  public String getReferenzgenom() {
    return referenzgenom;
  }

  @JsonProperty("referenzgenom")
  public void setReferenzgenom(String value) {
    this.referenzgenom = value;
  }
}
