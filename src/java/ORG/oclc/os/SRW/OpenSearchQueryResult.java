/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.ExtraDataType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Ralph
 */
class OpenSearchQueryResult extends QueryResult {
    private static final Log log=LogFactory.getLog(OpenSearchQueryResult.class);

    public OpenSearchQueryResult(String query, SearchRetrieveRequestType request,
            SRWOpenSearchDatabase db) throws InstantiationException, SRWDiagnostic {
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
