package com.monead.semantic.workbench.utilities;

/**
 * Collection of functions for dealing with text values
 * 
 * @author David Read
 * 
 */
public class TextProcessing {
  /**
   * Utility class, no instances should be created
   */
  private TextProcessing() {

  }

  /**
   * Check whether the value should be quoted if saving into a CSV file.
   * 
   * @param value
   *          The value being written to a CSV file
   * 
   * @return True if the value should be quoted, false otherwise
   */
  public static final boolean isCsvQuoteRequired(String value) {
    boolean quoteRequired = false;
    int dotCount = 0;

    if (value != null) {
      final char[] chars = value.toCharArray();
      for (int index = 0; index < chars.length && !quoteRequired; ++index) {
        // Allow a decimal point (e.g. part of real number)
        if (chars[index] == '.') {
          ++dotCount;
          // If more than one decimal point found, it isn't a number - quote it
          if (dotCount > 1) {
            quoteRequired = true;
          }
        } else {
          // If it isn't a digit then quote it
          quoteRequired = !Character.isDigit(chars[index]);
        }
      }
    }

    return quoteRequired;
  }

  /**
   * Determine if a quote is required around a value if it is being written to a
   * tab-separated file
   * 
   * @param value
   *          The value being written
   * 
   * @return True if the value needs to be quoted in a TSV file
   */
  public static final boolean isTsvQuoteRequired(String value) {
    return value != null && value.indexOf('\t') > -1;
  }

  /**
   * Format the value for output in a tab separated file.
   * 
   * @param value
   *          The value being written
   * 
   * @return The value properly quoted, if required
   */
  public static final String formatForTsvColumn(Object value) {
    return formatForTsvColumn(value.toString());
  }

  /**
   * Format the value for output in a tab separated file.
   * 
   * @param value
   *          The value being written
   * 
   * @return The value properly quoted, if required
   */
  public static final String formatForTsvColumn(String value) {
    final boolean quoteRequired = isTsvQuoteRequired(value);
    final StringBuffer resultingValue = new StringBuffer();

    if (quoteRequired) {
      resultingValue.append('"');
    }

    if (value != null) {
      resultingValue.append(value);
    }

    if (quoteRequired) {
      resultingValue.append('"');
    }

    return resultingValue.toString();
  }

  /**
   * Format the value for output in a comma separated file.
   * 
   * @param value
   *          The value being written
   * 
   * @return The value properly quoted, if required
   */
  public static final String formatForCsvColumn(Object value) {
    return formatForCsvColumn(value.toString());
  }

  /**
   * Format the value for output in a comma separated file.
   * 
   * @param value
   *          The value being written
   * 
   * @return The value properly quoted, if required
   */
  public static final String formatForCsvColumn(String value) {
    final boolean quoteRequired = isCsvQuoteRequired(value);
    final StringBuffer resultingValue = new StringBuffer();

    if (quoteRequired) {
      resultingValue.append('"');
    }

    if (value != null) {
      resultingValue.append(value);
    }

    if (quoteRequired) {
      resultingValue.append('"');
    }

    return resultingValue.toString();
  }
}
