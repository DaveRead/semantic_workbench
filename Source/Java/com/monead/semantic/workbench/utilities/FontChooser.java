package com.monead.semantic.workbench.utilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;

/**
 * FontChooser
 * 
 * From: http://examples.oreilly.com/jswing2/code/ch12/FontChooser.java
 * 
 * A font chooser that allows users to pick a font by name, size, style, and
 * color. The color selection is provided by a JColorChooser pane. This dialog
 * builds an AttributeSet suitable for use with JTextPane.
 * 
 * DSR: Minor alteration to make all attributes private
 */
@SuppressWarnings("serial")
public class FontChooser extends JDialog implements Runnable, ActionListener,
    KeyListener {

  private JColorChooser colorChooser;
  private JComboBox fontName;
  private JCheckBox fontBold, fontItalic;
  private JTextField fontSize;
  private JLabel previewLabel;
  private SimpleAttributeSet attributes;
  private Font newFont;
  private Color newColor;
  private Thread previewThread;
  private List<FontData> changeStack;

  public FontChooser(Frame parent) {
    super(parent, "Font Chooser", true);
    setSize(450, 450);
    attributes = new SimpleAttributeSet();
    changeStack = new ArrayList<FontData>();

    // Make sure that any way the user cancels the window does the right
    // thing
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        closeAndCancel();
      }
    });

    // Start the long process of setting up our interface
    Container c = getContentPane();

    JPanel fontPanel = new JPanel();
    fontName = new JComboBox(new String[] { "TimesRoman", "Helvetica",
        "Courier" });
    fontName.setSelectedIndex(1);
    fontName.addActionListener(this);
    fontSize = new JTextField("12", 4);
    fontSize.setHorizontalAlignment(SwingConstants.RIGHT);
    fontSize.addActionListener(this);
    fontSize.addKeyListener(this);
    fontBold = new JCheckBox("Bold");
    fontBold.setSelected(true);
    fontBold.addActionListener(this);
    fontItalic = new JCheckBox("Italic");
    fontItalic.addActionListener(this);

    fontPanel.add(fontName);
    fontPanel.add(new JLabel(" Size: "));
    fontPanel.add(fontSize);
    fontPanel.add(fontBold);
    fontPanel.add(fontItalic);

    c.add(fontPanel, BorderLayout.NORTH);

    // Set up the color chooser panel and attach a change listener so that
    // color
    // updates get reflected in our preview label.
    colorChooser = new JColorChooser(Color.black);
    colorChooser.getSelectionModel().addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            updatePreviewColor();
          }
        });
    c.add(colorChooser, BorderLayout.CENTER);

    JPanel previewPanel = new JPanel(new BorderLayout());
    previewLabel = new JLabel("Here's a sample of this font.");
    previewLabel.setForeground(colorChooser.getColor());
    previewPanel.add(previewLabel, BorderLayout.CENTER);

    // Add in the Ok and Cancel buttons for our dialog box
    JButton okButton = new JButton("Ok");
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        closeAndSave();
      }
    });
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        closeAndCancel();
      }
    });

    JPanel controlPanel = new JPanel();
    controlPanel.add(okButton);
    controlPanel.add(cancelButton);
    previewPanel.add(controlPanel, BorderLayout.SOUTH);

    // Give the preview label room to grow.
    previewPanel.setMinimumSize(new Dimension(100, 100));
    previewPanel.setPreferredSize(new Dimension(100, 100));

    c.add(previewPanel, BorderLayout.SOUTH);
  }

  /**
   * Detect when the font is changed and make a
   * new font for the preview label.
   */
  public void actionPerformed(ActionEvent ae) {
    fontChanged();
  }

  /**
   * Set the initial color for the color chooser.
   * 
   * @param color
   *          The initial color that the chooser should have selected
   */
  public void setColor(Color color) {
    colorChooser.setColor(color);
  }

  /**
   * Set the initial font for the font chooser.
   */
  public void setFont(Font font) {
    fontSize.setText(font.getSize() + "");
    fontBold.setSelected((font.getStyle() & Font.BOLD) == Font.BOLD);
    fontItalic.setSelected((font.getStyle() & Font.ITALIC) == Font.ITALIC);

    for (int index = 0; index < fontName.getItemCount(); ++index) {
      if (fontName.getItemAt(index).toString().equalsIgnoreCase(font.getName())) {
        fontName.setSelectedIndex(index);
      }
    }
  }

  /**
   * Process the new font and update the preview field.
   */
  private void fontChanged() {
    System.out.println("0: " + new java.util.Date());
    updatePreviewFont();
    System.out.println("0.1: " + new java.util.Date());
    showPreview();
    System.out.println("0.2: " + new java.util.Date());
  }

  /**
   * Get the appropriate font from our attributes object and update
   * the preview label
   */
  protected void updatePreviewFont() {
    // String name = StyleConstants.getFontFamily(attributes);
    // boolean bold = StyleConstants.isBold(attributes);
    // boolean ital = StyleConstants.isItalic(attributes);
    // int size = StyleConstants.getFontSize(attributes);

    System.out.println("5: " + new java.util.Date());
    String name = (String) fontName.getSelectedItem();
    boolean bold = fontBold.isSelected();
    boolean ital = fontItalic.isSelected();
    int size;
    try {
      size = Integer.parseInt(fontSize.getText());
    } catch (Throwable throwable) {
      size = 12; // Default
      System.out.println("Not a legitimate number for the font size");
      throwable.printStackTrace();
    }

    // Bold and italic don't work properly in beta 4.
    System.out.println("5.1: " + new java.util.Date());
    Font f = new Font(name, (bold ? Font.BOLD : 0)
        | (ital ? Font.ITALIC : 0), size);
    // previewLabel.setFont(f);
    System.out.println("6: " + new java.util.Date());
    newFont = f;
  }

  /**
   * Get the appropriate color from our chooser and update previewLabel.
   */
  protected void updatePreviewColor() {
    // previewLabel.setForeground(colorChooser.getColor());
    System.out.println("7: " + new java.util.Date());
    newColor = colorChooser.getColor();
    // System.out.println("New Color: " + newColor);
    // Manually force the label to repaint
    // previewLabel.repaint();
    showPreview();
  }

  /**
   * Run this chooser.
   */
  public void run() {
    Font font;
    Color color;
    boolean more;

    System.out.println("8: " + new java.util.Date());

    do {
      synchronized (changeStack) {
        System.out.println("8.1: " + new java.util.Date());
        font = changeStack.get(0).getFont();
        color = changeStack.get(0).getColor();
        changeStack.remove(0);
      }
      try {
        System.out.println("8.2: " + new java.util.Date());
        previewLabel.setFont(font);
        System.out.println("8.3: " + new java.util.Date());
        previewLabel.setForeground(color);
        System.out.println("8.4: " + new java.util.Date());
        /*
         * for (int x = 0; x < 10; ++x) {
         * previewLabel.setText("Sample Text Message (" + x + ")");
         * try {
         * Thread.sleep(500);
         * } catch (InterruptedException ie) {
         * System.out.println("Interrupted exception");
         * ie.printStackTrace();
         * }
         * }
         */
      } catch (Throwable throwable) {
        // Ignore any errors
      }
      synchronized (changeStack) {
        more = changeStack.size() > 0;
        System.out.println("8.5: " + new java.util.Date());
      }
    } while (more);
    previewThread = null;
  }

  /**
   * Show the preview window.
   */
  private synchronized void showPreview() {
    synchronized (changeStack) {
      changeStack.add(new FontData(newFont, newColor));
    }
    if (previewThread == null) {
      System.out.println("9: " + new java.util.Date());
      previewThread = new Thread(this);
      System.out.println("9.1: " + new java.util.Date());
      previewThread.start();
      System.out.println("9.2: " + new java.util.Date());
    }
  }

  /**
   * Get the currently selected Font.
   * 
   * @return The currently selected Font
   */
  public Font getNewFont() {
    return newFont;
  }

  /**
   * Get the currently selected color.
   * 
   * @return The currently selected Color
   */
  public Color getNewColor() {
    return newColor;
  }

  /**
   * Get the current set of attributes associated with the selected font.
   * 
   * @return The current set of attributes for the font
   */
  public AttributeSet getAttributes() {
    return attributes;
  }

  /**
   * Close the chooser dialog and save the user's selections.
   */
  public void closeAndSave() {
    // Save font & color information
    // newFont = previewLabel.getFont();
    // newColor = previewLabel.getForeground();

    // Close the window
    setVisible(false);
  }

  /**
   * Close the chooser dialog and discard the user's selections.
   */
  public void closeAndCancel() {
    // Erase any font information and then close the window
    newFont = null;
    newColor = null;
    setVisible(false);
  }

  @Override
  public void keyPressed(KeyEvent e) {

  }

  @Override
  public void keyReleased(KeyEvent e) {
    // if (key.getKeyCode() == 127) { // Delete key - no keyTyped Event
    System.out.println("10: " + new java.util.Date());
    fontChanged();
    System.out.println("10.1: " + new java.util.Date());
    // }
  }

  @Override
  public void keyTyped(KeyEvent e) {

  }
}

