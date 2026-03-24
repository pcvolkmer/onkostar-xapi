package dev.dnpm.onkostar.xapi.consent.idat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsentTemplateKey {
  private String domainName;
  private String version;
}
