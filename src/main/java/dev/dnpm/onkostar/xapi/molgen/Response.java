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

package dev.dnpm.onkostar.xapi.molgen;

class Response {
  public boolean success;
  public int addedVariants;
  public int updatedVariants;
  public int removedVariants;
  public int addedBiomarkers;
  public int updatedBiomarkers;
  public int removedBiomarkers;

  public Response(boolean success) {
    this.success = success;
  }

  public Response(
      boolean success,
      int addedVariants,
      int updatedVariants,
      int removedVariants,
      int addedBiomarkers,
      int updatedBiomarkers,
      int removedBiomarkers) {
    this.success = success;
    this.addedVariants = addedVariants;
    this.updatedVariants = updatedVariants;
    this.removedVariants = removedVariants;
    this.addedBiomarkers = addedBiomarkers;
    this.updatedBiomarkers = updatedBiomarkers;
    this.removedBiomarkers = removedBiomarkers;
  }
}
