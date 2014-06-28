package com.monead.semantic.workbench.utilities;

import java.util.Date;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Point;
import java.awt.Window;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import javax.swing.table.TableColumn;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.awt.Component;
import java.text.ParseException;

/**
 * <p>
 * Title: Utilities
 * </p>
 * 
 * <p>
 * Description: General utilities for manipulating data
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * 
 * <p>
 * Company: Blue Slate Solutions
 * </p>
 * 
 * @author David Read
 * @version $Id$
 */
public class GuiUtilities {
  /**
   * Date format used when inserting dates into the database as Strings.
   */
  private static final String FORMAT_SQLDATE = "yyyy-MM-dd";

  /**
   * Logger Instance
   */
  private static final Logger LOGGER = Logger.getLogger(GuiUtilities.class);

  /**
   * Fraction of screen size the restored window dimension may occupy
   */
  private static final double MAXIMUM_RESTORE_RELATIVE_WINDOW_TO_SCREEN_SIZE = 0.9;

  /**
   * Private since no one should create instances of this class.
   */
  private GuiUtilities() {
  }

  /**
   * Convert a date to String.
   * 
   * @param date
   *          Date The date instance.
   * @param format
   *          String The format string to apply to the date.
   * 
   * @return The resulting date as a String.
   */
  public static String dateToString(Date date, String format) {
    final SimpleDateFormat fmt = new SimpleDateFormat(format);
    return fmt.format(date);
  }

  /**
   * Convert a String representation of a date to a Date instance.
   * 
   * @param dateText
   *          String The date as a String.
   * @param format
   *          String The format of the Stirng date.
   * 
   * @return The resulting Date instance. If the format fails this
   *         value will be null.
   * 
   * @throws ParseException
   *           If the date cannot be parsed
   */
  public static Date stringToDate(String dateText, String format) throws
            ParseException {
    final SimpleDateFormat fmt = new SimpleDateFormat(format);
    Date date;

    date = fmt.parse(dateText);

    return date;
  }

  /**
   * Create a Strong representation of a date suitable for inserting into a
   * database date field as a string.
   * 
   * @param date
   *          Date The date to be formatted.
   * 
   * @return The formatted date.
   */
  public static String dateForSQL(Date date) {
    final SimpleDateFormat fmt = new SimpleDateFormat(FORMAT_SQLDATE);
    return fmt.format(date);
  }

  /**
   * Calculate the number of years between two dates.
   * 
   * @param startDate
   *          Date The beginning date (earlier)
   * @param endDate
   *          Date The ending date (later)
   * 
   * @return The number of years between dates. If startDate greater than
   *         endDate then this value will be less than 0.
   */
  public static int yearsBetweenDates(Date startDate, Date endDate) {
    GregorianCalendar startCal, endCal;

    startCal = new GregorianCalendar();
    startCal.setTime(startDate);
    endCal = new GregorianCalendar();
    endCal.setTime(endDate);

    return endCal.get(GregorianCalendar.YEAR)
        - startCal.get(GregorianCalendar.YEAR);
  }

  /**
   * Centers a window on the parent window or the screen. If the parent
   * is null, or smaller than the child, the child is centered on the
   * screen.
   * 
   * @param winaChild
   *          The window being centered.
   * @param winaParent
   *          The parent window, or null if no parent frame exists.
   */
  public static void centerWindow(Window winaChild, Window winaParent) {
    Dimension dimlParentSize, dimlMySize;
    Point ptlUpperLeft;
    int ilX, ilY;
    dimlMySize = winaChild.getSize();
    if (winaParent == null) {
      dimlParentSize = Toolkit.getDefaultToolkit().getScreenSize();
      ptlUpperLeft = new Point(0, 0);
    } else {
      dimlParentSize = winaParent.getSize();
      ptlUpperLeft = winaParent.getLocation();

      if (dimlMySize.width >= dimlParentSize.width
          || dimlMySize.height >= dimlParentSize.height) {
        dimlParentSize = Toolkit.getDefaultToolkit().getScreenSize();
        ptlUpperLeft = new Point(0, 0);
      }
    }
    if (dimlParentSize.width >= dimlMySize.width) {
      ilX = (dimlParentSize.width - dimlMySize.width) / 2;
    } else {
      ilX = 0;
    }
    if (dimlParentSize.height >= dimlMySize.height) {
      ilY = (dimlParentSize.height - dimlMySize.height) / 2;
    } else {
      ilY = 0;
    }
    ilX += ptlUpperLeft.x;
    ilY += ptlUpperLeft.y;
    winaChild.setLocation(ilX, ilY);
  }

  /*
   * This method picks good column sizes.
   * If all column heads are wider than the column's cells'
   * contents, then you can just use column.sizeWidthToFit().
   */

