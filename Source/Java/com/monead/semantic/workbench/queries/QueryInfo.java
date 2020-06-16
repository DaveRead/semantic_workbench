package com.monead.semantic.workbench.queries;

import java.io.Serializable;
import java.lang.ref.SoftReference;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.monead.semantic.workbench.SparqlTableModel;

/**
 * Title: QueryInfo
 * <p>
 * Description: Represents an individual SPARQL statement and associated
 * service, default graph URIs and results. This is used in a collection to
 * retain a history list of executed queries.
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

public class QueryInfo implements Serializable {
  /**
   * Serial UID
   */
  private static final long serialVersionUID = 20140501;

  /**
   * Logger Instance
   */
  private static final Logger LOGGER = LogManager.getLogger(QueryInfo.class);

  /**
   * The SPARQL query represented by this query info instance
   */
  private SparqlQuery sparqlQuery;

  /**
   * The index in the list of service URLs used for this query
   */
  private String serviceUrl;

  /**
   * The default graph URI used for this query
   */
  private String defaultGraphUri;

  /**
   * The result model represented by this history entry
   */
  private transient SoftReference<SparqlTableModel> results;

  /**
   * Create a history instance.
   * 
   * @param pQuery
   *          The SPARQL query
   * @param pServiceUrl
   *          The service URL used for this query (null if local model was used)
   * @param pDefaultGraphUri
   *          The default graph for this query (may be empty)
   * @param pResults
   *          The result model
   */
  public QueryInfo(SparqlQuery pQuery, String pServiceUrl,
      String pDefaultGraphUri,
      SparqlTableModel pResults) {
    setSparqlQuery(pQuery);
    setServiceUrl(pServiceUrl);
    setDefaultGraphUri(pDefaultGraphUri);
    setResults(pResults);
    LOGGER.debug("New QueryInfo: pQuery:" + pQuery + " pServiceUrl:"
        + pServiceUrl + " pDefaultGraph:" + pDefaultGraphUri + " pResults:"
        + pResults + ")");
  }

  /**
   * Get the SPARQL query
   * 
   * @return The SPARQL query
   */
  public SparqlQuery getSparqlQuery() {
    return sparqlQuery;
  }

  /**
   * Set the SPARQL query
   * 
   * @param pSparqlQuery
   *          The SPARQL query
   */
  public void setSparqlQuery(SparqlQuery pSparqlQuery) {
    this.sparqlQuery = pSparqlQuery;
  }

  /**
   * Get the service URL used for executing this query. If the local model was
   * used, this will be null
   * 
   * @return The service URL used for this query (null if local model is used)
   */
  public String getServiceUrl() {
    return serviceUrl;
  }

  /**
   * Set the service URL used when executing this query. Use null to represent
   * the local model.
   * 
   * @param pServiceUrl
   *          The service URL for the query
   */
  public void setServiceUrl(String pServiceUrl) {
    this.serviceUrl = pServiceUrl;
  }

  /**
   * Get the default graph URI for this query. This may be null or empty.
   * 
   * @return The default graph for this query which may be null or empty
   */
  public String getDefaultGraphUri() {
    return defaultGraphUri;
  }

  /**
   * Set the default graph URI for this query. This value is not required.
   * 
   * @param pDefaultGraphUri
   *          The default graph URI for this query
   */
  public void setDefaultGraphUri(String pDefaultGraphUri) {
    this.defaultGraphUri = pDefaultGraphUri;
  }

  /**
   * Set the result model represented by this history instance
   * 
   * @param pResults
   *          The result model
   */
  public void setResults(SparqlTableModel pResults) {
    results = new SoftReference<SparqlTableModel>(pResults);
  }

  /**
   * Set the result model represented by this history instance. This may be null
   * if the reference was released due to a low memory condition.
   * 
   * @return The result model or null if the reference was freed
   */
  public SparqlTableModel getResults() {
    if (results != null) {
      return results.get();
    } else {
      return null;
    }
  }
}
