Load, Infer, Query and Export Semantic Models
=============================================
This program allows the user to manually enter, or load from a file, semantic 
data expressed in RDF/XML, N3, Turtle and N-Triples formats.  The GUI interface 
provides a text area for editing the assertions and running a reasoner.  
Another tab on the interface allows the user to see the inferred triples, 
expressed in the same format as the input. A third tab provides a tree view of 
the ontology, rooted by classes.  Finally, a tab allowing for SPARQL query 
processing against the model is provided.

This program is intended to explore features of the Jena framework and semantic
technology in general.

The tool has continued to mature and has the following features:

1) Load and save assertion (ontology) files

2) Create a Jena model from the assertions, controlling the reasoning level

3) Extract the set of inferred triples

4) View the model as a tree, identifying classes, individuals, properties and data

5) Jump between individuals in the tree where they are linked in multiple places

6) Load and save SPARQL query files

7) Execute SPARQL queries against the local model

8) Execute SPARQL queries from remove RDF sources and SPARQL endpoints

9) Run a SPARQL server which uses a reasoned model to respond to queries

10) Support a proxy used when executing SPARQL queries against remote endpoints

=====
Setup
=====
Before running the program you will to download Cobertura if you don't already
have it.

You can download the latest version: 
http://cobertura.sourceforge.net/download.html

Once you've downloaded it, you will need to either place it in
semantic_workbench/cobertura or have a symbolic link at 
semantic_workbench/cobertura that points to the directory where you've placed 
the cobertura.jar file.

Then, to execute the program using ant, just execute the "run" target, 
e.g. ant run

To build a JAR - use the "dist" target,
e.g. ant dist

There is a BAT file in the project which will use the JAR file to run the 
program as well.

=====
Menus
=====
There are seven menus, File (Assertions), File (SPARQL), Edit, 
Setup, Model, Tree Filter, SPARQL Server and Help.

Menu: File (Assertions)
-----------------------
The File (Assertions) menu allows the user to load an assertion (ontology) 
files. The loaded data is not handed to the reasoner until the "Create Model" 
button is pressed. If there are errors in the syntax of the data 
(or the wrong language for the triples is chosen) then a parsing 
error will occur.

The File (Assertions) menu also has two save options.  The first, 
"Save Assertions Text", writes the text that is present in the assertions text 
area (second tab) to a file.  The second, "Save Model", writes the reasoned 
model out to a file.

The file menu will also contain a list of recent assertion files that have been
opened or saved.

Menu: File (SPARQL)
-------------------
The File (SPARQL) menu allows the user to load and save SPARQL query files. 

The File (SPARQL) menu also has two save options.  The first, 
"Save SPARQL Query", writes the query that is in the SPARQL query text area 
(fourth tab - upper text area) to a file.  The second, "Save SPARQL Results", 
writes the results from the SPARQL results area (fourth tab - lower table area)
out to a file.

The file (SPARQL) menu will also contain a list of recent SPARQL query files 
that have been opened or saved.

Menu: Edit
----------
The Edit menu contains three options.

"Insert Prefixes", inserts common prefixes (such as RDF, RDFS, and OWL) in to 
the assertions or SPARQL query text areas. When the option is selected, a
dialog containing a drop down is presented allowing the user to select where
(and what language in the case of assertions) the prefixes are to be inserted.

"Expand Entire Tree" is used to open all the nodes in the tree (third tab).
This menu item will only have an effect if a tree has been created from a
model.

"Collapse Entire Tree" is used to close all the nodes in the tree (third tab)
This menu item will only have an effect if a tree has been created from a
model.

Menu: Setup
-----------
The Setup menu allows for control over a variety of configurable options
within Semantic Workbench. The major configuration options include:

1) Which format (RDF/XML, N3,) will be used to save the model to a file 
(see the File|Save Model menu item)

2) Whether only the assertions or both assertions and inferred triples are
to be output when the model is saved

3) How the information from the execution of a SPARQL query is to be 
formatted in the results table (fourth tab)

4) Whether SPARQL query results should be saved in Comma Separated Value (CSV)
or Tab Separated Value (TSV) format

5) Whether Jena's strict type-checking mode should be enabled. Currently, this
mode can cause errors working with OWL2 constructs.

6) Choose the font used by the application for most of the data displayed

7) Configure and enable the proxy, which is used when executing SPARQL queries
against remote endpoints

Menu: Model
-----------
Contains two options for generating alternate views of the model.

"Create Tree" populates a tree view of the current model. This tree is placed on
the third tab (Tree View). This option is only available if a model has been 
created from a set of assertions.

"Identify Inferred Triples" lists the inferred triples on the second tab
(Inferences). This option is only available if a model has been created from 
a set of assertions. If The model does not contain any inferred triples, this
tab will remain blank.

Menu: Tree Filter
-----------------
These menu items allow control over what classes and properties are included
in the tree view.

"Enable Filters" turn on or off the use of the configured filters. This is
helpful if the user does not want to lose their configured filters but 
temporarily wants the tree to show all the classes and properties.

