/**
 * TermType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.srw;

public class TermType  implements java.io.Serializable {
    private java.lang.String value;
    private org.apache.axis.types.NonNegativeInteger numberOfRecords;
    private java.lang.String displayTerm;
    private gov.loc.www.zing.srw.TermTypeWhereInList whereInList;
    private gov.loc.www.zing.srw.ExtraDataType extraTermData;

    public TermType() {
    }

    public TermType(
           java.lang.String value,
           org.apache.axis.types.NonNegativeInteger numberOfRecords,
           java.lang.String displayTerm,
           gov.loc.www.zing.srw.TermTypeWhereInList whereInList,
           gov.loc.www.zing.srw.ExtraDataType extraTermData) {
           this.value = value;
           this.numberOfRecords = numberOfRecords;
           this.displayTerm = displayTerm;
           this.whereInList = whereInList;
           this.extraTermData = extraTermData;
    }


    /**
     * Gets the value value for this TermType.
     * 
     * @return value
     */
    public java.lang.String getValue() {
        return value;
    }


    /**
     * Sets the value value for this TermType.
     * 
     * @param value
     */
    public void setValue(java.lang.String value) {
        this.value = value;
    }


    /**
     * Gets the numberOfRecords value for this TermType.
     * 
     * @return numberOfRecords
     */
    public org.apache.axis.types.NonNegativeInteger getNumberOfRecords() {
        return numberOfRecords;
    }


    /**
     * Sets the numberOfRecords value for this TermType.
     * 
     * @param numberOfRecords
     */
    public void setNumberOfRecords(org.apache.axis.types.NonNegativeInteger numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }


    /**
     * Gets the displayTerm value for this TermType.
     * 
     * @return displayTerm
     */
    public java.lang.String getDisplayTerm() {
        return displayTerm;
    }


    /**
     * Sets the displayTerm value for this TermType.
     * 
     * @param displayTerm
     */
    public void setDisplayTerm(java.lang.String displayTerm) {
        this.displayTerm = displayTerm;
    }


    /**
     * Gets the whereInList value for this TermType.
     * 
     * @return whereInList
     */
    public gov.loc.www.zing.srw.TermTypeWhereInList getWhereInList() {
        return whereInList;
    }


    /**
     * Sets the whereInList value for this TermType.
     * 
     * @param whereInList
     */
    public void setWhereInList(gov.loc.www.zing.srw.TermTypeWhereInList whereInList) {
        this.whereInList = whereInList;
    }


    /**
     * Gets the extraTermData value for this TermType.
     * 
     * @return extraTermData
     */
    public gov.loc.www.zing.srw.ExtraDataType getExtraTermData() {
        return extraTermData;
    }


    /**
     * Sets the extraTermData value for this TermType.
     * 
     * @param extraTermData
     */
    public void setExtraTermData(gov.loc.www.zing.srw.ExtraDataType extraTermData) {
        this.extraTermData = extraTermData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TermType)) return false;
        TermType other = (TermType) obj;
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
            ((this.numberOfRecords==null && other.getNumberOfRecords()==null) || 
             (this.numberOfRecords!=null &&
              this.numberOfRecords.equals(other.getNumberOfRecords()))) &&
            ((this.displayTerm==null && other.getDisplayTerm()==null) || 
             (this.displayTerm!=null &&
              this.displayTerm.equals(other.getDisplayTerm()))) &&
            ((this.whereInList==null && other.getWhereInList()==null) || 
             (this.whereInList!=null &&
              this.whereInList.equals(other.getWhereInList()))) &&
            ((this.extraTermData==null && other.getExtraTermData()==null) || 
             (this.extraTermData!=null &&
              this.extraTermData.equals(other.getExtraTermData())));
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
        if (getNumberOfRecords() != null) {
            _hashCode += getNumberOfRecords().hashCode();
        }
        if (getDisplayTerm() != null) {
            _hashCode += getDisplayTerm().hashCode();
        }
        if (getWhereInList() != null) {
            _hashCode += getWhereInList().hashCode();
        }
        if (getExtraTermData() != null) {
            _hashCode += getExtraTermData().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TermType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "termType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "value"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numberOfRecords");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "numberOfRecords"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "numberOfRecords"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("displayTerm");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "displayTerm"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "displayTerm"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("whereInList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "whereInList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", ">termType>whereInList"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extraTermData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "extraTermData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "extraTermData"));
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

    public String toString() {
        return value;
    }
}
