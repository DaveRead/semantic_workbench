package com.monead.semantic.workbench.utilities;

/**
 * Parameters for searching
 * 
 * @author David Read
 * 
 */
public class SearchParameters {
  /**
   * The text to be located
   */
  private String pattern;

  /**
   * The position in the text area where the search should start
   */
  private int position;

  /**
   * Whether the last match was found after wrapping back to the top of the text
   * area
   */
  private boolean lastSearchWrapped;

  /**
   * The line number within the text area where the last match was found
   */
  private int lastMatchLineNumber = -1;

  /**
   * Setup a new search parameter.
   * 
   * @param pPattern
   *          The text to be found
   * @param pPosition
   *          The position in the text area to start the search
   */
  public SearchParameters(String pPattern, int pPosition) {
    setPattern(pPattern);
    setPosition(pPosition);
  }

  /**
   * Get the text to be found
   * 
   * @return The text to be found
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * Set the text to be found
   * 
   * @param pPattern
   *          The text to be found
   */
  public void setPattern(String pPattern) {
    pattern = pPattern;
  }

  /**
   * Get the position where the search should start
   * 
   * @return The start position of the search
   */
  public int getPosition() {
    return position;
  }

  /**
   * Set the position where the search should start
   * 
   * @param pPosition
   *          The start position of the search
   */
  public void setPosition(int pPosition) {
    position = pPosition;
  }

  /**
   * Determine if the last search wrapped back to the top of the text area
   * before a match was found
   * 
   * @return True if the last match that was found required the search to wrap
   *         back to the top of the text area
   */
  public boolean isLastSearchWrapped() {
    return lastSearchWrapped;
  }

  /**
   * Record whether the last search wrapped back to the top of the text area
   * 
   * @param pLastSearchWrapped
   *          True if the last search wrapped back to the top of the text area
   */
  public void setLastSearchWrapped(boolean pLastSearchWrapped) {
    lastSearchWrapped = pLastSearchWrapped;
  }

  /**
   * Get the line number where the latest match was found
   * 
   * @return The line number where the latest match was found
   */
  public int getLastMatchLineNumber() {
    return lastMatchLineNumber;
  }

  /**
   * Set the line number where the latest match was found
   * 
   * @param pLastMatchLineNumber The line number where the latest match was found
   */
  public void setLastMatchLineNumber(int pLastMatchLineNumber) {
    lastMatchLineNumber = pLastMatchLineNumber;
  }
}
