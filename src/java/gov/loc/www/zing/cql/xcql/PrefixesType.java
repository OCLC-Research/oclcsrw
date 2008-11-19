/**
 * PrefixesType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.cql.xcql;

public class PrefixesType  implements java.io.Serializable {
    private gov.loc.www.zing.cql.xcql.PrefixType[] prefix;

    public PrefixesType() {
    }

    public PrefixesType(
           gov.loc.www.zing.cql.xcql.PrefixType[] prefix) {
           this.prefix = prefix;
    }


    /**
     * Gets the prefix value for this PrefixesType.
     * 
     * @return prefix
     */
    public gov.loc.www.zing.cql.xcql.PrefixType[] getPrefix() {
        return prefix;
    }


    /**
     * Sets the prefix value for this PrefixesType.
     * 
     * @param prefix
     */
    public void setPrefix(gov.loc.www.zing.cql.xcql.PrefixType[] prefix) {
        this.prefix = prefix;
    }

    public gov.loc.www.zing.cql.xcql.PrefixType getPrefix(int i) {
        return this.prefix[i];
    }

    public void setPrefix(int i, gov.loc.www.zing.cql.xcql.PrefixType _value) {
        this.prefix[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PrefixesType)) return false;
        PrefixesType other = (PrefixesType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.prefix==null && other.getPrefix()==null) || 
             (this.prefix!=null &&
              java.util.Arrays.equals(this.prefix, other.getPrefix())));
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
        if (getPrefix() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPrefix());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPrefix(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PrefixesType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "prefixesType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("prefix");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "prefix"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "prefix"));
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
