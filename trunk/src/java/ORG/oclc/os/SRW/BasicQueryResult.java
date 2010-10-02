/*
Expression licensePrefix is undefined on line 4, column 3 in Templates/Licenses/license-default.txt.To change this template, choose Tools | Templates
Expression licensePrefix is undefined on line 5, column 3 in Templates/Licenses/license-default.txt.and open the template in the editor.
*/

package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.ExtraDataType;

/**
 *
 * @author levan
 */
public class BasicQueryResult extends QueryResult {
    private long numberOfRecords=0;
    private String query=null;
    private StringBuilder records[];

    @Override
    public long getNumberOfRecords() {
        return numberOfRecords;
    }

    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    @Override
    public RecordIterator newRecordIterator(long index, int numRecs, String schemaId, ExtraDataType edt) throws InstantiationException {
        return new BasicRecordIterator(records, schemaId, index);
    }

    public void setNumberOfRecords(long n) {
        numberOfRecords=n;
    }

    /**
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * @param records the records to set
     */
    public void setRecords(StringBuilder[] records) {
        this.records = records;
    }

}
