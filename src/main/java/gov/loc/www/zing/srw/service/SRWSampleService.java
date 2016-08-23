/**
 * SRWSampleService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package gov.loc.www.zing.srw.service;

public interface SRWSampleService extends javax.xml.rpc.Service {
    public java.lang.String getSRWAddress();

    public gov.loc.www.zing.srw.interfaces.SRWPort getSRW() throws javax.xml.rpc.ServiceException;

    public gov.loc.www.zing.srw.interfaces.SRWPort getSRW(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getExplainSOAPAddress();

    public gov.loc.www.zing.srw.interfaces.ExplainPort getExplainSOAP() throws javax.xml.rpc.ServiceException;

    public gov.loc.www.zing.srw.interfaces.ExplainPort getExplainSOAP(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
