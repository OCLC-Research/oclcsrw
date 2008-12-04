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
 * SRUTimer.java
 *
 * Created on November 19, 2004, 1:53 PM
 */

package ORG.oclc.os.SRW;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 *
 * @author  levan
 */
public class SRUTimer {
    String url;

    public SRUTimer(String urlString) {
        this.url=urlString+"?version=1.1&maximumRecords=20&recordSchema=briefMarcXML&resultSetTTL=0&query=";
    }

    public void run(String query) throws Exception {
        int length=0;
        URL u=new URL(url+Utilities.urlEncode(query));
        long start=System.currentTimeMillis();
        BufferedReader in = new BufferedReader(new InputStreamReader(
            u.openStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            length+=inputLine.length();
        }
        System.out.println("elapsed time="+(System.currentTimeMillis()-start));
        in.close();
        System.out.println("length="+length);
    }
    
    public static void main(String[] args) throws Exception {
        SRUTimer timer=new SRUTimer("http://levan-r:8080/SRW/search/VIAF");
        for(int i=0; i<10; i++)
            timer.run("auth.Name=austen");
    }
}
