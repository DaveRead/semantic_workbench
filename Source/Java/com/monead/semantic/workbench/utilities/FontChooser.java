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

import org.apache.log4j.Logger;

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
  /**
   * Logger Instance
   */
  private static final Logger LOGGER = Logger.getLogger(FontChooser.class);

  /**
   * Default font size
   */
  private static final int DEFAULT_FONT_SIZE = 12;

  /**
   * The color chooser
   */
  private JColorChooser colorChooser;

  /**
   * The font name
   */
  private JComboBox fontName;

  /**
   * Select bold font
   */
  private JCheckBox fontBold;

  /**
   * Select italic font
   */
  private JCheckBox fontItalic;

  /**
   * The font size
   */
  private JTextField fontSize;

  /**
   * Preview the configured font
   */
  private JLabel previewLabel;

  /**
   * The attributes selected
   */
  private SimpleAttributeSet attributes;

  /**
   * The font selected
   */
  private Font newFont;

  /**
   * The color selected
   */
  private Color newColor;

  /**
   * Thread for rendering the preview
   */
  private Thread previewThread;

  /**
   * Synchronized control of font settings
   */
  private List<FontData> changeStack;

  /**
   * Setup the dialog for setting the font and color
   * 
   * @param parent
   *          The parent frame
   */
  public FontChooser(Frame parent) {
    super(parent, "Font Chooser", true);
    // setSize(450, 450);
    attributes = new SimpleAttributeSet();
    changeStack = new ArrayList<FontData>();

    // Make sure that any way the user cancels the window does the right
    // thing
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        closeAndCancel();
      }
    });

    final Container c = getContentPane();

    final JPanel fontPanel = new JPanel();
    fontName = new JComboBox(new String[] {
        "TimesRoman", "Helvetica", "Courier"
    });
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

    final JPanel previewPanel = new JPanel(new BorderLayout());
    previewLabel = new JLabel("Here's a sample of this font.");
    previewLabel.setForeground(colorChooser.getColor());
    previewPanel.add(previewLabel, BorderLayout.CENTER);

    // Add in the Ok and Cancel buttons for our dialog box
    final JButton okButton = new JButton("Ok");
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        closeAndSave();
      }
    });

    final JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        closeAndCancel();
      }
    });

    final JPanel controlPanel = new JPanel();
    controlPanel.add(okButton);
    controlPanel.add(cancelButton);
    previewPanel.add(controlPanel, BorderLayout.SOUTH);

    // Give the preview label room to grow.
    previewPanel.setMinimumSize(new Dimension(100, 100));
    previewPanel.setPreferredSize(new Dimension(100, 100));

    c.add(previewPanel, BorderLayout.SOUTH);

    pack();
    
    GuiUtilities.centerWindow(this, parent);
  }

  /**
   * Detect when the font is changed and make a
   * new font for the preview label.
   * 
   * @param ae
   *          The action event
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
   * 
   * @param font
   *          The font selected
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
    updatePreviewFont();

    showPreview();
  }

  /**
   * Get the appropriate font from our attributes object and update
   * the preview label
   */
  protected void updatePreviewFont() {
    final String name = (String) fontName.getSelectedItem();
    final boolean bold = fontBold.isSelected();
    final boolean ital = fontItalic.isSelected();
    int size;

    try {
      size = Integer.parseInt(fontSize.getText());
    } catch (Throwable throwable) {
      size = DEFAULT_FONT_SIZE;
      System.out.println("Not a legitimate number for the font size");
      throwable.printStackTrace();
    }

    // Bold and italic don't work properly in beta 4.
    final Font f = new Font(name, (bold ? Font.BOLD : 0)
        | (ital ? Font.ITALIC : 0), size);

    newFont = f;
  }

  /**
   * Get the appropriate color from our chooser and update previewLabel.
   */
  protected void updatePreviewColor() {
    newColor = colorChooser.getColor();

    showPreview();
  }

  /**
   * Run this chooser.
   */
  public void run() {
    Font font;
    Color color;
    boolean more;

    do {
      synchronized (changeStack) {
        font = changeStack.get(0).getFont();
        color = changeStack.get(0).getColor();
        changeStack.remove(0);
      }
      try {
        previewLabel.setFont(font);
        previewLabel.setForeground(color);
      } catch (Throwable throwable) {
        LOGGER.warn("Error previewing the font choice", throwable);
      }
      synchronized (changeStack) {
        more = changeStack.size() > 0;
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
      previewThread = new Thread(this);
      previewThread.start();
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
    setVisible(false);
  }

  /**
   * Close the chooser dialog and discard the user's selections.
   */
  public void closeAndCancel() {
    newFont = null;
    newColor = null;
    setVisible(false);
  }

  @Override
  public void keyPressed(KeyEvent e) {

  }

  @Override
  public void keyReleased(KeyEvent e) {
    fontChanged();
  }

  @Override
  public void keyTyped(KeyEvent e) {

  }
}