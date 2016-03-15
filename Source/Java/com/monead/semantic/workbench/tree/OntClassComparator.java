package com.monead.semantic.workbench.tree;

import java.util.Comparator;

import org.apache.jena.ontology.OntClass;

/**
 * Compare two ontology classes for sorting based on the class name (without the namespace)
 * 
 * @author David Read
 *
 */
public class OntClassComparator implements Comparator<OntClass> {

  /**
   * No operation
   */
  public OntClassComparator() {
    
  }
  
  @Override
  public int compare(OntClass o1, OntClass o2) {
    String o1Label;
    String o2Label;
    int compareResult;
    
    // Anonymous classes go below named classes
    if (!o1.isAnon() && o2.isAnon()) {
      compareResult = -1;
    } else if (o1.isAnon() && !o2.isAnon()) {
      compareResult = 1;
    } else {
      if (o1.isAnon()) {
        o1Label = o1.getId().getLabelString();
        o2Label = o2.getId().getLabelString();
      } else {
        o1Label = o1.getLocalName() + " " + o1.getURI();
        o2Label = o2.getLocalName() + " " + o2.getURI();        
      }
      compareResult = o1Label.compareTo(o2Label);
    }

    return compareResult;
  }
}
