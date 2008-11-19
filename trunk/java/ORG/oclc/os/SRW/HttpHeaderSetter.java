package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author levan
 */
public interface HttpHeaderSetter {
    public void init(Properties props);
    public void setGetResponseHeaders(SearchRetrieveRequestType searchRequest,
            SearchRetrieveResponseType searchResponse, String soapResponse,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse);
    public void setDeleteResponseHeaders(String record,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse);
    public void setPostResponseHeaders(String record,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse);
    public void setPutResponseHeaders(String record,
            HttpServletRequest request, HttpServletResponse response);
}
