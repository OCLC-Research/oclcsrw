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
 * SRWGuiClient.java
 *
 * Created on November 19, 2002, 1:53 PM
 */

package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.interfaces.ExplainPort;
import gov.loc.www.zing.srw.interfaces.SRWPort;
import gov.loc.www.zing.srw.ExplainRequestType;
import gov.loc.www.zing.srw.ExplainResponseType;
import gov.loc.www.zing.srw.RecordType;
import gov.loc.www.zing.srw.RecordsType;
import gov.loc.www.zing.srw.ScanRequestType;
import gov.loc.www.zing.srw.ScanResponseType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.service.SRWSampleServiceLocator;
import gov.loc.www.zing.srw.StringOrXmlFragment;
import gov.loc.www.zing.srw.TermType;
import gov.loc.www.zing.srw.TermsType;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.event.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.axis.types.PositiveInteger;

/**
 *
 * @author  levan
 */
public class SRWGuiClient extends JFrame implements HyperlinkListener, 
                                               ActionListener {

    public static void main(String[] args) {
        try {
            SRWSampleServiceLocator service=new SRWSampleServiceLocator();
            String urlString=
                             "http://alcme.oclc.org/srw/search/SOAR";
                             //"http://localhost:8082/srw/search/SOAR";
                             //"http://localhost:8082/SRW/search/SOAR";
                             //"http://localhost:8080/SRW/search/SOAR";
                             //"http://srw.cheshire3.org:8080/l5r";
                             //"http://localhost:8082/l5r";
                             //"http://www.rdn.ac.uk:8080/xxdefault";
                             //"http://localhost:8082/xxdefault";
            if(args.length==1)
                urlString=args[0];
            new SRWGuiClient(urlString);

            URL url=new URL(urlString);
            ExplainPort explain=service.getExplainSOAP(url);
            ExplainRequestType explainRequest=new ExplainRequestType();
            explainRequest.setRecordPacking("xml");
            explainRequest.setVersion("1.1");
            ExplainResponseType explainResponse=explain.explainOperation(explainRequest);
            System.out.println("explainResponse="+explainResponse);
            
            SRWPort port=service.getSRW(url);
            ScanRequestType scanRequest=new ScanRequestType();
            scanRequest.setVersion("1.1");
            scanRequest.setScanClause("education");
            ScanResponseType scanResponse=port.scanOperation(scanRequest);
            if(scanResponse!=null) {
                TermsType terms=scanResponse.getTerms();
                if(terms!=null) {
                    TermType[] term=terms.getTerm();
                    System.out.println(term.length+" terms returned");
                    for(int i=0; i<term.length; i++)
                        System.out.println(term[i].getValue()+"("+term[i].getNumberOfRecords().intValue()+")");
                }
                else
                    System.out.println("0 terms returned");
            }
            else
                System.out.println("no scan response returned");

            SearchRetrieveRequestType request=new SearchRetrieveRequestType();
            request.setVersion("1.1");
            request.setQuery("en and education");
            //request.setQuery("dc.title any sword");
            request.setRecordSchema("info:srw/schema/1/dc-v1.1");
            request.setStartRecord(new PositiveInteger("1"));
            request.setMaximumRecords(new NonNegativeInteger("1"));
            request.setRecordPacking("xml");
            SearchRetrieveResponseType response=
                port.searchRetrieveOperation(request);
            System.out.println("postings="+response.getNumberOfRecords());
            RecordType[] record;
            RecordsType records=response.getRecords();
            if(records==null || (record=records.getRecord())==null)
                System.out.println("0 records returned");
            else {
                System.out.println(record.length+" records returned");
                System.out.println("record="+record);
                System.out.println("record[0] has record number "+
                record[0].getRecordPosition());
                StringOrXmlFragment frag=record[0].getRecordData();
                System.out.println("frag="+frag);
                MessageElement[] elems=frag.get_any();
                System.out.println("elems="+elems);
                System.out.println("value="+elems[0].getValue());
            }
            System.out.println("nextRecordPosition="+response.getNextRecordPosition());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
  private JIconButton homeButton;
  private JTextField urlField;
  private JEditorPane htmlPane;
  private String initialURL;
  private Hashtable transformers=new Hashtable();

  public SRWGuiClient(String initialURL) {
    super("Simple Swing Browser");
    this.initialURL = initialURL;
    addWindowListener(new ExitListener());
    WindowUtilities.setNativeLookAndFeel();

    JPanel topPanel = new JPanel();
    topPanel.setBackground(Color.lightGray);
    homeButton = new JIconButton("home.gif");
    homeButton.addActionListener(this);
    JLabel urlLabel = new JLabel("URL:");
    urlField = new JTextField(30);
    urlField.setText(initialURL);
    urlField.addActionListener(this);
    topPanel.add(homeButton);
    topPanel.add(urlLabel);
    topPanel.add(urlField);
    getContentPane().add(topPanel, BorderLayout.NORTH);

    try {
        URL url=new URL(initialURL);
	BufferedReader in = new BufferedReader(
				new InputStreamReader(
				url.openStream()));
	boolean xml=true;
        String inputLine;
        StringBuffer content=new StringBuffer(), stylesheet=null;
        Transformer transformer=null;
        inputLine=in.readLine();
        if(inputLine==null) {
            System.out.println("No input read from URL: "+initialURL);
            return;
        }
        if(!inputLine.startsWith("<?xml ")) {
            xml=false;
            content.append(inputLine);
        }
        if(xml) {
            inputLine=in.readLine();
            if(inputLine.startsWith("<?xml-stylesheet ")) {
                int offset=inputLine.indexOf("href=");
                String href=(inputLine.substring(inputLine.indexOf("href=")+6));
                href=href.substring(0, href.indexOf('"'));
                System.out.println("href="+href);
                url=new URL(url, href);
                String stylesheetURL=url.toString();
                System.out.println("stylesheet URL="+url.toString());
                transformer=(Transformer)transformers.get(stylesheetURL);
                if(transformer==null) {
                    BufferedReader sheetIn = new BufferedReader(
                                    new InputStreamReader(
                                    url.openStream()));
                    sheetIn.readLine(); // throw away xml declaration
                    stylesheet=new StringBuffer();
                    while ((inputLine = sheetIn.readLine()) != null)
                        stylesheet.append(inputLine).append('\n');
                    System.out.println(stylesheet.toString());
                    TransformerFactory tFactory=
                        TransformerFactory.newInstance();
                    StreamSource xslSource=new StreamSource(
                        new StringReader(stylesheet.toString()));
                    transformer=tFactory.newTransformer(xslSource);
                    transformers.put(stylesheetURL, transformer);
                }
            }
        }
	while ((inputLine = in.readLine()) != null)
            content.append(inputLine).append('\n');

        htmlPane = new JEditorPane();
        if(transformer!=null) {
            StringReader stringRecordReader=new StringReader(content.toString());
            StringWriter xmlRecordWriter=new StringWriter();
            StreamSource streamXMLRecord=new StreamSource(stringRecordReader);
            transformer.transform(streamXMLRecord,
                new StreamResult(xmlRecordWriter));
            String html=xmlRecordWriter.toString();
            int i=html.indexOf('>');
            html="<html>"+html.substring(html.indexOf('>')+1);
            System.out.println(html);
            htmlPane.setContentType("text/html");
            htmlPane.setText("<html><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>GSAFD Thesaurus</title></head><body><center><h2>GSAFD Thesaurus</h2></center><p>A thesaurus of genre terms to support the \"Guidelines on Subject Access to Individual Works of Fiction , Drama\" (GSAFD)</p><table cellspacing=\"5\" width=\"100%\"><tr><td><h3>Search</h3><p></p></td><td valign=\"top\"><h3>Browse</h3></td></tr></table></body></html>");
            //htmlPane.setText(html);
        }
        else
            htmlPane.setText(content.toString());
        htmlPane.setEditable(false);
        htmlPane.addHyperlinkListener(this);
        JScrollPane scrollPane = new JScrollPane(htmlPane);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }
    catch(IOException ioe) {
       warnUser("Can't build HTML pane for " + initialURL 
                + ": " + ioe);
    }
    catch(TransformerException e) {
        
    }
    

    Dimension screenSize = getToolkit().getScreenSize();
    int width = screenSize.width * 8 / 10;
    int height = screenSize.height * 8 / 10;
    setBounds(width/8, height/8, width, height);
    setVisible(true);
  }

  public void actionPerformed(ActionEvent event) {
    String url;
    if (event.getSource() == urlField) 
      url = urlField.getText();
    else  // Clicked "home" button instead of entering URL
      url = initialURL;
    try {
      htmlPane.setPage(new URL(url));
      urlField.setText(url);
    } catch(IOException ioe) {
      warnUser("Can't follow link to " + url + ": " + ioe);
    }
  }

  public void hyperlinkUpdate(HyperlinkEvent event) {
    if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      try {
        htmlPane.setPage(event.getURL());
        urlField.setText(event.getURL().toExternalForm());
      } catch(IOException ioe) {
        warnUser("Can't follow link to " 
                 + event.getURL().toExternalForm() + ": " + ioe);
      }
    }
  }

  private void warnUser(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", 
                                  JOptionPane.ERROR_MESSAGE);
  }
}

