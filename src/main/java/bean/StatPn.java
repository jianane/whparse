package bean;

/**
 * @author Jianan
 * @Date
 */
public class StatPn {
	String pn;
	int u8Cnt; //u8数量
	int scanCnt; //扫码数量
	int selfCheckCnt; //自盘数量
	boolean serial;
	String wh;
	String selfCheckWh;
	String pName;
	String unitPrice;
	int u8ChangeCnt;

	public String getPn() {
		return pn;
	}

	public void setPn(String pn) {
		this.pn = pn;
	}

	public int getU8Cnt() {
		return u8Cnt;
	}

	public void setU8Cnt(int u8Cnt) {
		this.u8Cnt = u8Cnt;
	}

	public int getScanCnt() {
		return scanCnt;
	}

	public void setScanCnt(int scanCnt) {
		this.scanCnt = scanCnt;
	}

	public int getSelfCheckCnt() {
		return selfCheckCnt;
	}

	public void setSelfCheckCnt(int selfCheckCnt) {
		this.selfCheckCnt = selfCheckCnt;
	}

	public boolean isSerial() {
		return serial;
	}

	public void setSerial(boolean serial) {
		this.serial = serial;
	}

	public String getWh() {
		return wh;
	}

	public void setWh(String wh) {
		this.wh = wh;
	}

	public String getSelfCheckWh() {
		return selfCheckWh;
	}

	public void setSelfCheckWh(String selfCheckWh) {
		this.selfCheckWh = selfCheckWh;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

	public int getU8ChangeCnt() {
		return u8ChangeCnt;
	}

	public void setU8ChangeCnt(int u8ChangeCnt) {
		this.u8ChangeCnt = u8ChangeCnt;
	}
}


