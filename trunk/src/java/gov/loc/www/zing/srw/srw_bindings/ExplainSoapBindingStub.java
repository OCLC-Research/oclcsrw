/**
 * ExplainSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.srw.srw_bindings;

public class ExplainSoapBindingStub extends org.apache.axis.client.Stub implements gov.loc.www.zing.srw.interfaces.ExplainPort {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[1];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ExplainOperation");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "explainRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "explainRequestType"), gov.loc.www.zing.srw.ExplainRequestType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "explainResponseType"));
        oper.setReturnClass(gov.loc.www.zing.srw.ExplainResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "explainResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

    }

    public ExplainSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public ExplainSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public ExplainSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "booleanType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.cql.xcql.BooleanType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "modifiersType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.cql.xcql.ModifiersType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "modifierType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.cql.xcql.ModifierType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "operandType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.cql.xcql.OperandType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "prefixesType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.cql.xcql.PrefixesType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "prefixType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.cql.xcql.PrefixType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "relationType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.cql.xcql.RelationType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "searchClauseType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.cql.xcql.SearchClauseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "tripleType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.cql.xcql.TripleType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/diagnostic/", "diagnosticType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.diagnostic.DiagnosticType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", ">termType>whereInList");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.TermTypeWhereInList.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "diagnosticsType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.DiagnosticsType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "echoedScanRequestType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.EchoedScanRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "echoedSearchRetrieveRequestType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.EchoedSearchRetrieveRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "explainRequestType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.ExplainRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "explainResponseType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.ExplainResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "extraDataType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.ExtraDataType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordsType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.RecordsType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.RecordType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "requestType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.RequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "responseType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.ResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "scanRequestType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.ScanRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "scanResponseType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.ScanResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "searchRetrieveRequestType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.SearchRetrieveRequestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "searchRetrieveResponseType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.SearchRetrieveResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "sortKeyType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.SortKeyType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "stringOrXmlFragment");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.StringOrXmlFragment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "termsType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.TermsType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "termType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.TermType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "xmlFragment");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.XmlFragment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "xSortKeysType");
            cachedSerQNames.add(qName);
            cls = gov.loc.www.zing.srw.XSortKeysType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public gov.loc.www.zing.srw.ExplainResponseType explainOperation(gov.loc.www.zing.srw.ExplainRequestType body) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "ExplainOperation"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {body});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (gov.loc.www.zing.srw.ExplainResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (gov.loc.www.zing.srw.ExplainResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, gov.loc.www.zing.srw.ExplainResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
