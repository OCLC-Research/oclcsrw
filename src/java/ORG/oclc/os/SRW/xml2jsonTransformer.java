/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ORG.oclc.os.SRW;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Properties;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**b
 *
 * @author levan
 */
public class xml2jsonTransformer extends DatabaseRecordTransformer {
    private static final Log log=LogFactory.getLog(xml2jsonTransformer.class);

    @Override
    public void transform(Source xmlSource, Result outputTarget) throws TransformerException {
        StreamSource source=(StreamSource) xmlSource;
        BufferedReader br=new BufferedReader(source.getReader());
        StringBuilder sb=new StringBuilder();
        String line;
        try {
            while((line=br.readLine())!=null)
                sb.append(line);
        } catch (IOException ex) {
            throw new TransformerException(ex);
        }
        String xmlRecord=sb.toString();
        if(log.isDebugEnabled())
            log.debug("xmlSource: "+xmlRecord);
        XMLSerializer xmlSerializer = new XMLSerializer();
        try {
            JSON json=xmlSerializer.read(xmlRecord);
            StreamResult sr=(StreamResult) outputTarget;
            json.write(sr.getWriter());
            if(log.isDebugEnabled())
                log.debug("convertedJSON: "+sr.toString());
        }
        catch(Exception e) {
            log.error("Unable to transform to JSON: "+xmlRecord);
            throw new TransformerException(e);
        }
    }

    @Override
    public void setParameter(String name, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getParameter(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearParameters() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setURIResolver(URIResolver resolver) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public URIResolver getURIResolver() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setOutputProperties(Properties oformat) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Properties getOutputProperties() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setOutputProperty(String name, String value) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getOutputProperty(String name) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setErrorListener(ErrorListener listener) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ErrorListener getErrorListener() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
