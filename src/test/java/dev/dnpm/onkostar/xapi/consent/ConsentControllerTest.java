package dev.dnpm.onkostar.xapi.consent;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Patient;
import de.itc.onkostar.api.Procedure;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ConsentControllerTest {

  private IOnkostarApi onkostarApi;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp(@Mock IOnkostarApi onkostarApi) {
    this.onkostarApi = onkostarApi;
    this.mockMvc = MockMvcBuilders.standaloneSetup(new ConsentController(this.onkostarApi)).build();
  }

  @Test
  void testShouldSaveNewResearchConsent() throws Exception {
    var patient = new Patient(this.onkostarApi);
    patient.setId(1);
    patient.setPatientId("12345678");
    when(onkostarApi.getPatient(anyString())).thenReturn(patient);

    var consent =
        Objects.requireNonNull(
                this.getClass().getClassLoader().getResourceAsStream("consent/mii_consent.json"))
            .readAllBytes();

    this.mockMvc
        .perform(
            put("/x-api/patient/12345678/consent/research")
                .contentType("application/json")
                .content(consent))
        .andExpect(status().isAccepted());

    var captor = ArgumentCaptor.forClass(Procedure.class);
    verify(onkostarApi, times(1)).saveProcedure(captor.capture(), eq(false));
    assertThat(captor.getValue().getId()).isNull();
  }

  @Test
  void testShouldUpdateExistingResearchConsent() throws Exception {
    var patient = new Patient(this.onkostarApi);
    patient.setId(1);
    patient.setPatientId("12345678");
    when(onkostarApi.getPatient(anyString())).thenReturn(patient);

    var procedure = new Procedure(this.onkostarApi);
    procedure.setId(42);
    when(onkostarApi.getProceduresForPatientByForm(eq(1), eq("DNPM ConsentMV"), any()))
        .thenReturn(List.of(procedure));

    var consent =
        Objects.requireNonNull(
                this.getClass().getClassLoader().getResourceAsStream("consent/mii_consent.json"))
            .readAllBytes();

    this.mockMvc
        .perform(
            put("/x-api/patient/12345678/consent/research")
                .contentType("application/json")
                .content(consent))
        .andExpect(status().isAccepted());

    var captor = ArgumentCaptor.forClass(Procedure.class);
    verify(onkostarApi, times(1)).saveProcedure(captor.capture(), eq(false));
    assertThat(captor.getValue().getId()).isEqualTo(42);
  }

  @Test
  void testShouldSaveNewMvConsent() throws Exception {
    var patient = new Patient(this.onkostarApi);
    patient.setId(1);
    patient.setPatientId("12345678");
    when(onkostarApi.getPatient(anyString())).thenReturn(patient);

    var consent =
        Objects.requireNonNull(
                this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("consent/genom-de_consent.json"))
            .readAllBytes();

    this.mockMvc
        .perform(
            put("/x-api/patient/12345678/consent/mv64e")
                .contentType("application/json")
                .content(consent))
        .andExpect(status().isAccepted());

    var captor = ArgumentCaptor.forClass(Procedure.class);
    verify(onkostarApi, times(1)).saveProcedure(captor.capture(), eq(false));
    assertThat(captor.getValue().getId()).isNull();
  }

  @Test
  void testShouldUpdateExistingMvConsent() throws Exception {
    var patient = new Patient(this.onkostarApi);
    patient.setId(1);
    patient.setPatientId("12345678");
    when(onkostarApi.getPatient(anyString())).thenReturn(patient);

    var procedure = new Procedure(this.onkostarApi);
    procedure.setId(42);
    when(onkostarApi.getProceduresForPatientByForm(eq(1), eq("DNPM ConsentMV"), any()))
        .thenReturn(List.of(procedure));

    var consent =
        Objects.requireNonNull(
                this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("consent/genom-de_consent.json"))
            .readAllBytes();

    this.mockMvc
        .perform(
            put("/x-api/patient/12345678/consent/mv64e")
                .contentType("application/json")
                .content(consent))
        .andExpect(status().isAccepted());

    var captor = ArgumentCaptor.forClass(Procedure.class);
    verify(onkostarApi, times(1)).saveProcedure(captor.capture(), eq(false));
    assertThat(captor.getValue().getId()).isEqualTo(42);
  }
}
