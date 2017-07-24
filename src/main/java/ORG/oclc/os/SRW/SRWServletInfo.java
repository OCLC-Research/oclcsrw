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
 * SRWServletInfo.java
 *
 * Created on December 5, 2002, 2:19 PM
 */

package ORG.oclc.os.SRW;

import de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerConfigurationException;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author  levan
 */
public class SRWServletInfo {
    public static Log log=LogFactory.getLog(SRWServletInfo.class);
    public static HashMap<String, String> realXsls=new HashMap<>();
    public HashMap<String, Normalizer.Form> normalForm=new HashMap<>();
    public HashMap<String, String> mediaTypes=new HashMap<>();
    public HashMap<String, Object> templatesOrTransformers=new HashMap<>();
    public static String srwHome, tomcatHome, webappHome;
    protected HashMap<String, String>
                          nameSpaces=new HashMap<>(),
                          schemas=new HashMap<>();

    static private boolean madeIndexDotHtmlAlready=false;

    private boolean      makeIndexDotHtml=false;
    private final HashMap<String, String> extensions=new HashMap<>(), namespaces=new HashMap<>();
    public  HashMap<String, String> dbnames=new HashMap<>();
    public int           pathInfoIndex=100;
    private final Properties   serverProperties=new Properties();
    public String        addressInHeader=null, defaultDatabase,
                         indexDotHtmlLocation=null, propsfileName;
    public ArrayList<DbEntry> dbList=new ArrayList<>();
    public ContentTypeNegotiator conneg;
    private final String defaultDatabaseNotSpecified="default.database not specified in properties file";

    public SRWServletInfo() {
    }

    private void addMoreProperties(Properties properties, String propsfileName, String srwHome) {
        InputStream is;
        int i=propsfileName.lastIndexOf('.');
        String root=propsfileName, suffix=null;
        if(i>0) {
            suffix=propsfileName.substring(i);
            root=propsfileName.substring(0, i);
        }
        try {
            for(i=2;;i++) {
                propsfileName=root+"-"+Integer.toString(i)+suffix;
                is=Utilities.openInputStream(propsfileName, srwHome, null);
                properties.load(is);
                is.close();
            }
        }
        catch(java.io.IOException e) {
            if(i==2)
                log.info("Unable to load extra properties files after: "+propsfileName);
            else
                log.info("Loaded "+(i-2)+" extra properties files after: "+propsfileName);
        }
    }
    
    private static void buildDbList(Properties properties, ArrayList<DbEntry> dbList, HashMap<String, String> dbnames, String path, boolean justDbNames) {
        buildDbList(properties, dbList, dbnames, path, null, justDbNames);
    }

    private static void buildDbList(Properties properties, ArrayList<DbEntry> dbList, HashMap<String, String> dbnames, String path, String remote, boolean justDbNames) {
        Enumeration<?> enumer=properties.propertyNames();
        String      fileName, dbHome, dbName, description, hidden=null, t;
        while(enumer.hasMoreElements()) {
            t=(String)enumer.nextElement();
            if(t.startsWith("db.") && t.endsWith(".class")) {
                dbName=t.substring(3, t.length()-6);
                if(remote==null)
                    hidden=properties.getProperty("db."+dbName+".hidden");
                description=properties.getProperty("db."+dbName+".description");
                if(description==null  && remote==null && !justDbNames) { // see if it is in the database props
                    dbHome=properties.getProperty("db."+dbName+".home");
                    fileName=properties.getProperty(
                        "db."+dbName+".configuration");
                    if(fileName!=null)
                        try {
                            Properties dbProperties;
                            try (InputStream is = Utilities.openInputStream(
                                    fileName, dbHome, srwHome)) {
                                dbProperties = new Properties();
                                dbProperties.load(is);
                            }
                            description=dbProperties.getProperty("databaseInfo.description");
                        }
                        catch(FileNotFoundException e) {
                            if(remote==null)
                                log.error(e);
                        }
                        catch(IOException e) {
                            if(remote==null)
                                log.error(e, e);
                        }
                }
                if(remote!=null || hidden==null || !hidden.equalsIgnoreCase("true")) {
                    if(!justDbNames)
                        dbList.add(new DbEntry(dbName, remote, path, description));
                    dbnames.put(dbName, dbName);
                }
            }
            else if(remote==null && t.startsWith("remote.") && t.endsWith(".configuration")) {
                remote=t.substring(7, t.length()-14);
                try {
                    String remotePath=properties.getProperty("remote."+remote+".path");
                    Properties srwProperties;
                    try (InputStream is = new URL(properties.getProperty(t)).openStream()) {
                        srwProperties = new Properties();
                        srwProperties.load(is);
                    }
                    buildDbList(srwProperties, dbList, dbnames, remotePath, remote, justDbNames);
                }
                catch(IOException e) {
                    log.error(e);
                }
                remote=null;
            }
        }
    }

