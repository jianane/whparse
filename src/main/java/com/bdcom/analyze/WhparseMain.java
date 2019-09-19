package com.bdcom.analyze;

import com.bdcom.bean.*;
import com.bdcom.bean.ncimport.NCImpBody;
import com.bdcom.bean.ncimport.NCImpHeader;
import com.bdcom.util.MyUtil;
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

	static int SN_START_INDEX = 0;
	static String IMP_DATE = "2019-08-31";//com.bdcom.util.MyUtil.getTodayDate();
	final static String U8_ONLY_KW = "期初-差异"; // 66

	static Map<String, StatPn> pnToStat = new LinkedHashMap<>();

	static Map<NCImpHeader, List<NCImpBody>> headerToBodies = new HashMap<>();

	static Set<String> allScanPn = new HashSet<>();
	static Set<String> allU8Pn = new HashSet<>();
	static Set<String> allSelfCheckPn = new HashSet<>();

	static Map<String, Map<String, Integer>> pnTo_u8KbToCnt = new HashMap<>();

	static Map<String, NCMaterialDoc> pnToNCMaterial = new HashMap<>();


	final static String SELF_CHECK_PATH = "C:/Users/Administrator/Desktop/whscan/self_check/电子台账0831（关账汇总）.xlsx";
	final static String U8_PATH = "C:/Users/Administrator/Desktop/whscan/u8/现存量查询_2019.8.31.xls";
	final static String U8_PATH_Pre_Day = "C:/Users/Administrator/Desktop/whscan/u8/现存量查询_2019.08.29.xlsx";
	final static String PN_DOC_PATH = "C:/Users/Administrator/Desktop/whscan/pdoc/物料档案（序列号管理）_0829.xlsx";
	final static String NC_MATERIAL_DOC_PATH = "C:/Users/Administrator/Desktop/whscan/pdoc/物料档案20190902.xlsx";

	final static String EXPORT_PRE = "C:/Users/Administrator/Desktop/whscan/stat_jianan/0917/";
	final static String EXPORT_PATH_ALL = EXPORT_PRE + "all_jianan.xlsx";
	final static String EXPORT_PATH_OTHER = EXPORT_PRE + "other_jianan.xlsx";
	final static String EXPORT_PATH_NOT_IN_NC = EXPORT_PRE + "notInNC_jianan.xlsx";
	final static String EXPORT_PATH_IN_NC_NO_SERIAL = EXPORT_PRE + "inNCNoSerial_jianan.xlsx";

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
//		createImportStatistics();
	}

	public static Map<String, NCMaterialDoc> generateNCMaterial() {
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

			cell = row.getCell(5);
			String unit = MyUtil.getCellString(cell);
			maDoc.setUnit(unit);

			cell = row.getCell(6);
			String unitNo = MyUtil.getCellString(cell);
			maDoc.setUnitNo(unitNo);


			cell = row.getCell(7);
			if (!MyUtil.cellIsNull(cell) && "Y".equals(MyUtil.getCellString(cell))) {
				maDoc.setSerial(true);
			}

			pnToNCMaterial.put(pn, maDoc);

		}

		return pnToNCMaterial;

	}

	public static void statAddStock() {
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

			cell = row.getCell(0);// 生成U8物料编码下库别对应的数量
			String u8KbCode = MyUtil.getCellString(cell);
			Map<String, Integer> u8kbToCnt = pnTo_u8KbToCnt.get(pn);
			if (u8kbToCnt == null) {
				u8kbToCnt = new HashMap<String, Integer>();
				pnTo_u8KbToCnt.put(pn, u8kbToCnt);
			}
			if (!u8kbToCnt.containsKey(u8KbCode)) {
				u8kbToCnt.put(u8KbCode, 0);
			}
			u8kbToCnt.put(u8KbCode, u8kbToCnt.get(u8KbCode) + u8Cnt);

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
			} else if (scanCnt > 0 && !isSerial(pn)) {
				inNCNoSerialStats.add(statPn);
			}

		}

