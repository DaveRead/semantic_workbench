package com.monead.semantic.workbench.queries;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Iterator;

import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * Export SPARQL results as JSON
 * 
 * <p>
 * Copyright: Copyright (c) 2015, David Read
 * </p>
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * </p>
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * </p>
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * </p>
 * <p>
 * </p>
 * 
 * @author David Read
 * 
 */
public class JsonExporter {
  /**
   * Logger Instance
   */
  private static final Logger LOGGER = Logger
      .getLogger(JsonExporter.class);

  /**
   * Default format for any flating point values
   */
  private static final DecimalFormat FLOAT_FORMAT = new DecimalFormat("0.#");

  /**
   * The Jackson JSON Factory instance
   */
  private JsonFactory jsonFactory = new JsonFactory();

  /**
   * The JSON generator
   */
  private JsonGenerator jg;

  /**
   * The file being written
   */
  private File file;

  /**
   * Create an exporter that will write to the supplied file.
   * 
   * @param outputFile
   *          The file to write
   */
  public JsonExporter(File outputFile) {
    file = outputFile;

    try {
      jg = jsonFactory.createGenerator(file, JsonEncoding.UTF8);
      jg.configure(Feature.QUOTE_FIELD_NAMES, true);
      jg.configure(Feature.QUOTE_NON_NUMERIC_NUMBERS, true);
      jg.setPrettyPrinter(new MinimalPrettyPrinter("\n"));
    } catch (IOException ioe) {
      LOGGER.error("Unable to create JSON file: " + file, ioe);
      throw new IllegalStateException("Unable to create JSON file: " + file,
          ioe);
    }
  }

  /**
   * Close the JSON generator and hence the file being written.
   */
  public void close() {
    try {
      if (jg != null) {
        jg.close();
      }
    } catch (IOException ioe) {
      LOGGER.error("Unable to close JSON file: " + file, ioe);
      throw new IllegalStateException("Unable to close JSON file: " + file, ioe);
    }
  }

  /**
   * Check that the generator is writable.
   * 
   * @return True if the generator is writeable
   */
  public boolean isWritable() {
    return jg != null;
  }

  /**
   * Test the state of the generator. If the generator is closed and no further
   * data will be written and exception will be thrown.
   */
  private void testState() {
    if (jg == null) {
      throw new IllegalStateException("This exporter has already been closed");
    }
  }

  /**
   * Write a SPARQL result set to the file
   * 
   * @param results
   *          The SPARQL result set
   */
  public void writeResultSet(ResultSet results) {
    testState();

    try {
      while (results.hasNext()) {
        jg.writeStartObject();
        final QuerySolution solution = results.next();
        final Iterator<String> vars = solution.varNames();
        while (vars.hasNext()) {
          processRsVar(vars.next(), solution);
        }
        jg.writeEndObject();
      }
    } catch (Throwable throwable) {
      LOGGER.error("Failed to serialize the results to the JSON file: " + file,
          throwable);
      throw new IllegalStateException(
          "Failed to serialize the result set to the JSON file: " + file,
          throwable);
    } finally {
      close();
    }
  }

  /**
   * Access the data in the variable and send it to the JSON generator for
   * writing to the file. The JSON formatting is handled by determining the type
   * of data value associated with the variable and formatting it appropriately.
   * 
   * @param var
   *          The variable currently being accessed
   * @param solution
   *          The current SPARQL query solutions
   * 
   * @throws IOException
   *           If the file cannot be written
   */
  private void processRsVar(String var, QuerySolution solution)
      throws IOException {
    final RDFNode node = solution.get(var);
    if (node.isLiteral()) {
      final Object value = solution.getLiteral(var).getValue();
      jg.writeFieldName(var);
      if (value instanceof java.lang.Integer) {
        jg.writeNumber((Integer) value);
      } else if (value instanceof java.lang.Long) {
        jg.writeNumber((Long) value);
      } else if (value instanceof java.lang.Float) {
        jg.writeNumber(FLOAT_FORMAT.format((Float) value));
      } else if (value instanceof java.lang.Double) {
        jg.writeNumber(FLOAT_FORMAT.format((Double) value));
      } else if (value instanceof java.math.BigInteger) {
        jg.writeNumber((BigInteger) value);
      } else if (value instanceof java.math.BigDecimal) {
        jg.writeNumber(FLOAT_FORMAT.format((BigDecimal) value));
      } else {
        // jg.writeStringField(var + "_type",
        // node.asLiteral().getDatatype().toString());
        jg.writeString(value.toString());
      }
    } else if (node.isURIResource()) {
      jg.writeStringField(var, solution.getResource(var).getURI());
    } else if (node.isResource()) {
      jg.writeStringField(var, solution.getResource(var).getURI());
    } else if (node.isAnon()) {
      jg.writeStringField(var, node.toString());
    } else {
      jg.writeStringField(var, node.toString());
    }
  }

  /**
   * Write a TableModel as a JSON document. The expectation is that this is a
   * TableModel that contains the results of a SPARQL query. The JSON formatting
   * is handled by determining the type of data value in each node and
   * formatting it appropriately.
   * 
   * @param model
   *          The model containing the SPARQL results
   */
  public void writeTableModel(TableModel model) {
    testState();
    String value;
    Double valueDouble;
    try {
      for (int row = 0; row < model.getRowCount(); ++row) {
        jg.writeStartObject();
        for (int col = 0; col < model.getColumnCount(); ++col) {
          jg.writeFieldName(model.getColumnName(col));

          value = model.getValueAt(row, col).toString();
          try {
            valueDouble = Double.parseDouble(value);
          } catch (Throwable throwable) {
            // This is not a number
            valueDouble = null;
          }

          if (valueDouble != null) {
            jg.writeNumber(FLOAT_FORMAT.format(valueDouble));
          } else {
            jg.writeString(value);
          }
        }
        jg.writeEndObject();
      }
    } catch (Throwable throwable) {
      LOGGER.error("Failed to serialize the results to the JSON file: " + file,
          throwable);
      throw new IllegalStateException(
          "Failed to serialize the result set to the JSON file: " + file,
          throwable);
    } finally {
      close();
    }
  }
}
