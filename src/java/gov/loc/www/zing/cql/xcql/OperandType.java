/**
 * OperandType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.cql.xcql;

public class OperandType  implements java.io.Serializable {
    private gov.loc.www.zing.cql.xcql.PrefixesType prefixes;
    private gov.loc.www.zing.cql.xcql.TripleType triple;
    private gov.loc.www.zing.cql.xcql.SearchClauseType searchClause;

    public OperandType() {
    }

    public OperandType(
           gov.loc.www.zing.cql.xcql.PrefixesType prefixes,
           gov.loc.www.zing.cql.xcql.TripleType triple,
           gov.loc.www.zing.cql.xcql.SearchClauseType searchClause) {
           this.prefixes = prefixes;
           this.triple = triple;
           this.searchClause = searchClause;
    }


    /**
     * Gets the prefixes value for this OperandType.
     * 
     * @return prefixes
     */
    public gov.loc.www.zing.cql.xcql.PrefixesType getPrefixes() {
        return prefixes;
    }


    /**
     * Sets the prefixes value for this OperandType.
     * 
     * @param prefixes
     */
    public void setPrefixes(gov.loc.www.zing.cql.xcql.PrefixesType prefixes) {
        this.prefixes = prefixes;
    }


    /**
     * Gets the triple value for this OperandType.
     * 
     * @return triple
     */
    public gov.loc.www.zing.cql.xcql.TripleType getTriple() {
        return triple;
    }


    /**
     * Sets the triple value for this OperandType.
     * 
     * @param triple
     */
    public void setTriple(gov.loc.www.zing.cql.xcql.TripleType triple) {
        this.triple = triple;
    }


    /**
     * Gets the searchClause value for this OperandType.
     * 
     * @return searchClause
     */
    public gov.loc.www.zing.cql.xcql.SearchClauseType getSearchClause() {
        return searchClause;
    }


    /**
     * Sets the searchClause value for this OperandType.
     * 
     * @param searchClause
     */
    public void setSearchClause(gov.loc.www.zing.cql.xcql.SearchClauseType searchClause) {
        this.searchClause = searchClause;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OperandType)) return false;
        OperandType other = (OperandType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.prefixes==null && other.getPrefixes()==null) || 
             (this.prefixes!=null &&
              this.prefixes.equals(other.getPrefixes()))) &&
            ((this.triple==null && other.getTriple()==null) || 
             (this.triple!=null &&
              this.triple.equals(other.getTriple()))) &&
            ((this.searchClause==null && other.getSearchClause()==null) || 
             (this.searchClause!=null &&
              this.searchClause.equals(other.getSearchClause())));
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
        if (getPrefixes() != null) {
            _hashCode += getPrefixes().hashCode();
        }
        if (getTriple() != null) {
            _hashCode += getTriple().hashCode();
        }
        if (getSearchClause() != null) {
            _hashCode += getSearchClause().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OperandType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "operandType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("prefixes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "prefixes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "prefixes"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("triple");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "triple"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "triple"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchClause");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "searchClause"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "searchClause"));
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
