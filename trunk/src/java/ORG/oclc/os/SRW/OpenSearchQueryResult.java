/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.ExtraDataType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import java.util.HashMap;

/**
 *
 * @author Ralph
 */
class OpenSearchQueryResult extends QueryResult {

    public OpenSearchQueryResult(String query, SearchRetrieveRequestType request,
            HashMap<String, String> templates) {
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
