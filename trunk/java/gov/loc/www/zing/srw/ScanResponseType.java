/**
 * ScanResponseType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.srw;

public class ScanResponseType  extends gov.loc.www.zing.srw.ResponseType  implements java.io.Serializable {
    private gov.loc.www.zing.srw.TermsType terms;
    private gov.loc.www.zing.srw.EchoedScanRequestType echoedScanRequest;
    private gov.loc.www.zing.srw.DiagnosticsType diagnostics;
    private gov.loc.www.zing.srw.ExtraDataType extraResponseData;

    public ScanResponseType() {
    }

    public ScanResponseType(
           gov.loc.www.zing.srw.TermsType terms,
           gov.loc.www.zing.srw.EchoedScanRequestType echoedScanRequest,
           gov.loc.www.zing.srw.DiagnosticsType diagnostics,
           gov.loc.www.zing.srw.ExtraDataType extraResponseData) {
           this.terms = terms;
           this.echoedScanRequest = echoedScanRequest;
           this.diagnostics = diagnostics;
           this.extraResponseData = extraResponseData;
    }


    /**
     * Gets the terms value for this ScanResponseType.
     * 
     * @return terms
     */
    public gov.loc.www.zing.srw.TermsType getTerms() {
        return terms;
    }


    /**
     * Sets the terms value for this ScanResponseType.
     * 
     * @param terms
     */
    public void setTerms(gov.loc.www.zing.srw.TermsType terms) {
        this.terms = terms;
    }


    /**
     * Gets the echoedScanRequest value for this ScanResponseType.
     * 
     * @return echoedScanRequest
     */
    public gov.loc.www.zing.srw.EchoedScanRequestType getEchoedScanRequest() {
        return echoedScanRequest;
    }


    /**
     * Sets the echoedScanRequest value for this ScanResponseType.
     * 
     * @param echoedScanRequest
     */
    public void setEchoedScanRequest(gov.loc.www.zing.srw.EchoedScanRequestType echoedScanRequest) {
        this.echoedScanRequest = echoedScanRequest;
    }


    /**
     * Gets the diagnostics value for this ScanResponseType.
     * 
     * @return diagnostics
     */
    public gov.loc.www.zing.srw.DiagnosticsType getDiagnostics() {
        return diagnostics;
    }


    /**
     * Sets the diagnostics value for this ScanResponseType.
     * 
     * @param diagnostics
     */
    public void setDiagnostics(gov.loc.www.zing.srw.DiagnosticsType diagnostics) {
        this.diagnostics = diagnostics;
    }


    /**
     * Gets the extraResponseData value for this ScanResponseType.
     * 
     * @return extraResponseData
     */
    public gov.loc.www.zing.srw.ExtraDataType getExtraResponseData() {
        return extraResponseData;
    }


    /**
     * Sets the extraResponseData value for this ScanResponseType.
     * 
     * @param extraResponseData
     */
    public void setExtraResponseData(gov.loc.www.zing.srw.ExtraDataType extraResponseData) {
        this.extraResponseData = extraResponseData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ScanResponseType)) return false;
        ScanResponseType other = (ScanResponseType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.terms==null && other.getTerms()==null) || 
             (this.terms!=null &&
              this.terms.equals(other.getTerms()))) &&
            ((this.echoedScanRequest==null && other.getEchoedScanRequest()==null) || 
             (this.echoedScanRequest!=null &&
              this.echoedScanRequest.equals(other.getEchoedScanRequest()))) &&
            ((this.diagnostics==null && other.getDiagnostics()==null) || 
             (this.diagnostics!=null &&
              this.diagnostics.equals(other.getDiagnostics()))) &&
            ((this.extraResponseData==null && other.getExtraResponseData()==null) || 
             (this.extraResponseData!=null &&
              this.extraResponseData.equals(other.getExtraResponseData())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getTerms() != null) {
            _hashCode += getTerms().hashCode();
        }
        if (getEchoedScanRequest() != null) {
            _hashCode += getEchoedScanRequest().hashCode();
        }
        if (getDiagnostics() != null) {
            _hashCode += getDiagnostics().hashCode();
        }
        if (getExtraResponseData() != null) {
            _hashCode += getExtraResponseData().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ScanResponseType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "scanResponseType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terms");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "terms"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "terms"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("echoedScanRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "echoedScanRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "echoedScanRequest"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("diagnostics");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "diagnostics"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "diagnostics"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extraResponseData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "extraResponseData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "extraResponseData"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
