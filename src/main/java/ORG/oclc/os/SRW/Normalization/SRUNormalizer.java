package ORG.oclc.os.SRW.Normalization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.icu.text.CollationKey;
import com.ibm.icu.text.RuleBasedCollator;

/**
 * author@ Jui-wen Chang Date: Mar. 22, 2006 Time: 10:20:43 PM
 */
public class SRUNormalizer {
	//public static final String ruleset = "[strength 1][alternate shifted] &[before 1]0 < ' ' &'\u20B1'=[variable top] ";
	public static final String ruleset = "[strength 1][alternate shifted]";
	public static RuleBasedCollator col = null;
	private static char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	static Log log=LogFactory.getLog(SRUNormalizer.class);

	static {
		try{
			col = new RuleBasedCollator (ruleset);
		} catch (Exception e){
			log.fatal("Customized ICU collation creation failed. Rule= " + ruleset + ". " + e.toString());
			System.err.println ("Customized collation creation failed.");
			e.printStackTrace();
		}		
	}
	public static StringBuffer toHexString ( byte[] b, int start, int len ){
		StringBuffer sb = new StringBuffer( (b.length-1) * 2 );
		for ( int i=start; i<len; i++ ){
			//	 look up high nibble char
			sb.append( hexChar [( b[i] & 0xf0 ) >>> 4] );
			//	 look up low nibble char
			sb.append( hexChar [b[i] & 0x0f] );
		}
		return sb;
	}
	private static byte[] getSortByteArray (String term)
	{
		CollationKey colKey = col.getCollationKey(term);
		return colKey.toByteArray();
	}
	
	public static byte[] getSortBytes (String term)
	{
		byte[] bytes = getSortByteArray(term);       //    bytes (with trailing x'00')
		byte[] retbytes = new byte[bytes.length-1];
		System.arraycopy(bytes, 0, retbytes, 0, bytes.length-1);
		return retbytes;
    }
	 
	public static String getSrotString(String term) {
		byte[] bytes = getSortByteArray(term);       //    bytes (with trailing x'00')
		String retStr = new String(bytes, 0, bytes.length-1);
		return retStr;
	}
	  
	public static String getSortHexString (String term)
	{
		byte[] bytes = getSortByteArray(term);       //    bytes (with trailing x'00')
		return toHexString(bytes, 0, bytes.length-1).toString();
   }
	 
	public static String getSortHexString_word (String term) {
		byte[] bytes = getSortByteArray(term);       //    bytes (with trailing x'00')
		if (bytes.length<=0)
			return "";
		if ((int)bytes[0]>=38 && (int)bytes[0] <=93)  //remove leading none 0..z byte for non-roman word search
			return toHexString(bytes, 0, bytes.length-1).toString();
		return toHexString(bytes, 1, bytes.length-1).toString();
	}
	
	public static String getIndexString(String func, String term) {
		if (func.equalsIgnoreCase("upper"))
			return term.toUpperCase();
		if (func.equalsIgnoreCase("lower"))
			return term.toLowerCase();
		if (func.equalsIgnoreCase("ICUSort")) {
			String hexString = getSortHexString(term);
			if (hexString.length()>0)
				return hexString;
			return hexString;
		}
		return null;
	}

	public static String getIndexString_word(String func, String term) {
		if (func.equalsIgnoreCase("upper"))
			return term.toUpperCase();
		if (func.equalsIgnoreCase("lower"))
			return term.toLowerCase();
		if (func.equalsIgnoreCase("ICUSort")) {
			String hexString = getSortHexString_word(term);
			if (hexString.length()>0)
				return hexString;
			return hexString;
		}
		return null;
	}

	public static byte[] getIndexBytes(String func, String term) {
		if (func.equals("ICUSort"))
			return getSortBytes(term);
		return null;	
	}
	public static int getIndexInt(String func, String term) {
		return Integer.parseInt(term);
	}
	public static double getIndexDecimal(String func, String term) {
		return Double.parseDouble(term);
	}

	
}
