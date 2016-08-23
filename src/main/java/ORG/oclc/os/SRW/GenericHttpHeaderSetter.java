/*
Expression licensePrefix is undefined on line 4, column 3 in Templates/Licenses/license-default.txt.To change this template, choose Tools | Templates
Expression licensePrefix is undefined on line 5, column 3 in Templates/Licenses/license-default.txt.and open the template in the editor.
*/

package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author levan
 */
public class GenericHttpHeaderSetter implements HttpHeaderSetter {
    private static Log log=LogFactory.getLog(GenericHttpHeaderSetter.class);
    private boolean checkEveryRecord=false;

    @Override
    public void init(Properties props) {
        String str=props.getProperty("GenericHttpHeaderSetter.checkEveryRecord");
        if(str!=null && "true".equals(str.toLowerCase()))
            checkEveryRecord=true;
        if(log.isDebugEnabled()) {
            log.debug("checkEveryRecord="+checkEveryRecord);
        }
    }

    @Override
    public int setGetResponseHeaders(SearchRetrieveRequestType searchRequest,
      SearchRetrieveResponseType searchResponse, String soapResponse,
      HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        int status=-1;
        String extraStuff=httpRequest.getParameter("extraStuff");
        String location=(String)httpRequest.getAttribute("Location");
        String statusStr=(String)httpRequest.getAttribute("status");
        if(statusStr!=null)
            status=Integer.parseInt(statusStr);
        if(extraStuff!=null && extraStuff.startsWith("\""))
            extraStuff=extraStuff.substring(1);
        if(extraStuff!=null && extraStuff.startsWith("/"))
            extraStuff=extraStuff.substring(1);
        if(extraStuff!=null && extraStuff.endsWith("\""))
            extraStuff=extraStuff.substring(0, extraStuff.length()-1);
        if(log.isDebugEnabled()) {
            log.debug("Location="+location);
            log.debug("status="+status);
            log.debug("extraStuff="+extraStuff);
        }
        if(status>=0 || checkEveryRecord) {
            if(log.isDebugEnabled())
                log.debug("soapResponse="+soapResponse);
            if(status==-1) { // look for status in response
                int start=soapResponse.indexOf("status>");
                if(start>0) {
                    start+=7;
                    int end=soapResponse.indexOf("</", start);
                    status=Integer.parseInt(soapResponse.substring(start, end));
                    if(log.isDebugEnabled()) {
                        log.debug("status="+status);
                    }
                }
                if(status==-1)
                    return -1;
            }
            httpResponse.setStatus(status);
            if(status>=300 && status<400 && location==null) {
                // redirect without location?  better be in the soapResponse!
//                System.out.println(soapResponse);
                int start=soapResponse.indexOf("location>");
                if(start>0) {
                    start+=9;
                    while(Character.isWhitespace(soapResponse.charAt(start)))
                        start++;
                    int end=soapResponse.indexOf("</", start);
                    location=soapResponse.substring(start, end);
                    if(extraStuff!=null && extraStuff.length()>0) {
                        if(location.endsWith("/"))
                            location=location+extraStuff;
                        else
                            location=location+"/"+extraStuff;
                    }
                    if(log.isDebugEnabled()) {
                        log.debug("Location="+location);
                    }
                }
            }
        }
        if(location!=null)
            httpResponse.setHeader("Location", location);
        return status;
    }

    @Override
    public void setDeleteResponseHeaders(String record, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    }

    @Override
    public void setPostResponseHeaders(String record, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    }

    @Override
    public void setPutResponseHeaders(String record, HttpServletRequest request, HttpServletResponse response) {
    }

}
