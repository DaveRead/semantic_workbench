Load, Infer, Query and Export Semantic Models
=============================================

This program allows the user to manually enter, or load from a file, semantic data
expressed in RDF/XML, N3, Turtle and N-Triples formats.  The GUI interface 
provides a text area for editing the assertions and running a reasoner.  Another 
tab on the interface allows the user to see the inferred triples, expressed in the 
same format as the input. A third tab provides a tree view of the ontology, rooted 
by classes.  Finally, a tab allowing for SPARQL query processing against the model 
is provided.

This program is intended to explore features of the Jena framework as well as
leverage the pellet reasoner.  This first generation tool has some severe
limitations that will be resolved over time.  Longer term this project may
serve as a springboard for a Java-based Semantic Workbench that is being 
kicked-off on SourceForge.

To execute the program using ant, just execute the "run" target, 
e.g. ant run

=====
Menus
=====
There are four menus, File, Edit, Setup and Help.

The File menu allows the user to load an ontology file.  The loaded data
is not handed to the reasoner until the "Run Reasoner" button is pressed.
if there are errors in the syntax of the data (or the wrong language for
the triples is chosen) then a parsing error will occur.

The File menu also has two save options.  The first, "Save Assertions Text", 
writes the text that is present in the assertions text area to a file.  The 
second, "Save Model", writes the reasoned model out to a file.

The Edit menu contains one option, "Insert Prefixes", which insert common 
prefixes (such as RDF, RDFS, and OWL) in to the assertions text area.  This 
option can also be used to place those same prefixes in the SPARQL Query 
text area (on the SPARQL tab).

The Setup menu allows for control over which format will be used to save the 
model to a file (see the File|Save Model menu item).  The Setup menu also 
allows for control over whether the entire reasoned model is output to the 
file or just the inferred triples.

The Help menu contains one option, "About", which reports the program version 
as well as the version of Jena and Pellet that has been loaded.

=========
Operation
=========
After entering or loading assertions in to the assertions text area
press the "Run Reasoner" button (on the Assertions tab) to reason
over the assertions.  Reasoning will run at the level chosen in the
drop town at the top of the window.

Once the model has been reasoned over, the Inferences tab will display any
inferred triples and the Tree View tab will show the classes and nested
instances, properties and objects.  Also, once the model has been reasoned
over, the SPARQL tab may be used to run SPARQL queries against the model.

The SPARQL tab contains an input text area for the query and the results.
once a query has been entered the user must press the Run SPARQL button,
on the SPARQL tab, to run the query.

To quickly try the program use the sample ontology, SampleOntology.turtle, 
which is included as a simple test.  Once the program is started, use the 
File|Load menu option to load the SampleOntology.turtle file, select the 
Assertions tab and press the Run Reasoner button.  Next select the 
Inferences tab.  You should see a few inferred triples.  Select the Tree 
view tab to see the entire model, asserted and inferred, displayed in tree 
format.  Finally, select the SPARQL tab and press the Run SPARQL button to
run the default query to see a listing of the triples.

Note that this program uses Log4J so if you encounter errors you may find
the output there helpful in debugging the situation.  By default the
log4j.properties file is configured to place the logs in the main project
directory.

====================================================
A few limitations include (in no particular order):
====================================================
External data sources are not supported in the SPARQL queries

The tree view of the model is incomplete, only showing class to instance
to property to object relationships with all classes depicted at the 
root level.

The SPARQL output is presented in a text window rather than a table

All the functionality is in one class

There are no tests created
====================================================

If you have questions or comments, please feel free to use the feedback 
form on my website:

http://monead.com/contact/

Thank you!

David Read
