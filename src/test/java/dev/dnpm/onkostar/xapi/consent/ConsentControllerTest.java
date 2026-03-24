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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Patient;
import de.itc.onkostar.api.Procedure;
import dev.dnpm.onkostar.xapi.security.DelegatingDataBasedPermissionEvaluator;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ConsentControllerTest {

  private IOnkostarApi onkostarApi;
  private DelegatingDataBasedPermissionEvaluator permissionEvaluator;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp(
      @Mock IOnkostarApi onkostarApi,
      @Mock DelegatingDataBasedPermissionEvaluator permissionEvaluator) {
    this.onkostarApi = onkostarApi;
    this.permissionEvaluator = permissionEvaluator;
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(
                new ConsentController(this.onkostarApi, this.permissionEvaluator))
            .build();
  }

  @ParameterizedTest
  @CsvSource({
    "consent/genom-de_consent.json, /x-api/patient/12345678/consent/mv64e",
    "consent/mii_consent.json, /x-api/patient/12345678/consent/research"
  })
  void testShouldSaveNewConsent(final String consentFile, final String consentUrl)
      throws Exception {
    var patient = new Patient(this.onkostarApi);
    patient.setId(1);
    patient.setPatientId("12345678");
    when(onkostarApi.getPatient(anyString())).thenReturn(patient);

    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);

    var consent =
        Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(consentFile))
            .readAllBytes();

    this.mockMvc
        .perform(put(consentUrl).contentType("application/json").content(consent))
        .andExpect(status().isAccepted());

    var captor = ArgumentCaptor.forClass(Procedure.class);
    verify(onkostarApi, times(1)).saveProcedure(captor.capture(), eq(false));
    assertThat(captor.getValue().getId()).isNull();
  }

  @ParameterizedTest
  @CsvSource({
    "consent/genom-de_consent.json, /x-api/patient/12345678/consent/mv64e",
    "consent/mii_consent.json, /x-api/patient/12345678/consent/research"
  })
  void testShouldUpdateExistingConsent(final String consentFile, final String consentUrl)
      throws Exception {
    var patient = new Patient(this.onkostarApi);
    patient.setId(1);
    patient.setPatientId("12345678");
    when(onkostarApi.getPatient(anyString())).thenReturn(patient);

    var procedure = new Procedure(this.onkostarApi);
    procedure.setId(42);
    when(onkostarApi.getProceduresForPatientByForm(eq(1), eq("DNPM ConsentMV"), any()))
        .thenReturn(List.of(procedure));

    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(true);

    var consent =
        Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(consentFile))
            .readAllBytes();

    this.mockMvc
        .perform(put(consentUrl).contentType("application/json").content(consent))
        .andExpect(status().isAccepted());

    var captor = ArgumentCaptor.forClass(Procedure.class);
    verify(onkostarApi, times(1)).saveProcedure(captor.capture(), eq(false));
    assertThat(captor.getValue().getId()).isEqualTo(42);
  }

  @ParameterizedTest
  @CsvSource({
    "consent/genom-de_consent.json, /x-api/patient/12345678/consent/mv64e",
    "consent/mii_consent.json, /x-api/patient/12345678/consent/research"
  })
  void testShouldIgnoreUnknownPatient(final String consentFile, final String consentUrl)
      throws Exception {
    when(onkostarApi.getPatient(anyString())).thenReturn(null);

    var consent =
        Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(consentFile))
            .readAllBytes();

    this.mockMvc
        .perform(put(consentUrl).contentType("application/json").content(consent))
        .andExpect(status().isNotFound());

    verify(onkostarApi, times(0)).saveProcedure(any(), eq(false));
  }

  @ParameterizedTest
  @CsvSource({
    "consent/genom-de_consent.json, /x-api/patient/11112222/consent/mv64e",
    "consent/mii_consent.json, /x-api/patient/11112222/consent/research"
  })
  void testShouldIgnoreConsentIfPatientDoesNotMatch(
      final String consentFile, final String consentUrl) throws Exception {
    var patient = new Patient(this.onkostarApi);
    patient.setId(1);
    patient.setPatientId("11112222");
    when(onkostarApi.getPatient(anyString())).thenReturn(patient);

    var procedure = new Procedure(this.onkostarApi);
    procedure.setId(42);
    when(onkostarApi.getProceduresForPatientByForm(eq(1), eq("DNPM ConsentMV"), any()))
        .thenReturn(List.of(procedure));

    var consent =
        Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(consentFile))
            .readAllBytes();

    this.mockMvc
        .perform(put(consentUrl).contentType("application/json").content(consent))
        .andExpect(status().isUnprocessableEntity());

    verify(onkostarApi, times(0)).saveProcedure(any(), eq(false));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/x-api/patient/12345678/consent/mv64e",
        "/x-api/patient/12345678/consent/research"
      })
  void testShouldIgnoreNonConsentIdatPayload(final String consentUrl) throws Exception {
    var patient = new Patient(this.onkostarApi);
    patient.setId(1);
    patient.setPatientId("12345678");
    when(onkostarApi.getPatient(anyString())).thenReturn(patient);

    this.mockMvc
        .perform(put(consentUrl).contentType("application/json").content("{\"value\": \"test\"}"))
        .andExpect(status().isUnprocessableEntity());

    verify(onkostarApi, times(0)).saveProcedure(any(), eq(false));
  }

  @ParameterizedTest
  @CsvSource({
    "consent/genom-de_consent.json, /x-api/patient/12345678/consent/mv64e",
    "consent/mii_consent.json, /x-api/patient/12345678/consent/research"
  })
  void testShouldNotSaveConsentIfNoPermission(final String consentFile, final String consentUrl)
      throws Exception {
    var patient = new Patient(this.onkostarApi);
    patient.setId(1);
    patient.setPatientId("12345678");
    when(onkostarApi.getPatient(anyString())).thenReturn(patient);

    when(permissionEvaluator.hasPermission(any(), any(), any())).thenReturn(false);

    var consent =
        Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(consentFile))
            .readAllBytes();

    this.mockMvc
        .perform(put(consentUrl).contentType("application/json").content(consent))
        .andExpect(status().isForbidden());

    verify(onkostarApi, times(0)).saveProcedure(any(), eq(false));
  }
}
