/*
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 */
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
package ORG.oclc.os.SRW;

import de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator;
import de.fuberlin.wiwiss.pubby.negotiation.MediaRangeSpec;
import gov.loc.www.zing.srw.DiagnosticsType;
import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.RecordsType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.sf.json.JSON;
import net.sf.json.JSONException;
import net.sf.json.xml.XMLSerializer;
import org.apache.axis.*;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.security.servlet.ServletSecurityProvider;
import org.apache.axis.transport.http.*;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.utils.Admin;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Doug Davis (dug@us.ibm.com)
 * @author Steve Loughran xdoclet tags are not active yet; keep web.xml in sync
 * web.servlet name="AxisServlet" display-name="Apache-Axis Servlet"
 * web.servlet-mapping url-pattern="/servlet/AxisServlet" web.servlet-mapping
 * url-pattern="*.jws" web.servlet-mapping url-pattern="/services/\*"
 */
public class SRWServlet extends AxisServlet {

    protected Log servletLog = LogFactory.getLog(SRWServlet.class.getName());

    private static final long serialVersionUID = 1L;

    private String portNumber, serverAddress=null, serverName, transportName;

    private ServletSecurityProvider securityProvider = null;

    private boolean isDebug = false;
    private final Pattern queryFinder = Pattern.compile("query=([^&]+)");

    /**
     * Should we enable the "?list" functionality on GETs? (off by default
     * because deployment information is a potential security hole)
     */
    private boolean enableList = false;

    /**
     * Cached path to JWS output directory
     */
    private String jwsClassDir = null;
    private URIResolverFromDisk uriResolverFromDisk;
    private String addressInHeader;
    private int addressInHeaderErrorCount = 0;
//    private boolean APP;

    @Override
    protected String getJWSClassDir() {
        return jwsClassDir;
    }

    protected SRWServletInfo srwInfo = null;

    /**
     * create a new servlet instance
     */
    public SRWServlet() {
        if (servletLog instanceof org.apache.commons.logging.impl.SimpleLog) {
            ((org.apache.commons.logging.impl.SimpleLog) servletLog).setLevel(3); // info
        }
    }

    @Override
    public void destroy() {
        SRWDatabase.TIMER.cancel();
    }

