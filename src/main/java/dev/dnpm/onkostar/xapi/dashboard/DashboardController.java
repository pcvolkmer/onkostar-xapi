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

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

  private final Logger log = LoggerFactory.getLogger(DashboardController.class);

  private final DashboardService dashboardService;

  public DashboardController(final DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @Scheduled(cron = "0 */5 * * * *")
  void refreshDashboardEntries() {
    this.dashboardService.evictDashboardEntriesCache();
    this.dashboardService.getDashboardEntries();
    log.info("Refreshed dashboard entries cache");
  }

  @GetMapping("/x-api/mv-dashboard")
  public List<DashboardEntry> getDashboardEntries() {
    return this.dashboardService.getDashboardEntries();
  }
}
