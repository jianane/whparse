package com.bdcom.analyze;

import com.bdcom.bean.OriginalBean;
import com.bdcom.util.MyUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

/**
 * @author Jianan
 * @Date
 */
public class WhParseStart {

	public static final String ERR_TO_RIGHT_PN = "C:/Users/Administrator/Desktop/whscan/ErrToRight/物料档案比对.xls";

	public static Map<String, OriginalBean> snUniMap = new HashMap<String, OriginalBean>();

	static List<String> fileList = new ArrayList<String>();

	static Map<String, List<OriginalBean>> fileSheetToOrigin = new LinkedHashMap<String, List<OriginalBean>>();
	static Map<String, List<OriginalBean>> fileToOrigin = new LinkedHashMap<String, List<OriginalBean>>();


	static List<OriginalBean> allOriginalBean = new ArrayList<OriginalBean>();


	static List<String> pnSheets = new ArrayList<String>();//size / sheet
	static List<String> pnSheetUnis = new ArrayList<String>();// uni size / sheet
	static List<String> pnIsNull = new ArrayList<String>();
	static int sum;

	public static Map<String, OriginalBean> parseScanData() {

		List<String> fileList = travFolder("C:/Users/Administrator/Desktop/whscan/扫描所有库位信息");
		for (String fileName : fileList) {
			readExcel(fileName);
		}

		parseInData();

		Set<Map.Entry<String, List<OriginalBean>>> entries = fileSheetToOrigin.entrySet();
		for (Map.Entry<String, List<OriginalBean>> entry : entries) {
			allOriginalBean.addAll(entry.getValue());
			HashSet<String> snSheetSet = new HashSet<String>();
			for (OriginalBean originalBean : entry.getValue()) {
				snSheetSet.add(originalBean.getSn());
			}
			sum += entry.getValue().size();
			pnSheets.add(entry.getKey() + " -> " + entry.getValue().size());

			pnSheetUnis.add(entry.getKey() + " -> " + snSheetSet.size() + " / " + entry.getValue().size());

		}


//		System.out.println("----------------------PnIsNull---------------------------");
//		for (String pn : pnIsNull) {
//			System.out.println(pn);
//		}
//		System.out.println("----------------------ALL---------------------------");
//		for (String pnSheet : pnSheets) {
//			System.out.println(pnSheet);
//		}
//		System.out.println(sum);
//		System.out.println("----------------------Sheet UNIQ---------------------------");
//		for (String pnSheetUni : pnSheetUnis) {
//			System.out.println(pnSheetUni);
//		}

		Map<String, String> sameSnToPn = getSameSnToPn();
		for (OriginalBean originalBean : allOriginalBean) {
			String sn = originalBean.getSn();
			String pn = originalBean.getPn();
			if ("XKSWI-SWI2021C".equals(pn) || "CBNNN-JHJ0151A".equals(pn) ) {
				continue;
			}
			if ("XKPON-PON0359A".equals(pn)) {
				System.out.println();
			}

			if (sameSnToPn.containsKey(sn)) {
				if (!sameSnToPn.get(sn).equals(pn)) {
					continue;
				}
			}
			if (MyUtil.isContainChinese(sn)) {
				continue;
			}

			OriginalBean existOriginal = snUniMap.get(sn);
			if (existOriginal != null &&
					existOriginal.getPn().equals(pn) &&
					existOriginal.getKw().compareTo(originalBean.getKw()) <= 0) {

				continue;

			}
			snUniMap.put(sn, originalBean);
		}
		System.out.println(snUniMap.size() + " / " + allOriginalBean.size());

//		System.out.println("----------------------File UNIQ---------------------------");
//
//		List<String> pnFileUnis = new ArrayList<String>();// uni size / sheet
//		Set<Map.Entry<String, List<OriginalBean>>> entries1 = fileToOrigin.entrySet();
//		for (Map.Entry<String, List<OriginalBean>> entry : entries1) {
//			HashSet<String> snFileSet = new HashSet<String>();
//			for (OriginalBean originalBean : entry.getValue()) {
//				snFileSet.add(originalBean.getSn());
//			}
//			pnFileUnis.add(entry.getKey() + " -> " + snFileSet.size() + " / " + entry.getValue().size());
//		}
//		for (String pnFileUni : pnFileUnis) {
//			System.out.println(pnFileUni);
//		}

		parseOutData();

		rectifyPn();

		System.out.println("**************************** UNIQ SN : " + snUniMap.size() + "*******************************");
		return snUniMap;
	}

