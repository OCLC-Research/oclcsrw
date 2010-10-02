/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fuberlin.wiwiss.pubby.negotiation;

import junit.framework.TestCase;

/**
 *
 * @author levan
 */
public class ContentTypeNegotiatorTest extends TestCase {
    
    public ContentTypeNegotiatorTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of addVariant method, of class ContentTypeNegotiator.
     */
    public void testAddVariant() {
        System.out.println("addVariant");
        ContentTypeNegotiator instance = new ContentTypeNegotiator();
        MediaRangeSpec mrs=instance.getBestMatch("*/*");
        assertNull(mrs);
        instance.addVariant("text/html;q=0.9")
				.addAliasMediaType("application/xhtml+xml");
        instance.addVariant("text/xml;q=0.95");
        mrs=instance.getBestMatch("*/*");
        System.out.println(mrs.getParameterNames());
        assertEquals("text/xml", mrs.getMediaType());
    }

    /**
     * Test of addVariant method, of class ContentTypeNegotiator.
     */
    public void testYetAnotherAddVariant() {
        System.out.println("yetAnotherAddVariant");
        ContentTypeNegotiator instance = new ContentTypeNegotiator();
        instance.addVariant("text/html;q=0.8")
				.addAliasMediaType("application/xhtml+xml");
        instance.addVariant("application/sru+xml;q=0.9").addAliasMediaType("application/xml");
        MediaRangeSpec mrs=instance.getBestMatch("application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
        System.out.println(mrs.getParameterNames());
        assertEquals("application/sru+xml", mrs.getMediaType());
    }

    /**
     * Test of setDefaultAccept method, of class ContentTypeNegotiator.
     */
    public void testSetDefaultAccept() {
        System.out.println("setDefaultAccept");
        ContentTypeNegotiator instance = new ContentTypeNegotiator();
        instance.addVariant("text/html;q=0.9")
				.addAliasMediaType("application/xhtml+xml");
        instance.addVariant("text/xml;q=0.95");
        instance.setDefaultAccept("text/html");
        MediaRangeSpec mrs=instance.getBestMatch(null);
        assertEquals("text/html", mrs.getMediaType());
    }
}
