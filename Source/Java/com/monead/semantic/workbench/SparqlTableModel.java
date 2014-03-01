package com.monead.semantic.workbench;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * A basic table model for reporting the SPARQL results
 * 
 * @author David Read
 */
@SuppressWarnings("serial")
public class SparqlTableModel extends AbstractTableModel {
  /**
   * Logger Instance
   */
  private static Logger LOGGER = Logger.getLogger(SparqlTableModel.class);

  /**
   * The result rows
   */
  private List<List<String>> rows = new ArrayList<List<String>>();

  /**
   * The column labels
   */
  private List<String> columnLabels = new ArrayList<String>();

  /**
   * Creates a new table model populating the model with the supplied results
   * 
   * @param results
   *          A Jena query result set
   */
  public SparqlTableModel(ResultSet results, Query query, OntModel ontModel,
      boolean addLiteralFlag, boolean includeDataType,
      boolean useFqn) {
    setupModel(results, query, ontModel, addLiteralFlag, includeDataType,
        useFqn);
  }

  /**
   * Creates a new table model that contains no data
   */
  public SparqlTableModel() {

  }

  /**
   * Convenience method to allow a message to be displayed in the table. This is
   * useful for notifying the user if the SPARQL results are being sent directly
   * to a file rather than to the GUI table.
   */
  public void displayMessageInTable(String heading, String[] rowValues) {
    rows.clear();
    columnLabels.clear();

    columnLabels.add(heading);

    for (String message : rowValues) {
      List<String> row = new ArrayList<String>();
      row.add(message);
      rows.add(row);
    }

    fireTableStructureChanged();
  }

  /**
   * Removes all results from the model.
   */
  public void clearModel() {
    rows.clear();
    columnLabels.clear();
    fireTableStructureChanged();
  }

  /**
   * Is the model empty
   * 
   * @return True if the model contains no information
   */
  public boolean isEmpty() {
    return rows.size() == 0 && columnLabels.size() == 0;
  }

  /**
   * Replaces the current data in the table model with the supplied data.
   * 
   * @param results
   *          A Jena query result set
   */
  public void setupModel(ResultSet results, Query query, OntModel ontModel,
      boolean addLiteralFlag, boolean includeDataType,
      boolean useFqn) {
    SparqlResultsFormatter formatter = new SparqlResultsFormatter(query,
        ontModel, addLiteralFlag, includeDataType, useFqn);
    List<String> columns;
    rows.clear();
    columnLabels.clear();

    columns = results.getResultVars();
    for (String colName : columns) {
      columnLabels.add(colName);
    }

    while (results.hasNext()) {
      QuerySolution solution = results.next();
      List<String> row = new ArrayList<String>();

      for (String var : columnLabels) {
        row.add(formatter.format(solution, var));
      }

      rows.add(row);
      LOGGER.debug("Added row with col count: " + row.size());
    }

    LOGGER.debug("Total rows in results: " + rows.size());

    fireTableStructureChanged();
  }

  /**
   * Get the number of columns in the resulting model.
   */
  public int getColumnCount() {
    return columnLabels.size();
  }

  /**
   * Get the number of rows in the resulting model.
   */
  public int getRowCount() {
    return rows.size();
  }

  /**
   * Get the value at the supplied row and column (cell).
   */
  public Object getValueAt(int arg0, int arg1) {
    return rows.get(arg0).get(arg1);
  }

  /**
   * The the label for the supplied column number.
   */
  public String getColumnName(int col) {
    return columnLabels.get(col);
  }
}
