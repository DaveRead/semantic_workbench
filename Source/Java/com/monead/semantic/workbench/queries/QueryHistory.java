package com.monead.semantic.workbench.queries;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Maintain the query history and current location in that history.
 * 
 * @author David Read
 */
public class QueryHistory implements Serializable {
  /**
   * Logger
   */
  private static final Logger LOGGER = Logger.getLogger(QueryHistory.class);

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 20140501;

  /**
   * Maximum size of the query history list
   */
  private static final int MAXIMUM_HISTORY_ENTRIES = 1000;

  /**
   * Singleton instance
   */
  private static QueryHistory instance = new QueryHistory();

  /**
   * Collection of historical queries
   */
  private final List<QueryInfo> queryHistoryList = new ArrayList<QueryInfo>();

  /**
   * Current position in the history list
   */
  private int currentPosition = -1;

  /**
   * Private constructor for Singleton
   */
  private QueryHistory() {

  }

  /**
   * Get the instance
   * 
   * @return The instance
   */
  public static final QueryHistory getInstance() {
    return instance;
  }

  /**
   * Serialize the query history instance to a file
   * 
   * @param historyFile
   *          The file to house the serialized query history
   */
  public void saveHistory(File historyFile) {
    ObjectOutputStream out = null;

    try {
      out = new ObjectOutputStream(new FileOutputStream(historyFile, false));
      out.writeObject(this);
    } catch (Throwable throwable) {
      LOGGER.warn("Unable to save SPARQL query history to " + historyFile,
          throwable);
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (Throwable throwable) {
          LOGGER.warn("Unable to close SPARQL history file: " + historyFile,
              throwable);
        }
      }
    }
  }

  /**
   * Reconstitute the history instance from a file.
   * 
   * @param historyFile
   *          The file with the serialized history object
   */
  public void retrieveHistory(File historyFile) {
    ObjectInputStream in = null;

    try {
      in = new ObjectInputStream(new FileInputStream(historyFile));
      instance = (QueryHistory) in.readObject();
      queryHistoryList.addAll(instance.queryHistoryList);
      if (getNumberOfQueries() > 0) {
        setHistoryPosition(getNumberOfQueries() - 1);
      }
      LOGGER.debug("Retrieved history: " + getNumberOfQueries() + " @ "
          + getHistoryPosition());
    } catch (Throwable throwable) {
      LOGGER.warn("Unable to load SPARQL query history to " + historyFile,
          throwable);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (Throwable throwable) {
          LOGGER.warn("Unable to close SPARQL history file: " + historyFile,
              throwable);
        }
      }
    }
  }

  /**
   * Places the query at end of list and changes current position to point to
   * that query. If the query is at the same position as the current query in
   * the history, then only the URL, URI and result set are replaced.
   * 
   * 
   * @param query
   *          The query that is to be added to the history list
   */
  public void addQuery(QueryInfo query) {
    if (getNumberOfQueries() > 0
        && !query.getSparqlQuery().equals(
            getCurrentQueryInfo().getSparqlQuery())) {
      truncateQueryHistory();
    }

    appendQuery(query);
  }

  /**
   * Remove queries that follow the currently selected query in the history
   * list.
   */
  private void truncateQueryHistory() {
    if (getHistoryPosition() > -1) {
      while (queryHistoryList.size() > getHistoryPosition() + 1) {
        LOGGER.debug("Removed item at=" + (getHistoryPosition() + 1));
        queryHistoryList.remove(getHistoryPosition() + 1);
      }
    }

    LOGGER.debug("historyPosition=" + getHistoryPosition()
        + " historyQueries.size="
        + getNumberOfQueries());
  }

  /**
   * Append a query at the end of the history list. If the SQL index matches the
   * current history position's query's SQL index then only the results and
   * connection URL are updated in the query history.
   * 
   * @param query
   *          The query to be inserted at the end of the query history list
   */
  private void appendQuery(QueryInfo query) {
    if (getNumberOfQueries() == 0
        || !query.getSparqlQuery().equals(
            getCurrentQueryInfo().getSparqlQuery())) {
      queryHistoryList.add(query);
      setHistoryPosition(getNumberOfQueries() - 1);
    } else if (getNumberOfQueries() > 0
        && query.getSparqlQuery()
            .equals(getCurrentQueryInfo().getSparqlQuery())) {
      getCurrentQueryInfo().setResults(query.getResults());
      getCurrentQueryInfo().setDefaultGraphUri(query.getDefaultGraphUri());
      getCurrentQueryInfo().setServiceUrl(query.getServiceUrl());
    }

    enforceSize();

    LOGGER.debug("Query added - current position: " + getHistoryPosition());
  }

  /**
   * Ensure that the size of the history list does not go beyond the defined
   * maximum size
   */
  private void enforceSize() {
    if (queryHistoryList.size() > MAXIMUM_HISTORY_ENTRIES) {
      while (queryHistoryList.size() > MAXIMUM_HISTORY_ENTRIES) {
        queryHistoryList.remove(0);
        --currentPosition;
      }

      /*
       * This "can't" happen, but is here in case there is a bug that allows the
       * history list to grow beyond the max size
       */
      if (getHistoryPosition() < 0) {
        currentPosition = 0;
      }
    }
  }

  /**
   * Set the current history position
   * 
   * @param position
   *          The current history position
   */
  private void setHistoryPosition(int position) {
    currentPosition = position;
  }

  /**
   * Get the number of queries in the history list
   * 
   * @return The number of queries in the history list
   */
  public int getNumberOfQueries() {
    return queryHistoryList.size();
  }

  /**
   * Get the position of the current query in the history list
   * 
   * @return The position of the current query in the history list
   */
  private int getHistoryPosition() {
    return currentPosition;
  }

  /**
   * Get the query info for the current query position in the history list. If
   * there is no query history and exception is thrown.
   * 
   * @return The current query info from the history list
   */
  public QueryInfo getCurrentQueryInfo() {
    if (getHistoryPosition() > -1) {
      return queryHistoryList.get(getHistoryPosition());
    } else {
      throw new IllegalAccessError("No queries in the history list");
    }
  }

  /**
   * Move back one position in the history list. If the list is empty or already
   * at the beginning an exception is thrown
   * 
   * @see #hasPrevious()
   */
  public void moveBackward() {
    if (getHistoryPosition() > 0) {
      --currentPosition;
    } else {
      throw new IllegalAccessError("No previous queries in the history list");
    }
  }

  /**
   * Move forward on position in the history list. If the list is empty of
   * already at the end an exception is thrown
   * 
   * @see #hasNext()
   */
  public void moveForward() {
    if (getHistoryPosition() + 1 < getNumberOfQueries()) {
      ++currentPosition;
    } else {
      throw new IllegalAccessError("No more queries in the history list");
    }
  }

  /**
   * Check whether there is a query in the history list prior to the current
   * history position.
   * 
   * @see #moveBackward()
   * 
   * @return True if there is a query prior to the current history position
   */
  public boolean hasPrevious() {
    return getHistoryPosition() > 0;
  }

  /**
   * Check whether there is a query in the history list after to the current
   * history position.
   * 
   * @see #moveForward()
   * 
   * @return True if there is a query after the current history position
   */
  public boolean hasNext() {
    return getHistoryPosition() + 1 < getNumberOfQueries()
        && getNumberOfQueries() > 0;
  }

  /**
   * Remove all queries from history
   */
  public void clearAllQueries() {
    queryHistoryList.clear();
    currentPosition = -1;
  }
}
