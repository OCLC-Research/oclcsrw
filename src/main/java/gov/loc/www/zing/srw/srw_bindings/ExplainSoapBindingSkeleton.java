/**
 * ExplainSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2alpha Dec 01, 2003 (04:33:24 EST) WSDL2Java emitter.
 */

package gov.loc.www.zing.srw.srw_bindings;

public class ExplainSoapBindingSkeleton implements gov.loc.www.zing.srw.interfaces.ExplainPort, org.apache.axis.wsdl.Skeleton {
    private gov.loc.www.zing.srw.interfaces.ExplainPort impl;
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "explainRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "explainRequestType"), gov.loc.www.zing.srw.ExplainRequestType.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("explainOperation", _params, new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "explainResponse"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "explainResponseType"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "ExplainOperation"));
        _oper.setSoapAction("explain");
        _myOperationsList.add(_oper);
        if (_myOperations.get("explainOperation") == null) {
            _myOperations.put("explainOperation", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("explainOperation")).add(_oper);
    }

    public ExplainSoapBindingSkeleton() {
        this.impl = new gov.loc.www.zing.srw.srw_bindings.ExplainSoapBindingImpl();
    }

    public ExplainSoapBindingSkeleton(gov.loc.www.zing.srw.interfaces.ExplainPort impl) {
        this.impl = impl;
    }
    public gov.loc.www.zing.srw.ExplainResponseType explainOperation(gov.loc.www.zing.srw.ExplainRequestType body) throws java.rmi.RemoteException
    {
        gov.loc.www.zing.srw.ExplainResponseType ret = impl.explainOperation(body);
        return ret;
    }

}