  /**
   * This method picks good column sizes. If all column heads are wider than the
   * column's cells' contents, then you can just use column.sizeWidthToFit().
   * 
   * @param table
   *          The table being rendered
   * @param model
   *          The model backing the table
   */
  public static void initColumnSizes(JTable table, TableModel model) {
    TableColumn column = null;
    Component comp = null;
    int headerWidth = 0;
    int cellWidth = 0;
    final Object[] longValues = getLongestValues(model);

    for (int i = 0; i < longValues.length; i++) {
      column = table.getColumnModel().getColumn(i);

      try {
        comp = table.getTableHeader().getDefaultRenderer().
              getTableCellRendererComponent(
                  null, column.getHeaderValue() + "W",
                  false, false, 0, 0);
        headerWidth = comp.getPreferredSize().width;

        // Periodically the return value from getPreferredSize() is huge
        // No idea what is going on - this recheck seems to always come back
        // with a sane value
        if (headerWidth > 10000) {
          LOGGER.debug("Header unusually wide ("
              + headerWidth + "): calc again");

          headerWidth = comp.getPreferredSize().width;

          LOGGER.debug("Result of header recalc ("
              + headerWidth + ")");
        }
      } catch (NullPointerException e) {
        System.err.println("Null pointer exception!");
        System.err.println("  getHeaderRenderer returns null in 1.3.");
        System.err.println("  The replacement is getDefaultRenderer.");
      }

      comp = table.getDefaultRenderer(model.getColumnClass(i)).
            getTableCellRendererComponent(
                table, longValues[i] + "W",
                false, false, 0, i);
      cellWidth = comp.getPreferredSize().width;

      // Periodically the return value from getPreferredSize() is huge
      // No idea what is going on - this recheck seems to always come back
      // with a sane value
      if (cellWidth > 10000) {
        LOGGER.debug("Column unusually wide ("
            + cellWidth + "): calc again");

        cellWidth = comp.getPreferredSize().width;

        LOGGER.debug("Result of recalc ("
            + cellWidth + ")");
      }

      LOGGER.debug("Initializing width of column "
            + i + ". "
            + "headerWidth = " + headerWidth
            + "; cellWidth = " + cellWidth
            + "; longValue = [" + longValues[i] + "]");

      // NOTE: Before Swing 1.1 Beta 2, use setMinWidth instead.
      column.setPreferredWidth(Math.max(headerWidth, cellWidth));
    }
  }

  /**
   * Gets longest values , one for each column
   * 
   * @param model
   *          The DefaultTable model
   * 
   * @return obj1longest Object that has the longest value for each column
   */
  public static Object[] getLongestValues(TableModel model) {
    Object[] objlLongest;
    Object objlValue;
    int[] ilLen;
    int ilThisLen;
    int ilNumRows;
    int ilRow;
    int ilNumCols;
    int ilCol;

    ilNumCols = model.getColumnCount();
    ilNumRows = model.getRowCount();

    objlLongest = new Object[ilNumCols];
    ilLen = new int[ilNumCols];

    for (ilCol = 0; ilCol < ilNumCols; ++ilCol) {
      objlLongest[ilCol] = "";
      ilLen[ilCol] = 0;
    }

    for (ilRow = 0; ilRow < ilNumRows; ++ilRow) {
      for (ilCol = 0; ilCol < ilNumCols; ++ilCol) {
        objlValue = model.getValueAt(ilRow, ilCol);
        if (objlValue != null) {
          if ((ilThisLen = objlValue.toString().length()) > ilLen[ilCol]) {
            objlLongest[ilCol] = objlValue;
            ilLen[ilCol] = ilThisLen;
            LOGGER.debug("Get longest value, Checking(" + ilRow + "," + ilCol
                + ")=" + ilThisLen);
          }
        }
      }
    }

    return objlLongest;
  }

  /**
   * Add a panel with a flow layout to a component
   * 
   * @param component
   *          The component to add the panel to
   * @param align
   *          The alignment setting for the flow layout
   * 
   * @return The created panel
   */
  public static final Component flowPanel(Component component, int align) {
    JPanel panel;
    panel = new JPanel();
    panel.setLayout(new FlowLayout(align));
    panel.add(component);

    return panel;
  }

