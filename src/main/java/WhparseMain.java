import bean.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @author Jianan
 * @Date
 */
public class WhparseMain {

	static Map<String, StatPn> pnToStat = new LinkedHashMap<String, StatPn>();

	static Set<String> allScanPn = new HashSet<String>();
	static Set<String> allU8Pn = new HashSet<String>();
	static Set<String> allSelfCheckPn = new HashSet<String>();
	static Set<String> errorPnSet = new HashSet<String>();

	static Map<String, NCMaterialDoc> pnToNCMaterial = new HashMap<String, NCMaterialDoc>();


	final static String SELF_CHECK_PATH = "C:/Users/Administrator/Desktop/whscan/self_check/电子台账0831（关账汇总）.xlsx";
	final static String U8_PATH = "C:/Users/Administrator/Desktop/whscan/u8/现存量查询_2019.8.31.xls";
	final static String U8_PATH_Pre_Day = "C:/Users/Administrator/Desktop/whscan/u8/现存量查询_2019.08.29.xlsx";
	final static String PN_DOC_PATH = "C:/Users/Administrator/Desktop/whscan/pdoc/物料档案（序列号管理）_0829.xlsx";
	final static String NC_MATERIAL_DOC_PATH = "C:/Users/Administrator/Desktop/whscan/pdoc/物料档案20190831.xlsx";

	final static String EXPORT_PRE = "C:/Users/Administrator/Desktop/whscan/stat_jianan/0831/";
	final static String EXPORT_PATH_ALL_RIGHT = EXPORT_PRE + "allRight_jianan.xlsx";
	final static String EXPORT_PATH_ERR_PN = EXPORT_PRE + "errPn_jianan.xlsx";
	final static String EXPORT_PATH_NO_SCAN_SAME = EXPORT_PRE + "noScanU8StoreSame_jianan.xlsx";
	final static String EXPORT_PATH_OTHER = EXPORT_PRE + "other_jianan.xlsx";
	final static String EXPORT_PATH_ALL = EXPORT_PRE + "all_jianan.xlsx";
	final static String EXPORT_PATH_NOT_IN_NC = EXPORT_PRE + "notInNC_jianan.xlsx";
	final static String EXPORT_PATH_IN_NC_NO_SERIAL = EXPORT_PRE + "inNCNoSerial_jianan.xlsx";
	final static String EXPORT_PATH_NO_Serial = EXPORT_PRE + "noSerial_jianan.xlsx";

	public static void main(String[] args) {
		parseScanToPnStatPn();
		parseSelfData();
		parseU8Data();
		System.out.println("***************************************allScanPn : " + allScanPn.size() + "*************************************************");
		System.out.println("***************************************allSelfCheckPn : " + allSelfCheckPn.size() + "*************************************************");
		System.out.println("***************************************allU8Pn : " + allU8Pn.size() + "*************************************************");

		statAddStock();

		generateNCMaterial();

		createStatistics();
	}
	static void generateNCMaterial(){
		Workbook wb = WhparseMain.readExcel(NC_MATERIAL_DOC_PATH);
		Sheet sheet = wb.getSheetAt(0);
		Cell cell;
		Row row;
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}

			cell = row.getCell(1);
			if (MyUtil.cellIsNull(cell)) {
				continue;
			}
			NCMaterialDoc maDoc = new NCMaterialDoc();
			String pn = MyUtil.getCellString(cell);
			maDoc.setPn(pn);
			cell = row.getCell(2);
			String pName = MyUtil.getCellString(cell);
			maDoc.setpName(pName);
			cell = row.getCell(7);
			if ("Y".equals(MyUtil.getCellString(cell))) {
				maDoc.setSerial(true);
			}