    private void createDBs(final String list) throws InstantiationException {
        if(list==null || list.equals(""))
            return;
        String          dbname;
        StringTokenizer st=new StringTokenizer(list, ", ");
        while(st.hasMoreTokens()) {
            dbname=st.nextToken();
            SRWDatabase.createDB(dbname, serverProperties);
        }
    }

    public static String findRealXsl(String languages, Enumeration<Locale> locales, String xsl) {
        log.debug("looking for stylesheet: "+xsl+" in languages: "+languages);
        File   f;
        Locale l;
        String base=xsl, realXsl=xsl, suffix=null;
        
        // strip off suffix
        int off=xsl.lastIndexOf('.');
        if(off>0) {
            base=xsl.substring(0, off);
            suffix=xsl.substring(off);
        }
        while(locales.hasMoreElements()) {
            l=locales.nextElement();
            log.debug("looking for "+xsl+" in "+webappHome+"/"+base+"_"+l.getLanguage()+"_"+l.getCountry()+suffix);
            f=new File(webappHome+"/"+base+"_"+l.getLanguage()+"_"+l.getCountry()+suffix);
            if(f.exists()) {
                log.debug("found "+f.getAbsolutePath());
                realXsl=base+"_"+l.getLanguage()+"_"+l.getCountry()+suffix;
                break;
            }
            log.debug("didn't find "+f.getAbsolutePath());
            log.debug("looking in "+tomcatHome+"/"+base+"_"+l.getLanguage()+"_"+l.getCountry()+suffix);
            f=new File(tomcatHome+"/"+base+"_"+l.getLanguage()+"_"+l.getCountry()+suffix);
            if(f.exists()) {
                log.debug("found "+f.getAbsolutePath());
                realXsl=base+"_"+l.getLanguage()+"_"+l.getCountry()+suffix;
                break;
            }
            log.debug("didn't find "+f.getAbsolutePath());
            log.debug("looking in "+webappHome+"/"+base+"_"+l.getLanguage()+suffix);
            f=new File(webappHome+"/"+base+"_"+l.getLanguage()+suffix);
            if(f.exists()) {
                log.debug("found "+f.getAbsolutePath());
                realXsl=base+"_"+l.getLanguage()+suffix;
                break;
            }
            log.debug("looking in "+tomcatHome+"/"+base+"_"+l.getLanguage()+suffix);
            f=new File(tomcatHome+"/"+base+"_"+l.getLanguage()+suffix);
            if(f.exists()) {
                log.debug("found "+f.getAbsolutePath());
                realXsl=base+"_"+l.getLanguage()+suffix;
                break;
            }
            log.debug("didn't find "+f.getAbsolutePath());
        }
        realXsls.put(languages+"/"+xsl, realXsl);
        return realXsl;
    }

