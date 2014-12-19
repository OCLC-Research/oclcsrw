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
 * SRWTestDatabase.java
 *
 * Created on August 5, 2003, 4:17 PM
 */

package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.ScanRequestType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.TermType;
import gov.loc.www.zing.srw.TermTypeWhereInList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLParseException;
import org.z3950.zing.cql.CQLParser;
import org.z3950.zing.cql.CQLTermNode;

/**
 *
 * @author  levan
 */
public class SRWFileSystemDatabase extends SRWDatabase {
    static Log log=LogFactory.getLog(SRWFileSystemDatabase.class);

    DirFilter filter=null;
    File directory=null;
    private String author, contact, description, restrictions, title;
    private ArrayList<String> filenames=null;
    String schemaID, schemaLocation, schemaName;

    @Override
    public String add(byte[] record, RecordMetadata metadata) {
        try { // the filename comes in through the comment
            FileOutputStream fos = new FileOutputStream(new File(directory, metadata.getComment()));
            fos.write(record);
            fos.close();
        } catch (Exception ex) {
            log.error(ex, ex);
            return null;
        }
        filenames.add(metadata.getComment());
        Collections.sort(filenames);
        return metadata.getComment();
    }


    @Override
    public void addRenderer(String schemaName, String schemaID, Properties props)
      throws InstantiationException {
    }
    
    @Override
    public String getDatabaseInfo() {
        StringBuilder sb=new StringBuilder();
        sb.append("        <databaseInfo>\n");
        if(title!=null)
            sb.append("          <title>").append(title).append("</title>\n");
        if(description!=null)
            sb.append("          <description>").append(description).append("</description>\n");
        if(author!=null)
            sb.append("          <author>").append(author).append("</author>\n");
        if(contact!=null)
            sb.append("          <contact>").append(contact).append("</contact>\n");
        if(restrictions!=null)
            sb.append("          <restrictions>").append(restrictions).append("</restrictions>\n");
        sb.append("          </databaseInfo>\n");
        return sb.toString();
    }

    @Override
    public String getExtraResponseData(QueryResult result,
      SearchRetrieveRequestType request) {
        return null;
    }

    private void getFilenames() {
        File[] files;
        if(filter!=null)
            files=directory.listFiles(filter);
        else
            files=directory.listFiles();
        filenames=new ArrayList<String>();
        for(File f : files) {
            filenames.add(f.getName());
        }
        Collections.sort(filenames);
    }

    @Override
    public String getIndexInfo() {
        StringBuilder sb=new StringBuilder();
        sb.append("        <indexInfo>\n")
          .append("          <set identifier=\"info:srw/oai-context-set/1/oai-v1.0\"")
          .append(" name=\"oai\"/>\n")
          .append("          <index>\n")
          .append("            <title>identifier</title>\n")
          .append("            <map>\n")
          .append("              <name set=\"oai\">identifier</name>\n")
          .append("              </map>\n")
          .append("            </index>\n")
          .append("          <index>\n")
          .append("            <title>datestamp</title>\n")
          .append("            <map>\n")
          .append("              <name set=\"oai\">datestamp</name>\n")
          .append("              </map>\n")
          .append("            </index>\n")
          .append("          </indexInfo>\n");
        return sb.toString();
    }

    @Override
    public int getMaximumRecords() {
        return maximumRecords;
    }

    @Override
    public int getNumberOfRecords() {
        return defaultNumRecs;
    }

    @Override
    public QueryResult getQueryResult(String queryStr,
      SearchRetrieveRequestType request) {
        BasicQueryResult result=new BasicQueryResult();
        CQLNode query;
        try {
            query=parser.parse(queryStr);
        }
        catch(CQLParseException e) {
            result.addDiagnostic(SRWDiagnostic.QuerySyntaxError, queryStr);
            return result;
        } catch (IOException e) {
            result.addDiagnostic(SRWDiagnostic.QuerySyntaxError, queryStr);
            return result;
        }
        if(!(query instanceof CQLTermNode)) {
            result.addDiagnostic(SRWDiagnostic.UnsupportedBooleanOperator, null);
        }
        CQLTermNode term=(CQLTermNode) query;
        String index;
        index=term.getIndex();
        if(index.equals("oai.identifier") || index.equals("identifier")) {
            int filenameOffset=Collections.binarySearch(filenames, term.getTerm());
            if(filenameOffset>=0) {
                result.setNumberOfRecords(1);
                try {
                    byte[] b;
                    InputStream recStream=Utilities.openInputStream(filenames.get(filenameOffset), dbHome, null);
                    BufferedReader br=new BufferedReader(new InputStreamReader(recStream, "UTF-8"));
                    String line;
                    StringBuilder rec, recs[]=new StringBuilder[1];
                    rec = recs[0] = new StringBuilder();
                    while((line=br.readLine())!=null)
                        rec.append(line);
                    result.setRecords(recs);
                } catch (IOException ex) {
                    log.error(ex, ex);
                    result.addDiagnostic(SRWDiagnostic.GeneralSystemError, ex.getMessage());
                }
            }
        }
        else {
            result.addDiagnostic(SRWDiagnostic.UnsupportedIndex, index);
            return result;
        }
        result.setQuery(queryStr);

        return result;
    }

