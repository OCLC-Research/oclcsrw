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
 * SRWDatabasePool.java
 *
 * Created on August 2, 2005, 3:55 PM
 */

package ORG.oclc.os.SRW;
import gov.loc.www.zing.srw.ScanRequestType;
import gov.loc.www.zing.srw.ScanResponseType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;

import java.util.LinkedList;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.z3950.zing.cql.CQLTermNode;
/**
 *
 * @author  levan
 */
public class SRWDatabasePool extends SRWDatabase {
    static Log log=LogFactory.getLog(SRWDatabasePool.class);

    LinkedList<SRWDatabase> queue=new LinkedList<SRWDatabase>();
    String dbList=null;
    
    /** Creates a new instance of SRWDatabasePool */
    public SRWDatabasePool() {
    }

    private void addMoreDbs() {
        if(!queue.isEmpty()) // someone put a db back or called us already
            return;
        String part;
        StringTokenizer st=new StringTokenizer(dbList, ", \t");
        log.info("adding "+st.countTokens()+" databases to the queue for database "+dbname);
        while(st.hasMoreTokens()) {
            part=st.nextToken();
            log.info("adding database "+part+" to the queue for database "+dbname);
            queue.add(getDB(part, srwProperties));
        }
        dbs.put(dbname, queue);
    }

    public void addRenderer(String schemaName, String schemaID, Properties props) throws InstantiationException {
        log.error("we shouldn't have been called directly!");
    }

    public String getExtraResponseData(QueryResult result, SearchRetrieveRequestType request) {
        log.error("we shouldn't have been called directly!");
        return null;
    }

    public String getIndexInfo() {
        log.error("we shouldn't have been called directly!");
        return ((SRWDatabase)queue.getFirst()).getIndexInfo();
    }

    public QueryResult getQueryResult(String query, SearchRetrieveRequestType request) throws InstantiationException {
        log.error("we shouldn't have been called directly!");
        return null;
    }

    public TermList getTermList(CQLTermNode term, int position, int maxTerms,
      ScanRequestType request) {
        log.error("we shouldn't have been called directly!");
        return null;
    }

    public void init(String dbname, String srwHome, String dbHome, String dbPropertiesFileName, java.util.Properties dbProperties) throws Exception {
        dbList=dbProperties.getProperty("DBList");
        this.dbname=dbname;
        addMoreDbs();
    }
    
    public boolean supportsSort() {
        log.error("we shouldn't have been called directly!");
        return ((SRWDatabase)queue.getFirst()).supportsSort();
    }
}
