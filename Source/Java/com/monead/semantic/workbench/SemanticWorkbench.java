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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.jena.ontology.ConversionException;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.reasoner.ValidityReport.Report;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.web.HttpOp;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.log4j.Logger;
import org.mindswap.pellet.utils.VersionInfo;

import com.monead.semantic.workbench.images.ImageLibrary;
import com.monead.semantic.workbench.queries.ExportFormat;
import com.monead.semantic.workbench.queries.JsonExporter;
import com.monead.semantic.workbench.queries.QueryHistory;
import com.monead.semantic.workbench.queries.QueryInfo;
import com.monead.semantic.workbench.queries.SparqlQuery;
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
import com.monead.semantic.workbench.utilities.FileFilterDefinition;
import com.monead.semantic.workbench.utilities.FileSource;
import com.monead.semantic.workbench.utilities.FontChooser;
import com.monead.semantic.workbench.utilities.GuiUtilities;
import com.monead.semantic.workbench.utilities.MemoryWarningSystem;
import com.monead.semantic.workbench.utilities.NewVersionInformation;
import com.monead.semantic.workbench.utilities.ReasonerSelection;
import com.monead.semantic.workbench.utilities.SuffixFileFilter;
import com.monead.semantic.workbench.utilities.TextProcessing;
import com.monead.semantic.workbench.utilities.TextSearch;
import com.monead.semantic.workbench.utilities.ValueFormatter;

/**
 * SemanticWorkbench - A GUI to input assertions, work with inferencing engines
 * and SPARQL queries
 * 
 * This program uses Jena to provide the inference support.
 * 
 * Copyright (C) 2010-2016 David S. Read
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
 * 
 * TODO Add another reasoner such as HermiT
 * 
 * TODO Provide an option: When writing SPARQL results to a file, wrap URIs with
 * <>, including data types on literals
 * 
 * TODO Allow editing of an ontology/model from the tree view (graphical
 * ontology editor)
 * 
 * TODO Add unsaved file detection for Assertions and SPARQL queries
 * 
 * TODO Add a "File New" for assertions to clear hasIncompleteAssertionsInput
 * 
 * TODO Add a cancel feature for long running tasks
 * 
 * TODO Alerts for SPARQL service requests that are killed due to timeout
 * 
 * TODO Encode user id and password in SPARQL query file as encrypted value
 * using a key entered by the user at startup if they enable that feature
 * 
 * TODO Remove use of DefaultTreeModel with proprietary model that is backed by
 * the OntModel directly - hoping this speeds up creating the tree
 * 
 * TODO Expand tree node option, expands all nodes under the selected node
 * 
 * TODO Add option for creating a tree of individuals in the tree view
 * 
 * TODO Log SPARQL response that cannot be parsed (e.g. error or JSON format,
 * etc)
 * 
 * @author David Read
 * 
 */
