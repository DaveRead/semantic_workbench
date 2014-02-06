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
  private UUID uuid;
  
  private String localName;
  private String uri;

  /**
   * Accepts the node's local name and full uri.
   * 
   * @param localName
   *          The local name of the ontology's URI component
   * @param uri
   *          The URI of the ontology component
   */
  public Wrapper(String localName, String uri) {
    uuid = UUID.randomUUID();
    
    this.localName = localName;
    this.uri = uri;
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
   */
  public String toString() {
    return getLocalName() + " (" + getUri() + ")";
  }
}
