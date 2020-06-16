package com.monead.semantic.workbench.utilities;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextArea;
import javax.swing.text.Document;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Search a JTextArea for specific text and highlight it if found.
 * 
 * @author David Read
 * 
 */
public class TextSearch {
  /**
   * The singleton instance of this class
   */
  private static final TextSearch INSTANCE = new TextSearch();

  /**
   * The collection of search parameters related to text areas
   */
  private final Map<JTextArea, SearchParameters> currentSearchInfo;

  /**
   * Logger Instance
   */
  private static final Logger LOGGER = LogManager
      .getLogger(TextSearch.class);

  /**
   * Private constructor for singleton instance
   */
  private TextSearch() {
    currentSearchInfo = new HashMap<JTextArea, SearchParameters>();
  }

  /**
   * Get the singleton instance
   * 
   * @return The single instance of this class
   */
  public static final TextSearch getInstance() {
    return INSTANCE;
  }

  /**
   * Start a new search for a text area
   * 
   * @param textArea
   *          The text area being searched
   * @param pattern
   *          The text to try to find in the text area
   * 
   * @return True if a match was found
   */
  public boolean startSearch(JTextArea textArea, String pattern) {
    final String fixedPattern = pattern.trim().toLowerCase();
    setupSearchParameters(textArea, fixedPattern);
    return doSearch(textArea);
  }

  /**
   * Determine whether there is a search currently defined for a text area
   * 
   * @param textArea
   *          The text area
   * 
   * @return True if search parameters are set for the text area
   */
  public boolean hasActiveSearch(JTextArea textArea) {
    return currentSearchInfo.get(textArea) != null;
  }

  /**
   * Search again for a text value in a text area
   * 
   * @see #lastSearchWrapped(JTextArea)
   * 
   * @param textArea
   *          The text area to search for another match
   * 
   * @return True if a match was found (this may be the only match). Check
   *         lastSearchWrapped to determine if the search wrapped back to the
   *         top of the text area
   */
  public boolean continueSearch(JTextArea textArea) {
    if (hasActiveSearch(textArea)) {
      return doSearch(textArea);
    } else {
      return false;
    }
  }

  /**
   * Get the line number of the latest match
   * 
   * @param textArea
   *          The text area that was searched
   * 
   * @return The line number of the latest match found
   */
  public int getLineOfLastMatch(JTextArea textArea) {
    if (hasActiveSearch(textArea)) {
      return currentSearchInfo.get(textArea).getLastMatchLineNumber();
    } else {
      return -1;
    }
  }

  /**
   * Determine whether the latest search caused the search to wrap back to the
   * top of the test area before finding a match
   * 
   * @param textArea
   *          The text area being searched
   * 
   * @return True if the latest search wrapped back to the top of the text area
   */
  public boolean lastSearchWrapped(JTextArea textArea) {
    if (hasActiveSearch(textArea)) {
      return currentSearchInfo.get(textArea).isLastSearchWrapped();
    } else {
      return false;
    }
  }

  /**
   * Initialize the search parameters
   * 
   * @param textArea
   *          The text area being searched
   * @param pattern
   *          The text to be located in the text area
   */
  private void setupSearchParameters(JTextArea textArea, String pattern) {
    final SearchParameters parameters = new SearchParameters(pattern, 0);
    currentSearchInfo.put(textArea, parameters);
  }

  /**
   * Update the search position for a defined search
   * 
   * @param textArea
   *          The text area being searched
   * @param position
   *          The position to start the next search
   * @param wrapped
   *          Whether this latest search wrapped to the top of the text area
   * @param lineNumber
   *          The line number of the latest match found in the text area
   */
  private void updateSearchPosition(JTextArea textArea, int position,
      boolean wrapped, int lineNumber) {
    final SearchParameters parameters = currentSearchInfo.get(textArea);
    parameters.setPosition(position);
    parameters.setLastSearchWrapped(wrapped);
    parameters.setLastMatchLineNumber(lineNumber);
  }

  /**
   * Carry out the search
   * 
   * @param textArea
   *          The text area to search
   * 
   * @return True if matching text was found
   */
  private boolean doSearch(JTextArea textArea) {
    final String pattern = currentSearchInfo.get(textArea).getPattern();
    final int startPosition = currentSearchInfo.get(textArea).getPosition();
    int position = startPosition;
    boolean found = false;
    boolean wrapped = false;

    textArea.requestFocusInWindow();

    // Make sure we have a valid search term
    if (pattern != null && pattern.length() > 0) {
      final Document document = textArea.getDocument();
      final int findLength = pattern.length();
      try {
        // While we haven't gotten back to where we started
        while (true) {
          if (position + findLength > document.getLength()) {
            position = 0;
            wrapped = true;
          }
          if (wrapped && position >= startPosition) {
            break;
          }
          // Extract the text from the document
          final String match = document.getText(position, findLength)
              .toLowerCase();
          // Check to see if it matches or request
          if (match.equals(pattern)) {
            found = true;
            break;
          }
          position++;
        }

        // Did we find something...
        if (found) {
          // Get the rectangle of the where the text would be visible...
          final Rectangle viewRect = textArea.modelToView(position);
          // Scroll to make the rectangle visible
          textArea.scrollRectToVisible(viewRect);
          // Highlight the text
          textArea.setCaretPosition(position + findLength);
          textArea.moveCaretPosition(position);
          final int lineNumber = textArea.getLineOfOffset(position);
          // Move the search position beyond the current match
          position += findLength;
          updateSearchPosition(textArea, position, wrapped, lineNumber);
        }

      } catch (Throwable throwable) {
        LOGGER.error("Error while searching the text field for [" + pattern
            + "] at [" + position + "]", throwable);
      }

    }

    return found;
  }
}
