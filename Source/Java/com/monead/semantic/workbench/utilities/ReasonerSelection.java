package com.monead.semantic.workbench.utilities;

import com.hp.hpl.jena.ontology.OntModelSpec;

/**
 * Represents a resoner that can be selected when creating a model.
 * 
 * @author David Read
 * 
 */
public class ReasonerSelection {
  /**
   * The name of the reasoner
   */
  private String name;

  /**
   * The description of the reasoner
   */
  private String description;

  /**
   * The Jena specification for the reasoner
   */
  private OntModelSpec jenaSpecification;

  /**
   * Creates the definition of a reasoner.
   * 
   * @param pName
   *          The name displayed to the user
   * @param pDescription
   *          A description of the reasoner
   * @param pJenaSpecification
   *          The Jena constant used for this reasoner mode
   */
  public ReasonerSelection(String pName, String pDescription,
      OntModelSpec pJenaSpecification) {
    setName(pName);
    setDescription(pDescription);
    setJenaSpecification(pJenaSpecification);
  }

  /**
   * Get the display name for the reasoner
   * 
   * @return The reasoner name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the name of the reasoner
   * 
   * @param pNname
   *          The name of the reasoner
   */
  private void setName(String pNname) {
    name = pNname;
  }

  /**
   * Get the description of the reasoner
   * 
   * @return The description of the reasoner
   */
  public String getDescription() {
    return description;
  }

  /**
   * Set the description of the reasoner
   * 
   * @param pDescription
   *          The description of the reasoner
   */
  private void setDescription(String pDescription) {
    description = pDescription;
  }

  /**
   * Get the Jena specification for the reasoner
   * 
   * @return The Jena specification of the reasoner
   */
  public OntModelSpec getJenaSpecification() {
    return jenaSpecification;
  }

  /**
   * Set the Jena specification for the reasoner
   * 
   * @param pJenaSpecification
   *          The Jena specification of the reasoner
   */
  private void setJenaSpecification(OntModelSpec pJenaSpecification) {
    jenaSpecification = pJenaSpecification;
  }

  /**
   * Get the display name of the reasoner
   * 
   * @return The display name of the reasoner
   */
  public String toString() {
    return getName();
  }
}
