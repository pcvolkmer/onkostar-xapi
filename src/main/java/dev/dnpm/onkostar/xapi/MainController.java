package dev.dnpm.onkostar.xapi;

import java.io.Serializable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/x-api")
public class MainController {

  @GetMapping
  public ResponseEntity<Info> getInfo() {
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(new Info());
  }

  @GetMapping("me")
  public String getUser(Authentication auth) {
    return auth.getName();
  }

  public static class Info implements Serializable {

    public String getDescription() {
      return "Extended API for Onkostar";
    }

    public String getVersion() {
      return "0.1.0"; // x-release-please
    }
  }
}
