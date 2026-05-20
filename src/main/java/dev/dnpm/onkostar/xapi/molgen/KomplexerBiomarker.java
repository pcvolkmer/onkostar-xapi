package dev.dnpm.onkostar.xapi.molgen;

import com.fasterxml.jackson.annotation.*;
import java.io.IOException;

public enum KomplexerBiomarker {
  HRD,
  MSI,
  TMB;

  @JsonValue
  public String toValue() {
    switch (this) {
      case HRD:
        return "HRD";
      case MSI:
        return "MSI";
      case TMB:
        return "TMB";
    }
    return null;
  }

  @JsonCreator
  public static KomplexerBiomarker forValue(String value) throws IOException {
    if (value.equals("HRD")) return HRD;
    if (value.equals("MSI")) return MSI;
    if (value.equals("TMB")) return TMB;
    throw new IOException("Cannot deserialize KomplexerBiomarker");
  }
}
