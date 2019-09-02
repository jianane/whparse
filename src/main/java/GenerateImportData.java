import bean.NCMaterialDoc;
import bean.ncimport.NCImpBody;
import bean.ncimport.NCImpHeader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * @author Jianan
 * @Date
 */
public class GenerateImportData {

	static String JIECE_QICHU_PATH = "C:/Users/Administrator/Desktop/whscan/借测期初/借测期初.xlsx";
	static String IMP_JIECE_QICHU_PATH = "C:/Users/Administrator/Desktop/whscan/借测期初/NC借测期初_Imp.xlsx";

	static String ONLINE_QICHU_PATH = "C:/Users/Administrator/Desktop/whscan/在线期初/生产工单待出物料汇总20190901.xls";
	static String IMP_ONLINE_QICHU_PATH = "C:/Users/Administrator/Desktop/whscan/在线期初/NC在线期初_Imp.xlsx";

	static String SHIPPED_DATA_PATH = "C:/Users/Administrator/Desktop/whscan/已发货未开票期初/已发货未开票期初.xlsx";
	static String WORKBOOK_TEMPLATE_PATH = "C:/Users/Administrator/Desktop/whscan/已发货未开票期初/workboot_template.xlsx";
	static String IMP_SHIPPED_BDCOM_PATH = "C:/Users/Administrator/Desktop/whscan/已发货未开票期初/NC已发货未开票_博达_Imp.xlsx";
	static String IMP_SHIPPED_TEC_PATH = "C:/Users/Administrator/Desktop/whscan/已发货未开票期初/NC已发货未开票_科技_Imp.xlsx";

	static String IMP_DATE = MyUtil.getTodayDate();

	static Map<String, String> pnToCnt = new HashMap<String, String>();

	static Map<String, Integer> jiecePnToCnt = new HashMap<String, Integer>();

	static void generateJieceData() {
		Map<String, NCMaterialDoc> pnToNCMaterial = WhparseMain.generateNCMaterial();

		Workbook wb = WhparseMain.readExcel(JIECE_QICHU_PATH);
		int sheetNum = wb.getNumberOfSheets();
		for (int i = 0; i < sheetNum; i++) {
			Sheet sheet = wb.getSheetAt(i);
			for (int j = 1; j < sheet.getLastRowNum(); j++) {
				Row row = sheet.getRow(j);

				Cell cell = row.getCell(0);
				String pn = MyUtil.getCellString(cell);

				cell = row.getCell(1);
				String cntStr = MyUtil.getStringTypeCell(cell);
				int cnt = Integer.parseInt(cntStr);
				if (cnt == 0) {
					continue;
				}
				if (jiecePnToCnt.containsKey(pn)) {
					jiecePnToCnt.put(pn, jiecePnToCnt.get(pn) + cnt);
				} else {
					jiecePnToCnt.put(pn, cnt);
				}
			}
		}

		System.out.println(jiecePnToCnt.size());
		NCImpHeader header = new NCImpHeader();
		header.setId(1l);
		header.setImpDate(IMP_DATE);
		header.setNcOrg("01");
		header.setKb("66");
		Set<Map.Entry<String, Integer>> entries = jiecePnToCnt.entrySet();
		List<NCImpBody> impBodies = new ArrayList<NCImpBody>();
		int serialSum = 0;
		for (Map.Entry<String, Integer> entry : entries) {
			String pn = entry.getKey();
			Integer count = entry.getValue();
			NCMaterialDoc mDoc = pnToNCMaterial.get(pn);
			String unit = mDoc.getUnitNo();
			if (mDoc.isSerial()) {
				serialSum++;
				for (Integer i = 0; i < count; i++) {
					NCImpBody body = new NCImpBody();
					body.setHeadId(1l);
					body.setPn(pn);
					body.setUnit(unit);
					body.setCount("1");
					body.setKw("01");
					body.setImpDate(IMP_DATE);
					body.setSn(InventedSerialNumberUtil.getInventedSerialNumber(header.getNcOrg(), header.getKb(), body.getKw()));
					body.setSnUnit(unit);

					impBodies.add(body);
				}
			} else {
				NCImpBody body = new NCImpBody();
				body.setHeadId(1l);
				body.setPn(pn);
				body.setUnit(unit);
				body.setCount(count + "");
				body.setKw("01");
				body.setImpDate(IMP_DATE);

				impBodies.add(body);
			}
		}


		for (int i = 0; i < impBodies.size(); i++) {
			impBodies.get(i).setId(i + 1l);
		}
		System.out.println(serialSum);
		createImpExcel(IMP_JIECE_QICHU_PATH, header, impBodies);
	}

