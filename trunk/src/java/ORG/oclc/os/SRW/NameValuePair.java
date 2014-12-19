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

import java.util.HashMap;
import java.util.StringTokenizer;

/**
 *
 * @author  levan
 */
public class NameValuePair {
    private HashMap<String, String> attributeMap;
    private String attributes, name, value;
    /** Creates a new instance of DataPair */
    public NameValuePair(String name, String value) {
        this.name=name;
        this.value=value;
        attributes=null;
    }

    public NameValuePair(String name, String attributes, String value) {
        this.name=name;
        this.value=value;
        this.attributes=attributes;
        attributeMap=toHashMap(attributes);
    }

    public String getAttributes() {
        return attributes;
    }

    public HashMap<String, String> getAttributeMap() {
        return attributeMap;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name=name;
    }
    
    public void setValue(String value) {
        this.value=value;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder("NameValuePair: name=").append(name);
        if(attributes!=null)
            sb.append(", attributes=\"").append(attributes).append('"');
        sb.append(", value=\"").append(value).append('"');
        return sb.toString();
    }

    private HashMap<String, String> toHashMap(String attributes) {
        if(attributes==null)
            return null;
        HashMap<String, String> map=new HashMap<String, String>();
        String attribute;
        StringTokenizer attrs=new StringTokenizer(attributes), avp;
        while(attrs.hasMoreTokens()) {
            attribute=attrs.nextToken();
            avp=new StringTokenizer(attribute, "=");
            map.put(avp.nextToken(), trimQuotes(avp.nextToken()));
        }
        return map;
    }

    private String trimQuotes(String str) {
        return str.substring(1, str.length()-1);
    }
}
