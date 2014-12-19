/*
 * TermTypeIteratorTest.java
 * JUnit based test
 *
 * Created on November 5, 2007, 3:44 PM
 */

package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.TermType;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import junit.framework.*;

/**
 *
 * @author levan
 */
public class TermTypeIteratorTest extends TestCase {
        List<String> languages=Arrays.asList(
"A",
"amharic",
"arabic",
"armenian",
"basque",
"bengali",
"bulgarian",
"burmese",
"catalan",
"caucasian",
"chinese",
"croatian",
"czech",
"danish",
"dutch",
"english",
"esperanto",
"finnish",
"french",
"german",
"greek",
"gujarati",
"hebrew",
"hindi",
"hungarian",
"italian",
"japanese",
"kazakh",
"korean",
"latvian",
"malayalam",
"modern",
"norwegian",
"persian",
"polish",
"portuguese",
"romanian",
"russian",
"serbo",
"slovak",
"slovenian",
"spanish",
"swedish",
"tagalog",
"turkish",
"ukrainian",
"undetermined",
"unknown*N",
"uzbek",
"welsh");
    
    public TermTypeIteratorTest(String testName) {
        super(testName);
    }

    public void testNextBackward() throws Exception {
//System.out.println("enter testNextBackward");
//        TermTypeIterator instance = new TermTypeIterator("http://viaf.org/hosted/search/scifi", "dc.language", "=", "~~~~~~~~~~", true);
//        Collections.reverse(languages);
//        Iterator<String> base=languages.iterator();
//        while(base.hasNext()) {
//            assertTrue(instance.hasNext());
//            assertEquals(base.next(), instance.next().getValue());
//        }
//        assertFalse(".hasNext() should be false", instance.hasNext());
    }

    public void testNextForward() throws Exception {
//System.out.println("enter testNextForward");
//        TermTypeIterator instance = new TermTypeIterator("http://viaf.org/hosted/search/scifi", "dc.language", "=", "");
//        Iterator<String> base=languages.iterator();
//        while(base.hasNext()) {
//            assertTrue(instance.hasNext());
//            TermType t=instance.next();
//            String language=base.next();
////            System.out.println("language="+language+", db="+t.getValue());
//            assertEquals(language, t.getValue());
//        }
//        assertFalse(".hasNext() should be false", instance.hasNext());
    }

    /**
     * Test of remove method, of class ORG.oclc.os.SRW.TermTypeIterator.
     */
    public void testRemove() throws Exception {
//        TermTypeIterator instance = new TermTypeIterator("http://viaf.org/hosted/search/scifi", "dc.language", "=", "");
//        
//        try {
//            instance.remove();
//            fail("Should have thrown an UnsupportedOperationException");
//        }
//        catch(UnsupportedOperationException e) {
//        }
    }
    
}
