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
 * Utilities.java
 *
 * Created on October 17, 2005, 3:13 PM
 */

package ORG.oclc.os.SRW;

import com.Ostermiller.util.CGIParser;
import gov.loc.www.zing.srw.ScanRequestType;
import gov.loc.www.zing.srw.ScanResponseType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Enumeration;
import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.server.AxisServer;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.z3950.zing.cql.CQLBooleanNode;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLTermNode;

/**
 *
 * @author levan
 */
public class Utilities {
    static final Log log=LogFactory.getLog(Utilities.class);
    static final String newLine = System.getProperty("line.separator");

    public static String byteArrayToString(byte array[]) {
        return byteArrayToString(array, 0, array.length);
    }

    public static String byteArrayToString(byte array[], int offset, int length) {
        StringBuffer str = new StringBuffer();
        StringBuffer alpha = new StringBuffer();
        int stopat = length + offset;
        char c;
        int i, type;

        for (i=1; offset < stopat; offset++,i++) {
            if ((array[offset]&0xff)<16)
                str.append(" 0");
            else
                str.append(" ");
            str.append(Integer.toString(array[offset]&0xff,16));

            c = (char)array[offset];
            type = Character.getType(c);

            //      if (Character.isLetterOrDigit(c) || (c > )
            if (c<' ' || c>=0x7f)
                alpha.append('.');
            else
                alpha.append(c);


            if ((i%16)==0) {
                str.append("  " + alpha + newLine);
                alpha.setLength(0);
            }
        }
        while(i++%16!=1)
            str.append("   ");
        offset = 0;

        str.append("  " + alpha + newLine);
        str.append(newLine);

        return str.toString();
    }


    public static String escapeBackslash(String s) {
        boolean      changed=false;
        char         c;
        StringBuffer sb=null;
        for(int i=0; i<s.length(); i++) {
            c=s.charAt(i);
            if(c=='\\') {
                if(!changed) {
                    if(i>0)
                        sb=new StringBuffer(s.substring(0, i));
                    else
                        sb=new StringBuffer();
                    changed=true;
                }
                sb.append("\\\\");
            }
            else
                if(changed)
                    sb.append(c);
        }
        if(!changed)
            return s;
        return sb.toString();
    }


    public static File findFile(final String fileName, String directory1,
      String directory2) {
        // try the current directory
        File f=new File(fileName);
        if(f.exists())
            return f;

        if(directory1!=null) {
            if(directory1.endsWith("/"))
                f=new File(directory1+fileName);
            else
                f=new File(directory1+"/"+fileName);
            if(f.exists())
                return f;
        }

        if(directory2!=null) {
            if(directory2.endsWith("/"))
                f=new File(directory2+fileName);
            else
                f=new File(directory2+"/"+fileName);
            if(f.exists())
                return f;
        }

        // finally, let's see if we can find it on the classpath
        URL url=Thread.currentThread().getContextClassLoader().getResource(fileName);
        if(url!=null) {
            f=new File(unUrlEncode(url.getFile()));
            if(f.exists())
                return f;
        }

        log.error("Couldn't find \""+fileName+"\" in the CWD or in \""+
            directory1+"\" or \""+directory2+"\" or on the classpath");
        log.error("classpath: "+System.getProperty("java.class.path"));
        return null;
    }

    
    public static CQLTermNode getFirstTerm(CQLNode node) {
        if(node instanceof CQLTermNode)
            return (CQLTermNode)node;
        if(node instanceof CQLBooleanNode)
            return getFirstTerm(((CQLBooleanNode)node).left);
        log.error("processing node of type: "+node);
        return null;
    }

    public static String hex07Encode(String s) {
        boolean      changed=false;
        char         c;
        StringBuffer sb=null;
        for(int i=0; i<s.length(); i++) {
            c=s.charAt(i);
            if(c<0xa) {
                if(!changed) {
                    if(i>0)
                        sb=new StringBuffer(s.substring(0, i));
                    else
                        sb=new StringBuffer();
                    changed=true;
                }
                sb.append("&#x").append(Integer.toHexString(c)).append(';');
            }
            else
                if(changed)
                    sb.append(c);
        }
        if(!changed)
            return s;
        return sb.toString();
    }

