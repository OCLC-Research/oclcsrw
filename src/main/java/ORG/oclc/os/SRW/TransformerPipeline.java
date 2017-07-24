/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ORG.oclc.os.SRW;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author levan
 */
public class TransformerPipeline extends Transformer {
    Log log=LogFactory.getLog(TransformerPipeline.class.getName());
    ArrayList<Object> templateOrTransformer=new ArrayList<>();

    @Override
    public void transform(Source xmlSource, Result outputTarget) throws TransformerException {
        ByteArrayOutputStream baos;
        Iterator<Object> iter = templateOrTransformer.iterator();
        Object o;
        String intermediateRecord;
        Transformer t;
        for(int i=1; iter.hasNext(); i++) {
            o=iter.next();
            if(o instanceof Templates) {
                t=((Templates)o).newTransformer();
            }
            else
                t=(Transformer)o;
            if(iter.hasNext()) { // chain
                t.transform(xmlSource, new StreamResult(baos=new ByteArrayOutputStream()));
                // a redirect response ends the chain
                intermediateRecord=new String(baos.toByteArray());
                if(log.isDebugEnabled()) {
                    log.debug("conversion "+i+" of "+templateOrTransformer.size()+":");
                    log.debug(intermediateRecord);
                }
                if(intermediateRecord.startsWith("<re:redirect")) {
                    try {
                        if(((StreamResult)outputTarget).getWriter()!=null)
                            ((StreamResult)outputTarget).getWriter().write(intermediateRecord);
                        else
                            ((StreamResult)outputTarget).getOutputStream().write(intermediateRecord.getBytes(StandardCharsets.UTF_8));
                    }
                    catch(IOException e) {
                        throw new TransformerException(e);
                    }
                    return;
                }
                xmlSource=new StreamSource(new ByteArrayInputStream(baos.toByteArray()));
            } else { // write to output
                t.transform(xmlSource, outputTarget);
            }
        }
    }

    public void addTemplates(Templates t) {
        templateOrTransformer.add(t);
    }
    
    public void addTransformer(Transformer t) {
        templateOrTransformer.add(t);
    }
    
    static String convertStreamToString(StreamSource source) {
        InputStream is = source.getInputStream();
        is.mark(Integer.MAX_VALUE);
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        String str = s.hasNext() ? s.next() : "";
        try {
            is.reset();
        } catch (IOException ex) {
            Logger.getLogger(TransformerPipeline.class.getName()).log(Level.SEVERE, null, ex);
        }
        return str;
    }

    public boolean isEmpty() {
        return templateOrTransformer.isEmpty();
    }

    @Override
    public void reset() {
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
