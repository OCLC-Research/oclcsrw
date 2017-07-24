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
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;
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
    private static final Log LOG=LogFactory.getLog(SRWDatabase.class);

    public static final String DEFAULT_SCHEMA = "default";
    public static final int DEFAULT_RESULT_SET_TTL=0;
    // don't create a result set unless the client asks for it
    public String defaultRecordPacking="xml";

    public abstract String      getExtraResponseData(QueryResult result,
                                  SearchRetrieveRequestType request);

    public abstract String      getIndexInfo();

    public abstract QueryResult getQueryResult(String query,
                                  SearchRetrieveRequestType request)
                                  throws InstantiationException;

    public abstract TermList    getTermList(CQLTermNode term, int position,
                                  int maxTerms, ScanRequestType request);

    public abstract void        init(String dbname, String srwHome,
                                  String dbHome, String dbPropertiesFileName,
                                  Properties dbProperties,
                                  HttpServletRequest request) throws Exception;

    public abstract boolean     supportsSort();


    public static ArrayList<SRWDatabase> allDbs=new ArrayList<>();
    public static HashSet<String> badDbs=new HashSet<>();
    public static HashSet<String> goodDbs=new HashSet<>();
    public static HashMap<String, Integer> dbCount=new HashMap<>();
    public static HashMap<String, LinkedList<SRWDatabase>> dbs=new HashMap<>();
    public final static HashMap<String, QueryResult> OLD_RESULT_SETS;
    public final static HashMap<String, Long> TIMERS;

    public static Properties srwProperties;
    public static String servletContext, srwHome;
    public final static Timer TIMER;

    static {
        TIMER=new Timer();
        TIMERS=new HashMap<>();
        OLD_RESULT_SETS=new HashMap<>();
        TIMER.schedule(new HouseKeeping(TIMERS, OLD_RESULT_SETS), 60000L, 60000L);
    }
    private boolean anonymousUpdates=false;
    private boolean returnResultSetId=true;
    private Matcher extractRecordIdFromUriPatternMatcher;
    private final Random rand=new Random();
    private final SimpleDateFormat ISO8601FORMAT=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public ArrayList<String> additionalRecordPackings=new ArrayList<>();
    public boolean reportedAWOL=false;
    public ContentTypeNegotiator conneg=null;
    public HashMap<String, Integer> schemaMaximumRecords=new HashMap<>();
    public HashMap<String, String> contentLocations=new HashMap<>();
    public HashMap<String, String> mediaTypes=new HashMap<>();
    public HashMap<String, String> recordPacking=new HashMap<>();
    public HashMap<String, String> recordSchemas=new HashMap<>();
    public HashMap<String, Templates> templates=new HashMap<>();
    public HashMap<String, Transformer> transformers=new HashMap<>();
    public HashMap<String, Normalizer.Form> normalForm=new HashMap<>();
    public HttpHeaderSetter httpHeaderSetter=null;
    public long checkinTime=0, checkoutTime=0;
    public SearchRetrieveRequestType searchRequest;
    public SearchRetrieveResponseType response;
    public String  appStyleSheet=null, baseURL=null, checkoutReason=null,
                   databaseTitle, dbname, explainStyleSheet=null,
                   scanStyleSheet=null,
                   searchStyleSheet=null, singleRecordStyleSheet=null,
                   multipleRecordsStyleSheet=null, nativeSchema,
                   noRecordsStyleSheet=null;

    protected boolean     letDefaultsBeDefault=false;
    protected CQLParser   parser = new CQLParser(CQLParser.V1POINT1);
    protected DocumentBuilder docb = null;
    protected HashMap<String, String>
                          nameSpaces=new HashMap<>(),
                          schemas=new HashMap<>();
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
        sb.append("          <supports type=\"").append(st.nextToken()).append("\">");
        sb.append(s.substring(s.indexOf("=")+1).trim()).append("</supports>\n");
    }
    
    public void close() {
        TIMER.cancel();
    }

    public void addRenderer(String schemaName, String schemaID, Properties props)
      throws InstantiationException {
    }

    public static String cleanConvertedJSONtoXML(String dirtyRecord) {
        // strip xml header
        int i=dirtyRecord.indexOf("?>");
        if(i>0) {
            while(Character.isWhitespace(dirtyRecord.charAt(i+2)))
                i++;
            dirtyRecord=dirtyRecord.substring(i+2);
        }
        LOG.debug("after stripping: "+dirtyRecord);
        
        // get rid of the <o></o> wrapper
        if(dirtyRecord.startsWith("<o>")) {
            dirtyRecord=dirtyRecord.substring(3, dirtyRecord.length()-6);
            LOG.debug("after removing <o> wrapper: "+dirtyRecord);
        }
        
        // get rid of <content></content> wrappers
        while((i=dirtyRecord.indexOf("<__content>"))>0)
            dirtyRecord=new StringBuilder(dirtyRecord.substring(0, i)).append(dirtyRecord.substring(i+"<__content>".length())).toString();
        while((i=dirtyRecord.indexOf("</__content>"))>0)
            dirtyRecord=new StringBuilder(dirtyRecord.substring(0, i)).append(dirtyRecord.substring(i+"</__content>".length())).toString();
        LOG.debug("after removing <__content> wrappers: "+dirtyRecord);

        // get rid of the repeating <e> elements and make the container element repeat instead
        dirtyRecord=revertJSONEntries(dirtyRecord);
        
        // move attributes into their containing elements
        int ce;
        String attrElement;
        while((i=dirtyRecord.indexOf("<__attributes>"))>0) {
            ce=findContainingElement(dirtyRecord, i);
            attrElement=extractElement(dirtyRecord, i, "__attributes");
            dirtyRecord=removeElement(dirtyRecord, i, attrElement);
            dirtyRecord=insertAttributeIntoElement(dirtyRecord, ce, attrElement);
        }
        LOG.debug("after relocating: "+dirtyRecord);

        return dirtyRecord;
    }

    public static synchronized void createDB(final String dbname, Properties srwServerProperties)
      throws InstantiationException {
        createDB(dbname, srwServerProperties, null, null);
    }

    public static synchronized void createDB(final String dbname, Properties srwServerProperties, String context, HttpServletRequest request)
      throws InstantiationException {
        LOG.debug("Enter: initDB, dbname="+dbname);
        if(srwServerProperties==null)
            throw new InstantiationException("properties==null");
        LOG.info("creating instance "+dbCount.get(dbname)+" of database "+dbname);
        String dbn="db."+dbname;

        srwProperties=srwServerProperties;
        srwHome=srwServerProperties.getProperty("SRW.Home");
        servletContext=context;
        if(srwHome!=null && !srwHome.endsWith("/"))
            srwHome=srwHome+"/";
        LOG.debug("SRW.Home="+srwHome);
        Properties dbProperties=new Properties();
        String dbHome=srwServerProperties.getProperty(dbn+".home"),
               dbPropertiesFileName;
        if(dbHome!=null) {
            if(!dbHome.endsWith("/"))
                dbHome=dbHome+"/";
            LOG.debug("dbHome="+dbHome);
        }

        String className=srwServerProperties.getProperty(dbn+".class");
        LOG.debug("className="+className);
        if(className==null) {
            // let's see if there's a fallback database to use
            className=srwServerProperties.getProperty("db.default.class");
            if(className==null)
                throw new InstantiationException("No "+
                    dbn+".class entry in properties file");
            dbn="db.default";
        }
        switch (className) {
            case "ORG.oclc.os.SRW.SRWPearsDatabase":
                LOG.info("** Warning ** the class ORG.oclc.os.SRW.SRWPearsDatabase has been replaced with ORG.oclc.os.SRW.Pears.SRWPearsDatabase");
                LOG.info("              Please correct the server's properties file");
                className="ORG.oclc.os.SRW.Pears.SRWPearsDatabase";
                LOG.debug("new className="+className);
                break;
            case "ORG.oclc.os.SRW.SRWRemoteDatabase":
                LOG.info("** Warning ** the class ORG.oclc.os.SRW.SRWRemoteDatabase has been replaced with ORG.oclc.os.SRW.ParallelSearching.SRWRemoteDatabase");
                LOG.info("              Please correct the server's properties file");
                className="ORG.oclc.os.SRW.ParallelSearching.SRWRemoteDatabase";
                LOG.debug("new className="+className);
                break;
            case "ORG.oclc.os.SRW.Pears.SRWRemoteDatabase":
                LOG.info("** Warning ** the class ORG.oclc.os.SRW.Pears.SRWRemoteDatabase has been replaced with ORG.oclc.os.SRW.ParallelSearching.SRWRemoteDatabase");
                LOG.info("              Please correct the server's properties file");
                className="ORG.oclc.os.SRW.ParallelSearching.SRWRemoteDatabase";
                LOG.debug("new className="+className);
                break;
            case "ORG.oclc.os.SRW.SRWMergeDatabase":
                LOG.info("** Warning ** the class ORG.oclc.os.SRW.SRWMergeDatabase has been replaced with ORG.oclc.os.SRW.ParallelSearching.SRWMergeDatabase");
                LOG.info("              Please correct the server's properties file");
                className="ORG.oclc.os.SRW.ParallelSearching.SRWMergeDatabase";
                LOG.debug("new className="+className);
                break;
            case "ORG.oclc.os.SRW.Pears.SRWMergeDatabase":
                LOG.info("** Warning ** the class ORG.oclc.os.SRW.Pears.SRWMergeDatabase has been replaced with ORG.oclc.os.SRW.ParallelSearching.SRWMergeDatabase");
                LOG.info("              Please correct the server's properties file");
                className="ORG.oclc.os.SRW.ParallelSearching.SRWMergeDatabase";
                LOG.debug("new className="+className);
                break;
            case "ORG.oclc.os.SRW.SRWDLuceneDatabase":
                LOG.info("** Warning ** the class ORG.oclc.os.SRW.SRWLuceneDatabase has been replaced with ORG.oclc.os.SRW.DSpaceLucene.SRWLuceneDatabase");
                LOG.info("              Please correct the server's properties file");
                className="ORG.oclc.os.SRW.DSpaceLucene.SRWLuceneDatabase";
                LOG.debug("new className="+className);
                break;
            default:
                break;
        }
        SRWDatabase db;
        try {
            LOG.debug("creating class "+className);
            Class<? extends SRWDatabase>  dbClass = Class.forName(className).asSubclass(SRWDatabase.class);
            LOG.debug("creating instance of class "+dbClass);
            db=dbClass.newInstance();
            LOG.debug("class created");
        }
        catch(ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            LOG.error("Unable to create Database class "+className+
                " for database "+dbname);
            LOG.error(e, e);
            throw new InstantiationException(e.getMessage());
        }

        dbPropertiesFileName=srwServerProperties.getProperty(dbn+".configuration");
        if(db.hasaConfigurationFile() || dbPropertiesFileName!=null) {
            if(dbPropertiesFileName==null) {
                throw new InstantiationException("No "+dbn+
                    ".configuration entry in properties file");
            }

            try {
                LOG.debug("Reading database configuration file: "+
                    dbPropertiesFileName);
                try (InputStream is = Utilities.openInputStream(dbPropertiesFileName, dbHome, srwHome)) {
                    dbProperties.load(is);
                }
            }
            catch(java.io.FileNotFoundException e) {
                LOG.error("Unable to open database configuration file!");
                LOG.error(e);
            }
            catch(IOException e) {
                LOG.error("Unable to load database configuration file!");
                LOG.error(e, e);
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
                LOG.error("Unable to initialize database "+dbname);
                LOG.error(e, e);
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
                    LOG.error("bad value for maximumRecords: \""+temp+"\"");
                    LOG.error("maximumRecords parameter ignored");
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
                    LOG.error("bad value for numberOfRecords: \""+temp+"\"");
                    LOG.error("numberOfRecords parameter ignored");
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
                    LOG.error("bad value for defaultResultSetTTL: \""+temp+"\"");
                    LOG.error("defaultResultSetTTL parameter ignored");
                    db.setDefaultResultSetTTL(DEFAULT_RESULT_SET_TTL);
                }
            }
            else
                db.setDefaultResultSetTTL(DEFAULT_RESULT_SET_TTL);

            temp=dbProperties.getProperty("letDefaultsBeDefault");
            if(temp!=null && temp.equals("true"))
                db.letDefaultsBeDefault=true;
        }
        else { // default settings
            try {
                db.init(dbname, srwHome, dbHome, "(no database configuration file specified)", dbProperties, request);
            }
            catch(Exception e) {
                LOG.error("Unable to create Database class "+className+
                    " for database "+dbname);
                LOG.error(e, e);
                throw new InstantiationException(e.getMessage());
            }
            db.setDefaultResultSetTTL(DEFAULT_RESULT_SET_TTL);
            LOG.info("no configuration file needed or specified");
        }

        if(!(db instanceof SRWDatabasePool)) {
            LinkedList<SRWDatabase> queue=dbs.get(dbname);
            if(queue==null)
                queue=new LinkedList<>();
            queue.add(db);
            allDbs.add(db);
            if(LOG.isDebugEnabled())
                LOG.debug(dbname+" has "+queue.size()+" copies");
            dbs.put(dbname, queue);
        }
        if(dbCount.containsKey(dbname))
            dbCount.put(dbname, dbCount.get(dbname)+1);
        else
            dbCount.put(dbname, 1);
        LOG.debug("Exit: initDB");
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
            LOG.info("scanTerm:\n" + Utilities.byteArrayToString(scanTerm.getBytes(StandardCharsets.UTF_8)));
        LOG.info("maxTerms="+max+", position="+pos);
        try {
            root = Utilities.getFirstTerm(parser.parse(scanTerm));
        } catch (CQLParseException | IOException e) {
            LOG.error(e);
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

        LOG.info("scan "+scanTerm+": (" + (System.currentTimeMillis() - startTime) + "ms)");
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

        if(LOG.isDebugEnabled())
            LOG.debug("query:\n" + Utilities.byteArrayToString(query.getBytes(StandardCharsets.UTF_8)));

        String resultSetID = getResultSetId(query);
        if (resultSetID!=null) { // got a cached result
            LOG.info("resultSetID="+resultSetID);
            result = OLD_RESULT_SETS.get(resultSetID);
            if (result==null)
                return diagnostic(SRWDiagnostic.ResultSetDoesNotExist,
                        resultSetID, response);
            cachedResultSet=true;
        }
        else { // Evaluate the query.
            try {
                result = getQueryResult(query, request);
            } catch (InstantiationException e) {
                LOG.error("Exception "+e.getMessage()+" caught while doing query:");
                LOG.error(Utilities.byteArrayToString(query.getBytes(StandardCharsets.UTF_8)));
                LOG.error("request: "+request);
                LOG.error(e, e);
                return diagnostic(SRWDiagnostic.GeneralSystemError,
                        e.getMessage(), response);
            }
        }

        long postingsCount = result.getNumberOfRecords();
        LOG.info("'" + query + "'==> " + postingsCount);
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
                LOG.debug("keeping resultSet '"+resultSetID+"' for "+resultSetTTL+
                    " seconds");
                OLD_RESULT_SETS.put(resultSetID, result);
                resetTimer(resultSetID);
                response.setResultSetId(resultSetID);
                response.setResultSetIdleTime(
                    new PositiveInteger(Integer.toString(resultSetTTL)));
                cachedResultSet=true;
            }

            int numRecs = defaultNumRecs;
            String schemaName = request.getRecordSchema();
            LOG.debug("request.getRecordSchema()="+schemaName);
            if(schemaName==null)
                schemaName="default";
            LOG.debug("schemaName="+schemaName);
            String schemaID=null;
