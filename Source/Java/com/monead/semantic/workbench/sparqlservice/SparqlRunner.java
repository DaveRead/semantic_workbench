package com.monead.semantic.workbench.sparqlservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
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
   */
  public SparqlRunner(Socket pConnection, OntModel pModel,
      int pMaxRuntimeSeconds) {
    connection = pConnection;
    model = pModel;
    maxRuntimeMilliseconds = pMaxRuntimeSeconds * 1000;
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
    BufferedReader in = null;
    PrintWriter out = null;

    try {
      in = new BufferedReader(new InputStreamReader(
          connection.getInputStream()));
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
    final String query = URLDecoder.decode(sparql, "UTF-8");

    LOGGER.debug("Sparql converted: [" + sparql + "] to [" + query + "]");

    qe = QueryExecutionFactory.create(query, model);

    resultSet = qe.execSelect();
    returnResults(out, resultSet);

    qe.close();
  }

  /**
   * Formats the query results to meet the SPARQL protocol
   * 
   * @param out
   *          The PrintWriter where the SPARQL results are to be sent
   * @param results
   *          The results from executing the SPARQL query
   * 
   * @throws IOException
   *           If a communications error occurs
   */
  private void returnResults(PrintWriter out, ResultSet results)
      throws IOException {
    List<String> columns;
    final List<String> columnLabels = new ArrayList<String>();
    QuerySolution solution;
    int caratPosit;
    String literal;
    String value;
    String dataType;

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

    columns = results.getResultVars();
    for (String colName : columns) {
      columnLabels.add(colName);
      out.println("<variable name=\"" + colName + "\"/>");
    }

    out.println("</head>");

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
    out.println("</sparql>");

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
  private String getRequestMessage(BufferedReader in) throws IOException {
    String line;
    String sparql = null;
    int queryStart;

    while ((line = in.readLine()) != null) {
      LOGGER.trace("SPARQL request line: " + line);
      line = line.trim();
      if (line.startsWith("GET")) {
        queryStart = line.toUpperCase().indexOf("QUERY=");
        if (queryStart > -1) {
          sparql = line.substring(queryStart + "QUERY=".length());
          sparql = sparql.split(" ")[0]; // Remove the HTTP/1.X suffix
        }
      } else if (line.trim().length() == 0) {
        LOGGER.trace("SPARQL request blank line found");
        break;
      }
    }

    LOGGER.debug("SPARQL query received: " + sparql);

    return sparql;
  }
}
