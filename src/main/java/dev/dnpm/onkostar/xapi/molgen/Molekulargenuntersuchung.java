package dev.dnpm.onkostar.xapi.molgen;

import com.fasterxml.jackson.annotation.*;

public class Molekulargenuntersuchung {
  private Double allelfrequenz;
  private String cdnanomenklatur;
  private Ergebnis ergebnis;
  private String evaltnucleotide;
  private String evchromosom;
  private String evdbsnpid;
  private Long evende;
  private String evensemblid;
  private String evhgncid;
  private String evhgncname;
  private String evhgncsymbol;
  private Long evreaddepth;
  private String evrefnucleotide;
  private Long evstart;
  private String genomposition;
  private String pathogenitaetsklasse;
  private String proteinebenenomenklatur;
  private String untersucht;
  private String cnvchromosom;
  private String cnvensemblid;
  private String cnvhgncid;
  private String cnvhgncname;
  private String cnvhgncsymbol;
  private Double cnvtotalcndouble;
  private String copynumbervariation;
  private String fusioniertesgen;
  private String fusionrna3Ensemblid;
  private String fusionrna3Exonid;
  private String fusionrna3Hgncid;
  private String fusionrna3Hgncname;
  private String fusionrna3Hgncsymbol;
  private String fusionrna3Strand;
  private String fusionrna3Transcriptid;
  private Long fusionrna3Transposition;
  private String fusionrna5Ensemblid;
  private String fusionrna5Exonid;
  private String fusionrna5Hgncid;
  private String fusionrna5Hgncname;
  private String fusionrna5Hgncsymbol;
  private String fusionrna5Strand;
  private String fusionrna5Transcriptid;
  private Long fusionrna5Transposition;
  private Long fusionrnareportednumread;

  @JsonProperty("allelfrequenz")
  public Double getAllelfrequenz() {
    return allelfrequenz;
  }

  @JsonProperty("allelfrequenz")
  public void setAllelfrequenz(Double value) {
    this.allelfrequenz = value;
  }

  @JsonProperty("cdnanomenklatur")
  public String getCdnanomenklatur() {
    return cdnanomenklatur;
  }

  @JsonProperty("cdnanomenklatur")
  public void setCdnanomenklatur(String value) {
    this.cdnanomenklatur = value;
  }

  @JsonProperty("ergebnis")
  public Ergebnis getErgebnis() {
    return ergebnis;
  }

  @JsonProperty("ergebnis")
  public void setErgebnis(Ergebnis value) {
    this.ergebnis = value;
  }

  @JsonProperty("evaltnucleotide")
  public String getEvaltnucleotide() {
    return evaltnucleotide;
  }

  @JsonProperty("evaltnucleotide")
  public void setEvaltnucleotide(String value) {
    this.evaltnucleotide = value;
  }

  @JsonProperty("evchromosom")
  public String getEvchromosom() {
    return evchromosom;
  }

  @JsonProperty("evchromosom")
  public void setEvchromosom(String value) {
    this.evchromosom = value;
  }

  @JsonProperty("evdbsnpid")
  public String getEvdbsnpid() {
    return evdbsnpid;
  }

  @JsonProperty("evdbsnpid")
  public void setEvdbsnpid(String value) {
    this.evdbsnpid = value;
  }

  @JsonProperty("evende")
  public Long getEvende() {
    return evende;
  }

  @JsonProperty("evende")
  public void setEvende(Long value) {
    this.evende = value;
  }

  @JsonProperty("evensemblid")
  public String getEvensemblid() {
    return evensemblid;
  }

  @JsonProperty("evensemblid")
  public void setEvensemblid(String value) {
    this.evensemblid = value;
  }

  @JsonProperty("evhgncid")
  public String getEvhgncid() {
    return evhgncid;
  }

  @JsonProperty("evhgncid")
  public void setEvhgncid(String value) {
    this.evhgncid = value;
  }

  @JsonProperty("evhgncname")
  public String getEvhgncname() {
    return evhgncname;
  }

  @JsonProperty("evhgncname")
  public void setEvhgncname(String value) {
    this.evhgncname = value;
  }

  @JsonProperty("evhgncsymbol")
  public String getEvhgncsymbol() {
    return evhgncsymbol;
  }

  @JsonProperty("evhgncsymbol")
  public void setEvhgncsymbol(String value) {
    this.evhgncsymbol = value;
  }

  @JsonProperty("evreaddepth")
  public Long getEvreaddepth() {
    return evreaddepth;
  }

