package dev.dnpm.onkostar.xapi.molgen;

import com.fasterxml.jackson.annotation.*;
import java.io.IOException;

public enum ImmunergebnisMsi {
  MMD,
  MMP;

  @JsonValue
  public String toValue() {
    switch (this) {
      case MMD:
        return "MMD";
      case MMP:
        return "MMP";
    }
    return null;
  }

  @JsonCreator
  public static ImmunergebnisMsi forValue(String value) throws IOException {
    if (value.equals("MMD")) return MMD;
    if (value.equals("MMP")) return MMP;
    throw new IOException("Cannot deserialize ImmunergebnisMsi");
  }
}
