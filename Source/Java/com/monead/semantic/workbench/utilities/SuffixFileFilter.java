package com.monead.semantic.workbench.utilities;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * A FileFilter that limits files to those that end with one of the supplied
 * suffix values. A dot should be the first character of the suffix pattern if
 * the entire suffix must match the pattern. If only the ending characters must
 * match then the dot should be excluded. For example the suffix pattern ".txt"
 * will match files ending with ".txt" while a suffix pattern of "xt" will match
 * files ending in things like ".txt", ".pxt", ".ext", ".next" and so forth.
 * 
 * @author David Read
 */
public class SuffixFileFilter extends FileFilter {
  /**
   * The description of the filter
   */
  private String description;

  /**
   * The collection of suffixes that match the filter
   */
  private String[] acceptedSuffixes;

  /**
   * Create a new filter with the supplied description and collection of
   * suffixes. Note that the suffixes are not case sensitive even on case
   * sensitive operating systems.
   * 
   * @param pDescription
   *          The description of the filter to be shown to the user
   * @param pAcceptedSuffixes
   *          The set of file suffix patterns that match the filter
   */
  public SuffixFileFilter(String pDescription, String[] pAcceptedSuffixes) {
    super();

    setDescription(pDescription);
    setAcceptedSuffixes(pAcceptedSuffixes);
  }

  /**
   * Set the description for this filter
   * 
   * @param pDescription
   *          The filter description
   */
  private void setDescription(String pDescription) {
    description = pDescription;
  }

  /**
   * Set the collection of suffix patterns to be accepted by this filter
   * 
   * @param pAcceptedSuffixes
   *          The accepted suffix patterns
   */
  private void setAcceptedSuffixes(String[] pAcceptedSuffixes) {
    acceptedSuffixes = new String[pAcceptedSuffixes.length];

    for (int index = 0; index < pAcceptedSuffixes.length; ++index) {
      acceptedSuffixes[index] = pAcceptedSuffixes[index].toLowerCase();
    }
  }

  /**
   * Check whether the supplied file's name agrees with this suffix file filter.
   * If not (e.g. the suffix is not a match) then append the first suffix in the
   * list for this filter to the filename and return it. Otherwise return the
   * original file name.
   * 
   * @param file
   *          The file whose name is to be check against this filter
   * 
   * @return The file name adjust ed match this suffix filter if necessary
   */
  public File makeWithPrimarySuffix(File file) {
    if (!accept(file)) {
      String primarySuffix = acceptedSuffixes[0];
      if (!primarySuffix.startsWith(".")) {
        primarySuffix = "." + primarySuffix;
      }
      return new File(file.getAbsolutePath() + primarySuffix);
    } else {
      return file;
    }
  }

  @Override
  public boolean accept(File file) {
    if (file.isDirectory()) {
      return true;
    }

    final String fileNameLowerCase = file.getName().toLowerCase();

    for (String suffix : acceptedSuffixes) {
      if (fileNameLowerCase.endsWith(suffix)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public String getDescription() {
    return description;
  }
}
