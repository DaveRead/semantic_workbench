package com.monead.semantic.workbench.images;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

/**
 * Provide access to images stored within the package.
 * 
 * @author David Read
 *
 */
public class ImageLibrary {

  /**
   * The set of defined image files that can be loaded by
   * this class.
   */
  private final static String[] ICON_FILES = {
      "SWBinitialed.Icon.32x32.png", "SWB.Icon.16x16.png",
      "TreeNode-Class.png", "TreeNode-Class-NoInstances.png",
      "TreeNode-Class-Boxed.png", "TreeNode-Class-NoInstances-Boxed.png",
      "TreeNode-DataProperty.png", "TreeNode-DataProperty-Boxed.png",
      "TreeNode-Instance.png", "TreeNode-Instance-NoProperties.png",
      "TreeNode-Instance-Boxed.png",
      "TreeNode-Instance-NoProperties-Boxed.png",
      "TreeNode-Literal.png", "TreeNode-Literal-Boxed.png",
      "TreeNode-ObjectProperty.png", "TreeNode-ObjectProperty-Boxed.png",
      "TreeNode-Property.png", "TreeNode-Property-Boxed.png"
  };

  /**
   * Constants for program icons
   */
  public final static int ICON_SEMANTIC_WORKBENCH_32X32 = 0;
  public final static int ICON_SEMANTIC_WORKBENCH_16X16 = 1;
  /**
   * Constants for ontology class-related tree node icons
   */
  public final static int ICON_TREE_CLASS = 2;
  public final static int ICON_TREE_CLASS_NOINSTANCES = 3;
  public final static int ICON_TREE_CLASS_BOXED = 4;
  public final static int ICON_TREE_CLASS_NOINSTANCES_BOXED = 5;

  /**
   * Constants for ontology property-related tree node icons
   */
  public final static int ICON_TREE_PROPERTY_DATA = 6;
  public final static int ICON_TREE_PROPERTY_DATA_BOXED = 7;

  /**
   * Constants for ontology individual-related tree node icons
   */
  public final static int ICON_TREE_INSTANCE = 8;
  public final static int ICON_TREE_INSTANCE_NOPROPERTIES = 9;
  public final static int ICON_TREE_INSTANCE_BOXED = 10;
  public final static int ICON_TREE_INSTANCE_NOPROPERTIES_BOXED = 11;

  /**
   * Constants for ontology literal-related tree node icons
   */
  public final static int ICON_TREE_LITERAL = 12;
  public final static int ICON_TREE_LITERAL_BOXED = 13;

  /**
   * Constants for ontology object property-related tree node icons
   */
  public final static int ICON_TREE_PROPERTY_OBJECT = 14;
  public final static int ICON_TREE_PROPERTY_OBJECT_BOXED = 15;

  /**
   * Constants for ontology property-related tree node icons
   */
  public final static int ICON_TREE_PROPERTY = 16;
  public final static int ICON_TREE_PROPERTY_BOXED = 17;

  /**
   * Logger Instance
   */
  private static Logger LOGGER = Logger.getLogger(ImageLibrary.class);

  /**
   * A cache of loaded images
   */
  private ImageIcon[] loadedIcons = new ImageIcon[ICON_FILES.length];

  /**
   * Singleton instance of this class
   */
  private static ImageLibrary instance = new ImageLibrary();

  /**
   * Singleton class - private constructor
   */
  private ImageLibrary() {

  }

  /**
   * Singleton instance access
   * 
   * @return The singleton instance of this class
   */
  public final static ImageLibrary instance() {
    return instance;
  }

  /**
   * Get an icon from this package.
   * 
   * @param iconIndex The index of the image. Constants are provided for these.
   * 
   * @return The Icon instance or null if it cannot be loaded.
   */
  public ImageIcon getImageIcon(int iconIndex) {
    ImageIcon theIcon = null;

    if (iconIndex < ICON_FILES.length) {
      if ((theIcon = loadedIcons[iconIndex]) == null) {
        try {
          theIcon = new ImageIcon(
              ImageLibrary.class.getResource(ICON_FILES[iconIndex]));// ImageIO.read(ImageLibrary.class.getResource(ICON_FILES[iconIndex]));
          loadedIcons[iconIndex] = theIcon;
        } catch (Throwable throwable) {
          LOGGER.warn("Unable to load image number: " + iconIndex, throwable);
        }
      }
    }

    return theIcon;
  }
}