	static Map<String, String> getSameSnToPn(){
		Map<String, String> snToPn = new HashMap<String, String>();
		Workbook wb = MyUtil.readExcel("C:/Users/Administrator/Desktop/whscan/Pn_Sn/重复的SN对应的物料编码(2).xlsx");
		Sheet sheet = wb.getSheetAt(0);
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row == null) {
				continue;
			}

			Cell cell = row.getCell(0);
			if (MyUtil.cellIsNull(cell)) {
				continue;
			}
			String sn = MyUtil.getCellString(cell);

			cell = row.getCell(2);
			if (MyUtil.cellIsNull(cell)) {
				continue;
			}
			String pn = MyUtil.getCellUppercaseString(cell);
			snToPn.put(sn, pn);
		}

		return snToPn;
	}

	static void rectifyPn(){
		Map<String, String> errToRight = new HashMap<String, String>();

		Workbook wb = MyUtil.readExcel(ErrToRightPnParse.ERR_TO_RIGHT_PATH);
		Sheet sheet = wb.getSheetAt(0);
		Cell cell;
		Row row;
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}

			cell = row.getCell(0);
			if (MyUtil.cellIsNull(cell)) {
				continue;
			}
			String errPn = MyUtil.getCellUppercaseString(cell);

			cell = row.getCell(1);
			if (MyUtil.cellIsNull(cell)) {
				continue;
			}
			String rightPn = MyUtil.getCellUppercaseString(cell);

			errToRight.put(errPn, rightPn);

