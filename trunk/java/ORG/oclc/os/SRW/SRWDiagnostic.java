/*
   Copyright 2006 OCLC Online Computer Library Center, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
/*
 * SRWDiagnostic.java
 *
 * Created on January 16, 2004, 10:11 AM
 */

package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.DiagnosticsType;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;
import java.net.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  levan
 */
public class SRWDiagnostic extends Exception {
    static final Log log=LogFactory.getLog(SRWDiagnostic.class);

    public static final String[] message= {
        /* 000 */ "omitted diagnostic 0",
        /* 001 */ "General system error",
        /* 002 */ "System temporarily unavailable",
        /* 003 */ "Authentication error",
        /* 004 */ "Unsupported operation",
        /* 005 */ "Unsupported version",
        /* 006 */ "Unsupported parameter value",
        /* 007 */ "Mandatory parameter not supplied",
        /* 008 */ "Unsupported Parameter",
        /* 009 */ "omitted diagnostic 9",
        /* 010 */ "Query syntax error", 
        /* 011 */ "DEPRECATED. Unsupported query type",
        /* 012 */ "DEPRECATED. Too many characters in query",
        /* 013 */ "Invalid or unsupported use of parentheses",
        /* 014 */ "Invalid or unsupported use of quotes",
        /* 015 */ "Unsupported context set",
        /* 016 */ "Unsupported index",
        /* 017 */ "DEPRECATED. Unsupported combination of index and index set",
        /* 018 */ "Unsupported combination of indexes",
        /* 019 */ "Unsupported relation Relation",
        /* 020 */ "Unsupported relation modifier",
        /* 021 */ "Unsupported combination of relation modifers",
        /* 022 */ "Unsupported combination of relation and index",
        /* 023 */ "Too many characters in term",
        /* 024 */ "Unsupported combination of relation and term",
        /* 025 */ "DEPRECATED. Special characters not quoted in term",
        /* 026 */ "Non special character escaped in term",
        /* 027 */ "Empty term unsupported",
        /* 028 */ "Masking character not supported",
        /* 029 */ "Masked words too short",
        /* 030 */ "Too many masking characters in term",
        /* 031 */ "Anchoring character not supported",
        /* 032 */ "Anchoring character in unsupported position",
        /* 033 */ "Combination of proximity/adjacency and masking characters not supported",
        /* 034 */ "Combination of proximity/adjacency and anchoring characters not supported", 
        /* 035 */ "Term contains only stopwords",
        /* 036 */ "Term in invalid format for index or relation",
        /* 037 */ "Unsupported boolean operator",
        /* 038 */ "Too many boolean operators in query",
        /* 039 */ "Proximity not supported",
        /* 040 */ "Unsupported proximity relation",
        /* 041 */ "Unsupported proximity distance",
        /* 042 */ "Unsupported proximity unit",
        /* 043 */ "Unsupported proximity ordering",
        /* 044 */ "Unsupported combination of proximity modifiers",
        /* 045 */ "DEPRECATED. Prefix assigned to multiple identifiers",
        /* 046 */ "Unsupported boolean modifier",
        /* 047 */ "omitted diagnostic 47",
        /* 048 */ "omitted diagnostic 48",
        /* 049 */ "omitted diagnostic 49",
        /* 050 */ "Result sets not supported",
        /* 051 */ "Result set does not exist",
        /* 052 */ "Result set temporarily unavailable",
        /* 053 */ "Result sets only supported for retrieval",
        /* 054 */ "DEPRECATED. Retrieval may only occur from an existing result set",
        /* 055 */ "Combination of result sets with search terms not supported",
        /* 056 */ "DEPRECATED. Only combination of single result set with"+
                       " search terms supported",
        /* 057 */ "DEPRECATED. Result set created but no records available",
        /* 058 */ "Result set created with unpredictable partial results available",
        /* 059 */ "Result set created with valid partial results available",
        /* 060 */ "Result set not created: too many matching records",
        /* 061 */ "First record position out of range",
        /* 062 */ "DEPRECATED. Negative number of records requested",
        /* 063 */ "DEPRECATED. System error in retrieving records",
        /* 064 */ "Record temporarily unavailable",
        /* 065 */ "Record does not exist",
        /* 066 */ "Unknown schema for retrieval",
        /* 067 */ "Record not available in this schema",
        /* 068 */ "Not authorised to send record",
        /* 069 */ "Not authorised to send record in this schema",
        /* 070 */ "Record too large to send",
        /* 071 */ "Unsupported record packing",
        /* 072 */ "XPath retrieval unsupported",
        /* 073 */ "XPath expression contains unsupported feature",
        /* 074 */ "Unable to evaluate XPath expression",
        /* 075 */ "omitted diagnostic 75",
        /* 076 */ "omitted diagnostic 76",
        /* 077 */ "omitted diagnostic 77",
        /* 078 */ "omitted diagnostic 78",
        /* 079 */ "omitted diagnostic 79",
        /* 080 */ "Sort not supported",
        /* 081 */ "DEPRECATED. Unsupported sort type",
        /* 082 */ "Unsupported sort sequence",
        /* 083 */ "Too many records to sort",
        /* 084 */ "Too many sort keys to sort",
        /* 085 */ "DEPRECATED. Duplicate sort keys",
        /* 086 */ "Cannot sort: incompatible record formats",
        /* 087 */ "Unsupported schema for sort",
        /* 088 */ "Unsupported path for sort",
        /* 089 */ "Path unsupported for schema",
        /* 090 */ "Unsupported direction",
        /* 091 */ "Unsupported case",
        /* 092 */ "Unsupported missing value action",
        /* 093 */ "Sort ended due to missing value",
        /* 094 */ "Sort Unsupported When startRecord Not One And Query Not A resultSetId",
        /* 095 */ "omitted diagnostic 95",
        /* 096 */ "omitted diagnostic 96",
        /* 097 */ "omitted diagnostic 97",
        /* 098 */ "omitted diagnostic 98",
        /* 099 */ "omitted diagnostic 99",
        /* 100 */ "DEPRECATED. Explain not supported (Use 4)",
        /* 101 */ "DEPRECATED. Explain request type not supported",
        /* 102 */ "DEPRECATED. Explain record temporarily unavailable (Use 64)",
        /* 103 */ "omitted diagnostic 103",
        /* 104 */ "omitted diagnostic 104",
        /* 105 */ "omitted diagnostic 105",
        /* 106 */ "omitted diagnostic 106",
        /* 107 */ "omitted diagnostic 107",
        /* 108 */ "omitted diagnostic 108",
        /* 109 */ "omitted diagnostic 109",
        /* 110 */ "Stylesheets not supported",
        /* 111 */ "Unsupported stylesheet",
        /* 112 */ "omitted diagnostic 112",
        /* 113 */ "omitted diagnostic 113",
        /* 114 */ "omitted diagnostic 114",
        /* 115 */ "omitted diagnostic 115",
        /* 116 */ "omitted diagnostic 116",
        /* 117 */ "omitted diagnostic 117",
        /* 118 */ "omitted diagnostic 118",
        /* 119 */ "omitted diagnostic 119",
        /* 120 */ "Response position out of range",
        /* 121 */ "omitted diagnostic 121",
        /* 122 */ "omitted diagnostic 122",
        /* 123 */ "omitted diagnostic 123",
        /* 124 */ "omitted diagnostic 124",
        /* 125 */ "omitted diagnostic 125",
        /* 126 */ "omitted diagnostic 126",
        /* 127 */ "omitted diagnostic 127",
        /* 128 */ "omitted diagnostic 128",
        /* 129 */ "omitted diagnostic 129",
        /* 130 */ "Too many terms matched by masked query term"};
    public static final int GeneralSystemError=1;
    public static final int SystemTemporarilyUnavailable=2;
    public static final int UnsupportedOperation=4;
    public static final int UnsupportedVersion=5;
    public static final int UnsupportedParameterValue=6;
    public static final int MandatoryParameterNotSupplied=7;
    public static final int QuerySyntaxError=10;
    public static final int UnsupportedIndex=16;
    public static final int UnsupportedCombinationOfIndexes=18;
    public static final int UnsupportedRelationRelation=19;
    public static final int UnsupportedCombinationOfRelationAndIndex=22;
    public static final int EmptyTermUnsupported=27;
    public static final int MaskedWordsTooShort=29;
    public static final int ProximityNotSupported=39;
    public static final int ResultSetDoesNotExist=51;
    public static final int FirstRecordPositionOutOfRange=61;
    public static final int RecordTemporarilyUnavailable=64;
    public static final int UnknownSchemaForRetrieval=66;
    public static final int RecordNotAvailableInThisSchema=67;
    public static final int UnsupportedRecordPacking=71;
    public static final int SortNotSupported=80;
    public static final int UnsupportedSortSequence=82;
    public static final int UnsupportedSchemaForSort=87;
    public static final int SortEndedDueToMissingValue=93;
    public static final int SortUnsupportedWhenStartRecordNotOneAndQueryNotAResultSetId=94;
    public static final int StylesheetsNotSupported=110;
    public static final int ResponsePositionOutOfRange=120;
    public static final int TooManyTermsMatchedByMaskedQueryTerm=130;

