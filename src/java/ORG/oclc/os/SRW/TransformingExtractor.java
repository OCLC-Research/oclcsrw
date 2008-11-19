/*
   Copyright 2006 OCLC Online Computer Library Center, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
/*
 * TransformingExtractor.java
 *
 * Created on October 17, 2005, 3:55 PM
 */

package ORG.oclc.os.SRW;

/**
 *
 * @author levan
 */

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TransformingExtractor implements SortElementExtractor {
    static Log log=LogFactory.getLog(TransformingExtractor.class);
    
    Transformer extractor;
    
    /** Creates a new instance of TransformingExtractor */
    public TransformingExtractor() {
    }
    
    public void init(String xpath, String prefix, String schema) throws SortElementExtractorException {
        StringBuffer sb=new StringBuffer();
        sb.append("<?xml version='1.0'?>\n")
          .append("<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>\n")
          .append("<xsl:output method='text'/>\n")
          .append("<xsl:template match='/'>\n")
          .append("<xsl:value-of select=\"").append(xpath).append("\"");
        if(prefix.length()>0)
            sb.append(" xmlns:").append(prefix).append("=\"").append(schema).append("\"");
        sb.append("/>\n")
          .append("</xsl:template>\n")
          .append("</xsl:stylesheet>\n");

        if(log.isDebugEnabled())
            log.debug("extractor source:\n"+sb.toString());
        log.info("extractor source:\n"+sb.toString());
        TransformerFactory tFactory=TransformerFactory.newInstance();
        StreamSource xslSource=new StreamSource(new StringReader(sb.toString()));
        try {
            extractor=tFactory.newTransformer(xslSource);
        }
        catch(TransformerException e) {
            throw new SortElementExtractorException(e);
        }
    }
    
    public String extract(Object record) throws SortElementExtractorException {
        if(!(record instanceof String))
            throw new IllegalArgumentException("Expected a String record");
        StringReader recordReader=new StringReader((String)record);
        StringWriter xmlRecordWriter=new StringWriter();
        StreamSource streamXMLRecord=new StreamSource(recordReader);
        try {
            extractor.transform(streamXMLRecord, new StreamResult(xmlRecordWriter));
        }
        catch(TransformerException e) {
            throw new SortElementExtractorException(e);
        }
        return xmlRecordWriter.toString();
    }
}
