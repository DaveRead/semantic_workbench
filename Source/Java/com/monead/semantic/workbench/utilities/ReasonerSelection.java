package com.monead.semantic.workbench.utilities;

// Removed Pellet options since it is not compatible with Jena 1.13
//import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.OntModelSpec;

/**
 * Represents a reasoner that can be selected when creating a model.
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
public enum ReasonerSelection {

  /**
   * RDFS model, No reasoning
   */
  RDFS_NONE("RDFS/None",
      "An RDFS model which does no entailment reasoning",
      OntModelSpec.RDFS_MEM),

  /**
   * RDFS model, Transitive reasoning
   */
  RDFS_TRANSITIVE("RDFS/Transitive",
      "A RDFS model which supports transitive class-hierarchy inference",
      OntModelSpec.RDFS_MEM_TRANS_INF),

  /**
   * RDFS model, RDFS reasoning
   */
  RDFS_RDFS(
      "RDFS/RDFS",
      "A RDFS model which uses a rule reasoner with RDFS-level entailment-rules",
      OntModelSpec.RDFS_MEM_RDFS_INF),

  /**
   * OWL Lite model, No reasoning
   */
  OWLLITE_NONE("OWL Lite/None",
      "An OWL Lite model which does no entailment reasoning",
      OntModelSpec.OWL_LITE_MEM),

  /**
   * OWL Lite model, Transitive reasoning
   */
  OWLLITE_TRANSITIVE(
      "OWL Lite/Transitive",
      "An OWL Lite model which supports transitive class-hierarchy inference",
      OntModelSpec.OWL_LITE_MEM_TRANS_INF),

  /**
   * OWL Lite model, RDFS reasoning
   */
  OWLLITE_RDFS(
      "OWL Lite/RDFS",
      "An OWL Lite model which uses a rule reasoner with RDFS-level entailment-rules",
      OntModelSpec.OWL_LITE_MEM_RDFS_INF),

  /**
   * OWL Lite model, OWL reasoning
   */
  OWLLITE_OWL("OWL Lite/OWL",
      "An OWL LLite model which uses a rule-based reasoner with OWL rules",
      OntModelSpec.OWL_DL_MEM_RULE_INF),

  /**
   * OWL DL model, No reasoning
   */
  OWLDL_NONE("OWL DL/None",
      "An OWL DL model which does no entailment reasoning",
      OntModelSpec.OWL_DL_MEM),

  /**
   * OWL DL model, Transitive reasoning
   */
  OWLDL_TRANSITIVE(
      "OWL DL/Transitive",
      "An OWL DL model which supports transitive class-hierarchy inference",
      OntModelSpec.OWL_DL_MEM_TRANS_INF),

  /**
   * OWL DL model, RDFS reasoning
   */
  OWLFL_RDFS(
      "OWL DL/RDFS",
      "An OWL DL model which uses a rule reasoner with RDFS-level entailment-rules",
      OntModelSpec.OWL_DL_MEM_RDFS_INF),

  /**
   * OWL DL model, OWL reasoning
   */
  OWLDL_OWL("OWL DL/OWL",
      "An OWL DL model which uses a rule-based reasoner with OWL rules",
      OntModelSpec.OWL_DL_MEM_RULE_INF),

  /**
   * OWL Full model, No reasoning
   */
  OWLFULL_NONE("OWL Full/None",
      "An OWL model which does no entailment reasoning",
      OntModelSpec.OWL_MEM),

  /**
   * OWL Full model, Transitive reasoning
   */
  OWLFULL_TRANSITIVE(
      "OWL Full/Transitive",
      "An OWL Full model which supports transitive class-hierarchy inference",
      OntModelSpec.OWL_MEM_TRANS_INF),

  /**
   * OWL Full model, OWL Micro reasoning
   */
  OWLFULL_OWLMICRO(
      "OWL Full/OWL Micro",
      "An OWL Full model which uses an optimised rule-based reasoner with OWL rules",
      OntModelSpec.OWL_MEM_MICRO_RULE_INF),

  /**
   * OWL Full model, OWL Mini reasoning
   */
  OWLFULL_OWLMINI(
      "OWL Full/OWL Mini",
      "An OWL Full model which uses a rule-based reasoner with subset of OWL rules",
      OntModelSpec.OWL_MEM_MINI_RULE_INF),

  /**
   * OWL Full model, OWL reasoning
   */
  OWLFULL_OWL("OWL Full/OWL",
      "An OWL DL model which uses a rule-based reasoner with OWL rules",
      OntModelSpec.OWL_MEM_RULE_INF);

  /**
   * Pellet reasoner
   */
  // PELLET("Pellet", "The Pellet reasoner", PelletReasonerFactory.THE_SPEC);

  /**
   * The name of the reasoner
   */
  private final String reasonerName;

  /**
   * The description of the reasoner
   */
  private final String description;

  /**
   * The Jena specification for the reasoner
   */
  private final OntModelSpec jenaSpecification;

  /**
   * Creates the definition of a reasoner.
   * 
   * @param pReasonerName
   *          The name displayed to the user
   * @param pDescription
   *          A description of the reasoner
   * @param pJenaSpecification
   *          The Jena constant used for this reasoner mode
   */
  ReasonerSelection(String pReasonerName, String pDescription,
      OntModelSpec pJenaSpecification) {
    reasonerName = pReasonerName;
    description = pDescription;
    jenaSpecification = pJenaSpecification;
  }

  /**
   * Get the display name for the reasoner
   * 
   * @return The reasoner name
   */
  public String reasonerName() {
    return reasonerName;
  }

  /**
   * Get the description of the reasoner
   * 
   * @return The description of the reasoner
   */
  public String description() {
    return description;
  }

  /**
   * Get the Jena specification for the reasoner
   * 
   * @return The Jena specification of the reasoner
   */
  public OntModelSpec jenaSpecification() {
    return jenaSpecification;
  }

  /**
   * Get the display name of the reasoner
   * 
   * @return The display name of the reasoner
   */
  public String toString() {
    return reasonerName();
  }
}
