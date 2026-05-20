package dev.dnpm.onkostar.xapi.molgen;

import com.fasterxml.jackson.annotation.*;
import java.io.IOException;

public enum Ergebnis {
  CNV,
  F,
  P;

  @JsonValue
  public String toValue() {
    switch (this) {
      case CNV:
        return "CNV";
      case F:
        return "F";
      case P:
        return "P";
    }
    return null;
  }

  @JsonCreator
  public static Ergebnis forValue(String value) throws IOException {
    if (value.equals("CNV")) return CNV;
    if (value.equals("F")) return F;
    if (value.equals("P")) return P;
    throw new IOException("Cannot deserialize Ergebnis");
  }
}