class JIconButton extends JButton {
  public JIconButton(String file) {
    super(new ImageIcon(file));
    setContentAreaFilled(false);
    setBorderPainted(false);
    setFocusPainted(false);
  }
}
class WindowUtilities {

  /** Tell system to use native look and feel, as in previous
   *  releases. Metal (Java) LAF is the default otherwise.
   */

  public static void setNativeLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch(Exception e) {
      System.out.println("Error setting native LAF: " + e);
    }
  }

  public static void setJavaLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch(Exception e) {
      System.out.println("Error setting Java LAF: " + e);
    }
  }

   public static void setMotifLookAndFeel() {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
    } catch(Exception e) {
      System.out.println("Error setting Motif LAF: " + e);
    }
  }

  /** A simplified way to see a JPanel or other Container.
   *  Pops up a JFrame with specified Container as the content pane.
   */

  public static JFrame openInJFrame(Container content,
                                    int width,
                                    int height,
                                    String title,
                                    Color bgColor) {
    JFrame frame = new JFrame(title);
    frame.setBackground(bgColor);
    content.setBackground(bgColor);
    frame.setSize(width, height);
    frame.setContentPane(content);
    frame.addWindowListener(new ExitListener());
    frame.setVisible(true);
    return(frame);
  }

  /** Uses Color.white as the background color. */

  public static JFrame openInJFrame(Container content,
                                    int width,
                                    int height,
                                    String title) {
    return(openInJFrame(content, width, height, title, Color.white));
  }

  /** Uses Color.white as the background color, and the
   *  name of the Container's class as the JFrame title.
   */

  public static JFrame openInJFrame(Container content,
                                    int width,
                                    int height) {
    return(openInJFrame(content, width, height,
                        content.getClass().getName(),
                        Color.white));
  }
}
class ExitListener extends WindowAdapter {
  public void windowClosing(WindowEvent event) {
    System.exit(0);
  }
}
