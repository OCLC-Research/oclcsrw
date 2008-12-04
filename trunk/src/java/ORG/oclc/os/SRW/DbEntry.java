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
 * DbEntry.java
 *
 * Created on May 20, 2005, 11:08 AM
 */

package ORG.oclc.os.SRW;

/**
 *
 * @author  levan
 */
public class DbEntry implements Comparable {
    String description, host, name, path;
    /** Creates a new instance of DbEntry */
    public DbEntry(String name, String host, String path, String description) {
        this.name=name;
        if(host==null)
            host="";
        this.host=host;
        this.path=path;
        this.description=name;
    }
    public int compareTo(DbEntry other) {
        return (host+": "+name).compareTo(other.getHost()+": "+other.getName());
    }
    public int compareTo(Object o) {
        return compareTo((DbEntry)o);
    }
    public String getDescription() {
        return description;
    }
    public String getHost() {
        return host;
    }
    public String getName() {
        return name;
    }
    public String getPath() {
        return path;
    }
}
