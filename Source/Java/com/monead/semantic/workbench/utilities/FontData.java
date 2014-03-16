package com.monead.semantic.workbench.utilities;

import java.awt.Color;
import java.awt.Font;

/**
 * Class for housing information for a font and forground color selection.
 * 
 * @author David Read
 * 
 */
public class FontData {
  /**
   * A Font instance
   */
  private Font font;

  /**
   * A Color instance (foreground)
   */
  private Color color;

  /**
   * Create a FontData instance with the supplied Font and foreground Color.
   * 
   * @param pFont
   *          The Font instance
   * @param pColor
   *          The foreground Color instance
   */
  public FontData(Font pFont, Color pColor) {
    setFont(pFont);
    setColor(pColor);
  }

  /**
   * Set the Font instance for this FontData instance.
   * 
   * @param pFont
   *          The Font being set.
   */
  private void setFont(Font pFont) {
    font = pFont;
  }

  /**
   * Get the current Font from this FontData instance.
   * 
   * @return The current Font
   */
  public Font getFont() {
    return font;
  }

  /**
   * Set the foreground Color instance for this FontData instance.
   * 
   * @param pColor
   *          The foreground Color
   */
  private void setColor(Color pColor) {
    color = pColor;
  }

  /**
   * Get the current foreground Color from theis FontData instance.
   * 
   * @return The current foreground Color
   */
  public Color getColor() {
    return color;
  }
}
