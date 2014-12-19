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
 * ElementParser.java
 *
 * Created on October 28, 2005, 9:35 AM
 */

package ORG.oclc.os.SRW;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 *
 * @author levan
 */
public class ElementParser implements Enumeration<NameValuePair> {
    static boolean debug=false;
    char[] frag;
    int    offset=0;
    String attributes=null;

    /** Creates a new instance of ElementParser */
    public ElementParser(String xmlFragment) {
        if(debug) System.out.println("parsing: "+xmlFragment);
        frag=xmlFragment.toCharArray();
        while(offset<frag.length && frag[offset]!='<')
            offset++;
    }

    private int find(String elementName, int offset) {
        int i;
        if(debug) System.out.println("looking for \""+elementName+"\" in \""+new String(frag, offset, frag.length-offset)+"\"");
        // look for a close tag
        for(; offset<frag.length; offset++)
            if(frag[offset]=='<' && frag[offset+1]=='/')
                break;
        // is it our tag?
        i=0;
        while(i<elementName.length() && frag[i+offset+2]==elementName.charAt(i))
            i++;
        if(i<elementName.length() || frag[i+offset+2]!='>') // some other close tag
            return(find(elementName, offset+2)); // keep looking
        return offset;
    }

    public String getAttributes() {
        return attributes;
    }

    public boolean hasMoreElements() {
        if(offset<frag.length && frag[offset]=='<') {
            if(debug) System.out.println("\""+new String(frag, offset, frag.length-offset)+"\"");
            return true;
        }
        return false;
    }

    public boolean hasMoreElements(boolean debug) {
        if(offset<frag.length && frag[offset]=='<') {
            if(debug) System.out.println("\""+new String(frag, offset, frag.length-offset)+"\"");
            return true;
        }
        return false;
    }

    public NameValuePair nextElement() {
        if(!hasMoreElements(false))
            throw new NoSuchElementException();
        boolean foundEndOfElement=false;
        int end, start=offset+1;
        end=start+1;
        while(end<frag.length && frag[end]!=' ' && frag[end]!='/' && frag[end]!='>')
            end++;
        if(frag[end]=='/')
            foundEndOfElement=true;
        String elementName=new String(frag, start, end-start);
        if(debug) System.out.println("elementName="+elementName+", frag["+end+"]='"+frag[end]+"'");
        start=end;
        while(start<frag.length && frag[start]!='>')
            start++;
        if(start!=end) { // found some attributes
            attributes=new String(frag, end+1, start-end-1);
            if(attributes.endsWith("/")) {
                if(attributes.length()==1)
                    attributes=null;
                else
                    attributes=attributes.substring(0, attributes.length()-1);
                foundEndOfElement=true;
            }
        }
        else
            attributes=null;
        if(debug) System.out.println("attributes="+attributes+", frag["+start+"]='"+frag[start]+"'");
        if(frag[start]=='/') {
            foundEndOfElement=true;
            start++;  // now we point at the '>'
        }
        start++; // now we point at the beginning of our data
        String value="";
        offset=start;
        if(!foundEndOfElement)
            if(start<frag.length && !(frag[start]=='<' && frag[start+1]=='/')) {
                end=find(elementName, start);
                offset=end+1;
                while(offset<frag.length && frag[offset]!='<')
                    offset++;
                value=new String(frag, start, end-start);
                if(debug) System.out.println("value="+value);
            }
        return new NameValuePair(elementName, attributes, value);
    }
}
