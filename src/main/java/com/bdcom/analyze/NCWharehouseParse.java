package com.bdcom.analyze;

import com.bdcom.bean.whmap.NCKb;
import com.bdcom.bean.whmap.NCKw;
import com.bdcom.bean.whmap.NCOrg;
import com.bdcom.util.MyUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jianan
 * @Date
 */
public class NCWharehouseParse {

	static void parseNcBdWh(){
		Map<String, String> kbNameToU8KbCode = new HashMap<String, String>();
		kbNameToU8KbCode.put("产品封存库", "86");
		kbNameToU8KbCode.put("成品库", "30");
		kbNameToU8KbCode.put("外观不良品库", "31");
//		kbNameToU8KbCode.put("产品封存库", "86");
//		kbNameToU8KbCode.put("产品封存库", "86");
//		kbNameToU8KbCode.put("产品封存库", "86");
//		kbNameToU8KbCode.put("产品封存库", "86");
		Workbook wb = MyUtil.readExcel("C:/Users/Administrator/Desktop/whscan/whMap/huowei(1).xlsx");
		Sheet sheet = wb.getSheetAt(0);
		Row row;
		Cell cell;
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);

			cell = row.getCell(1);// kwCode
			String kwCode = MyUtil.getStringTypeCell(cell);

			cell = row.getCell(2);// kwName
			String kwName = MyUtil.getStringTypeCell(cell);

			cell = row.getCell(3);// kbCode
			String kbCode = MyUtil.getStringTypeCell(cell);

			cell = row.getCell(4);// kbName
			String kbName = MyUtil.getStringTypeCell(cell);
			String u8kbCode = kbNameToU8KbCode.get(kbName);
			if (u8kbCode == null) {
				continue;
			}

			cell = row.getCell(5);// orgCode
			String orgCode = MyUtil.getStringTypeCell(cell);

			cell = row.getCell(6);// orgName
			String orgName = MyUtil.getStringTypeCell(cell);

			NCOrg ncOrg = NCOrg.orgCodeToOrg.get(orgCode);
			if (ncOrg == null) {
				ncOrg = new NCOrg();
				ncOrg.setCode(orgCode);
				ncOrg.setName(orgName);
				NCOrg.orgCodeToOrg.put(orgCode, ncOrg);
			}

			NCKb ncKb = ncOrg.u8CodeToNCKb.get(u8kbCode);
			if (ncKb == null) {
				ncKb = new NCKb();
				ncKb.setCode(kbCode);
				ncKb.setName(kbName);
				ncKb.setU8Code(u8kbCode);
				ncOrg.u8CodeToNCKb.put(u8kbCode, ncKb);
			}

			NCKw ncKw = ncKb.kwNameToNCKw.get(kwName);
			if (ncKw == null) {
				ncKw = new NCKw();
				ncKw.setCode(kwCode);
				ncKw.setName(kwName);
				ncKb.kwNameToNCKw.put(kwName, ncKw);
			}
		}
	}

	public static String getNCKwCode(String orgCode, String u8KbCode, String kwName){
		if (NCOrg.orgCodeToOrg.size() == 0) {
			parseNcBdWh();
		}
		NCOrg ncOrg = NCOrg.orgCodeToOrg.get(orgCode);
		NCKb ncKb = ncOrg.u8CodeToNCKb.get(u8KbCode);
		NCKw ncKw = ncKb.kwNameToNCKw.get(kwName);
		String kwCode = ncKw.getCode();
		return kwCode;
	}
	public static String getBdcomNCKwCode(String u8KbCode, String kwName){
		return getNCKwCode("01", u8KbCode, kwName);
	}

	public static void main(String[] args){
		parseNcBdWh();
		System.out.println();

	}

}
