/*
   Copyright 2008 OCLC Online Computer Library Center, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
/*
 * SRWDatabase.java
 *
 * Created on August 4, 2003, 1:49 PM
 */

package ORG.oclc.os.SRW;

import de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator;
import de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator.VariantSpec;
import gov.loc.www.zing.srw.*;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;
import gov.loc.www.zing.srw.srw_bindings.SRWSoapBindingImpl;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.axis.MessageContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.Text;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.z3950.zing.cql.*;

/**
 *
 * @author  levan
 */
public abstract class SRWDatabase {
    private static final Log log=LogFactory.getLog(SRWDatabase.class);

    public static final String DEFAULT_SCHEMA = "default";
    public static final int DefaultResultSetTTL=0;
    // don't create a result set unless the client asks for it

    public abstract String      getExtraResponseData(QueryResult result,
                                  SearchRetrieveRequestType request);

    public abstract String      getIndexInfo();

    public abstract QueryResult getQueryResult(String query,
                                  SearchRetrieveRequestType request)
                                  throws InstantiationException, SRWDiagnostic;

    public abstract TermList    getTermList(CQLTermNode term, int position,
                                  int maxTerms, ScanRequestType request);

    public abstract void        init(String dbname, String srwHome,
                                  String dbHome, String dbPropertiesFileName,
                                  Properties dbProperties,
                                  HttpServletRequest request) throws Exception;

    public abstract boolean     supportsSort();


    public static ArrayList<SRWDatabase> allDbs=new ArrayList<SRWDatabase>();
    public static HashSet<String> badDbs=new HashSet<String>();
    public static HashSet<String> goodDbs=new HashSet<String>();
    public static HashMap<String, Integer> dbCount=new HashMap<String, Integer>();
    public static HashMap<String, LinkedList<SRWDatabase>> dbs=new HashMap<String, LinkedList<SRWDatabase>>();
    public static final HashMap<String, QueryResult> oldResultSets=new HashMap<String, QueryResult>();
    public static final HashMap<String, Long> timers=new HashMap<String, Long>();

    public static Properties srwProperties;
    public static String servletContext, srwHome;
    public static final Timer timer;

    static {
        timer=new Timer();
        timer.schedule(new HouseKeeping(timers, oldResultSets), 60000L, 60000L);
    }
    private boolean anonymousUpdates=false;
    private boolean returnResultSetId=true;
    private Matcher extractRecordIdFromUriPatternMatcher;
    private final Random rand=new Random();
    private final SimpleDateFormat ISO8601FORMAT=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public boolean reportedAWOL=false;
    public ContentTypeNegotiator conneg=null;
    public HashMap<String, Integer> schemaMaximumRecords=new HashMap<String, Integer>();
    public HashMap<String, String> contentLocations=new HashMap<String, String>();
    public HashMap<String, String> mediaTypes=new HashMap<String, String>();
    public HashMap<String, String> recordSchemas=new HashMap<String, String>();
    public HashMap<String, Transformer> transformers=new HashMap<String, Transformer>();
    public HashMap<String, Normalizer.Form> normalForm=new HashMap<String, Normalizer.Form>();
    public HttpHeaderSetter httpHeaderSetter=null;
    public long checkinTime=0, checkoutTime=0;
    public SearchRetrieveRequestType searchRequest;
    public SearchRetrieveResponseType response;
    public String  appStyleSheet=null, baseURL=null, checkoutReason=null,
                   databaseTitle, dbname, explainStyleSheet=null,
                   scanStyleSheet=null,
                   searchStyleSheet=null, singleRecordStyleSheet=null,
                   multipleRecordsStyleSheet=null, noRecordsStyleSheet=null;

    protected boolean     letDefaultsBeDefault=false;
    protected CQLParser   parser = new CQLParser(CQLParser.V1POINT1);
    protected DocumentBuilder docb = null;
    protected HashMap<String, String>
                          nameSpaces=new HashMap<String, String>(),
                          schemas=new HashMap<String, String>();
//    protected HashMap     sortTools = new HashMap();
    protected int         defaultNumRecs=10, defaultResultSetTTL,
                          maximumRecords=20, maxTerms = 9, position = 5, useCount=0;
    protected Properties  dbProperties;
    protected SRWDatabase db;
    protected String      dbHome, dbPropertiesFileName, defaultMimeType="text/xml",
                          defaultSchema, defaultStylesheet=null,
                          explainRecord=null, schemaInfo;
    
    
    public String add(byte[] record, RecordMetadata metadata) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void addSupports(String s, StringBuilder sb) {
        StringTokenizer st=new StringTokenizer(s, " =");
        if(st.countTokens()<2)
            return;
        sb.append("        <supports type=\"").append(st.nextToken()).append("\">");
        sb.append(s.substring(s.indexOf("=")+1).trim()).append("</supports>\n");
    }
    
    public void close() {
        timer.cancel();
    }

    public void addRenderer(String schemaName, String schemaID, Properties props)
      throws InstantiationException {
    }

    public static synchronized void createDB(final String dbname, Properties srwServerProperties)
      throws InstantiationException {
        createDB(dbname, srwServerProperties, null, null);
    }

