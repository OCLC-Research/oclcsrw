/**
 * SRWSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2alpha Dec 01, 2003 (04:33:24 EST) WSDL2Java emitter.
 */

package gov.loc.www.zing.srw.srw_bindings;

public class SRWSoapBindingSkeleton implements gov.loc.www.zing.srw.interfaces.SRWPort, org.apache.axis.wsdl.Skeleton {
    private gov.loc.www.zing.srw.interfaces.SRWPort impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "searchRetrieveRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "searchRetrieveRequestType"), gov.loc.www.zing.srw.SearchRetrieveRequestType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("searchRetrieveOperation", _params, new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "searchRetrieveResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "searchRetrieveResponseType"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "SearchRetrieveOperation"));
        _oper.setSoapAction("searchRetrieve");
        _myOperationsList.add(_oper);
        if (_myOperations.get("searchRetrieveOperation") == null) {
            _myOperations.put("searchRetrieveOperation", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("searchRetrieveOperation")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "scanRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "scanRequestType"), gov.loc.www.zing.srw.ScanRequestType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("scanOperation", _params, new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "scanResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "scanResponseType"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "ScanOperation"));
        _oper.setSoapAction("scan");
        _myOperationsList.add(_oper);
        if (_myOperations.get("scanOperation") == null) {
            _myOperations.put("scanOperation", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("scanOperation")).add(_oper);
    }

    public SRWSoapBindingSkeleton() {
        this.impl = new gov.loc.www.zing.srw.srw_bindings.SRWSoapBindingImpl();
    }

    public SRWSoapBindingSkeleton(gov.loc.www.zing.srw.interfaces.SRWPort impl) {
        this.impl = impl;
    }
    public gov.loc.www.zing.srw.SearchRetrieveResponseType searchRetrieveOperation(gov.loc.www.zing.srw.SearchRetrieveRequestType body) throws java.rmi.RemoteException
    {
        gov.loc.www.zing.srw.SearchRetrieveResponseType ret = impl.searchRetrieveOperation(body);
        return ret;
    }

    public gov.loc.www.zing.srw.ScanResponseType scanOperation(gov.loc.www.zing.srw.ScanRequestType body) throws java.rmi.RemoteException
    {
        gov.loc.www.zing.srw.ScanResponseType ret = impl.scanOperation(body);
        return ret;
    }

}