    /**
     * Initialization method.
     *
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init() throws ServletException {
        srwInfo = new SRWServletInfo();
        ServletConfig config = getServletConfig();
        srwInfo.init(config);

        uriResolverFromDisk = new URIResolverFromDisk(SRWServletInfo.srwHome, SRWServletInfo.webappHome);
        addressInHeader = srwInfo.addressInHeader;

        super.init();

        isDebug = servletLog.isDebugEnabled();
        if (isDebug) {
            servletLog.debug("In servlet init");
        }

        ServletContext context = config.getServletContext();
        transportName = getOption(context,
                INIT_PROPERTY_TRANSPORT_NAME,
                HTTPTransport.DEFAULT_TRANSPORT_NAME);

        if (JavaUtils.isTrueExplicitly(getOption(context, INIT_PROPERTY_USE_SECURITY, null))) {
            securityProvider = new ServletSecurityProvider();
        }

        enableList
                = JavaUtils.isTrueExplicitly(getOption(context, INIT_PROPERTY_ENABLE_LIST, null));

        jwsClassDir = getOption(context, INIT_PROPERTY_JWS_CLASS_DIR, null);

        /**
         * There are DEFINATE problems here if getHomeDir and/or
         * getDefaultJWSClassDir return null (as they could with WebLogic). This
         * needs to be reexamined in the future, but this should fix any NPE's
         * in the mean time.
         */
        if (jwsClassDir != null) {
            if (getHomeDir() != null) {
                jwsClassDir = getHomeDir() + jwsClassDir;
            }
        } else {
            jwsClassDir = getDefaultJWSClassDir();
        }
    }

    void init(HttpServletRequest request) {
        if(serverAddress==null) { // some missing initialization
            ServletConfig config = getServletConfig();
            portNumber = config.getInitParameter("portNumber");
            if (servletLog.isDebugEnabled()) {
                servletLog.debug("portNumber=" + portNumber);
            }
            if (portNumber == null) {
                portNumber = ":"+request.getLocalPort();
            } else if (!portNumber.startsWith(":")) {
                portNumber = ":" + portNumber;
            }

            serverName = config.getInitParameter("serverName");
            if (servletLog.isDebugEnabled()) {
                servletLog.debug("serverName=" + serverName);
            }
            if (serverName == null) {
                serverName = "localhost";
            }

            serverAddress = "http://" + serverName + portNumber;
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MessageContext msgContext = createMessageContext(getEngine(), request, response);
        if (!srwInfo.setSRWStuff(request, response, msgContext)) {
            servletLog.error("srwInfo.setSRWStuff failed!");
            return;
        }
        init(request);
        String uri = request.getRequestURI();
//        APP=true;
        if (servletLog.isDebugEnabled()) {
            servletLog.debug("in doDelete: got APP request for " + uri);
        }
        SRWDatabase db = (SRWDatabase) msgContext.getProperty("db");
        if (servletLog.isDebugEnabled()) {
            servletLog.debug("deleting from database " + db);
        }
        if (servletLog.isDebugEnabled()) {
            servletLog.debug("deleting from database " + db.dbname);
        }
        String user = null;
        String authhead = request.getHeader(HTTPConstants.HEADER_AUTHORIZATION);
        if (authhead != null) {
            int i = authhead.indexOf("username=\"");
            if (i >= 0) {
                int j = authhead.indexOf('"', i + 10);
                user = authhead.substring(i + 10, j);
            }
        }
        if (servletLog.isDebugEnabled()) {
            servletLog.debug("user: " + user);
        }
        if (user == null && !db.isAnonymousUpdates()) {
            response.setStatus(HttpURLConnection.HTTP_FORBIDDEN);
            return;
        }
        try {
            RecordMetadata rm = new RecordMetadata();
            rm.setUser(user);
            rm.setComment(request.getHeader("X-Comment"));
            rm.setWorkflowStatus(request.getHeader("X-WorkflowStatus"));
            if (db.delete(db.extractRecordIdFromUri(uri), rm)) {
                response.setStatus(HttpURLConnection.HTTP_OK);
            } else {
                response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
            }
        } catch (UnsupportedOperationException e) {
            servletLog.warn("delete not supported by database " + db, e);
            response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
        } catch (Exception e) {
            throw new ServletException("DELETE failed", e);
        } finally {
            SRWDatabase.putDb((String) msgContext.getProperty("dbname"), db);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MessageContext msgContext = createMessageContext(getEngine(), request, response);
        if (!srwInfo.setSRWStuff(request, response, msgContext)) {
            servletLog.error("srwInfo.setSRWStuff failed!");
            return;
        }
//        APP=true;
        if (servletLog.isDebugEnabled()) {
            servletLog.debug("in doPut: got APP request for " + request.getRequestURI());
        }
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET");
        SRWDatabase db = (SRWDatabase) msgContext.getProperty("db");
        if (servletLog.isDebugEnabled()) {
            servletLog.debug("replacing in database " + db);
        }
        if (servletLog.isDebugEnabled()) {
            servletLog.debug("replacing in database " + db.dbname);
        }
        InputStream is = request.getInputStream();
        int len, offset = 0, totlen = request.getContentLength();
        byte[] b = new byte[totlen];
        while (totlen > 0) {
            len = is.read(b, offset, totlen);
            totlen -= len;
            offset += len;
        }
        if (servletLog.isDebugEnabled()) {
            servletLog.debug("replacing data in database: " + new String(b, "UTF-8"));
        }
        String uri = request.getRequestURI();
        String user = null;
        String authhead = request.getHeader(HTTPConstants.HEADER_AUTHORIZATION);
        if (authhead != null) {
            int i = authhead.indexOf("username=\"");
            if (i >= 0) {
                int j = authhead.indexOf('"', i + 10);
                user = authhead.substring(i + 10, j);
            }
        }
        if (servletLog.isDebugEnabled()) {
            servletLog.debug("user: " + user);
        }
        if (user == null && !db.isAnonymousUpdates()) {
            response.setStatus(HttpURLConnection.HTTP_FORBIDDEN);
            return;
        }
        try {
            RecordMetadata rm = new RecordMetadata();
            rm.setUser(user);
            rm.setComment(request.getHeader("X-Comment"));
            rm.setWorkflowStatus(request.getHeader("X-WorkflowStatus"));
            servletLog.debug(db);
            db.replace(db.extractRecordIdFromUri(uri), b, rm);
            if (db.httpHeaderSetter != null) {
                db.httpHeaderSetter.setPutResponseHeaders(new String(b, "UTF-8"), request, response);
            }
            response.setStatus(HttpURLConnection.HTTP_OK);
        } finally {
            SRWDatabase.putDb((String) msgContext.getProperty("dbname"), db);
        }
    }

    /**
     * Process GET requests. This includes handoff of pseudo-SOAP requests
     *
     * @param request request in
     * @param response request out
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        servletLog.debug("Enter: doGet()");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET");
        init(request);

        try {
            AxisEngine engine = getEngine();
            ServletContext servletContext
                    = getServletConfig().getServletContext();

            String pathInfo = request.getPathInfo();
            String realpath = servletContext.getRealPath(request.getServletPath());
            if (realpath == null) {
                realpath = request.getServletPath();
            }

            boolean wsdlRequested = false;
            boolean listRequested = false;
            boolean hasParameters = request.getParameterNames().hasMoreElements();

            //JWS pages are special; they are the servlet path and there
            //is no pathinfo...we map the pathinfo to the servlet path to keep
            //it happy
            boolean isJWSPage = request.getRequestURI().endsWith(".jws");
            if (isJWSPage) {
                pathInfo = request.getServletPath();
            }

            // check first if we are doing WSDL or a list operation
            String queryString = request.getQueryString();
            boolean doExplain = false;
            if (queryString != null) {
                if (queryString.equalsIgnoreCase("wsdl")) {
                    wsdlRequested = true;
                } else if (queryString.equalsIgnoreCase("list")) {
                    listRequested = true;
                } else {
                    String operation = request.getParameter("operation");
                    if (operation != null) {
                        if (operation.equals("explain")) {
                            doExplain = true;
                        }
                    } else if (request.getParameter("query") == null
                            && request.getParameter("scanClause") == null) {
                        doExplain = true;
                    }
                }
            } else {
                doExplain = true;
            }

            boolean hasNoPath = (pathInfo == null || pathInfo.equals(""));
            if (!wsdlRequested && !listRequested && hasNoPath && srwInfo.defaultDatabase == null) {
                // If the user requested the servlet (i.e. /axis/servlet/AxisServlet)
                // with no service name, present the user with a list of deployed
                // services to be helpful
                // Don't do this if we are doing WSDL or list.
                reportAvailableServices(response, request);
            } else if (realpath != null || doExplain) {
                // We have a pathname, so now we perform WSDL or list operations

                // get message context w/ various properties set
                MessageContext msgContext = createMessageContext(engine, request, response);

                if (doExplain) {
                    srwInfo.handleExplain(request, response, msgContext);
                    return;
                }

                // NOTE:  HttpUtils.getRequestURL has been deprecated.
                // This line SHOULD be:
                //    String url = req.getRequestURL().toString()
                // HOWEVER!!!!  DON'T REPLACE IT!  There's a bug in
                // req.getRequestURL that is not in HttpUtils.getRequestURL
                // req.getRequestURL returns "localhost" in the remote
                // scenario rather than the actual host name.
                //
                // ? Still true?  For which JVM's?
                //String url = HttpUtils.getRequestURL(request).toString();
                String url = request.getRequestURL().toString();

                msgContext.setProperty(MessageContext.TRANS_URL, url);

                if (wsdlRequested) {
                    // Do WSDL generation
                    msgContext.setTargetService("SRW");
                    processWsdlRequest(msgContext, response);
                } else if (listRequested) {
                    // Do list, if it is enabled
                    processListRequest(response);
                } else if (hasParameters) {
                    // If we have ?method=x&param=y in the URL, make a stab
                    // at invoking the method with the parameters specified
                    // in the URL

                    if (!srwInfo.setSRWStuff(request, response, msgContext)) {
                        servletLog.error("srwInfo.setSRWStuff failed!");
                        return;
                    }
                    try {
                        processMethodRequest(msgContext, request, response);
                    } finally {
                        SRWDatabase.putDb((String) msgContext.getProperty("dbname"),
                                (SRWDatabase) msgContext.getProperty("db"));
                    }

                } else {

                    // See if we can locate the desired service.  If we
                    // can't, return a 404 Not Found.  Otherwise, just
                    // print the placeholder message.
                    String serviceName;
                    if (pathInfo != null && pathInfo.startsWith("/")) {
                        serviceName = pathInfo.substring(1);
                    } else {
                        serviceName = pathInfo;
                    }

                    SOAPService s = engine.getService(serviceName);
                    if (s == null) {
                        //no service: report it
                        if (isJWSPage) {
                            reportCantGetJWSService(request, response);
                        } else {
                            reportCantGetAxisService(request, response);
                        }

                    } else {
                        //print a snippet of service info.
                        reportServiceInfo(response, s, serviceName);
                    }
                }
            } else {
                // We didn't have a real path in the request, so just
                // print a message informing the user that they reached
                // the servlet.

                response.setContentType("text/html");
                try (PrintWriter writer = response.getWriter()) {
                    writer.println("<html><h1>Axis HTTP Servlet</h1>");
                    writer.println(Messages.getMessage("reachedServlet00"));
                    
                    writer.println("<p>"
                            + Messages.getMessage("transportName00",
                                    "<b>" + transportName + "</b>"));
                    writer.println("</html>");
                }
            }
        } catch (AxisFault fault) {
            servletLog.error(fault, fault);
            reportTroubleInGet(fault, response);
        } catch (IOException e) {
            servletLog.error(e, e);
            reportTroubleInGet(e, response);
        } catch (ServletException | MissingResourceException e) {
            servletLog.error(e, e);
            reportTroubleInGet(e, response);
        } finally {
            //writer.close();
            servletLog.debug("Exit: doGet()");
        }
    }

    @Override
    public void doHead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        servletLog.debug("Enter: doHead()");
        response.setHeader("srwRequestMethod", "HEAD");
        doGet(request, response);
    }

    @Override
    public void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        servletLog.debug("Enter: doOptions()");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET");
        super.doOptions(request, response);
    }

    /**
     * when we get an exception or an axis fault in a GET, we handle it almost
     * identically: we go 'something went wrong', set the response code to 500
     * and then dump info. But we dump different info for an axis fault or
     * subclass thereof.
     *
     * @param exception what went wrong
     * @param response current response
     */
    private void reportTroubleInGet(Exception exception, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        servletLog.error(exception, exception);
        try (PrintWriter writer = response.getWriter()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.println("<h2>"
                    + Messages.getMessage("error00")
                    + "</h2>");
            writer.println("<p>"
                    + Messages.getMessage("somethingWrong00")
                    + "</p>");
            if (exception instanceof AxisFault) {
                AxisFault fault = (AxisFault) exception;
                processAxisFault(fault);
                writeFault(writer, fault);
            } else {
                logException(exception);
                writer.println("<pre>Exception - " + exception + "<br>");
                //dev systems only give fault dumps
                if (isDevelopment()) {
                    writer.println(JavaUtils.stackToString(exception));
                }
                writer.println("</pre>");
            }
        }
    }

    /**
     * routine called whenever an axis fault is caught; where they are logged
     * and any other business. The method may modify the fault in the process
     *
     * @param fault what went wrong.
     */
    @Override
    protected void processAxisFault(AxisFault fault) {
        //log the fault
        Element runtimeException = fault.lookupFaultDetail(
                Constants.QNAME_FAULTDETAIL_RUNTIMEEXCEPTION);
        if (runtimeException != null) {
            //strip runtime details
            fault.removeFaultDetail(Constants.QNAME_FAULTDETAIL_RUNTIMEEXCEPTION);
        }
        //dev systems only give fault dumps
        if (!isDevelopment()) {
            //strip out the stack trace
            fault.removeFaultDetail(Constants.QNAME_FAULTDETAIL_STACKTRACE);
        }
    }

    /**
     * log any exception to our output log, at our chosen level
     *
     * @param e what went wrong
     */
//    protected void logException(Exception e) {
//        exceptionLog.info(Messages.getMessage("exception00"), e);
//    }

    /**
     * this method writes a fault out to an HTML stream. This includes escaping
     * the strings to defend against cross-site scripting attacks
     *
     * @param writer
     * @param axisFault
     */
    private void writeFault(PrintWriter writer, AxisFault axisFault) {
        String localizedMessage = XMLUtils.xmlEncodeString(axisFault.getLocalizedMessage());
        writer.println("<pre>Fault - " + localizedMessage + "<br>");
        writer.println(axisFault.dumpToString());
        writer.println("</pre>");
    }

    /**
     * handle a ?wsdl request
     *
     * @param msgContext message context so far
     * @param response response to write to
     * @throws AxisFault when anything other than a Server.NoService fault is
     * reported during WSDL generation
     */
    protected void processWsdlRequest(MessageContext msgContext,
            HttpServletResponse response) throws AxisFault, IOException {
        AxisEngine engine = getEngine();
        PrintWriter writer;
        try {
            engine.generateWSDL(msgContext);
            Document doc = (Document) msgContext.getProperty("WSDL");
            if (doc != null) {
                response.setContentType("text/xml");
                writer = response.getWriter();
                XMLUtils.DocumentToWriter(doc, writer);
                writer.close();
            } else {
                if (servletLog.isDebugEnabled()) {
                    servletLog.debug("processWsdlRequest: failed to create WSDL");
                }
                reportNoWSDL(response, "noWSDL02", null);
            }
        } catch (AxisFault axisFault) {
            //the no-service fault is mapped to a no-wsdl error
            if (axisFault.getFaultCode().equals(Constants.QNAME_NO_SERVICE_FAULT_CODE)) {
                //which we log
                processAxisFault(axisFault);
                //then report under a 404 error
                response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
                reportNoWSDL(response, "noWSDL01", axisFault);
            } else {
                //all other faults get thrown
                throw axisFault;
            }
        }
    }

    /**
     * invoke an endpoint from a get request by building an XML request and
     * handing it down. If anything goes wrong, we generate an XML formatted
     * axis fault
     *
     * @param msgContext current message
     * @param response to return data
     * @param method method to invoke (may be null)
     * @param args argument list in XML form
     * @throws AxisFault iff something goes wrong when turning the response
     * message into a SOAP string.
     */
    protected void invokeEndpointFromGet(MessageContext msgContext,
            HttpServletResponse response,
            String method,
            String args) throws AxisFault, IOException {
        String body
                = "<" + method + ">" + args + "</" + method + ">";

        String msgtxt
                = "<SOAP-ENV:Envelope"
                + " xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<SOAP-ENV:Body>" + body + "</SOAP-ENV:Body>"
                + "</SOAP-ENV:Envelope>";

        Message responseMsg = null;
        try {
            ByteArrayInputStream istream
                    = new ByteArrayInputStream(msgtxt.getBytes("ISO-8859-1"));

            AxisEngine engine = getEngine();
            Message msg = new Message(istream, false);
            msgContext.setRequestMessage(msg);
            engine.invoke(msgContext);
            responseMsg = msgContext.getResponseMessage();
            //turn off caching for GET requests
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            if (responseMsg == null) {
                //tell everyone that something is wrong
                throw new Exception(Messages.getMessage("noResponse01"));
            }
        } catch (AxisFault fault) {
            processAxisFault(fault);
            configureResponseFromAxisFault(response, fault);
            if (responseMsg == null) {
                responseMsg = new Message(fault);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMsg = convertExceptionToAxisFault(e, responseMsg);
        }
        //this call could throw an AxisFault. We delegate it up, because
        //if we cant write the message there is not a lot we can do in pure SOAP terms.
        response.setContentType("text/xml");
        try (PrintWriter writer = response.getWriter()) {
            writer.println(responseMsg.getSOAPPartAsString());
        }
    }

    /**
     * print a snippet of service info.
     *
     * @param response
     * @param service
     * @param serviceName where to put stuff
     * @throws java.io.IOException
     */
    protected void reportServiceInfo(HttpServletResponse response, SOAPService service, String serviceName) throws IOException {
        response.setContentType("text/html");
        try (PrintWriter writer = response.getWriter()) {
            writer.println("<h1>"
                    + service.getName()
                    + "</h1>");
            writer.println(
                    "<p>"
                            + Messages.getMessage("axisService00")
                            + "</p>");
            writer.println(
                    "<i>"
                            + Messages.getMessage("perhaps00")
                            + "</i>");
        }
    }

    /**
     * respond to the ?list command. if enableList is set, we list the engine
     * config. If it isnt, then an error is written out
     *
     * @param response
     * @throws AxisFault, IOException
     */
    protected void processListRequest(HttpServletResponse response) throws AxisFault, IOException {
        AxisEngine engine = getEngine();
        if (enableList) {
            Document doc = Admin.listConfig(engine);
            if (doc != null) {
                response.setContentType("text/xml");
                try (PrintWriter writer = response.getWriter()) {
                    XMLUtils.DocumentToWriter(doc, writer);
                }
            } else {
                //error code is 404
                response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
                response.setContentType("text/html");
                try (PrintWriter writer = response.getWriter()) {
                    writer.println("<h2>"
                            + Messages.getMessage("error00")
                            + "</h2>");
                    writer.println("<p>"
                            + Messages.getMessage("noDeploy00")
                            + "</p>");
                }
            }
        } else {
            // list not enable, return error
            //error code is, what, 401
            response.setStatus(HttpURLConnection.HTTP_FORBIDDEN);
            response.setContentType("text/html");
            try (PrintWriter writer = response.getWriter()) {
                writer.println("<h2>"
                        + Messages.getMessage("error00")
                        + "</h2>");
                writer.println("<p><i>?list</i> "
                        + Messages.getMessage("disabled00")
                        + "</p>");
            }
        }
    }

    /**
     * report that we have no WSDL
     *
     * @param res
     * @param moreDetailCode optional name of a message to provide more detail
     * @param axisFault optional fault string, for extra info at debug time only
     * @throws java.io.IOException
     */
    protected void reportNoWSDL(HttpServletResponse res,
            String moreDetailCode, AxisFault axisFault) throws IOException {
        res.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
        res.setContentType("text/html");
        try (PrintWriter writer = res.getWriter()) {
            writer.println("<h2>"
                    + Messages.getMessage("error00")
                    + "</h2>");
            writer.println("<p>"
                    + Messages.getMessage("noWSDL00")
                    + "</p>");
            if (moreDetailCode != null) {
                writer.println("<p>"
                        + Messages.getMessage(moreDetailCode)
                        + "</p>");
            }
            
            if (axisFault != null && isDevelopment()) {
                //dev systems only give fault dumps
                writeFault(writer, axisFault);
            }
        }
    }

    /**
     * This method lists the available services; it is called when there is
     * nothing to execute on a GET
     *
     * @param response
     * @param request
     * @throws ConfigurationException
     * @throws AxisFault
     */
    protected void reportAvailableServices(HttpServletResponse response,
            HttpServletRequest request)
            throws ConfigurationException, AxisFault, IOException {
        AxisEngine engine = getEngine();
        response.setContentType("text/html");
        try (PrintWriter writer = response.getWriter()) {
            writer.println("<h2>And now... Some Services</h2>");
            Iterator<?> i;
            try {
                i = engine.getConfig().getDeployedServices();
            } catch (ConfigurationException configException) {
                //turn any internal configuration exceptions back into axis faults
                //if that is what they are
                Exception e = configException.getContainedException();
                if (e instanceof AxisFault) {
                    throw (AxisFault) e;
                } else {
                    throw configException;
                }
            }
            String baseURL = getWebappBase(request) + "/services/";
            writer.println("<ul>");
            while (i.hasNext()) {
                ServiceDesc sd = (ServiceDesc) i.next();
                StringBuilder sb = new StringBuilder();
                sb.append("<li>");
                String name = sd.getName();
                sb.append(name);
                sb.append(" <a href=\"");
                sb.append(baseURL);
                sb.append(name);
                sb.append("?wsdl\"><i>(wsdl)</i></a></li>");
                writer.println(sb.toString());
                ArrayList<?> operations = sd.getOperations();
                if (!operations.isEmpty()) {
                    writer.println("<ul>");
                    for (Iterator<?> it = operations.iterator(); it.hasNext();) {
                        OperationDesc desc = (OperationDesc) it.next();
                        writer.println("<li>" + desc.getName());
                    }
                    writer.println("</ul>");
                }
            }
            writer.println("</ul>");
        }    }

    /**
     * generate the error response to indicate that there is apparently no
     * endpoint there
     *
     * @param request the request that didnt have an edpoint
     * @param response response we are generating
     * @throws java.io.IOException
     */
    protected void reportCantGetAxisService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // no such service....
        response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
        response.setContentType("text/html");
        try (PrintWriter writer = response.getWriter()) {
            writer.println("<h2>"
                    + Messages.getMessage("error00") + "</h2>");
            writer.println("<p>"
                    + Messages.getMessage("noService06")
                    + "</p>");
        }
    }

    /**
     * probe for a JWS page and report 'no service' if one is not found there
     *
     * @param request the request that didnt have an edpoint
     * @param response response we are generating
     * @throws java.io.IOException
     */
    protected void reportCantGetJWSService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //first look to see if there is a service
        String realpath
                = getServletConfig().getServletContext()
                .getRealPath(request.getServletPath());
        boolean foundJWSFile = (new File(realpath).exists())
                && (realpath.endsWith(Constants.JWS_DEFAULT_FILE_EXTENSION));
        response.setContentType("text/html");
        try (PrintWriter writer = response.getWriter()) {
            if (foundJWSFile) {
                response.setStatus(HttpURLConnection.HTTP_OK);
                writer.println(Messages.getMessage("foundJWS00") + "<p>");
                String url = request.getRequestURI();
                String urltext = Messages.getMessage("foundJWS01");
                writer.println("<a href='" + url + "?wsdl'>" + urltext + "</a>");
            } else {
                response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
                writer.println(Messages.getMessage("noService06"));
            }
        }
    }

    /**
     * Process a POST to the servlet by handing it off to the Axis Engine. Here
     * is where SOAP messages are received
     *
     * @param req posted request
     * @param res respose
     * @throws ServletException trouble
     * @throws IOException different trouble
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String soapAction;
        MessageContext msgContext;
        if (isDebug) {
            servletLog.debug("Enter: doPost()");
        }
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET");

        Message responseMsg = null;
        String contentType = req.getContentType().toLowerCase();
        if (servletLog.isDebugEnabled()) {
            servletLog.debug("in SRWServlet.doPost: contentType=" + contentType);
        }
        if (contentType.indexOf("atom+xml") > 0 || contentType.equals("application/x-www-form-urlencoded")) {
            msgContext = createMessageContext(getEngine(), req, res);
            if (!srwInfo.setSRWStuff(req, res, msgContext)) {
                servletLog.error("srwInfo.setSRWStuff failed!");
                return;
            }
            SRWDatabase db = (SRWDatabase) msgContext.getProperty("db");
            try {
                if (servletLog.isDebugEnabled()) {
                    servletLog.debug("adding to database " + db);
                }
                if (servletLog.isDebugEnabled()) {
                    servletLog.debug("adding to database " + db.dbname);
                }
                InputStream is = req.getInputStream();
                int len, offset = 0, totlen = req.getContentLength();
                if (servletLog.isDebugEnabled()) {
                    servletLog.debug("adding " + totlen + " bytes to database");
                }
                byte[] b = new byte[totlen];
                while (totlen > 0) {
                    len = is.read(b, offset, totlen);
                    totlen -= len;
                    offset += len;
                }
                if (servletLog.isDebugEnabled()) {
                    servletLog.debug("adding data to database: " + new String(b, "UTF-8"));
                }
                if (contentType.equals("application/x-www-form-urlencoded")) {
//                    String converted=convertFormDataToTrivialXML(new String(b, "UTF-8"));
//                    Transformer trans=null;
//                    trans=db.transformers.get(mediaType);
//                    if(servletLog.isDebugEnabled())
//                        servletLog.debug("trans="+trans);
//                    if(trans!=null) {
//                        StringWriter toRec = new StringWriter();
//                        StreamSource fromRec = new StreamSource(new StringReader(soapResponse));
//                        try {
//                            trans.transform(fromRec, new StreamResult(toRec));
//                            soapResponse=toRec.toString();
//                            if(soapResponse.substring(0, Math.min(100, soapResponse.length())).contains("<?xml-stylesheet")) {
//                                if(!mediaType.contains("html"))
//                                    resp.setContentType("text/xml");
//                            }
//                            else
//                                resp.setContentType(mediaType);
//                        } catch (TransformerException e) {
//                            servletLog.error(e, e);
//                            resp.setContentType("text/xml");
//                            sos=resp.getOutputStream();
//                            srwInfo.writeXmlHeader(sos, msgContext, req,
//                                db.searchStyleSheet);
//                            if(db.httpHeaderSetter!=null)
//                                db.httpHeaderSetter.setGetResponseHeaders(db.searchRequest, db.response, soapResponse, req, resp);
//                        }
                }
                String user = null;
                String authhead = req.getHeader(HTTPConstants.HEADER_AUTHORIZATION);
                if (authhead != null) {
                    int i = authhead.indexOf("username=\"");
                    if (i >= 0) {
                        int j = authhead.indexOf('"', i + 10);
                        user = authhead.substring(i + 10, j);
                    }
                }
                if (servletLog.isDebugEnabled()) {
                    servletLog.debug("user: " + user);
                }
                if (user == null && !db.isAnonymousUpdates()) {
                    res.setStatus(HttpURLConnection.HTTP_FORBIDDEN);
                    return;
                }
                RecordMetadata rm = new RecordMetadata();
                rm.setUser(user);
                rm.setComment(req.getHeader("X-Comment"));
                rm.setWorkflowStatus(req.getHeader("X-WorkflowStatus"));
                String recordID = db.add(b, rm);
                if (recordID != null) {
                    if (db.httpHeaderSetter != null) {
                        db.httpHeaderSetter.setPostResponseHeaders(new String(b, "UTF-8"), req, res);
                    }
                    res.setHeader("Content-Location", recordID + "/");
                    res.setStatus(HttpURLConnection.HTTP_CREATED);
                } else {
                    servletLog.warn("db.add failed!  returning status code 500");
                    res.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
                }
            } catch (IOException e) {
                servletLog.error(e, e);
                throw new ServletException(e);
            } finally {
                SRWDatabase.putDb((String) msgContext.getProperty("dbname"), db);
            }
            servletLog.debug("exit: doPost() after processing atompub request");
            return;
        }

        AxisEngine engine = getEngine();

        if (engine == null) {
            // !!! should return a SOAP fault...
            ServletException se
                    = new ServletException(Messages.getMessage("noEngine00"));
            servletLog.debug("No Engine!", se);
            throw se;
        }

        res.setBufferSize(1024 * 8); // provide performance boost.

        /**
         * get message context w/ various properties set
         */
        msgContext = createMessageContext(engine, req, res);

        try {
            // ? OK to move this to 'getMessageContext',
            // ? where it would also be picked up for 'doGet()' ?
            if (securityProvider != null) {
                if (isDebug) {
                    servletLog.debug("securityProvider:" + securityProvider);
                }
                msgContext.setProperty(MessageContext.SECURITY_PROVIDER, securityProvider);
            }

            /* Get request message
             */
            Message requestMsg
                    = new Message(req.getInputStream(),
                            false,
                            req.getHeader(HTTPConstants.HEADER_CONTENT_TYPE),
                            req.getHeader(HTTPConstants.HEADER_CONTENT_LOCATION));

            if (isDebug) {
                servletLog.debug("Request Message:" + requestMsg);
            }

            /* Set the request(incoming) message field in the context */
            /**
             * *******************************************************
             */
            msgContext.setRequestMessage(requestMsg);
            //String url = HttpUtils.getRequestURL(req).toString();
            String url = req.getRequestURL().toString();
            msgContext.setProperty(MessageContext.TRANS_URL, url);

            try {
                /**
                 * Save the SOAPAction header in the MessageContext bag. This
                 * will be used to tell the Axis Engine which service is being
                 * invoked. This will save us the trouble of having to parse the
                 * Request message - although we will need to double-check later
                 * on that the SOAPAction header does in fact match the URI in
                 * the body.
                 */
                // (is this last stmt true??? (I don't think so - Glen))
                /**
                 * *****************************************************
                 */
                soapAction = getSoapAction(req);

                if (soapAction != null) {
                    msgContext.setUseSOAPAction(true);
                    msgContext.setSOAPActionURI(soapAction);
                } else { // not a SOAP request
                    String content = new String(((SOAPPart) (requestMsg.getSOAPPart())).getAsBytes());
                    if (content.startsWith("<")) { // must be an ATOM request
                        return;
                    }
                    if (content.contains("query=") || content.contains("scanClause=")) {
                        try {
                            srwInfo.setSRWStuff(req, res, msgContext);
                            processMethodRequest(msgContext, new LateContentParsingHttpServletRequestWrapper(req, content), res);
                        } finally {
                            SRWDatabase.putDb((String) msgContext.getProperty("dbname"),
                                    (SRWDatabase) msgContext.getProperty("db"));
                        }
                        return;
                    }

                    // nothing I recognize
                    AxisFault af = new AxisFault("Client.NoSOAPAction",
                            Messages.getMessage("noHeader00",
                                    "SOAPAction"),
                            null, null);

                    throw af;
                }

                // Create a Session wrapper for the HTTP session.
                // These can/should be pooled at some point.
                // (Sam is Watching! :-)
                msgContext.setSession(new AxisHttpSession(req));

                srwInfo.setSRWStuff(req, res, msgContext);

                /* Invoke the Axis engine... */
                /**
                 * **************************
                 */
                if (isDebug) {
                    servletLog.debug("Invoking Axis Engine.");
                }
                //here we run the message by the engine
                engine.invoke(msgContext);
                if (isDebug) {
                    servletLog.debug("Return from Axis Engine.");
                }
                responseMsg = msgContext.getResponseMessage();
                if (responseMsg == null) {
                    //tell everyone that something is wrong
                    throw new Exception(Messages.getMessage("noResponse01"));
                }
            } catch (AxisFault fault) {
                //log and sanitize
                processAxisFault(fault);
                configureResponseFromAxisFault(res, fault);
                responseMsg = msgContext.getResponseMessage();
                if (responseMsg == null) {
                    responseMsg = new Message(fault);
                }
            } catch (Exception e) {
                //other exceptions are internal trouble
                responseMsg = msgContext.getResponseMessage();
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                responseMsg = convertExceptionToAxisFault(e, responseMsg);
            } finally {
                SRWDatabase.putDb((String) msgContext.getProperty("dbname"), (SRWDatabase) msgContext.getProperty("db"));
            }

        } catch (AxisFault fault) {
            processAxisFault(fault);
            configureResponseFromAxisFault(res, fault);
            responseMsg = msgContext.getResponseMessage();
            if (responseMsg == null) {
                responseMsg = new Message(fault);
            }
        }
        //determine content type from message response
        if (responseMsg == null) {
            //tell everyone that something is wrong
            throw new ServletException(Messages.getMessage("noResponse02"));
        }
        contentType = responseMsg.getContentType(msgContext.getSOAPConstants());

        /* Send response back along the wire...  */
        /**
         * ********************************
         */
        sendResponse(getProtocolVersion(req), contentType, res, responseMsg);

        if (isDebug) {
            servletLog.debug("Response sent.");
            servletLog.debug("Exit: doPost()");
        }
    }

    /**
     * Configure the servlet response status code and maybe other headers from
     * the fault info.
     *
     * @param response response to configure
     * @param fault what went wrong
     */
    private void configureResponseFromAxisFault(HttpServletResponse response,
            AxisFault fault) {
        // then get the status code
        // It's been suggested that a lack of SOAPAction
        // should produce some other error code (in the 400s)...
        int status = getHttpServletResponseStatus(fault);
        if (status == HttpServletResponse.SC_UNAUTHORIZED) {
            // unauth access results in authentication request
            response.setHeader("WWW-Authenticate", "Basic realm=\"AXIS\"");
        }
        response.setStatus(status);
    }

    /**
     * turn any Exception into an AxisFault, log it, set the response status
     * code according to what the specifications say and return a response
     * message for posting. This will be the response message passed in if
     * non-null; one generated from the fault otherwise.
     *
     * @param exception what went wrong
     * @param responseMsg what response we have (if any)
     * @return a response message to send to the user
     */
    private Message convertExceptionToAxisFault(Exception exception,
            Message responseMsg) {
        logException(exception);
        if (responseMsg == null) {
            AxisFault fault = AxisFault.makeFault(exception);
            processAxisFault(fault);
            responseMsg = new Message(fault);
        }
        return responseMsg;
    }

    /**
     * Extract information from AxisFault and map it to a HTTP Status code.
     *
     * @param af Axis Fault
     * @return HTTP Status code.
     */
    @Override
    protected int getHttpServletResponseStatus(AxisFault af) {
        return af.getFaultCode().getLocalPart().startsWith("Server.Unauth")
                ? HttpServletResponse.SC_UNAUTHORIZED
                : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
       // This will raise a 401 for both
        // "Unauthenticated" & "Unauthorized"...
    }

    /**
     * write a message to the response, set appropriate headers for content
     * type..etc.
     *
     * @param clientVersion client protocol, one of the HTTPConstants strings
     * @param res response
     * @param responseMsg message to write
     * @throws AxisFault
     * @throws IOException if the response stream can not be written to
     */
    private void sendResponse(final String clientVersion,
            String contentType,
            HttpServletResponse res, Message responseMsg)
            throws AxisFault, IOException {
        if (responseMsg == null) {
            res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            if (isDebug) {
                servletLog.debug("NO AXIS MESSAGE TO RETURN!");
            }
            //String resp = Messages.getMessage("noData00");
            //res.setContentLength((int) resp.getBytes().length);
            //res.getWriter().print(resp);
        } else {
            if (isDebug) {
                servletLog.debug("Returned Content-Type:"
                        + contentType);
                // servletLog.debug("Returned Content-Length:" +
                //          responseMsg.getContentLength());
            }

            try {
                servletLog.debug("#3: setting ContentType to " + contentType);
                res.setContentType(contentType);

                /* My understand of Content-Length
                 * HTTP 1.0
                 *   -Required for requests, but optional for responses.
                 * HTTP 1.1
                 *  - Either Content-Length or HTTP Chunking is required.
                 *   Most servlet engines will do chunking if content-length is not specified.
                 *
                 *
                 */
                //if(clientVersion == HTTPConstants.HEADER_PROTOCOL_V10) //do chunking if necessary.
                //     res.setContentLength(responseMsg.getContentLength());
                responseMsg.writeTo(res.getOutputStream());
            } catch (SOAPException e) {
                logException(e);
            }
        }

        if (!res.isCommitted()) {
            res.flushBuffer(); // Force it right now.
        }
    }

    /**
     * Place the Request message in the MessagContext object - notice that we
     * just leave it as a 'ServletRequest' object and let the Message processing
     * routine convert it - we don't do it since we don't know how it's going to
     * be used - perhaps it might not even need to be parsed.
     *
     * @return a message context
     */
    private MessageContext createMessageContext(AxisEngine engine,
            HttpServletRequest req,
            HttpServletResponse res) {
        MessageContext msgContext = new MessageContext(engine);

        if (isDebug) {
            servletLog.debug("MessageContext:" + msgContext);
            servletLog.debug("HEADER_CONTENT_TYPE:"
                    + req.getHeader(HTTPConstants.HEADER_CONTENT_TYPE));
            servletLog.debug("HEADER_CONTENT_LOCATION:"
                    + req.getHeader(HTTPConstants.HEADER_CONTENT_LOCATION));
            servletLog.debug("Constants.MC_HOME_DIR:" + String.valueOf(getHomeDir()));
            servletLog.debug("Constants.MC_RELATIVE_PATH:" + req.getServletPath());

            servletLog.debug("HTTPConstants.MC_HTTP_SERVLETLOCATION:" + String.valueOf(getWebInfPath()));
            servletLog.debug("HTTPConstants.MC_HTTP_SERVLETPATHINFO:"
                    + req.getPathInfo());
            servletLog.debug("HTTPConstants.HEADER_AUTHORIZATION:"
                    + req.getHeader(HTTPConstants.HEADER_AUTHORIZATION));
            servletLog.debug("Constants.MC_REMOTE_ADDR:" + req.getRemoteAddr());
            servletLog.debug("configPath:" + String.valueOf(getWebInfPath()));
        }

        /* Set the Transport */
        /**
         * ******************
         */
        msgContext.setTransportName(transportName);

        /* Save some HTTP specific info in the bag in case someone needs it */
        /**
         * *****************************************************************
         */
        msgContext.setProperty(Constants.MC_JWS_CLASSDIR, jwsClassDir);
        msgContext.setProperty(Constants.MC_HOME_DIR, getHomeDir());
        msgContext.setProperty(Constants.MC_RELATIVE_PATH,
                req.getServletPath());
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLET, this);
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST, req);
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE, res);
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETLOCATION,
                getWebInfPath());
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETPATHINFO,
                req.getPathInfo());
        msgContext.setProperty(HTTPConstants.HEADER_AUTHORIZATION,
                req.getHeader(HTTPConstants.HEADER_AUTHORIZATION));
        msgContext.setProperty(Constants.MC_REMOTE_ADDR, req.getRemoteAddr());

        // Set up a javax.xml.rpc.server.ServletEndpointContext
        ServletEndpointContextImpl sec = new ServletEndpointContextImpl();

        msgContext.setProperty(Constants.MC_SERVLET_ENDPOINT_CONTEXT, sec);
        /* Save the real path */
        /**
         * *******************
         */
        String realpath
                = getServletConfig().getServletContext()
                .getRealPath(req.getServletPath());

        if (realpath != null) {
            msgContext.setProperty(Constants.MC_REALPATH, realpath);
        }

        msgContext.setProperty(Constants.MC_CONFIGPATH, getWebInfPath());