    public static String objToSru(Object obj) {
        StringBuffer sru=new StringBuffer();
        if(obj instanceof SearchRetrieveRequestType) {
            SearchRetrieveRequestType request=(SearchRetrieveRequestType)obj;
            sru.append("operation=searchRetrieve");
            if(request.getMaximumRecords()!=null)
                sru.append('&').append("maximumRecords=").append(request.getMaximumRecords().toString());
            if(request.getQuery()!=null)
                sru.append('&').append("query=").append(urlEncode(request.getQuery()));
            if(request.getRecordPacking()!=null)
                sru.append('&').append("recordPacking=").append(request.getRecordPacking());
            if(request.getRecordSchema()!=null)
                sru.append('&').append("recordSchema=").append(request.getRecordSchema());
            if(request.getRecordXPath()!=null)
                sru.append('&').append("recordXPath=").append(request.getRecordXPath());
            if(request.getResultSetTTL()!=null)
                sru.append('&').append("resultSetTTL=").append(request.getResultSetTTL());
            if(request.getSortKeys()!=null)
                sru.append('&').append("sortKeys=").append(request.getSortKeys());
            if(request.getStartRecord()!=null)
                sru.append('&').append("startRecord=").append(request.getStartRecord().toString());
            if(request.getVersion()!=null)
                sru.append('&').append("version=").append(request.getVersion());
        }
        else if(obj instanceof ScanRequestType) {
            ScanRequestType request=(ScanRequestType)obj;
            sru.append("operation=scan");
            if(request.getMaximumTerms()!=null)
                sru.append('&').append("maximumTerms=").append(request.getMaximumTerms().toString());
            if(request.getResponsePosition()!=null)
                sru.append('&').append("responsePosition=").append(request.getResponsePosition().toString());
            if(request.getScanClause()!=null)
                sru.append('&').append("scanClause=\"").append(urlEncode(request.getScanClause())).append('"');
            if(request.getVersion()!=null)
                sru.append('&').append("version=").append(request.getVersion());
        }
        else throw new IllegalArgumentException(
            "Unrecognized object: "+obj.getClass().getName());
        return sru.toString();
    }

    public static String objToXml(Object obj)
      throws IllegalArgumentException, IOException {
        QName qn;
        Serializer s;
        if(obj instanceof  SearchRetrieveRequestType) {
            qn=new QName("http://www.loc.gov/zing/srw/", "searchRetrieveRequest"); 
            s=SearchRetrieveRequestType.getSerializer(
                null, obj.getClass(), qn);
        }
        else if(obj instanceof  SearchRetrieveResponseType) {
            qn=new QName("http://www.loc.gov/zing/srw/", "searchRetrieveResponse"); 
            s=SearchRetrieveResponseType.getSerializer(
                null, obj.getClass(), qn);
        }
        else if(obj instanceof  ScanRequestType) {
            qn=new QName("http://www.loc.gov/zing/srw/", "scanRequest"); 
            s=ScanRequestType.getSerializer(
                null, obj.getClass(), qn);
        }
        else if(obj instanceof  ScanResponseType) {
            qn=new QName("http://www.loc.gov/zing/srw/", "scanResponse"); 
            s=ScanResponseType.getSerializer(
                null, obj.getClass(), qn);
        }
        else throw new IllegalArgumentException(
            "Unrecognized object: "+obj.getClass().getName());

        StringWriter xmlWriter=new StringWriter(); 
        s.serialize(qn, null, obj, new SerializationContext(xmlWriter));
        return xmlWriter.toString();
    }

    public static InputStream openInputStream(final String fileName,
      String directory1, String directory2) throws FileNotFoundException {
        File f=findFile(fileName, directory1, directory2);
        if(f==null) {
            throw new FileNotFoundException(fileName);
        }
        try {
            return new FileInputStream(f);
        }
        catch(Exception e) {
            log.error(e, e);
        }
        throw new FileNotFoundException(fileName);
    }

    private static ScanRequestType sruScanToObj(CGIParser parser) {
        Enumeration enumer=parser.getParameterNames();
        ScanRequestType request=new ScanRequestType();
        String name, value;
        StringBuffer extraData=new StringBuffer();
        while(enumer.hasMoreElements()) {
            name=(String)enumer.nextElement();
            if(name.equals("maximumTerms"))
                request.setMaximumTerms(new PositiveInteger(parser.getParameter(name)));
            else if(name.equals("operation"))
                {}
            else if(name.equals("responsePosition"))
                request.setResponsePosition(new NonNegativeInteger(parser.getParameter(name)));
            else if(name.equals("scanClause"))
                request.setScanClause(parser.getParameter(name));
            else if(name.equals("version"))
                request.setVersion(parser.getParameter(name));
            else // must be extraData
                extraData.append('<').append(name).append('>')
                         .append(parser.getParameter(name))
                         .append("</").append(name).append('>');
        }
        if(extraData.length()>0)
            request.setExtraRequestData(SRWDatabase.makeExtraRequestDataType(extraData.toString()));
        return request;
    }

