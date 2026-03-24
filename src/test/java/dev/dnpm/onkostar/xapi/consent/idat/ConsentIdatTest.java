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
