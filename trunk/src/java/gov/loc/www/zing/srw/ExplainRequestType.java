/**
 * ExplainRequestType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.srw;

public class ExplainRequestType  extends gov.loc.www.zing.srw.RequestType  implements java.io.Serializable {
    private java.lang.String recordPacking;
    private org.apache.axis.types.URI stylesheet;
    private gov.loc.www.zing.srw.ExtraDataType extraRequestData;

    public ExplainRequestType() {
    }

    public ExplainRequestType(
           java.lang.String recordPacking,
           org.apache.axis.types.URI stylesheet,
           gov.loc.www.zing.srw.ExtraDataType extraRequestData) {
           this.recordPacking = recordPacking;
           this.stylesheet = stylesheet;
           this.extraRequestData = extraRequestData;
    }


    /**
     * Gets the recordPacking value for this ExplainRequestType.
     * 
     * @return recordPacking
     */
    public java.lang.String getRecordPacking() {
        return recordPacking;
    }


    /**
     * Sets the recordPacking value for this ExplainRequestType.
     * 
     * @param recordPacking
     */
    public void setRecordPacking(java.lang.String recordPacking) {
        this.recordPacking = recordPacking;
    }


    /**
     * Gets the stylesheet value for this ExplainRequestType.
     * 
     * @return stylesheet
     */
    public org.apache.axis.types.URI getStylesheet() {
        return stylesheet;
    }


    /**
     * Sets the stylesheet value for this ExplainRequestType.
     * 
     * @param stylesheet
     */
    public void setStylesheet(org.apache.axis.types.URI stylesheet) {
        this.stylesheet = stylesheet;
    }


    /**
     * Gets the extraRequestData value for this ExplainRequestType.
     * 
     * @return extraRequestData
     */
    public gov.loc.www.zing.srw.ExtraDataType getExtraRequestData() {
        return extraRequestData;
    }


    /**
     * Sets the extraRequestData value for this ExplainRequestType.
     * 
     * @param extraRequestData
     */
    public void setExtraRequestData(gov.loc.www.zing.srw.ExtraDataType extraRequestData) {
        this.extraRequestData = extraRequestData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ExplainRequestType)) return false;
        ExplainRequestType other = (ExplainRequestType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.recordPacking==null && other.getRecordPacking()==null) || 
             (this.recordPacking!=null &&
              this.recordPacking.equals(other.getRecordPacking()))) &&
            ((this.stylesheet==null && other.getStylesheet()==null) || 
             (this.stylesheet!=null &&
              this.stylesheet.equals(other.getStylesheet()))) &&
            ((this.extraRequestData==null && other.getExtraRequestData()==null) || 
             (this.extraRequestData!=null &&
              this.extraRequestData.equals(other.getExtraRequestData())));
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
        if (getRecordPacking() != null) {
            _hashCode += getRecordPacking().hashCode();
        }
        if (getStylesheet() != null) {
            _hashCode += getStylesheet().hashCode();
        }
        if (getExtraRequestData() != null) {
            _hashCode += getExtraRequestData().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ExplainRequestType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "explainRequestType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordPacking");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordPacking"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordPacking"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stylesheet");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "stylesheet"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "stylesheet"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extraRequestData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "extraRequestData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "extraRequestData"));
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
