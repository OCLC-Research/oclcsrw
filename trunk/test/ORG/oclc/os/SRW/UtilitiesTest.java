/*
 * UtilitiesTest.java
 * JUnit based test
 *
 * Created on June 7, 2007, 2:14 PM
 */

package ORG.oclc.os.SRW;

import junit.framework.*;
import com.Ostermiller.util.CGIParser;
import gov.loc.www.zing.srw.ExtraDataType;
import gov.loc.www.zing.srw.ScanRequestType;
import gov.loc.www.zing.srw.ScanResponseType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import javax.xml.namespace.QName;
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
import org.z3950.zing.cql.CQLParser;
import org.z3950.zing.cql.CQLTermNode;

/**
 *
 * @author levan
 */
public class UtilitiesTest extends TestCase {
    static final String newLine = System.getProperty("line.separator");
    
    public UtilitiesTest(String testName) {
        super(testName);
    }

    /**
     * Test of byteArrayToString method, of class ORG.oclc.os.SRW.Utilities.
     */
    public void testByteArrayToString() throws Exception {
        String source="abcdefghijklmnopqrstuvwxyz\u1234\n";
//        System.out.println("testByteArrayToString:\n"+
//            Utilities.byteArrayToString(source.getBytes("UTF-8")));
        String result = Utilities.byteArrayToString(source.getBytes("UTF-8"));
        assertEquals(" 61 62 63 64 65 66 67 68 69 6a 6b 6c 6d 6e 6f 70  abcdefghijklmnop"+newLine+
                     " 71 72 73 74 75 76 77 78 79 7a e1 88 b4 0a        qrstuvwxyz...."+newLine+newLine, result);
    }

    /**
     * Test of escapeBackslash method, of class ORG.oclc.os.SRW.Utilities.
     */
    public void testEscapeBackslash() {
        String source="\\a\\b\\";
        assertEquals("\\\\a\\\\b\\\\", Utilities.escapeBackslash(source));
    }

    /**
     * Test of findFile method, of class ORG.oclc.os.SRW.Utilities.
     */
    public void testFindFile() {
        File result = Utilities.findFile("ORG/oclc/os/SRW/Utilities.class", null, null);
        assertNotNull(result);
        assertTrue(result.exists());
    }

    /**
     * Test of getFirstTerm method, of class ORG.oclc.os.SRW.Utilities.
     */
    public void testGetFirstTerm() throws Exception {
        CQLParser parser = new CQLParser();        
        CQLNode rootNode=parser.parse("dog");
        assertEquals("dog", Utilities.getFirstTerm(rootNode).getTerm());

        rootNode=parser.parse("dog or cat and mouse");
        assertEquals("dog", Utilities.getFirstTerm(rootNode).getTerm());
    }

    /**
     * Test of hex07Encode method, of class ORG.oclc.os.SRW.Utilities.
     */
    public void testHex07Encode() {
        assertEquals("a", Utilities.hex07Encode("a"));
        assertEquals("a&#x7;b", Utilities.hex07Encode("a\u0007b"));
    }

    /**
     * Test of objToSru method, of class ORG.oclc.os.SRW.Utilities.
     */
    public void testObjToSru() {
        SearchRetrieveRequestType srreq=new SearchRetrieveRequestType();
        srreq.setQuery("dog or cat and mouse");
        System.out.println("objToSru: "+Utilities.objToSru(srreq));
        assertEquals("operation=searchRetrieve&query=\"dog+or+cat+and+mouse\"", Utilities.objToSru(srreq));
        srreq.setResultSetTTL(new NonNegativeInteger("123"));
        assertEquals("operation=searchRetrieve&query=\"dog+or+cat+and+mouse\"&resultSetTTL=123", Utilities.objToSru(srreq));
        
        ScanRequestType sreq=new ScanRequestType();
        sreq.setScanClause("dog");
        System.out.println("objToSru: "+Utilities.objToSru(sreq));
        assertEquals("operation=scan&scanClause=\"dog\"", Utilities.objToSru(sreq));
        sreq.setMaximumTerms(new PositiveInteger("20"));
        sreq.setResponsePosition(new NonNegativeInteger("10"));
        assertEquals("operation=scan&maximumTerms=20&responsePosition=10&scanClause=\"dog\"", Utilities.objToSru(sreq));
        
        try {
            Utilities.objToSru(new SearchRetrieveResponseType());
            fail("should have thrown an IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
        }

        try {
            Utilities.objToSru(new ScanResponseType());
            fail("should have thrown an IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
        }

        try {
            Utilities.objToSru(this);
            fail("should have thrown an IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
        }
    }

    /**
     * Test of objToXml method, of class ORG.oclc.os.SRW.Utilities.
     */
    public void testObjToXml() throws Exception {
        SearchRetrieveRequestType srreq=new SearchRetrieveRequestType();
        srreq.setQuery("dog or cat and mouse");
        srreq.setVersion("1.1");
//        System.out.println("objToXml: "+Utilities.objToXml(srreq));
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns1:searchRetrieveRequest xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version><query xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">dog or cat and mouse</query><startRecord xsi:type=\"ns1:startRecord\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><maximumRecords xsi:type=\"ns1:maximumRecords\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><recordPacking xsi:type=\"ns1:recordPacking\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><recordSchema xsi:type=\"ns1:recordSchema\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><recordXPath xsi:type=\"ns1:recordXPath\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><resultSetTTL xsi:type=\"ns1:resultSetTTL\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><sortKeys xsi:type=\"ns1:sortKeys\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><stylesheet xsi:type=\"ns1:stylesheet\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><extraRequestData xsi:type=\"ns1:extraRequestData\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:searchRetrieveRequest>", Utilities.objToXml(srreq));
        srreq.setResultSetTTL(new NonNegativeInteger("123"));
//        System.out.println("objToXml: "+Utilities.objToXml(srreq));
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns1:searchRetrieveRequest xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version><query xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">dog or cat and mouse</query><startRecord xsi:type=\"ns1:startRecord\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><maximumRecords xsi:type=\"ns1:maximumRecords\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><recordPacking xsi:type=\"ns1:recordPacking\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><recordSchema xsi:type=\"ns1:recordSchema\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><recordXPath xsi:type=\"ns1:recordXPath\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><resultSetTTL xsi:type=\"xsd:nonNegativeInteger\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">123</resultSetTTL><sortKeys xsi:type=\"ns1:sortKeys\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><stylesheet xsi:type=\"ns1:stylesheet\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><extraRequestData xsi:type=\"ns1:extraRequestData\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:searchRetrieveRequest>", Utilities.objToXml(srreq));
        
        ScanRequestType sreq=new ScanRequestType();
        sreq.setScanClause("dog");
        sreq.setVersion("1.1");
//        System.out.println("objToXml: "+Utilities.objToXml(sreq));
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns1:scanRequest xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version><scanClause xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">dog</scanClause><responsePosition xsi:type=\"ns1:responsePosition\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><maximumTerms xsi:type=\"ns1:maximumTerms\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><stylesheet xsi:type=\"ns1:stylesheet\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><extraRequestData xsi:type=\"ns1:extraRequestData\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:scanRequest>", Utilities.objToXml(sreq));
        sreq.setMaximumTerms(new PositiveInteger("20"));
        sreq.setResponsePosition(new NonNegativeInteger("10"));
//        System.out.println("objToXml: "+Utilities.objToXml(sreq));
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns1:scanRequest xmlns:ns1=\"http://www.loc.gov/zing/srw/\"><version xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">1.1</version><scanClause xsi:type=\"xsd:string\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">dog</scanClause><responsePosition xsi:type=\"xsd:nonNegativeInteger\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">10</responsePosition><maximumTerms xsi:type=\"xsd:positiveInteger\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">20</maximumTerms><stylesheet xsi:type=\"ns1:stylesheet\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/><extraRequestData xsi:type=\"ns1:extraRequestData\" xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></ns1:scanRequest>", Utilities.objToXml(sreq));
        
