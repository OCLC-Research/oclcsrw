/**
 * BooleanType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.cql.xcql;

public class BooleanType  implements java.io.Serializable {
    private java.lang.String value;
    private gov.loc.www.zing.cql.xcql.ModifiersType modifiers;

    public BooleanType() {
    }

    public BooleanType(
           java.lang.String value,
           gov.loc.www.zing.cql.xcql.ModifiersType modifiers) {
           this.value = value;
           this.modifiers = modifiers;
    }


    /**
     * Gets the value value for this BooleanType.
     * 
     * @return value
     */
    public java.lang.String getValue() {
        return value;
    }


    /**
     * Sets the value value for this BooleanType.
     * 
     * @param value
     */
    public void setValue(java.lang.String value) {
        this.value = value;
    }


    /**
     * Gets the modifiers value for this BooleanType.
     * 
     * @return modifiers
     */
    public gov.loc.www.zing.cql.xcql.ModifiersType getModifiers() {
        return modifiers;
    }


    /**
     * Sets the modifiers value for this BooleanType.
     * 
     * @param modifiers
     */
    public void setModifiers(gov.loc.www.zing.cql.xcql.ModifiersType modifiers) {
        this.modifiers = modifiers;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BooleanType)) return false;
        BooleanType other = (BooleanType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.value==null && other.getValue()==null) || 
             (this.value!=null &&
              this.value.equals(other.getValue()))) &&
            ((this.modifiers==null && other.getModifiers()==null) || 
             (this.modifiers!=null &&
              this.modifiers.equals(other.getModifiers())));
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
        if (getValue() != null) {
            _hashCode += getValue().hashCode();
        }
        if (getModifiers() != null) {
            _hashCode += getModifiers().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BooleanType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "booleanType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "value"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("modifiers");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "modifiers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "modifiers"));
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
