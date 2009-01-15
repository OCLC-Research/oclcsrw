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

import gov.loc.www.zing.srw.ExtraDataType;
import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.RecordsType;
import gov.loc.www.zing.srw.StringOrXmlFragment;
import gov.loc.www.zing.srw.TermsType;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;
import gov.loc.www.zing.srw.DiagnosticsType;
import gov.loc.www.zing.srw.ExplainResponseType;
import gov.loc.www.zing.srw.ScanRequestType;
import gov.loc.www.zing.srw.ScanResponseType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.srw_bindings.SRWSoapBindingImpl;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
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
import org.z3950.zing.cql.CQLBooleanNode;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLParseException;
import org.z3950.zing.cql.CQLParser;
import org.z3950.zing.cql.CQLTermNode;

/**
 *
 * @author  levan
 */
public abstract class SRWDatabase {
    private static Log log=LogFactory.getLog(SRWDatabase.class);

    public static final String DEFAULT_SCHEMA = "default";

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
                                  Properties dbProperties) throws Exception;

    public abstract boolean     supportsSort();


    public static Hashtable<String, String> badDbs=new Hashtable<String, String>();
    public static Hashtable<String, LinkedList<SRWDatabase>> dbs=new Hashtable<String, LinkedList<SRWDatabase>>();
    public static Hashtable<String, QueryResult> oldResultSets=new Hashtable<String, QueryResult>();
    public static Hashtable<String, Long> timers=new Hashtable<String, Long>();

    private static HouseKeeping houseKeeping=null;
    public static Properties srwProperties;
    public static String servletContext, srwHome;
    private static Timer timer=new Timer();

    static {
        houseKeeping=new HouseKeeping(timers, oldResultSets);
        timer.schedule(houseKeeping, 60000L, 60000L);
    }
    private boolean returnResultSetId=true;
    private Random rand=new Random();
    public HttpHeaderSetter httpHeaderSetter=null;
    public SearchRetrieveRequestType searchRequest;
    public SearchRetrieveResponseType response;
    public String  databaseTitle, dbname, explainStyleSheet=null,
                   scanStyleSheet=null, searchStyleSheet=null;

    protected boolean     letDefaultsBeDefault=false;
    protected CQLParser   parser = new CQLParser();
    protected Hashtable<String, String> nameSpaces=new Hashtable<String, String>(), schemas=new Hashtable<String, String>();
    protected Hashtable   sortTools = new Hashtable();
    protected Hashtable<String, Transformer> transformers=new Hashtable<String, Transformer>();
    protected int         defaultNumRecs=10, defaultResultSetTTL,
                          maximumRecords=20, maxTerms = 9, position = 5;
    protected Properties  dbProperties;
    protected SRWDatabase db;
    protected String      dbHome, dbPropertiesFileName, defaultSchema,
                          defaultStylesheet=null, explainRecord=null, schemaInfo;
    
    
    public boolean add(byte[] record) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void addSupports(String s, StringBuffer sb) {
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

    public Transformer addTransformer(String schemaName, String schemaID,
      String transformerFileName, Vector parameterNames, Vector parameterValues)
      throws FileNotFoundException, TransformerConfigurationException {
        if(schemaID!=null) {
            schemas.put(schemaName, schemaID);
            schemas.put(schemaID, schemaID);
        }
        if(transformerFileName==null) {
            log.info(schemaName+".transformer not specified");
            log.info(".props filename is " + dbPropertiesFileName);
            return null;
        }
        if(transformerFileName.startsWith("Renderer=")) // old notation, ignore
            return null;
//        StringTokenizer st=new StringTokenizer(transformerFileName, " \t=");
//        String token=st.nextToken();
        Source             xslSource;
        TransformerFactory tFactory=
            TransformerFactory.newInstance();
        File f=Utilities.findFile(transformerFileName, dbHome, srwHome);
        xslSource=new StreamSource(Utilities.openInputStream(
            transformerFileName, dbHome, srwHome));
        if(xslSource==null) {
            log.error("Unable to make StreamSource for: "+
                transformerFileName);
            log.error(".props filename is " + dbPropertiesFileName);
            return null;
        }
        try {
            xslSource.setSystemId(f.toURI().toURL().toString());
        }
        catch(MalformedURLException e) {
            log.error("trying to set the xslSource SystemID", e);
        }

        Transformer t=tFactory.newTransformer(xslSource);
        // set any parameters to be passed to the transformer
        if(parameterNames!=null)
            for(int i=0; i<parameterNames.size(); i++)
                t.setParameter((String)parameterNames.get(i), parameterValues.get(i));

        transformers.put(schemaName, t);
        if(schemaID!=null)
            transformers.put(schemaID, t);
        log.info("added transformer for schemaName "+schemaName+", and schemaID "+schemaID);
        return t;
    }

    public boolean delete(String recordKey) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public ExplainResponseType diagnostic(final int code,
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


    public ScanResponseType diagnostic(final int code,
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


    public SearchRetrieveResponseType diagnostic(final int code,
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

        CQLTermNode root = null;
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
        try{
            if (scanTerm!=null)
                log.info("scanTerm:\n" + Utilities.byteArrayToString(scanTerm.getBytes("UTF-8")));
        } catch (Exception e){}
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
            root = new CQLTermNode(SRWSoapBindingImpl.getQualifier(root), root.getRelation(), "$");
        String resultSetID = root.getResultSetName();
        if (resultSetID!=null) { // you can't scan on resultSetId!
            return diagnostic(SRWDiagnostic.UnsupportedIndex,
                    "cql.resultSetId", scanResponse);
        }
        TermsType terms = new TermsType();
        TermList tl=getTermList(root, pos, max, request);
        terms.setTerm(tl.getTerms());
        scanResponse.setTerms(terms);

        Vector<DiagnosticType> diagnostics = tl.getDiagnostics();
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
            try{
                log.debug("query:\n" + Utilities.byteArrayToString(query.getBytes("UTF-8")));
            } catch (Exception e){}

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
                log.error(e, e);
                return diagnostic(SRWDiagnostic.GeneralSystemError,
                        e.getMessage(), response);
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
            NonNegativeInteger maxRecs = request.getMaximumRecords();
            if (maxRecs!=null)
                numRecs = (int) Math.min(maxRecs.longValue(), maximumRecords);

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

            String schemaName = request.getRecordSchema();
            if(schemaName==null)
                schemaName="default";
            String schemaID=null;
            if(!letDefaultsBeDefault || !schemaName.equals("default")) {
                schemaID = getSchemaID(schemaName);
                if(schemaID==null) {
                    diagnostic(SRWDiagnostic.UnknownSchemaForRetrieval, schemaName, response);
                    numRecs=0;
                }
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
                            diagnostic(SRWDiagnostic.SortNotSupported,
                                null, response);
                        else
                            result=sortedResult;
                    }  // if(sortKeys!=null && sortKeys.length()>0)
                    
                                        // render some records
                                        RecordIterator list = null;
                    try {
                        log.debug("making RecordIterator, startPoint="+startPoint+", schemaID="+schemaID);
                        list=result.recordIterator(startPoint, numRecs, schemaID, request.getExtraRequestData());
                    } catch (InstantiationException e) {
                        diagnostic(SRWDiagnostic.GeneralSystemError,
                            e.getMessage(), response);
                    }
                    RecordsType records = new RecordsType();

                    records.setRecord(new RecordType[numRecs]);
                    Document               domDoc;
                    DocumentBuilder        docb = null;
                    DocumentBuilderFactory dbf = null;
                    int                    i,  listEntry = -1;
                    MessageElement         elems[];
                    Record                 rec;
                    RecordType             rt;
                    String                 recStr = "";
                    StringOrXmlFragment    frag;
                    if (recordPacking.equals("xml")) {
                        dbf = DocumentBuilderFactory.newInstance();
                        dbf.setNamespaceAware(true);
                        try {
                            docb=dbf.newDocumentBuilder();
                        } catch (ParserConfigurationException e) {
                            log.error(e, e);
                        }
                    }

                    /**
                     * One at a time, retrieve and display the requested documents.
                     */
                    log.debug("trying to get "+numRecs+
                        " records starting with record "+startPoint+
                        " from a set of "+postingsCount+" records");
                    for (i=0; list!=null && i<numRecs && list.hasNext(); i++) {
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
                                try {
                                    log.debug("Transformed XML:\n" + Utilities.byteArrayToString(
                                        recStr.getBytes("UTF8")));
                                } catch (UnsupportedEncodingException e) {} // can't happen
                            makeElem(recStr, rt, schemaID, schemaName, recordPacking, docb, elems);
                            if(rec.hasExtraRecordInfo())
                                setExtraRecordData(rt, rec.getExtraRecordInfo());
                        } catch (IOException e) {
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
                        } catch (NoSuchElementException e) {
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
                            try {
                                log.error("Bad record:\n" + Utilities.byteArrayToString(
                                        recStr.getBytes("UTF8")));
                            } catch (UnsupportedEncodingException e2) {} // can't happen
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
                            try {
                                log.error("Bad record:\n" + Utilities.byteArrayToString(
                                        recStr.getBytes("UTF8")));
                            } catch (UnsupportedEncodingException e2) {} // can't happen
                        }

                        rt.setRecordData(frag);

                        rt.setRecordPosition(new PositiveInteger(Long.toString(startPoint+i)));

                        records.setRecord(i, rt);
                    }
                    response.setRecords(records);
                    if (startPoint+i<=postingsCount)
                        response.setNextRecordPosition(new PositiveInteger(Long.toString(startPoint+i)));
                } // else if(numRecs>0)
        } // if(postingsCount>0)

        String extraResponseData = getExtraResponseData(result, request);
        if(extraResponseData!=null)
            setExtraResponseData(response, extraResponseData);

        Vector<DiagnosticType> diagnostics = result.getDiagnostics();
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


    public String extractSortField(Object record) {
        return null;
    }

    public String getConfigInfo() {
        StringBuffer sb=new StringBuffer();
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
        StringBuffer sb=new StringBuffer();
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


    public static SRWDatabase getDB(String dbname, Properties properties) {
        return getDB(dbname, properties, null);
    }
    public static SRWDatabase getDB(String dbname, Properties properties, String servletContext) {
        log.debug("enter SRWDatabase.getDB");
        if(badDbs.get(dbname)!=null) // we've seen this one before
            return null;

        LinkedList<SRWDatabase> queue=dbs.get(dbname);
        SRWDatabase db=null;
        try {
        if(queue==null)
            log.info("No databases created yet for database "+dbname);
        else {
            log.debug("about to synchronize #1 on queue");
            synchronized(queue) {
                if(queue.isEmpty())
                    log.info("No databases available for database "+dbname);
                else {
                    db=queue.removeFirst();
                    if(db==null)
                        log.debug("popped a null database off the queue for database "+dbname);
                }
            }
            log.debug("done synchronize #1 on queue");
        }
        if(db==null) {
            log.info("creating a database for "+dbname);
            try{
                while(db==null) {
                    initDB(dbname, properties, servletContext);
                    queue=dbs.get(dbname);
                    log.debug("about to synchronize #2 on queue");
                    synchronized(queue) {
                        if(!queue.isEmpty()) // crap, someone got to it before us
                            db=queue.removeFirst();
                    }
                }
            log.debug("done synchronize #2 on queue");
            }
            catch(Exception e) { // database not available
                badDbs.put(dbname, dbname);
                log.error(e, e);
                return null;
            }
        }
        }
        catch(Exception e) {
            log.error(e,e);
            log.error("shoot!");
        }
        if(log.isDebugEnabled())
            log.debug("getDB: db="+db);
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


    public String getExplainRecord() {
        if(explainRecord==null)
            makeExplainRecord(null);
        return explainRecord;
    }


    public String getExplainRecord(HttpServletRequest request) {
        if(explainRecord==null)
            makeExplainRecord(request);
        return explainRecord;
    }


    public int getMaximumRecords() {
        return maximumRecords;
    }
    
    
    public String getMetaInfo() {
        StringBuffer sb=new StringBuffer();
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

    public static Vector getResultSetIds(CQLNode root) throws SRWDiagnostic {
        Vector<String> resultSetIds=new Vector<String>();
        getResultSetIds(root, resultSetIds);
        return resultSetIds;
    }
    
    public static void getResultSetIds(CQLNode root, Vector<String> resultSetIds) throws SRWDiagnostic {
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


    public static synchronized void initDB(final String dbname, Properties properties)
      throws InstantiationException {
        initDB(dbname, properties, null);
    }

    public static synchronized void initDB(final String dbname, Properties properties, String context)
      throws InstantiationException {
        log.debug("Enter: initDB, dbname="+dbname);
        String dbn="db."+dbname;

        srwProperties=properties;
        srwHome=properties.getProperty("SRW.Home");
        servletContext=context;
        if(srwHome!=null && !srwHome.endsWith("/"))
            srwHome=srwHome+"/";
        log.debug("SRW.Home="+srwHome);
        Properties dbProperties=new Properties();
        String dbHome=properties.getProperty(dbn+".home"),
               dbPropertiesFileName=null;
        if(dbHome!=null) {
            if(!dbHome.endsWith("/"))
                dbHome=dbHome+"/";
            log.debug("dbHome="+dbHome);
        }

        String className=properties.getProperty(dbn+".class");
        log.debug("className="+className);
        if(className==null) {
            // let's see if there's a fallback database to use
            className=properties.getProperty("db.default.class");
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
        SRWDatabase db=null;
        try {
            log.debug("creating class "+className);
            Class  dbClass=Class.forName(className);
            log.debug("creating instance of class "+dbClass);
            db=(SRWDatabase)dbClass.newInstance();
            log.debug("class created");
        }
        catch(Exception e) {
            log.error("Unable to create Database class "+className+
                " for database "+dbname);
            log.error(e, e);
            throw new InstantiationException(e.getMessage());
        }

        dbPropertiesFileName=properties.getProperty(dbn+".configuration");
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
            catch(Exception e) {
                log.error("Unable to load database configuration file!");
                log.error(e, e);
            }
            makeUnqualifiedIndexes(dbProperties);
            try {
                db.init(dbname, srwHome, dbHome, dbPropertiesFileName, dbProperties);
            }
            catch(InstantiationException e) {
                throw e;
            }
            catch(Exception e) {
                log.error("Unable to initialize database "+dbname);
                log.error(e, e);
                throw new InstantiationException(e.getMessage());
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
                }
            }
            else
                db.setDefaultResultSetTTL(300);

            temp=dbProperties.getProperty("letDefaultsBeDefault");
            if(temp!=null && temp.equals("true"))
                db.letDefaultsBeDefault=true;
        }
        else { // default settings
            try {
                db.init(dbname, srwHome, dbHome, "(no database configuration file specified)", dbProperties);
            }
            catch(Exception e) {
                log.error("Unable to create Database class "+className+
                    " for database "+dbname);
                log.error(e, e);
                throw new InstantiationException(e.getMessage());
            }
            db.setDefaultResultSetTTL(300);
            log.info("no configuration file needed or specified");
        }

        if(!(db instanceof SRWDatabasePool)) {
            LinkedList<SRWDatabase> queue=dbs.get(dbname);
            if(queue==null)
                queue=new LinkedList<SRWDatabase>();
            queue.add(db);
            if(log.isDebugEnabled())
                log.debug(dbname+" has "+queue.size()+" copies");
            dbs.put(dbname, queue);
        }
        log.debug("Exit: initDB");
        return;
    }
    
    
    protected void initDB(final String dbname, String srwHomeVal, String dbHome, String dbPropertiesFileName, Properties dbProperties) {
        log.debug("Enter: private initDB, dbname="+dbname);
        this.dbname=dbname;
        srwHome=srwHomeVal;
        this.dbHome=dbHome;
        this.dbPropertiesFileName=dbPropertiesFileName;
        this.dbProperties=dbProperties;

        if(dbProperties!=null) {
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

            // get schema transformers
            String          firstSchema=null, xmlSchemaList=dbProperties.getProperty("xmlSchemas");
            StringBuffer    schemaInfoBuf=new StringBuffer("        <schemaInfo>\n");
            StringTokenizer st;
            if(xmlSchemaList!=null) {
                Enumeration propertyNames;
                String      name, schemaIdentifier, schemaName, transformerName,
                            value;
                Vector<String> parms, values;
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
                    parms=new Vector<String>();
                    values=new Vector<String>();
                    propertyNames=dbProperties.propertyNames();
                    while(propertyNames.hasMoreElements()) {
                        name=(String)propertyNames.nextElement();
                        if(name.startsWith(schemaName+".parameter.")) {
                            value=dbProperties.getProperty(name);
                            values.add(value);
                            name=name.substring(schemaName.length()+11);
                            parms.add(name);
                            if(log.isDebugEnabled())
                                log.debug("transformer parm: "+name+"="+value);
                        }
                    }

                    try {
                        addTransformer(schemaName, schemaIdentifier, transformerName, parms, values);
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
                    catch(Exception e) {
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

            String t=srwProperties.getProperty("SRW.Context");
            if(t!=null)
                servletContext=t;
            explainStyleSheet=dbProperties.getProperty("explainStyleSheet");
            if(explainStyleSheet==null)
                explainStyleSheet="/$context/explainResponse.xsl";
            searchStyleSheet=dbProperties.getProperty("searchStyleSheet");
            if(searchStyleSheet==null)
                searchStyleSheet="/$context/searchRetrieveResponse.xsl";
            scanStyleSheet=dbProperties.getProperty("scanStyleSheet");
            if(scanStyleSheet==null)
                scanStyleSheet="/$context/scanResponse.xsl";
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
        }
        log.debug("Exit: private initDB");
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
        log.debug("Making an explain record for database "+dbname);
        StringBuffer sb=new StringBuffer();
sb.append("      <explain authoritative=\"true\" xmlns=\"http://explain.z3950.org/dtd/2.0/\">\n");
sb.append("        <serverInfo protocol=\"SRW/U\">\n");
if(request!=null) {
sb.append("          <host>"+request.getServerName()+"</host>\n");
sb.append("          <port>"+request.getServerPort()+"</port>\n");
sb.append("          <database>");
        String contextPath=request.getContextPath();
if(contextPath!=null && contextPath.length()>1)
    sb.append(contextPath.substring(1));
sb.append(request.getServletPath()).append(request.getPathInfo()).append("</database>\n");
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
            domDoc=null;
        } catch (IOException e) {
            log.error(e, e);
        } catch (ParserConfigurationException e) {
            log.error(e, e);
        } catch (SAXException e) {
            log.error(e, e);
            try {
                log.error("Bad ExtraResponseData:\n" + Utilities.byteArrayToString(
                    extraData.getBytes("UTF8")));
            } catch (UnsupportedEncodingException e2) {} // can't happen
        }
        return edt;
    }

    public static ExtraDataType makeExtraRequestDataType(String extraData) {
        return makeExtraDataType(extraData);
    }

    protected String makeResultSetID() {
        int          i, j;
        StringBuffer sb=new StringBuffer();
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
     */
    static protected void makeUnqualifiedIndexes(Properties props) {
        Enumeration enumer=props.propertyNames();
        Hashtable<String, String> newIndexes=new Hashtable<String, String>();
        int         start;
        String      name, newName, value;
        while(enumer.hasMoreElements()) {
            name=(String)enumer.nextElement();
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
        enumer=newIndexes.keys();
        while(enumer.hasMoreElements()) {
            name=(String)enumer.nextElement();
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


    public static Hashtable<String, String> parseElements(ExtraDataType extraData) {
        Hashtable<String, String> extraDataTable = new Hashtable<String, String>();
        if (extraData!=null) {
            MessageElement[] elems = extraData.get_any();
            NameValuePair    nvp;
            String extraRequestData = elems[0].toString();
            ElementParser ep = new ElementParser(extraRequestData);
            log.debug("extraRequestData="+extraRequestData);
            while (ep.hasMoreElements()) {
                nvp = (NameValuePair) ep.nextElement();
                extraDataTable.put(nvp.getName(), nvp.getValue());
                log.debug(nvp);
            }
        }
        return extraDataTable;
    }

    
    public static void putDb(String dbname, SRWDatabase db) {
        LinkedList<SRWDatabase> queue=dbs.get(dbname);
        log.debug("about to synchronize #3 on queue");
        synchronized(queue) {
            queue.add(db);
            if(log.isDebugEnabled())
                log.debug("returning "+dbname+" database to the queue; "+queue.size()+" available");
        }
        log.debug("done synchronize #3 on queue");
    }

    public boolean replace(byte[] record) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    static public void resetTimer(String resultSetID) {
        QueryResult qr=oldResultSets.get(resultSetID);
        timers.put(resultSetID, new Long(System.currentTimeMillis() + (qr.getResultSetIdleTime()*1000)));
    }

    public void setDefaultResultSetTTL(int defaultResultSetTTL) {
        this.defaultResultSetTTL=defaultResultSetTTL;
    }


    public void setExplainRecord(String explainRecord) {
        this.explainRecord=explainRecord;
    }


    static public void setExtraRecordData(RecordType rt, String extraData) {
        ExtraDataType edt=rt.getExtraRecordData();
        StringBuffer extraResponseData = new StringBuffer("<extraData xmlns=\"http://oclc.org/srw/extraData\">");
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
        StringBuffer extraResponseData = new StringBuffer("<extraData xmlns=\"http://oclc.org/srw/extraData\">");
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
        StringBuffer extraResponseData = new StringBuffer("<extraData xmlns=\"http://oclc.org/srw/extraData\">");
        if(edt!=null) {
            MessageElement[] elems = edt.get_any();
            String currentExtraData=elems[0].toString();
            int end=currentExtraData.lastIndexOf('<'), start=currentExtraData.indexOf('<', 1);
            extraResponseData.append(currentExtraData.substring(start, end-1));
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
        StringBuffer sb=new StringBuffer();
        sb.append("Database ").append(dbname).append(" of type ")
          .append(this.getClass().getName());
        return sb.toString();
    }


    public Record transform(Record rec, String schemaID) throws SRWDiagnostic {
        String recStr = Utilities.hex07Encode(rec.getRecord());
        if (schemaID!=null && !rec.getRecordSchemaID().equals(schemaID)) {
            log.debug("transforming to "+schemaID);
            // They must have specified a transformer
            Transformer t = transformers.get(schemaID);
            if (t==null) {
                log.error("can't transform record in schema "+rec.getRecordSchemaID());
                log.error("record not available in schema "+schemaID);
                log.error("available schemas are:");
                Enumeration enumer = transformers.keys();
                while (enumer.hasMoreElements()) {
                    log.error("    " + (String) enumer.nextElement());
                }
                throw new SRWDiagnostic(SRWDiagnostic.RecordNotAvailableInThisSchema, schemaID);
            }
            StringWriter toRec = new StringWriter();
            StreamSource fromRec = new StreamSource(new StringReader(recStr));
            try {
                t.transform(fromRec, new StreamResult(toRec));
            } catch (TransformerException e) {
                log.error(e, e);
                throw new SRWDiagnostic(SRWDiagnostic.RecordNotAvailableInThisSchema, schemaID);
            }
            recStr=toRec.toString();
        }
        return new Record(recStr, schemaID);
    }


    public void useConfigInfo(String configInfo) {
        if(log.isDebugEnabled()) log.debug("configInfo="+configInfo);
        ElementParser ep = new ElementParser(configInfo);
        NameValuePair nvp,  configInfoPair = (NameValuePair)ep.nextElement();
        String attribute,  attributes,  type;
        StringTokenizer st;
        ep = new ElementParser(configInfoPair.getValue());
        while (ep.hasMoreElements()) {
            nvp = (NameValuePair) ep.nextElement();
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
        NameValuePair nvp,  schemaInfoPair = (NameValuePair)ep.nextElement();
        String attribute,  attributes,  schemaID,  schemaName;
        StringTokenizer st;
        ep = new ElementParser(schemaInfoPair.getValue());
        while (ep.hasMoreElements()) {
            nvp = (NameValuePair) ep.nextElement();
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