"Show Anonymous Nodes" turns on or off the inclusion of anonymous nodes
in the tree view.

"Edit List of Filtered Classes" displays a list of classes that are to be
filtered out of the tree view (assuming Enable Filters is enabled). The user
may select one or more of the displayed classes and click the OK button
to remove those classes from the list of filtered classes. (See the 
description, "Navigating the Tree View," for information on adding classes
to the list of filtered classes). The tree must be recreated in order
to see the results of removing items to the filter list.

"Edit List of Filtered Properties" displays a list of properties that are to be
filtered out of the tree view (assuming Enable Filters is enabled). The user
may select one or more of the displayed properties and click the OK button
to remove those properties from the list of filtered properties. (See the 
description, "Navigating the Tree View," for information on adding properties
to the list of filtered properties). The tree must be recreated in order
to see the results of removing items to the filter list.

Menu: SPARQL Server
-------------------
These menu items control a local SPARQL server endpoint which Semantic 
Workbench can provide. Note that the model used by the SPARQL server
is independent of new models created within Semantic Workbench.
In order to replace the server's model, a new model must be "published" 
to the server (see the following descriptions for details).

"Startup SPARQL Server" starts the server listening on the configured port and
limiting query execution time based on the configured timeout period. The
SPARQL server may only be started if there is a model created. The server will
use the current model as the initial basis for SPARQL query handling.

"Shutdown SPARQL Server" stops the server. The server must be stopped if its
configuration needs to be changed. The server is automatically stopped if
Semantic Workbench is exited.

"Publish Current Reasoned Model" replaces the model being used by the SPARQL
server with the current model created within Semantic Workbench.

"Configure the SPARQL Server" allows for the port number and the timeout 
period to be configured. This can only be done when the SPARQL server is not 
running.

Menu: Help
----------
The Help menu contains one option, "About", which reports the program version 
as well as the version of Jena and Pellet that has been loaded.

=========
Operation
=========
After entering or loading assertions in to the assertions text area
press the "Create Model" button (on the Assertions tab) to reason
over the assertions.  Reasoning will run at the level chosen in the
drop town at the top of the window.

Once the model has been reasoned over, the Model menu items may be used to 
populate their respective tabs (see the menu discussion, above). The Inferences 
tab will display any inferred triples and the Tree View tab will show the 
classes and nested instances, properties and objects.  

Also, once the model has been reasoned over, the SPARQL tab may be used to run 
SPARQL queries against the model.

The SPARQL tab contains an input text area for the query and a display table 
area for the results. Once a query has been entered the user must press the 
"Run SPARQL" button, on the SPARQL tab, to run the query.

The SPARQL tab also contains two display fields. One, "SPARQL Server Status" 
shows information about the SPARQL Server. The other, "Proxy Status" shows
whether or not the proxy is enabled.

============
Status Field
============
The results of commands and menu items are reported in the status field to confirm
success or failure. The status field is a text area along the bottom of the
Semantic Workbench window. Look here to see the status resulting from each
request made in the application. More details are written to the log file.

===========
Quick Start
===========
To quickly try the program use the sample ontology, SampleOntology.turtle, 
which is included as a simple test.  Once the program is started, use the 
File|Load menu option to load the SampleOntology.turtle file, select the 
Assertions tab, choose "OWL Lite (Jena)" in the "Reasoning Level" dropdown
and press the Run Reasoner button.  

Next, select the "Model|Identify Inferred Triples" and "Model|Create Tree"
menu items to populate the Inferences and Tree View tabs.

Now select the Inferences tab.  You should see a few inferred triples.  
Select the Tree View tab to see the entire model, asserted and inferred, 
displayed in tree format.  Finally, select the SPARQL tab and press the 
Run SPARQL button to run the default query to see a listing of some of the 
triples.

========================
Navigating the Tree View
========================
The tree has two main features.

1) Clicking on an individual searches for the next occurrence of that
individual in the tree. This is helpful when looking for individuals
linked in the underlying graph. Left click searched forward (down the tree)
while right clicking searches backward (up the tree). The search will wrap
if necessary. The status field (bottom of window) will show the result
of the search.


2) Right-clicking on a Class or Property will bring up a menu asking if
the class or property should be added to the list of classes or properties
being filtered out of the tree view. The tree must be recreated in order
to see the results of adding items to the filter lists.

================
Logging Reminder
================
Note that this program uses Log4J so if you encounter errors you may find
the output there helpful in debugging the situation.  By default the
log4j.properties file is configured to place the logs in the main project
directory.

Please report bugs (including relevant input files and log message)
using the contact form: http://monead.com/contact.html


====================================================
A few limitations include (in no particular order):
====================================================
The tree view of the model is incomplete, only showing class to instance
to property to object relationships with all classes depicted at the 
root level.

Much of the functionality is in one class

There are no tests created

=============================
Questions, Comments, Feedback
=============================
If you have questions or comments, please feel free to use the feedback 
form on my website:

http://monead.com/contact/

Thank you!

David Read


(Description updated for Version 1.8.1 on 20140213)