    public String getDBName(final HttpServletRequest request) {
        String dbname=defaultDatabase, path=request.getPathInfo();
        StringTokenizer st;
        log.info("pathInfo="+path);
        if(path!=null && path.indexOf('/')>=0) {
            st=new StringTokenizer(path, "/");
            for(int i=0; i<pathInfoIndex && st.hasMoreTokens(); i++) {
                dbname=st.nextToken();
                if(dbnames.get(dbname)!=null)
                    break;
            }
        }
        if(dbnames.get(dbname)==null)
            dbname=defaultDatabase;
        log.info("dbname="+dbname);
        return dbname;
    }

    public String getExtension(String sruParm) {
        return extensions.get(sruParm);
    }

    public String getNamespace(String sruParm) {
        return namespaces.get(sruParm);
    }

    public Properties getProperties() {
        return serverProperties;
    }

    @SuppressWarnings("unchecked")
    public StringBuffer getXmlHeaders(final HttpServletRequest req, final String defaultXsl) {
        StringBuffer sb=new StringBuffer("<?xml version=\"1.0\"  encoding=\"UTF-8\"?> \n");
        String xsl=req.getParameter("xsl"); // version 1.0
        if(xsl==null)
            xsl=req.getParameter("stylesheet"); // version 1.1
        if(xsl==null)
            xsl=defaultXsl;
        if(xsl!=null) {
            String languages=req.getHeader("Accept-Language");
            String realXsl=realXsls.get(languages+'/'+xsl);
            if(realXsl==null)
                xsl=findRealXsl(languages, req.getLocales(), xsl);
            else
                xsl=realXsl;
            sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"")
              .append(xsl).append("\"?>\n");
        }
        return sb;
    }

    public boolean handleExplain(final HttpServletRequest request,
      final HttpServletResponse response, final MessageContext msgContext)
      throws org.apache.axis.AxisFault, IOException {
        String dbname=getDBName(request),
               recordPacking=request.getParameter("recordPacking"),
               stylesheet=request.getParameter("stylesheet");
        log.info("Got an explain request for database "+dbname);
        SRWDatabase db=SRWDatabase.getDB(dbname, serverProperties, request.getContextPath(), request);
        if(db==null) {
            if(dbname.equals(defaultDatabaseNotSpecified)) {
                log.error("Non-existant database "+dbname);
                log.error("requesting url was: "+request.getRequestURL().toString());
                log.error(new Exception("called from"));
                response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
                response.setContentType("text/html");
                response.setHeader("srwFailReason", "nonExistantDatabase");
                if (!"HEAD".equals(request.getMethod())) {
                    try (ServletOutputStream sos = response.getOutputStream()) {
                        sos.println("<html><head><title>Database not found: \""+dbname+"\"</title></head><body><h2>404: Database not found: \""+dbname+"\"</h2><hr/>OCLC SRW/SRU Server</body></html>");
                    }
                }
            } else {
                log.error("Unavailable database "+dbname);
                log.error("requesting url was: "+request.getRequestURL().toString());
                log.error(new Exception("called from"));
                response.setStatus(HttpURLConnection.HTTP_BAD_GATEWAY);
                response.setContentType("text/html");
                response.setHeader("srwFailReason", "unavailableDatabase");
                if (!"HEAD".equals(request.getMethod())) {
                    try (ServletOutputStream sos = response.getOutputStream()) {
                        sos.println("<html><head><title>Database unavailable: \""+dbname+"\"</title></head><body><h2>502: Database unavailable: \""+dbname+"\"</h2><hr/>OCLC SRW/SRU Server</body></html>");
                    }
                }
            }
            return true;
        }
        if(recordPacking==null) {
                recordPacking="xml"; // default for sru
        }
        if(stylesheet==null)
            stylesheet=db.explainStyleSheet;
        try(PrintWriter writer = response.getWriter()) {
            response.setContentType("text/xml");
            try{
                writeXmlHeader(writer, msgContext, request, stylesheet);
                writer.write("<SRW:explainResponse "+
                        "xmlns:SRW=\"http://www.loc.gov/zing/srw/\">\n");
                writer.write("  <SRW:version>1.1</SRW:version>\n");
                writer.write("  <SRW:record>\n");
                writer.write("    <SRW:recordSchema>http://explain.z3950.org/dtd/2.0/</SRW:recordSchema>\n");
                writer.write("    <SRW:recordPacking>"+recordPacking+"</SRW:recordPacking>\n");
                writer.write("    <SRW:recordData>\n");
                String explainRecord=db.getExplainRecord(request);
                if(explainRecord==null) {
                    explainRecord=db.getExplainRecord(request);
                }
                if(recordPacking.equals("string"))
                    Utilities.writeEncoded(writer, explainRecord);
                else
                    writer.write(explainRecord);
                writer.write("      </SRW:recordData>\n");
                writer.write("    </SRW:record>\n");
                writer.write("  </SRW:explainResponse>\n");
            }
            catch(IOException e) {
                log.error(e, e);
            }
        }
        log.info("Finished the explain response for database "+dbname);
        SRWDatabase.putDb(dbname, db);
        return true;
    }

    public void init(final ServletConfig config) {
        try {
            propsfileName=config.getInitParameter("PropertiesFile");
            if(propsfileName==null) {
                log.info("PropertiesFile not specified as an <init-param> in the web.xml");
                log.info("Using the default value of SRWServer.props instead");
                propsfileName="SRWServer.props";
            }
            log.info("Reading properties file: "+propsfileName);
            srwHome=config.getServletContext().getRealPath("/");
            if(srwHome.endsWith("/"))
                srwHome=srwHome.substring(0, srwHome.length()-1);
            if(srwHome.endsWith("\\"))
                srwHome=srwHome.substring(0, srwHome.length()-1);
            log.debug("srwHome="+srwHome);
            int off=srwHome.lastIndexOf('/');
            if(off<0)
                off=srwHome.lastIndexOf('\\');
            if(off>0) {
                webappHome=srwHome.substring(0, off);
                log.debug("webappHome="+webappHome);
                off=webappHome.lastIndexOf('/');
                if(off<0)
                    off=webappHome.lastIndexOf('\\');
                if(off>0)
                    tomcatHome=webappHome.substring(0, off);
                log.debug("tomcatHome="+tomcatHome);
            }
            InputStream is;
            try {
                is=Utilities.openInputStream(propsfileName, srwHome, null);
                serverProperties.load(is);
                is.close();
                addMoreProperties(serverProperties, propsfileName, srwHome);
            }
            catch(java.io.FileNotFoundException e) {
                log.info("Unable to load properties file: "+propsfileName);
                log.info("Will using web.xml for configuration parameters");
                Enumeration<?> enumer = config.getInitParameterNames();
                String propName;
                while(enumer.hasMoreElements()) {
                    propName=(String)enumer.nextElement();
                    serverProperties.setProperty(propName, config.getInitParameter(propName));
                }
            }
            buildDbList(serverProperties, dbList, dbnames, config.getServletContext().getRealPath("/"), true);
            srwHome=serverProperties.getProperty("SRW.Home");
            if(srwHome==null) {
                srwHome=config.getServletContext().getRealPath("/");
            }
            log.info("SRW.Home="+srwHome);
            pathInfoIndex=Integer.parseInt(serverProperties.getProperty("pathInfoIndex",
              Integer.toString(pathInfoIndex)));
            log.info("pathInfoIndex="+pathInfoIndex);
            defaultDatabase=serverProperties.getProperty("default.database");
            if(defaultDatabase==null)
                defaultDatabase=
                    defaultDatabaseNotSpecified;
            log.info("default.database="+defaultDatabase);
            indexDotHtmlLocation=serverProperties.getProperty("index.html");
            if(indexDotHtmlLocation==null)
                indexDotHtmlLocation=srwHome+"index.html";
            String s=serverProperties.getProperty("makeIndex.html");
            if(s!=null)
                if(s.equalsIgnoreCase("true"))
                    makeIndexDotHtml=true;
            addressInHeader=serverProperties.getProperty("addressInHeader");
            log.info("addressInHeader=\""+addressInHeader+'"');

            // any dbs to open automatically?
            s=serverProperties.getProperty("SRW.OpenAllDatabasesOnStartup");
            if(s==null) { // maybe a short list to open?
                s=serverProperties.getProperty("SRW.OpenDatabasesInListOnStartup");
                if(s!=null) {
                    log.info("Opening databases: "+s);
                    createDBs(s);
                }
                else
                    log.info("Not opening databases yet");
            }
            else
                if(s.equalsIgnoreCase("true") || s.equalsIgnoreCase("t") ||
                  s.equals("1")) {
                    Enumeration<?> enumer=serverProperties.propertyNames();
                    int         offset;
                    String      dbname, list=",", t;
                    while(enumer.hasMoreElements()) {
                        t=(String)enumer.nextElement();
                        if(t.startsWith("db.")) {
                            offset=t.indexOf(".", 3);
                            dbname=t.substring(3, offset);
                            if(!list.contains(", "+dbname+",")) { // not yet in list
                                list=list+" "+dbname+",";
                            }
                        }
                    }
                    log.info("Opening all databases :"+list);
                    createDBs(list);
                }
            // load the sru-srw extension mapping table
            Enumeration<?> enumer = serverProperties.propertyNames();
            String      extension, namespace, sruParm, t;
            while(enumer.hasMoreElements()) {
                t=(String)enumer.nextElement();
                if(t.startsWith("extension.") && !t.endsWith(".namespace")) {
                    sruParm=t.substring(10);
                    extension=serverProperties.getProperty(t);
                    extensions.put(sruParm, extension);
                    log.debug("added extension="+extension+", with sru parm="+sruParm);
                    namespace=serverProperties.getProperty("extension."+extension+".namespace");
                    namespaces.put(sruParm, namespace);
                    log.debug("added namespace="+namespace+", with sru parm="+sruParm);
                }
            }

            // content negotiation for SRU responses?
            conneg=new ContentTypeNegotiator();
            log.debug("conneg="+conneg);
            boolean foundHtml=false;
            Enumeration<Object> propertyNames = serverProperties.keys();
            int offset;
            String key, mediaType, mimeType, name, form, value;
            StringTokenizer st;
            ContentTypeNegotiator.VariantSpec vs;
            while(propertyNames.hasMoreElements()) {
                key=(String) propertyNames.nextElement();
                offset=key.indexOf("mimeTypes");
                if(offset>0) {
                    name=key.substring(0, offset-1);
                    value=serverProperties.getProperty(key);
                    log.info(key+"="+value);
                    st=new StringTokenizer(value, ", ");
                    mimeType=st.nextToken();
                    vs=conneg.addVariant(mimeType);
                    mediaType=vs.getMediaType().getMediaType();
                    try {
                        Object trans = Utilities.addTransformer(mediaType,
                                serverProperties.getProperty(name+".styleSheet"),
                                srwHome, null, null, serverProperties, propsfileName, name);
                        if(trans!=null) {
                            templatesOrTransformers.put(mediaType, trans);
                        }
                        while(st.hasMoreTokens())
                            vs.addAliasMediaType(st.nextToken());
                    } catch (FileNotFoundException | TransformerConfigurationException | UnsupportedEncodingException e) {
                        log.error("Unable to add stylesheet "+serverProperties.getProperty(name+".styleSheet")+", stylesheet ignored");
                        log.error(e, e);
                    }
                    if(mediaType.contains("html")) {
                        foundHtml=true;
                        while(st.hasMoreTokens())
                            vs.addAliasMediaType(st.nextToken());
                    }
                    // do they want a normalizer applied after the transformation?
                    form=serverProperties.getProperty(name+".normalForm");
                    if(form!=null) try {
                        normalForm.put(mediaType, Normalizer.Form.valueOf(form));
                    }
                    catch(IllegalArgumentException e) {
                        log.error("illegal unicode normal form: "+form+", normalizer ignored");
                        log.error(e, e);
                    }
                }
            }
            if(!foundHtml) { // let's add minimal support for html, even if they forgot
                st=new StringTokenizer("text/html;q=0.9, application/xhtml+xml", ", ");
                mimeType=st.nextToken();
                vs=conneg.addVariant(mimeType);
                while(st.hasMoreTokens())
                    vs.addAliasMediaType(st.nextToken());
            }
            log.info("SRWServletInfo initialization complete");
        }
        catch(IOException | NumberFormatException | InstantiationException e) {
            log.error(e, e);
        }
    }
    
    
    public void makeIndexDotHtml(Properties properties, HttpServletRequest request) {
        if(madeIndexDotHtmlAlready)
            return;
        madeIndexDotHtmlAlready=true;
        String      path=request.getContextPath()+request.getServletPath();
        try {
            try (PrintStream ps = new PrintStream(new FileOutputStream(indexDotHtmlLocation))) {
                log.debug("writing index.html to: "+indexDotHtmlLocation);
                ps.println("<!doctype html public \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
                ps.println("<html>");
                ps.println("<head>");
                ps.println("<title>SRW/U Databases</title>");
                ps.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />");
                ps.println("<link href=\"http://www.oclc.org/common/css/basic_oclc.css\" rel=\"stylesheet\" type=\"text/css\" />");
                ps.println("<link href=\"http://www.oclc.org/common/css/researchproject_oclc.css\" rel=\"stylesheet\" type=\"text/css\" />");
                ps.println("<style type=\"text/css\">");
                ps.println("<!--");
                ps.println("table.layout { border: none; margin: 0; padding: 0; width: 100%; }");
                ps.println("table.layout td { border: none; margin: 0; padding: 0; width: 50%; }");
                ps.println("table.formtable th, table.formtable td { border-top: 1px solid #999; border-left: 1px solid #999; color: #333; padding: 4px; text-align: left; vertical-align: top; }");
                ps.println("input.button { margin: 0; }");
                ps.println("-->");
                ps.println("</style>");
                ps.println("</head>");
                ps.println("<body>");
                ps.println("<div align=\"center\">");
                ps.println("<table cellspacing=\"0\" id=\"bnrResearch\">");
                ps.println("<tr>");
                ps.println("<td id=\"tdResearch\"><a href=\"http://www.oclc.org/research/\">A Project of OCLC Research</a></td>");
                ps.println("<td id=\"tdOclc\"><a href=\"http://www.oclc.org/\">OCLC Online Computer Library Center</a></td>");
                ps.println("</tr>");
                ps.println("<tr>");
                ps.println("<td id=\"tdProject\"><h2><a href=\"index.html\">SRW/U Databases</a></h2></td>");
                ps.println("<td id=\"tdLogo\"><a href=\"http://www.oclc.org/research/software/srw\"><img height=\"15\" width=\"80\" alt=\"Powered by OCLC SRW/U\" src=\"http://www.oclc.org/research/images/badges/oclc_srwu.gif\"/></a></td>");
                ps.println("</tr>");
                ps.println("</table>");
                ps.println("</div>");
                ps.println("<table class=\"formtable\">");
                if(dbList.isEmpty())
                    buildDbList(properties, dbList, dbnames, path, false);
                Object[] dbArray=this.dbList.toArray();
                Arrays.sort(dbArray);
                DbEntry de;
                for (Object dbArray1 : dbArray) {
                    de = (DbEntry) dbArray1;
                    ps.print("<tr><th><a href=\""+de.getPath()+"/"+de.getName()+"\">");
                    if(de.getHost().length()>0)
                        ps.print(de.getHost()+": ");
                    ps.print(de.getName()+"</a></th>");
                    if(de.getDescription()!=null) {
                        ps.println("<td>"+de.getDescription()+"</td>");
                    }
                }
                ps.println("</table>");
                ps.println("<a href=\"http://www.oclc.org/research/software/srw\">");
                ps.println("<img height=\"15\" width=\"80\" alt=\"Powered by OCLC SRW/U\" src=\"http://oaweb4server:8001/DesignDept/sandbox/osborne/badges/badge_srwu.gif\"/></p>");
                ps.println("</a>");
                ps.println("</body>");
                ps.println("</html>");
            }
        }
        catch(FileNotFoundException e){
            log.error(e,e);
        }
    }
    
    public boolean setSRWStuff(final HttpServletRequest request,
      final HttpServletResponse response, final MessageContext msgContext) 
      throws org.apache.axis.AxisFault, IOException {
        log.debug("entering SRWServletInfo.setSRWStuff");
        String databaseURL=request.getRequestURL().toString();
        String contextPath=request.getContextPath(), dbname=getDBName(request);

        if(contextPath.length()>1)
            contextPath=contextPath.substring(1);
        if(log.isDebugEnabled())
            log.debug("contextPath="+contextPath);

        SRWDatabase db=SRWDatabase.getDB(dbname, serverProperties, contextPath, request);
        if(db==null) {
            if(dbname.equals(defaultDatabaseNotSpecified)) {
                log.error("Non-existant database "+dbname);
                log.error("requesting url was: "+request.getRequestURL().toString());
                log.error(new Exception("called from"));
                response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
                response.setContentType("text/html");
                response.setHeader("srwFailReason", "nonExistantDatabase");
                if (!"HEAD".equals(request.getMethod())) {
                    try (ServletOutputStream sos = response.getOutputStream()) {
                        sos.println("<html><head><title>Database not found: \""+dbname+"\"</title></head><body><h2>404: Database not found: \""+dbname+"\"</h2><hr/>OCLC SRW/SRU Server</body></html>");
                    }
                }
            } else {
                log.error("Unavailable database "+dbname);
                log.error("requesting url was: "+request.getRequestURL().toString());
                log.error(new Exception("called from"));
                response.setStatus(HttpURLConnection.HTTP_BAD_GATEWAY);
                response.setContentType("text/html");
                response.setHeader("srwFailReason", "unavailableDatabase");
                if (!"HEAD".equals(request.getMethod())) {
                    try (ServletOutputStream sos = response.getOutputStream()) {
                        sos.println("<html><head><title>Database unavailable: \""+dbname+"\"</title></head><body><h2>502: Database unavailable: \""+dbname+"\"</h2><hr/>OCLC SRW/SRU Server</body></html>");
                    }
                }
            }
            return false;
        }
        db.getExplainRecord(request);
        if(makeIndexDotHtml && !madeIndexDotHtmlAlready)
            makeIndexDotHtml(serverProperties, request);
        
        msgContext.setProperty("dbname", dbname);
        msgContext.setProperty("db", db);
        msgContext.setProperty("databaseURL", databaseURL);
        Message m=msgContext.getCurrentMessage();
        String msg=null;
        if(m!=null) {
            msg=m.getSOAPPartAsString();
            int i=msg.indexOf("Body");
            msg=msg.substring(i+5, i+35);
        }
//        log.info("msg part='"+msg+"'");
        if(msg!=null && msg.contains("explainRequest")) {
//            log.info("set service to ExplainSoap");
            msgContext.setTargetService("ExplainSOAP");
        }
        else {
//            log.info("set service to SRW");
            msgContext.setTargetService("SRW");
        }
        log.debug("exiting SRWServletInfo.setSRWStuff");
        return true;
    }
    
    public void writeXmlHeader(final PrintWriter printWriter,
      final MessageContext msgContext, final HttpServletRequest req,
      final String defaultXsl) {
        printWriter.println(getXmlHeaders(req, defaultXsl));
    }        


    public void writeXmlHeader(final javax.servlet.ServletOutputStream sos,
      final MessageContext msgContext, final HttpServletRequest req,
      final String defaultXsl) {
        try {
            sos.write(getXmlHeaders(req, defaultXsl).toString().getBytes(StandardCharsets.UTF_8));
        }
        catch(IOException e){}
    }
}
