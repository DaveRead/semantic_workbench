package com.monead.semantic.workbench;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.tree.*;

import org.apache.log4j.Logger;
//import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.utils.VersionInfo;

import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.monead.semantic.workbench.images.ImageLibrary;
import com.monead.semantic.workbench.sparqlservice.SparqlServer;
import com.monead.semantic.workbench.tree.IndividualComparator;
import com.monead.semantic.workbench.tree.OntClassComparator;
import com.monead.semantic.workbench.tree.OntologyTreeCellRenderer;
import com.monead.semantic.workbench.tree.StatementComparator;
import com.monead.semantic.workbench.tree.Wrapper;
import com.monead.semantic.workbench.tree.WrapperClass;
import com.monead.semantic.workbench.tree.WrapperDataProperty;
import com.monead.semantic.workbench.tree.WrapperInstance;
import com.monead.semantic.workbench.tree.WrapperLiteral;
import com.monead.semantic.workbench.tree.WrapperObjectProperty;
import com.monead.semantic.workbench.utilities.CheckLatestVersion;
import com.monead.semantic.workbench.utilities.NewVersionInformation;
import com.monead.semantic.workbench.utilities.ReasonerSelection;
import com.monead.semantic.workbench.utilities.TextProcessing;

/**
 * SemanticWorkbench - A GUI to input assertions, run an inferencing engine and
 * use
 * SPARQL queries on the resulting model
 * 
 * This program uses Jena and Pellet to provide the inference support.
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
 * For information on Jena: http://jena.sourceforge.net/ For information on
 * Pellet: http://clarkparsia.com/pellet
 * 
 * @author David Read
 * 
 *         TODO Add support for reading and writing RDF from URLs
 * 
 *         TODO Add support for SPARQL queries that use the model and URLs
 * 
 *         TODO make the service dropdown a combo box and remember manually
 *         entered service urls
 * 
 *         TODO Allow editing of an ontology/model from the tree view (graphical
 *         ontology editor)
 * 
 *         TODO Only use one model to support the diff function
 *         
 *         TODO Get Pellet working or drop support for it
 * 
 */
