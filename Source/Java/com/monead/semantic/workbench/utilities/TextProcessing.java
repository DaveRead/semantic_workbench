package com.monead.semantic.workbench.utilities;

/**
 * Collection of functions for dealing with text values
 * 
 * @author David Read
 *
 */
public class TextProcessing {
  /**
   * Check whether the value should be quoted if saving into a CSV file.
   * 
   * @param value The value being written to a CSV file
   * 
   * @return True if the value should be quoted, false otherwise
   */
  public final static boolean isCsvQuoteRequired(String value) {
    boolean quoteRequired = false;
    int dotCount = 0;
    
    if (value != null) {
      char[] chars = value.toCharArray();
      for (int index = 0;index < chars.length && !quoteRequired;++index) {
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
  
  public final static boolean isTsvQuoteRequired(String value) {
    return value != null && value.indexOf('\t') > -1;
  }
  
  public final static String formatForTsvColumn(String value) {
    boolean quoteRequired = isTsvQuoteRequired(value);
    StringBuffer resultingValue = new StringBuffer();
    
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

  public final static String formatForCsvColumn(String value) {
    boolean quoteRequired = isCsvQuoteRequired(value);
    StringBuffer resultingValue = new StringBuffer();
    
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