    private static SearchRetrieveRequestType sruSearchToObj(CGIParser parser) {
        Enumeration enumer=parser.getParameterNames();
        SearchRetrieveRequestType request=new SearchRetrieveRequestType();
        String name, value;
        StringBuffer extraData=new StringBuffer();
        while(enumer.hasMoreElements()) {
            name=(String)enumer.nextElement();
            if(name.equals("maximumRecords"))
                request.setMaximumRecords(new NonNegativeInteger(parser.getParameter(name)));
            else if(name.equals("operation"))
                {}
            else if(name.equals("query"))
                request.setQuery(parser.getParameter(name));
            else if(name.equals("recordPacking"))
                request.setRecordPacking(parser.getParameter(name));
            else if(name.equals("recordSchema"))
                request.setRecordSchema(parser.getParameter(name));
            else if(name.equals("recordXPath"))
                request.setRecordXPath(parser.getParameter(name));
            else if(name.equals("resultSetTTL"))
                request.setResultSetTTL(new NonNegativeInteger(parser.getParameter(name)));
            else if(name.equals("sortKeys"))
                request.setSortKeys(parser.getParameter(name));
            else if(name.equals("startRecord"))
                request.setStartRecord(new PositiveInteger(parser.getParameter(name)));
            else if(name.equals("version"))
                request.setVersion(parser.getParameter(name));
            else // must be extraData
                extraData.append('<').append(name).append('>')
                         .append(parser.getParameter(name))
                         .append("</").append(name).append('>');
        }
        if(extraData.length()>0)
            request.setExtraRequestData(SRWDatabase.makeExtraRequestDataType(extraData.toString()));
        return request;
    }

    public static Object sruToObj(String sruRequest) throws IOException {
        CGIParser parser=new CGIParser(sruRequest, "UTF-8");
        if(parser.getParameter("query")!=null)
            return sruSearchToObj(parser);
        if(parser.getParameter("scanClause")!=null)
            return sruScanToObj(parser);
        throw new IllegalArgumentException("SRU requests must contain either a 'query' or 'scanTerm' parameter");
    }

    public String sruToXml(String sruRequest) throws IOException {
        return(objToXml(sruToObj(sruRequest)));
    }

    public static String readURL(String urlStr) {
        return readURL(urlStr, false);
    }

