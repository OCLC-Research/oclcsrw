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
/*
 * EmbeddedSRWDatabase.java
 *
 * Created on November 19, 2002, 1:53 PM
 */

package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.diagnostic.DiagnosticType;
import gov.loc.www.zing.srw.DiagnosticsType;
import gov.loc.www.zing.srw.ExtraDataType;
import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.RecordsType;
import gov.loc.www.zing.srw.ScanRequestType;
import gov.loc.www.zing.srw.ScanResponseType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.StringOrXmlFragment;
import gov.loc.www.zing.srw.TermType;
import gov.loc.www.zing.srw.TermsType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

//import ORG.oclc.ber.DataDir;
//import ORG.oclc.RecordHandler.HandleSGML;

public class EmbeddedSRWDatabase {

    public EmbeddedSRWDatabase() {
    }

    public static void main(String args[]) {
        try {
            Properties props = new Properties();
//            String dbHome = "/proj/scorpion/rrl/dbs/ORPublications/";
            String dbname=null, propertiesFileName = "SRWServer.props";
            char argType, c;
            int  argOffset, i, j;

            for(i=0; i<args.length; i++) {
                if(args[i].charAt(0)=='-') {
                    argType=args[i].charAt(1);
                    if(args[i].length()==2 && (i+1)<args.length &&
                       args[i+1].charAt(0)!='-') {
                        i++;
                        argOffset=0;
                        System.out.println("\t-" + argType + args[i]);
                    }
                    else {
                        argOffset=2;
                        if(!args[i].equals("-fm"))
                            System.out.println("\t" + args[i]);
                    }
                    switch(argType) {
                        case 'd':
                            dbname=args[i].substring(argOffset);
                            break;

                        case 'p':
                            propertiesFileName=args[i].substring(argOffset);
                            break;

                        default:
                            System.out.println("unrecognized argument!");
                            usage();
                            return;
                    }
                }
            }
            if(dbname==null) {
                usage();
                return;
            }
            try {
                System.out.println("Reading SRW Server configuration file: " + propertiesFileName);
                InputStream is = Utilities.openInputStream(propertiesFileName, ".", null);
                props.load(is);
                is.close();
            }
            catch(java.io.FileNotFoundException e) {
                System.out.println("Unable to open database configuration file!");
                e.printStackTrace();
                return;
            }
            catch(Exception e) {
                System.out.println("Unable to load database configuration file!");
                e.printStackTrace();
                return;
            }
            SRWDatabase db = SRWDatabase.getDB(dbname, props);

            boolean quit=false, restrictorSummary=false, scan, search;
            Enumeration keys;
            Hashtable parms;
            int maximumRecords=1, maximumTerms=11, responsePosition=6, resultSetTTL=0, startRecord=1;
            long startTime;
            ScanRequestType scanRequest;
            SearchRetrieveRequestType searchRequest;
            SearchRetrieveResponseType searchResponse;
            String input, key, query=null, recordPacking="xml", scanClause=null,
                   schema=null, sortKeys=null;
            Transformer transformer=null;
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            System.out.println("\ntype \"help\" for an explanation of the commands");
            System.out.print("enter request: ");
            input=br.readLine();
            while(input!=null && input.length()>0 && !quit) {
                parms=parseInput(input);
                scan=search=false;
                keys=parms.keys();
                while(keys.hasMoreElements()) {
                    key=(String)keys.nextElement();
                    if(key.equals("explain")) {
                        System.out.println(db.getExplainRecord(null));
                    }
                    else if(key.equals("help")) {
                        help();
                    }
                    else if(key.equals("maximumRecords")) {
                        maximumRecords=Integer.parseInt((String)parms.get(key));
                    }
                    else if(key.equals("maximumTerms")) {
                        maximumTerms=Integer.parseInt((String)parms.get(key));
                    }
                    else if(key.equals("q") || key.equals("quit")) {
                        quit=true;
                    }
                    else if(key.equals("query")) {
                        query=convertUnicodeSequence((String)parms.get(key));
                        search=true;
                    }
                    else if(key.equals("recordPacking")) {
                        recordPacking=(String)parms.get(key);
                    }
                    else if(key.equals("recordSchema")) {
                        schema=(String)parms.get(key);
                    }
                    else if(key.equals("responsePosition")) {
                        responsePosition=Integer.parseInt((String)parms.get(key));
                    }
                    else if(key.equals("resultSetTTL")) {
                        resultSetTTL=Integer.parseInt((String)parms.get(key));
                    }
                    else if(key.equals("scanClause")) {
                        scanClause=convertUnicodeSequence((String)parms.get(key));
                        scan=true;
                    }
                    else if(key.equals("sortKeys")) {
                        sortKeys=(String)parms.get(key);
                    }
                    else if(key.equals("startRecord")) {
                        startRecord=Integer.parseInt((String)parms.get(key));
                    }
                    else if(key.equals("x-info-5-restrictorSummary")) {
                        restrictorSummary=!((String)parms.get(key)).equals("false");
                    }
                    else if(key.equalsIgnoreCase("xsl")) {
                        String xslFileName=(String)parms.get(key);
                        Source xslSource=new StreamSource(xslFileName);
                        if(xslSource==null) {
                            System.out.println("Unable to make StreamSource for: "+xslFileName);
                            continue;
                        }
                        TransformerFactory tFactory=TransformerFactory.newInstance();
                        transformer=tFactory.newTransformer(xslSource);
                    }
                    else { // assume it is a query
                        query=key;
                        search=true;
                    }
                }
                if(search) {
                    System.out.print("operation=searchRetrieve");
                    searchRequest=new SearchRetrieveRequestType();
                    System.out.print("&version=1.1");
                    searchRequest.setVersion("1.1");
                    System.out.print("&maximumRecords="+maximumRecords);
                    searchRequest.setMaximumRecords(new NonNegativeInteger(Integer.toString(maximumRecords)));
                    System.out.print("&query="+query);
                    searchRequest.setQuery(query);
                    System.out.print("&recordPacking="+recordPacking);
                    searchRequest.setRecordPacking(recordPacking);
                    if(schema!=null && schema.length()>0) {
                        System.out.print("&recordSchema="+schema);
                        searchRequest.setRecordSchema(schema);
                    }
                    if(restrictorSummary) {
                        System.out.print("&x-info-5-restrictorSummary");
                        ExtraDataType edt=new ExtraDataType();
                        MessageElement elems[]=new MessageElement[1];
                        edt.set_any(elems);
                        Document domDoc;
                        DocumentBuilderFactory dbf=
                            DocumentBuilderFactory.newInstance();
                        dbf.setNamespaceAware(true);
                        DocumentBuilder docb=dbf.newDocumentBuilder();
                        StringReader sr=new StringReader("<restrictorSummary xmlns=\"info:srw/extension/5/restrictorSummary\"/>");
                        domDoc=docb.parse(new InputSource(sr));
                        sr.close();
                        Element el=domDoc.getDocumentElement();
                        elems[0]=new MessageElement(el);
                        domDoc=null;
                        searchRequest.setExtraRequestData(edt);
                    }
                    System.out.print("&resultSetTTL="+resultSetTTL);
                    searchRequest.setResultSetTTL(new NonNegativeInteger(Integer.toString(resultSetTTL)));
                    if(sortKeys!=null && sortKeys.length()>0) {
                        System.out.print("&sortKeys="+sortKeys);
                        searchRequest.setSortKeys(sortKeys);
                    }
                    System.out.println("&startRecord="+startRecord+"\n");
                    searchRequest.setStartRecord(new PositiveInteger(Integer.toString(startRecord)));
                    startTime=System.currentTimeMillis();
                    searchResponse = db.doRequest(searchRequest);
                    System.out.println("elapsed time: "+(System.currentTimeMillis()-startTime)+"ms");
                    DiagnosticsType diags=searchResponse.getDiagnostics();
                    if(diags!=null) {
                        DiagnosticType diag, diagArray[]=diags.getDiagnostic();
                        for(i=0; i<diagArray.length; i++) {
                            diag=diagArray[i];
                            System.out.print("diagnostic: "+diag.getUri());
                            if(diag.getDetails()!=null)
                                System.out.print(", details: "+diag.getDetails());
                            System.out.println();
                        }
                    }
                    System.out.println("postings=" + searchResponse.getNumberOfRecords());
                    
//                    ExtraDataType edt=searchResponse.getExtraResponseData();
//                    if(edt!=null) {
//                        HandleSGML rh=new HandleSGML();
//                        StringBuffer tags=new StringBuffer();
//                        tags.append("ns1:restrictorSummary 0 recordTag\n");
//                        tags.append("restrictor             10\n");
//                        tags.append("entry                  11\n");
//                        tags.append("xmlns                  12\n");
//                        tags.append("xmlns:ns1              12\n");
//                        tags.append("count                  13\n");
//                        tags.append("use                    14\n");
//                        rh.loadTags(new StringReader(tags.toString()));
//                        rh.setByteToCharConverter("utf8");
//                        MessageElement elems[]=edt.get_any();
//                        rh.Input(new ByteArrayInputStream(elems[0].toString().getBytes(Charset.forName("UTF-8"))));
//                        DataDir summary=rh.getNextRecord();
//                        DataDir restrictors[]=new DataDir[summary.count()-1];
//                        restrictors[0]=summary.child().next();
//                        for(int i=1; i<restrictors.length; i++)
//                            restrictors[i]=restrictors[i-1].next();
//                        for(int i=0; i<restrictors.length; i++) {
//                            System.out.print("use="+restrictors[i].find(2).find(14).getUTFString());
//                            System.out.print(" count="+restrictors[i].find(2).find(13).getUTFString());
//                            System.out.print("     ");
//                        }
//                        System.out.println();
//                        for(int i=0; i<restrictors.length; i++)
//                            restrictors[i]=restrictors[i].child().next();
//                        for(int j=0; j<5; j++) {
//                            for(int i=0; i<restrictors.length; i++) {
//                                if(restrictors[i]!=null) {
//                                    System.out.print(restrictors[i].find(1).getUTFString());
//                                    System.out.print("("+restrictors[i].find(2).find(13).getUTFString()+")");
//                                    System.out.print("     ");
//                                    restrictors[i]=restrictors[i].next();
//                                }
//                                else
//                                    System.out.print("           ");
//                            }
//                            System.out.println();
//                        }
//                    }

                    RecordsType records = searchResponse.getRecords();
                    RecordType record[];
                    if(records == null || (record = records.getRecord()) == null) {
                        System.out.println("0 records returned");
                    }
                    else {
                        System.out.println(record.length + " records returned");
                        MessageElement[]    elems;
                        Source              source;
                        String              recordStr;
                        StringOrXmlFragment frag;
                        StringWriter        sw;
                        for(i=0; i<record.length; i++) {
                            System.out.print("record number " + record[i].getRecordPosition());
                            frag=record[i].getRecordData();
                            elems=frag.get_any();
                            recordStr=elems[0].toString();
                            if(transformer!=null) {
                                source=new StreamSource(new StringReader(recordStr));
                                sw=new StringWriter();
                                try {
                                    transformer.transform(source, new StreamResult(sw));
                                    System.out.print(sw.toString());
                                }
                                catch(Exception e) {
                                    transformer.reset();
                                    e.printStackTrace();
                                }
                            }
                            else
                                System.out.println(recordStr);
                            transformer.reset();
                        }
                    }
                    System.out.println("nextRecordPosition=" + searchResponse.getNextRecordPosition());
                }
                else if(scan) {
                    System.out.print("operation=searchRetrieve");
                    scanRequest = new ScanRequestType();
                    System.out.print("&version=1.1");
                    scanRequest.setVersion("1.1");
                    System.out.println("&scanClause="+scanClause);
                    scanRequest.setScanClause(scanClause);
                    scanRequest.setMaximumTerms(new PositiveInteger(Integer.toString(maximumTerms)));
                    scanRequest.setResponsePosition(new NonNegativeInteger(Integer.toString(responsePosition)));
                    ScanResponseType scanResponse = db.doRequest(scanRequest);
                    if(scanResponse != null) {
                        TermsType terms = scanResponse.getTerms();
                        if(terms != null) {
                            TermType term[] = terms.getTerm();
                            System.out.println(term.length + " terms returned");
                            for(i = 0; i < term.length; i++)
                                System.out.println(term[i].getValue() + "(" + term[i].getNumberOfRecords().intValue() + ")");

                        } else
                            System.out.println("0 terms returned");
                    }
                    else
                        System.out.println("no scan response returned");
                    
                }
                if(!quit) {
                    System.out.print("\nenter request: ");
                    input=br.readLine();
                }
            }
//            searchRequest = new SearchRetrieveRequestType();
//            searchRequest.setVersion("1.1");
//            searchRequest.setQuery("(oai.datestamp>='00010101' and oai.datestamp<='99991231')");
//            searchRequest.setStartRecord(new PositiveInteger("1"));
//            searchRequest.setMaximumRecords(new NonNegativeInteger("1"));
//            searchRequest.setRecordPacking("string");
//            searchResponse = db.doRequest(searchRequest);
//            System.out.println("postings=" + searchResponse.getNumberOfRecords());
//            RecordsType records = searchResponse.getRecords();
//            RecordType record[];
//            if(records == null || (record = records.getRecord()) == null) {
//                System.out.println("0 records returned");
//            }
//            else {
//                System.out.println(record.length + " records returned");
//                System.out.println("record[0] has record number " + record[0].getRecordPosition());
//                StringOrXmlFragment frag = record[0].getRecordData();
//                org.apache.axis.message.MessageElement elems[] = frag.get_any();
//                List al=elems[0].getChildren();
//                System.out.println("elems[0].child[0]: "+al.get(0).toString());
//            }
//            System.out.println("nextRecordPosition=" + searchResponse.getNextRecordPosition());
            db.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String convertUnicodeSequence(String input) {
        char c, chars[]=input.toCharArray();
        StringBuffer sb=new StringBuffer();
        for(int i=0; i<chars.length; i++) {
            if(chars[i]=='\\' && i+5<chars.length && chars[i+1]=='u' &&
              isHexDigit(chars[i+2]) && isHexDigit(chars[i+3]) &&
              isHexDigit(chars[i+4]) && isHexDigit(chars[i+5])) {
                c=(char)(fromHexDigit(chars[i+2])*16*16*16+
                  fromHexDigit(chars[i+3])*16*16+
                  fromHexDigit(chars[i+4])*16+
		  fromHexDigit(chars[i+5]));
                if(sb.length()==0 && i>0)
                    sb.append(input.substring(0,i));
                sb.append(c);
                i+=5;
            }
            else if(sb.length()>0)
                sb.append(chars[i]);
        }
        if(sb.length()>0)
            return sb.toString();
        return input;
    }

    public static final int fromHexDigit(char c) throws IllegalArgumentException {
        if(c>='0' && c<='9')
            return c-'0';
        if(c>='a' && c<='f')
            return c-'a'+10;
        if(c>='A' && c<='F')
            return c-'A'+10;
        throw new IllegalArgumentException("Not a hex digit: "+c);
    }

    public static void help() {
        System.out.println("\nCommands are entered as SRU parameters (name=value pairs)");
        System.out.println("Multiple commands can be entered by separating them with ampersands");
        System.out.println("(e.g. recordSchema=XER&maximumRecords=10&startRecord=11&query=dog)");
        System.out.println("unrecognized commands are assumed to be queries");
        System.out.println("Currently, the recognized SRU parameters are:");
        System.out.println("\tmaximumRecords");
        System.out.println("\tmaximumTerms");
        System.out.println("\tquery");
        System.out.println("\trecordPacking");
        System.out.println("\trecordSchema");
        System.out.println("\tresponsePosition");
        System.out.println("\tresultSetTTL");
        System.out.println("\tscanClause");
        System.out.println("\tsortKeys");
        System.out.println("\tstartRecord");
        System.out.println("\tx-info-5-restrictorSummary[=false]");
        System.out.println("besides the SRU parameters, additional commands are:");
        System.out.println("\texplain");
        System.out.println("\thelp");
        System.out.println("\tquit (or q)");
        System.out.println("\txsl");
    }

    public static final boolean isHexDigit(char c) {
        if(c>='0' && c<='9')
            return true;
        if(c>='a' && c<='f')
            return true;
        if(c>='A' && c<='F')
            return true;
        return false;
    }

    public static Hashtable<String, String> parseInput(String input) {
        Hashtable<String, String> parms=new Hashtable<String, String>();
        int offset;
        String name, parm, value;
        StringTokenizer st=new StringTokenizer(input, "&");
        while(st.hasMoreTokens()) {
            parm=st.nextToken();
            offset=parm.indexOf('=');
            if(offset>0) {
                name=parm.substring(0, offset);
                if(offset<parm.length())
                    value=parm.substring(offset+1);
                else
                    value=null;
                parms.put(name, value);
            }
            else
                parms.put(parm, "");
        }
        return parms;
    }

    static void usage() {
        System.out.println("usage: java ORG.oclc.os.SRW.EmbeddedSRWDatabase [-p<SRWServer.props>] -d<dbname>");
    }
}