  @JsonProperty("evreaddepth")
  public void setEvreaddepth(Long value) {
    this.evreaddepth = value;
  }

  @JsonProperty("evrefnucleotide")
  public String getEvrefnucleotide() {
    return evrefnucleotide;
  }

  @JsonProperty("evrefnucleotide")
  public void setEvrefnucleotide(String value) {
    this.evrefnucleotide = value;
  }

  @JsonProperty("evstart")
  public Long getEvstart() {
    return evstart;
  }

  @JsonProperty("evstart")
  public void setEvstart(Long value) {
    this.evstart = value;
  }

  @JsonProperty("genomposition")
  public String getGenomposition() {
    return genomposition;
  }

  @JsonProperty("genomposition")
  public void setGenomposition(String value) {
    this.genomposition = value;
  }

  @JsonProperty("pathogenitaetsklasse")
  public String getPathogenitaetsklasse() {
    return pathogenitaetsklasse;
  }

  @JsonProperty("pathogenitaetsklasse")
  public void setPathogenitaetsklasse(String value) {
    this.pathogenitaetsklasse = value;
  }

  @JsonProperty("proteinebenenomenklatur")
  public String getProteinebenenomenklatur() {
    return proteinebenenomenklatur;
  }

  @JsonProperty("proteinebenenomenklatur")
  public void setProteinebenenomenklatur(String value) {
    this.proteinebenenomenklatur = value;
  }

  @JsonProperty("untersucht")
  public String getUntersucht() {
    return untersucht;
  }

  @JsonProperty("untersucht")
  public void setUntersucht(String value) {
    this.untersucht = value;
  }

  @JsonProperty("cnvchromosom")
  public String getCnvchromosom() {
    return cnvchromosom;
  }

  @JsonProperty("cnvchromosom")
  public void setCnvchromosom(String value) {
    this.cnvchromosom = value;
  }

  @JsonProperty("cnvensemblid")
  public String getCnvensemblid() {
    return cnvensemblid;
  }

  @JsonProperty("cnvensemblid")
  public void setCnvensemblid(String value) {
    this.cnvensemblid = value;
  }

  @JsonProperty("cnvhgncid")
  public String getCnvhgncid() {
    return cnvhgncid;
  }

  @JsonProperty("cnvhgncid")
  public void setCnvhgncid(String value) {
    this.cnvhgncid = value;
  }

  @JsonProperty("cnvhgncname")
  public String getCnvhgncname() {
    return cnvhgncname;
  }

  @JsonProperty("cnvhgncname")
  public void setCnvhgncname(String value) {
    this.cnvhgncname = value;
  }

  @JsonProperty("cnvhgncsymbol")
  public String getCnvhgncsymbol() {
    return cnvhgncsymbol;
  }

  @JsonProperty("cnvhgncsymbol")
  public void setCnvhgncsymbol(String value) {
    this.cnvhgncsymbol = value;
  }

  @JsonProperty("cnvtotalcndouble")
  public Double getCnvtotalcndouble() {
    return cnvtotalcndouble;
  }

  @JsonProperty("cnvtotalcndouble")
  public void setCnvtotalcndouble(Double value) {
    this.cnvtotalcndouble = value;
  }

  @JsonProperty("copynumbervariation")
  public String getCopynumbervariation() {
    return copynumbervariation;
  }

  @JsonProperty("copynumbervariation")
  public void setCopynumbervariation(String value) {
    this.copynumbervariation = value;
  }

  @JsonProperty("fusioniertesgen")
  public String getFusioniertesgen() {
    return fusioniertesgen;
  }

  @JsonProperty("fusioniertesgen")
  public void setFusioniertesgen(String value) {
    this.fusioniertesgen = value;
  }

  @JsonProperty("fusionrna3ensemblid")
  public String getFusionrna3Ensemblid() {
    return fusionrna3Ensemblid;
  }

  @JsonProperty("fusionrna3ensemblid")
  public void setFusionrna3Ensemblid(String value) {
    this.fusionrna3Ensemblid = value;
  }

  @JsonProperty("fusionrna3exonid")
  public String getFusionrna3Exonid() {
    return fusionrna3Exonid;
  }

  @JsonProperty("fusionrna3exonid")
  public void setFusionrna3Exonid(String value) {
    this.fusionrna3Exonid = value;
  }

  @JsonProperty("fusionrna3hgncid")
  public String getFusionrna3Hgncid() {
    return fusionrna3Hgncid;
  }

