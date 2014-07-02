package com.monead.semantic.workbench;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;

/**
 * A renderer for SPARQL results. This is used by the JTable to render the
 * SparqlResultItem instances.
 * 
 * @see SparqlResultItem
 */
public class SparqlResultItemRenderer extends JTextArea implements
    TableCellRenderer {
  /**
   * Logger Instance
   */
  private static final Logger LOGGER = Logger
      .getLogger(SparqlResultItemRenderer.class);

  /**
   * Serial UID
   */
  private static final long serialVersionUID = -6942939194476788279L;

  /**
   * The image for the current cell. Null if the current cell is not an image
   */
  private ImageIcon myImageIcon;

  /**
   * Detect if this is the first cell being rendered. THIS IS A HACK. For some
   * reason the preferred height for the first cell is always the available
   * height of the JTable instead of being properly calculated as the height
   * required by the text of the cell. All subsequent calls that ask for the
   * preferred height work fine.
   */
  private boolean firstDimValue = true;

  /**
   * The tallest column in the current row. The row is set to this height
   */
  private Map<Integer, Integer> highestCol = new HashMap<Integer, Integer>();

  /**
   * Create a renderer. Whether lines are allowed to wrap is optional. If cells
   * cannot wrap then the rows will be a fixed and consistent height (other than
   * those containing images).
   * 
   * @param supportWrappedLines
   *          True if long lines of text should wraped in the cell. Otherwise
   *          they will be truncated.
   */
  public SparqlResultItemRenderer(boolean supportWrappedLines) {
    LOGGER.debug("Renderer instance created");
    setLineWrap(supportWrappedLines);
    setWrapStyleWord(supportWrappedLines);
    setOpaque(true);
  }

  /**
   * Set the font for this renderer
   * 
   * @param newFont
   *          The font to be set
   */
  public void setFont(Font newFont) {
    super.setFont(newFont);
  }

  /**
   * Get the underlying component for this renderer. The JTable will use this to
   * get the proper cell display value.
   * 
   * @param table
   *          The table used to render the data
   * @param value
   *          The value to be rendered
   * @param isSelected
   *          Is the cell selected
   * @param hasFocus
   *          Does the cell have focus
   * @param row
   *          The row for this component
   * @param column
   *          The column for this component
   * 
   * @return The component used to render the value in the table
   */
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    Integer highestColumnForThisRow = highestCol.get(row);

    if (highestColumnForThisRow == null) {
      highestColumnForThisRow = 1;
      highestCol.put(row, highestColumnForThisRow);
    }

    if (isSelected) {
      setForeground(table.getSelectionForeground());
      setBackground(table.getSelectionBackground());
    } else {
      setForeground(table.getForeground());
      setBackground(table.getBackground());
    }

    if (hasFocus) {
      setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
    } else {
      setBorder(new EmptyBorder(1, 2, 1, 2));
    }

    if (value instanceof SparqlResultItem) {
      final SparqlResultItem resultValue = (SparqlResultItem) value;

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Value is a SparqlResultItem, image? "
            + resultValue.isImageIcon());
      }

      if (resultValue.isImageIcon()) {
        setOpaque(false);
        myImageIcon = resultValue.getImageIcon();
        setText(null);
        if (resultValue.getImageIcon().getIconHeight() > highestColumnForThisRow) {
          highestCol.put(row, resultValue.getImageIcon().getIconHeight());
        }
      } else {
        setOpaque(true);
        myImageIcon = null;
        setText(resultValue.toString().trim());

        // Adjust column width so preferred can calculate required height
        final int cWidth = table.getTableHeader().getColumnModel()
            .getColumn(column).getWidth();

        if (LOGGER.isTraceEnabled()) {
          LOGGER.trace("Width for row " + row + " = " + cWidth);
          LOGGER.trace("Height info for [" + resultValue.toString().trim()
              + "] = " + getMinimumSize().height + ", " + getHeight() + ", "
              + getRowHeight() + ", " + getRows());
        }

        if (getLineWrap()) {
          setSize(new Dimension(cWidth, 1));
        }

        if (getPreferredSize().height > highestColumnForThisRow) {
          if (!firstDimValue) {
            highestCol.put(row, getPreferredSize().height);
          } else {
            firstDimValue = false;
          }
        }
      }
    } else {
      LOGGER.trace("Value is not a SparqlResultItem");
      setOpaque(true);
      myImageIcon = null;
      setText(value.toString().trim());

      // Adjust column width so preferred can calculate required height
      final int cWidth = table.getTableHeader().getColumnModel()
          .getColumn(column)
          .getWidth();

      if (getLineWrap()) {
        setSize(new Dimension(cWidth, 1));
      }

      if (getPreferredSize().height > highestColumnForThisRow) {
        if (!firstDimValue) {
          LOGGER.trace("Store row " + row + " height");
          highestCol.put(row, getPreferredSize().height);
        } else {
          LOGGER.trace("Ignore row " + row + " height");
          firstDimValue = false;
        }
      }
    }

    table.setRowHeight(row, highestCol.get(row));

    return this;
  }

  /**
   * Paint the component - overridden in order to support images
   * 
   * @param g
   *          The graphics object
   */
  public void paintComponent(Graphics g) {
    if (myImageIcon != null) {
      g.drawImage(myImageIcon.getImage(), 0, 0, this);
    }
    super.paintComponent(g);
  }
}
