/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.ExtraDataType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLParseException;
import org.z3950.zing.cql.CQLParser;
import org.z3950.zing.cql.CQLTermNode;

/**
 *
 * @author Ralph
 */
class OpenSearchQueryResult extends QueryResult {
    private static final Log log=LogFactory.getLog(OpenSearchQueryResult.class);
    String query;
    int count=0;
    private long numberOfRecords;
    private Element channel;
    
    public OpenSearchQueryResult(String queryStr, SearchRetrieveRequestType request,
            SRWOpenSearchDatabase db) throws InstantiationException, SRWDiagnostic {
        int start;
        PositiveInteger pi;
        String parameter;
        this.query=queryStr;
        // figure out what schema/template to use
        String schema=request.getRecordSchema();
        if(schema==null)
            schema=db.defaultSchemaID;
        if(schema==null)
            schema=db.defaultSchemaName;
        if(schema==null) {
            log.error("No schema provided");
            throw new InstantiationException("No schema provided");
        }

        CQLParser parser = new CQLParser(CQLParser.V1POINT1);
        CQLNode cqlQuery;
        try {
            cqlQuery=parser.parse(queryStr);
        }
        catch(CQLParseException | IOException e) {
            throw new SRWDiagnostic(SRWDiagnostic.QuerySyntaxError, queryStr);
        }
        if(!(cqlQuery instanceof CQLTermNode)) {
            throw new SRWDiagnostic(SRWDiagnostic.UnsupportedBooleanOperator, null);
        }
        CQLTermNode term=(CQLTermNode) cqlQuery;
        
        String template=db.templates.get(schema);
        log.debug("template="+template);
        if(template==null)
            throw new SRWDiagnostic(SRWDiagnostic.RecordNotAvailableInThisSchema, schema);
        Pattern p=Pattern.compile("\\{([^\\}]+)\\}");
        Matcher m=p.matcher(template);
        while(m.find()) {
            parameter=m.group();
            log.debug("template parameter="+parameter);
            switch (parameter) {
                case "{searchTerms}":
                    template=template.replace(parameter, term.getTerm());
                    break;
                case "{count}":
                    NonNegativeInteger nni = request.getMaximumRecords();
                    if(nni!=null)
                        count=nni.intValue();
                    else {
                        count=db.defaultNumRecs;
                    }   if(count<=0)
                        throw new InstantiationException("maximumRecords parameter not supplied and defaultNumRecs not specified in the database properties file");
                    template=template.replace(parameter, Integer.toString(count));
                    break;
                case "{startIndex}":
                    pi = request.getStartRecord();
                    if(pi!=null)
                        start=pi.intValue();
                    else {
                        start=1;
                    }
                    template=template.replace(parameter, Integer.toString(start));
                    break;
                case "{startPage}":
                    pi = request.getStartRecord();
                    if(pi!=null) {
                        start=pi.intValue();
                        if(db.itemsPerPage==0)
                            throw new InstantiationException("template expects startPage parameter but itemsPerPage not specified in the database properties file");
                        start=start/db.itemsPerPage;
                    }
                    else {
                        start=1;
                    }
                    template=template.replace(parameter, Integer.toString(start));
                    break;
            }
        }
        int i=template.indexOf('?');
        if(i>0)
            template=template.substring(0, i+1)+template.substring(i+1).replaceAll(" ", "+");
        
        log.debug("url="+template);
        URL url;
        try {
            url=new URL(template);
        } catch (MalformedURLException ex) {
            throw new SRWDiagnostic(SRWDiagnostic.GeneralSystemError, template);
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            log.debug("contentType="+conn.getContentType());
            log.debug("responseCode="+conn.getResponseCode());
            processResponse(request, conn, schema, conn.getContentType());
        } catch (IOException ex) {
            throw new SRWDiagnostic(SRWDiagnostic.GeneralSystemError, ex.getMessage());
        }
    }

    @Override
    public long getNumberOfRecords() {
        return numberOfRecords;
    }

    @Override
    public RecordIterator newRecordIterator(long index, int numRecs, String schemaId, ExtraDataType edt) throws InstantiationException {
        return new OpenSearchRecordIterator(index, numRecs, schemaId, edt, channel);
    }

    private void processResponse(SearchRetrieveRequestType request, HttpURLConnection conn, String schema, String type) throws SRWDiagnostic {
        try {
            int i;
            if((i=type.indexOf(';'))>=0) {
                type=type.substring(0, i).trim();
            }
            switch(type) {
                case "application/rss+xml":
                    processRssResponse(request, conn);
                    break;
                case "text/xml": // argh! not very helpful!
                default:
                    String input=readInput(conn);
                    log.error(input.substring(0, Math.min(500, input.length())));
                    throw new SRWDiagnostic(SRWDiagnostic.RecordNotAvailableInThisSchema, type);
            }
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            throw new SRWDiagnostic(SRWDiagnostic.GeneralSystemError, ex.getClass().getName()+":"+ex.getMessage());
        }
    }
    
    private String readInput(HttpURLConnection conn) throws IOException {
        StringBuilder response;
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            String inputLine;
            response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        return response.toString();
    }

    private void processRssResponse(SearchRetrieveRequestType request, HttpURLConnection conn) throws IOException, ParserConfigurationException, SAXException {
        Document doc = parseInput(conn.getInputStream());
        Element root = doc.getDocumentElement();
        channel = (Element)root.getElementsByTagName("channel").item(0);
        log.debug("channel="+channel);
        Node title=channel.getElementsByTagName("title").item(0);
        NodeList nodes = channel.getElementsByTagName("*");
        for(int i=0; i<nodes.getLength(); i++)
            log.debug("node["+i+"]="+nodes.item(i).getNodeName());
        log.debug("title="+title.getTextContent());
        Node totalResults=root.getElementsByTagNameNS("*", "totalResults").item(0);
        log.debug("totalResults="+totalResults);
        numberOfRecords=Long.parseLong(totalResults.getTextContent());
    }

    private Document parseInput(InputStream input) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(input);
    }
}