    public static synchronized void createDB(final String dbname, Properties srwServerProperties, String context, HttpServletRequest request)
      throws InstantiationException {
        log.debug("Enter: initDB, dbname="+dbname);
        if(srwServerProperties==null)
            throw new InstantiationException("properties==null");
        log.info("creating instance "+dbCount.get(dbname)+" of database "+dbname);
        String dbn="db."+dbname;

        srwProperties=srwServerProperties;
        srwHome=srwServerProperties.getProperty("SRW.Home");
        servletContext=context;
        if(srwHome!=null && !srwHome.endsWith("/"))
            srwHome=srwHome+"/";
        log.debug("SRW.Home="+srwHome);
        Properties dbProperties=new Properties();
        String dbHome=srwServerProperties.getProperty(dbn+".home"),
               dbPropertiesFileName;
        if(dbHome!=null) {
            if(!dbHome.endsWith("/"))
                dbHome=dbHome+"/";
            log.debug("dbHome="+dbHome);
        }

        String className=srwServerProperties.getProperty(dbn+".class");
        log.debug("className="+className);
        if(className==null) {
            // let's see if there's a fallback database to use
            className=srwServerProperties.getProperty("db.default.class");
            if(className==null)
                throw new InstantiationException("No "+
                    dbn+".class entry in properties file");
            dbn="db.default";
        }
        if(className.equals("ORG.oclc.os.SRW.SRWPearsDatabase")) {
            log.info("** Warning ** the class ORG.oclc.os.SRW.SRWPearsDatabase has been replaced with ORG.oclc.os.SRW.Pears.SRWPearsDatabase");
            log.info("              Please correct the server's properties file");
            className="ORG.oclc.os.SRW.Pears.SRWPearsDatabase";
            log.debug("new className="+className);
        }
        else if(className.equals("ORG.oclc.os.SRW.SRWRemoteDatabase")) {
            log.info("** Warning ** the class ORG.oclc.os.SRW.SRWRemoteDatabase has been replaced with ORG.oclc.os.SRW.ParallelSearching.SRWRemoteDatabase");
            log.info("              Please correct the server's properties file");
            className="ORG.oclc.os.SRW.ParallelSearching.SRWRemoteDatabase";
            log.debug("new className="+className);
        }
        else if(className.equals("ORG.oclc.os.SRW.Pears.SRWRemoteDatabase")) {
            log.info("** Warning ** the class ORG.oclc.os.SRW.Pears.SRWRemoteDatabase has been replaced with ORG.oclc.os.SRW.ParallelSearching.SRWRemoteDatabase");
            log.info("              Please correct the server's properties file");
            className="ORG.oclc.os.SRW.ParallelSearching.SRWRemoteDatabase";
            log.debug("new className="+className);
        }
        else if(className.equals("ORG.oclc.os.SRW.SRWMergeDatabase")) {
            log.info("** Warning ** the class ORG.oclc.os.SRW.SRWMergeDatabase has been replaced with ORG.oclc.os.SRW.ParallelSearching.SRWMergeDatabase");
            log.info("              Please correct the server's properties file");
            className="ORG.oclc.os.SRW.ParallelSearching.SRWMergeDatabase";
            log.debug("new className="+className);
        }
        else if(className.equals("ORG.oclc.os.SRW.Pears.SRWMergeDatabase")) {
            log.info("** Warning ** the class ORG.oclc.os.SRW.Pears.SRWMergeDatabase has been replaced with ORG.oclc.os.SRW.ParallelSearching.SRWMergeDatabase");
            log.info("              Please correct the server's properties file");
            className="ORG.oclc.os.SRW.ParallelSearching.SRWMergeDatabase";
            log.debug("new className="+className);
        }
        else if(className.equals("ORG.oclc.os.SRW.SRWDLuceneDatabase")) {
            log.info("** Warning ** the class ORG.oclc.os.SRW.SRWLuceneDatabase has been replaced with ORG.oclc.os.SRW.DSpaceLucene.SRWLuceneDatabase");
            log.info("              Please correct the server's properties file");
            className="ORG.oclc.os.SRW.DSpaceLucene.SRWLuceneDatabase";
            log.debug("new className="+className);
        }
        SRWDatabase db;
        try {
            log.debug("creating class "+className);
            Class<? extends SRWDatabase>  dbClass = Class.forName(className).asSubclass(SRWDatabase.class);
            log.debug("creating instance of class "+dbClass);
            db=dbClass.newInstance();
            log.debug("class created");
        }
        catch(ClassNotFoundException e) {
            log.error("Unable to create Database class "+className+
                " for database "+dbname);
            log.error(e, e);
            throw new InstantiationException(e.getMessage());
        } catch (IllegalAccessException e) {
            log.error("Unable to create Database class "+className+
                    " for database "+dbname);
            log.error(e, e);
            throw new InstantiationException(e.getMessage());
        } catch (InstantiationException e) {
            log.error("Unable to create Database class "+className+
                    " for database "+dbname);
            log.error(e, e);
            throw new InstantiationException(e.getMessage());
        }

        dbPropertiesFileName=srwServerProperties.getProperty(dbn+".configuration");
        if(db.hasaConfigurationFile() || dbPropertiesFileName!=null) {
            if(dbPropertiesFileName==null) {
                throw new InstantiationException("No "+dbn+
                    ".configuration entry in properties file");
            }

            try {
                log.debug("Reading database configuration file: "+
                    dbPropertiesFileName);
                InputStream is=Utilities.openInputStream(dbPropertiesFileName, dbHome, srwHome);
                dbProperties.load(is);
                is.close();
            }
            catch(java.io.FileNotFoundException e) {
                log.error("Unable to open database configuration file!");
                log.error(e);
            }
            catch(IOException e) {
                log.error("Unable to load database configuration file!");
                log.error(e, e);
            }
            makeUnqualifiedIndexes(dbProperties);
            if(srwServerProperties.getProperty("hostAndPort")!=null)
                dbProperties.setProperty("hostAndPort", srwServerProperties.getProperty("hostAndPort"));
            try {
                db.init(dbname, srwHome, dbHome, dbPropertiesFileName, dbProperties, request);
            }
            catch(InstantiationException e) {
                throw e;
            }
            catch(Exception e) {
                log.error("Unable to initialize database "+dbname);
                log.error(e, e);
                throw new InstantiationException(e.getMessage());
            }
            if(request!=null) {
                StringBuilder urlStr=new StringBuilder("http://").append(request.getServerName());
                if(request.getServerPort()!=80)
                    urlStr.append(":").append(request.getServerPort());
                urlStr.append('/');
                String contextPath=request.getContextPath();
                if(contextPath!=null && contextPath.length()>1) {
                    urlStr.append(contextPath.substring(1));
                }
                int pathInfoIndex=Integer.parseInt(
                    srwServerProperties.getProperty("pathInfoIndex", "1"));
                String path=request.getPathInfo();
                StringBuilder newPath=new StringBuilder();
                if(path!=null) {
                    StringTokenizer st=new StringTokenizer(path, "/");
                    for(int i=0; st.hasMoreTokens(); i++)
                        if(i==pathInfoIndex-1) {
                            newPath.append('/').append(dbname);
                            st.nextToken();
                        }
                        else
                            newPath.append('/').append(st.nextToken());
                }
                urlStr.append(request.getServletPath()).append(newPath.toString());
                db.baseURL=urlStr.toString();
            }
            String temp=dbProperties.getProperty("maximumRecords");
            if(temp==null)
                temp=dbProperties.getProperty("configInfo.maximumRecords");
            if(temp!=null) {
                try {
                    db.setMaximumRecords(Integer.parseInt(temp));
                }
                catch(NumberFormatException e) {
                    log.error("bad value for maximumRecords: \""+temp+"\"");
                    log.error("maximumRecords parameter ignored");
                }
            }
            temp=dbProperties.getProperty("numberOfRecords");
            if(temp==null)
                temp=dbProperties.getProperty("configInfo.numberOfRecords");
            if(temp!=null) {
                try {
                    db.setNumberOfRecords(Integer.parseInt(temp));
                }
                catch(NumberFormatException e) {
                    log.error("bad value for numberOfRecords: \""+temp+"\"");
                    log.error("numberOfRecords parameter ignored");
                }
            }
            temp=dbProperties.getProperty("defaultResultSetTTL");
            if(temp==null)
                temp=dbProperties.getProperty("configInfo.resultSetTTL");
            if(temp!=null) {
                try {
                    db.setDefaultResultSetTTL(Integer.parseInt(temp));
                }
                catch(NumberFormatException e) {
                    log.error("bad value for defaultResultSetTTL: \""+temp+"\"");
                    log.error("defaultResultSetTTL parameter ignored");
                    db.setDefaultResultSetTTL(DefaultResultSetTTL);
                }
            }
            else
                db.setDefaultResultSetTTL(DefaultResultSetTTL);

            temp=dbProperties.getProperty("letDefaultsBeDefault");
            if(temp!=null && temp.equals("true"))
                db.letDefaultsBeDefault=true;
        }
        else { // default settings
            try {
                db.init(dbname, srwHome, dbHome, "(no database configuration file specified)", dbProperties, request);
            }
            catch(Exception e) {
                log.error("Unable to create Database class "+className+
                    " for database "+dbname);
                log.error(e, e);
                throw new InstantiationException(e.getMessage());
            }
            db.setDefaultResultSetTTL(DefaultResultSetTTL);
            log.info("no configuration file needed or specified");
        }

        if(!(db instanceof SRWDatabasePool)) {
            LinkedList<SRWDatabase> queue=dbs.get(dbname);
            if(queue==null)
                queue=new LinkedList<SRWDatabase>();
            queue.add(db);
            allDbs.add(db);
            if(log.isDebugEnabled())
                log.debug(dbname+" has "+queue.size()+" copies");
            dbs.put(dbname, queue);
        }
        if(dbCount.containsKey(dbname))
            dbCount.put(dbname, dbCount.get(dbname)+1);
        else
            dbCount.put(dbname, 1);
        log.debug("Exit: initDB");
    }


