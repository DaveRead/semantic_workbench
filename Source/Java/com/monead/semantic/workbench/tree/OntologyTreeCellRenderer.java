package com.monead.semantic.workbench.tree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.monead.semantic.workbench.images.ImageLibrary;

/**
 * Specialized TreeCellRenderer which attempts to identify
 * the wrapper used for the node and selects the appropriate
 * icon to place next to the value in the tree.
 * 
 * @author David Read
 * 
 */
public class OntologyTreeCellRenderer extends DefaultTreeCellRenderer {
  /**
   * Serial version Id
   */
  private static final long serialVersionUID = -4654608660501772470L;

  /**
   * No operation
   */
  public OntologyTreeCellRenderer() {

  }

  @Override
  public Component getTreeCellRendererComponent(
      JTree tree,
      Object value,
      boolean sel,
      boolean expanded,
      boolean leaf,
      int row,
      boolean hasFocus) {

    super.getTreeCellRendererComponent(
        tree, value, sel,
        expanded, leaf, row,
        hasFocus);

    if (!updateIcon(value, leaf, expanded)) {
      setToolTipText(null); // no tool tip
    }

    return this;
  }

  /**
   * Identify the correct icon to place next to this
   * tree node's value.
   * 
   * @see WrapperClass
   * @see WrapperDataProperty
   * @see WrapperInstance
   * @see WrapperObjectProperty
   * @see WrapperLiteral
   * 
   * @param value
   *          The tree node object
   * @param leaf
   *          Whether this is a leaf node
   * @param expanded
   *          Whether this node is expanded
   * 
   * @return True if a specialized icon was chosen for this tree node
   */
  private boolean updateIcon(Object value, boolean leaf, boolean expanded) {
    boolean hasSpecializedIcon = false;
    final Object object =
            ((DefaultMutableTreeNode) value).getUserObject();

    if (object instanceof WrapperLiteral) {
      setIcon(ImageLibrary.instance().getImageIcon(
          ImageLibrary.ICON_TREE_LITERAL_BOXED));
      setToolTipText("This is a literal value");
      hasSpecializedIcon = true;
    } else if (object instanceof WrapperClass) {
      setIcon(ImageLibrary.instance().getImageIcon(
          leaf ? ImageLibrary.ICON_TREE_CLASS_NOINSTANCES_BOXED
              : ImageLibrary.ICON_TREE_CLASS_BOXED));
      // setLeafIcon(ImageLibrary.instance().getIcon(
      // ImageLibrary.ICON_TREE_CLASS_NOINSTANCES_BOXED));
      setToolTipText("This is a class");
      hasSpecializedIcon = true;

    } else if (object instanceof WrapperInstance) {
      setIcon(ImageLibrary.instance().getImageIcon(
          leaf ? ImageLibrary.ICON_TREE_INSTANCE_NOPROPERTIES_BOXED
              : ImageLibrary.ICON_TREE_INSTANCE_BOXED));
      // setLeafIcon(ImageLibrary.instance().getIcon(
      // ImageLibrary.ICON_TREE_INSTANCE_NOPROPERTIES_BOXED));
      setToolTipText("This is an individual");
      hasSpecializedIcon = true;

    } else if (object instanceof WrapperDataProperty) {
      setIcon(ImageLibrary.instance().getImageIcon(
          ImageLibrary.ICON_TREE_PROPERTY_DATA_BOXED));
      setToolTipText("This is a data property");
      hasSpecializedIcon = true;

    } else if (object instanceof WrapperObjectProperty) {
      setIcon(ImageLibrary.instance().getImageIcon(
          ImageLibrary.ICON_TREE_PROPERTY_OBJECT_BOXED));
      setToolTipText("This is an object property");
      hasSpecializedIcon = true;

    }

    return hasSpecializedIcon;
  }
}
