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
    static final Log log=LogFactory.getLog(URIResolverFromDisk.class);
    private final String directory1, directory2;
    private static final ConcurrentHashMap<String, Source> cachedSources=new ConcurrentHashMap<String, Source>();

    public URIResolverFromDisk(String directory1, String directory2) {
        this.directory1=directory1;
        this.directory2=directory2;
    }

    public Source resolve(String href, String base) throws TransformerException {
        String fullName;
        if(href.charAt(0)=='/')
            fullName=href;
        else {
            base=base.substring(base.indexOf(':')+1);
            base=base.substring(base.indexOf(':')+1);
            base=base.substring(0, base.lastIndexOf('/')+1);
            fullName=base.substring(base.indexOf('/'))+href;
        }
        Source source=cachedSources.get(fullName);
//        System.out.println("in URIResolverFromDisk.resolve("+href+", "+base+")("+fullName+"): found "+source);
        if(source==null) {
            File f=Utilities.findFile(fullName, directory1, directory2);
//            System.out.println("in URIResolverFromDisk.resolve: found "+f);
            if(f!=null) {
                source=new StreamSource(f);
            }
            else {
                log.error("unable to find file "+fullName+": href="+href+", base="+base);
                source=new DOMSource(); // placeholder to indicate we found no such file
            }
            cachedSources.put(fullName, source);
        }
        if(source instanceof DOMSource)
            return null;
        return source;
    }
}
