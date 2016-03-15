package com.monead.semantic.workbench.tree;

import java.util.Comparator;

import org.apache.jena.ontology.Individual;

/**
 * Compares Individuals by name. A non-anonymous node will always be less than
 * an anonymous one
 * 
 * @author David Read
 * 
 */
public class IndividualComparator implements Comparator<Individual> {

  /**
   * No operation
   */
  public IndividualComparator() {

  }

  @Override
  public int compare(Individual i1, Individual i2) {
    String i1Label;
    String i2Label;
    int compareResult;

    // Anonymous individuals go below named individuals
    if (!i1.isAnon() && i2.isAnon()) {
      compareResult = -1;
    } else if (i1.isAnon() && !i2.isAnon()) {
      compareResult = 1;
    } else {
      if (i1.isAnon()) {
        i1Label = i1.getId().getLabelString();
        i2Label = i2.getId().getLabelString();
      } else {
        i1Label = i1.getLocalName() + " " + i1.getURI();
        i2Label = i2.getLocalName() + " " + i2.getURI();
      }
      compareResult = i1Label.compareTo(i2Label);
    }

    return compareResult;
  }

}