			pnToNCMaterial.put(pn, maDoc);


		}

	}

	static void statAddStock() {
		Map<String, Stock> pnToStock = StockParse.parseStock();
		Set<Map.Entry<String, StatPn>> entries = pnToStat.entrySet();
		for (Map.Entry<String, StatPn> entry : entries) {
			String pn = entry.getKey();
			Stock stock = pnToStock.get(pn);
			if (stock != null) {
				StatPn stat = entry.getValue();
				stat.setpName(stock.getpName());
				stat.setUnitPrice(stock.getUnitPrice());
			} else {
//				System.out.println(pn + ": stock is null");
			}
		}

	}

	static void parseScanToPnStatPn() {
		Map<String, OriginalBean> originalMap = WhParseStart.parseScanData();
		originalMap = WhParseStart.snUniMap;
		Map<String, Integer> pnToCnt = new HashMap<String, Integer>();
		Map<String, List<String>> pnToWhs = new HashMap<String, List<String>>();
		for (OriginalBean originalBean : originalMap.values()) {
			String pn = originalBean.getPn();
			allScanPn.add(pn);
			if (!pnToCnt.containsKey(pn)) {
				pnToCnt.put(pn, 0);
			}
			pnToCnt.put(pn, pnToCnt.get(pn) + 1);

			List<String> whList = pnToWhs.get(pn);
			if (whList == null) {
				whList = new ArrayList<String>();
				pnToWhs.put(pn, whList);
			}
			whList.add(originalBean.getKw());
		}
		Set<Map.Entry<String, Integer>> entries = pnToCnt.entrySet();
		for (Map.Entry<String, Integer> entry : entries) {
			String pn = entry.getKey();
			StatPn statPn = new StatPn();
			statPn.setPn(pn);
			statPn.setScanCnt(entry.getValue());
			List<String> whList = pnToWhs.get(entry.getKey());
			Map<String, Integer> whToCnt = new HashMap<String, Integer>();
			for (String wh : whList) {
				if (!whToCnt.containsKey(wh)) {
					whToCnt.put(wh, 0);
				}
				whToCnt.put(wh, whToCnt.get(wh) + 1);
			}
			Set<Map.Entry<String, Integer>> entries1 = whToCnt.entrySet();
			StringBuilder whBuilder = new StringBuilder();
			int enterCnt = 0;
			for (Map.Entry<String, Integer> wcEntry : entries1) {
				enterCnt++;
				whBuilder.append(wcEntry.getKey());
				whBuilder.append("=");
				whBuilder.append(wcEntry.getValue());
				whBuilder.append(" ");
//				if (enterCnt % 5 == 0) {
//					whBuilder.append("\r\n");
//				}
			}
			statPn.setWh(whBuilder.toString());
			pnToStat.put(pn, statPn);
		}

	}

	static void parseSelfData() {
		Map<String, Integer> pnToCnt = new HashMap<String, Integer>();
		Map<String, List<Warehouse>> pnToWhs = new HashMap<String, List<Warehouse>>();
		Workbook wb = readExcel(SELF_CHECK_PATH);
		Sheet sheet = wb.getSheetAt(0);
		Row row;
		Cell cell;

		for (int i = 3; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			cell = row.getCell(2);
			if (cell == null || "".equals(cell.toString().trim())) {
				continue;
			}
			cell.setCellType(CellType.STRING);
			String pn = cell.toString().toUpperCase().trim();
			allSelfCheckPn.add(pn);
			cell = row.getCell(5);
			if (MyUtil.cellIsNull(cell)) {
				continue;
			}
			cell.setCellType(CellType.STRING);
			String num = cell.getStringCellValue().replace("O", "0");
			num = num.replace("I", "1");
			int selfCnt = Integer.parseInt(num);
			if (!pnToCnt.containsKey(pn)) {
				pnToCnt.put(pn, 0);
			}
			pnToCnt.put(pn, pnToCnt.get(pn) + selfCnt);

			cell = row.getCell(4);
			cell.setCellType(CellType.STRING);
			String wh = cell.getStringCellValue().trim();

			List<Warehouse> whs = pnToWhs.get(pn);
			if (whs == null) {
				whs = new ArrayList<Warehouse>();
				pnToWhs.put(pn, whs);
			}
			Warehouse warehouse = new Warehouse();
			warehouse.setWh(wh);
			warehouse.setCount(selfCnt);
			whs.add(warehouse);

		}
		System.out.println(pnToStat.size());
		Set<Map.Entry<String, List<Warehouse>>> entries = pnToWhs.entrySet();
		for (Map.Entry<String, List<Warehouse>> entry : entries) {
			String pn = entry.getKey();
			List<Warehouse> whs = entry.getValue();
			int selfCnt = 0;
			Map<String, Integer> whToCnt = new HashMap<String, Integer>();
			for (Warehouse wh : whs) {
				String whName = wh.getWh();
				int whCount = wh.getCount();

				selfCnt += whCount;
				if (!whToCnt.containsKey(whName)) {
					whToCnt.put(whName, 0);
				}
				whToCnt.put(whName, whToCnt.get(whName) + whCount);
			}
			Set<Map.Entry<String, Integer>> entries1 = whToCnt.entrySet();
			StringBuilder whBuilder = new StringBuilder();
			int enterCnt = 0;
			for (Map.Entry<String, Integer> wcEntry : entries1) {
				enterCnt++;
				whBuilder.append(wcEntry.getKey());
				whBuilder.append("=");
				whBuilder.append(wcEntry.getValue());
				whBuilder.append(" ");
//				if (enterCnt % 5 == 0) {
//					whBuilder.append("\r\n");
//				}
			}
			StatPn statPn = pnToStat.get(pn);
			if (statPn == null) {
				statPn = new StatPn();
				statPn.setPn(pn);
				pnToStat.put(pn, statPn);
			}
			statPn.setSelfCheckCnt(selfCnt);
			statPn.setSelfCheckWh(whBuilder.toString());
		}


		System.out.println(pnToStat.size());
	}


	public static Workbook readExcel(String filePath) {
		Workbook wb = null;
		if (filePath == null) {
			return wb;
		}
		String extString = filePath.substring(filePath.lastIndexOf("."));
		InputStream is = null;
		try {
			is = new FileInputStream(filePath);
			if (".xls".equals(extString)) {
				wb = new HSSFWorkbook(is);
			} else if (".xlsx".equals(extString) || ".XLSX".equals(extString)) {
				wb = new XSSFWorkbook(is);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wb;
	}

	static Map<String, Integer> getPreU8Stat() {
		Map<String, Integer> pnToCnt = new HashMap<String, Integer>();
		Workbook wb = readExcel(U8_PATH_Pre_Day);
		Sheet sheet = wb.getSheetAt(0);
		Row row;
		Cell cell;
		for (int i = 3; i < sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			cell = row.getCell(1);
			if (cell == null || "".equals(cell.toString().trim())) {
				continue;
			}
//				cell.setCellType(CellType.STRING);
			String pn = MyUtil.getCellUppercaseString(cell);


			cell = row.getCell(3);
			if (cell == null || "".equals(cell.toString().trim())) {
				continue;
			}
			cell.setCellType(CellType.STRING);
			String strVal = cell.getStringCellValue();
			int i1 = strVal.indexOf(".");
			if (i1 > 0) {
				strVal = strVal.substring(0, i1);
			}
			int u8Cnt = Integer.parseInt(strVal);

			if (!pnToCnt.containsKey(pn)) {
				pnToCnt.put(pn, 0);
			}
			pnToCnt.put(pn, pnToCnt.get(pn) + u8Cnt);
		}
		return pnToCnt;
	}

	static void parseU8Data() {
		Map<String, Integer> pnToCnt = new HashMap<String, Integer>();
		Workbook wb = readExcel(U8_PATH);
		Sheet sheet = wb.getSheetAt(0);
		Row row;
		Cell cell;
		for (int i = 3; i < sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			cell = row.getCell(1);
			if (cell == null || "".equals(cell.toString().trim())) {
				continue;
			}
//				cell.setCellType(CellType.STRING);
			String pn = MyUtil.getCellUppercaseString(cell);
			allU8Pn.add(pn);

			cell = row.getCell(3);
			if (cell == null || "".equals(cell.toString().trim())) {
				continue;
			}
			cell.setCellType(CellType.STRING);
			String strVal = cell.getStringCellValue();
			int i1 = strVal.indexOf(".");
			if (i1 > 0) {
				strVal = strVal.substring(0, i1);
			}
			int u8Cnt = Integer.parseInt(strVal);

			if (!pnToCnt.containsKey(pn)) {
				pnToCnt.put(pn, 0);
			}
			pnToCnt.put(pn, pnToCnt.get(pn) + u8Cnt);
		}

		System.out.println(pnToStat.size());
		Set<Map.Entry<String, Integer>> entries = pnToCnt.entrySet();
		for (Map.Entry<String, Integer> entry : entries) {
			String pn = entry.getKey();
			StatPn statPn = pnToStat.get(pn);
			Integer value = entry.getValue();
			if (statPn == null) {
				if (value == 0) {
					continue;
				}
				statPn = new StatPn();
				statPn.setPn(pn);
				statPn.setU8Cnt(value);
				pnToStat.put(pn, statPn);
			} else {
				statPn.setU8Cnt(value);
			}
		}

		Map<String, Integer> pnToCntPre = getPreU8Stat();
		Set<Map.Entry<String, StatPn>> entries1 = pnToStat.entrySet();
		for (Map.Entry<String, StatPn> entry : entries1) {
			String pn = entry.getKey();
			StatPn statPn = entry.getValue();
			int preU8Cnt = 0;
			Integer preInt = pnToCntPre.get(pn);
			if (preInt != null) {
				preU8Cnt = preInt;
			}
			statPn.setU8ChangeCnt(statPn.getU8Cnt() - preU8Cnt);
		}
		System.out.println(pnToStat.size());
	}

	static final String[] HEAD_DESC = new String[]{"物料编码", "扫码数量", "自盘数量", "U8数量", "U8变动数量", "库位(扫码)", "库位(自盘)", "物料名称", "单价"};

	static void createStatistics() {
		List<StatPn> allStats = new ArrayList();
		List<StatPn> notInNCStats = new ArrayList();
		List<StatPn> inNCNoSerialStats = new ArrayList();

		Set<Map.Entry<String, StatPn>> entries = pnToStat.entrySet();
		for (Map.Entry<String, StatPn> entry : entries) {
			StatPn stat = entry.getValue();
			String pn = stat.getPn();
			if (StockParse.virtualPnSet.contains(pn)) {
				continue;
			}
			allStats.add(stat);
		}
		for (StatPn statPn : allStats) {
			String pn = statPn.getPn();
			int scanCnt = statPn.getScanCnt();
			int selfCheckCnt = statPn.getSelfCheckCnt();
			int u8Cnt = statPn.getU8Cnt();
			if (!pnToNCMaterial.containsKey(pn)) {
				notInNCStats.add(statPn);
			}else if(scanCnt > 0 && !pnToNCMaterial.get(pn).isSerial()){
				inNCNoSerialStats.add(statPn);
			}

		}
		createExcel(EXPORT_PATH_ALL, allStats);
		createExcel(EXPORT_PATH_NOT_IN_NC, notInNCStats);
		createExcel(EXPORT_PATH_IN_NC_NO_SERIAL, inNCNoSerialStats);
	}
	static void createImpStats() {
		List<StatPn> allStats = new ArrayList();
		Set<Map.Entry<String, StatPn>> entries = pnToStat.entrySet();
		for (Map.Entry<String, StatPn> entry : entries) {
			StatPn stat = entry.getValue();
			String pn = stat.getPn();
			if (StockParse.virtualPnSet.contains(pn)) {
				continue;
			}
			allStats.add(stat);
		}
		Map<String, OriginalBean> originalMap = WhParseStart.snUniMap;
		for (OriginalBean originalBean : originalMap.values()) {
			String pn = originalBean.getPn();
			if (StockParse.virtualPnSet.contains(pn)) {
				continue;
			}
		}
	}

	static void createExcel(String fileName, List<StatPn> statPns) {
		try {
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFCellStyle cellStyle = wb.createCellStyle();
			cellStyle.setWrapText(true);      //强制换行，关键的一句

			XSSFSheet sheet = wb.createSheet();
			sheet.setColumnWidth(5, 70 * 256);
			sheet.setColumnWidth(6, 70 * 256);
			int rowCnt = 0;
			XSSFRow row = sheet.createRow(rowCnt);
			XSSFCell cell = null;
			for (int i = 0; i < HEAD_DESC.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(HEAD_DESC[i]);
			}
			for (StatPn stat : statPns) {
				rowCnt++;
				row = sheet.createRow(rowCnt);
				row.createCell(0).setCellValue(stat.getPn());
				row.createCell(1).setCellValue(stat.getScanCnt());
				row.createCell(2).setCellValue(stat.getSelfCheckCnt());
				row.createCell(3).setCellValue(stat.getU8Cnt());
				row.createCell(4).setCellValue(stat.getU8ChangeCnt());
				cell = row.createCell(5);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(stat.getWh());
				cell = row.createCell(6);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(stat.getSelfCheckWh());
				row.createCell(7).setCellValue(stat.getpName());
				row.createCell(8).setCellValue(stat.getUnitPrice());
			}

			File file = new File(fileName);
			FileOutputStream os = new FileOutputStream(file);
			wb.write(os);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	static Set<String> getIsSerialPn(){
//		Set<String> retSet = new HashSet<String>();
//
//		Workbook wb = readExcel(PN_DOC_PATH);
//		Sheet sheet = wb.getSheetAt(0);
//		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//
//			Row row = sheet.getRow(i);
//			if (row == null) {
//				continue;
//			}
//
//			Cell cell = row.getCell(1);
//			if (MyUtil.cellIsNull(cell)) {
//				continue;
//			}
//			String pn = MyUtil.getCellUppercaseString(cell);
//			retSet.add(pn);
//		}
//
//		return retSet;
//	}

}
