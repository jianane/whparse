package com.bdcom.analyze;

import com.bdcom.util.MyUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.*;

/**
 * @author Jianan
 * @Date
 */
public class WhU8NcParse {

	private static Map<String, String> org_ncKb_u8KwToNcKb = new HashMap<String, String>();

	private static Map<String, String> pn_kwToU8Kb = new HashMap<String, String>();
	private static Map<String, String> pn_kwToKbCode = new HashMap<String, String>();
	private static Map<String, String> pn_kwToKwCode = new HashMap<String, String>();
	private static Map<String, String> pn_kwToKwName = new HashMap<String, String>();

	private static Map<String, Set<String>> selfNotInNC = new HashMap<String, Set<String>>();

	public static Map<String, String> u8kbToNCkb = new HashMap<String, String>();
	public static Map<String, List<String>> ncKbToU8Kw = new HashMap<String, List<String>>();

	private static Map<String, List<String>> u8KbToU8Kw = new HashMap<String, List<String>>();

	public static void parseKbu8ToNc(){
		Workbook wb = MyUtil.readExcel("C:/Users/Administrator/Desktop/whscan/whMap/仓库对照.xlsx");
		Sheet sheet = wb.getSheetAt(0);//u8 to NC kb
		Row row;
		Cell cell;
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			cell = row.getCell(1);
			if (MyUtil.cellIsNull(cell)) {
				continue;
			}
			String u8Kb = MyUtil.getCellString(cell);

			cell = row.getCell(3);
			String ncKb = MyUtil.getStringTypeCell(cell);
			u8kbToNCkb.put(u8Kb, ncKb);

		}
	}

	private static void parseNcBdWh(){
		Workbook wb = MyUtil.readExcel("C:/Users/Administrator/Desktop/whscan/whMap/货位导入-0830.xlsx");
		Sheet sheet = wb.getSheetAt(0);
		Row row;
		Cell cell;
		for (int i = 2; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);

			cell = row.getCell(1);// ncOrg
			String ncOrg = MyUtil.getStringTypeCell(cell);

			cell = row.getCell(2);// ncKb
			String ncKb = MyUtil.getStringTypeCell(cell);

			cell = row.getCell(3);// ncKw
			String ncKw = MyUtil.getStringTypeCell(cell);

			cell = row.getCell(4);// u8Kw
			String u8Kw = MyUtil.getStringTypeCell(cell);

			org_ncKb_u8KwToNcKb.put(getNCKbKey(ncOrg, ncKb, u8Kw), ncKw);

			List<String> u8Kws = ncKbToU8Kw.get(ncKb);
			if (u8Kws == null) {
				u8Kws = new ArrayList<String>();
				ncKbToU8Kw.put(ncKb, u8Kws);
			}
			u8Kws.add(u8Kw);

		}
	}
	private static String getNCKbKey(String ncOrg, String ncKb, String u8Kw){
		return ncOrg + "_" + ncKb + "_" + u8Kw;
	}

	private static void parseSelfCheckZhMap(){
		Workbook wb = MyUtil.readExcel("C:/Users/Administrator/Desktop/whscan/whMap/onlyCompleteKuBie.xlsx");
		Sheet sheet = wb.getSheetAt(0);
		Row row;
		Cell cell;
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			cell = row.getCell(0); //pn
			String pn = MyUtil.getCellString(cell);

			cell = row.getCell(1); //u8Kw
			String u8Kw = MyUtil.getCellString(cell);

			cell = row.getCell(2); //kwCode
			String kwCode = MyUtil.getCellString(cell);

			cell = row.getCell(3); //kwName
			String kwName = MyUtil.getCellString(cell);

			cell = row.getCell(4); //kbCode  10  11  13
			String kbCode = MyUtil.getCellString(cell);

			cell = row.getCell(5); //u8Kb  30  31  86
			String u8Kb = MyUtil.getCellString(cell);

			pn_kwToKwCode.put(getPn_KwKey(pn, u8Kw), kwCode);
			pn_kwToKwName.put(getPn_KwKey(pn, u8Kw), kwName);

			pn_kwToU8Kb.put(getPn_KwKey(pn, u8Kw), u8Kb);
			pn_kwToKbCode.put(getPn_KwKey(pn, u8Kw), kbCode);


		}
	}
	private static String getPn_KwKey(String pn, String u8Kw){
		return pn + "_" + u8Kw;
	}

	public static String getU8KbCode(String pn, String kw){
		if(pn_kwToU8Kb.size() == 0){
			parseSelfCheckZhMap();
		}
		String u8Kb = pn_kwToU8Kb.get(getPn_KwKey(pn, kw));
		return u8Kb;
	}
	public static String getKbCode(String pn, String kw){
		if(pn_kwToKbCode.size() == 0){
			parseSelfCheckZhMap();
		}
		String kbCode = pn_kwToKbCode.get(getPn_KwKey(pn, kw));
		return kbCode;

//		return "10";
	}

	public static String getKwCode(String pn, String kw){
		if(pn_kwToKwCode.size() == 0){
			parseSelfCheckZhMap();
		}
		String kwCode = pn_kwToKwCode.get(getPn_KwKey(pn, kw));
		return kwCode;
	}
	public static String getKwName(String pn, String kw){
		if(pn_kwToKwName.size() == 0){
			parseSelfCheckZhMap();
		}
		String kwCode = pn_kwToKwName.get(getPn_KwKey(pn, kw));
		return kwCode;

//		return "KWZZZZ";
	}
//	public static void initU8NcWhMap(){
//		parseKbu8ToNc();
////		parseNcBdWh();
//		parseSelfCheckZhMap();
//	}

	public static void main(String[] args){
	    parseKbu8ToNc();
	    parseNcBdWh();
	    parseSelfCheckZhMap();

		Set<Map.Entry<String, List<String>>> entries = u8KbToU8Kw.entrySet();
		for (Map.Entry<String, List<String>> entry : entries) {
			String u8Kb = entry.getKey();
			List<String> selfU8Kws = entry.getValue();

			String ncKb = u8kbToNCkb.get(u8Kb);
			List<String> u8KwsInNC = ncKbToU8Kw.get(ncKb);

			for (String selfU8Kw : selfU8Kws) {
				if (u8KwsInNC.contains(selfU8Kw) || u8KwsInNC.contains("KW" + selfU8Kw)) {
					continue;
				}
				Set<String> selfNoIns = selfNotInNC.get(u8Kb);
				if (selfNoIns == null) {
					selfNoIns = new HashSet<String>();
					selfNotInNC.put(u8Kb, selfNoIns);
				}
				selfNoIns.add(selfU8Kw);
			}
		}

		Set<Map.Entry<String, Set<String>>> entries1 = selfNotInNC.entrySet();
		int errCnt = 0;
		for (Map.Entry<String, Set<String>> entry : entries1) {
			String kb = entry.getKey();
			Set<String> kws = entry.getValue();
			for (String kw : kws) {
				errCnt++;
				System.out.println(kb + "," + kw);
			}
		}
		System.out.println(errCnt);
	}

}
