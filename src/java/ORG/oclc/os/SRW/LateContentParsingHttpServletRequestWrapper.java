package ORG.oclc.os.SRW;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author levan
 */
public class LateContentParsingHttpServletRequestWrapper extends HttpServletRequestWrapper {
    boolean hasParametersAlready;
    HashMap<String, ArrayList<String>> parms=new HashMap<String, ArrayList<String>>();

    public LateContentParsingHttpServletRequestWrapper(HttpServletRequest request, String content) {
        super(request);
        Enumeration enumer=request.getParameterNames();
        hasParametersAlready=enumer.hasMoreElements();
        if(!hasParametersAlready) { // we have to parse the content ourselves
//            System.out.println("content: "+content);
            int eq;
            String token;
            StringTokenizer st=new StringTokenizer(content, "&");
            while(st.hasMoreTokens()) {
                token=st.nextToken();
//                System.out.println("token: "+token);
                if((eq=token.indexOf('='))>0) {
//                    System.out.println(token.substring(0, eq)+": "+token.substring(eq+1));
                    add(token.substring(0, eq), Utilities.unUrlEncode(token.substring(eq+1)));
                }
                else
                    add(token, null);
            }
        }
    }

    @Override
    public String getParameter(String name) {
        if(hasParametersAlready)
            return super.getParameter(name);
        ArrayList<String> v = parms.get(name);
        if(v==null)
            return null;
        return v.get(0);
    }

    @Override
    public Enumeration getParameterNames() {
        if(hasParametersAlready)
            return super.getParameterNames();
        return Collections.enumeration(parms.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        if(hasParametersAlready)
            return super.getParameterValues(name);
        ArrayList<String> v=parms.get(name);
        if(v==null)
            return null;
        return (String[])v.toArray();
    }

    private void add(String name, String value) {
        ArrayList<String> v=parms.get(name);
        if(v==null) {
            v=new ArrayList<String>();
            parms.put(name, v);
        }
        v.add(value);
    }
}
