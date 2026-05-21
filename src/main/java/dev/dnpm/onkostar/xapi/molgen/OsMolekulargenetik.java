package dev.dnpm.onkostar.xapi.molgen;

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
