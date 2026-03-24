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
