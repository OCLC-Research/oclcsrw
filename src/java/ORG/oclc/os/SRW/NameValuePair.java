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
 * NameValuePair.java
 *
 * Created on October 28, 2005, 9:59 AM
 */

package ORG.oclc.os.SRW;

/**
 *
 * @author  levan
 */
public class NameValuePair {
    private String name, value;
    /** Creates a new instance of DataPair */
    public NameValuePair(String name, String value) {
        this.name=name;
        this.value=value;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public void setValue(String value) {
        this.value=value;
    }

    public void setName(String name) {
        this.name=name;
    }
    
    public String toString() {
        StringBuffer sb=new StringBuffer("NameValuePair: name=");
        sb.append(name).append(", value=\"").append(value).append('"');
        return sb.toString();
    }
}