	static void generateOnLineData() {
		Map<String, NCMaterialDoc> pnToNCMaterial = WhparseMain.generateNCMaterial();

		Workbook wb = WhparseMain.readExcel(ONLINE_QICHU_PATH);
		int sheetNum = wb.getNumberOfSheets();
//		for (int i = 0; i < sheetNum; i++) {
		Sheet sheet = wb.getSheetAt(0);
		for (int j = 4; j < sheet.getLastRowNum(); j++) {
			Row row = sheet.getRow(j);

			Cell cell = row.getCell(0);
			String pn = MyUtil.getCellString(cell);

			cell = row.getCell(2);
			String cntStr = MyUtil.getStringTypeCell(cell);
			pnToCnt.put(pn, cntStr);
		}
//		}

		System.out.println(pnToCnt.size());
		NCImpHeader header = new NCImpHeader();
		header.setId(1l);
		header.setImpDate(IMP_DATE);
		header.setNcOrg("01");
		header.setKb("66");
		Set<Map.Entry<String, String>> entries = pnToCnt.entrySet();
		List<NCImpBody> impBodies = new ArrayList<NCImpBody>();
		for (Map.Entry<String, String> entry : entries) {
			String pn = entry.getKey();
			String count = entry.getValue();
			NCMaterialDoc mDoc = pnToNCMaterial.get(pn);
			if (mDoc == null) {
				System.out.println(pn);
				continue;
			}
			String unit = mDoc.getUnitNo();
			if (mDoc.isSerial()) {
				for (Integer i = 0; i < Integer.valueOf(count); i++) {
					NCImpBody body = new NCImpBody();
					body.setHeadId(1l);
					body.setPn(pn);
					body.setUnit(unit);
					body.setCount("1");
					body.setKw("在线期初");
					body.setImpDate("2019-08-31");
					body.setSn(InventedSerialNumberUtil.getInventedSerialNumber(header.getNcOrg(), header.getKb(), body.getKw(), 89000) );
					body.setSnUnit(unit);

					impBodies.add(body);
				}
			} else {
				NCImpBody body = new NCImpBody();
				body.setHeadId(1l);
				body.setPn(pn);
				body.setUnit(unit);
				body.setCount(count);
				body.setKw("在线期初");
				body.setImpDate("2019-08-31");

				impBodies.add(body);
			}
		}


		for (int i = 0; i < impBodies.size(); i++) {
			impBodies.get(i).setId(i + 1l);
		}
		createImpExcel(IMP_ONLINE_QICHU_PATH, header, impBodies);
	}

	static void generateShippedData() {
		Map<String, NCMaterialDoc> pnToNCMaterial = WhparseMain.generateNCMaterial();
		Map<String, Integer> bdcomPnToCnt = new HashMap<String, Integer>();
		Map<String, Integer> tecPnToCnt = new HashMap<String, Integer>();

		Workbook wb = WhparseMain.readExcel(SHIPPED_DATA_PATH);
		int sheetNum = wb.getNumberOfSheets();
//		for (int i = 0; i < sheetNum; i++) {
		Sheet sheet = wb.getSheetAt(0);
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);

			Cell cell = row.getCell(0);
			if (MyUtil.cellIsNull(cell)) {
				continue;
			}
			String pn = MyUtil.getCellString(cell);

			cell = row.getCell(2);
			if (!MyUtil.cellIsNull(cell)) {
				String cntStr = MyUtil.getStringTypeCell(cell);
				int cnt = Integer.parseInt(cntStr);
				if (cnt > 0) {
					if (bdcomPnToCnt.containsKey(pn)) {
						bdcomPnToCnt.put(pn, bdcomPnToCnt.get(pn) + cnt);
					} else {
						bdcomPnToCnt.put(pn, cnt);
					}
				}
			}

