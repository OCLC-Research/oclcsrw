/**
 * RecordType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.srw;

public class RecordType  implements java.io.Serializable {
    private java.lang.String recordSchema;
    private java.lang.String recordPacking;
    private gov.loc.www.zing.srw.StringOrXmlFragment recordData;
    private org.apache.axis.types.PositiveInteger recordPosition;
    private gov.loc.www.zing.srw.ExtraDataType extraRecordData;

    public RecordType() {
    }

    public RecordType(
           java.lang.String recordSchema,
           java.lang.String recordPacking,
           gov.loc.www.zing.srw.StringOrXmlFragment recordData,
           org.apache.axis.types.PositiveInteger recordPosition,
           gov.loc.www.zing.srw.ExtraDataType extraRecordData) {
           this.recordSchema = recordSchema;
           this.recordPacking = recordPacking;
           this.recordData = recordData;
           this.recordPosition = recordPosition;
           this.extraRecordData = extraRecordData;
    }


    /**
     * Gets the recordSchema value for this RecordType.
     * 
     * @return recordSchema
     */
    public java.lang.String getRecordSchema() {
        return recordSchema;
    }


    /**
     * Sets the recordSchema value for this RecordType.
     * 
     * @param recordSchema
     */
    public void setRecordSchema(java.lang.String recordSchema) {
        this.recordSchema = recordSchema;
    }


    /**
     * Gets the recordPacking value for this RecordType.
     * 
     * @return recordPacking
     */
    public java.lang.String getRecordPacking() {
        return recordPacking;
    }


    /**
     * Sets the recordPacking value for this RecordType.
     * 
     * @param recordPacking
     */
    public void setRecordPacking(java.lang.String recordPacking) {
        this.recordPacking = recordPacking;
    }


    /**
     * Gets the recordData value for this RecordType.
     * 
     * @return recordData
     */
    public gov.loc.www.zing.srw.StringOrXmlFragment getRecordData() {
        return recordData;
    }


    /**
     * Sets the recordData value for this RecordType.
     * 
     * @param recordData
     */
    public void setRecordData(gov.loc.www.zing.srw.StringOrXmlFragment recordData) {
        this.recordData = recordData;
    }


    /**
     * Gets the recordPosition value for this RecordType.
     * 
     * @return recordPosition
     */
    public org.apache.axis.types.PositiveInteger getRecordPosition() {
        return recordPosition;
    }


    /**
     * Sets the recordPosition value for this RecordType.
     * 
     * @param recordPosition
     */
    public void setRecordPosition(org.apache.axis.types.PositiveInteger recordPosition) {
        this.recordPosition = recordPosition;
    }


    /**
     * Gets the extraRecordData value for this RecordType.
     * 
     * @return extraRecordData
     */
    public gov.loc.www.zing.srw.ExtraDataType getExtraRecordData() {
        return extraRecordData;
    }


    /**
     * Sets the extraRecordData value for this RecordType.
     * 
     * @param extraRecordData
     */
    public void setExtraRecordData(gov.loc.www.zing.srw.ExtraDataType extraRecordData) {
        this.extraRecordData = extraRecordData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RecordType)) return false;
        RecordType other = (RecordType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.recordSchema==null && other.getRecordSchema()==null) || 
             (this.recordSchema!=null &&
              this.recordSchema.equals(other.getRecordSchema()))) &&
            ((this.recordPacking==null && other.getRecordPacking()==null) || 
             (this.recordPacking!=null &&
              this.recordPacking.equals(other.getRecordPacking()))) &&
            ((this.recordData==null && other.getRecordData()==null) || 
             (this.recordData!=null &&
              this.recordData.equals(other.getRecordData()))) &&
            ((this.recordPosition==null && other.getRecordPosition()==null) || 
             (this.recordPosition!=null &&
              this.recordPosition.equals(other.getRecordPosition()))) &&
            ((this.extraRecordData==null && other.getExtraRecordData()==null) || 
             (this.extraRecordData!=null &&
              this.extraRecordData.equals(other.getExtraRecordData())));
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
        if (getRecordSchema() != null) {
            _hashCode += getRecordSchema().hashCode();
        }
        if (getRecordPacking() != null) {
            _hashCode += getRecordPacking().hashCode();
        }
        if (getRecordData() != null) {
            _hashCode += getRecordData().hashCode();
        }
        if (getRecordPosition() != null) {
            _hashCode += getRecordPosition().hashCode();
        }
        if (getExtraRecordData() != null) {
            _hashCode += getExtraRecordData().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RecordType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordSchema");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordSchema"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordSchema"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordPacking");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordPacking"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordPacking"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordData"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordPosition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordPosition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordPosition"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extraRecordData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "extraRecordData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "extraRecordData"));
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
