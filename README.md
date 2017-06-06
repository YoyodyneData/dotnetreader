# dotnetreader
Java application that provides functionality to read through the XML of a .NET project to find all SQL statements.
Very useful when reading through extremely large SSIS packages to find all references to a specific table or to find all 
references to certain tables to determine how many changes need to be made to implement changes.
One method provided will find all SQL statements in all of the .dtsx files within a directory structure.
Another method can be used to find all references to a specific table in all of the .dtsx files within a directory structure.
