package dev.dnpm.onkostar.xapi;

import de.itc.onkostar.api.IOnkostarApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExampleAnalyzerTest {

  private IOnkostarApi onkostarApi;

  @BeforeEach
  void setup(@Mock IOnkostarApi onkostarApi) {
    this.onkostarApi = onkostarApi;
  }

  @Test
  void testShouldTestSomeAnalyzerImplementation() {
    // Implement your first test
  }
}
