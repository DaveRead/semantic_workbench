package com.monead.semantic.workbench;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.jena.atlas.web.auth.SimpleAuthenticator;
import org.apache.jena.riot.RiotException;
import org.apache.log4j.Logger;
//import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.utils.VersionInfo;

//import com.complexible.stardog.StardogException;
//import com.complexible.stardog.api.ConnectionConfiguration;
//import com.complexible.stardog.jena.SDJenaFactory;
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
import com.hp.hpl.jena.query.QueryParseException;
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
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.monead.semantic.workbench.images.ImageLibrary;
import com.monead.semantic.workbench.security.StarDogSparqlAuthenticator;
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
import com.monead.semantic.workbench.utilities.FileSource;
import com.monead.semantic.workbench.utilities.FontChooser;
import com.monead.semantic.workbench.utilities.NewVersionInformation;
import com.monead.semantic.workbench.utilities.ReasonerSelection;
import com.monead.semantic.workbench.utilities.TextProcessing;

/**
 * SemanticWorkbench - A GUI to input assertions, work with inferencing engines
 * and SPARQL queries
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
 * TODO Provide an option: When writing SPARQL results to a file, wrap URIs with
 * <>,
 * including data types on literals
 * 
 * TODO Add support for SPARQL queries that use the model and URLs
 * 
 * TODO Allow editing of an ontology/model from the tree view (graphical
 * ontology editor)
 * 
 * TODO Get Pellet working with new version of Jena or drop claimed support for
 * it
 * 
 * TODO Add a "File New" for assertions to clear hasIncompleteAssertionsInput
 * 
 * TODO Add a cancel feature for long running tasks
 * 
 * TODO Alerts for SPARQL service requests that are killed due to timeout
 * 
 * TODO Remember last SPARQL query default graph URI value
 * 
 * TODO Store service and default named graph URI in the SPARQL query file as an
 * encoded comment
 * 
 * TODO Encode user id and password in SPARQL query file as encrypted value
 * using a key entered by the user at startup if they enable that feature
 * 
 * TODO Expand tree node option, expands all nodes under the selected node
 * 
 * @author David Read
 * 
 */
