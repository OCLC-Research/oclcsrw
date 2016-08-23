/*
 * QueryTranslator.java
 *
 * Created on October 27, 2006, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ORG.oclc.os.SRW;

import gov.loc.www.zing.srw.ExtraDataType;
import java.util.Properties;
import org.z3950.zing.cql.CQLNode;

/**
 *
 * @author levan
 */
public interface QueryTranslator {
    public void init(Properties properties);
    public Object translate(CQLNode queryRootNode, ExtraDataType extraRequestData);
}