//		createExcel(EXPORT_PATH_OTHER, allStats);
		createExcel(EXPORT_PATH_ALL, allStats);
		createExcel(EXPORT_PATH_NOT_IN_NC, notInNCStats);
		createExcel(EXPORT_PATH_IN_NC_NO_SERIAL, inNCNoSerialStats);
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

	public static void createImportStatistics() {
		WhU8NcParse.parseKbu8ToNc();
		Map<String, String> u8KbToNcKb = WhU8NcParse.u8kbToNCkb;
		String orgNo = "01";
		List<StatPn> allStats = new ArrayList();
		Set<Map.Entry<String, StatPn>> entries = pnToStat.entrySet();
		for (Map.Entry<String, StatPn> entry : entries) {
			StatPn stat = entry.getValue();
			String pn = stat.getPn();
//			if (com.bdcom.analyze.StockParse.virtualPnSet.contains(pn)) {
//				continue;
//			}
			allStats.add(stat);
		}
		Map<String, OriginalBean> originalMap = WhParseStart.snUniMap;
		for (OriginalBean originalBean : originalMap.values()) {
			String pn = originalBean.getPn();
//			if (com.bdcom.analyze.StockParse.virtualPnSet.contains(pn)) {
//				continue;
//			}
			String sn = originalBean.getSn();
			String unit = getImpUnit(pn);

			String kw = originalBean.getKw();
			String kbCode = WhU8NcParse.getKbCode(pn, kw);
			if (kbCode == null) {
				System.out.println();
			}
			String impKw = getImpKw(pn, kw);

			NCImpBody body = new NCImpBody();
			body.setPn(pn);
			body.setUnit(unit);
			body.setCount("1");
			body.setImpDate(IMP_DATE);
			body.setKw(impKw);
			body.setSn(sn);
			body.setSnUnit(unit);

			setHeaderToBodies(orgNo, kbCode, body);
		}
		for (StatPn stat : allStats) {
			String pn = stat.getPn();
			int scanCnt = stat.getScanCnt();
			int selfCheckCnt = stat.getSelfCheckCnt();
			int u8Cnt = stat.getU8Cnt();
			if (scanCnt < selfCheckCnt || scanCnt < u8Cnt) {
				if (scanCnt == 0 && selfCheckCnt == 0) {// 找u8中的库别，并 入对应库别的虚拟库位

					Map<String, Integer> u8KbToCnt = pnTo_u8KbToCnt.get(pn);
					Set<Map.Entry<String, Integer>> entries1 = u8KbToCnt.entrySet();
					for (Map.Entry<String, Integer> entry : entries1) {
						String u8Kb = entry.getKey();
						Integer cnt = entry.getValue();
						String kbCode = u8KbToNcKb.get(u8Kb);

						String kw = U8_ONLY_KW;

						generateBody(pn, cnt, kw, orgNo, kbCode);

					}
				} else if (scanCnt == 0) {//  如果U8大，用U8的数量，库位借用自盘的
					if (selfCheckCnt >= u8Cnt) {
//						Map<String, Integer> kwToCnt = new HashMap<String, Integer>();
						String[] kw_cnts = stat.getSelfCheckWh().split(" ");
						for (String kwCnt : kw_cnts) {
							if ("".equals(kwCnt.trim())) {
								continue;
							}
							String[] kw_cnt = kwCnt.split("=");
							String kw = kw_cnt[0];
							int cnt = Integer.parseInt(kw_cnt[1]);

							generateBodyByRealKw(pn, cnt, kw, orgNo);
						}
					} else {
						int cnt = u8Cnt;
						String kw = getVirtualSnKw(pn, stat.getSelfCheckWh());

						generateBodyByRealKw(pn, cnt, kw, orgNo);
					}

				} else {// 库位借用扫码的
					int cnt = selfCheckCnt > u8Cnt ? selfCheckCnt : u8Cnt;
					cnt -= scanCnt;
					String kw = getVirtualSnKw(pn, stat.getWh());

					generateBodyByRealKw(pn, cnt, kw, orgNo);
				}
			}
		}


//		createImpExcelByVo();
		createImpExcelByVo10kSplit();

	}

	static String getVirtualSnKw(String pn, String whs) {
		String[] kw_cnts = whs.split(" ");
		String lessKw = null;
		String less30Kw = null;
		for (String kwCnt : kw_cnts) {
			if ("".equals(kwCnt.trim())) {
				continue;
			}
			String[] kw_cnt = kwCnt.split("=");
			String kw = kw_cnt[0];
			String kbCode = WhU8NcParse.getKbCode(pn, kw);
			if ("10".equals(kbCode)) {
				if (less30Kw == null) {
					less30Kw = kw;
				}
				less30Kw = less30Kw.compareTo(kw) < 0 ? less30Kw : kw;
			}
			if (lessKw == null) {
				lessKw = kw;
			}
			lessKw = lessKw.compareTo(kw) < 0 ? lessKw : kw;
		}
		return less30Kw == null ? lessKw : less30Kw;
	}

	static void generateBodyByRealKw(String pn, int cnt, String kw, String orgNo) {
		String kbCode = WhU8NcParse.getKbCode(pn, kw);
		if (kbCode == null) {
			System.out.println(pn + " -> " + kw);
		}
		String impKw = getImpKw(pn, kw);
		generateBody(pn, cnt, impKw, orgNo, kbCode);
	}


	static void generateBody(String pn, int cnt, String kw, String orgNo, String kbCode) {
		String unit = getImpUnit(pn);
		if (isSerial(pn)) {
			for (Integer i = 0; i < cnt; i++) {
				NCImpBody body = new NCImpBody();
				body.setPn(pn);
				body.setUnit(unit);
				body.setCount("1");
				body.setImpDate(IMP_DATE);
				body.setKw(kw);
				body.setSn(InventedSerialNumberUtil.getInventedSerialNumber(orgNo, kbCode, body.getKw(), SN_START_INDEX));
				body.setSnUnit(unit);

				setHeaderToBodies(orgNo, kbCode, body);
			}
		} else {
			NCImpBody body = new NCImpBody();
			body.setPn(pn);
			body.setUnit(unit);
			body.setCount(cnt + "");
			body.setImpDate(IMP_DATE);
			body.setKw(kw);
//			body.setSn(com.bdcom.analyze.InventedSerialNumberUtil.getInventedSerialNumber(orgNo, kbCode, body.getKw(), SN_START_INDEX));
//			body.setSnUnit(unit);

			setHeaderToBodies(orgNo, kbCode, body);
		}
	}

	static void setHeaderToBodies(String orgNo, String kbCode, NCImpBody body) {
		if (kbCode == null) {
			throw new NullPointerException();
		}
		NCImpHeader header = NCImpHeader.getHeader(orgNo, kbCode);
		List<NCImpBody> impBodies = headerToBodies.get(header);
		if (impBodies == null) {
			impBodies = new ArrayList<NCImpBody>();
			headerToBodies.put(header, impBodies);
		}
		impBodies.add(body);
	}

	static void createImpExcelByVo() {
		Map<String, String> u8kbToImpPath = new HashMap<String, String>();
		u8kbToImpPath.put("10", "C:/Users/Administrator/Desktop/whscan/博达303186Serial期初数据/盘库30期初.xlsx");
		u8kbToImpPath.put("11", "C:/Users/Administrator/Desktop/whscan/博达303186Serial期初数据/盘库31期初.xlsx");
		u8kbToImpPath.put("13", "C:/Users/Administrator/Desktop/whscan/博达303186Serial期初数据/盘库86期初.xlsx");
		Set<Map.Entry<NCImpHeader, List<NCImpBody>>> entries1 = headerToBodies.entrySet();
		long id = 0;
		for (Map.Entry<NCImpHeader, List<NCImpBody>> entry : entries1) {
			NCImpHeader header = entry.getKey();
			List<NCImpBody> bodies = entry.getValue();
			String path = u8kbToImpPath.get(header.getKb());

			header.setId(1l);
			header.setImpDate(IMP_DATE);

			for (NCImpBody body : bodies) {
				body.setId(++id);
				body.setHeadId(1l);
			}

			GenerateImportData.createImpExcel(path, header, bodies);
		}
	}

	static void createImpExcelByVo10kSplit() {
		int splitCnt = 20000;
		Map<String, String> u8kbToImpPath = new HashMap<String, String>();
		u8kbToImpPath.put("10", "C:/Users/Administrator/Desktop/whscan/博达303186Serial期初数据/30/盘库30期初_");
		u8kbToImpPath.put("11", "C:/Users/Administrator/Desktop/whscan/博达303186Serial期初数据/31/盘库31期初_");
		u8kbToImpPath.put("13", "C:/Users/Administrator/Desktop/whscan/博达303186Serial期初数据/86/盘库86期初_");
		Set<Map.Entry<NCImpHeader, List<NCImpBody>>> entries1 = headerToBodies.entrySet();
		long id = 0;
		List<NCImpBody> splitBodyList = new ArrayList<NCImpBody>();
		for (Map.Entry<NCImpHeader, List<NCImpBody>> entry : entries1) {
			NCImpHeader header = entry.getKey();
			List<NCImpBody> bodies = entry.getValue();
			String path = u8kbToImpPath.get(header.getKb());

			header.setId(1l);
			header.setImpDate(IMP_DATE);

			int cnt = 0;
			int fileCnt = 0;
			for (NCImpBody body : bodies) {

				body.setId(++id);
				body.setHeadId(1l);

				cnt++;
				splitBodyList.add(body);
				if (cnt % splitCnt == 0) {
					fileCnt++;
					GenerateImportData.createImpExcel(path + fileCnt + ".xlsx", header, splitBodyList);
					splitBodyList.clear();
				}
			}
			if (splitBodyList.size() > 0) {
				fileCnt++;
				GenerateImportData.createImpExcel(path + fileCnt + ".xlsx", header, splitBodyList);
				splitBodyList.clear();
			}
		}
	}

	static String getUnit(String pn) {
		return pnToNCMaterial.get(pn).getUnit();
	}

	static String getUnitNo(String pn) {
		return pnToNCMaterial.get(pn).getUnitNo();
	}

	static boolean isSerial(String pn) {
		return pnToNCMaterial.get(pn).isSerial();
	}

	static String getImpUnit(String pn) {
		return getUnitNo(pn);
//		return getUnit(pn);
	}

	static String getImpKw(String pn, String kw) {
		String impKw = WhU8NcParse.getKwName(pn, kw);
//		String impKw = com.bdcom.analyze.WhU8NcParse.getKwCode(pn, kw);
		return impKw;
	}


}
