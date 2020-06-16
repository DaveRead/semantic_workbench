package com.monead.semantic.workbench.images;

import javax.swing.ImageIcon;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
  private static final String[] ICON_FILES = {
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
   * Program icon - 32x32
   */
  public static final int ICON_SEMANTIC_WORKBENCH_32X32 = 0;

  /**
   * Program icon - 16x16
   */
  public static final int ICON_SEMANTIC_WORKBENCH_16X16 = 1;

  /**
   * Class-related tree node icon - class
   */
  public static final int ICON_TREE_CLASS = 2;

  /**
   * Class-related tree node icon - class with no instances
   */
  public static final int ICON_TREE_CLASS_NOINSTANCES = 3;

  /**
   * Class-related tree node icon (boxed) - class
   */
  public static final int ICON_TREE_CLASS_BOXED = 4;

  /**
   * Class-related tree node icon (boxed) - class with no instances
   */
  public static final int ICON_TREE_CLASS_NOINSTANCES_BOXED = 5;

  /**
   * Property-related tree node icon - data property
   */
  public static final int ICON_TREE_PROPERTY_DATA = 6;

  /**
   * Property-related tree node icon (boxed) - data property
   */
  public static final int ICON_TREE_PROPERTY_DATA_BOXED = 7;

  /**
   * Individual-related tree node icon - individual
   */
  public static final int ICON_TREE_INSTANCE = 8;

  /**
   * Individual-related tree node icon - individual with no properties
   */
  public static final int ICON_TREE_INSTANCE_NOPROPERTIES = 9;

  /**
   * Individual-related tree node icon (boxed) - individual
   */
  public static final int ICON_TREE_INSTANCE_BOXED = 10;

  /**
   * Individual-related tree node icon (boxed) - individual with no properties
   */
  public static final int ICON_TREE_INSTANCE_NOPROPERTIES_BOXED = 11;

  /**
   * Literal-related tree node icon - literal
   */
  public static final int ICON_TREE_LITERAL = 12;

  /**
   * Literal-related tree node icon (boxed) - literal
   */
  public static final int ICON_TREE_LITERAL_BOXED = 13;

  /**
   * Object property-related tree node icon - object property
   */
  public static final int ICON_TREE_PROPERTY_OBJECT = 14;

  /**
   * Object property-related tree node icon (boxed) - object property
   */
  public static final int ICON_TREE_PROPERTY_OBJECT_BOXED = 15;

  /**
   * Property-related tree node icon - property
   */
  public static final int ICON_TREE_PROPERTY = 16;

  /**
   * Property-related tree node icon (boxed) - property
   */
  public static final int ICON_TREE_PROPERTY_BOXED = 17;

  /**
   * Logger Instance
   */
  private static final Logger LOGGER = LogManager.getLogger(ImageLibrary.class);

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
  public static final ImageLibrary instance() {
    return instance;
  }

  /**
   * Get an icon from this package.
   * 
   * @param iconIndex
   *          The index of the image. Constants are provided for these.
   * 
   * @return The Icon instance or null if it cannot be loaded.
   */
  public ImageIcon getImageIcon(int iconIndex) {
    ImageIcon theIcon = null;

    if (iconIndex < ICON_FILES.length) {
      if ((theIcon = loadedIcons[iconIndex]) == null) {
        try {
          theIcon = new ImageIcon(
              ImageLibrary.class.getResource(ICON_FILES[iconIndex]));
          loadedIcons[iconIndex] = theIcon;
        } catch (Throwable throwable) {
          LOGGER.warn("Unable to load image number: " + iconIndex, throwable);
        }
      }
    }

    return theIcon;
  }
}
