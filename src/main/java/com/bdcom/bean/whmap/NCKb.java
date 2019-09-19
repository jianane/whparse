package com.bdcom.bean.whmap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jianan
 * @Date
 */
public class NCKb {

	public Map<String, NCKw> kwNameToNCKw = new HashMap<String, NCKw>();

	String code;
	String name;
	String u8Code;


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

	public String getU8Code() {
		return u8Code;
	}

	public void setU8Code(String u8Code) {
		this.u8Code = u8Code;
	}
}