//			cell = row.getCell(5);
//			if (cell == null || "".equals(cell.toString().toUpperCase().trim())) {
//				continue;
//			}
//			cell.setCellType(CellType.STRING);
//			String newPn = cell.toString().toUpperCase().trim();
//
//			cell = row.getCell(1);
//			if(cell != null && !"".equals(cell.toString().trim())){
//				cell.setCellType(CellType.STRING);
//				String pn = cell.toString().toUpperCase().trim();
//				pnToNew.put(pn, newPn);
//			}


		}
		for (OriginalBean original : snUniMap.values()) {
			String pn = original.getPn();
			if(errToRight.containsKey(pn)){
				original.setPn(errToRight.get(pn));
			}
		}

	}

	public static Set<String> getErrorPn(){
		Set<String> errorPnSet = new HashSet<String>();

		Workbook wb = MyUtil.readExcel(ERR_TO_RIGHT_PN);
		Sheet sheet = wb.getSheetAt(0);
		Cell cell;
		Row row;

		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			cell = row.getCell(5);
			if (cell != null && !"".equals(cell.toString().toUpperCase().trim())) {
				continue;
			}
			cell = row.getCell(2);
			if (cell == null || "Y".equals(cell.toString().toUpperCase().trim())) {
				continue;
			}

			cell = row.getCell(1);
			if(cell != null && !"".equals(cell.toString().trim())){
				cell.setCellType(CellType.STRING);
				String pn = cell.toString().toUpperCase().trim();
				errorPnSet.add(pn);
			}

		}
		return errorPnSet;
	}

	public static void traverseFolder(String path) {

		File file = new File(path);
		if (file.exists()) {
			File[] files = file.listFiles();
			if (null == files || files.length == 0) {
				System.out.println("文件夹是空的!");
				return;
			} else {
				for (File file2 : files) {
					if (file2.isDirectory()) {
						traverseFolder(file2.getAbsolutePath());
					} else {
						fileList.add(file2.getAbsolutePath());
					}
				}
			}
		} else {
			System.out.println("文件不存在!");
		}
	}

	public static List<String> travFolder(String path) {
		fileList.clear();
		traverseFolder(path);
		return fileList;
	}

	public static List<Map<String, String>> readExcel(String filePath) {
		Sheet sheet = null;
		Row row = null;
		List<Map<String, String>> list = null;
		String cellData = null;
		Workbook wb = null;
		if (filePath == null) {
			return null;
		}
		String extString = filePath.substring(filePath.lastIndexOf("."));
		InputStream is = null;
		try {
			is = new FileInputStream(filePath);
			if (".xls".equals(extString)) {
				wb = new HSSFWorkbook(is);
			} else if (".xlsx".equals(extString)) {
				wb = new XSSFWorkbook(is);
			} else {
				wb = null;
			}
			if (wb != null) {
				int sheetNum = wb.getNumberOfSheets();
				List<OriginalBean> fileOriginalBeans = new ArrayList<OriginalBean>();
				for (int i = 0; i < sheetNum; i++) {
					sheet = wb.getSheetAt(i);
					try {
						String sheetStart = sheet.getRow(1).getCell(0).toString().toUpperCase().trim();
						if ("".equals(sheetStart)) {
							continue;
						}
					} catch (Exception e) {
						continue;
					}


					List<OriginalBean> originalBeans = new ArrayList<OriginalBean>();
					String kw = null;
					String pn = null;
					for (int i1 = 1; i1 <= sheet.getLastRowNum(); i1++) {
						row = sheet.getRow(i1);
						if (row == null) {
							continue;
						}
						boolean newKw = false;
						Cell cell = row.getCell(0);
						if (cell != null && !"".equals(cell.toString().toUpperCase().trim())) {
							cell.setCellType(CellType.STRING);
							kw = cell.toString().toUpperCase().trim();
							newKw = true;
							pn = null;
						}
						cell = row.getCell(1);
						if (cell != null && !"".equals(cell.toString().toUpperCase().trim())) {
							cell.setCellType(CellType.STRING);
							pn = cell.toString().toUpperCase().trim();
							if (pn.startsWith("PN:")) {
								pn = pn.substring(3, pn.length());
							}
							if (pn.startsWith("PN：")) {
								pn = pn.substring(3, pn.length());
							}
						}
						if (pn == null) {
							if (newKw) {

								pnIsNull.add(filePath + " -> " + sheet.getSheetName() + " -- " + (i1 + 1) + " ->" + kw);
							}
							continue;
						}
						cell = row.getCell(2);
						if (cell == null || "".equals(cell.toString().toUpperCase().trim())) {
							continue;
						}
						cell.setCellType(CellType.STRING);
						String sn = cell.toString().toUpperCase().trim();
						List<String> sns = QrCodeParser.parse(sn);
						for (String snStr : sns) {
							OriginalBean original = new OriginalBean();
							original.setKw(kw);
							original.setPn(pn);
							original.setSn(snStr);
							original.setSheet(sheet.getSheetName());
							original.setFile(filePath);
							originalBeans.add(original);
						}
					}
					fileSheetToOrigin.put(filePath + " -> " + sheet.getSheetName(), originalBeans);
					fileOriginalBeans.addAll(originalBeans);
				}
				fileToOrigin.put(filePath + " -> ", fileOriginalBeans);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	static void parseInData() {
		parseInDataCol3();
		parseInDataCol4();
	}
	static void parseInDataCol3() {
		List<String> fileList = WhParseStart.travFolder("C:/Users/Administrator/Desktop/whscan/每日出入库扫码记录/入库扫码记录/column3");
		for (String fileName : fileList) {
			Workbook wb = MyUtil.readExcel(fileName);
			Sheet sheet = null;
			Row row = null;
			int sheetNum = wb.getNumberOfSheets();
			List<OriginalBean> fileOriginalBeans = new ArrayList<OriginalBean>();
			for (int i = 0; i < sheetNum; i++) {
				sheet = wb.getSheetAt(i);
				try {
					String sheetStart = sheet.getRow(1).getCell(0).toString().toUpperCase().trim();
					if ("".equals(sheetStart)) {
						continue;
					}
				} catch (Exception e) {
					continue;
				}


				List<OriginalBean> originalBeans = new ArrayList<OriginalBean>();
				String kw = null;
				String pn = null;
				List<OriginalBean> nullKwOriginals = new ArrayList<OriginalBean>();
				for (int i1 = 1; i1 <= sheet.getLastRowNum(); i1++) {
					row = sheet.getRow(i1);
					if (row == null) {
						continue;
					}
					Cell cell = row.getCell(0);
					if (cell != null && !"".equals(cell.toString().toUpperCase().trim())) {
						cell.setCellType(CellType.STRING);
						pn = cell.toString().toUpperCase().trim();
						if (pn.startsWith("PN:")) {
							pn = pn.substring(3, pn.length());
						}
						if (pn.startsWith("PN：")) {
							pn = pn.substring(3, pn.length());
						}
					}
					cell = row.getCell(2);
					if (cell != null && !"".equals(cell.toString().toUpperCase().trim())) {
						cell.setCellType(CellType.STRING);
						kw = cell.toString().toUpperCase().trim();
						int colonIndex = kw.indexOf(QrCodeParser.colon1);
						if (colonIndex > 0) {
							kw = kw.substring(0, colonIndex);
						}else if( (colonIndex = kw.indexOf(QrCodeParser.colon2)) > 0){
							kw = kw.substring(0, colonIndex);
						}
					}else {
						kw = null;
					}
					cell = row.getCell(1);
					if (cell == null || "".equals(cell.toString().toUpperCase().trim())) {
						continue;
					}
					cell.setCellType(CellType.STRING);
					String sn = cell.toString().toUpperCase().trim();
					List<String> sns = QrCodeParser.parse(sn);
					for (String snStr : sns) {
						OriginalBean original = new OriginalBean();
						original.setKw(kw);
						original.setPn(pn);
						original.setSn(snStr);
						original.setSheet(sheet.getSheetName());
						original.setFile(fileName);

						if (kw == null) {
							nullKwOriginals.add(original);
						}else {
							for (OriginalBean nullKwOriginal : nullKwOriginals) {
								nullKwOriginal.setKw(kw);
							}
							nullKwOriginals.clear();
						}

						originalBeans.add(original);
					}
				}
				fileSheetToOrigin.put(fileName + " -> " + sheet.getSheetName(), originalBeans);
				fileOriginalBeans.addAll(originalBeans);
			}
			fileToOrigin.put(fileName + " -> ", fileOriginalBeans);
		}
	}
	static void parseInDataCol4(){
		List<String> fileList = travFolder("C:/Users/Administrator/Desktop/whscan/每日出入库扫码记录/入库扫码记录/column4");
		for (String fileName : fileList) {
			readExcel(fileName);
		}
	}

	static void parseOutData() {
		List<String> fileList = WhParseStart.travFolder("C:/Users/Administrator/Desktop/whscan/每日出入库扫码记录/出库扫码记录");
		Set<String> snSet = new HashSet<String>();
		for (String fileName : fileList) {
			Workbook wb = MyUtil.readExcel(fileName);
			int sheetNum = wb.getNumberOfSheets();
			for (int i = 0; i < sheetNum; i++) {
				Sheet sheet = wb.getSheetAt(i);
				try {
					String sheetStart = sheet.getRow(1).getCell(2).toString().toUpperCase().trim();
					if ("".equals(sheetStart)) {
						continue;
					}
				} catch (Exception e) {
					continue;
				}

				for (int i1 = 1; i1 <= sheet.getLastRowNum(); i1++) {
					Row row = sheet.getRow(i1);
					try {
						Cell cell = row.getCell(2);
						cell.setCellType(CellType.STRING);
						String sn = cell.toString().toUpperCase().trim();
						List<String> sns = QrCodeParser.parse(sn);
						for (String s : sns) {
							snSet.add(s);
						}
					} catch (Exception e) {
//						System.out.println(e.getMessage());
					}

				}

			}
		}
		System.out.println("SN UNIQ Count -> " + snUniMap.size());
		System.out.println("out count -> " + snSet.size());
		for (String sn : snSet) {
//			System.out.println(sn);
			if (snUniMap.containsKey(sn)) {
				snUniMap.remove(sn);
			}
		}
		System.out.println("SN UNIQ Count -> " + snUniMap.size());
	}

	public static void main(String[] args) {
//		parseScanData();
		rectifyPn();
	}
}
