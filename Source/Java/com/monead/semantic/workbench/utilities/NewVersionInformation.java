package com.monead.semantic.workbench.utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Populated with information for a newer version of the program.
 * 
 * @author David Read
 * 
 */
public class NewVersionInformation {
  private String latestVersionString;
  private List<String> newFeatures = new ArrayList<String>();

  /**
   * No operation
   */
  public NewVersionInformation() {

  }

  /**
   * Get the instructions for obtaining the new version.
   * 
   * TODO populate from website source
   * 
   * @return Instructions for obtaining a new version
   */
  public String getDownloadInformation() {
    return "The latest installer is at:\n"
        + "  - http://monead.com/semantic/semantic_workbench\n\n"
        + "The source code is on GitHub at:\n"
        + "  - https://github.com/DaveRead/semantic_workbench";
  }

  /**
   * Get the latest version number
   * 
   * @return The version number
   */
  public String getLatestVersion() {
    return latestVersionString;
  }

  /**
   * Set the latest version number
   * 
   * @param latestVersionString
   *          The version number
   */
  public void setLatestVersion(String latestVersionString) {
    this.latestVersionString = latestVersionString;
  }

  /**
   * Get the description of new features
   * 
   * @return Listing of new features
   */
  public String getNewFeaturesDescription() {
    StringBuffer message = new StringBuffer();

    for (String line : newFeatures) {
      message.append(line);
      message.append('\n');
    }

    return message.toString();
  }

  /**
   * Add a new feature description
   * 
   * @param newFeatureMessage
   *          New feature in the more recent version
   */
  public void addNewFeatureMessage(String newFeatureMessage) {
    newFeatures.add("  * " + newFeatureMessage);
  }
}
