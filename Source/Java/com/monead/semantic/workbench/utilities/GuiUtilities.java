package com.monead.semantic.workbench.utilities;

import java.util.Date;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.awt.Toolkit;
import java.awt.Point;
import java.awt.Window;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import javax.swing.table.TableColumn;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.awt.Component;
import java.text.ParseException;


/**
 * <p>Title: Utilities</p>
 *
 * <p>Description: General utilities for manipulating data</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Blue Slate Solutions</p>
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
    private static Logger logger = Logger.getLogger(GuiUtilities.class);

    /**
     * Private since no one should create instances of this class.
     */
    private GuiUtilities() {
    }

    /**
     * Convert a date to String.
     *
     * @param date Date The date instance.
     * @param format String The format string to apply to the date.
     *
     * @return String The resulting date as a String.
     */
    public static String dateToString(Date date, String format) {
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        return fmt.format(date);
    }

    /**
     * Convert a String representation of a date to a Date instance.
     *
     * @param dateText String The date as a String.
     * @param format String The format of the Stirng date.
     *
     * @return Date The resulting Date instance.  If the format fails this
     *         value will be null.
     */
    public static Date stringToDate(String dateText, String format) throws
            ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        Date date;

        date = fmt.parse(dateText);

        return date;
    }

    /**
     * Create a Strong representation of a date suitable for inserting into a
     * database date field as a string.
     *
     * @param date Date The date to be formatted.
     *
     * @return String The formatted date.
     */
    public static String dateForSQL(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat(FORMAT_SQLDATE);
        return fmt.format(date);
    }

    /**
     * Calculate the number of years between two dates.
     *
     * @param startDate Date The beginning date (earlier)
     * @param endDate Date The ending date (later)
     *
     * @return int The number of years between dates.  If startDate>ndDate
     *         then this value will be <0.
     */
    public static int yearsBetweenDates(Date startDate, Date endDate) {
        GregorianCalendar startCal, endCal;

        startCal = new GregorianCalendar();
        startCal.setTime(startDate);
        endCal = new GregorianCalendar();
        endCal.setTime(endDate);

        return endCal.get(GregorianCalendar.YEAR) -
                startCal.get(GregorianCalendar.YEAR);
    }

    /**
     * Centers a window on the parent window or the screen.  If the parent
     * is null, or smaller than the child, the child is centered on the
     * screen.
     *
     * @param winaChild The window being centered.
     * @param winaParent The parent window, or null if no parent frame exists.
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

            if (dimlMySize.width >= dimlParentSize.width ||
                dimlMySize.height >= dimlParentSize.height) {
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

    public static void initColumnSizes(JTable table, TableModel model) {
      TableColumn column = null;
      Component comp = null;
      int headerWidth = 0;
      int cellWidth = 0;
      Object[] longValues = getLongestValues(model);

      for (int i = 0; i < longValues.length; i++) {
        column = table.getColumnModel().getColumn(i);

        try {
//                comp = column.getHeaderRenderer().
//                                 getTableCellRendererComponent(
//                                     null, column.getHeaderValue(),
//                                     false, false, 0, 0);
          comp = table.getTableHeader().getDefaultRenderer().
              getTableCellRendererComponent(
              null, column.getHeaderValue() + "W",
              false, false, 0, 0);
          headerWidth = comp.getPreferredSize().width;

          // Periodically the return value from getPreferredSize() is huge
          // No idea what is going on - this recheck seems to always come back
          // with a sane value
          if (headerWidth > 10000) {
            logger.debug("Header unusually wide (" +
                headerWidth + "): calc again");

            headerWidth = comp.getPreferredSize().width;

            logger.debug("Result of header recalc (" +
                headerWidth + ")");
          }
        }
        catch (NullPointerException e) {
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
          logger.debug("Column unusually wide (" +
              cellWidth + "): calc again");

          cellWidth = comp.getPreferredSize().width;

          logger.debug("Result of recalc (" +
              cellWidth + ")");
        }

        logger.debug("Initializing width of column "
            + i + ". "
            + "headerWidth = " + headerWidth
            + "; cellWidth = " + cellWidth
            + "; longValue = [" + longValues[i] + "]");

        //NOTE: Before Swing 1.1 Beta 2, use setMinWidth instead.
        column.setPreferredWidth(Math.max(headerWidth, cellWidth));
      }
    }

    /**
     * Gets longest values , one for each column
     *
     * @param model The DefaultTable model
     *
     * @return obj1longest Object that has the longest value for each column
     */
    public static Object[] getLongestValues(TableModel model) {
      Object objlLongest[], objlValue;
      int ilLen[], ilThisLen;
      int ilNumRows, ilRow, ilNumCols, ilCol;

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
              logger.debug(
                  "Get longest value, Checking(" + ilRow + "," +
                  ilCol + ")=" + ilThisLen);
            }
          }
        }
      }

      return objlLongest;
    }

    public final static Component flowPanel(Component component, int align) {
        JPanel panel;
        panel = new JPanel();
        panel.setLayout(new FlowLayout(align));
        panel.add(component);

        return panel;
    }
}
