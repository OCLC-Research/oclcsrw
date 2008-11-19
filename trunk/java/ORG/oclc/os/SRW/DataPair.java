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
 * DataPair.java
 *
 * Created on June 9, 2005, 9:59 AM
 */

package ORG.oclc.os.SRW;

/**
 *
 * @author  levan
 */
public class DataPair implements Comparable {
    private int    count;
    private String name;
    /** Creates a new instance of DataPair */
    public DataPair(String name, int count) {
        this.name=name;
        this.count=count;
    }

    public int compareTo(Object dp) {
        return count-((DataPair)dp).getCount();
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public void setCount(int count) {
        this.count=count;
    }

    public void setName(String name) {
        this.name=name;
    }
}
