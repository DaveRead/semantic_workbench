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
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
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
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * SemanticWorkbench - A GUI to input assertions, run an inferencing engine and use
 * SPARQL queries on the resulting model
 * 
 * This program uses Jena and Pellet to provide the inference support.
 * 
 * Copyright (C) 2010 David S. Read
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
public class SemanticWorkbench extends JFrame implements Runnable, WindowListener {
	/**
	 * The version identifier
	 */
	public final static String VERSION = "1.2";

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
	protected final static String[] REASONING_LEVELS = { "none", "rdfs", "owl" };

	/**
	 * Constant used if a value cannot be found in an array
	 */
	private final static int UNKNOWN = -1;

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
	 * The results from running the inferencing engine
	 */
	private JTextArea results;

	/**
	 * The resulting model displayed as a tree
	 */
	private JTree ontModelTree;

	/**
	 * Should the reasoner be run (otherwise SPARQL query is being run)
	 * 
	 * TODO prevent collision (e.g. lock execution)
	 */
	private boolean isRunReasoner;

	/**
	 * Constructor - sets up the UI
	 */
	public SemanticWorkbench() {
		LOGGER.info("Startup");

		addWindowListener(this);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("Semantic UI");
		setupGUI();
		enableControls(true);
		pack();
		setStatus("");
		setVisible(true);
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

		// Tree
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
		FontMetrics fontMetrics;

		chooser = new FontChooser(this);

		LOGGER.debug("Font before choices: " + chooser.getNewFont());

		chooser.setVisible(true);
		newFont = chooser.getNewFont();
		newColor = chooser.getNewColor();

		LOGGER.debug("Font after choices: " + newFont);

		if (newFont != null) {
			assertions.setFont(newFont);
			results.setFont(newFont);
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
			results.setForeground(newColor);
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
		inferencesPanel.add(new JScrollPane(results));

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

		// SPARQL Input
		labelPanel = new JPanel();
		labelPanel.setLayout(new BorderLayout());
		gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(2, 1));

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

		labelPanel.add(gridPanel, BorderLayout.NORTH);
		labelPanel.add(new JScrollPane(sparqlInput), BorderLayout.CENTER);
		sparqlPanel.add(labelPanel);

		// SPARQL results
		labelPanel = new JPanel();
		labelPanel.setLayout(new BorderLayout());
		labelPanel.add(new JLabel("Results"), BorderLayout.NORTH);
		// labelPanel.add(new JScrollPane(sparqlResults), BorderLayout.CENTER);
		labelPanel
				.add(new JScrollPane(sparqlResultsTable), BorderLayout.CENTER);
		sparqlPanel.add(labelPanel);

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

		setupFont = new JMenuItem("Font");
		setupFont.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				ActionEvent.ALT_MASK));
		setupFont.setMnemonic(KeyEvent.VK_F);
		setupFont.getAccessibleContext().setAccessibleDescription(
				"Set the font used for the display");
		setupFont.addActionListener(new FontSetupListener());
		menu.add(setupFont);

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
	 *            Whether to enable or disable controls
	 */
	private void enableControls(boolean enable) {
		String sparqlService;

		LOGGER.debug("Called enableControls with setting " + enable);

		assertions.setEditable(enable);
		sparqlInput.setEditable(enable);
		fileOpen.setEnabled(enable);

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

		results = new JTextArea(10, 50);
		results.setEditable(false);

		// SPARQL Input
		sparqlInput = new JTextArea(10, 50);
		sparqlInput.addKeyListener(new UserInputListener());

		sparqlServiceUrl = new JComboBox();
		sparqlServiceUrl.setEditable(true);
		sparqlServiceUrl.addItem("Local Model");
		sparqlServiceUrl.addItem("http://DBpedia.org/sparql");
		sparqlServiceUrl.addItem("http://lod.openlinksw.com/sparql/");
		sparqlServiceUrl.addItem("http://semantic.data.gov/sparql");
		sparqlServiceUrl
				.addItem("http://www4.wiwiss.fu-berlin.de/gutendata/sparql");

		sparqlServiceUrl.addActionListener(new SparqlModelChoiceListener());
		sparqlServiceUrl.getEditor().getEditorComponent().addKeyListener(
				new UserInputListener());

		// A basic default query
		sparqlInput.setText("select ?s ?p ?o where { ?s ?p ?o }");

		sparqlResultsTable = new JTable(new SparqlTableModel());

		ontModelTree = new JTree(new DefaultTreeModel(
				new DefaultMutableTreeNode("No Model Created")));

		status = new JLabel("Initializing");
		status.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
	}

	/**
	 * Create a JPanel that uses a FlowLayout and add a component to the JPanel.
	 * The method creates a JPanel, sets its layout to FlowLayout and adds the
	 * supplied component to itself.
	 * 
	 * @param component
	 *            The component to be placed using a FlowLayout
	 * @param alignment
	 *            How to align the component. Use a FlowLayout constant.
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
	 *            The message to place in the status field
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
	 *            Whether to display the system default wait cursor
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
	 * Run the reasoner or the SPARQL query
	 */
	public void run() {
		enableControls(false);

		try {
			setStatus("Running...");
			setWaitCursor(true);
			if (isRunReasoner) {
				reasonerExecution();
			} else {
				sparqlExecution();
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
		}
	}

	/**
	 * Setup to run the reasoner and start a thread
	 */
	private void runReasoner() {
		isRunReasoner = true;
		new Thread(this).start();
	}

	/**
	 * Setup to run the SPARQL query and start a thread
	 */
	private void runSPARQL() {
		isRunReasoner = false;
		new Thread(this).start();

	}

	/**
	 * Execute the steps to run the reasoner
	 */
	private void reasonerExecution() {
		setStatus("Running reasoner");
		results.setText("");
		loadModel();
		outputResults();
		setStatus("Ready");
	}

	/**
	 * Execute the steps to run the SPARQL query
	 */
	private void sparqlExecution() {
		int numResults;

		setStatus("Running SPARQL");
		setWaitCursor(true);
		// sparqlResults.setText("");
		numResults = callSparqlEngine();

		setStatus("Number of Results: " + numResults);
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
		Query query = QueryFactory.create(queryString);

		serviceUrl = ((String) sparqlServiceUrl.getSelectedItem()).trim();

		// Execute the query and obtain results
		if (sparqlServiceUrl.getSelectedIndex() == 0
				|| serviceUrl.length() == 0) {
			qe = QueryExecutionFactory.create(query, ontModel);
		} else {
			qe = QueryExecutionFactory.sparqlService(serviceUrl, query);
		}
		resultSet = qe.execSelect();

		SparqlTableModel tableModel = (SparqlTableModel) sparqlResultsTable
				.getModel();
		tableModel.setupModel(resultSet);

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
	 *            An array
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
	 *            The file containing the ontology
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

		title = "Semantic UI";

		if (assertionLanguage != null) {
			title += " - " + assertionLanguage;
		}

		if (rdfFile != null) {
			title += " - " + rdfFile.getName();
		}

		setTitle(title);
	}

	/**
	 * Convert the ontology into a set of Strings representing the triples
	 * 
	 * @return A Map containing Lists that relate subjects to objects and
	 *         predicates
	 */
	private void outputResults() {
		StringWriter writer;
		Model tempModel;

		LOGGER.debug("ontModelNoInference [" + ontModelNoInference + "]");
		LOGGER.debug("ontModel [" + ontModel + "]");
		tempModel = ontModel.difference(ontModelNoInference);

		writer = new StringWriter();
		tempModel.write(writer, assertionLanguage);

		results.setText(writer.toString());
	}

	/**
	 * Create a model with a reasoner set based on the chosen reasoning level.
	 * 
	 * @param reasoningLevel
	 *            The reasoning level for this model
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
		} else if (reasoningLevelIndex == 2) { // OWL
			Reasoner reasoner = PelletReasonerFactory.theInstance().create();
			Model infModel = ModelFactory.createInfModel(reasoner, ModelFactory
					.createDefaultModel());
			model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM,
					infModel);
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
	 *            The format to use, must be a value in the array FORMATS
	 * @throws UnsupportedEncodingException
	 *             If the assertions cannot be loaded
	 */
	private void tryFormat(String format) throws UnsupportedEncodingException {
		InputStream inputStream = null;

		try {
			inputStream = new ByteArrayInputStream(assertions.getText()
					.getBytes("UTF-8"));
			ontModelNoInference = createModel("NONE");
			ontModelNoInference.read(inputStream, null, format.toUpperCase());

			inputStream = new ByteArrayInputStream(assertions.getText()
					.getBytes("UTF-8"));
			ontModel = createModel(reasoningLevel.getSelectedItem().toString());
			ontModel.read(inputStream, null, format.toUpperCase());

			createTreeFromModel();
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
	 * TODO aggregate items from duplicate nodes
	 */
	private void createTreeFromModel() {
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

		treeTopNode = new DefaultMutableTreeNode("Model");

		// Classes
		classesNode = new DefaultMutableTreeNode("Classes");
		treeTopNode.add(classesNode);

		classesIterator = ontModel.listClasses();
		while (classesIterator.hasNext()) {
			ontClass = classesIterator.next();
			if (ontClass.isAnon()) {
				oneClassNode = new DefaultMutableTreeNode("Anonymous Class ("
						+ ontClass.getId().getLabelString() + ")");
			} else {
				oneClassNode = new DefaultMutableTreeNode(ontClass
						.getLocalName()
						+ " (" + ontClass.getURI() + ")");
			}
			classesNode.add(oneClassNode);

			// Individuals
			individualsIterator = ontModel.listIndividuals(ontClass);
			while (individualsIterator.hasNext()) {
				individual = individualsIterator.next();
				if (individual.isAnon()) {
					oneIndividualNode = new DefaultMutableTreeNode(
							"Anonymous Individual ("
									+ individual.getId().getLabelString() + ")");
				} else {
					oneIndividualNode = new DefaultMutableTreeNode(individual
							.getLocalName()
							+ " (" + individual.getURI() + ")");
				}
				oneClassNode.add(oneIndividualNode);

				// Properties (predicates) and Objects
				stmtIterator = individual.listProperties();
				while (stmtIterator.hasNext()) {
					statement = stmtIterator.next();
					property = statement.getPredicate();
					rdfNode = statement.getObject();
					if (property.isAnon()) {
						onePropertyNode = new DefaultMutableTreeNode(
								"Anonymous Property ("
										+ property.getId().getLabelString()
										+ ")");
					} else {
						onePropertyNode = new DefaultMutableTreeNode(property
								.getLocalName()
								+ " (" + property.getURI() + ")");
					}
					oneIndividualNode.add(onePropertyNode);

					if (rdfNode.isLiteral()) {
						onePropertyNode.add(new DefaultMutableTreeNode(
								"Literal (" + statement.getString() + ")"));
					} else {
						onePropertyNode.add(new DefaultMutableTreeNode(
								statement.getResource().getLocalName() + " ("
										+ statement.getResource().getURI()
										+ ")"));

					}
				}
			}
		}

		ontModelTree.setModel(new DefaultTreeModel(treeTopNode));
	}

	/**
	 * Get the index position of the supplied reasoning level label
	 * 
	 * @param reasonerName
	 *            A reasoning level label
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
	 *            An array of string to search
	 * @param name
	 *            The value to find in the array
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

		fileChooser = new JFileChooser(new File("."));

		fileChooser.showOpenDialog(this);
		chosenFile = fileChooser.getSelectedFile();

		if (chosenFile != null) {
			loadOntologyFile(chosenFile);
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
	 *            The file to load (should be an ontology)
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

		fileChooser = new JFileChooser();

		if (rdfFile != null) {
			fileChooser.setSelectedFile(rdfFile);
		} else {
			fileChooser.setSelectedFile(new File("."));
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

		fileChooser = new JFileChooser();

		if (rdfFile != null) {
			fileChooser.setSelectedFile(rdfFile);
		} else {
			fileChooser.setSelectedFile(new File("."));
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

		message.append("Semantic UI\n\nVersion:");
		message.append(VERSION);
		message.append("\n\nDavid Read\n\n");
		message.append("Jena Version: ");
		message.append(getJenaVersion());
		message.append("\n");
		message.append("Pellet Version: ");
		message.append(getPelletVersion());

		JOptionPane.showMessageDialog(this, message.toString(),
				"About Semantic UI", JOptionPane.INFORMATION_MESSAGE);
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
		setVisible(false);
		LOGGER.info("Shutdown");
		System.exit(0);
	}

	public void windowActivated(WindowEvent arg0) {
	}

	public void windowClosed(WindowEvent arg0) {
	}

	public void windowClosing(WindowEvent arg0) {
		closeApplication();
	}

	public void windowDeactivated(WindowEvent arg0) {
	}

	public void windowDeiconified(WindowEvent arg0) {
	}

	public void windowIconified(WindowEvent arg0) {
	}

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
	 *            The array of input arguments, not used yet
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
	 *            A Jena query result set
	 */
	public SparqlTableModel(ResultSet results) {
		setupModel(results);
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
	 *            A Jena query result set
	 */
	public void setupModel(ResultSet results) {
		rows.clear();
		columnLabels.clear();

		while (results.hasNext()) {
			QuerySolution solution = results.next();

			if (columnLabels.size() == 0) {
				Iterator<String> names;
				names = solution.varNames();
				while (names.hasNext()) {
					columnLabels.add(names.next().toString());
					LOGGER.debug("Added column label: "
							+ columnLabels.get(columnLabels.size() - 1));
				}
			}

			List<String> row = new ArrayList<String>();

			for (String var : columnLabels) {
				if (solution.get(var).isLiteral()) {
					row.add("Lit: " + solution.getLiteral(var));
				} else {
					row.add(solution.getResource(var).toString());
				}
			}

			rows.add(row);
			LOGGER.debug("Added row with col count: " + row.size());
		}

		LOGGER.debug("Total rows in results: " + rows.size());

		fireTableStructureChanged();
	}

	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnLabels.size();
	}

	public int getRowCount() {
		// TODO Auto-generated method stub
		return rows.size();
	}

	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return rows.get(arg0).get(arg1);
	}

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
class FontChooser extends JDialog implements Runnable, ActionListener, KeyListener {

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

	// Ok, something in the font changed, so figure that out and make a
	// new font for the preview label
	public void actionPerformed(ActionEvent ae) {
		fontChanged();
	}
	
	private void fontChanged() {
		System.out.println("0: " + new java.util.Date());
		updatePreviewFont();
		System.out.println("0.1: " + new java.util.Date());
		showPreview();
		System.out.println("0.2: " + new java.util.Date());
	}

	// Get the appropriate font from our attributes object and update
	// the preview label
	protected void updatePreviewFont() {
		//String name = StyleConstants.getFontFamily(attributes);
		//boolean bold = StyleConstants.isBold(attributes);
		//boolean ital = StyleConstants.isItalic(attributes);
		//int size = StyleConstants.getFontSize(attributes);

		System.out.println("5: " + new java.util.Date());
		String name = (String)fontName.getSelectedItem();
		boolean bold = fontBold.isSelected();
		boolean ital = fontItalic.isSelected();
		int size;
		try {
			size = Integer.parseInt(fontSize.getText());
		}
		catch (Throwable throwable) {
			size = 12;	// Default
			System.out.println("Not a legitimate number for the font size");
			throwable.printStackTrace();
		}
		
		// Bold and italic don’t work properly in beta 4.
		System.out.println("5.1: " + new java.util.Date());
		Font f = new Font(name, (bold ? Font.BOLD : 0)
				+ (ital ? Font.ITALIC : 0), size);
		// previewLabel.setFont(f);
		System.out.println("6: " + new java.util.Date());
		newFont = f;
	}

	// Get the appropriate color from our chooser and update previewLabel
	protected void updatePreviewColor() {
		// previewLabel.setForeground(colorChooser.getColor());
		System.out.println("7: " + new java.util.Date());
		newColor = colorChooser.getColor();
		//System.out.println("New Color: " + newColor);
		// Manually force the label to repaint
		// previewLabel.repaint();
		showPreview();
	}

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
/*				for (int x = 0; x < 10; ++x) {
					previewLabel.setText("Sample Text Message (" + x + ")");
					try {
						Thread.sleep(500);
					} catch (InterruptedException ie) {
						System.out.println("Interrupted exception");
						ie.printStackTrace();
					}
				} */
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

	public Font getNewFont() {
		return newFont;
	}

	public Color getNewColor() {
		return newColor;
	}

	public AttributeSet getAttributes() {
		return attributes;
	}

	public void closeAndSave() {
		// Save font & color information
		// newFont = previewLabel.getFont();
		// newColor = previewLabel.getForeground();

		// Close the window
		setVisible(false);
	}

	public void closeAndCancel() {
		// Erase any font information and then close the window
		newFont = null;
		newColor = null;
		setVisible(false);
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyReleased(KeyEvent e) {
        //if (key.getKeyCode() == 127) { // Delete key - no keyTyped Event
		System.out.println("10: " + new java.util.Date());
		fontChanged();
		System.out.println("10.1: " + new java.util.Date());
        //}
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}

class FontData {
	private Font font;
	private Color color;

	public FontData(Font font, Color color) {
		setFont(font);
		setColor(color);
	}

	private void setFont(Font pFont) {
		font = pFont;
	}

	public Font getFont() {
		return font;
	}

	private void setColor(Color pColor) {
		color = pColor;
	}

	public Color getColor() {
		return color;
	}
}