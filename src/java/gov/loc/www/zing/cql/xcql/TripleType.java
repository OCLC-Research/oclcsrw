/**
 * TripleType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.cql.xcql;

public class TripleType  implements java.io.Serializable {
    private gov.loc.www.zing.cql.xcql.BooleanType _boolean;
    private gov.loc.www.zing.cql.xcql.OperandType leftOperand;
    private gov.loc.www.zing.cql.xcql.OperandType rightOperand;

    public TripleType() {
    }

    public TripleType(
           gov.loc.www.zing.cql.xcql.BooleanType _boolean,
           gov.loc.www.zing.cql.xcql.OperandType leftOperand,
           gov.loc.www.zing.cql.xcql.OperandType rightOperand) {
           this._boolean = _boolean;
           this.leftOperand = leftOperand;
           this.rightOperand = rightOperand;
    }


    /**
     * Gets the _boolean value for this TripleType.
     * 
     * @return _boolean
     */
    public gov.loc.www.zing.cql.xcql.BooleanType get_boolean() {
        return _boolean;
    }


    /**
     * Sets the _boolean value for this TripleType.
     * 
     * @param _boolean
     */
    public void set_boolean(gov.loc.www.zing.cql.xcql.BooleanType _boolean) {
        this._boolean = _boolean;
    }


    /**
     * Gets the leftOperand value for this TripleType.
     * 
     * @return leftOperand
     */
    public gov.loc.www.zing.cql.xcql.OperandType getLeftOperand() {
        return leftOperand;
    }


    /**
     * Sets the leftOperand value for this TripleType.
     * 
     * @param leftOperand
     */
    public void setLeftOperand(gov.loc.www.zing.cql.xcql.OperandType leftOperand) {
        this.leftOperand = leftOperand;
    }


    /**
     * Gets the rightOperand value for this TripleType.
     * 
     * @return rightOperand
     */
    public gov.loc.www.zing.cql.xcql.OperandType getRightOperand() {
        return rightOperand;
    }


    /**
     * Sets the rightOperand value for this TripleType.
     * 
     * @param rightOperand
     */
    public void setRightOperand(gov.loc.www.zing.cql.xcql.OperandType rightOperand) {
        this.rightOperand = rightOperand;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TripleType)) return false;
        TripleType other = (TripleType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this._boolean==null && other.get_boolean()==null) || 
             (this._boolean!=null &&
              this._boolean.equals(other.get_boolean()))) &&
            ((this.leftOperand==null && other.getLeftOperand()==null) || 
             (this.leftOperand!=null &&
              this.leftOperand.equals(other.getLeftOperand()))) &&
            ((this.rightOperand==null && other.getRightOperand()==null) || 
             (this.rightOperand!=null &&
              this.rightOperand.equals(other.getRightOperand())));
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
        if (get_boolean() != null) {
            _hashCode += get_boolean().hashCode();
        }
        if (getLeftOperand() != null) {
            _hashCode += getLeftOperand().hashCode();
        }
        if (getRightOperand() != null) {
            _hashCode += getRightOperand().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TripleType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "tripleType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_boolean");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "boolean"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("leftOperand");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "leftOperand"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "leftOperand"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rightOperand");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "rightOperand"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "rightOperand"));
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
