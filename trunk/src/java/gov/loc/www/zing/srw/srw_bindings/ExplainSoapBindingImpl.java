/**
 * ExplainSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package gov.loc.www.zing.srw.srw_bindings;

import gov.loc.www.zing.srw.ExplainRequestType;
import gov.loc.www.zing.srw.ExplainResponseType;
import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.StringOrXmlFragment;
import java.io.StringReader;
import org.apache.axis.MessageContext;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.axis.message.MessageElement;
import ORG.oclc.os.SRW.SRWDatabase;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class ExplainSoapBindingImpl implements gov.loc.www.zing.srw.interfaces.ExplainPort{
    Log log=LogFactory.getLog(ExplainSoapBindingImpl.class);

    public ExplainResponseType explainOperation(ExplainRequestType request)
      throws java.rmi.RemoteException {
        MessageContext   msgContext=MessageContext.getCurrentContext();
        ExplainResponseType response=new ExplainResponseType();
        String dbname=(String)msgContext.getProperty("dbname");
        SRWDatabase db=(SRWDatabase)msgContext.getProperty("db");
        log.info("db="+db);
        response.setVersion("1.1");
        RecordType record=new RecordType();
        record.setRecordSchema("http://explain.z3950.org/dtd/2.0/");
        StringOrXmlFragment frag=new StringOrXmlFragment();
        String recordPacking=request.getRecordPacking();
        if(recordPacking==null) {
            if(msgContext.getProperty("sru")!=null)
                recordPacking="xml"; // default for sru
            else
                recordPacking="string"; // default for srw
        }
        if(!recordPacking.equals("xml") &&
          !recordPacking.equals("string")) {
            return db.diagnostic(71, recordPacking, response);
        }
        try {
            if(recordPacking.equals("xml")) {
                record.setRecordPacking("xml");
                Document domDoc=XMLUtils.newDocument(
                    new InputSource(
                    new StringReader(db.getExplainRecord())));
                MessageElement elems[]=new MessageElement[1];
                elems[0]=new MessageElement(
                    domDoc.getDocumentElement());
                frag.set_any(elems);
            }
            else { // srw
                record.setRecordPacking("string");
                MessageElement elems[]=new MessageElement[1];
                elems[0]=new MessageElement();
                elems[0].addTextNode(db.getExplainRecord());
                frag.set_any(elems);
            }
            record.setRecordData(frag);
            response.setRecord(record);
        }
        catch(Exception e) {
            log.info(e, e);
            return null;
        }
        response.setEchoedExplainRequest(request);
        return response;
    }
}
