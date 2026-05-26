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

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Patient;
import de.itc.onkostar.api.Procedure;
import dev.dnpm.onkostar.xapi.molgen.model.BiomarkerElement;
import dev.dnpm.onkostar.xapi.molgen.model.Molekulargenuntersuchung;
import dev.dnpm.onkostar.xapi.molgen.model.OsMolekulargenetik;
import dev.dnpm.onkostar.xapi.security.DelegatingDataBasedPermissionEvaluator;
import dev.dnpm.onkostar.xapi.security.PermissionType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MolGenController {

  private static final Logger log = LoggerFactory.getLogger(MolGenController.class);

  private final IOnkostarApi onkostarApi;
  private final DelegatingDataBasedPermissionEvaluator permissionEvaluator;

  public MolGenController(
      final IOnkostarApi onkostarApi,
      final DelegatingDataBasedPermissionEvaluator permissionEvaluator) {
    this.onkostarApi = onkostarApi;
    this.permissionEvaluator = permissionEvaluator;
  }

  @PutMapping("/x-api/patient/{pid}/molgen/{hnummer}")
  public ResponseEntity<Response> saveMolGenData(
      @PathVariable String pid,
      @PathVariable String hnummer,
      @RequestBody OsMolekulargenetik content) {
    if (null == pid || null == hnummer || null == content) {
      return ResponseEntity.badRequest().body(new Response(false));
    }

    if (!pid.equals(content.getPatientID()) || !hnummer.equals(content.getEinsendenummer())) {
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(false));
    }

    // Since UMR has no unique PID format and Patho sends without leading zeros,
    // try to get all available Patients
    var patients = findPatientWithSimilarPid(pid);

    if (patients.size() != 1) {
      // No patients or to many patients found
      return ResponseEntity.notFound().build();
    }

    // Unique patient found
    var patient = patients.get(0);

    var procedures =
        onkostarApi
            .getProceduresForPatientByForm(patient.getId(), "OS.Molekulargenetik", null)
            .stream()
            .filter(
                p -> {
                  var einsendenummer = p.getValue("Einsendenummer");
                  if (null == einsendenummer || einsendenummer.getString().isBlank()) {
                    return false;
                  }
                  return hnummer.equals(einsendenummer.getString());
                })
            .collect(Collectors.toList());

    if (procedures.size() > 1) {
      // Multiple existing procedure
      return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response(false));
    }

    var response = new Response(true);

    var procedure = new Procedure(onkostarApi);
    procedure.setFormName("OS.Molekulargenetik");

    // Use existing procedure if only one exists
    if (procedures.size() == 1) {
      procedure = procedures.get(0);
    }

    procedure.setPatient(patient);

    procedure.setStartDate(content.getDatum());
    procedure.setValue("Datum", new Item("Datum", content.getDatum()));

    if (!permissionEvaluator.hasPermission(
        SecurityContextHolder.getContext().getAuthentication(),
        procedure,
        PermissionType.READ_WRITE)) {
      log.warn("No permission to write Broad Consent for: '{}'", patient.getId());
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    procedure.setValue("Dokumentation", new Item("Dokumentation", "ERW"));
    procedure.setValue("Einsendenummer", new Item("Einsendenummer", content.getEinsendenummer()));

    var analyseMethoden = procedure.getValue("AnalyseMethoden");
    var newAnalyseMethoden = new ArrayList<String>();
    if (null != analyseMethoden) {
      List.of(analyseMethoden.getString().split(","))
          .forEach(value -> newAnalyseMethoden.add(value.trim()));
    }
    if (null != content.getMolekulargenuntersuchung() && !newAnalyseMethoden.contains("S")) {
      newAnalyseMethoden.add("S");
    }
    if (null != content.getBiomarker() && !newAnalyseMethoden.contains("KOMPBIO")) {
      newAnalyseMethoden.add("KOMPBIO");
    }
    procedure.setValue(
        "AnalyseMethoden", new Item("AnalyseMethoden", String.join(",", newAnalyseMethoden)));
    procedure.setValue("ReferenzGenom", new Item("ReferenzGenom", content.getReferenzgenom()));

    // Varianten
    var variantResponse = this.updateVariants(procedure, content, patient);
    response.setAddedVariants(variantResponse.getAddedVariants());
    response.setUpdatedVariants(variantResponse.getUpdatedVariants());
    response.setRemovedVariants(variantResponse.getRemovedVariants());

    // Komplexe Biomarker
    var biomarkerResponse = this.updateBiomarkers(procedure, content, patient);
    response.setAddedBiomarkers(biomarkerResponse.getAddedBiomarkers());
    response.setUpdatedBiomarkers(biomarkerResponse.getUpdatedBiomarkers());
    response.setRemovedBiomarkers(biomarkerResponse.getRemovedBiomarkers());

    try {
      onkostarApi.saveProcedure(procedure, false);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(false));
    }

    return ResponseEntity.ok().body(response);
  }

  private Response updateVariants(
      Procedure procedure, OsMolekulargenetik content, Patient patient) {
    var response = new Response(true);

    if (null != procedure.getSubProceduresMap().get("MolekulargenetischeUntersuchung")) {
      final var existingProcedures =
          procedure.getSubProceduresMap().get("MolekulargenetischeUntersuchung");

      final var existingProcedureKeys =
          existingProcedures.stream()
              .map(MolGenUtils::getSubProcedureKey)
              .collect(Collectors.toList());

      final var newEntryKeys =
          mapVariants(content, patient).stream()
              .map(MolGenUtils::getSubProcedureKey)
              .collect(Collectors.toList());

      final var proceduresToRemove =
          existingProcedures.stream()
              .filter(p -> !newEntryKeys.contains(MolGenUtils.getSubProcedureKey(p)))
              .collect(Collectors.toList());
      for (var p : proceduresToRemove) {
        // TODO: Remove procedure if sources are correct
        // procedure.removeSubProcedure("MolekulargenetischeUntersuchung", p);
      }
      response.setRemovedVariants(0); // proceduresToRemove.size();

      final var proceduresToUpdate =
          mapVariants(content, patient).stream()
              .filter(p -> existingProcedureKeys.contains(MolGenUtils.getSubProcedureKey(p)))
              .collect(Collectors.toList());

      for (var p : proceduresToUpdate) {
        for (var existingProcedure : existingProcedures) {
          if (!MolGenUtils.getSubProcedureKey(p)
              .equals(MolGenUtils.getSubProcedureKey(existingProcedure))) {
            continue;
          }
          if (!MolGenUtils.getSubProcedureHash(p)
              .equals(MolGenUtils.getSubProcedureHash(existingProcedure))) {
            MolGenUtils.patchProcedure(existingProcedure, p);
            response.setUpdatedVariants(response.getUpdatedVariants() + 1);
          }
        }
      }

      final var proceduresToAdd =
          mapVariants(content, patient).stream()
              .filter(p -> !existingProcedureKeys.contains(MolGenUtils.getSubProcedureKey(p)))
              .collect(Collectors.toList());
      for (var p : proceduresToAdd) {
        procedure.addSubProcedure("MolekulargenetischeUntersuchung", p);
      }
      response.setAddedVariants(proceduresToAdd.size());
    } else {
      for (var p : mapVariants(content, patient)) {
        procedure.addSubProcedure("MolekulargenetischeUntersuchung", p);
      }
      response.setAddedVariants(mapVariants(content, patient).size());
    }

    return response;
  }

  private Response updateBiomarkers(
      Procedure procedure, OsMolekulargenetik content, Patient patient) {
    var response = new Response(true);

    if (null != procedure.getSubProceduresMap().get("KomplexeBiomarker")) {
      final var existingProcedures = procedure.getSubProceduresMap().get("KomplexeBiomarker");

      final var existingProcedureKeys =
          existingProcedures.stream()
              .map(MolGenUtils::getSubProcedureKey)
              .collect(Collectors.toList());

      final var newEntryKeys =
          mapBiomarkers(content, patient).stream()
              .map(MolGenUtils::getSubProcedureKey)
              .collect(Collectors.toList());

      final var proceduresToRemove =
          existingProcedures.stream()
              .filter(p -> !newEntryKeys.contains(MolGenUtils.getSubProcedureKey(p)))
              .collect(Collectors.toList());
      for (var p : proceduresToRemove) {
        // TODO: Remove procedure if sources are correct
        // procedure.removeSubProcedure("KomplexeBiomarker", p);
      }
      response.setRemovedBiomarkers(0); // proceduresToRemove.size();

      final var proceduresToUpdate =
          mapBiomarkers(content, patient).stream()
              .filter(p -> existingProcedureKeys.contains(MolGenUtils.getSubProcedureKey(p)))
              .collect(Collectors.toList());

      for (var p : proceduresToUpdate) {
        for (var existingProcedure : existingProcedures) {
          if (!MolGenUtils.getSubProcedureKey(p)
              .equals(MolGenUtils.getSubProcedureKey(existingProcedure))) {
            continue;
          }

          log.info(
              "Comparing procedures:\n\t{}\n\t{}",
              MolGenUtils.getSubProcedureHash(p),
              MolGenUtils.getSubProcedureHash(existingProcedure));
          if (!MolGenUtils.getSubProcedureHash(p)
              .equals(MolGenUtils.getSubProcedureHash(existingProcedure))) {
            MolGenUtils.patchProcedure(existingProcedure, p);
            response.setUpdatedBiomarkers(response.getUpdatedBiomarkers() + 1);
          }
        }
      }

      final var proceduresToAdd =
          mapBiomarkers(content, patient).stream()
              .filter(p -> !existingProcedureKeys.contains(MolGenUtils.getSubProcedureKey(p)))
              .collect(Collectors.toList());
      for (var p : proceduresToAdd) {
        procedure.addSubProcedure("KomplexeBiomarker", p);
      }
      response.setAddedBiomarkers(proceduresToAdd.size());
    } else {
      for (var p : mapBiomarkers(content, patient)) {
        procedure.addSubProcedure("KomplexeBiomarker", p);
      }
      response.setAddedBiomarkers(mapBiomarkers(content, patient).size());
    }

    return response;
  }

  private List<Patient> findPatientWithSimilarPid(String pid) {
    var result = new ArrayList<Patient>();
    for (int i = 5; i >= 0; i--) {
      var leadingZeros = "0".repeat(i);
      var patient = onkostarApi.getPatient(String.format("%s%s", leadingZeros, pid));
      if (null != patient) {
        result.add(patient);
      }
    }
    return result;
  }

  private List<Procedure> mapVariants(OsMolekulargenetik content, Patient patient) {
    if (null == content.getMolekulargenuntersuchung()) {
      return List.of();
    }

    return content.getMolekulargenuntersuchung().stream()
        .map(mapVariant())
        .map(
            p -> {
              p.setPatient(patient);
              return p;
            })
        .collect(Collectors.toList());
  }

  private Function<Molekulargenuntersuchung, Procedure> mapVariant() {
    return variant -> {
      var procedure = new Procedure(onkostarApi);
      procedure.setFormName("OS.Molekulargenetische Untersuchung");
      procedure.setValue("Dokumentation", new Item("Dokumentation", "ERW"));

      procedure.setValue("Ergebnis", new Item("Ergebnis", variant.getErgebnis()));
      procedure.setValue("Untersucht", new Item("Untersucht", variant.getUntersucht()));
      procedure.setValue(
          "Pathogenitaetsklasse",
          new Item("Pathogenitaetsklasse", variant.getPathogenitaetsklasse()));

      // SV
      procedure.setValue("Genomposition", new Item("Genomposition", variant.getGenomposition()));
      procedure.setValue(
          "cDNANomenklatur", new Item("cDNANomenklatur", variant.getCdnanomenklatur()));
      procedure.setValue(
          "ProteinebeneNomenklatur",
          new Item("ProteinebeneNomenklatur", variant.getProteinebenenomenklatur()));
      procedure.setValue("EVChromosom", new Item("EVChromosom", variant.getEvchromosom()));
      procedure.setValue("EVENSEMBLID", new Item("EVENSEMBLID", variant.getEvensemblid()));
      procedure.setValue("EVHGNCID", new Item("EVHGNCID", variant.getEvhgncid()));
      procedure.setValue("EVHGNCSymbol", new Item("EVHGNCSymbol", variant.getEvhgncsymbol()));
      procedure.setValue("EVHGNCName", new Item("EVHGNCName", variant.getEvhgncname()));
      procedure.setValue("EVStart", new Item("EVStart", variant.getEvstart()));
      procedure.setValue("EVEnde", new Item("EVEnde", variant.getEvende()));

      procedure.setValue(
          "EVAltNucleotide", new Item("EVAltNucleotide", variant.getEvaltnucleotide()));
      procedure.setValue(
          "EVRefNucleotide", new Item("EVRefNucleotide", variant.getEvrefnucleotide()));
      procedure.setValue("EVReadDepth", new Item("EVReadDepth", variant.getEvreaddepth()));
      procedure.setValue("Allelfrequenz", new Item("Allelfrequenz", variant.getAllelfrequenz()));
      procedure.setValue("EVdbSNPID", new Item("EVdbSNPID", variant.getEvdbsnpid()));

      // CNV
      procedure.setValue(
          "CopyNumberVariation", new Item("CopyNumberVariation", variant.getCopynumbervariation()));
      procedure.setValue("CNVChromosom", new Item("CNVChromosom", variant.getCnvchromosom()));
      procedure.setValue("CNVENSEMBLID", new Item("CNENSEMBLID", variant.getCnvensemblid()));
      procedure.setValue("CNVHGNCID", new Item("CNVHGNCID", variant.getCnvhgncid()));
      procedure.setValue("CNVHGNCSymbol", new Item("CNVHGNCSymbol", variant.getCnvhgncsymbol()));
      procedure.setValue("CNVHGNCName", new Item("CNVHGNCName", variant.getCnvhgncname()));
      procedure.setValue(
          "CNVTotalCNDouble", new Item("CNVTotalCNDouble", variant.getCnvtotalcndouble()));

      return procedure;
    };
  }

  private List<Procedure> mapBiomarkers(OsMolekulargenetik content, Patient patient) {
    if (null == content.getBiomarker()) {
      return List.of();
    }

    return content.getBiomarker().stream()
        .map(mapBiomarker())
        .map(
            p -> {
              p.setPatient(patient);
              return p;
            })
        .collect(Collectors.toList());
  }

  private Function<BiomarkerElement, Procedure> mapBiomarker() {
    return biomarker -> {
      var procedure = new Procedure(onkostarApi);
      procedure.setFormName("OS.MolGen Komplexe Biomarker");
      procedure.setValue(
          "KomplexerBiomarker", new Item("KomplexerBiomarker", biomarker.getKomplexerbiomarker()));

      var seqprozentwert = biomarker.getSeqprozentwert();
      if (null != seqprozentwert) {
        procedure.setValue("SeqProzentwert", new Item("SeqProzentwert", seqprozentwert));
        procedure.setValue("AnalyseMethoden", new Item("AnalyseMethoden", "S"));
      }

      var score = biomarker.getScore();
      if (null != score) {
        procedure.setValue("KomplMarkerScore", new Item("Score", seqprozentwert));
      }

      var tumormutationalburden = biomarker.getTumormutationalburden();
      if (null != tumormutationalburden) {
        procedure.setValue(
            "TumorMutationalBurden", new Item("TumorMutationalBurden", tumormutationalburden));
      }

      return procedure;
    };
  }
}