    public static String readURL(String urlStr, boolean debug) {
        if(debug)
            System.out.print("    trying: "+urlStr+"\n");
        URL url=null;
        try {
            url=new URL(urlStr);
        }
        catch(java.net.MalformedURLException e) {
            System.out.print("test failed: using URL: ");System.out.print(e.getMessage());System.out.print('\n');
            return null;
        }
        HttpURLConnection huc=null;
        try {
            huc=(HttpURLConnection)url.openConnection();
        }
        catch(IOException e) {
            System.out.print("test failed: using URL: ");System.out.print(e.getMessage());System.out.print('\n');
            return null;
        }
        String contentType=huc.getContentType();
        if(contentType==null || contentType.indexOf("text/xml")<0) {
            System.out.print("*** Warning ***  Content-Type not set to text/xml");System.out.print('\n');
            System.out.print("    Content-type: ");System.out.print(contentType);System.out.print('\n');
        }
        InputStream urlStream=null;
        try {
            urlStream=huc.getInputStream();
        }
        catch(java.io.IOException e) {
            e.printStackTrace();
            System.out.print("test failed: opening URL: ");System.out.print(e.getMessage());System.out.print('\n');
            return null;
        }
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                urlStream));
        boolean xml=true;
        String href=null, inputLine=null;
        StringBuffer content=new StringBuffer(), stylesheet=null;
        Transformer transformer=null;
        try {
            inputLine=in.readLine();
        }
        catch(java.io.IOException e) {
            System.out.print("test failed: reading first line of response: ");System.out.print(e.getMessage());System.out.print('\n');
            return null;
        }
        if(inputLine==null) {
            System.out.print("test failed: No input read from URL");System.out.print('\n');
            return null;
        }
        if(!inputLine.startsWith("<?xml ")) {
            xml=false;
            content.append(inputLine);
        }

        if(xml) {
            // normally, you'd expect to read the next line of input here
            // but some servers don't put a newline after the initial <?xml ?>
            int offset=inputLine.indexOf('>');
            if(offset+2<inputLine.length()) {
                inputLine=inputLine.substring(offset+1);
                offset=inputLine.indexOf('<');
                if(offset>0)
                    inputLine=inputLine.substring(offset);
            }
            else try {
                inputLine=in.readLine();
            }
            catch(java.io.IOException e) {
                System.out.print("test failed: reading response: ");System.out.print(e.getMessage());System.out.print('\n');
                return null;
            }

            content.append(inputLine);
        }

        try {
            while ((inputLine = in.readLine()) != null)
                content.append(inputLine);
        }
        catch(java.io.IOException e) {
            System.out.print("test failed: reading response: ");System.out.print(e.getMessage());System.out.print('\n');
            return null;
        }

        String contentStr=content.toString();
        if(transformer!=null) {
            StreamSource streamXMLRecord=new StreamSource(new StringReader(contentStr));
            StringWriter xmlRecordWriter=new StringWriter();
            try {
                transformer.transform(streamXMLRecord,
                    new StreamResult(xmlRecordWriter));
                System.out.print("        successfully applied stylesheet '");System.out.print(href);System.out.print("'");System.out.print('\n');
            }
            catch(javax.xml.transform.TransformerException e) {
                System.out.print("unable to apply stylesheet '");System.out.print(href);System.out.print("'to response: ");System.out.print(e.getMessage());System.out.print('\n');
                e.printStackTrace();
            }
        }
        return contentStr;
    }

    public static String unUrlEncode(String s) {
        boolean      changed=false;
        char         c;
        StringBuffer sb=null;
        for(int i=0; i<s.length(); i++) {
            c=s.charAt(i);
            if(c=='+') {
                if(!changed) {
                    if(i>0)
                        sb=new StringBuffer(s.substring(0, i));
                    else
                        sb=new StringBuffer();
                    changed=true;
                }
                sb.append(' ');
            }
            else if(c=='%') {
                if(!changed) {
                    if(i>0)
                        sb=new StringBuffer(s.substring(0, i));
                    else
                        sb=new StringBuffer();
                    changed=true;
                }
                sb.append((char)Integer.parseInt(s.substring(i+1, i+3), 16));
                i+=2;
            }
            else
                if(changed)
                    sb.append(c);
        }
        if(!changed)
            return s;
        return sb.toString();
    }
 

    public static String unXmlEncode(String s) {
        boolean      changed=false;
        char         c, c1;
        StringBuffer sb=null;
        for(int i=0; i<s.length(); i++) {
            c=s.charAt(i);
            if(c=='&') {
                c1=s.charAt(i+1);
                switch(c1) {
                    case '#':
                        if(s.length()>i+2 && s.charAt(i+2)=='x') {
                            if(!changed) {
                                if(i>0)
                                    sb=new StringBuffer(s.substring(0, i));
                                else
                                    sb=new StringBuffer();
                                changed=true;
                            }
                            sb.append((char)Integer.parseInt(s.substring(i+3, i+5), 16));
                            i+=5;
                        }
                        break;
                    case 'a':
                        if(s.length()>i+5 && s.charAt(i+2)=='p' && s.charAt(i+3)=='o' &&
                           s.charAt(i+4)=='s' && s.charAt(i+5)==';') {
                            if(!changed) {
                                if(i>0)
                                    sb=new StringBuffer(s.substring(0, i));
                                else
                                    sb=new StringBuffer();
                                changed=true;
                            }
                            sb.append('\'');
                            i+=5;
                        }
                        else if(s.length()>i+3 && s.charAt(i+2)=='m' && s.charAt(i+3)=='p' &&
                          s.charAt(i+4)==';') {
                            if(!changed) {
                                if(i>0)
                                    sb=new StringBuffer(s.substring(0, i));
                                else
                                    sb=new StringBuffer();
                                changed=true;
                            }
                            sb.append('&');
                            i+=5;
                        }
                        break;
                    case 'g':
                        if(s.length()>i+3 && s.charAt(i+2)=='t' && s.charAt(i+3)==';') {
                            if(!changed) {
                                if(i>0)
                                    sb=new StringBuffer(s.substring(0, i));
                                else
                                    sb=new StringBuffer();
                                changed=true;
                            }
                            sb.append('>');
                            i+=3;
                        }
                        break;
                    case 'l':
                        if(s.length()>i+3 && s.charAt(i+2)=='t' && s.charAt(i+3)==';') {
                            if(!changed) {
                                if(i>0)
                                    sb=new StringBuffer(s.substring(0, i));
                                else
                                    sb=new StringBuffer();
                                changed=true;
                            }
                            sb.append('<');
                            i+=3;
                        }
                        break;
                    case 'q':
                        if(s.length()>i+5 && s.charAt(i+2)=='u' && s.charAt(i+3)=='o' &&
                           s.charAt(i+4)=='t' && s.charAt(i+5)==';') {
                            if(!changed) {
                                if(i>0)
                                    sb=new StringBuffer(s.substring(0, i));
                                else
                                    sb=new StringBuffer();
                                changed=true;
                            }
                            sb.append('"');
                            i+=5;
                        }
                        break;
                    default:
                        if(changed)
                            sb.append(c);
                }
            }
            else
                if(changed)
                    sb.append(c);
        }
        if(changed)
            return sb.toString();
        return s;
    }


    public static String urlEncode(String s) {
        boolean      changed=false;
        char         c;
        StringBuffer sb=null;
        for(int i=0; i<s.length(); i++) {
            c=s.charAt(i);
            if(c==' ' || c=='+' || c=='<' || c=='&' || c=='>' || c=='"' ||
               c=='\'' || c=='#' || c>0x7f) {
                if(!changed) {
                    if(i>0)
                        sb=new StringBuffer(s.substring(0, i));
                    else
                        sb=new StringBuffer();
                    changed=true;
                }
                sb.append('%').append(Integer.toHexString(c));
            }
            else
                if(changed)
                    sb.append(c);
        }
        if(!changed)
            return s;
        return sb.toString();
    }
 

    /**
     * like urlEncode, but adds the equal sign
     * @param s the url parameter to encode
     * @return the encoded parameter
     */
    public static String urlParameterEncode(String s) {
        boolean      changed=false;
        char         c;
        StringBuffer sb=null;
        for(int i=0; i<s.length(); i++) {
            c=s.charAt(i);
            if(c==' ' || c=='+' || c=='<' || c=='&' || c=='>' || c=='"' ||
               c=='\'' || c=='=' || c>0x7f) {
                if(!changed) {
                    if(i>0)
                        sb=new StringBuffer(s.substring(0, i));
                    else
                        sb=new StringBuffer();
                    changed=true;
                }
                sb.append('%').append(Integer.toHexString(c));
            }
            else
                if(changed)
                    sb.append(c);
        }
        if(!changed)
            return s;
        return sb.toString();
    }
 

    public static void writeEncoded(java.io.Writer writer, String xmlString)
            throws java.io.IOException {
        if (xmlString == null) {
            return;
        }
        char[] characters = xmlString.toCharArray();
        char character;
        for (int i = 0; i < characters.length; i++) {
            character = characters[i];
            switch (character) {
                // we don't care about single quotes since axis will
                // use double quotes anyway
                case '&':
                case '"':
                case '<':
                case '>':
                case '\n':
                case '\r':
                case '\t':
                    writer.write("&#x");
                    writer.write(Integer.toHexString(character).toUpperCase());
                    writer.write(";");
                    break;
                default:
                    if (character < 0x20) {
                        throw new IllegalArgumentException(
                          "Invalid Xml Character 00"+
                          Integer.toHexString(character));
                    } else if (character > 0x7F) {
                        writer.write("&#x");
                        writer.write(Integer.toHexString(character).toUpperCase());
                        writer.write(";");
//                        TODO: Try fixing this block instead of code above.
//                        if (character < 0x80) {
//                            writer.write(character);
//                        } else if (character < 0x800) {
//                            writer.write((0xC0 | character >> 6));
//                            writer.write((0x80 | character & 0x3F));
//                        } else if (character < 0x10000) {
//                            writer.write((0xE0 | character >> 12));
//                            writer.write((0x80 | character >> 6 & 0x3F));
//                            writer.write((0x80 | character & 0x3F));
//                        } else if (character < 0x200000) {
//                            writer.write((0xF0 | character >> 18));
//                            writer.write((0x80 | character >> 12 & 0x3F));
//                            writer.write((0x80 | character >> 6 & 0x3F));
//                            writer.write((0x80 | character & 0x3F));
//                        }
                    } else {
                        writer.write(character);
                    }
                    break;
            }
        }
    }


    public static String xmlEncode(String s) {
        boolean      changed=false;
        char         c;
        StringBuffer sb=null;
        for(int i=0; i<s.length(); i++) {
            c=s.charAt(i);
            if(c<0xa) {
                if(!changed) {
                    if(i>0)
                        sb=new StringBuffer(s.substring(0, i));
                    else
                        sb=new StringBuffer();
                    changed=true;
                }
                sb.append("&#x").append(Integer.toHexString(c)).append(';');
            }
            else if(c=='<') {
                if(!changed) {
                    if(i>0)
                        sb=new StringBuffer(s.substring(0, i));
                    else
                        sb=new StringBuffer();
                    changed=true;
                }
                sb.append("&lt;");
            }
            else if(c=='>') {
                if(!changed) {
                    if(i>0)
                        sb=new StringBuffer(s.substring(0, i));
                    else
                        sb=new StringBuffer();
                    changed=true;
                }
                sb.append("&gt;");
            }
            else if(c=='"') {
                if(!changed) {
                    if(i>0)
                        sb=new StringBuffer(s.substring(0, i));
                    else
                        sb=new StringBuffer();
                    changed=true;
                }
                sb.append("&quot;");
            }
            else if(c=='&') {
                if(!changed) {
                    if(i>0)
                        sb=new StringBuffer(s.substring(0, i));
                    else
                        sb=new StringBuffer();
                    changed=true;
                }
                sb.append("&amp;");
            }
            else  if(c=='\'') {
                if(!changed) {
                    if(i>0)
                        sb=new StringBuffer(s.substring(0, i));
                    else
                        sb=new StringBuffer();
                    changed=true;
                }
                sb.append("&apos;");
            }
            else
                if(changed)
                    sb.append(c);
        }
        if(!changed)
            return s;
        return sb.toString();
    }

    static String EnvelopeStart="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Body>";
    static String EnvelopeEnd="</soapenv:Body></soapenv:Envelope>";
    public static Object xmlToObj(String xml) throws ParseException {
        log.debug(xml);
        Object obj;
        String leader, soapMessage;
        while(xml.startsWith("<?")) {
            // contains processing instruction.  Strip that off
            int i=xml.indexOf('<', 2);
            if(i<0) {
                log.error(xml);
                throw new ParseException("XML starts with processing instruction but contains no elements", 0);
            }
            xml=xml.substring(i);
        }

        soapMessage=xml; // use it as it is
        leader=xml.substring(0, 20);
         if(!leader.contains("Envelope"))
            soapMessage=EnvelopeStart+soapMessage+EnvelopeEnd;
        DeserializationContext dser = new DeserializationContext(
            new InputSource(new StringReader(soapMessage)),
            new MessageContext(new AxisServer()),
            org.apache.axis.Message.RESPONSE);
        try {
            dser.parse();

            SOAPEnvelope env = dser.getEnvelope();
            RPCElement rpcElem = (RPCElement)env.getFirstBody();
            String objectType=rpcElem.getLocalName();
            if(objectType.equals("searchRetrieveRequest"))
                obj=rpcElem.getObjectValue(SearchRetrieveRequestType.class);
            else if(objectType.equals("searchRetrieveResponse"))
                obj=rpcElem.getObjectValue(SearchRetrieveResponseType.class);
            else if(objectType.equals("scanRequest"))
                obj=rpcElem.getObjectValue(ScanRequestType.class);
            else if(objectType.equals("scanResponse"))
                obj=rpcElem.getObjectValue(ScanResponseType.class);
            else
                throw new ParseException("Unrecognized XML object: "+objectType, 0);
        }
        catch(Exception e) {
            log.error(e, e);
            throw new ParseException(e.getMessage(), 0);
        }

        return obj;
    }

    public static String xmlToSru(String xml) throws Exception {
        return objToSru(xmlToObj(xml));
    }
}
