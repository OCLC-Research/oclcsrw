/**
 * SortKeyType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.srw;

public class SortKeyType  implements java.io.Serializable {
    private java.lang.String path;
    private java.lang.String schema;
    private java.lang.Boolean ascending;
    private java.lang.Boolean caseSensitive;
    private java.lang.String missingValue;

    public SortKeyType() {
    }

    public SortKeyType(
           java.lang.String path,
           java.lang.String schema,
           java.lang.Boolean ascending,
           java.lang.Boolean caseSensitive,
           java.lang.String missingValue) {
           this.path = path;
           this.schema = schema;
           this.ascending = ascending;
           this.caseSensitive = caseSensitive;
           this.missingValue = missingValue;
    }


    /**
     * Gets the path value for this SortKeyType.
     * 
     * @return path
     */
    public java.lang.String getPath() {
        return path;
    }


    /**
     * Sets the path value for this SortKeyType.
     * 
     * @param path
     */
    public void setPath(java.lang.String path) {
        this.path = path;
    }


    /**
     * Gets the schema value for this SortKeyType.
     * 
     * @return schema
     */
    public java.lang.String getSchema() {
        return schema;
    }


    /**
     * Sets the schema value for this SortKeyType.
     * 
     * @param schema
     */
    public void setSchema(java.lang.String schema) {
        this.schema = schema;
    }


    /**
     * Gets the ascending value for this SortKeyType.
     * 
     * @return ascending
     */
    public java.lang.Boolean getAscending() {
        return ascending;
    }


    /**
     * Sets the ascending value for this SortKeyType.
     * 
     * @param ascending
     */
    public void setAscending(java.lang.Boolean ascending) {
        this.ascending = ascending;
    }


    /**
     * Gets the caseSensitive value for this SortKeyType.
     * 
     * @return caseSensitive
     */
    public java.lang.Boolean getCaseSensitive() {
        return caseSensitive;
    }


    /**
     * Sets the caseSensitive value for this SortKeyType.
     * 
     * @param caseSensitive
     */
    public void setCaseSensitive(java.lang.Boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }


    /**
     * Gets the missingValue value for this SortKeyType.
     * 
     * @return missingValue
     */
    public java.lang.String getMissingValue() {
        return missingValue;
    }


    /**
     * Sets the missingValue value for this SortKeyType.
     * 
     * @param missingValue
     */
    public void setMissingValue(java.lang.String missingValue) {
        this.missingValue = missingValue;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SortKeyType)) return false;
        SortKeyType other = (SortKeyType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.path==null && other.getPath()==null) || 
             (this.path!=null &&
              this.path.equals(other.getPath()))) &&
            ((this.schema==null && other.getSchema()==null) || 
             (this.schema!=null &&
              this.schema.equals(other.getSchema()))) &&
            ((this.ascending==null && other.getAscending()==null) || 
             (this.ascending!=null &&
              this.ascending.equals(other.getAscending()))) &&
            ((this.caseSensitive==null && other.getCaseSensitive()==null) || 
             (this.caseSensitive!=null &&
              this.caseSensitive.equals(other.getCaseSensitive()))) &&
            ((this.missingValue==null && other.getMissingValue()==null) || 
             (this.missingValue!=null &&
              this.missingValue.equals(other.getMissingValue())));
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
        if (getPath() != null) {
            _hashCode += getPath().hashCode();
        }
        if (getSchema() != null) {
            _hashCode += getSchema().hashCode();
        }
        if (getAscending() != null) {
            _hashCode += getAscending().hashCode();
        }
        if (getCaseSensitive() != null) {
            _hashCode += getCaseSensitive().hashCode();
        }
        if (getMissingValue() != null) {
            _hashCode += getMissingValue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SortKeyType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "sortKeyType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("path");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "path"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "path"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("schema");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "schema"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "schema"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ascending");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "ascending"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "ascending"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("caseSensitive");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "caseSensitive"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "caseSensitive"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("missingValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "missingValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "missingValue"));
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
