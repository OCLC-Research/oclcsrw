/*
Expression licensePrefix is undefined on line 4, column 3 in Templates/Licenses/license-default.txt.To change this template, choose Tools | Templates
Expression licensePrefix is undefined on line 5, column 3 in Templates/Licenses/license-default.txt.and open the template in the editor.
*/

package ORG.oclc.os.SRW;

import java.util.NoSuchElementException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author levan
 */
public class BasicRecordIterator implements RecordIterator {
    static final Log log=LogFactory.getLog(BasicRecordIterator.class);

    long offset;
    String schemaID;
    StringBuilder[] recs;
    public BasicRecordIterator(StringBuilder[] recs, String schemaID, long startPoint) {
        offset=startPoint;
        this.recs=recs;
        this.schemaID=schemaID;
    }

    public void close() {
    }

    public boolean hasNext() {
        if(offset<=recs.length)
            return true;
        return false;
    }

    public Object next() {
        if(hasNext()) {
            Record rec=new Record(recs[(int)offset-1].toString(), schemaID);
            offset++;
            return rec;
        }
        throw new NoSuchElementException(Long.toString(offset));
    }

    public Record nextRecord() throws SRWDiagnostic {
        return (Record) next();
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
