/**
 * ExplainResponseType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.srw;

public class ExplainResponseType  extends gov.loc.www.zing.srw.ResponseType  implements java.io.Serializable {
    private gov.loc.www.zing.srw.RecordType record;
    private gov.loc.www.zing.srw.ExplainRequestType echoedExplainRequest;
    private gov.loc.www.zing.srw.DiagnosticsType diagnostics;
    private gov.loc.www.zing.srw.ExtraDataType extraResponseData;

    public ExplainResponseType() {
    }

    public ExplainResponseType(
           gov.loc.www.zing.srw.RecordType record,
           gov.loc.www.zing.srw.ExplainRequestType echoedExplainRequest,
           gov.loc.www.zing.srw.DiagnosticsType diagnostics,
           gov.loc.www.zing.srw.ExtraDataType extraResponseData) {
           this.record = record;
           this.echoedExplainRequest = echoedExplainRequest;
           this.diagnostics = diagnostics;
           this.extraResponseData = extraResponseData;
    }


    /**
     * Gets the record value for this ExplainResponseType.
     * 
     * @return record
     */
    public gov.loc.www.zing.srw.RecordType getRecord() {
        return record;
    }


    /**
     * Sets the record value for this ExplainResponseType.
     * 
     * @param record
     */
    public void setRecord(gov.loc.www.zing.srw.RecordType record) {
        this.record = record;
    }


    /**
     * Gets the echoedExplainRequest value for this ExplainResponseType.
     * 
     * @return echoedExplainRequest
     */
    public gov.loc.www.zing.srw.ExplainRequestType getEchoedExplainRequest() {
        return echoedExplainRequest;
    }


    /**
     * Sets the echoedExplainRequest value for this ExplainResponseType.
     * 
     * @param echoedExplainRequest
     */
    public void setEchoedExplainRequest(gov.loc.www.zing.srw.ExplainRequestType echoedExplainRequest) {
        this.echoedExplainRequest = echoedExplainRequest;
    }


    /**
     * Gets the diagnostics value for this ExplainResponseType.
     * 
     * @return diagnostics
     */
    public gov.loc.www.zing.srw.DiagnosticsType getDiagnostics() {
        return diagnostics;
    }


    /**
     * Sets the diagnostics value for this ExplainResponseType.
     * 
     * @param diagnostics
     */
    public void setDiagnostics(gov.loc.www.zing.srw.DiagnosticsType diagnostics) {
        this.diagnostics = diagnostics;
    }


    /**
     * Gets the extraResponseData value for this ExplainResponseType.
     * 
     * @return extraResponseData
     */
    public gov.loc.www.zing.srw.ExtraDataType getExtraResponseData() {
        return extraResponseData;
    }


    /**
     * Sets the extraResponseData value for this ExplainResponseType.
     * 
     * @param extraResponseData
     */
    public void setExtraResponseData(gov.loc.www.zing.srw.ExtraDataType extraResponseData) {
        this.extraResponseData = extraResponseData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ExplainResponseType)) return false;
        ExplainResponseType other = (ExplainResponseType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.record==null && other.getRecord()==null) || 
             (this.record!=null &&
              this.record.equals(other.getRecord()))) &&
            ((this.echoedExplainRequest==null && other.getEchoedExplainRequest()==null) || 
             (this.echoedExplainRequest!=null &&
              this.echoedExplainRequest.equals(other.getEchoedExplainRequest()))) &&
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
        if (getRecord() != null) {
            _hashCode += getRecord().hashCode();
        }
        if (getEchoedExplainRequest() != null) {
            _hashCode += getEchoedExplainRequest().hashCode();
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
        new org.apache.axis.description.TypeDesc(ExplainResponseType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "explainResponseType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("record");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "record"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "record"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("echoedExplainRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "echoedExplainRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "echoedExplainRequest"));
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
