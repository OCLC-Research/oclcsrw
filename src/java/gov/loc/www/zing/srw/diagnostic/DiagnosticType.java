/**
 * DiagnosticType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.srw.diagnostic;

public class DiagnosticType  implements java.io.Serializable {
    private org.apache.axis.types.URI uri;
    private java.lang.String details;
    private java.lang.String message;

    public DiagnosticType() {
    }

    public DiagnosticType(
           org.apache.axis.types.URI uri,
           java.lang.String details,
           java.lang.String message) {
           this.uri = uri;
           this.details = details;
           this.message = message;
    }


    /**
     * Gets the uri value for this DiagnosticType.
     * 
     * @return uri
     */
    public org.apache.axis.types.URI getUri() {
        return uri;
    }


    /**
     * Sets the uri value for this DiagnosticType.
     * 
     * @param uri
     */
    public void setUri(org.apache.axis.types.URI uri) {
        this.uri = uri;
    }


    /**
     * Gets the details value for this DiagnosticType.
     * 
     * @return details
     */
    public java.lang.String getDetails() {
        return details;
    }


    /**
     * Sets the details value for this DiagnosticType.
     * 
     * @param details
     */
    public void setDetails(java.lang.String details) {
        this.details = details;
    }


    /**
     * Gets the message value for this DiagnosticType.
     * 
     * @return message
     */
    public java.lang.String getMessage() {
        return message;
    }


    /**
     * Sets the message value for this DiagnosticType.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DiagnosticType)) return false;
        DiagnosticType other = (DiagnosticType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.uri==null && other.getUri()==null) || 
             (this.uri!=null &&
              this.uri.equals(other.getUri()))) &&
            ((this.details==null && other.getDetails()==null) || 
             (this.details!=null &&
              this.details.equals(other.getDetails()))) &&
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getUri() != null) {
            _hashCode += getUri().hashCode();
        }
        if (getDetails() != null) {
            _hashCode += getDetails().hashCode();
        }
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DiagnosticType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/diagnostic/", "diagnosticType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("uri");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/diagnostic/", "uri"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/diagnostic/", "uri"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("details");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/diagnostic/", "details"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/diagnostic/", "details"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/diagnostic/", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/diagnostic/", "message"));
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
