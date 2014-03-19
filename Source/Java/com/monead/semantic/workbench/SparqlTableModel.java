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
  private static final Logger LOGGER = Logger.getLogger(SparqlTableModel.class);

  /**
   * The result rows
   */
  private List<List<SparqlResultItem>> rows = new ArrayList<List<SparqlResultItem>>();

  /**
   * The column labels
   */
  private List<String> columnLabels = new ArrayList<String>();

  /**
   * Creates a new table model populating the model with the supplied results
   * 
   * @param results
   *          A Jena query result set
   * @param query
   *          The SPARQL query
   * @param ontModel
   *          The model being queried
   * @param addLiteralFlag
   *          Whether literal values should be flagged in the output
   * @param includeDataType
   *          Whether literals whould be tagged with their data type
   * @param useFqn
   *          Whether the fully qualified name (URL) should be displayed instead
   *          of a prefix
   * @param displayImages
   *          Whether images should be displayed
   */
  public SparqlTableModel(ResultSet results, Query query, OntModel ontModel,
      boolean addLiteralFlag, boolean includeDataType,
      boolean useFqn, boolean displayImages) {
    setupModel(results, query, ontModel, addLiteralFlag, includeDataType,
        useFqn, displayImages);
  }

  /**
   * Creates a new table model that contains no data
   */
  public SparqlTableModel() {

  }

  /**
   * Get the class of the data in the column
   * 
   * @param columnIndex The column number
   * 
   * @return The class of the column's values
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Class getColumnClass(int columnIndex) {
    return SparqlResultItem.class;
  }

  /**
   * Convenience method to allow a message to be displayed in the table. This is
   * useful for notifying the user if the SPARQL results are being sent directly
   * to a file rather than to the GUI table.
   * 
   * @param heading The heading to be displayed at the top of the message
   * @param rowValues The array of strings to be displayed
   */
  public void displayMessageInTable(String heading, String[] rowValues) {
    rows.clear();
    columnLabels.clear();

    columnLabels.add(heading);

    for (String message : rowValues) {
      final List<SparqlResultItem> row = new ArrayList<SparqlResultItem>();
      row.add(new SparqlResultItem(message));
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
   * TODO handle representation of URLs leading to files (e.g. pictures, sounds,
   * videos, etc)
   * 
   * @param results
   *          A Jena query result set
   * @param query
   *          The SPARQL query
   * @param ontModel
   *          The model being queried
   * @param addLiteralFlag
   *          Whether literal values should be flagged in the output
   * @param includeDataType
   *          Whether literals whould be tagged with their data type
   * @param useFqn
   *          Whether the fully qualified name (URL) should be displayed instead
   *          of a prefix
   * @param displayImages
   *          Whether images should be displayed
   */
  public void setupModel(ResultSet results, Query query, OntModel ontModel,
      boolean addLiteralFlag, boolean includeDataType,
      boolean useFqn, boolean displayImages) {
    final SparqlResultsFormatter formatter = new SparqlResultsFormatter(query,
        ontModel, addLiteralFlag, includeDataType, useFqn);
    List<String> columns;
    rows.clear();
    columnLabels.clear();

    columns = results.getResultVars();
    for (String colName : columns) {
      columnLabels.add(colName);
    }

    fireTableStructureChanged();

    while (results.hasNext()) {
      final QuerySolution solution = results.next();
      final List<SparqlResultItem> row = new ArrayList<SparqlResultItem>();

      for (String var : columnLabels) {
        row.add(formatter.format(solution, var, displayImages));
      }

      rows.add(row);
      LOGGER.debug("Added row with col count: " + row.size());
    }

    LOGGER.debug("Total rows in results: " + rows.size());

    fireTableDataChanged();
  }

  @Override
  public int getColumnCount() {
    return columnLabels.size();
  }

  @Override
  public int getRowCount() {
    return rows.size();
  }

  @Override
  public Object getValueAt(int arg0, int arg1) {
    return rows.get(arg0).get(arg1);
  }

  @Override
  public String getColumnName(int col) {
    return columnLabels.get(col);
  }
}