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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.tree.*;

import org.apache.log4j.Logger;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.utils.VersionInfo;

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
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.monead.semantic.workbench.images.ImageLibrary;
import com.monead.semantic.workbench.tree.OntologyTreeCellRenderer;
import com.monead.semantic.workbench.tree.WrapperClass;
import com.monead.semantic.workbench.tree.WrapperDataProperty;
import com.monead.semantic.workbench.tree.WrapperInstance;
import com.monead.semantic.workbench.tree.WrapperLiteral;
import com.monead.semantic.workbench.tree.WrapperObjectProperty;

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
 *         TODO Add support to write inferred triples to a file
 * 
 *         TODO Add support for SPARQL queries that use the model and URLs
 * 
 *         TODO Store settings in properties file
 */
public class SemanticWorkbench extends JFrame implements Runnable,
    WindowListener {
  /**
   * The version identifier
   */
  public final static String VERSION = "1.5.2";

  /**
   * Serial UID to keep environment happy
   */
  private final static long serialVersionUID = 19991231;

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

  /**
   * The set of reasoning levels that will be compared.
   */
  protected final static String[] REASONING_LEVELS = { "None", "RDFS",
      "OWL Lite (Jena)", "OWL DL (Jena)", "OWL (Pellet)" };

  /**
   * Constant used if a value cannot be found in an array
   */
  private final static int UNKNOWN = -1;

  private final static String PROPERTIES_FILE_NAME = "semantic_workbench.properties";

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
   * Default classes to skip when creating the tree view. Overridden by settings
   * in the properties file.
   */
  private final static String[] defaultClassesToSkipInTree = {
      "http://www.w3.org/2002/07/owl#Thing",
  };

  /**
   * Default predicates to skip when creating the tree view. Overridden by
   * settings in the properties file.
   */
  private final static String[] defaultPredicatesToSkipInTree = {
      "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
      "http://www.w3.org/2002/07/owl#differentFrom",
      "http://www.w3.org/2002/07/owl#propertyDisjointWith",
      "http://www.w3.org/2002/07/owl#sameAs"
  };

  /**
   * The name (and path if necessary) to the ontology being loaded
   */
  private File rdfFile;

  /**
   * The processed ontology
   */
  private OntModel ontModel;

  /**
   * The asserted model (no inferred information)
   */
  private OntModel ontModelNoInference;

  /**
   * File open menu item
   * 
   * Used to load an ontology in to the assertions text area
   */
  private JMenuItem fileOpen;

  /**
   * File save menu item
   * 
   * Used to save the ontology text to a file
   */
  private JMenuItem fileSave;

  /**
   * File save menu item
   * 
   * Used to serialize the current model to a file
   */
  private JMenuItem fileSaveSerializedModel;

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
   * Set the font used for the major interface display widgets
   */
  private JMenuItem setupFont;

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

  private boolean replaceTreeImages = false;

  /**
   * The last directory where a file was opened or saved
   */
  private File lastDirectoryUsed;

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
   * Set up the application's UI
   */
  public SemanticWorkbench() {
    LOGGER.info("Startup");

    addWindowListener(this);
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setTitle("Semantic Workbench");

    loadProperties();

    setupGUI();
    processProperties();

    enableControls(true);
    pack();
    sizing();
    setStatus("");
    setVisible(true);
  }

  /**
   * Prevent the initial window from being too large. Constrains it
   * to a maximum of 90% of the screen height and width
   */
  private void sizing() {
    boolean resizeRequired = false;
    double height = this.getSize().getHeight();
    double width = this.getSize().getWidth();
    double screenHeight = Toolkit.getDefaultToolkit().getScreenSize()
        .getHeight();
    double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();

    if (screenHeight * .9 < height) {
      height = screenHeight * .9;
      resizeRequired = true;
    }

    if (screenWidth * .9 < width) {
      width = screenWidth * .9;
      resizeRequired = true;
    }

    if (resizeRequired) {
      this.setSize((int) width, (int) height);
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

    setFont(getFontFromProperties(), getColorFromProperties());

    extractSkipObjectsFromProperties();
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

    if (classesToSkipInTree.size() == 0) {
      addDefaultClassesToSkipInTree();
    }

    if (predicatesToSkipInTree.size() == 0) {
      addDefaultPredicatesToSkipInTree();
    }
  }

  /**
   * Add the default classes to be skipped to the configuration properties.
   */
  private void addDefaultClassesToSkipInTree() {
    int classNumber = 0;

    for (String classToSkipInTree : defaultClassesToSkipInTree) {
      ++classNumber;
      properties.put(PROP_PREFIX_SKIP_CLASS + classNumber,
          classToSkipInTree);
      classesToSkipInTree.put(classToSkipInTree, "");
    }

  }

  /**
   * Add the default predicates (properties) to be skipped to the configuration
   * properties.
   */
  private void addDefaultPredicatesToSkipInTree() {
    int propNumber = 0;
    for (String predicateToSkipInTree : defaultPredicatesToSkipInTree) {
      ++propNumber;
      properties.put(PROP_PREFIX_SKIP_PREDICATE + propNumber,
          predicateToSkipInTree);
      predicatesToSkipInTree.put(predicateToSkipInTree, "");
    }
  }

  /**
   * Save the current program configuration to the properties file.
   */
  private void saveProperties() {
    Writer writer = null;

    properties.setProperty(PROP_LAST_DIRECTORY,
        lastDirectoryUsed.getAbsolutePath());

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

    //
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
    flowPanel.add(new JLabel("Reasoning Level:"));
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
    innerGridPanel.setLayout(new GridLayout(1, 2));
    innerGridPanel.add(new JLabel("SPARQL Query"));
    flowPanel = new JPanel();
    flowPanel.setLayout(new FlowLayout());
    flowPanel.add(runSparql);
    innerGridPanel.add(flowPanel);
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
   * Setup the frame's menus
   */
  private void setupMenus() {
    JMenuBar menuBar;
    JMenu menu;
    ButtonGroup buttonGroup;

    menuBar = new JMenuBar();
    setJMenuBar(menuBar);

    menu = new JMenu("File");
    menu.setMnemonic(KeyEvent.VK_F);
    menu.getAccessibleContext().setAccessibleDescription(
        "Menu items related to file access");
    menuBar.add(menu);

    fileOpen = new JMenuItem("Open");
    fileOpen.setMnemonic('O');
    fileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
        ActionEvent.ALT_MASK));
    fileOpen.setMnemonic(KeyEvent.VK_L);
    fileOpen.getAccessibleContext().setAccessibleDescription(
        "Open an ontology file");
    fileOpen.addActionListener(new FileOpenListener());
    menu.add(fileOpen);

    menu.addSeparator();

    fileSave = new JMenuItem("Save Assertions Text");
    fileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
        ActionEvent.ALT_MASK));
    fileSave.setMnemonic(KeyEvent.VK_S);
    fileSave.getAccessibleContext().setAccessibleDescription(
        "Write the assertions to a file");
    fileSave.addActionListener(new FileSaveListener());
    menu.add(fileSave);

    fileSaveSerializedModel = new JMenuItem("Save Model");
    fileSaveSerializedModel.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_M, ActionEvent.ALT_MASK));
    fileSaveSerializedModel.setMnemonic(KeyEvent.VK_M);
    fileSaveSerializedModel.getAccessibleContext()
        .setAccessibleDescription(
            "Write an ontology file from the current model");
    fileSaveSerializedModel
        .addActionListener(new ModelSerializerListener());
    menu.add(fileSaveSerializedModel);

    menu.addSeparator();

    fileExit = new JMenuItem("Exit");
    fileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
        ActionEvent.ALT_MASK));
    fileExit.setMnemonic(KeyEvent.VK_X);
    fileExit.getAccessibleContext().setAccessibleDescription(
        "Exit the application");
    fileExit.addActionListener(new EndApplicationListener());
    menu.add(fileExit);

    // Edit Menu
    menu = new JMenu("Edit");
    menu.setMnemonic(KeyEvent.VK_E);
    menu.getAccessibleContext().setAccessibleDescription(
        "Menu items releated to editing the ontology");
    menuBar.add(menu);

    editInsertPrefixes = new JMenuItem("Insert Prefixes");
    editInsertPrefixes.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
        ActionEvent.ALT_MASK));
    editInsertPrefixes.setMnemonic(KeyEvent.VK_I);
    editInsertPrefixes.getAccessibleContext().setAccessibleDescription(
        "Insert standard prefixes (namespaces)");
    editInsertPrefixes.addActionListener(new InsertPrefixesListener());
    menu.add(editInsertPrefixes);

    menu.addSeparator();

    editExpandAllTreeNodes = new JMenuItem("Expand Entire Tree");
    editExpandAllTreeNodes.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_ADD, ActionEvent.ALT_MASK));
    editExpandAllTreeNodes.setMnemonic(KeyEvent.VK_E);
    editExpandAllTreeNodes.getAccessibleContext().setAccessibleDescription(
        "Expand all tree nodes");
    editExpandAllTreeNodes.addActionListener(new ExpandTreeListener());
    menu.add(editExpandAllTreeNodes);

    editCollapseAllTreeNodes = new JMenuItem("Collapse Entire Tree");
    editCollapseAllTreeNodes.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_SUBTRACT, ActionEvent.ALT_MASK));
    editCollapseAllTreeNodes.setMnemonic(KeyEvent.VK_C);
    editCollapseAllTreeNodes.getAccessibleContext()
        .setAccessibleDescription("Expand all tree nodes");
    editCollapseAllTreeNodes.addActionListener(new CollapseTreeListener());
    menu.add(editCollapseAllTreeNodes);

    // Setup Menu
    menu = new JMenu("Setup");
    menu.setMnemonic(KeyEvent.VK_S);
    menu.getAccessibleContext().setAccessibleDescription(
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

    setupFont = new JMenuItem("Font");
    setupFont.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
        ActionEvent.ALT_MASK));
    setupFont.setMnemonic(KeyEvent.VK_F);
    setupFont.getAccessibleContext().setAccessibleDescription(
        "Set the font used for the display");
    setupFont.addActionListener(new FontSetupListener());
    menu.add(setupFont);

    // Model Menu
    menu = new JMenu("Model");
    menu.setMnemonic(KeyEvent.VK_M);
    menu.getAccessibleContext().setAccessibleDescription(
        "Menu items releated to viewing the model");
    menuBar.add(menu);

    modelCreateTreeView = new JMenuItem("Create Tree");
    modelCreateTreeView.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
        ActionEvent.ALT_MASK));
    modelCreateTreeView.setMnemonic(KeyEvent.VK_T);
    modelCreateTreeView.getAccessibleContext().setAccessibleDescription(
        "Create tree representation of current model");
    modelCreateTreeView.addActionListener(new GenerateTreeListener());
    menu.add(modelCreateTreeView);

    modelListInferredTriples = new JMenuItem("Identify Inferred Triples");
    modelListInferredTriples.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_I,
        ActionEvent.ALT_MASK));
    modelListInferredTriples.setMnemonic(KeyEvent.VK_I);
    modelListInferredTriples.getAccessibleContext().setAccessibleDescription(
        "Create a list of inferred triples from the current model");
    modelListInferredTriples
        .addActionListener(new GenerateInferredTriplesListener());
    menu.add(modelListInferredTriples);

    // Help Menu
    menu = new JMenu("Help");
    menu.setMnemonic(KeyEvent.VK_H);
    menu.getAccessibleContext().setAccessibleDescription(
        "Menu items releated to user assistance");
    menuBar.add(menu);

    helpAbout = new JMenuItem("About");
    helpAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
        ActionEvent.ALT_MASK));
    helpAbout.setMnemonic(KeyEvent.VK_H);
    helpAbout.getAccessibleContext().setAccessibleDescription(
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
    fileOpen.setEnabled(enable);

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
      fileSave.setEnabled(true);
    } else {
      runInferencing.setEnabled(false);
      fileSave.setEnabled(false);
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
  }

  /**
   * Setup all the components
   */
  private void setupControls() {
    LOGGER.debug("setupControls");

    reasoningLevel = new JComboBox();
    for (String level : REASONING_LEVELS) {
      reasoningLevel.addItem(level);
    }
    reasoningLevel.setSelectedIndex(reasoningLevel.getItemCount() - 1);

    language = new JComboBox();
    language.addItem("Auto");
    for (String lang : FORMATS) {
      language.addItem(lang);
    }
    language.setSelectedIndex(0);

    runInferencing = new JButton("Run Reasoner");
    runInferencing.addActionListener(new ReasonerListener());

    runSparql = new JButton("Run SPARQL");
    runSparql.addActionListener(new SparqlListener());

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

    // Determine whether alternate tree icons exist
    if (ImageLibrary.instance().getIcon(ImageLibrary.ICON_TREE_CLASS) != null) {
      replaceTreeImages = true;
    }

    LOGGER.debug("Tree renderer, specialized icons available? "
        + replaceTreeImages);
    ontModelTree = new JTree(new DefaultTreeModel(
        new DefaultMutableTreeNode("No Tree Generated")));

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
  private void expandAll() {
    for (int row = 0; row < ontModelTree.getRowCount(); ++row) {
      ontModelTree.expandRow(row);
    }
  }

  /**
   * Collapse all the nodes in the tree representation of the model
   */
  private void collapseAll() {
    for (int row = 0; row < ontModelTree.getRowCount(); ++row) {
      ontModelTree.collapseRow(row);
    }
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
      } else {
        JOptionPane.showMessageDialog(this,
            "An instruction to execute a task was received\n"
                + "but the task was undefined.", "Error: No Process to Run",
            JOptionPane.ERROR_MESSAGE);
      }
    } catch (Throwable throwable) {
      setStatus("Error: " + throwable.getClass().getName() + ": "
          + throwable.getMessage());
      LOGGER.error("Failed during execution", throwable);
      JOptionPane.showMessageDialog(this, "Error: "
          + throwable.getClass().getName() + "\n\n"
          + throwable.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
    } finally {
      setWaitCursor(false);
      enableControls(true);
      if (finalStatus != null) {
        setStatus(finalStatus);
      } else {
        setStatus("");
      }
      isBuildingAssertionsList = isBuildingTree = isRunReasoner = isRunSparql = false;
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
    boolean okToRun = !isBuildingAssertionsList && !isBuildingTree
        && !isRunReasoner && !isRunSparql;

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

  /**
   * Execute the steps to run the reasoner
   */
  private String reasonerExecution() {
    setStatus("Running reasoner...");
    inferredTriples.setText("");
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
    return getArrayAsCSV(REASONING_LEVELS);
  }

  /**
   * Create a CSV list from a String array
   * 
   * @param array
   *          An array
   * @return The array values in a CSV list
   */
  public final static String getArrayAsCSV(String[] array) {
    StringBuffer csv;

    csv = new StringBuffer();

    for (String value : array) {
      if (csv.length() > 0) {
        csv.append(", ");
      }
      csv.append(value);
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

    return "Listing of inferred triples created in " + assertionLanguage;
  }

  /**
   * Create a model with a reasoner set based on the chosen reasoning level.
   * 
   * @param reasoningLevel
   *          The reasoning level for this model
   * 
   * @return The created ontology model
   */
  private OntModel createModel(String reasoningLevelName) {
    OntModel model;
    int reasoningLevelIndex;

    model = null;

    LOGGER.debug("Create model using reasoning level: "
        + reasoningLevelName);

    reasoningLevelIndex = getReasoningLevelIndex(reasoningLevelName);

    LOGGER.debug("Reasoning level index: " + reasoningLevelIndex);
    if (reasoningLevelIndex == 0) { // None
      model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
    } else if (reasoningLevelIndex == 1) { // RDFS
      model = ModelFactory
          .createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);
    } else if (reasoningLevelIndex == 2) { // OWL Lite (Jena)
      model = ModelFactory
          .createOntologyModel(OntModelSpec.OWL_LITE_MEM_TRANS_INF);
    } else if (reasoningLevelIndex == 3) { // OWL DL (Jena)
      model = ModelFactory
          .createOntologyModel(OntModelSpec.OWL_DL_MEM_TRANS_INF);
    } else if (reasoningLevelIndex == 4) { // OWL (Pellet)
      // Reasoner reasoner = PelletReasonerFactory.theInstance().create();
      // Model infModel = ModelFactory.createInfModel(reasoner, ModelFactory
      // .createDefaultModel());
      // model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM,
      // infModel);

      // create an empty ontology model using Pellet spec
      model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

    }

    return model;
  }

  /**
   * Obtain a text file containing a set of assertions. Load the assertions
   * into the ontology model.
   */
  private void loadModel() {
    String modelFormat;

    modelFormat = null;

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
      ontModelNoInference = createModel("NONE");
      ontModelNoInference.read(inputStream, null, format.toUpperCase());

      LOGGER.debug("Start " + reasoningLevel.getSelectedItem().toString()
          + " model load and setup");
      inputStream = new ByteArrayInputStream(assertions.getText()
          .getBytes("UTF-8"));
      ontModel = createModel(reasoningLevel.getSelectedItem().toString());
      ontModel.read(inputStream, null, format.toUpperCase());
      LOGGER.debug(reasoningLevel.getSelectedItem().toString()
          + " model load and setup completed");

      // TODO Make Tree Optional
      // createTreeFromModel();
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
    DefaultMutableTreeNode treeTopNode;
    DefaultMutableTreeNode classesNode;
    DefaultMutableTreeNode oneClassNode;
    DefaultMutableTreeNode oneIndividualNode;
    DefaultMutableTreeNode onePropertyNode;
    OntClass ontClass;
    Individual individual;
    Statement statement;
    Property property;
    RDFNode rdfNode;
    ExtendedIterator<OntClass> classesIterator;
    ExtendedIterator<Individual> individualsIterator;
    StmtIterator stmtIterator;

    setStatus("Creating the tree view of the model...");
    setWaitCursor(true);

    treeTopNode = new DefaultMutableTreeNode("Model");

    // Classes
    classesNode = new DefaultMutableTreeNode("Classes");
    treeTopNode.add(classesNode);

    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Building list of classes in the model");
    }
    classesIterator = ontModel.listClasses();
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("List of classes built");
    }
    while (classesIterator.hasNext()) {
      ontClass = classesIterator.next();

      // Check whether class is to be skipped
      if (classesToSkipInTree.get(ontClass.getURI()) != null) {
        LOGGER.debug("Class to be skipped: " + ontClass.getURI());
        continue;
      }

      if (ontClass.isAnon()) {
        // oneClassNode = new DefaultMutableTreeNode("Anonymous Class ("
        // + ontClass.getId().getLabelString() + ")");
        LOGGER.debug("Skip anonymous class: "
            + ontClass.getId().getLabelString());
        continue;
      } else {
        oneClassNode = new DefaultMutableTreeNode(new WrapperClass(ontClass
            .getLocalName()
            + " (" + ontClass.getURI() + ")"));
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
        LOGGER.trace("List of individuals built for " + ontClass.getURI());
      }
      while (individualsIterator.hasNext()) {
        individual = individualsIterator.next();
        if (individual.isAnon()) {
          continue;
          // oneIndividualNode = new DefaultMutableTreeNode(
          // "Anonymous Individual ("
          // + individual.getId().getLabelString() + ")");
        } else {
          oneIndividualNode = new DefaultMutableTreeNode(new WrapperInstance(
              individual
                  .getLocalName()
                  + " (" + individual.getURI() + ")"));
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
        while (stmtIterator.hasNext()) {
          statement = stmtIterator.next();
          property = statement.getPredicate();

          // Check whether predicate is to be skipped
          if (predicatesToSkipInTree.get(property.getURI()) != null) {
            // LOGGER.debug("Predicate to be skipped: " + property.getURI());
            continue;
          }

          rdfNode = statement.getObject();
          if (property.isAnon()) {
            continue;
            // onePropertyNode = new DefaultMutableTreeNode(
            // "Anonymous Property ("
            // + property.getId().getLabelString()
            // + ")");
          } else {
            if (rdfNode.isLiteral()) {
              onePropertyNode = new DefaultMutableTreeNode(
                  new WrapperDataProperty(property
                      .getLocalName()
                      + " (" + property.getURI() + ")"));
            } else {
              onePropertyNode = new DefaultMutableTreeNode(
                  new WrapperObjectProperty(property
                      .getLocalName()
                      + " (" + property.getURI() + ")"));
            }
          }
          oneIndividualNode.add(onePropertyNode);

          if (rdfNode.isLiteral()) {
            // onePropertyNode.add(new DefaultMutableTreeNode(
            // statement.getString() + " (Literal)"));
            onePropertyNode.add(new DefaultMutableTreeNode(new WrapperLiteral(
                statement.getString())));
          } else {
            onePropertyNode.add(new DefaultMutableTreeNode(new WrapperInstance(
                statement.getResource().getLocalName() + " ("
                    + statement.getResource().getURI()
                    + ")")));

          }
        }
      }
    }

    ontModelTree.setModel(new DefaultTreeModel(treeTopNode));
    LOGGER.debug("Tree representation of model created");

    return "Tree view of current model created";
  }

  /**
   * Get the index position of the supplied reasoning level label
   * 
   * @param reasonerName
   *          A reasoning level label
   * 
   * @return The index position of the reasoning level. Will be equal to the
   *         constant UNKNOWN if the value cannot be found in the collection
   *         of known reasoning levels
   */
  public final static int getReasoningLevelIndex(String reasonerName) {
    return getIndexValue(REASONING_LEVELS, reasonerName);
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
      lastDirectoryUsed = chosenFile.getParentFile();
    } else {
      JOptionPane.showMessageDialog(this, "No file to load",
          "No File Selected", JOptionPane.WARNING_MESSAGE);
    }
    enableControls(true);
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
   * Writes the triples to a data file.
   * 
   * This writes the current model to the file. The current model is the last
   * model successfully run through the reasoner. The output will be in the
   * same language (N3, Turtle, RDF/XML) as the assertions input unless a
   * specific language was set for output.
   * 
   * @see saveAssertionsToFile
   */
  private void writeOntologyModel() {
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

      LOGGER.info("Write model to file, " + destinationFile
          + ", in format: " + assertionLanguage);

      try {
        out = new FileWriter(destinationFile, false);
        if (setupOutputModelTypeAssertionsAndInferences.isSelected()) {
          LOGGER
              .info("Writing complete model (assertions and inferences)");
          ontModel.write(out, getSelectedOutputLanguage()
              .toUpperCase());
        } else {
          LOGGER.info("Writing assertions only");
          ontModelNoInference.write(out, getSelectedOutputLanguage()
              .toUpperCase());
        }
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
   * Presents a classic "About" box to the user displaying the current version
   * of the application.
   */
  private void about() {
    StringBuffer message;

    message = new StringBuffer();

    message.append("Semantic Workbench\n\nVersion:");
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
   * Executes the reasoner in response to action from any widget being
   * monitored
   */
  private class ReasonerListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      runReasoner();
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
  private class FileOpenListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      openOntologyFile();
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
  private class FileSaveListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      saveAssertionsToFile();
    }
  }

  /**
   * Expands the nodes in the tree representation of the model
   */
  private class ExpandTreeListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      expandAll();
    }
  }

  /**
   * Collapses the nodes in the tree representation of the model
   */
  private class CollapseTreeListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      collapseAll();
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
      writeOntologyModel();
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
   * @param pColor The foreground Color
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