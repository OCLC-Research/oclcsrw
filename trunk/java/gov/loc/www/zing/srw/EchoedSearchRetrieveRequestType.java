/**
 * EchoedSearchRetrieveRequestType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.srw;

public class EchoedSearchRetrieveRequestType  extends gov.loc.www.zing.srw.RequestType  implements java.io.Serializable {
    private java.lang.String query;
    private gov.loc.www.zing.cql.xcql.OperandType xQuery;
    private org.apache.axis.types.PositiveInteger startRecord;
    private org.apache.axis.types.NonNegativeInteger maximumRecords;
    private java.lang.String recordPacking;
    private java.lang.String recordSchema;
    private java.lang.String recordXPath;
    private org.apache.axis.types.NonNegativeInteger resultSetTTL;
    private java.lang.String sortKeys;
    private gov.loc.www.zing.srw.XSortKeysType xSortKeys;
    private org.apache.axis.types.URI stylesheet;
    private gov.loc.www.zing.srw.ExtraDataType extraRequestData;

    public EchoedSearchRetrieveRequestType() {
    }

    public EchoedSearchRetrieveRequestType(
           java.lang.String query,
           gov.loc.www.zing.cql.xcql.OperandType xQuery,
           org.apache.axis.types.PositiveInteger startRecord,
           org.apache.axis.types.NonNegativeInteger maximumRecords,
           java.lang.String recordPacking,
           java.lang.String recordSchema,
           java.lang.String recordXPath,
           org.apache.axis.types.NonNegativeInteger resultSetTTL,
           java.lang.String sortKeys,
           gov.loc.www.zing.srw.XSortKeysType xSortKeys,
           org.apache.axis.types.URI stylesheet,
           gov.loc.www.zing.srw.ExtraDataType extraRequestData) {
           this.query = query;
           this.xQuery = xQuery;
           this.startRecord = startRecord;
           this.maximumRecords = maximumRecords;
           this.recordPacking = recordPacking;
           this.recordSchema = recordSchema;
           this.recordXPath = recordXPath;
           this.resultSetTTL = resultSetTTL;
           this.sortKeys = sortKeys;
           this.xSortKeys = xSortKeys;
           this.stylesheet = stylesheet;
           this.extraRequestData = extraRequestData;
    }


    /**
     * Gets the query value for this EchoedSearchRetrieveRequestType.
     * 
     * @return query
     */
    public java.lang.String getQuery() {
        return query;
    }


    /**
     * Sets the query value for this EchoedSearchRetrieveRequestType.
     * 
     * @param query
     */
    public void setQuery(java.lang.String query) {
        this.query = query;
    }


    /**
     * Gets the xQuery value for this EchoedSearchRetrieveRequestType.
     * 
     * @return xQuery
     */
    public gov.loc.www.zing.cql.xcql.OperandType getXQuery() {
        return xQuery;
    }


    /**
     * Sets the xQuery value for this EchoedSearchRetrieveRequestType.
     * 
     * @param xQuery
     */
    public void setXQuery(gov.loc.www.zing.cql.xcql.OperandType xQuery) {
        this.xQuery = xQuery;
    }


    /**
     * Gets the startRecord value for this EchoedSearchRetrieveRequestType.
     * 
     * @return startRecord
     */
    public org.apache.axis.types.PositiveInteger getStartRecord() {
        return startRecord;
    }


    /**
     * Sets the startRecord value for this EchoedSearchRetrieveRequestType.
     * 
     * @param startRecord
     */
    public void setStartRecord(org.apache.axis.types.PositiveInteger startRecord) {
        this.startRecord = startRecord;
    }


    /**
     * Gets the maximumRecords value for this EchoedSearchRetrieveRequestType.
     * 
     * @return maximumRecords
     */
    public org.apache.axis.types.NonNegativeInteger getMaximumRecords() {
        return maximumRecords;
    }


    /**
     * Sets the maximumRecords value for this EchoedSearchRetrieveRequestType.
     * 
     * @param maximumRecords
     */
    public void setMaximumRecords(org.apache.axis.types.NonNegativeInteger maximumRecords) {
        this.maximumRecords = maximumRecords;
    }


    /**
     * Gets the recordPacking value for this EchoedSearchRetrieveRequestType.
     * 
     * @return recordPacking
     */
    public java.lang.String getRecordPacking() {
        return recordPacking;
    }


    /**
     * Sets the recordPacking value for this EchoedSearchRetrieveRequestType.
     * 
     * @param recordPacking
     */
    public void setRecordPacking(java.lang.String recordPacking) {
        this.recordPacking = recordPacking;
    }


    /**
     * Gets the recordSchema value for this EchoedSearchRetrieveRequestType.
     * 
     * @return recordSchema
     */
    public java.lang.String getRecordSchema() {
        return recordSchema;
    }


    /**
     * Sets the recordSchema value for this EchoedSearchRetrieveRequestType.
     * 
     * @param recordSchema
     */
    public void setRecordSchema(java.lang.String recordSchema) {
        this.recordSchema = recordSchema;
    }


    /**
     * Gets the recordXPath value for this EchoedSearchRetrieveRequestType.
     * 
     * @return recordXPath
     */
    public java.lang.String getRecordXPath() {
        return recordXPath;
    }


    /**
     * Sets the recordXPath value for this EchoedSearchRetrieveRequestType.
     * 
     * @param recordXPath
     */
    public void setRecordXPath(java.lang.String recordXPath) {
        this.recordXPath = recordXPath;
    }


    /**
     * Gets the resultSetTTL value for this EchoedSearchRetrieveRequestType.
     * 
     * @return resultSetTTL
     */
    public org.apache.axis.types.NonNegativeInteger getResultSetTTL() {
        return resultSetTTL;
    }


    /**
     * Sets the resultSetTTL value for this EchoedSearchRetrieveRequestType.
     * 
     * @param resultSetTTL
     */
    public void setResultSetTTL(org.apache.axis.types.NonNegativeInteger resultSetTTL) {
        this.resultSetTTL = resultSetTTL;
    }


    /**
     * Gets the sortKeys value for this EchoedSearchRetrieveRequestType.
     * 
     * @return sortKeys
     */
    public java.lang.String getSortKeys() {
        return sortKeys;
    }


    /**
     * Sets the sortKeys value for this EchoedSearchRetrieveRequestType.
     * 
     * @param sortKeys
     */
    public void setSortKeys(java.lang.String sortKeys) {
        this.sortKeys = sortKeys;
    }


    /**
     * Gets the xSortKeys value for this EchoedSearchRetrieveRequestType.
     * 
     * @return xSortKeys
     */
    public gov.loc.www.zing.srw.XSortKeysType getXSortKeys() {
        return xSortKeys;
    }


    /**
     * Sets the xSortKeys value for this EchoedSearchRetrieveRequestType.
     * 
     * @param xSortKeys
     */
    public void setXSortKeys(gov.loc.www.zing.srw.XSortKeysType xSortKeys) {
        this.xSortKeys = xSortKeys;
    }


    /**
     * Gets the stylesheet value for this EchoedSearchRetrieveRequestType.
     * 
     * @return stylesheet
     */
    public org.apache.axis.types.URI getStylesheet() {
        return stylesheet;
    }


    /**
     * Sets the stylesheet value for this EchoedSearchRetrieveRequestType.
     * 
     * @param stylesheet
     */
    public void setStylesheet(org.apache.axis.types.URI stylesheet) {
        this.stylesheet = stylesheet;
    }


    /**
     * Gets the extraRequestData value for this EchoedSearchRetrieveRequestType.
     * 
     * @return extraRequestData
     */
    public gov.loc.www.zing.srw.ExtraDataType getExtraRequestData() {
        return extraRequestData;
    }


    /**
     * Sets the extraRequestData value for this EchoedSearchRetrieveRequestType.
     * 
     * @param extraRequestData
     */
    public void setExtraRequestData(gov.loc.www.zing.srw.ExtraDataType extraRequestData) {
        this.extraRequestData = extraRequestData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EchoedSearchRetrieveRequestType)) return false;
        EchoedSearchRetrieveRequestType other = (EchoedSearchRetrieveRequestType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.query==null && other.getQuery()==null) || 
             (this.query!=null &&
              this.query.equals(other.getQuery()))) &&
            ((this.xQuery==null && other.getXQuery()==null) || 
             (this.xQuery!=null &&
              this.xQuery.equals(other.getXQuery()))) &&
            ((this.startRecord==null && other.getStartRecord()==null) || 
             (this.startRecord!=null &&
              this.startRecord.equals(other.getStartRecord()))) &&
            ((this.maximumRecords==null && other.getMaximumRecords()==null) || 
             (this.maximumRecords!=null &&
              this.maximumRecords.equals(other.getMaximumRecords()))) &&
            ((this.recordPacking==null && other.getRecordPacking()==null) || 
             (this.recordPacking!=null &&
              this.recordPacking.equals(other.getRecordPacking()))) &&
            ((this.recordSchema==null && other.getRecordSchema()==null) || 
             (this.recordSchema!=null &&
              this.recordSchema.equals(other.getRecordSchema()))) &&
            ((this.recordXPath==null && other.getRecordXPath()==null) || 
             (this.recordXPath!=null &&
              this.recordXPath.equals(other.getRecordXPath()))) &&
            ((this.resultSetTTL==null && other.getResultSetTTL()==null) || 
             (this.resultSetTTL!=null &&
              this.resultSetTTL.equals(other.getResultSetTTL()))) &&
            ((this.sortKeys==null && other.getSortKeys()==null) || 
             (this.sortKeys!=null &&
              this.sortKeys.equals(other.getSortKeys()))) &&
            ((this.xSortKeys==null && other.getXSortKeys()==null) || 
             (this.xSortKeys!=null &&
              this.xSortKeys.equals(other.getXSortKeys()))) &&
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
        if (getQuery() != null) {
            _hashCode += getQuery().hashCode();
        }
        if (getXQuery() != null) {
            _hashCode += getXQuery().hashCode();
        }
        if (getStartRecord() != null) {
            _hashCode += getStartRecord().hashCode();
        }
        if (getMaximumRecords() != null) {
            _hashCode += getMaximumRecords().hashCode();
        }
        if (getRecordPacking() != null) {
            _hashCode += getRecordPacking().hashCode();
        }
        if (getRecordSchema() != null) {
            _hashCode += getRecordSchema().hashCode();
        }
        if (getRecordXPath() != null) {
            _hashCode += getRecordXPath().hashCode();
        }
        if (getResultSetTTL() != null) {
            _hashCode += getResultSetTTL().hashCode();
        }
        if (getSortKeys() != null) {
            _hashCode += getSortKeys().hashCode();
        }
        if (getXSortKeys() != null) {
            _hashCode += getXSortKeys().hashCode();
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
        new org.apache.axis.description.TypeDesc(EchoedSearchRetrieveRequestType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "echoedSearchRetrieveRequestType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("query");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "query"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "query"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XQuery");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "xQuery"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/cql/xcql/", "operandType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startRecord");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "startRecord"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "startRecord"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maximumRecords");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "maximumRecords"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "maximumRecords"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordPacking");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordPacking"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordPacking"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordSchema");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordSchema"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordSchema"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordXPath");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordXPath"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "recordXPath"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultSetTTL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "resultSetTTL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "resultSetTTL"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sortKeys");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "sortKeys"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "sortKeys"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XSortKeys");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "xSortKeys"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "xSortKeysType"));
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
