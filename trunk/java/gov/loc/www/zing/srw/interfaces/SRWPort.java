/**
 * SRWPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.srw.interfaces;

public interface SRWPort extends java.rmi.Remote {
    public gov.loc.www.zing.srw.SearchRetrieveResponseType searchRetrieveOperation(gov.loc.www.zing.srw.SearchRetrieveRequestType body) throws java.rmi.RemoteException;
    public gov.loc.www.zing.srw.ScanResponseType scanOperation(gov.loc.www.zing.srw.ScanRequestType body) throws java.rmi.RemoteException;
}
