package com.monead.semantic.workbench;

import java.awt.Image;
import java.net.URI;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Wraps a singular result (cell, specific row/column) from a SPARQL query. This
 * could be a literal or a URI.
 * 
 * The SPARQL output JTable uses a special renderer to display these items to
 * the user.
 * 
 * @see SparqlResultItemRenderer
 * 
 * @author David Read
 * 
 */
public class SparqlResultItem {
  /**
   * Logger Instance
   */
  private static final Logger LOGGER = LogManager
      .getLogger(SparqlResultItem.class);

  /**
   * The literal value wrapped by this instance, if applicable
   */
  private String literal;

  /**
   * The URI wrapped by this instance, if applicable
   */
  private URI uri;

  /**
   * The display version of the URI, if applicable. FOr URIs, this value is used
   * when asked for an output representation of the URL. The actual URI should
   * be used if the URI is needed for other purposes (creating a query or
   * attempting to access the URI directly)
   */
  private String displayUri;

  /**
   * The image wrapped by this instance, if applicable. If the instance is a URI
   * and that URI is a URL to an image and the instance is asked to retrieve the
   * image, then this variable will contain the image.
   */
  private ImageIcon imageIcon;

  /**
   * Wrap a literal
   * 
   * @param pLiteral
   *          The value being wrapped
   */
  public SparqlResultItem(String pLiteral) {
    setLiteral(pLiteral);
  }

  /**
   * Wrap a URI. If the URI is a URL to an image and the image is to be
   * downloaded, an attempt will be made to load the image. Note that this will
   * greatly slow the processing of SPARQL results since this loading is not
   * done using a separate thread.
   * 
   * @param pUri
   *          The URI being wrapped
   * @param pDownloadImage
   *          True if the wrapper should fetch an image accessible at the URI.
   *          If the URI does not point to an image, there is no error
   */
  public SparqlResultItem(URI pUri, boolean pDownloadImage) {
    setUri(pUri);
    setDisplayUri(pUri.toString());

    if (pDownloadImage) {
      setupImage();
    }
  }

  /**
   * Wrap a URI and supply an alternate display string for the URI. If the URI
   * is a URL to an image and the image is to be downloaded, an attempt will be
   * made to load the image. Note that this will greatly slow the processing of
   * SPARQL results since this loading is not done using a separate thread.
   * 
   * @param pUri
   *          The URI being wrapped
   * @param pDisplayUri
   *          The display string to use when outputting the URI
   * @param pDownloadImage
   *          True if the wrapper should fetch an image accessible at the URI.
   *          If the URI does not point to an image, there is no error
   */
  public SparqlResultItem(URI pUri, String pDisplayUri,
      boolean pDownloadImage) {
    setUri(pUri);
    setDisplayUri(pDisplayUri);

    if (pDownloadImage) {
      setupImage();
    }
  }

  /**
   * Attempt to use the URI as a URL of an image. If an image is found, load it
   * into the wrapper instance. Otherwise take no action.
   * 
   * Note that the algorithm for determining whether the URI is for an image
   * simply checks for common image suffix values before attempting to connect
   * to the URI.
   * 
   * TODO Allow option to connect to URI and check MIME type to assess if the
   * URI is for an image.
   * 
   * TODO make list of image suffix values configurable
   */
  private void setupImage() {
    final String lowerCase = getUri().toString();

    if (lowerCase.contains(".jpg") || lowerCase.contains(".jpeg")
        || lowerCase.contains(".png") || lowerCase.contains(".gif")) {
      setImageIcon(tryToLoadImageIcon());
    }
  }

  /**
   * Return true if this instance contains an image
   * 
   * @return True if the instance has wrapped a downloaded image
   */
  public boolean isImageIcon() {
    return getImageIcon() != null;
  }

  /**
   * Retrieve the image from the URL. If the image cannot be loaded, the request
   * is ignored and no error occurs.
   * 
   * @return The image or null if the image cannot be loaded
   */
  private ImageIcon tryToLoadImageIcon() {
    ImageIcon lImageIcon = null;

    try {
      final Image image = ImageIO.read(getUri().toURL());
      lImageIcon = new ImageIcon(image);
    } catch (Throwable throwable) {
      LOGGER.warn("Cannot load likely image: " + getUri(), throwable);
      lImageIcon = null;
    }

    return lImageIcon;
  }

  /**
   * Retrieve the literal wrapped by this instance.
   * 
   * @return The wrapped literal or null if this wrapper represents a URI
   */
  public String getLiteral() {
    return literal;
  }

  /**
   * Set the literal being wrapped by this instance
   * 
   * @param pLiteral
   *          The value being wrapped
   */
  private void setLiteral(String pLiteral) {
    literal = pLiteral;
  }

  /**
   * Retrieve the URI wrapped by this instance
   * 
   * @return The wrapped URI or null if this wrapper represents a literal
   */
  public URI getUri() {
    return uri;
  }

  /**
   * Set the URI being wrapped by this instance
   * 
   * @param pUri
   *          The URI being wrapped
   */
  private void setUri(URI pUri) {
    uri = pUri;
  }

  /**
   * Get the image contained in this wrapper.
   * 
   * @return The image or null if no image was downloaded by this wrapper
   */
  public ImageIcon getImageIcon() {
    return imageIcon;
  }

  /**
   * Set the image that was loaded from this instances URI
   * 
   * @param pImageIcon
   *          The image
   */
  private void setImageIcon(ImageIcon pImageIcon) {
    imageIcon = pImageIcon;
  }

  /**
   * Get the display version of the URI wrapped by this instance.
   * 
   * @return The display version of the URI. If no separate display URI was set,
   *         then this will return the actual URI as a string
   */
  public String getDisplayUri() {
    return displayUri;
  }

  /**
   * Set the display version of the URI wrapped by this instance. This allows a
   * more readable form of the URI to be used for output (e.g. displaying with
   * prefixes instead of FQN)
   * 
   * @param pDisplayUri
   *          The string representation of the URI to be shown when displaying
   *          the URI
   */
  public void setDisplayUri(String pDisplayUri) {
    displayUri = pDisplayUri;
  }

  /**
   * Return the string representation of the wrapped value. This may be the
   * literal or string representation of the URI.
   * 
   * @return String representation of the literal or URI
   */
  public String toString() {
    if (displayUri != null) {
      return displayUri;
    } else {
      return literal;
    }
  }
}
