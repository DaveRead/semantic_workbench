package com.monead.semantic.workbench;

/**
 * Thread-based operations supported by the program.
 * 
 * @author David Read
 * 
 */
public enum Operation {
  /**
   * Loading assertions
   */
  LOAD_ASSERTIONS("loading assertions from a file"),

  /**
   * Identifying the inferred triples
   */
  IDENTIFY_ASSERTIONS("creating the assertions list"),

  /**
   * Creating the tree view of the model
   */
  BUILD_TREE_VIEW("building the tree view"),

  /**
   * Creating the model (running the reasoner)
   */
  CREATE_MODEL("reasoning over the ontology"),

  /**
   * Executing a SPARQL query
   */
  EXECUTE_SPARQL("executing a SPARQL query"),

  /**
   * Exporting a model to a serialization file
   */
  EXPORT_MODEL("exporting the model to a file"),

  /**
   * Exporting SPARQL results to a file
   */
  EXPORT_SPARQL_RESULTS("exporting the SPARQL results to a file"),

  /**
   * Expanding the nodes of the tree
   */
  EXPAND_TREE("expanding all tree nodes"),

  /**
   * Collapsing the nodes of the tree
   */
  COLLAPSE_TREE("collapsing all tree nodes");

  /**
   * The human-readable description of the operation
   */
  private final String description;

  /**
   * Setup the enum with its arguments.
   * 
   * @param pDescription
   *          The description of the operation
   */
  Operation(String pDescription) {
    description = pDescription;
  }

  /**
   * Get the description of the operation.
   * 
   * @return The description of the operation
   */
  public String description() {
    return description;
  }
}
