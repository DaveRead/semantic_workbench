package com.monead.semantic.workbench;

/**
 * Properties used to configure the application. The key strings are used in the
 * properties collection.
 * 
 * @author David Read
 * 
 */
public enum ConfigurationProperty {
  /**
   * Property file property name: store the last top X location of the main
   * window
   */
  LAST_TOP_X_POSITION("LastTopPositionX"),

  /**
   * Property file property name: store the last top Y location of the main
   * window
   */
  LAST_TOP_Y_POSITION("LastTopPositionY"),

  /**
   * Property file property name: store the last width of the main window
   */
  LAST_WIDTH("LastWidth"),

  /**
   * Property file property name: store the last height of the main window
   */
  LAST_HEIGHT("LastHeight"),

  /**
   * Property file property name: store the last directory read/written
   */
  LAST_DIRECTORY("LastDirectory"),

  /**
   * Property file property name: store the last output format chosen
   */
  OUTPUT_FORMAT("OutputFormat"),

  /**
   * Property file property name: store the last model output selection chosen
   * (assertions versus assertions and inferences)
   */
  OUTPUT_CONTENT("OutputContent"),

  /**
   * Property file property name: store the last FQN display setting
   */
  SHOW_FQN_NAMESPACES("ShowFqnNamespaces"),

  /**
   * Property file property name: store the last datatype display setting
   */
  SHOW_DATATYPES_ON_LITERALS("ShowDatatypesOnLiterals"),

  /**
   * Property file property name: store the last literal flag display setting
   */
  FLAG_LITERALS_IN_RESULTS("FlagLiteralsInResults"),

  /**
   * Property file property name: store the last font setting
   */
  FONT_NAME("FontName"),

  /**
   * Property file property name: store the last font size setting
   */
  FONT_SIZE("FontSize"),

  /**
   * Property file property name: store the last font style setting
   */
  FONT_STYLE("FontStyle"),

  /**
   * Property file property name: store the last font color setting
   */
  FONT_COLOR("FontColor"),

  /**
   * Property file property name: prefix for storing each class name to be
   * skipped in the tree
   */
  PREFIX_SKIP_CLASS("TreeClassToSkip_"),

  /**
   * Property file property name: prefix for storing each predicate name to be
   * skipped in the tree
   */
  PREFIX_SKIP_PREDICATE("TreePredicateToSkip_"),

  /**
   * Maximum number of individuals to display per class
   */
  MAX_INDIVIDUALS_PER_CLASS_IN_TREE("TreeMaxIndividualsPerClass"),

  /**
   * Property file property name: store the last reasoning level setting
   */
  REASONING_LEVEL("ReasoningLevel"),

  /**
   * Property file property name: store the last assertions input format setting
   */
  INPUT_LANGUAGE("InputFormat"),

  /**
   * Property file property name: prefix for storing the set of recently
   * accessed assertion files and URLs
   */
  PREFIX_RECENT_ASSERTIONS_FILE("RecentAssertedTriplesFile_"),

  /**
   * Property file property name: prefix for storing the set of recently
   * accessed SPARQL query files
   */
  PREFIX_RECENT_SPARQL_QUERY_FILE("RecentSparqlQueryFile_"),

  /**
   * Property file property name: store the last tree view filter setting
   */
  ENFORCE_FILTERS_IN_TREE_VIEW("EnforceTreeViewFilters"),

  /**
   * Property file property name: store the last anonymous node display setting
   */
  DISPLAY_ANONYMOUS_NODES_IN_TREE_VIEW("ShowAnonymousNodesInTreeView"),

  /**
   * Property file property name: store the last strict reasoner mode setting
   */
  ENABLE_STRICT_MODE("EnableStrictMode"),

  /**
   * Property file property name: store the last SPARQL output format setting
   */
  EXPORT_SPARQL_RESULTS_FORMAT("SparqlResultsExportFormat"),

  /**
   * Property file property name: store the last SPARQL server configured port
   * setting
   */
  SPARQL_SERVER_PORT("SparqlServerPort"),

  /**
   * Property file property name: store the last SPARQL server max query runtime
   * setting
   */
  SPARQL_SERVER_MAX_RUNTIME("SparqlServerMaxRuntimeSeconds"),

  /**
   * Property file property name: store the last proxy server address setting
   */
  PROXY_SERVER("ProxyServer"),

  /**
   * Property file property name: store the last proxy server port setting
   */
  PROXY_PORT("ProxyPort"),

  /**
   * Property file property name: store the last proxy server HTTP proxied
   * setting
   */
  PROXY_HTTP("ProxyHttpRequested"),

  /**
   * Property file property name: store the last proxy server SOCKS proxied
   * setting
   */
  PROXY_SOCKS("ProxySocksRequested"),

  /**
   * Property file property name: store the last proxy server enabled setting
   */
  PROXY_ENABLED("ProxyIsEnabled"),

  /**
   * Property file property name: store the last SPARQL query user id used
   */
  SPARQL_SERVICE_USER_ID("SparqlServiceUserId"),

  /**
   * Property file property name: store the last SPARQL query default graph
   * setting
   */
  SPARQL_DEFAULT_GRAPH_URI("SparqlDefaultGraphUri"),

  /**
   * Property file property name: prefix to store the set of known SPARQL
   * service URLs
   */
  PREFIX_SPARQL_SERVICE_URL("SparqlServiceUrl_"),

  /**
   * Property file property name: store the last SPARQL query service setting
   */
  SELECTED_SPARQL_SERVICE_URL("SparqlServiceSelectedIndex"),

  /**
   * Property file property name: store the setting for writing SPARQL results
   * directly to a file
   */
  SPARQL_RESULTS_TO_FILE("SparqlResultsToFile"),

  /**
   * Property file property name: store the last SPARQL tab split pane position
   */
  SPARQL_SPLIT_PANE_POSITION("SparqlSplitPanePosition"),

  /**
   * Property file property name: store the last SPARQL query image display
   * setting
   */
  SPARQL_DISPLAY_IMAGES_IN_RESULTS("SparqlDisplayImagesInResults"),

  /**
   * Property file property name: store the last SPARQL query multiline results
   * setting
   */
  SPARQL_DISPLAY_ALLOW_MULTILINE_OUTPUT(
      "SparqlDisplayAllowMultilineOutput"),

  /**
   * Property file property name: store the sparql service remote update support
   * setting
   */
  SPARQL_SERVER_ALLOW_REMOTE_UPDATE("SparqlServerAllowRemoteUpdate"),

  /**
   * Property file property name: display the FQN of classes, properties and
   * objects in the tree view
   */
  DISPLAY_FQN_IN_TREE_VIEW("TreeShowFqn"),

  /**
   * Property file property name: prefix of the format configuration for numbers
   */
  PREFIX_NUMERIC_DATA_XSD_FORMAT_MAPPING("NumericXsdFormat_"),

  /**
   * Property file property name for controlling whether formatting is applied
   * to output data (e.g. in SPARQL and tree views)
   */
  APPLY_FORMATTING_TO_LITERAL_VALUES("ApplyFormattingToLiteralValues");

  /**
   * The key used in the properties collection
   */
  private final String key;

  /**
   * Setup a property with the supplied key
   * 
   * @param pKey
   *          The key for the property
   */
  ConfigurationProperty(String pKey) {
    key = pKey;
  }

  /**
   * Get the key for this property used in the properties collection
   * 
   * @return The key for the property
   */
  public String key() {
    return key;
  }
}
