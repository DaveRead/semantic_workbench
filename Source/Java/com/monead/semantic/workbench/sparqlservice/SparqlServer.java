package com.monead.semantic.workbench.sparqlservice;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * Creates a socket for accepting and responding to SPARQL queries. This is a
 * Runnable and should be run on an independent thread.
 * 
 * A model MUST be set before running.
 * 
 * e.g.
 * 
 * SparqlServer.getInstance().setModel(SOME_MODEL);
 * 
 * new Thread(SparqlServer.getInstance()).start()
 * 
 * @author David Read
 * 
 */
public class SparqlServer extends Observable implements Runnable {
  /**
   * Logger Instance
   */
  private static final Logger LOGGER = Logger.getLogger(SparqlServer.class);

  /**
   * Default port number for the SPARQL server
   */
  private static final int DEFAULT_PORT = 5162;

  /**
   * Default maximum runtime allowed for a single query
   */
  private static final int DEFAULT_MAX_RUNTIME = 60;

  /**
   * Singleton instance
   */
  private static SparqlServer instance;

  /**
   * The ontology model to be used when running SPARQL queries
   */
  private OntModel model;

  /**
   * The server socket being monitored for requests
   */
  private ServerSocket serverSocket;

  /**
   * Are remote updates to the model allowed. e.g. SPARQL 1.1. updates
   */
  private boolean remoteUpdatePermitted;

  /**
   * The number of connections processed by this server
   */
  private long connectionsHandled;

  /**
   * Flag indicating that the server is starting up
   */
  private boolean isStarting;

  /**
   * The port used to accept requests
   */
  private int listenerPort = DEFAULT_PORT;

  /**
   * The number of seconds to allow a query to run before killing it
   */
  private int maxRuntimeSeconds = DEFAULT_MAX_RUNTIME;

  /**
   * Constructor - no operation
   */
  private SparqlServer() {

  }

  /**
   * Retrieve the instance
   * 
   * @return The Singleton instance
   */
  public static synchronized SparqlServer getInstance() {
    if (instance == null) {
      instance = new SparqlServer();
    }

    return instance;
  }

  /**
   * Runs the server. A model MUST be set on the instance before running it.
   */
  public void run() {
    try {
      serverSocket = new ServerSocket(getListenerPort());
    } catch (Throwable throwable) {
      LOGGER.error("Unable to setup SPARQL server endpoint", throwable);
      throw new IllegalStateException("Unable to setup SPARQL server endpoint",
          throwable);
    } finally {
      isStarting = false;
    }

    while (true) {
      try {
        processRequest(serverSocket.accept());
      } catch (SocketException se) {
        LOGGER.info("SparqlServer shutdown", se);
        break;
      } catch (Throwable throwable) {
        LOGGER.error("Error with SparqlServer", throwable);
      }
    }
  }

  /**
   * Handles an incoming request
   * 
   * @param connection
   *          The connection for the request
   */
  private synchronized void processRequest(Socket connection) {
    ++connectionsHandled;
    final Thread thread = new Thread(new SparqlRunner(connection, model,
        maxRuntimeSeconds, remoteUpdatePermitted));
    thread.setName("SPARQL Request Handler-" + connectionsHandled);
    thread.start();
    setChanged();
    notifyObservers();
  }

  /**
   * Sets the ontology model to be used for future requests
   * 
   * @param pModel
   *          The ontology model
   */
  public synchronized void setModel(OntModel pModel) {
    model = pModel;
  }

  /**
   * Start up the server
   */
  public void start() {
    if (model == null) {
      LOGGER.fatal("No model has been configured for the SPARQL server");
      throw new IllegalStateException(
          "No model has been configured for the server");
    }

    isStarting = true;
    connectionsHandled = 0;

    new Thread(this).start();
  }

  /**
   * Stop the server. This also clears the model.
   */
  public void stop() {
    try {
      if (serverSocket != null) {
        serverSocket.close();
        serverSocket = null;
        setModel(null);
      }
    } catch (Throwable throwable) {
      LOGGER.error("Error closing SparqlServer", throwable);
    }
  }

  /**
   * Checks to see if the server is active.
   * 
   * @return True if the server is active (listening for requests)
   */
  public boolean isActive() {
    return isStarting || serverSocket != null;
  }

  /**
   * Set the port to be monitored for requests. This may only be called if the
   * server is stopped.
   * 
   * @see #stop()
   * @see #isActive()
   * 
   * @param port
   *          The port to be monitored when the server is started
   */
  public void setListenerPort(int port) {
    if (!isActive()) {
      listenerPort = port;
    } else {
      throw new IllegalStateException(
          "The Sparql Server must be shutdown in order to change the listening port");
    }
  }

  /**
   * Select whether remote updates are permitted. if enabled then SPARQL 1.1.
   * updates are supported (e.g. insert, delete)
   * 
   * @param updatePermitted
   *          True if remote updates to the model are permitted
   */
  public void setRemoteUpdatesPermitted(boolean updatePermitted) {
    remoteUpdatePermitted = updatePermitted;
  }

  /**
   * Determine whether remote updates to the model are permitted.
   * 
   * @see #setRemoteUpdatesPermitted(boolean)
   * 
   * @return True if remote updates are permitted
   */
  public boolean areRemoteUpdatesPermitted() {
    return remoteUpdatePermitted;
  }

  /**
   * Get the number of connection handled since the last time the server was
   * started.
   * 
   * @return The number of connections handled
   */
  public synchronized long getConnectionsHandled() {
    return connectionsHandled;
  }

  /**
   * Get the port that this server listens on
   * 
   * @return The port this server listens on
   */
  public int getListenerPort() {
    return listenerPort;
  }

  /**
   * Get the maximum runtime (in seconds) for processing a query before killing
   * it
   * 
   * @return The maximum runtime for a query
   */
  public int getMaxRuntimeSeconds() {
    return maxRuntimeSeconds;
  }

  /**
   * Set the maximum runtime (in seconds) for processing a query before killing
   * it
   * 
   * @param pMaxRuntimeSeconds
   *          The maximum runtime for a query
   */
  public void setMaxRuntimeSeconds(int pMaxRuntimeSeconds) {
    maxRuntimeSeconds = pMaxRuntimeSeconds;
  }
}
