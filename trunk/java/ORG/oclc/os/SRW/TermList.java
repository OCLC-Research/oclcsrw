/*
 * TermList.java
 *
 * Created on October 20, 2006, 9:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.TermType;

/**
 *
 * @author levan
 */
public class TermList extends SRWDiagnosticsHolder {
    TermType[] terms;
    public TermType[] getTerms() {
        return terms;
    }
    public void setTerms(TermType[] terms) {
        this.terms=terms;
    }
}