  /**
   * Toggle commented text. This supports single-line comment characters
   * (assumes comment characters precede each line, not multi-line comment
   * character pairs).
   * 
   * @param jTextArea
   *          The text area whose selected lines are to have comments toggled
   * @param commentPattern
   *          The single-line comment pattern - such as # for SPARQL or // for C
   */
  public static final void commentToggle(JTextArea jTextArea,
      String commentPattern) {
    final int selectionStart = jTextArea.getSelectionStart();
    final int selectionEnd = jTextArea.getSelectionEnd();
    final String text = jTextArea.getText();
    final StringBuffer updatedText = new StringBuffer();
    int caratPosition = jTextArea.getCaretPosition();
    final Rectangle visible = jTextArea.getVisibleRect();

    if (selectionEnd > selectionStart) {
      final String[] lines = text.substring(selectionStart, selectionEnd)
          .split("\\r?\\n", -1);

      LOGGER.debug("Number of selected lines: " + lines.length);

      // Start with the portion of query prior to the selection
      if (selectionStart > 0) {
        updatedText.append(text.substring(0, selectionStart));
      }

      // Toggle the comment character for each selected line (or partial line)
      for (int line = 0; line < lines.length; ++line) {
        if (line > 0) {
          updatedText.append('\n');
        }

        /*
         * If the selection ends at beginning of a line the user will not expect
         * that line to be toggled
         */
        if (line + 1 == lines.length && lines[line].length() == 0) {
          continue;
        }

        if (lines[line].trim().startsWith(commentPattern)) {
          final int commentPatternStart = lines[line].indexOf(commentPattern);
          if (commentPatternStart > 0) {
            updatedText.append(lines[line].substring(0, commentPatternStart));
          }
          updatedText.append(lines[line].substring(lines[line]
              .indexOf(commentPattern) + commentPattern.length()));
          caratPosition -= commentPattern.length();
        } else {
          updatedText.append(commentPattern);
          updatedText.append(lines[line]);
          caratPosition += commentPattern.length();
        }
      }

      // End with the portion of the query beyond the selection
      if (selectionEnd < text.length()) {
        updatedText.append(text.substring(selectionEnd));
      }

      // Replace the query text and place view at same place
      jTextArea.setText(updatedText.toString());
      jTextArea.setCaretPosition(caratPosition);
      jTextArea.scrollRectToVisible(visible);
    }
  }

  /**
   * Open the window at its last location and last dimensions. If the window
   * will not fit on screen then assume that the screen dimensions
   * have change and move to upper left of 0, 0 and set width and height
   * to previous dimensions if they fit or reduce to 90% of screen dim.
   * 
   * If there is no window position or size information then simply assure that
   * the default window size is no more than 90% of screen height and width.
   * 
   * @param window The window to be adjusted
   * @param lastHeight The last height of the window
   * @param lastWidth The last width of the window
   * @param lastTopX The last top X position of the window
   * @param lastTopY The last top Y position of the window
   */
  public static void windowSizing(Window window, String lastHeight,
      String lastWidth, String lastTopX, String lastTopY) {
    boolean resizeRequired = false;
    boolean usePrevious = false;
    int previousHeight = 0;
    int previousWidth = 0;
    int previousTopX = 0;
    int previousTopY = 0;
    double setHeight = window.getSize().getHeight();
    double setWidth = window.getSize().getWidth();
    final double screenHeight = Toolkit.getDefaultToolkit().getScreenSize()
        .getHeight();
    final double screenWidth = Toolkit.getDefaultToolkit().getScreenSize()
        .getWidth();

    try {
      previousHeight = Integer.parseInt(lastHeight);
      previousWidth = Integer.parseInt(lastWidth);
      previousTopX = Integer.parseInt(lastTopX);
      previousTopY = Integer.parseInt(lastTopY);
      if (previousHeight > 0 && previousWidth > 0 && previousTopX >= 0
          && previousTopY >= 0) {
        usePrevious = true;
      }
    } catch (Throwable throwable) {
      LOGGER.warn("Illegal window position or size property value, ignoring",
          throwable);
    }

    if (usePrevious && previousTopX + previousWidth < screenWidth
        && previousTopY + previousHeight < screenHeight) {
      window.setLocation(previousTopX, previousTopY);
      setWidth = previousWidth;
      setHeight = previousHeight;
      resizeRequired = true;
    } else {
      if (screenHeight * MAXIMUM_RESTORE_RELATIVE_WINDOW_TO_SCREEN_SIZE < setHeight) {
        setHeight = screenHeight
            * MAXIMUM_RESTORE_RELATIVE_WINDOW_TO_SCREEN_SIZE;
        resizeRequired = true;
      }
      if (screenWidth * MAXIMUM_RESTORE_RELATIVE_WINDOW_TO_SCREEN_SIZE < setWidth) {
        setWidth = screenWidth * MAXIMUM_RESTORE_RELATIVE_WINDOW_TO_SCREEN_SIZE;
        resizeRequired = true;
      }
    }

    if (resizeRequired) {
      window.setSize((int) setWidth, (int) setHeight);
    }
  }
}
