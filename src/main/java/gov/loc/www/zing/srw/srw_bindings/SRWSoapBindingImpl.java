/*
   Copyright 2006 OCLC Online Computer Library Center, Inc.

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

package gov.loc.www.zing.srw.srw_bindings;

import ORG.oclc.os.SRW.SRWDatabase;
import ORG.oclc.os.SRW.SRWDiagnostic;
import ORG.oclc.os.SRW.Utilities;
import gov.loc.www.zing.cql.xcql.BooleanType;
import gov.loc.www.zing.cql.xcql.OperandType;
import gov.loc.www.zing.cql.xcql.RelationType;
import gov.loc.www.zing.cql.xcql.SearchClauseType;
import gov.loc.www.zing.cql.xcql.TripleType;
import gov.loc.www.zing.srw.EchoedScanRequestType;
import gov.loc.www.zing.srw.EchoedSearchRetrieveRequestType;
import gov.loc.www.zing.srw.ScanRequestType;
import gov.loc.www.zing.srw.ScanResponseType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.interfaces.SRWPort;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import org.apache.axis.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.z3950.zing.cql.CQLAndNode;
import org.z3950.zing.cql.CQLBooleanNode;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLNotNode;
import org.z3950.zing.cql.CQLOrNode;
import org.z3950.zing.cql.CQLParseException;
import org.z3950.zing.cql.CQLParser;
import org.z3950.zing.cql.CQLTermNode;

public class SRWSoapBindingImpl implements SRWPort {
    Log log=LogFactory.getLog(SRWSoapBindingImpl.class);
    static Method cqlWorkaroundMethod=null;
    CQLParser cqlparser=new CQLParser(CQLParser.V1POINT1);
    
    static {
        // The method getQualifier() was replaced by getIndex() in version 1.0 of 
        // the parser. This code ensures that either one works.
        Method m=null;
        try {
            m = CQLTermNode.class.getMethod("getQualifier", new Class<?>[0]);
        }
        catch (SecurityException ex) {
            throw new NoSuchMethodError("getQualifier; SecurityException");
        }
        catch (NoSuchMethodException ex) { /*try renamed method */ }
                        
        if ( m == null) {
            try {
                m = CQLTermNode.class.getMethod("getIndex", new Class<?>[0]);
            } 
            catch (SecurityException ex) {
                throw new NoSuchMethodError("getIndex; SecurityException");
            }
            catch (NoSuchMethodException ex) {
                throw new NoSuchMethodError("getIndex");
            }
        }
        cqlWorkaroundMethod=m;
    }

    @Override
    public SearchRetrieveResponseType searchRetrieveOperation(
      SearchRetrieveRequestType request) throws RemoteException {
        log.debug("Enter: searchRetrieveOperation");
        long startTime=System.currentTimeMillis();
        MessageContext             msgContext=MessageContext.getCurrentContext();
        SearchRetrieveResponseType response;
        String dbname=(String)msgContext.getProperty("dbname");
        SRWDatabase db=(SRWDatabase)msgContext.getProperty("db");
        if(log.isDebugEnabled())
            log.debug("db="+db);

        String sortKeys=request.getSortKeys();
        if(sortKeys!=null)
            request.setSortKeys(sortKeys);

        String query=request.getQuery();
        if(query.indexOf('%')>=0)
            try {
                request.setQuery(java.net.URLDecoder.decode(query, "utf-8"));
            }
            catch (java.io.UnsupportedEncodingException e) {
                log.error(query);
                log.error(e);
            }
            catch (IllegalArgumentException e) {
                log.error(query);
                log.error(e);
            }
        if(request.getStartRecord()!=null &&
          request.getStartRecord().intValue()==Integer.MAX_VALUE) {
            response=new SearchRetrieveResponseType();
            SRWDatabase.diagnostic(SRWDiagnostic.UnsupportedParameterValue,
                "startRecord", response);
        }
        else if(request.getMaximumRecords()!=null &&
          request.getMaximumRecords().intValue()==Integer.MAX_VALUE) {
            response=new SearchRetrieveResponseType();
            SRWDatabase.diagnostic(SRWDiagnostic.UnsupportedParameterValue,
                "maximumRecords", response);
        }
        else if(request.getResultSetTTL()!=null &&
          request.getResultSetTTL().intValue()==Integer.MAX_VALUE) {
            response=new SearchRetrieveResponseType();
            SRWDatabase.diagnostic(SRWDiagnostic.UnsupportedParameterValue,
                "resultSetTTL", response);
        }
        else
        try {
            response=db.doRequest(request);
            if(response==null) {
                response=new SearchRetrieveResponseType();
                response.setVersion("1.1");
                setEchoedSearchRetrieveRequestType(request, response);
                SRWDatabase.diagnostic(SRWDiagnostic.GeneralSystemError, null, response);
                return response;
            }
            if(msgContext.getProperty("sru")!=null &&
              request.getStylesheet()!=null) // you can't ask for a stylesheet in srw!
                SRWDatabase.diagnostic(SRWDiagnostic.StylesheetsNotSupported, null, response);

            setEchoedSearchRetrieveRequestType(request, response);
            if(request.getRecordXPath()!=null)
                SRWDatabase.diagnostic(72, null, response);
            if(request.getSortKeys()!=null &&
              !request.getSortKeys().equals("") && !db.supportsSort())
                SRWDatabase.diagnostic(SRWDiagnostic.SortNotSupported, null, response);

            // set extraResponseData
            StringBuilder extraResponseData = new StringBuilder();

            // we're going to stick the database name in extraResponseData every time
            if(db.databaseTitle!=null)
                extraResponseData.append("<databaseTitle>").append(db.databaseTitle).append("</databaseTitle>");
            else
                extraResponseData.append("<databaseTitle>").append(dbname).append("</databaseTitle>");

            // did they ask for the targetURL to be returned?
//            Hashtable extraRequestDataElements=SRWDatabase.parseElements(request.getExtraRequestData());
//            String s=(String)extraRequestDataElements.get("returnTargetURL");
//            log.info("returnTargetURL="+s);
//            if(s!=null && !s.equals("false")) {
//                String targetStr=(String)msgContext.getProperty("targetURL");
//                log.info("targetStr="+targetStr);
//                if(targetStr!=null && targetStr.length()>0) {
//                    URL target=new URL(targetStr);
//                    extraResponseData.append("<targetURL>")
//                        .append("<host>").append(target.getHost()).append("</host>")
//                        .append("<port>").append(target.getPort()).append("</port>")
//                        .append("<path>").append(target.getPath()).append("</path>")
//                        .append("<query>").append(Utilities.xmlEncode(target.getQuery())).append("</query>")
//                        .append("</targetURL>");
//                }
//            }

            // set extraResponseData
            SRWDatabase.setExtraResponseData(response, extraResponseData.toString());
        }
        catch(Exception e) {
            log.error(e, e);
            throw new RemoteException(e.getMessage(), e);
        }

        response.setVersion("1.1");
        log.info("\""+query+"\"==>"+response.getNumberOfRecords()+" ("+(System.currentTimeMillis()-startTime)+"ms)");
        log.debug("Exit: searchRetrieveOperation");
        return response;
    }


    @Override
    public ScanResponseType scanOperation(ScanRequestType request)
      throws java.rmi.RemoteException {
        log.debug("Enter: scanOperation");
        MessageContext   msgContext=MessageContext.getCurrentContext();
        ScanResponseType response;
        String dbname=(String)msgContext.getProperty("dbname");
        SRWDatabase db=(SRWDatabase)msgContext.getProperty("db");
        if(log.isDebugEnabled())
            log.debug("db="+db);
        if(request.getScanClause()==null) {
            response=new ScanResponseType();
            SRWDatabase.diagnostic(SRWDiagnostic.MandatoryParameterNotSupplied,
                "scanClause", response);
        }
        else if(request.getResponsePosition()!=null &&
          request.getResponsePosition().intValue()==Integer.MAX_VALUE) {
            response=new ScanResponseType();
            SRWDatabase.diagnostic(SRWDiagnostic.UnsupportedParameterValue,
                "responsePosition", response);
        }
        else if(request.getMaximumTerms()!=null &&
          request.getMaximumTerms().intValue()==Integer.MAX_VALUE) {
            response=new ScanResponseType();
            SRWDatabase.diagnostic(SRWDiagnostic.UnsupportedParameterValue,
                "maximumTerms", response);
        }
        else try {
            response=db.doRequest(request);

            // set extraResponseData
            StringBuilder extraResponseData = new StringBuilder();

            // we're going to stick the database name in extraResponseData every time
            if(db.databaseTitle!=null)
                extraResponseData.append("<databaseTitle>").append(db.databaseTitle).append("</databaseTitle>");
            else
                extraResponseData.append("<databaseTitle>").append(dbname).append("</databaseTitle>");

//                Hashtable extraRequestDataElements=SRWDatabase.parseElements(request.getExtraRequestData());
//                String s=(String)extraRequestDataElements.get("returnTargetURL");
//                log.info("returnTargetURL="+s);
//                if(s!=null && !s.equals("false")) {
//                    String targetStr=(String)msgContext.getProperty("targetURL");
//                    log.info("targetStr="+targetStr);
//                    if(targetStr!=null && targetStr.length()>0) {
//                        URL target=new URL(targetStr);
//                        extraResponseData.append("<targetURL>")
//                            .append("<host>").append(target.getHost()).append("</host>")
//                            .append("<port>").append(target.getPort()).append("</port>")
//                            .append("<path>").append(target.getPath()).append("</path>")
//                            .append("<query>").append(Utilities.xmlEncode(target.getQuery())).append("</query>")
//                            .append("</targetURL>");
//                    }
//                }

            // set extraResponseData
            SRWDatabase.setExtraResponseData(response, extraResponseData.toString());
        }
        catch(Exception e) {
            log.error(e, e);
            throw new RemoteException(e.getMessage(), e);
        }
        if(response!=null) {
                log.info("calling setEchoedScanRequestType");
            setEchoedScanRequestType(request, response);
                log.info("called setEchoedScanRequestType");
            response.setVersion("1.1");
        }
        log.debug("Exit: scanOperation");
        return response;
    }


    public void setEchoedScanRequestType(ScanRequestType request, ScanResponseType response) {
        EchoedScanRequestType ert=new EchoedScanRequestType();
        if(request.getVersion()!=null)
            ert.setVersion(request.getVersion());
        else
            ert.setVersion("1.1");
        ert.setMaximumTerms(request.getMaximumTerms());
        ert.setResponsePosition(request.getResponsePosition());

        String scanClause=request.getScanClause();
        log.info("scanClause="+scanClause);
        if(scanClause!=null) {
            ert.setScanClause(scanClause);
            try {
                CQLTermNode node=Utilities.getFirstTerm(new CQLParser(CQLParser.V1POINT1).parse(scanClause));
                if(node!=null) {
                    SearchClauseType sct=new SearchClauseType();
                    sct.setTerm(node.getTerm());
                    RelationType rt=new RelationType();
                    rt.setValue(node.getRelation().getBase());
                    sct.setRelation(rt);
                    sct.setIndex(getQualifier(node));
                    ert.setXScanClause(sct);
                }
                else {
                    SearchClauseType sct=new SearchClauseType();
                    sct.setTerm("Unrecognized node");
                    RelationType rt=new RelationType();
                    rt.setValue("");
                    sct.setRelation(rt);
                    sct.setIndex("");
                    ert.setXScanClause(sct);
                }
            }
            catch(java.io.IOException e) {
                log.error(e);
            }
            catch(org.z3950.zing.cql.CQLParseException e) {
                log.error(e);
                SearchClauseType sct=new SearchClauseType();
                sct.setTerm("CQLParseException");
                RelationType rt=new RelationType();
                rt.setValue("");
                sct.setRelation(rt);
                sct.setIndex("");
                ert.setXScanClause(sct);
            }
        }
        else {
            SearchClauseType sct=new SearchClauseType();
            sct.setTerm("");
            RelationType rt=new RelationType();
            rt.setValue("");
            sct.setRelation(rt);
            sct.setIndex("");
            ert.setXScanClause(sct);
        }


        response.setEchoedScanRequest(ert);
    }


    public void setEchoedSearchRetrieveRequestType(SearchRetrieveRequestType request, SearchRetrieveResponseType response) {
        EchoedSearchRetrieveRequestType ert=response.getEchoedSearchRetrieveRequest();
        if(ert==null) {
            ert=new EchoedSearchRetrieveRequestType();
        }
        ert.setMaximumRecords(request.getMaximumRecords());
        String query=request.getQuery();
        if(query!=null && query.length()>0) {
            ert.setQuery(makeSafe(query));
            try {
                CQLNode root=cqlparser.parse(query);
                ert.setXQuery(toOperandType(root));
            }
            catch (CQLParseException e) {
                log.error("parse problem: \""+query+"\"");
                RelationType rt=new RelationType("", null);
                SearchClauseType sct=new SearchClauseType("", rt, "");
                OperandType ot=new OperandType();
                ot.setSearchClause(sct);
                ert.setXQuery(ot);
            }
            catch (IOException e) {
                log.error(e,e);
                RelationType rt=new RelationType("", null);
                SearchClauseType sct=new SearchClauseType("", rt, "");
                OperandType ot=new OperandType();
                ot.setSearchClause(sct);
                ert.setXQuery(ot);
            }
        }
        else { // sadly, just because the request didn't include it doesn't mean
               // that the response gets to omit it.  So, provide an empty query
            ert.setQuery("");
            RelationType rt=new RelationType("", null);
            SearchClauseType sct=new SearchClauseType("", rt, "");
            OperandType ot=new OperandType();
            ot.setSearchClause(sct);
            ert.setXQuery(ot);
        }
        ert.setResultSetTTL(  request.getResultSetTTL());
        ert.setRecordPacking( request.getRecordPacking());
        ert.setSortKeys(      request.getSortKeys());
        ert.setStartRecord(   request.getStartRecord());
        if(request.getExtraRequestData()!=null)
            ert.setExtraRequestData(request.getExtraRequestData());
        if(request.getVersion()!=null)
            ert.setVersion(request.getVersion());
        else
            ert.setVersion("1.1");
        if(request.getRecordSchema()!=null)
            ert.setRecordSchema(request.getRecordSchema());
        else
            ert.setRecordSchema("default");
        response.setEchoedSearchRetrieveRequest(ert);
    }

    private OperandType toOperandType(CQLNode node) {
        OperandType ot=new OperandType();
        if(node instanceof CQLBooleanNode) {
            CQLBooleanNode cbn=(CQLBooleanNode)node;
            TripleType tt=new TripleType();
            if(cbn instanceof CQLAndNode)
                tt.set_boolean(new BooleanType("and", null));
            else if(cbn instanceof CQLOrNode)
                tt.set_boolean(new BooleanType("or", null));
            else if(cbn instanceof CQLNotNode)
                tt.set_boolean(new BooleanType("not", null));
            else tt.set_boolean(new BooleanType("prox", null));

            tt.setLeftOperand(toOperandType(cbn.left));
            tt.setRightOperand(toOperandType(cbn.right));
            ot.setTriple(tt);
        }
        else if(node instanceof CQLTermNode) {
            CQLTermNode ctn=(CQLTermNode)node;
            SearchClauseType sct=new SearchClauseType();
            sct.setIndex(getQualifier(ctn));
            RelationType rt=new RelationType();
            rt.setValue(ctn.getRelation().getBase());
            sct.setRelation(rt);
            sct.setTerm(makeSafe(ctn.getTerm()));
            ot.setSearchClause(sct);
        }
        else {
            log.error("Found a node on the parse tree of type: "+node);
        }
        return ot;
    }

    public static String getQualifier(CQLTermNode t) {
        Object args;
        try {
            args = cqlWorkaroundMethod.invoke(t, new Object[0]);
        }
        catch (IllegalArgumentException ex) {
            throw new NoSuchMethodError("getIndex; IllegalArgumentException");
        }
        catch (IllegalAccessException ex) {
            throw new NoSuchMethodError("getIndex; IllegalAccessException");
        }
        catch (InvocationTargetException ex) {
            throw new NoSuchMethodError("getIndex; InvocationTargetException");
        }
        
        return (String) args;
    }

    private String makeSafe(String s) {
        if (s == null) {
            return "";
        }
        boolean didSomething=false;
        StringBuilder sb = new StringBuilder();
        char[] chars=s.toCharArray();
        for(char c:chars) {
            if (c < ' ') // control characters got in as backslash sequences
            {            // in the CQL parser.  Return them to that format
                sb.append('\\').append(Integer.toString(c));
                didSomething=true;
            } else {
                sb.append(c);
            }
        }
        if(didSomething)
            return sb.toString();
        return s;
    }
}

