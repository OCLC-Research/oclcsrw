/**
 * SearchRetrieveResponseType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.srw;

public class SearchRetrieveResponseType  extends gov.loc.www.zing.srw.ResponseType  implements java.io.Serializable {
    private org.apache.axis.types.NonNegativeInteger numberOfRecords;
    private java.lang.String resultSetId;
    private org.apache.axis.types.PositiveInteger resultSetIdleTime;
    private gov.loc.www.zing.srw.RecordsType records;
    private org.apache.axis.types.PositiveInteger nextRecordPosition;
    private gov.loc.www.zing.srw.EchoedSearchRetrieveRequestType echoedSearchRetrieveRequest;
    private gov.loc.www.zing.srw.DiagnosticsType diagnostics;
    private gov.loc.www.zing.srw.ExtraDataType extraResponseData;

    public SearchRetrieveResponseType() {
    }

    public SearchRetrieveResponseType(
           org.apache.axis.types.NonNegativeInteger numberOfRecords,
           java.lang.String resultSetId,
           org.apache.axis.types.PositiveInteger resultSetIdleTime,
           gov.loc.www.zing.srw.RecordsType records,
           org.apache.axis.types.PositiveInteger nextRecordPosition,
           gov.loc.www.zing.srw.EchoedSearchRetrieveRequestType echoedSearchRetrieveRequest,
           gov.loc.www.zing.srw.DiagnosticsType diagnostics,
           gov.loc.www.zing.srw.ExtraDataType extraResponseData) {
           this.numberOfRecords = numberOfRecords;
           this.resultSetId = resultSetId;
           this.resultSetIdleTime = resultSetIdleTime;
           this.records = records;
           this.nextRecordPosition = nextRecordPosition;
           this.echoedSearchRetrieveRequest = echoedSearchRetrieveRequest;
           this.diagnostics = diagnostics;
           this.extraResponseData = extraResponseData;
    }


    /**
     * Gets the numberOfRecords value for this SearchRetrieveResponseType.
     * 
     * @return numberOfRecords
     */
    public org.apache.axis.types.NonNegativeInteger getNumberOfRecords() {
        return numberOfRecords;
    }


    /**
     * Sets the numberOfRecords value for this SearchRetrieveResponseType.
     * 
     * @param numberOfRecords
     */
    public void setNumberOfRecords(org.apache.axis.types.NonNegativeInteger numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }


    /**
     * Gets the resultSetId value for this SearchRetrieveResponseType.
     * 
     * @return resultSetId
     */
    public java.lang.String getResultSetId() {
        return resultSetId;
    }


    /**
     * Sets the resultSetId value for this SearchRetrieveResponseType.
     * 
     * @param resultSetId
     */
    public void setResultSetId(java.lang.String resultSetId) {
        this.resultSetId = resultSetId;
    }


    /**
     * Gets the resultSetIdleTime value for this SearchRetrieveResponseType.
     * 
     * @return resultSetIdleTime
     */
    public org.apache.axis.types.PositiveInteger getResultSetIdleTime() {
        return resultSetIdleTime;
    }


    /**
     * Sets the resultSetIdleTime value for this SearchRetrieveResponseType.
     * 
     * @param resultSetIdleTime
     */
    public void setResultSetIdleTime(org.apache.axis.types.PositiveInteger resultSetIdleTime) {
        this.resultSetIdleTime = resultSetIdleTime;
    }


    /**
     * Gets the records value for this SearchRetrieveResponseType.
     * 
     * @return records
     */
    public gov.loc.www.zing.srw.RecordsType getRecords() {
        return records;
    }


    /**
     * Sets the records value for this SearchRetrieveResponseType.
     * 
     * @param records
     */
    public void setRecords(gov.loc.www.zing.srw.RecordsType records) {
        this.records = records;
    }


    /**
     * Gets the nextRecordPosition value for this SearchRetrieveResponseType.
     * 
     * @return nextRecordPosition
     */
    public org.apache.axis.types.PositiveInteger getNextRecordPosition() {
        return nextRecordPosition;
    }


    /**
     * Sets the nextRecordPosition value for this SearchRetrieveResponseType.
     * 
     * @param nextRecordPosition
     */
    public void setNextRecordPosition(org.apache.axis.types.PositiveInteger nextRecordPosition) {
        this.nextRecordPosition = nextRecordPosition;
    }


    /**
     * Gets the echoedSearchRetrieveRequest value for this SearchRetrieveResponseType.
     * 
     * @return echoedSearchRetrieveRequest
     */
    public gov.loc.www.zing.srw.EchoedSearchRetrieveRequestType getEchoedSearchRetrieveRequest() {
        return echoedSearchRetrieveRequest;
    }


    /**
     * Sets the echoedSearchRetrieveRequest value for this SearchRetrieveResponseType.
     * 
     * @param echoedSearchRetrieveRequest
     */
    public void setEchoedSearchRetrieveRequest(gov.loc.www.zing.srw.EchoedSearchRetrieveRequestType echoedSearchRetrieveRequest) {
        this.echoedSearchRetrieveRequest = echoedSearchRetrieveRequest;
    }


    /**
     * Gets the diagnostics value for this SearchRetrieveResponseType.
     * 
     * @return diagnostics
     */
    public gov.loc.www.zing.srw.DiagnosticsType getDiagnostics() {
        return diagnostics;
    }


    /**
     * Sets the diagnostics value for this SearchRetrieveResponseType.
     * 
     * @param diagnostics
     */
    public void setDiagnostics(gov.loc.www.zing.srw.DiagnosticsType diagnostics) {
        this.diagnostics = diagnostics;
    }


    /**
     * Gets the extraResponseData value for this SearchRetrieveResponseType.
     * 
     * @return extraResponseData
     */
    public gov.loc.www.zing.srw.ExtraDataType getExtraResponseData() {
        return extraResponseData;
    }


    /**
     * Sets the extraResponseData value for this SearchRetrieveResponseType.
     * 
     * @param extraResponseData
     */
    public void setExtraResponseData(gov.loc.www.zing.srw.ExtraDataType extraResponseData) {
        this.extraResponseData = extraResponseData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SearchRetrieveResponseType)) return false;
        SearchRetrieveResponseType other = (SearchRetrieveResponseType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.numberOfRecords==null && other.getNumberOfRecords()==null) || 
             (this.numberOfRecords!=null &&
              this.numberOfRecords.equals(other.getNumberOfRecords()))) &&
            ((this.resultSetId==null && other.getResultSetId()==null) || 
             (this.resultSetId!=null &&
              this.resultSetId.equals(other.getResultSetId()))) &&
            ((this.resultSetIdleTime==null && other.getResultSetIdleTime()==null) || 
             (this.resultSetIdleTime!=null &&
              this.resultSetIdleTime.equals(other.getResultSetIdleTime()))) &&
            ((this.records==null && other.getRecords()==null) || 
             (this.records!=null &&
              this.records.equals(other.getRecords()))) &&
            ((this.nextRecordPosition==null && other.getNextRecordPosition()==null) || 
             (this.nextRecordPosition!=null &&
              this.nextRecordPosition.equals(other.getNextRecordPosition()))) &&
            ((this.echoedSearchRetrieveRequest==null && other.getEchoedSearchRetrieveRequest()==null) || 
             (this.echoedSearchRetrieveRequest!=null &&
              this.echoedSearchRetrieveRequest.equals(other.getEchoedSearchRetrieveRequest()))) &&
            ((this.diagnostics==null && other.getDiagnostics()==null) || 
             (this.diagnostics!=null &&
              this.diagnostics.equals(other.getDiagnostics()))) &&
            ((this.extraResponseData==null && other.getExtraResponseData()==null) || 
             (this.extraResponseData!=null &&
              this.extraResponseData.equals(other.getExtraResponseData())));
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
        if (getNumberOfRecords() != null) {
            _hashCode += getNumberOfRecords().hashCode();
        }
        if (getResultSetId() != null) {
            _hashCode += getResultSetId().hashCode();
        }
        if (getResultSetIdleTime() != null) {
            _hashCode += getResultSetIdleTime().hashCode();
        }
        if (getRecords() != null) {
            _hashCode += getRecords().hashCode();
        }
        if (getNextRecordPosition() != null) {
            _hashCode += getNextRecordPosition().hashCode();
        }
        if (getEchoedSearchRetrieveRequest() != null) {
            _hashCode += getEchoedSearchRetrieveRequest().hashCode();
        }
        if (getDiagnostics() != null) {
            _hashCode += getDiagnostics().hashCode();
        }
        if (getExtraResponseData() != null) {
            _hashCode += getExtraResponseData().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SearchRetrieveResponseType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "searchRetrieveResponseType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numberOfRecords");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "numberOfRecords"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "numberOfRecords"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultSetId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "resultSetId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "resultSetId"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultSetIdleTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "resultSetIdleTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "resultSetIdleTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("records");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "records"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "records"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nextRecordPosition");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "nextRecordPosition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "nextRecordPosition"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("echoedSearchRetrieveRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "echoedSearchRetrieveRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "echoedSearchRetrieveRequest"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("diagnostics");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "diagnostics"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "diagnostics"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extraResponseData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "extraResponseData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "extraResponseData"));
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
