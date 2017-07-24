/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ORG.oclc.os.SRW;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author levan
 */
public class URIResolverFromDisk implements URIResolver {
    static final Log LOG=LogFactory.getLog(URIResolverFromDisk.class);
    private final String directory1, directory2;
    private static final ConcurrentHashMap<String, Source> CHACHED_SOURCES=new ConcurrentHashMap<>();

    public URIResolverFromDisk(String directory1, String directory2) {
        this.directory1=directory1;
        this.directory2=directory2;
    }

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        String fullName;//, originalBase=base;
        fullName=href;
        if(href.charAt(0)!='/') { // maybe there's some path info in the base?
            //looking for stuff between the first and last slashes (after the protocol part)
            int i=base.indexOf(':');
            if(i>=0) {
                base=base.substring(i+1);
                i=base.indexOf(':');
                if(i>=0) {
                base=base.substring(i+1);
                    i=base.lastIndexOf('/');
                    if(i>=0) {
                        base=base.substring(0, i+1);
                        i=base.indexOf('/');
                        if(i>=0)
                            fullName=base.substring(i)+href;
                    }
                }
            }
        }
        Source source=CHACHED_SOURCES.get(fullName);
//        System.out.println("in URIResolverFromDisk.resolve("+href+", "+originalBase+")("+fullName+"): found "+source);
        if(source==null) {
            File f=Utilities.findFile(fullName, directory1, directory2);
//            System.out.println("in URIResolverFromDisk.resolve: found "+f);
            if(f!=null) {
                source=new StreamSource(f);
            }
            else {
                LOG.error("unable to find file "+fullName+": href="+href+", base="+base);
                source=new DOMSource(); // placeholder to indicate we found no such file
            }
            CHACHED_SOURCES.put(fullName, source);
        }
        if(source instanceof DOMSource)
            return null;
        return source;
    }
}
