package bean.ncimport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Jianan
 * @Date
 */
public class NCImpHeader {
	Long id;
	String ncOrg = "01"; //库存组织
	String impDate;
	String kb; //库别
	String type = "40-01";

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNcOrg() {
		return ncOrg;
	}

	public void setNcOrg(String ncOrg) {
		this.ncOrg = ncOrg;
	}

	public String getImpDate() {
		return impDate;
	}

	public void setImpDate(String impDate) {
		this.impDate = impDate;
	}

	public String getKb() {
		return kb;
	}

	public void setKb(String kb) {
		this.kb = kb;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	static Map<String, NCImpHeader> orgKbToHeader = new HashMap<String, NCImpHeader>();

	public static NCImpHeader getHeader(String orgNo, String kbNo){
		String key = orgNo + kbNo;
		NCImpHeader header = orgKbToHeader.get(key);
		if(header == null){
			header = new NCImpHeader();
			header.setNcOrg(orgNo);
			header.setKb(kbNo);
			orgKbToHeader.put(key, header);
		}
		return header;
	}

	public static void main(String[] args){
	    Set<NCImpHeader> set = new HashSet<NCImpHeader>();
	    set.add(getHeader("01", "86"));
		set.add(getHeader("01", "86"));
		set.add(getHeader("01", "86"));
		set.add(getHeader("01", "30"));
	    System.out.println(set.size());
	}

}
