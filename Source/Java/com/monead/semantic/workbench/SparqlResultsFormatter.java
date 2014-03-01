package com.monead.semantic.workbench;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;

/**
 * Formats SPARQL results using the configuration settings sent to the
 * constructor.
 */
public class SparqlResultsFormatter {
  /**
   * Logger Instance
   */
  private static Logger LOGGER = Logger.getLogger(SparqlResultsFormatter.class);

  /**
   * The original SPARQL query used to produce the results being formatted. This
   * may be null.
   */
  private Query query;

  /**
   * The ontological model against which the SPARQL query was run. This may be
   * null.
   */
  private OntModel ontModel;

  /**
   * Set if literals are to be prefixed with an identification string of "Lit:"
   */
  private boolean addLiteralFlag;

  /**
   * Whether the datatype is to be appended to literal values.
   */
  private boolean includeDataType;

  /**
   * Whether the fully qualified namespace is to be shown or if a prefix (if
   * defined) should be displayed instead
   */
  private boolean useFqn;

  /**
   * Setup a formatter for a specific query and model.
   * 
   * @param query
   *          The SPARQL query used to obtain the results being formatted. May
   *          be null.
   * @param ontModel
   *          The ontological model against which the SPARQL query was run. May
   *          be null.
   * @param addLiteralFlag
   *          Whether literal values should be prepended with a "Lit:"
   *          identifier
   * @param includeDataType
   *          Whether literals should have their data type appended to their
   *          value
   * @param useFqn
   *          Whether the fully qualified namespace should be used. Otherwise a
   *          prefix will be used if defined for the namespace
   */
  public SparqlResultsFormatter(Query query, OntModel ontModel,
      boolean addLiteralFlag, boolean includeDataType,
      boolean useFqn) {
    this.query = query;
    this.ontModel = ontModel;
    this.addLiteralFlag = addLiteralFlag;
    this.includeDataType = includeDataType;
    this.useFqn = useFqn;
  }

  /**
   * Format the value.
   * 
   * @param solution
   *          The current query solution to be formatted
   * @param variableName
   *          The bound variable name for the solution being formatted
   * 
   * @return The formatted value
   */
  public String format(QuerySolution solution, String variableName) {
    String formatted;

    if (solution.get(variableName) == null) {
      formatted = "";
    } else if (solution.get(variableName).isLiteral()) {
      formatted = solution.getLiteral(variableName).toString();
      int caratPosit;
      if (!includeDataType && formatted != null
          && (caratPosit = formatted.indexOf('^')) > -1) {
        formatted = formatted.substring(0, caratPosit);
      }
      if (addLiteralFlag) {
        formatted = "Lit: " + formatted;
      }
    } else {
      formatted = solution.getResource(variableName).toString();
      int hashAt;
      if (!useFqn && (hashAt = formatted.indexOf('#')) > -1) {
        String namespace = formatted.substring(0, hashAt + 1);
        String prefix = null;

        if (query != null) {
          prefix = query.getPrefixMapping().getNsURIPrefix(namespace);
          LOGGER.debug("Looking for prefix in SPARQL query: FQN["
              + namespace + "] Prefix[" + prefix + "]");
        }

        if (prefix == null && ontModel != null) {
          prefix = ontModel.getNsURIPrefix(namespace);
        }

        if (prefix == null) {
          prefix = namespace;
        } else {
          prefix = prefix + ":";
        }

        formatted = prefix + formatted.substring(hashAt + 1);
      }
    }

    return formatted;
  }
}

