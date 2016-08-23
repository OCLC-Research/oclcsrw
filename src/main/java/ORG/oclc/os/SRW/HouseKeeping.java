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
 * HouseKeeping.java
 *
 * Created on November 2, 2004, 2:19 PM
 */

package ORG.oclc.os.SRW;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  levan
 */
public class HouseKeeping extends java.util.TimerTask {
    static Log log=LogFactory.getLog(HouseKeeping.class);
    private int executionCount=0;

    private static class GoneAWOLException extends Exception {
        private static final long serialVersionUID = 1L;

        public GoneAWOLException(String checkoutReason) {
        }
    }

    HashMap<String, Long> timers;
    HashMap<String, QueryResult> oldResultSets;
    
    public HouseKeeping(final HashMap<String, Long> timers,
      final HashMap<String, QueryResult> oldResultSets) {
        this.timers=timers;
        this.oldResultSets=oldResultSets;
    }
    
    @Override
    public void run() {
        Iterator<QueryResult> enum2;
        HashMap<String, QueryResult> sortedResults;
        Iterator<String> timerIterator;
        LinkedList<SRWDatabase> queue;
        long   now=System.currentTimeMillis(), when;
        QueryResult result, sortedResult;
        String resultSetName;
        for(timerIterator=(new HashMap<String, Long>(timers)).keySet().iterator(); timerIterator.hasNext();) {
            resultSetName=timerIterator.next();
            when=timers.get(resultSetName);
            if(when<now) {
                log.debug("removing resultSet "+resultSetName);
                result=oldResultSets.remove(resultSetName);
                if(result!=null) {
                    sortedResults=result.getSortedResults();
                    enum2=sortedResults.values().iterator();
                    while(enum2.hasNext()) {
                        sortedResult=enum2.next();
                        sortedResult.close();
                    }
                    sortedResults.clear();
                    result.close();
                }
                else {
                    Exception e = new Exception("No QueryResult found for resultSetName '"+resultSetName+"'");
                    e.printStackTrace(System.out);
                }
                timers.remove(resultSetName);
            }
        }
        for (SRWDatabase db : SRWDatabase.allDbs) {
            // this test will catch things that have been checked out for
            // more than 10 minutes
            if(!db.reportedAWOL && db.checkoutTime>db.checkinTime &&
                    db.checkoutTime+600000<now) {
                Exception e=new GoneAWOLException("checked out: "+db.dbname);
                log.error(e, e);
                log.error("checked out: "+db.dbname);
                log.error(db);
                log.error(db.checkoutReason);
                db.reportedAWOL=true;
            }
            // verify that all the checked in databases are on their queue
            if(db.checkinTime>db.checkoutTime) {
                queue=SRWDatabase.dbs.get(db.dbname);
                if(!queue.contains(db) && db.checkinTime>db.checkoutTime) {
                    // redundant time test because the database might have been
                    // checked out between the first test and the second
                    Exception e=new GoneAWOLException("off queue: "+db.dbname);
                    log.error(e, e);
                    log.error(db);
                    log.error(db.checkoutReason);
                    db.reportedAWOL=true;
                }
            }
        }
        if(log.isDebugEnabled() && executionCount++%10==0) { // this should happen about every 10 minutes
            HashSet<String> stackTops=new HashSet<String>();
            int idleCount=0;
            int tCount = Thread.activeCount();
            log.debug("Thread count="+tCount);
            StackTraceElement[] trace;
            Thread t;
            Thread[] threads=new Thread[tCount];
            tCount=Thread.enumerate(threads);
            for(int i=0; i<tCount; i++) {
                t=threads[i];
                trace = t.getStackTrace();
                if(trace.length==0)
                    idleCount++;
                else {
                    log.debug(t.getName()+": "+trace[0]);
                    if(stackTops.add(trace[0].toString()))
                        for(StackTraceElement ste:trace)
                            System.out.println(ste);
                }
            }
            log.debug(idleCount+" idle threads");
        }
    }
}