public class SemanticWorkbench extends JFrame implements Runnable,
    WindowListener, Observer {
  /**
   * The version identifier
   */
  public static final String VERSION = "1.9.2";

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 20140303;

  /**
   * The set of formats that can be loaded. These are defined by Jena
   */
  private static final String[] FORMATS = {
      "N3", "N-Triples", "RDF/XML", "Turtle"
  };

  /**
   * Tab number for the assertions
   */
  private static final int TAB_NUMBER_ASSERTIONS = 0;

  /**
   * Tab number for the inferences
   */
  private static final int TAB_NUMBER_INFERENCES = 1;

  /**
   * Tab number for the tree view
   */
  private static final int TAB_NUMBER_TREE_VIEW = 2;

  /**
   * Tab number for the SPARQL interactions
   */
  private static final int TAB_NUMBER_SPARQL = 3;

  /**
   * Prefix of comment in saved SPARQL query to indicate the service URL used
   */
  private static final String SPARQL_QUERY_SAVE_SERVICE_URL_PARAM = "SERVICE_URL:";

  /**
   * Value to be used as the value for the service URL comment in saved SPARQL
   * query to indicate that the local model (or service keyword) was used.
   * This value should not be changed since it is stored in saved SPARQL query
   * files to indicate the query is to be executed against the local model.
   */
  private static final String SPARQL_QUERY_SAVE_SERVICE_URL_VALUE_FOR_NO_SERVICE_URL = "N/A";

  /**
   * Text used to indicate a setting is not applicable
   */
  private static final String NOT_APPLICABLE_DISPLAY = "N/A";

  /**
   * Default value used for true/false property values set to true
   */
  private static final String DEFAULT_PROPERTY_VALUE_YES = "Yes";

  /**
   * Default value used for true/false property values set to false
   */
  private static final String DEFAULT_PROPERTY_VALUE_NO = "No";

  /**
   * Value to identify a Comma Separated Value export format
   */
  private static final String EXPORT_FORMAT_LABEL_CSV = "CSV";

  /**
   * Value to identify a Tab Separated Value export format
   */
  private static final String EXPORT_FORMAT_LABEL_TSV = "TSV";

  /**
   * Minimum allowed font size
   */
  private static final int MINIMUM_FONT_SIZE = 5;

  /**
   * Fraction of screen size the restored window dimension may occupy
   */
  private static final double MAXIMUM_RESTORE_RELATIVE_WINDOW_TO_SCREEN_SIZE = 0.9;

  /**
   * Prefix of the comment in the saved SPARQL query to indicate the default
   * graph URI used
   */
  private static final String SPARQL_QUERY_SAVE_SERVICE_DEFAULT_GRAPH_PARAM = "DEFAULT_GRAPH_URI:";

  /**
   * Logger Instance
   */
  private static final Logger LOGGER = Logger
      .getLogger(SemanticWorkbench.class);

  /**
   * Standard prefixes
   * 
   * In N3, N-Triples, RDF/XML and Turtle Order matches that of the FORMAT
   * array
   */
  private static final String[][] STANDARD_PREFIXES = {
      // N3
      {
          "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.",
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
      {
          "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.",
          "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.",
          "@prefix owl: <http://www.w3.org/2002/07/owl#>.",
          "@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.",
          "@prefix dc: <http://purl.org/dc/elements/1.1/>.", },
      // SPARQL
      {
          "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
          "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
          "prefix owl: <http://www.w3.org/2002/07/owl#>",
          "prefix xsd: <http://www.w3.org/2001/XMLSchema#>",
          "prefix dc: <http://purl.org/dc/elements/1.1/>", }
  };

  /**
   * Service URLS to add in the drop down if none are defined
   */
  private static final String[] DEFAULT_SERVICE_URLS = {
      "http://semantic.monead.com/vehicleinfo/mileage",
      "http://dbpedia.org/sparql",
      "http://lod.openlinksw.com/sparql/",
  };

  /**
   * Default location for the divider location on the SPARQL tab
   */
  private static final int DEFAULT_SPARQL_QUERY_AND_RESULTS_DIVIDER_LOCATION = 150;

  /**
   * The set of reasoners that are supported
   */
  private static final List<ReasonerSelection> REASONER_SELECTIONS;

  /**
   * Constant used if a value cannot be found in an array
   */
  private static final int UNKNOWN = -1;

  /**
   * Maximum number of previous file names to retain
   */
  private static final int MAX_PREVIOUS_FILES_TO_STORE = 10;

  /**
   * The prefix for SPARQL results files exported with the direct export option
   */
  private static final String SPARQL_DIRECT_EXPORT_FILE_PREFIX = "Sem_WB_SPARQL_Results_";

  /**
   * Number format for presenting integers in comma-separated form
   */
  private static final NumberFormat INTEGER_COMMA_FORMAT = new DecimalFormat(
      "#,##0");

  /**
   * Normal foreground color for a tab's text
   */
  private static final Color NORMAL_TAB_FG = new Color(51, 51, 51);

  /**
   * Normal background color for a tab
   */
  private static final Color NORMAL_TAB_BG = new Color(184, 207, 229);

  /**
   * Maximum number of bytes to load into the assertions text area.
   * If a file is loaded which exceeds this amount, only the first
   * portion of the file will be loaded. However, the model
   * will be built using the whole file.
   * 
   * TODO provide a scrolling window over the entire file
   */
  private static final long MAX_ASSERTION_BYTES_TO_LOAD_INTO_TEXT_AREA = 10 * 1024 * 1024;

  /**
   * File name for the properties file
   */
  private static final String PROPERTIES_FILE_NAME = "semantic_workbench.properties";

  /**
   * Property file property name: store the last top X location of the main
   * window
   */
  private static final String PROP_LAST_TOP_X_POSITION = "LastTopPositionX";

  /**
   * Property file property name: store the last top Y location of the main
   * window
   */
  private static final String PROP_LAST_TOP_Y_POSITION = "LastTopPositionY";

  /**
   * Property file property name: store the last width of the main window
   */
  private static final String PROP_LAST_WIDTH = "LastWidth";

  /**
   * Property file property name: store the last height of the main window
   */
  private static final String PROP_LAST_HEIGHT = "LastHeight";

  /**
   * Property file property name: store the last directory read/written
   */
  private static final String PROP_LAST_DIRECTORY = "LastDirectory";

  /**
   * Property file property name: store the last output format chosen
   */
  private static final String PROP_OUTPUT_FORMAT = "OutputFormat";

  /**
   * Property file property name: store the last model output selection chosen
   * (assertions versus assertions and inferences)
   */
  private static final String PROP_OUTPUT_CONTENT = "OutputContent";

  /**
   * Property file property name: store the last FQN display setting
   */
  private static final String PROP_SHOW_FQN_NAMESPACES = "ShowFqnNamespaces";

  /**
   * Property file property name: store the last datatype display setting
   */
  private static final String PROP_SHOW_DATATYPES_ON_LITERALS = "ShowDatatypesOnLiterals";

  /**
   * Property file property name: store the last literal flag display setting
   */
  private static final String PROP_FLAG_LITERALS_IN_RESULTS = "FlagLiteralsInResults";

  /**
   * Property file property name: store the last font setting
   */
  private static final String PROP_FONT_NAME = "FontName";

  /**
   * Property file property name: store the last font size setting
   */
  private static final String PROP_FONT_SIZE = "FontSize";

  /**
   * Property file property name: store the last font style setting
   */
  private static final String PROP_FONT_STYLE = "FontStyle";

  /**
   * Property file property name: store the last font color setting
   */
  private static final String PROP_FONT_COLOR = "FontColor";

  /**
   * Property file property name: prefix for storing each class name to be
   * skippe din the tree
   */
  private static final String PROP_PREFIX_SKIP_CLASS = "TreeClassToSkip_";

  /**
   * Property file property name: prefix for storing each predicate name to be
   * skippe din the tree
   */
  private static final String PROP_PREFIX_SKIP_PREDICATE = "TreePredicateToSkip_";

  /**
   * Property file property name: store the last reasoning level setting
   */
  private static final String PROP_REASONING_LEVEL = "ReasoningLevel";

  /**
   * Property file property name: store the last assertions input format setting
   */
  private static final String PROP_INPUT_LANGUAGE = "InputFormat";

  /**
   * Property file property name: prefix for storing the set of recently
   * accessed assertion files and URLs
   */
  private static final String PROP_PREFIX_RECENT_ASSERTIONS_FILE = "RecentAssertedTriplesFile_";

  /**
   * Property file property name: prefix for storing the set of recently
   * accessed SPARQL query files
   */
  private static final String PROP_PREFIX_RECENT_SPARQL_QUERY_FILE = "RecentSparqlQueryFile_";

  /**
   * Property file property name: store the last tree view filter setting
   */
  private static final String PROP_ENFORCE_FILTERS_IN_TREE_VIEW = "EnforceTreeViewFilters";

  /**
   * Property file property name: store the last anonymous node display setting
   */
  private static final String PROP_DISPLAY_ANONYMOUS_NODES_IN_TREE_VIEW = "ShowAnonymousNodesInTreeView";

  /**
   * Property file property name: store the last strict reasoner mode setting
   */
  private static final String PROP_ENABLE_STRICT_MODE = "EnableStrictMode";

  /**
   * Property file property name: store the last SPARQL output format setting
   */
  private static final String PROP_EXPORT_SPARQL_RESULTS_FORMAT = "SparqlResultsExportFormat";

  /**
   * Property file property name: store the last SPARQL server configured port
   * setting
   */
  private static final String PROP_SPARQL_SERVER_PORT = "SparqlServerPort";

  /**
   * Property file property name: store the last SPARQL server max query runtime
   * setting
   */
  private static final String PROP_SPARQL_SERVER_MAX_RUNTIME = "SparqlServerMaxRuntimeSeconds";

  /**
   * Property file property name: store the last proxy server address setting
   */
  private static final String PROP_PROXY_SERVER = "ProxyServer";

  /**
   * Property file property name: store the last proxy server port setting
   */
  private static final String PROP_PROXY_PORT = "ProxyPort";

  /**
   * Property file property name: store the last proxy server HTTP proxied
   * setting
   */
  private static final String PROP_PROXY_HTTP = "ProxyHttpRequested";

  /**
   * Property file property name: store the last proxy server SOCKS proxied
   * setting
   */
  private static final String PROP_PROXY_SOCKS = "ProxySocksRequested";

  /**
   * Property file property name: store the last proxy server enabled setting
   */
  private static final String PROP_PROXY_ENABLED = "ProxyIsEnabled";

  /**
   * Property file property name: store the last SPARQL query user id used
   */
  private static final String PROP_SPARQL_SERVICE_USER_ID = "SparqlServiceUserId";

  /**
   * Property file property name: store the last SPARQL query default graph
   * setting
   */
  private static final String PROP_SPARQL_DEFAULT_GRAPH_URI = "SparqlDefaultGraphUri";

  /**
   * Property file property name: prefix to store the set of known SPARQL
   * service URLs
   */
  private static final String PROP_PREFIX_SPARQL_SERVICE_URL = "SparqlServiceUrl_";

  /**
   * Property file property name: store the last SPARQL query service setting
   */
  private static final String PROP_SELECTED_SPARQL_SERVICE_URL = "SparqlServiceSelectedIndex";

  /**
   * Property file property name: store the setting for writing SPARQL results
   * directly to a file
   */
  private static final String PROP_SPARQL_RESULTS_TO_FILE = "SparqlResultsToFile";

  /**
   * Property file property name: store the last SPARQL tab split pane position
   */
  private static final String PROP_SPARQL_SPLIT_PANE_POSITION = "SparqlSplitPanePosition";

  /**
   * Property file property name: store the last SPARQL query image display
   * setting
   */
  private static final String PROP_SPARQL_DISPLAY_IMAGES_IN_RESULTS = "SparqlDisplayImagesInResults";

  /**
   * Property file property name: store the last SPARQL query multiline results
   * setting
   */
  private static final String PROP_SPARQL_DISPLAY_ALLOW_MULTILINE_OUTPUT = "SparqlDisplayAllowMultilineOutput";

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
  private FileSource rdfFileSource;

  /**
   * The name (and path if necessary) to the SPARQL file being loaded
   */
  private File sparqlQueryFile;

  /**
   * The processed ontology
   */
  private OntModel ontModel;

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
   * File open triples URL menu item
   * 
   * Used to load an ontology from a remote source (generally HTTP endpoint)
   */
  private JMenuItem fileOpenTriplesUrl;

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
   * Used to save the SPARQL query from the SPARQL text area into a file
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
   * Edit the list of stored SPARQL service URLs
   */
  private JMenuItem editEditListOfSparqlServiceUrls;
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
   * Allow multiple lines in the SPARQL result display cells
   */
  private JCheckBoxMenuItem setupAllowMultilineResultOutput;

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
   * Display images in SPARQL results
   */
  private JCheckBoxMenuItem setupDisplayImagesInSparqlResults;

  /**
   * Export SPARQL results in a Comma Separated Value file
   */
  private JCheckBoxMenuItem setupExportSparqlResultsAsCsv;

  /**
   * Export SPARQL results in a Tab Separated Value file
   */
  private JCheckBoxMenuItem setupExportSparqlResultsAsTsv;

  /**
   * Should SPARQL output be written directly to file rather than presented in
   * the results grid
   */
  private JCheckBoxMenuItem setupSparqlResultsToFile;

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

  /**
   * Enable or disable the use of a proxy for remote SPARQL requests
   */
  private JCheckBoxMenuItem setupProxyEnabled;

  /**
   * Setup the proxy configuration
   */
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

  /**
   * Startup the SPARQL service listener
   */
  private JMenuItem sparqlServerStartup;

  /**
   * Shutdown the SPARQL service listener
   */
  private JMenuItem sparqlServerShutdown;

  /**
   * Configure the SPARQL service
   */
  private JMenuItem sparqlServerConfig;

  /**
   * Replace the model used by the SPARQL service with the current model
   */
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
   * Display the number of asserted triples in the model
   */
  private JLabel assertedTripleCount;

  /**
   * Display the number of inferred triples in the current model
   */
  private JLabel inferredTripleCount;

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
  private JTextArea assertionsInput;

  /**
   * SPARQL input
   */
  private JTextArea sparqlInput;

  /**
   * Choose SPARQL service to use
   */
  private JComboBox sparqlServiceUrl;

  /**
   * User id for accessing a secured SPARQL service
   */
  private JTextField sparqlServiceUserId;

  /**
   * Password for accessing a secured SPARQL service
   */
  private JPasswordField sparqlServicePassword;

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
   * Holding this reference since its state is persisted in the properties file
   */
  private JSplitPane sparqlQueryAndResults;

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
  private boolean replaceTreeImages;

  /**
   * The last directory where a file was opened or saved
   */
  private File lastDirectoryUsed;

  /**
   * Recently opened and saved asserted triples files
   */
  private List<FileSource> recentAssertionsFiles = new ArrayList<FileSource>();

  /**
   * Recently opened and save QPARQL query files
   */
  private List<File> recentSparqlFiles = new ArrayList<File>();

  /**
   * What operation is running on a thread (if any)
   */
  private Operation runningOperation;

  /**
   * Flag if the loaded assertions were larger than what is shown in the
   * assertionsInput text area
   */
  private boolean hasIncompleteAssertionsInput;

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
   * Are the currently displayed SPARQL query results in sync with the model and
   * the SPARQL query
   */
  private boolean areSparqlResultsInSyncWithModel;

  /**
   * Setup static information
   */
  static {
    REASONER_SELECTIONS = new ArrayList<ReasonerSelection>();

    REASONER_SELECTIONS.add(new ReasonerSelection("RDFS/None",
        "An RDFS model which does no entailment reasoning",
        OntModelSpec.RDFS_MEM));

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
    final CheckLatestVersion versionCheck = new CheckLatestVersion(VERSION);
    versionCheck.addObserver(this);
    new Thread(versionCheck).start();
  }

  /**
   * Sets the available icons for the windowing environment to use when the
   * program is running
   */
  private void setIcons() {
    final List<Image> icons = new ArrayList<Image>();

    try {
      icons.add(ImageLibrary.instance()
          .getImageIcon(ImageLibrary.ICON_SEMANTIC_WORKBENCH_32X32).getImage());
      icons.add(ImageLibrary.instance()
          .getImageIcon(ImageLibrary.ICON_SEMANTIC_WORKBENCH_16X16).getImage());

      setIconImages(icons);
    } catch (Throwable throwable) {
      LOGGER.warn("Cannot find application icons in the image library");
    }
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
    final double screenHeight = Toolkit.getDefaultToolkit().getScreenSize()
        .getHeight();
    final double screenWidth = Toolkit.getDefaultToolkit().getScreenSize()
        .getWidth();

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
      if (screenHeight * MAXIMUM_RESTORE_RELATIVE_WINDOW_TO_SCREEN_SIZE < setHeight) {
        setHeight = screenHeight
            * MAXIMUM_RESTORE_RELATIVE_WINDOW_TO_SCREEN_SIZE;
        resizeRequired = true;
      }
      if (screenWidth * MAXIMUM_RESTORE_RELATIVE_WINDOW_TO_SCREEN_SIZE < setWidth) {
        setWidth = screenWidth * MAXIMUM_RESTORE_RELATIVE_WINDOW_TO_SCREEN_SIZE;
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
      final Integer index = Integer.parseInt(value);
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

    setupAllowMultilineResultOutput.setSelected(properties
        .getProperty(PROP_SPARQL_DISPLAY_ALLOW_MULTILINE_OUTPUT, "Y")
        .toUpperCase()
        .startsWith("Y"));
    setupOutputFqnNamespaces.setSelected(properties
        .getProperty(PROP_SHOW_FQN_NAMESPACES, "Y").toUpperCase()
        .startsWith("Y"));
    setupOutputDatatypesForLiterals.setSelected(properties
        .getProperty(PROP_SHOW_DATATYPES_ON_LITERALS, "Y").toUpperCase()
        .startsWith("Y"));
    setupOutputFlagLiteralValues.setSelected(properties
        .getProperty(PROP_FLAG_LITERALS_IN_RESULTS, "N").toUpperCase()
        .startsWith("Y"));
    setupDisplayImagesInSparqlResults.setSelected(properties
        .getProperty(PROP_SPARQL_DISPLAY_IMAGES_IN_RESULTS, "N").toUpperCase()
        .startsWith("Y"));

    // SPARQL query export format - default to CSV
    if (properties.getProperty(PROP_EXPORT_SPARQL_RESULTS_FORMAT,
        EXPORT_FORMAT_LABEL_CSV)
        .equalsIgnoreCase(EXPORT_FORMAT_LABEL_TSV)) {
      setupExportSparqlResultsAsTsv.setSelected(true);
    } else {
      setupExportSparqlResultsAsCsv.setSelected(true);
    }

    setupSparqlResultsToFile.setSelected(properties
        .getProperty(PROP_SPARQL_RESULTS_TO_FILE, "N").toUpperCase()
        .startsWith("Y"));

    value = properties.getProperty(PROP_SPARQL_SERVICE_USER_ID);
    if (value != null) {
      sparqlServiceUserId.setText(value);
    }

    value = properties.getProperty(PROP_SPARQL_DEFAULT_GRAPH_URI);
    if (value != null) {
      defaultGraphUri.setText(value);
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

    // SPARQL Split Pane Position
    value = properties.getProperty(PROP_SPARQL_SPLIT_PANE_POSITION);
    if (value != null) {
      try {
        final int position = Integer.parseInt(value);
        if (position > 0) {
          sparqlQueryAndResults.setDividerLocation(position);
        }
      } catch (Throwable throwable) {
        LOGGER.warn("Cannot use the SPARQL split pane divider location value: "
            + value, throwable);
      }
    }

    // Sparql server port
    value = properties.getProperty(PROP_SPARQL_SERVER_PORT);
    if (value != null) {
      try {
        final Integer port = Integer.parseInt(value);
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
        final Integer maxRuntimeSeconds = Integer.parseInt(value);
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

    populateSparqlServiceUrls();
  }

  /**
   * Populate the drop down of SPARQL service URLs from the
   * properties file. If none are found, populate with a
   * default list.
   */
  private void populateSparqlServiceUrls() {
    final List<String> prefixNames = new ArrayList<String>();
    boolean foundUrl;
    int lastSelectedIndex;

    /**
     * Find any keys for previous files
     */
    for (Object key : properties.keySet()) {
      if (key.toString().startsWith(PROP_PREFIX_SPARQL_SERVICE_URL)) {
        prefixNames.add(key.toString());
      }
    }

    // Want the files in order so the drop down is consitent from run to run
    // Also, the last selected index is used to select the last selected service
    Collections.sort(prefixNames);

    // This must be the first option - use the local model
    sparqlServiceUrl.addItem("Local Model, FROM or SERVICE Clause");

    // Detect if at least one service URL is found in the properties file
    foundUrl = false;

    for (String key : prefixNames) {
      sparqlServiceUrl.addItem(properties.get(key));
      foundUrl = true;
    }

    // If there are no service URLs defined, setup some defaults
    if (!foundUrl) {
      for (String url : DEFAULT_SERVICE_URLS) {
        sparqlServiceUrl.addItem(url);
      }
    }

    // If the last selected index value is legal, set the service selection to
    // that option
    try {
      lastSelectedIndex = Integer.parseInt(properties.getProperty(
          PROP_SELECTED_SPARQL_SERVICE_URL, "0"));
      if (lastSelectedIndex < 0
          || lastSelectedIndex >= sparqlServiceUrl.getItemCount()) {
        throw new IllegalArgumentException(
            "SPARQL Service URL index in properties file out of range (0-"
                + sparqlServiceUrl.getItemCount() + "): " + lastSelectedIndex);
      }
    } catch (Throwable throwable) {
      LOGGER.warn(
          "Illegal value for the last SPARQL service URL selection index",
          throwable);
      lastSelectedIndex = 0;
    }
    sparqlServiceUrl.setSelectedIndex(lastSelectedIndex);
  }

  /**
   * Obtain the list of recently opened or saved assertions
   * files to update the assertions file menu.
   */
  private void extractRecentAssertedTriplesFilesFromProperties() {
    final List<String> prefixNames = new ArrayList<String>();
    String value;
    String[] parsed;

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
      value = properties.getProperty(prefixNames.get(index));
      LOGGER.debug("Got previous filename: [" + value + "]");
      try {
        if (value.startsWith("URL:")) {
          parsed = value.split(":", 2);
          recentAssertionsFiles.add(new FileSource(new URL(parsed[1])));
        } else if (value.startsWith("FILE:")) {
          parsed = value.split(":", 2);
          recentAssertionsFiles.add(new FileSource(new File(parsed[1])));
        } else {
          // Take a guess at what it is, File path or URL
          if (value.indexOf(':') > -1) {
            // Assume it is URL since it has a colon
            recentAssertionsFiles.add(new FileSource(new URL(value)));
          } else {
            recentAssertionsFiles.add(new FileSource(new File(value)));
          }
        }
      } catch (Throwable throwable) {
        LOGGER.warn(
            "Unable to parse the File or URL from the properties file: "
                + value, throwable);
      }

    }

    setupAssertionsFileMenu();
  }

  /**
   * Obtain the list of recently opened or saved SPARQL
   * files to update the SPARQL file menu.
   */
  private void extractRecentSparqlQueryFilesFromProperties() {
    final List<String> prefixNames = new ArrayList<String>();

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
  private void addRecentAssertedTriplesFile(FileSource file) {
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

  /**
   * Setup the list of stored SPARQL service URLs from the dropdown into a map
   * and use the editFilterMap method to allow the user to remove unwanted
   * entries
   */
  private void editListOfSparqlServiceUrls() {
    final Map<String, String> urls = new HashMap<String, String>();
    int sizeBefore;
    int currentSelectedIndex;

    for (int index = 1; index < sparqlServiceUrl.getItemCount(); ++index) {
      urls.put(sparqlServiceUrl.getItemAt(index).toString(), "");
    }

    sizeBefore = urls.size();
    editFilterMap(urls);

    // If one or more values were removed, rebuild dropdown
    if (urls.size() != sizeBefore) {
      currentSelectedIndex = sparqlServiceUrl.getSelectedIndex();

      // Figure out what was removed
      for (int index = 1; index < sparqlServiceUrl.getItemCount(); ++index) {
        if (urls.get(sparqlServiceUrl.getItemAt(index)) == null) {
          // Item was removed - remove from dropdown
          sparqlServiceUrl.removeItemAt(index);
          // If the currentSelection was this one, make the current Selection 0
          if (currentSelectedIndex == index) {
            currentSelectedIndex = 0;
          } else if (currentSelectedIndex > index) {
            // If the current selection is after this deleted one
            // then it has moved up one position
            currentSelectedIndex--;
          }

          // Since an item was removed, back up one position so the next
          // iteration doesn't skip the new value in this position
          --index;
        }
      }

      // Select the proper item (either the previous one selected or the default
      // if the previously selected one was deleted
      sparqlServiceUrl.setSelectedIndex(currentSelectedIndex);
    }

    enableControls(true);
  }

  /**
   * Edit the list of filters.
   * 
   * @param filterMap
   *          The map whose entries are being edited
   */
  private void editFilterMap(Map<String, String> filterMap) {
    final List<String> filteredItems = new ArrayList<String>(filterMap.keySet());
    int[] selectedIndices;

    Collections.sort(filteredItems);
    final JList jListOfItems = new JList(
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

    // Remove the set of SPARQL service URL entries.
    // They will be recreated from the dropdown
    removePrefixedProperties(PROP_PREFIX_SPARQL_SERVICE_URL);

    updatePropertiesWithClassesToSkipInTree();
    updatePropertiesWithPredicatesToSkipInTree();
    updatePropertiesWithServiceUrls();

    // Add the set of recent asserted triples files
    for (int index = 0; index < MAX_PREVIOUS_FILES_TO_STORE
        && index < recentAssertionsFiles.size(); ++index) {
      properties.put(PROP_PREFIX_RECENT_ASSERTIONS_FILE + index,
          (recentAssertionsFiles.get(index).isFile() ? "FILE:" : "URL:")
              + recentAssertionsFiles.get(index)
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

    // Only store the divider position if it is not at an extreme setting (e.g.
    // both the query and results panels are visible)
    if (sparqlQueryAndResults.getDividerLocation() > 1
        && sparqlQueryAndResults.getHeight()
            - (sparqlQueryAndResults.getDividerLocation() + sparqlQueryAndResults
                .getDividerSize()) > 1) {
      properties.setProperty(PROP_SPARQL_SPLIT_PANE_POSITION,
          sparqlQueryAndResults.getDividerLocation() + "");
    } else {
      LOGGER.debug("SPARQL split pane position not being stored - Size:"
          + sparqlQueryAndResults.getHeight() + " DividerLoc:"
          + sparqlQueryAndResults.getDividerLocation() + " DividerSize:"
          + sparqlQueryAndResults.getDividerSize());
      properties.remove(PROP_SPARQL_SPLIT_PANE_POSITION);
    }
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

    properties
        .setProperty(
            PROP_SPARQL_DISPLAY_ALLOW_MULTILINE_OUTPUT,
            setupAllowMultilineResultOutput.isSelected() ? DEFAULT_PROPERTY_VALUE_YES
                : DEFAULT_PROPERTY_VALUE_NO);
    properties.setProperty(PROP_SHOW_FQN_NAMESPACES,
        setupOutputFqnNamespaces.isSelected() ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);
    properties
        .setProperty(
            PROP_SHOW_DATATYPES_ON_LITERALS,
            setupOutputDatatypesForLiterals.isSelected() ? DEFAULT_PROPERTY_VALUE_YES
                : DEFAULT_PROPERTY_VALUE_NO);
    properties.setProperty(PROP_FLAG_LITERALS_IN_RESULTS,
        setupOutputFlagLiteralValues.isSelected() ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);
    properties
        .setProperty(
            PROP_SPARQL_DISPLAY_IMAGES_IN_RESULTS,
            setupDisplayImagesInSparqlResults.isSelected() ? DEFAULT_PROPERTY_VALUE_YES
                : DEFAULT_PROPERTY_VALUE_NO);

    if (setupExportSparqlResultsAsTsv.isSelected()) {
      properties.setProperty(PROP_EXPORT_SPARQL_RESULTS_FORMAT,
          EXPORT_FORMAT_LABEL_TSV);
    } else {
      properties.setProperty(PROP_EXPORT_SPARQL_RESULTS_FORMAT,
          EXPORT_FORMAT_LABEL_CSV);
    }

    properties.setProperty(PROP_SPARQL_RESULTS_TO_FILE,
        setupSparqlResultsToFile.isSelected() ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);

    if (sparqlServiceUserId.getText().trim().length() > 0) {
      properties.setProperty(PROP_SPARQL_SERVICE_USER_ID, sparqlServiceUserId
          .getText().trim());
    } else {
      properties.remove(PROP_SPARQL_SERVICE_USER_ID);
    }

    if (defaultGraphUri.getText().trim().length() > 0) {
      properties.setProperty(PROP_SPARQL_DEFAULT_GRAPH_URI, defaultGraphUri
          .getText().trim());
    } else {
      properties.remove(PROP_SPARQL_DEFAULT_GRAPH_URI);
    }

    properties.setProperty(PROP_ENABLE_STRICT_MODE,
        setupEnableStrictMode.isSelected() ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);

    properties.setProperty(PROP_ENFORCE_FILTERS_IN_TREE_VIEW,
        filterEnableFilters.isSelected() ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);
    properties.setProperty(PROP_DISPLAY_ANONYMOUS_NODES_IN_TREE_VIEW,
        filterShowAnonymousNodes.isSelected() ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);

    properties.setProperty(PROP_SPARQL_SERVER_PORT, SparqlServer.getInstance()
        .getListenerPort() + "");
    properties.setProperty(PROP_SPARQL_SERVER_MAX_RUNTIME, SparqlServer
        .getInstance().getMaxRuntimeSeconds() + "");

    properties.setProperty(PROP_PROXY_ENABLED,
        proxyEnabled ? DEFAULT_PROPERTY_VALUE_YES : DEFAULT_PROPERTY_VALUE_NO);
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

    properties.setProperty(PROP_PROXY_HTTP,
        proxyProtocolHttp ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);
    properties.setProperty(PROP_PROXY_SOCKS,
        proxyProtocolSocks ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);

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
   * Save the set of SPARQL Service URLs.
   */
  private void updatePropertiesWithServiceUrls() {
    // Start at 1 - skip the default "hardcoded" model entry
    for (int index = 1; index < sparqlServiceUrl.getItemCount(); ++index) {
      properties.setProperty(PROP_PREFIX_SPARQL_SERVICE_URL + index,
          sparqlServiceUrl.getItemAt(index).toString());
    }

    properties.setProperty(PROP_SELECTED_SPARQL_SERVICE_URL,
        sparqlServiceUrl.getSelectedIndex() + "");
  }

  /**
   * Remove all the properties in the properties collection that begin with the
   * supplied property key prefix.
   * 
   * @param prefix
   *          The property prefix for entries to be removed
   */
  private void removePrefixedProperties(String prefix) {
    final List<String> propertiesToBeRemoved = new ArrayList<String>();

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
   * @return The directory name for the properties file
   */
  private String getPropertiesDirectory() {
    String home = System.getProperty("user.home");

    if (home == null || home.trim().length() == 0) {
      home = ".";
    } else {
      home += "/SemanticWorkbench";
      final File homeFile = new File(home);
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
    final String fontName = properties.getProperty(PROP_FONT_NAME, "Courier");
    final String fontSize = properties.getProperty(PROP_FONT_SIZE, "12");
    final String fontStyle = properties.getProperty(PROP_FONT_STYLE, "0");

    try {
      newFont = new Font(fontName, Integer.parseInt(fontStyle),
          Integer.parseInt(fontSize));
      if (newFont.getSize() < MINIMUM_FONT_SIZE) {
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
    final String colorRgb = properties.getProperty(PROP_FONT_COLOR,
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
    // FontMetrics fontMetrics;
    if (newFont != null) {
      assertionsInput.setFont(newFont);
      inferredTriples.setFont(newFont);
      sparqlInput.setFont(newFont);
      ontModelTree.setFont(newFont);
      sparqlResultsTable.setFont(newFont);
      sparqlResultsTable.getTableHeader().setFont(newFont);
      final SparqlResultItemRenderer renderer = new SparqlResultItemRenderer(
          setupAllowMultilineResultOutput.isSelected());
      renderer.setFont(newFont);
      sparqlResultsTable.setDefaultRenderer(SparqlResultItem.class, renderer);
      ((AbstractTableModel) sparqlResultsTable.getModel())
          .fireTableStructureChanged();
      // fontMetrics = sparqlResultsTable.getFontMetrics(newFont);
      // sparqlResultsTable.setRowHeight(((int) ((double) fontMetrics
      // .getHeight() * 1.1)));
      status.setFont(newFont);
    }

    if (newColor != null) {
      assertionsInput.setForeground(newColor);
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
    gridPanel.setLayout(new GridLayout(0, 3));

    // First Row

    // Create Model Button
    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    flowPanel.add(runInferencing);
    gridPanel.add(flowPanel);

    // Model/Reasoner Choice
    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    flowPanel.add(new JLabel("Model/Reasoning:"));
    flowPanel.add(reasoningLevel);
    gridPanel.add(flowPanel);

    // Number of asserted triples
    flowPanel = new JPanel();
    flowPanel.setLayout(new GridLayout(1, 1));
    flowPanel.add(assertedTripleCount);
    flowPanel.setBorder(BorderFactory.createTitledBorder("Asserted Triples"));
    gridPanel.add(flowPanel);

    // Second Row

    // Empty cell
    gridPanel.add(new JLabel());

    // Language drop-down
    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    flowPanel.add(new JLabel("Language:"));
    flowPanel.add(language);
    gridPanel.add(flowPanel);

    // Number of inferred triples
    flowPanel = new JPanel();
    flowPanel.setLayout(new GridLayout(1, 1));
    flowPanel.add(inferredTripleCount);
    flowPanel.setBorder(BorderFactory.createTitledBorder("Inferred Triples"));
    gridPanel.add(flowPanel);

    assertionPanel.add(gridPanel, BorderLayout.NORTH);

    gridPanel = new JPanel();
    gridPanel.setLayout(new GridLayout(1, 1));
    gridPanel.setBorder(BorderFactory.createTitledBorder("Assertions"));
    gridPanel.add(new JScrollPane(assertionsInput));
    assertionPanel.add(gridPanel, BorderLayout.CENTER);

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
    JPanel queryPanel;
    JPanel resultsPanel;

    sparqlPanel = new JPanel();
    // sparqlPanel.setLayout(new GridLayout(2, 1));
    sparqlPanel.setLayout(new BorderLayout());

    // Controls
    labelPanel = new JPanel();
    labelPanel.setLayout(new BorderLayout());
    gridPanel = new JPanel();
    gridPanel.setLayout(new GridLayout(0, 1));

    innerGridPanel = new JPanel();
    innerGridPanel.setLayout(new GridLayout(1, 3));
    // innerGridPanel.add(new JLabel("SPARQL Query"));
    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    flowPanel.add(runSparql);
    innerGridPanel.add(flowPanel);
    innerGridPanel.add(sparqlServerInfo);
    innerGridPanel.add(proxyInfo);
    gridPanel.add(innerGridPanel);

    innerGridPanel = new JPanel();
    innerGridPanel.setLayout(new GridLayout(1, 2));
    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    flowPanel.add(new JLabel("Service: "));
    flowPanel.add(sparqlServiceUrl);
    // gridPanel.add(flowPanel);
    innerGridPanel.add(flowPanel);
    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    flowPanel.add(new JLabel("Service User Id: "));
    flowPanel.add(sparqlServiceUserId);
    innerGridPanel.add(flowPanel);
    gridPanel.add(innerGridPanel);

    innerGridPanel = new JPanel();
    innerGridPanel.setLayout(new GridLayout(1, 2));
    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    flowPanel.add(new JLabel("Default Graph URI: "));
    flowPanel.add(defaultGraphUri);
    // gridPanel.add(flowPanel);
    innerGridPanel.add(flowPanel);
    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    flowPanel.add(new JLabel("Service Password: "));
    flowPanel.add(sparqlServicePassword);
    innerGridPanel.add(flowPanel);
    gridPanel.add(innerGridPanel);

    // labelPanel.add(gridPanel, BorderLayout.NORTH);
    // labelPanel.add(new JScrollPane(sparqlInput), BorderLayout.CENTER);
    // sparqlPanel.add(labelPanel);
    sparqlPanel.add(gridPanel, BorderLayout.NORTH);

    // SPARQL query
    queryPanel = new JPanel();
    queryPanel.setLayout(new GridLayout(1, 1));
    queryPanel.setBorder(BorderFactory.createTitledBorder("Query"));
    queryPanel.add(new JScrollPane(sparqlInput));

    // SPARQL results
    resultsPanel = new JPanel();
    resultsPanel.setLayout(new GridLayout(1, 1));
    resultsPanel.setBorder(BorderFactory.createTitledBorder("Results"));
    resultsPanel.add(new JScrollPane(sparqlResultsTable));

    // Query and Results Split Pane
    sparqlQueryAndResults = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        queryPanel,
        resultsPanel);

    sparqlQueryAndResults
        .setDividerLocation(DEFAULT_SPARQL_QUERY_AND_RESULTS_DIVIDER_LOCATION);
    sparqlQueryAndResults.setOneTouchExpandable(true);

    sparqlPanel.add(sparqlQueryAndResults, BorderLayout.CENTER);
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
    statusPanel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.black), "Status"));
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

    fileOpenTriplesUrl = new JMenuItem("Open Assertions Url");
    fileOpenTriplesUrl.setMnemonic('U');
    fileOpenTriplesUrl.setToolTipText(
        "Access asserted triples from a URL");
    fileOpenTriplesUrl
        .addActionListener(new FileAssertedTriplesUrlOpenListener());
    fileAssertionsMenu.add(fileOpenTriplesUrl);

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
    menuBar.add(setupEditMenu());

    // Configuration Menu
    menuBar.add(setupConfigurationMenu());

    // Model Menu
    menuBar.add(setupModelMenu());

    // Filters Menu
    menuBar.add(setupFiltersMenu());

    // SPARQL Server Menu
    menuBar.add(setupSparqlServerMenu());

    // Help Menu
    menuBar.add(setupHelpMenu());
  }

  /**
   * Create the edit menu
   * 
   * @return The edit menu
   */
  private JMenu setupEditMenu() {
    final JMenu menu = new JMenu("Edit");

    menu.setMnemonic(KeyEvent.VK_E);
    menu.setToolTipText(
        "Menu items related to editing the ontology");

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

    menu.addSeparator();

    editEditListOfSparqlServiceUrls = new JMenuItem(
        "Edit SPARQL Service URLs List");
    editEditListOfSparqlServiceUrls.setMnemonic(KeyEvent.VK_S);
    editEditListOfSparqlServiceUrls
        .setToolTipText("Remove unwanted URLs from the dropdown list");
    editEditListOfSparqlServiceUrls
        .addActionListener(new EditListOfSparqlServiceUrls());
    menu.add(editEditListOfSparqlServiceUrls);

    return menu;
  }

  /**
   * Create the configuration menu
   * 
   * @return The configuration menu
   */
  private JMenu setupConfigurationMenu() {
    final JMenu menu = new JMenu("Configure");
    ButtonGroup buttonGroup;

    menu.setMnemonic(KeyEvent.VK_C);
    menu.setToolTipText(
        "Menu items related to configuration");

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

    setupAllowMultilineResultOutput = new JCheckBoxMenuItem(
        "Allow Multiple Lines of Text Per Row in SPARQL Query Output");
    setupAllowMultilineResultOutput
        .setToolTipText("Wrap long values into multiple lines in a display cell");
    setupAllowMultilineResultOutput.setSelected(true);
    menu.add(setupAllowMultilineResultOutput);

    setupOutputFqnNamespaces = new JCheckBoxMenuItem(
        "Show FQN Namespaces Instead of Prefixes in Query Output");
    setupOutputFqnNamespaces
        .setToolTipText("Use the fully qualified namespace. If unchecked use the prefix, if defined");
    setupOutputFqnNamespaces.setSelected(true);
    menu.add(setupOutputFqnNamespaces);

    setupOutputDatatypesForLiterals = new JCheckBoxMenuItem(
        "Show Datatypes on Literals in Query Output");
    setupOutputDatatypesForLiterals
        .setToolTipText("Display the datatype after the value, e.g. 4^^xsd:integer");
    setupOutputDatatypesForLiterals.setSelected(true);
    menu.add(setupOutputDatatypesForLiterals);

    setupOutputFlagLiteralValues = new JCheckBoxMenuItem(
        "Flag Literal Values in Query Output");
    setupOutputFlagLiteralValues
        .setToolTipText("Includes the text 'Lit:' in front of any literal values");
    setupOutputFlagLiteralValues.setSelected(true);
    menu.add(setupOutputFlagLiteralValues);

    setupDisplayImagesInSparqlResults = new JCheckBoxMenuItem(
        "Display Images in Query Output (Slows Results Retrieval)");
    setupDisplayImagesInSparqlResults
        .setToolTipText("Attempts to download images linked in the results. "
            + "Can run very slowly depending on number and size of images");
    setupDisplayImagesInSparqlResults.setSelected(true);
    menu.add(setupDisplayImagesInSparqlResults);

    menu.addSeparator();

    buttonGroup = new ButtonGroup();
    setupExportSparqlResultsAsCsv = new JCheckBoxMenuItem(
        "Export SPARQL Results to " + EXPORT_FORMAT_LABEL_CSV);
    setupExportSparqlResultsAsCsv
        .setToolTipText("Export to Comma Separated Value format");
    buttonGroup.add(setupExportSparqlResultsAsCsv);
    menu.add(setupExportSparqlResultsAsCsv);

    setupExportSparqlResultsAsTsv = new JCheckBoxMenuItem(
        "Export SPARQL Results to " + EXPORT_FORMAT_LABEL_TSV);
    setupExportSparqlResultsAsTsv
        .setToolTipText("Export to Tab Separated Value format");
    buttonGroup.add(setupExportSparqlResultsAsTsv);
    menu.add(setupExportSparqlResultsAsTsv);

    menu.addSeparator();

    setupSparqlResultsToFile = new JCheckBoxMenuItem(
        "Send SPARQL Results Directly to File");
    setupSparqlResultsToFile
        .setToolTipText("For large results sets this permits writing to file without trying to render on screen");
    menu.add(setupSparqlResultsToFile);

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

    return menu;
  }

  /**
   * Create the model menu
   * 
   * @return The model menu
   */
  private JMenu setupModelMenu() {
    final JMenu menu = new JMenu("Model");

    menu.setMnemonic(KeyEvent.VK_M);
    menu.setToolTipText(
        "Menu items related to viewing the model");

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

    return menu;
  }

  /**
   * Create the filters menu
   * 
   * @return The filters menu
   */
  private JMenu setupFiltersMenu() {
    final JMenu menu = new JMenu("Tree Filter");

    menu.setMnemonic(KeyEvent.VK_F);
    menu.setToolTipText(
        "Menu items related to filtering values out of the model's tree");

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

    return menu;
  }

  /**
   * Create the SPARQL server menu
   * 
   * @return The SPARQL server menu
   */
  private JMenu setupSparqlServerMenu() {
    final JMenu menu = new JMenu("SPARQL Server");

    menu.setMnemonic(KeyEvent.VK_P);
    menu.setToolTipText(
        "Options for using the SPARQL server");

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

    return menu;
  }

  /**
   * Create the help menu
   * 
   * @return The help menu
   */
  private JMenu setupHelpMenu() {
    final JMenu menu = new JMenu("Help");

    menu.setMnemonic(KeyEvent.VK_H);
    menu.setToolTipText(
        "Menu items related to user assistance");

    helpAbout = new JMenuItem("About");
    helpAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
        ActionEvent.ALT_MASK));
    helpAbout.setMnemonic(KeyEvent.VK_H);
    helpAbout.setToolTipText(
        "View version information");
    helpAbout.addActionListener(new AboutListener());
    menu.add(helpAbout);

    return menu;
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

    // Don't allow editing if the ontology file did not load completely. This is
    // to avoid confusion for the user since when the ontology file isn't loaded
    // completely the reasoner will be run on the actual file rather than the
    // version in the text area, so text area edits would be ignored.

    // TODO Consider switching the status of hasIncompleteAssertionsInput if the
    // user edits the text area after an incomplete load. Would want to warn the
    // user about avoiding overwriting the complete version of the file
    assertionsInput.setEditable(enable && !hasIncompleteAssertionsInput);
    sparqlInput.setEditable(enable);
    fileOpenTriplesFile.setEnabled(enable);

    colorCodeTabs();

    // If inferencing is completed and the models are setup, enable
    // the tree view and inferred triples listing options
    if (enable && ontModel != null) {
      modelCreateTreeView.setEnabled(true);
      modelListInferredTriples.setEnabled(true);
      fileSaveSerializedModel.setEnabled(true);
    } else {
      modelCreateTreeView.setEnabled(false);
      modelListInferredTriples.setEnabled(false);
      fileSaveSerializedModel.setEnabled(false);
    }

    // if (enable && assertionsText.getText().trim().length() > 0) {
    if (enable && assertionsInput.getText().trim().length() > 0) {
      runInferencing.setEnabled(true);

      // The file save is not available if a local file could not be loaded
      // completely (don't want to accidentally overwrite it with an incomplete
      // version) However, if the incomplete load is from a URL, then it can be
      // saved to a file for local manipulation.
      fileSaveTriplesToFile.setEnabled(!hasIncompleteAssertionsInput
          || (rdfFileSource != null && rdfFileSource.isUrl()));
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
      sparqlServicePassword.setEnabled(true);
      sparqlServiceUserId.setEnabled(true);
      LOGGER.debug("Enabled run query button (" + sparqlService + ")");
    } else if (enable && sparqlInput.getText().trim().length() > 0
        && ontModel != null) {
      runSparql.setEnabled(true);
      sparqlServicePassword.setEnabled(true);
      sparqlServiceUserId.setEnabled(true);
      LOGGER.debug("Enabled run query button (" + sparqlService + ")");
    } else if (enable
        && sparqlInput.getText().toLowerCase().indexOf("from") > -1) {
      runSparql.setEnabled(true);
      sparqlServicePassword.setEnabled(true);
      sparqlServiceUserId.setEnabled(true);
      LOGGER.debug("Enabled run query button due to 'from' clause ("
          + sparqlService + ")");
    } else {
      runSparql.setEnabled(false);
      sparqlServicePassword.setEnabled(false);
      sparqlServiceUserId.setEnabled(false);
      LOGGER.debug("Disabled run query button (" + sparqlService + ")");
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

  /**
   * Setup the network environment based on the proxy configuration. If the
   * proxy is not enabled no changes will be made to the network operation.
   */
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
   * @param alertUser
   *          Whether to popup a message dialog if an error exists in the proxy
   *          configuration
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
    final ProxyConfigurationDialog dialog = new ProxyConfigurationDialog(this,
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
        // new Thread(SparqlServer.getInstance()).start();
        SparqlServer.getInstance().addObserver(this);

        try {
          SparqlServer.getInstance().start();
        } catch (Throwable throwable) {
          LOGGER.error("Unable to start the SPARQL server", throwable);
          SparqlServer.getInstance().deleteObserver(this);
          JOptionPane.showMessageDialog(this,
              "Unable to start the SPARQL server\n" + throwable.getMessage(),
              "Cannot Start the SPARQL Server",
              JOptionPane.ERROR_MESSAGE);
        }

        if (SparqlServer.getInstance().isActive()) {
          sparqlServerInfo.setForeground(Color.blue.darker());
          updateSparqlServerInfo();
          setStatus("SPARQL server started on port "
              + SparqlServer.getInstance().getListenerPort());
        }
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
      final OntModel newModel = ModelFactory.createOntologyModel(ontModel
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
      final SparqlServerConfigurationDialog dialog = new SparqlServerConfigurationDialog(
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

  /**
   * Set the tab background and foreground color based on whether the tab's data
   * is out of synch with the model or other configuration changes
   */
  private void colorCodeTabs() {
    if (hasIncompleteAssertionsInput) {
      tabbedPane.setForegroundAt(0, Color.orange.darker());
      tabbedPane.setBackgroundAt(0, Color.yellow.brighter());
      tabbedPane.setToolTipTextAt(0,
          "Only part of the assertions file is displayed");
    } else {
      tabbedPane.setForegroundAt(0, NORMAL_TAB_FG);
      tabbedPane.setBackgroundAt(0, NORMAL_TAB_BG);
      tabbedPane.setToolTipTextAt(0, null);
    }

    if (!areInferencesInSyncWithModel
        && inferredTriples.getText().trim().length() > 0) {
      tabbedPane.setForegroundAt(1, Color.red);
      tabbedPane.setBackgroundAt(1, Color.pink);
      tabbedPane.setToolTipTextAt(1,
          "Inferences are out of sync with loaded assertions");
      // inferredTriples.setBackground(Color.red);
      // inferredTriples.setForeground(Color.white);
    } else {
      tabbedPane.setForegroundAt(1, NORMAL_TAB_FG);
      tabbedPane.setBackgroundAt(1, NORMAL_TAB_BG);
      tabbedPane.setToolTipTextAt(1, null);
      // inferredTriples.setBackground(Color.white);
      // inferredTriples.setForeground(status.getForeground());
    }

    if (!isTreeInSyncWithModel
        && !ontModelTree.getModel().isLeaf(ontModelTree.getModel().getRoot())) {
      tabbedPane.setForegroundAt(2, Color.red);
      tabbedPane.setBackgroundAt(2, Color.pink);
      tabbedPane.setToolTipTextAt(2,
          "Tree is out of sync with loaded assertions");
      // ontModelTree.setBackground(Color.red);
      // ontModelTree.setForeground(Color.white);
    } else {
      tabbedPane.setForegroundAt(2, NORMAL_TAB_FG);
      tabbedPane.setBackgroundAt(2, NORMAL_TAB_BG);
      tabbedPane.setToolTipTextAt(2, null);
      // ontModelTree.setBackground(Color.white);
      // ontModelTree.setForeground(status.getForeground());
    }

    if (!areSparqlResultsInSyncWithModel
        && sparqlResultsTable.getRowCount() > 0) {
      tabbedPane.setForegroundAt(3, Color.red);
      tabbedPane.setBackgroundAt(3, Color.pink);
      tabbedPane.setToolTipTextAt(3,
          "Results are out of sync with loaded assertions");
      // sparqlResultsTable.setBackground(Color.red);
      // sparqlResultsTable.setForeground(Color.white);
    } else {
      tabbedPane.setForegroundAt(3, NORMAL_TAB_FG);
      tabbedPane.setBackgroundAt(3, NORMAL_TAB_BG);
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

    assertedTripleCount = new JLabel(NOT_APPLICABLE_DISPLAY);
    assertedTripleCount.setHorizontalAlignment(JLabel.CENTER);
    inferredTripleCount = new JLabel(NOT_APPLICABLE_DISPLAY);
    inferredTripleCount.setHorizontalAlignment(JLabel.CENTER);

    runInferencing = new JButton("Create Model");
    runInferencing
        .setToolTipText("Creates an ontology model using the provieed assertions "
            + "and the selected reasoning level");
    runInferencing.addActionListener(new ReasonerListener());

    runSparql = new JButton("Run Query");
    runSparql.addActionListener(new SparqlListener());

    sparqlServerInfo = new JLabel("Shutdown");
    sparqlServerInfo.setHorizontalAlignment(SwingConstants.CENTER);
    sparqlServerInfo.setBorder(BorderFactory
        .createTitledBorder("SPARQL Server Status"));

    proxyInfo = new JLabel("Disabled");
    proxyInfo.setHorizontalAlignment(SwingConstants.CENTER);
    proxyInfo.setBorder(BorderFactory.createTitledBorder("Proxy Status"));

    assertionsInput = new JTextArea(10, 50);
    assertionsInput.addKeyListener(new UserInputListener());

    inferredTriples = new JTextArea(10, 50);
    inferredTriples.setEditable(false);

    // SPARQL Input
    sparqlInput = new JTextArea(10, 50);
    sparqlInput.addKeyListener(new UserInputListener());

    // User id and password for accessing secured SPARQL endpoints
    sparqlServiceUserId = new JTextField(10);
    sparqlServicePassword = new JPasswordField(10);

    // SPARQL service URLs
    sparqlServiceUrl = new JComboBox();
    sparqlServiceUrl.setEditable(true);
    sparqlServiceUrl.addActionListener(new SparqlModelChoiceListener());
    sparqlServiceUrl.getEditor().getEditorComponent().addKeyListener(
        new UserInputListener());

    // Default graph if required
    defaultGraphUri = new JTextField();
    defaultGraphUri.setColumns(30);

    // A basic default query
    sparqlInput.setText("select ?s ?p ?o where { ?s ?p ?o } limit 100");

    // Results table
    sparqlResultsTable = new JTable(new SparqlTableModel());
    sparqlResultsTable.setAutoCreateRowSorter(true);
    // sparqlResultsTable.setDefaultRenderer(SparqlResultItem.class, new
    // SparqlResultItemRenderer());
    // sparqlResultsTable.setDefaultRenderer(Object.class, new
    // SparqlResultItemRenderer());

    // Determine whether alternate tree icons exist
    if (ImageLibrary.instance().getImageIcon(ImageLibrary.ICON_TREE_CLASS) != null) {
      replaceTreeImages = true;
    }

    LOGGER.debug("Tree renderer, specialized icons available? "
        + replaceTreeImages);

    // Create the tree UI with a default model
    ontModelTree = new JTree(new DefaultTreeModel(
        new DefaultMutableTreeNode("No Tree Generated")));

    ontModelTree.addMouseListener(new OntologyModelTreeMouseListener());

    if (replaceTreeImages) {
      ToolTipManager.sharedInstance().registerComponent(ontModelTree);
      ontModelTree.setCellRenderer(new OntologyTreeCellRenderer());
    }

    // Status label
    status = new JLabel("Initializing");
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
   * 
   * @return The final status to display
   */
  private String expandAll() {
    int numNodes;
    ProgressMonitor progress;
    boolean canceled;
    final DefaultMutableTreeNode root = (DefaultMutableTreeNode) ontModelTree
        .getModel().getRoot();
    @SuppressWarnings("rawtypes")
    final Enumeration enumerateNodes = root.breadthFirstEnumeration();

    numNodes = 0;
    while (enumerateNodes.hasMoreElements()) {
      enumerateNodes.nextElement();
      ++numNodes;
    }

    LOGGER.debug("Expanding tree with row count: " + numNodes);

    progress = new ProgressMonitor(this,
        "Expanding Tree Nodes", "Starting node expansion", 0, numNodes);

    setStatus("Expanding all tree nodes");

    for (int row = 0; !progress.isCanceled() && row < numNodes; ++row) {
      progress.setProgress(row);
      if (row % 1000 == 0) {
        progress.setNote("Row " + row + " of " + numNodes);
      }

      ontModelTree.expandRow(row);
      // ontModelTree.scrollRowToVisible(row);
    }

    canceled = progress.isCanceled();

    progress.close();

    ontModelTree.scrollRowToVisible(0);

    if (!canceled) {
      // Select the tree view tab
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          tabbedPane.setSelectedIndex(TAB_NUMBER_TREE_VIEW);
        }
      });
    }

    return canceled ? "Tree node expansion canceled by user"
        : "Tree nodes expanded";
  }

  /**
   * Collapse all the nodes in the tree representation of the model
   * 
   * @return The final status to display
   */
  private String collapseAll() {
    setStatus("Collapsing all tree nodes");

    for (int row = ontModelTree.getRowCount(); row > 0; --row) {
      ontModelTree.collapseRow(row);
    }

    // Select the tree view tab
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        tabbedPane.setSelectedIndex(TAB_NUMBER_TREE_VIEW);
      }
    });

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
    final JRootPane rootPane = getRootPane();
    final Component glassPane = rootPane.getGlassPane();

    if (wait) {
      final Cursor cursorWait = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
      rootPane.setCursor(cursorWait);
      glassPane.setCursor(cursorWait);
      glassPane.setVisible(true);
    } else {
      final Cursor cursorDefault = Cursor
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
      switch (runningOperation) {
        case LOAD_ASSERTIONS:
          finalStatus = loadOntologyFile();
          break;
        case CREATE_MODEL:
          finalStatus = reasonerExecution();
          break;
        case BUILD_TREE_VIEW:
          finalStatus = createTreeFromModel();
          break;
        case IDENTIFY_ASSERTIONS:
          finalStatus = identifyInferredTriples();
          break;
        case EXECUTE_SPARQL:
          finalStatus = sparqlExecution();
          break;
        case EXPORT_MODEL:
          finalStatus = writeOntologyModel();
          break;
        case EXPORT_SPARQL_RESULTS:
          finalStatus = writeSparqlResults();
          break;
        case EXPAND_TREE:
          finalStatus = expandAll();
          break;
        case COLLAPSE_TREE:
          finalStatus = collapseAll();
          break;
        default:
          JOptionPane.showMessageDialog(this,
              "An instruction to execute a task was received\n"
                  + "but the task was undefined. (" + runningOperation + ")",
              "Error: No Process to Run",
              JOptionPane.ERROR_MESSAGE);
      }
    } catch (ConversionException ce) {
      LOGGER.error("Failed during conversion within the model", ce);
      finalStatus = errorAlert(
          ce,
          "An error occurred when converting a resource to a language element within the model.\n"
              + "If strict checking mode is enabled you may want to try disabling it.");
    } catch (QueryExceptionHTTP httpExc) {
      LOGGER.error("Failed during remote execution", httpExc);
      finalStatus = errorAlert(httpExc,
          "Failure attempting to query against a remote data source");
    } catch (Throwable throwable) {
      LOGGER.error("Failed during execution", throwable);
      finalStatus = errorAlert(throwable, null);
    } finally {
      setWaitCursor(false);
      enableControls(true);
      if (finalStatus != null) {
        setStatus(finalStatus);
      } else {
        setStatus("");
      }

      runningOperation = null;
    }
  }

  /**
   * Creates the status message for the error, alerts the user with a popup. If
   * the issue is a recognized syntax error and the line and column numbers can
   * be found int he exception message, the cursor will be moved to that
   * position.
   * 
   * @param throwable
   *          The error that occurred
   * @param operationDetailMessage
   *          A message specific to the operation that was running. This may be
   *          null
   * 
   * @return The message to be presented on the status line
   */
  private String errorAlert(Throwable throwable, String operationDetailMessage) {
    String statusMessage;
    String alertMessage;
    String httpStatusMessage = null;
    String causeClass;
    String causeMessage;
    QueryExceptionHTTP httpExc = null;
    int[] lineAndColumn = new int[2];
    int whichSelectedTab = -1;
    JTextArea whichFocusJTextArea = null;

    if (throwable.getCause() != null) {
      causeClass = throwable.getCause().getClass().getName();
      causeMessage = throwable.getCause().getMessage();
    } else {
      causeClass = throwable.getClass().getName();
      causeMessage = throwable.getMessage();
    }

    if (operationDetailMessage != null) {
      alertMessage = operationDetailMessage + "\n\n";
    } else {
      alertMessage = "Error:";
    }

    alertMessage += causeClass + "\n" + causeMessage;

    statusMessage =
        causeMessage.trim().length() > 0 ? causeMessage : causeClass;

    if (throwable instanceof QueryExceptionHTTP) {
      httpExc = (QueryExceptionHTTP) throwable;

      httpStatusMessage = httpExc.getResponseMessage();
      if (httpStatusMessage == null || httpStatusMessage.trim().length() == 0) {
        try {
          httpStatusMessage = HttpStatus.getStatusText(httpExc
              .getResponseCode());
        } catch (Throwable lookupExc) {
          LOGGER.info("Cannot find message for returned HTTP code of "
              + httpExc.getResponseCode());
        }
      }
    }

    if (httpExc != null) {
      statusMessage += ": "
          + "Response Code: "
          + httpExc.getResponseCode()
          + (httpStatusMessage != null && httpStatusMessage.trim().length() > 0 ? " ("
              + httpStatusMessage + ")"
              : "");

      JOptionPane.showMessageDialog(this, alertMessage
          + "\n\n"
          + "Response Code: "
          + httpExc.getResponseCode()
          + "\n"
          +
          (httpStatusMessage != null ? httpStatusMessage
              : ""),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(this, alertMessage, "Error",
          JOptionPane.ERROR_MESSAGE);
    }

    // Attempt to deal with a syntax error and positioning the cursor
    if (runningOperation == Operation.CREATE_MODEL) {
      // Assertions processing issue
      RiotException riotExc = null;
      Throwable nextThrowable = throwable;
      while (riotExc == null && nextThrowable != null) {
        if (nextThrowable instanceof RiotException) {
          riotExc = (RiotException) nextThrowable;
        } else {
          LOGGER.trace("Not a riot exception, another? "
              + throwable.getClass().toString() + "->" + throwable.getCause());
          nextThrowable = nextThrowable.getCause();
        }
      }
      if (riotExc != null) {
        lineAndColumn = getSyntaxErrorLineColLocation(riotExc
            .getMessage().toLowerCase(), "line: ", ",", "col: ", "]");
        whichSelectedTab = TAB_NUMBER_ASSERTIONS;
        whichFocusJTextArea = assertionsInput;
      } else {
        LOGGER
            .debug("No riot exception found so the caret cannot be positioned");
      }
    }
    if (runningOperation == Operation.EXECUTE_SPARQL) {
      // SPARQL processing issue
      QueryParseException queryParseExc = null;
      Throwable nextThrowable = throwable;
      while (queryParseExc == null && nextThrowable != null) {
        if (nextThrowable instanceof QueryParseException) {
          queryParseExc = (QueryParseException) nextThrowable;
        } else {
          LOGGER.trace("Not a query parse exception, another? "
              + throwable.getClass().toString() + "->" + throwable.getCause());
          nextThrowable = nextThrowable.getCause();
        }
      }
      if (queryParseExc != null) {
        lineAndColumn = getSyntaxErrorLineColLocation(queryParseExc
            .getMessage().toLowerCase(), "at line ", ",", ", column ", ".");
        whichSelectedTab = TAB_NUMBER_SPARQL;
        whichFocusJTextArea = sparqlInput;
      } else {
        LOGGER
            .debug("No query parse exception found so the caret cannot be positioned");
      }
    }

    if (lineAndColumn[0] > 0 && lineAndColumn[1] > 0) {
      LOGGER.debug("Attempt to set assertions caret to position ("
          + lineAndColumn[0] + "," + lineAndColumn[1] + ")");
      final int finalLineNumber = lineAndColumn[0] - 1;
      final int finalColumnNumber = lineAndColumn[1] - 1;
      final int finalWhichSelectedTab = whichSelectedTab;
      final JTextArea finalWhichFocusJTextArea = whichFocusJTextArea;
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          tabbedPane.setSelectedIndex(finalWhichSelectedTab);
          try {
            finalWhichFocusJTextArea.setCaretPosition(finalWhichFocusJTextArea
                .getLineStartOffset(finalLineNumber) + finalColumnNumber);
            finalWhichFocusJTextArea.requestFocusInWindow();
          }
          catch (Throwable throwable) {
            LOGGER.warn(
                "Cannot set " + finalWhichFocusJTextArea.getName()
                    + " carat position to ("
                    + finalLineNumber
                    + "," + finalColumnNumber + ") on tab "
                    + finalWhichSelectedTab, throwable);
          }
        }
      });
    }

    return statusMessage;
  }

  /**
   * Extract the line and column position in a reported syntax error. The
   * information is returned in a two-element int array. Index 0 is the line
   * number and index 1 is the column number. If the line or column number
   * cannot be found in the message, these values will be -1.
   * 
   * @param message
   *          The syntax error message containing the line and column position
   *          of the error
   * @param lineNumStartToken
   *          The token preceeding the line number in the message
   * @param lineNumEndToken
   *          The token following the line number in the message
   * @param colNumStartToken
   *          The token preceeding the column number in the message
   * @param colNumEndToken
   *          The token following the column number in the message
   * 
   * @return A 2-element array with the line number in element 0 and the column
   *         number in element 1. The value for the line and/or column will be
   *         -1 if the information cannot be found in the message.
   */
  private int[] getSyntaxErrorLineColLocation(String message,
      String lineNumStartToken, String lineNumEndToken,
      String colNumStartToken, String colNumEndToken) {
    final int[] lineAndColumn = new int[2];

    lineAndColumn[0] = -1;
    lineAndColumn[1] = -1;

    final int startLinePos = message.indexOf(lineNumStartToken);
    final int endLinePos = message.substring(startLinePos).indexOf(
        lineNumEndToken)
          + startLinePos;
    final int startColPos = message.indexOf(colNumStartToken);
    final int endColPos = message.substring(startColPos)
        .indexOf(colNumEndToken)
          + startColPos;
    if (startLinePos > -1 && startColPos > startLinePos) {
      try {
        lineAndColumn[0] = Integer.parseInt(message.substring(
            startLinePos + lineNumStartToken.length(),
              endLinePos).trim());
        lineAndColumn[1] = Integer.parseInt(message.substring(
            startColPos + colNumStartToken.length(),
              endColPos).trim());
      } catch (Throwable parseError) {
        LOGGER.warn("Cannot extract line/col from exception message: "
              + message, parseError);
      }
    }

    return lineAndColumn;
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
    final boolean okToRun = runningOperation == null;

    if (!okToRun) {
      JOptionPane.showMessageDialog(this,
          "A process is already running and must complete.\n\n"
              + "The program is currently " + runningOperation.description(),
          "Operation in Process",
          JOptionPane.INFORMATION_MESSAGE);
    }

    return okToRun;
  }

  /**
   * Setup to load the assertions and start a thread
   */
  private void runModelLoad() {
    if (okToRunThread()) {
      runningOperation = Operation.LOAD_ASSERTIONS;
      new Thread(this).start();
    }
  }

  /**
   * Setup to run the reasoner and start a thread
   */
  private void runReasoner() {
    if (okToRunThread()) {
      runningOperation = Operation.CREATE_MODEL;
      new Thread(this).start();
    }
  }

  /**
   * Setup to run the SPARQL query and start a thread
   */
  private void runSPARQL() {
    if (okToRunThread()) {
      runningOperation = Operation.EXECUTE_SPARQL;
      new Thread(this).start();
    }
  }

  /**
   * Setup to build the tree representation of the model.
   */
  private void runCreateTreeFromModel() {
    if (okToRunThread()) {
      runningOperation = Operation.BUILD_TREE_VIEW;
      new Thread(this).start();
    }
  }

  /**
   * Setup to build the list of inferred triples in the model and start a thread
   */
  private void runIdentifyInferredTriples() {
    if (okToRunThread()) {
      runningOperation = Operation.IDENTIFY_ASSERTIONS;
      new Thread(this).start();
    }
  }

  /**
   * Setup to export the model and start a thread
   */
  private void runModelExport() {
    if (okToRunThread()) {
      runningOperation = Operation.EXPORT_MODEL;
      new Thread(this).start();
    }
  }

  /**
   * Setup to export the SPARQL results to a file and start a thread
   */
  private void runSparqlResultsExport() {
    if (okToRunThread()) {
      runningOperation = Operation.EXPORT_SPARQL_RESULTS;
      new Thread(this).start();
    }
  }

  /**
   * Setup to expand all the tree nodes and start a thread
   */
  private void runExpandAllTreeNodes() {
    if (okToRunThread()) {
      runningOperation = Operation.EXPAND_TREE;
      new Thread(this).start();
    }
  }

  /**
   * Setup to collapse all tree nodes and start a thread
   */
  private void runCollapseAllTreeNodes() {
    if (okToRunThread()) {
      runningOperation = Operation.COLLAPSE_TREE;
      new Thread(this).start();
    }
  }

  /**
   * Execute the steps to run the reasoner
   * 
   * @return The message to be presented on the status line
   */
  private String reasonerExecution() {
    setStatus("Running reasoner...");
    // inferredTriples.setText("");
    loadModel();

    return "Reasoning complete";
  }

  /**
   * Execute the steps to run the SPARQL query
   * 
   * @return The message to be presented on the status line
   */
  private String sparqlExecution() {
    long numResults;

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
  private long callSparqlEngine() {
    QueryExecution qe;
    String serviceUrl;
    ResultSet resultSet;
    long numResults;

    // Get the query
    final String queryString = sparqlInput.getText().trim();

    // Create a Query instance
    final Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);

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
      // ConnectionConfiguration
      final String defaultGraphUriText = defaultGraphUri.getText().trim();

      // Check for default graph
      if (defaultGraphUriText.length() > 0) {
        // Use default graph definition

        // Check for User Id
        if (sparqlServiceUserId.getText().trim().length() == 0) {
          // Unauthenticated - open endpoint
          qe = QueryExecutionFactory.sparqlService(serviceUrl, query,
              defaultGraphUriText);
        } else {
          // Authenticated
          qe = QueryExecutionFactory.sparqlService(serviceUrl, query,
              defaultGraphUriText,
              new SimpleAuthenticator(sparqlServiceUserId.getText(),
                  sparqlServicePassword.getPassword()));

        }
      } else {
        // No default graph

        // Check for User Id
        if (sparqlServiceUserId.getText().trim().length() == 0) {
          // Unauthenticated - open endpoint
          qe = QueryExecutionFactory.sparqlService(serviceUrl, query,
              new StarDogSparqlAuthenticator());
        } else {
          // Authenticated
          qe = QueryExecutionFactory.sparqlService(serviceUrl, query,
              new SimpleAuthenticator(sparqlServiceUserId.getText(),
                  sparqlServicePassword.getPassword()));
        }
      }
    }
    resultSet = qe.execSelect();

    if (setupSparqlResultsToFile.isSelected()) {
      numResults = writeSparqlResultsDirectlyToFile(resultSet,
          new SparqlResultsFormatter(
              query, ontModel,
              setupOutputFlagLiteralValues.isSelected(),
              setupOutputDatatypesForLiterals.isSelected(),
              setupOutputFqnNamespaces.isSelected()));
    } else {
      final SparqlTableModel tableModel = (SparqlTableModel) sparqlResultsTable
          .getModel();
      final SparqlResultItemRenderer renderer = new SparqlResultItemRenderer(
          setupAllowMultilineResultOutput.isSelected());
      renderer.setFont(sparqlResultsTable.getFont());
      sparqlResultsTable.setDefaultRenderer(SparqlResultItem.class, renderer);
      tableModel.setupModel(resultSet, query, ontModel,
          setupOutputFlagLiteralValues.isSelected(),
          setupOutputDatatypesForLiterals.isSelected(),
          setupOutputFqnNamespaces.isSelected(),
          setupDisplayImagesInSparqlResults.isSelected());
      numResults = tableModel.getRowCount();
    }
    // Important - free up resources used running the query
    qe.close();

    return numResults;
  }

  /**
   * Get the set of defined ontology file formats that the program can load as
   * a CSV list String
   * 
   * @return The known ontology file formats as a CSV list
   */
  public static final String getFormatsAsCSV() {
    return getArrayAsCSV(FORMATS);
  }

  /**
   * Get the set of reasoning levels that the program will use as a CSV list
   * String
   * 
   * @return The known reasoning levels as a CSV list
   */
  public static final String getReasoningLevelsAsCSV() {
    return getArrayAsCSV(REASONER_SELECTIONS.toArray());
  }

  /**
   * Create a CSV list from a String array
   * 
   * @param array
   *          An array
   * @return The array values in a CSV list
   */
  public static final String getArrayAsCSV(Object[] array) {
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
   * @param pRdfFileSource
   *          The FileSource of the ontology
   */
  public void setRdfFileSource(FileSource pRdfFileSource) {
    rdfFileSource = pRdfFileSource;
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

    if (rdfFileSource != null) {
      title += " - " + rdfFileSource.getName();
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
    // tempModel = ontModel.difference(ontModelNoInference);
    tempModel = ontModel.difference(ontModel.getBaseModel());
    LOGGER.debug("Model differences computed to identify inferred triples");

    writer = new StringWriter();
    tempModel.write(writer, assertionLanguage);
    LOGGER
        .debug("String representation of differences created to show inferred triples using "
            + assertionLanguage);

    inferredTriples.setText(writer.toString());

    areInferencesInSyncWithModel = true;
    colorCodeTabs();

    // Select the inferences tab
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        tabbedPane.setSelectedIndex(TAB_NUMBER_INFERENCES);
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
    OntModel model = null;

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
    Throwable lastThrowable = null;

    modelFormat = null;

    isTreeInSyncWithModel = false;
    areInferencesInSyncWithModel = false;
    areSparqlResultsInSyncWithModel = false;

    if (language.getSelectedIndex() == 0) {
      for (String format : FORMATS) {
        try {
          tryFormat(format);
          modelFormat = format;
          break;
        } catch (Throwable throwable) {
          LOGGER.debug("Error processing assertions as format: "
              + format, throwable);
          lastThrowable = throwable;
        }
      }
    } else {
      try {
        tryFormat(language.getSelectedItem().toString());
        modelFormat = language.getSelectedItem().toString();
      } catch (Throwable throwable) {
        LOGGER.error("Error processing assertions as format: "
            + language.getSelectedItem().toString(), throwable);
        lastThrowable = throwable;
      }
    }

    if (modelFormat == null) {
      if (ontModel != null) {
        ontModel.close();
      }

      invalidateModel(false);

      if (language.getSelectedIndex() == 0) {
        throw new IllegalStateException(
            "The assertions cannot be loaded using known languages.\nTried: "
                + getFormatsAsCSV(), lastThrowable);
      } else {
        throw new IllegalStateException(
            "The assertions cannot be loaded using the input format: "
                + language.getSelectedItem().toString(), lastThrowable);
      }
    } else {
      LOGGER.info("Loaded assertions" + " using format: " + modelFormat);
      assertedTripleCount.setText(INTEGER_COMMA_FORMAT.format(ontModel
          .getBaseModel().size()) + "");
      inferredTripleCount.setText(INTEGER_COMMA_FORMAT.format(ontModel.size()
          - ontModel.getBaseModel()
              .size())
          + "");
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
   * 
   * @throws IOException
   *           If the file cannot be read
   */
  private void tryFormat(String format) throws IOException {
    InputStream inputStream = null;

    try {
      LOGGER.debug("Start " + reasoningLevel.getSelectedItem().toString()
          + " model load and setup with format " + format);

      if (hasIncompleteAssertionsInput) {
        inputStream = new ProgressMonitorInputStream(this,
            "Reading file "
                + rdfFileSource.getAbsolutePath(),
            rdfFileSource.getInputStream());

        if (rdfFileSource.isUrl()) {
          final ProgressMonitor pm = ((ProgressMonitorInputStream) inputStream)
              .getProgressMonitor();
          // pm.setMillisToDecideToPopup(0);
          // pm.setMillisToPopup(0);
          pm.setMaximum((int) rdfFileSource.length());
        }
        LOGGER.debug("Using a ProgressMonitorInputStream");
      } else {
        inputStream = new ByteArrayInputStream(assertionsInput.getText()
            .getBytes("UTF-8"));
      }

      ontModel = createModel(reasoningLevel.getSelectedIndex());
      LOGGER.debug("Begin loading model");
      ontModel.read(inputStream, null, format.toUpperCase());

      LOGGER.debug(reasoningLevel.getSelectedItem().toString()
          + " model load and setup completed");
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (Throwable throwable) {
          LOGGER.error("Error closing input file", throwable);
        }
      }
    }
  }

  /**
   * Build a tree representation of the semantic model
   * 
   * TODO aggregate items from duplicate nodes
   * 
   * TODO Consider more efficient approach that scans the model once rather than
   * quering for each class, individual and property collection
   * 
   * @see OntologyTreeCellRenderer
   * 
   * @return The message to be presented on the status line
   */
  private String createTreeFromModel() {
    final String messagePrefix = "Creating the tree view";
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
    String message;

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

        if (progress.isCanceled()) {
          throw new RuntimeException("Tree model creation canceled by user");
        }

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

      // Select the tree view tab
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          tabbedPane.setSelectedIndex(TAB_NUMBER_TREE_VIEW);
        }
      });
      message = "Tree view of current model created";
      ontModelTree.setModel(new DefaultTreeModel(treeTopNode));
      isTreeInSyncWithModel = true;
      colorCodeTabs();
    } catch (RuntimeException rte) {
      if (rte.getMessage().contains("canceled by user")) {
        message = rte.getMessage();
      } else {
        throw rte;
      }
    } finally {
      if (progress != null) {
        progress.close();
      }
    }

    return message;
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
  public static final int getIndexValue(String[] array, String name) {
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
    final Wrapper wrapper = getSelectedWrapperInTree(event);
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
      final DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeModel
          .getRoot();
      @SuppressWarnings("unchecked")
      final Enumeration<DefaultMutableTreeNode> nodeEnumeration = node
          .preorderEnumeration();
      while (nodeEnumeration.hasMoreElements()) {
        final DefaultMutableTreeNode nextNode = nodeEnumeration.nextElement();
        if (nextNode.getUserObject() instanceof Wrapper) {
          final Wrapper nodeWrapper = (Wrapper) nextNode.getUserObject();
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

        // Scroll to the selected node
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            ontModelTree.setSelectionPath(new TreePath(treeModel
                .getPathToRoot(finalMatchAt)));
            ontModelTree.scrollPathToVisible(new TreePath(treeModel
                .getPathToRoot(finalMatchAt)));
            final Rectangle visible = ontModelTree.getVisibleRect();
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
    final Wrapper wrapper = getSelectedWrapperInTree(event);
    if (wrapper != null) { // && wrapper.getUri().indexOf("Anonymous") == -1) {
      if (wrapper instanceof WrapperClass
            || wrapper instanceof WrapperDataProperty
            || wrapper instanceof WrapperObjectProperty) {
        final FilterValuePopup popup = new FilterValuePopup(wrapper);
        popup.show(event.getComponent(), event.getX(), event.getY());
      } else if (wrapper instanceof WrapperInstance) {
        findMatchingIndividual(wrapper, false);
      }
    }
  }

  /**
   * Invalidates the existing ontology model.
   * 
   * @param alterControls
   *          Whether or not the current enable/disable setting of GUI controls
   *          should be updated
   */
  private void invalidateModel(boolean alterControls) {
    if (ontModel != null) {
      ontModel.close();
    }

    ontModel = null;
    assertedTripleCount.setText(NOT_APPLICABLE_DISPLAY);
    inferredTripleCount.setText(NOT_APPLICABLE_DISPLAY);

    reasoningLevel.setToolTipText(((ReasonerSelection) reasoningLevel
        .getSelectedItem()).getDescription());

    isTreeInSyncWithModel = false;
    areInferencesInSyncWithModel = false;
    areSparqlResultsInSyncWithModel = false;

    if (alterControls) {
      enableControls(true);
    }
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
    final TreePath path = ontModelTree.getPathForLocation(event.getX(),
        event.getY());
    if (path != null) {
      LOGGER.debug("Tree right-mouse event on: " + path.getLastPathComponent()
          + " of class " + path.getLastPathComponent().getClass());
      if (path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
        final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path
            .getLastPathComponent();
        final Object selectedObject = selectedNode.getUserObject();
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
      setupToLoadOntologyFile(new FileSource(chosenFile));
      // loadOntologyFile(chosenFile);
      // } else {
      // JOptionPane.showMessageDialog(this, "No file to load",
      // "No File Selected", JOptionPane.WARNING_MESSAGE);
    }
  }

  /**
   * Loads the assertions from a URL (e.g. across the network)
   */
  private void openOntologyUrl() {
    String urlString = null;
    URL url = null;

    do {
      try {
        urlString = JOptionPane.showInputDialog(this,
            "Input the URL for the remote ontology file to load.",
            "Input Ontology File URL", JOptionPane.QUESTION_MESSAGE);
        if (urlString != null) {
          urlString = urlString.trim();
          if (urlString.length() > 0) {
            url = new URL(urlString);
          }
        }
      } catch (Throwable throwable) {
        LOGGER.error("Unable to open the URL", throwable);
        JOptionPane.showMessageDialog(this,
            "Incorrect format for a URL, cannot be parsed\n" + urlString
                + "\n\n" + throwable.getMessage(), "Incorrect URL Format",
            JOptionPane.ERROR_MESSAGE);
        url = null;
      }
    } while (url == null && urlString != null && urlString.length() > 0);

    if (url != null) {
      setupToLoadOntologyFile(new FileSource(url));
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
      setupToLoadOntologyFile(recentAssertionsFiles.get(chosenFileIndex));
      // loadOntologyFile(recentAssertionsFiles.get(chosenFileIndex));
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
   * @return The message to be presented on the status line
   */
  private String loadOntologyFile() {
    final int chunkSize = 32000;
    StringBuilder allData;
    char[] chunk;
    long totalBytesRead = 0;
    int chunksRead = 0;
    int maxChunks;
    int bytesRead = -1;
    ProgressMonitor monitor = null;
    Reader reader = null;
    String message;
    boolean loadCanceled = false;

    if (rdfFileSource.isFile()) {
      lastDirectoryUsed = rdfFileSource.getBackingFile().getParentFile();
    }

    assertionsInput.setText("");
    invalidateModel(false);

    allData = new StringBuilder();
    chunk = new char[chunkSize];

    setStatus("Loading file " + rdfFileSource.getAbsolutePath());

    if (rdfFileSource.length() > 0
        && rdfFileSource.length() < MAX_ASSERTION_BYTES_TO_LOAD_INTO_TEXT_AREA) {
      maxChunks = (int) (rdfFileSource.length() / chunkSize);
    } else {
      maxChunks = (int) (MAX_ASSERTION_BYTES_TO_LOAD_INTO_TEXT_AREA / chunkSize);
    }

    if (rdfFileSource.length() % chunkSize > 0) {
      ++maxChunks;
    }

    // Assume the file can be loaded
    hasIncompleteAssertionsInput = false;

    monitor = new ProgressMonitor(this, "Loading assertions from "
        + rdfFileSource.getName(), "0 bytes read", 0, maxChunks);

    try {
      reader = new InputStreamReader(rdfFileSource.getInputStream());
      // reader = new BufferedReader(new FileReader(inputFile));
      // while ((data = reader.readLine()) != null) {
      while (!loadCanceled && (rdfFileSource.isUrl() || chunksRead < maxChunks)
          && (bytesRead = reader.read(chunk)) > -1) {
        totalBytesRead += bytesRead;

        chunksRead = (int) (totalBytesRead / chunk.length);

        if (chunksRead < maxChunks) {
          allData.append(chunk, 0, bytesRead);
        }

        if (chunksRead >= maxChunks) {
          monitor.setMaximum(chunksRead + 1);
        }

        monitor.setProgress(chunksRead);
        monitor
            .setNote("Read "
                + INTEGER_COMMA_FORMAT.format(totalBytesRead)
                + (rdfFileSource.isFile() ? " of "
                    + INTEGER_COMMA_FORMAT.format(rdfFileSource.length())
                    : " bytes")
                + (chunksRead >= maxChunks ? " (Determining total file size)"
                    : ""));

        loadCanceled = monitor.isCanceled();
      }

      if (!loadCanceled && rdfFileSource.isUrl()) {
        rdfFileSource.setLength(totalBytesRead);
      }

      if (!loadCanceled
          && rdfFileSource.length() > MAX_ASSERTION_BYTES_TO_LOAD_INTO_TEXT_AREA) { // &&
                                                                                    // bytesRead
                                                                                    // !=
                                                                                    // -1)
                                                                                    // {
        // The entire file was not loaded
        hasIncompleteAssertionsInput = true;
      }

      if (hasIncompleteAssertionsInput) {
        StringBuilder warningMessage;

        warningMessage = new StringBuilder();
        warningMessage
            .append("The file is too large to display. However the entire file will be loaded\n");
        warningMessage
            .append("into the model when it is built.\n\nDisplay size limit (bytes): ");
        warningMessage.append(INTEGER_COMMA_FORMAT
                        .format(MAX_ASSERTION_BYTES_TO_LOAD_INTO_TEXT_AREA));
        if (rdfFileSource.isFile()) {
          warningMessage.append("\nFile size (bytes):");
          warningMessage.append(INTEGER_COMMA_FORMAT.format(rdfFileSource
              .length()));
        }
        warningMessage.append("\n\n");
        warningMessage
            .append("Note that the assersions text area will not permit editing\n");
        warningMessage
            .append("of the partially loaded file and the 'save assertions' menu\n");
        warningMessage
            .append("option will be disabled. These limitations are enabled\n");
        warningMessage
            .append("to prevent the accidental loss of information from the\n");
        warningMessage.append("source assertions file.");

        JOptionPane
            .showMessageDialog(
                this,
                warningMessage.toString(),
                "Max Display Size Reached"
                , JOptionPane.WARNING_MESSAGE);

        // Add text to the assertions text area to highlight the fact that the
        // entire file was not loaded into the text area
        allData.insert(0, "# First "
            + INTEGER_COMMA_FORMAT
                .format(MAX_ASSERTION_BYTES_TO_LOAD_INTO_TEXT_AREA)
            + " of " + INTEGER_COMMA_FORMAT.format(rdfFileSource.length())
            + " bytes displayed\n\n");
        allData.insert(0, "# INCOMPLETE VERSION of the file: "
            + rdfFileSource.getAbsolutePath() + "\n");
        allData.append("\n\n# INCOMPLETE VERSION of the file: "
            + rdfFileSource.getAbsolutePath() + "\n");
        allData.append("# First "
            + INTEGER_COMMA_FORMAT
                .format(MAX_ASSERTION_BYTES_TO_LOAD_INTO_TEXT_AREA)
            + " of " + INTEGER_COMMA_FORMAT.format(rdfFileSource.length())
            + " bytes displayed\n");
      }

      // Set the loaded assertions into the text area, cleaning up Windows \r\n
      // endings, if found
      if (!loadCanceled) {
        assertionsInput.setText(allData.toString().replaceAll("\r\n", "\n"));
        assertionsInput.moveCaretPosition(0);

        message = "Loaded file"
            + (hasIncompleteAssertionsInput ? " (incomplete)" : "") + ": "
            + rdfFileSource.getName();
        addRecentAssertedTriplesFile(rdfFileSource);

        // Select the assertions tab
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            tabbedPane.setSelectedIndex(TAB_NUMBER_ASSERTIONS);
          }
        });
      } else {
        message = "Assertions file load canceled by user";
      }
    } catch (Throwable throwable) {
      setStatus("Unable to load file: " + rdfFileSource.getName());
      JOptionPane.showMessageDialog(this,
          "Error: Unable to read file\n\n"
              + rdfFileSource.getAbsolutePath() + "\n\n"
              + throwable.getMessage(), "Error Reading File",
          JOptionPane.ERROR_MESSAGE);
      LOGGER.error("Unable to load the file: "
          + rdfFileSource.getAbsolutePath(), throwable);
      message = "Unable to load the file: "
          + rdfFileSource.getAbsolutePath();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (Throwable throwable) {
          LOGGER.error("Unable to close input file", throwable);
        }
      }

      if (monitor != null) {
        monitor.close();
      }
    }

    return message;
  }

  /**
   * Extract a value located in a comment in a file and idnetified by a prefix
   * in front of the value. This is used to store information such as the
   * service URL for a sparql query as a comment in the query file.
   * 
   * @param data
   *          The line containing the prefix and associated value
   * @param prefix
   *          The prefix identifying the value
   * 
   * @return The extracted value (following the prefix) or null if the prefix is
   *         not found or no value follows it
   */
  private String extractCommentData(String data, String prefix) {
    String extractedValue = null;
    int location;

    location = data.indexOf(prefix);
    if (location > -1 && data.length() > location + prefix.length()) {
      extractedValue = data.substring(location + prefix.length()).trim();
    }

    return extractedValue;
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
    String serviceUrlFromFile = null;
    String defaultGraphUriFromFile = null;

    lastDirectoryUsed = inputFile.getParentFile();

    reader = null;
    allData = new StringBuffer();

    try {
      reader = new BufferedReader(new FileReader(inputFile));
      while ((data = reader.readLine()) != null) {
        if (data.indexOf(SPARQL_QUERY_SAVE_SERVICE_URL_PARAM) > -1) {
          serviceUrlFromFile = extractCommentData(data,
              SPARQL_QUERY_SAVE_SERVICE_URL_PARAM);
        } else if (data.indexOf(SPARQL_QUERY_SAVE_SERVICE_DEFAULT_GRAPH_PARAM) > -1) {
          defaultGraphUriFromFile = extractCommentData(data,
              SPARQL_QUERY_SAVE_SERVICE_DEFAULT_GRAPH_PARAM);
        } else {
          allData.append(data);
          allData.append('\n');
        }
      }

      sparqlInput.setText(allData.toString());
      sparqlInput.moveCaretPosition(0);

      if (defaultGraphUriFromFile != null) {
        defaultGraphUri.setText(defaultGraphUriFromFile);
      }

      if (serviceUrlFromFile != null) {
        int matchAt = -1;
        if (serviceUrlFromFile
            .equals(SPARQL_QUERY_SAVE_SERVICE_URL_VALUE_FOR_NO_SERVICE_URL)) {
          matchAt = 0;
        } else {
          for (int index = 1; matchAt == -1
              && index < sparqlServiceUrl.getItemCount(); ++index) {
            if (sparqlServiceUrl.getItemAt(index).toString()
                .equals(serviceUrlFromFile)) {
              matchAt = index;
            }
          }
        }
        if (matchAt > -1) {
          sparqlServiceUrl.setSelectedIndex(matchAt);
        } else {
          sparqlServiceUrl.addItem(serviceUrlFromFile);
        }
      }

      setStatus("Loaded SPARQL query file: " + inputFile.getName());
      sparqlQueryFile = inputFile;
      addRecentSparqlFile(inputFile);

      // Select the SPARQL tab
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          tabbedPane.setSelectedIndex(TAB_NUMBER_SPARQL);
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

      invalidateSparqlResults(true);
    }
  }

  /**
   * Remove prior SPARQL results if the SPARQL query changes
   * 
   * @param alterControls
   *          Whether or not the current enable/disable setting of GUI controls
   *          should be updated
   */
  private void invalidateSparqlResults(boolean alterControls) {
    // SparqlTableModel tableModel = (SparqlTableModel) sparqlResultsTable
    // .getModel();

    areSparqlResultsInSyncWithModel = false;

    // tableModel.clearModel();
    if (alterControls) {
      enableControls(true);
    }
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

    if (rdfFileSource != null && rdfFileSource.isFile()) {
      fileChooser.setSelectedFile(rdfFileSource.getBackingFile());
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

    okayToWrite = okToOverwriteFile(destinationFile);
    /*
     * okayToWrite = !destinationFile.exists();
     * if (!okayToWrite) {
     * int verifyOverwrite;
     * verifyOverwrite = JOptionPane.showConfirmDialog(this,
     * "The file exists: " + destinationFile.getName()
     * + "\n\nOkay to overwrite?", "Overwrite File?",
     * JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
     * okayToWrite = verifyOverwrite == JOptionPane.YES_OPTION;
     * }
     */

    if (okayToWrite) {

      LOGGER.info("Write assertions to file, " + destinationFile
          + ", in format: " + assertionLanguage);

      try {
        out = new FileWriter(destinationFile, false);
        out.write(assertionsInput.getText());
        setRdfFileSource(new FileSource(destinationFile));
        addRecentAssertedTriplesFile(new FileSource(destinationFile));
      } catch (IOException ioExc) {
        final String errorMessage = "Unable to write to file: "
            + destinationFile;
        LOGGER.error(errorMessage, ioExc);
        throw new RuntimeException(errorMessage, ioExc);
      } finally {
        if (out != null) {
          try {
            out.close();
          } catch (Throwable throwable) {
            final String errorMessage = "Failed to close output file: "
                + destinationFile;
            LOGGER.error(errorMessage, throwable);
            throw new RuntimeException(
                errorMessage, throwable);
          }
        }
      }
    }
  }

  /**
   * Determine whether an output file already exists and if so, popup a dialog
   * asking they user whether it is okay to overwrite the existing file.
   * 
   * @param destinationFile
   *          The output file
   * 
   * @return True of the file may be written (overwritten)
   */
  private boolean okToOverwriteFile(File destinationFile) {
    boolean okayToWrite = !destinationFile.exists();
    if (!okayToWrite) {
      int verifyOverwrite;
      verifyOverwrite = JOptionPane.showConfirmDialog(this,
          "The file exists: " + destinationFile.getName()
              + "\n\nOkay to overwrite?", "Overwrite File?",
          JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      okayToWrite = verifyOverwrite == JOptionPane.YES_OPTION;
    }

    return okayToWrite;
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

    okayToWrite = okToOverwriteFile(destinationFile);
    /*
     * okayToWrite = !destinationFile.exists();
     * if (!okayToWrite) {
     * int verifyOverwrite;
     * verifyOverwrite = JOptionPane.showConfirmDialog(this,
     * "The file exists: " + destinationFile.getName()
     * + "\n\nOkay to overwrite?", "Overwrite File?",
     * JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
     * okayToWrite = verifyOverwrite == JOptionPane.YES_OPTION;
     * }
     */

    if (okayToWrite) {

      LOGGER.info("Write SPARQL query to file, " + destinationFile);

      try {
        out = new FileWriter(destinationFile, false);

        if (sparqlServiceUrl.getSelectedIndex() != 0) {

          out.write("# " + SPARQL_QUERY_SAVE_SERVICE_URL_PARAM
              + sparqlServiceUrl.getSelectedItem() + "\n");
        } else {
          out.write("# " + SPARQL_QUERY_SAVE_SERVICE_URL_PARAM
              + SPARQL_QUERY_SAVE_SERVICE_URL_VALUE_FOR_NO_SERVICE_URL + "\n");
        }

        if (defaultGraphUri.getText().trim().length() > 0) {
          out.write("# " + SPARQL_QUERY_SAVE_SERVICE_DEFAULT_GRAPH_PARAM
              + defaultGraphUri.getText().trim()
              + "\n");
        }
        out.write(sparqlInput.getText());
        sparqlQueryFile = destinationFile;
        addRecentSparqlFile(destinationFile);
      } catch (IOException ioExc) {
        final String errorMessage = "Unable to write to file: "
            + destinationFile;
        LOGGER.error(errorMessage, ioExc);
        throw new RuntimeException(errorMessage, ioExc);
      } finally {
        if (out != null) {
          try {
            out.close();
          } catch (Throwable throwable) {
            final String errorMessage = "Failed to close output file: "
                + destinationFile;
            LOGGER.error(errorMessage, throwable);
            throw new RuntimeException(errorMessage, throwable);
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

  /**
   * Notify the user that a newer version of the program has been released.
   * 
   * @param newVersionInformation
   *          Information about the new version of the program
   */
  private void notifyNewerVersion(NewVersionInformation newVersionInformation) {
    if (newVersionInformation.getUrlToDownloadPage() != null) {
      int choice;

      choice = JOptionPane.showConfirmDialog(
          this,
          "There is a newer version of Semantic Workbench Available\n"
              + "You are running version " + VERSION
              + " and the latest version is "
              + newVersionInformation.getLatestVersion()
              + "\n\n"
              + newVersionInformation.getDownloadInformation()
              + "\n"
              + "New features include:\n"
              + newVersionInformation.getNewFeaturesDescription() + "\n\n"
              + "Would you like to download the new version now?\n\n",
          "Newer Version Available (" + VERSION + "->"
              + newVersionInformation.getLatestVersion() + ")",
          JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

      if (choice == JOptionPane.YES_OPTION) {
        try {
          Desktop.getDesktop().browse(
              newVersionInformation.getUrlToDownloadPage().toURI());
        } catch (Throwable throwable) {
          LOGGER.error("Cannot launch browser to access download page",
              throwable);
          JOptionPane.showMessageDialog(this,
              "Unable to launch a browser to access the download page\n"
                  + "at "
                  + newVersionInformation.getUrlToDownloadPage().toString()
                  + "\n\n" + throwable.getMessage(),
              "Unable to Access Download Page", JOptionPane.ERROR_MESSAGE);
        }
      }
    } else {
      JOptionPane.showMessageDialog(
          this,
          "There is a newer version of Semantic Workbench Available\n"
              + "You are running version " + VERSION
              + " and the latest version is "
              + newVersionInformation.getLatestVersion()
              + "\n\n"
              + "New features include:\n"
              + newVersionInformation.getNewFeaturesDescription(),
          "Newer Version Available (" + VERSION + "->"
              + newVersionInformation.getLatestVersion() + ")",
          JOptionPane.INFORMATION_MESSAGE);
    }
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
        areaToUpdate = assertionsInput;
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
   * Setup to read assertions from a file source
   * 
   * @param inputFileSource
   *          The file source of the assertions
   */
  private void setupToLoadOntologyFile(FileSource inputFileSource) {
    if (inputFileSource.isFile() && !inputFileSource.getBackingFile().isFile()) {
      JOptionPane.showMessageDialog(this,
          "Cannot read the file\n" + inputFileSource.getAbsolutePath(),
          "Cannot Read Assertions File", JOptionPane.ERROR_MESSAGE);
    } else {
      setRdfFileSource(inputFileSource);
      runModelLoad();
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

    if (rdfFileSource != null && rdfFileSource.isFile()) {
      fileChooser.setSelectedFile(rdfFileSource.getBackingFile());
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

    okayToWrite = okToOverwriteFile(destinationFile);
    /*
     * okayToWrite = !destinationFile.exists();
     * if (!okayToWrite) {
     * int verifyOverwrite;
     * verifyOverwrite = JOptionPane.showConfirmDialog(this,
     * "The file exists: " + destinationFile.getName()
     * + "\n\nOkay to overwrite?", "Overwrite File?",
     * JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
     * okayToWrite = verifyOverwrite == JOptionPane.YES_OPTION;
     * }
     */

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
   * @return The message to be presented on the status line
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
        ontModel.getBaseModel().write(out, getSelectedOutputLanguage()
            .toUpperCase());
      }
      message = "Completed writing " + message;
    } catch (IOException ioExc) {
      LOGGER.error("Unable to write file: " + modelExportFile,
          ioExc);
      setStatus("Failed to write file (check log) " + message);
      throw new RuntimeException("Unable to write output file ("
          + modelExportFile + ")", ioExc);
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (Throwable throwable) {
          final String errorMessage = "Failed to close output file: "
              + modelExportFile;
          LOGGER.error(errorMessage, throwable);
          throw new RuntimeException(errorMessage, throwable);
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

    okayToWrite = okToOverwriteFile(destinationFile);
    /*
     * okayToWrite = !destinationFile.exists();
     * if (!okayToWrite) {
     * int verifyOverwrite;
     * verifyOverwrite = JOptionPane.showConfirmDialog(this,
     * "The file exists: " + destinationFile.getName()
     * + "\n\nOkay to overwrite?", "Overwrite File?",
     * JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
     * okayToWrite = verifyOverwrite == JOptionPane.YES_OPTION;
     * }
     */

    if (okayToWrite) {
      sparqlResultsExportFile = destinationFile;
      runSparqlResultsExport();
      // writeOntologyModel(destinationFile);
    }
  }

  /**
   * Send QPARQL results to a file without presenting them in the results table.
   * This allows for arbitrarily large result sets to be handled and written
   * since there is no memory limitation being imposed by attempting to
   * represent the results within the GUI
   * 
   * @param results
   *          The result set from the SPARQL query
   * @param formatter
   *          The formatter to be used to output the results
   * 
   * @return The number of result rows written to the file
   */
  private long writeSparqlResultsDirectlyToFile(ResultSet results,
      SparqlResultsFormatter formatter) {
    String message;
    long numRows = 0;
    List<String> columns;
    PrintWriter out = null;
    final boolean toCsv = setupExportSparqlResultsAsCsv.isSelected();
    int fileNumber = 0;
    File outputFile;
    File outputDirectory;
    final SparqlTableModel tableModel = (SparqlTableModel) sparqlResultsTable
        .getModel();
    final SparqlResultItemRenderer renderer = new SparqlResultItemRenderer(
        setupAllowMultilineResultOutput.isSelected());
    renderer.setFont(sparqlResultsTable.getFont());
    sparqlResultsTable.setDefaultRenderer(SparqlResultItem.class, renderer);

    if (lastDirectoryUsed != null) {
      outputDirectory = lastDirectoryUsed;
    } else {
      outputDirectory = new File(".");
    }

    // Create a unique file
    do {
      ++fileNumber;
      outputFile = new File(outputDirectory, SPARQL_DIRECT_EXPORT_FILE_PREFIX
          + fileNumber + "." + (toCsv ? "csv" : "txt"));
    } while (outputFile.exists());

    message = "SPARQL results ("
        + (toCsv ? EXPORT_FORMAT_LABEL_CSV : EXPORT_FORMAT_LABEL_TSV)
        + ") directly to "
        + outputFile.getAbsolutePath();

    setStatus("Writing " + message);

    tableModel.displayMessageInTable("Exporting Results Directly to File",
        new String[] {
          outputFile.getAbsolutePath()
        });

    LOGGER
        .info("Write SPARQL results to file, " + outputFile.getAbsolutePath());

    try {
      out = new PrintWriter(new FileWriter(outputFile));

      // Output column names
      columns = results.getResultVars();
      for (int columnNumber = 0; columnNumber < columns.size(); ++columnNumber) {
        if (columnNumber > 0) {
          out.print(toCsv ? ',' : '\t');
        }

        if (toCsv) {
          out.print(TextProcessing.formatForCsvColumn(columns.get(columnNumber)));
        } else {
          out.print(TextProcessing.formatForTsvColumn(columns.get(columnNumber)));
        }
      }

      out.println();

      // Output data
      while (results.hasNext()) {
        ++numRows;
        final QuerySolution solution = results.next();

        for (int columnNumber = 0; columnNumber < columns.size(); ++columnNumber) {
          if (columnNumber > 0) {
            out.print(toCsv ? ',' : '\t');
          }

          if (toCsv) {
            out.print(TextProcessing.formatForCsvColumn(formatter.format(
                solution, columns.get(columnNumber), false)));
          } else {
            out.print(TextProcessing.formatForTsvColumn(formatter.format(
                solution, columns.get(columnNumber), false)));
          }
        }

        out.println();
      }
    } catch (Throwable throwable) {
      LOGGER.error(
          "Failed to write SPARQL results to " + outputFile.getAbsolutePath(),
          throwable);
      tableModel.displayMessageInTable("Exporting Results Directly to File",
          new String[] {
              "Failed to write the file", throwable.getMessage(),
              outputFile.getAbsolutePath()
          });
      throw new RuntimeException("Failed to write SPARQL results to "
          + outputFile.getAbsolutePath(), throwable);
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (Throwable throwable) {
          LOGGER.warn("Unable to close output file: " + outputFile);
        }
      }
    }

    return numRows;
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
   * @return The message to be presented on the status line
   */
  private String writeSparqlResults() {
    PrintWriter out = null;
    boolean toCsv;

    // Either CSV or TSV currently
    toCsv = setupExportSparqlResultsAsCsv.isSelected();

    String message = "SPARQL results ("
        + (toCsv ? EXPORT_FORMAT_LABEL_CSV : EXPORT_FORMAT_LABEL_TSV) + ") to "
        + sparqlResultsExportFile;

    setStatus("Writing " + message);

    LOGGER.info("Write SPARQL results to file, " + sparqlResultsExportFile);

    try {
      out = new PrintWriter(new FileWriter(sparqlResultsExportFile, false));

      final SparqlTableModel model = (SparqlTableModel) sparqlResultsTable
          .getModel();

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
            out.print(TextProcessing.formatForCsvColumn(model
                .getValueAt(rowNumber, columnNumber)));
          } else {
            out.print(TextProcessing.formatForTsvColumn(model
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
          final String errorMessage = "Unable to close output file: "
              + sparqlResultsExportFile;
          LOGGER.error(errorMessage, throwable);
          throw new RuntimeException(
              errorMessage, throwable);
        }
      }
    }

    return message;
  }

  /**
   * Evaluate the SPARQL service field and see if a new service address has been
   * entered
   */
  private void processSparqlServiceChoice() {
    final String currentSelection = sparqlServiceUrl.getSelectedItem()
        .toString();

    LOGGER.debug("SPARQL Service URL Change: Index: "
        + sparqlServiceUrl.getSelectedIndex() + " Value: [" + currentSelection
        + "]");

    // Add a new service URL to the dropdown
    // A new service URL will likely be at least 13 characters long
    // e.g. http://1.2.3.4
    if (sparqlServiceUrl.getSelectedIndex() == -1
        && currentSelection.trim().length() > 10) {
      sparqlServiceUrl.addItem(currentSelection);
    }

    enableControls(true);
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
    message.append("\n\nDavid Read\n");
    message.append("http://monead.com/\n\n");
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
    LOGGER.info("Shutdown application");
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

  /**
   * Handle mnouse events in the ontology tree view
   */
  private class OntologyModelTreeMouseListener extends MouseAdapter {
    /**
     * No operation
     */
    public OntologyModelTreeMouseListener() {

    }

    @Override
    public void mouseClicked(MouseEvent event) {
      if (event.getButton() == MouseEvent.BUTTON1) {
        processOntologyModelTreeLeftClick(event);
      } else {
        // Assume right-mouse click (e.g. not button 1)
        processOntologyModelTreeRightClick(event);
      }
    }
  }

  /**
   * A popup menu for allowing the user to select a set of values to be removed
   * from a list
   */
  private class FilterValuePopup extends JPopupMenu implements ActionListener {
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 6587807055541029847L;

    /**
     * The collection of items to be shown to the user
     */
    private Wrapper wrappedObject;

    /**
     * The menu item to be presented to the user
     */
    private JMenuItem menuItem;

    /**
     * Setup the menu item with the list of objects
     * 
     * @param pWrappedObject
     *          The collection of items to be shown to the user
     */
    public FilterValuePopup(Wrapper pWrappedObject) {
      wrappedObject = pWrappedObject;

      menuItem = new JMenuItem("Filter out: " + wrappedObject.toString());
      menuItem.addActionListener(this);
      add(menuItem);

      // Don't need listener, no action to take for cancel
      add(new JMenuItem("Cancel"));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
      if (event.getSource() instanceof JMenuItem) {
        final JMenuItem source = (JMenuItem) event.getSource();
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
    /**
     * No operation
     */
    public ReasonerListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      runReasoner();
    }
  }

  /**
   * Used to detect configuration changes that would cause the reasoner to
   * behave differently, thereby invalidating the current model.
   */
  private class ReasonerConfigurationChange implements ActionListener {
    /**
     * No operation
     */
    public ReasonerConfigurationChange() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      invalidateModel(true);
    }
  }

  /**
   * Executes the SPARQL query in response to action from any widget being
   * monitored
   */
  private class SparqlListener implements ActionListener {
    /**
     * No operation
     */
    public SparqlListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      runSPARQL();
    }
  }

  /**
   * Opens an ontology file
   */
  private class FileAssertedTriplesOpenListener implements ActionListener {
    /**
     * No operation
     */
    public FileAssertedTriplesOpenListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      openOntologyFile();
    }
  }

  /**
   * Opens an ontology using a URL
   */
  private class FileAssertedTriplesUrlOpenListener implements ActionListener {
    /**
     * No operation
     */
    public FileAssertedTriplesUrlOpenListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      openOntologyUrl();
    }
  }

  /**
   * Loads a recently accessed ontology file
   */
  private class RecentAssertedTriplesFileOpenListener implements ActionListener {
    /**
     * No operation
     */
    public RecentAssertedTriplesFileOpenListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      openRecentOntologyFile(e.getSource());
    }
  }

  /**
   * Loads a recently accessed ontology file
   */
  private class RecentSparqlFileOpenListener implements ActionListener {
    /**
     * No operation
     */
    public RecentSparqlFileOpenListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      openRecentSparqlQueryFile(e.getSource());
    }
  }

  /**
   * Opens a SPARQL query file
   */
  private class FileSparqlOpenListener implements ActionListener {
    /**
     * No operation
     */
    public FileSparqlOpenListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      openSparqlQueryFile();
    }
  }

  /**
   * Exits the application
   */
  private class EndApplicationListener implements ActionListener {
    /**
     * No operation
     */
    public EndApplicationListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      closeApplication();
    }
  }

  /**
   * Evaluates control status
   */
  private class SparqlModelChoiceListener implements ActionListener {
    /**
     * No operation
     */
    public SparqlModelChoiceListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      processSparqlServiceChoice();
    }
  }

  /**
   * Inserts standard prefixes
   */
  private class InsertPrefixesListener implements ActionListener {
    /**
     * No operation
     */
    public InsertPrefixesListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      insertPrefixes();
    }
  }

  /**
   * Writes assertions to a file
   */
  private class FileAssertedTriplesSaveListener implements ActionListener {
    /**
     * No operation
     */
    public FileAssertedTriplesSaveListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      saveAssertionsToFile();
    }
  }

  /**
   * Writes SPARQL query to a file
   */
  private class FileSparqlSaveListener implements ActionListener {
    /**
     * No operation
     */
    public FileSparqlSaveListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      saveSparqlQueryToFile();
    }
  }

  /**
   * Expands the nodes in the tree representation of the model
   */
  private class ExpandTreeListener implements ActionListener {
    /**
     * No operation
     */
    public ExpandTreeListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      runExpandAllTreeNodes();
    }
  }

  /**
   * Collapses the nodes in the tree representation of the model
   */
  private class CollapseTreeListener implements ActionListener {
    /**
     * No operation
     */
    public CollapseTreeListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      runCollapseAllTreeNodes();
    }
  }

  /**
   * Allows the user to edit the list of stored SPARQL service URLs in the
   * dropdown
   */
  private class EditListOfSparqlServiceUrls implements ActionListener {
    /**
     * No operation
     */
    public EditListOfSparqlServiceUrls() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      editListOfSparqlServiceUrls();
    }
  }

  /**
   * Allows the user to change the display font
   */
  private class FontSetupListener implements ActionListener {
    /**
     * No operation
     */
    public FontSetupListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      configureFont();
    }
  }

  /**
   * Writes the current model to an ontology file
   */
  private class ModelSerializerListener implements ActionListener {
    /**
     * No operation
     */
    public ModelSerializerListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      setupToWriteOntologyModel();
    }
  }

  /**
   * Writes the current SPARQL results to a CSV file
   */
  private class FileSparqlResultsSaveListener implements ActionListener {
    /**
     * No operation
     */
    public FileSparqlResultsSaveListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      setupToWriteSparqlResults();
    }
  }

  /**
   * Allows the user to edit the list of classes filtered in the tree view
   */
  private class EditFilteredClassesListener implements ActionListener {
    /**
     * No operation
     */
    public EditFilteredClassesListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      editFilterMap(classesToSkipInTree);
    }
  }

  /**
   * Allows the user to edit the list of properties filtered in the tree view
   */
  private class EditFilteredPropertiesListener implements ActionListener {
    /**
     * No operation
     */
    public EditFilteredPropertiesListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      editFilterMap(predicatesToSkipInTree);
    }
  }

  /**
   * Enable or disable the use of a proxy for network-based requests
   */
  private class ProxyStatusChangeListener implements ActionListener {
    /**
     * No operation
     */
    public ProxyStatusChangeListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      changeProxyMode();
    }
  }

  /**
   * Configure the proxy
   */
  private class ProxySetupListener implements ActionListener {
    /**
     * No operation
     */
    public ProxySetupListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      configureProxy();
    }
  }

  /**
   * Start the SPARQL server
   */
  private class SparqlServerStartupListener implements ActionListener {
    /**
     * No operation
     */
    public SparqlServerStartupListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      startSparqlServer();
    }
  }

  /**
   * Stop the SPARQL server
   */
  private class SparqlServerShutdownListener implements ActionListener {
    /**
     * No operation
     */
    public SparqlServerShutdownListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      stopSparqlServer();
    }
  }

  /**
   * Publish the current ontology model to the SPARQL server
   */
  private class SparqlServerPublishModelListener implements ActionListener {
    /**
     * No operation
     */
    public SparqlServerPublishModelListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      publishModelToTheSparqlServer();
    }
  }

  /**
   * Publish the current ontology model to the SPARQL server
   */
  private class SparqlServerConfigurationListener implements ActionListener {
    /**
     * No operation
     */
    public SparqlServerConfigurationListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      configureSparqlServer();
    }
  }

  /**
   * Displays About dialog
   */
  private class AboutListener implements ActionListener {
    /**
     * No operation
     */
    public AboutListener() {

    }

    @Override
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
    /**
     * No operation
     */
    public GenerateTreeListener() {

    }

    @Override
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
    /**
     * No operation
     */
    public GenerateInferredTriplesListener() {

    }

    @Override
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
    /**
     * The content of the SPARQL input prior to the key release event
     */
    private String lastSparql;

    /**
     * The content of the assertions input prior to the key release event
     */
    private String lastAssertions;

    /**
     * No operation
     */
    public UserInputListener() {

    }

    /**
     * Track current content of assertions and query fields to detect a change
     * if there is a current model or results
     * 
     * @param arg0
     *          The key event received
     */
    public void keyPressed(KeyEvent arg0) {
      if (arg0.getSource() == assertionsInput && ontModel != null) {
        lastAssertions = assertionsInput.getText();
      }

      if (arg0.getSource() == sparqlInput
          && !((SparqlTableModel) sparqlResultsTable.getModel()).isEmpty()) {
        lastSparql = sparqlInput.getText();
      }
    }

    /**
     * Detect whether a change was made to the assertions or query that would
     * require invalidating the model or results
     * 
     * @param arg0
     *          The key event received
     */
    public void keyReleased(KeyEvent arg0) {
      if (arg0.getSource() == assertionsInput) {
        if (ontModel != null
            && !lastAssertions.equals(assertionsInput.getText())) {
          invalidateModel(true);
        }
      } else if (arg0.getSource() == sparqlInput) {
        if (!((SparqlTableModel) sparqlResultsTable.getModel()).isEmpty()
            && !lastSparql.equals(sparqlInput.getText())) {
          invalidateSparqlResults(true);
        }
      }
    }

    /**
     * No operation
     * 
     * @param arg0
     *          The key event received
     */
    public void keyTyped(KeyEvent arg0) {

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
