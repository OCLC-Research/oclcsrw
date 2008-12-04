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
 * Record.java
 *
 * Created on November 1, 2005, 8:39 AM
 */

package ORG.oclc.os.SRW;

/**
 *
 * @author levan
 */
public class Record {
    String extraRecordInfo, identifier, record, schemaID;

    public Record(String record, String schemaID) {
        this.record=record;
        this.schemaID=schemaID;
    }

    public String getExtraRecordInfo() {
        return extraRecordInfo;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getRecordSchemaID() {
        return schemaID;
    }

    public String getRecord() {
        return record;
    }
    
    public boolean hasExtraRecordInfo() {
        return extraRecordInfo!=null;
    }

    public void setExtraRecordInfo(String extraRecordInfo) {
        this.extraRecordInfo=extraRecordInfo;
    }

    public void setIdentifier(String identifier){
        this.identifier = identifier;
    }

    public String toString() {
        StringBuffer sb=new StringBuffer();
        sb.append("Record: schemaID=").append(schemaID);
        if(record.length()<=80) {
            sb.append(", content:\n");
            sb.append(record);
        }
        else {
            sb.append(", first 80 bytes of content:\n");
            sb.append(record.substring(0, 80));
        }
        return sb.toString();
    }
}
