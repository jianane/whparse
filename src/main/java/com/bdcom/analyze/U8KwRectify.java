package com.bdcom.analyze;

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
public class U8KwRectify {

	final static String SELF_CHECK_KW_RECTIFY_PATH = "C:/Users/Administrator/Desktop/whscan/whMap/库位修正/电子台账库位修复.xlsx";

	static Map<String, String> selfKwErrorToRight = new HashMap<String, String>();

	static void loadSelfCheckKwRectify(){
		Workbook wb = MyUtil.readExcel(SELF_CHECK_KW_RECTIFY_PATH);
		Sheet sheet = wb.getSheetAt(0);
		Row row;
		Cell cell;
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);

			cell = row.getCell(1);
			String errKw = MyUtil.getCellString(cell);

			cell = row.getCell(2);
			String rightKw = MyUtil.getCellString(cell);

			selfKwErrorToRight.put(errKw, rightKw);
		}
	}

	static String rectifySelfKw(String kw){
		String rightKw = selfKwErrorToRight.get(kw);


		return rightKw == null ? kw : rightKw;
	}

}
