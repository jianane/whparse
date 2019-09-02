package bean.whmap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jianan
 * @Date
 */
public class NCOrg {

	public static Map<String, NCOrg> orgCodeToOrg = new HashMap<String, NCOrg>();

	public Map<String, NCKb> u8CodeToNCKb = new HashMap<String, NCKb>();

	String code;
	String name;


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
