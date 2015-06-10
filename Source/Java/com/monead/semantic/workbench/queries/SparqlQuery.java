package com.monead.semantic.workbench.queries;

import java.io.Serializable;

/**
 * Title: Query
 * <p>
 * Description: Represents an individual SPARQL query. 
 * </p>
 * <p>
 * Copyright: Copyright (c) 2014, David Read
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
public class SparqlQuery implements Serializable {
  /**
   * Serial UID
   */
  private static final long serialVersionUID = 20140501;

  /**
   * The SPARQL statement
   */
  private String sparqlStatement;

  /**
   * Constructs a Query
   * 
   * Assumes the mode is a select statement (returns result set)
   * 
   * @param pSparqlStatement
   *          The SPARQL statement
   */
  public SparqlQuery(String pSparqlStatement) {
    setSparqlStatement(pSparqlStatement);
  }

  /**
   * Set the SPARQL statement for this query to wrap
   * 
   * @param pSparqlStatement
   *          The SPARQL statement
   */
  private void setSparqlStatement(String pSparqlStatement) {
    sparqlStatement = pSparqlStatement;
  }

  /**
   * Returns the SPARQL string
   * 
   * @return String THe SPARQL statement
   */
  public String getSparqlStatement() {
    return sparqlStatement;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof SparqlQuery) {
      return equals((SparqlQuery) object);
    }

    return false;
  }

  /**
   * Check whether this Query instance is equivalent to another. Equivalence is
   * true if the SPARQL statements are the same
   * 
   * @param query
   *          A query to compare to this query
   * 
   * @return True if the queries are equivalent
   */
  public boolean equals(SparqlQuery query) {
    String thisQuery, otherQuery;

    if (query == null) {
      return false;
    }

    thisQuery = getSparqlStatement();

    otherQuery = query.getSparqlStatement();

    return thisQuery.equals(otherQuery);
  }

  @Override
  public int hashCode() {
      return getSparqlStatement().hashCode();
  }

  /**
   * Returns the SPARQL statement as a string
   * 
   * @return The SQL statement
   */
  public String toString() {
    return getSparqlStatement();
  }
}