  @JsonProperty("fusionrna3hgncid")
  public void setFusionrna3Hgncid(String value) {
    this.fusionrna3Hgncid = value;
  }

  @JsonProperty("fusionrna3hgncname")
  public String getFusionrna3Hgncname() {
    return fusionrna3Hgncname;
  }

  @JsonProperty("fusionrna3hgncname")
  public void setFusionrna3Hgncname(String value) {
    this.fusionrna3Hgncname = value;
  }

  @JsonProperty("fusionrna3hgncsymbol")
  public String getFusionrna3Hgncsymbol() {
    return fusionrna3Hgncsymbol;
  }

  @JsonProperty("fusionrna3hgncsymbol")
  public void setFusionrna3Hgncsymbol(String value) {
    this.fusionrna3Hgncsymbol = value;
  }

  @JsonProperty("fusionrna3strand")
  public String getFusionrna3Strand() {
    return fusionrna3Strand;
  }

  @JsonProperty("fusionrna3strand")
  public void setFusionrna3Strand(String value) {
    this.fusionrna3Strand = value;
  }

  @JsonProperty("fusionrna3transcriptid")
  public String getFusionrna3Transcriptid() {
    return fusionrna3Transcriptid;
  }

  @JsonProperty("fusionrna3transcriptid")
  public void setFusionrna3Transcriptid(String value) {
    this.fusionrna3Transcriptid = value;
  }

  @JsonProperty("fusionrna3transposition")
  public Long getFusionrna3Transposition() {
    return fusionrna3Transposition;
  }

  @JsonProperty("fusionrna3transposition")
  public void setFusionrna3Transposition(Long value) {
    this.fusionrna3Transposition = value;
  }

  @JsonProperty("fusionrna5ensemblid")
  public String getFusionrna5Ensemblid() {
    return fusionrna5Ensemblid;
  }

  @JsonProperty("fusionrna5ensemblid")
  public void setFusionrna5Ensemblid(String value) {
    this.fusionrna5Ensemblid = value;
  }

  @JsonProperty("fusionrna5exonid")
  public String getFusionrna5Exonid() {
    return fusionrna5Exonid;
  }

  @JsonProperty("fusionrna5exonid")
  public void setFusionrna5Exonid(String value) {
    this.fusionrna5Exonid = value;
  }

  @JsonProperty("fusionrna5hgncid")
  public String getFusionrna5Hgncid() {
    return fusionrna5Hgncid;
  }

  @JsonProperty("fusionrna5hgncid")
  public void setFusionrna5Hgncid(String value) {
    this.fusionrna5Hgncid = value;
  }

  @JsonProperty("fusionrna5hgncname")
  public String getFusionrna5Hgncname() {
    return fusionrna5Hgncname;
  }

  @JsonProperty("fusionrna5hgncname")
  public void setFusionrna5Hgncname(String value) {
    this.fusionrna5Hgncname = value;
  }

  @JsonProperty("fusionrna5hgncsymbol")
  public String getFusionrna5Hgncsymbol() {
    return fusionrna5Hgncsymbol;
  }

  @JsonProperty("fusionrna5hgncsymbol")
  public void setFusionrna5Hgncsymbol(String value) {
    this.fusionrna5Hgncsymbol = value;
  }

  @JsonProperty("fusionrna5strand")
  public String getFusionrna5Strand() {
    return fusionrna5Strand;
  }

  @JsonProperty("fusionrna5strand")
  public void setFusionrna5Strand(String value) {
    this.fusionrna5Strand = value;
  }

  @JsonProperty("fusionrna5transcriptid")
  public String getFusionrna5Transcriptid() {
    return fusionrna5Transcriptid;
  }

  @JsonProperty("fusionrna5transcriptid")
  public void setFusionrna5Transcriptid(String value) {
    this.fusionrna5Transcriptid = value;
  }

  @JsonProperty("fusionrna5transposition")
  public Long getFusionrna5Transposition() {
    return fusionrna5Transposition;
  }

  @JsonProperty("fusionrna5transposition")
  public void setFusionrna5Transposition(Long value) {
    this.fusionrna5Transposition = value;
  }

  @JsonProperty("fusionrnareportednumread")
  public Long getFusionrnareportednumread() {
    return fusionrnareportednumread;
  }

  @JsonProperty("fusionrnareportednumread")
  public void setFusionrnareportednumread(Long value) {
    this.fusionrnareportednumread = value;
  }
}
