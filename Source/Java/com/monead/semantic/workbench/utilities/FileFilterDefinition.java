package com.monead.semantic.workbench.utilities;

/**
 * The file filter pattern definitions used within the program
 * 
 * @author David Read
 * 
 */
public enum FileFilterDefinition {
  /**
   * N3 Files
   */
  ONTOLOGY_N3("N3 Files",
      new String[] {
        ".n3"
      }, false),

  /**
   * RDF/XML Files
   */
  ONTOLOGY_RDFXML("RDF/XML Files",
      new String[] {
        ".xml"
      }, false),

  /**
   * Turtle Files
   */
  ONTOLOGY_TURTLE("Turtle Files",
      new String[] {
          ".ttl", ".turtle"
      }, true),

  /**
   * RDF/XML Files
   */
  SPARQL("SPARQL Query Files",
      new String[] {
          ".rq", ".sparql"
      }, true);

  /**
   * The description of the file filter
   */
  private final String description;

  /**
   * The accepted file name suffixes for the file filter
   */
  private final String[] acceptedSuffixes;

  /**
   * Is this the preferred option within its set of patterns
   */
  private boolean isPreferredOption;

  /**
   * Create a file filter
   * 
   * @param pDescription
   *          The description of the filter
   * @param pAcceptedSuffixes
   *          The accepted file name suffixes for the filter
   * @param pIsPreferredOption
   *          Is this the preferred filter within the related group of filters
   */
  FileFilterDefinition(String pDescription, String[] pAcceptedSuffixes,
      boolean pIsPreferredOption) {
    description = pDescription;
    acceptedSuffixes = pAcceptedSuffixes;
    isPreferredOption = pIsPreferredOption;
  }

  /**
   * Get the description for the file filter
   * 
   * @return The file filter description
   */
  public String description() {
    return description;
  }

  /**
   * Get the accepted file name suffixes for the filter
   * 
   * @return The file name suffixes
   */
  public String[] acceptedSuffixes() {
    return acceptedSuffixes;
  }

  /**
   * Determine whether this is the preferred (default) option for a group of
   * file filters
   * 
   * @return True if this filter is preferred
   */
  public boolean isPreferredOption() {
    return isPreferredOption;
  }
}