    public boolean delete(String recordID, RecordMetadata metadata) throws Exception {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static ExplainResponseType diagnostic(final int code,
      final String details, final ExplainResponseType response) {
        boolean         addDiagnostics=false;
        DiagnosticsType diagnostics=response.getDiagnostics();
        if(diagnostics==null)
            addDiagnostics=true;
        diagnostics=newDiagnostic(code, details, diagnostics);
        if(addDiagnostics)
            response.setDiagnostics(diagnostics);
        return response;
    }


    public static ScanResponseType diagnostic(final int code,
      final String details, final ScanResponseType response) {
        boolean         addDiagnostics=false;
        DiagnosticsType diagnostics=response.getDiagnostics();
        if(diagnostics==null)
            addDiagnostics=true;
        diagnostics=newDiagnostic(code, details, diagnostics);
        if(addDiagnostics)
            response.setDiagnostics(diagnostics);
        return response;
    }


    public static SearchRetrieveResponseType diagnostic(final int code,
      final String details, final SearchRetrieveResponseType response) {
        boolean         addDiagnostics=false;
        DiagnosticsType diagnostics=response.getDiagnostics();
        if(diagnostics==null)
            addDiagnostics=true;
        diagnostics=newDiagnostic(code, details, diagnostics);
        if(addDiagnostics)
            response.setDiagnostics(diagnostics);
        return response;
    }


    public ScanResponseType doRequest(ScanRequestType request) throws ServletException {
        searchRequest=null;
        response=null;
        ScanResponseType scanResponse = new ScanResponseType();
        String version=request.getVersion();
        if(version!=null && !version.equals("1.1"))
            return diagnostic(SRWDiagnostic.UnsupportedVersion, version, scanResponse);

        CQLTermNode root;
        int max = maxTerms,  pos = position;
        long startTime = System.currentTimeMillis();
        PositiveInteger pi = request.getMaximumTerms();
        if(pi!=null) {
            max=pi.intValue();
            pos=max/2+1;
        }
        NonNegativeInteger nni = request.getResponsePosition();
        if(nni!=null)
            pos=nni.intValue();
        String scanTerm = request.getScanClause();
        if (scanTerm!=null)
            log.info("scanTerm:\n" + Utilities.byteArrayToString(scanTerm.getBytes(Charset.forName("UTF-8"))));
        log.info("maxTerms="+max+", position="+pos);
        try {
            root = Utilities.getFirstTerm(parser.parse(scanTerm));
        } catch (CQLParseException e) {
            log.error(e);
            return diagnostic(SRWDiagnostic.QuerySyntaxError, e.getMessage(),
                scanResponse);
        } catch (IOException e) {
            log.error(e);
            return diagnostic(SRWDiagnostic.QuerySyntaxError, e.getMessage(),
                scanResponse);
        }
        if (root.getTerm().length()==0)
            // The method getQualifier() was replaced by getIndex() in version 
            // 1.0 of the parser. This code ensures that either one works.
            //root = new CQLTermNode(root.getQualifier(), root.getRelation(), "$");
            root = new CQLTermNode(SRWSoapBindingImpl.getQualifier(root), root.getRelation(), "!");
        String resultSetID = root.getResultSetName();
        if (resultSetID!=null) { // you can't scan on resultSetId!
            return diagnostic(SRWDiagnostic.UnsupportedIndex,
                    "cql.resultSetId", scanResponse);
        }
        TermsType terms = new TermsType();
        TermList tl=getTermList(root, pos, max, request);
        terms.setTerm(tl.getTerms());
        scanResponse.setTerms(terms);

        ArrayList<DiagnosticType> diagnostics = tl.getDiagnostics();
        if (diagnostics!=null && !diagnostics.isEmpty()) {
            DiagnosticType diagArray[] = new DiagnosticType[diagnostics.size()];
            diagnostics.toArray(diagArray);
            scanResponse.setDiagnostics(new DiagnosticsType(diagArray));
        }

        log.info("scan "+scanTerm+": (" + (System.currentTimeMillis() - startTime) + "ms)");
        return scanResponse;
    }



    public SearchRetrieveResponseType doRequest(SearchRetrieveRequestType request) throws ServletException {
        boolean cachedResultSet=false;
        QueryResult result;
        searchRequest=request;
        response = new SearchRetrieveResponseType();

        response.setNumberOfRecords(new NonNegativeInteger("0"));        
        //        try {
                    MessageContext msgContext = MessageContext.getCurrentContext();

        String version=request.getVersion();
        if(version!=null && !version.equals("1.1"))
            return diagnostic(SRWDiagnostic.UnsupportedVersion, version, response);

        String query = request.getQuery();
        if(query==null || query.length()==0)
            return diagnostic(SRWDiagnostic.MandatoryParameterNotSupplied, "query", response);

        if(log.isDebugEnabled())
            log.debug("query:\n" + Utilities.byteArrayToString(query.getBytes(Charset.forName("UTF-8"))));

        String recordPacking = request.getRecordPacking();
        if(recordPacking==null) {
            if(msgContext!=null && msgContext.getProperty("sru")!=null)
                recordPacking="xml"; // default for sru
            else
                recordPacking="string"; // default for srw
        }

        String resultSetID = getResultSetId(query);
        if (resultSetID!=null) { // got a cached result
            log.info("resultSetID="+resultSetID);
            result = oldResultSets.get(resultSetID);
            if (result==null)
                return diagnostic(SRWDiagnostic.ResultSetDoesNotExist,
                        resultSetID, response);
            cachedResultSet=true;
        }
        else { // Evaluate the query.
            try {
                result = getQueryResult(query, request);
            } catch (InstantiationException e) {
                log.error("Exception "+e.getMessage()+" caught while doing query:");
                log.error(Utilities.byteArrayToString(query.getBytes(Charset.forName("UTF-8"))));
                log.error("request: "+request);
                log.error(e, e);
                return diagnostic(SRWDiagnostic.GeneralSystemError,
                        e.getMessage(), response);
            } catch (SRWDiagnostic e) {
                log.error("Diagnostic "+e.getCode()+" caught while doing query:");
                log.error(Utilities.byteArrayToString(query.getBytes(Charset.forName("UTF-8"))));
                log.error("request: "+request);
                log.error(e, e);
                return diagnostic(e.getCode(),
                        e.getAddInfo(), response);
            }
        }

        long postingsCount = result.getNumberOfRecords();
        log.info("'" + query + "'==> " + postingsCount);
        response.setNumberOfRecords(new NonNegativeInteger(Long.toString(postingsCount)));

        int resultSetTTL = defaultResultSetTTL;
        NonNegativeInteger nni = request.getResultSetTTL();
        if(nni!=null)
            resultSetTTL=nni.intValue();
        result.setResultSetIdleTime(resultSetTTL);
        response.setResultSetIdleTime(new PositiveInteger(Integer.toString(resultSetTTL+1)));
        if (postingsCount>0) {  // we don't mess with records otherwise
            if (resultSetTTL>0 && returnResultSetId) {
                // cache the resultSet and set (or reset) its timer
                if(resultSetID==null)
                    resultSetID=makeResultSetID();
                log.debug("keeping resultSet '"+resultSetID+"' for "+resultSetTTL+
                    " seconds");
                oldResultSets.put(resultSetID, result);
                resetTimer(resultSetID);
                response.setResultSetId(resultSetID);
                response.setResultSetIdleTime(
                    new PositiveInteger(Integer.toString(resultSetTTL)));
                cachedResultSet=true;
            }

            int numRecs = defaultNumRecs;
            String schemaName = request.getRecordSchema();
            log.debug("request.getRecordSchema()="+schemaName);
            if(schemaName==null)
                schemaName="default";
            log.debug("schemaName="+schemaName);
            String schemaID=null;
//            if(!letDefaultsBeDefault && !schemaName.equals("default")) {
            if(!letDefaultsBeDefault) {
                schemaID = getSchemaID(schemaName);
                if(schemaID==null) {
                    log.debug("unknown schema: "+schemaName);
                    diagnostic(SRWDiagnostic.UnknownSchemaForRetrieval, schemaName, response);
                    numRecs=0;
                }
            }
            
            NonNegativeInteger maxRecs = request.getMaximumRecords();
            if (maxRecs!=null)
                numRecs = (int) Math.min(maxRecs.longValue(), maximumRecords);
            Integer schemaNumRecs=schemaMaximumRecords.get(schemaID);
            log.info("schemaID="+schemaID+", schemaNumRecs="+schemaNumRecs+", numRecs="+numRecs);
            if(schemaNumRecs!=null && schemaNumRecs<numRecs)
                numRecs=schemaNumRecs;
            log.info("numRecs="+numRecs);

            long startPoint = 1;
            PositiveInteger startRec = request.getStartRecord();
            if(startRec!=null)
                startPoint=startRec.longValue();
            if (startPoint>postingsCount)
                diagnostic(SRWDiagnostic.FirstRecordPositionOutOfRange,
                        null, response);

            if ((startPoint-1+numRecs)>postingsCount)
                numRecs = (int) (postingsCount-(startPoint-1));

            if (!recordPacking.equals("xml") &&
              !recordPacking.equals("string")) {
                diagnostic(SRWDiagnostic.UnsupportedRecordPacking, recordPacking, response);
                numRecs=0;
            }

            if (numRecs==0)
                response.setNextRecordPosition(new PositiveInteger("1"));
            else
                if (numRecs>0) { // render some records into SGML
                    String sortKeys = request.getSortKeys();
                    log.debug("schemaName="+schemaName+", schemaID="+schemaID+
                        ", sortKeys="+sortKeys);                    
                    if(sortKeys!=null && sortKeys.length()>0) { // do we need to sort them first?
                        QueryResult sortedResult=result.getSortedResult(sortKeys);
                        if(sortedResult==null) { // sigh, we've got some sorting to do
//                            log.info("sorting resultSet");
//                            boolean       ascending=true;
//                            SortTool sortTool=null;
//                            String   sortKey;
//                            if(schemaName==null)
//                                schemaName="default";
//                            log.info("recordSchema="+schemaName);
//                            Object handler=transformers.get(schemaName);
//                            if(handler==null) {
//                                log.error("no handler for schema "+schemaName);
//                                if(log.isInfoEnabled()) {
//                                    for(Enumeration enum2=transformers.keys();
//                                      enum2.hasMoreElements();)
//                                        log.info("handler name="+(String)enum2.nextElement());
//                                }
//                                return diagnostic(SRWDiagnostic.UnknownSchemaForRetrieval,
//                                    schemaName, response);
//                            }
//                            StringTokenizer keysTokenizer=new StringTokenizer(sortKeys);
//                            //while(keysTokenizer.hasMoreTokens()) {
//                            // just one key for now
//                                sortKey=keysTokenizer.nextToken();
//                                sortTool=new PearsSortTool(sortKey, transformers);
//                            //}
//                            String sortSchema=(String)nameSpaces.get(sortTool.prefix);
//                            if(sortSchema==null)
//                                sortSchema="";
//                            sortTool.setSchema(sortSchema);
//                            sortTool.makeSortElementExtractor();
//                            BerString        doc;
//                            DataDir          recDir;
//                            DocumentIterator list=(DocumentIterator)result.getDocumentIdList();
//                            int              listEntry;
//                            String           stringRecord;
//                            entries=new SortEntry[postings];
//                            for(int i=0; i<postings; i++) {
//                                listEntry=list.nextInt();
//                                log.debug("listEntry="+listEntry);
//                                doc=(BerString)pdb.getDocument(listEntry);
//                                recDir=new DataDir(doc);
//                                if(sortTool.dataType.equals("text"))
//                                    entries[i]=new SortEntry(sortTool.extract(recDir), listEntry);
//                                else {
//                                    try {
//                                        entries[i]=new SortEntry(Integer.parseInt(sortTool.extract(recDir)), listEntry);
//                                    }
//                                    catch(java.lang.NumberFormatException e) {
//                                        entries[i]=new SortEntry(0, listEntry);
//                                    }
//                                }
//                                if(entries[i].getKey()==null) { // missing value code
//                                    if(sortTool.missingValue.equals("abort"))
//                                        return diagnostic(SRWDiagnostics.SortEndedDueToMissingValue,
//                                            null, response);
//                                    else if(sortTool.missingValue.equals("highValue"))
//                                        entries[i]=new SortEntry("\ufffffe\ufffffe\ufffffe\ufffffe\ufffffe", listEntry);
//                                    else if(sortTool.missingValue.equals("lowValue"))
//                                        entries[i]=new SortEntry("\u000000", listEntry);
//                                    else { // omit
//                                        i--;
//                                        postings--;
//                                    }
//                                }
//                                if(log.isDebugEnabled())
//                                    log.debug("entries["+i+"]="+entries[i]);
//                            }
//                            Arrays.sort(entries);
//                            sortedResultSets.put(resultSetID+"/"+sortKeys, entries);
//                            sortTools.put(sortKeys, sortTool);
                        }
                        else {
                            log.debug("reusing old sorted resultSet");
                        }
                        if(sortedResult==null)
                            return diagnostic(SRWDiagnostic.SortNotSupported,
                                null, response);
                        else
                            result=sortedResult;
                    }  // if(sortKeys!=null && sortKeys.length()>0)
                    
                    // render some records
                    RecordIterator list;
                    try {
                        log.debug("making RecordIterator, startPoint="+startPoint+", schemaID="+schemaID);
                        list=result.recordIterator(startPoint, numRecs, schemaID, request.getExtraRequestData());
                        if(list==null)
                            throw new InstantiationException();
                    } catch (InstantiationException e) {
                        log.error(e, e);
                        return diagnostic(SRWDiagnostic.GeneralSystemError,
                            e.getMessage(), response);
                    }
                    RecordsType records = new RecordsType();

                    records.setRecord(new RecordType[numRecs]);
                    int                    i;
                    MessageElement         elems[];
                    Record                 rec;
                    RecordType             rt;
                    String                 recStr = "";
                    StringOrXmlFragment    frag;

                    /**
                     * One at a time, retrieve and display the requested documents.
                     */
                    log.debug("trying to get "+numRecs+
                        " records starting with record "+startPoint+
                        " from a set of "+postingsCount+" records");
                    for (i=0; i<numRecs && list.hasNext(); i++) {
                        rt = new RecordType();
                        rt.setRecordPacking(recordPacking);
                        frag = new StringOrXmlFragment();
                        elems = new MessageElement[1];
                        frag.set_any(elems);
                        try {
                            rec=list.nextRecord();
                            log.debug("rec="+rec);
                            recStr=transform(rec, schemaID).getRecord();
                            if (log.isDebugEnabled())
                                log.debug("Transformed XML:\n" + Utilities.byteArrayToString(
                                    recStr.getBytes(Charset.forName("UTF-8"))));
                            makeElem(recStr, rt, schemaID, schemaName, recordPacking, docb, elems);
                            if(rec.hasExtraRecordInfo())
                                setExtraRecordData(rt, rec.getExtraRecordInfo());
                        } catch (IOException e) {
                            log.error("error getting document "+(i+1)+", postings="+postingsCount);
                            log.error(e, e);
                            try {
                                makeElem(SRWDiagnostic.newSurrogateDiagnostic(
                                    "info:srw/diagnostic/1/",
                                    SRWDiagnostic.RecordTemporarilyUnavailable,
                                    null), rt, null,
                                    "info:srw/schema/1/diagnostics-v1.1",
                                    recordPacking, docb, elems);
                            } catch (IOException e2) {
                                log.error(e, e);
                                break;
                            } catch (SAXException e2) {
                                log.error(e, e);
                                break;
                            }
                            log.error("error getting document "+(i+1)+", postings="+postingsCount);
                            log.error(e, e);
                        } catch (NoSuchElementException e) {
                            log.error("error getting document "+(i+1)+", postings="+postingsCount);                            log.error(e, e);
                            try {
                                makeElem(SRWDiagnostic.newSurrogateDiagnostic(
                                    "info:srw/diagnostic/1/",
                                    SRWDiagnostic.RecordTemporarilyUnavailable,
                                    null), rt, null,
                                    "info:srw/schema/1/diagnostics-v1.1",
                                    recordPacking, docb, elems);
                            } catch (IOException e2) {
                                log.error(e, e);
                                break;
                            } catch (SAXException e2) {
                                log.error(e, e);
                                break;
                            }
                        } catch (SAXException e) {
                            try {
                                log.error(e, e);
                                makeElem(SRWDiagnostic.newSurrogateDiagnostic(
                                    "info:srw/diagnostic/1/",
                                    SRWDiagnostic.RecordTemporarilyUnavailable,
                                    null), rt, null,
                                    "info:srw/schema/1/diagnostics-v1.1",
                                    recordPacking, docb, elems);
                            } catch (IOException e2) {
                                log.error(e, e);
                                break;
                            } catch (SAXException e2) {
                                log.error(e, e);
                                break;
                            }
                            log.error("error getting document "+(i+1)+", postings="+postingsCount);
                            log.error(e, e);
                            log.error("Bad record:\n" + Utilities.byteArrayToString(
                                    recStr.getBytes(Charset.forName("UTF-8"))));
                        } catch (SRWDiagnostic e) {
                            try {
                                makeElem(SRWDiagnostic.newSurrogateDiagnostic(
                                    "info:srw/diagnostic/1/", e.getCode(),
                                    e.getAddInfo()), rt, null,
                                    "info:srw/schema/1/diagnostics-v1.1",
                                    recordPacking, docb, elems);
                            } catch (IOException e2) {
                                log.error(e, e);
                                break;
                            } catch (SAXException e2) {
                                log.error(e, e);
                                break;
                            }
                            log.error("error getting document "+(i+1)+", postings="+postingsCount);
                            log.error(e, e);
                            log.error("Bad record:\n" + Utilities.byteArrayToString(
                                    recStr.getBytes(Charset.forName("UTF-8"))));
                        }

                        rt.setRecordData(frag);

                        rt.setRecordPosition(new PositiveInteger(Long.toString(startPoint+i)));

                        records.setRecord(i, rt);
                    }
                    list.close();
                    response.setRecords(records);
                    if (startPoint+i<=postingsCount)
                        response.setNextRecordPosition(new PositiveInteger(Long.toString(startPoint+i)));
                } // else if(numRecs>0)
        } // if(postingsCount>0)

        String extraResponseData = getExtraResponseData(result, request);
        if(extraResponseData!=null)
            setExtraResponseData(response, extraResponseData);

        ArrayList<DiagnosticType> diagnostics = result.getDiagnostics();
        if (diagnostics!=null && !diagnostics.isEmpty()) {
            DiagnosticType diagArray[] = new DiagnosticType[diagnostics.size()];
            diagnostics.toArray(diagArray);
            response.setDiagnostics(new DiagnosticsType(diagArray));
        }
        
        if(!cachedResultSet)
            result.close();

        log.debug("exit doRequest");
        return response;
//        }
//        catch(Exception e) {
//            //log.error(e);
//            log.error(e, e);
//            throw new ServletException(e.getMessage());
//        }
    }

    public String extractRecordIdFromUri(String uri) {
        String decodedURI=null;
        try {
            decodedURI = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            log.error("undecodable URI: "+uri, ex);
        }
        log.info("uri="+decodedURI);
        extractRecordIdFromUriPatternMatcher.reset(decodedURI);
        if(extractRecordIdFromUriPatternMatcher.find()) {
            log.info("groupCount="+extractRecordIdFromUriPatternMatcher.groupCount());
            StringBuilder sb=new StringBuilder();
            for(int i=1; i<=extractRecordIdFromUriPatternMatcher.groupCount(); i++) {
                log.info("group("+i+")="+extractRecordIdFromUriPatternMatcher.group(i));
                sb.append(extractRecordIdFromUriPatternMatcher.group(i));
            }
            return sb.toString();
        }
        log.info("recordID not found.  Pattern="+extractRecordIdFromUriPatternMatcher.pattern());
        return null;
    }

    public String extractSortField(Object record) {
        return null;
    }

    public String getConfigInfo() {
        StringBuilder sb=new StringBuilder();
        sb.append("        <configInfo>\n");
        sb.append("          <default type=\"maximumRecords\">").append(getMaximumRecords()).append("</default>\n");
        sb.append("          <default type=\"numberOfRecords\">").append(getNumberOfRecords()).append("</default>\n");
        sb.append("          <default type=\"retrieveSchema\">").append(schemas.get("default")).append("</default>\n");
        if(dbProperties!=null) {
            String s=dbProperties.getProperty("supports");
            if(s!=null)
                addSupports(s, sb);
            else
                for(int i=1; ; i++) {
                    s=dbProperties.getProperty("supports"+i);
                    if(s!=null)
                        addSupports(s, sb);
                    else
                        break;
                }
        }
        sb.append("          </configInfo>\n");
        return sb.toString();
    }


    public String getDatabaseInfo() {
        StringBuilder sb=new StringBuilder();
        sb.append("        <databaseInfo>\n");
        if(dbProperties!=null) {
            String t=dbProperties.getProperty("databaseInfo.title");
            if(t!=null) {
                databaseTitle=t;
                sb.append("          <title>").append(t).append("</title>\n");
            }
            t=dbProperties.getProperty("databaseInfo.description");
            if(t!=null)
                sb.append("          <description>").append(t).append("</description>\n");
            t=dbProperties.getProperty("databaseInfo.author");
            if(t!=null)
                sb.append("          <author>").append(t).append("</author>\n");
            t=dbProperties.getProperty("databaseInfo.contact");
            if(t!=null)
                sb.append("          <contact>").append(t).append("</contact>\n");
            t=dbProperties.getProperty("databaseInfo.restrictions");
            if(t!=null)
                sb.append("          <restrictions>").append(t).append("</restrictions>\n");
        }
        sb.append("          <implementation version='1.1' indentifier='http://www.oclc.org/research/software/srw'>\n");
        sb.append("            <title>OCLC Research SRW Server version 1.1</title>\n");
        sb.append("            </implementation>\n");
        sb.append("          </databaseInfo>\n");
        return sb.toString();
    }


    public static SRWDatabase getDB(String dbname, Properties srwServerProperties) {
        return getDB(dbname, srwServerProperties, null, null);
    }
    public static SRWDatabase getDB(String dbname, Properties srwServerProperties, String servletContext, HttpServletRequest request) {
        log.debug("enter SRWDatabase.getDB");
//        new Exception("getDB called").printStackTrace();
        if(badDbs.contains(dbname)) // we've seen this one before
            return null;

        LinkedList<SRWDatabase> queue=dbs.get(dbname);
        SRWDatabase db=null;
        try {
            if(queue==null)
                log.info("No SRW databases opened yet for database "+dbname);
            else {
                log.debug("about to synchronize #1 on queue");
                synchronized(queue) {
                    if(queue.isEmpty())
                        log.info("No SRW databases left in queue for database "+dbname);
                    else {
                        db=queue.removeFirst();
                        if(db==null)
                            log.debug("popped a null database off the queue for database "+dbname);
                    }
                }
                log.debug("done synchronize #1 on queue");
            }
            if(db==null) {
                if(request!=null)
                    log.info("Opening an SRW database for "+dbname+
                        " for request "+request.getContextPath()+"?"+
                        request.getQueryString()+
                        " for IP "+request.getRemoteAddr());
                else {
                    log.info("Opening an SRW database for "+dbname);
                    log.info("called from ", new Exception());
                }
                try{
                    while(db==null) {
                        createDB(dbname, srwServerProperties, servletContext, request);
                        queue=dbs.get(dbname);
                        log.debug("about to synchronize #2 on queue");
                        synchronized(queue) {
                            if(!queue.isEmpty()) // crap, someone got to it before us
                                db=queue.removeFirst();
                        }
                    }
                    log.debug("done synchronize #2 on queue");
                }
                catch(InstantiationException e) { // database not available
                    // but, we don't want to mark a database that was good
                    // as bad now because of some transient error
                    if(!goodDbs.contains(dbname)) // we've never had it before
                        badDbs.add(dbname); // mark it as a bad dbname
                    log.error(e, e);
                    return null;
                }
            }
        }
        catch(Exception e) {
            log.error(e,e);
            log.error("shoot!");
            return null;
        }
        if(log.isDebugEnabled())
            log.debug("getDB: db="+db);
        goodDbs.add(dbname);
        if(db.useCount!=0) {
            // we're trying to check out something that is in use!
            // let's drop this one on the floor, hoping that whoever checked
            // it out will return it eventually, and ask for a new one
            log.error("dropping db="+db);
            return getDB(dbname, srwServerProperties, servletContext, request);
        }
        db.useCount++;
        if(request!=null)
            db.checkoutReason=request.getQueryString();
        db.checkoutTime=System.currentTimeMillis();
        log.debug("exit SRWDatabase.getDB");
        return db;
    }

    
    public Properties getDbProperties() {
        return dbProperties;
    }

    
    public int getDefaultResultSetTTL() {
        return defaultResultSetTTL;
    }


    public String getDefaultSchema() {
        return defaultSchema;
    }


//    public String getExplainRecord() {
//        if(explainRecord==null)
//            makeExplainRecord(null);
//        return explainRecord;
//    }


    public String getExplainRecord(HttpServletRequest request) {
        if(explainRecord==null)
            makeExplainRecord(request);
        return explainRecord;
    }


    /**
     *  overridable method to return the update date for a database
     * @return long
     */
    public long getLastUpdated() {
        return 0;
    }


    public int getMaximumRecords() {
        return maximumRecords;
    }
    
    
    public String getMetaInfo() {
        StringBuilder sb=new StringBuilder();
        sb.append("        <metaInfo>\n");
        if(dbProperties!=null) {
            String t=dbProperties.getProperty("metaInfo.dateModified");
            if(t!=null)
                sb.append("          <dateModified>").append(t).append("</dateModified>\n");
            t=dbProperties.getProperty("metaInfo.aggregatedFrom");
            if(t!=null)
                sb.append("          <aggregatedFrom>").append(t).append("</aggregatedFrom>\n");
            t=dbProperties.getProperty("metaInfo.dateAggregated");
            if(t!=null)
                sb.append("          <dateAggregated>").append(t).append("</dateAggregated>\n");
        }
        sb.append("          </metaInfo>\n");
        return sb.toString();
    }


    /**
     *  How many records does this database have in it?
     * @return int
     */
    public int getNumberOfDatabaseRecords() {
        return 0;
    }


    public int getNumberOfRecords() {
        return defaultNumRecs;
    }


    public static String getResultSetId(String query) {
        StringTokenizer st = new StringTokenizer(query, " =\"");
        int num = st.countTokens();
        if(num<2 || num>3)
            return null;
        String index = st.nextToken();
        if(!index.equals("cql.resultSetId"))
            return null;
        String relationOrResultSetId = st.nextToken();
        if(relationOrResultSetId.equals("exact")) {
            if(num<3)
                return null;
            return st.nextToken();
        }
        if(num==2)
            return relationOrResultSetId;
        return null;
    }

    public static ArrayList<String> getResultSetIds(CQLNode root) throws SRWDiagnostic {
        ArrayList<String> resultSetIds=new ArrayList<String>();
        getResultSetIds(root, resultSetIds);
        return resultSetIds;
    }
    
    public static void getResultSetIds(CQLNode root, ArrayList<String> resultSetIds) throws SRWDiagnostic {
        if(root instanceof CQLBooleanNode) {
            CQLBooleanNode cbn=(CQLBooleanNode)root;
            getResultSetIds(cbn.left, resultSetIds);
            getResultSetIds(cbn.right, resultSetIds);
        }
        else {
            CQLTermNode ctn=(CQLTermNode)root;
            //if(ctn.getQualifier().equals("cql.resultSetId")) {
            if(SRWSoapBindingImpl.getQualifier(ctn).equals("cql.resultSetId")) {
                String resultSetId=ctn.getTerm();
                if(oldResultSets.get(resultSetId)==null)
                    throw new SRWDiagnostic(SRWDiagnostic.ResultSetDoesNotExist, resultSetId);
                resultSetIds.add(resultSetId);
                resetTimer(resultSetId);
                log.info("added resultSetId "+ctn.getTerm());
            }
        }
    }



    /**
     *  This class assumes that schema information was provided in the .props
     *  file for this database.  This method provides a way for extending
     *  classes to provide the schemaName to schemaID mapping themselves.
     * @param schemaName
     * @return 
     */
    public String getSchemaID(String schemaName) {
        return schemas.get(schemaName);
    }


    public String getSchemaInfo() {
        return schemaInfo;
    }


    public boolean hasaConfigurationFile() {
        return true;  //  expect a configuration file unless overridden
    }


    protected void initDB(final String dbname, String srwHomeVal, String dbHome, String dbPropertiesFileName, Properties dbProperties) {
        log.debug("Enter: private initDB, dbname="+dbname);
        this.dbname=dbname;
        srwHome=srwHomeVal;
        this.dbHome=dbHome;
        this.dbPropertiesFileName=dbPropertiesFileName;
        this.dbProperties=dbProperties;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
//            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            docb=dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.error(e, e);
        }

        if(dbProperties==null) {
            log.warn("No properties file provided for database "+dbname);
            return;
        }

        String httpHeaderSetterClass=dbProperties.getProperty("HttpHeaderSetter");
        if(httpHeaderSetterClass!=null) {
            try {
                httpHeaderSetter = (HttpHeaderSetter) Class.forName(httpHeaderSetterClass).newInstance();
                httpHeaderSetter.init(dbProperties);
            }
            catch (InstantiationException ex) {
                log.error("Unable to create HttpHeaderSetter: "+httpHeaderSetterClass, ex);
            }
            catch (IllegalAccessException ex) {
                log.error("Unable to create HttpHeaderSetter: "+httpHeaderSetterClass, ex);
            }
            catch (ClassNotFoundException ex) {
                log.error("Unable to create HttpHeaderSetter: "+httpHeaderSetterClass, ex);
            }
        }

        // if the client doesn't say what they want, what should they get?
        // typically, this only happens with applications that don't set
        // the "accept" header, i.e., non-browser applications.  The
        // default value is set to text/xml, assuming that non-browser apps
        // want to see the full xml, not some rendered html
        defaultMimeType=dbProperties.getProperty("defaultMimeType", defaultMimeType);

        // get schema transformers
        String          firstSchema=null, xmlSchemaList=dbProperties.getProperty("xmlSchemas");
        StringBuilder    schemaInfoBuf=new StringBuilder("        <schemaInfo>\n");
        StringTokenizer st;
        if(xmlSchemaList!=null) {
            String      name, schemaIdentifier, schemaName, transformerName,
                        value;
            ArrayList<String> parms, values;
            st=new StringTokenizer(xmlSchemaList, ", \t");
            log.info("xmlSchemaList="+xmlSchemaList);
            while(st.hasMoreTokens()) {
                schemaName=st.nextToken();
                log.debug("looking for schema "+schemaName);
                if(firstSchema==null)
                    firstSchema=schemaName;
                schemaIdentifier=dbProperties.getProperty(schemaName+".identifier");
                transformerName=dbProperties.getProperty(schemaName+".transformer");
                if(transformerName==null) {
                    // maybe this is an old .props file and the transformer name
                    // is associated with the bare schemaName
                    transformerName=dbProperties.getProperty(schemaName);
                }
                parms=new ArrayList<String>();
                values=new ArrayList<String>();
                Enumeration<Object> propertyNames = dbProperties.keys();
                while(propertyNames.hasMoreElements()) {
                    name=(String) propertyNames.nextElement();
                    if(name.startsWith(schemaName+".parameter.")) {
                        value=dbProperties.getProperty(name);
                        values.add(value);
                        name=name.substring(schemaName.length()+11);
                        parms.add(name);
                        if(log.isDebugEnabled())
                            log.debug("transformer parm: "+name+"="+value);
                    }
                }

                String s=dbProperties.getProperty(schemaName+".maximumRecords");
                if(s!=null) {
                    schemaMaximumRecords.put(schemaName, Integer.parseInt(s));
                    if(schemaIdentifier!=null)
                        schemaMaximumRecords.put(schemaIdentifier, Integer.parseInt(s));
                }

                try {
                    if(schemaIdentifier!=null) {
                        schemas.put(schemaName, schemaIdentifier);
                        schemas.put(schemaIdentifier, schemaIdentifier);
                    }
                    Transformer t = Utilities.addTransformer(schemaName,
                            transformerName, dbHome, parms, values,
                            dbProperties, dbPropertiesFileName);
                    if(t!=null) {
                        transformers.put(schemaName, t);
                        if(schemaIdentifier!=null)
                            transformers.put(schemaIdentifier, t);
                    }
                    addRenderer(schemaName, schemaIdentifier, dbProperties);
                    String schemaLocation=dbProperties.getProperty(schemaName+".location");
                    String schemaTitle=dbProperties.getProperty(schemaName+".title");
                    String schemaNamespace=dbProperties.getProperty(schemaName+".namespace");
                    if(schemaNamespace!=null)
                        nameSpaces.put(schemaName, schemaNamespace);
                    else
                        nameSpaces.put(schemaName, "NoNamespaceProvided");
                    schemaInfoBuf.append("          <schema sort=\"false\" retrieve=\"true\"")
                                 .append(" name=\"").append(schemaName)
                                 .append("\"\n              identifier=\"").append(schemaIdentifier)
                                 .append("\"\n              location=\"").append(schemaLocation).append("\">\n")
                                 .append("            <title>").append(schemaTitle).append("</title>\n")
                                 .append("            </schema>\n");
                }
                catch(FileNotFoundException e) {
                    log.error("Unable to load schema "+schemaName);
                    log.error(e, e);
                } catch (TransformerConfigurationException e) {
                    log.error("Unable to load schema "+schemaName);
                    log.error(e, e);
                } catch (UnsupportedEncodingException e) {
                    log.error("Unable to load schema "+schemaName);
                    log.error(e, e);
                } catch (InstantiationException e) {
                    log.error("Unable to load schema "+schemaName);
                    log.error(e, e);
                }
            }

            defaultSchema=dbProperties.getProperty("defaultSchema");
            if(defaultSchema==null)
                defaultSchema=firstSchema;
            log.info("defaultSchema="+defaultSchema);
            schemaIdentifier=schemas.get(defaultSchema);
            log.info("default schemaID="+schemaIdentifier);
            if(schemaIdentifier==null)
                log.error("Default schema "+defaultSchema+" not loaded");
            else {
                schemas.put("default", schemaIdentifier);
                Transformer t=transformers.get(defaultSchema);
                if(t!=null) {
                    transformers.put("default", t);
                }
            }
        }
        schemaInfoBuf.append("          </schemaInfo>\n");
        schemaInfo=schemaInfoBuf.toString();

        if(srwProperties!=null) {
            String t=srwProperties.getProperty("SRW.Context");
            if(t!=null)
                servletContext=t;
        }
        explainStyleSheet=dbProperties.getProperty("explainStyleSheet");
        if(explainStyleSheet==null)
            explainStyleSheet="/$context/explainResponse.xsl";
        searchStyleSheet=dbProperties.getProperty("searchStyleSheet");
        singleRecordStyleSheet=dbProperties.getProperty("singleRecordStyleSheet");
        multipleRecordsStyleSheet=dbProperties.getProperty("multipleRecordsStyleSheet");
        noRecordsStyleSheet=dbProperties.getProperty("noRecordsStyleSheet");
        if(searchStyleSheet==null && singleRecordStyleSheet==null && multipleRecordsStyleSheet==null)
            searchStyleSheet="/$context/searchRetrieveResponse.xsl";
        scanStyleSheet=dbProperties.getProperty("scanStyleSheet");
        if(scanStyleSheet==null)
            scanStyleSheet="/$context/scanResponse.xsl";
        appStyleSheet=dbProperties.getProperty("APPStyleSheet");
        if(servletContext!=null && servletContext.startsWith("/"))
            servletContext=servletContext.substring(1);
        if(servletContext!=null && servletContext.length()>0) {
            explainStyleSheet=explainStyleSheet.replace("$context", servletContext);
            searchStyleSheet=searchStyleSheet.replace("$context", servletContext);
            scanStyleSheet=scanStyleSheet.replace("$context", servletContext);
        }
        else {
            explainStyleSheet=explainStyleSheet.replace("/$context", "");
            searchStyleSheet=searchStyleSheet.replace("/$context", "");
            scanStyleSheet=scanStyleSheet.replace("/$context", "");
        }

        conneg=new ContentTypeNegotiator();
        log.debug("conneg="+conneg);
        Enumeration<Object> propertyNames = dbProperties.keys();
        int offset;
        String contentLocation, key, mediaType, mimeType, name, form,
               recordSchema, value;
        VariantSpec vs;
        while(propertyNames.hasMoreElements()) {
            key=(String) propertyNames.nextElement();
            offset=key.indexOf("mimeTypes");
            if(offset>0) {
                name=key.substring(0, offset-1);
                value=dbProperties.getProperty(key);
                log.info(key+"="+value);
                st=new StringTokenizer(value, ", ");
                mimeType=st.nextToken();
                vs=conneg.addVariant(mimeType);
                mediaType=vs.getMediaType().getMediaType();
                try {
                    Transformer t = Utilities.addTransformer(mediaType,
                            dbProperties.getProperty(name+".styleSheet"),
                            dbHome, null, null, dbProperties, dbPropertiesFileName);
                    if(t!=null) {
                        transformers.put(mediaType, t);
                    }
                    while(st.hasMoreTokens())
                        vs.addAliasMediaType(st.nextToken());
                } catch (FileNotFoundException e) {
                    log.error("Unable to add stylesheet "+dbProperties.getProperty(name+".styleSheet")+", stylesheet ignored");
                    log.error(e, e);
                } catch (TransformerConfigurationException e) {
                    log.error("Unable to add stylesheet "+dbProperties.getProperty(name+".styleSheet")+", stylesheet ignored");
                    log.error(e, e);
                } catch (UnsupportedEncodingException e) {
                    log.error("Unable to add stylesheet "+dbProperties.getProperty(name+".styleSheet")+", stylesheet ignored");
                    log.error(e, e);
                }
                // do they want a normalizer applied after the transformation?
                form=dbProperties.getProperty(name+".normalForm");
                if(form!=null) try {
                    normalForm.put(mediaType, Normalizer.Form.valueOf(form));
                }
                catch(IllegalArgumentException e) {
                    log.error("illegal unicode normal form: "+form+", normalizer ignored");
                    log.error(e, e);
                }
                // we should only need this when the transformer
                // can't run from the default schema
                recordSchema=dbProperties.getProperty(name+".recordSchema");
                if(recordSchema!=null && transformers.get(recordSchema)==null) {
                    recordSchemas.put(mediaType, recordSchema);
                    if(log.isDebugEnabled())
                        log.debug("mediaType: "+mediaType+
                            " requires recordSchema: "+
                            dbProperties.getProperty(name+".recordSchema"));
                }
                contentLocation=dbProperties.getProperty(name+".ContentLocation");
                if(contentLocation!=null) {
                    contentLocations.put(mediaType, contentLocation);
                    if(log.isDebugEnabled())
                        log.debug("mediaType: "+mediaType+
                            " ContentLocation: "+contentLocation);
                }
            }
        }

        String patternStr=dbProperties.getProperty("extractRecordIdFromUriPattern");
        if(patternStr!=null)
            extractRecordIdFromUriPatternMatcher=Pattern.compile(patternStr).matcher("");
        anonymousUpdates=Boolean.valueOf(dbProperties.getProperty("allowAnonymousUpdates"));
        log.debug("Exit: private initDB");
    }

