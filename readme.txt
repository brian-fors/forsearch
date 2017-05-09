ForSearch - Class project
Author: Brian Fors
CSC 575, Winter 2012

********************   INSTRUCTIONS   ********************
Main class: com.fors.ir.controller.Main

The application requires JRE6 (Java 1.6 compiler) and build file uses Ant 1.7.  The application runs on Windows OS.

To run, extract files from zip, and execute the following from command line in the application root folder - ForSearch (containing build.xml):
  ant build
  ant run


Once run, the application will prompt the user for parameters using the Console.  For example:

  Which document set to index (0=TIME, 1=MEDLARS, 2=CRANFIELD):
  > 1
  
System returns the number of terms and documents indexed --
11273 terms loaded.
1033 documents loaded.

Enter query:
  > the crystalline lens in vertebrates, including humans
 
System returns the query text, and pre-processed query terms (stemmed and excluding stopwords)
and also returns the TOP 50 search results, ranked in descending order.  For each result,
the system displays the ranking #, document #, cosSim score and 1st 80-characters of the document  --
Query: the crystalline lens in vertebrates, including humans
includ
vertebr
crystallin
len
human
============================
           TOP 50           
============================
1-965-5.72-posterior scalloping of vertebral bodies in uncontrolled hydrocephalus. two case
2-500-3.646-1949. studies on the soluble proteins of bovine lens.  immuno- chemical analyses
3-499-2.515-1463. investigations of lens protein and microelectrophoresis of hydrosoluble pr
4-15-1.962-lens development.. the differentiation of embryonic chick lens           epithel
5-511-1.766-1747. the problem of albuminoid albuminoid is the main constituent of the insolu
6-212-1.661-experiments dealing with the role played by the aqueous humor and retina in lens
7-637-1.613-some intraspecies differences in antigens on the surface of certain living human
8-181-1.5-the insoluble proteins of bovine crystalline lens .                        the i
9-206-1.486-isozymes of lactic dehydrogenase.. sequential alterations during         develop
10-142-1.483-the effects of electrophoretically separated lens proteins on lens       regener

  The user can display the full context of a document by entering *D###, where ### is the document number.  For example:
Enter query:
  > *D965
posterior scalloping of vertebral bodies in uncontrolled hydrocephalus. two cases of extensive posterior scalloping of the vertebral bodies are presen
ted in men aged 17 and 23 years, having long-standing hydrocephalus.  two additional cases with scalloping of only one lumbar vertebra when partially 
controlled hydrocephalus has been present for a shorter time are also noted.  no previous association between these entities has been recorded.  it is
 supposed that the increased intraspinal pressure which must have been present in the first 2 patients for many years, was present near the time of cl
osure of the epiphysis at the junction of the arch and the bodies and caused not only widening of the spinal canal but also excavation of the vertebra
l bodies.  scalloping of vertebral bodies has been described in: (1) neoplasms (neurofibromas, meningiomas, gliomas, hemangio-endotheliomas, hemangiom
as, lipomas): (2) intraspinal cysts (intradural arachnoid cysts, tarlov's perineural cysts, thoracic extradural cysts in kyphosis dorsalis juvenilis):
 (3) congenital anomalies of the spine and cord (fusion defects, myelodysplasia, hydromyelia, absence of a single vertebral pedicle, meningoceles): an
d (4) neurofibromatosis (with or without a thoracic meningocele). 
Enter query:
  >   

  The user can quit the application by entering Q.
  
  Otherwise, all other words are treated as query terms.


********************   DESCRIPTION   *********************
The system has 3 packages:

1) Controller - contains Main.java.  This class creates the ClientView for reading parameters from user.  It also creates the Index and initiates the loop for executing queries

2) Model - contains classes that create the inverted document index, execute searches and rank search results

3) View - contains ClientView class which prompts users for input parameters and understands how to parse the IR datasets (TIME, MEDLARS and CRANFIELD)


For detailed description and sample test results, please see the following documents:
1.  CSC 575 Final Project BFors.docx
2.  ForSearch Test Results - Medlars collection.xlsx
