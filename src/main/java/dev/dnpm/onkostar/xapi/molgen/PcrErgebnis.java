package dev.dnpm.onkostar.xapi.molgen;

import com.fasterxml.jackson.annotation.*;
import java.io.IOException;

public enum PcrErgebnis {
  H,
  L,
  MSS;

  @JsonValue
  public String toValue() {
    switch (this) {
      case H:
        return "H";
      case L:
        return "L";
      case MSS:
        return "MSS";
    }
    return null;
  }

  @JsonCreator
  public static PcrErgebnis forValue(String value) throws IOException {
    if (value.equals("H")) return H;
    if (value.equals("L")) return L;
    if (value.equals("MSS")) return MSS;
    throw new IOException("Cannot deserialize PcrErgebnis");
  }
}