    /**
     * @return the anonymousUpdates
     */
    public boolean isAnonymousUpdates() {
        return anonymousUpdates;
    }

    public void makeElem(String recStr, RecordType rt, String schemaID, String schemaName, String recordPacking, DocumentBuilder db, Element[] elems) throws IOException, SAXException {
        if (recordPacking.equals("xml")) {
            Document domDoc;
            try {
                domDoc = db.parse(new InputSource(new StringReader(recStr)));
            }
            catch(SAXParseException e) {
                log.error("bad XML!");
                log.error(recStr);
                throw e;
            }
            Element el = domDoc.getDocumentElement();
            log.debug("got the DocumentElement");
            elems[0] = new MessageElement(el);
            log.debug("put the domDoc into elems[0]");
//            if(log.isDebugEnabled())
//                log.debug("elems[0]\n"+elems[0].toString());
        }
        else { // string
            Text t = new Text(recStr);
            elems[0] = new MessageElement(t);
        }
        if(schemaID!=null)
            rt.setRecordSchema(schemaID);
        else
            rt.setRecordSchema(schemaName);
    }


    public void makeExplainRecord(HttpServletRequest request) {
//        log.error("makeExplainRecord being called from:");
//        Thread.dumpStack();
        log.debug("Making an explain record for database "+dbname);
        StringBuilder sb=new StringBuilder(), urlStr=new StringBuilder();
        sb.append("      <explain authoritative=\"true\" xmlns=\"http://explain.z3950.org/dtd/2.0/\">\n");
        sb.append("        <serverInfo protocol=\"SRW/U\">\n");
        if(request!=null) {
            sb.append("          <host>").append(request.getServerName()).append("</host>\n");
            urlStr.append("http://").append(request.getServerName());
            sb.append("          <port>").append(request.getServerPort()).append("</port>\n");
            if(request.getServerPort()!=80)
                urlStr.append(":").append(request.getServerPort());
            long lastUpdated=getLastUpdated();
            if(lastUpdated==0)
                sb.append("          <database>");
            else {
                String iso8601date=ISO8601FORMAT.format(new Date(lastUpdated));
                String rfc3339date=iso8601date.substring(0, iso8601date.length()-2)+":00";
                sb.append("          <database lastUpdate=\"")
                  .append(rfc3339date)
                  .append("\" numRecs=\"")
                  .append(getNumberOfDatabaseRecords())
                  .append("\">");
            }

            urlStr.append('/');
            String contextPath=request.getContextPath();
            if(contextPath!=null && contextPath.length()>1) {
                sb.append(contextPath.substring(1));
                urlStr.append(contextPath.substring(1));
            }
            sb.append(request.getServletPath());
            urlStr.append(request.getServletPath());
            String pathInfo=request.getPathInfo();
            if(pathInfo!=null) {
                sb.append(request.getPathInfo());
                urlStr.append(request.getPathInfo());
            }
            sb.append("</database>\n");
            baseURL=urlStr.toString();
            log.debug("baseURL="+baseURL);
        }
        sb.append("          </serverInfo>\n");
        sb.append(getDatabaseInfo());
        sb.append(getMetaInfo());
        sb.append(getIndexInfo());
        sb.append(getSchemaInfo());
        sb.append(getConfigInfo());
        sb.append("        </explain>\n");
        setExplainRecord(sb.toString());
    }