//            if(!letDefaultsBeDefault && !schemaName.equals("default")) {
            if(!letDefaultsBeDefault) {
                schemaID = getSchemaID(schemaName);
                if(schemaID==null) {
                    LOG.debug("unknown schema: "+schemaName);
                    diagnostic(SRWDiagnostic.UnknownSchemaForRetrieval, schemaName, response);
                    numRecs=0;
                }
            }
            
            NonNegativeInteger maxRecs = request.getMaximumRecords();
            if (maxRecs!=null)
                numRecs = (int) Math.min(maxRecs.longValue(), maximumRecords);
            Integer schemaNumRecs=schemaMaximumRecords.get(schemaID);
            LOG.info("schemaID="+schemaID+", schemaNumRecs="+schemaNumRecs+", numRecs="+numRecs);
            if(schemaNumRecs!=null && schemaNumRecs<numRecs)
                numRecs=schemaNumRecs;
            LOG.info("numRecs="+numRecs);

            long startPoint = 1;
            PositiveInteger startRec = request.getStartRecord();
            if(startRec!=null)
                startPoint=startRec.longValue();
            if (startPoint>postingsCount)
                diagnostic(SRWDiagnostic.FirstRecordPositionOutOfRange,
                        null, response);

            if ((startPoint-1+numRecs)>postingsCount)
                numRecs = (int) (postingsCount-(startPoint-1));

            String packing = request.getRecordPacking();
            if(packing==null) {
                if(msgContext!=null && msgContext.getProperty("sru")!=null) {
                    packing=defaultRecordPacking; // default for sru
                }
                else {
                    if(defaultRecordPacking.equals("xml"))
                        packing="string"; // default for srw
                    else
                        packing=defaultRecordPacking;
                }
            }
            if (!packing.equals("xml") &&
              !packing.equals("string") && !additionalRecordPackings.contains(packing)) {
                diagnostic(SRWDiagnostic.UnsupportedRecordPacking, packing, response);
                numRecs=0;
            }

            if (numRecs==0)
                response.setNextRecordPosition(new PositiveInteger("1"));
            else
                if (numRecs>0) { // render some records into SGML
                    String sortKeys = request.getSortKeys();
                    LOG.debug("schemaName="+schemaName+", schemaID="+schemaID+
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
                            LOG.debug("reusing old sorted resultSet");
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
                        LOG.debug("making RecordIterator, startPoint="+startPoint+", schemaID="+schemaID);
                        list=result.recordIterator(startPoint, numRecs, schemaID, request.getExtraRequestData());
                        if(list==null)
                            throw new InstantiationException();
                    } catch (InstantiationException e) {
                        LOG.error(e, e);
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
                    LOG.debug("trying to get "+numRecs+
                        " records starting with record "+startPoint+
                        " from a set of "+postingsCount+" records");
                    for (i=0; i<numRecs && list.hasNext(); i++) {
                        rt = new RecordType();
                        rt.setRecordPacking(packing);
                        frag = new StringOrXmlFragment();
                        elems = new MessageElement[1];
                        frag.set_any(elems);
                        try {
                            rec=list.nextRecord();
                            if(LOG.isDebugEnabled())
                                LOG.debug("rec="+rec);
                            recStr=transform(rec, schemaID, packing).getRecord();
                            if (LOG.isDebugEnabled())
                                LOG.debug("Transformed Record:\n" + Utilities.byteArrayToString(
                                    recStr.getBytes(StandardCharsets.UTF_8)));
                            makeElem(recStr, rt, schemaID, schemaName, packing, docb, elems);
                            if(rec.hasExtraRecordInfo())
                                setExtraRecordData(rt, rec.getExtraRecordInfo());
                        } catch (IOException e) {
                            LOG.error("error getting document "+(i+startPoint)+", postings="+postingsCount);
                            LOG.error(e, e);
                            try {
                                makeElem(SRWDiagnostic.newSurrogateDiagnostic(
                                    "info:srw/diagnostic/1/",
                                    SRWDiagnostic.RecordTemporarilyUnavailable,
                                    null), rt, null,
                                    "info:srw/schema/1/diagnostics-v1.1",
                                    packing, docb, elems);
                            } catch (IOException | SAXException e2) {
                                LOG.error(e, e);
                                break;
                            }
                            LOG.error("error getting document "+(i+startPoint)+", postings="+postingsCount);
                            LOG.error(e, e);
                        } catch (NoSuchElementException e) {
                            LOG.error("error getting document "+(i+startPoint)+", postings="+postingsCount);                            LOG.error(e, e);
                            try {
                                makeElem(SRWDiagnostic.newSurrogateDiagnostic(
                                    "info:srw/diagnostic/1/",
                                    SRWDiagnostic.GeneralSystemError,
                                    null), rt, null,
                                    "info:srw/schema/1/diagnostics-v1.1",
                                    packing, docb, elems);
                            } catch (IOException | SAXException e2) {
                                LOG.error(e2, e2);
                            }
                            break;
                        } catch (SAXException e) {
                            try {
                                LOG.error(e, e);
                                makeElem(SRWDiagnostic.newSurrogateDiagnostic(
                                    "info:srw/diagnostic/1/",
                                    SRWDiagnostic.RecordTemporarilyUnavailable,
                                    null), rt, null,
                                    "info:srw/schema/1/diagnostics-v1.1",
                                    packing, docb, elems);
                            } catch (IOException | SAXException e2) {
                                LOG.error(e2, e2);
                                break;
                            }
                            LOG.error("error getting document "+(i+startPoint)+", postings="+postingsCount);
                            LOG.error(e, e);
                            LOG.error("Bad record:\n" + Utilities.byteArrayToString(
                                    recStr.getBytes(StandardCharsets.UTF_8)));
                        } catch (SRWDiagnostic e) {
                            try {
                                makeElem(SRWDiagnostic.newSurrogateDiagnostic(
                                    "info:srw/diagnostic/1/", e.getCode(),
                                    e.getAddInfo()), rt, null,
                                    "info:srw/schema/1/diagnostics-v1.1",
                                    packing, docb, elems);
                            } catch (IOException | SAXException e2) {
                                LOG.error(e2, e2);
                                break;
                            }
                            LOG.error("error getting document "+(i+startPoint)+", postings="+postingsCount);
                            LOG.error(e, e);
                            LOG.error("Bad record:\n" + Utilities.byteArrayToString(
                                    recStr.getBytes(StandardCharsets.UTF_8)));
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

        LOG.debug("exit doRequest");
        return response;
//        }
//        catch(Exception e) {
//            //log.error(e);
//            log.error(e, e);
//            throw new ServletException(e.getMessage());
//        }
    }

    static public String extractElement(String rec, int offset, String elementName) {
        int end=rec.indexOf("</"+elementName, offset+1);
        int start=rec.indexOf("<"+elementName, offset+1);
        int elCount=0;
//        System.out.println("rec:"+rec);
        while(start>=0 && start<end) {
//            System.out.println("start="+start+", end="+end);
            end=rec.indexOf("</"+elementName, end+1);
            start=rec.indexOf("<"+elementName, start+1);
        }
//        System.out.println("done: start="+start+", end="+end);
        end=rec.indexOf('>', end)+1;
        return rec.substring(offset, end);
    }

    public String extractRecordIdFromUri(String uri) {
        String decodedURI=null;
        try {
            decodedURI = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOG.error("undecodable URI: "+uri, ex);
        }
        LOG.info("uri="+decodedURI);
        extractRecordIdFromUriPatternMatcher.reset(decodedURI);
        if(extractRecordIdFromUriPatternMatcher.find()) {
            LOG.info("groupCount="+extractRecordIdFromUriPatternMatcher.groupCount());
            StringBuilder sb=new StringBuilder();
            for(int i=1; i<=extractRecordIdFromUriPatternMatcher.groupCount(); i++) {
                LOG.info("group("+i+")="+extractRecordIdFromUriPatternMatcher.group(i));
                sb.append(extractRecordIdFromUriPatternMatcher.group(i));
            }
            return sb.toString();
        }
        LOG.info("recordID not found.  Pattern="+extractRecordIdFromUriPatternMatcher.pattern());
        return null;
    }

    public String extractSortField(Object record) {
        return null;
    }

    static public int findContainingElement(String record, int eStart) {
        int skipNum=0;
        for(int i=eStart-1; i>=0; i--) {
            if(record.charAt(i)=='/' && record.charAt(i+1)=='>') {
                skipNum++;
                continue;
            }
            if(record.charAt(i)=='<') {
                if(record.charAt(i+1)=='/') {
                    skipNum++;
                    continue;
                }
                if(skipNum==0)
                    return i;
                skipNum--;
            }
        }
        return Integer.MIN_VALUE; // will only happen with badly formed XML
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
        for(String packing:additionalRecordPackings)
            addSupports("recordPacking="+packing, sb);
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
        LOG.debug("enter SRWDatabase.getDB");
//        new Exception("getDB called").printStackTrace();
        if(badDbs.contains(dbname)) // we've seen this one before
            return null;

        LinkedList<SRWDatabase> queue=dbs.get(dbname);
        SRWDatabase db=null;
        try {
            if(queue==null)
                LOG.info("No SRW databases opened yet for database "+dbname);
            else {
                LOG.debug("about to synchronize #1 on queue");
                synchronized(queue) {
                    if(queue.isEmpty())
                        LOG.info("No SRW databases left in queue for database "+dbname);
                    else {
                        db=queue.removeFirst();
                        if(db==null)
                            LOG.debug("popped a null database off the queue for database "+dbname);
                    }
                }
                LOG.debug("done synchronize #1 on queue");
            }
            if(db==null) {
                if(request!=null)
                    LOG.info("Opening an SRW database for "+dbname+
                        " for request "+request.getContextPath()+"?"+
                        request.getQueryString()+
                        " for IP "+request.getRemoteAddr());
                else {
                    LOG.info("Opening an SRW database for "+dbname);
                    LOG.info("called from ", new Exception());
                }
                try{
                    while(db==null) {
                        createDB(dbname, srwServerProperties, servletContext, request);
                        queue=dbs.get(dbname);
                        LOG.debug("about to synchronize #2 on queue");
                        synchronized(queue) {
                            if(!queue.isEmpty()) // crap, someone got to it before us
                                db=queue.removeFirst();
                        }
                    }
                    LOG.debug("done synchronize #2 on queue");
                }
                catch(InstantiationException e) { // database not available
                    // but, we don't want to mark a database that was good
                    // as bad now because of some transient error
                    if(!goodDbs.contains(dbname)) // we've never had it before
                        badDbs.add(dbname); // mark it as a bad dbname
                    LOG.error(e, e);
                    return null;
                }
            }
        }
        catch(Exception e) {
            LOG.error(e,e);
            LOG.error("shoot!");
            return null;
        }
        if(LOG.isDebugEnabled())
            LOG.debug("getDB: db="+db);
        goodDbs.add(dbname);
        if(db.useCount!=0) {
            // we're trying to check out something that is in use!
            // let's drop this one on the floor, hoping that whoever checked
            // it out will return it eventually, and ask for a new one
            LOG.error("dropping db="+db);
            return getDB(dbname, srwServerProperties, servletContext, request);
        }
        db.useCount++;
        if(request!=null)
            db.checkoutReason=request.getQueryString();
        db.checkoutTime=System.currentTimeMillis();
        LOG.debug("exit SRWDatabase.getDB");
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


    static public String getElementName(String rec, int offset) {
        offset++; // skip past opening '<'
        while(Character.isWhitespace(rec.charAt(offset)))
            offset++;
        String name="";
        char c=rec.charAt(offset);
        while(!Character.isWhitespace(c) && c!='>') {
            name=name+c;
            c=rec.charAt(++offset);
        }
        return name;
    }

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
        ArrayList<String> resultSetIds=new ArrayList<>();
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
                if(OLD_RESULT_SETS.get(resultSetId)==null)
                    throw new SRWDiagnostic(SRWDiagnostic.ResultSetDoesNotExist, resultSetId);
                resultSetIds.add(resultSetId);
                resetTimer(resultSetId);
                LOG.info("added resultSetId "+ctn.getTerm());
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
        LOG.debug("Enter: private initDB, dbname="+dbname);
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
            LOG.error(e, e);
        }

        if(dbProperties==null) {
            LOG.warn("No properties file provided for database "+dbname);
            return;
        }

        String httpHeaderSetterClass=dbProperties.getProperty("HttpHeaderSetter");
        if(httpHeaderSetterClass!=null) {
            try {
                httpHeaderSetter = (HttpHeaderSetter) Class.forName(httpHeaderSetterClass).newInstance();
                httpHeaderSetter.init(dbProperties);
            }
            catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                LOG.error("Unable to create HttpHeaderSetter: "+httpHeaderSetterClass, ex);
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
            String      name, packing, schemaIdentifier, schemaName, transformerName,
                        value;
            ArrayList<String> parms, values;
            st=new StringTokenizer(xmlSchemaList, ", \t");
            LOG.info("xmlSchemaList="+xmlSchemaList);
            while(st.hasMoreTokens()) {
                schemaName=st.nextToken();
                LOG.debug("looking for schema "+schemaName);
                if(firstSchema==null)
                    firstSchema=schemaName;
                schemaIdentifier=dbProperties.getProperty(schemaName+".identifier");
                transformerName=dbProperties.getProperty(schemaName+".transformer");
                if(transformerName==null) {
                    // maybe this is an old .props file and the transformer name
                    // is associated with the bare schemaName
                    transformerName=dbProperties.getProperty(schemaName);
                }
                packing=dbProperties.getProperty(schemaName+".recordPacking");
                if(packing!=null) {
                    recordPacking.put(schemaName, packing);
                    if(schemaIdentifier!=null)
                        recordPacking.put(schemaIdentifier, packing);
                }
                parms=new ArrayList<>();
                values=new ArrayList<>();
                Enumeration<Object> propertyNames = dbProperties.keys();
                while(propertyNames.hasMoreElements()) {
                    name=(String) propertyNames.nextElement();
                    if(name.startsWith(schemaName+".parameter.")) {
                        value=dbProperties.getProperty(name);
                        values.add(value);
                        name=name.substring(schemaName.length()+11);
                        parms.add(name);
                        if(LOG.isDebugEnabled())
                            LOG.debug("transformer parm: "+name+"="+value);
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
                    Object t = Utilities.addTransformer(schemaName,
                            transformerName, dbHome, parms, values,
                            dbProperties, dbPropertiesFileName, schemaName);
                    if(t!=null) {
                        if(t instanceof Templates) {
                            templates.put(schemaName, (Templates)t);
                            if(schemaIdentifier!=null)
                                templates.put(schemaIdentifier, (Templates)t);
                        }
                        else {
                            transformers.put(schemaName, (Transformer)t);
                            if(schemaIdentifier!=null)
                                transformers.put(schemaIdentifier, (Transformer)t);
                        }
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
                catch(FileNotFoundException | TransformerConfigurationException | UnsupportedEncodingException | InstantiationException e) {
                    LOG.error("Unable to load schema "+schemaName);
                    LOG.error(e, e);
                }
            }

            defaultSchema=dbProperties.getProperty("defaultSchema");
            if(defaultSchema==null)
                defaultSchema=firstSchema;
            LOG.info("defaultSchema="+defaultSchema);
            schemaIdentifier=schemas.get(defaultSchema);
            LOG.info("default schemaID="+schemaIdentifier);
            if(schemaIdentifier==null)
                LOG.error("Default schema "+defaultSchema+" not loaded");
            else {
                schemas.put("default", schemaIdentifier);
                defaultSchema=schemaIdentifier;
                Templates temp=templates.get(defaultSchema);
                if(temp!=null)
                    templates.put("default", temp);
                Transformer t=transformers.get(defaultSchema);
                if(t!=null) {
                    transformers.put("default", t);
                }
            }
            
            nativeSchema=dbProperties.getProperty("nativeSchema");
            LOG.debug("nativeSchema="+nativeSchema);
            if(nativeSchema==null)
                nativeSchema=defaultSchema;
            LOG.debug("nativeSchema="+nativeSchema);
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
        LOG.debug("conneg="+conneg);
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
                LOG.info(key+"="+value);
                st=new StringTokenizer(value, ", ");
                mimeType=st.nextToken();
                vs=conneg.addVariant(mimeType);
                mediaType=vs.getMediaType().getMediaType();
                try {
                    Object t = Utilities.addTransformer(mediaType,
                            dbProperties.getProperty(name+".styleSheet"),
                            dbHome, null, null, dbProperties, dbPropertiesFileName, name);
                    if(t!=null) {
                        if(t instanceof Templates)
                            templates.put(mediaType, (Templates)t);
                        else
                            transformers.put(mediaType, (Transformer)t);
                    }
                    while(st.hasMoreTokens())
                        vs.addAliasMediaType(st.nextToken());
                } catch (FileNotFoundException | TransformerConfigurationException | UnsupportedEncodingException e) {
                    LOG.error("Unable to add stylesheet "+dbProperties.getProperty(name+".styleSheet")+", stylesheet ignored");
                    LOG.error(e, e);
                } catch (RuntimeException e) {
                    LOG.error("Unable to add stylesheet "+dbProperties.getProperty(name+".styleSheet")+", stylesheet ignored");
                    LOG.error(e, e);
                    throw e;
                }
                // do they want a normalizer applied after the transformation?
                form=dbProperties.getProperty(name+".normalForm");
                if(form!=null) try {
                    normalForm.put(mediaType, Normalizer.Form.valueOf(form));
                }
                catch(IllegalArgumentException e) {
                    LOG.error("illegal unicode normal form: "+form+", normalizer ignored");
                    LOG.error(e, e);
                }
                // we should only need this when the transformer
                // can't run from the default schema
                recordSchema=dbProperties.getProperty(name+".recordSchema");
                if(recordSchema!=null && transformers.get(recordSchema)==null && templates.get(recordSchema)==null) {
                    recordSchemas.put(mediaType, recordSchema);
                    if(LOG.isDebugEnabled())
                        LOG.debug("mediaType: "+mediaType+
                            " requires recordSchema: "+
                            dbProperties.getProperty(name+".recordSchema"));
                }
                contentLocation=dbProperties.getProperty(name+".ContentLocation");
                if(contentLocation!=null) {
                    contentLocations.put(mediaType, contentLocation);
                    if(LOG.isDebugEnabled())
                        LOG.debug("mediaType: "+mediaType+
                            " ContentLocation: "+contentLocation);
                }
            }
        }

        String patternStr=dbProperties.getProperty("extractRecordIdFromUriPattern");
        if(patternStr!=null)
            extractRecordIdFromUriPatternMatcher=Pattern.compile(patternStr).matcher("");
        anonymousUpdates=Boolean.valueOf(dbProperties.getProperty("allowAnonymousUpdates"));
        additionalRecordPackings.addAll(Arrays.asList(dbProperties.getProperty("additionalRecordPackings", "").split("(,|\\t|\\s)")));
        LOG.debug("Exit: private initDB");
    }

    public static String insertAttributeIntoElement(String rec, int elementOffset, String attributeString) {
//        System.out.println("rec="+rec);
//        System.out.println("elementOffset="+elementOffset);
//        System.out.println("attributeString="+attributeString);
        // find place to start inserting
        char c;
        int i;
        for(i=elementOffset; i<rec.length(); i++) {
            c=rec.charAt(i);
            if(c==' ' || c=='>')
                break;
        }
        Pattern p=Pattern.compile("<([^>]*)>([^<]*)</");
        Matcher m=p.matcher(attributeString);
        StringBuilder newAttributeString=new StringBuilder();
        while(m.find()) {
//            System.out.println(m.group(1)+"='"+m.group(2)+"'");
            newAttributeString.append(' ').append(m.group(1)).append("='").append(m.group(2)).append("'");
        }
        StringBuilder sb=new StringBuilder(rec.substring(0, i));
        sb.append(newAttributeString);
        sb.append(rec.substring(i));
        return sb.toString();
    }

    /**
     * @return the anonymousUpdates
     */
    public boolean isAnonymousUpdates() {
        return anonymousUpdates;
    }

    public void makeElem(String recStr, RecordType rt, String schemaID,
            String schemaName, String recordPacking, DocumentBuilder db,
            Element[] elems) throws IOException, SAXException {
        if(this.recordPacking.get(schemaID)!=null)
            recordPacking=this.recordPacking.get(schemaID);
        if (recordPacking.equals("xml")) {
            Document domDoc;
            try {
                domDoc = db.parse(new InputSource(new StringReader(recStr)));
            }
            catch(SAXParseException e) {
                LOG.error("bad XML!");
                LOG.error(recStr);
                throw e;
            }
            Element el = domDoc.getDocumentElement();
            LOG.debug("got the DocumentElement");
            elems[0] = new MessageElement(el);
            LOG.debug("put the domDoc into elems[0]");
//            if(log.isDebugEnabled())
//                log.debug("elems[0]\n"+elems[0].toString());
        }
        else { // string or json (really, not XML)
            Text t = new Text(recStr);
            if(LOG.isDebugEnabled()) {
                LOG.debug("recStr: "+recStr);
            }
            MessageElement me = new MessageElement(t);
            elems[0] = me;
        }
        if(schemaID!=null)
            rt.setRecordSchema(schemaID);
        else
            rt.setRecordSchema(schemaName);
    }


    public void makeExplainRecord(HttpServletRequest request) {
//        log.error("makeExplainRecord being called from:");
//        Thread.dumpStack();
        LOG.debug("Making an explain record for database "+dbname);
        StringBuilder sb=new StringBuilder(), urlStr=new StringBuilder();
        sb.append("      <explain authoritative=\"true\" xmlns=\"http://explain.z3950.org/dtd/2.0/\">\n");
        sb.append("        <serverInfo protocol=\"SRW/U\">\n");
        if(request!=null) {
            sb.append("          <host>").append(request.getServerName()).append("</host>\n");
            urlStr.append("http://").append(request.getServerName());
            sb.append("          <port>").append(request.getServerPort()).append("</port>\n");
            if(request.getServerPort()!=80)
                urlStr.append(":").append(request.getServerPort());
            sb.append("          <database");
            long lastUpdated=getLastUpdated();
            if(lastUpdated>0) {
                String iso8601date=ISO8601FORMAT.format(new Date(lastUpdated));
                String rfc3339date=iso8601date.substring(0, iso8601date.length()-2)+":00";
                sb.append(" lastUpdate=\"")
                  .append(rfc3339date)
                  .append('"');
            }
            long numRecs=getNumberOfDatabaseRecords();
            if(numRecs>0) {
                NumberFormat numberFormatter = NumberFormat.getNumberInstance();
                sb.append(" numRecs=\"")
                  .append(numberFormatter.format(numRecs))
                  .append('"');
            }
            sb.append('>');

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
            LOG.debug("baseURL="+baseURL);
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
            try (StringReader sr = new StringReader("<bogus>"+extraData+"</bogus>")) {
                domDoc = db.parse(new InputSource(sr));
            }
            Element el = domDoc.getDocumentElement();
            NodeList nodes=el.getChildNodes();
            MessageElement elems[] = new MessageElement[nodes.getLength()];
            for(int i=0; i<elems.length; i++)
                elems[i]=new MessageElement((Element)nodes.item(i));
            edt = new ExtraDataType();
            edt.set_any(elems);
        } catch (IOException | ParserConfigurationException e) {
            LOG.error(e, e);
        } catch (SAXException e) {
            LOG.error(e, e);
            LOG.error("Bad ExtraResponseData:\n" + Utilities.byteArrayToString(
                extraData.getBytes(StandardCharsets.UTF_8)));
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
        HashMap<String, String> newIndexes=new HashMap<>();
        int         start;
        String      name, newName, value;
        while(propertyNames.hasMoreElements()) {
            name=(String) propertyNames.nextElement();
            if(name.startsWith("qualifier.")) {
                if((start=name.indexOf('.', 11))>0) {
                    newName="hiddenQualifier."+name.substring(start+1);
                    LOG.debug("checking for "+newName);
                    if(newIndexes.get(newName)!=null) { // already got one
                        LOG.debug("dropping "+newName);
                        newIndexes.remove(newName); // so throw it away
                    }
                    else {
                        LOG.debug("keeping "+newName);
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
                LOG.debug("adding: "+name+"="+value);
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
        HashMap<String, String> extraDataTable = new HashMap<>();
        if (extraData!=null) {
            MessageElement[] elems = extraData.get_any();
            NameValuePair    nvp;
            String extraRequestData = elems[0].toString();
            ElementParser ep = new ElementParser(extraRequestData);
            LOG.debug("extraRequestData="+extraRequestData);
            while (ep.hasMoreElements()) {
                nvp = ep.nextElement();
                extraDataTable.put(nvp.getName(), nvp.getValue());
                LOG.debug(nvp);
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
            LOG.error(ex, ex);
            return;
        }
        db.useCount--;
        db.checkinTime=System.currentTimeMillis();
        LinkedList<SRWDatabase> queue=dbs.get(dbname);
        LOG.debug("about to synchronize #3 on queue");
        synchronized(queue) {
            queue.add(db);
            if(LOG.isDebugEnabled())
                LOG.debug("returning "+dbname+" database to the queue; "+queue.size()+" available");
        }
        LOG.debug("done synchronize #3 on queue");
    }

    static private String removeElement(String dirtyRecord, int offset, String nsElement) {
        StringBuilder sb=new StringBuilder(dirtyRecord.substring(0, offset));
        sb.append(dirtyRecord.substring(offset+nsElement.length()));
        return sb.toString();
    }

    public boolean replace(String recordID, byte[] record, RecordMetadata metadata) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    static public void resetTimer(String resultSetID) {
        QueryResult qr=OLD_RESULT_SETS.get(resultSetID);
        TIMERS.put(resultSetID, System.currentTimeMillis() + (qr.getResultSetIdleTime()*1000));
    }

    static public String revertJSONEntries(String rec) {
        int ce, i;
        String after, before, ceName, containingElement, entry, newCE;
//        System.out.println("rec="+rec);
        while((i=rec.indexOf("<e>"))>0) {
            ce=findContainingElement(rec, i);
            ceName=getElementName(rec, ce);
            containingElement=extractElement(rec, ce, ceName);
            before=rec.substring(0, ce);
            after=rec.substring(ce+containingElement.length());
//            System.out.println("before="+before);
//            System.out.println("after="+after);
            newCE="";
            while((i=containingElement.indexOf("<e>"))>0) {
                entry=extractElement(containingElement, i, "e");
//                System.out.println("entry="+entry);
                containingElement=removeElement(containingElement, i, entry);
                entry=entry.substring(3, entry.length()-4);
                newCE=newCE+"<"+ceName+">"+entry+"</"+ceName+">";
//                System.out.println("newCE="+newCE);
            }
            rec=before+newCE+after;
//            System.out.println("rec="+rec);
        }
        return rec;
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


    public Record transform(Record rec, String schemaID, String packing) throws SRWDiagnostic {
        if(LOG.isDebugEnabled()) {
            LOG.debug("schemaID="+schemaID+", rec.getRecordSchemaID="+rec.getRecordSchemaID());
            LOG.debug("packing="+packing+", rec.recordPacking="+rec.recordPacking);
        }

        switch(rec.recordPacking) {
            case "json":
                if("xml".equals(packing)) { // json to xml
                    if(LOG.isDebugEnabled())
                        LOG.debug("original JSON: "+rec.record);
                    JSON jo = JSONSerializer.toJSON(rec.getRecord());
                    XMLSerializer xmls = new XMLSerializer();
                    xmls.setTypeHintsEnabled(false);
//                    xmls.setWriteXMLDeclaration(false);
                    String dirtyRecord = xmls.write(jo);
                    if(LOG.isDebugEnabled())
                        LOG.debug("JSON rendered XML before cleanup: "+dirtyRecord);
                    // strip the xml declaration from the top.
                    dirtyRecord=cleanConvertedJSONtoXML(dirtyRecord);
                    if(LOG.isDebugEnabled())
                        LOG.debug("JSON rendered XML after cleanup: "+dirtyRecord);
                    rec=new Record(dirtyRecord, rec.schemaID, packing);
                    if (schemaID!=null && !rec.getRecordSchemaID().equals(schemaID)) {
                        if(LOG.isDebugEnabled())
                            LOG.debug("transforming to "+schemaID);
                        rec=xmlTransform(schemaID, rec);
                    }
                }
                else
                    throw new SRWDiagnostic(SRWDiagnostic.UnsupportedRecordPacking, "json to "+packing);
                break;
            case "xml":
                if (schemaID!=null && !rec.getRecordSchemaID().equals(schemaID)) {
                    if(LOG.isDebugEnabled())
                        LOG.debug("transforming to "+schemaID);
                    rec=xmlTransform(schemaID, rec);
                    if(LOG.isDebugEnabled())
                        LOG.debug("rec after xmlTransform: "+rec);
                }
                switch (packing) {
                    case "json":
                        if(!"xml".equals(rec.recordPacking) || rec.record.startsWith("{")) { // ooo! the transform made json!
                            rec.recordPacking="json";
                        } else {
                            // xml to json
                            if(LOG.isDebugEnabled())
                                LOG.debug("original XML: "+rec.record);
                            XMLSerializer xmlSerializer = new XMLSerializer();
                            JSON json = xmlSerializer.read(rec.record);
                            String dirtyRecord=json.toString(2);
                            if(LOG.isDebugEnabled())
                                LOG.debug("XML rendered JSON: "+dirtyRecord);
                            rec=new Record(dirtyRecord, rec.schemaID, packing);
                        }
                        break;
                    case "string":
                        rec=new Record(Utilities.xmlEncode(rec.record), rec.schemaID, packing);
                        break;
                    case "xml": // nothing to do
                        break;
                    default:
                        throw new SRWDiagnostic(SRWDiagnostic.UnsupportedRecordPacking, "xml to "+packing);
                }
                break;
            default:
                throw new SRWDiagnostic(SRWDiagnostic.UnsupportedRecordPacking, rec.recordPacking);
        }
        
        return rec;
    }


    public void useConfigInfo(String configInfo) {
        if(LOG.isDebugEnabled()) LOG.debug("configInfo="+configInfo);
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
                    switch (type) {
                        case "retrieveSchema":
                            schemas.put("default", nvp.getValue());
                            break;
                        case "maximumRecords":
                            maximumRecords = Integer.parseInt(nvp.getValue());
                            break;
                        case "numberOfRecords":
                            defaultNumRecs = Integer.parseInt(nvp.getValue());
                            break;
                        default:
                            break;
                    }
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
                LOG.debug("in useSchemaInfo: attributes="+attributes);
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
                    LOG.info("adding schema: "+schemaName);
                    schemas.put(schemaID, schemaID);
                    LOG.info("with schemaID: "+schemaID);
                }
            }
        }
    }
    
    Record xmlTransform(String schemaID, Record rec) throws SRWDiagnostic {
        // They must have specified a transformer
        Transformer t;
        Templates temp=templates.get(schemaID);
        if(temp!=null)
            try {
                t=temp.newTransformer();
            } catch (TransformerConfigurationException ex) {
                Logger.getLogger(SRWDatabase.class.getName()).log(Level.SEVERE, null, ex);
                t=null;
            }
        else
            t = transformers.get(schemaID);
        if (t==null) {
            LOG.warn("can't transform record in schema "+rec.getRecordSchemaID());
            LOG.warn("record: "+rec);
            LOG.warn("record not available in schema "+schemaID);
            LOG.warn("available schemas are:");
            for(String key:transformers.keySet())
                LOG.warn("    " + key);
            //throw new SRWDiagnostic(SRWDiagnostic.RecordNotAvailableInThisSchema, schemaID);
            return rec;  // return what we have and hope for the best
        }
        String recStr;
        if("xml".equals(rec.recordPacking))
            recStr= Utilities.hex07Encode(rec.getRecord());
        else {
            switch (rec.recordPacking) {
                case "json":
                    JSON jo = JSONSerializer.toJSON(rec.getRecord());
                    recStr = new XMLSerializer().write(jo);
                    break;
                case "string":
                    recStr=rec.getRecord(); // just guessing here
                    break;
                default:
                    throw new SRWDiagnostic(SRWDiagnostic.UnsupportedRecordPacking, rec.recordPacking+" to xml");
            }
        }
        StringWriter toRec = new StringWriter();
        StreamSource fromRec = new StreamSource(new StringReader(recStr));
        try {
            t.transform(fromRec, new StreamResult(toRec));
        } catch (TransformerException e) {
            LOG.error(e, e);
            LOG.error("bad record: "+recStr);
            throw new SRWDiagnostic(SRWDiagnostic.RecordNotAvailableInThisSchema, schemaID);
        }
        recStr=toRec.toString();
        String packing="xml";
        if(recordPacking.get(schemaID)!=null)
            packing=recordPacking.get(schemaID);
        rec=new Record(recStr, schemaID, packing);
        return rec;
    }
}
