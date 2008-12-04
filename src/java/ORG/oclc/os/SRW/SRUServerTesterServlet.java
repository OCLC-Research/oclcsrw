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
 * SRUServerTesterServlet.java
 *
 * Created on September 15, 2005, 11:28 AM
 */

package ORG.oclc.os.SRW;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  levan
 * @version 1.0
 */
public class SRUServerTesterServlet extends HttpServlet {
    static Log log=LogFactory.getLog(SRUServerTesterServlet.class);
    
    /** Initializes the servlet.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
    }
    
    /** Destroys the servlet.
     */
    public void destroy() {
        
    }
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String url=request.getParameter("url");
        log.info("url="+url);
        SRUServerTester tester=new SRUServerTester(url);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!doctype html public \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>SRU Server Testing Page</title>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"/>");
        out.println("<link href=\"http://www.oclc.org/common/css/basic_oclc.css\" rel=\"stylesheet\" type=\"text/css\"/>");
        out.println("<link href=\"http://www.oclc.org/common/css/researchproject_oclc.css\" rel=\"stylesheet\" type=\"text/css\"/>");
        out.println("<style type=\"text/css\">");
        out.println("<!--");
        out.println("table.layout { border: none; margin: 0; padding: 0; width: 100%; }");
        out.println("table.layout td { border: none; margin: 0; padding: 0; width: 50%; }");
        out.println("table.formtable th, table.formtable td { border-top: 1px solid #999; border-left: 1px solid #999; color: #333; padding: 4px; text-align: left; vertical-align: top; }");
        out.println("input.button { margin: 0; }");
        out.println(".red { color:red; }");
        out.println("-->");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div align=\"center\">");
        out.println("<table cellspacing=\"0\" id=\"bnrResearch\">");
        out.println("<tr>");
        out.println("<td id=\"tdResearch\"><a href=\"http://www.oclc.org/research/\">A Project of OCLC Research</a></td>");
        out.println("<td id=\"tdOclc\"><a href=\"http://www.oclc.org/\">OCLC Online Computer Library Center</a></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td id=\"tdProject\"><h2><a href=\"index.html\">SRU Server Tester</a></h2></td>");
        out.println("<td id=\"tdLogo\"><a href=\"http://www.oclc.org/\"><img src=\"http://www.oclc.org/common/images/logos/oclclogo_gray.gif\" alt=\"OCLC\" width=\"60\" height=\"31\"/></a></td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("</div>");
        out.println("<pre>");
        out.println(tester.test());
        out.println("</pre>");
        out.println("</body>");
        out.println("</html>");
        out.close();
    }
    
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    
}
