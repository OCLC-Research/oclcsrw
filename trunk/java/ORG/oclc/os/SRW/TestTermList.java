/**
 * Copyright 2006 OCLC Online Computer Library Center, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * TestTermList.java
 *
 * Created on October 20, 2006, 10:42 AM
 */

package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.TermType;
import org.apache.axis.types.PositiveInteger;

/**
 *
 * @author levan
 */
public class TestTermList extends TermList {
    
    /** Creates a new instance of TestTermList */
    public TestTermList() {
    }

    public TermType[] getTerms() {
        TermType  term[]=new TermType[6];
        term[0]=new TermType();
        term[0].setValue("levan");
        term[0].setNumberOfRecords(new PositiveInteger("1"));
        term[1]=new TermType();
        term[1].setValue("ralph");
        term[1].setNumberOfRecords(new PositiveInteger("1"));
        term[2]=new TermType();
        term[2].setValue("record");
        term[2].setNumberOfRecords(new PositiveInteger("1"));
        term[3]=new TermType();
        term[3].setValue("srw");
        term[3].setNumberOfRecords(new PositiveInteger("1"));
        term[4]=new TermType();
        term[4].setValue("test");
        term[4].setNumberOfRecords(new PositiveInteger("1"));
        term[5]=new TermType();
        term[5].setValue("test001");
        term[5].setNumberOfRecords(new PositiveInteger("1"));
        return term;
    }


}
