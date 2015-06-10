package com.monead.semantic.workbench.queries;

/**
 * File export formats supported for SPARQL results
 * 
 * <p>
 * Copyright: Copyright (c) 2015, David Read
 * </p>
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * </p>
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * </p>
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * </p>
 * <p>
 * </p>
 * 
 * @author David Read
 */
public enum ExportFormat {
  /**
   * Comma-separated value format
   */
  CSV("CSV", "comma-separated values", "csv"),
  /**
   * Javascript Object Notation format
   */
  JSON("JSON",
      "Javascript Object Notation", "js"),
  /**
   * Tab-separated value format
   */
  TSV("TSV", "tab-separated values", "txt");

  /**
   * The human-readable name for the format
   */
  private String formatName;

  /**
   * A longer description of the format
   */
  private String formatDescription;

  /**
   * The default file suffix for this format
   */
  private String defaultFileSuffix;

  /**
   * Create the enumerated value
   * 
   * @param name
   *          The name of the format
   * @param description
   *          The description of the format
   * @param suffix
   *          The default file suffix for this format
   */
  private ExportFormat(String name, String description, String suffix) {
    formatName = name;
    formatDescription = description;
    defaultFileSuffix = suffix;
  }

  /**
   * Get the human-readable format name
   * 
   * @return The format name
   */
  public String getFormatName() {
    return formatName;
  }

  /**
   * Get the long description of the format
   * 
   * @return The description of the format
   */
  public String getFormatDescription() {
    return formatDescription;
  }

  /**
   * Get the default file suffix to use for this format
   * 
   * @return The file suffix
   */
  public String getDefaultFileSuffix() {
    return defaultFileSuffix;
  }

  /**
   * Get a human-readable form of the format
   * 
   * @return A readable format name
   */
  public String toString() {
    return getFormatName();
  }
}
