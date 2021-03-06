package com.monead.semantic.workbench.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.jena.reasoner.IllegalParameterException;

/**
 * Wraps the source of a file which may be either a File or URL.
 * 
 * @author David Read
 * 
 */
public class FileSource {
  /**
   * If this FileSource is a file, this value will be populated
   */
  private File myFile;

  /**
   * If this FileSource is a URL, this value will be populated
   */
  private URL myUrl;

  /**
   * Byte length of the data returned from the URL
   * 
   * This would have to be set by making a call to the URL and reading the
   * returned data or checking the header for a content length
   */
  private long urlSourceFileLength = -1;

  /**
   * Creates a FileSource that wraps a File
   * 
   * @param pMyFile
   *          The File represented by this FileSource
   */
  public FileSource(File pMyFile) {
    myFile = pMyFile;
  }

  /**
   * Creates a FileSource that wraps a URL
   * 
   * @param pMyUrl
   *          The URL represented by this FileSource
   */
  public FileSource(URL pMyUrl) {
    myUrl = pMyUrl;
  }

  /**
   * Checks whether this FileSource represents a URL
   * 
   * @return True if the FileSource is a URL
   */
  public boolean isUrl() {
    return myUrl != null;
  }

  /**
   * Checks whether this FileSource represents a File
   * 
   * @return True if the FileSource is a File
   */
  public boolean isFile() {
    return myFile != null;
  }

  /**
   * Get the Reader for the FileSource
   * 
   * @return The Reader for the FileSource
   * 
   * @throws IOException
   *           If the underlying File or URL cannot be read
   */
  public InputStream getInputStream() throws IOException {
    return isUrl() ? getUrlReader() : getFileReader();
  }

  /**
   * Gets the InputStream for a URL
   * 
   * @return The InputStream for the URL
   * 
   * @throws IOException
   *           If the URL cannot be accessed
   */
  private InputStream getUrlReader() throws IOException {
    return myUrl.openStream();
  }

  /**
   * Get the InputStream for the File
   * 
   * @return The InputStream for the File
   * 
   * @throws IOException
   *           If the File cannot be read
   */
  private InputStream getFileReader() throws IOException {
    return new FileInputStream(myFile);
  }

  /**
   * Get the name of the backing File or URL
   * 
   * @return The File name or URL backing this FileSource
   */
  public String getName() {
    return isFile() ? myFile.getName() : getAbsolutePath();
  }

  /**
   * Get the absolute path of the backing File or URL
   * 
   * @return The absolute path of the File or complete URL backing this
   *         FileSource
   */
  public String getAbsolutePath() {
    if (isFile()) {
      return myFile.getAbsolutePath();
    } else {
      StringBuilder urlString;

      urlString = new StringBuilder();

      urlString.append(myUrl.getProtocol());
      urlString.append("://");
      urlString.append(myUrl.getHost());
      if (myUrl.getPort() > -1) {
        urlString.append(':');
        urlString.append(myUrl.getPort());
      }
      urlString.append(myUrl.getPath());
      if (myUrl.getQuery() != null) {
        urlString.append('?');
        urlString.append(myUrl.getQuery());
      }

      return urlString.toString();
    }
  }

  /**
   * Get the length of the File. If the backing source is a URL, -1 is returned.
   * 
   * @return The File length or -1 for URL sources
   */
  public long length() {
    return isFile() ? myFile.length() : urlSourceFileLength;
  }

  /**
   * Set the number of bytes in a page referenced by the File Source. This may
   * only be called for URLs. If the FileSource is a file, this method will
   * throw an IllegalParameterException.
   * 
   * @param byteLength
   *          The length of the file referenced by the FileSource's URL
   */
  public void setLength(long byteLength) {
    if (!isUrl()) {
      throw new IllegalParameterException(
          "Only the length for a URL can be overridden");
    }

    urlSourceFileLength = byteLength;
  }

  /**
   * Get the File backing this FileSource. If this FileSource is not backed by a
   * File, an exception is thrown
   * 
   * @return The File for this FileSource
   */
  public File getBackingFile() {
    if (!isFile()) {
      throw new IllegalStateException("This " + this.getClass().getName()
          + " is not backed by a File: " + getAbsolutePath());
    }

    return myFile;
  }

  /**
   * Get the URL backing this FileSource. If this FileSource is not backed by a
   * URL, an exception is thrown
   * 
   * @return The URL for this FileSource
   */
  public URL getBackingUrl() {
    if (!isUrl()) {
      throw new IllegalStateException("This " + this.getClass().getName()
          + " is not backed by a URL: " + getAbsolutePath());
    }

    return myUrl;
  }

  @Override
  public int hashCode() {
    if (isUrl()) {
      return getBackingUrl().hashCode();
    } else {
      return getBackingFile().hashCode();
    }
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof FileSource) {
      return equals((FileSource) object);
    }

    return false;
  }

  /**
   * Compare this FileSource to another FileSource. They are equivalent if they
   * contain the same file path or URL.
   * 
   * @param fileSource
   *          The file source to compare
   * 
   * @return True if the two file sources reference the same file path or URL
   */
  public boolean equals(FileSource fileSource) {
    boolean equal = false;

    if (isUrl() && fileSource.isUrl()) {
      equal = getBackingUrl().equals(fileSource.getBackingUrl());
    } else if (isFile() && fileSource.isFile()) {
      equal = getBackingFile().equals(fileSource.getBackingFile());
    }

    return equal;
  }

  /**
   * The toString() call will return the file name or full URL
   * 
   * @return The File name or URL
   */
  public String toString() {
    return getName();
  }
}
