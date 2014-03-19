package com.monead.semantic.workbench.sparqlservice;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateAction;
import com.monead.semantic.workbench.SemanticWorkbench;

/**
 * Executes and returns the results of a SPARQL query as part of the SPARQL
 * endpoint.
 * 
 * @author David Read
 * 
 */
public class SparqlRunner implements Runnable {
  /**
   * Logger Instance
   */
  private static final Logger LOGGER = Logger.getLogger(SparqlRunner.class);

  /**
   * Maximum number of characters to read from connection at a time
   */
  private static final int CHARS_PER_READ = 2048;

  /**
   * The date format used in the HTTP response header
   */
  private static final SimpleDateFormat HTTP_HEADER_DATE_FORMAT = new SimpleDateFormat(
      "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

  /**
   * Max processing time for handling a SPARQL query
   */
  private long maxRuntimeMilliseconds;

  /**
   * The socket connection to the requester
   */
  private Socket connection;

  /**
   * The ontology model to use for handling the SPARQL query
   */
  private OntModel model;

  /**
   * Are remote updates to the model permitted
   */
  private boolean remoteUpdatesPermitted;

  /**
   * A timer to constrain the runtime of this request
   */
  private Timer timer;

  /**
   * Create a new instance of the SparqlRunner class to execute a SPARQL query.
   * 
   * @param pConnection
   *          The connection from the requester
   * @param pModel
   *          The ontology model to use for processing this SPARQL query
   * @param pMaxRuntimeSeconds
   *          Maximum seconds to allow query to run before canceling the query
   * @param pRemoteUpdatesPermitted
   *          Are remote updates to the model permitted
   */
  public SparqlRunner(Socket pConnection, OntModel pModel,
      int pMaxRuntimeSeconds, boolean pRemoteUpdatesPermitted) {
    connection = pConnection;
    model = pModel;
    maxRuntimeMilliseconds = pMaxRuntimeSeconds * 1000;
    remoteUpdatesPermitted = pRemoteUpdatesPermitted;

    HTTP_HEADER_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  @Override
  public void run() {
    try {
      setStopTimer(Thread.currentThread());
      processRequest();
      connection.close();
      timer.cancel();
    } catch (ThreadDeath death) {
      LOGGER.warn("Long running SPARQL query killed by timeout", death);
      throw death;
    } catch (Throwable throwable) {
      LOGGER.error("Error processing request", throwable);
    }
  }

  /**
   * Set a timer to constrain the runtime for this request.
   * 
   * Yes it uses Thread.stop since the underlying SPARQL processing
   * in ARQ ignores an interrupt. Also, there is nothing to be left
   * in an inconsistent state outside of the single thread.
   * 
   * @param thread
   *          The thread to be killed if it runs too long
   */
  private void setStopTimer(final Thread thread) {
    timer = new Timer(thread.getName() + "-StopTimer");

    timer.schedule(
        new TimerTask() {
          @SuppressWarnings("deprecation")
          public void run() {
            LOGGER.debug("Stop time reached");
            thread.stop();
          }
        }, maxRuntimeMilliseconds);
  }

  /**
   * Process the SPARQL query
   * 
   * @throws IOException
   *           If an error occurs with the communications
   */
  private void processRequest() throws IOException {
    InputStreamReader in = null;
    PrintWriter out = null;

    try {
      in = new InputStreamReader(
          connection.getInputStream());
      out = new PrintWriter(connection.getOutputStream(), true);

      final String request = getRequestMessage(in);
      executeSparql(out, request);
      out.flush();
    } finally {
      if (in != null) {
        in.close();
      }
      if (out != null) {
        out.close();
      }
    }
  }

  /**
   * Execute the SPARQL query against the ontology model
   * 
   * @param out
   *          The PrintWriter where the SPARQL results are to be sent
   * @param sparql
   *          The SPARQL query
   * 
   * @throws IOException
   *           If a communications error occurs
   */
  private void executeSparql(PrintWriter out, String sparql) throws IOException {
    QueryExecution qe;
    ResultSet resultSet;

    LOGGER.debug("Raw sparql: " + sparql);
    final String query = URLDecoder.decode(sparql, "UTF-8");

    LOGGER.debug("Sparql converted: [" + sparql + "] to [" + query + "]");

    if (query.toLowerCase().contains("delete")
        || query.toLowerCase().contains("insert")) {
      // SPARQL update
      if (remoteUpdatesPermitted) {
        final GraphStore graphStore = GraphStoreFactory.create(model);
        UpdateAction.parseExecute(query, graphStore);
        returnResults(out, null, null);
      } else {
        returnResults(out, null, "403 Forbidden");
      }
    } else {
      // SPARQL query
      qe = QueryExecutionFactory.create(query, model);
      resultSet = qe.execSelect();
      returnResults(out, resultSet, null);
      qe.close();
    }
  }

  /**
   * Formats the query results to meet the SPARQL protocol
   * 
   * @param out
   *          The PrintWriter where the SPARQL results are to be sent
   * @param results
   *          The results from executing the SPARQL query
   * @param httpErrorCodeAndMessage
   *          The return code and message for an error (e.g. "403 Forbidden")
   * 
   * @throws IOException
   *           If a communications error occurs
   */
  private void returnResults(PrintWriter out, ResultSet results,
      String httpErrorCodeAndMessage)
      throws IOException {
    List<String> columns;
    final List<String> columnLabels = new ArrayList<String>();
    QuerySolution solution;
    int caratPosit;
    String literal;
    String value;
    String dataType;

    if (httpErrorCodeAndMessage != null) {
      out.println("HTTP/1.1 " + httpErrorCodeAndMessage);
      // out.println("Date: Tue, 11 Feb 2014 01:15:05 GMT");
      out.println("Date: " + HTTP_HEADER_DATE_FORMAT.format(new Date()));
      out.println("Pragma: no-cache");
      out.println("Cache-Control: no-cache");
      // out.println("Expires: Wed, 31 Dec 1969 19:00:00 EST");
      out.println("Expires: " + HTTP_HEADER_DATE_FORMAT.format(new Date()));
      out.println("X-Monead-Server: Monead-Semantic-Workbench-Server-"
              + SemanticWorkbench.VERSION);
      out.println("Content-Type: application/sparql-results+xml");
      out.println("Connection: close");
      out.println("");
    } else {
      out.println("HTTP/1.1 200 OK");
      // out.println("Date: Tue, 11 Feb 2014 01:15:05 GMT");
      out.println("Date: " + HTTP_HEADER_DATE_FORMAT.format(new Date()));
      out.println("Pragma: no-cache");
      out.println("Cache-Control: no-cache");
      // out.println("Expires: Wed, 31 Dec 1969 19:00:00 EST");
      out.println("Expires: " + HTTP_HEADER_DATE_FORMAT.format(new Date()));
      out.println("X-Monead-Server: Monead-Semantic-Workbench-Server-"
            + SemanticWorkbench.VERSION);
      out.println("Content-Type: application/sparql-results+xml");
      out.println("Connection: close");
      out.println("");
      out.println("<?xml version=\"1.0\"?>");
      out.println("<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\">");
      out.println("<head>");

      if (results != null) {
        columns = results.getResultVars();
        for (String colName : columns) {
          columnLabels.add(colName);
          out.println("<variable name=\"" + colName + "\"/>");
        }
      }

      out.println("</head>");

      if (results != null) {
        out.println("<results>");

        while (results.hasNext()) {
          solution = results.next();

          out.println("<result>");

          for (String var : columnLabels) {
            if (solution.get(var) == null) {
              continue;
            }
            if (solution.get(var).isLiteral()) {
              literal = solution.getLiteral(var).toString();
              if ((caratPosit = literal.indexOf('^')) > -1) {
                value = literal.substring(0, caratPosit);
                dataType = literal.substring(caratPosit + 2);
              } else {
                value = literal;
                dataType = null;
              }
              out.println("<binding name=\"" + var + "\">");
              if (dataType == null) {
                out.println("<literal>" + value + "</literal>");
              } else {
                out.println("<literal datatype=\"" + dataType + "\">" + value
                    + "</literal>");
              }
              out.println("</binding>");
            } else if (solution.get(var).isAnon()) {
              value = solution.getResource(var).toString();
              out.println("<binding name=\"" + var + "\">");
              out.println("<bnode>" + value + "</bnode>");
              out.println("</binding>");
            } else {
              value = solution.getResource(var).toString();
              out.println("<binding name=\"" + var + "\">");
              out.println("<uri>" + value + "</uri>");
              out.println("</binding>");
            }
          }
          out.println("</result>");
        }

        out.println("</results>");
      } else {
        out.println("<boolean>true</boolean>");
      }

      out.println("</sparql>");
    }
  }

  /**
   * A hardcoded test response
   * 
   * TODO remove this once the protocol options are tested out
   * 
   * @param out
   *          The PrintWriter where the SPARQL results are to be sent
   * 
   * @throws IOException
   *           If a communications error occurs
   */
  @SuppressWarnings("unused")
  private void testReturnResults(PrintWriter out) throws IOException {
    out.println("HTTP/1.1 200 OK");
    out.println("Date: Tue, 11 Feb 2014 01:15:05 GMT");
    out.println("Pragma: no-cache");
    out.println("Cache-Control: no-cache");
    out.println("Expires: Wed, 31 Dec 1969 19:00:00 EST");
    out.println("X-Monead-Server: Monead-Semantic-Workbench-Server-"
          + SemanticWorkbench.VERSION);
    out.println("Content-Type: application/sparql-results+xml");
    out.println("Connection: close");
    out.println("");
    out.println("<?xml version=\"1.0\"?>");
    out.println("<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\">");
    out.println("<head>");
    out.println("<variable name=\"p\"/>");
    out.println("<variable name=\"o\"/>");
    out.println("</head>");
    out.println("<results>");
    out.println("<result>");
    out.println("<binding name=\"p\">");
    out.println("<uri>http://monead.com/semantic/data/vehicle#gallons</uri>");
    out.println("</binding>");
    out.println("<binding name=\"o\">");
    out.println("<literal datatype=\"http://www.w3.org/2001/XMLSchema#decimal\">10.316</literal>");
    out.println("</binding>");
    out.println("</result>");
    out.println("<result>");
    out.println("<binding name=\"p\">");
    out.println("<uri>http://monead.com/semantic/data/vehicle#vehicle</uri>");
    out.println("</binding>");
    out.println("<binding name=\"o\">");
    out.println("<uri>http://monead.com/semantic/data/vehicle/personal#car2</uri>");
    out.println("</binding>");
    out.println("</result>");
    out.println("</results>");
    out.println("</sparql>");
  }

  /**
   * Extract the SPARQL query from the HTTP request
   * 
   * @param in
   *          The input from the requester's connection
   * 
   * @return The SPARQL query
   * 
   * @throws IOException
   *           If a communications error occurs
   */
  private String getRequestMessage(InputStreamReader in) throws IOException {
    String sparql = null;
    final StringBuffer message = new StringBuffer();
    int queryStart;
    boolean isPost = false;
    int contentLength = -1;
    int charsRead = 0;
    int receivedContentLength;
    final char[] input = new char[CHARS_PER_READ];
    int charsPerNewline;
    int postContentBeginningLine;
    final String[] parsed;

    connection.setSoTimeout(1000);

    try {
      while ((charsRead = in.read(input)) > -1) {
        message.append(input, 0, charsRead);
        if (message.toString().split("$").length == 2) {
          LOGGER.trace("Found a blank line");
          break;
        }
      }
    } catch (Throwable throwable) {
      LOGGER.warn("Error while processing SPARQL request", throwable);
    }

    if (message.toString().contains("\\r\\n")) {
      charsPerNewline = 2;
    } else {
      charsPerNewline = 1;
    }
    LOGGER.trace("Characters per newline: " + charsPerNewline);

    // Extract the HTTP header information
    parsed = message.toString().split("\\r?\\n");
    sparql = "";
    postContentBeginningLine = 0;

    for (String line : parsed) {
      ++postContentBeginningLine;
      LOGGER.debug("One Line: " + line);
      line = line.toLowerCase();

      if (line.startsWith("content-length:")) {
        try {
          contentLength = Integer.parseInt(line.substring(
              "Content-Length:".length()).trim());
          LOGGER.debug("POST content length: " + contentLength);
        } catch (Throwable throwable) {
          LOGGER
              .warn("Could not parse content length, relying on socket timeout since determining end of message isn't possible ("
                  + line + ")");
        }
      } else if (line.startsWith("get")) {
        queryStart = line.toUpperCase().indexOf("QUERY=");
        if (queryStart > -1) {
          sparql = line.substring(queryStart + "QUERY=".length());
          sparql = sparql.split(" ")[0]; // Remove the HTTP/1.X suffix
        }
      } else if (line.startsWith("post")) {
        isPost = true;
        LOGGER.debug("POST Request");
      } else if (line.trim().length() == 0) {
        LOGGER
            .debug("SPARQL request blank line found, POST payload will follow (if applicable): "
                + postContentBeginningLine);
        break;
      }
    }

    if (isPost) {
      receivedContentLength = 0;
      for (int row = postContentBeginningLine; row < parsed.length; ++row) {
        receivedContentLength += parsed[row].length();
        if (row < parsed.length) {
          receivedContentLength += charsPerNewline;
        }
        sparql += parsed[row] + " ";
      }

      LOGGER.debug("Payload length: " + receivedContentLength
          + "  Content length: " + contentLength);

      while (receivedContentLength < contentLength
          && (charsRead = in.read(input)) > -1) {
        sparql += new String(input, 0, charsRead);
        receivedContentLength += charsRead;
        LOGGER.debug("Payload length: " + receivedContentLength
            + "  Content length: " + contentLength);
      }
    }

    /*
     * try {
     * while ((line = in.readLine()) != null) {
     * LOGGER.debug("SPARQL request line: " + line);
     * line = line.trim();
     * if (contentStarted) {
     * sparql = sparql + line + " ";
     * charsReceived += line.length() + 2;
     * LOGGER.debug("Characters received: " + charsReceived);
     * if (charsReceived >= expectedPostLength) {
     * LOGGER
     * .debug(
     * "Reached expected number of characters in POST, stop reading from input stream"
     * );
     * break;
     * }
     * } else if (line.startsWith("Content-Length:")) {
     * try {
     * expectedPostLength = Integer.parseInt(line.substring(
     * "Content-Length:".length()).trim());
     * LOGGER.debug("POST request length: " + expectedPostLength);
     * } catch (Throwable throwable) {
     * LOGGER
     * .warn(
     * "Could not parse content length, relying on socket timeout since determining end of message isn't possible"
     * );
     * }
     * } else if (line.startsWith("GET")) {
     * queryStart = line.toUpperCase().indexOf("QUERY=");
     * if (queryStart > -1) {
     * sparql = line.substring(queryStart + "QUERY=".length());
     * sparql = sparql.split(" ")[0]; // Remove the HTTP/1.X suffix
     * }
     * } else if (line.startsWith("POST")) {
     * isPost = true;
     * LOGGER.debug("POST Request");
     * } else if (line.trim().length() == 0) {
     * LOGGER.debug("SPARQL request blank line found");
     * if (!isPost) {
     * break;
     * } else {
     * contentStarted = true;
     * sparql = "";
     * }
     * }
     * }
     * } catch (SocketTimeoutException ste) {
     * LOGGER.debug("Socket timeout: done reading input", ste);
     * }
     */
    LOGGER.debug("SPARQL query received: " + sparql);

    return sparql;
  }
}
