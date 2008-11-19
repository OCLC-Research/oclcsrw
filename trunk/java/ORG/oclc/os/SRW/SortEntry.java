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
 * SortEntry.java
 *
 * Created on August 3, 2004, 9:13 PM
 */

package ORG.oclc.os.SRW;

/**
 *
 * @author  levan
 */
public class SortEntry implements Comparable {
    private int    number=-1, numericKey;
    private Object entry=null;
    private String key=null;
    
    /** Creates a new instance of SortEntry */
    public SortEntry(String key, Object entry) {
        this.key=key;
        this.entry=entry;
    }

    /** Creates a new instance of SortEntry */
    public SortEntry(String key, int number) {
        this.key=key;
        this.number=number;
    }

    /** Creates a new instance of SortEntry */
    public SortEntry(int key, int number) {
        this.numericKey=key;
        this.number=number;
    }

    /** Creates a new instance of SortEntry */
    public SortEntry(int key, Object entry) {
        this.numericKey=key;
        this.entry=entry;
    }

    public int compareTo(Object o) {
        if(key!=null)
            return key.compareTo(((SortEntry)o).getKey());
        return numericKey-(((SortEntry)o).getNumericKey());
    }
    
    public Object getEntry() {
        return entry;
    }
    
    public String getKey() {
        return key;
    }

    public int getNumber() {
        return number;
    }

    public int getNumericKey() {
        return numericKey;
    }

    public String toString() {
        StringBuffer sb=new StringBuffer("ORG.oclc.os.SRW.SortEntry: key=").append(key);
        if(number!=-1)
            sb.append(", number=").append(number);
        if(entry!=null)
            sb.append(", entry=").append(entry.toString());
        return sb.toString();
    }
}
