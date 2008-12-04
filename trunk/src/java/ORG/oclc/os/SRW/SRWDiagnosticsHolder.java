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
 * TermList.java
 *
 * Created on October 20, 2006, 11:01 AM
 */

package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.DiagnosticsType;
import gov.loc.www.zing.srw.diagnostic.DiagnosticType;
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.Vector;

/**
 *
 * @author levan
 */
public abstract class SRWDiagnosticsHolder {
    Vector    diagnostics=null;

    public boolean addDiagnostic(DiagnosticType diagnostic) {
        if(diagnostics==null)
            diagnostics=new Vector();
        return diagnostics.add(diagnostic);
    }

    public boolean addDiagnostic(int code, String addInfo) {
        if(diagnostics==null)
            diagnostics=new Vector();
        DiagnosticType dt=SRWDiagnostic.newDiagnosticType(code, addInfo);
        return diagnostics.add(dt);
    }

    public boolean addDiagnostics(DiagnosticsType diagnostics) {
        if(diagnostics==null)
            return false;
        return addDiagnostics(diagnostics.getDiagnostic());
    }

    public boolean addDiagnostics(DiagnosticType[] diagnostics) {
        for(int i=0; i<diagnostics.length; i++)
            addDiagnostic(diagnostics[i]);
        return true;
    }

    public boolean addDiagnostics(Vector diagnostics) {
        for(int i=0; i<diagnostics.size(); i++)
            addDiagnostic((DiagnosticType)diagnostics.get(i));
        return true;
    }

    public Vector getDiagnostics() {
        return diagnostics;
    }

    public boolean hasDiagnostics() {
        if(diagnostics!=null)
            return true;
        return false;
    }
}
