package bean.ncimport;

/**
 * @author Jianan
 * @Date
 */
public class NCImpHeader {
	Long id;
	String whOrg = "01"; //库存组织
	String impDate;
	String whCat; //库别
	String type = "期初余额";

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWhOrg() {
		return whOrg;
	}

	public void setWhOrg(String whOrg) {
		this.whOrg = whOrg;
	}

	public String getImpDate() {
		return impDate;
	}

	public void setImpDate(String impDate) {
		this.impDate = impDate;
	}

	public String getWhCat() {
		return whCat;
	}

	public void setWhCat(String whCat) {
		this.whCat = whCat;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