			cell = row.getCell(3);
			if (!MyUtil.cellIsNull(cell)) {
				String cntStr = MyUtil.getStringTypeCell(cell);
				int cnt = Integer.parseInt(cntStr);
				if (cnt > 0) {
					if (tecPnToCnt.containsKey(pn)) {
						tecPnToCnt.put(pn, tecPnToCnt.get(pn) + cnt);
					} else {
						tecPnToCnt.put(pn, cnt);
					}
				}
			}


		}

		System.out.println(bdcomPnToCnt.size());
		System.out.println(tecPnToCnt.size());
		createExcelByShippedMap(bdcomPnToCnt, pnToNCMaterial, IMP_SHIPPED_BDCOM_PATH);
		createExcelByShippedMap(tecPnToCnt, pnToNCMaterial, IMP_SHIPPED_TEC_PATH);



	}
	static void createExcelByShippedMap(Map<String, Integer> pnToCnt, Map<String, NCMaterialDoc> pnToNCMaterial, String impPath){
		NCImpHeader header = new NCImpHeader();
		header.setId(1l);
		header.setImpDate(IMP_DATE);
		header.setNcOrg("01");
		header.setKb("66");
		Set<Map.Entry<String, Integer>> entries = pnToCnt.entrySet();
		List<NCImpBody> impBodies = new ArrayList<NCImpBody>();
		int serialSum = 0;
		for (Map.Entry<String, Integer> entry : entries) {
			String pn = entry.getKey();
			Integer count = entry.getValue();
			NCMaterialDoc mDoc = pnToNCMaterial.get(pn);
			String unit = mDoc.getUnitNo();
			if (mDoc.isSerial()) {
				serialSum++;
				for (Integer i = 0; i < count; i++) {
					NCImpBody body = new NCImpBody();
					body.setHeadId(1l);
					body.setPn(pn);
					body.setUnit(unit);
					body.setCount("1");
					body.setKw("01");
					body.setImpDate(IMP_DATE);
					body.setSn(InventedSerialNumberUtil.getInventedSerialNumber(header.getNcOrg(), header.getKb(), body.getKw(), 2000));
					body.setSnUnit(unit);

					impBodies.add(body);
				}
			} else {
				NCImpBody body = new NCImpBody();
				body.setHeadId(1l);
				body.setPn(pn);
				body.setUnit(unit);
				body.setCount(count + "");
				body.setKw("01");
				body.setImpDate(IMP_DATE);

				impBodies.add(body);
			}
		}
		for (int i = 0; i < impBodies.size(); i++) {
			impBodies.get(i).setId(i + 1l);
		}
		System.out.println(serialSum);
		createImpExcel(impPath, header, impBodies);
	}


	static void createImpExcel(String fileName, NCImpHeader header, List<NCImpBody> bodies) {
		try {
			Workbook wb = WhparseMain.readExcel(WORKBOOK_TEMPLATE_PATH);

			Sheet sheet = wb.getSheetAt(0);
			Row row;
			row = sheet.createRow(2);
			row.createCell(0).setCellValue(header.getId());
			row.createCell(1).setCellValue(header.getNcOrg());
			row.createCell(2).setCellValue(header.getImpDate());
			row.createCell(3).setCellValue(header.getKb());
			row.createCell(4).setCellValue(header.getType());
//			row = sheet.getRow(3);
//			row.

			int rowCnt = 4;
			for (NCImpBody stat : bodies) {
				rowCnt++;
				row = sheet.createRow(rowCnt);
				row.createCell(0).setCellValue(stat.getHeadId());
				row.createCell(1).setCellValue(stat.getId());
				row.createCell(2).setCellValue(stat.getPn());
				row.createCell(3).setCellValue(stat.getUnit());
				row.createCell(4).setCellValue(stat.getCount());
				row.createCell(5).setCellValue(stat.getImpDate());
				row.createCell(6).setCellValue(stat.getKw());

				if (stat.getSn() != null && !"".equals(stat.getSn())) {
					row.createCell(7).setCellValue(stat.getSn());
					row.createCell(8).setCellValue(stat.getUnit());
				}
				System.out.println(rowCnt);
			}

			File file = new File(fileName);
			FileOutputStream os = new FileOutputStream(file);
			wb.write(os);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
//		generateJieceData();
		generateOnLineData();
//		generateShippedData();
	}


}
