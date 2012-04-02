/*
 * ElementParserTest.java
 * JUnit based test
 *
 * Created on January 20, 2006, 3:25 PM
 */

package ORG.oclc.os.SRW;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author levan
 */
public class ElementParserTest extends TestCase {
    
    public ElementParserTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ElementParserTest.class);
        return suite;
    }

    /**
     * Test of getAttributes method, of class ORG.oclc.os.SRW.ElementParser.
     */
    public void testGetAttributes() {
        System.out.println("getAttributes");
        
        ElementParser ep = new ElementParser("<test attr=\"a\">1</test>");
        NameValuePair nvp=(NameValuePair)ep.nextElement();
        assertEquals("attr=\"a\"", ep.getAttributes());
        
        ep = new ElementParser("<test attr=\"a\"/>");
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("attr=\"a\"", ep.getAttributes());
        
        ep = new ElementParser("<test attr=\"a/b\">1</test>");
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("attr=\"a/b\"", ep.getAttributes());
        
        ep = new ElementParser("<test attr=\"a/b\"/>");
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("attr=\"a/b\"", ep.getAttributes());
        
        ep = new ElementParser("<test attr=\"a\">1</test><test attr=\"b\"/>");
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("attr=\"a\"", ep.getAttributes());
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("attr=\"b\"", ep.getAttributes());
        
        ep = new ElementParser("<test attr=\"a\"/><test attr=\"b\">2</test>");
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("attr=\"a\"", ep.getAttributes());
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("attr=\"b\"", ep.getAttributes());
        
        ep = new ElementParser("<test attr=\"a\">1</test><test attr=\"b\"/><test attr=\"c\">3</test>");
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("attr=\"a\"", ep.getAttributes());
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("attr=\"b\"", ep.getAttributes());
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("attr=\"c\"", ep.getAttributes());
    }

    /**
     * Test of hasMoreElements method, of class ORG.oclc.os.SRW.ElementParser.
     */
    public void testHasMoreElements() {
        System.out.println("hasMoreElements");
        
        ElementParser ep = new ElementParser("<test attr=\"a\">1</test>");
        assertTrue(ep.hasMoreElements());
        ep.nextElement();
        assertTrue(!ep.hasMoreElements());
        
        ep = new ElementParser("<test attr=\"a\"/>");
        assertTrue(ep.hasMoreElements());
        ep.nextElement();
        assertTrue(!ep.hasMoreElements());
        
        ep = new ElementParser("<test attr=\"a\">1</test><test attr=\"b\"/>");
        assertTrue(ep.hasMoreElements());
        ep.nextElement();
        assertTrue(ep.hasMoreElements());
        ep.nextElement();
        assertTrue(!ep.hasMoreElements());
        
        ep = new ElementParser("<test attr=\"a\"/><test attr=\"b\">2</test>");
        assertTrue(ep.hasMoreElements());
        ep.nextElement();
        assertTrue(ep.hasMoreElements());
        ep.nextElement();
        assertTrue(!ep.hasMoreElements());
        
        ep = new ElementParser("<test attr=\"a\">1</test><test attr=\"b\"/><test attr=\"c\">3</test>");
        assertTrue(ep.hasMoreElements());
        ep.nextElement();
        assertTrue(ep.hasMoreElements());
        ep.nextElement();
        assertTrue(ep.hasMoreElements());
        ep.nextElement();
        assertTrue(!ep.hasMoreElements());
    }

    /**
     * Test of nextElement method, of class ORG.oclc.os.SRW.ElementParser.
     */
    public void testNextElement() {
        System.out.println("nextElement");
        
        ElementParser ep = new ElementParser("<test attr=\"a\">1</test>");
        NameValuePair nvp=(NameValuePair)ep.nextElement();
        assertEquals("test", nvp.getName());
        assertEquals("1", nvp.getValue());

        ep = new ElementParser("<test attr=\"a\"/>");
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("test", nvp.getName());
        assertEquals("", nvp.getValue());
        
        ep = new ElementParser("<test1 attr=\"a\">1</test1><test2 attr=\"b\"/>");
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("test1", nvp.getName());
        assertEquals("1", nvp.getValue());
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("test2", nvp.getName());
        assertEquals("", nvp.getValue());
        
        ep = new ElementParser("<test1 attr=\"a\"/><test2 attr=\"b\">2</test2>");
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("test1", nvp.getName());
        assertEquals("", nvp.getValue());
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("test2", nvp.getName());
        assertEquals("2", nvp.getValue());
        
        ep = new ElementParser("<test1 attr=\"a\">1</test1><test2 attr=\"b\"/><test3 attr=\"c\">3</test3>");
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("test1", nvp.getName());
        assertEquals("1", nvp.getValue());
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("test2", nvp.getName());
        assertEquals("", nvp.getValue());
        nvp=(NameValuePair)ep.nextElement();
        assertEquals("test3", nvp.getName());
        assertEquals("3", nvp.getValue());
    }
}