    int code;
    String addInfo;

    public SRWDiagnostic(int code, String addInfo) {
        super(Integer.toString(code)+"/"+addInfo);
        this.code=code;
        this.addInfo=addInfo;
    }

    public static DiagnosticType[] addDiagnostic(DiagnosticType[] diagnostics,
      int code, String details) {
        return addDiagnostic(diagnostics, newDiagnosticType(code, details));
    }

    public static DiagnosticType[] addDiagnostic(DiagnosticType[] diagnostics,
      DiagnosticType diagnostic) {
        if(diagnostic==null)
            return diagnostics;
        DiagnosticType[] newDiagnostics;
        if(diagnostics==null || diagnostics.length==0)
            newDiagnostics=new DiagnosticType[1];
        else {
            newDiagnostics=new DiagnosticType[diagnostics.length+1];
            System.arraycopy(diagnostics, 0, newDiagnostics, 0, diagnostics.length);
        }
        newDiagnostics[newDiagnostics.length-1]=diagnostic;
        return newDiagnostics;
    }

    public static DiagnosticType[] addDiagnostic(DiagnosticType[] diagnostics,
      DiagnosticType[] newDiags) {
        if(newDiags==null || newDiags.length==0)
            return diagnostics;
        DiagnosticType[] newDiagnostics;
        if(diagnostics==null || diagnostics.length==0)
            newDiagnostics=new DiagnosticType[newDiags.length];
        else {
            newDiagnostics=new DiagnosticType[diagnostics.length+newDiags.length];
            System.arraycopy(diagnostics, 0, newDiagnostics, 0, diagnostics.length);
        }
        for(int i=0; i<newDiags.length; i++)
            newDiagnostics[newDiagnostics.length-newDiags.length+i]=newDiags[i];
        return newDiagnostics;
    }

