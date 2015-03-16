A typical SRU URL looks like this: http://localhost:8080/SRW/search/test?query=dog&maximumRecords=1.

The first part points at your host and port. After the slash is the name of the Tomcat webapp that should process the request; SRW in this case. If you changed the name of the SRW.war file before Tomcat deployed it, then the webapp will have a different name. (If you changed the name to dspace-srw.war, then the webapp is named dspace-srw and the URL becomes http://localhost:8080/dspace-srw/search/DSpace?query=dog&maximumRecords=1.

Everything after the webapp part is compared with the patterns specified in the web.xml file. We have told Tomcat that the pattern `"search/*"` indicates a URL that should be given to the SRW Servlet. You can edit the web.xml file and change the url-pattern in the servlet-mapping for the SRWServlet, if you want to use a different pattern.

After the pattern comes the name of the database to be searched. If this is omitted, then the default database, as specified in your SRWServer.props file, will be searched. As originally deployed, the DSpace database is the default and can be omitted from the URL (i.e. http://localhost:8080/dspace-srw/search?query=dog&maximumRecords=1).

Finally, comes a question-mark and the SRW parameters. Information about SRW can be found at http://loc.gov/standards/sru