//        msgContext.setProperty("targetURL", req.getRequestURL().append('?').append(req.getQueryString()).toString());
//        servletLog.debug("targetURL property set to "+msgContext.getProperty("targetURL"));

        return msgContext;
    }

    /**
     * Extract the SOAPAction header. if SOAPAction is null then we'll we be
     * forced to scan the body for it. if SOAPAction is "" then use the URL
     *
     * @param req incoming request
     * @return the action
     * @throws AxisFault
     */
    private String getSoapAction(HttpServletRequest req)
            throws AxisFault {
        String soapAction = req.getHeader(HTTPConstants.HEADER_SOAP_ACTION);

        if (isDebug) {
            servletLog.debug("HEADER_SOAP_ACTION:" + soapAction);
        }

        /**
         * Technically, if we don't find this header, we should probably fault.
         * It's required in the SOAP HTTP binding.
         */
        if (soapAction != null && soapAction.length() == 0) {
            soapAction = req.getContextPath(); // Is this right?
        }
        return soapAction;
    }

    private String getXmlContent(String xml, String tag) {
        int start=xml.indexOf("<"+tag);
        if(start<0)
            return null;
        start = xml.indexOf('>', start + 1);
        int stop = xml.indexOf("</"+tag+">", start);
        return xml.substring(start+1, stop);
    }

    /**
     * Provided to allow overload of default JWSClassDir by derived class.
     *
     * @return directory for JWS files
     */
    @Override
    protected String getDefaultJWSClassDir() {
        return (getWebInfPath() == null)
                ? null // ??? what is a good FINAL default for WebLogic?
                : getWebInfPath() + File.separator + "jwsClasses";
    }

    private MediaInfo getMediaType(HttpServletRequest request, HashMap<String, String> mediaTypes, ContentTypeNegotiator conneg) {
        String mediaType;
        MediaInfo response=new MediaInfo();
        response.mimeType = request.getParameter("http:accept");
        if (response.mimeType == null) {
            servletLog.debug("1: no mimeType");
            response.mimeType = request.getParameter("httpAccept");
        } else {
            servletLog.debug("1: mimeType=" + response.mimeType);
        }
        if (response.mimeType == null) {
            servletLog.debug("2: no mimeType");
            response.mimeType = request.getHeader("accept");
        } else {
            servletLog.debug("2: mimeType=" + response.mimeType);
        }
        if (response.mimeType == null || response.mimeType.length()==0) {
            servletLog.debug("3: no mimeType");
            response.mimeType = mediaType = "text/xml";
        } else {
            servletLog.debug("3: mimeType=" + response.mimeType);
            mediaType = mediaTypes.get(response.mimeType);
            if (mediaType == null) { // never seen this mimeType before
                if (servletLog.isDebugEnabled()) {
                    servletLog.debug("conneg=" + conneg);
                }
                MediaRangeSpec mrs = conneg.getBestMatch(response.mimeType);
                if (mrs != null) {
                    mediaType = mrs.getMediaType();
                } else {
                    mediaType = "";
                }
                mediaTypes.put(response.mimeType, mediaType);
            }
        }
        if (servletLog.isInfoEnabled()) {
            String ipAddress = null;
            if (addressInHeader != null) {
                @SuppressWarnings("unchecked")
                Enumeration<String> addrs = request.getHeaders(addressInHeader);
                while (addrs.hasMoreElements()) {
                    ipAddress = addrs.nextElement();
                    if (ipAddress == null) {
                        if (++addressInHeaderErrorCount < 10) {
                            servletLog.warn("Found a " + addressInHeader + " header but had value null");
                        }
                        continue;
                    }
                    if (ipAddress.lastIndexOf('.') >= 0) {
                        break;
                    }
                }
                if (ipAddress == null && ++addressInHeaderErrorCount < 10 && !request.getRemoteAddr().startsWith("127.0.0")) {
                    servletLog.warn("Expected a " + addressInHeader + " header but never found one");
                    @SuppressWarnings("unchecked")
                    Enumeration<String> headerNames = request.getHeaderNames();
                    String headerName;
                    servletLog.info("available headers are:");
                    while (headerNames.hasMoreElements()) {
                        headerName = headerNames.nextElement();
                        servletLog.info('\t' + headerName + ": " + request.getHeader(headerName));
                    }
                }
            }
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
            servletLog.info("mimeType=" + response.mimeType + " and mediaType=" + mediaType + " from IP=" + ipAddress);
        }
        if (response.mimeType != null && mediaType != null && mediaType.length() > 0) {
            response.negotiatedContent = true;
        }
        response.mediaType=mediaType;
        return response;
    }

    /**
     * Return the HTTP protocol level 1.1 or 1.0 by derived class.
     *
     * @param req
     * @return one of the HTTPConstants values
     */
    protected String getProtocolVersion(HttpServletRequest req) {
        String ret = HTTPConstants.HEADER_PROTOCOL_V10;
        String prot = req.getProtocol();
        if (prot != null) {
            int sindex = prot.indexOf('/');
            if (-1 != sindex) {
                String ver = prot.substring(sindex + 1);
                if (HTTPConstants.HEADER_PROTOCOL_V11.equals(ver.trim())) {
                    ret = HTTPConstants.HEADER_PROTOCOL_V11;
                }
            }
        }
        return ret;
    }

