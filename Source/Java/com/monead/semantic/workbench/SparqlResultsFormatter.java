package com.monead.semantic.workbench;

import java.net.URL;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.monead.semantic.workbench.utilities.ValueFormatter;

/**
 * Formats SPARQL results using the configuration settings sent to the
 * constructor.
 */
public class SparqlResultsFormatter {
  /**
   * Logger Instance
   */
  private static final Logger LOGGER = Logger
      .getLogger(SparqlResultsFormatter.class);

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
   * Whether to apply XSD-based formatting rules to literal property values
   */
  private boolean applyFormattingToLiteralValues;

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
   * @param pQuery
   *          The SPARQL query used to obtain the results being formatted. May
   *          be null.
   * @param pOntModel
   *          The ontological model against which the SPARQL query was run. May
   *          be null.
   * @param pApplyFormattingToLiteralValues
   *          Whether to apply XSD-based formatting rules to literal property
   *          values
   * @param pAddLiteralFlag
   *          Whether literal values should be prepended with a "Lit:"
   *          identifier
   * @param pIncludeDataType
   *          Whether literals should have their data type appended to their
   *          value
   * @param pUseFqn
   *          Whether the fully qualified namespace should be used. Otherwise a
   *          prefix will be used if defined for the namespace
   */
  public SparqlResultsFormatter(Query pQuery, OntModel pOntModel,
      boolean pApplyFormattingToLiteralValues, boolean pAddLiteralFlag,
      boolean pIncludeDataType,
      boolean pUseFqn) {
    query = pQuery;
    ontModel = pOntModel;
    applyFormattingToLiteralValues = pApplyFormattingToLiteralValues;
    addLiteralFlag = pAddLiteralFlag;
    includeDataType = pIncludeDataType;
    useFqn = pUseFqn;
  }

  /**
   * Format the value.
   * 
   * @param solution
   *          The current query solution to be formatted
   * @param variableName
   *          The bound variable name for the solution being formatted
   * @param displayImages
   *          Whether images should be displayed
   * 
   * @return The formatted value
   */
  public SparqlResultItem format(QuerySolution solution, String variableName,
      boolean displayImages) {
    SparqlResultItem formatted = null;

    if (solution.get(variableName) == null) {
      formatted = new SparqlResultItem("");
    } else if (solution.get(variableName).isLiteral()) {
      String literal = solution.getLiteral(variableName).toString();
      int caratPosit = -1;
      String xsdType = null;

      if (literal != null) {
        caratPosit = literal.indexOf('^');
        if (caratPosit > -1) {
          xsdType = literal.substring(caratPosit + 2);
          literal = literal.substring(0, caratPosit);
        }
      }

      if (addLiteralFlag) {
        literal = "Lit: " + literal;
      }

      if (applyFormattingToLiteralValues && xsdType != null) {
        literal = ValueFormatter.getInstance().applyFormat(literal, xsdType);
        /*
         * if (xsdType.endsWith("double") || xsdType.endsWith("float")) {
         * literal = decimalFormat(literal);
         * } else if (xsdType.equals("xsd:decimal")) {
         * literal = integerFormat(literal);
         * }
         */
      }

      if (xsdType != null && includeDataType) {
        literal += "^^" + xsdType;
      }

      formatted = new SparqlResultItem(literal);
    } else {
      final String uriString = solution.getResource(variableName).toString();
      String displayFormat;
      int endOfNamespace;

      displayFormat = uriString;

      if (!useFqn) {
        endOfNamespace = uriString.indexOf('#');

        if (endOfNamespace == -1) {
          endOfNamespace = uriString.lastIndexOf('/');
        }

        if (endOfNamespace > -1) {
          final String namespace = uriString.substring(0, endOfNamespace + 1);
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

          displayFormat = prefix + uriString.substring(endOfNamespace + 1);
        }
      }
      try {
        formatted = new SparqlResultItem(new URL(uriString).toURI(),
            displayFormat, displayImages);
      } catch (Throwable throwable) {
        LOGGER.error("URL not correctly formatted, defaulting to a literal: "
            + solution.getResource(variableName).toString(), throwable);
        formatted = new SparqlResultItem(solution.getResource(variableName)
            .toString());
      }

      /*
       * if (formatted == null) {
       * int hashAt;
       * formatted = uriString;
       * if (!useFqn && (hashAt = uriString.indexOf('#')) > -1) {
       * String namespace = uriString.substring(0, hashAt + 1);
       * String prefix = null;
       * 
       * if (query != null) {
       * prefix = query.getPrefixMapping().getNsURIPrefix(namespace);
       * LOGGER.debug("Looking for prefix in SPARQL query: FQN["
       * + namespace + "] Prefix[" + prefix + "]");
       * }
       * 
       * if (prefix == null && ontModel != null) {
       * prefix = ontModel.getNsURIPrefix(namespace);
       * }
       * 
       * if (prefix == null) {
       * prefix = namespace;
       * } else {
       * prefix = prefix + ":";
       * }
       * 
       * formatted = prefix + uriString.substring(hashAt + 1);
       * }
       * }
       */
    }

    return formatted;
  }
}
