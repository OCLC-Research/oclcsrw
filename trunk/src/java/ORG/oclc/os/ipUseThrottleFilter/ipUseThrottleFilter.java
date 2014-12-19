/*
   Copyright 2012 OCLC Online Computer Library Center, Inc.

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

package ORG.oclc.os.ipUseThrottleFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author levan
 */
public class ipUseThrottleFilter implements Filter {
    static Log log=LogFactory.getLog(ipUseThrottleFilter.class);
    static final ConcurrentHashMap<String, Integer> simultaneousRequestsByShortIPAddr=new ConcurrentHashMap<String, Integer>();
    static final HashMap<String, String> equivalentAddresses=new HashMap<String, String>();
    static final HashMap<String, Integer> totalRequests=new HashMap<String, Integer>();
    static final Set<String> simultaneousRequests=new HashSet<String>();
    static int /*maxSimultaneousRequests=3,*/ maxTotalSimultaneousRequests=10, nextReportingHour;
    static int addressInHeaderErrorCount=0, totalSimultaneousRequests=0;
    static String contactInfo=null;
    private String addressInHeader;

    @Override
    public void init(FilterConfig fc) throws ServletException {
        Calendar c=new GregorianCalendar();
        nextReportingHour=c.get(Calendar.HOUR_OF_DAY)+1;
        String t=fc.getInitParameter("maxTotalSimultaneousRequests");
        if(t!=null)
            try {
                maxTotalSimultaneousRequests=Integer.parseInt(t);
            }
            catch(NumberFormatException e) {
                log.error("Bad value for parameter 'maxTotalSimultaneousRequests': '"+t+"'");
                log.error("Using the default value of 10 instead");
            }

//        t=fc.getInitParameter("maxSimultaneousRequests");
//        if(t!=null)
//        try {
//            maxSimultaneousRequests=Integer.parseInt(t);
//        }
//        catch(Exception e) {
//            log.error("Bad value for parameter 'maxSimultaneousRequests': '"+t+"'");
//            log.error("Using the default value of 3 instead");
//        }

        contactInfo=fc.getInitParameter("contactInfo");
        if(log.isDebugEnabled())
            log.debug("contactInfo="+contactInfo);
        addressInHeader=fc.getInitParameter("addressInHeader");
        if(log.isDebugEnabled())
            log.debug("addressInHeader="+addressInHeader);
        
        String eA=fc.getInitParameter("equivalentAddresses");
        if(eA!=null) { // comma/blank/tab separated list of shortAddr=shortAddr
            // e.g. 157.55.33=157.55.32, 157.55.34=157.55.32, 157.55.35=157.55.32, 157.55.36=157.55.32, 157.55.37=157.55.32
            // or 157.55.*=157.55.0
            // or 69.171.224-255=69.171.224
            String first, pair, second;
            StringTokenizer findPairs=new StringTokenizer(eA, ",\t ");
            StringTokenizer findValues;
            while(findPairs.hasMoreTokens()) {
                pair=findPairs.nextToken();
                findValues=new StringTokenizer(pair, "=");
                first=findValues.nextToken();
                second=findValues.nextToken();
                if(first.endsWith(".*")) {
                    first=first.substring(0, first.indexOf(".*"));
                    for(int i=0; i<256; i++)
                        equivalentAddresses.put(first+"."+i, second);
                }
                else if(first.contains("-")) {
                    int offset=first.lastIndexOf('.');
                    String base=first.substring(0, offset);
                    String rest=first.substring(offset+1);
                    StringTokenizer range=new StringTokenizer(rest, "-");
                    int start=Integer.parseInt(range.nextToken());
                    int end=Integer.parseInt(range.nextToken());
                    for(int i=start; i<end; i++) {
                        System.out.println("adding "+base+"."+i+"="+second+" to the equivalence table");
                        equivalentAddresses.put(base+"."+i, second);
                    }
                }
                else
                    equivalentAddresses.put(first, second);
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String longAddr=null, shortAddr, s, transactionKey;
        int count;
        synchronized(simultaneousRequestsByShortIPAddr) {
            if(totalSimultaneousRequests>=maxTotalSimultaneousRequests) {
                log.error("This system has exceeded the maxTotalSimultaneousRequests limit of "+maxTotalSimultaneousRequests);
                log.error(simultaneousRequestsByShortIPAddr);
                for(String str:simultaneousRequests)
                    log.error(str);
                ((HttpServletResponse)response).setStatus(HttpURLConnection.HTTP_UNAVAILABLE);
                response.setContentType("text/html");
                PrintWriter writer = response.getWriter();
                writer.println( "<html><body><h1>Service Temporarily Unavailable</h1>" );
                writer.println( "The system is experiencing a severe load and is temporarily unable to accept new requests");
                if(contactInfo!=null)
                    writer.println("<p>Contact "+contactInfo+" for more information</p>");
                writer.println("</body></html>");
                writer.close();
                return;
            }
            if(addressInHeader!=null) {
                @SuppressWarnings("unchecked")
                Enumeration<String> addrs = ((HttpServletRequest)request).getHeaders(addressInHeader);
                while(addrs.hasMoreElements()) {
                    longAddr=addrs.nextElement();
                    if(longAddr==null) {
                        if(++addressInHeaderErrorCount<10)
                            log.error("Expected a "+addressInHeader+" header but got null");
                        continue;
                    }
                    if(longAddr.lastIndexOf('.')>=0)
                        break;
                }
            }
            if(longAddr==null)
                longAddr=request.getRemoteAddr();
            int i=longAddr.lastIndexOf('.');
            if(i<0) {
                log.error("bogus IP address: '"+longAddr+"'");
                longAddr="0.0.0.0";
            }
            shortAddr=longAddr.substring(0, i); // trim off 4th number group
                // that lets us spot requests from clusters
            s=equivalentAddresses.get(shortAddr); // map one short addr to another?
            if(s!=null)
                shortAddr=s;
            Integer icount=simultaneousRequestsByShortIPAddr.get(shortAddr);
            if(icount!=null)
                count=icount;
            else
                count=0;

            int maxSimultaneousRequests=(maxTotalSimultaneousRequests-totalSimultaneousRequests)/4;
            if(maxSimultaneousRequests==0)
                maxSimultaneousRequests=1;
            if(count>=maxSimultaneousRequests) {
                log.error("IP addr "+shortAddr+".* has exceeded "+maxSimultaneousRequests+" simultaneous requests!");
                log.error("maxTotalSimultaneousRequests="+maxTotalSimultaneousRequests);
                log.error("totalSimultaneousRequests="+totalSimultaneousRequests);
                for(String str:simultaneousRequests)
                    log.error(str);
//                ((HttpServletResponse)response).setStatus(HttpURLConnection.HTTP_TOO_MANY_REQUESTS); // someday
                ((HttpServletResponse)response).setStatus(429); // too many requests
                response.setContentType("text/html");
                PrintWriter writer = response.getWriter();
                writer.println( "<html><head><title>Too Many Requests</title></head><body><h1>Too Many Requests</h1>" );
                writer.println( "You have exceeded the maximum simultaneous request value of "+maxSimultaneousRequests);
                writer.println("<p>This message and your IP address have been logged and reported</p>");
                if(contactInfo!=null)
                    writer.println("<p>Contact "+contactInfo+" for more information</p>");
                writer.println("</body></html>");
                writer.close();
                return;
            }
            simultaneousRequestsByShortIPAddr.put(shortAddr, count+1);
            icount=totalRequests.get(shortAddr);
            if(icount!=null)
                count=icount;
            else
                count=0;
            totalRequests.put(shortAddr, count+1);
            totalSimultaneousRequests++;
            transactionKey=new StringBuilder((new Date(System.currentTimeMillis())).toString()).append('|').append(shortAddr).append('|').append(((HttpServletRequest)request).getQueryString()).toString();
            simultaneousRequests.add(transactionKey);
        }

        try {
            HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse)response);
            chain.doFilter(request, wrapper);
        }
        finally {
            synchronized(simultaneousRequestsByShortIPAddr) {
                totalSimultaneousRequests--;
                simultaneousRequests.remove(transactionKey);
                count=simultaneousRequestsByShortIPAddr.get(shortAddr);
                if(count==1) // prune them from the table
                    simultaneousRequestsByShortIPAddr.remove(shortAddr);
                else
                    simultaneousRequestsByShortIPAddr.put(shortAddr, count-1);
            }
        }

        Calendar c=new GregorianCalendar();
        int hour=c.get(Calendar.HOUR_OF_DAY);
        if(hour==0 && nextReportingHour==24) { // new day!
            // you could reset your daily limits table here
            nextReportingHour=0;
        }

        if(hour>=nextReportingHour) { // generate the hourly report
            // you could reset your hourly limits table here
            nextReportingHour=hour+1;

            if(log.isInfoEnabled()) {
                HashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
                List<String> yourMapKeys = new ArrayList<String>(totalRequests.keySet());
                List<Integer> yourMapValues = new ArrayList<Integer>(totalRequests.values());
                TreeSet<Integer> sortedSet = new TreeSet<Integer>(yourMapValues);
                Integer[] sortedArray = sortedSet.descendingSet().toArray(new Integer[0]);
                int size = sortedArray.length;

                for (int i=0; i<size; i++)
                    map.put(yourMapKeys.get(yourMapValues.indexOf(sortedArray[i])),
                        sortedArray[i]);
                Iterator<String> it=map.keySet().iterator();
                String key;
                StringBuilder sb=new StringBuilder("Top 10 users in the last hour");
                for(int i=0; i<10 && it.hasNext(); i++) {
                    key=it.next();
                    sb.append("\n    ").append(key).append(" : ").append(map.get(key));
                }
                log.info(sb);
            }
            totalRequests.clear();
        }
    }

    @Override
    public void destroy() {
    }
    
}