public class SemanticWorkbench extends JFrame implements Runnable,
    WindowListener, Observer {
  /**
   * The version identifier
   */
  public final static String VERSION = "1.8.4";

  /**
   * Serial UID
   */
  private final static long serialVersionUID = 20140216;

  /**
   * The set of formats that can be loaded. These are defined by Jena
   */
  private final static String[] FORMATS = { "N3", "N-Triples", "RDF/XML",
      "Turtle" };

  /**
   * Logger Instance
   */
  private static Logger LOGGER = Logger.getLogger(SemanticWorkbench.class);

  /**
   * Standard prefixes
   * 
   * In N3, N-Triples, RDF/XML and Turtle Order matches that of the FORMAT
   * array
   */
  private final static String[][] STANDARD_PREFIXES = {
      // N3
      { "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.",
          "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.",
          "@prefix owl: <http://www.w3.org/2002/07/owl#>.",
          "@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.",
          "@prefix dc: <http://purl.org/dc/elements/1.1/>.", },
      // N-triples
      {},
      // RDF/XML
      {
          "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"",
          "    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"",
          "    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"",
          "    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"",
          "    xmlns:dc=\"http://purl.org/dc/elements/1.1/\">" },
      // Turtle
      { "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.",
          "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.",
          "@prefix owl: <http://www.w3.org/2002/07/owl#>.",
          "@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.",
          "@prefix dc: <http://purl.org/dc/elements/1.1/>.", },
      // SPARQL
      { "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
          "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
          "prefix owl: <http://www.w3.org/2002/07/owl#>",
          "prefix xsd: <http://www.w3.org/2001/XMLSchema#>",
          "prefix dc: <http://purl.org/dc/elements/1.1/>", } };

  // /**
  // * The set of reasoning levels that will be compared.
  // */
  // protected final static String[] REASONING_LEVELS = { "None", "RDFS/RDFS",
  // "RDFS/Trans", "OWL/RDFS", "OWL/Trans", "OWL/OWL Lite" };
  // "OWL Trans (Jena)", "OWL Lite (Jena)" }; // , "OWL (Pellet)" };

  /**
   * The set of reasoners that are supported
   */
  private static List<ReasonerSelection> REASONER_SELECTIONS;

  /**
   * Constant used if a value cannot be found in an array
   */
  private final static int UNKNOWN = -1;

  /**
   * Maximum number of previous file names to retain
   */
  private final static int MAX_PREVIOUS_FILES_TO_STORE = 10;

  /**
   * File name for the properties file
   */
  private final static String PROPERTIES_FILE_NAME = "semantic_workbench.properties";

  private final static String PROP_LAST_TOP_X_POSITION = "LastTopPositionX";
  private final static String PROP_LAST_TOP_Y_POSITION = "LastTopPositionY";
  private final static String PROP_LAST_WIDTH = "LastWidth";
  private final static String PROP_LAST_HEIGHT = "LastHeight";
  private final static String PROP_LAST_DIRECTORY = "LastDirectory";
  private final static String PROP_OUTPUT_FORMAT = "OutputFormat";
  private final static String PROP_OUTPUT_CONTENT = "OutputContent";
  private final static String PROP_SHOW_FQN_NAMESPACES = "ShowFqnNamespaces";
  private final static String PROP_SHOW_DATATYPES_ON_LITERALS = "ShowDatatypesOnLiterals";
  private final static String PROP_FLAG_LITERALS_IN_RESULTS = "FlagLiteralsInResults";
  private final static String PROP_FONT_NAME = "FontName";
  private final static String PROP_FONT_SIZE = "FontSize";
  private final static String PROP_FONT_STYLE = "FontStyle";
  private final static String PROP_FONT_COLOR = "FontColor";
  private final static String PROP_PREFIX_SKIP_CLASS = "TreeClassToSkip_";
  private final static String PROP_PREFIX_SKIP_PREDICATE = "TreePredicateToSkip_";
  private final static String PROP_REASONING_LEVEL = "ReasoningLevel";
  private final static String PROP_INPUT_LANGUAGE = "InputFormat";
  private final static String PROP_PREFIX_RECENT_ASSERTIONS_FILE = "RecentAssertedTriplesFile_";
  private final static String PROP_PREFIX_RECENT_SPARQL_QUERY_FILE = "RecentSparqlQueryFile_";
  private final static String PROP_ENFORCE_FILTERS_IN_TREE_VIEW = "EnforceTreeViewFilters";
  private final static String PROP_DISPLAY_ANONYMOUS_NODES_IN_TREE_VIEW = "ShowAnonymousNodesInTreeView";
  private final static String PROP_ENABLE_STRICT_MODE = "EnableStrictMode";
  private final static String PROP_EXPORT_SPARQL_RESULTS_FORMAT = "SparqlResultsExportFormat";
  private final static String PROP_SPARQL_SERVER_PORT = "SparqlServerPort";
  private final static String PROP_SPARQL_SERVER_MAX_RUNTIME = "SparqlServerMaxRuntimeSeconds";
  private final static String PROP_PROXY_SERVER = "ProxyServer";
  private final static String PROP_PROXY_PORT = "ProxyPort";
  private final static String PROP_PROXY_HTTP = "ProxyHttpRequested";
  private final static String PROP_PROXY_SOCKS = "ProxySocksRequested";
  private final static String PROP_PROXY_ENABLED = "ProxyIsEnabled";

  /**
   * Configuration properties
   */
  private Properties properties;

  /**
   * Classes to be skipped when creating the tree view
   */
  private Map<String, String> classesToSkipInTree;

  /**
   * Predicates (properties) to be skipped when creating the tree view
   */
  private Map<String, String> predicatesToSkipInTree;

  /**
   * The name (and path if necessary) to the ontology being loaded
   */
  private File rdfFile;

  /**
   * The name (and path if necessary) to the SPARQL file being loaded
   */
  private File sparqlQueryFile;

  /**
   * The processed ontology
   */
  private OntModel ontModel;

  /**
   * The asserted model (no inferred information)
   */
  private OntModel ontModelNoInference;

  /**
   * Is proxying enabled
   */
  private boolean proxyEnabled;

  /**
   * The proxy server - used if proxying is enabled
   */
  private String proxyServer;

  /**
   * The proxy port number - used if proxying is enabled
   */
  private Integer proxyPort;
  /**
   * Whether to proxy HTTP requests - used if proxying is enabled
   */
  private boolean proxyProtocolHttp;

  /**
   * Whether to proxy SOCKS protocol requests - used if proxying is enabled
   */
  private boolean proxyProtocolSocks;

  /**
   * The assertions file menu - retained since the menu items include
   * the list of recently opened files which changes dynamically
   */
  private JMenu fileAssertionsMenu;

  /**
   * File open triples menu item
   * 
   * Used to load an ontology in to the assertions text area
   */
  private JMenuItem fileOpenTriplesFile;

  /**
   * File open recent triples file menu item
   * 
   * Created dynamically from the list of recent triples files
   */
  private JMenuItem[] fileOpenRecentTriplesFile;

  /**
   * File save triples menu item
   * 
   * Used to save the asserted triples text to a file
   */
  private JMenuItem fileSaveTriplesToFile;

  /**
   * File save model menu item
   * 
   * Used to serialize the current model to a file
   */
  private JMenuItem fileSaveSerializedModel;

  /**
   * The SPARQL file menu - retained since the menu items include
   * the list of recently opened files which changes dynamically
   */
  private JMenu fileSparqlMenu;

  /**
   * File open SPARQL menu item
   * 
   * Used to load a file with a SPARQL query into the SPARQL text area
   */
  private JMenuItem fileOpenSparqlFile;

  /**
   * File open recent SPARQL file menu item
   * 
   * Created dynamically from the list of recent SPARQL query files
   */
  private JMenuItem[] fileOpenRecentSparqlFile;

  /**
   * File save SPARQL query menu item
   * 
   * Used to save the SPARQL query from the SPARQL test area into a file
   */
  private JMenuItem fileSaveSparqlQueryToFile;

  /**
   * File save SPARQL results menu item
   * 
   * Used to save the SPARQL results to a file
   */
  private JMenuItem fileSaveSparqlResultsToFile;

  /**
   * End the program
   */
  private JMenuItem fileExit;

  /**
   * Edit insert prefixes menu item
   * 
   * Used to insert standard prefixes as a convenience
   */
  private JMenuItem editInsertPrefixes;

  /**
   * Expand all the nodes of the tree view of the model
   */
  private JMenuItem editExpandAllTreeNodes;

  /**
   * Collapse all the nodes of the tree view of the model
   */
  private JMenuItem editCollapseAllTreeNodes;

  /**
   * Generate list of inferred triples from the model
   */
  private JMenuItem modelListInferredTriples;

  /**
   * Generate tree view of model
   */
  private JMenuItem modelCreateTreeView;

  /**
   * The format to use when writing the RDF to a file
   */
  private JCheckBoxMenuItem[] setupOutputAssertionLanguage;

  /**
   * Write only assertions when outputting the model
   */
  private JCheckBoxMenuItem setupOutputModelTypeAssertions;

  /**
   * Write assertions and inferences when outputting the model
   */
  private JCheckBoxMenuItem setupOutputModelTypeAssertionsAndInferences;

  /**
   * Show FQN for namespace instead of prefixes in SPARQL output
   */
  private JCheckBoxMenuItem setupOutputFqnNamespaces;

  /**
   * Show data types for literals
   */
  private JCheckBoxMenuItem setupOutputDatatypesForLiterals;

  /**
   * Flag literal values
   */
  private JCheckBoxMenuItem setupOutputFlagLiteralValues;

  /**
   * Export SPARQL results in a Comma Separated Value file
   */
  private JCheckBoxMenuItem setupExportSparqlResultsAsCsv;

  /**
   * Export SPARQL results in a Tab Separated Value file
   */
  private JCheckBoxMenuItem setupExportSparqlResultsAsTsv;

  /**
   * Enable/disable strict mode. (From the Jena documentation: Strict mode means
   * that converting a common resource to a particular language element, such as
   * an ontology class, will be subject to some simple syntactic-level checks
   * for appropriateness.)
   */
  private JCheckBoxMenuItem setupEnableStrictMode;

  /**
   * Set the font used for the major interface display widgets
   */
  private JMenuItem setupFont;

  private JCheckBoxMenuItem setupProxyEnabled;
  private JMenuItem setupProxyConfiguration;

  /**
   * Set whether the filters for classes and properties are enabled
   */
  private JCheckBoxMenuItem filterEnableFilters;

  /**
   * Set whether anonymous classes, individuals and properties are shown in the
   * tree
   */
  private JCheckBoxMenuItem filterShowAnonymousNodes;

  /**
   * Edit the list of currently filtered classes
   */
  private JMenuItem filterEditFilteredClasses;

  /**
   * Edit the list of currently filtered properties
   */
  private JMenuItem filterEditFilteredProperties;

  private JMenuItem sparqlServerStartup;
  private JMenuItem sparqlServerShutdown;
  private JMenuItem sparqlServerConfig;
  private JMenuItem sparqlServerPublishCurrentModel;

  /**
   * View the about dialog
   */
  private JMenuItem helpAbout;

  /**
   * Allows selection of the reasoning level
   */
  private JComboBox reasoningLevel;

  /**
   * Allows selection of the semantic syntax being used
   */
  private JComboBox language;

  /**
   * The semantic syntax used for the assertions
   * 
   * Set when the assertions are loaded into the model
   */
  private String assertionLanguage;

  /**
   * Run the inferencing engine
   */
  private JButton runInferencing;

  /**
   * Execute the SPARQL query
   */
  private JButton runSparql;

  /**
   * Display information about the SPARQL server
   */
  private JLabel sparqlServerInfo;

  /**
   * Display information about proxy settings
   */
  private JLabel proxyInfo;

  /**
   * The assertions
   */
  private JTextArea assertions;

  /**
   * SPARQL input
   */
  private JTextArea sparqlInput;

  /**
   * Choose SPARQL service to use
   */
  private JComboBox sparqlServiceUrl;

  /**
   * The default graph URI (optional)
   */
  private JTextField defaultGraphUri;

  /**
   * SPARQL execution results
   */
  private JTable sparqlResultsTable;

  /**
   * Main work area housing the assertions, output and SPARQL text areas
   */
  private JTabbedPane tabbedPane;

  /**
   * Status reporting
   */
  private JLabel status;

  /**
   * The inferred results from running the inferencing engine
   */
  private JTextArea inferredTriples;

  /**
   * The resulting model displayed as a tree
   */
  private JTree ontModelTree;

  /**
   * Flag to indicate whether alternate images for the tree view have been
   * located
   */
  private boolean replaceTreeImages = false;

  /**
   * The last directory where a file was opened or saved
   */
  private File lastDirectoryUsed;

  /**
   * Recently opened and saved asserted triples files
   */
  private List<File> recentAssertionsFiles = new ArrayList<File>();

  /**
   * Recently opened and save QPARQL query files
   */
  private List<File> recentSparqlFiles = new ArrayList<File>();

  /**
   * Is the reasoner running (e.g. active thread)
   */
  private boolean isRunReasoner;

  /**
   * Is a SPARQL query running (e.g. active thread)
   */
  private boolean isRunSparql;

  /**
   * Is the tree view being populated (e.g. active thread)
   */
  private boolean isBuildingTree;

  /**
   * Are the assertions being found (e.g. active thread)
   */
  private boolean isBuildingAssertionsList;

  /**
   * Is the tree being expanded
   */
  private boolean isExpandingEntireTree;

  /**
   * Is the tree being collapsed
   */
  private boolean isCollapsingEntireTree;

  /**
   * Is the model being written to a file
   */
  private boolean isExportingModel;

  /**
   * Are SPARQL results being written to a file
   */
  private boolean isExportingSparqlResults;

  /**
   * The file that the model is being written to
   */
  private File modelExportFile;

  /**
   * The file that the SPARQL results are being written to
   */
  private File sparqlResultsExportFile;

  /**
   * Is the current tree view in synch with the current model
   */
  private boolean isTreeInSyncWithModel;

  /**
   * Are the currently displayed inferences in sync with the model
   */
  private boolean areInferencesInSyncWithModel;

  /**
   * Are the surrently displayed SPARQL query results in sync with the model and
   * the SPARQL query
   */
  private boolean areSparqlResultsInSyncWithModel;

  /**
   * Setup static information
   */
  static {
    REASONER_SELECTIONS = new ArrayList<ReasonerSelection>();

    REASONER_SELECTIONS.add(new ReasonerSelection("OWL DL/None",
        "An OWL DL model which does no entailment reasoning",
        OntModelSpec.OWL_DL_MEM));
    REASONER_SELECTIONS
        .add(new ReasonerSelection(
            "RDFS/RDFS",
            "A RDFS model which uses the RDFS inferencer for additional entailments",
            OntModelSpec.RDFS_MEM_RDFS_INF));
    REASONER_SELECTIONS.add(new ReasonerSelection("RDFS/Transitive",
        "A RDFS model which uses the transitive reasoner for entailments",
        OntModelSpec.RDFS_MEM_TRANS_INF));
    REASONER_SELECTIONS
        .add(new ReasonerSelection(
            "OWL DL/RDFS",
            "An OWL DL model which uses the RDFS inferencer for additional entailments",
            OntModelSpec.OWL_DL_MEM_RDFS_INF));
    REASONER_SELECTIONS
        .add(new ReasonerSelection(
            "OWL DL/Transitive",
            "An OWL DL model which uses the transitive inferencer for additional entailments",
            OntModelSpec.OWL_DL_MEM_TRANS_INF));
    REASONER_SELECTIONS
        .add(new ReasonerSelection(
            "OWL DL/OWL",
            "An OWL DL model which uses the OWL rules inference engine for additional entailments",
            OntModelSpec.OWL_DL_MEM_RULE_INF));

  }

  /**
   * Set up the application's UI
   */
  public SemanticWorkbench() {
    LOGGER.info("Startup");

    addWindowListener(this);
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setTitle("Semantic Workbench");
    setIcons();

    loadProperties();

    setupGUI();
    processProperties();

    enableControls(true);
    pack();
    sizing();
    setStatus("");
    setVisible(true);

    checkForNewVersion();

  }

  /**
   * Check for a new version. The checking class will notify this class if a
   * newer version is available.
   */
  private void checkForNewVersion() {
    CheckLatestVersion versionCheck = new CheckLatestVersion(VERSION);
    versionCheck.addObserver(this);
    new Thread(versionCheck).start();
  }

  private void setIcons() {
    List<Image> icons = new ArrayList<Image>();

    icons.add(ImageLibrary.instance()
        .getImageIcon(ImageLibrary.ICON_SEMANTIC_WORKBENCH_32X32).getImage());
    icons.add(ImageLibrary.instance()
        .getImageIcon(ImageLibrary.ICON_SEMANTIC_WORKBENCH_16X16).getImage());

    setIconImages(icons);
  }

  /**
   * Open the window at its last location and last dimenstions. If the window
   * will not fit on screen then assume that the screen dimensions
   * have change and move to upper left of 0, 0 and set width and height
   * to previous dimensions if they fit or reduce to 90% of screen dim.
   * 
   * If there is no window position or size information then simply assure that
   * the default window size is no more than 90% of screen height and width.
   */
  private void sizing() {
    boolean resizeRequired = false;
    boolean usePrevious = false;
    int previousHeight = 0;
    int previousWidth = 0;
    int previousTopX = 0;
    int previousTopY = 0;
    double setHeight = this.getSize().getHeight();
    double setWidth = this.getSize().getWidth();
    double screenHeight = Toolkit.getDefaultToolkit().getScreenSize()
        .getHeight();
    double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();

    try {
      previousHeight = Integer.parseInt(properties.getProperty(
          PROP_LAST_HEIGHT, setHeight + ""));
      previousWidth = Integer.parseInt(properties.getProperty(PROP_LAST_WIDTH,
          setWidth + ""));
      previousTopX = Integer.parseInt(properties.getProperty(
          PROP_LAST_TOP_X_POSITION, "0"));
      previousTopY = Integer.parseInt(properties.getProperty(
          PROP_LAST_TOP_Y_POSITION, "0"));
      if (previousHeight > 0 && previousWidth > 0 && previousTopX >= 0
          && previousTopY >= 0) {
        usePrevious = true;
      }
    } catch (Throwable throwable) {
      LOGGER.warn("Illegal window position or size property value, ignoring",
          throwable);
    }

    if (usePrevious && previousTopX + previousWidth < screenWidth
        && previousTopY + previousHeight < screenHeight) {
      this.setLocation(previousTopX, previousTopY);
      setWidth = previousWidth;
      setHeight = previousHeight;
      resizeRequired = true;
    } else {
      if (screenHeight * .9 < setHeight) {
        setHeight = screenHeight * .9;
        resizeRequired = true;
      }
      if (screenWidth * .9 < setWidth) {
        setWidth = screenWidth * .9;
        resizeRequired = true;
      }
    }

    if (resizeRequired) {
      this.setSize((int) setWidth, (int) setHeight);
    }
  }

  /**
   * Load the configuration properties file.
   */
  private void loadProperties() {
    Reader reader = null;

    properties = new Properties();

    try {
      reader = new FileReader(getPropertiesDirectory() + "/"
          + PROPERTIES_FILE_NAME);
      properties.load(reader);

      for (Object key : properties.keySet()) {
        LOGGER.debug("Startup Property [" + key + "] = ["
            + properties.getProperty(key.toString()) + "]");
      }
    } catch (Throwable throwable) {
      LOGGER.warn(
          "Unable to read the properties file: " + PROPERTIES_FILE_NAME,
          throwable);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (Throwable throwable) {
          LOGGER.warn("Unable to close the properties file: "
              + PROPERTIES_FILE_NAME, throwable);
        }
      }
    }
  }

  /**
   * Setup the program's initial state based on the configuration
   * properties.
   */
  private void processProperties() {
    String value;

    lastDirectoryUsed = new File(properties.getProperty(PROP_LAST_DIRECTORY,
        "."));

    LOGGER.debug("Last directory used from properties file: "
        + lastDirectoryUsed.getAbsolutePath());

    value = properties.getProperty(PROP_INPUT_LANGUAGE, "?");
    language.setSelectedItem(value);

    value = properties.getProperty(PROP_REASONING_LEVEL, "-1");
    try {
      Integer index = Integer.parseInt(value);
      if (index < 0 || index >= reasoningLevel.getItemCount()) {
        throw new IllegalArgumentException(
            "Incorrect reasoning level index property value: " + value);
      }
      reasoningLevel.setSelectedIndex(index);
    } catch (Throwable throwable) {
      LOGGER.warn("Index for reasoner level must be a number from zero to "
          + (reasoningLevel.getItemCount() - 1));
    }
    reasoningLevel.setToolTipText(((ReasonerSelection) reasoningLevel
        .getSelectedItem()).getDescription());

    value = properties.getProperty(PROP_OUTPUT_FORMAT, "?");
    for (JCheckBoxMenuItem outputLanguage : setupOutputAssertionLanguage) {
      if (outputLanguage.getText().equalsIgnoreCase(value)) {
        outputLanguage.setSelected(true);
      }
    }

    value = properties.getProperty(PROP_OUTPUT_CONTENT, "?");
    if (setupOutputModelTypeAssertionsAndInferences.getText().equalsIgnoreCase(
        value)) {
      setupOutputModelTypeAssertionsAndInferences.setSelected(true);
    } else {
      setupOutputModelTypeAssertions.setSelected(true);
    }

    setupOutputFqnNamespaces.setSelected(properties
        .getProperty(PROP_SHOW_FQN_NAMESPACES, "Y").toUpperCase()
        .startsWith("Y"));
    setupOutputDatatypesForLiterals.setSelected(properties
        .getProperty(PROP_SHOW_DATATYPES_ON_LITERALS, "Y").toUpperCase()
        .startsWith("Y"));
    setupOutputFlagLiteralValues.setSelected(properties
        .getProperty(PROP_FLAG_LITERALS_IN_RESULTS, "Y").toUpperCase()
        .startsWith("Y"));

    // SPARQL query export format - default to CSV
    if (properties.getProperty(PROP_EXPORT_SPARQL_RESULTS_FORMAT, "CSV")
        .equalsIgnoreCase("TSV")) {
      setupExportSparqlResultsAsTsv.setSelected(true);
    } else {
      setupExportSparqlResultsAsCsv.setSelected(true);
    }

    setupEnableStrictMode.setSelected(properties
        .getProperty(PROP_ENABLE_STRICT_MODE, "Y").toUpperCase()
        .startsWith("Y"));

    filterEnableFilters.setSelected(properties
        .getProperty(PROP_ENFORCE_FILTERS_IN_TREE_VIEW, "Y").toUpperCase()
        .startsWith("Y"));

    filterShowAnonymousNodes.setSelected(properties
        .getProperty(PROP_DISPLAY_ANONYMOUS_NODES_IN_TREE_VIEW, "N")
        .toUpperCase().startsWith("Y"));

    setFont(getFontFromProperties(), getColorFromProperties());

    extractSkipObjectsFromProperties();

    extractRecentAssertedTriplesFilesFromProperties();
    extractRecentSparqlQueryFilesFromProperties();

    // Sparql server port
    value = properties.getProperty(PROP_SPARQL_SERVER_PORT);
    if (value != null) {
      try {
        Integer port = Integer.parseInt(value);
        if (port > 0) {
          SparqlServer.getInstance().setListenerPort(port);
        } else {
          LOGGER
              .warn("Configured port for SPARQL Server must be greater than zero. Was set to "
                  + port);
        }
      } catch (Throwable throwable) {
        LOGGER
            .warn("Configured port for SPARQL Server must be a number greater than zero. Was set to "
                + value);
      }
    }

    // Sparql server max runtime
    value = properties.getProperty(PROP_SPARQL_SERVER_MAX_RUNTIME);
    if (value != null) {
      try {
        Integer maxRuntimeSeconds = Integer.parseInt(value);
        if (maxRuntimeSeconds > 0) {
          SparqlServer.getInstance().setMaxRuntimeSeconds(maxRuntimeSeconds);
        } else {
          LOGGER
              .warn("Configured maximum runtime for the SPARQL Server must be greater than zero seconds. Was set to "
                  + maxRuntimeSeconds);
        }
      } catch (Throwable throwable) {
        LOGGER
            .warn("Configured maximum runtime for the SPARQL Server must be a number greater than zero. Was set to "
                + value);
      }
    }

    // Proxy
    proxyServer = properties.getProperty(PROP_PROXY_SERVER);
    value = properties.getProperty(PROP_PROXY_PORT);
    if (value != null) {
      try {
        proxyPort = Integer.parseInt(value);
      } catch (Throwable throwable) {
        LOGGER.warn("Illegal proxy port number in the properties file: "
            + value);
      }
    }
    proxyProtocolHttp = properties.getProperty(PROP_PROXY_HTTP, "N")
        .toUpperCase().startsWith("Y");
    proxyProtocolSocks = properties.getProperty(PROP_PROXY_SOCKS, "N")
        .toUpperCase().startsWith("Y");
    proxyEnabled = properties.getProperty(PROP_PROXY_ENABLED, "N")
        .toUpperCase().startsWith("Y");
    setupProxy();
  }

  private void extractRecentAssertedTriplesFilesFromProperties() {
    List<String> prefixNames = new ArrayList<String>();

    /**
     * Find any keys for previous files
     */
    for (Object key : properties.keySet()) {
      if (key.toString().startsWith(PROP_PREFIX_RECENT_ASSERTIONS_FILE)) {
        prefixNames.add(key.toString());
      }
    }

    // Want the files in order from most recent
    Collections.sort(prefixNames);

    // Only keep up to MAX_PREVIOUS_FILES_TO_STORE values
    for (int index = 0; index < MAX_PREVIOUS_FILES_TO_STORE
        && index < prefixNames.size(); index++) {
      recentAssertionsFiles.add(new File(properties.getProperty(prefixNames
          .get(index))));
    }

    setupAssertionsFileMenu();
  }

  private void extractRecentSparqlQueryFilesFromProperties() {
    List<String> prefixNames = new ArrayList<String>();

    /**
     * Find any keys for previous files
     */
    for (Object key : properties.keySet()) {
      if (key.toString().startsWith(PROP_PREFIX_RECENT_SPARQL_QUERY_FILE)) {
        prefixNames.add(key.toString());
      }
    }

    // Want the files in order from most recent
    Collections.sort(prefixNames);

    // Only keep up to MAX_PREVIOUS_FILES_TO_STORE values
    for (int index = 0; index < MAX_PREVIOUS_FILES_TO_STORE
        && index < prefixNames.size(); index++) {
      recentSparqlFiles.add(new File(properties.getProperty(prefixNames
          .get(index))));
    }

    setupSparqlFileMenu();
  }

  /**
   * Add a file to the collection of recent asserted triples files
   * 
   * @param file
   *          The file to be added to the collection
   */
  private void addRecentAssertedTriplesFile(File file) {
    int matchAt;

    matchAt = recentAssertionsFiles.indexOf(file);

    if (matchAt == -1) {
      recentAssertionsFiles.add(0, file);
    } else if (matchAt > 0) {
      recentAssertionsFiles.remove(matchAt);
      recentAssertionsFiles.add(0, file);
    }

    // Assure only MAX_PREVIOUS_FILES_TO_STORE entries are stored
    while (recentAssertionsFiles.size() > MAX_PREVIOUS_FILES_TO_STORE) {
      recentAssertionsFiles.remove(recentAssertionsFiles.size() - 1);
    }

    setupAssertionsFileMenu();
  }

  /**
   * Add a file to the collection of recent SPARQL query files
   * 
   * @param file
   *          The file to be added to the collection
   */
  private void addRecentSparqlFile(File file) {
    int matchAt;

    matchAt = recentSparqlFiles.indexOf(file);

    if (matchAt == -1) {
      recentSparqlFiles.add(0, file);
    } else if (matchAt > 0) {
      recentSparqlFiles.remove(matchAt);
      recentSparqlFiles.add(0, file);
    }

    // Assure only MAX_PREVIOUS_FILES_TO_STORE entries are stored
    while (recentSparqlFiles.size() > MAX_PREVIOUS_FILES_TO_STORE) {
      recentSparqlFiles.remove(recentSparqlFiles.size() - 1);
    }

    setupSparqlFileMenu();
  }

  /**
   * Get the property names to be skipped from the configuration properties
   * file.
   */
  private void extractSkipObjectsFromProperties() {
    classesToSkipInTree = new HashMap<String, String>();
    predicatesToSkipInTree = new HashMap<String, String>();

    for (Object key : properties.keySet()) {
      if (key.toString().startsWith(PROP_PREFIX_SKIP_CLASS)) {
        classesToSkipInTree.put(properties.getProperty(key.toString()), "");
      } else if (key.toString().startsWith(PROP_PREFIX_SKIP_PREDICATE)) {
        predicatesToSkipInTree
            .put(properties.getProperty(key.toString()), "");
      }
    }
  }

  /**
   * Add a class or property to be filtered from the tree view when it is
   * created
   * 
   * @param wrapper
   *          An instance of a wrapper for an ontology class or property
   */
  private void addToFilter(Wrapper wrapper) {
    if (wrapper instanceof WrapperClass) {
      classesToSkipInTree.put(wrapper.getUri(), "");
      LOGGER.debug("Added class to skip in tree view: " + wrapper.getUri());
    } else if (wrapper instanceof WrapperDataProperty
        || wrapper instanceof WrapperObjectProperty) {
      LOGGER.debug("Added property to skip in tree view: " + wrapper.getUri());
      predicatesToSkipInTree.put(wrapper.getUri(), "");
    }
  }

  private void editFilterMap(Map<String, String> filterMap) {
    List<String> filteredItems = new ArrayList<String>(filterMap.keySet());
    int[] selectedIndices;

    Collections.sort(filteredItems);
    JList jListOfItems = new JList(
        filteredItems.toArray(new String[filteredItems.size()]));
    JOptionPane.showMessageDialog(this, jListOfItems, "Select Items to Remove",
        JOptionPane.QUESTION_MESSAGE);

    selectedIndices = jListOfItems.getSelectedIndices();
    if (selectedIndices.length > 0) {
      LOGGER.debug("Items to remove from the filter map: "
          + Arrays.toString(jListOfItems.getSelectedValues()));
      LOGGER
          .trace("Filtered list size before removal: " + filteredItems.size());
      for (int index = 0; index < selectedIndices.length; ++index) {
        LOGGER.trace("Remove filtered item: "
            + filteredItems.get(selectedIndices[index]));
        filterMap.remove(filteredItems.get(selectedIndices[index]));
      }
      LOGGER.trace("Filtered list size after removal: " + filteredItems.size());
    } else {
      LOGGER.debug("No items removed from filter map");
    }
  }

  /**
   * Save the current program configuration to the properties file.
   */
  private void saveProperties() {
    Writer writer = null;

    // Remove the recent asserted triples files entries.
    // They will be recreated from the new list
    removePrefixedProperties(PROP_PREFIX_RECENT_ASSERTIONS_FILE);

    // Remove the recent SPARQL query files entries.
    // They will be recreated from the new list
    removePrefixedProperties(PROP_PREFIX_RECENT_SPARQL_QUERY_FILE);

    updatePropertiesWithClassesToSkipInTree();
    updatePropertiesWithPredicatesToSkipInTree();

    // Add the set of recent asserted triples files
    for (int index = 0; index < MAX_PREVIOUS_FILES_TO_STORE
        && index < recentAssertionsFiles.size(); ++index) {
      properties.put(PROP_PREFIX_RECENT_ASSERTIONS_FILE + index,
          recentAssertionsFiles.get(index)
              .getAbsolutePath());
    }

    // Add the set of recent SPARQL query files
    for (int index = 0; index < MAX_PREVIOUS_FILES_TO_STORE
        && index < recentSparqlFiles.size(); ++index) {
      properties.put(PROP_PREFIX_RECENT_SPARQL_QUERY_FILE + index,
          recentSparqlFiles.get(index)
              .getAbsolutePath());
    }

    properties.setProperty(PROP_LAST_HEIGHT, this.getSize().height + "");
    properties.setProperty(PROP_LAST_WIDTH, this.getSize().width + "");
    properties.setProperty(PROP_LAST_TOP_X_POSITION, this.getLocation().x + "");
    properties.setProperty(PROP_LAST_TOP_Y_POSITION, this.getLocation().y + "");

    properties.setProperty(PROP_LAST_DIRECTORY,
        lastDirectoryUsed.getAbsolutePath());

    properties.setProperty(PROP_INPUT_LANGUAGE, language.getSelectedItem()
        .toString());

    properties.setProperty(PROP_REASONING_LEVEL, reasoningLevel
        .getSelectedIndex() + "");

    for (JCheckBoxMenuItem outputLanguage : setupOutputAssertionLanguage) {
      if (outputLanguage.isSelected()) {
        properties.setProperty(PROP_OUTPUT_FORMAT, outputLanguage.getText());
      }
    }

    if (setupOutputModelTypeAssertionsAndInferences.isSelected()) {
      properties.setProperty(PROP_OUTPUT_CONTENT,
          setupOutputModelTypeAssertionsAndInferences.getText());
    } else {
      properties.setProperty(PROP_OUTPUT_CONTENT,
          setupOutputModelTypeAssertions.getText());
    }

    properties.setProperty(PROP_SHOW_FQN_NAMESPACES,
        setupOutputFqnNamespaces.isSelected() ? "Yes" : "No");
    properties.setProperty(PROP_SHOW_DATATYPES_ON_LITERALS,
        setupOutputDatatypesForLiterals.isSelected() ? "Yes" : "No");
    properties.setProperty(PROP_FLAG_LITERALS_IN_RESULTS,
        setupOutputFlagLiteralValues.isSelected() ? "Yes" : "No");

    if (setupExportSparqlResultsAsTsv.isSelected()) {
      properties.setProperty(PROP_EXPORT_SPARQL_RESULTS_FORMAT, "TSV");
    } else {
      properties.setProperty(PROP_EXPORT_SPARQL_RESULTS_FORMAT, "CSV");
    }

    properties.setProperty(PROP_ENABLE_STRICT_MODE,
        setupEnableStrictMode.isSelected() ? "Yes" : "No");

    properties.setProperty(PROP_ENFORCE_FILTERS_IN_TREE_VIEW,
        filterEnableFilters.isSelected() ? "Yes" : "No");
    properties.setProperty(PROP_DISPLAY_ANONYMOUS_NODES_IN_TREE_VIEW,
        filterShowAnonymousNodes.isSelected() ? "Yes" : "No");

    properties.setProperty(PROP_SPARQL_SERVER_PORT, SparqlServer.getInstance()
        .getListenerPort() + "");
    properties.setProperty(PROP_SPARQL_SERVER_MAX_RUNTIME, SparqlServer
        .getInstance().getMaxRuntimeSeconds() + "");

    properties.setProperty(PROP_PROXY_ENABLED, proxyEnabled ? "Yes" : "No");
    if (proxyServer != null) {
      properties.setProperty(PROP_PROXY_SERVER, proxyServer);
    } else {
      properties.remove(PROP_PROXY_SERVER);
    }

    if (proxyPort != null) {
      properties.setProperty(PROP_PROXY_PORT, proxyPort + "");
    } else {
      properties.remove(PROP_PROXY_PORT);
    }

    properties.setProperty(PROP_PROXY_HTTP, proxyProtocolHttp ? "Yes" : "No");
    properties.setProperty(PROP_PROXY_SOCKS, proxyProtocolSocks ? "Yes" : "No");

    try {
      writer = new FileWriter(getPropertiesDirectory() + "/"
          + PROPERTIES_FILE_NAME, false);
      properties.store(writer, "Generated by Semantic Workbench version "
          + VERSION + " on " + new Date());
    } catch (Throwable throwable) {
      LOGGER.warn("Unable to write the properties file: "
          + PROPERTIES_FILE_NAME, throwable);
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (Throwable throwable) {
          LOGGER.warn("Unable to close the properties file: "
              + PROPERTIES_FILE_NAME, throwable);
        }
      }
    }
  }

  /**
   * Remove all the properties in the properties collection that begin with the
   * supplied property key prefix.
   * 
   * @param prefix
   *          The property prefix for entries to be removed
   */
  private void removePrefixedProperties(String prefix) {
    List<String> propertiesToBeRemoved = new ArrayList<String>();

    for (Object key : properties.keySet()) {
      if (key.toString().startsWith(prefix)) {
        propertiesToBeRemoved.add(key.toString());
      }
    }
    for (String propertyToRemove : propertiesToBeRemoved) {
      properties.remove(propertyToRemove);
    }
  }

  /**
   * Add the classes to be skipped to the configuration properties.
   */
  private void updatePropertiesWithClassesToSkipInTree() {
    int classNumber = 0;

    // Remove the existing class entries. They will be recreated from the new
    // list
    removePrefixedProperties(PROP_PREFIX_SKIP_CLASS);

    for (String classToSkipInTree : classesToSkipInTree.keySet()) {
      ++classNumber;
      properties.put(PROP_PREFIX_SKIP_CLASS + classNumber,
          classToSkipInTree);
      classesToSkipInTree.put(classToSkipInTree, "");
    }
  }

  /**
   * Add the predicates (properties) to be skipped to the configuration
   * properties.
   */
  private void updatePropertiesWithPredicatesToSkipInTree() {
    int propNumber = 0;

    // Remove the existing predicate entries. They will be recreated from the
    // new list
    for (String predicateToSkipInTree : predicatesToSkipInTree.keySet()) {
      ++propNumber;
      properties.put(PROP_PREFIX_SKIP_PREDICATE + propNumber,
          predicateToSkipInTree);
      predicatesToSkipInTree.put(predicateToSkipInTree, "");
    }
  }

  /**
   * Get the path to the properties directory. Normally this is located within
   * the user's home directory.
   * 
   * @return
   */
  private String getPropertiesDirectory() {
    String home = System.getProperty("user.home");

    if (home == null || home.trim().length() == 0) {
      home = ".";
    } else {
      home += "/SemanticWorkbench";
      File homeFile = new File(home);
      if (!homeFile.exists()) {
        homeFile.mkdirs();
      }
    }

    return home;
  }

  /**
   * Place the components in the JFrame
   */
  private void setupGUI() {
    JPanel panel;

    LOGGER.debug("SetupGUI");

    setupControls();

    setupMenus();

    getContentPane().setLayout(new BorderLayout());

    panel = new JPanel();
    panel.setLayout(new BorderLayout());
    getContentPane().add(panel, BorderLayout.CENTER);

    tabbedPane = new JTabbedPane();

    // Assertions
    tabbedPane.add("Assertions", setupAssertionsPanel());

    // Inferences
    tabbedPane.add("Inferences", setupInferencesPanel());

    // Tree view
    tabbedPane.add("Tree View", setupTreePanel());

    // SPARQL
    tabbedPane.add("SPARQL", setupSparqlPanel());

    // Add the tabbed pane to the main window
    panel.add(tabbedPane, BorderLayout.CENTER);

    // Status label, bottom of window
    panel.add(setupStatusPanel(), BorderLayout.SOUTH);
  }

  /**
   * Present the user with a font selection dialog and update the widgets if
   * the user chooses a font.
   */
  private void configureFont() {
    FontChooser chooser;
    Font newFont;
    Color newColor;

    chooser = new FontChooser(this);
    chooser.setFont(getFontFromProperties());
    chooser.setColor(getColorFromProperties());

    LOGGER.debug("Font before choices: " + chooser.getNewFont());

    chooser.setVisible(true);

    newFont = chooser.getNewFont();
    newColor = chooser.getNewColor();

    // Values will be null if user canceled request
    if (newFont != null && newColor != null) {
      properties.setProperty(PROP_FONT_NAME, newFont.getName());
      properties.setProperty(PROP_FONT_SIZE, newFont.getSize() + "");
      properties.setProperty(PROP_FONT_STYLE, newFont.getStyle() + "");

      properties.setProperty(PROP_FONT_COLOR, newColor.getRGB() + "");

      LOGGER.debug("Font after choices: " + newFont);
      setFont(newFont, newColor);
    }
  }

  /**
   * Get the font information from the configuration properties.
   * 
   * @return A Font instance based on the configuration
   */
  private Font getFontFromProperties() {
    Font newFont = null;
    String fontName = properties.getProperty(PROP_FONT_NAME, "Courier");
    String fontSize = properties.getProperty(PROP_FONT_SIZE, "12");
    String fontStyle = properties.getProperty(PROP_FONT_STYLE, "0");

    try {
      newFont = new Font(fontName, Integer.parseInt(fontStyle),
          Integer.parseInt(fontSize));
      if (newFont.getSize() < 5) {
        throw new IllegalArgumentException("Font size too small: "
            + newFont.getSize());
      }
    } catch (Throwable throwable) {
      LOGGER.warn("Cannot setup font from properties (" + fontName + ","
          + fontSize + "," + fontStyle + ")", throwable);
    }

    return newFont;
  }

  /**
   * Get the font color information from the configuration properties.
   * 
   * @return A Color instance based on the properties
   */
  private Color getColorFromProperties() {
    Color newColor = null;
    String colorRgb = properties.getProperty(PROP_FONT_COLOR,
        Color.BLACK.getRGB() + "");

    try {
      newColor = new Color(Integer.parseInt(colorRgb), true);
    } catch (Throwable throwable) {
      LOGGER.warn("Cannot setup font color from property (" + colorRgb + ")",
          throwable);
    }

    return newColor;
  }

  /**
   * Set the font and foreground color used by the widgets.
   * 
   * @param newFont
   *          The Font for widgets to use. May be null, in which case it is
   *          ignored.
   * @param newColor
   *          The foreground color for widgets to use. May be null, in which
   *          case it is ignored.
   */
  private void setFont(Font newFont, Color newColor) {
    FontMetrics fontMetrics;
    if (newFont != null) {
      assertions.setFont(newFont);
      inferredTriples.setFont(newFont);
      sparqlInput.setFont(newFont);
      ontModelTree.setFont(newFont);
      sparqlResultsTable.setFont(newFont);
      sparqlResultsTable.getTableHeader().setFont(newFont);
      fontMetrics = sparqlResultsTable.getFontMetrics(newFont);
      sparqlResultsTable.setRowHeight(((int) ((double) fontMetrics
          .getHeight() * 1.1)));
      status.setFont(newFont);
    }

    if (newColor != null) {
      assertions.setForeground(newColor);
      inferredTriples.setForeground(newColor);
      sparqlInput.setForeground(newColor);
      ontModelTree.setForeground(newColor);
      ((DefaultTreeCellRenderer) ontModelTree.getCellRenderer())
          .setTextNonSelectionColor(newColor);
      sparqlResultsTable.setForeground(newColor);
      sparqlResultsTable.getTableHeader().setForeground(newColor);
      status.setForeground(newColor);
    }
  }

  /**
   * Create the assertions panel
   * 
   * @return The assertions JPanel
   */
  private JPanel setupAssertionsPanel() {
    JPanel assertionPanel;
    JPanel gridPanel;
    JPanel flowPanel;

    assertionPanel = new JPanel();
    assertionPanel.setLayout(new BorderLayout());

    // Top of panel will allow for configuration of
    // inferencing environment
    gridPanel = new JPanel();
    gridPanel.setLayout(new GridLayout(0, 2));

    // First Row (reasoning level and input language)
    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    flowPanel.add(new JLabel("Model/Reasoning:"));
    flowPanel.add(reasoningLevel);
    gridPanel.add(flowPanel);

    // Language drop-down
    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout());
    flowPanel.add(new JLabel("Language:"));
    flowPanel.add(language);
    gridPanel.add(flowPanel);

    // Second Row
    gridPanel.add(makeFlowPanel(new JLabel("Enter Assertions"),
        FlowLayout.LEFT));
    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout());
    flowPanel.add(runInferencing);
    gridPanel.add(flowPanel);

    assertionPanel.add(gridPanel, BorderLayout.NORTH);
    assertionPanel.add(new JScrollPane(assertions), BorderLayout.CENTER);

    return assertionPanel;
  }

  /**
   * Setup the inferences panel
   * 
   * @return The inferences JPanel
   */
  private JPanel setupInferencesPanel() {
    JPanel inferencesPanel;

    // output
    inferencesPanel = new JPanel();
    inferencesPanel.setLayout(new GridLayout(1, 1));
    inferencesPanel.add(new JScrollPane(inferredTriples));

    return inferencesPanel;
  }

  /**
   * Setup the model tree display panel
   * 
   * @return The model tree JPanel
   */
  private JPanel setupTreePanel() {
    JPanel treePanel;

    treePanel = new JPanel();
    treePanel.setLayout(new GridLayout(1, 1));
    treePanel.add(new JScrollPane(ontModelTree));

    return treePanel;
  }

  /**
   * Setup the SPARQL panel
   * 
   * @return The SPARQL JPanel
   */
  private JPanel setupSparqlPanel() {
    JPanel sparqlPanel;
    JPanel labelPanel;
    JPanel gridPanel;
    JPanel innerGridPanel;
    JPanel flowPanel;

    sparqlPanel = new JPanel();
    sparqlPanel.setLayout(new GridLayout(2, 1));
    // sparqlPanel.setLayout(new BorderLayout());

    // SPARQL Input
    labelPanel = new JPanel();
    labelPanel.setLayout(new BorderLayout());
    gridPanel = new JPanel();
    gridPanel.setLayout(new GridLayout(0, 1));

    innerGridPanel = new JPanel();
    innerGridPanel.setLayout(new GridLayout(1, 4));
    innerGridPanel.add(new JLabel("SPARQL Query"));
    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout());
    flowPanel.add(runSparql);
    innerGridPanel.add(flowPanel);
    innerGridPanel.add(sparqlServerInfo);
    innerGridPanel.add(proxyInfo);
    gridPanel.add(innerGridPanel);

    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    flowPanel.add(new JLabel("SPARQL Service: "));
    flowPanel.add(sparqlServiceUrl);
    gridPanel.add(flowPanel);

    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    flowPanel.add(new JLabel("Default Graph URI: "));
    flowPanel.add(defaultGraphUri);
    gridPanel.add(flowPanel);

    labelPanel.add(gridPanel, BorderLayout.NORTH);
    labelPanel.add(new JScrollPane(sparqlInput), BorderLayout.CENTER);
    sparqlPanel.add(labelPanel);
    // sparqlPanel.add(labelPanel, BorderLayout.NORTH);

    // SPARQL results
    labelPanel = new JPanel();
    labelPanel.setLayout(new BorderLayout());
    labelPanel.add(new JLabel("Results"), BorderLayout.NORTH);
    // labelPanel.add(new JScrollPane(sparqlResults), BorderLayout.CENTER);
    labelPanel
        .add(new JScrollPane(sparqlResultsTable), BorderLayout.CENTER);
    sparqlPanel.add(labelPanel);
    // sparqlPanel.add(labelPanel, BorderLayout.CENTER);

    return sparqlPanel;
  }

  /**
   * Setup the status panel
   * 
   * @return The status JPanel
   */
  private JPanel setupStatusPanel() {
    JPanel statusPanel;

    statusPanel = new JPanel();
    statusPanel.setLayout(new GridLayout(1, 1));
    statusPanel.add(makeFlowPanel(status, FlowLayout.LEFT));

    return statusPanel;
  }

  /**
   * Configures the assertions file menu. Called at startup
   * and whenever an assertions file is opened or saved since
   * the list of recent assertions files is presented on the
   * file menu.
   */
  private void setupAssertionsFileMenu() {
    fileAssertionsMenu.removeAll();

    fileOpenTriplesFile = new JMenuItem("Open Assertions File");
    fileOpenTriplesFile.setMnemonic('O');
    fileOpenTriplesFile.setToolTipText(
        "Open an asserted triples file");
    fileOpenTriplesFile
        .addActionListener(new FileAssertedTriplesOpenListener());
    fileAssertionsMenu.add(fileOpenTriplesFile);

    fileAssertionsMenu.addSeparator();

    // Create menu options to open recently accessed ontology files
    fileOpenRecentTriplesFile = new JMenuItem[recentAssertionsFiles.size()];
    for (int recentFileNumber = 0; recentFileNumber < recentAssertionsFiles
        .size(); ++recentFileNumber) {
      fileOpenRecentTriplesFile[recentFileNumber] = new JMenuItem(
          recentAssertionsFiles.get(
              recentFileNumber).getName());
      fileOpenRecentTriplesFile[recentFileNumber].setToolTipText(
              recentAssertionsFiles.get(recentFileNumber)
                      .getAbsolutePath());
      fileOpenRecentTriplesFile[recentFileNumber]
          .addActionListener(new RecentAssertedTriplesFileOpenListener());
      fileAssertionsMenu.add(fileOpenRecentTriplesFile[recentFileNumber]);
    }

    if (fileOpenRecentTriplesFile.length > 0) {
      fileAssertionsMenu.addSeparator();
    }

    fileSaveTriplesToFile = new JMenuItem("Save Assertions Text");
    fileSaveTriplesToFile.setMnemonic(KeyEvent.VK_S);
    fileSaveTriplesToFile.setToolTipText(
        "Write the asserted triples to a file");
    fileSaveTriplesToFile
        .addActionListener(new FileAssertedTriplesSaveListener());
    fileAssertionsMenu.add(fileSaveTriplesToFile);

    fileSaveSerializedModel = new JMenuItem("Save Model (processed triples)");
    fileSaveSerializedModel.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_M, ActionEvent.ALT_MASK));
    fileSaveSerializedModel.setMnemonic(KeyEvent.VK_M);
    fileSaveSerializedModel.setToolTipText(
            "Write the triples from the current model to a file");
    fileSaveSerializedModel
        .addActionListener(new ModelSerializerListener());
    fileAssertionsMenu.add(fileSaveSerializedModel);

    fileAssertionsMenu.addSeparator();

    fileExit = new JMenuItem("Exit");
    fileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
        ActionEvent.ALT_MASK));
    fileExit.setMnemonic(KeyEvent.VK_X);
    fileExit.setToolTipText(
        "Exit the application");
    fileExit.addActionListener(new EndApplicationListener());
    fileAssertionsMenu.add(fileExit);

  }

  /**
   * Configures the SPARQL file menu. Called at startup
   * and whenever an SPARQL file is opened or saved since
   * the list of recent SPARQL files is presented on the
   * file menu.
   */
  private void setupSparqlFileMenu() {
    fileSparqlMenu.removeAll();

    fileOpenSparqlFile = new JMenuItem("Open SPARQL File");
    fileOpenSparqlFile.setMnemonic(KeyEvent.VK_S);
    fileOpenSparqlFile.setToolTipText(
        "Open a SPARQL query file");
    fileOpenSparqlFile.addActionListener(new FileSparqlOpenListener());
    fileSparqlMenu.add(fileOpenSparqlFile);

    fileSparqlMenu.addSeparator();

    // Create menu options to open recently accessed SPARQL files
    fileOpenRecentSparqlFile = new JMenuItem[recentSparqlFiles.size()];
    for (int recentFileNumber = 0; recentFileNumber < recentSparqlFiles.size(); ++recentFileNumber) {
      fileOpenRecentSparqlFile[recentFileNumber] = new JMenuItem(
          recentSparqlFiles.get(
              recentFileNumber).getName());
      fileOpenRecentSparqlFile[recentFileNumber].setToolTipText(
              recentSparqlFiles.get(recentFileNumber).getAbsolutePath());
      fileOpenRecentSparqlFile[recentFileNumber]
          .addActionListener(new RecentSparqlFileOpenListener());
      fileSparqlMenu.add(fileOpenRecentSparqlFile[recentFileNumber]);
    }

    if (fileOpenRecentSparqlFile.length > 0) {
      fileSparqlMenu.addSeparator();
    }

    fileSaveSparqlQueryToFile = new JMenuItem("Save SPARQL Query");
    fileSaveSparqlQueryToFile.setMnemonic(KeyEvent.VK_Q);
    fileSaveSparqlQueryToFile.setToolTipText(
        "Write the SPARQL query to a file");
    fileSaveSparqlQueryToFile.addActionListener(new FileSparqlSaveListener());
    fileSparqlMenu.add(fileSaveSparqlQueryToFile);

    fileSaveSparqlResultsToFile = new JMenuItem("Save SPARQL Results");
    fileSaveSparqlResultsToFile.setMnemonic(KeyEvent.VK_R);
    fileSaveSparqlResultsToFile.setToolTipText(
            "Write the current SPARQL results to a file");
    fileSaveSparqlResultsToFile
        .addActionListener(new FileSparqlResultsSaveListener());
    fileSparqlMenu.add(fileSaveSparqlResultsToFile);
  }

  /**
   * Setup the frame's menus
   */
  private void setupMenus() {
    JMenuBar menuBar;
    JMenu menu;
    ButtonGroup buttonGroup;

    menuBar = new JMenuBar();
    setJMenuBar(menuBar);

    // Assertions file menu
    fileAssertionsMenu = new JMenu("File (Assertions)");
    fileAssertionsMenu.setMnemonic(KeyEvent.VK_A);
    fileAssertionsMenu.setToolTipText(
        "Menu items related to asserted triples file access");
    menuBar.add(fileAssertionsMenu);

    setupAssertionsFileMenu();

    // SPARQL file menu
    fileSparqlMenu = new JMenu("File (SPARQL)");
    fileSparqlMenu.setMnemonic(KeyEvent.VK_S);
    fileSparqlMenu.setToolTipText(
        "Menu items related to SPARQL file access");
    menuBar.add(fileSparqlMenu);

    setupSparqlFileMenu();

    // Edit Menu
    menu = new JMenu("Edit");
    menu.setMnemonic(KeyEvent.VK_E);
    menu.setToolTipText(
        "Menu items releated to editing the ontology");
    menuBar.add(menu);

    editInsertPrefixes = new JMenuItem("Insert Prefixes");
    editInsertPrefixes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
        ActionEvent.ALT_MASK));
    editInsertPrefixes.setMnemonic(KeyEvent.VK_I);
    editInsertPrefixes.setToolTipText(
        "Insert standard prefixes (namespaces)");
    editInsertPrefixes.addActionListener(new InsertPrefixesListener());
    menu.add(editInsertPrefixes);

    menu.addSeparator();

    editExpandAllTreeNodes = new JMenuItem("Expand Entire Tree");
    editExpandAllTreeNodes.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_ADD, ActionEvent.ALT_MASK));
    editExpandAllTreeNodes.setMnemonic(KeyEvent.VK_E);
    editExpandAllTreeNodes.setToolTipText(
        "Expand all tree nodes");
    editExpandAllTreeNodes.addActionListener(new ExpandTreeListener());
    menu.add(editExpandAllTreeNodes);

    editCollapseAllTreeNodes = new JMenuItem("Collapse Entire Tree");
    editCollapseAllTreeNodes.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_SUBTRACT, ActionEvent.ALT_MASK));
    editCollapseAllTreeNodes.setMnemonic(KeyEvent.VK_C);
    editCollapseAllTreeNodes.setToolTipText("Expand all tree nodes");
    editCollapseAllTreeNodes.addActionListener(new CollapseTreeListener());
    menu.add(editCollapseAllTreeNodes);

    // Setup Menu
    menu = new JMenu("Setup");
    menu.setMnemonic(KeyEvent.VK_S);
    menu.setToolTipText(
        "Menu items related to configuration");
    menuBar.add(menu);

    buttonGroup = new ButtonGroup();
    setupOutputAssertionLanguage = new JCheckBoxMenuItem[FORMATS.length + 1];
    setupOutputAssertionLanguage[0] = new JCheckBoxMenuItem(
        "Output Format: Auto");
    buttonGroup.add(setupOutputAssertionLanguage[0]);
    menu.add(setupOutputAssertionLanguage[0]);

    for (int index = 0; index < FORMATS.length; ++index) {
      setupOutputAssertionLanguage[index + 1] = new JCheckBoxMenuItem(
          "Output Format: " + FORMATS[index]);
      buttonGroup.add(setupOutputAssertionLanguage[index + 1]);
      menu.add(setupOutputAssertionLanguage[index + 1]);
    }
    setupOutputAssertionLanguage[0].setSelected(true);

    menu.addSeparator();

    buttonGroup = new ButtonGroup();
    setupOutputModelTypeAssertions = new JCheckBoxMenuItem(
        "Output Assertions Only");
    buttonGroup.add(setupOutputModelTypeAssertions);
    menu.add(setupOutputModelTypeAssertions);

    setupOutputModelTypeAssertionsAndInferences = new JCheckBoxMenuItem(
        "Output Assertions and Inferences");
    buttonGroup.add(setupOutputModelTypeAssertionsAndInferences);
    menu.add(setupOutputModelTypeAssertionsAndInferences);

    setupOutputModelTypeAssertions.setSelected(true);

    menu.addSeparator();

    setupOutputFqnNamespaces = new JCheckBoxMenuItem(
        "Show FQN Namespaces Instead of Prefixes in Query Output");
    setupOutputFqnNamespaces.setSelected(true);
    menu.add(setupOutputFqnNamespaces);

    setupOutputDatatypesForLiterals = new JCheckBoxMenuItem(
        "Show Datatypes on Literals in Query Output");
    setupOutputDatatypesForLiterals.setSelected(true);
    menu.add(setupOutputDatatypesForLiterals);

    setupOutputFlagLiteralValues = new JCheckBoxMenuItem(
        "Flag Literal Values in Query Output");
    setupOutputFlagLiteralValues.setSelected(true);
    menu.add(setupOutputFlagLiteralValues);

    menu.addSeparator();

    buttonGroup = new ButtonGroup();
    setupExportSparqlResultsAsCsv = new JCheckBoxMenuItem(
        "Export SPARQL Results to CSV");
    setupExportSparqlResultsAsCsv
        .setToolTipText("Export to Comma Separated Value format");
    buttonGroup.add(setupExportSparqlResultsAsCsv);
    menu.add(setupExportSparqlResultsAsCsv);

    setupExportSparqlResultsAsTsv = new JCheckBoxMenuItem(
        "Export SPARQL Results to TSV");
    setupExportSparqlResultsAsTsv
        .setToolTipText("Export to Tab Separated Value format");
    buttonGroup.add(setupExportSparqlResultsAsTsv);
    menu.add(setupExportSparqlResultsAsTsv);

    menu.addSeparator();

    setupEnableStrictMode = new JCheckBoxMenuItem("Enable Strict Checking Mode");
    setupEnableStrictMode.setSelected(true);
    setupEnableStrictMode.addActionListener(new ReasonerConfigurationChange());
    menu.add(setupEnableStrictMode);

    menu.addSeparator();

    setupFont = new JMenuItem("Font");
    setupFont.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
        ActionEvent.ALT_MASK));
    setupFont.setMnemonic(KeyEvent.VK_F);
    setupFont.setToolTipText(
        "Set the font used for the display");
    setupFont.addActionListener(new FontSetupListener());
    menu.add(setupFont);

    menu.addSeparator();

    setupProxyEnabled = new JCheckBoxMenuItem("Enable Proxy");
    setupProxyEnabled.setToolTipText(
        "Pass network SPARQL requests through a proxy");
    setupProxyEnabled.addActionListener(new ProxyStatusChangeListener());
    menu.add(setupProxyEnabled);

    setupProxyConfiguration = new JMenuItem("Proxy Settings");
    setupProxyConfiguration.setToolTipText(
        "Configure the proxy");
    setupProxyConfiguration.addActionListener(new ProxySetupListener());
    menu.add(setupProxyConfiguration);

    // Model Menu
    menu = new JMenu("Model");
    menu.setMnemonic(KeyEvent.VK_M);
    menu.setToolTipText(
        "Menu items releated to viewing the model");
    menuBar.add(menu);

    modelCreateTreeView = new JMenuItem("Create Tree");
    modelCreateTreeView.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
        ActionEvent.ALT_MASK));
    modelCreateTreeView.setMnemonic(KeyEvent.VK_T);
    modelCreateTreeView.setToolTipText(
        "Create tree representation of current model");
    modelCreateTreeView.addActionListener(new GenerateTreeListener());
    menu.add(modelCreateTreeView);

    modelListInferredTriples = new JMenuItem("Identify Inferred Triples");
    modelListInferredTriples.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_I,
        ActionEvent.ALT_MASK));
    modelListInferredTriples.setMnemonic(KeyEvent.VK_I);
    modelListInferredTriples.setToolTipText(
        "Create a list of inferred triples from the current model");
    modelListInferredTriples
        .addActionListener(new GenerateInferredTriplesListener());
    menu.add(modelListInferredTriples);

    // Filters Menu
    // private JCheckBoxMenuItem filterEnableFilters;
    // private JCheckBoxMenuItem filterShowAnonymousNodes;
    // private JMenuItem filterEditFilteredClasses;
    // private JMenuItem filterEditFilteredProperties;

    menu = new JMenu("Tree Filter");
    menu.setMnemonic(KeyEvent.VK_F);
    menu.setToolTipText(
        "Menu items related to filtering values out of the model's tree");
    menuBar.add(menu);

    filterEnableFilters = new JCheckBoxMenuItem("Enable Filters");
    filterEnableFilters.setSelected(true);
    filterEnableFilters
        .setToolTipText(
            "Enforce the filtered list of classes and properties when creating the tree view");
    menu.add(filterEnableFilters);

    filterShowAnonymousNodes = new JCheckBoxMenuItem("Show Anonymous Nodes");
    filterShowAnonymousNodes.setSelected(false);
    filterShowAnonymousNodes.setToolTipText(
        "Include anonymous nodes in the tree view");
    menu.add(filterShowAnonymousNodes);

    menu.addSeparator();

    filterEditFilteredClasses = new JMenuItem("Edit List of Filtered Classes");
    filterEditFilteredClasses.setToolTipText(
        "Present the list of filtered classes and allow them to be edited");
    filterEditFilteredClasses
        .addActionListener(new EditFilteredClassesListener());
    menu.add(filterEditFilteredClasses);

    filterEditFilteredProperties = new JMenuItem(
        "Edit List of Filtered Properties");
    filterEditFilteredProperties
        .setToolTipText(
            "Present the list of filtered properties and allow them to be edited");
    filterEditFilteredProperties
        .addActionListener(new EditFilteredPropertiesListener());
    menu.add(filterEditFilteredProperties);

    // SPARQL Server Menu
    menu = new JMenu("SPARQL Server");
    menu.setMnemonic(KeyEvent.VK_P);
    menu.setToolTipText(
        "Options for using the SPARQL server");
    menuBar.add(menu);

    sparqlServerStartup = new JMenuItem("Startup SPARQL Server");
    sparqlServerStartup.setMnemonic(KeyEvent.VK_S);
    sparqlServerStartup.setToolTipText(
        "Start the SPARQL server");
    sparqlServerStartup.addActionListener(new SparqlServerStartupListener());
    menu.add(sparqlServerStartup);

    sparqlServerShutdown = new JMenuItem("Shutdown SPARQL Server");
    sparqlServerShutdown.setMnemonic(KeyEvent.VK_H);
    sparqlServerShutdown.setToolTipText(
        "Stop the SPARQL server");
    sparqlServerShutdown.addActionListener(new SparqlServerShutdownListener());
    menu.add(sparqlServerShutdown);

    menu.addSeparator();

    sparqlServerPublishCurrentModel = new JMenuItem(
        "Publish Current Reasoned Model");
    sparqlServerPublishCurrentModel.setMnemonic(KeyEvent.VK_P);
    sparqlServerPublishCurrentModel.setToolTipText(
            "Set the model for the SPARQL server to the current one reasoned");
    sparqlServerPublishCurrentModel
        .addActionListener(new SparqlServerPublishModelListener());
    menu.add(sparqlServerPublishCurrentModel);

    menu.addSeparator();

    sparqlServerConfig = new JMenuItem("Configure the SPARQL Server");
    sparqlServerConfig.setMnemonic(KeyEvent.VK_C);
    sparqlServerConfig.setToolTipText(
        "Configure the server endpoint");
    sparqlServerConfig
        .addActionListener(new SparqlServerConfigurationListener());
    menu.add(sparqlServerConfig);

    // Help Menu
    menu = new JMenu("Help");
    menu.setMnemonic(KeyEvent.VK_H);
    menu.setToolTipText(
        "Menu items releated to user assistance");
    menuBar.add(menu);

    helpAbout = new JMenuItem("About");
    helpAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
        ActionEvent.ALT_MASK));
    helpAbout.setMnemonic(KeyEvent.VK_H);
    helpAbout.setToolTipText(
        "View version information");
    helpAbout.addActionListener(new AboutListener());
    menu.add(helpAbout);
  }

  /**
   * Enable and disable controls based on current state
   * 
   * @param enable
   *          Whether to enable or disable controls
   */
  private void enableControls(boolean enable) {
    String sparqlService;

    LOGGER.debug("Called enableControls with setting " + enable);

    assertions.setEditable(enable);
    sparqlInput.setEditable(enable);
    fileOpenTriplesFile.setEnabled(enable);

    colorCodeTabs();

    // If inferencing is completed and the models are setup, enable
    // the tree view and inferred triples listing options
    if (enable && ontModelNoInference != null && ontModel != null) {
      modelCreateTreeView.setEnabled(true);
      modelListInferredTriples.setEnabled(true);
    } else {
      modelCreateTreeView.setEnabled(false);
      modelListInferredTriples.setEnabled(false);
    }

    if (enable && ontModelNoInference != null) {
      fileSaveSerializedModel.setEnabled(true);
    } else {
      fileSaveSerializedModel.setEnabled(false);
    }

    if (enable && assertions.getText().trim().length() > 0) {
      runInferencing.setEnabled(true);
      fileSaveTriplesToFile.setEnabled(true);
    } else {
      runInferencing.setEnabled(false);
      fileSaveTriplesToFile.setEnabled(false);
    }

    if (enable && sparqlInput.getText().trim().length() > 0) {
      fileSaveSparqlQueryToFile.setEnabled(true);
    } else {
      fileSaveSparqlQueryToFile.setEnabled(false);
    }

    if (enable && sparqlResultsTable.getModel().getRowCount() > 0) {
      fileSaveSparqlResultsToFile.setEnabled(true);
    } else {
      fileSaveSparqlResultsToFile.setEnabled(false);
    }

    sparqlService = ((String) sparqlServiceUrl.getEditor().getItem())
        .trim();
    if (!sparqlService.equals((String) sparqlServiceUrl.getItemAt(0))
        && sparqlService.length() > 0
        && sparqlInput.getText().trim().length() > 0) {
      runSparql.setEnabled(true);
      LOGGER.debug("Enabled Run SPARQL Button (" + sparqlService + ")");
    } else if (enable && sparqlInput.getText().trim().length() > 0
        && ontModel != null) {
      runSparql.setEnabled(true);
      LOGGER.debug("Enabled Run SPARQL Button (" + sparqlService + ")");
    } else if (enable
        && sparqlInput.getText().toLowerCase().indexOf("from") > -1) {
      runSparql.setEnabled(true);
      LOGGER.debug("Enabled Run SPARQL Button due to 'from' clause ("
          + sparqlService + ")");
    } else {
      runSparql.setEnabled(false);
      LOGGER.debug("Disabled Run SPARQL Button (" + sparqlService + ")");
    }

    // SPARQL Server
    sparqlServerShutdown.setEnabled(SparqlServer.getInstance().isActive());
    sparqlServerConfig.setEnabled(!SparqlServer.getInstance().isActive());
    sparqlServerPublishCurrentModel.setEnabled(SparqlServer.getInstance()
        .isActive() && ontModel != null);
    sparqlServerStartup.setEnabled(!SparqlServer.getInstance().isActive()
        && ontModel != null);

    // Proxy
    setupProxyConfiguration.setEnabled(true);
    setupProxyEnabled.setEnabled(isProxyConfigOkay(false));
  }

  private void setupProxy() {

    if (isProxyConfigOkay(true) && proxyEnabled) {
      setupProxyEnabled.setSelected(proxyEnabled);
      if (proxyProtocolHttp) {
        System.setProperty("http.proxyHost", proxyServer);
        System.setProperty("http.proxyPort", proxyPort + "");
      }

      if (proxyProtocolSocks) {
        System.setProperty("socksProxyHost", proxyServer);
        System.setProperty("socksProxyPort", proxyPort + "");
      }

      proxyInfo
          .setText("Enabled (" + proxyServer + "@" + proxyPort + ")");
      proxyInfo.setForeground(Color.red);

    }

    // Proxy definitions
    // -DsocksProxyHost=YourSocksServer

    // -DsocksProxyHost=YourSocksServer -DsocksProxyPort=port

    // -Dhttp.proxyHost=WebProxy -Dhttp.proxyPort=Port

    // System.setProperty("http.proxyHost", "localhost");
    // System.setProperty("http.proxyPort", "8080");

  }

  /**
   * Checks the proxy configuration and alerts the user if it is obviously
   * flawed.
   * 
   * @return False if there is an issue with the proxy configuration
   */
  private boolean isProxyConfigOkay(boolean alertUser) {
    String errorMessages = "";

    if (proxyEnabled || !alertUser) {
      if (!proxyProtocolHttp && !proxyProtocolSocks) {
        errorMessages += "No protocols were set for proxying.\n";
      }

      if (proxyServer == null || proxyServer.trim().length() == 0) {
        errorMessages += "No proxy server is defined.\n";
      }

      if (proxyPort == null || proxyPort < 1) {
        errorMessages += "No proxy port is defined.\n";
      }

      if (errorMessages.length() > 0 && alertUser) {
        setupProxyEnabled.setSelected(false); // Force proxying off
        JOptionPane
            .showMessageDialog(
                this,
                "Proxying cannot be enabled.\nPlease see the information below.\n\n"
                    + errorMessages
                    + "\nUse the Proxy Configuration option to update the proxy settings.",
                "Proxy Cannot Be Enabled",
                JOptionPane.ERROR_MESSAGE);
      }
    }

    return errorMessages.length() == 0;
  }

  /**
   * Enable or disable the use of a proxy for remote SPARQL requests
   */
  private void changeProxyMode() {
    proxyEnabled = setupProxyEnabled.isSelected();

    if (isProxyConfigOkay(true)) {
      String changeType;

      changeType = setupProxyEnabled.isSelected() ? "Enabled" : "Disabled";

      JOptionPane
          .showMessageDialog(
              this,
              "You must restart the program for the proxy change to take effect.\n\nAfter the restart the proxy will be "
                  + changeType, "Proxy Setting Changed: " + changeType,
              JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /**
   * Configure the proxy settings for executing remote SPARQL queries through a
   * proxy
   */
  private void configureProxy() {
    ProxyConfigurationDialog dialog = new ProxyConfigurationDialog(this,
        proxyServer, proxyPort, proxyProtocolHttp, proxyProtocolSocks);

    if (dialog.isAccepted()) {
      if (dialog.getProxyServer().trim().length() > 0) {
        proxyServer = dialog.getProxyServer();
      } else {
        JOptionPane
            .showMessageDialog(
                this,
                "The proxy server cannot be blank", "Proxy Server Required",
                JOptionPane.ERROR_MESSAGE);
      }
      if (dialog.getProxyPort() != null && dialog.getProxyPort() > 0) {
        proxyPort = dialog.getProxyPort();
      } else {
        JOptionPane
            .showMessageDialog(
                this,
                "The proxy port number must be a number greater than 0\n\nEntered value: "
                    + dialog.getProxyPort(), "Illegal Proxy Port Number",
                JOptionPane.ERROR_MESSAGE);
      }
      proxyProtocolHttp = dialog.isProtocolHttp();
      proxyProtocolSocks = dialog.isProtocolSocks();
      if (!proxyProtocolHttp && !proxyProtocolSocks) {
        JOptionPane
            .showMessageDialog(
                this,
                "No protocols were set for proxying.\nEnabling proxying will have no effect.",
                "Proxy Setting: No Protocols Selected",
                JOptionPane.WARNING_MESSAGE);
      }

      if (setupProxyEnabled.isSelected()) {
        JOptionPane.showMessageDialog(this,
            "You must restart the program for these changes to take effect",
            "Proxy Setting Changed", JOptionPane.INFORMATION_MESSAGE);
      }
    }

    enableControls(true);
  }

  /**
   * Startup the SPARQL server
   */
  private void startSparqlServer() {
    if (!SparqlServer.getInstance().isActive()) {
      if (ontModel != null) {
        publishModelToTheSparqlServer();
        new Thread(SparqlServer.getInstance()).start();
        SparqlServer.getInstance().addObserver(this);
        sparqlServerInfo.setForeground(Color.blue.darker());
        updateSparqlServerInfo();
      } else {
        JOptionPane.showMessageDialog(this,
            "You must create a model before starting the SPARQL server",
            "Cannot Start SPARQL Server",
            JOptionPane.WARNING_MESSAGE);
      }
    } else {
      setStatus("SPARQL server is already running");
    }

    enableControls(true);
  }

  /**
   * Shutdown the SPARQL server
   */
  private void stopSparqlServer() {
    if (SparqlServer.getInstance().isActive()) {
      SparqlServer.getInstance().stop();
      setStatus("SPARQL server stopped");
      SparqlServer.getInstance().deleteObserver(this);
    } else {
      setStatus("SPARQL server is not running");
    }

    sparqlServerInfo.setText("Shutdown");
    sparqlServerInfo.setForeground(Color.black);

    enableControls(true);
  }

  /**
   * Show the SPARQL Server Status
   */
  private void updateSparqlServerInfo() {
    sparqlServerInfo.setText("Port:"
        + SparqlServer.getInstance().getListenerPort() + "  Requests: "
        + SparqlServer.getInstance().getConnectionsHandled());
  }

  /**
   * Publishes the current ontology model to the SPARQL server endpoint
   */
  private void publishModelToTheSparqlServer() {
    if (ontModel != null) {
      OntModel newModel = ModelFactory.createOntologyModel(ontModel
          .getSpecification());
      newModel.add(ontModel.getBaseModel());
      SparqlServer.getInstance().setModel(newModel);
    } else {
      LOGGER.warn("There is no model to set on the SPARQL server");
      setStatus("There is no model to set on the SPARQL server");
    }
  }

  /**
   * Make this functional
   */
  private void configureSparqlServer() {
    if (!SparqlServer.getInstance().isActive()) {
      SparqlServerConfigurationDialog dialog = new SparqlServerConfigurationDialog(
          this, SparqlServer.getInstance().getListenerPort(), SparqlServer
              .getInstance().getMaxRuntimeSeconds());
      if (dialog.isAccepted()) {
        if (dialog.getPortNumber() != null && dialog.getPortNumber() > 0) {
          SparqlServer.getInstance().setListenerPort(dialog.getPortNumber());
        } else {
          JOptionPane
              .showMessageDialog(
                  this,
                  "The port number must be a number greater than 0\n\nEntered value: "
                      + dialog.getPortNumber(), "Illegal Port Number",
                  JOptionPane.ERROR_MESSAGE);
        }
        if (dialog.getMaxRuntime() != null && dialog.getMaxRuntime() > 0) {
          SparqlServer.getInstance().setMaxRuntimeSeconds(
              dialog.getMaxRuntime());
        } else {
          JOptionPane
              .showMessageDialog(
                  this,
                  "The maximum runtime setting must be a number greater than 0\n\nEntered value: "
                      + dialog.getMaxRuntime(), "Illegal Maximum Runtime",
                  JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }

  private void colorCodeTabs() {
    Color tabNormalFgColor = tabbedPane.getForegroundAt(0);
    Color tabNormalBgColor = tabbedPane.getBackgroundAt(0);

    if (!areInferencesInSyncWithModel
        && inferredTriples.getText().trim().length() > 0) {
      tabbedPane.setForegroundAt(1, Color.red);
      tabbedPane.setBackgroundAt(1, Color.pink);
      tabbedPane.setToolTipTextAt(1,
          "Results out of sync with loaded assertions");
      // inferredTriples.setBackground(Color.red);
      // inferredTriples.setForeground(Color.white);
    } else {
      tabbedPane.setForegroundAt(1, tabNormalFgColor);
      tabbedPane.setBackgroundAt(1, tabNormalBgColor);
      tabbedPane.setToolTipTextAt(1, null);
      // inferredTriples.setBackground(Color.white);
      // inferredTriples.setForeground(status.getForeground());
    }

    if (!isTreeInSyncWithModel
        && !ontModelTree.getModel().isLeaf(ontModelTree.getModel().getRoot())) {
      tabbedPane.setForegroundAt(2, Color.red);
      tabbedPane.setBackgroundAt(2, Color.pink);
      tabbedPane.setToolTipTextAt(2,
          "Results out of sync with loaded assertions");
      // ontModelTree.setBackground(Color.red);
      // ontModelTree.setForeground(Color.white);
    } else {
      tabbedPane.setForegroundAt(2, tabNormalFgColor);
      tabbedPane.setBackgroundAt(2, tabNormalBgColor);
      tabbedPane.setToolTipTextAt(2, null);
      // ontModelTree.setBackground(Color.white);
      // ontModelTree.setForeground(status.getForeground());
    }

    if (!areSparqlResultsInSyncWithModel
        && sparqlResultsTable.getRowCount() > 0) {
      tabbedPane.setForegroundAt(3, Color.red);
      tabbedPane.setBackgroundAt(3, Color.pink);
      tabbedPane.setToolTipTextAt(3,
          "Results out of sync with loaded assertions");
      // sparqlResultsTable.setBackground(Color.red);
      // sparqlResultsTable.setForeground(Color.white);
    } else {
      tabbedPane.setForegroundAt(3, tabNormalFgColor);
      tabbedPane.setBackgroundAt(3, tabNormalBgColor);
      tabbedPane.setToolTipTextAt(3, null);
      // sparqlResultsTable.setBackground(Color.white);
      // sparqlResultsTable.setForeground(status.getForeground());
    }

  }

  /**
   * Setup all the components
   */
  private void setupControls() {
    LOGGER.debug("setupControls");

    reasoningLevel = new JComboBox();
    for (ReasonerSelection reasoner : REASONER_SELECTIONS) {
      reasoningLevel.addItem(reasoner);
    }
    reasoningLevel.setSelectedIndex(reasoningLevel.getItemCount() - 1);
    reasoningLevel.setToolTipText(((ReasonerSelection) reasoningLevel
        .getSelectedItem()).getDescription());
    reasoningLevel.addActionListener(new ReasonerConfigurationChange());

    language = new JComboBox();
    language.addItem("Auto");
    for (String lang : FORMATS) {
      language.addItem(lang);
    }
    language.setSelectedIndex(0);

    runInferencing = new JButton("Create Model");
    runInferencing
        .setToolTipText("Creates an ontology model using the provieed assertions "
            + "and the selected reasoning level");
    runInferencing.addActionListener(new ReasonerListener());

    runSparql = new JButton("Run SPARQL");
    runSparql.addActionListener(new SparqlListener());

    sparqlServerInfo = new JLabel("Shutdown");
    sparqlServerInfo.setHorizontalAlignment(SwingConstants.CENTER);
    sparqlServerInfo.setBorder(BorderFactory
        .createTitledBorder("SPARQL Server Status"));

    proxyInfo = new JLabel("Disabled");
    proxyInfo.setHorizontalAlignment(SwingConstants.CENTER);
    proxyInfo.setBorder(BorderFactory.createTitledBorder("Proxy Status"));

    assertions = new JTextArea(10, 50);
    assertions.addKeyListener(new UserInputListener());

    inferredTriples = new JTextArea(10, 50);
    inferredTriples.setEditable(false);

    // SPARQL Input
    sparqlInput = new JTextArea(10, 50);
    sparqlInput.addKeyListener(new UserInputListener());

    sparqlServiceUrl = new JComboBox();
    sparqlServiceUrl.setEditable(true);
    sparqlServiceUrl.addItem("Local Model or Using FROM Clause");
    sparqlServiceUrl.addItem("http://DBpedia.org/sparql");
    sparqlServiceUrl
        .addItem("http://api.talis.com/stores/bbc-backstage/services/sparql");
    sparqlServiceUrl.addItem("http://dbtune.org/bbc/programmes/sparql/");
    sparqlServiceUrl
        .addItem("http://api.talis.com/stores/space/services/sparql");
    sparqlServiceUrl.addItem("http://lod.openlinksw.com/sparql/");
    sparqlServiceUrl.addItem("http://semantic.data.gov/sparql");
    sparqlServiceUrl
        .addItem("http://www4.wiwiss.fu-berlin.de/gutendata/sparql");
    sparqlServiceUrl.addItem("http://semantic.monead.com/vehicleinfo/mileage");
    sparqlServiceUrl.addItem("http://sw.unime.it:8890/sparql");
    sparqlServiceUrl.addActionListener(new SparqlModelChoiceListener());
    sparqlServiceUrl.getEditor().getEditorComponent().addKeyListener(
        new UserInputListener());

    defaultGraphUri = new JTextField();
    defaultGraphUri.setColumns(30);

    // A basic default query
    sparqlInput.setText("select ?s ?p ?o where { ?s ?p ?o } limit 100");

    sparqlResultsTable = new JTable(new SparqlTableModel());
    sparqlResultsTable.setAutoCreateRowSorter(true);

    // Determine whether alternate tree icons exist
    if (ImageLibrary.instance().getImageIcon(ImageLibrary.ICON_TREE_CLASS) != null) {
      replaceTreeImages = true;
    }

    LOGGER.debug("Tree renderer, specialized icons available? "
        + replaceTreeImages);
    ontModelTree = new JTree(new DefaultTreeModel(
        new DefaultMutableTreeNode("No Tree Generated")));

    ontModelTree.addMouseListener(new OntologyModelTreeMouseListener());

    if (replaceTreeImages) {
      ToolTipManager.sharedInstance().registerComponent(ontModelTree);
      ontModelTree.setCellRenderer(new OntologyTreeCellRenderer());
    }

    /*
     * LOGGER.debug("Tree renderer information: Leaf node width:"
     * + ((DefaultTreeCellRenderer) ontModelTree.getCellRenderer())
     * .getDefaultLeafIcon().getIconWidth()
     * + " Leaf node height:"
     * + ((DefaultTreeCellRenderer) ontModelTree.getCellRenderer())
     * .getDefaultLeafIcon().getIconHeight());
     * LOGGER.debug("Tree renderer information: Closed node width:"
     * + ((DefaultTreeCellRenderer) ontModelTree.getCellRenderer())
     * .getDefaultClosedIcon().getIconWidth()
     * + " Closed node height:"
     * + ((DefaultTreeCellRenderer) ontModelTree.getCellRenderer())
     * .getDefaultClosedIcon().getIconHeight());
     * LOGGER.debug("Tree renderer information: Open node width:"
     * + ((DefaultTreeCellRenderer) ontModelTree.getCellRenderer())
     * .getDefaultOpenIcon().getIconWidth()
     * + " Open node height:"
     * + ((DefaultTreeCellRenderer) ontModelTree.getCellRenderer())
     * .getDefaultOpenIcon().getIconHeight());
     */

    status = new JLabel("Initializing");
    status.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
  }

  /**
   * Create a JPanel that uses a FlowLayout and add a component to the JPanel.
   * The method creates a JPanel, sets its layout to FlowLayout and adds the
   * supplied component to itself.
   * 
   * @param component
   *          The component to be placed using a FlowLayout
   * @param alignment
   *          How to align the component. Use a FlowLayout constant.
   * 
   * @return The new JPanel instance
   */
  private JPanel makeFlowPanel(JComponent component, int alignment) {
    JPanel panel;

    panel = new JPanel();
    panel.setLayout(new FlowLayout(alignment));
    panel.add(component);

    return panel;
  }

  /**
   * Expand all the nodes in the tree representation of the model
   */
  private String expandAll() {
    int numNodes;
    ProgressMonitor progress;
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) ontModelTree
        .getModel().getRoot();
    @SuppressWarnings("rawtypes")
    Enumeration enumerateNodes = root.breadthFirstEnumeration();

    numNodes = 0;
    while (enumerateNodes.hasMoreElements()) {
      enumerateNodes.nextElement();
      ++numNodes;
    }

    LOGGER.debug("Expanding tree with row count: " + numNodes);

    progress = new ProgressMonitor(this,
        "Expanding Tree Nodes", "Starting node expansion", 0, numNodes);

    progress.setMillisToPopup(2000);

    setStatus("Expanding all tree nodes");

    for (int row = 0; row < numNodes; ++row) {
      progress.setProgress(row);
      if (row % 1000 == 0) {
        progress.setNote("Row " + row + " of " + numNodes);
      }

      ontModelTree.expandRow(row);
      // ontModelTree.scrollRowToVisible(row);
    }

    progress.close();

    ontModelTree.scrollRowToVisible(0);

    return "Tree nodes expanded";
  }

  /**
   * Collapse all the nodes in the tree representation of the model
   */
  private String collapseAll() {
    setStatus("Collapsing all tree nodes");

    for (int row = ontModelTree.getRowCount(); row > 0; --row) {
      ontModelTree.collapseRow(row);
    }

    return "Tree nodes collapsed";
  }

  /**
   * Set the status message to the supplied text.
   * 
   * @param message
   *          The message to place in the status field
   */
  private void setStatus(String message) {
    status.setText(message);
  }

  /**
   * Sets the mouse pointer. If the supplied parameter is true then the wait
   * cursor (usually an hourglass) is displayed. otherwise the system default
   * cursor is displayed.
   * 
   * @param wait
   *          Whether to display the system default wait cursor
   */
  private void setWaitCursor(boolean wait) {
    JRootPane rootPane = getRootPane();
    Component glassPane = rootPane.getGlassPane();

    if (wait) {
      Cursor cursorWait = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
      rootPane.setCursor(cursorWait);
      glassPane.setCursor(cursorWait);
      glassPane.setVisible(true);
    } else {
      Cursor cursorDefault = Cursor
          .getPredefinedCursor(Cursor.DEFAULT_CURSOR);
      glassPane.setVisible(false);
      glassPane.setCursor(cursorDefault);
      rootPane.setCursor(cursorDefault);
    }

    glassPane.invalidate();
    rootPane.validate();
  }

  /**
   * Run a thread to support long-running operations.
   */
  public void run() {
    enableControls(false);
    String finalStatus = null;

    try {
      setStatus("Running...");
      setWaitCursor(true);
      if (isRunReasoner) {
        finalStatus = reasonerExecution();
      } else if (isBuildingTree) {
        finalStatus = createTreeFromModel();
      } else if (isBuildingAssertionsList) {
        finalStatus = identifyInferredTriples();
      } else if (isRunSparql) {
        finalStatus = sparqlExecution();
      } else if (isExportingModel) {
        finalStatus = writeOntologyModel();
      } else if (isExportingSparqlResults) {
        finalStatus = writeSparqlResults();
      } else if (isExpandingEntireTree) {
        finalStatus = expandAll();
      } else if (isCollapsingEntireTree) {
        finalStatus = collapseAll();
      } else {
        JOptionPane.showMessageDialog(this,
            "An instruction to execute a task was received\n"
                + "but the task was undefined.", "Error: No Process to Run",
            JOptionPane.ERROR_MESSAGE);
      }
    } catch (ConversionException ce) {
      setStatus("Error: " + ce.getClass().getName() + ": "
          + ce.getMessage());
      LOGGER.error("Failed during conversion within the model", ce);
      JOptionPane
          .showMessageDialog(
              this,
              "An error occurred when converting a resource to a language element within the model.\n"
                  + "If strict checking mode is enabled you may want to try disabling it.\n\n"
                  + ce.getClass().getName() + "\n\n"
                  + ce.getMessage(),
              "Error Converting Resource to Language Element",
              JOptionPane.ERROR_MESSAGE);
    } catch (Throwable throwable) {
      String causeMessage;

      if (throwable.getCause() != null) {
        causeMessage = throwable.getCause().getClass().getName() + ": "
            + throwable.getCause().getMessage();
      } else {
        causeMessage = throwable.getClass().getName() + ": "
            + throwable.getMessage();
      }
      setStatus("Error: " + causeMessage);
      LOGGER.error("Failed during execution", throwable);
      JOptionPane.showMessageDialog(this, "Error: "
          + throwable.getClass().getName()
          + "\n"
          + throwable.getMessage()
          + "\n\n"
          + (throwable.getCause() != null ? throwable.getCause().getClass()
              .getName()
              + "\n" + throwable.getCause().getMessage() : ""), "Error",
          JOptionPane.ERROR_MESSAGE);
    } finally {
      setWaitCursor(false);
      enableControls(true);
      if (finalStatus != null) {
        setStatus(finalStatus);
      } else {
        setStatus("");
      }

      isBuildingAssertionsList = isBuildingTree = isRunReasoner
          = isRunSparql
              = isExportingModel
                  = isExportingSparqlResults
                      = isExpandingEntireTree
                          = isCollapsingEntireTree = false;
    }
  }

  /**
   * Check whether it is okay to start a new process (e.g. assure that no
   * threads are currently executing against the model). If a thread is
   * currently running, a message dialog is presented to the user indicate what
   * process is running and that a new process cannot be started.
   * 
   * @return True if a new thread may be started
   */
  private boolean okToRunThread() {
    boolean okToRun = !isBuildingAssertionsList
        && !isBuildingTree
        && !isRunReasoner
        && !isRunSparql
        && !isExportingModel
        && !isExportingSparqlResults
        && !isExpandingEntireTree && !isCollapsingEntireTree;

    if (!okToRun) {
      JOptionPane.showMessageDialog(this,
          "A process is already running and must complete.\n\n"
              + "The program is currently " + whichThreadIsActive(),
          "Information",
          JOptionPane.INFORMATION_MESSAGE);
    }

    return okToRun;
  }

  /**
   * Create a message based on the process currently running.
   * 
   * @see #okToRunThread()
   * 
   * @return The message indicating which process is running
   */
  private String whichThreadIsActive() {
    if (isBuildingAssertionsList) {
      return "creating the assertions List";
    } else if (isBuildingTree) {
      return "building the tree view";
    } else if (isRunReasoner) {
      return "reasoning over the ontology";
    } else if (isRunSparql) {
      return "executing SPARQL query";
    } else if (isExportingModel) {
      return "exporting the model to a file";
    } else if (isExportingSparqlResults) {
      return "exporting the SPARQL results to a file";
    } else if (isExpandingEntireTree) {
      return "expanding all tree nodes";
    } else if (isCollapsingEntireTree) {
      return "collapsing all tree nodes";
    }

    return "No thread running";
  }

  /**
   * Setup to run the reasoner and start a thread
   */
  private void runReasoner() {
    if (okToRunThread()) {
      isRunReasoner = true;
      new Thread(this).start();
    }
  }

  /**
   * Setup to run the SPARQL query and start a thread
   */
  private void runSPARQL() {
    if (okToRunThread()) {
      isRunSparql = true;
      new Thread(this).start();
    }
  }

  /**
   * Setup to build the tree representation of the model.
   */
  private void runCreateTreeFromModel() {
    if (okToRunThread()) {
      isBuildingTree = true;
      new Thread(this).start();
    }
  }

  /**
   * Setup to build the list of inferred triples in the model.
   */
  private void runIdentifyInferredTriples() {
    if (okToRunThread()) {
      isBuildingAssertionsList = true;
      new Thread(this).start();
    }
  }

  private void runModelExport() {
    if (okToRunThread()) {
      isExportingModel = true;
      new Thread(this).start();
    }
  }

  private void runSparqlResultsExport() {
    if (okToRunThread()) {
      isExportingSparqlResults = true;
      new Thread(this).start();
    }
  }

  private void runExpandAllTreeNodes() {
    if (okToRunThread()) {
      isExpandingEntireTree = true;
      new Thread(this).start();
    }
  }

  private void runCollapseAllTreeNodes() {
    if (okToRunThread()) {
      isCollapsingEntireTree = true;
      new Thread(this).start();
    }
  }

  /**
   * Execute the steps to run the reasoner
   */
  private String reasonerExecution() {
    setStatus("Running reasoner...");
    // inferredTriples.setText("");
    loadModel();

    return "Reasoning complete";
  }

  /**
   * Execute the steps to run the SPARQL query
   */
  private String sparqlExecution() {
    int numResults;

    setStatus("Running SPARQL query...");
    setWaitCursor(true);
    // sparqlResults.setText("");
    numResults = callSparqlEngine();

    areSparqlResultsInSyncWithModel = true;
    colorCodeTabs();

    return "Number of query results: " + numResults;
  }

  /**
   * Use the SPARQL engine and report the results
   * 
   * @return The number of resulting rows
   */
  private int callSparqlEngine() {
    QueryExecution qe;
    String serviceUrl;
    ResultSet resultSet;

    // Get the query
    String queryString = sparqlInput.getText().trim();

    // Create a Query instance
    Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);

    LOGGER.debug("Query Graph URIs? " + query.getGraphURIs());

    serviceUrl = ((String) sparqlServiceUrl.getSelectedItem()).trim();

    // Execute the query and obtain results
    if (query.getGraphURIs() != null && query.getGraphURIs().size() > 0) {
      LOGGER.debug("Query has Graph URIs: " + query.getGraphURIs().size());
      qe = QueryExecutionFactory.create(query);
    } else if (sparqlServiceUrl.getSelectedIndex() == 0
        || serviceUrl.length() == 0) {
      qe = QueryExecutionFactory.create(query, ontModel);

      // Serialize the model - unfortunately Jena's OntModel isn't serializable
      /*
       * ObjectOutputStream out = null;
       * try {
       * out = new ObjectOutputStream(new
       * FileOutputStream("/usr/daveread/ontModel.junk.obj"));
       * out.writeObject(ontModel);
       * }
       * catch (Throwable throwable) {
       * LOGGER.error("Unable to write model to binary file", throwable);
       * }
       * finally {
       * if (out != null) {
       * try {
       * out.close();
       * }
       * catch (Throwable throwable) {
       * 
       * }
       * }
       * }
       */
    } else {
      String defaultGraphUriText = defaultGraphUri.getText().trim();

      if (defaultGraphUriText.length() > 0) {
        qe = QueryExecutionFactory.sparqlService(serviceUrl, query,
            defaultGraphUriText);
      } else {
        qe = QueryExecutionFactory.sparqlService(serviceUrl, query);
      }
    }
    resultSet = qe.execSelect();

    SparqlTableModel tableModel = (SparqlTableModel) sparqlResultsTable
        .getModel();
    tableModel.setupModel(resultSet, ontModel,
        setupOutputFlagLiteralValues.isSelected(),
        setupOutputDatatypesForLiterals.isSelected(),
        setupOutputFqnNamespaces.isSelected());

    // Important - free up resources used running the query
    qe.close();

    return tableModel.getRowCount();
  }

  /**
   * Get the set of defined ontology file formats that the program can load as
   * a CSV list String
   * 
   * @return The known ontology file formats as a CSV list
   */
  public final static String getFormatsAsCSV() {
    return getArrayAsCSV(FORMATS);
  }

  /**
   * Get the set of reasoning levels that the program will use as a CSV list
   * String
   * 
   * @return The known reasoning levels as a CSV list
   */
  public final static String getReasoningLevelsAsCSV() {
    return getArrayAsCSV(REASONER_SELECTIONS.toArray());
  }

  /**
   * Create a CSV list from a String array
   * 
   * @param array
   *          An array
   * @return The array values in a CSV list
   */
  public final static String getArrayAsCSV(Object[] array) {
    StringBuffer csv;

    csv = new StringBuffer();

    for (Object value : array) {
      if (csv.length() > 0) {
        csv.append(", ");
      }
      csv.append(value.toString());
    }

    return csv.toString();

  }

  /**
   * Set the RDF file , where the ontology is located
   * 
   * @param pRdfFile
   *          The file containing the ontology
   */
  public void setRdfFile(File pRdfFile) {
    rdfFile = pRdfFile;
    setTitle();
  }

  /**
   * Set the window title
   */
  private void setTitle() {
    String title;

    title = "Semantic Workbench";

    if (assertionLanguage != null) {
      title += " - " + assertionLanguage;
    }

    if (rdfFile != null) {
      title += " - " + rdfFile.getName();
    }

    setTitle(title);
  }

  /**
   * Convert the ontology into a set of Strings representing the inferred
   * triples
   * 
   * @return A Map containing Lists that relate subjects to objects and
   *         predicates
   */
  private String identifyInferredTriples() {
    StringWriter writer;
    Model tempModel;

    setStatus("Identifying inferences in the model...");
    setWaitCursor(true);

    // LOGGER.debug("ontModelNoInference [" + ontModelNoInference + "]");
    // LOGGER.debug("ontModel [" + ontModel + "]");
    LOGGER
        .debug("Compute differences between reasoned and non-reasoned models to show inferred triples");
    tempModel = ontModel.difference(ontModelNoInference);
    LOGGER.debug("Model differences computed to identify inferred triples");

    writer = new StringWriter();
    tempModel.write(writer, assertionLanguage);
    LOGGER
        .debug("String representation of differences created to show inferred triples using "
            + assertionLanguage);

    inferredTriples.setText(writer.toString());

    areInferencesInSyncWithModel = true;
    colorCodeTabs();

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        tabbedPane.setSelectedIndex(1);
      }
    });

    return "Listing of inferred triples created in " + assertionLanguage;
  }

  /**
   * Create a model with a reasoner set based on the chosen reasoning level.
   * 
   * @param whichReasoner
   *          The reasoner from the REASONER_SELECTIONS list to be used with
   *          this model
   * 
   * @return The created ontology model
   */
  // private OntModel createModel(String reasoningLevelName) {
  private OntModel createModel(int whichReasoner) {
    OntModel model;
    // int reasoningLevelIndex;

    model = null;

    // LOGGER.debug("Create model using reasoning level: "
    // + reasoningLevelName);

    /*
     * reasoningLevelIndex = getReasoningLevelIndex(reasoningLevelName);
     * 
     * LOGGER.debug("Reasoning level index: " + reasoningLevelIndex);
     * if (reasoningLevelIndex == 0) { // None
     * model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
     * } else if (reasoningLevelIndex == 1) { // RDFS
     * model = ModelFactory
     * .createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF);
     * } else if (reasoningLevelIndex == 2) { // OWL Trans(Jena)
     * model = ModelFactory
     * .createOntologyModel(OntModelSpec.OWL_DL_MEM_TRANS_INF);
     * } else if (reasoningLevelIndex == 3) { // OWL Lite (Jena)
     * model = ModelFactory
     * .createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF);//
     * OWL_MEM_RULE_INF);//LITE_MEM_TRANS_INF);
     * } else if (reasoningLevelIndex == 4) { // OWL (Pellet)
     * // Reasoner reasoner = PelletReasonerFactory.theInstance().create();
     * // Model infModel = ModelFactory.createInfModel(reasoner, ModelFactory
     * // .createDefaultModel());
     * // model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM,
     * // infModel);
     * 
     * // create an empty ontology model using Pellet spec
     * model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
     * }
     */

    LOGGER.debug("Create reasoner: "
        + REASONER_SELECTIONS.get(whichReasoner).getName());

    model = ModelFactory.createOntologyModel(REASONER_SELECTIONS.get(
        whichReasoner).getJenaSpecification());
    model.setStrictMode(setupEnableStrictMode.isSelected());

    return model;
  }

  /**
   * Load the assertions into the ontology model.
   */
  private void loadModel() {
    String modelFormat;

    modelFormat = null;

    isTreeInSyncWithModel = areInferencesInSyncWithModel
        = areSparqlResultsInSyncWithModel = false;

    if (language.getSelectedIndex() == 0) {
      for (String format : FORMATS) {
        try {
          tryFormat(format);
          modelFormat = format;
          break;
        } catch (Throwable throwable) {
          LOGGER.debug("Error processing assertions as format: "
              + format, throwable);
        }
      }
    } else {
      try {
        tryFormat(language.getSelectedItem().toString());
        modelFormat = language.getSelectedItem().toString();
      } catch (Throwable throwable) {
        LOGGER.error("Error processing assertions as format: "
            + language.getSelectedItem().toString(), throwable);
      }
    }

    if (modelFormat == null) {
      ontModel = null;
      ontModelNoInference = null;

      if (language.getSelectedIndex() == 0) {
        throw new IllegalStateException(
            "The assertions cannot be loaded using known languages.\nTried: "
                + getFormatsAsCSV());
      } else {
        throw new IllegalStateException(
            "The assertions cannot be loaded using the input format: "
                + language.getSelectedItem().toString());
      }
    } else {
      LOGGER.info("Loaded assertions" + " using format: " + modelFormat);
    }
    assertionLanguage = modelFormat;
    setTitle();
  }

  /**
   * Attempt to load a set of assertions with the supplied format (e.g. N3,
   * RDF/XML, etc)
   * 
   * @param format
   *          The format to use, must be a value in the array FORMATS
   * @throws UnsupportedEncodingException
   *           If the assertions cannot be loaded
   */
  private void tryFormat(String format) throws UnsupportedEncodingException {
    InputStream inputStream = null;

    try {
      LOGGER.debug("Start no inferencing model load and setup");
      inputStream = new ByteArrayInputStream(assertions.getText()
          .getBytes("UTF-8"));
      ontModelNoInference = createModel(0);
      ontModelNoInference.read(inputStream, null, format.toUpperCase());

      LOGGER.debug("Start " + reasoningLevel.getSelectedItem().toString()
          + " model load and setup");
      inputStream = new ByteArrayInputStream(assertions.getText()
          .getBytes("UTF-8"));
      ontModel = createModel(reasoningLevel.getSelectedIndex());
      ontModel.read(inputStream, null, format.toUpperCase());
      LOGGER.debug(reasoningLevel.getSelectedItem().toString()
          + " model load and setup completed");
    } finally {
      try {
        inputStream.close();
      } catch (Throwable throwable) {
        LOGGER.error("Error closing input file", throwable);
      }
    }
  }

  /**
   * Build a tree representation of the semantic model
   * 
   * @see OntologyTreeCellRenderer
   * 
   *      TODO aggregate items from duplicate nodes
   */
  private String createTreeFromModel() {
    String messagePrefix = "Creating the tree view";
    DefaultMutableTreeNode treeTopNode;
    DefaultMutableTreeNode classesNode;
    DefaultMutableTreeNode oneClassNode;
    DefaultMutableTreeNode oneIndividualNode;
    DefaultMutableTreeNode onePropertyNode;
    List<OntClass> ontClasses;
    List<Individual> individuals;
    List<Statement> statements;
    Property property;
    RDFNode rdfNode;
    Literal literal;
    ExtendedIterator<OntClass> classesIterator;
    ExtendedIterator<Individual> individualsIterator;
    StmtIterator stmtIterator;
    int classNumber;
    ProgressMonitor progress = null;

    setStatus(messagePrefix);
    setWaitCursor(true);

    treeTopNode = new DefaultMutableTreeNode("Model");

    // Classes
    classesNode = new DefaultMutableTreeNode("Classes");
    treeTopNode.add(classesNode);

    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Building list of classes in the model");
    }

    try {
      classesIterator = ontModel.listClasses();

      setStatus(messagePrefix + "... obtaining the list of classes");

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("List of classes built");
      }
      ontClasses = new ArrayList<OntClass>();
      while (classesIterator.hasNext()) {
        ontClasses.add(classesIterator.next());
      }
      progress = new ProgressMonitor(this, "Create the model tree view",
          "Setting up the class list", 0, ontClasses.size());
      Collections.sort(ontClasses, new OntClassComparator());
      if (LOGGER.isTraceEnabled()) {
        LOGGER
            .trace("List of classes sorted. Num classes:" + ontClasses.size());
      }

      classNumber = 0;
      for (OntClass ontClass : ontClasses) {
        setStatus(messagePrefix + " for class " + ontClass);
        progress.setNote(ontClass.toString());
        progress.setProgress(++classNumber);

        // Check whether class is to be skipped
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace("Check if class to be skipped: " + ontClass.getURI());
          for (String skipClass : classesToSkipInTree.keySet()) {
            LOGGER.trace("Class to skip: " + skipClass + "  equal? "
                + (skipClass.equals(ontClass.getURI())));
          }
        }
        if (filterEnableFilters.isSelected()
            && classesToSkipInTree.get(ontClass.getURI()) != null) {
          LOGGER.debug("Class to be skipped: " + ontClass.getURI());
          continue;
        }

        if (ontClass.isAnon()) {
          // Show anonymous classes based on configuration
          if (filterShowAnonymousNodes.isSelected()) {
            oneClassNode = new DefaultMutableTreeNode(new WrapperClass(ontClass
                .getId().getLabelString(), "[Anonymous class]"));
          } else {
            LOGGER.debug("Skip anonymous class: "
                + ontClass.getId().getLabelString());
            continue;
          }
        } else {
          oneClassNode = new DefaultMutableTreeNode(new WrapperClass(ontClass
              .getLocalName(), ontClass.getURI()));
          LOGGER.debug("Add class node: " + ontClass
              .getLocalName()
              + " (" + ontClass.getURI() + ")");
        }
        classesNode.add(oneClassNode);

        // Individuals
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace("Get list of individuals for " + ontClass.getURI());
        }
        individualsIterator = ontModel.listIndividuals(ontClass);
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace("List of individuals built for " + ontClass.getURI()
              + " Is there at least one individual? "
              + individualsIterator.hasNext());
        }
        individuals = new ArrayList<Individual>();

        while (individualsIterator.hasNext()) {
          individuals.add(individualsIterator.next());
        }

        Collections.sort(individuals, new IndividualComparator());
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace("List of individuals sorted for " + ontClass.getURI());
        }

        for (Individual individual : individuals) {
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Next individual: " + individual.getLocalName());
          }

          if (individual.isAnon()) {
            // Show anonymous individuals based on configuration
            if (filterShowAnonymousNodes.isSelected()) {
              if (individual.getId().getLabelString() != null) {
                oneIndividualNode = new DefaultMutableTreeNode(
                    new WrapperInstance(
                        individual.getId().getLabelString(),
                        "[Anonymous individual]"));
              } else {
                oneIndividualNode = new DefaultMutableTreeNode(
                    new WrapperInstance(individual.toString(),
                        "[null label - anonymous individual]"));
              }
            } else {
              LOGGER.debug("Skip anonymous individual: "
                  + individual.getId().getLabelString());
              continue;
            }
          } else if (individual.getLocalName() != null) {
            oneIndividualNode = new DefaultMutableTreeNode(new WrapperInstance(
                individual
                    .getLocalName(), individual.getURI()));
          } else {
            oneIndividualNode = new DefaultMutableTreeNode(new WrapperInstance(
                individual.toString(), "[null name - non anonymous]"));
          }
          oneClassNode.add(oneIndividualNode);

          // Properties (predicates) and Objects
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Get list of statements for " + individual.getURI());
          }
          stmtIterator = individual.listProperties();
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("List of statements built for " + individual.getURI());
          }
          statements = new ArrayList<Statement>();
          while (stmtIterator.hasNext()) {
            statements.add(stmtIterator.next());
          }
          Collections.sort(statements, new StatementComparator());
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("List of statements sorted for " + ontClass.getURI());
          }

          for (Statement statement : statements) {
            property = statement.getPredicate();

            // Check whether predicate is to be skipped
            if (filterEnableFilters.isSelected()
                && predicatesToSkipInTree.get(property.getURI()) != null) {
              // LOGGER.debug("Predicate to be skipped: " + property.getURI());
              continue;
            }

            rdfNode = statement.getObject();

            if (property.isAnon()) {
              // Show anonymous properties based on configuration
              if (filterShowAnonymousNodes.isSelected()) {
                if (rdfNode.isLiteral()) {
                  onePropertyNode = new DefaultMutableTreeNode(
                      new WrapperDataProperty(
                          property.getId().getLabelString(),
                          "[Anonymous data property]"));
                } else {
                  onePropertyNode = new DefaultMutableTreeNode(
                      new WrapperObjectProperty(
                          property.getId().getLabelString(),
                          "[Anonymous object property]"));
                }
              } else {
                LOGGER.debug("Skip anonymous property: "
                    + property.getId().getLabelString());
                continue;
              }
            } else if (rdfNode.isLiteral() || !statement.getResource().isAnon()
                || filterShowAnonymousNodes.isSelected()) {
              if (rdfNode.isLiteral()) {
                onePropertyNode = new DefaultMutableTreeNode(
                    new WrapperDataProperty(property
                        .getLocalName(), property.getURI()));
              } else {
                onePropertyNode = new DefaultMutableTreeNode(
                    new WrapperObjectProperty(property
                        .getLocalName(), property.getURI()));
              }
            } else {
              LOGGER
                  .debug("Skip concrete property of an anonymous individual: "
                      + property.getURI() + ", "
                      + statement.getResource().getId().getLabelString());
              continue;

            }
            oneIndividualNode.add(onePropertyNode);

            if (rdfNode.isLiteral()) {
              // onePropertyNode.add(new DefaultMutableTreeNode(
              // statement.getString() + " (Literal)"));
              literal = statement.getLiteral();
              if (setupOutputDatatypesForLiterals.isSelected()) {
                onePropertyNode.add(new DefaultMutableTreeNode(
                    new WrapperLiteral(
                        literal.getString() + " [" + literal.getDatatypeURI()
                            + "]")));

              } else {
                onePropertyNode.add(new DefaultMutableTreeNode(
                    new WrapperLiteral(
                        literal.getString())));
              }
              // onePropertyNode.add(new DefaultMutableTreeNode(new
              // WrapperLiteral(
              // statement.getString())));
            } else {
              if (statement.getResource().isAnon()) {
                if (filterShowAnonymousNodes.isSelected()) {
                  onePropertyNode.add(new DefaultMutableTreeNode(
                      new WrapperInstance(
                          statement.getResource().getId().getLabelString(),
                          "[Anonymous individual]")));
                } else {
                  LOGGER.debug("Skip anonymous individual: "
                      + statement.getResource().getId().getLabelString());
                  continue;
                }
              } else {
                onePropertyNode.add(new DefaultMutableTreeNode(
                    new WrapperInstance(
                        statement.getResource().getLocalName(), statement
                            .getResource()
                            .getURI())));
              }
            }
          }
        }
      }
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          tabbedPane.setSelectedIndex(2);
        }
      });
    } finally {
      if (progress != null) {
        progress.close();
      }
    }

    ontModelTree.setModel(new DefaultTreeModel(treeTopNode));

    isTreeInSyncWithModel = true;
    colorCodeTabs();

    LOGGER.debug("Tree representation of model created");

    return "Tree view of current model created";
  }

  /**
   * Find a String value within an array of Strings. Return the index position
   * where the value was found.
   * 
   * @param array
   *          An array of string to search
   * @param name
   *          The value to find in the array
   * 
   * @return The position where the value was found in the array. Will be
   *         equal to the constant UNKNOWN if the value cannot be found in the
   *         collection of known reasoning levels
   */
  public final static int getIndexValue(String[] array, String name) {
    Integer indexValue;

    indexValue = null;

    for (int index = 0; index < array.length && indexValue == null; ++index) {
      if (array[index].toUpperCase().equals(name.toUpperCase())) {
        indexValue = index;
      }
    }

    return indexValue == null ? UNKNOWN : indexValue;
  }

  /**
   * Handle left-mouse click on ontology model tree. Initial use will be to jump
   * to a matching individual in the tree
   * 
   * @param event
   *          The mouse click event
   */
  private void processOntologyModelTreeLeftClick(MouseEvent event) {
    Wrapper wrapper = getSelectedWrapperInTree(event);
    findMatchingIndividual(wrapper, true);
  }

  /**
   * Find a matching individual in the tree.
   * 
   * @param wrapper
   *          The individual to be matched
   * @param forward
   *          True to search forward, false to search backward
   */
  private void findMatchingIndividual(Wrapper wrapper, boolean forward) {
    final DefaultTreeModel treeModel = (DefaultTreeModel) ontModelTree
        .getModel();
    final DefaultMutableTreeNode finalMatchAt;
    DefaultMutableTreeNode latestMatchAt = null;
    DefaultMutableTreeNode firstMatchAt = null;
    boolean foundClickedNode = false;
    boolean wrappedAroundBackward = false;

    if (wrapper != null && wrapper instanceof WrapperInstance) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeModel
          .getRoot();
      @SuppressWarnings("unchecked")
      Enumeration<DefaultMutableTreeNode> nodeEnumeration = node
          .preorderEnumeration();
      while (nodeEnumeration.hasMoreElements()) {
        DefaultMutableTreeNode nextNode = nodeEnumeration.nextElement();
        if (nextNode.getUserObject() instanceof Wrapper) {
          Wrapper nodeWrapper = (Wrapper) nextNode.getUserObject();
          if (wrapper.getUuid().equals(nodeWrapper.getUuid())) {
            foundClickedNode = true;
            if (forward) {
              // If there is one past this location then
              // use it, otherwise wrap back to top
              latestMatchAt = null;
            } else {
              if (firstMatchAt != null || latestMatchAt != null) {
                // Searching backward and have found a previous one
                break;
              }
              wrappedAroundBackward = true;
            }
          } else if (!wrapper.getUuid().equals(nodeWrapper.getUuid())
              && wrapper.getClass().equals(wrapper.getClass())
              && wrapper.getLocalName().equals(nodeWrapper.getLocalName())
              && wrapper.getUri().equals(nodeWrapper.getUri())) {
            if (firstMatchAt == null && !foundClickedNode) {
              // First one found, keep it in case we wrap around
              firstMatchAt = nextNode;
              LOGGER
                  .debug("Found first matching node: search UUID: "
                      + wrapper.getUuid() + " found UUID: "
                      + nodeWrapper.getUuid());
            } else {
              // Keep track of latest one found
              latestMatchAt = nextNode;
              LOGGER
                  .debug("Found a following matching node: search UUID: "
                      + wrapper.getUuid() + " found UUID: "
                      + nodeWrapper.getUuid());
              // If going forward then this is the next match
              if (forward && foundClickedNode) {
                break;
              }
            }
          }
        }
      }

      if ((!forward || foundClickedNode) && latestMatchAt != null) {
        finalMatchAt = latestMatchAt;
        if (forward) {
          setStatus("Next " + wrapper.getLocalName() + " found");
        } else {
          if (wrappedAroundBackward) {
            setStatus("Wrapped to bottom of tree and found a "
                + wrapper.getLocalName());
          } else {
            setStatus("Previous " + wrapper.getLocalName() + " found");
          }
        }
      } else if (firstMatchAt != null) {
        finalMatchAt = firstMatchAt;
        if (forward) {
          setStatus("Wrapped to top of tree and found a "
              + wrapper.getLocalName());
        } else {
          setStatus("Previous " + wrapper.getLocalName() + " found");
        }
      } else {
        finalMatchAt = null;
        setStatus(wrapper.getLocalName()
            + " could not be found elsewhere in the tree");
      }

      if (finalMatchAt != null) {
        ontModelTree.setExpandsSelectedPaths(true);

        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            ontModelTree.setSelectionPath(new TreePath(treeModel
                .getPathToRoot(finalMatchAt)));
            ontModelTree.scrollPathToVisible(new TreePath(treeModel
                .getPathToRoot(finalMatchAt)));
            Rectangle visible = ontModelTree.getVisibleRect();
            visible.x = 0;
            ontModelTree.scrollRectToVisible(visible);
          }
        });
      }
    }
  }

  /**
   * Handle right-mouse click on ontology model tree. Initial use will be to add
   * a class or property to the list of suppressed classes and properties so
   * that it won't show up in the tree.
   * 
   * @param event
   *          The mouse click event
   */
  private void processOntologyModelTreeRightClick(MouseEvent event) {
    Wrapper wrapper = getSelectedWrapperInTree(event);
    if (wrapper != null) { // && wrapper.getUri().indexOf("Anonymous") == -1) {
      if (wrapper instanceof WrapperClass
            || wrapper instanceof WrapperDataProperty
            || wrapper instanceof WrapperObjectProperty) {
        FilterValuePopup popup = new FilterValuePopup(wrapper);
        popup.show(event.getComponent(), event.getX(), event.getY());
      } else if (wrapper instanceof WrapperInstance) {
        findMatchingIndividual(wrapper, false);
      }
    }
  }

  /**
   * Invalidates the existing ontology model.
   */
  private void invalidateModel() {
    ontModel = null;
    ontModelNoInference = null;

    reasoningLevel.setToolTipText(((ReasonerSelection) reasoningLevel
        .getSelectedItem()).getDescription());

    isTreeInSyncWithModel = areInferencesInSyncWithModel
        = areSparqlResultsInSyncWithModel = false;

    enableControls(true);
  }

  /**
   * Get the chosen Wrapper from the tree node that was clicked on.
   * 
   * @param event
   *          The mouse click event
   * 
   * @return The Wrapper instance or null if the node is not a Wrapper
   */
  private Wrapper getSelectedWrapperInTree(MouseEvent event) {
    Wrapper chosenWrapper = null;

    LOGGER.debug("Tree mouse event: " + event.paramString());
    TreePath path = ontModelTree.getPathForLocation(event.getX(), event.getY());
    if (path != null) {
      LOGGER.debug("Tree right-mouse event on: " + path.getLastPathComponent()
          + " of class " + path.getLastPathComponent().getClass());
      if (path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path
            .getLastPathComponent();
        Object selectedObject = selectedNode.getUserObject();
        LOGGER.debug("Class of object in the selected tree node: "
            + selectedObject.getClass().getName());
        if (selectedObject instanceof Wrapper) {
          chosenWrapper = (Wrapper) selectedObject;
          LOGGER.debug("Wrapper found: "
              + selectedObject);
        }
      }
    }

    return chosenWrapper;
  }

  /**
   * Allow the user to select a file, which is expected to be an ontology, and
   * then load the file.
   */
  private void openOntologyFile() {
    JFileChooser fileChooser;
    File chosenFile;

    if (lastDirectoryUsed == null) {
      lastDirectoryUsed = new File(".");
    }

    fileChooser = new JFileChooser(lastDirectoryUsed);

    fileChooser.showOpenDialog(this);
    chosenFile = fileChooser.getSelectedFile();

    if (chosenFile != null) {
      loadOntologyFile(chosenFile);
      // } else {
      // JOptionPane.showMessageDialog(this, "No file to load",
      // "No File Selected", JOptionPane.WARNING_MESSAGE);
    }
  }

  /**
   * Allow the user to select a file, which is expected to be an ontology, and
   * then load the file.
   */
  private void openSparqlQueryFile() {
    JFileChooser fileChooser;
    File chosenFile;

    if (lastDirectoryUsed == null) {
      lastDirectoryUsed = new File(".");
    }

    fileChooser = new JFileChooser(lastDirectoryUsed);

    fileChooser.showOpenDialog(this);
    chosenFile = fileChooser.getSelectedFile();

    if (chosenFile != null) {
      loadSparqlQueryFile(chosenFile);
      // } else {
      // JOptionPane.showMessageDialog(this, "No file to load",
      // "No File Selected", JOptionPane.WARNING_MESSAGE);
    }
  }

  /**
   * Open a recently used ontology file chosen from the file menu.
   * 
   * @param obj
   *          The menu item selected - associated with the recently used file
   */
  private void openRecentOntologyFile(Object obj) {
    int chosenFileIndex = -1;

    for (int index = 0; index < fileOpenRecentTriplesFile.length; ++index) {
      if (obj == fileOpenRecentTriplesFile[index]) {
        chosenFileIndex = index;
      }
    }

    if (chosenFileIndex > -1) {
      loadOntologyFile(recentAssertionsFiles.get(chosenFileIndex));
    }
  }

  /**
   * Open a recently used SPARQL query file chosen from the file menu.
   * 
   * @param obj
   *          The menu item selected - associated with the recently used file
   */
  private void openRecentSparqlQueryFile(Object obj) {
    int chosenFileIndex = -1;

    for (int index = 0; index < fileOpenRecentSparqlFile.length; ++index) {
      if (obj == fileOpenRecentSparqlFile[index]) {
        chosenFileIndex = index;
      }
    }

    if (chosenFileIndex > -1) {
      loadSparqlQueryFile(recentSparqlFiles.get(chosenFileIndex));
    }
  }

  /**
   * Load the provided file as an ontology replacing any assertions currently
   * in the assertions text area.
   * 
   * @param inputFile
   *          The file to load (should be an ontology)
   */
  private void loadOntologyFile(File inputFile) {
    BufferedReader reader;
    String data;
    StringBuffer allData;

    lastDirectoryUsed = inputFile.getParentFile();

    reader = null;
    allData = new StringBuffer();

    try {
      reader = new BufferedReader(new FileReader(inputFile));
      while ((data = reader.readLine()) != null) {
        allData.append(data);
        allData.append('\n');
      }
      assertions.setText(allData.toString());
      assertions.moveCaretPosition(0);
      setStatus("Loaded file: " + inputFile.getName());
      setRdfFile(inputFile);
      addRecentAssertedTriplesFile(inputFile);

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          tabbedPane.setSelectedIndex(0);
        }
      });
    } catch (IOException ioExc) {
      setStatus("Unable to load file: " + inputFile.getName());
      JOptionPane.showMessageDialog(this,
          "Error: Unable to read file\n\n"
              + inputFile.getAbsolutePath() + "\n\n"
              + ioExc.getMessage(), "Error Reading File",
          JOptionPane.ERROR_MESSAGE);
      LOGGER.error("Unable to load the file: "
          + inputFile.getAbsolutePath(), ioExc);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (Throwable throwable) {
          LOGGER.error("Unable to close input file", throwable);
        }
      }
      ontModel = null;
      ontModelNoInference = null;

      isTreeInSyncWithModel = areInferencesInSyncWithModel
          = areSparqlResultsInSyncWithModel = false;

      enableControls(true);
    }
  }

  /**
   * Load the provided file as a SPARQL query replacing any query currently
   * in the query text area.
   * 
   * @param inputFile
   *          The file to load (should be a SPARQL query)
   */
  private void loadSparqlQueryFile(File inputFile) {
    BufferedReader reader;
    String data;
    StringBuffer allData;

    lastDirectoryUsed = inputFile.getParentFile();

    reader = null;
    allData = new StringBuffer();

    try {
      reader = new BufferedReader(new FileReader(inputFile));
      while ((data = reader.readLine()) != null) {
        allData.append(data);
        allData.append('\n');
      }
      sparqlInput.setText(allData.toString());
      sparqlInput.moveCaretPosition(0);
      setStatus("Loaded SPARQL query file: " + inputFile.getName());
      sparqlQueryFile = inputFile;
      addRecentSparqlFile(inputFile);

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          tabbedPane.setSelectedIndex(3);
        }
      });
    } catch (IOException ioExc) {
      setStatus("Unable to load file: " + inputFile.getName());
      JOptionPane.showMessageDialog(this,
          "Error: Unable to read file\n\n"
              + inputFile.getAbsolutePath() + "\n\n"
              + ioExc.getMessage(), "Error Reading File",
          JOptionPane.ERROR_MESSAGE);
      LOGGER.error("Unable to load the file: "
          + inputFile.getAbsolutePath(), ioExc);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (Throwable throwable) {
          LOGGER.error("Unable to close input file", throwable);
        }
      }

      invalidateSparqlResults();

      enableControls(true);
    }
  }

  /**
   * Remove prior SPARQL results if the SPARQL query changes
   */
  private void invalidateSparqlResults() {
    SparqlTableModel tableModel = (SparqlTableModel) sparqlResultsTable
        .getModel();
    tableModel.clearModel();
  }

  /**
   * Save the text from the assertions text area to a file Note that this is
   * not the model and it may not be in an legal syntax for RDF triples. It is
   * simply storing what the user has placed in the text area.
   * 
   * @see writeOntologyModel
   */
  private void saveAssertionsToFile() {
    FileWriter out;
    JFileChooser fileChooser;
    File destinationFile;
    boolean okayToWrite;
    int choice;

    out = null;

    if (lastDirectoryUsed == null) {
      lastDirectoryUsed = new File(".");
    }

    fileChooser = new JFileChooser();

    if (rdfFile != null) {
      fileChooser.setSelectedFile(rdfFile);
    } else {
      fileChooser.setSelectedFile(lastDirectoryUsed);
    }

    choice = fileChooser.showSaveDialog(this);
    destinationFile = fileChooser.getSelectedFile();

    // Did not click save, did not select a file or chose a directory
    // So do not write anything
    if (choice != JFileChooser.APPROVE_OPTION || destinationFile == null
        || (destinationFile.exists() && !destinationFile.isFile())) {
      return; // EARLY EXIT!
    }

    okayToWrite = !destinationFile.exists();
    if (!okayToWrite) {
      int verifyOverwrite;
      verifyOverwrite = JOptionPane.showConfirmDialog(this,
          "The file exists: " + destinationFile.getName()
              + "\n\nOkay to overwrite?", "Overwrite File?",
          JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      okayToWrite = verifyOverwrite == JOptionPane.YES_OPTION;
    }

    if (okayToWrite) {

      LOGGER.info("Write assertions to file, " + destinationFile
          + ", in format: " + assertionLanguage);

      try {
        out = new FileWriter(destinationFile, false);
        out.write(assertions.getText());
        setRdfFile(destinationFile);
        addRecentAssertedTriplesFile(destinationFile);
      } catch (IOException ioExc) {
        LOGGER.error("Unable to write to file: " + destinationFile,
            ioExc);
        throw new RuntimeException("Unable to write output file ("
            + destinationFile + ")", ioExc);
      } finally {
        if (out != null) {
          try {
            out.close();
          } catch (Throwable throwable) {
            LOGGER.error("Failed to close output file: "
                + destinationFile, throwable);
            throw new RuntimeException(
                "Failed to close output file", throwable);
          }
        }
      }
    }
  }

  /**
   * Save the text from the SPARQL query text area to a file.
   * 
   */
  private void saveSparqlQueryToFile() {
    FileWriter out;
    JFileChooser fileChooser;
    File destinationFile;
    boolean okayToWrite;
    int choice;

    out = null;

    if (lastDirectoryUsed == null) {
      lastDirectoryUsed = new File(".");
    }

    fileChooser = new JFileChooser();

    if (sparqlQueryFile != null) {
      fileChooser.setSelectedFile(sparqlQueryFile);
    } else {
      fileChooser.setSelectedFile(lastDirectoryUsed);
    }

    choice = fileChooser.showSaveDialog(this);
    destinationFile = fileChooser.getSelectedFile();

    // Did not click save, did not select a file or chose a directory
    // So do not write anything
    if (choice != JFileChooser.APPROVE_OPTION || destinationFile == null
        || (destinationFile.exists() && !destinationFile.isFile())) {
      return; // EARLY EXIT!
    }

    okayToWrite = !destinationFile.exists();
    if (!okayToWrite) {
      int verifyOverwrite;
      verifyOverwrite = JOptionPane.showConfirmDialog(this,
          "The file exists: " + destinationFile.getName()
              + "\n\nOkay to overwrite?", "Overwrite File?",
          JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      okayToWrite = verifyOverwrite == JOptionPane.YES_OPTION;
    }

    if (okayToWrite) {

      LOGGER.info("Write SPARQL query to file, " + destinationFile);

      try {
        out = new FileWriter(destinationFile, false);
        out.write(sparqlInput.getText());
        sparqlQueryFile = destinationFile;
        addRecentSparqlFile(destinationFile);
      } catch (IOException ioExc) {
        LOGGER.error("Unable to write to file: " + destinationFile,
            ioExc);
        throw new RuntimeException("Unable to write output file ("
            + destinationFile + ")", ioExc);
      } finally {
        if (out != null) {
          try {
            out.close();
          } catch (Throwable throwable) {
            LOGGER.error("Failed to close output file: "
                + destinationFile, throwable);
            throw new RuntimeException(
                "Failed to close output file", throwable);
          }
        }
      }
    }
  }

  /**
   * Get the name of the selected output format
   * 
   * @return the name of the output format (e.g. N3, Turtle, RDF/XML)
   */
  private String getSelectedOutputLanguage() {
    int selectedItem;

    selectedItem = 0;
    for (int index = 1; selectedItem == 0
        && index < setupOutputAssertionLanguage.length; ++index) {
      if (setupOutputAssertionLanguage[index].isSelected()) {
        selectedItem = index;
      }
    }

    return selectedItem == 0 ? assertionLanguage
        : FORMATS[selectedItem - 1];
  }

  private void notifyNewerVersion(NewVersionInformation newVersionInformation) {
    JOptionPane.showMessageDialog(
        this,
        "There is a newer version of Semantic Workbench Available\n"
            + "You are running version " + VERSION
            + " and the latest version is "
            + newVersionInformation.getLatestVersion()
            + "\n\n"
            + newVersionInformation.getDownloadInformation()
            + "\n\n"
            + "New features include:\n"
            + newVersionInformation.getNewFeaturesDescription(),
        "Newer Version Available (" + VERSION + "->"
            + newVersionInformation.getLatestVersion() + ")",
        JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Insert standard prefixes in the assertions text area.
   * 
   * The user must choose the format (syntax) to use.
   */
  private void insertPrefixes() {
    String whichFormat;
    int formatIndex;
    String[] formatsToChoose;
    List<String> formatsAvail;
    StringBuffer prefixesToAdd;
    JTextArea areaToUpdate;

    formatsAvail = new ArrayList<String>();

    for (int format = 0; format < FORMATS.length; ++format) {
      if (STANDARD_PREFIXES[format].length > 0) {
        formatsAvail.add(FORMATS[format]);
      }
    }

    // SPARQL is a special case - not an RDF serialization
    formatsAvail.add("SPARQL (tab)");

    formatsToChoose = new String[formatsAvail.size()];
    for (int format = 0; format < formatsAvail.size(); ++format) {
      formatsToChoose[format] = formatsAvail.get(format);
    }

    whichFormat = (String) JOptionPane.showInputDialog(this,
        "Choose the format for the prefixes", "Choose Format",
        JOptionPane.QUESTION_MESSAGE, null, formatsToChoose, null);

    LOGGER.debug("Chosen format for prefixes: " + whichFormat);

    if (whichFormat != null) {
      formatIndex = getIndexValue(FORMATS, whichFormat);

      // SPARQL - special case - not an RDF serialization format
      if (formatIndex == UNKNOWN) {
        formatIndex = STANDARD_PREFIXES.length - 1;
        areaToUpdate = sparqlInput;
      } else {
        areaToUpdate = assertions;
      }

      LOGGER.debug("Chosen format index for prefixes: " + formatIndex);

      if (formatIndex >= 0) {
        String currentData;

        currentData = areaToUpdate.getText();

        prefixesToAdd = new StringBuffer();
        for (int prefix = 0; prefix < STANDARD_PREFIXES[formatIndex].length; ++prefix) {
          prefixesToAdd
              .append(STANDARD_PREFIXES[formatIndex][prefix]);
          prefixesToAdd.append('\n');
        }

        areaToUpdate.setText(prefixesToAdd.toString() + currentData);
      }
    }
  }

  /**
   * Setup the environment to save the current model to a file.
   * 
   * @see #writeOntologyModel(File)
   */
  private void setupToWriteOntologyModel() {
    JFileChooser fileChooser;
    File destinationFile;
    boolean okayToWrite;
    int choice;

    if (lastDirectoryUsed == null) {
      lastDirectoryUsed = new File(".");
    }

    fileChooser = new JFileChooser();

    if (rdfFile != null) {
      fileChooser.setSelectedFile(rdfFile);
    } else {
      fileChooser.setSelectedFile(lastDirectoryUsed);
    }

    choice = fileChooser.showSaveDialog(this);
    destinationFile = fileChooser.getSelectedFile();

    // Did not click save, did not select a file or chose a directory
    // So do not write anything
    if (choice != JFileChooser.APPROVE_OPTION || destinationFile == null
        || (destinationFile.exists() && !destinationFile.isFile())) {
      return; // EARLY EXIT!
    }

    okayToWrite = !destinationFile.exists();
    if (!okayToWrite) {
      int verifyOverwrite;
      verifyOverwrite = JOptionPane.showConfirmDialog(this,
          "The file exists: " + destinationFile.getName()
              + "\n\nOkay to overwrite?", "Overwrite File?",
          JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      okayToWrite = verifyOverwrite == JOptionPane.YES_OPTION;
    }

    if (okayToWrite) {
      modelExportFile = destinationFile;
      runModelExport();
      // writeOntologyModel(destinationFile);
    }
  }

  /**
   * Writes the triples to a data file.
   * 
   * This writes the current model to the file. The current model is the last
   * model successfully run through the reasoner. The output will be in the
   * same language (N3, Turtle, RDF/XML) as the assertions input unless a
   * specific language was set for output.
   * 
   * @see #setupToWriteOntologyModel()
   * 
   */
  private String writeOntologyModel() {
    FileWriter out = null;
    String message = "model ("
        + (setupOutputModelTypeAssertionsAndInferences.isSelected() ? "assertions and inferences"
            : "assertions only") + ") to " + modelExportFile;

    setStatus("Writing " + message);

    LOGGER.info("Write model to file, " + modelExportFile
        + ", in format: " + assertionLanguage);

    try {
      out = new FileWriter(modelExportFile, false);
      if (setupOutputModelTypeAssertionsAndInferences.isSelected()) {
        LOGGER
            .info("Writing complete model (assertions and inferences)");
        ontModel.writeAll(out, getSelectedOutputLanguage()
            .toUpperCase(), null);
      } else {
        LOGGER.info("Writing assertions only");
        ontModelNoInference.write(out, getSelectedOutputLanguage()
            .toUpperCase());
      }
      message = "Completed writing " + message;
    } catch (IOException ioExc) {
      LOGGER.error("Unable to write to file: " + modelExportFile,
          ioExc);
      setStatus("Failed to write file (check log) " + message);
      throw new RuntimeException("Unable to write output file ("
          + modelExportFile + ")", ioExc);
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (Throwable throwable) {
          LOGGER.error("Failed to close output file: "
              + modelExportFile, throwable);
          throw new RuntimeException(
              "Failed to close output file", throwable);
        }
      }
    }

    return message;
  }

  /**
   * Setup the environment to save the current SPARQL results to a file.
   * 
   * @see #writeSparqlResults(File)
   */
  private void setupToWriteSparqlResults() {
    JFileChooser fileChooser;
    File destinationFile;
    boolean okayToWrite;
    int choice;

    if (lastDirectoryUsed == null) {
      lastDirectoryUsed = new File(".");
    }

    fileChooser = new JFileChooser();

    if (sparqlResultsExportFile != null) {
      fileChooser.setSelectedFile(sparqlResultsExportFile);
    } else {
      fileChooser.setSelectedFile(lastDirectoryUsed);
    }

    choice = fileChooser.showSaveDialog(this);
    destinationFile = fileChooser.getSelectedFile();

    // Did not click save, did not select a file or chose a directory
    // So do not write anything
    if (choice != JFileChooser.APPROVE_OPTION || destinationFile == null
        || (destinationFile.exists() && !destinationFile.isFile())) {
      return; // EARLY EXIT!
    }

    okayToWrite = !destinationFile.exists();
    if (!okayToWrite) {
      int verifyOverwrite;
      verifyOverwrite = JOptionPane.showConfirmDialog(this,
          "The file exists: " + destinationFile.getName()
              + "\n\nOkay to overwrite?", "Overwrite File?",
          JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      okayToWrite = verifyOverwrite == JOptionPane.YES_OPTION;
    }

    if (okayToWrite) {
      sparqlResultsExportFile = destinationFile;
      runSparqlResultsExport();
      // writeOntologyModel(destinationFile);
    }
  }

  /**
   * Writes the SPARQL results to a file.
   * 
   * This writes the current SPARQL results to the file. The format used is
   * determined by the configuration (e.g. setupExportSparqlResultsAsCsv and
   * setupExportSparqlResultsAsTsv menu items).
   * 
   * @see #setupToWriteSparqlResults()
   * 
   */
  private String writeSparqlResults() {
    PrintWriter out = null;
    boolean toCsv;

    // Either CSV or TSV currently
    toCsv = setupExportSparqlResultsAsCsv.isSelected();

    String message = "SPARQL results (" + (toCsv ? "CSV" : "TSV") + ") to "
        + sparqlResultsExportFile;

    setStatus("Writing " + message);

    LOGGER.info("Write SPARQL results to file, " + sparqlResultsExportFile);

    try {
      out = new PrintWriter(new FileWriter(sparqlResultsExportFile, false));

      SparqlTableModel model = (SparqlTableModel) sparqlResultsTable.getModel();

      // Output column names
      for (int columnNumber = 0; columnNumber < model.getColumnCount(); ++columnNumber) {
        if (columnNumber > 0) {
          out.print(toCsv ? ',' : '\t');
        }

        if (toCsv) {
          out.print(TextProcessing.formatForCsvColumn(model
              .getColumnName(columnNumber)));
        } else {
          out.print(TextProcessing.formatForTsvColumn(model
              .getColumnName(columnNumber)));
        }
      }

      out.println();

      // Output row data
      for (int rowNumber = 0; rowNumber < model.getRowCount(); ++rowNumber) {
        for (int columnNumber = 0; columnNumber < model.getColumnCount(); ++columnNumber) {
          if (columnNumber > 0) {
            out.print(toCsv ? ',' : '\t');
          }

          if (toCsv) {
            out.print(TextProcessing.formatForCsvColumn((String) model
                .getValueAt(rowNumber, columnNumber)));
          } else {
            out.print(TextProcessing.formatForTsvColumn((String) model
                .getValueAt(rowNumber, columnNumber)));
          }
        }
        out.println();
      }

      message = "Completed writing " + message;
    } catch (IOException ioExc) {
      LOGGER.error("Unable to write to file: " + sparqlResultsExportFile,
          ioExc);
      setStatus("Failed to write file (check log) " + message);
      throw new RuntimeException("Unable to write output file ("
          + sparqlResultsExportFile + ")", ioExc);
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (Throwable throwable) {
          LOGGER.error("Failed to close output file: "
              + sparqlResultsExportFile, throwable);
          throw new RuntimeException(
              "Failed to close output file", throwable);
        }
      }
    }

    return message;
  }

  /**
   * Presents a classic "About" box to the user displaying the current version
   * of the application.
   */
  private void about() {
    StringBuffer message;

    message = new StringBuffer();

    message.append("Semantic Workbench\n\nVersion: ");
    message.append(VERSION);
    message.append("\n\nDavid Read\n\n");
    message.append("Jena Version: ");
    message.append(getJenaVersion());
    message.append("\n");
    message.append("Pellet Version: ");
    message.append(getPelletVersion());

    JOptionPane.showMessageDialog(this, message.toString(),
        "About Semantic Workbench", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Get the version of the loaded Jena library
   * 
   * @return The version of the Jena library
   */
  private String getJenaVersion() {
    return ARQ.VERSION;
  }

  /**
   * Get the version of the loaded Pellet library
   * 
   * @return The version of the Pellet library
   */
  private String getPelletVersion() {
    return VersionInfo.getInstance().getVersionString() + " ("
        + VersionInfo.getInstance().getReleaseDate() + ")";
  }

  /**
   * End the application
   */
  private void closeApplication() {
    saveProperties();
    setVisible(false);
    LOGGER.info("Shutdown");
    System.exit(0);
  }

  @Override
  public void update(Observable o, Object arg) {
    LOGGER.debug("Update received from " + o.getClass().getName());

    if (o instanceof SparqlServer) {
      updateSparqlServerInfo();
    } else if (o instanceof CheckLatestVersion) {
      notifyNewerVersion((NewVersionInformation) arg);
    }
  }

  @Override
  public void windowActivated(WindowEvent arg0) {
  }

  @Override
  public void windowClosed(WindowEvent arg0) {
  }

  @Override
  public void windowClosing(WindowEvent arg0) {
    closeApplication();
  }

  @Override
  public void windowDeactivated(WindowEvent arg0) {
  }

  @Override
  public void windowDeiconified(WindowEvent arg0) {
  }

  @Override
  public void windowIconified(WindowEvent arg0) {
  }

  @Override
  public void windowOpened(WindowEvent arg0) {
  }

  private class OntologyModelTreeMouseListener extends MouseAdapter {
    public void mouseClicked(MouseEvent event) {
      if (event.getButton() == MouseEvent.BUTTON1) {
        processOntologyModelTreeLeftClick(event);
      } else {
        // Assume right-mouse click (e.g. not button 1)
        processOntologyModelTreeRightClick(event);
      }
    }
  }

  private class FilterValuePopup extends JPopupMenu implements ActionListener {
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 6587807055541029847L;

    private Wrapper wrappedObject;
    private JMenuItem menuItem;

    public FilterValuePopup(Wrapper wrappedObject) {
      this.wrappedObject = wrappedObject;

      menuItem = new JMenuItem("Filter out: " + wrappedObject.toString());
      menuItem.addActionListener(this);
      add(menuItem);

      // Don't need listener, no action to take for cancel
      add(new JMenuItem("Cancel"));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
      if (event.getSource() instanceof JMenuItem) {
        JMenuItem source = (JMenuItem) event.getSource();
        if (source == menuItem) {
          LOGGER.debug("FilterValuePopup choice: "
              + wrappedObject.getClass().toString() + " - " + wrappedObject);
          addToFilter(wrappedObject);
        }
      }
    }
  }

  /**
   * Executes the reasoner in response to action from any widget being
   * monitored
   */
  private class ReasonerListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      runReasoner();
    }
  }

  /**
   * Used to detect configuration changes that would cause the reasoner to
   * behave differently, thereby invalidating the current model.
   * 
   * @author David Read
   * 
   */
  private class ReasonerConfigurationChange implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      invalidateModel();
    }
  }

  /**
   * Executes the SPARQL query in response to action from any widget being
   * monitored
   */
  private class SparqlListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      runSPARQL();
    }
  }

  /**
   * Opens an ontology file
   */
  private class FileAssertedTriplesOpenListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      openOntologyFile();
    }
  }

  /**
   * Loads a recently accessed ontology file
   */
  private class RecentAssertedTriplesFileOpenListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      openRecentOntologyFile(e.getSource());
    }
  }

  /**
   * Loads a recently accessed ontology file
   */
  private class RecentSparqlFileOpenListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      openRecentSparqlQueryFile(e.getSource());
    }
  }

  /**
   * Opens a SPARQL query file
   */
  private class FileSparqlOpenListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      openSparqlQueryFile();
    }
  }

  /**
   * Exits the application
   */
  private class EndApplicationListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      closeApplication();
    }
  }

  /**
   * Evaluates control status
   */
  private class SparqlModelChoiceListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      enableControls(true);
    }
  }

  /**
   * Inserts standard prefixes
   */
  private class InsertPrefixesListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      insertPrefixes();
    }
  }

  /**
   * Writes assertions to a file
   */
  private class FileAssertedTriplesSaveListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      saveAssertionsToFile();
    }
  }

  /**
   * Writes SPARQL query to a file
   */
  private class FileSparqlSaveListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      saveSparqlQueryToFile();
    }
  }

  /**
   * Expands the nodes in the tree representation of the model
   */
  private class ExpandTreeListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      runExpandAllTreeNodes();
    }
  }

  /**
   * Collapses the nodes in the tree representation of the model
   */
  private class CollapseTreeListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      runCollapseAllTreeNodes();
    }
  }

  /**
   * Allows the user to change the display font
   */
  private class FontSetupListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      configureFont();
    }
  }

  /**
   * Writes the current model to an ontology file
   */
  private class ModelSerializerListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      setupToWriteOntologyModel();
    }
  }

  /**
   * Writes the current SPARQL results to a CSV file
   */
  private class FileSparqlResultsSaveListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      setupToWriteSparqlResults();
    }
  }

  /**
   * Allows the user to edit the list of classes filtered in the tree view
   */
  private class EditFilteredClassesListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      editFilterMap(classesToSkipInTree);
    }
  }

  /**
   * Allows the user to edit the list of properties filtered in the tree view
   */
  private class EditFilteredPropertiesListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      editFilterMap(predicatesToSkipInTree);
    }
  }

  /**
   * Enable or disable the use of a proxy for network-based requests
   */
  private class ProxyStatusChangeListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      changeProxyMode();
    }
  }

  /**
   * Configure the proxy
   */
  private class ProxySetupListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      configureProxy();
    }
  }

  /**
   * Start the SPARQL server
   */
  private class SparqlServerStartupListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      startSparqlServer();
    }
  }

  /**
   * Stop the SPARQL server
   */
  private class SparqlServerShutdownListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      stopSparqlServer();
    }
  }

  /**
   * Publish the current ontology model to the SPARQL server
   */
  private class SparqlServerPublishModelListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      publishModelToTheSparqlServer();
    }
  }

  /**
   * Publish the current ontology model to the SPARQL server
   */
  private class SparqlServerConfigurationListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      configureSparqlServer();
    }
  }

  /**
   * Displays About dialog
   */
  private class AboutListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      about();
    }
  }

  /**
   * Creates tree view of model
   * 
   * @author David Read
   * 
   */
  private class GenerateTreeListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      runCreateTreeFromModel();
      // createTreeFromModel();
    }
  }

  /**
   * Creates a list of inferred triples from the model
   * 
   * @author David Read
   * 
   */
  private class GenerateInferredTriplesListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      runIdentifyInferredTriples();
      // identifyInferredTriples();
    }
  }

  /**
   * Updates state of controls based on changes to text widgets being
   * monitored
   */
  private class UserInputListener implements KeyListener {

    public void keyPressed(KeyEvent arg0) {
    }

    public void keyReleased(KeyEvent arg0) {
      if (arg0.getSource() == assertions) {
        invalidateModel();
      } else if (arg0.getSource() == sparqlInput) {
        invalidateSparqlResults();
      }

      enableControls(true);
    }

    public void keyTyped(KeyEvent arg0) {
      enableControls(true);
    }
  }

  /**
   * The execution point for the program. Creates an instance of the
   * SemanticWorkbench class.
   * 
   * @param args
   *          The array of input arguments, not used yet
   */
  public static void main(String[] args) {
    org.apache.log4j.PropertyConfigurator.configure("log4j.properties");
    new SemanticWorkbench();
  }
}

