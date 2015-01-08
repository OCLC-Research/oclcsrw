/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.ExtraDataType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Ralph
 */
class OpenSearchQueryResult extends QueryResult {
    private static final Log log=LogFactory.getLog(OpenSearchQueryResult.class);
    String query;
    int count=0;
    
    public OpenSearchQueryResult(String query, SearchRetrieveRequestType request,
            SRWOpenSearchDatabase db) throws InstantiationException, SRWDiagnostic {
        this.query=query;
        // figure out what schema/template to use
        String schema=request.getRecordSchema();
        if(schema==null)
            schema=db.defaultSchemaID;
        if(schema==null)
            schema=db.defaultSchemaName;
        if(schema==null) {
            log.error("No schema provided");
            throw new InstantiationException("No schema provided");
        }
        String template=db.templates.get(schema);
        if(template==null)
            throw new SRWDiagnostic(SRWDiagnostic.RecordNotAvailableInThisSchema, schema);
        Pattern p=Pattern.compile("\\{[^\\}]\\}");
        Matcher m=p.matcher(template);
        String parameter;
        while(m.find()) {
            parameter=m.group();
            if(parameter.equals("{searchTerms}"))
                template=template.replace(parameter, query);
            else if(parameter.equals("{count}")) {
                NonNegativeInteger nni = request.getMaximumRecords();
                if(nni!=null)
                    count=nni.intValue();
                else {
                    count=db.defaultNumRecs;
                }
                if(count<=0)
                    throw new InstantiationException("maximumRecords parameter not supplied and defaultNumRecs not specified in the database properties file");
                template=template.replace(parameter, Integer.toString(count));
            }
            else if(parameter.equals("{startIndex}")) {
                PositiveInteger pi = request.getStartRecord();
                int start;
                if(pi!=null)
                    start=pi.intValue();
                else {
                    start=1;
                }
                template=template.replace(parameter, Integer.toString(start));
            }
            else if(parameter.equals("{startPage}")) {
                PositiveInteger pi = request.getStartRecord();
                int start;
                if(pi!=null) {
                    start=pi.intValue();
                    if(db.itemsPerPage==0)
                        throw new InstantiationException("template expects startPage parameter but itemsPerPage not specified in the database properties file");
                    start=start/db.itemsPerPage;
                }
                else {
                    start=1;
                }
                template=template.replace(parameter, Integer.toString(start));
            }
        }
        template.replaceAll("{queryterms}", query);
    }

    @Override
    public long getNumberOfRecords() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RecordIterator newRecordIterator(long index, int numRecs, String schemaId, ExtraDataType edt) throws InstantiationException {
        return new OpenSearchRecordIterator(index, numRecs, schemaId, edt);
    }
    
}
