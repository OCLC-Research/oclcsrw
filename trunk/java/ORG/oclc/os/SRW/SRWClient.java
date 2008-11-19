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
 * SRWClient.java
 *
 * Created on November 19, 2002, 1:53 PM
 */

package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.interfaces.ExplainPort;
import gov.loc.www.zing.srw.interfaces.SRWPort;
import gov.loc.www.zing.srw.ExplainRequestType;
import gov.loc.www.zing.srw.ExplainResponseType;
import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.RecordsType;
import gov.loc.www.zing.srw.ScanRequestType;
import gov.loc.www.zing.srw.ScanResponseType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.service.SRWSampleServiceLocator;
import gov.loc.www.zing.srw.StringOrXmlFragment;
import gov.loc.www.zing.srw.TermType;
import gov.loc.www.zing.srw.TermsType;

import java.net.URL;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;
/**
 *
 * @author  levan
 */
public class SRWClient {
    public static void main(String[] args) {
        try {
            SRWSampleServiceLocator service=new SRWSampleServiceLocator();
            URL url=new URL(
//                  "http://alcme.oclc.org/srw/search/SOAR");
                  //"http://localhost:8082/srw/search/SOAR");
                  //"http://localhost:8082/SRW/search/SOAR");
                  "http://localhost:8082/SRW/search/GSAFD");
                  //"http://srw.cheshire3.org:8080/l5r");
                  //"http://localhost:8082/l5r");
                  //"http://www.rdn.ac.uk:8080/xxdefault");
                  //"http://localhost:8082/xxdefault");
            ExplainPort explain=service.getExplainSOAP(url);
            ExplainRequestType explainRequest=new ExplainRequestType();
            explainRequest.setRecordPacking("xml");
            explainRequest.setVersion("1.1");
            ExplainResponseType explainResponse=explain.explainOperation(explainRequest);
            System.out.println("explainResponse="+explainResponse);
            
            SRWPort port=service.getSRW(url);
            ScanRequestType scanRequest=new ScanRequestType();
            scanRequest.setVersion("1.1");
            scanRequest.setScanClause("education");
            ScanResponseType scanResponse=port.scanOperation(scanRequest);
            if(scanResponse!=null) {
                TermsType terms=scanResponse.getTerms();
                if(terms!=null) {
                    TermType[] term=terms.getTerm();
                    System.out.println(term.length+" terms returned");
                    for(int i=0; i<term.length; i++)
                        System.out.println(term[i].getValue()+"("+term[i].getNumberOfRecords().intValue()+")");
                }
                else
                    System.out.println("0 terms returned");
            }
            else
                System.out.println("no scan response returned");

            SearchRetrieveRequestType request=new SearchRetrieveRequestType();
            request.setVersion("1.1");
            request.setQuery("en and education");
            //request.setQuery("dc.title any sword");
            request.setRecordSchema("info:srw/schema/1/dc-v1.1");
            request.setStartRecord(new PositiveInteger("1"));
            request.setMaximumRecords(new NonNegativeInteger("1"));
            request.setRecordPacking("xml");
            SearchRetrieveResponseType response=
                port.searchRetrieveOperation(request);
            System.out.println("postings="+response.getNumberOfRecords());
            RecordType[] record;
            RecordsType records=response.getRecords();
            if(records==null || (record=records.getRecord())==null)
                System.out.println("0 records returned");
            else {
                System.out.println(record.length+" records returned");
                System.out.println("record[0] has record number "+
                record[0].getRecordPosition());
                StringOrXmlFragment frag=record[0].getRecordData();
                MessageElement[] elems=frag.get_any();
                System.out.println("record="+elems[0].toString());
            }
            System.out.println("nextRecordPosition="+response.getNextRecordPosition());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