/**
 * A basic table model for reporting the SPARQL results
 * 
 * @author David Read
 */
@SuppressWarnings("serial")
class SparqlTableModel extends AbstractTableModel {
  /**
   * Logger Instance
   */
  private static Logger LOGGER = Logger.getLogger(SparqlTableModel.class);

  /**
   * The result rows
   */
  private List<List<String>> rows = new ArrayList<List<String>>();

  /**
   * The column labels
   */
  private List<String> columnLabels = new ArrayList<String>();

  /**
   * Creates a new table model populating the model with the supplied results
   * 
   * @param results
   *          A Jena query result set
   */
  public SparqlTableModel(ResultSet results, OntModel ontModel,
      boolean addLiteralFlag, boolean includeDataType,
      boolean useFqn) {
    setupModel(results, ontModel, addLiteralFlag, includeDataType, useFqn);
  }

  /**
   * Creates a new table model that contains no data
   */
  public SparqlTableModel() {

  }

  /**
   * Removes all results from the model.
   */
  public void clearModel() {
    rows.clear();
    columnLabels.clear();
    fireTableStructureChanged();
  }

  /**
   * Replaces the current data in the table model with the supplied data.
   * 
   * @param results
   *          A Jena query result set
   */
  public void setupModel(ResultSet results, OntModel ontModel,
      boolean addLiteralFlag, boolean includeDataType,
      boolean useFqn) {
    List<String> columns;
    rows.clear();
    columnLabels.clear();

    columns = results.getResultVars();
    for (String colName : columns) {
      columnLabels.add(colName);
    }

    while (results.hasNext()) {
      QuerySolution solution = results.next();
      String value;

      /*
       * if (columnLabels.size() == 0) {
       * Iterator<String> names;
       * names = solution.varNames();
       * while (names.hasNext()) {
       * columnLabels.add(names.next().toString());
       * LOGGER.debug("Added column label: "
       * + columnLabels.get(columnLabels.size() - 1));
       * }
       * }
       */

      List<String> row = new ArrayList<String>();

      for (String var : columnLabels) {
        if (solution.get(var) == null) {
          row.add("");
        } else if (solution.get(var).isLiteral()) {
          value = solution.getLiteral(var).toString();
          int caratPosit;
          if (!includeDataType && value != null
              && (caratPosit = value.indexOf('^')) > -1) {
            value = value.substring(0, caratPosit);
          }
          if (addLiteralFlag) {
            value = "Lit: " + value;
          }
          row.add(value);
        } else {
          value = solution.getResource(var).toString();
          int hashAt;
          if (!useFqn && ontModel != null && (hashAt = value.indexOf('#')) > -1) {
            String namespace = value.substring(0, hashAt + 1);
            String prefix = ontModel.getNsURIPrefix(namespace);
            if (prefix == null) {
              prefix = namespace;
            } else {
              prefix = prefix + ":";
            }
            value = prefix + value.substring(hashAt + 1);
          }
          row.add(value);
        }
      }

      rows.add(row);
      LOGGER.debug("Added row with col count: " + row.size());
    }

    LOGGER.debug("Total rows in results: " + rows.size());

    fireTableStructureChanged();
  }

