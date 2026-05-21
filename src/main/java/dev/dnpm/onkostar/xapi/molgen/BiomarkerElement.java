package dev.dnpm.onkostar.xapi.molgen;

import com.fasterxml.jackson.annotation.*;

public class BiomarkerElement {
  private Bewertung bewertung;
  private KomplexerBiomarker komplexerbiomarker;
  private Double loh;
  private Double lst;
  private Double score;
  private Double tai;
  private ImmunergebnisMsi immunergebnismsi;
  private PcrErgebnis pcrergebnis;
  private Double seqprozentwert;
  private Double tumormutationalburden;

  @JsonProperty("bewertung")
  public Bewertung getBewertung() {
    return bewertung;
  }

  @JsonProperty("bewertung")
  public void setBewertung(Bewertung value) {
    this.bewertung = value;
  }

  @JsonProperty("komplexerbiomarker")
  public KomplexerBiomarker getKomplexerbiomarker() {
    return komplexerbiomarker;
  }

  @JsonProperty("komplexerbiomarker")
  public void setKomplexerbiomarker(KomplexerBiomarker value) {
    this.komplexerbiomarker = value;
  }

  @JsonProperty("loh")
  public Double getLoh() {
    return loh;
  }

  @JsonProperty("loh")
  public void setLoh(Double value) {
    this.loh = value;
  }

  @JsonProperty("lst")
  public Double getLst() {
    return lst;
  }

  @JsonProperty("lst")
  public void setLst(Double value) {
    this.lst = value;
  }

  @JsonProperty("score")
  public Double getScore() {
    return score;
  }

  @JsonProperty("score")
  public void setScore(Double value) {
    this.score = value;
  }

  @JsonProperty("tai")
  public Double getTai() {
    return tai;
  }

  @JsonProperty("tai")
  public void setTai(Double value) {
    this.tai = value;
  }

  @JsonProperty("immunergebnismsi")
  public ImmunergebnisMsi getImmunergebnismsi() {
    return immunergebnismsi;
  }

  @JsonProperty("immunergebnismsi")
  public void setImmunergebnismsi(ImmunergebnisMsi value) {
    this.immunergebnismsi = value;
  }

  @JsonProperty("pcrergebnis")
  public PcrErgebnis getPcrergebnis() {
    return pcrergebnis;
  }

  @JsonProperty("pcrergebnis")
  public void setPcrergebnis(PcrErgebnis value) {
    this.pcrergebnis = value;
  }

  @JsonProperty("seqprozentwert")
  public Double getSeqprozentwert() {
    return seqprozentwert;
  }

  @JsonProperty("seqprozentwert")
  public void setSeqprozentwert(Double value) {
    this.seqprozentwert = value;
  }

  @JsonProperty("tumormutationalburden")
  public Double getTumormutationalburden() {
    return tumormutationalburden;
  }

  @JsonProperty("tumormutationalburden")
  public void setTumormutationalburden(Double value) {
    this.tumormutationalburden = value;
  }
}
