/**
 * DiagnosticsType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package gov.loc.www.zing.srw;

public class DiagnosticsType  implements java.io.Serializable {
    private gov.loc.www.zing.srw.diagnostic.DiagnosticType[] diagnostic;

    public DiagnosticsType() {
    }

    public DiagnosticsType(
           gov.loc.www.zing.srw.diagnostic.DiagnosticType[] diagnostic) {
           this.diagnostic = diagnostic;
    }


    /**
     * Gets the diagnostic value for this DiagnosticsType.
     * 
     * @return diagnostic
     */
    public gov.loc.www.zing.srw.diagnostic.DiagnosticType[] getDiagnostic() {
        return diagnostic;
    }


    /**
     * Sets the diagnostic value for this DiagnosticsType.
     * 
     * @param diagnostic
     */
    public void setDiagnostic(gov.loc.www.zing.srw.diagnostic.DiagnosticType[] diagnostic) {
        this.diagnostic = diagnostic;
    }

    public gov.loc.www.zing.srw.diagnostic.DiagnosticType getDiagnostic(int i) {
        return this.diagnostic[i];
    }

    public void setDiagnostic(int i, gov.loc.www.zing.srw.diagnostic.DiagnosticType _value) {
        this.diagnostic[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DiagnosticsType)) return false;
        DiagnosticsType other = (DiagnosticsType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.diagnostic==null && other.getDiagnostic()==null) || 
             (this.diagnostic!=null &&
              java.util.Arrays.equals(this.diagnostic, other.getDiagnostic())));
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
        if (getDiagnostic() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDiagnostic());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDiagnostic(), i);
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
        new org.apache.axis.description.TypeDesc(DiagnosticsType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/", "diagnosticsType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("diagnostic");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/diagnostic/", "diagnostic"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.loc.gov/zing/srw/diagnostic/", "diagnostic"));
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
