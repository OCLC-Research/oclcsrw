/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ORG.oclc.os.SRW;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.util.Properties;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author levan
 */
public class JenaTransformer extends DatabaseRecordTransformer {
    private String language;
    
    @Override
    public void init(String prefix, Properties properties) {
        language=properties.getProperty(prefix+".JenaTransformer.language");
        if(language==null)
            throw new IllegalArgumentException("missing mandator parameter: "+
                    prefix+".JenaTransformer.language");
    }
    
    @Override
    public void transform(Source xmlSource, Result outputTarget) throws TransformerException {
        Model model=ModelFactory.createDefaultModel();
        try {
            if(((StreamSource)xmlSource).getReader()!=null)
                model.read(((StreamSource)xmlSource).getReader(), null);
            else
                model.read(((StreamSource)xmlSource).getInputStream(), null);
            if(((StreamResult)outputTarget).getWriter()!=null)
                model.write(((StreamResult)outputTarget).getWriter(), language);
            else
                model.write(((StreamResult)outputTarget).getOutputStream(), language);
        } finally {
            model.close();
        }
    }

    @Override
    public void reset() {}
    
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