    public static DiagnosticType[] addDiagnostic(DiagnosticType[] diagnostics,
      DiagnosticsType newDiags) {
        if(newDiags==null)
            return diagnostics;
        return addDiagnostic(diagnostics, newDiags.getDiagnostic());
    }
    
    public static DiagnosticType newDiagnosticType(int code, String details) {
        return newDiagnosticType("info:srw/diagnostic/1/", code, details);
    }

    public static DiagnosticType newDiagnosticType(String baseURI, int code,
      String details) {
        DiagnosticType dt=new DiagnosticType();
        try {
            dt.setUri(new org.apache.axis.types.URI(baseURI+code));
        }
        catch(org.apache.axis.types.URI.MalformedURIException e) {
            log.error("error creating uri for code: "+code);
            log.error(e,e);
        }
        dt.setDetails(details);
        if(log.isDebugEnabled()) {
            try {
                log.debug(SRWDiagnostic.message[code]+"("+code+"): \""+details+
                    "\"");
            }
            catch(Exception e) {
                log.error("Diagnostic code "+code+" not in SRWDiagnostics");
                log.error(code+": \""+details+"\"");
            }
            if(code!=10 && code!=16 && code!=22 && code!=130) {
                Exception e=new Exception();
                StackTraceElement[] trace=e.getStackTrace();
                int i;
                String className;
                for(i=0; i<trace.length; i++) {
                    className=trace[i].getClassName();
                    if(!className.startsWith("ORG") && !className.startsWith("gov"))
                        break;
                }
                StackTraceElement[] shortTrace=new StackTraceElement[i];
                System.arraycopy(trace, 0, shortTrace, 0, i);
                e.setStackTrace(shortTrace);
                log.debug(e, e);
            }
        }
        return dt;
    }

    public static String newSurrogateDiagnostic(String baseURI, int code, String details) {
        StringBuffer sb=new StringBuffer("<diagnostic xmlns=\"http://www.loc.gov/zing/srw/diagnostic/\">\n");
        sb.append("  <uri>").append(baseURI).append(code).append("</uri>\n");
        if(details!=null)
            sb.append("  <details>").append(details).append("</details>\n");
        sb.append("  </diagnostic>");
        return sb.toString();
    }

    public int getCode() {
        return code;
    }

    public String getAddInfo() {
        return addInfo;
    }
}
