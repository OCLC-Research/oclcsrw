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
import gov.loc.www.zing.srw.TermTypeWhereInList;

/**
 *
 * @author levan
 */
public class TermList extends SRWDiagnosticsHolder {
    TermType[] terms;
    public TermList() {
    }

    public TermList(TermType[] terms) {
        TermType t;
        this.terms=new TermType[terms.length];
        for(int i=0; i<terms.length; i++) {
            t=terms[i];
            this.terms[i]=new TermType(t.getValue(), t.getNumberOfRecords(), t.getDisplayTerm(), TermTypeWhereInList.inner, null);
        }
    }

    public TermList(String[] terms) {
        String t;
        this.terms=new TermType[terms.length];
        for(int i=0; i<terms.length; i++) {
            t=terms[i];
            this.terms[i]=new TermType(t, null, t, TermTypeWhereInList.inner, null);
        }
    }

    public TermType[] getTerms() {
        return terms;
    }

    public void setTerms(TermType[] terms) {
        this.terms=terms;
    }
}
