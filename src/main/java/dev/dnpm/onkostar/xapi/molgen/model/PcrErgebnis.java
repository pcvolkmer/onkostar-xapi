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
