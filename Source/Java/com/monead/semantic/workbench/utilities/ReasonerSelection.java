package com.monead.semantic.workbench.utilities;

import com.hp.hpl.jena.ontology.OntModelSpec;

/**
 * Represents a resoner that can be selected when creating a model.
 * 
 * @author David Read
 * 
 */
public class ReasonerSelection {
  private String name;
  private String description;
  private OntModelSpec jenaSpecification;

  /**
   * Creates the definition of a reasoner.
   * 
   * @param name
   *          The name displayed to the user
   * @param description
   *          A description of the reasoner
   * @param jenaSpecification
   *          The Jena constant used for this reasoner mode
   */
  public ReasonerSelection(String name, String description,
      OntModelSpec jenaSpecification) {
    setName(name);
    setDescription(description);
    setJenaSpecification(jenaSpecification);
  }

  public String getName() {
    return name;
  }

  private void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  private void setDescription(String description) {
    this.description = description;
  }

  public OntModelSpec getJenaSpecification() {
    return jenaSpecification;
  }

  private void setJenaSpecification(OntModelSpec jenaSpecification) {
    this.jenaSpecification = jenaSpecification;
  }

  public String toString() {
    return getName();
  }
}
