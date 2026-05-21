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

package dev.dnpm.onkostar.xapi.molgen;

import de.itc.onkostar.api.Procedure;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;

public class MolGenUtils {

  private static final List<String> KEY_FIELDS =
      List.of(
          // All variants
          "Ergebnis",
          "Untersucht",
          // Simple variants
          "ProteinebeneNomenklatur",
          "Allelfrequenz",
          // CNVs
          "CNVTotalCNDouble",
          // Biomarkers
          "KomplexerBiomarker");

  private static final List<String> PATCHABLE_FIELDS =
      List.of(
          // All variants
          "Dokumentation",
          "Ergebnis",
          "Untersucht",
          "Pathogenitaetsklasse",
          // Simple variants
          "Genomposition",
          "cDNANomenklatur",
          "ProteinebeneNomenklatur",
          "EVChromosom",
          "EVENSEMBLID",
          "EVHGNCID",
          "EVHGNCSymbol",
          "EVHGNCName",
          "EVStart",
          "EVEnde",
          "EVAltNucleotide",
          "EVRefNucleotide",
          "EVReadDepth",
          "EVdbSNPID",
          "Allelfrequenz",
          // CNVs
          "CopyNumberVariation",
          "CNVChromosom",
          "CNENSEMBLID",
          "CNVHGNCID",
          "CNVHGNCSymbol",
          "CNVHGNCName",
          "CNVTotalCNDouble",
          // Biomarkers
          "KomplexerBiomarker",
          "SeqProzentwert",
          "KomplMarkerScore",
          "TumorMutationalBurden");

  private MolGenUtils() {}

  /**
   * Generates a unique key for a sub-procedure based on identifying properties. The key is
   * generated using SHA-256 hashing to ensure uniqueness.
   *
   * @param procedure The sub-procedure for which to generate the key.
   * @return A unique key string for the sub-procedure.
   */
  public static String getSubProcedureKey(Procedure procedure) {
    var keyBuilder = new StringBuilder();
    for (var field : KEY_FIELDS) {
      var value = procedure.getValue(field);
      if (null != value) {
        keyBuilder.append(value.getString());
      }
    }

    return DigestUtils.sha256Hex(keyBuilder.toString());
  }

  /**
   * Generates a unique hash for a sub-procedure based on content properties. The hash is generated
   * using SHA-256 hashing to ensure uniqueness.
   *
   * @param procedure The sub-procedure for which to generate the hash.
   * @return A unique hash string for the sub-procedure.
   */
  public static String getSubProcedureHash(Procedure procedure) {
    var hashBuilder = new StringBuilder();
    for (var field : PATCHABLE_FIELDS) {
      final var value = procedure.getValue(field);
      if (null != value && !value.getString().isBlank()) {
        hashBuilder.append(field).append(":").append(value.getString());
      }
    }
    return DigestUtils.sha256Hex(hashBuilder.toString());
  }

  /**
   * Patches an existing procedure with new values from a new procedure. It keeps non-patchable
   * fields unchanged.
   *
   * @param existingProcedure The existing procedure to be patched.
   * @param newProcedure The new procedure containing updated values.
   */
  public static void patchProcedure(Procedure existingProcedure, final Procedure newProcedure) {
    for (var field : PATCHABLE_FIELDS) {
      var newValue = newProcedure.getValue(field);
      if (null != newValue && !newValue.getString().isBlank()) {
        existingProcedure.setValue(field, newValue);
      }
    }
  }
}