public class SemanticWorkbench extends JFrame
    implements Runnable, WindowListener, Observer {
  /**
   * The version identifier
   */
  public static final String VERSION = "02.02.01";

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 20160501;

  /**
   * The set of formats that can be loaded. These are defined by Jena
   */
  private static final String[] FORMATS = { "N3", "N-Triples", "RDF/XML",
      "Turtle", "JSON-LD", "RDF/JSON" };

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
   * Minimum allowed font size
   */
  private static final int MINIMUM_FONT_SIZE = 5;

  /**
   * Prefix of the comment in the saved SPARQL query to indicate the default
   * graph URI used
   */
  private static final String SPARQL_QUERY_SAVE_SERVICE_DEFAULT_GRAPH_PARAM = "DEFAULT_GRAPH_URI:";

  /**
   * Minimum heap that must be available for the tree view to be created. If
   * this limit is reached while the tree is being build, it will be left in
   * an incomplete state.
   */
  private static final long MINIMUM_BYTES_REQUIRED_FOR_TREE_BUILD = 10 * 1024
      * 1024;

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
      { "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.",
          "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.",
          "@prefix owl: <http://www.w3.org/2002/07/owl#>.",
          "@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.",
          "@prefix dc: <http://purl.org/dc/elements/1.1/>.", },
      // N-triples
      {},
      // RDF/XML
      { "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"",
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
      // JSON-LD
      {},
      // RDF/JSON
      {},
      // SPARQL
      { "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
          "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
          "prefix owl: <http://www.w3.org/2002/07/owl#>",
          "prefix xsd: <http://www.w3.org/2001/XMLSchema#>",
          "prefix dc: <http://purl.org/dc/elements/1.1/>", } };

  /**
   * Service URLS to add in the drop down if none are defined
   */
  private static final String[] DEFAULT_SERVICE_URLS = {
      "http://semantic.monead.com/vehicleinfo/mileage",
      "http://dbpedia.org/sparql", "http://lod.openlinksw.com/sparql/", };

  /**
   * Default location for the divider location on the SPARQL tab
   */
  private static final int DEFAULT_SPARQL_QUERY_AND_RESULTS_DIVIDER_LOCATION = 150;

  /**
   * Constant used if a value cannot be found in an array
   */
  private static final int UNKNOWN = -1;

  /**
   * Maximum number of previous file names to retain
   */
  private static final int MAX_PREVIOUS_FILES_TO_STORE = 10;

  /**
   * The prefix for SPARQL results files exported with the direct export
   * option
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
   * Maximum number of bytes to load into the assertions text area. If a file
   * is loaded which exceeds this amount, only the first portion of the file
   * will be loaded. However, the model will be built using the whole file.
   * 
   * TODO provide a scrolling window over the entire file
   */
  private static final long MAX_ASSERTION_BYTES_TO_LOAD_INTO_TEXT_AREA = 10
      * 1024 * 1024;

  /**
   * File name for the properties file
   */
  private static final String PROPERTIES_FILE_NAME = "semantic_workbench.properties";

  /**
   * File name for the history of SPARQL queries
   */
  private static final String SPARQL_QUERY_HISTORY_FILE_NAME = "SWB.SparqlQueryHistory.ser";

  /**
   * URL to overview video
   */
  private static final String OVERVIEW_VIDEO_LOCATION = "http://monead.com/semantic/semantic_workbench/8MinutesWithSemanticWorkbench/";

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
   * Status of whether the ontology file has been saved since the last update
   */
  private boolean rdfFileSaved;

  /**
   * The RDF file filter description last used by the user
   */
  private String latestChosenRdfFileFilterDescription;

  /**
   * The name (and path if necessary) to the SPARQL file being loaded
   */
  private File sparqlQueryFile;

  /**
   * Status of whether the SPARQL query file has been saved since the last
   * update
   */
  private boolean sparqlQuerySaved;

  /**
   * The SPARQL file filter description last used by the user
   */
  private String latestChosenSparqlFileFilterDescription;

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
   * The assertions file menu - retained since the menu items include the list
   * of recently opened files which changes dynamically
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
   * End the program
   */
  private JMenuItem fileExit;

  /**
   * The SPARQL file menu - retained since the menu items include the list of
   * recently opened files which changes dynamically
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
   * Clear the history of SPARQL queries
   */
  private JMenuItem fileClearSparqlHistory;

  /**
   * Edit find text menu item
   * 
   * Used to search for text in the assertions text area
   */
  private JMenuItem editFind;

  /**
   * Edit find next text menu item
   * 
   * Used to continue a search for text in the assertions text area
   */
  private JMenuItem editFindNextMatch;

  /**
   * Edit insert prefixes menu item
   * 
   * Used to insert standard prefixes as a convenience
   */
  private JMenuItem editInsertPrefixes;

  /**
   * Toggle the selected SPARQL query lines between commented and not
   * commented
   */
  private JMenuItem editCommentToggle;

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
   * Apply formatting rules to literal values
   */
  private JCheckBoxMenuItem setupApplyFormattingToLiteralValues;

  /**
   * Display images in SPARQL results
   */
  private JCheckBoxMenuItem setupDisplayImagesInSparqlResults;

  /**
   * Export SPARQL results in chosen format
   */
  private JCheckBoxMenuItem[] setupExportSparqlResultsFormat;

  /**
   * Should SPARQL output be written directly to file rather than presented in
   * the results grid
   */
  private JCheckBoxMenuItem setupSparqlResultsToFile;

  /**
   * Enable/disable strict mode. (From the Jena documentation: Strict mode
   * means that converting a common resource to a particular language element,
   * such as an ontology class, will be subject to some simple syntactic-level
   * checks for appropriateness.)
   */
  private JCheckBoxMenuItem setupEnableStrictMode;

  /**
   * Enable/disable ontology validity checking. Enabling this may
   * significantly slow the reasoning process.
   */
  private JCheckBoxMenuItem setupEnableValidation;

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
   * Set whether the FQN for classes and objects are displayed in the tree
   */
  private JCheckBoxMenuItem showFqnInTree;

  /**
   * Set whether anonymous classes, individuals and properties are shown in
   * the tree
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
   * Reset the tree
   */
  private JMenuItem filterResetTree;

  /**
   * Set the maximum number of individuals to display for each class in the
   * tree view of the model
   */
  private JMenuItem filterSetMaximumIndividualsPerClassInTree;

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
   * View the overview video
   */
  private JMenuItem helpOverviewVideo;

  /**
   * Allows selection of the reasoning level
   */
  private JComboBox<ReasonerSelection> reasoningLevel;

  /**
   * Allows selection of the semantic syntax being used
   */
  private JComboBox<String> language;

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
  private JComboBox<String> sparqlServiceUrl;

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
   * Bring up the next query in the history list
   */
  private JButton nextQuery;

  /**
   * Bring up the previous query in the history list
   */
  private JButton previousQuery;

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
   * Holding this reference since its state is persisted in the properties
   * file
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
   * Are the currently displayed SPARQL query results in sync with the model
   * and the SPARQL query
   */
  private boolean areSparqlResultsInSyncWithModel;

  /**
   * SPARQL query history
   */
  private QueryHistory queryHistory = QueryHistory.getInstance();

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

    queryHistory.retrieveHistory(
        new File(getUserHomeDirectory(), SPARQL_QUERY_HISTORY_FILE_NAME));
    if (queryHistory.getNumberOfQueries() > 0) {
      processSparqlQueryHistoryMove(0);
    }

    enableControls(true);
    pack();
    GuiUtilities.windowSizing(this,
        properties.getProperty(ConfigurationProperty.LAST_HEIGHT.key()),
        properties.getProperty(ConfigurationProperty.LAST_WIDTH.key()),
        properties.getProperty(ConfigurationProperty.LAST_TOP_X_POSITION.key()),
        properties
            .getProperty(ConfigurationProperty.LAST_TOP_Y_POSITION.key()));
    setStatus("");
    rdfFileSaved = true;
    sparqlQuerySaved = true;
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
   * Load the configuration properties file.
   */
  private void loadProperties() {
    Reader reader = null;

    properties = new Properties();

    try {
      reader = new FileReader(
          getUserHomeDirectory() + "/" + PROPERTIES_FILE_NAME);
      properties.load(reader);

      for (Object key : properties.keySet()) {
        LOGGER.debug("Startup Property [" + key + "] = ["
            + properties.getProperty(key.toString()) + "]");
      }
    } catch (Throwable throwable) {
      LOGGER.warn("Unable to read the properties file: " + PROPERTIES_FILE_NAME,
          throwable);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (Throwable throwable) {
          LOGGER.warn(
              "Unable to close the properties file: " + PROPERTIES_FILE_NAME,
              throwable);
        }
      }
    }
  }

  /**
   * Setup the program's initial state based on the configuration properties.
   */
  private void processProperties() {
    String value;

    lastDirectoryUsed = new File(properties
        .getProperty(ConfigurationProperty.LAST_DIRECTORY.key(), "."));

    LOGGER.debug("Last directory used from properties file: "
        + lastDirectoryUsed.getAbsolutePath());

    value = properties.getProperty(ConfigurationProperty.INPUT_LANGUAGE.key(),
        "?");
    language.setSelectedItem(value);

    value = properties.getProperty(ConfigurationProperty.REASONING_LEVEL.key(),
        "-1");
    try {
      final Integer index = Integer.parseInt(value);
      if (index < 0 || index >= reasoningLevel.getItemCount()) {
        throw new IllegalArgumentException(
            "Incorrect reasoning level index property value: " + value);
      }
      reasoningLevel.setSelectedIndex(index);
    } catch (Throwable throwable) {
      LOGGER.warn(
          "Index for reasoner level must be a number from zero to "
              + (reasoningLevel.getItemCount() - 1));
    }
    reasoningLevel.setToolTipText(
        ((ReasonerSelection) reasoningLevel.getSelectedItem()).description());

    value = properties.getProperty(ConfigurationProperty.OUTPUT_FORMAT.key(),
        "?");
    for (JCheckBoxMenuItem outputLanguage : setupOutputAssertionLanguage) {
      if (outputLanguage.getText().equalsIgnoreCase(value)) {
        outputLanguage.setSelected(true);
      }
    }

    value = properties.getProperty(ConfigurationProperty.OUTPUT_CONTENT.key(),
        "?");
    if (setupOutputModelTypeAssertionsAndInferences.getText()
        .equalsIgnoreCase(value)) {
      setupOutputModelTypeAssertionsAndInferences.setSelected(true);
    } else {
      setupOutputModelTypeAssertions.setSelected(true);
    }

    setupAllowMultilineResultOutput.setSelected(
        properties.getProperty(
            ConfigurationProperty.SPARQL_DISPLAY_ALLOW_MULTILINE_OUTPUT.key(),
            "N")
            .toUpperCase().startsWith("Y"));
    setupOutputFqnNamespaces.setSelected(properties
        .getProperty(ConfigurationProperty.SHOW_FQN_NAMESPACES.key(), "N")
        .toUpperCase().startsWith("Y"));
    setupOutputDatatypesForLiterals
        .setSelected(properties
            .getProperty(ConfigurationProperty.SHOW_DATATYPES_ON_LITERALS.key(),
                "N")
            .toUpperCase().startsWith("Y"));
    setupOutputFlagLiteralValues.setSelected(properties
        .getProperty(ConfigurationProperty.FLAG_LITERALS_IN_RESULTS.key(), "N")
        .toUpperCase().startsWith("Y"));
    setupApplyFormattingToLiteralValues
        .setSelected(properties.getProperty(
            ConfigurationProperty.APPLY_FORMATTING_TO_LITERAL_VALUES.key(), "N")
            .toUpperCase().startsWith("Y"));
    setupDisplayImagesInSparqlResults
        .setSelected(properties.getProperty(
            ConfigurationProperty.SPARQL_DISPLAY_IMAGES_IN_RESULTS.key(), "N")
            .toUpperCase().startsWith("Y"));

    // SPARQL query export format - default to first option
    int formatIndex = 0;
    ExportFormat[] formats = ExportFormat.values();
    value = properties.getProperty(
        ConfigurationProperty.EXPORT_SPARQL_RESULTS_FORMAT.key(),
        formats[0].getFormatName());
    if (value != null) {
      for (int index = 0; index < formats.length; ++index) {
        if (value.equalsIgnoreCase(formats[index].getFormatName())) {
          formatIndex = index;
        }
      }
    }
    setupExportSparqlResultsFormat[formatIndex].setSelected(true);

    setupSparqlResultsToFile.setSelected(properties
        .getProperty(ConfigurationProperty.SPARQL_RESULTS_TO_FILE.key(), "N")
        .toUpperCase().startsWith("Y"));

    value = properties
        .getProperty(ConfigurationProperty.SPARQL_SERVICE_USER_ID.key());
    if (value != null) {
      sparqlServiceUserId.setText(value);
    }

    value = properties
        .getProperty(ConfigurationProperty.SPARQL_DEFAULT_GRAPH_URI.key());
    if (value != null) {
      defaultGraphUri.setText(value);
    }

    setupEnableStrictMode.setSelected(properties
        .getProperty(ConfigurationProperty.ENABLE_STRICT_MODE.key(), "Y")
        .toUpperCase().startsWith("Y"));

    setupEnableValidation.setSelected(properties
        .getProperty(ConfigurationProperty.ENABLE_VALIDATION.key(), "Y")
        .toUpperCase().startsWith("Y"));

    filterEnableFilters
        .setSelected(properties.getProperty(
            ConfigurationProperty.ENFORCE_FILTERS_IN_TREE_VIEW.key(), "Y")
            .toUpperCase().startsWith("Y"));

    showFqnInTree.setSelected(properties
        .getProperty(ConfigurationProperty.DISPLAY_FQN_IN_TREE_VIEW.key(), "Y")
        .toUpperCase().startsWith("Y"));

    filterShowAnonymousNodes.setSelected(
        properties.getProperty(
            ConfigurationProperty.DISPLAY_ANONYMOUS_NODES_IN_TREE_VIEW.key(),
            "N")
            .toUpperCase().startsWith("Y"));

    setFont(getFontFromProperties(), getColorFromProperties());

    extractSkipObjectsFromProperties();

    extractRecentAssertedTriplesFilesFromProperties();
    extractRecentSparqlQueryFilesFromProperties();

    // SPARQL Split Pane Position
    value = properties
        .getProperty(ConfigurationProperty.SPARQL_SPLIT_PANE_POSITION.key());
    if (value != null) {
      try {
        final int position = Integer.parseInt(value);
        if (position > 0) {
          sparqlQueryAndResults.setDividerLocation(position);
        }
      } catch (Throwable throwable) {
        LOGGER.warn(
            "Cannot use the SPARQL split pane divider location value: " + value,
            throwable);
      }
    }

    // Sparql server port
    value = properties
        .getProperty(ConfigurationProperty.SPARQL_SERVER_PORT.key());
    if (value != null) {
      try {
        final Integer port = Integer.parseInt(value);
        if (port > 0) {
          SparqlServer.getInstance().setListenerPort(port);
        } else {
          LOGGER.warn(
              "Configured port for SPARQL Server must be greater than zero. Was set to "
                  + port);
        }
      } catch (Throwable throwable) {
        LOGGER.warn(
            "Configured port for SPARQL Server must be a number greater than zero. Was set to "
                + value);
      }
    }

    // SPARQL server max runtime
    value = properties
        .getProperty(ConfigurationProperty.SPARQL_SERVER_MAX_RUNTIME.key());
    if (value != null) {
      try {
        final Integer maxRuntimeSeconds = Integer.parseInt(value);
        if (maxRuntimeSeconds > 0) {
          SparqlServer.getInstance().setMaxRuntimeSeconds(maxRuntimeSeconds);
        } else {
          LOGGER.warn(
              "Configured maximum runtime for the SPARQL Server must be greater than zero seconds. Was set to "
                  + maxRuntimeSeconds);
        }
      } catch (Throwable throwable) {
        LOGGER.warn(
            "Configured maximum runtime for the SPARQL Server must be a number greater than zero. Was set to "
                + value);
      }
    }

    // SPARQL server remote updates permitted
    SparqlServer.getInstance().setRemoteUpdatesPermitted(
        properties.getProperty(
            ConfigurationProperty.SPARQL_SERVER_ALLOW_REMOTE_UPDATE.key(), "N")
            .toUpperCase()
            .startsWith("Y"));

    // Proxy
    proxyServer = properties
        .getProperty(ConfigurationProperty.PROXY_SERVER.key());
    value = properties.getProperty(ConfigurationProperty.PROXY_PORT.key());
    if (value != null) {
      try {
        proxyPort = Integer.parseInt(value);
      } catch (Throwable throwable) {
        LOGGER
            .warn("Illegal proxy port number in the properties file: " + value);
      }
    }
    proxyProtocolHttp = properties
        .getProperty(ConfigurationProperty.PROXY_HTTP.key(), "N").toUpperCase()
        .startsWith("Y");
    proxyProtocolSocks = properties
        .getProperty(ConfigurationProperty.PROXY_SOCKS.key(), "N").toUpperCase()
        .startsWith("Y");
    proxyEnabled = properties
        .getProperty(ConfigurationProperty.PROXY_ENABLED.key(), "N")
        .toUpperCase()
        .startsWith("Y");
    setupProxy();

    populateSparqlServiceUrls();

    extractXsdFormatsFromProperties();
  }

  /**
   * Populate the drop down of SPARQL service URLs from the properties file.
   * If none are found, populate with a default list.
   */
  private void populateSparqlServiceUrls() {
    final List<String> prefixNames = new ArrayList<String>();
    boolean foundUrl;
    int lastSelectedIndex;

    /**
     * Find any keys for previous files
     */
    for (Object key : properties.keySet()) {
      if (key.toString()
          .startsWith(ConfigurationProperty.PREFIX_SPARQL_SERVICE_URL.key())) {
        prefixNames.add(key.toString());
      }
    }

    // Want the files in order so the drop down is consistent from run to
    // run
    // Also, the last selected index is used to select the last selected
    // service
    Collections.sort(prefixNames);

    // This must be the first option - use the local model
    sparqlServiceUrl.addItem("Local Model, FROM or SERVICE Clause");

    // Detect if at least one service URL is found in the properties file
    foundUrl = false;

    for (String key : prefixNames) {
      sparqlServiceUrl.addItem((String) properties.get(key));
      foundUrl = true;
    }

    // If there are no service URLs defined, setup some defaults
    if (!foundUrl) {
      for (String url : DEFAULT_SERVICE_URLS) {
        sparqlServiceUrl.addItem(url);
      }
    }

    // If the last selected index value is legal, set the service selection
    // to
    // that option
    try {
      lastSelectedIndex = Integer
          .parseInt(properties.getProperty(
              ConfigurationProperty.SELECTED_SPARQL_SERVICE_URL.key(), "0"));
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
   * Obtain the list of recently opened or saved assertions files to update
   * the assertions file menu.
   */
  private void extractRecentAssertedTriplesFilesFromProperties() {
    final List<String> prefixNames = new ArrayList<String>();
    String value;
    String[] parsed;

    /**
     * Find any keys for previous files
     */
    for (Object key : properties.keySet()) {
      if (key.toString().startsWith(
          ConfigurationProperty.PREFIX_RECENT_ASSERTIONS_FILE.key())) {
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
        LOGGER.warn("Unable to parse the File or URL from the properties file: "
            + value, throwable);
      }

    }

    setupAssertionsFileMenu();
  }

  /**
   * Obtain the list of recently opened or saved SPARQL files to update the
   * SPARQL file menu.
   */
  private void extractRecentSparqlQueryFilesFromProperties() {
    final List<String> prefixNames = new ArrayList<String>();

    /**
     * Find any keys for previous files
     */
    for (Object key : properties.keySet()) {
      if (key.toString().startsWith(
          ConfigurationProperty.PREFIX_RECENT_SPARQL_QUERY_FILE.key())) {
        prefixNames.add(key.toString());
      }
    }

    // Want the files in order from most recent
    Collections.sort(prefixNames);

    // Only keep up to MAX_PREVIOUS_FILES_TO_STORE values
    for (int index = 0; index < MAX_PREVIOUS_FILES_TO_STORE
        && index < prefixNames.size(); index++) {
      recentSparqlFiles
          .add(new File(properties.getProperty(prefixNames.get(index))));
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
      if (key.toString()
          .startsWith(ConfigurationProperty.PREFIX_SKIP_CLASS.key())) {
        classesToSkipInTree.put(properties.getProperty(key.toString()), "");
      } else if (key.toString()
          .startsWith(ConfigurationProperty.PREFIX_SKIP_PREDICATE.key())) {
        predicatesToSkipInTree.put(properties.getProperty(key.toString()), "");
      }
    }
  }

  /**
   * Get the XSD and format mapping from the properties files. These formats
   * are used to display data that matches the supplied XSD type.
   */
  private void extractXsdFormatsFromProperties() {
    boolean foundXsdFormat = false;

    for (Object key : properties.keySet()) {
      if (key.toString().startsWith(
          ConfigurationProperty.PREFIX_NUMERIC_DATA_XSD_FORMAT_MAPPING.key())) {
        ValueFormatter.setFormat(
            key.toString()
                .substring(
                    ConfigurationProperty.PREFIX_NUMERIC_DATA_XSD_FORMAT_MAPPING
                        .key().length()),
            properties.getProperty(key.toString()));
        foundXsdFormat = true;
      }
    }

    if (!foundXsdFormat) {
      ValueFormatter.setFormat("decimal", "#,##0");
      ValueFormatter.setFormat("double", "#,##0.0##");
      properties.put(
          ConfigurationProperty.PREFIX_NUMERIC_DATA_XSD_FORMAT_MAPPING.key()
              + "decimal",
          "#,##0");
      properties.put(
          ConfigurationProperty.PREFIX_NUMERIC_DATA_XSD_FORMAT_MAPPING.key()
              + "double",
          "#,##0.0##");
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
          // If the currentSelection was this one, make the current
          // Selection 0
          if (currentSelectedIndex == index) {
            currentSelectedIndex = 0;
          } else if (currentSelectedIndex > index) {
            // If the current selection is after this deleted one
            // then it has moved up one position
            currentSelectedIndex--;
          }

          // Since an item was removed, back up one position so the
          // next
          // iteration doesn't skip the new value in this position
          --index;
        }
      }

      // Select the proper item (either the previous one selected or the
      // default
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
    final List<String> filteredItems = new ArrayList<String>(
        filterMap.keySet());
    int[] selectedIndices;

    Collections.sort(filteredItems);
    final JList<String> jListOfItems = new JList<String>(
        filteredItems.toArray(new String[filteredItems.size()]));
    JOptionPane.showMessageDialog(this, jListOfItems, "Select Items to Remove",
        JOptionPane.QUESTION_MESSAGE);
    selectedIndices = jListOfItems.getSelectedIndices();
    if (selectedIndices.length > 0) {
      LOGGER.debug("Items to remove from the filter map: "
          + Arrays.toString(jListOfItems.getSelectedValuesList().toArray()));
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
    removePrefixedProperties(
        ConfigurationProperty.PREFIX_RECENT_ASSERTIONS_FILE.key());

    // Remove the recent SPARQL query files entries.
    // They will be recreated from the new list
    removePrefixedProperties(
        ConfigurationProperty.PREFIX_RECENT_SPARQL_QUERY_FILE.key());

    // Remove the set of SPARQL service URL entries.
    // They will be recreated from the dropdown
    removePrefixedProperties(
        ConfigurationProperty.PREFIX_SPARQL_SERVICE_URL.key());

    updatePropertiesWithClassesToSkipInTree();
    updatePropertiesWithPredicatesToSkipInTree();
    updatePropertiesWithServiceUrls();

    // Add the set of recent asserted triples files
    for (int index = 0; index < MAX_PREVIOUS_FILES_TO_STORE
        && index < recentAssertionsFiles.size(); ++index) {
      properties.put(
          ConfigurationProperty.PREFIX_RECENT_ASSERTIONS_FILE.key() + index,
          (recentAssertionsFiles.get(index).isFile() ? "FILE:" : "URL:")
              + recentAssertionsFiles.get(index).getAbsolutePath());
    }

    // Add the set of recent SPARQL query files
    for (int index = 0; index < MAX_PREVIOUS_FILES_TO_STORE
        && index < recentSparqlFiles.size(); ++index) {
      properties.put(
          ConfigurationProperty.PREFIX_RECENT_SPARQL_QUERY_FILE.key() + index,
          recentSparqlFiles.get(index).getAbsolutePath());
    }

    properties.setProperty(ConfigurationProperty.LAST_HEIGHT.key(),
        this.getSize().height + "");
    properties.setProperty(ConfigurationProperty.LAST_WIDTH.key(),
        this.getSize().width + "");
    properties.setProperty(ConfigurationProperty.LAST_TOP_X_POSITION.key(),
        this.getLocation().x + "");
    properties.setProperty(ConfigurationProperty.LAST_TOP_Y_POSITION.key(),
        this.getLocation().y + "");

    // Only store the divider position if it is not at an extreme setting
    // (e.g.
    // both the query and results panels are visible)
    if (sparqlQueryAndResults.getDividerLocation() > 1
        && sparqlQueryAndResults.getHeight()
            - (sparqlQueryAndResults.getDividerLocation()
                + sparqlQueryAndResults.getDividerSize()) > 1) {
      properties.setProperty(
          ConfigurationProperty.SPARQL_SPLIT_PANE_POSITION.key(),
          sparqlQueryAndResults.getDividerLocation() + "");
    } else {
      LOGGER.debug("SPARQL split pane position not being stored - Size:"
          + sparqlQueryAndResults.getHeight()
          + " DividerLoc:" + sparqlQueryAndResults.getDividerLocation()
          + " DividerSize:"
          + sparqlQueryAndResults.getDividerSize());
      properties.remove(ConfigurationProperty.SPARQL_SPLIT_PANE_POSITION.key());
    }
    properties.setProperty(ConfigurationProperty.LAST_DIRECTORY.key(),
        lastDirectoryUsed.getAbsolutePath());

    properties.setProperty(ConfigurationProperty.INPUT_LANGUAGE.key(),
        language.getSelectedItem().toString());

    properties.setProperty(ConfigurationProperty.REASONING_LEVEL.key(),
        reasoningLevel.getSelectedIndex() + "");

    for (JCheckBoxMenuItem outputLanguage : setupOutputAssertionLanguage) {
      if (outputLanguage.isSelected()) {
        properties.setProperty(ConfigurationProperty.OUTPUT_FORMAT.key(),
            outputLanguage.getText());
      }
    }

    if (setupOutputModelTypeAssertionsAndInferences.isSelected()) {
      properties.setProperty(ConfigurationProperty.OUTPUT_CONTENT.key(),
          setupOutputModelTypeAssertionsAndInferences.getText());
    } else {
      properties.setProperty(ConfigurationProperty.OUTPUT_CONTENT.key(),
          setupOutputModelTypeAssertions.getText());
    }

    properties.setProperty(
        ConfigurationProperty.SPARQL_DISPLAY_ALLOW_MULTILINE_OUTPUT.key(),
        setupAllowMultilineResultOutput.isSelected()
            ? DEFAULT_PROPERTY_VALUE_YES : DEFAULT_PROPERTY_VALUE_NO);
    properties.setProperty(ConfigurationProperty.SHOW_FQN_NAMESPACES.key(),
        setupOutputFqnNamespaces.isSelected() ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);
    properties.setProperty(
        ConfigurationProperty.SHOW_DATATYPES_ON_LITERALS.key(),
        setupOutputDatatypesForLiterals.isSelected()
            ? DEFAULT_PROPERTY_VALUE_YES : DEFAULT_PROPERTY_VALUE_NO);
    properties.setProperty(ConfigurationProperty.FLAG_LITERALS_IN_RESULTS.key(),
        setupOutputFlagLiteralValues.isSelected() ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);
    properties.setProperty(
        ConfigurationProperty.APPLY_FORMATTING_TO_LITERAL_VALUES.key(),
        setupApplyFormattingToLiteralValues.isSelected()
            ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);
    properties.setProperty(
        ConfigurationProperty.SPARQL_DISPLAY_IMAGES_IN_RESULTS.key(),
        setupDisplayImagesInSparqlResults.isSelected()
            ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);

    properties.setProperty(
        ConfigurationProperty.EXPORT_SPARQL_RESULTS_FORMAT.key(),
        chosenFormat().getFormatName());

    properties.setProperty(ConfigurationProperty.SPARQL_RESULTS_TO_FILE.key(),
        setupSparqlResultsToFile.isSelected() ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);

    if (sparqlServiceUserId.getText().trim().length() > 0) {
      properties.setProperty(ConfigurationProperty.SPARQL_SERVICE_USER_ID.key(),
          sparqlServiceUserId.getText().trim());
    } else {
      properties.remove(ConfigurationProperty.SPARQL_SERVICE_USER_ID.key());
    }

    if (defaultGraphUri.getText().trim().length() > 0) {
      properties.setProperty(
          ConfigurationProperty.SPARQL_DEFAULT_GRAPH_URI.key(),
          defaultGraphUri.getText().trim());
    } else {
      properties.remove(ConfigurationProperty.SPARQL_DEFAULT_GRAPH_URI.key());
    }

    properties.setProperty(ConfigurationProperty.ENABLE_STRICT_MODE.key(),
        setupEnableStrictMode.isSelected() ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);

    properties.setProperty(ConfigurationProperty.ENABLE_VALIDATION.key(),
        setupEnableValidation.isSelected() ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);

    properties.setProperty(
        ConfigurationProperty.ENFORCE_FILTERS_IN_TREE_VIEW.key(),
        filterEnableFilters.isSelected() ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);

    properties.setProperty(ConfigurationProperty.DISPLAY_FQN_IN_TREE_VIEW.key(),
        showFqnInTree.isSelected() ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);

    properties.setProperty(
        ConfigurationProperty.DISPLAY_ANONYMOUS_NODES_IN_TREE_VIEW.key(),
        filterShowAnonymousNodes.isSelected() ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);

    properties.setProperty(ConfigurationProperty.SPARQL_SERVER_PORT.key(),
        SparqlServer.getInstance().getListenerPort() + "");
    properties.setProperty(
        ConfigurationProperty.SPARQL_SERVER_MAX_RUNTIME.key(),
        SparqlServer.getInstance().getMaxRuntimeSeconds() + "");
    properties.setProperty(
        ConfigurationProperty.SPARQL_SERVER_ALLOW_REMOTE_UPDATE.key(),
        SparqlServer.getInstance().areRemoteUpdatesPermitted()
            ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);

    properties.setProperty(ConfigurationProperty.PROXY_ENABLED.key(),
        proxyEnabled ? DEFAULT_PROPERTY_VALUE_YES : DEFAULT_PROPERTY_VALUE_NO);
    if (proxyServer != null) {
      properties.setProperty(ConfigurationProperty.PROXY_SERVER.key(),
          proxyServer);
    } else {
      properties.remove(ConfigurationProperty.PROXY_SERVER.key());
    }

    if (proxyPort != null) {
      properties.setProperty(ConfigurationProperty.PROXY_PORT.key(),
          proxyPort + "");
    } else {
      properties.remove(ConfigurationProperty.PROXY_PORT.key());
    }

    properties.setProperty(ConfigurationProperty.PROXY_HTTP.key(),
        proxyProtocolHttp ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);
    properties.setProperty(ConfigurationProperty.PROXY_SOCKS.key(),
        proxyProtocolSocks ? DEFAULT_PROPERTY_VALUE_YES
            : DEFAULT_PROPERTY_VALUE_NO);

    try {
      writer = new FileWriter(
          getUserHomeDirectory() + "/" + PROPERTIES_FILE_NAME, false);
      properties.store(writer, "Generated by Semantic Workbench version "
          + VERSION + " on " + new Date());
    } catch (Throwable throwable) {
      LOGGER.warn(
          "Unable to write the properties file: " + PROPERTIES_FILE_NAME,
          throwable);
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (Throwable throwable) {
          LOGGER.warn(
              "Unable to close the properties file: " + PROPERTIES_FILE_NAME,
              throwable);
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
      properties.setProperty(
          ConfigurationProperty.PREFIX_SPARQL_SERVICE_URL.key() + index,
          sparqlServiceUrl.getItemAt(index).toString());
    }

    properties.setProperty(
        ConfigurationProperty.SELECTED_SPARQL_SERVICE_URL.key(),
        sparqlServiceUrl.getSelectedIndex() + "");
  }

  /**
   * Remove all the properties in the properties collection that begin with
   * the supplied property key prefix.
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

    // Remove the existing class entries. They will be recreated from the
    // new
    // list
    removePrefixedProperties(ConfigurationProperty.PREFIX_SKIP_CLASS.key());

    for (String classToSkipInTree : classesToSkipInTree.keySet()) {
      ++classNumber;
      properties.put(
          ConfigurationProperty.PREFIX_SKIP_CLASS.key() + classNumber,
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

    // Remove the existing predicate entries. They will be recreated from
    // the
    // new list
    for (String predicateToSkipInTree : predicatesToSkipInTree.keySet()) {
      ++propNumber;
      properties.put(
          ConfigurationProperty.PREFIX_SKIP_PREDICATE.key() + propNumber,
          predicateToSkipInTree);
      predicatesToSkipInTree.put(predicateToSkipInTree, "");
    }
  }

  /**
   * Get the path to the user's home directory
   * 
   * @return The user's home directory
   */
  private String getUserHomeDirectory() {
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

    // Detect tab selections
    tabbedPane.addChangeListener(new TabbedPaneChangeListener());

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
      properties.setProperty(ConfigurationProperty.FONT_NAME.key(),
          newFont.getName());
      properties.setProperty(ConfigurationProperty.FONT_SIZE.key(),
          newFont.getSize() + "");
      properties.setProperty(ConfigurationProperty.FONT_STYLE.key(),
          newFont.getStyle() + "");

      properties.setProperty(ConfigurationProperty.FONT_COLOR.key(),
          newColor.getRGB() + "");

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
    final String fontName = properties
        .getProperty(ConfigurationProperty.FONT_NAME.key(), "Courier");
    final String fontSize = properties
        .getProperty(ConfigurationProperty.FONT_SIZE.key(), "12");
    final String fontStyle = properties
        .getProperty(ConfigurationProperty.FONT_STYLE.key(), "0");

    try {
      newFont = new Font(fontName, Integer.parseInt(fontStyle),
          Integer.parseInt(fontSize));
      if (newFont.getSize() < MINIMUM_FONT_SIZE) {
        throw new IllegalArgumentException(
            "Font size too small: " + newFont.getSize());
      }
    } catch (Throwable throwable) {
      LOGGER.warn("Cannot setup font from properties (" + fontName + ","
          + fontSize + "," + fontStyle + ")",
          throwable);
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
    final String colorRgb = properties.getProperty(
        ConfigurationProperty.FONT_COLOR.key(),
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
    JPanel controlGrid;

    sparqlPanel = new JPanel();
    sparqlPanel.setLayout(new BorderLayout());

    // Controls
    labelPanel = new JPanel();
    labelPanel.setLayout(new BorderLayout());
    gridPanel = new JPanel();
    gridPanel.setLayout(new GridLayout(0, 1));

    innerGridPanel = new JPanel();
    innerGridPanel.setLayout(new GridLayout(1, 3));
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
    innerGridPanel.add(flowPanel);
    controlGrid = new JPanel();
    controlGrid.setLayout(new GridLayout(1, 2));
    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    flowPanel.add(new JLabel("User Id:"));
    flowPanel.add(sparqlServiceUserId);
    controlGrid.add(flowPanel);
    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    flowPanel.add(new JLabel("Password:"));
    flowPanel.add(sparqlServicePassword);
    controlGrid.add(flowPanel);
    innerGridPanel.add(controlGrid);
    gridPanel.add(innerGridPanel);

    innerGridPanel = new JPanel();
    innerGridPanel.setLayout(new GridLayout(1, 2));
    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    flowPanel.add(new JLabel("Default Graph URI: "));
    flowPanel.add(defaultGraphUri);
    innerGridPanel.add(flowPanel);
    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    flowPanel.add(previousQuery);
    flowPanel.add(nextQuery);
    innerGridPanel.add(flowPanel);
    gridPanel.add(innerGridPanel);

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
        queryPanel, resultsPanel);

    sparqlQueryAndResults
        .setDividerLocation(DEFAULT_SPARQL_QUERY_AND_RESULTS_DIVIDER_LOCATION);
    sparqlQueryAndResults.setOneTouchExpandable(true);

    sparqlPanel.add(sparqlQueryAndResults, BorderLayout.CENTER);

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
   * Configures the assertions file menu. Called at startup and whenever an
   * assertions file is opened or saved since the list of recent assertions
   * files is presented on the file menu.
   */
  private void setupAssertionsFileMenu() {
    fileAssertionsMenu.removeAll();

    fileOpenTriplesFile = new JMenuItem("Open Assertions File");
    fileOpenTriplesFile.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.ALT_MASK));
    fileOpenTriplesFile.setMnemonic('A');
    fileOpenTriplesFile.setToolTipText("Open an asserted triples file");
    fileOpenTriplesFile
        .addActionListener(new FileAssertedTriplesOpenListener());
    fileAssertionsMenu.add(fileOpenTriplesFile);

    fileOpenTriplesUrl = new JMenuItem("Open Assertions Url");
    fileOpenTriplesUrl.setMnemonic('U');
    fileOpenTriplesUrl.setToolTipText("Access asserted triples from a URL");
    fileOpenTriplesUrl
        .addActionListener(new FileAssertedTriplesUrlOpenListener());
    fileAssertionsMenu.add(fileOpenTriplesUrl);

    fileAssertionsMenu.addSeparator();

    // Create menu options to open recently accessed ontology files
    fileOpenRecentTriplesFile = new JMenuItem[recentAssertionsFiles.size()];
    for (int recentFileNumber = 0; recentFileNumber < recentAssertionsFiles
        .size(); ++recentFileNumber) {
      fileOpenRecentTriplesFile[recentFileNumber] = new JMenuItem(
          recentAssertionsFiles.get(recentFileNumber).getName());
      fileOpenRecentTriplesFile[recentFileNumber]
          .setToolTipText(
              recentAssertionsFiles.get(recentFileNumber).getAbsolutePath());
      fileOpenRecentTriplesFile[recentFileNumber]
          .addActionListener(new RecentAssertedTriplesFileOpenListener());
      fileAssertionsMenu.add(fileOpenRecentTriplesFile[recentFileNumber]);
    }

    if (fileOpenRecentTriplesFile.length > 0) {
      fileAssertionsMenu.addSeparator();
    }

    fileSaveTriplesToFile = new JMenuItem("Save Assertions Text");
    fileSaveTriplesToFile
        .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
            KeyEvent.ALT_MASK | KeyEvent.CTRL_MASK));
    fileSaveTriplesToFile.setMnemonic(KeyEvent.VK_S);
    fileSaveTriplesToFile
        .setToolTipText("Write the asserted triples to a file");
    fileSaveTriplesToFile
        .addActionListener(new FileAssertedTriplesSaveListener());
    fileAssertionsMenu.add(fileSaveTriplesToFile);

    fileSaveSerializedModel = new JMenuItem("Save Model (processed triples)");
    fileSaveSerializedModel
        .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
            KeyEvent.ALT_MASK | KeyEvent.CTRL_MASK));
    fileSaveSerializedModel.setMnemonic(KeyEvent.VK_M);
    fileSaveSerializedModel
        .setToolTipText("Write the triples from the current model to a file");
    fileSaveSerializedModel.addActionListener(new ModelSerializerListener());
    fileAssertionsMenu.add(fileSaveSerializedModel);

    fileAssertionsMenu.addSeparator();

    fileExit = new JMenuItem("Exit");
    fileExit.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));
    fileExit.setMnemonic(KeyEvent.VK_X);
    fileExit.setToolTipText("Exit the application");
    fileExit.addActionListener(new EndApplicationListener());
    fileAssertionsMenu.add(fileExit);

  }

  /**
   * Configures the SPARQL file menu. Called at startup and whenever an SPARQL
   * file is opened or saved since the list of recent SPARQL files is
   * presented on the file menu.
   */
  private void setupSparqlFileMenu() {
    fileSparqlMenu.removeAll();

    fileOpenSparqlFile = new JMenuItem("Open SPARQL File");
    fileOpenSparqlFile.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.ALT_MASK));
    fileOpenSparqlFile.setMnemonic(KeyEvent.VK_S);
    fileOpenSparqlFile.setToolTipText("Open a SPARQL query file");
    fileOpenSparqlFile.addActionListener(new FileSparqlOpenListener());
    fileSparqlMenu.add(fileOpenSparqlFile);

    fileSparqlMenu.addSeparator();

    // Create menu options to open recently accessed SPARQL files
    fileOpenRecentSparqlFile = new JMenuItem[recentSparqlFiles.size()];
    for (int recentFileNumber = 0; recentFileNumber < recentSparqlFiles
        .size(); ++recentFileNumber) {
      fileOpenRecentSparqlFile[recentFileNumber] = new JMenuItem(
          recentSparqlFiles.get(recentFileNumber).getName());
      fileOpenRecentSparqlFile[recentFileNumber]
          .setToolTipText(
              recentSparqlFiles.get(recentFileNumber).getAbsolutePath());
      fileOpenRecentSparqlFile[recentFileNumber]
          .addActionListener(new RecentSparqlFileOpenListener());
      fileSparqlMenu.add(fileOpenRecentSparqlFile[recentFileNumber]);
    }

    if (fileOpenRecentSparqlFile.length > 0) {
      fileSparqlMenu.addSeparator();
    }

    fileSaveSparqlQueryToFile = new JMenuItem("Save SPARQL Query");
    fileSaveSparqlQueryToFile
        .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
            KeyEvent.ALT_MASK | KeyEvent.CTRL_MASK));
    fileSaveSparqlQueryToFile.setMnemonic(KeyEvent.VK_Q);
    fileSaveSparqlQueryToFile
        .setToolTipText("Write the SPARQL query to a file");
    fileSaveSparqlQueryToFile.addActionListener(new FileSparqlSaveListener());
    fileSparqlMenu.add(fileSaveSparqlQueryToFile);

    fileSaveSparqlResultsToFile = new JMenuItem("Save SPARQL Results");
    fileSaveSparqlResultsToFile
        .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
            KeyEvent.ALT_MASK | KeyEvent.CTRL_MASK));
    fileSaveSparqlResultsToFile.setMnemonic(KeyEvent.VK_R);
    fileSaveSparqlResultsToFile
        .setToolTipText("Write the current SPARQL results to a file");
    fileSaveSparqlResultsToFile
        .addActionListener(new FileSparqlResultsSaveListener());
    fileSparqlMenu.add(fileSaveSparqlResultsToFile);

    fileSparqlMenu.addSeparator();

    fileClearSparqlHistory = new JMenuItem("Clear SPARQL Query History");
    fileClearSparqlHistory.setMnemonic(KeyEvent.VK_C);
    fileClearSparqlHistory
        .setToolTipText("Clear the history of executed SPARQL queries");
    fileClearSparqlHistory
        .addActionListener(new FileClearSparqlHistoryListener());
    fileSparqlMenu.add(fileClearSparqlHistory);

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
    fileAssertionsMenu
        .setToolTipText("Menu items related to asserted triples file access");
    menuBar.add(fileAssertionsMenu);

    setupAssertionsFileMenu();

    // SPARQL file menu
    fileSparqlMenu = new JMenu("File (SPARQL)");
    fileSparqlMenu.setMnemonic(KeyEvent.VK_S);
    fileSparqlMenu.setToolTipText("Menu items related to SPARQL file access");
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
    menu.setToolTipText("Menu items related to editing the ontology");

    editFind = new JMenuItem("Find (in assertions)");
    editFind.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK));
    editFind.setMnemonic(KeyEvent.VK_F);
    editFind.setToolTipText("Find text in the assertions editor");
    editFind.addActionListener(new FindAssertionsTextListener());
    menu.add(editFind);

    editFindNextMatch = new JMenuItem("Next (matching assertion text)");
    editFindNextMatch.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
    editFindNextMatch.setMnemonic(KeyEvent.VK_N);
    editFindNextMatch
        .setToolTipText("Find next text match in the assertions editor");
    editFindNextMatch.addActionListener(new FindNextAssertionsTextListener());
    menu.add(editFindNextMatch);

    menu.addSeparator();

    editCommentToggle = new JMenuItem("Toggle Comment");
    editCommentToggle.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_MASK));
    editCommentToggle.setMnemonic(KeyEvent.VK_T);
    editCommentToggle
        .setToolTipText(
            "Switch the chosen assertion or query lines between commented and not commented");
    editCommentToggle.addActionListener(new CommentToggleListener());
    editCommentToggle.setEnabled(false);
    menu.add(editCommentToggle);

    editInsertPrefixes = new JMenuItem("Insert Prefixes");
    editInsertPrefixes.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
    editInsertPrefixes.setMnemonic(KeyEvent.VK_I);
    editInsertPrefixes.setToolTipText("Insert standard prefixes (namespaces)");
    editInsertPrefixes.addActionListener(new InsertPrefixesListener());
    menu.add(editInsertPrefixes);

    menu.addSeparator();

    editExpandAllTreeNodes = new JMenuItem("Expand Entire Tree");
    editExpandAllTreeNodes.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_ADD, ActionEvent.ALT_MASK));
    editExpandAllTreeNodes.setMnemonic(KeyEvent.VK_E);
    editExpandAllTreeNodes.setToolTipText("Expand all tree nodes");
    editExpandAllTreeNodes.addActionListener(new ExpandTreeListener());
    menu.add(editExpandAllTreeNodes);

    editCollapseAllTreeNodes = new JMenuItem("Collapse Entire Tree");
    editCollapseAllTreeNodes.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, ActionEvent.ALT_MASK));
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
    menu.setToolTipText("Menu items related to configuration");

    buttonGroup = new ButtonGroup();
    setupOutputAssertionLanguage = new JCheckBoxMenuItem[FORMATS.length + 1];
    setupOutputAssertionLanguage[0] = new JCheckBoxMenuItem(
        "Save Model as Input Format");
    setupOutputAssertionLanguage[0].setToolTipText(
        "Save the model using the serialization used to parse it");
    buttonGroup.add(setupOutputAssertionLanguage[0]);
    menu.add(setupOutputAssertionLanguage[0]);

    for (int index = 0; index < FORMATS.length; ++index) {
      setupOutputAssertionLanguage[index + 1] = new JCheckBoxMenuItem(
          "Save Model as " + FORMATS[index]);
      setupOutputAssertionLanguage[index + 1]
          .setToolTipText(
              "Save the model using the " + FORMATS[index] + " format");
      buttonGroup.add(setupOutputAssertionLanguage[index + 1]);
      menu.add(setupOutputAssertionLanguage[index + 1]);
    }
    setupOutputAssertionLanguage[0].setSelected(true);

    menu.addSeparator();

    buttonGroup = new ButtonGroup();
    setupOutputModelTypeAssertions = new JCheckBoxMenuItem(
        "Save Model with Assertions Only");
    buttonGroup.add(setupOutputModelTypeAssertions);
    menu.add(setupOutputModelTypeAssertions);

    setupOutputModelTypeAssertionsAndInferences = new JCheckBoxMenuItem(
        "Save Model with Assertions and Inferences");
    buttonGroup.add(setupOutputModelTypeAssertionsAndInferences);
    menu.add(setupOutputModelTypeAssertionsAndInferences);

    setupOutputModelTypeAssertions.setSelected(true);

    menu.addSeparator();

    setupAllowMultilineResultOutput = new JCheckBoxMenuItem(
        "Allow Multiple Lines of Text Per Row in SPARQL Query Output");
    setupAllowMultilineResultOutput.setToolTipText(
        "Wrap long values into multiple lines in a display cell");
    setupAllowMultilineResultOutput.setSelected(false);
    menu.add(setupAllowMultilineResultOutput);

    setupOutputFqnNamespaces = new JCheckBoxMenuItem(
        "Show FQN Namespaces Instead of Prefixes in Query Output");
    setupOutputFqnNamespaces
        .setToolTipText(
            "Use the fully qualified namespace. If unchecked use the prefix, if defined");
    setupOutputFqnNamespaces.setSelected(false);
    menu.add(setupOutputFqnNamespaces);

    setupOutputDatatypesForLiterals = new JCheckBoxMenuItem(
        "Show Datatypes on Literals");
    setupOutputDatatypesForLiterals.setToolTipText(
        "Display the datatype after the value, e.g. 4^^xsd:integer");
    setupOutputDatatypesForLiterals.setSelected(false);
    menu.add(setupOutputDatatypesForLiterals);

    setupOutputFlagLiteralValues = new JCheckBoxMenuItem(
        "Flag Literal Values in Query Output");
    setupOutputFlagLiteralValues.setToolTipText(
        "Includes the text 'Lit:' in front of any literal values");
    setupOutputFlagLiteralValues.setSelected(false);
    menu.add(setupOutputFlagLiteralValues);

    setupApplyFormattingToLiteralValues = new JCheckBoxMenuItem(
        "Apply Formatting to Literal Values");
    setupApplyFormattingToLiteralValues.setToolTipText(
        "Apply the XSD-based formatting defined in the configuration to literal values in SPARQL results and tree view display");
    setupApplyFormattingToLiteralValues.setSelected(true);
    menu.add(setupApplyFormattingToLiteralValues);

    setupDisplayImagesInSparqlResults = new JCheckBoxMenuItem(
        "Display Images in Query Output (Slows Results Retrieval)");
    setupDisplayImagesInSparqlResults
        .setToolTipText("Attempts to download images linked in the results. "
            + "Can run very slowly depending on number and size of images");
    setupDisplayImagesInSparqlResults.setSelected(true);
    menu.add(setupDisplayImagesInSparqlResults);

    menu.addSeparator();

    buttonGroup = new ButtonGroup();
    final ExportFormat[] formats = ExportFormat.values();
    setupExportSparqlResultsFormat = new JCheckBoxMenuItem[formats.length];
    for (int formatIndex = 0; formatIndex < formats.length; ++formatIndex) {
      setupExportSparqlResultsFormat[formatIndex] = new JCheckBoxMenuItem(
          "Save SPARQL Results as " + formats[formatIndex].getFormatName());
      setupExportSparqlResultsFormat[formatIndex]
          .setToolTipText("Export as "
              + formats[formatIndex].getFormatDescription() + " format");
      buttonGroup.add(setupExportSparqlResultsFormat[formatIndex]);
      menu.add(setupExportSparqlResultsFormat[formatIndex]);
    }

    menu.addSeparator();

    setupSparqlResultsToFile = new JCheckBoxMenuItem(
        "Send SPARQL Results Directly to File");
    setupSparqlResultsToFile.setToolTipText(
        "For large results sets this permits writing to file without trying to render on screen");
    menu.add(setupSparqlResultsToFile);

    menu.addSeparator();

    setupEnableStrictMode = new JCheckBoxMenuItem(
        "Enable Strict Checking Mode");
    setupEnableStrictMode.setSelected(true);
    setupEnableStrictMode.addActionListener(new ReasonerConfigurationChange());
    menu.add(setupEnableStrictMode);

    setupEnableValidation = new JCheckBoxMenuItem("Enable Ontology Validation");
    setupEnableValidation.setSelected(true);
    setupEnableValidation.addActionListener(new ReasonerConfigurationChange());
    menu.add(setupEnableValidation);

    menu.addSeparator();

    setupFont = new JMenuItem("Font");
    setupFont.setMnemonic(KeyEvent.VK_F);
    setupFont.setToolTipText("Set the font used for the display");
    setupFont.addActionListener(new FontSetupListener());
    menu.add(setupFont);

    menu.addSeparator();

    setupProxyEnabled = new JCheckBoxMenuItem("Enable Proxy");
    setupProxyEnabled
        .setToolTipText("Pass network SPARQL requests through a proxy");
    setupProxyEnabled.addActionListener(new ProxyStatusChangeListener());
    menu.add(setupProxyEnabled);

    setupProxyConfiguration = new JMenuItem("Proxy Settings");
    setupProxyConfiguration.setToolTipText("Configure the proxy");
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
    menu.setToolTipText("Menu items related to viewing the model");

    modelCreateTreeView = new JMenuItem("Create Tree");
    modelCreateTreeView.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.ALT_MASK));
    modelCreateTreeView.setMnemonic(KeyEvent.VK_T);
    modelCreateTreeView
        .setToolTipText("Create tree representation of current model");
    modelCreateTreeView.addActionListener(new GenerateTreeListener());
    menu.add(modelCreateTreeView);

    modelListInferredTriples = new JMenuItem("Identify Inferred Triples");
    modelListInferredTriples.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK));
    modelListInferredTriples.setMnemonic(KeyEvent.VK_I);
    modelListInferredTriples.setToolTipText(
        "Create a list of inferred triples from the current model");
    modelListInferredTriples
        .addActionListener(new GenerateInferredTriplesListener());
    menu.add(modelListInferredTriples);

    menu.addSeparator();

    filterResetTree = new JMenuItem("Clear Tree");
    filterResetTree.setToolTipText(
        "Remove the tree view of the ontology. This may help if memory is running low");
    filterResetTree.addActionListener(new ClearTreeModelListener());
    menu.add(filterResetTree);

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
    filterShowAnonymousNodes
        .setToolTipText("Include anonymous nodes in the tree view");
    menu.add(filterShowAnonymousNodes);

    showFqnInTree = new JCheckBoxMenuItem("Show FQN In Tree");
    showFqnInTree.setSelected(false);
    showFqnInTree.setToolTipText(
        "Show the fully qualified name for classes, properties and objects in the tree");
    menu.add(showFqnInTree);

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

    menu.addSeparator();

    filterSetMaximumIndividualsPerClassInTree = new JMenuItem(
        "Set Maximum Individuals Per Class in Tree");
    filterSetMaximumIndividualsPerClassInTree
        .setToolTipText(
            "Limit number of individuals shown for each class in the tree view.");
    filterSetMaximumIndividualsPerClassInTree
        .addActionListener(new SetMaximumIndividualsPerClassInTreeListener());
    menu.add(filterSetMaximumIndividualsPerClassInTree);

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
    menu.setToolTipText("Options for using the SPARQL server");

    sparqlServerStartup = new JMenuItem("Startup SPARQL Server");
    sparqlServerStartup.setMnemonic(KeyEvent.VK_S);
    sparqlServerStartup.setToolTipText("Start the SPARQL server");
    sparqlServerStartup.addActionListener(new SparqlServerStartupListener());
    menu.add(sparqlServerStartup);

    sparqlServerShutdown = new JMenuItem("Shutdown SPARQL Server");
    sparqlServerShutdown.setMnemonic(KeyEvent.VK_H);
    sparqlServerShutdown.setToolTipText("Stop the SPARQL server");
    sparqlServerShutdown.addActionListener(new SparqlServerShutdownListener());
    menu.add(sparqlServerShutdown);

    menu.addSeparator();

    sparqlServerPublishCurrentModel = new JMenuItem(
        "Publish Current Reasoned Model");
    sparqlServerPublishCurrentModel.setMnemonic(KeyEvent.VK_P);
    sparqlServerPublishCurrentModel
        .setToolTipText(
            "Set the model for the SPARQL server to the current one reasoned");
    sparqlServerPublishCurrentModel
        .addActionListener(new SparqlServerPublishModelListener());
    menu.add(sparqlServerPublishCurrentModel);

    menu.addSeparator();

    sparqlServerConfig = new JMenuItem("Configure the SPARQL Server");
    sparqlServerConfig.setMnemonic(KeyEvent.VK_C);
    sparqlServerConfig.setToolTipText("Configure the server endpoint");
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
    menu.setToolTipText("Menu items related to user assistance");

    helpOverviewVideo = new JMenuItem("8 Minute Overview Video");
    helpOverviewVideo.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.ALT_MASK));
    helpOverviewVideo.setMnemonic(KeyEvent.VK_V);
    helpOverviewVideo
        .setToolTipText("View an 8 minute overview of Semantic Workbench");
    helpOverviewVideo.addActionListener(new OverviewVideoListener());
    menu.add(helpOverviewVideo);

    helpAbout = new JMenuItem("About");
    helpAbout.setAccelerator(
        KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
    helpAbout.setMnemonic(KeyEvent.VK_A);
    helpAbout.setToolTipText("View version information");
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

    // Don't allow editing if the ontology file did not load completely.
    // This is
    // to avoid confusion for the user since when the ontology file isn't
    // loaded
    // completely the reasoner will be run on the actual file rather than
    // the
    // version in the text area, so text area edits would be ignored.

    // TODO Consider switching the status of hasIncompleteAssertionsInput if
    // the
    // user edits the text area after an incomplete load. Would want to warn
    // the
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

    enableAssertionsHandling(enable);

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

    sparqlService = ((String) sparqlServiceUrl.getEditor().getItem()).trim();
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
        && (sparqlInput.getText().toLowerCase().indexOf("from") > -1
            || sparqlInput.getText().toLowerCase().indexOf("service") > -1)) {
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
    sparqlServerPublishCurrentModel
        .setEnabled(SparqlServer.getInstance().isActive() && ontModel != null);
    sparqlServerStartup
        .setEnabled(!SparqlServer.getInstance().isActive() && ontModel != null);

    // Proxy
    setupProxyConfiguration.setEnabled(true);
    setupProxyEnabled.setEnabled(isProxyConfigOkay(false));

    // Query history
    previousQuery.setEnabled(enable && queryHistory.hasPrevious());
    nextQuery.setEnabled(enable && queryHistory.hasNext());
  }

  /**
   * Enable or disable the assertions save and inferencing execution
   * 
   * @param enable
   *          Whether controls may be enabled
   */
  private void enableAssertionsHandling(boolean enable) {
    boolean enableRunInferencing;
    boolean enableSaveTriples;

    enableRunInferencing = enable
        && assertionsInput.getText().trim().length() > 0;

    /*
     * The file save option is not available if a local file could not be
     * loaded completely (don't want to accidentally overwrite it with an
     * incomplete version) However, if the incomplete load is from a URL,
     * then it can be saved to a file for local manipulation.
     */
    enableSaveTriples = enable && assertionsInput.getText().trim().length() > 0
        && (!hasIncompleteAssertionsInput
            || (rdfFileSource != null && rdfFileSource.isUrl()));

    if (runInferencing.isEnabled() != enableRunInferencing) {
      runInferencing.setEnabled(enableRunInferencing);
    }

    if (fileSaveTriplesToFile.isEnabled() != enableSaveTriples) {
      fileSaveTriplesToFile.setEnabled(enableSaveTriples);
    }
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
        System.setProperty("http.nonProxyHosts", "");
      }

      if (proxyProtocolSocks) {
        System.setProperty("socksProxyHost", proxyServer);
        System.setProperty("socksProxyPort", proxyPort + "");
      }

      proxyInfo.setText("Enabled (" + proxyServer + "@" + proxyPort + ")");
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
   *          Whether to popup a message dialog if an error exists in the
   *          proxy configuration
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
        // Force proxying off
        setupProxyEnabled.setSelected(false);
        JOptionPane.showMessageDialog(this,
            "Proxying cannot be enabled.\nPlease see the information below.\n\n"
                + errorMessages
                + "\nUse the Proxy Configuration option to update the proxy settings.",
            "Proxy Cannot Be Enabled", JOptionPane.ERROR_MESSAGE);
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
          .showMessageDialog(this,
              "You must restart the program for the proxy change to take effect.\n\nAfter the restart the proxy will be "
                  + changeType,
              "Proxy Setting Changed: " + changeType,
              JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /**
   * Configure the proxy settings for executing remote SPARQL queries through
   * a proxy
   */
  private void configureProxy() {
    final ProxyConfigurationDialog dialog = new ProxyConfigurationDialog(this,
        proxyServer, proxyPort,
        proxyProtocolHttp, proxyProtocolSocks);

    if (dialog.isAccepted()) {
      if (dialog.getProxyServer().trim().length() > 0) {
        proxyServer = dialog.getProxyServer();
      } else {
        JOptionPane.showMessageDialog(this, "The proxy server cannot be blank",
            "Proxy Server Required",
            JOptionPane.ERROR_MESSAGE);
      }
      if (dialog.getProxyPort() != null && dialog.getProxyPort() > 0) {
        proxyPort = dialog.getProxyPort();
      } else {
        JOptionPane
            .showMessageDialog(this,
                "The proxy port number must be a number greater than 0\n\nEntered value: "
                    + dialog.getProxyPort(),
                "Illegal Proxy Port Number", JOptionPane.ERROR_MESSAGE);
      }
      proxyProtocolHttp = dialog.isProtocolHttp();
      proxyProtocolSocks = dialog.isProtocolSocks();
      if (!proxyProtocolHttp && !proxyProtocolSocks) {
        JOptionPane.showMessageDialog(this,
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
    LOGGER
        .debug("Request to start SPARQL server. SPARQL server already running? "
            + SparqlServer.getInstance().isActive() + " ontModel exists? "
            + (ontModel != null));
    if (!SparqlServer.getInstance().isActive()) {
      if (ontModel != null) {
        publishModelToTheSparqlServer();
        SparqlServer.getInstance().addObserver(this);

        try {
          SparqlServer.getInstance().start();
        } catch (Throwable throwable) {
          LOGGER.error("Unable to start the SPARQL server", throwable);
          SparqlServer.getInstance().deleteObserver(this);
          JOptionPane.showMessageDialog(this,
              "Unable to start the SPARQL server\n" + throwable.getMessage(),
              "Cannot Start the SPARQL Server", JOptionPane.ERROR_MESSAGE);
        }

        if (SparqlServer.getInstance().isActive()) {
          if (SparqlServer.getInstance().areRemoteUpdatesPermitted()) {
            sparqlServerInfo.setBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.red.darker()),
                    "SPARQL Server Status (Updates Allowed)",
                    TitledBorder.DEFAULT_JUSTIFICATION,
                    TitledBorder.DEFAULT_POSITION,
                    BorderFactory.createTitledBorder("").getTitleFont(),
                    Color.red.darker()));

          }
          sparqlServerInfo.setForeground(Color.blue.darker());
          updateSparqlServerInfo();
          setStatus("SPARQL server started on port "
              + SparqlServer.getInstance().getListenerPort()
              + (SparqlServer.getInstance().areRemoteUpdatesPermitted()
                  ? " (Remote Updates Enabled)"
                  : ""));
        }
      } else {
        JOptionPane.showMessageDialog(this,
            "You must create a model before starting the SPARQL server",
            "Cannot Start SPARQL Server", JOptionPane.WARNING_MESSAGE);
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

    sparqlServerInfo
        .setBorder(BorderFactory.createTitledBorder("SPARQL Server Status"));
    sparqlServerInfo.setText("Shutdown");
    sparqlServerInfo.setForeground(Color.black);

    enableControls(true);
  }

  /**
   * Show the SPARQL Server Status
   */
  private void updateSparqlServerInfo() {
    sparqlServerInfo.setText(
        "Port:" + SparqlServer.getInstance().getListenerPort() + "  Requests: "
            + SparqlServer.getInstance().getConnectionsHandled());
  }

  /**
   * Publishes the current ontology model to the SPARQL server endpoint
   */
  private void publishModelToTheSparqlServer() {
    if (ontModel != null) {
      LOGGER.debug("Create a new model for the SPARQL server");
      final OntModel newModel = ModelFactory
          .createOntologyModel(OntModelSpec.OWL_DL_MEM);
      // final OntModel newModel = createModel((ReasonerSelection)
      // reasoningLevel
      // .getSelectedItem());
      LOGGER
          .debug("Add the current model to the new one for the SPARQL server");
      try {
        newModel.add(ontModel);
      } catch (Throwable throwable) {
        LOGGER.error("Unable to setup the model for the SPARQL server",
            throwable);
      }

      LOGGER.debug("Set the model for the SPARQL server");
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
          this,
          SparqlServer.getInstance().getListenerPort(),
          SparqlServer.getInstance().getMaxRuntimeSeconds(),
          SparqlServer.getInstance().areRemoteUpdatesPermitted());
      if (dialog.isAccepted()) {
        if (dialog.getPortNumber() != null && dialog.getPortNumber() > 0) {
          SparqlServer.getInstance().setListenerPort(dialog.getPortNumber());
        } else {
          JOptionPane.showMessageDialog(this,
              "The port number must be a number greater than 0\n\nEntered value: "
                  + dialog.getPortNumber(),
              "Illegal Port Number", JOptionPane.ERROR_MESSAGE);
        }
        if (dialog.getMaxRuntime() != null && dialog.getMaxRuntime() > 0) {
          SparqlServer.getInstance()
              .setMaxRuntimeSeconds(dialog.getMaxRuntime());
        } else {
          JOptionPane
              .showMessageDialog(this,
                  "The maximum runtime setting must be a number greater than 0\n\nEntered value: "
                      + dialog.getMaxRuntime(),
                  "Illegal Maximum Runtime", JOptionPane.ERROR_MESSAGE);
        }
        SparqlServer.getInstance()
            .setRemoteUpdatesPermitted(dialog.areRemoteUpdatesAllowed());
      }
    }
  }

  /**
   * Set the tab background and foreground color based on whether the tab's
   * data is out of synch with the model or other configuration changes
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
    } else {
      tabbedPane.setForegroundAt(1, NORMAL_TAB_FG);
      tabbedPane.setBackgroundAt(1, NORMAL_TAB_BG);
      tabbedPane.setToolTipTextAt(1, null);
    }

    if (!isTreeInSyncWithModel
        && !ontModelTree.getModel().isLeaf(ontModelTree.getModel().getRoot())) {
      tabbedPane.setForegroundAt(2, Color.red);
      tabbedPane.setBackgroundAt(2, Color.pink);
      tabbedPane.setToolTipTextAt(2,
          "Tree is out of sync with loaded assertions");
    } else {
      tabbedPane.setForegroundAt(2, NORMAL_TAB_FG);
      tabbedPane.setBackgroundAt(2, NORMAL_TAB_BG);
      tabbedPane.setToolTipTextAt(2, null);
    }

    if (!areSparqlResultsInSyncWithModel
        && sparqlResultsTable.getRowCount() > 0) {
      tabbedPane.setForegroundAt(3, Color.red);
      tabbedPane.setBackgroundAt(3, Color.pink);
      tabbedPane.setToolTipTextAt(3,
          "Results are out of sync with loaded assertions");
    } else {
      tabbedPane.setForegroundAt(3, NORMAL_TAB_FG);
      tabbedPane.setBackgroundAt(3, NORMAL_TAB_BG);
      tabbedPane.setToolTipTextAt(3, null);
    }
  }

  /**
   * Setup all the components
   */
  private void setupControls() {
    LOGGER.debug("setupControls");

    reasoningLevel = new JComboBox<ReasonerSelection>();
    for (ReasonerSelection reasoner : ReasonerSelection.values()) {
      reasoningLevel.addItem(reasoner);
    }
    reasoningLevel.setSelectedIndex(reasoningLevel.getItemCount() - 1);
    reasoningLevel.setToolTipText(
        ((ReasonerSelection) reasoningLevel.getSelectedItem()).description());
    reasoningLevel.addActionListener(new ReasonerConfigurationChange());

    language = new JComboBox<String>();
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
    runInferencing.setToolTipText(
        "Creates an ontology model using the provieed assertions "
            + "and the selected reasoning level");
    runInferencing.addActionListener(new ReasonerListener());

    runSparql = new JButton("Run Query");
    runSparql.addActionListener(new SparqlListener());

    sparqlServerInfo = new JLabel("Shutdown");
    sparqlServerInfo.setHorizontalAlignment(SwingConstants.CENTER);
    sparqlServerInfo
        .setBorder(BorderFactory.createTitledBorder("SPARQL Server Status"));

    proxyInfo = new JLabel("Disabled");
    proxyInfo.setHorizontalAlignment(SwingConstants.CENTER);
    proxyInfo.setBorder(BorderFactory.createTitledBorder("Proxy Status"));

    assertionsInput = new JTextArea(10, 50);
    assertionsInput.addKeyListener(new UserInputListener());
    assertionsInput.addCaretListener(new TextAreaCaratListener());

    inferredTriples = new JTextArea(10, 50);
    inferredTriples.setEditable(false);

    // SPARQL Input
    sparqlInput = new JTextArea(10, 50);
    sparqlInput.addKeyListener(new UserInputListener());
    sparqlInput.addCaretListener(new TextAreaCaratListener());

    // User id and password for accessing secured SPARQL endpoints
    sparqlServiceUserId = new JTextField(10);
    sparqlServicePassword = new JPasswordField(10);

    // SPARQL service URLs
    sparqlServiceUrl = new JComboBox<String>();
    sparqlServiceUrl.setEditable(true);
    sparqlServiceUrl.addActionListener(new SparqlModelChoiceListener());
    sparqlServiceUrl.getEditor().getEditorComponent()
        .addKeyListener(new UserInputListener());

    // Default graph if required
    defaultGraphUri = new JTextField();
    defaultGraphUri.setColumns(20);

    // Move through query history
    previousQuery = new JButton("Previous");
    previousQuery.addActionListener(new SparqlHistoryPreviousListener());
    nextQuery = new JButton("Next");
    nextQuery.addActionListener(new SparqlHistoryNextListener());

    // A basic default query
    sparqlInput.setText("select ?s ?p ?o where { ?s ?p ?o } limit 100");

    // Results table
    // sparqlResultsTable = new JTable(new SparqlTableModel());
    sparqlResultsTable = new JTable();

    // TODO Allow configuration to switch auto-resizing on/off (e.g.
    // horizontal
    // scrolling)
    sparqlResultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    sparqlResultsTable.setAutoCreateRowSorter(true);

    // Determine whether alternate tree icons exist
    if (ImageLibrary.instance()
        .getImageIcon(ImageLibrary.ICON_TREE_CLASS) != null) {
      replaceTreeImages = true;
    }

    LOGGER.debug(
        "Tree renderer, specialized icons available? " + replaceTreeImages);

    // Create the tree UI with a default model
    ontModelTree = new JTree(
        new DefaultTreeModel(new DefaultMutableTreeNode("No Tree Generated")));

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

    progress = new ProgressMonitor(this, "Expanding Tree Nodes",
        "Starting node expansion", 0, numNodes);

    setStatus("Expanding all tree nodes");

    for (int row = 0; !progress.isCanceled() && row < numNodes; ++row) {
      progress.setProgress(row);
      if (row % 1000 == 0) {
        progress.setNote("Row " + row + " of " + numNodes);
      }

      ontModelTree.expandRow(row);
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
          JOptionPane
              .showMessageDialog(this,
                  "An instruction to execute a task was received\n"
                      + "but the task was undefined. ("
                      + runningOperation + ")",
                  "Error: No Process to Run", JOptionPane.ERROR_MESSAGE);
      }
    } catch (ConversionException ce) {
      LOGGER.error("Failed during conversion within the model", ce);
      finalStatus = errorAlert(ce,
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
   * Creates the status message for the error, alerts the user with a popup.
   * If the issue is a recognized syntax error and the line and column numbers
   * can be found int he exception message, the cursor will be moved to that
   * position.
   * 
   * @param throwable
   *          The error that occurred
   * @param operationDetailMessage
   *          A message specific to the operation that was running. This may
   *          be null
   * 
   * @return The message to be presented on the status line
   */
  private String errorAlert(Throwable throwable,
      String operationDetailMessage) {
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

    statusMessage = causeMessage.trim().length() > 0 ? causeMessage
        : causeClass;

    if (throwable instanceof QueryExceptionHTTP) {
      httpExc = (QueryExceptionHTTP) throwable;

      httpStatusMessage = httpExc.getResponseMessage();
      if (httpStatusMessage == null || httpStatusMessage.trim().length() == 0) {
        try {
          // From version 3 of HTTP components
          // httpStatusMessage = HttpStatus.getStatusText(httpExc
          // .getResponseCode());
          // For version 4 of HTTP components
          httpStatusMessage = EnglishReasonPhraseCatalog.INSTANCE.getReason(
              httpExc.getResponseCode(),
              Locale.ENGLISH);
        } catch (Throwable lookupExc) {
          LOGGER.info("Cannot find message for returned HTTP code of "
              + httpExc.getResponseCode());
        }
      }
    }

    if (httpExc != null) {
      statusMessage += ": " + "Response Code: " + httpExc.getResponseCode()
          + (httpStatusMessage != null && httpStatusMessage.trim().length() > 0
              ? " (" + httpStatusMessage + ")" : "");

      JOptionPane.showMessageDialog(this,
          alertMessage + "\n\n" + "Response Code: " + httpExc.getResponseCode()
              + "\n" + (httpStatusMessage != null ? httpStatusMessage : ""),
          "Error", JOptionPane.ERROR_MESSAGE);
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
              + throwable.getClass().toString() + "->"
              + throwable.getCause());
          nextThrowable = nextThrowable.getCause();
        }
      }
      if (riotExc != null) {
        lineAndColumn = getSyntaxErrorLineColLocation(
            riotExc.getMessage().toLowerCase(), "line: ", ",",
            "col: ", "]");
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
              + throwable.getClass().toString() + "->"
              + throwable.getCause());
          nextThrowable = nextThrowable.getCause();
        }
      }
      if (queryParseExc != null) {
        lineAndColumn = getSyntaxErrorLineColLocation(
            queryParseExc.getMessage().toLowerCase(), "at line ", ",",
            ", column ", ".");
        whichSelectedTab = TAB_NUMBER_SPARQL;
        whichFocusJTextArea = sparqlInput;
      } else {
        LOGGER.debug(
            "No query parse exception found so the caret cannot be positioned");
      }
    }

    if (lineAndColumn[0] > 0 && lineAndColumn[1] > 0) {
      LOGGER.debug(
          "Attempt to set assertions caret to position (" + lineAndColumn[0]
              + "," + lineAndColumn[1] + ")");
      final int finalLineNumber = lineAndColumn[0] - 1;
      final int finalColumnNumber = lineAndColumn[1] - 1;
      final int finalWhichSelectedTab = whichSelectedTab;
      final JTextArea finalWhichFocusJTextArea = whichFocusJTextArea;
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          tabbedPane.setSelectedIndex(finalWhichSelectedTab);
          try {
            finalWhichFocusJTextArea.setCaretPosition(
                finalWhichFocusJTextArea.getLineStartOffset(finalLineNumber)
                    + finalColumnNumber);
            finalWhichFocusJTextArea.requestFocusInWindow();
          } catch (Throwable throwable) {
            LOGGER.warn("Cannot set " + finalWhichFocusJTextArea.getName()
                + " carat position to ("
                + finalLineNumber + "," + finalColumnNumber + ") on tab "
                + finalWhichSelectedTab,
                throwable);
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
   *          The syntax error message containing the line and column
   *          position of the error
   * @param lineNumStartToken
   *          The token preceeding the line number in the message
   * @param lineNumEndToken
   *          The token following the line number in the message
   * @param colNumStartToken
   *          The token preceeding the column number in the message
   * @param colNumEndToken
   *          The token following the column number in the message
   * 
   * @return A 2-element array with the line number in element 0 and the
   *         column number in element 1. The value for the line and/or column
   *         will be -1 if the information cannot be found in the message.
   */
  private int[] getSyntaxErrorLineColLocation(String message,
      String lineNumStartToken, String lineNumEndToken,
      String colNumStartToken, String colNumEndToken) {
    final int[] lineAndColumn = new int[2];

    lineAndColumn[0] = -1;
    lineAndColumn[1] = -1;

    final int startLinePos = message.indexOf(lineNumStartToken);
    final int endLinePos = message.substring(startLinePos)
        .indexOf(lineNumEndToken) + startLinePos;
    final int startColPos = message.indexOf(colNumStartToken);
    final int endColPos = message.substring(startColPos).indexOf(colNumEndToken)
        + startColPos;
    if (startLinePos > -1 && startColPos > startLinePos) {
      try {
        lineAndColumn[0] = Integer
            .parseInt(message.substring(
                startLinePos + lineNumStartToken.length(), endLinePos).trim());
        lineAndColumn[1] = Integer
            .parseInt(message
                .substring(startColPos + colNumStartToken.length(), endColPos)
                .trim());
      } catch (Throwable parseError) {
        LOGGER.warn(
            "Cannot extract line/col from exception message: " + message,
            parseError);
      }
    }

    return lineAndColumn;
  }

  /**
   * Check whether it is okay to start a new process (e.g. assure that no
   * threads are currently executing against the model). If a thread is
   * currently running, a message dialog is presented to the user indicate
   * what process is running and that a new process cannot be started.
   * 
   * @return True if a new thread may be started
   */
  private boolean okToRunThread() {
    final boolean okToRun = runningOperation == null;

    if (!okToRun) {
      JOptionPane
          .showMessageDialog(this,
              "A process is already running and must complete.\n\n"
                  + "The program is currently "
                  + runningOperation.description(),
              "Operation in Process", JOptionPane.INFORMATION_MESSAGE);
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
  private void runSparql() {
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
   * Setup to build the list of inferred triples in the model and start a
   * thread
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

    loadModel();

    return "Reasoning complete";
  }

  /**
   * Execute the steps to run the SPARQL query
   * 
   * @return The message to be presented on the status line
   */
  private String sparqlExecution() {
    String statusMessage;

    setStatus("Running SPARQL query...");
    setWaitCursor(true);

    // Check if this is a local model SPARQL update
    if (sparqlInput.getText().toLowerCase().indexOf("delete") > -1
        || sparqlInput.getText().toLowerCase().indexOf("insert") > -1) {
      statusMessage = callSparqlUpdateEngine();
    } else {
      statusMessage = callSparqlEngine();
    }
    areSparqlResultsInSyncWithModel = true;
    colorCodeTabs();

    return statusMessage;
  }

  /**
   * Handle a SPARQL update request
   * 
   * @return The message to be presented on the status line
   */
  private String callSparqlUpdateEngine() {
    String serviceUrl;
    String message = "Update completed";

    final SparqlTableModel tableModel = new SparqlTableModel();
    final SparqlResultItemRenderer renderer = new SparqlResultItemRenderer(
        setupAllowMultilineResultOutput.isSelected());
    renderer.setFont(sparqlResultsTable.getFont());
    sparqlResultsTable.setDefaultRenderer(SparqlResultItem.class, renderer);
    tableModel.displayMessageInTable("SPARQL Update, No Results",
        new String[] {});
    sparqlResultsTable.setModel(tableModel);

    serviceUrl = ((String) sparqlServiceUrl.getSelectedItem()).trim();

    if (sparqlServiceUrl.getSelectedIndex() == 0 || serviceUrl.length() == 0) {
      // Updating the local model
      long originalAssertionCount = 0;
      long resultingAssertionCount = 0;

      // final GraphStore graphStore = GraphStoreFactory.create(ontModel);
      originalAssertionCount = ontModel.size();
      UpdateAction.parseExecute(sparqlInput.getText(), ontModel);
      resultingAssertionCount = ontModel.size();

      message = "Local model update completed [Original Assertion Count:"
          + originalAssertionCount
          + "  Resulting Assertion Count:" + resultingAssertionCount + "]";

      /*
       * Assume the update modified the model: update counts then
       * invalidate the tree and inferences
       */
      showModelTripleCounts();
      isTreeInSyncWithModel = false;
      areInferencesInSyncWithModel = false;

      // Query history
      final SparqlQuery sparqlQuery = new SparqlQuery(sparqlInput.getText());
      final QueryInfo queryInfo = new QueryInfo(sparqlQuery,
          sparqlServiceUrl.getSelectedIndex() == 0 ? null
              : sparqlServiceUrl.getSelectedItem().toString(),
          defaultGraphUri.getText(), tableModel);
      queryHistory.addQuery(queryInfo);
    } else {
      // Updating via a remote endpoint
      final UpdateRequest request = UpdateFactory.create(sparqlInput.getText());
      UpdateProcessor processor;

      if (sparqlServiceUserId.getText().trim().length() == 0) {
        processor = UpdateExecutionFactory.createRemote(request, serviceUrl);
      } else {
        processor = UpdateExecutionFactory.createRemote(request, serviceUrl,
            createCredentialedProvider(sparqlServiceUserId.getText(),
                sparqlServicePassword.getPassword()));
      }
      processor.execute();
    }

    return message;
  }

  /**
   * Create an HTTP client that is configured to use basic authentication
   * based on the supplied user id and password.
   * 
   * @param uid
   *          The user id
   * @param password
   *          The password
   * @return The HttpClient instance setup for basic authentication
   */
  private HttpClient createCredentialedProvider(String uid, char[] password) {
    CredentialsProvider credsProvider = new BasicCredentialsProvider();
    Credentials credentials = new UsernamePasswordCredentials(uid,
        new String(password));
    credsProvider.setCredentials(AuthScope.ANY, credentials);
    HttpClient httpclient = HttpClients.custom()
        .setDefaultCredentialsProvider(credsProvider).build();
    HttpOp.setDefaultHttpClient(httpclient);
    return httpclient;
  }

  /**
   * Handle a SPARQL query request, use the SPARQL engine and report the
   * results
   * 
   * @return The number of resulting rows
   */
  private String callSparqlEngine() {
    QueryExecution qe;
    String serviceUrl;
    ResultSet resultSet = null;
    long numResults = 0;
    String message = null;
    final SparqlTableModel tableModel = new SparqlTableModel();

    // Get the query
    final String queryString = sparqlInput.getText().trim();

    /*
     * Query history initialized - in case the query fails it will be in the
     * history list
     */
    final SparqlQuery sparqlQuery = new SparqlQuery(sparqlInput.getText());
    QueryInfo queryInfo = new QueryInfo(sparqlQuery,
        sparqlServiceUrl.getSelectedIndex() == 0 ? null
            : sparqlServiceUrl.getSelectedItem().toString(),
        defaultGraphUri.getText(), null);
    queryHistory.addQuery(queryInfo);

    final Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
    LOGGER.debug("Query Graph URIs? " + query.getGraphURIs());

    serviceUrl = ((String) sparqlServiceUrl.getSelectedItem()).trim();

    // Execute the query and obtain results
    if (query.getGraphURIs() != null && query.getGraphURIs().size() > 0) {
      LOGGER.debug("Query has Graph URIs: " + query.getGraphURIs().size());
      qe = QueryExecutionFactory.create(query);
    } else if (sparqlServiceUrl.getSelectedIndex() == 0
        || serviceUrl.length() == 0) {
      if (ontModel == null) {
        qe = QueryExecutionFactory.create(query,
            ModelFactory.createOntologyModel());
      } else {
        qe = QueryExecutionFactory.create(query, ontModel);
      }
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
              createCredentialedProvider(sparqlServiceUserId.getText(),
                  sparqlServicePassword.getPassword()));
        }
      } else {
        // No default graph

        // Check for User Id
        if (sparqlServiceUserId.getText().trim().length() == 0) {
          // Unauthenticated - open endpoint
          // TODO Test with StarDog and resolve issues with deprecated
          // authenticator
          qe = QueryExecutionFactory.sparqlService(serviceUrl, query);
          // qe = QueryExecutionFactory.sparqlService(serviceUrl,
          // query, new StarDogSparqlAuthenticator());
        } else {
          // Authenticated
          qe = QueryExecutionFactory.sparqlService(serviceUrl, query,
              createCredentialedProvider(
                  sparqlServiceUserId.getText(),
                  sparqlServicePassword.getPassword()));
        }
      }
    }

    if (query.isDescribeType()) {
      final Model model = qe.execDescribe();
      if (model != null) {
        tableModel.displayStatementsInTable(model.listStatements(), 1000,
            "Describe ");
        message = "DESCRIBE executed, resulting model size: " + model.size();
      } else {
        message = "DESCRIBE executed, no model returned";
      }

    } else if (query.isAskType()) {
      final boolean result = qe.execAsk();
      message = "ASK executed, result: " + result;
      tableModel.displayMessageInTable(message, new String[] {});
    } else if (query.isConstructType()) {
      final Model model = qe.execConstruct();
      int numCreatedAssertions = 0;
      long numAddedAssertions = 0;
      if (model != null) {
        tableModel.displayStatementsInTable(model.listStatements(), 1000,
            "Constructed ");
        numCreatedAssertions = model.getGraph().size();
        numAddedAssertions = ontModel.size();
        ontModel.add(model);
        numAddedAssertions = ontModel.size() - numAddedAssertions;
        if (numAddedAssertions != 0) {
          // Assume the update modified the model: update counts then
          // invalidate
          // the tree and inferences
          showModelTripleCounts();
          isTreeInSyncWithModel = false;
          areInferencesInSyncWithModel = false;
        }
      }
      message = "CONSTRUCT executed [Created Assertions:" + numCreatedAssertions
          + "  New Assertions:"
          + numAddedAssertions + "]";
    } else {
      // Not a construct - assume select
      resultSet = qe.execSelect();
      if (setupSparqlResultsToFile.isSelected()) {
        numResults = writeSparqlResultsDirectlyToFile(resultSet,
            new SparqlResultsFormatter(query, ontModel,
                setupApplyFormattingToLiteralValues.isSelected(),
                setupOutputFlagLiteralValues.isSelected(),
                setupOutputDatatypesForLiterals.isSelected(),
                setupOutputFqnNamespaces.isSelected()));
        /*
         * Release these results since writing directly to file often is
         * used for very large result sets. Also, do not want to hold
         * these results in history
         */
        resultSet = null;
        message = "Results written to file";
      }
    }

    if (resultSet != null) {
      tableModel.setupModel(resultSet, query, ontModel,
          setupApplyFormattingToLiteralValues.isSelected(),
          setupOutputFlagLiteralValues.isSelected(),
          setupOutputDatatypesForLiterals.isSelected(),
          setupOutputFqnNamespaces.isSelected(),
          setupDisplayImagesInSparqlResults.isSelected());
    }

    final SparqlResultItemRenderer renderer = new SparqlResultItemRenderer(
        setupAllowMultilineResultOutput.isSelected());
    renderer.setFont(sparqlResultsTable.getFont());
    sparqlResultsTable.setDefaultRenderer(SparqlResultItem.class, renderer);
    sparqlResultsTable.setModel(tableModel);

    numResults = tableModel.getRowCount();

    if (numResults > 0) {
      // TODO Allow configuration to switch auto-resizing on/off (e.g.
      // horizontal scrolling)
      GuiUtilities.initColumnSizes(sparqlResultsTable, tableModel);
    }

    // Important - free up resources used running the query
    qe.close();

    // Update query history with table model (which may be null)
    queryInfo = new QueryInfo(sparqlQuery,
        sparqlServiceUrl.getSelectedIndex() == 0 ? null
            : sparqlServiceUrl.getSelectedItem().toString(),
        defaultGraphUri.getText(), tableModel);
    queryHistory.addQuery(queryInfo);

    if (message == null) {
      message = "Number of query results: " + numResults;
    }

    return message;
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
    rdfFileSaved = true;
    setTitle();
  }

  /**
   * Set the SPARQL query file
   * 
   * @param pSparqlQueryFile
   *          The FileSource of the SPARQL query
   */
  public void setSparqlQueryFile(File pSparqlQueryFile) {
    sparqlQueryFile = pSparqlQueryFile;
    sparqlQuerySaved = true;
    addRecentSparqlFile(pSparqlQueryFile);
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
      if (!rdfFileSaved) {
        title += "*";
      }
    }

    if (sparqlQueryFile != null) {
      title += " (" + sparqlQueryFile.getName();
      if (!sparqlQuerySaved) {
        title += "*";
      }
      title += ")";
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

    LOGGER.debug(
        "Compute differences between reasoned and non-reasoned models to show inferred triples");
    tempModel = ontModel.difference(ontModel.getBaseModel());
    LOGGER.debug("Model differences computed to identify inferred triples");

    writer = new StringWriter();
    // TODO JenaJSONLD.init();
    tempModel.write(writer, assertionLanguage);
    LOGGER.debug(
        "String representation of differences created to show inferred triples using "
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
   * @param reasoner
   *          The reasoner from the ReasonerSelection enum to be used with
   *          this model
   * 
   * @return The created ontology model
   */
  private OntModel createModel(ReasonerSelection reasoner) {
    OntModel model = null;

    LOGGER.debug("Create reasoner: " + reasoner.reasonerName());

    model = ModelFactory.createOntologyModel(reasoner.jenaSpecification());
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
          LOGGER.debug("Error processing assertions as format: " + format,
              throwable);
          lastThrowable = throwable;
        }
      }
    } else {
      try {
        tryFormat(language.getSelectedItem().toString());
        modelFormat = language.getSelectedItem().toString();
      } catch (Throwable throwable) {
        LOGGER.error("Error processing assertions as format: "
            + language.getSelectedItem().toString(),
            throwable);
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
                + getFormatsAsCSV(),
            lastThrowable);
      } else {
        throw new IllegalStateException(
            "The assertions cannot be loaded using the input format: "
                + language.getSelectedItem().toString(),
            lastThrowable);
      }
    } else {
      LOGGER.info("Loaded assertions" + " using format: " + modelFormat);
      showModelTripleCounts();
    }
    assertionLanguage = modelFormat;

    setTitle();
  }

  /**
   * Display the triple counts from the local model
   */
  private void showModelTripleCounts() {
    if (ontModel != null) {
      assertedTripleCount.setText(
          INTEGER_COMMA_FORMAT.format(ontModel.getBaseModel().size()) + "");
      inferredTripleCount
          .setText(INTEGER_COMMA_FORMAT
              .format(ontModel.size() - ontModel.getBaseModel().size()) + "");
    } else {
      assertedTripleCount.setText(NOT_APPLICABLE_DISPLAY);
      inferredTripleCount.setText(NOT_APPLICABLE_DISPLAY);
    }
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
          + " model load and setup with format "
          + format);

      if (hasIncompleteAssertionsInput) {
        inputStream = new ProgressMonitorInputStream(this,
            "Reading file " + rdfFileSource.getAbsolutePath(),
            rdfFileSource.getInputStream());

        if (rdfFileSource.isUrl()) {
          final ProgressMonitor pm = ((ProgressMonitorInputStream) inputStream)
              .getProgressMonitor();
          pm.setMaximum((int) rdfFileSource.length());
        }
        LOGGER.debug("Using a ProgressMonitorInputStream");
      } else {
        inputStream = new ByteArrayInputStream(
            assertionsInput.getText().getBytes("UTF-8"));
      }

      ontModel = createModel(
          (ReasonerSelection) reasoningLevel.getSelectedItem());
      LOGGER.debug("Begin loading model");
      ontModel.read(inputStream, null, format.toUpperCase());

      if (setupEnableValidation.isSelected()) {
        LOGGER.debug("Ontology validation is enabled, starting validation");
        ValidityReport validity = ontModel.validate();
        if (validity.isValid()) {
          LOGGER.warn("Validation OK");
        } else {
          StringBuffer message = new StringBuffer();
          message.append("Validation conflicts:\n");
          LOGGER.fatal("Conflicts");
          for (Iterator<Report> i = validity.getReports(); i.hasNext();) {
            String conflict = i.next().toString();
            message.append("  ");
            message.append(conflict);
            message.append('\n');
            LOGGER.fatal(" - " + conflict);
          }
          JOptionPane.showMessageDialog(this, message,
              "Ontology Validation Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }

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
   * Clears the tree model
   */
  private void clearTree() {
    ontModelTree.setModel(
        new DefaultTreeModel(new DefaultMutableTreeNode("No Tree Generated")));
    isTreeInSyncWithModel = true;
    colorCodeTabs();
  }

  /**
   * Set the maximum number of individuals to display for each class in the
   * tree view of the model
   */
  private void setMaximumIndividualsPerClassInTree() {
    final String currentValue = properties
        .getProperty(
            ConfigurationProperty.MAX_INDIVIDUALS_PER_CLASS_IN_TREE.key(), "0");
    final String maximumChildNodes = JOptionPane.showInputDialog(this,
        "Enter the maximum number of individuals to be displayed\n"
            + "under each class in the tree view of the model.\n\n"
            + "Enter the value 0 (zero) to allow all the individuals\n"
            + "to be shown.\n\n"
            + "The current setting is: " + currentValue,
        "Limit Individuals Displayed Per Class in the Tree View",
        JOptionPane.QUESTION_MESSAGE);

    if (maximumChildNodes != null && maximumChildNodes.trim().length() > 0) {
      try {
        final int maxNodes = Integer.parseInt(maximumChildNodes);
        if (maxNodes >= 0) {
          properties.setProperty(
              ConfigurationProperty.MAX_INDIVIDUALS_PER_CLASS_IN_TREE.key(),
              maxNodes + "");
        } else {
          JOptionPane.showMessageDialog(this,
              "The value entered for the maximum number of individuals\n"
                  + "was less than 0. The original setting is unchanged.",
              "Negative Value Entered", JOptionPane.ERROR_MESSAGE);
        }
      } catch (Throwable throwable) {
        JOptionPane.showMessageDialog(this,
            "The value entered for the maximum number of individuals\n"
                + "was not a number. The original setting is unchanged.",
            "Non-numeric Value Entered", JOptionPane.ERROR_MESSAGE);
      }
    }

  }

  /**
   * Build a tree representation of the semantic model
   * 
   * TODO aggregate items from duplicate nodes
   * 
   * TODO Consider more efficient approach that scans the model once rather
   * than querying for each class, individual and property collection
   * 
   * @see #addClassesToTree(DefaultMutableTreeNode, String)
   * @see OntologyTreeCellRenderer
   * 
   * @return The message to be presented on the status line
   */
  private String createTreeFromModel() {
    final String messagePrefix = "Creating the tree view";
    DefaultMutableTreeNode treeTopNode;
    DefaultMutableTreeNode classesNode;
    String message;
    int maxIndividualsPerClass;

    setStatus(messagePrefix);
    setWaitCursor(true);

    clearTree();

    try {
      maxIndividualsPerClass = Integer.parseInt(
          properties.getProperty(
              ConfigurationProperty.MAX_INDIVIDUALS_PER_CLASS_IN_TREE.key(),
              "0"));
    } catch (Throwable throwable) {
      maxIndividualsPerClass = 0;
    }

    treeTopNode = new DefaultMutableTreeNode("Model");

    // Ignore latest value since we have just released the old tree (if
    // there
    // was one)
    MemoryWarningSystem
        .hasLatestAvailableTenuredGenAfterCollectionChanged(this);

    // Classes
    classesNode = new DefaultMutableTreeNode("Classes");
    treeTopNode.add(classesNode);

    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Building list of classes in the model");
    }

    try {
      addClassesToTree(classesNode, maxIndividualsPerClass, messagePrefix);

      // Select the tree view tab
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          tabbedPane.setSelectedIndex(TAB_NUMBER_TREE_VIEW);
        }
      });

      message = "Tree view of current model created";
      if (maxIndividualsPerClass > 0) {
        message += " (individuals per class limited to "
            + maxIndividualsPerClass + ")";
      }

      ontModelTree.setModel(new DefaultTreeModel(treeTopNode));
      isTreeInSyncWithModel = true;
      colorCodeTabs();
    } catch (IllegalStateException ise) {
      // Memory exhaustion, keep the incomplete tree
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          tabbedPane.setSelectedIndex(TAB_NUMBER_TREE_VIEW);
        }
      });
      message = "Insufficient memory for entire tree, partial tree view of current model created";
      ontModelTree.setModel(new DefaultTreeModel(treeTopNode));
      isTreeInSyncWithModel = false;
      colorCodeTabs();
      throw ise;
    } catch (RuntimeException rte) {
      if (rte.getMessage().contains("canceled by user")) {
        message = rte.getMessage();
      } else {
        throw rte;
      }
    }

    return message;
  }

  /**
   * Add the classes to the tree view
   * 
   * @see #addIndividualsToTree(OntClass, DefaultMutableTreeNode,
   *      ProgressMonitor)
   * 
   * @param classesNode
   *          The classes parent node in the tree
   * @param maxIndividualsPerClass
   *          The maximum number of individuals to display in each class
   * @param messagePrefix
   *          Prefix for display messages
   */
  private void addClassesToTree(DefaultMutableTreeNode classesNode,
      int maxIndividualsPerClass,
      String messagePrefix) {
    ProgressMonitor progress = null;
    DefaultMutableTreeNode oneClassNode;
    List<OntClass> ontClasses;
    ExtendedIterator<OntClass> classesIterator;
    int classNumber;
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
          "Setting up the class list", 0,
          ontClasses.size());
      Collections.sort(ontClasses, new OntClassComparator());
      if (LOGGER.isTraceEnabled()) {
        LOGGER
            .trace("List of classes sorted. Num classes:" + ontClasses.size());
      }

      classNumber = 0;
      for (OntClass ontClass : ontClasses) {
        setStatus(messagePrefix + " for class " + ontClass);

        if (MemoryWarningSystem
            .hasLatestAvailableTenuredGenAfterCollectionChanged(this)
            && MemoryWarningSystem
                .getLatestAvailableTenuredGenAfterCollection() < MINIMUM_BYTES_REQUIRED_FOR_TREE_BUILD) {
          throw new IllegalStateException(
              "Insufficient memory available to complete building the tree (class iteration)");
        }

        if (progress.isCanceled()) {
          throw new RuntimeException("Tree model creation canceled by user");
        }

        progress.setNote(ontClass.toString());
        progress.setProgress(++classNumber);

        // Check whether class is to be skipped
        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace("Check if class to be skipped: " + ontClass.getURI());
          for (String skipClass : classesToSkipInTree.keySet()) {
            LOGGER.trace(
                "Class to skip: " + skipClass + "  equal? "
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
            oneClassNode = new DefaultMutableTreeNode(
                new WrapperClass(ontClass.getId().getLabelString(),
                    "[Anonymous class]", true));
          } else {
            LOGGER.debug(
                "Skip anonymous class: " + ontClass.getId().getLabelString());
            continue;
          }
        } else {
          oneClassNode = new DefaultMutableTreeNode(
              new WrapperClass(ontClass.getLocalName(), ontClass.getURI(),
                  showFqnInTree.isSelected()));
          LOGGER.debug("Add class node: " + ontClass.getLocalName() + " ("
              + ontClass.getURI() + ")");
        }
        classesNode.add(oneClassNode);

        addIndividualsToTree(ontClass, oneClassNode, maxIndividualsPerClass,
            progress);
      }

    } finally {
      if (progress != null) {
        progress.close();
      }
    }
  }

  /**
   * Add individuals of a class to the tree
   * 
   * @see #addStatementsToTree(OntClass, Individual, DefaultMutableTreeNode,
   *      ProgressMonitor)
   * 
   * @param ontClass
   *          The class of individuals to be added
   * @param oneClassNode
   *          The class's node in the tree
   * @param maxIndividualsPerClass
   *          The maximum number of individuals to display in each class
   * @param progress
   *          A progress monitor to display progress to the user
   */
  private void addIndividualsToTree(OntClass ontClass,
      DefaultMutableTreeNode oneClassNode,
      int maxIndividualsPerClass, ProgressMonitor progress) {
    DefaultMutableTreeNode oneIndividualNode;
    List<Individual> individuals;
    ExtendedIterator<Individual> individualsIterator;
    int nodeCount = 0;

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
      ++nodeCount;
      if (maxIndividualsPerClass > 0 && nodeCount >= maxIndividualsPerClass) {
        break;
      }
    }

    Collections.sort(individuals, new IndividualComparator());
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("List of individuals sorted for " + ontClass.getURI());
    }

    for (Individual individual : individuals) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Next individual: " + individual.getLocalName());
      }

      if (MemoryWarningSystem
          .hasLatestAvailableTenuredGenAfterCollectionChanged(this)
          && MemoryWarningSystem
              .getLatestAvailableTenuredGenAfterCollection() < MINIMUM_BYTES_REQUIRED_FOR_TREE_BUILD) {
        throw new IllegalStateException(
            "Insufficient memory available to complete building the tree (individual iteration)");
      }

      if (individual.isAnon()) {
        // Show anonymous individuals based on configuration
        if (filterShowAnonymousNodes.isSelected()) {
          if (individual.getId().getLabelString() != null) {
            oneIndividualNode = new DefaultMutableTreeNode(new WrapperInstance(
                individual.getId().getLabelString(), "[Anonymous individual]",
                true));
          } else {
            oneIndividualNode = new DefaultMutableTreeNode(
                new WrapperInstance(individual.toString(),
                    "[null label - anonymous individual]", true));
          }
        } else {
          LOGGER.debug("Skip anonymous individual: "
              + individual.getId().getLabelString());
          continue;
        }
      } else if (individual.getLocalName() != null) {
        oneIndividualNode = new DefaultMutableTreeNode(
            new WrapperInstance(individual.getLocalName(),
                individual.getURI(), showFqnInTree.isSelected()));
      } else {
        oneIndividualNode = new DefaultMutableTreeNode(
            new WrapperInstance(individual.toString(),
                "[null name - non anonymous]", true));
      }
      oneClassNode.add(oneIndividualNode);

      addStatementsToTree(individual, oneIndividualNode, progress);
    }

  }

  /**
   * Add statements to the tree (predicates and properties)
   * 
   * @param individual
   *          The individual whose statements are to be added to the tree
   * @param oneIndividualNode
   *          The individual's node in the tree
   * @param progress
   *          A progress monitor to display progress to the user
   */
  private void addStatementsToTree(Individual individual,
      DefaultMutableTreeNode oneIndividualNode,
      ProgressMonitor progress) {
    DefaultMutableTreeNode onePropertyNode;
    List<Statement> statements;
    Property property;
    RDFNode rdfNode;
    Literal literal;
    StmtIterator stmtIterator;

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
      LOGGER.trace("List of statements sorted for " + individual.getURI());
    }

    for (Statement statement : statements) {
      property = statement.getPredicate();

      // Check whether predicate is to be skipped
      if (filterEnableFilters.isSelected()
          && predicatesToSkipInTree.get(property.getURI()) != null) {
        continue;
      }

      rdfNode = statement.getObject();

      if (MemoryWarningSystem
          .hasLatestAvailableTenuredGenAfterCollectionChanged(this)
          && MemoryWarningSystem
              .getLatestAvailableTenuredGenAfterCollection() < MINIMUM_BYTES_REQUIRED_FOR_TREE_BUILD) {
        throw new IllegalStateException(
            "Insufficient memory available to complete building the tree (statement iteration)");
      }

      if (property.isAnon()) {
        // Show anonymous properties based on configuration
        if (filterShowAnonymousNodes.isSelected()) {
          if (rdfNode.isLiteral()) {
            onePropertyNode = new DefaultMutableTreeNode(
                new WrapperDataProperty(
                    property.getId().getLabelString(),
                    "[Anonymous data property]", true));
          } else {
            onePropertyNode = new DefaultMutableTreeNode(
                new WrapperObjectProperty(
                    property.getId().getLabelString(),
                    "[Anonymous object property]", true));
          }
        } else {
          LOGGER.debug(
              "Skip anonymous property: " + property.getId().getLabelString());
          continue;
        }
      } else if (rdfNode.isLiteral() || !statement.getResource().isAnon()
          || filterShowAnonymousNodes.isSelected()) {
        if (rdfNode.isLiteral()) {
          onePropertyNode = new DefaultMutableTreeNode(
              new WrapperDataProperty(property.getLocalName(),
                  property.getURI(), showFqnInTree.isSelected()));
        } else {
          onePropertyNode = new DefaultMutableTreeNode(
              new WrapperObjectProperty(property.getLocalName(),
                  property.getURI(), showFqnInTree.isSelected()));
        }
      } else {
        LOGGER.debug("Skip concrete property of an anonymous individual: "
            + property.getURI() + ", "
            + statement.getResource().getId().getLabelString());
        continue;

      }
      oneIndividualNode.add(onePropertyNode);

      if (rdfNode.isLiteral()) {
        literal = statement.getLiteral();
        if (setupOutputDatatypesForLiterals.isSelected()) {
          onePropertyNode.add(new DefaultMutableTreeNode(new WrapperLiteral(
              ValueFormatter.getInstance().applyFormat(literal.getString(),
                  literal.getDatatypeURI())
                  + " [" + literal.getDatatypeURI() + "]")));

        } else {
          onePropertyNode.add(new DefaultMutableTreeNode(new WrapperLiteral(
              ValueFormatter.getInstance().applyFormat(literal.getString(),
                  literal.getDatatypeURI()))));
        }
      } else {
        if (statement.getResource().isAnon()) {
          if (filterShowAnonymousNodes.isSelected()) {
            onePropertyNode.add(new DefaultMutableTreeNode(new WrapperInstance(
                statement.getResource().getId().getLabelString(),
                "[Anonymous individual]", true)));
          } else {
            LOGGER.debug("Skip anonymous individual: "
                + statement.getResource().getId().getLabelString());
            continue;
          }
        } else {
          onePropertyNode
              .add(new DefaultMutableTreeNode(
                  new WrapperInstance(statement.getResource().getLocalName(),
                      statement.getResource().getURI(),
                      showFqnInTree.isSelected())));
        }
      }
    }
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
   * Handle left-mouse click on ontology model tree.
   * 
   * If the selected node is an individual, the tree will be searched forward
   * (down) to jump to the next matching individual in the tree
   * 
   * Otherwise, no action will be taken in response to the left mouse click in
   * the tree.
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
      //@SuppressWarnings("unchecked")
      final Enumeration<TreeNode> nodeEnumeration = node
          .preorderEnumeration();
      while (nodeEnumeration.hasMoreElements()) {
        final DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode) nodeEnumeration.nextElement();
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
                // Searching backward and have found a previous
                // one
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
              LOGGER.debug(
                  "Found first matching node: search UUID: " + wrapper.getUuid()
                      + " found UUID: " + nodeWrapper.getUuid());
            } else {
              // Keep track of latest one found
              latestMatchAt = nextNode;
              LOGGER.debug("Found a following matching node: search UUID: "
                  + wrapper.getUuid()
                  + " found UUID: " + nodeWrapper.getUuid());
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
          setStatus(
              "Wrapped to top of tree and found a " + wrapper.getLocalName());
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
            ontModelTree.setSelectionPath(
                new TreePath(treeModel.getPathToRoot(finalMatchAt)));
            ontModelTree.scrollPathToVisible(
                new TreePath(treeModel.getPathToRoot(finalMatchAt)));
            final Rectangle visible = ontModelTree.getVisibleRect();
            visible.x = 0;
            ontModelTree.scrollRectToVisible(visible);
          }
        });
      }
    }
  }

  /**
   * Handle right-mouse click on ontology model tree.
   * 
   * If the selected node is a class or property it will be added to the list
   * of suppressed classes and properties so that it won't show up in the
   * tree.
   * 
   * If the selected node is an individual, the tree will be searched backward
   * (up) to jump to the previous matching individual in the tree
   * 
   * Otherwise, no action will be taken in response to the right mouse click
   * in the tree.
   * 
   * @param event
   *          The mouse click event
   */
  private void processOntologyModelTreeRightClick(MouseEvent event) {
    final Wrapper wrapper = getSelectedWrapperInTree(event);
    if (wrapper != null) {
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
   *          Whether or not the current enable/disable setting of GUI
   *          controls should be updated
   */
  private void invalidateModel(boolean alterControls) {
    if (ontModel != null) {
      ontModel.close();
    }

    ontModel = null;
    showModelTripleCounts();

    reasoningLevel.setToolTipText(
        ((ReasonerSelection) reasoningLevel.getSelectedItem()).description());

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
          + " of class "
          + path.getLastPathComponent().getClass());
      if (path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
        final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path
            .getLastPathComponent();
        final Object selectedObject = selectedNode.getUserObject();
        LOGGER.debug("Class of object in the selected tree node: "
            + selectedObject.getClass().getName());
        if (selectedObject instanceof Wrapper) {
          chosenWrapper = (Wrapper) selectedObject;
          LOGGER.debug("Wrapper found: " + selectedObject);
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
    FileFilter defaultFileFilter = null;
    FileFilter preferredFileFilter = null;
    File chosenFile;

    if (lastDirectoryUsed == null) {
      lastDirectoryUsed = new File(".");
    }

    fileChooser = new JFileChooser(lastDirectoryUsed);

    for (FileFilterDefinition filterDefinition : FileFilterDefinition
        .values()) {
      if (filterDefinition.name().startsWith("ONTOLOGY")) {
        final FileFilter fileFilter = new SuffixFileFilter(
            filterDefinition.description(),
            filterDefinition.acceptedSuffixes());
        if (filterDefinition.isPreferredOption()) {
          preferredFileFilter = fileFilter;
        }
        fileChooser.addChoosableFileFilter(fileFilter);
        if (filterDefinition.description()
            .equals(latestChosenRdfFileFilterDescription)) {
          defaultFileFilter = fileFilter;
        }
      }
    }

    if (defaultFileFilter != null) {
      fileChooser.setFileFilter(defaultFileFilter);
    } else if (latestChosenRdfFileFilterDescription != null
        && latestChosenRdfFileFilterDescription.startsWith("All")) {
      fileChooser.setFileFilter(fileChooser.getAcceptAllFileFilter());
    } else if (preferredFileFilter != null) {
      fileChooser.setFileFilter(preferredFileFilter);
    }

    fileChooser.setDialogTitle("Open Assertions File");
    fileChooser.showOpenDialog(this);

    try {
      latestChosenRdfFileFilterDescription = fileChooser.getFileFilter()
          .getDescription();
    } catch (Throwable throwable) {
      LOGGER.warn("Unable to determine which ontology file filter was chosen",
          throwable);
    }

    chosenFile = fileChooser.getSelectedFile();

    if (chosenFile != null) {
      setupToLoadOntologyFile(new FileSource(chosenFile));
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
                + "\n\n" + throwable.getMessage(),
            "Incorrect URL Format", JOptionPane.ERROR_MESSAGE);
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
    FileFilter defaultFileFilter = null;
    FileFilter preferredFileFilter = null;
    File chosenFile;

    if (lastDirectoryUsed == null) {
      lastDirectoryUsed = new File(".");
    }

    fileChooser = new JFileChooser(lastDirectoryUsed);

    for (FileFilterDefinition filterDefinition : FileFilterDefinition
        .values()) {
      if (filterDefinition.name().startsWith("SPARQL")) {
        final FileFilter fileFilter = new SuffixFileFilter(
            filterDefinition.description(),
            filterDefinition.acceptedSuffixes());
        if (filterDefinition.isPreferredOption()) {
          preferredFileFilter = fileFilter;
        }
        fileChooser.addChoosableFileFilter(fileFilter);
        if (filterDefinition.description()
            .equals(latestChosenSparqlFileFilterDescription)) {
          defaultFileFilter = fileFilter;
        }
      }
    }

    if (defaultFileFilter != null) {
      fileChooser.setFileFilter(defaultFileFilter);
    } else if (latestChosenSparqlFileFilterDescription != null
        && latestChosenSparqlFileFilterDescription.startsWith("All")) {
      fileChooser.setFileFilter(fileChooser.getAcceptAllFileFilter());
    } else if (preferredFileFilter != null) {
      fileChooser.setFileFilter(preferredFileFilter);
    }

    fileChooser.setDialogTitle("Open SPARQL Query File");
    fileChooser.showOpenDialog(this);

    try {
      latestChosenSparqlFileFilterDescription = fileChooser.getFileFilter()
          .getDescription();
    } catch (Throwable throwable) {
      LOGGER.warn("Unable to determine which SPARQL file filter was chosen",
          throwable);
    }

    chosenFile = fileChooser.getSelectedFile();

    if (chosenFile != null) {
      loadSparqlQueryFile(chosenFile);
    }
  }

  /**
   * Open a recently used ontology file chosen from the file menu.
   * 
   * @param obj
   *          The menu item selected - associated with the recently used
   *          file
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
    }
  }

  /**
   * Open a recently used SPARQL query file chosen from the file menu.
   * 
   * @param obj
   *          The menu item selected - associated with the recently used
   *          file
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

    if (rdfFileSource.length() > 0 && rdfFileSource
        .length() < MAX_ASSERTION_BYTES_TO_LOAD_INTO_TEXT_AREA) {
      maxChunks = (int) (rdfFileSource.length() / chunkSize);
    } else {
      maxChunks = (int) (MAX_ASSERTION_BYTES_TO_LOAD_INTO_TEXT_AREA
          / chunkSize);
    }

    if (rdfFileSource.length() % chunkSize > 0) {
      ++maxChunks;
    }

    // Assume the file can be loaded
    hasIncompleteAssertionsInput = false;

    monitor = new ProgressMonitor(this,
        "Loading assertions from " + rdfFileSource.getName(), "0 bytes read", 0,
        maxChunks);

    try {
      reader = new InputStreamReader(rdfFileSource.getInputStream());
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
        monitor.setNote("Read "
            + INTEGER_COMMA_FORMAT.format(totalBytesRead)
            + (rdfFileSource.isFile()
                ? " of " + INTEGER_COMMA_FORMAT.format(rdfFileSource.length())
                : " bytes")
            + (chunksRead >= maxChunks ? " (Determining total file size)"
                : ""));

        loadCanceled = monitor.isCanceled();
      }

      if (!loadCanceled && rdfFileSource.isUrl()) {
        rdfFileSource.setLength(totalBytesRead);
      }

      if (!loadCanceled && rdfFileSource
          .length() > MAX_ASSERTION_BYTES_TO_LOAD_INTO_TEXT_AREA) {
        // The entire file was not loaded
        hasIncompleteAssertionsInput = true;
      }

      if (hasIncompleteAssertionsInput) {
        StringBuilder warningMessage;

        warningMessage = new StringBuilder();
        warningMessage.append(
            "The file is too large to display. However the entire file will be loaded\n");
        warningMessage.append(
            "into the model when it is built.\n\nDisplay size limit (bytes): ");
        warningMessage.append(INTEGER_COMMA_FORMAT
            .format(MAX_ASSERTION_BYTES_TO_LOAD_INTO_TEXT_AREA));
        if (rdfFileSource.isFile()) {
          warningMessage.append("\nFile size (bytes):");
          warningMessage
              .append(INTEGER_COMMA_FORMAT.format(rdfFileSource.length()));
        }
        warningMessage.append("\n\n");
        warningMessage.append(
            "Note that the assersions text area will not permit editing\n");
        warningMessage.append(
            "of the partially loaded file and the 'save assertions' menu\n");
        warningMessage
            .append("option will be disabled. These limitations are enabled\n");
        warningMessage
            .append("to prevent the accidental loss of information from the\n");
        warningMessage.append("source assertions file.");

        JOptionPane.showMessageDialog(this, warningMessage.toString(),
            "Max Display Size Reached",
            JOptionPane.WARNING_MESSAGE);

        // Add text to the assertions text area to highlight the fact
        // that the
        // entire file was not loaded into the text area
        allData.insert(0, "# First " + INTEGER_COMMA_FORMAT
            .format(MAX_ASSERTION_BYTES_TO_LOAD_INTO_TEXT_AREA)
            + " of " + INTEGER_COMMA_FORMAT.format(rdfFileSource.length())
            + " bytes displayed\n\n");
        allData.insert(0, "# INCOMPLETE VERSION of the file: "
            + rdfFileSource.getAbsolutePath() + "\n");
        allData.append("\n\n# INCOMPLETE VERSION of the file: "
            + rdfFileSource.getAbsolutePath() + "\n");
        allData.append("# First " + INTEGER_COMMA_FORMAT
            .format(MAX_ASSERTION_BYTES_TO_LOAD_INTO_TEXT_AREA)
            + " of " + INTEGER_COMMA_FORMAT.format(rdfFileSource.length())
            + " bytes displayed\n");
      }

      // Set the loaded assertions into the text area, cleaning up Windows
      // \r\n
      // endings, if found
      if (!loadCanceled) {
        assertionsInput.setText(allData.toString().replaceAll("\r\n", "\n"));
        assertionsInput.setSelectionEnd(0);
        assertionsInput.setSelectionStart(0);
        assertionsInput.moveCaretPosition(0);
        assertionsInput.scrollRectToVisible(new Rectangle(0, 0, 1, 1));

        message = "Loaded file"
            + (hasIncompleteAssertionsInput ? " (incomplete)" : "") + ": "
            + rdfFileSource.getName();
        addRecentAssertedTriplesFile(rdfFileSource);

        // Select the assertions tab
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            tabbedPane.setSelectedIndex(TAB_NUMBER_ASSERTIONS);
            setFocusOnCorrectTextArea();
          }
        });
      } else {
        message = "Assertions file load canceled by user";
      }
    } catch (Throwable throwable) {
      setStatus("Unable to load file: " + rdfFileSource.getName());
      JOptionPane.showMessageDialog(this,
          "Error: Unable to read file\n\n" + rdfFileSource.getAbsolutePath()
              + "\n\n" + throwable.getMessage(),
          "Error Reading File", JOptionPane.ERROR_MESSAGE);
      LOGGER.error(
          "Unable to load the file: " + rdfFileSource.getAbsolutePath(),
          throwable);
      message = "Unable to load the file: " + rdfFileSource.getAbsolutePath();
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
   * @return The extracted value (following the prefix) or null if the prefix
   *         is not found or no value follows it
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
   * Load the provided file as a SPARQL query replacing any query currently in
   * the query text area.
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

    checkForUnsavedSparqlQuery();

    lastDirectoryUsed = inputFile.getParentFile();

    reader = null;
    allData = new StringBuffer();

    try {
      reader = new BufferedReader(new FileReader(inputFile));
      while ((data = reader.readLine()) != null) {
        if (data.indexOf(SPARQL_QUERY_SAVE_SERVICE_URL_PARAM) > -1) {
          serviceUrlFromFile = extractCommentData(data,
              SPARQL_QUERY_SAVE_SERVICE_URL_PARAM);
        } else if (data
            .indexOf(SPARQL_QUERY_SAVE_SERVICE_DEFAULT_GRAPH_PARAM) > -1) {
          defaultGraphUriFromFile = extractCommentData(data,
              SPARQL_QUERY_SAVE_SERVICE_DEFAULT_GRAPH_PARAM);
        } else {
          allData.append(data);
          allData.append('\n');
        }
      }

      sparqlInput.setText(allData.toString());
      sparqlInput.setSelectionEnd(0);
      sparqlInput.setSelectionStart(0);
      sparqlInput.moveCaretPosition(0);
      sparqlInput.scrollRectToVisible(new Rectangle(0, 0, 1, 1));

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
      setSparqlQueryFile(inputFile);

      // Select the SPARQL tab
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          tabbedPane.setSelectedIndex(TAB_NUMBER_SPARQL);
          setFocusOnCorrectTextArea();
        }
      });
    } catch (IOException ioExc) {
      setStatus("Unable to load file: " + inputFile.getName());
      JOptionPane.showMessageDialog(this,
          "Error: Unable to read file\n\n" + inputFile.getAbsolutePath()
              + "\n\n" + ioExc.getMessage(),
          "Error Reading File", JOptionPane.ERROR_MESSAGE);
      LOGGER.error("Unable to load the file: " + inputFile.getAbsolutePath(),
          ioExc);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (Throwable throwable) {
          LOGGER.error("Unable to close input file", throwable);
        }
      }

      invalidateSparqlResults(true);
      setTitle();
    }
  }

  /**
   * Remove prior SPARQL results if the SPARQL query changes
   * 
   * @param alterControls
   *          Whether or not the current enable/disable setting of GUI
   *          controls should be updated
   */
  private void invalidateSparqlResults(boolean alterControls) {
    areSparqlResultsInSyncWithModel = false;

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

    fileChooser.setDialogTitle("Save Assertions to File");
    choice = fileChooser.showSaveDialog(this);
    destinationFile = fileChooser.getSelectedFile();

    // Did not click save, did not select a file or chose a directory
    // So do not write anything
    if (choice != JFileChooser.APPROVE_OPTION || destinationFile == null
        || (destinationFile.exists() && !destinationFile.isFile())) {
      return; // EARLY EXIT!
    }

    if (okToOverwriteFile(destinationFile)) {

      LOGGER.info("Write assertions to file, " + destinationFile);

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
            throw new RuntimeException(errorMessage, throwable);
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
              + "\n\nOkay to overwrite?",
          "Overwrite File?",
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
    FileFilter defaultFileFilter = null;
    FileFilter preferredFileFilter = null;
    File destinationFile;
    int choice;

    out = null;

    if (lastDirectoryUsed == null) {
      lastDirectoryUsed = new File(".");
    }

    fileChooser = new JFileChooser();

    for (FileFilterDefinition filterDefinition : FileFilterDefinition
        .values()) {
      if (filterDefinition.name().startsWith("SPARQL")) {
        final FileFilter fileFilter = new SuffixFileFilter(
            filterDefinition.description(),
            filterDefinition.acceptedSuffixes());
        if (filterDefinition.isPreferredOption()) {
          preferredFileFilter = fileFilter;
        }
        fileChooser.addChoosableFileFilter(fileFilter);
        if (filterDefinition.description()
            .equals(latestChosenSparqlFileFilterDescription)) {
          defaultFileFilter = fileFilter;
        }
      }
    }

    if (defaultFileFilter != null) {
      fileChooser.setFileFilter(defaultFileFilter);
    } else if (latestChosenSparqlFileFilterDescription != null
        && latestChosenSparqlFileFilterDescription.startsWith("All")) {
      fileChooser.setFileFilter(fileChooser.getAcceptAllFileFilter());
    } else if (preferredFileFilter != null) {
      fileChooser.setFileFilter(preferredFileFilter);
    }

    if (sparqlQueryFile != null) {
      fileChooser.setSelectedFile(sparqlQueryFile);
    } else {
      fileChooser.setSelectedFile(lastDirectoryUsed);
    }

    fileChooser.setDialogTitle("Save SPARQL Query to File");
    choice = fileChooser.showSaveDialog(this);

    try {
      latestChosenSparqlFileFilterDescription = fileChooser.getFileFilter()
          .getDescription();
    } catch (Throwable throwable) {
      LOGGER.warn("Unable to determine which SPARQL file filter was chosen",
          throwable);
    }

    destinationFile = fileChooser.getSelectedFile();

    // Did not click save, did not select a file or chose a directory
    // So do not write anything
    if (choice != JFileChooser.APPROVE_OPTION || destinationFile == null
        || (destinationFile.exists() && !destinationFile.isFile())) {
      return; // EARLY EXIT!
    }

    // Adjust file suffix if necessary
    final FileFilter fileFilter = fileChooser.getFileFilter();
    if (fileFilter != null && fileFilter instanceof SuffixFileFilter
        && !fileChooser.getFileFilter().accept(destinationFile)) {
      destinationFile = ((SuffixFileFilter) fileFilter)
          .makeWithPrimarySuffix(destinationFile);
    }

    if (okToOverwriteFile(destinationFile)) {

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
        setSparqlQueryFile(destinationFile);
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
        setTitle();
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

    return selectedItem == 0 ? assertionLanguage : FORMATS[selectedItem - 1];
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

      choice = JOptionPane.showConfirmDialog(this,
          "There is a newer version of Semantic Workbench Available\n"
              + "You are running version " + VERSION
              + " and the latest version is "
              + newVersionInformation.getLatestVersion() + "\n\n"
              + newVersionInformation.getDownloadInformation() + "\n"
              + "New features include:\n"
              + newVersionInformation.getNewFeaturesDescription() + "\n\n"
              + "Would you like to download the new version now?\n\n",
          "Newer Version Available (" + VERSION + "->"
              + newVersionInformation.getLatestVersion() + ")",
          JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

      if (choice == JOptionPane.YES_OPTION) {
        try {
          Desktop.getDesktop()
              .browse(newVersionInformation.getUrlToDownloadPage().toURI());
        } catch (Throwable throwable) {
          LOGGER.error("Cannot launch browser to access download page",
              throwable);
          JOptionPane.showMessageDialog(this,
              "Unable to launch a browser to access the download page\n" + "at "
                  + newVersionInformation.getUrlToDownloadPage().toString()
                  + "\n\n"
                  + throwable.getMessage(),
              "Unable to Access Download Page", JOptionPane.ERROR_MESSAGE);
        }
      }
    } else {
      JOptionPane.showMessageDialog(this,
          "There is a newer version of Semantic Workbench Available\n"
              + "You are running version " + VERSION
              + " and the latest version is "
              + newVersionInformation.getLatestVersion() + "\n\n"
              + "New features include:\n"
              + newVersionInformation.getNewFeaturesDescription(),
          "Newer Version Available (" + VERSION + "->"
              + newVersionInformation.getLatestVersion() + ")",
          JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /**
   * Search for text in the assertions text area. If found, the window will
   * scroll to the text location and the matching text will be highlighted.
   * 
   * @see #findNextTextInAssertionsTextArea()
   */
  private void findTextInAssertionsTextArea() {
    String textToFind;
    boolean found;

    textToFind = JOptionPane.showInputDialog(this,
        "Enter the text to find in the assertions editor",
        "Find Assertions Text", JOptionPane.QUESTION_MESSAGE);

    if (textToFind != null && textToFind.trim().length() > 0) {
      // Select the assertions tab
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          tabbedPane.setSelectedIndex(TAB_NUMBER_ASSERTIONS);
        }
      });

      textToFind = textToFind.trim();
      found = TextSearch.getInstance().startSearch(assertionsInput, textToFind);

      if (!found) {
        JOptionPane.showMessageDialog(this,
            "The text [" + textToFind + "] was not found", "Not Found",
            JOptionPane.INFORMATION_MESSAGE);
        setStatus("Did not find the assertions text: " + textToFind);
      } else {
        setStatus(
            "Found matching text at line "
                + TextSearch.getInstance().getLineOfLastMatch(assertionsInput));
      }
    } else {
      setStatus("No text entered to find");
    }
  }

  /**
   * Find the next occurrence of text matching a search. If found, the window
   * will scroll to the text location and the matching text will be
   * highlighted.
   * 
   * @see #findTextInAssertionsTextArea()
   */
  private void findNextTextInAssertionsTextArea() {
    boolean found;

    if (TextSearch.getInstance().hasActiveSearch(assertionsInput)) {
      // Select the assertions tab
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          tabbedPane.setSelectedIndex(TAB_NUMBER_ASSERTIONS);
        }
      });

      found = TextSearch.getInstance().continueSearch(assertionsInput);

      if (found
          && TextSearch.getInstance().lastSearchWrapped(assertionsInput)) {
        JOptionPane.showMessageDialog(this,
            "Wrapped back to the top when locating the text", "Wrapped To Top",
            JOptionPane.INFORMATION_MESSAGE);
        setStatus("Found matching text at line "
            + TextSearch.getInstance().getLineOfLastMatch(assertionsInput)
            + " (wrapped back to top)");
      } else if (found) {
        setStatus(
            "Found matching text at line "
                + TextSearch.getInstance().getLineOfLastMatch(assertionsInput));
      } else {
        JOptionPane.showMessageDialog(this, "The text was not found",
            "Not Found",
            JOptionPane.INFORMATION_MESSAGE);
      }
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
          prefixesToAdd.append(STANDARD_PREFIXES[formatIndex][prefix]);
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

    if (inputFileSource.isFile()
        && !inputFileSource.getBackingFile().isFile()) {
      JOptionPane.showMessageDialog(this,
          "Cannot read the file\n" + inputFileSource.getAbsolutePath(),
          "Cannot Read Assertions File", JOptionPane.ERROR_MESSAGE);
    } else {
      checkForUnsavedRdf();
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

    fileChooser.setDialogTitle(
        "Save Ontology Model to File (" + getSelectedOutputLanguage() + ")");
    choice = fileChooser.showSaveDialog(this);
    destinationFile = fileChooser.getSelectedFile();

    // Did not click save, did not select a file or chose a directory
    // So do not write anything
    if (choice != JFileChooser.APPROVE_OPTION || destinationFile == null
        || (destinationFile.exists() && !destinationFile.isFile())) {
      return; // EARLY EXIT!
    }

    if (okToOverwriteFile(destinationFile)) {
      modelExportFile = destinationFile;
      runModelExport();
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
        + (setupOutputModelTypeAssertionsAndInferences.isSelected()
            ? "assertions and inferences" : "assertions only")
        + ") to " + modelExportFile;

    setStatus("Writing " + message);

    LOGGER.info("Write model to file, " + modelExportFile + ", in format: "
        + getSelectedOutputLanguage());

    try {
      out = new FileWriter(modelExportFile, false);
      if (setupOutputModelTypeAssertionsAndInferences.isSelected()) {
        LOGGER.info("Writing complete model (assertions and inferences)");
        ontModel.writeAll(out, getSelectedOutputLanguage().toUpperCase(), null);
      } else {
        LOGGER.info("Writing assertions only");
        ontModel.getBaseModel().write(out,
            getSelectedOutputLanguage().toUpperCase());
      }
      message = "Completed writing " + message;
    } catch (IOException ioExc) {
      LOGGER.error("Unable to write file: " + modelExportFile, ioExc);
      setStatus("Failed to write file (check log) " + message);
      throw new RuntimeException(
          "Unable to write output file (" + modelExportFile + ")", ioExc);
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

    fileChooser.setDialogTitle("Save SPARQL Results to File (Format:"
        + chosenFormat().getFormatName() + ")");
    // + (setupExportSparqlResultsAsCsv.isSelected() ? "CSV" : "TSV") +
    // ")");
    choice = fileChooser.showSaveDialog(this);
    destinationFile = fileChooser.getSelectedFile();

    // Did not click save, did not select a file or chose a directory
    // So do not write anything
    if (choice != JFileChooser.APPROVE_OPTION || destinationFile == null
        || (destinationFile.exists() && !destinationFile.isFile())) {
      return; // EARLY EXIT!
    }

    if (okToOverwriteFile(destinationFile)) {
      sparqlResultsExportFile = destinationFile;
      runSparqlResultsExport();
    }
  }

  /**
   * Get the currently chosen format from the menu. Defaults to the first
   * available format
   * 
   * @return The currently chosen format
   */
  private ExportFormat chosenFormat() {
    ExportFormat[] formats = ExportFormat.values();

    for (int formatIndex = 0; formatIndex < formats.length; ++formatIndex) {
      if (setupExportSparqlResultsFormat[formatIndex].isSelected()) {
        return formats[formatIndex];
      }
    }

    return formats[0];
  }

  /**
   * Clear the history of executed SPARQL queries
   */
  private void clearSparqlQueryHistory() {
    queryHistory.clearAllQueries();
    enableControls(true);
    setStatus("SPARQL history cleared");
  }

  /**
   * Send SPARQL results to a file without presenting them in the results
   * table. This allows for arbitrarily large result sets to be handled and
   * written since there is no memory limitation being imposed by attempting
   * to represent the results within the GUI
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
    final ExportFormat chosenFormat = chosenFormat();
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
      outputFile = new File(outputDirectory,
          SPARQL_DIRECT_EXPORT_FILE_PREFIX + fileNumber + "."
              + chosenFormat.getDefaultFileSuffix());
    } while (outputFile.exists());

    message = "SPARQL results (" + chosenFormat.getFormatName()
        + ") directly to " + outputFile.getAbsolutePath();

    setStatus("Writing " + message);

    tableModel.displayMessageInTable("Exporting Results Directly to File",
        new String[] { outputFile.getAbsolutePath() });

    LOGGER
        .info("Write SPARQL results to file, " + outputFile.getAbsolutePath());

    try {
      if (chosenFormat.equals(ExportFormat.JSON)) {
        // JSON
        JsonExporter exporter = new JsonExporter(outputFile);
        exporter.writeResultSet(results);
      } else {
        // TSV or CSV - manually format and output the text
        out = new PrintWriter(outputFile);

        // Output column names
        columns = results.getResultVars();
        for (int columnNumber = 0; columnNumber < columns
            .size(); ++columnNumber) {
          if (columnNumber > 0) {
            out.print(chosenFormat.equals(ExportFormat.CSV) ? ',' : '\t');
          }

          if (chosenFormat.equals(ExportFormat.CSV)) {
            out.print(
                TextProcessing.formatForCsvColumn(columns.get(columnNumber)));
          } else {
            out.print(
                TextProcessing.formatForTsvColumn(columns.get(columnNumber)));
          }
        }

        out.println();

        // Output data
        while (results.hasNext()) {
          ++numRows;
          final QuerySolution solution = results.next();

          for (int columnNumber = 0; columnNumber < columns
              .size(); ++columnNumber) {
            if (columnNumber > 0) {
              out.print(chosenFormat.equals(ExportFormat.CSV) ? ',' : '\t');
            }

            if (chosenFormat.equals(ExportFormat.CSV)) {
              out.print(TextProcessing
                  .formatForCsvColumn(formatter.format(solution,
                      columns.get(columnNumber), false)));
            } else {
              out.print(TextProcessing
                  .formatForTsvColumn(formatter.format(solution,
                      columns.get(columnNumber), false)));
            }
          }

          out.println();
        }
      }
    } catch (Throwable throwable) {
      LOGGER.error(
          "Failed to write SPARQL results to " + outputFile.getAbsolutePath(),
          throwable);
      tableModel.displayMessageInTable("Exporting Results Directly to File",
          new String[] { "Failed to write the file", throwable.getMessage(),
              outputFile.getAbsolutePath() });
      throw new RuntimeException(
          "Failed to write SPARQL results to " + outputFile.getAbsolutePath(),
          throwable);
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
    ExportFormat chosenFormat = chosenFormat();

    String message = "SPARQL results (" + chosenFormat.getFormatName() + ") to "
        + sparqlResultsExportFile;

    setStatus("Writing " + message);

    LOGGER.info("Write SPARQL results to file, " + sparqlResultsExportFile);

    try {
      final SparqlTableModel model = (SparqlTableModel) sparqlResultsTable
          .getModel();

      if (chosenFormat.equals(ExportFormat.JSON)) {
        JsonExporter exporter = new JsonExporter(sparqlResultsExportFile);
        exporter.writeTableModel(model);
      } else {
        boolean toCsv = chosenFormat.equals(ExportFormat.CSV);
        out = new PrintWriter(new FileWriter(sparqlResultsExportFile, false));

        // Output column names
        for (int columnNumber = 0; columnNumber < model
            .getColumnCount(); ++columnNumber) {
          if (columnNumber > 0) {
            out.print(toCsv ? ',' : '\t');
          }

          if (toCsv) {
            out.print(TextProcessing
                .formatForCsvColumn(model.getColumnName(columnNumber)));
          } else {
            out.print(TextProcessing
                .formatForTsvColumn(model.getColumnName(columnNumber)));
          }
        }

        out.println();

        // Output row data
        for (int rowNumber = 0; rowNumber < model.getRowCount(); ++rowNumber) {
          for (int columnNumber = 0; columnNumber < model
              .getColumnCount(); ++columnNumber) {
            if (columnNumber > 0) {
              out.print(toCsv ? ',' : '\t');
            }

            if (toCsv) {
              out.print(TextProcessing.formatForCsvColumn(
                  model.getValueAt(rowNumber, columnNumber)));
            } else {
              out.print(TextProcessing.formatForTsvColumn(
                  model.getValueAt(rowNumber, columnNumber)));
            }
          }
          out.println();
        }
      }
      message = "Completed writing " + message;
    } catch (IOException ioExc) {
      LOGGER.error("Unable to write to file: " + sparqlResultsExportFile,
          ioExc);
      setStatus("Failed to write file (check log) " + message);
      throw new RuntimeException(
          "Unable to write output file (" + sparqlResultsExportFile + ")",
          ioExc);
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (Throwable throwable) {
          final String errorMessage = "Unable to close output file: "
              + sparqlResultsExportFile;
          LOGGER.error(errorMessage, throwable);
          throw new RuntimeException(errorMessage, throwable);
        }
      }
    }

    return message;
  }

  /**
   * Evaluate the SPARQL service field and see if a new service address has
   * been entered
   */
  private void processSparqlServiceChoice() {
    String currentSelection;

    try {
      currentSelection = sparqlServiceUrl.getSelectedItem().toString();

      LOGGER.debug("SPARQL Service URL Change: Index: "
          + sparqlServiceUrl.getSelectedIndex() + " Value: ["
          + currentSelection + "]");

      // Add a new service URL to the dropdown
      // A new service URL will likely be at least 13 characters long
      // e.g. http://1.2.3.4
      if (sparqlServiceUrl.getSelectedIndex() == -1
          && currentSelection.trim().length() > 10) {
        sparqlServiceUrl.addItem(currentSelection);
      }
    } catch (Throwable throwable) {
      LOGGER.warn("Unable to set the SPARQL service value", throwable);
      // Set to local model if the field value cannot be processed
      sparqlServiceUrl.setSelectedIndex(0);
    }

    enableControls(true);
  }

  /**
   * Load a SPARQL query from the history collection. A direction of 1 or more
   * means to move to the next query in the history list. A value of -1 or
   * less means to move to the previous query in the history list. If the
   * direction is equal to 0 then it means to load the current history query.
   * This is used when the program starts up to load the latest query.
   * 
   * @param direction
   *          Direction to traverse history. 1=forward, -1=backward,
   *          0=current
   */
  private void processSparqlQueryHistoryMove(int direction) {
    if (direction < 0) {
      queryHistory.moveBackward();
    } else if (direction > 0) {
      queryHistory.moveForward();
    }

    final QueryInfo queryInfo = queryHistory.getCurrentQueryInfo();

    sparqlInput.setText(queryInfo.getSparqlQuery().getSparqlStatement());
    sparqlInput.setCaretPosition(0);

    if (queryInfo.getServiceUrl() == null) {
      sparqlServiceUrl.setSelectedIndex(0);
    } else {
      sparqlServiceUrl.setSelectedItem(queryInfo.getServiceUrl());
    }

    String histDefaultGraphUri = queryInfo.getDefaultGraphUri();
    if (histDefaultGraphUri == null) {
      histDefaultGraphUri = "";
    }
    defaultGraphUri.setText(histDefaultGraphUri);

    final SparqlTableModel tableModel = queryInfo.getResults();
    LOGGER.debug("Historical results: " + tableModel);
    if (tableModel != null) {
      final SparqlResultItemRenderer renderer = new SparqlResultItemRenderer(
          setupAllowMultilineResultOutput.isSelected());
      renderer.setFont(sparqlResultsTable.getFont());
      sparqlResultsTable.setDefaultRenderer(SparqlResultItem.class, renderer);
      sparqlResultsTable.setModel(tableModel);
      setStatus("Historical query retrieved. Number of query results: "
          + sparqlResultsTable.getRowCount());
    } else {
      sparqlResultsTable.setModel(new SparqlTableModel());
      setStatus("Historical query retrieved.");
    }

    setFocusOnCorrectTextArea();
    sparqlQuerySaved = true;
    enableControls(true);
  }

  /**
   * Enable or disable the comment toggle menu item based on whether the
   * selected tab supports comment toggling and has one or more lines
   * selected.
   */
  private void enableCommentToggle() {
    final int selectedTab = tabbedPane.getSelectedIndex();

    if (selectedTab == TAB_NUMBER_SPARQL) {
      editCommentToggle.setEnabled(
          sparqlInput.getSelectionEnd() > sparqlInput.getSelectionStart());
    } else if (selectedTab == TAB_NUMBER_ASSERTIONS) {
      editCommentToggle.setEnabled(assertionsInput
          .getSelectionEnd() > assertionsInput.getSelectionStart());
    } else {
      editCommentToggle.setEnabled(false);
    }
  }

  /**
   * Set the focus in the "default" text area for the tab (if there is one).
   * Also assure that the caret is visible.
   */
  private void setFocusOnCorrectTextArea() {
    final int selectedTab = tabbedPane.getSelectedIndex();

    if (selectedTab == TAB_NUMBER_SPARQL) {
      sparqlInput.requestFocus();
      sparqlInput.getCaret().setVisible(true);
    } else if (selectedTab == TAB_NUMBER_ASSERTIONS) {
      assertionsInput.requestFocus();
      assertionsInput.getCaret().setVisible(true);
    }
  }

  /**
   * Toggle the comment character on/off for the chosen lines.
   * 
   * Currently this only supports using the pound sign (#) as the comment
   * character which works for most assertions formats except RDF/XML as well
   * as for SPARQL.
   */
  private void commentToggle() {
    final int selectedTab = tabbedPane.getSelectedIndex();

    if (selectedTab == TAB_NUMBER_SPARQL) {
      GuiUtilities.commentToggle(sparqlInput, "#");
    } else if (selectedTab == TAB_NUMBER_ASSERTIONS) {
      GuiUtilities.commentToggle(assertionsInput, "#");
    }
  }

  /**
   * Check for unsaved changes to information
   */
  private void checkForUnsavedInformation() {
    checkForUnsavedRdf();
    checkForUnsavedSparqlQuery();
  }

  /**
   * Check for unsaved changes to the assertions. If there are unsaved
   * changes, verify with the user whether to save them.
   */
  private void checkForUnsavedRdf() {
    if (!rdfFileSaved && assertionsInput.getText().trim().length() > 0) {
      if (checkWhetherToSaveFile("Assertion Data")) {
        saveAssertionsToFile();
      }
    }
  }

  /**
   * Check for unsaved changes to the SPARQL query. If there are unsaved
   * changes, verify with the user whether to save them.
   */
  private void checkForUnsavedSparqlQuery() {
    if (!sparqlQuerySaved && sparqlInput.getText().trim().length() > 0) {
      if (checkWhetherToSaveFile("SPARQL Query")) {
        saveSparqlQueryToFile();
      }
    }
  }

  /**
   * Ask the user whether to save unsaved information to a file.
   * 
   * @param description
   *          The description of the information to be saved. It should be
   *          written in the singular.
   * 
   * @return True if the user wants to save the information to a file
   */
  private boolean checkWhetherToSaveFile(String description) {
    return JOptionPane.showConfirmDialog(this,
        "The " + description + " has not been saved.\n\n"
            + "Do you want to save it?",
        "Unsaved Changes: " + description, JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
  }

  /**
   * Presents a classic "About" box to the user displaying the current version
   * of the application.
   */
  private void about() {
    String slMessage;

    slMessage = "Semantic Workbench\n\nVersion: " + VERSION + "\nJena Version: "
        + getJenaVersion()
        + "\nPellet Version: " + getPelletVersion()
        + "\n\nSystem information:\n  Free Memory: "
        + INTEGER_COMMA_FORMAT.format(Runtime.getRuntime().freeMemory())
        + "\n  Total Memory: "
        + INTEGER_COMMA_FORMAT.format(Runtime.getRuntime().totalMemory())
        + "\n  Maximum Memory: "
        + INTEGER_COMMA_FORMAT.format(Runtime.getRuntime().maxMemory())
        + "\n  Available Processors: "
        + Runtime.getRuntime().availableProcessors() + "\n\n";

    slMessage += "Graphical user interface for working with semantic "
        + "technology concepts including ontologies, reasoners and queries.\n\n";

    slMessage += "David Read, www.monead.com\n\n";

    slMessage += "Copyright (c) 2010-2016\n\n"
        + "This program is free software: you can redistribute it and/or modify "
        + "it under the terms of the GNU Affero General Public License as "
        + "published by the Free Software Foundation, either version 3 of the "
        + "License, or (at your option) any later version.\n\n"
        + "This program is distributed in the hope that it will be useful, "
        + "but WITHOUT ANY WARRANTY; without even the implied warranty of "
        + "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the "
        + "GNU Affero General Public License for more details.\n\n"
        + "You should have received a copy of the GNU Affero General Public License "
        + "along with this program.  If not, see <http://www.gnu.org/licenses/>.";

    JOptionPane.showMessageDialog(this,
        TextProcessing.characterInsert(slMessage, "\n", 70, 90, " ."),
        "About Semantic Workbench", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Launch a browser pointed at the overview video
   */
  private void viewOverviewVideo() {
    try {
      final URL url = new URL(OVERVIEW_VIDEO_LOCATION);
      Desktop.getDesktop().browse(url.toURI());
    } catch (Throwable throwable) {
      LOGGER.error("Cannot launch browser to show the overview video",
          throwable);
      JOptionPane
          .showMessageDialog(this,
              "Unable to launch a browser to show the overview video\n" + "at "
                  + OVERVIEW_VIDEO_LOCATION
                  + "\n\n" + throwable.getMessage(),
              "Unable to Launch Video", JOptionPane.ERROR_MESSAGE);
    }
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
    checkForUnsavedInformation();
    saveProperties();
    queryHistory.saveHistory(
        new File(getUserHomeDirectory(), SPARQL_QUERY_HISTORY_FILE_NAME));
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
   * A popup menu for allowing the user to select a set of values to be
   * removed from a list
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
          LOGGER.debug(
              "FilterValuePopup choice: " + wrappedObject.getClass().toString()
                  + " - " + wrappedObject);
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
      runSparql();
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
  private class RecentAssertedTriplesFileOpenListener
      implements ActionListener {
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
   * Move to previous query in the query history
   */
  private class SparqlHistoryPreviousListener implements ActionListener {
    /**
     * No operation
     */
    public SparqlHistoryPreviousListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      processSparqlQueryHistoryMove(-1);
    }
  }

  /**
   * Move to next query in the query history
   */
  private class SparqlHistoryNextListener implements ActionListener {
    /**
     * No operation
     */
    public SparqlHistoryNextListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      processSparqlQueryHistoryMove(1);
    }
  }

  /**
   * Search for text in the assertions text area
   */
  private class FindAssertionsTextListener implements ActionListener {
    /**
     * No operation
     */
    public FindAssertionsTextListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      findTextInAssertionsTextArea();
    }

  }

  /**
   * Search for the next text match in the assertions text area
   */
  private class FindNextAssertionsTextListener implements ActionListener {
    /**
     * No operation
     */
    public FindNextAssertionsTextListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      findNextTextInAssertionsTextArea();
    }
  }

  /**
   * Toggles selected lines in the SPARQL query between commented and not
   * commented
   */
  private class CommentToggleListener implements ActionListener {
    /**
     * No operation
     */
    public CommentToggleListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      commentToggle();
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
   * Clears the history of executed SPARQL queries
   */
  private class FileClearSparqlHistoryListener implements ActionListener {
    /**
     * No operation
     */
    public FileClearSparqlHistoryListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      clearSparqlQueryHistory();
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
   * Clears the existing tree model
   */
  private class ClearTreeModelListener implements ActionListener {
    /**
     * No operation
     */
    public ClearTreeModelListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      clearTree();
    }
  }

  /**
   * Set the maximum number of individuals to display for each class in the
   * tree view of the model
   */
  private class SetMaximumIndividualsPerClassInTreeListener
      implements ActionListener {
    /**
     * No operation
     */
    public SetMaximumIndividualsPerClassInTreeListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      setMaximumIndividualsPerClassInTree();
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
   * Displays overview video
   */
  private class OverviewVideoListener implements ActionListener {
    /**
     * No operation
     */
    public OverviewVideoListener() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
      viewOverviewVideo();
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
    }
  }

  /**
   * Listen for caret events on the SAPARQL text area. Intereted in whether
   * text has been selected in which case the comment toggle options needs to
   * be enabled.
   */
  private class TextAreaCaratListener implements CaretListener {
    /**
     * No operation
     */
    public TextAreaCaratListener() {

    }

    @Override
    public void caretUpdate(CaretEvent arg0) {
      enableCommentToggle();
    }

  }

  /**
   * Detect tab selections
   */
  private class TabbedPaneChangeListener implements ChangeListener {
    /**
     * No operation
     */
    public TabbedPaneChangeListener() {

    }

    @Override
    public void stateChanged(ChangeEvent arg0) {
      enableCommentToggle();
      setFocusOnCorrectTextArea();
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
     * Track current content of assertions and query fields to detect a
     * change if there is a current model or results
     * 
     * @param arg0
     *          The key event received
     */
    public void keyPressed(KeyEvent arg0) {
      if (arg0.getSource() == assertionsInput) {
        lastAssertions = assertionsInput.getText();
      }

      if (arg0.getSource() == sparqlInput) {
        lastSparql = sparqlInput.getText();
      }
    }

    /**
     * Detect whether a change was made to the assertions or query that
     * would require invalidating the model or results
     * 
     * @param arg0
     *          The key event received
     */
    public void keyReleased(KeyEvent arg0) {
      if (arg0.getSource() == assertionsInput) {
        if (!lastAssertions.equals(assertionsInput.getText())) {
          if (ontModel != null) {
            invalidateModel(true);
          }
          if (rdfFileSaved) {
            rdfFileSaved = false;
            setTitle();
          }
        }
        enableAssertionsHandling(true);
      } else if (arg0.getSource() == sparqlInput) {
        if (!lastSparql.equals(sparqlInput.getText())) {
          if (sparqlResultsTable.getModel() instanceof SparqlTableModel
              && !((SparqlTableModel) sparqlResultsTable.getModel()).isEmpty()
              && !lastSparql.equals(sparqlInput.getText())) {
            invalidateSparqlResults(true);
          }
          if (sparqlQuerySaved) {
            sparqlQuerySaved = false;
            setTitle();
          }
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
