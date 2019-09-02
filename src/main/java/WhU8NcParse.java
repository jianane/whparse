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

	static Map<String, String> org_ncKb_u8KwToNcKb = new HashMap<String, String>();

	static Map<String, String> pn_kwToU8Kb = new HashMap<String, String>();

	static Map<String, Set<String>> selfNotInNC = new HashMap<String, Set<String>>();

	static Map<String, String> u8kbToNCkb = new HashMap<String, String>();
	static Map<String, List<String>> ncKbToU8Kw = new HashMap<String, List<String>>();

	static Map<String, List<String>> u8KbToU8Kw = new HashMap<String, List<String>>();

	static void parseKbu8ToNc(){
		Workbook wb = WhparseMain.readExcel("C:/Users/Administrator/Desktop/whscan/whMap/仓库对照.xlsx");
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

	static void parseNcBdWh(){
		Workbook wb = WhparseMain.readExcel("C:/Users/Administrator/Desktop/whscan/whMap/货位导入-0830.xlsx");
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
	static String getNCKbKey(String ncOrg, String ncKb, String u8Kw){
		return ncOrg + "_" + ncKb + "_" + u8Kw;
	}

	static void parseSelfCheckZhMap(){
		Workbook wb = WhparseMain.readExcel("C:/Users/Administrator/Desktop/whscan/whMap/onlyWuWeiLeiBie.xlsx");
		Sheet sheet = wb.getSheetAt(0);
		Row row;
		Cell cell;
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			cell = row.getCell(0); //u8Kw
			String u8Kw = MyUtil.getCellString(cell);

			cell = row.getCell(1); //pn
			String pn = MyUtil.getCellString(cell);

			cell = row.getCell(2); //u8Kb
			String u8Kb = MyUtil.getStringTypeCell(cell);


			List<String> u8Kws = u8KbToU8Kw.get(u8Kb);
			if (u8Kws == null) {
				u8Kws = new ArrayList<String>();
				u8KbToU8Kw.put(u8Kb, u8Kws);
			}
			u8Kws.add(u8Kw);

			pn_kwToU8Kb.put(getPn_KwKey(pn, u8Kw), u8Kb);
		}
	}
	static String getPn_KwKey(String pn, String u8Kw){
		return pn + "_" + u8Kw;
	}

	static void parsePnKwToNCKbMap(String pn, String kw){

		if(pn_kwToU8Kb.size() == 0){
			parseSelfCheckZhMap();
		}
		String u8Kb = pn_kwToU8Kb.get(getPn_KwKey(pn, kw));
		if (u8kbToNCkb.size() == 0) {
			parseKbu8ToNc();
		}
		String ncKb = u8kbToNCkb.get(u8Kb);

		if (org_ncKb_u8KwToNcKb.size() == 0) {
			parseNcBdWh();
		}
		String ncKw = org_ncKb_u8KwToNcKb.get(getNCKbKey("01", ncKb, kw));

	}
	public static void initU8NcWhMap(){
		parseKbu8ToNc();
		parseNcBdWh();
		parseSelfCheckZhMap();
	}

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
