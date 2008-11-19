/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ORG.oclc.os.SRW;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.axis.message.MessageElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 *
 * @author levan
 */
public class MessageElementBugDemo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db=dbf.newDocumentBuilder();
        Document domDoc = db.parse(new InputSource(new StringReader("<rec><data>stuff</data><?boom?></rec>")));
        Element el = domDoc.getDocumentElement();
        new MessageElement(el);
    }

}
