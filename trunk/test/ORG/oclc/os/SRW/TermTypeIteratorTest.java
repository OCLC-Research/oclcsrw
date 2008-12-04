/*
 * TermTypeIteratorTest.java
 * JUnit based test
 *
 * Created on November 5, 2007, 3:44 PM
 */

package ORG.oclc.os.SRW;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.*;
import gov.loc.www.zing.srw.TermType;
import java.util.Iterator;

/**
 *
 * @author levan
 */
public class TermTypeIteratorTest extends TestCase {
        String[] mappedTerms={
"adventure and adventurers", 
"adventure films", 
"adventure stories", 
"adventure television programs", 
"allegories", 
"animated films", 
"animated television programs", 
"anti-war films", 
"anti-war poetry", 
"arthurian romances", 
"autobiographical fiction", 
"bible", 
"bible films", 
"bible plays", 
"bildungsroman", 
"biographical fiction", 
"biographical films", 
"biographical television programs", 
"black humor (literature)", 
"cartoons and comics", 
"christian fiction", 
"christian life", 
"comedy films", 
"comedy programs", 
"comic books, strips, etc", 
"coming of age", 
"crime", 
"detective and mystery comic books, strips, etc", 
"detective and mystery films", 
"detective and mystery plays", 
"detective and mystery radio programs", 
"detective and mystery stories", 
"detective and mystery television programs", 
"didactic drama", 
"didactic fiction", 
"didactic poetry", 
"dystopias", 
"elegiac poetry", 
"epic films", 
"epic literature",
"epic poetry", 
"epistolary fiction", 
"epistolary poetry", 
"erotic comic books, strips, etc", 
"erotic films", 
"erotic poetry", 
"erotic stories", 
"fables", 
"fairy tales", 
"fantasy", 
"fantasy fiction", 
"fantasy films", 
"fantasy poetry", 
"fantasy television programs", 
"farces", 
"feature films", 
"film noir", 
"film novelizations", 
"folklore",
"gangster films", 
"ghost stories", 
"ghosts", 
"gothic revival (literature)", 
"historical drama", 
"historical fiction", 
"historical films", 
"historical poetry", 
"horror films", 
"horror plays", 
"horror radio programs", 
"horror stories", 
"horror tales", 
"horror television programs", 
"hospital films", 
"hospital television programs", 
"humorous plays", 
"humorous poetry", 
"humorous stories", 
"imaginary histories", 
"legal stories", 
"legends", 
"love",
"love poetry", 
"love stories", 
"made-for-tv movies", 
"medical fiction", 
"melodrama", 
"moralities", 
"motion picture plays", 
"motion picture serials", 
"musical films", 
"mysteries and miracle plays", 
"mystery and detective plays", 
"mystery and detective stories", 
"mystery and detective television programs", 
"narrative poetry", 
"noir fiction", 
"occult fiction", 
"parables", 
"passion-plays", 
"pastoral drama", 
"pastoral fiction", 
"pastoral poetry", 
"picaresque literature", 
"radio comedies", 
"radio plays", 
"radio scripts", 
"radio serials", 
"robinsonades", 
"romance fiction", 
"romances", 
"rural comedies", 
"science fiction", 
"science fiction comic books, strips, etc", 
"science fiction films", 
"science fiction plays", 
"science fiction poetry", 
"science fiction radio programs", 
"science fiction television programs", 
"sea stories", 
"short films", 
"short stories", 
"silent films", 
"spy films", 
"spy television programs", 
"superhero films", 
"suspense fiction", 
"tall tales", 
"television broadcasting of films", 
"television plays", 
"television scripts", 
"television serials", 
"trials",
"utopias", 
"variety shows (radio programs)", 
"variety shows (television programs)", 
"voyages to the otherworld", 
"voyages, imaginary", 
"war",
"war films", 
"war poetry", 
"war stories", 
"west (u.s.)", 
"western comic books, strips, etc", 
"western films", 
"western radio programs", 
"western stories", 
"western television programs"
        };
    
    public TermTypeIteratorTest(String testName) {
        super(testName);
    }

    public void testNextBackward() throws Exception {
System.out.println("enter testNextBackward");
        TermTypeIterator instance = new TermTypeIterator("http://alcme.oclc.org/srw/search/GSAFD", "z3919.mappedTerms", "exact", "~~~~~~~~~~", true);
        List l=Arrays.asList(mappedTerms);
        Collections.reverse(l);
        Iterator base=l.iterator();
        while(base.hasNext()) {
            assertTrue(instance.hasNext());
            assertEquals((String)base.next(), ((TermType)instance.next()).getValue());
        }
        assertFalse(".hasNext() should be false", instance.hasNext());
    }

    public void testNextForward() throws Exception {
System.out.println("enter testNextForward");
        TermTypeIterator instance = new TermTypeIterator("http://alcme.oclc.org/srw/search/GSAFD", "z3919.mappedTerms", "exact", "");
        Iterator base=(Arrays.asList(mappedTerms)).iterator();
        while(base.hasNext()) {
            assertTrue(instance.hasNext());
            assertEquals((String)base.next(), ((TermType)instance.next()).getValue());
        }
        assertFalse(".hasNext() should be false", instance.hasNext());
    }

    /**
     * Test of remove method, of class ORG.oclc.os.SRW.TermTypeIterator.
     */
    public void testRemove() throws Exception {
        TermTypeIterator instance = new TermTypeIterator("http://alcme.oclc.org/srw/search/GSAFD", "z3919.mappedTerms", "exact", "");
        
        try {
            instance.remove();
            fail("Should have thrown an UnsupportedOperationException");
        }
        catch(UnsupportedOperationException e) {
        }
    }
    
}
