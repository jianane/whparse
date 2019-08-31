package bean.ncimport;

import java.util.List;

/**
 * @author Jianan
 * @Date
 */
public class NCImpBody {
	Long headId;
	Long id;
	String pn;
	String unit;
	int cnt;
	String impDate;
	String whCat; //库别
	String wh;
	String sn;
	String snUnit;

	public Long getHeadId() {
		return headId;
	}

	public void setHeadId(Long headId) {
		this.headId = headId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPn() {
		return pn;
	}

	public void setPn(String pn) {
		this.pn = pn;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

	public String getImpDate() {
		return impDate;
	}

	public void setImpDate(String impDate) {
		this.impDate = impDate;
	}

	public String getWh() {
		return wh;
	}

	public void setWh(String wh) {
		this.wh = wh;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getSnUnit() {
		return snUnit;
	}

	public void setSnUnit(String snUnit) {
		this.snUnit = snUnit;
	}

	public String getWhCat() {
		return whCat;
	}

	public void setWhCat(String whCat) {
		this.whCat = whCat;
	}
}
