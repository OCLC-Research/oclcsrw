/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.ScanRequestType;
import gov.loc.www.zing.srw.ScanResponseType;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;
import gov.loc.www.zing.srw.SearchRetrieveResponseType;
import gov.loc.www.zing.srw.interfaces.SRWPort;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.ParseException;

/**
 *
 * @author levan
 */
public class SRUService implements SRWPort {
    String serviceUrl;
    public SRUService(URL serviceUrl) {
        this.serviceUrl=serviceUrl.toExternalForm()+'?';
    }

    @Override
    public SearchRetrieveResponseType searchRetrieveOperation(SearchRetrieveRequestType request) throws RemoteException {
        try {
            String url=serviceUrl+Utilities.toSRU(request);
            String response=Utilities.readURL(url);
            return (SearchRetrieveResponseType) Utilities.xmlToObj(response);
        } catch (IOException ex) {
            throw new RemoteException(null, ex);
        } catch (ParseException ex) {
            throw new RemoteException(null, ex);
        }
    }

    @Override
    public ScanResponseType scanOperation(ScanRequestType request) throws RemoteException {
        try {
            String url=serviceUrl+Utilities.toSRU(request);
            String response=Utilities.readURL(url);
            return (ScanResponseType) Utilities.xmlToObj(response);
        } catch (IOException ex) {
            throw new RemoteException(null, ex);
        } catch (ParseException ex) {
            throw new RemoteException(null, ex);
        }
    }
}