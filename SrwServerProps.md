#SRW/SRU Server Configuration File Details.

# SRWServer.props #

This file contains the list of databases supported and configuration information common to all databases.

## Common Configuration Information ##
All of the common configuration parameters are optional.
### SRW.Context ###
This information is only used in constructing the stylesheet reference in an SRU response.  Normally, the SRW server can figure out its own context name, but sometimes you want to find those stylesheets in a different context (e.g. when you're running more than one SRW server in the same Tomcat).  The optional parameter lets you specify that context.
```
SRW.Context=identities
```
### resultSetIdleTime ###
By default, the SRW server saves the results of searches so that they may be referred to again in subsequent requests. resultSetIdleTime specifies how long that time should be.  By default, that time is 300 seconds.  Setting the resultSetIdleTime to zero specifies that no resultSet should be created.
```
resultSetIdleTime=300
```
### makeIndex.html ###
When I have multiple databases up under a single SRW server, I find it convenient to produce a page with pointers to all of those databases.  (An individual database can declare itself hidden in its own configuration file.)  When turned on, this creates a file named index.html in the root directory of the SRW server.
```
makeIndex.html=true
```
### index.html ###
If you want a page of database pointers produced but have another need for the index.html file, you can specify a different name for the file.
```
index.html=databases.html
```
### SRU Input Parameter Extensions ###

SRU input parameter extensions take the form:
```
extension.<sru-parm>=<srw-element>
extension.<srw-element>.namespace=<srw-element-namespace>
```

For instance, to request a restrictor summary (a local version of facets) I add this to my SRWServer.props file:
```
extension.x-info-14-restrictorSummary=restrictorSummary
extension.restrictorSummary.namespace=info:srw/extension/14/restrictorSummary
```

The value for the namespace is pretty much meaningless, so make up your own URL.

See the separate document [SRUInputParameterExtensions](SRUInputParameterExtensions.md) for information on how to access this parameter within your code.

## Databases ##
### default.database ###
### Individual Databases ###
Each database is represented by a triple of information