  /**
   * Get the number of columns in the resulting model.
   */
  public int getColumnCount() {
    return columnLabels.size();
  }

  /**
   * Get the number of rows in the resulting model.
   */
  public int getRowCount() {
    return rows.size();
  }

  /**
   * Get the value at the supplied row and column (cell).
   */
  public Object getValueAt(int arg0, int arg1) {
    return rows.get(arg0).get(arg1);
  }

  /**
   * The the label for the supplied column number.
   */
  public String getColumnName(int col) {
    return columnLabels.get(col);
  }
}

/**
 * FontChooser
 * 
 * From: http://examples.oreilly.com/jswing2/code/ch12/FontChooser.java
 * 
 * A font chooser that allows users to pick a font by name, size, style, and
 * color. The color selection is provided by a JColorChooser pane. This dialog
 * builds an AttributeSet suitable for use with JTextPane.
 * 
 * DSR: Minor alteration to make all attributes private
 */
@SuppressWarnings("serial")
class FontChooser extends JDialog implements Runnable, ActionListener,
    KeyListener {

  private JColorChooser colorChooser;
  private JComboBox fontName;
  private JCheckBox fontBold, fontItalic;
  private JTextField fontSize;
  private JLabel previewLabel;
  private SimpleAttributeSet attributes;
  private Font newFont;
  private Color newColor;
  private Thread previewThread;
  private List<FontData> changeStack;

  public FontChooser(Frame parent) {
    super(parent, "Font Chooser", true);
    setSize(450, 450);
    attributes = new SimpleAttributeSet();
    changeStack = new ArrayList<FontData>();

    // Make sure that any way the user cancels the window does the right
    // thing
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        closeAndCancel();
      }
    });

    // Start the long process of setting up our interface
    Container c = getContentPane();

    JPanel fontPanel = new JPanel();
    fontName = new JComboBox(new String[] { "TimesRoman", "Helvetica",
        "Courier" });
    fontName.setSelectedIndex(1);
    fontName.addActionListener(this);
    fontSize = new JTextField("12", 4);
    fontSize.setHorizontalAlignment(SwingConstants.RIGHT);
    fontSize.addActionListener(this);
    fontSize.addKeyListener(this);
    fontBold = new JCheckBox("Bold");
    fontBold.setSelected(true);
    fontBold.addActionListener(this);
    fontItalic = new JCheckBox("Italic");
    fontItalic.addActionListener(this);

    fontPanel.add(fontName);
    fontPanel.add(new JLabel(" Size: "));
    fontPanel.add(fontSize);
    fontPanel.add(fontBold);
    fontPanel.add(fontItalic);

    c.add(fontPanel, BorderLayout.NORTH);

    // Set up the color chooser panel and attach a change listener so that
    // color
    // updates get reflected in our preview label.
    colorChooser = new JColorChooser(Color.black);
    colorChooser.getSelectionModel().addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            updatePreviewColor();
          }
        });
    c.add(colorChooser, BorderLayout.CENTER);

    JPanel previewPanel = new JPanel(new BorderLayout());
    previewLabel = new JLabel("Here's a sample of this font.");
    previewLabel.setForeground(colorChooser.getColor());
    previewPanel.add(previewLabel, BorderLayout.CENTER);

    // Add in the Ok and Cancel buttons for our dialog box
    JButton okButton = new JButton("Ok");
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        closeAndSave();
      }
    });
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        closeAndCancel();
      }
    });

    JPanel controlPanel = new JPanel();
    controlPanel.add(okButton);
    controlPanel.add(cancelButton);
    previewPanel.add(controlPanel, BorderLayout.SOUTH);

    // Give the preview label room to grow.
    previewPanel.setMinimumSize(new Dimension(100, 100));
    previewPanel.setPreferredSize(new Dimension(100, 100));

    c.add(previewPanel, BorderLayout.SOUTH);
  }

  /**
   * Detect when the font is changed and make a
   * new font for the preview label.
   */
  public void actionPerformed(ActionEvent ae) {
    fontChanged();
  }

  /**
   * Set the initial color for the color chooser.
   * 
   * @param color
   *          The initial color that the chooser should have selected
   */
  public void setColor(Color color) {
    colorChooser.setColor(color);
  }

  /**
   * Set the initial font for the font chooser.
   */
  public void setFont(Font font) {
    fontSize.setText(font.getSize() + "");
    fontBold.setSelected((font.getStyle() & Font.BOLD) == Font.BOLD);
    fontItalic.setSelected((font.getStyle() & Font.ITALIC) == Font.ITALIC);

    for (int index = 0; index < fontName.getItemCount(); ++index) {
      if (fontName.getItemAt(index).toString().equalsIgnoreCase(font.getName())) {
        fontName.setSelectedIndex(index);
      }
    }
  }

  /**
   * Process the new font and update the preview field.
   */
  private void fontChanged() {
    System.out.println("0: " + new java.util.Date());
    updatePreviewFont();
    System.out.println("0.1: " + new java.util.Date());
    showPreview();
    System.out.println("0.2: " + new java.util.Date());
  }

  /**
   * Get the appropriate font from our attributes object and update
   * the preview label
   */
  protected void updatePreviewFont() {
    // String name = StyleConstants.getFontFamily(attributes);
    // boolean bold = StyleConstants.isBold(attributes);
    // boolean ital = StyleConstants.isItalic(attributes);
    // int size = StyleConstants.getFontSize(attributes);

    System.out.println("5: " + new java.util.Date());
    String name = (String) fontName.getSelectedItem();
    boolean bold = fontBold.isSelected();
    boolean ital = fontItalic.isSelected();
    int size;
    try {
      size = Integer.parseInt(fontSize.getText());
    } catch (Throwable throwable) {
      size = 12; // Default
      System.out.println("Not a legitimate number for the font size");
      throwable.printStackTrace();
    }

    // Bold and italic don't work properly in beta 4.
    System.out.println("5.1: " + new java.util.Date());
    Font f = new Font(name, (bold ? Font.BOLD : 0)
        | (ital ? Font.ITALIC : 0), size);
    // previewLabel.setFont(f);
    System.out.println("6: " + new java.util.Date());
    newFont = f;
  }

  /**
   * Get the appropriate color from our chooser and update previewLabel.
   */
  protected void updatePreviewColor() {
    // previewLabel.setForeground(colorChooser.getColor());
    System.out.println("7: " + new java.util.Date());
    newColor = colorChooser.getColor();
    // System.out.println("New Color: " + newColor);
    // Manually force the label to repaint
    // previewLabel.repaint();
    showPreview();
  }

  /**
   * Run this chooser.
   */
  public void run() {
    Font font;
    Color color;
    boolean more;

    System.out.println("8: " + new java.util.Date());

    do {
      synchronized (changeStack) {
        System.out.println("8.1: " + new java.util.Date());
        font = changeStack.get(0).getFont();
        color = changeStack.get(0).getColor();
        changeStack.remove(0);
      }
      try {
        System.out.println("8.2: " + new java.util.Date());
        previewLabel.setFont(font);
        System.out.println("8.3: " + new java.util.Date());
        previewLabel.setForeground(color);
        System.out.println("8.4: " + new java.util.Date());
        /*
         * for (int x = 0; x < 10; ++x) {
         * previewLabel.setText("Sample Text Message (" + x + ")");
         * try {
         * Thread.sleep(500);
         * } catch (InterruptedException ie) {
         * System.out.println("Interrupted exception");
         * ie.printStackTrace();
         * }
         * }
         */
      } catch (Throwable throwable) {
        // Ignore any errors
      }
      synchronized (changeStack) {
        more = changeStack.size() > 0;
        System.out.println("8.5: " + new java.util.Date());
      }
    } while (more);
    previewThread = null;
  }

  /**
   * Show the preview window.
   */
  private synchronized void showPreview() {
    synchronized (changeStack) {
      changeStack.add(new FontData(newFont, newColor));
    }
    if (previewThread == null) {
      System.out.println("9: " + new java.util.Date());
      previewThread = new Thread(this);
      System.out.println("9.1: " + new java.util.Date());
      previewThread.start();
      System.out.println("9.2: " + new java.util.Date());
    }
  }

  /**
   * Get the currently selected Font.
   * 
   * @return The currently selected Font
   */
  public Font getNewFont() {
    return newFont;
  }

  /**
   * Get the currently selected color.
   * 
   * @return The currently selected Color
   */
  public Color getNewColor() {
    return newColor;
  }

  /**
   * Get the current set of attributes associated with the selected font.
   * 
   * @return The current set of attributes for the font
   */
  public AttributeSet getAttributes() {
    return attributes;
  }

  /**
   * Close the chooser dialog and save the user's selections.
   */
  public void closeAndSave() {
    // Save font & color information
    // newFont = previewLabel.getFont();
    // newColor = previewLabel.getForeground();

    // Close the window
    setVisible(false);
  }

  /**
   * Close the chooser dialog and discard the user's selections.
   */
  public void closeAndCancel() {
    // Erase any font information and then close the window
    newFont = null;
    newColor = null;
    setVisible(false);
  }

  @Override
  public void keyPressed(KeyEvent e) {

  }

  @Override
  public void keyReleased(KeyEvent e) {
    // if (key.getKeyCode() == 127) { // Delete key - no keyTyped Event
    System.out.println("10: " + new java.util.Date());
    fontChanged();
    System.out.println("10.1: " + new java.util.Date());
    // }
  }

  @Override
  public void keyTyped(KeyEvent e) {

  }
}

/**
 * Class for housing information for a font and forground color selection.
 * 
 * @author David Read
 * 
 */
class FontData {
  /**
   * A Font instance
   */
  private Font font;

  /**
   * A Color instance (foreground)
   */
  private Color color;

  /**
   * Create a FontData instance with the supplied Font and foreground Color.
   * 
   * @param font
   *          The Font instance
   * @param color
   *          The foreground Color instance
   */
  public FontData(Font font, Color color) {
    setFont(font);
    setColor(color);
  }

  /**
   * Set the Font instance for this FontData instance.
   * 
   * @param pFont
   *          The Font being set.
   */
  private void setFont(Font pFont) {
    font = pFont;
  }

  /**
   * Get the current Font from this FontData instance.
   * 
   * @return The current Font
   */
  public Font getFont() {
    return font;
  }

  /**
   * Set the foreground Color instance for this FontData instance.
   * 
   * @param pColor
   *          The foreground Color
   */
  private void setColor(Color pColor) {
    color = pColor;
  }

  /**
   * Get the current foreground Color from theis FontData instance.
   * 
   * @return The current foreground Color
   */
  public Color getColor() {
    return color;
  }
}