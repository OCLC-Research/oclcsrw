#OCLC SRW/SRU Server#
## Introduction ##

The **SRW** (Search & Retrieve Web Service) initiative is part of an international collaborative effort to develop a standard web-based text-searching interface. It draws heavily on the abstract models and functionality of Z39.50, but removes much of the complexity. SRW is built using common web development tools (WSDL, SOAP, HTTP and XML) and development of SRW interfaces to data repositories is significantly easier than for Z39.50. In addition, such arcane record formats as MARC and GRS-1 have been replaced with XML.

**SRU** (Search & Retrieve URL Service) is a URL-based alternative to SRW. Messages are sent via HTTP using the GET method and the components of the SRW SOAP request are mapped to simple HTTP parameters. The response to an SRU request is identical to the response to an SRW request, with the SOAP wrapper removed.

Complete information on the SRW/SRU standard can be found at the maintenance agency web site: http://www.loc.gov/standards/sru/

## Build Instructions ##

Instructions for how to build and install an SRW/SRU server for DSpace, Lucene and/or Pears/Newton.

### OCLC Research SRW Server 2.0 Installation Instructions ###
Download and build the SRW Database interface software that you want.
  * [DSpace](https://github.com/OCLC-Research/oclcsrwdspacelucene)
  * [Lucene](https://github.com/OCLC-Research/oclcsrwlucene)
  * [Federated Searching](https://github.com/OCLC-Research/oclcsrwparallelsearching)

### Prerequisites ###
  * Java 1.5 or higher
  * Tomcat or some other servlet engine
  * Ant

### Step 1 ###
Download the latest SRW server from the SVN repository.  (Instructions for that can be found [here](http://code.google.com/p/oclcsrw/source/checkout).)  It contains everything you need to build the base SRW server, but does not include the libraries necessary to build the DSpace, Lucene or SRW Federated Searching interfaces.

### Barely Optional Step 2 ###
The server, as downloaded, includes a test database interface.  This will allow you to verify that the server is operating.  But, eventually you will want to add an interface for a specific database.  Download the package for that database and follow its build instructions.  Then, copy the jar file from the "dist" directory of the database interface package and the jars (except the SRW jar!) from the "lib" directory to the web/WEB-INF/lib directory of the SRW distribution.

### Step 3 ###
Run “ant” to compile the code and build a war file.  After you’ve run “ant”, you’ll find a directory named “dist” and in it you will find the war file and the SRW jar files.  If you have an existing SRW deployment with its own configuration files, you may want to just copy the SRW.jar file to your `<tomcat>/webapps/<SRW>/WEB-INF/lib` directory. Otherwise, copy the SRW.war file to your `<tomcat>/webapps` directory.

### Step 4 ###
Start (or restart) your tomcat server.  You should see an SRW directory in the `<tomcat>/webapps` directory.  Try searching the test database.  The URL below assumes you have installed the servlet engine at port 8080 on your local machine.  Change the host and port if your configuration is different.

## Testing ##
http://localhost:8080/SRW/search/test should get you something like this:

![http://oclcsrw.googlecode.com/svn/wiki/images/TestExplainResponse.jpg](http://oclcsrw.googlecode.com/svn/wiki/images/TestExplainResponse.jpg)

http://localhost:8080/SRW/search/test?query=dog should get you something like this:

![http://oclcsrw.googlecode.com/svn/wiki/images/TestSearchResponse.jpg](http://oclcsrw.googlecode.com/svn/wiki/images/TestSearchResponse.jpg)

http://localhost:8080/SRW/search/test?scanClause=dog should get you something like this:

![http://oclcsrw.googlecode.com/svn/wiki/images/TestScanResponse.jpg](http://oclcsrw.googlecode.com/svn/wiki/images/TestScanResponse.jpg)

#### Test Failure Modes ####
"Cannot find server or DNS Error"
You've specified the wrong host or port or Tomcat isn't running.

"The download of the specified resource has failed"
The SRW Servlet failed during initialization. The Tomcat log should contain information explaining the failure. If the servlet was unable to find the SRWServer.props file, you will see in the log several attempts to open the file and finally a `FileNotFoundException`.

I am sure there are other failure modes during testing. I'll document them as they are reported.