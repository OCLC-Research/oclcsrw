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
 * QueryResult.java
 *
 * Created on October 28, 2005, 11:01 AM
 */

package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.ExtraDataType;
import java.util.Hashtable;

/**
 *
 * @author levan
 */
public abstract class QueryResult extends SRWDiagnosticsHolder {
    public abstract long getNumberOfRecords();
    public abstract RecordIterator newRecordIterator(long index, int numRecs,
            String schemaId, ExtraDataType edt) throws InstantiationException;

    Hashtable sortedResults=null;
    private int       resultSetIdleTime;
    RecordIterator recordIterator=null;

    /**
     *  If you choose to override close(), don't forget to call super.close();
     */
    public void close() {
        if(recordIterator!=null)
            recordIterator.close();
    }

    public int getResultSetIdleTime() {
        return resultSetIdleTime;
    }

    public Hashtable getSortedResults() {
        if(sortedResults==null)
            sortedResults=new Hashtable();
        return sortedResults;
    }

    public QueryResult getSortedResult(String sortKeys) {
        if(sortedResults==null)
            return null;
        return (QueryResult)sortedResults.get(sortKeys);
    }

    public void putSortedResult(String sortKeys, QueryResult sortedResult) {
        if(sortedResults==null)
            sortedResults=new Hashtable();
        sortedResults.put(sortKeys, sortedResult);
    }

    public RecordIterator recordIterator(long index, int numRecs,
            String schemaId, ExtraDataType edt) throws InstantiationException {
        recordIterator=newRecordIterator(index, numRecs, schemaId, edt);
        return recordIterator;
    }

    public void setResultSetIdleTime(int resultSetIdleTime) {
        this.resultSetIdleTime = resultSetIdleTime;
    }
}