        SearchRetrieveResponseType srresp=new SearchRetrieveResponseType();
        srresp.setVersion("1.1");
        Utilities.objToXml(srresp);

        ScanResponseType sresp=new ScanResponseType();
        sresp.setVersion("1.1");
        Utilities.objToXml(sresp);

        try {
            Utilities.objToXml(this);
            fail("should have thrown an IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
        }
    }

    /**
     * Test of openInputStream method, of class ORG.oclc.os.SRW.Utilities.
     */
    public void testOpenInputStream() throws Exception {
        System.out.println("openInputStream");
        
        String fileName = "";
        String directory1 = "";
        String directory2 = "";
        
        InputStream expResult = null;
        InputStream result = Utilities.openInputStream(fileName, directory1, directory2);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sruToObj method, of class ORG.oclc.os.SRW.Utilities.
     */
    public void testSruToObj() throws Exception {
        System.out.println("sruToObj");
        
        String sruRequest = "";
        Utilities instance = new Utilities();
        
        Object expResult = null;
        Object result = instance.sruToObj(sruRequest);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sruToXml method, of class ORG.oclc.os.SRW.Utilities.
     */
    public void testSruToXml() throws Exception {
        System.out.println("sruToXml");
        
        String sruRequest = "";
        Utilities instance = new Utilities();
        
        String expResult = "";
        String result = instance.sruToXml(sruRequest);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of unUrlEncode method, of class ORG.oclc.os.SRW.Utilities.
     */
    public void testUnUrlEncode() {
        System.out.println("unUrlEncode");
        
        String s = "";
        
        String expResult = "";
        String result = Utilities.unUrlEncode(s);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of unXmlEncode method, of class ORG.oclc.os.SRW.Utilities.
     */
    public void testUnXmlEncode() {
        System.out.println("unXmlEncode");
        
        String s = "";
        
        String expResult = "";
        String result = Utilities.unXmlEncode(s);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of urlEncode method, of class ORG.oclc.os.SRW.Utilities.
     */
    public void testUrlEncode() {
        System.out.println("urlEncode");
        
        String s = "";
        
        String expResult = "";
        String result = Utilities.urlEncode(s);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of writeEncoded method, of class ORG.oclc.os.SRW.Utilities.
     */
    public void testWriteEncoded() throws Exception {
        System.out.println("writeEncoded");
        
        java.io.Writer writer = null;
        String xmlString = "";
        
        Utilities.writeEncoded(writer, xmlString);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of xmlEncode method, of class ORG.oclc.os.SRW.Utilities.
     */
    public void testXmlEncode() {
        System.out.println("xmlEncode");
        
        String s = "";
        
        String expResult = "";
        String result = Utilities.xmlEncode(s);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of xmlToObj method, of class ORG.oclc.os.SRW.Utilities.
     */
    public void testXmlToObj() throws Exception {
        System.out.println("xmlToObj");
        
        String xml = "";
        
        Object expResult = null;
        Object result = Utilities.xmlToObj(xml);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of xmlToSru method, of class ORG.oclc.os.SRW.Utilities.
     */
    public void testXmlToSru() throws Exception {
        System.out.println("xmlToSru");
        
        String xml = "";
        
        String expResult = "";
        String result = Utilities.xmlToSru(xml);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
