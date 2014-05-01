package com.monead.semantic.workbench.tree;

import java.util.UUID;

/**
 * Wraps an ontology component for use by the OntologyTreeCellRenderer.
 * 
 * Subclasses of this wrapper are used by the SemanticWorkbench
 * tree view and the OntologyTreeCellRenderer in order for the
 * correct tree node icon to be chosen when rendering a value
 * as a node in the tree.
 * 
 * Copyright (C) 2010-2014 David S. Read
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author David Read
 * 
 */
public abstract class Wrapper {
  /**
   * The unique id for this wrapped component
   */
  private UUID uuid;

  /**
   * The local name of the wrapped component (e.g. without namespace)
   */
  private String localName;

  /**
   * The full URI for the wrapped component
   */
  private String uri;

  /**
   * Display the URI when getting the string representation of this component
   */
  private boolean showUri;

  /**
   * Accepts the node's local name and full uri.
   * 
   * @param pLocalName
   *          The local name of the ontology's URI component
   * @param pUri
   *          The URI of the ontology component
   */
  public Wrapper(String pLocalName, String pUri) {
    this(pLocalName, pUri, true);
  }

  /**
   * Accepts the node's local name and full uri.
   * 
   * @param pLocalName
   *          The local name of the ontology's URI component
   * @param pUri
   *          The URI of the ontology component
   * @param pShowUri
   *          Whether to show the URI in the string representation of the
   *          component
   */
  public Wrapper(String pLocalName, String pUri, boolean pShowUri) {
    uuid = UUID.randomUUID();

    localName = pLocalName;
    uri = pUri;
    showUri = pShowUri;
  }

  /**
   * Get this instance's UUID.
   * 
   * @return The instance's UUID
   */
  public UUID getUuid() {
    return uuid;
  }

  /**
   * Get the local name portion of the ontology's URI component.
   * 
   * @return The local name
   */
  public String getLocalName() {
    return localName;
  }

  /**
   * Get the URI of the ontology component.
   * 
   * @return The URI
   */
  public String getUri() {
    return uri;
  }

  /**
   * Output the local name and append the URI
   * 
   * @return The local name with appended URI in parentheses
   */
  public String toString() {
    return getLocalName() + (showUri ? " (" + getUri() + ")" : "");
  }
}