    private static ExtraDataType makeExtraDataType(String extraData) {
        ExtraDataType edt = null;
        // extraData is always encoded as "xml"
        Document domDoc;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            StringReader sr = new StringReader("<bogus>"+extraData+"</bogus>");
            domDoc = db.parse(new InputSource(sr));
            sr.close();
            Element el = domDoc.getDocumentElement();
            NodeList nodes=el.getChildNodes();
            MessageElement elems[] = new MessageElement[nodes.getLength()];
            for(int i=0; i<elems.length; i++)
                elems[i]=new MessageElement((Element)nodes.item(i));
            edt = new ExtraDataType();
            edt.set_any(elems);
        } catch (IOException e) {
            log.error(e, e);
        } catch (ParserConfigurationException e) {
            log.error(e, e);
        } catch (SAXException e) {
            log.error(e, e);
            log.error("Bad ExtraResponseData:\n" + Utilities.byteArrayToString(
                extraData.getBytes(Charset.forName("UTF-8"))));
        }
        return edt;
    }

    public static ExtraDataType makeExtraRequestDataType(String extraData) {
        return makeExtraDataType(extraData);
    }

    protected String makeResultSetID() {
        int          i, j;
        StringBuilder sb=new StringBuilder();
        for(i=0; i<6; i++) {
            j=rand.nextInt(35);
            if(j<26)
                sb.append((char)('a'+j));
            else
                sb.append((char)('0'+j-26));
        }
        return sb.toString();
    }

    /**
     * Look for indexes with context sets and construct a new index entry
     * without the context set.  If the new index is unique, keep it.
     * @param props
     */
    static protected void makeUnqualifiedIndexes(Properties props) {
        Enumeration<Object> propertyNames = props.keys();
        HashMap<String, String> newIndexes=new HashMap<String, String>();
        int         start;
        String      name, newName, value;
        while(propertyNames.hasMoreElements()) {
            name=(String) propertyNames.nextElement();
            if(name.startsWith("qualifier.")) {
                if((start=name.indexOf('.', 11))>0) {
                    newName="hiddenQualifier."+name.substring(start+1);
                    log.debug("checking for "+newName);
                    if(newIndexes.get(newName)!=null) { // already got one
                        log.debug("dropping "+newName);
                        newIndexes.remove(newName); // so throw it away
                    }
                    else {
                        log.debug("keeping "+newName);
                        newIndexes.put(newName, (String)props.get(name));
                    }
                }
            }
        }
        Iterator<String> iter = newIndexes.keySet().iterator();
        while(iter.hasNext()) {
            name=iter.next();
            value=newIndexes.get(name);
            if(value!=null) {
                log.debug("adding: "+name+"="+value);
                props.put(name, value);
            }
        }
    }

    public static DiagnosticsType newDiagnostic(final int code,
      final String details, final DiagnosticsType diagnostics) {
        DiagnosticType  diags[];
        DiagnosticsType newDiagnostics=diagnostics;
        int numExistingDiagnostics=0;
        if(diagnostics!=null) {
            diags=diagnostics.getDiagnostic();
            numExistingDiagnostics=diags.length;
            DiagnosticType[] newDiags=
                new DiagnosticType[numExistingDiagnostics+1];
            System.arraycopy(diags, 0, newDiags, 0, numExistingDiagnostics);
            diags=newDiags;
            diagnostics.setDiagnostic(diags);
        }
        else {
            diags=new DiagnosticType[1];
            newDiagnostics=new DiagnosticsType();
            newDiagnostics.setDiagnostic(diags);
        }
        diags[numExistingDiagnostics]=SRWDiagnostic.newDiagnosticType(code, details);
        return newDiagnostics;
    }


    public static HashMap<String, String> parseElements(ExtraDataType extraData) {
        HashMap<String, String> extraDataTable = new HashMap<String, String>();
        if (extraData!=null) {
            MessageElement[] elems = extraData.get_any();
            NameValuePair    nvp;
            String extraRequestData = elems[0].toString();
            ElementParser ep = new ElementParser(extraRequestData);
            log.debug("extraRequestData="+extraRequestData);
            while (ep.hasMoreElements()) {
                nvp = ep.nextElement();
                extraDataTable.put(nvp.getName(), nvp.getValue());
                log.debug(nvp);
            }
        }
        return extraDataTable;
    }

    
    public static void putDb(String dbname, SRWDatabase db) {
//        new Exception("putDB called").printStackTrace();
        db.response=null;
        if(db.useCount!=1) {
            // we'll drop this on the floor and live with the leak rather than
            // have the possibility of multiple threads using the same database
            Exception ex = new Exception("returning a database with a useCount other than 1!!  db=" + db);
            log.error(ex, ex);
            return;
        }
        db.useCount--;
        db.checkinTime=System.currentTimeMillis();
        LinkedList<SRWDatabase> queue=dbs.get(dbname);
        log.debug("about to synchronize #3 on queue");
        synchronized(queue) {
            queue.add(db);
            if(log.isDebugEnabled())
                log.debug("returning "+dbname+" database to the queue; "+queue.size()+" available");
        }
        log.debug("done synchronize #3 on queue");
    }

    public boolean replace(String recordID, byte[] record, RecordMetadata metadata) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    static public void resetTimer(String resultSetID) {
        QueryResult qr=oldResultSets.get(resultSetID);
        timers.put(resultSetID, System.currentTimeMillis() + (qr.getResultSetIdleTime()*1000));
    }

    public void setDefaultResultSetTTL(int defaultResultSetTTL) {
        this.defaultResultSetTTL=defaultResultSetTTL;
    }


    public void setExplainRecord(String explainRecord) {
        this.explainRecord=explainRecord;
    }


    static public void setExtraRecordData(RecordType rt, String extraData) {
        ExtraDataType edt=rt.getExtraRecordData();
        StringBuilder extraResponseData = new StringBuilder("<extraData xmlns=\"http://oclc.org/srw/extraData\">");
        if(edt!=null) {
            MessageElement[] elems = edt.get_any();
            String currentExtraData=elems[0].toString();
            int end=currentExtraData.lastIndexOf('<'), start=currentExtraData.indexOf('<', 1);
            extraResponseData.append(currentExtraData.substring(start, end-1));
        }
        extraResponseData.append(extraData).append("</extraData>");
        rt.setExtraRecordData(makeExtraDataType(extraResponseData.toString()));
    }


    static public void setExtraResponseData(ScanResponseType response, String extraData) {
        ExtraDataType edt=response.getExtraResponseData();
        StringBuilder extraResponseData = new StringBuilder("<extraData xmlns=\"http://oclc.org/srw/extraData\">");
        if(edt!=null) {
            MessageElement[] elems = edt.get_any();
            String currentExtraData=elems[0].toString();
            int end=currentExtraData.lastIndexOf('<'), start=currentExtraData.indexOf('<', 1);
            extraResponseData.append(currentExtraData.substring(start, end-1));
        }
        extraResponseData.append(extraData).append("</extraData>");
        response.setExtraResponseData(makeExtraDataType(extraResponseData.toString()));
    }


    static public void setExtraResponseData(SearchRetrieveResponseType response, String extraData) {
        ExtraDataType edt=response.getExtraResponseData();
        StringBuilder extraResponseData = new StringBuilder("<extraData xmlns=\"http://oclc.org/srw/extraData\">");
        if(edt!=null) {
            MessageElement[] elems = edt.get_any();
            String currentExtraData=elems[0].toString();
            int end=currentExtraData.lastIndexOf('<'), start=currentExtraData.indexOf('<', 1);
            extraResponseData.append(currentExtraData.substring(start, end));
        }
        extraResponseData.append(extraData).append("</extraData>");
        response.setExtraResponseData(makeExtraDataType(extraResponseData.toString()));
    }


    public void setMaximumRecords(int maximumRecords) {
        this.maximumRecords=maximumRecords;
    }


    public void setReturnResultSetId(boolean value) {
        returnResultSetId=value;
    }


    public void setNumberOfRecords(int numberOfRecords) {
        defaultNumRecs=numberOfRecords;
    }
    

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append("Database ").append(dbname).append(" of type ")
          .append(this.getClass().getName()).append(", useCount=").append(useCount);
        return sb.toString();
    }


    public Record transform(Record rec, String schemaID) throws SRWDiagnostic {
        if(log.isDebugEnabled())
            log.debug("schemaID="+schemaID+", rec.getRecordSchemaID"+rec.getRecordSchemaID());
        if (schemaID!=null && !rec.getRecordSchemaID().equals(schemaID)) {
            if(log.isDebugEnabled())
                log.debug("transforming to "+schemaID);
            // They must have specified a transformer
            Transformer t = transformers.get(schemaID);
            if (t==null) {
                log.error("can't transform record in schema "+rec.getRecordSchemaID());
                log.error("record: "+rec);
                log.error("record not available in schema "+schemaID);
                log.error("available schemas are:");
                for(String key:transformers.keySet())
                    log.error("    " + key);
                throw new SRWDiagnostic(SRWDiagnostic.RecordNotAvailableInThisSchema, schemaID);
            }
            String recStr = Utilities.hex07Encode(rec.getRecord());
            StringWriter toRec = new StringWriter();
            StreamSource fromRec = new StreamSource(new StringReader(recStr));
            try {
                t.transform(fromRec, new StreamResult(toRec));
                t.reset();
            } catch (TransformerException e) {
                log.error(e, e);
                t.reset();
                throw new SRWDiagnostic(SRWDiagnostic.RecordNotAvailableInThisSchema, schemaID);
            }
            recStr=toRec.toString();
            return new Record(recStr, schemaID);
        }
        return rec;
    }


    public void useConfigInfo(String configInfo) {
        if(log.isDebugEnabled()) log.debug("configInfo="+configInfo);
        ElementParser ep = new ElementParser(configInfo);
        NameValuePair nvp,  configInfoPair = ep.nextElement();
        String attribute,  attributes,  type;
        StringTokenizer st;
        ep = new ElementParser(configInfoPair.getValue());
        while (ep.hasMoreElements()) {
            nvp = ep.nextElement();
            if (nvp.getName().equals("default")) {
                attributes=ep.getAttributes();
                st = new StringTokenizer(attributes, " =\"");
                type=null;
                while(st.hasMoreTokens()) {
                    attribute=st.nextToken();
                    if(attribute.equals("type")) {
                        type=st.nextToken();
                    }
                }
                if (type!=null) {
                    if (type.equals("retrieveSchema"))
                        schemas.put("default", nvp.getValue());
                    else
                        if (type.equals("maximumRecords"))
                            maximumRecords = Integer.parseInt(nvp.getValue());
                        else
                            if (type.equals("numberOfRecords"))
                                defaultNumRecs = Integer.parseInt(nvp.getValue());
                }
            }
        }
    }


    public void useSchemaInfo(String schemaInfo) {
        ElementParser ep = new ElementParser(schemaInfo);
        NameValuePair nvp,  schemaInfoPair = ep.nextElement();
        String attribute,  attributes,  schemaID,  schemaName;
        StringTokenizer st;
        ep = new ElementParser(schemaInfoPair.getValue());
        while (ep.hasMoreElements()) {
            nvp = ep.nextElement();
            if (nvp.getName().equals("schema")) {
                attributes=ep.getAttributes();
                log.debug("in useSchemaInfo: attributes="+attributes);
                st = new StringTokenizer(attributes, " =\"");
                schemaID=schemaName=null;
                while(st.hasMoreTokens()) {
                    attribute=st.nextToken();
                    if(attribute.equals("name")) {
                        schemaName=st.nextToken();
                    }
                    else if(attribute.equals("identifier")) {
                        schemaID=st.nextToken();
                    }
                }
                if(schemaID!=null && schemaName!=null) {
                    schemas.put(schemaName, schemaID);
                    log.info("adding schema: "+schemaName);
                    schemas.put(schemaID, schemaID);
                    log.info("with schemaID: "+schemaID);
                }
            }
        }
    }
}
