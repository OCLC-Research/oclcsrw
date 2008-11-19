/**
 * SRWSampleServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.srw.srw_sample_service;

public class SRWSampleServiceLocator extends org.apache.axis.client.Service implements gov.loc.www.zing.srw.srw_sample_service.SRWSampleService {

    public SRWSampleServiceLocator() {
    }


    public SRWSampleServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SRWSampleServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SRW
    private java.lang.String SRW_address = "http://insertserver/inserturl/";

    public java.lang.String getSRWAddress() {
        return SRW_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SRWWSDDServiceName = "SRW";

    public java.lang.String getSRWWSDDServiceName() {
        return SRWWSDDServiceName;
    }

    public void setSRWWSDDServiceName(java.lang.String name) {
        SRWWSDDServiceName = name;
    }

    public gov.loc.www.zing.srw.interfaces.SRWPort getSRW() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SRW_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSRW(endpoint);
    }

    public gov.loc.www.zing.srw.interfaces.SRWPort getSRW(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            gov.loc.www.zing.srw.srw_bindings.SRWSoapBindingStub _stub = new gov.loc.www.zing.srw.srw_bindings.SRWSoapBindingStub(portAddress, this);
            _stub.setPortName(getSRWWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSRWEndpointAddress(java.lang.String address) {
        SRW_address = address;
    }


    // Use to get a proxy class for ExplainSOAP
    private java.lang.String ExplainSOAP_address = "http://insertserver/inserturl/";

    public java.lang.String getExplainSOAPAddress() {
        return ExplainSOAP_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ExplainSOAPWSDDServiceName = "ExplainSOAP";

    public java.lang.String getExplainSOAPWSDDServiceName() {
        return ExplainSOAPWSDDServiceName;
    }

    public void setExplainSOAPWSDDServiceName(java.lang.String name) {
        ExplainSOAPWSDDServiceName = name;
    }

    public gov.loc.www.zing.srw.interfaces.ExplainPort getExplainSOAP() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ExplainSOAP_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getExplainSOAP(endpoint);
    }

    public gov.loc.www.zing.srw.interfaces.ExplainPort getExplainSOAP(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            gov.loc.www.zing.srw.srw_bindings.ExplainSoapBindingStub _stub = new gov.loc.www.zing.srw.srw_bindings.ExplainSoapBindingStub(portAddress, this);
            _stub.setPortName(getExplainSOAPWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setExplainSOAPEndpointAddress(java.lang.String address) {
        ExplainSOAP_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (gov.loc.www.zing.srw.interfaces.SRWPort.class.isAssignableFrom(serviceEndpointInterface)) {
                gov.loc.www.zing.srw.srw_bindings.SRWSoapBindingStub _stub = new gov.loc.www.zing.srw.srw_bindings.SRWSoapBindingStub(new java.net.URL(SRW_address), this);
                _stub.setPortName(getSRWWSDDServiceName());
                return _stub;
            }
            if (gov.loc.www.zing.srw.interfaces.ExplainPort.class.isAssignableFrom(serviceEndpointInterface)) {
                gov.loc.www.zing.srw.srw_bindings.ExplainSoapBindingStub _stub = new gov.loc.www.zing.srw.srw_bindings.ExplainSoapBindingStub(new java.net.URL(ExplainSOAP_address), this);
                _stub.setPortName(getExplainSOAPWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("SRW".equals(inputPortName)) {
            return getSRW();
        }
        else if ("ExplainSOAP".equals(inputPortName)) {
            return getExplainSOAP();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/srw-sample-service/", "SRWSampleService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/srw-sample-service/", "SRW"));
            ports.add(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/srw-sample-service/", "ExplainSOAP"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SRW".equals(portName)) {
            setSRWEndpointAddress(address);
        }
        else 
if ("ExplainSOAP".equals(portName)) {
            setExplainSOAPEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
