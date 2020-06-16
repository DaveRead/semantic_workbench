package com.monead.semantic.workbench.utilities;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Formats values using XSD type to Java format mappings
 * 
 * @author David Read
 * 
 */
public class ValueFormatter {
  // private static final NumberFormat integerFormatter = new
  // DecimalFormat("#,##0");
  // private static final NumberFormat decimalFormatter = new
  // DecimalFormat("#,##0.0");

  /**
   * Singleton instance
   */
  private static final ValueFormatter INSTANCE = new ValueFormatter();

  /**
   * The collection of mappings from XSD type to Java format
   */
  private static final Map<String, NumberFormat> FORMATS = new HashMap<String, NumberFormat>();

  /**
   * Logger Instance
   */
  private static final Logger LOGGER = LogManager
      .getLogger(ValueFormatter.class);

  /**
   * Private constructor to support singleton pattern
   */
  private ValueFormatter() {

  }

  /**
   * Get the singleton instance
   * 
   * @return The single instance of this class
   */
  public static ValueFormatter getInstance() {
    return INSTANCE;
  }

  /**
   * Apply the format to the value. If the XSD type does not have a defined
   * format, or if the value supplied vcannot be formatted per the format
   * configuration, the original value is returned intact
   * 
   * @param value
   *          The value to be formatted
   * @param xsdType
   *          The XSD type of the value
   * 
   * @return The formatted value or the original value if formatting is not
   *         defined or cannot be applied
   */
  public String applyFormat(String value, String xsdType) {
    String formattedValue = value;
    String typeName;
    int hashPosition;
    NumberFormat formatter;

    if (xsdType != null) {
      typeName = xsdType;
      if ((hashPosition = typeName.indexOf('#')) > -1) {
        typeName = typeName.substring(hashPosition + 1);
      }

      formatter = FORMATS.get(typeName);

      if (formatter != null) {
        try {
          formattedValue = formatter.format(Double.parseDouble(value));
        } catch (Throwable throwable) {
          LOGGER.debug("Error formatting value [" + value + "]", throwable);
        }
      }
    }

    return formattedValue;
  }

  /**
   * Add an XSD to Java Format mapping
   * 
   * @param xsdType
   *          The XSD type (e.g. double)
   * @param decimalFormatMask
   *          The format mask to apply (e.g. #,##0.0)
   */
  public static void setFormat(String xsdType, String decimalFormatMask) {
    FORMATS.put(xsdType, new DecimalFormat(decimalFormatMask));
  }
}