    @Override
    public String getSchemaID(String schemaName) {
        return schemaID;
    }

    @Override
    public String getSchemaInfo() {
        StringBuilder sb=new StringBuilder();
        sb.append("        <schemaInfo>\n")
          .append("          <schema identifier=\"").append(schemaID).append("\"\n")
          .append("              location=\"").append(schemaLocation).append("\"\n")
          .append("              sort=\"false\" retrieve=\"true\" name=\"").append(schemaName).append("\">\n")
          .append("            <title>RDF</title>\n")
          .append("            </schema>\n")
          .append("          </schemaInfo>\n");
        return sb.toString();
    }

    @Override
    public TermList getTermList(CQLTermNode term, int position, int maxTerms, ScanRequestType request) {
        String index=term.getIndex();
        TermList termList=new TermList();
        TermType tt=null;
        ArrayList<String> terms;
        ArrayList<TermType> tts=new ArrayList<TermType>();
        if(index.equals("oai.identifier") || index.equals("identifier"))
            terms=filenames;
        else {
            termList.addDiagnostic(SRWDiagnostic.UnsupportedIndex, index);
            return termList;
        }

        for(String t:terms) {
            if(tts.isEmpty())
                tts.add(tt=new TermType(t, new NonNegativeInteger("1"), t, TermTypeWhereInList.first, null));
            else if(tt!=null && t.equals(tt.getValue())) {
                tt.setNumberOfRecords(new NonNegativeInteger(Integer.toString(tt.getNumberOfRecords().intValue()+1)));
            }
            else
                tts.add(tt=new TermType(t, new NonNegativeInteger("1"), t, TermTypeWhereInList.inner, null));
        }
        if(tt!=null)
            if(tts.size()==1)
                tt.setWhereInList(TermTypeWhereInList.only);
            else
                tt.setWhereInList(TermTypeWhereInList.last);
        termList.setTerms(tts.toArray(new TermType[0]));
        return termList;
    }

    @Override
    public boolean hasaConfigurationFile() {
        return true;
    }

    @Override
    public void init(String dbname, String srwHome, String dbHome,
      String dbPropertiesFileName, Properties dbProperties, HttpServletRequest request) {
        log.debug("entering SRWFileSystemDatabase.init, dbname="+dbname);
        super.initDB(dbname,  srwHome, dbHome, dbPropertiesFileName, dbProperties);

        String filterstr=dbProperties.getProperty("SRWFileSystemDatabase.filenameFilter");
        if(filterstr!=null)
            filter=new DirFilter(filterstr);
        author=dbProperties.getProperty("SRWFileSystemDatabase.author");
        contact=dbProperties.getProperty("SRWFileSystemDatabase.contact");
        description=dbProperties.getProperty("SRWFileSystemDatabase.description");
        restrictions=dbProperties.getProperty("SRWFileSystemDatabase.restrictions");
        title=dbProperties.getProperty("SRWFileSystemDatabase.title");
        schemaName=dbProperties.getProperty("SRWFileSystemDatabase.schemaName");
        schemaID=dbProperties.getProperty("SRWFileSystemDatabase.schemaID");
        schemaLocation=dbProperties.getProperty("SRWFileSystemDatabase.schemaLocation");
        directory=new File(dbHome);
        parser=new CQLParser(CQLParser.V1POINT1);
        getFilenames();
        log.debug("leaving SRWFileSystemDatabase.init");
    }

    @Override
    public boolean supportsSort() {
        return false;
    }
}

class DirFilter implements FilenameFilter {
  private final Pattern pattern;

  public DirFilter(String regex) {
    pattern = Pattern.compile(regex);
  }

    @Override
  public boolean accept(File dir, String name) {
    // Strip path information, search for regex:
    return pattern.matcher(new File(name).getName()).matches();
  }
} ///:~
