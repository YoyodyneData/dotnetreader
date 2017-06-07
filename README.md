# dotnetreader
Java application that provides functionality to read through the XML of a .NET project to find all SQL statements.
Very useful when reading through extremely large SSIS packages to find all references to a specific table or to find all 
references to certain tables to determine how many changes need to be made to implement changes.
One method provided will find all SQL statements in all of the .dtsx files within a directory structure.
Another method can be used to find all references to a specific table in all of the .dtsx files within a directory structure.


# How To Use dotnetreader

The main screen provides text boxes to select the root directory.  Clicking on the first search button will search the 
root directory and every subdirectory under it.  Every .dtsx file will be searched for source query blocks within data flow 
packages.  The SQL statements are written to an output file.  The location of the output file can be configured in the Settings.

If a table name is specified and the second Search button is clicked then the search will only return SQL statements with a 
reference to the specified table name.

# Reading XML
The dotnetreader is set by default to identify the tags within a .dtsx file but the settings can be changed to read any XML file source.  
You must change the file extension to search for and the namespace used by the XML file being read.

# Mapping Fields
The dotnetreader also has the capability to create a mapping of fields from the Source to Destination within components of an SSIS 
project.  For this a specific .dtsx file must be selected.  The application will generate an output file showing the mapping from source to destination for each field within each data flow task.  

# Output
The results are written to an XML file.  They can be opened in a browser or any application that allows viewing XML including 
the dotnetreader.  
