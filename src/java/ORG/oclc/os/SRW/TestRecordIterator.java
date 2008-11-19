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
 * TestRecordIterator.java
 *
 * Created on November 1, 2005, 2:29 PM
 */

package ORG.oclc.os.SRW;

import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * @author levan
 */
public class TestRecordIterator implements RecordIterator {
    static final Log log=LogFactory.getLog(TestRecordIterator.class);

    long startPoint=0;
    String record, schemaID;

    /**
     * Creates a new instance of TestRecordIterator
     */
    public TestRecordIterator(long startPoint, String schemaID)
      throws InstantiationException {
        this.startPoint=startPoint;
        this.schemaID=schemaID;
        StringBuffer sb=new StringBuffer();
        sb.append("<srw_dc:dc xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:srw_dc=\"info:srw/schema/1/dc-v1.1\">")
          .append("    <dc:identifier>test001</dc:identifier>")
          .append("    <dc:creator>Ralph LeVan</dc:creator><date>2003-08-25</date>")
          .append("    <dc:title>SRW Test Record</dc:title>")
          .append("    </srw_dc:dc>");
        record=sb.toString();
    }

    public void close() {
    }

    public boolean hasNext() {
        if(startPoint==1)
            return true;
        return false;
    }

    public Object next() throws NoSuchElementException {
        return nextRecord();
    }

    public Record nextRecord() throws NoSuchElementException {
        if(hasNext()) {
            startPoint++;
            return new Record(record, schemaID);
        }
        throw new NoSuchElementException();
    }
    
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
