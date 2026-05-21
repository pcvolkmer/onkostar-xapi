package dev.dnpm.onkostar.xapi.molgen;

import com.fasterxml.jackson.annotation.*;
import java.io.IOException;

public enum Bewertung {
  H,
  L,
  M,
  X;

  @JsonValue
  public String toValue() {
    switch (this) {
      case H:
        return "H";
      case L:
        return "L";
      case M:
        return "M";
      case X:
        return "X";
    }
    return null;
  }

  @JsonCreator
  public static Bewertung forValue(String value) throws IOException {
    if (value.equals("H")) return H;
    if (value.equals("L")) return L;
    if (value.equals("M")) return M;
    if (value.equals("X")) return X;
    throw new IOException("Cannot deserialize Bewertung");
  }
}
