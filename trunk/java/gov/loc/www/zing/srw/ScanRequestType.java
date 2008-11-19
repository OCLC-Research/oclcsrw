/**
 * ScanRequestType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.srw;

public class ScanRequestType  extends gov.loc.www.zing.srw.RequestType  implements java.io.Serializable {
    private java.lang.String scanClause;
    private org.apache.axis.types.NonNegativeInteger responsePosition;
    private org.apache.axis.types.PositiveInteger maximumTerms;
    private org.apache.axis.types.URI stylesheet;
    private gov.loc.www.zing.srw.ExtraDataType extraRequestData;

    public ScanRequestType() {
    }

    public ScanRequestType(
           java.lang.String scanClause,
           org.apache.axis.types.NonNegativeInteger responsePosition,
           org.apache.axis.types.PositiveInteger maximumTerms,
           org.apache.axis.types.URI stylesheet,
           gov.loc.www.zing.srw.ExtraDataType extraRequestData) {
           this.scanClause = scanClause;
           this.responsePosition = responsePosition;
           this.maximumTerms = maximumTerms;
           this.stylesheet = stylesheet;
           this.extraRequestData = extraRequestData;
    }


    /**
     * Gets the scanClause value for this ScanRequestType.
     * 
     * @return scanClause
     */
    public java.lang.String getScanClause() {
        return scanClause;
    }


    /**
     * Sets the scanClause value for this ScanRequestType.
     * 
     * @param scanClause
     */
    public void setScanClause(java.lang.String scanClause) {
        this.scanClause = scanClause;
    }


    /**
     * Gets the responsePosition value for this ScanRequestType.
     * 
     * @return responsePosition
     */
    public org.apache.axis.types.NonNegativeInteger getResponsePosition() {
        return responsePosition;
    }


    /**
     * Sets the responsePosition value for this ScanRequestType.
     * 
     * @param responsePosition
     */
    public void setResponsePosition(org.apache.axis.types.NonNegativeInteger responsePosition) {
        this.responsePosition = responsePosition;
    }


    /**
     * Gets the maximumTerms value for this ScanRequestType.
     * 
     * @return maximumTerms
     */
    public org.apache.axis.types.PositiveInteger getMaximumTerms() {
        return maximumTerms;
    }


    /**
     * Sets the maximumTerms value for this ScanRequestType.
     * 
     * @param maximumTerms
     */
    public void setMaximumTerms(org.apache.axis.types.PositiveInteger maximumTerms) {
        this.maximumTerms = maximumTerms;
    }


    /**
     * Gets the stylesheet value for this ScanRequestType.
     * 
     * @return stylesheet
     */
    public org.apache.axis.types.URI getStylesheet() {
        return stylesheet;
    }


    /**
     * Sets the stylesheet value for this ScanRequestType.
     * 
     * @param stylesheet
     */
    public void setStylesheet(org.apache.axis.types.URI stylesheet) {
        this.stylesheet = stylesheet;
    }


    /**
     * Gets the extraRequestData value for this ScanRequestType.
     * 
     * @return extraRequestData
     */
    public gov.loc.www.zing.srw.ExtraDataType getExtraRequestData() {
        return extraRequestData;
    }


    /**
     * Sets the extraRequestData value for this ScanRequestType.
     * 
     * @param extraRequestData
     */
    public void setExtraRequestData(gov.loc.www.zing.srw.ExtraDataType extraRequestData) {
        this.extraRequestData = extraRequestData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ScanRequestType)) return false;
        ScanRequestType other = (ScanRequestType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.scanClause==null && other.getScanClause()==null) || 
             (this.scanClause!=null &&
              this.scanClause.equals(other.getScanClause()))) &&
            ((this.responsePosition==null && other.getResponsePosition()==null) || 
             (this.responsePosition!=null &&
              this.responsePosition.equals(other.getResponsePosition()))) &&
            ((this.maximumTerms==null && other.getMaximumTerms()==null) || 
             (this.maximumTerms!=null &&
              this.maximumTerms.equals(other.getMaximumTerms()))) &&
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
        if (getScanClause() != null) {
            _hashCode += getScanClause().hashCode();
        }
        if (getResponsePosition() != null) {
            _hashCode += getResponsePosition().hashCode();
        }
        if (getMaximumTerms() != null) {
            _hashCode += getMaximumTerms().hashCode();
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
        new org.apache.axis.description.TypeDesc(ScanRequestType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "scanRequestType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scanClause");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "scanClause"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "scanClause"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responsePosition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "responsePosition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "responsePosition"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maximumTerms");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "maximumTerms"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "maximumTerms"));
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
