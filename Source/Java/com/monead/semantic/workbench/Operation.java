package com.monead.semantic.workbench;

public enum Operation {
  LOAD_ASSERTIONS ("loading assertions from file"),
  IDENTIFY_ASSERTIONS ("creating the assertions list"),
  BUILD_TREE_VIEW ("building the tree view"),
  CREATE_MODEL ("reasoning over the ontology"),
  EXECUTE_SPARQL ("executing SPARQL query"),
  EXPORT_MODEL ("exporting the model to a file"),
  EXPORT_SPARQL_RESULTS ("exporting the SPARQL results to a file"),
  EXPAND_TREE ("expanding all tree nodes"),
  COLLAPSE_TREE ("collapsing all tree nodes");
  
  private final String description;
  
  Operation(String description) {
    this.description = description;
  }
  
  public String description() {
    return description;
  }
}
