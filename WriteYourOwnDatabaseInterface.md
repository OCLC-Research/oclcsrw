# Introduction #

To provide your own database interface, you need to implement three SRU classes: SRWDatabase, QueryResult and RecordIterator.  The SRWDatabase class has an init() method you can use for general initialization and a getQueryResult() method that is passed the CQL query from the client and the complete SRU request (in case there is other information in the request that might effect your processing of the query.)  The QueryResult object that is returned by getQueryResult() contains the state information for the query and exposes a newRecordIterator() method that returns a RecordIterator object used to retrieve the records from your underlying database.  The RecordIterator class extends the Java Iterator and implements the hasNext() and next() methods.

A simple example of all of this can be found in the SRWFileSystemDatabase, which is a set of classes that make the contents of a directory look like a database, providing searching on filenames and file attributes.


# Details #