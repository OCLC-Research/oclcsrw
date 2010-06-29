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

    public void init(Properties props) {
    }

    public void setGetResponseHeaders(SearchRetrieveRequestType searchRequest, SearchRetrieveResponseType searchResponse, String soapResponse, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        int status=-1;
        String location=(String)httpRequest.getAttribute("Location");
        String statusStr=(String)httpRequest.getAttribute("status");
        if(statusStr!=null)
            status=Integer.parseInt(statusStr);
        if(status>=0) {
            log.info("Location="+location);
            log.info("status="+status);
            httpResponse.setStatus(status);
            if(status>=300 && status<400 && location==null) {
                // redirect without location?  better be in the soapResponse!
                System.out.println(soapResponse);
                int start=soapResponse.indexOf("<location>");
                if(start>0) {
                    start+=10;
                    int end=soapResponse.indexOf("</location>", start);
                    location=soapResponse.substring(start, end);
                    log.info("location="+location);
                }
            }
        }
        if(location!=null)
            httpResponse.setHeader("Location", location);
    }

    public void setDeleteResponseHeaders(String record, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setPostResponseHeaders(String record, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setPutResponseHeaders(String record, HttpServletRequest request, HttpServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
