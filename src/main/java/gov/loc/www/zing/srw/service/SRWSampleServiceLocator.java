/**
 * SRWSampleServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package gov.loc.www.zing.srw.service;

public class SRWSampleServiceLocator extends org.apache.axis.client.Service implements gov.loc.www.zing.srw.service.SRWSampleService {

    // Use to get a proxy class for SRW
    private final java.lang.String SRW_address = "http://insertserver/inserturl/";

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


    // Use to get a proxy class for ExplainSOAP
    private final java.lang.String ExplainSOAP_address = "http://insertserver/inserturl/";

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
        String inputPortName = portName.getLocalPart();
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
        return new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/service/", "SRWSampleService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("SRW"));
            ports.add(new javax.xml.namespace.QName("ExplainSOAP"));
        }
        return ports.iterator();
    }

}
