package com.bdcom.bean.ncimport;

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
	String count;
	String impDate;
	String kw;
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

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getImpDate() {
		return impDate;
	}

	public void setImpDate(String impDate) {
		this.impDate = impDate;
	}

	public String getKw() {
		return kw;
	}

	public void setKw(String kw) {
		this.kw = kw;
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

	public static void main(String[] args){
	    System.out.println("KWA9通道".compareTo("5楼"));

	}
}
