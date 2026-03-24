package dev.dnpm.onkostar.xapi.consent.idat;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConsentIdatTest {

  ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    this.objectMapper = new ObjectMapper();
  }

  @Test
  void shouldDeserializeJson() throws Exception {
    final var json =
        Objects.requireNonNull(
                this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("consent/genom-de_consent.json"))
            .readAllBytes();

    final var consentObj = objectMapper.readValue(json, ConsentIdat.class);

    assertThat(consentObj)
        .isInstanceOf(ConsentIdat.class)
        .satisfies(
            consent -> {
              assertThat(consent.getConsentKey())
                  .satisfies(
                      consentKey -> {
                        assertThat(consentKey.getConsentTemplateKey())
                            .isInstanceOf(ConsentTemplateKey.class)
                            .satisfies(
                                consentTemplateKey -> {
                                  assertThat(consentTemplateKey.getDomainName())
                                      .isEqualTo("GenomDE_MV");
                                  assertThat(consentTemplateKey.getVersion()).isEqualTo("1.1");
                                });
                        assertThat(consentKey.getSignerIds())
                            .hasAtLeastOneElementOfType(SignerId.class)
                            .contains(new SignerId("Patienten-ID", "12345678", 0));
                        assertThat(consentKey.getConsentDate()).isEqualTo("2026-03-17T12:00:00Z");
                        assertThat(consentKey.getConsentDate()).isEqualTo("2026-03-17T12:00:00Z");
                      });
            });
  }
}
