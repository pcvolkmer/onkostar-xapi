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

package dev.dnpm.onkostar.xapi.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import de.itc.onkostar.api.Disease;
import de.itc.onkostar.api.IOnkostarApi;
import de.itc.onkostar.api.Item;
import de.itc.onkostar.api.Procedure;
import de.itc.onkostar.api.filter.IProcedureFilter;
import java.sql.Date;
import java.time.LocalDate;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

  private IOnkostarApi onkostarApi;
  private DataSource dataSource;

  private DashboardService dashboardService;

  @BeforeEach
  void setup(@Mock IOnkostarApi onkostarApi, @Mock DataSource dataSource) {
    this.onkostarApi = onkostarApi;
    this.dataSource = dataSource;
    dashboardService = new DashboardService(onkostarApi, dataSource);
  }

  public static Stream<Arguments> sequencingTypeProvider() {
    return Stream.of(
        Arguments.of(
            "IBE+A123456789+A123456789&20240701001&260530103&KDKK00001&0&O&9&1&C&0&1+9+96d85ebcc840627e9ebd4d54a433356d7adc66f4e5441922037d07a1bb0c4e8e",
            "Keine"),
        Arguments.of(
            "IBE+A123456789+A123456789&20240701001&260530103&KDKK00001&0&O&9&1&C&1&1+9+510cc272030ca1a78a9efaecfab9a337a632f53cd74695199ee2e79dd33b335e",
            "WGS"),
        Arguments.of(
            "IBE+A123456789+A123456789&20240701001&260530103&KDKK00001&0&O&9&1&C&2&1+9+bad8a31b1759b565bee3d283e68af38e173499bfcce2f50691e7eddda62b2f31",
            "WES"),
        Arguments.of(
            "IBE+A123456789+A123456789&20240701001&260530103&KDKK00001&0&O&9&1&C&3&1+9+a658ed563923cade2d27c05e92e1654975288dca8d2dc6f04c482396680e49e9",
            "Panel"),
        Arguments.of(
            "IBE+A123456789+A123456789&20240701001&260530103&KDKK00001&0&O&9&1&C&4&1+9+0135ea8402926c7213e40c44b93cdc294b82b1de3d5ce7cfee22f80ea18f8c59",
            "WGS/LR"));
  }

  @ParameterizedTest
  @MethodSource("sequencingTypeProvider")
  void shouldExtractSequencingType(String meldebestaetigung, String expectedSequencingType) {
    String actualSequencingType = dashboardService.extractSequencingType(meldebestaetigung);
    assertThat(actualSequencingType).isEqualTo(expectedSequencingType);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "IBE+A123456789+A123456789&20240701001&260530103&KDKK00001&0&O&9&1&C&4&1+9+badbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadb",
        "IBE+A123456789+A123456789&20240701001&260530103&KDKK00001&0&O&9&1&C&4&1+9+badbadbad"
      })
  void shouldNotBeValidMeldebestaetigung(String meldebestaetigung) {
    assertThat(dashboardService.isValid(meldebestaetigung)).isFalse();
  }

  @Test
  void shouldReturnKDKSubmission() {
    final var procedure = new Procedure(this.onkostarApi);
    procedure.setId(1);
    procedure.setValue("IDKDK", new Item("", "A123456789"));
    procedure.setValue("DatumKDK", new Item("", "2026-04-15"));
    procedure.setValue(
        "VorgangsnummerKDK",
        new Item("", "bad8a31b1759b565bee3d283e68af38e173499bfcce2f50691e7eddda62b2f31"));
    procedure.setValue(
        "MeldebestaetigungKDK",
        new Item(
            "",
            "IBE+A123456789+A123456789&20240701001&260530103&KDKK00001&0&O&9&1&C&0&1+9+96d85ebcc840627e9ebd4d54a433356d7adc66f4e5441922037d07a1bb0c4e8e"));

    when(this.onkostarApi.getLastProcedureForDiseaseByForm(
            anyInt(), anyString(), any(IProcedureFilter.class)))
        .thenReturn(procedure);

    final var disease = new Disease(this.onkostarApi);
    disease.setId(1);
    disease.setPatientId(1);

    final var actual = dashboardService.getClinicalSubmission(disease);

    assertThat(actual).isNotNull();
  }

  @Test
  void shouldNotReturnKDKSubmissionIfInvalidMeldebestaetigung() {
    final var procedure = new Procedure(this.onkostarApi);
    procedure.setId(1);
    procedure.setValue("IDKDK", new Item("", "A123456789"));
    procedure.setValue("DatumKDK", new Item("", "2026-04-15"));
    procedure.setValue(
        "VorgangsnummerKDK",
        new Item("", "bad8a31b1759b565bee3d283e68af38e173499bfcce2f50691e7eddda62b2f31"));
    procedure.setValue(
        "MeldebestaetigungKDK",
        new Item(
            "",
            "IBE+A123456789+A123456789&20240701001&260530103&KDKK00001&0&O&9&1&C&0&1+9+badbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadb"));

    when(this.onkostarApi.getLastProcedureForDiseaseByForm(
            anyInt(), anyString(), any(IProcedureFilter.class)))
        .thenReturn(procedure);

    final var disease = new Disease(this.onkostarApi);
    disease.setId(1);
    disease.setPatientId(1);

    final var actual = dashboardService.getClinicalSubmission(disease);

    assertThat(actual).isNull();
  }

  @Test
  void shouldReturnGRZSubmission() {
    final var procedure = new Procedure(this.onkostarApi);
    procedure.setId(1);
    procedure.setValue("IDGRZ", new Item("", "A123456789"));
    procedure.setValue("DatumGRZ", new Item("", "2026-04-15"));
    procedure.setValue(
        "VorgangsnummerGRZ",
        new Item("", "bad8a31b1759b565bee3d283e68af38e173499bfcce2f50691e7eddda62b2f31"));
    procedure.setValue(
        "MeldebestaetigungGRZ",
        new Item(
            "",
            "IBE+A123456789+A123456789&20240701001&260530103&GRZK00001&0&O&9&1&C&0&1+9+51c8344e97ff881f2c261b8d23235601aac7f53bc819f2b1de1d162fdd79ed21"));

    when(this.onkostarApi.getLastProcedureForDiseaseByForm(
            anyInt(), anyString(), any(IProcedureFilter.class)))
        .thenReturn(procedure);

    final var disease = new Disease(this.onkostarApi);
    disease.setId(1);
    disease.setPatientId(1);

    final var actual = dashboardService.getGenomicSubmission(disease);

    assertThat(actual).isNotNull();
  }

  @Test
  void shouldNotReturnGRZSubmissionIfInvalidMeldebestaetigung() {
    final var procedure = new Procedure(this.onkostarApi);
    procedure.setId(1);
    procedure.setValue("IDGRZ", new Item("", "A123456789"));
    procedure.setValue("DatumGRZ", new Item("", "2026-04-15"));
    procedure.setValue(
        "VorgangsnummerGRZ",
        new Item("", "bad8a31b1759b565bee3d283e68af38e173499bfcce2f50691e7eddda62b2f31"));
    procedure.setValue(
        "MeldebestaetigungGRZ",
        new Item(
            "",
            "IBE+A123456789+A123456789&20240701001&260530103&GRZK00001&0&O&9&1&C&0&1+9+badbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadbadb"));

    when(this.onkostarApi.getLastProcedureForDiseaseByForm(
            anyInt(), anyString(), any(IProcedureFilter.class)))
        .thenReturn(procedure);

    final var disease = new Disease(this.onkostarApi);
    disease.setId(1);
    disease.setPatientId(1);

    final var actual = dashboardService.getGenomicSubmission(disease);

    assertThat(actual).isNull();
  }

  @ParameterizedTest
  @CsvSource({"HG19,S,false", "HG19,,true", ",S,true"})
  void shouldMapProcedureToFinding(
      String referenzGenom, String artDerSequenzierung, boolean valid) {
    final var procedure = new Procedure(this.onkostarApi);
    procedure.setId(1);
    procedure.setValue("Datum", new Item("", Date.valueOf(LocalDate.parse("2026-04-15"))));
    procedure.setValue("ReferenzGenom", new Item("", referenzGenom));
    procedure.setValue("ArtDerSequenzierung", new Item("", artDerSequenzierung));

    final var actual = dashboardService.mapFinding(procedure);

    assertThat(actual).isPresent();
    assertThat(actual.get().getDate()).isEqualTo("2026-04-15");
    assertThat(actual.get().isHasIssues()).isEqualTo(valid);
  }
}
