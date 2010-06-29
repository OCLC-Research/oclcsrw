/*
 * TermTypeIterator.java
 *
 * Created on November 5, 2007, 2:46 PM
 *
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

package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.ScanResponseType;
import gov.loc.www.zing.srw.TermType;
import gov.loc.www.zing.srw.TermTypeWhereInList;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.axis.types.NonNegativeInteger;

/**
 *
 * @author levan
 */
public class TermTypeIterator implements Iterator {
    static final int NumScanTerms=500;
    boolean    calledHasNext=false, decreasing;
    int        pos;
    String     baseURL, index, relation;
    TermType[] terms;

    /**
     * Creates a new instance of TermTypeIterator
     */
    public TermTypeIterator(String baseURL, String index, String relation, String term) throws ParseException, IOException {
        this(baseURL, index, relation, term, false);
    }
    
    public TermTypeIterator(String baseURL, String index, String relation, String term, boolean decreasing) throws ParseException, IOException {
        this.decreasing=decreasing;
        this.baseURL=baseURL+"?operation=scan&version=1.1";
        this.index=index;
        this.relation=relation;
        URL url=new URL(baseURL);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        if(conn.getResponseCode()!=200)
            System.out.println("responseCode="+conn.getResponseCode());
        if(conn.getContentType().toLowerCase().indexOf("xml")<0)
            throw new IOException("baseURL \""+baseURL+
                "\" should have returned a content type"+
                " of text/xml but actually returned "+conn.getContentType());
        if(decreasing) {
            pos=-1;
        }
        else {
            pos=1;
        }
        terms=new TermType[1];
        terms[0]=new TermType(term, new NonNegativeInteger("0"), term, null, null);
    }

    public boolean hasNext() {
        calledHasNext=true;
        if(decreasing) {
            if(pos<=0) {
                String seed=terms[0].getValue();
                pos=NumScanTerms;
                try {
                    terms=((ScanResponseType)Utilities.xmlToObj(Utilities.readURL(
                      baseURL+"&scanClause="+index+"%20"+relation+"%20%22"+URLEncoder.encode(seed, "UTF-8")+
                              "%22&responsePosition="+(NumScanTerms+1)+
                              "&maximumTerms="+NumScanTerms))).getTerms().getTerm();
                }
                catch(ParseException e) {
                    throw new NoSuchElementException(e.getMessage());
                }
                catch(UnsupportedEncodingException e) {
                }
                if(terms==null)
                    return false;
                if(terms.length==1 && terms[0].getValue().equals(seed)) {
//                    System.out.println("url returned seed term: "+baseURL+"&scanClause="+index+"+"+relation+"+%22"+Utilities.urlEncode(seed)+
//                              "%22&responsePosition=1"+
//                              "&maximumTerms="+NumScanTerms);
                    return false;
                }
                pos=terms.length;
                return true;
            }
            if(pos>0)
                return true;
            if(terms[0].getWhereInList()==TermTypeWhereInList.first)
                return false;
            return true;
        }
        if(pos>=terms.length) {
            pos=0;
            String seed=terms[terms.length-1].getValue();
//            System.out.println(seed);
            if(seed.indexOf('"')>=0)  // I don't know how to escape this
                seed=seed.replace('"', 'z');  // so we'll just skip over it
//            if(seed.indexOf("&quot;")>=0)  // I don't know how to escape this
//                seed=seed.replace("&quot;", "z");  // so we'll just skip over it
//            if(seed.indexOf("%22")>=0)  // I don't know how to escape this
//                seed=seed.replace("%22", "z");  // so we'll just skip over it
            try {
//                System.out.println("url: "+baseURL+"&scanClause="+index+"+"+relation+"+%22"+URLEncoder.encode(seed, "UTF-8")+
//                          "%22&responsePosition=1"+
//                          "&maximumTerms="+NumScanTerms);
                terms=((ScanResponseType)Utilities.xmlToObj(Utilities.readURL(
                  baseURL+"&scanClause="+index+"+"+relation+"+%22"+URLEncoder.encode(seed, "UTF-8")+
                          "%22&responsePosition=1"+
                          "&maximumTerms="+NumScanTerms))).getTerms().getTerm();
//                System.out.println("first returned term is "+terms[0].getValue());
            }
            catch(ParseException e) {
                throw new NoSuchElementException(e.getMessage());
            }
            catch(UnsupportedEncodingException e){}
            if(terms==null) {
//                System.out.println("url returned no terms: "+baseURL+"&scanClause="+index+"+"+relation+"+%22"+Utilities.urlEncode(seed)+
//                          "%22&responsePosition=1"+
//                          "&maximumTerms="+NumScanTerms);
                return false;
            }
            if(terms[terms.length-1].getValue().equals(seed)) {
//                System.out.println("url returned seed term: "+baseURL+"&scanClause="+index+"+"+relation+"+%22"+Utilities.urlEncode(seed)+
//                          "%22&responsePosition=1"+
//                          "&maximumTerms="+NumScanTerms);
                return false;
            }
            return true;
        }
        if(pos<terms.length-1)
            return true;
        if(terms[terms.length-1].getWhereInList()==TermTypeWhereInList.last) {
            System.out.println("encountered \"last\" term for "+baseURL);
            return false;
        }
        return true;
    }

    public Object next() throws NoSuchElementException {
        if(!calledHasNext)
            if(!hasNext())
                throw new NoSuchElementException();
        calledHasNext=false;
        if(decreasing) {
            return terms[--pos];
        }

//        System.out.println("returning term "+pos+" for url "+baseURL);
        return terms[pos++];
    }

    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
