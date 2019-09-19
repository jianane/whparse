package com.bdcom.analyze;

import com.bdcom.analyze.GenerateImportData;
import com.bdcom.bean.NCMaterialDoc;
import com.bdcom.bean.ncimport.NCImpBody;
import com.bdcom.bean.ncimport.NCImpHeader;
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
public class GenerateOtherRealData {
	static String IMP_DATE = "2019-08-31";//com.bdcom.util.MyUtil.getTodayDate();
	static Map<String, Integer> pnToCnt = new HashMap<String, Integer>();
	static String KEJI_DATA_PATH = "C:/Users/Administrator/Desktop/whscan/子公司期初/科技真实库存.XLSX";
	static String IMP_KEJI_DATA_PATH = "C:/Users/Administrator/Desktop/whscan/子公司期初/IMP_科技真实库存.XLSX";
	
	static String XUNKUN_DATA_PATH = "C:/Users/Administrator/Desktop/whscan/子公司期初/迅坤真实库存.XLSX";
	static String IMP_XUNKUN_DATA_PATH = "C:/Users/Administrator/Desktop/whscan/子公司期初/IMP_迅坤真实库存.XLSX";

	static void generateData(String srcFilePath, String impPath, String ncOrg) {
		pnToCnt.clear();
		Map<String, NCMaterialDoc> pnToNCMaterial = WhparseMain.generateNCMaterial();

		Workbook wb = MyUtil.readExcel(srcFilePath);
		Sheet sheet = wb.getSheetAt(1);
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);

			Cell cell = row.getCell(4);
			String pn = MyUtil.getCellString(cell);

			cell = row.getCell(8);
			String cntStr = MyUtil.getStringTypeCell(cell);
			int cnt = Integer.parseInt(cntStr);
			if (!pnToCnt.containsKey(pn)) {
				pnToCnt.put(pn, 0);
			}
			pnToCnt.put(pn, pnToCnt.get(pn) + cnt);
		}

		NCImpHeader header = new NCImpHeader();
		header.setId(1l);
		header.setImpDate(IMP_DATE);
		header.setNcOrg(ncOrg);
		header.setKb("10");
		Set<Map.Entry<String, Integer>> entries = pnToCnt.entrySet();
		List<NCImpBody> impBodies = new ArrayList<NCImpBody>();
		for (Map.Entry<String, Integer> entry : entries) {
			String pn = entry.getKey();
			Integer count = entry.getValue();
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
					body.setKw("成品");
					body.setImpDate(IMP_DATE);
					body.setSn(InventedSerialNumberUtil.getInventedSerialNumber(header.getNcOrg(), header.getKb(), body.getKw()) );
					body.setSnUnit(unit);

					impBodies.add(body);
				}
			} else {
				NCImpBody body = new NCImpBody();
				body.setHeadId(1l);
				body.setPn(pn);
				body.setUnit(unit);
				body.setCount(count + "");
				body.setKw("成品");
				body.setImpDate(IMP_DATE);

				impBodies.add(body);
			}
		}


		for (int i = 0; i < impBodies.size(); i++) {
			impBodies.get(i).setId(i + 1l);
		}
		GenerateImportData.createImpExcel(impPath, header, impBodies);
	}
	public static void main(String[] args){
	    generateData(XUNKUN_DATA_PATH, IMP_XUNKUN_DATA_PATH, "0102");
		generateData(KEJI_DATA_PATH, IMP_KEJI_DATA_PATH, "0101");
	}
}
