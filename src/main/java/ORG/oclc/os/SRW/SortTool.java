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
 * SortTool.java
 *
 * Created on August 3, 2004, 2:17 PM
 */

package ORG.oclc.os.SRW;

import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  levan
 */
public class SortTool {
    static Log log=LogFactory.getLog(SortTool.class);
    public boolean ascending=true, caseSensitive=false;
    public String  dataType="text", missingValue="highValue", prefix,
                   schema=null, xpath;

    int         offset=0;
    char[]      sortKey;
    protected SortElementExtractor extractor=null;
    
    /** Creates a new instance of SortTool */
    public SortTool(String sortKey) {
        this.sortKey=sortKey.toCharArray();
        xpath=parseSortKey();
        prefix=parseSortKey();
        if(prefix==null)
            prefix="";
        String temp=parseSortKey();
        log.info("ascendingKey="+temp);
        if(temp!=null && temp.equals("0"))
            ascending=false;
        temp=parseSortKey();
        if(temp!=null && temp.equals("1"))
            caseSensitive=true;
        temp=parseSortKey();
        if(temp!=null)
            missingValue=temp;
        temp=parseSortKey();
        if(temp!=null)
            dataType=temp;
        log.info(toString());
    }

    public String extract(String record) throws SortElementExtractorException {
        return extractor.extract(record);
    }

    public void makeSortElementExtractor() throws SortElementExtractorException {
        extractor=new TransformingExtractor();
        extractor.init(xpath, prefix, schema);
    }


    private String nextToken() {
        if(sortKey[offset]==',') {
            log.info("found a null sort parameter");
            offset++;
            return null;
        }
        if(sortKey[offset]=='"') { // suck up until the next quote
            int start=offset+1;
            while(sortKey[++offset]!='"');
            String token=new String(sortKey,start,offset-start);
            offset+=2; // skip trailing comma, if any
            return token;
        }
        // suck up until the next comma or EOF
        int start=offset;
        while(offset<sortKey.length-1 && sortKey[++offset]!=',');
        String token;
        if(sortKey[offset]==',')
            token=new String(sortKey,start,offset-start);
        else
            token=new String(sortKey,start,offset-start+1);
        offset++; // skip trailing comma, if any
        return token;
    }

    private String parseSortKey() {
        if(offset>=sortKey.length)
            return null;
        String value=nextToken();
        return value;
    }

    public void setSchema(String schema) {
        this.schema=schema;
    }

    public String toString() {
        StringBuffer sb=new StringBuffer("SortTool: xpath=").append(xpath)
        .append(", prefix=").append(prefix)
        .append(", ascending=").append(ascending)
        .append(", caseSensitive=").append(caseSensitive)
        .append(", missingValue=").append(missingValue)
        .append(", dataType=").append(dataType);
        return sb.toString();
    }
}
