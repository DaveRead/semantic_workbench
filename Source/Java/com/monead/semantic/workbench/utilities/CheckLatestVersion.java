package com.monead.semantic.workbench.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Observable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Checks for the latest version information. Version identifier string must be
 * in the form of MAJOR#.MINOR#.PATCH# with 1 or 2 digits for each part.
 * 
 * e.g. 1.8.3
 * 
 * @author David Read
 * 
 */
public class CheckLatestVersion extends Observable implements Runnable {
  /**
   * URL to version information
   */
  private static final String VERSION_INFO_URL = "http://monead.com/semantic/semantic_workbench/semantic_workbench_version.txt";

  /**
   * Logger Instance
   */
  private static final Logger LOGGER = Logger.getLogger(CheckLatestVersion.class);

  /**
   * Maximum number of lines in the upgrade message
   */
  private static final int MAX_NEW_FEATURES_MESSAGE_LINES = 10;

  /**
   * The current version of the application
   */
  private String currentVersion;

  /**
   * The calculated value of the latest version. Used in order to identify
   * keep track of the most recent version identified from the list of newer
   * versions.
   */
  private int latestVersionValue;

  /**
   * Information regarding the newest version. This is only created if there is
   * a newer version than the currently running version.
   */
  private NewVersionInformation newVersionInformation;

  /**
   * Create an instance that will identify if a version newer than the supplied
   * one exists.
   * 
   * @param pCurrentVersion
   *          The current version (#.#.# format)
   */
  public CheckLatestVersion(String pCurrentVersion) {
    currentVersion = pCurrentVersion;
  }

  @Override
  public void run() {
    setupVersionInfo();

    if (newVersionInformation != null) {
      setChanged();
      notifyObservers(newVersionInformation);
    }
  }

  /**
   * Get version information from the website and check if any listed versions
   * are newer than the current version.
   */
  private void setupVersionInfo() {
    final String latestVersionDetails = getVersionInfoFromWebsite();

    if (latestVersionDetails != null) {
      setupVersionMessage(latestVersionDetails);
    }
  }

  /**
   * Download the version history information from the website.
   * 
   * @return The version history information
   */
  private String getVersionInfoFromWebsite() {
    String informationFromWebsite = null;

    try {
      final URL url = new URL(VERSION_INFO_URL);
      final URLConnection con = url.openConnection();
      final Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
      final Matcher m = p.matcher(con.getContentType());

      /*
       * If Content-Type doesn't match this pre-conception, choose default and
       * hope for the best.
       */
      final String charset = m.matches() ? m.group(1) : "UTF-8";

      final Reader r = new InputStreamReader(con.getInputStream(), charset);
      final StringBuilder buf = new StringBuilder();
      while (true) {
        final int ch = r.read();
        if (ch < 0) {
          break;
        }
        buf.append((char) ch);
      }
      informationFromWebsite = buf.toString();

      LOGGER.debug("Got version information from website: "
          + informationFromWebsite);

    } catch (Throwable throwable) {
      LOGGER.warn("Unable to get version information from website", throwable);
    }

    return informationFromWebsite;
  }

  /**
   * Determine if a newer version of the application exists and if so setup the
   * newVersionInformation instance with information regarding the newest
   * version.
   * 
   * @param informationFromWebsite
   *          Downloadeed version information
   */
  private void setupVersionMessage(String informationFromWebsite) {
    final int myVersion = versionParser(currentVersion);
    String line = null;
    int lineNumber = 0;
    int version;
    boolean newFeatures = false;
    int linesOfMessage = 0;

    try {
      if (informationFromWebsite != null) {
        final BufferedReader in = new BufferedReader(new StringReader(
            informationFromWebsite));

        while ((line = in.readLine()) != null) {
          line = line.trim();
          ++lineNumber;
          LOGGER.debug("Version file line: " + line);
          if (line.startsWith("VERSION:")) {
            version = versionParser(line.split(":")[1].trim());
            if (version > myVersion) {
              LOGGER.debug("Newer version found: " + version);
              newFeatures = true;
              if (newVersionInformation == null) {
                newVersionInformation = new NewVersionInformation();
                // TODO encode download locations into website info file
                try {
                  newVersionInformation.setUrlToDownloadPage(new URL(
                      "http://monead.com/semantic/semantic_workbench"));
                  newVersionInformation.setUrlToSourceCode(new URL(
                      "https://github.com/DaveRead/semantic_workbench"));
                } catch (Throwable throwable) {
                  LOGGER.warn("Error configuring program download URLs",
                      throwable);
                }
              }
              if (version > latestVersionValue) {
                latestVersionValue = version;
                newVersionInformation.setLatestVersion(line.split(":")[1]
                    .trim());
                LOGGER.debug("Latest version found: "
                    + newVersionInformation.getLatestVersion());
              }
            } else {
              newFeatures = false;
            }
          } else if (newFeatures && line.length() > 0
              && linesOfMessage < MAX_NEW_FEATURES_MESSAGE_LINES) {
            LOGGER.debug("Adding a new feature message: " + line);
            newVersionInformation.addNewFeatureMessage(line);
            ++linesOfMessage;
          }
        }

      }
    } catch (Throwable throwable) {
      LOGGER.warn("Error parsing information from version file at line "
          + lineNumber + ": " + line, throwable);
    }

  }

  /**
   * Converts a version string MAJOR#.MINOR#.PATCH# into an integer so that
   * versions can be compared numerically. There can only be 1 or 2 digits for
   * each value for this to work properly.
   * 
   * @param version
   *          The version string (#.#.#)
   * 
   * @return An integer value suitable for comparing versions numerically
   */
  private int versionParser(String version) {
    int versionValue = -1;

    LOGGER.debug("Calculate version number for version: " + version);
    try {
      final String[] parsed = version.split("\\.");
      versionValue = Integer.parseInt(parsed[0]) * 10000;
      versionValue += Integer.parseInt(parsed[1]) * 100;
      versionValue += Integer.parseInt(parsed[2]);
    } catch (Throwable throwable) {
      LOGGER.warn("Unable to parse version number: " + version, throwable);
    }

    LOGGER.debug("Version calculation result for version: " + version
        + "  Value:" + versionValue);

    return versionValue;
  }
}
