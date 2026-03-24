package dev.dnpm.onkostar.xapi.consent.idat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsentIdat {
  private ConsentKey consentKey;
  private List<PolicyState> currentPolicyStates;
}