//    static Runtime rt=Runtime.getRuntime();
    protected void processMethodRequest(
            org.apache.axis.MessageContext msgContext, HttpServletRequest req,
            HttpServletResponse resp)
            throws org.apache.axis.AxisFault, IOException, ServletException {
        long startTime = System.currentTimeMillis();
        servletLog.debug("enter processMethodRequest");
//        servletLoginfo("at start: totalMemory="+rt.totalMemory()+", freeMemory="+rt.freeMemory());
        SRWDatabase db = (SRWDatabase) msgContext.getProperty("db");
        if (servletLog.isDebugEnabled()) {
            Enumeration<?> enumer = req.getParameterNames();
            String name;
            while (enumer.hasMoreElements()) {
                name = (String) enumer.nextElement();
                servletLog.debug(name + "=\"" + req.getParameter(name) + "\"");
            }
        }
        String operation = req.getParameter("operation"), query=null,
                scanClause = req.getParameter("scanClause");
        // getParameter kept interpreting the request as latin-1, despite
        // the uriEncoding="utf8" in the tomcat connector
        Matcher m=queryFinder.matcher(req.getQueryString());
        if(m.find())
            query=m.group(1);
        if (servletLog.isDebugEnabled()) {
            servletLog.debug("in processMethodRequest: operation=" + operation);
        }
        if (query != null) {
            if (servletLog.isDebugEnabled()) {
                servletLog.debug("in processMethodRequest: query:\n" + Utilities.byteArrayToString(query.getBytes(StandardCharsets.UTF_8)));
            }
        }
        if (scanClause != null) {
            if (servletLog.isDebugEnabled()) {
                servletLog.debug("in processMethodRequest: scanClause:\n" + Utilities.byteArrayToString(scanClause.getBytes(StandardCharsets.UTF_8)));
            }
        }

        boolean app="APP".equals(req.getAttribute("service")) || "APP".equals(req.getParameter("service"));
        MediaInfo mediaInfo=null;
        if(!app) {
            synchronized(srwInfo.conneg) {
                mediaInfo=getMediaType(req, srwInfo.mediaTypes, srwInfo.conneg);
            }
            if(servletLog.isInfoEnabled())
                servletLog.info("server defined mediaType: "+mediaInfo.mediaType);
        }
        if (mediaInfo==null || mediaInfo.mediaType.equals("")) {// nothing at the server level.  Maybe the db?
            mediaInfo = getMediaType(req, db.mediaTypes, db.conneg);
            if(servletLog.isInfoEnabled())
                servletLog.info("database defined mediaType: "+mediaInfo.mediaType);
        }
        String recordPacking=req.getParameter("recordPacking");
        if(recordPacking==null && mediaInfo.mimeType.contains("json") && !app)
            recordPacking="json";

        if ((operation != null && operation.equals("searchRetrieve")) || query != null) { // searchRetrieveRequest
            int i;
            StringBuilder sb = new StringBuilder();

            sb.append("<soap:Envelope ")
                    .append("xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" ")
                    .append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ")
                    .append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">")
                    .append("<soap:Body xmlns:srw=\"http://www.loc.gov/zing/srw/\">")
                    .append("<srw:searchRetrieveRequest>")
                    .append("<srw:query>").append(encode(query)).append("</srw:query>");
            if(recordPacking!=null)
                sb.append("<srw:recordPacking>").append(encode(recordPacking)).append("</srw:recordPacking>");

            // is this a request for a single record?
//            if("APP".equals(req.getAttribute("service"))) {
//                System.out.println("in SRWServlet.processMethodRequest: processing an APP request");
//                // should we set a recordSchema parameter?
//                if(req.getParameter("recordSchema")==null) { // if one wasn't provided
//                    String recordSchema=db.recordSchemas.get(mediaType);
//                    if(recordSchema!=null) {
//                        System.out.println("in SRWServlet.processMethodRequest: adding recordSchema="+recordSchema+" parm to request");
//                        req.setAttribute("recordSchema", recordSchema);
//                    }
//                    else
//                        System.out.println("in SRWServlet.processMethodRequest: mediaType "+mediaType+" has no associated recordSchema");
//                }
//            }
            boolean badRequest = false;
            String extension, namespace, parm, t;
            Enumeration<?> parms = req.getParameterNames();
            OUTER:
            while (parms.hasMoreElements()) {
                parm = (String) parms.nextElement();
                switch (parm) {
                    case "sortKeys":
                        t = req.getParameter(parm);
                        if (t != null) {
                            sb.append("<srw:sortKeys>").append(encode(t)).append("</srw:sortKeys>");
                        }   break;
                    case "startRecord":
                        t = req.getParameter(parm);
                        if (t != null) {
                            try {
                                i = Integer.parseInt(t);
                                if (i < 1) {
                                    i = Integer.MAX_VALUE;
                                }
                            } catch (NumberFormatException e) {
                                db.response = new SearchRetrieveResponseType(new NonNegativeInteger("0"), null, null, null, null, null, null, null);
                                SRWDatabase.diagnostic(SRWDiagnostic.UnsupportedParameterValue, "startRecord=" + t, db.response);
                                badRequest = true;
                                break OUTER;
                            }
                            sb.append("<srw:startRecord>").append(i).append("</srw:startRecord>");
                        }
                        break;
                    case "maximumRecords":
                        t = req.getParameter(parm);
                        if (t != null) {
                            try {
                                i = Integer.parseInt(t);
                                if (i < 0) {
                                    i = Integer.MAX_VALUE;
                                }
                            } catch (NumberFormatException e) {
                                i = Integer.MAX_VALUE;
                            }
                            
                            sb.append("<srw:maximumRecords>").append(i).append("</srw:maximumRecords>");
                        }   break;
                    case "recordSchema":
                        t = req.getParameter(parm);
                        if (t == null) {
                            t = (String) req.getAttribute(parm);
                        }   if (t != null) {
                            sb.append("<srw:recordSchema>").append(encode(t)).append("</srw:recordSchema>");
                        }   break;
                    case "resultSetTTL":
                        t = req.getParameter(parm);
                        if (t != null) {
                            try {
                                i = Integer.parseInt(t);
                                if (i < 0) {
                                    i = Integer.MAX_VALUE;
                                }
                                sb.append("<srw:resultSetTTL>").append(i).append("</srw:resultSetTTL>");
                            } catch (NumberFormatException e) {
                                db.response = new SearchRetrieveResponseType(new NonNegativeInteger("0"), null, null, null, null, null, null, null);
                                SRWDatabase.diagnostic(SRWDiagnostic.UnsupportedParameterValue, "resultSetTTL=" + t, db.response);
                                badRequest = true;
                                break OUTER;
                            }
                        }
                        break;
                    case "version":
                        t = req.getParameter(parm);
                        if (t != null) {
                            sb.append("<srw:version>").append(encode(t)).append("</srw:version>");
                        }   break;
                    default:
                        break;
                }
            }
            boolean hasExtraRequestData = false;
            parms = req.getParameterNames();
            while (parms.hasMoreElements()) {
                parm = (String) parms.nextElement();
                servletLog.debug("parm=" + parm);
                extension = srwInfo.getExtension(parm);
                servletLog.debug("extension=" + extension);
                if (extension != null) {
                    if (!hasExtraRequestData) {
                        sb.append("<srw:extraRequestData>");
                    }
                    namespace = srwInfo.getNamespace(parm);
                    sb.append("    <").append(extension).append(" xmlns=\"").append(namespace).append("\"");
                    t = req.getParameter(parm);
                    if (t != null && t.length() > 0) {
                        sb.append(">").append(t).append("</").append(extension).append(">");
                    } else {
                        sb.append("/>");
                    }
                    hasExtraRequestData = true;
                }
            }
            if (hasExtraRequestData) {
                sb.append("    </srw:extraRequestData>");
                servletLog.debug(sb.toString());
            }

            sb.append("</srw:searchRetrieveRequest></soap:Body></soap:Envelope>");
            if (servletLog.isDebugEnabled()) {
                servletLog.debug("request=" + sb.toString());
                servletLog.debug(Utilities.byteArrayToString(sb.toString().getBytes(StandardCharsets.UTF_8)));
            }
            if (!badRequest) {
                servletLog.info(db.dbname + ": " + req.getQueryString());
                msgContext.setProperty("sru", "");
                AxisEngine engine = getEngine();
                ByteArrayInputStream bais = new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
                Message msg = new Message(bais, false);
                msgContext.setRequestMessage(msg);
                msgContext.setTargetService("SRW");
                try {
                    engine.invoke(msgContext);
                } catch (AxisFault e) {
                    servletLog.error(sb.toString());
                    servletLog.error(e, e);
                }
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            int numRecordsReturned = 0;
            if (db.response.getNumberOfRecords().intValue() > 0) {
                // records were found. were any returned?
                RecordsType records = db.response.getRecords();
                if (records != null) {
                    numRecordsReturned = records.getRecord().length;
                } else {
                    numRecordsReturned = 0;
                }
            }
            if (servletLog.isDebugEnabled()) {
                servletLog.debug(numRecordsReturned + " records returned");
            }
            Message respMsg = msgContext.getResponseMessage();
            String jsonp=req.getParameter("jsonp");
            if(jsonp==null)
                jsonp=req.getParameter("callback");
            boolean redirectResponse=false;
            if (respMsg != null) {
                ServletOutputStream sos = null;
                // code to strip SOAP stuff out.  Hope this can go away some day
                String soapResponse = null;
                try {
                    soapResponse = respMsg.getSOAPPartAsString();
                }
                catch(OutOfMemoryError e) {
                    servletLog.error("OutOfMemory doing "+sb.toString());
                    servletLog.error(HouseKeeping.WhatsHappening());
                    throw e;
                }
                if(servletLog.isDebugEnabled())
                    servletLog.debug("soapResponse: "+soapResponse);
                if (app) {
                    RecordType record=null;
                    String recordData=null;
                    if(db.response.getNumberOfRecords().intValue()>0) {
                        record = db.response.getRecords().getRecord(0);
                        try {
                            recordData = record.getRecordData().get_any()[0].getAsString();
                        } catch (Exception ex) {
                            Logger.getLogger(SRWServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if(servletLog.isDebugEnabled())
                            servletLog.debug("recordData: "+recordData);
                    }
                    if (record!=null && recordData!=null) {
                        String contentType = (String) req.getAttribute("ContentType");
                        mediaInfo = getMediaType(req, db.mediaTypes, db.conneg);
                        if(servletLog.isInfoEnabled())
                            servletLog.info("database defined mediaType for APP: "+mediaInfo.mediaType);
                        if (contentType == null && mediaInfo.negotiatedContent) {
                            contentType = mediaInfo.mediaType;
                        }
                        if (servletLog.isDebugEnabled()) {
                            servletLog.debug("contentType=" + contentType
                                    + ", mediaType=" + mediaInfo.mediaType
                                    + ", negotiatedContent=" + mediaInfo.negotiatedContent);
                        }
                        if (contentType == null) {
                            contentType = "text/xml";
                            mediaInfo.negotiatedContent = true;
                        }
                        // we're going to dumb-down the contentType to text/xml
                        // if we're returning some other form of xml and we
                        // think a browser is asking.  The intent is to let
                        // browsers display xml rather than download it.
                        if (servletLog.isDebugEnabled()) {
                            servletLog.debug("#1: contentType=" + contentType);
                        }
                        soapResponse = recordData;
//                        servletLog.info("app record: "+soapResponse);
                        if(soapResponse.startsWith("<re:redirect"))
                            redirectResponse=true;
                        else {
                            recordPacking=record.getRecordPacking();
                            if(servletLog.isDebugEnabled())
                                servletLog.debug("recordPacking="+recordPacking);
                            resp.setContentType(contentType);
                            if (contentType.toLowerCase().contains("html")) {
                                String styleSheet = req.getParameter("stylesheet");
                                if(styleSheet==null) {
                                    styleSheet = db.searchStyleSheet;
                                    if (numRecordsReturned == 0 && db.noRecordsStyleSheet != null) {
                                        styleSheet = db.noRecordsStyleSheet;
                                    }
                                    if (numRecordsReturned == 1 && db.singleRecordStyleSheet != null) {
                                        styleSheet = db.singleRecordStyleSheet;
                                    }
                                    if (numRecordsReturned > 1 && db.multipleRecordsStyleSheet != null) {
                                        styleSheet = db.multipleRecordsStyleSheet;
                                    }
                                }
                                soapResponse = srwInfo.getXmlHeaders(req, styleSheet) + soapResponse;
                            }
                            if (mediaInfo.negotiatedContent) {
                                String contentLocation = db.contentLocations.get(mediaInfo.mediaType);
                                if (contentLocation != null) {
                                    resp.setHeader("Content-Location", contentLocation);
                                }
                            }
                        }
                    } else { // this was an APP get, but we got nothing!
                        resp.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
                        resp.setContentType("text/html");
                        resp.setHeader("srwFailReason", "emptyAPPResponse");
                        if (!"HEAD".equals(req.getMethod())) {
                            sos = resp.getOutputStream();
                            sos.println("<html><head><title>Document not found</title></head><body><h2>404: Document not found</h2><hr/>OCLC SRW/SRU Server</body></html>");
                            sos.close();
                        }
                        return;
                    }
                } else { // extract SRU response from inside the SRW response
                    int start = soapResponse.indexOf("<searchRetrieveResponse");
                    if (start >= 0) {
                        int stop = soapResponse.indexOf("</searchRetrieveResponse>");
                        soapResponse = "<searchRetrieveResponse xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.loc.gov/zing/srw/ http://www.loc.gov/standards/sru/sru1-1archive/xml-files/srw-types.xsd\"" + soapResponse.substring(start + 23, stop + 25);
                    } else { // damn, no searchRetrieveResponse!
                        resp.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
                        resp.setHeader("srwFailReason", "no searchRetrieveResponse");
                        resp.setContentType("text/html");
                        if (!"HEAD".equals(req.getMethod())) {
                            sos = resp.getOutputStream();
                            sos.println("<html><head><title>Document not found</title></head><body><h2>404: Document not found (No searchRetrieveResponse)</h2><hr/>OCLC SRW/SRU Server</body></html>");
                            sos.close();
                        }
                        return;
                    }
                }

                // now, let's see if they wanted something other than SRU/XML
                if (soapResponse.length() > 0 && !mediaInfo.mediaType.equals("") && !redirectResponse) {
                    Transformer trans=null;
                    if(!app) {
                        Object o = srwInfo.templatesOrTransformers.get(mediaInfo.mediaType);
                        if(o!=null) {
                            if(o instanceof Templates)
                                try {
                                    trans=((Templates)o).newTransformer();
                                } catch (TransformerConfigurationException ex) {
                                    Logger.getLogger(SRWServlet.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            else
                                trans=(Transformer)o;
                        }
                    }
                    if (trans == null) {
                        Templates temp=db.templates.get(mediaInfo.mediaType);
                        if(temp!=null)
                            try {
                                trans=temp.newTransformer();
                            } catch (TransformerConfigurationException ex) {
                                Logger.getLogger(SRWServlet.class.getName()).log(Level.SEVERE, null, ex);
                            }
                    }
                    if (trans == null) {
                        trans = db.transformers.get(mediaInfo.mediaType);
                    }
                    if (servletLog.isDebugEnabled()) {
                        servletLog.debug("1 trans=" + trans);
                    }
                    if (trans != null) {
                        StringWriter toRec = new StringWriter();
                        StreamSource fromRec = new StreamSource(new StringReader(soapResponse));
                        if (servletLog.isDebugEnabled()) {
                            servletLog.debug("original record: " + soapResponse);
                        }
                        try {
                            trans.transform(fromRec, new StreamResult(toRec));
                            trans.reset();
                            soapResponse = toRec.toString();
                            if (servletLog.isDebugEnabled()) {
                                servletLog.debug("transformed record: " + soapResponse);
                            }
                            // do they want normalization applied to the result?
                            Normalizer.Form normalForm = db.normalForm.get(mediaInfo.mediaType);
//                            servletLog.info("mediaType "+mediaType+" requires normalForm "+normalForm);
                            if ((normalForm) != null) {
                                soapResponse = Normalizer.normalize(soapResponse, normalForm);
                            }
                            if (jsonp != null) {
                                soapResponse = jsonp + "(" + soapResponse + ");";
                            }
                            if (soapResponse.substring(0, Math.min(100, soapResponse.length())).contains("<?xml-stylesheet")) {
                                if (!mediaInfo.mediaType.contains("html")) {
                                    resp.setContentType("text/xml");
                                }
                            } else {
                                String contentType = mediaInfo.mediaType;
                                servletLog.debug("#2: contentType=" + contentType);
                                resp.setContentType(contentType);
                            }
                            if (mediaInfo.mediaType.equals("application/redirect+xml")) {
                                if (db.httpHeaderSetter != null) {
                                    db.httpHeaderSetter.setGetResponseHeaders(db.searchRequest, db.response, soapResponse, req, resp);
                                }
                            }
                        } catch (TransformerException e) {
                            servletLog.error(e, e);
                            resp.setContentType("text/xml");
                            if (!"HEAD".equals(req.getMethod())) {
                                sos = resp.getOutputStream();
                                String styleSheet = req.getParameter("stylesheet");
                                if(styleSheet==null) {
                                    styleSheet = db.searchStyleSheet;
                                    if (numRecordsReturned == 0 && db.noRecordsStyleSheet != null) {
                                        styleSheet = db.noRecordsStyleSheet;
                                    }
                                    if (numRecordsReturned == 1 && db.singleRecordStyleSheet != null) {
                                        styleSheet = db.singleRecordStyleSheet;
                                    }
                                    if (numRecordsReturned > 1 && db.multipleRecordsStyleSheet != null) {
                                        styleSheet = db.multipleRecordsStyleSheet;
                                    }
                                }
                                srwInfo.writeXmlHeader(sos, msgContext, req,
                                        styleSheet);
                            }
                        }
                    } else if (mediaInfo.mediaType.contains("html")) {
                        // use the stylesheet that would have gone into the XML response as the transformer
                        String styleSheet = req.getParameter("stylesheet");
                        boolean userProvidedStylesheet=true;
//                        HttpURLConnection stylesheetconn=null;
//                        if(styleSheet!=null && styleSheet.length()>0) {
//                        URL stylesheeturl=null;
//                            // make sure the user hasn't given us a bogus stylesheet
//                            stylesheeturl=new URL(serverAddress+styleSheet);
//                            if(servletLog.isDebugEnabled())
//                                servletLog.debug("stylesheet url: "+stylesheeturl);
//                            try {
//                                stylesheetconn=(HttpURLConnection) stylesheeturl.openConnection();
//                                if(servletLog.isDebugEnabled())
//                                    servletLog.debug("opened URL with response code "+stylesheetconn.getResponseCode());
//                                if(stylesheetconn.getResponseCode()!=200) {
//                                    styleSheet=null; // try again later with a standard stylesheet
//                                    servletLog.error("opened URL with response code "+stylesheetconn.getResponseCode());
//                                }
//                            }
//                            catch(ConnectException e) {
//                                servletLog.error("ConnectException for stylesheet url: "+serverAddress+styleSheet);
//                                styleSheet=null; // try again later with a standard stylesheet
//                            }
//                        }
                        if (styleSheet == null) {
                            userProvidedStylesheet=false;
                            if ("APP".equals(req.getAttribute("service"))
                                    || "APP".equals(req.getParameter("service"))) {
                                styleSheet = db.appStyleSheet;
                            }
                            if(styleSheet==null)
                                styleSheet = db.searchStyleSheet;
                            if (numRecordsReturned == 0 && db.noRecordsStyleSheet != null) {
                                styleSheet = db.noRecordsStyleSheet;
                            }
                            if (numRecordsReturned == 1 && db.singleRecordStyleSheet != null) {
                                styleSheet = db.singleRecordStyleSheet;
                            }
                            if (numRecordsReturned > 1 && db.multipleRecordsStyleSheet != null) {
                                styleSheet = db.multipleRecordsStyleSheet;
                            }
                        }
                        String languages = req.getHeader("Accept-Language");
                        String realXsl = SRWServletInfo.realXsls.get(languages + '/' + styleSheet);
                        if (servletLog.isDebugEnabled()) {
                            servletLog.debug("realXsl=" + realXsl);
                        }
                        if (realXsl == null) {
                            @SuppressWarnings("unchecked")
                            Enumeration<Locale> locales = req.getLocales();
                            styleSheet = SRWServletInfo.findRealXsl(languages, locales, styleSheet);
                        } else {
                            styleSheet = realXsl;
                        }
//                                if(!styleSheet.startsWith("/")) // relative URL
//                                    styleSheet=req.getContextPath()+"/"+styleSheet;
                        if (styleSheet == null) { // crap
                            servletLog.warn("req.getParameter(\"stylesheet\")=" + req.getParameter("stylesheet"));
                            servletLog.warn("APP=" + ("APP".equals(req.getAttribute("service"))
                                    || "APP".equals(req.getParameter("service"))));
                            servletLog.warn("db.appStyleSheet=" + db.appStyleSheet);
                            servletLog.warn("db.searchStyleSheet=" + db.searchStyleSheet);
                            servletLog.warn("languages=" + languages);
                            servletLog.warn("realXsl=" + realXsl);
                        }
                        if (servletLog.isDebugEnabled()) {
                            servletLog.debug("styleSheet=" + styleSheet);
                        }
                        Templates templ=db.templates.get(styleSheet);
                        if(templ!=null) { // turn the template into a transformer
                            try {
                                trans=templ.newTransformer();
                            } catch (TransformerConfigurationException ex) {
                                Logger.getLogger(SRWServlet.class.getName()).log(Level.SEVERE, null, ex);
                                trans=null;
                            }
                        }
                        else
                            trans = db.transformers.get(styleSheet);
                        if (trans == null) { // gotta load it
                            Source so = null;
                            try {
                                so = uriResolverFromDisk.resolve(styleSheet, serverAddress);
                            } catch (TransformerException ex) {
                                Logger.getLogger(SRWServlet.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (so == null) {
                                servletLog.warn("uriResolver didn't find " + serverAddress + styleSheet);
                                URL stylesheeturl;
                                if(styleSheet!=null && styleSheet.startsWith("/"))
                                    stylesheeturl= new URL(serverAddress + styleSheet);
                                else
                                    stylesheeturl = new URL(serverAddress + "/" + styleSheet);
                                if (servletLog.isDebugEnabled()) {
                                    servletLog.debug("stylesheet url: " + stylesheeturl);
                                }
                                HttpURLConnection stylesheetconn;
                                try {
                                    stylesheetconn = (HttpURLConnection) stylesheeturl.openConnection();
                                    if (servletLog.isDebugEnabled()) {
                                        servletLog.debug("opened URL with response code " + stylesheetconn.getResponseCode());
                                    }
                                } catch (ConnectException e) {
                                    throw new ServletException("ConnectException for stylesheet url: " + serverAddress + styleSheet, e);
                                }
                                if (stylesheetconn.getResponseCode() != 200) {
                                    servletLog.warn("response code=" + stylesheetconn.getResponseCode() + " getting " + stylesheeturl);
                                    servletLog.warn("request was: " + req.getQueryString());
                                    if(!userProvidedStylesheet)
                                        throw new ServletException("Error retrieving stylesheet: " + serverAddress + styleSheet + ", response code=" + stylesheetconn.getResponseCode());
                                    resp.setContentType("text/html");
                                    resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
                                    try (PrintWriter writer = resp.getWriter()) {
                                        writer.println("<html><head><title>VIAF: error</title></head>");
                                        writer.println("<body><h1>Error: unable to find stylesheet</h1>");
                                        writer.println("<p>The stylesheet specified ("+styleSheet+
                                            ") does not exist</p>");
                                        writer.println("</body></html>");
                                    }
                                    return;
                                }
                                so = new StreamSource(stylesheetconn.getInputStream());
                            }
                            so.setSystemId(serverAddress + styleSheet);
                            try {
                                servletLog.debug("building HTML transformer");
                                templ=TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null).newTemplates(so);
                                db.templates.put(styleSheet, templ);
                                trans = templ.newTransformer();
                                if (trans == null) {
                                    throw new ServletException("newTransformer returned null for styleSheet=" + styleSheet + ", serverAddress=" + serverAddress);
                                }
                                servletLog.debug("built HTML transformer");
                                trans.setURIResolver(uriResolverFromDisk);
                                servletLog.debug("setURIResolver");
                                servletLog.debug("put HTML transformer");
                            } catch (TransformerConfigurationException ex) {
                                if(!userProvidedStylesheet)
                                    throw new ServletException("unable to build transformer " + serverAddress + styleSheet);
                                resp.setContentType("text/html");
                                resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
                                try (PrintWriter writer = resp.getWriter()) {
                                    writer.println("<html><head><title>VIAF: error</title></head>");
                                    writer.println("<body><h1>Error: unknown stylesheet</h1>");
                                    writer.println("<p>The stylesheet specified ("+styleSheet+
                                        ") does not exist</p>");
                                    writer.println("</body></html>");
                                }
                                return;
                            }
                        }
                        StringWriter toRec = new StringWriter();
                        StreamSource fromRec = new StreamSource(new StringReader(soapResponse));
                        try {
                            if (servletLog.isDebugEnabled()) {
                                servletLog.debug("making HTML from: " + soapResponse);
                            }
                            servletLog.debug("2 trans=" + trans);
                            trans.transform(fromRec, new StreamResult(toRec));
                            trans.reset();
                            servletLog.debug("made HTML");
                            soapResponse = toRec.toString();
                            if (soapResponse.substring(0, Math.min(100, soapResponse.length())).contains("<?xml-stylesheet")) {
                                resp.setContentType("text/xml");
                            } else {
                                resp.setContentType("text/html");
                            }
                        } catch (TransformerException e) {
                            servletLog.error(e, e);
                            resp.setContentType("text/xml");
                            if (!"HEAD".equals(req.getMethod())) {
                                sos = resp.getOutputStream();
                                styleSheet = db.searchStyleSheet;
                                if (numRecordsReturned == 0 && db.noRecordsStyleSheet != null) {
                                    styleSheet = db.noRecordsStyleSheet;
                                }
                                if (numRecordsReturned == 1 && db.singleRecordStyleSheet != null) {
                                    styleSheet = db.singleRecordStyleSheet;
                                }
                                if (numRecordsReturned > 1 && db.multipleRecordsStyleSheet != null) {
                                    styleSheet = db.multipleRecordsStyleSheet;
                                }
                                srwInfo.writeXmlHeader(sos, msgContext, req,
                                        styleSheet);
                            }
                        }
                    } else if(mediaInfo.mediaType.contains("json")) {
                        servletLog.info("recordPacking="+recordPacking);
                        if("json".equals(recordPacking)) {
                            soapResponse=Utilities.unXmlEncode(soapResponse);
                        } else {
                            if(servletLog.isDebugEnabled())
                                servletLog.debug("XML about to become JSON: "+soapResponse);
                            XMLSerializer xmlSerializer = new XMLSerializer();  
                            try {
                                JSON json = xmlSerializer.read(soapResponse);
                                soapResponse=json.toString(2);
                            }
                            catch(JSONException e) {
                                servletLog.error("Bad XML: "+soapResponse);
                                throw e;
                            }
                        }
                        if (jsonp != null) {
                            soapResponse = jsonp + "(" + soapResponse + ");";
                        }
                        if(servletLog.isDebugEnabled())
                            servletLog.debug("JSON: "+soapResponse);
                        resp.setContentType(mediaInfo.mediaType);
                        sos = resp.getOutputStream();
                        sos.write(soapResponse.getBytes(StandardCharsets.UTF_8));
                        sos.close();
                    } else { // returning native XML from the database
//                        String xmlHeaders=srwInfo.getXmlHeaders(req, db.searchStyleSheet).toString();
//                        if(xmlHeaders.substring(0, Math.min(100, xmlHeaders.length())).contains("xml-stylesheet"))
//                            resp.setContentType("text/xml");
//                        else if(soapResponse.substring(0, Math.min(100, soapResponse.length())).contains("xml-stylesheet"))
//                            resp.setContentType("text/xml");
//                        else
                        resp.setContentType("text/xml");
                        if (!"HEAD".equals(req.getMethod())) {
                            sos = resp.getOutputStream();
//                            if("none".equals(db.appStyleSheet))
//                                srwInfo.writeXmlHeader(sos, msgContext, req,
//                                    null);
//                            else 
                            if (db.appStyleSheet != null
                                    && !("none".equals(db.appStyleSheet))
                                    && ("APP".equals(req.getAttribute("service"))
                                    || "APP".equals(req.getParameter("service")))) {
                                srwInfo.writeXmlHeader(sos, msgContext, req,
                                        db.appStyleSheet);
                            } else {
                                srwInfo.writeXmlHeader(sos, msgContext, req, null);
                            }
                        }
                    }
                } else if(!redirectResponse) { //we don't care what they want, just return the SRU response
                    resp.setContentType("text/xml");
                    if (!"HEAD".equals(req.getMethod())) {
                        sos = resp.getOutputStream();
                        if ("none".equals(db.appStyleSheet)) {
                            srwInfo.writeXmlHeader(sos, msgContext, req,
                                    null);
                        } else if (db.appStyleSheet != null
                                && ("APP".equals(req.getAttribute("service"))
                                || "APP".equals(req.getParameter("service")))) {
                            srwInfo.writeXmlHeader(sos, msgContext, req,
                                    db.appStyleSheet);
                        } else {
                            srwInfo.writeXmlHeader(sos, msgContext, req,
                                    "text/xml".equals(mediaInfo.mimeType) ? null : db.searchStyleSheet);
                        }
                    }
                }

                if (db.httpHeaderSetter != null) {
                    int status = db.httpHeaderSetter.setGetResponseHeaders(db.searchRequest, db.response, soapResponse, req, resp);
                    if (status >= 300 && status < 400) {
                        if (servletLog.isDebugEnabled()) {
                            servletLog.debug("doing a redirect: " + soapResponse);
                        }
                        return;  // doing a redirect.  Nothing more to do
                    } else if (servletLog.isDebugEnabled()) {
                        servletLog.debug("httpHeaderSetter returned: " + status);
                    }
                } else if (servletLog.isDebugEnabled()) {
                    servletLog.debug("no httpHeaderSetter for db: " + db.dbname);
                }

                //writer.println(soapResponse);
                //writer.close();
                if (!"HEAD".equals(req.getMethod())) {
                    if (sos == null) {
                        sos = resp.getOutputStream();
                    }
                    try {
                        sos.write(soapResponse.getBytes(StandardCharsets.UTF_8));
                        sos.close();
                    } catch (IOException e) {
                        // no need to worry about this, let it slide
                    }
                }
            } else {
                resp.setContentType("text/html");
                resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
                if (!"HEAD".equals(req.getMethod())) {
                    StringBuilder sb2 = new StringBuilder("<p>");
                    sb2.append(Messages.getMessage("noResponse01"));
                    if (db.response != null) {
                        DiagnosticsType diagnostics = db.response.getDiagnostics();
                        if (diagnostics != null) {
                            DiagnosticType diagnostic = diagnostics.getDiagnostic(0);
                            sb2.append("<br/>SRU Diagnostic: ").append(SRWDiagnostic.toString(diagnostic));
                            servletLog.warn("SRU Diagnostic: "+SRWDiagnostic.toString(diagnostic));
                        }
                    }
                    sb2.append("</p>");
                    try (PrintWriter writer = resp.getWriter()) {
                        writer.println(sb2.toString());
                        if(!badRequest) { // we had a good request, but generated no response
                            servletLog.warn("search request generated no response!");
                            servletLog.warn("queryString: "+req.getQueryString());
                        }
                    }
                }
            }
//            servletLog.info("at exit: totalMemory="+rt.totalMemory()+", freeMemory="+rt.freeMemory());
            servletLog.debug("exit processMethodRequest");
            return;
        }

        if ((operation != null && operation.equals("scan")) || scanClause != null) { // scanRequest
            int i;
            String t;
            StringBuilder sb = new StringBuilder();
            servletLog.info(db.dbname + " scan: " + req.getQueryString());
            sb.append("<soap:Envelope ")
                    .append("xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" ")
                    .append("xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" ")
                    .append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">")
                    .append("<soap:Body>")
                    .append("<srw:scanRequest ")
                    .append("xmlns:srw=\"http://www.loc.gov/zing/srw/\">")
                    .append("<srw:scanClause>");
            if (scanClause != null) {
                sb.append(encode(scanClause));
            } else {
                sb.append("\"\"");
            }
            sb.append("</srw:scanClause>");

            t = req.getParameter("responsePosition");
            if (t != null) {
                try {
                    i = Integer.parseInt(t);
                    if (i < 0) {
                        i = Integer.MAX_VALUE;
                    }
                } catch (NumberFormatException e) {
                    i = Integer.MAX_VALUE;
                }

                sb.append("<srw:responsePosition>").append(i).append("</srw:responsePosition>");
            }

            t = req.getParameter("maximumTerms");
            if (t != null) {
                try {
                    i = Integer.parseInt(t);
                    if (i < 1) {
                        i = Integer.MAX_VALUE;
                    }
                } catch (NumberFormatException e) {
                    i = Integer.MAX_VALUE;
                }

                sb.append("<srw:maximumTerms>").append(i).append("</srw:maximumTerms>");
            }

            sb.append("</srw:scanRequest></soap:Body></soap:Envelope>");
            if (servletLog.isDebugEnabled()) {
                servletLog.debug(sb.toString());
            }
            msgContext.setProperty("sru", "");
            msgContext.setTargetService("SRW");
            AxisEngine engine = getEngine();
            if (servletLog.isDebugEnabled()) {
                servletLog.debug("request=" + sb.toString());
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
            Message msg = new Message(bais, false);
            msgContext.setRequestMessage(msg);
            try {
                engine.invoke(msgContext);
            } catch (AxisFault e) {
                servletLog.error(e, e);
            }
            Message respMsg = msgContext.getResponseMessage();
            if (respMsg != null) {
                resp.setContentType("text/xml");
                if (!"HEAD".equals(req.getMethod())) {
                    // code to strip SOAP stuff out.  Hope this can go away some day
                    try (PrintWriter writer = resp.getWriter()) {
                        // code to strip SOAP stuff out.  Hope this can go away some day
                        String soapResponse = respMsg.getSOAPPartAsString();
                        int start = soapResponse.indexOf("<scanResponse");
                        if (start >= 0) {
                            int stop = soapResponse.indexOf("</scanResponse>");
                            //                    soapResponse=cleanup(soapResponse.substring(start, stop+15)
                            //                        .toCharArray());
                            soapResponse = "<scanResponse xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.loc.gov/zing/srw/ http://www.loc.gov/standards/sru/sru1-1archive/xml-files/srw-types.xsd\"" + soapResponse.substring(start + 13, stop + 15);
                            srwInfo.writeXmlHeader(writer, msgContext, req,
                                    db.scanStyleSheet);
                        }
                        writer.println(soapResponse);
                    }
                }
            } else {
                resp.setContentType("text/html");
                if (!"HEAD".equals(req.getMethod())) {
                    try (PrintWriter writer = resp.getWriter()) {
                        writer.println("<p>" + Messages.getMessage("noResponse01") + "</p>");
                        servletLog.warn("scan request generated no response!");
                        servletLog.warn("queryString: "+req.getQueryString());
                    }
                }
            }
            if (servletLog.isDebugEnabled()) {
                servletLog.debug("elapsed time: " + (System.currentTimeMillis() - startTime) + "ms");
                servletLog.debug("exit processMethodRequest");
            }
            return;
        }
        servletLog.error("fell through processMethodRequest!");
        servletLog.error("operation=" + operation + ", query=" + query + ", scanClause=" + scanClause);
    }

    public static String encode(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        char c, chars[] = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            c = chars[i];
            if (c < ' ') // skip control characters.  They don't work in queries
            {
                continue; // and they mess up the XML response
            }
//            if(c=='%' && i+2<chars.length) { // hex code! They might be sneeking in a control character!
//                if(Character.isDigit(chars[i+1]) && Character.isDigit(chars[i+2])) {
//                    if(Integer.parseInt(new String(chars, i+1, 2))<' ') {
//                        i+=2;
//                        continue;
//                    }
//                }
//            }
            if (c == '<' || c == '&' || c == '>' || c == '"' || c == '\'' || c == '\\') {
                sb.append("&#").append((int)c).append(';');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String convertFormDataToTrivialXML(String formData) {
        int i;
        String name, parm;
        StringBuilder sb = new StringBuilder("<form>\n");
        StringTokenizer st = new StringTokenizer(formData, "&");
        while (st.hasMoreTokens()) {
            parm = st.nextToken();
            i = parm.indexOf('=');
            if (i > 0) {
                name = parm.substring(0, i);
                sb.append('<').append(name).append('>').append(parm.substring(i + 1)).append("</").append(name).append(">\n");
            }
        }
        sb.append("</form>\n");
        return sb.toString();
    }

    private static class MediaInfo {
        public String mediaType, mimeType;
        public boolean negotiatedContent=false;
        public MediaInfo() {
        }
    }
}
