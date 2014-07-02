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
  
  /**
   * Ensure that a given character is inserted at intervals along a String. This
   * is typically used to force inclusion of a space periodically.
   * 
   * The method will walk through the supplied data string, looking for
   * characters that are in the break characters String. If found, and the
   * minimum number of characters have been traversed, the insert string is
   * inserted and the character count reset. If the maximum section length is
   * reached and no break characters have been found, the insert string is
   * inserted anyway.
   * 
   * @param saData
   *          The string to have characters inserted into
   * @param saInsert
   *          The String to insert into the data string
   * @param iaMinSectionLength
   *          The minumum number of characters in the data
   *          string, that must occur between inserted strings
   * @param iaMaxSectionLength
   *          The maximum number of characters to allow
   *          in the data string between inserted strings
   * @param saBreakCharacters
   *          The characters that signal a possible insertion
   *          point in the data string
   * 
   * @return The updated data string. If the data string is null, a null
   *         String is returned.
   */
  public static final String characterInsert(String saData, String saInsert,
      int iaMinSectionLength,
      int iaMaxSectionLength,
      String saBreakCharacters) {
    String result;
    StringBuffer sblResult;
    char[] clData;
    int ilLen, ilPosit, ilSectionLength;

    if (saData != null) {
      ilLen = saData.length();
      clData = new char[ilLen];
      saData.getChars(0, ilLen, clData, 0);
      ilPosit = 0;
      ilSectionLength = 0;
      sblResult = new StringBuffer(ilLen * 2);

      for (ilPosit = 0; ilPosit < ilLen; ++ilPosit) {
        sblResult.append(clData[ilPosit]);
        ++ilSectionLength;
        if (saInsert.length() == 1 && clData[ilPosit] == saInsert.charAt(0)) {
          ilSectionLength = 0;
        } else if ((ilSectionLength >= iaMinSectionLength && saBreakCharacters
            .indexOf(clData[ilPosit]) >= 0)
            || ilSectionLength >= iaMaxSectionLength) {
          sblResult.append(saInsert);
          ilSectionLength = 0;
        }
      }
      result = sblResult.toString();
    } else {
      result = saData;
    }

    return result;
  }
}
