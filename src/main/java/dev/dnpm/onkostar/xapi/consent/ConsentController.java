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

package dev.dnpm.onkostar.xapi.consent;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import dev.dnpm.onkostar.xapi.consent.idat.ConsentIdat;
import dev.dnpm.onkostar.xapi.security.DelegatingDataBasedPermissionEvaluator;
import dev.dnpm.onkostar.xapi.security.PermissionType;
import dev.pcvolkmer.mv64e.mtb.ConsentProvision;
import java.util.Objects;
import java.util.stream.Collectors;
import org.joda.time.LocalDate;
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
public class ConsentController {

  private static final Logger log = LoggerFactory.getLogger(ConsentController.class);

  private final IOnkostarApi onkostarApi;
  private final DelegatingDataBasedPermissionEvaluator permissionEvaluator;

  public ConsentController(
      final IOnkostarApi onkostarApi,
      final DelegatingDataBasedPermissionEvaluator permissionEvaluator) {
    this.onkostarApi = onkostarApi;
    this.permissionEvaluator = permissionEvaluator;
  }

  @PutMapping("/x-api/patient/{pid}/consent/research")
  public ResponseEntity<Void> putResearchConsent(
      @PathVariable("pid") String patientId, @RequestBody ConsentIdat consent) {
    final var patient = onkostarApi.getPatient(patientId);
    if (null == patient) {
      log.error("Patient not found: '{}'", patientId);
      return ResponseEntity.notFound().build();
    }

    // Patient ID

    if (null == consent.getConsentKey()
        || null == consent.getConsentKey().getSignerIds()
        || consent.getConsentKey().getSignerIds().isEmpty()
        || !patientId.equals(consent.getConsentKey().getSignerIds().get(0).getId())) {
      log.error("Unprocessable Entity: Patient ID not found or not equal to consent signer ID");
      return ResponseEntity.unprocessableEntity().build();
    }

    // Consent Date

    if (null == consent.getConsentKey().getConsentDate()) {
      log.error("Unprocessable Entity: MV Consent Date is null");
      return ResponseEntity.unprocessableEntity().build();
    }

    Procedure procedure = findFirstOrCreateNewConsentProcedure(patient.getId());

    procedure.setStartDate(consent.getConsentKey().getConsentDate());
    procedure.setValue(
        "ebroadconsentdate", new Item("date", consent.getConsentKey().getConsentDate()));
    procedure.setValue("ebroadconsentpresent", new Item("ebroadconsentpresent", true));

    if (!permissionEvaluator.hasPermission(
        SecurityContextHolder.getContext().getAuthentication(),
        procedure,
        PermissionType.READ_WRITE)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    try {
      final var newProcedure = null == procedure.getId();
      onkostarApi.saveProcedure(procedure, false);
      log.info("Broad Consent saved successfully");
      if (newProcedure) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
      }
      return ResponseEntity.accepted().build();
    } catch (Exception e) {
      log.error("Broad Consent not saved successfully", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PutMapping("/x-api/patient/{pid}/consent/mv64e")
  public ResponseEntity<Void> putMvConsent(
      @PathVariable("pid") String patientId, @RequestBody ConsentIdat consent) {
    final var patient = onkostarApi.getPatient(patientId);
    if (null == patient) {
      log.error("Patient not found: '{}'", patientId);
      return ResponseEntity.notFound().build();
    }

    // Patient ID

    if (null == consent.getConsentKey()
        || null == consent.getConsentKey().getSignerIds()
        || consent.getConsentKey().getSignerIds().isEmpty()
        || !patientId.equals(consent.getConsentKey().getSignerIds().get(0).getId())) {
      log.error("Unprocessable Entity: Patient ID not found or not equal to consent signer ID");
      return ResponseEntity.unprocessableEntity().build();
    }

    // Consent Date

    if (null == consent.getConsentKey().getConsentDate()) {
      log.error("Unprocessable Entity: MV Consent Date is null");
      return ResponseEntity.unprocessableEntity().build();
    }

    Procedure procedure = findFirstOrCreateNewConsentProcedure(patient.getId());

    procedure.setStartDate(consent.getConsentKey().getConsentDate());
    procedure.setValue("date", new Item("date", consent.getConsentKey().getConsentDate()));

    // Consent Provisions
    if (null == consent.getCurrentPolicyStates()
        || consent.getCurrentPolicyStates().isEmpty()
        || null == consent.getConsentKey().getConsentTemplateKey()) {
      log.error("Unprocessable Entity: MV Consent Provisions are null or empty");
      return ResponseEntity.unprocessableEntity().build();
    }
    updateProvisions(procedure, consent);

    final var existingVerlauf = procedure.getSubProceduresMap().get("Verlauf");

    // Remove old Verlauf entries from same day to avoid duplicates
    if (null != existingVerlauf) {
      final var entriesToRemove =
          existingVerlauf.stream()
              .filter(Objects::nonNull)
              .filter(
                  p -> {
                    final var existingDate = p.getValue("date");
                    // Ignore if date is null - keep entry
                    if (null == existingDate) {
                      return false;
                    }

                    var consentDate =
                        LocalDate.fromDateFields(consent.getConsentKey().getConsentDate())
                            .toDateTimeAtStartOfDay();
                    var existingConsentDate =
                        LocalDate.fromDateFields(existingDate.getDate()).toDateTimeAtStartOfDay();
                    // If date (by day) is equal
                    return consentDate.equals(existingConsentDate);
                  })
              .collect(Collectors.toList());
      for (var entry : entriesToRemove) {
        procedure.removeSubProcedure("Verlauf", entry);
      }
    }

    // Verlauf Einwilligung MV
    final var verlauf = new Procedure(onkostarApi);
    verlauf.setFormName("DNPM UF ConsentMV Verlauf");
    verlauf.setPatientId(patient.getId());
    verlauf.setStartDate(consent.getConsentKey().getConsentDate());
    verlauf.setValue("date", new Item("date", consent.getConsentKey().getConsentDate()));
    verlauf.setValue(
        "version",
        new Item("version", consent.getConsentKey().getConsentTemplateKey().getVersion()));
    updateProvisions(verlauf, consent);
    procedure.addSubProcedure("Verlauf", verlauf);

    if (!permissionEvaluator.hasPermission(
        SecurityContextHolder.getContext().getAuthentication(),
        procedure,
        PermissionType.READ_WRITE)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    try {
      final var newProcedure = null == procedure.getId();
      onkostarApi.saveProcedure(procedure, false);
      log.info("MV Consent saved successfully");
      if (newProcedure) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
      }
      return ResponseEntity.accepted().build();
    } catch (Exception e) {
      log.error("MV Consent not saved successfully", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  private Procedure findFirstOrCreateNewConsentProcedure(int patientId) {
    final var procedures =
        onkostarApi.getProceduresForPatientByForm(patientId, "DNPM ConsentMV", null);

    Procedure procedure;

    if (procedures.isEmpty()) {
      procedure = new Procedure(onkostarApi);
      procedure.setFormName("DNPM ConsentMV");
    } else {
      procedure = procedures.get(0);
    }

    procedure.setPatientId(patientId);

    return procedure;
  }

  private boolean updateProvisions(Procedure procedure, ConsentIdat consent) {
    // Consent Provisions

    if (null == consent.getCurrentPolicyStates() || consent.getCurrentPolicyStates().isEmpty()) {
      return false;
    }

    final var policyStates = consent.getCurrentPolicyStates();

    final var sequencing =
        policyStates.stream()
            .filter(
                policyState ->
                    "GenomDE_MV".equals(policyState.getKey().getDomainName())
                        && "sequencing".equals(policyState.getKey().getName()))
            .findFirst();
    sequencing.ifPresent(
        policyState ->
            procedure.setValue(
                "sequencing",
                new Item(
                    "sequencing",
                    policyState.getValue()
                        ? ConsentProvision.PERMIT.toValue()
                        : ConsentProvision.DENY.toValue())));

    final var caseIdentification =
        policyStates.stream()
            .filter(
                policyState ->
                    "GenomDE_MV".equals(policyState.getKey().getDomainName())
                        && "case-identification".equals(policyState.getKey().getName()))
            .findFirst();
    caseIdentification.ifPresent(
        policyState ->
            procedure.setValue(
                "caseidentification",
                new Item(
                    "caseidentification",
                    policyState.getValue()
                        ? ConsentProvision.PERMIT.toValue()
                        : ConsentProvision.DENY.toValue())));

    final var reidentification =
        policyStates.stream()
            .filter(
                policyState ->
                    "GenomDE_MV".equals(policyState.getKey().getDomainName())
                        && "reidentification".equals(policyState.getKey().getName()))
            .findFirst();
    reidentification.ifPresent(
        policyState ->
            procedure.setValue(
                "reidentification",
                new Item(
                    "reidentification",
                    policyState.getValue()
                        ? ConsentProvision.PERMIT.toValue()
                        : ConsentProvision.DENY.toValue())));

    return true;
  }
}
