import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jianan
 * @Date
 */
public class ErrToRightPnParse {

	static Map<String, String> errToRight = new HashMap<String, String>();


	public static final String ERR_TO_RIGHT_PATH = "C:/Users/Administrator/Desktop/whscan/ErrToRight/ErrToRight.xlsx";

	public static final String ERR_TO_RIGHT_PN_SRC = "C:/Users/Administrator/Desktop/whscan/ErrToRight/modify_shi";
	public static final String ERR_TO_RIGHT_PN = "C:/Users/Administrator/Desktop/whscan/ErrToRight/物料档案比对.xls";


	public static void main(String[] args) {

		addOrModifyExcel();

	}

	static void parseOld() {
		Workbook wb = WhparseMain.readExcel(ERR_TO_RIGHT_PN);

		Sheet sheet = wb.getSheetAt(0);
		Cell cell;
		Row row;
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			if (row == null) {
				continue;
			}

			cell = row.getCell(5);
			if (cellIsNull(cell)) {
				continue;
			}
			String rightPn = getCellUppercaseString(cell);

			cell = row.getCell(1);
			if (cellIsNull(cell)) {
				continue;
			}
			String errPn = getCellUppercaseString(cell);

			errToRight.put(errPn, rightPn);
		}
	}

	static void parseNew() {
		List<String> fileNames = WhParseStart.travFolder(ERR_TO_RIGHT_PN_SRC);
		for (String fileName : fileNames) {
			Workbook wb = WhparseMain.readExcel(fileName);
			Sheet sheet = wb.getSheetAt(0);
			Row row;
			Cell cell;
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				cell = row.getCell(0);
				if (cellIsNull(cell)) {
					continue;
				}
				String errPn = getCellUppercaseString(cell);

				cell = row.getCell(1);
				cell.setCellType(CellType.STRING);
				if (cellIsNull(cell) || "1".equals(cell.toString().trim())) {
					continue;
				}
				String rightPn = getCellUppercaseString(cell);
				if (!errPn.equals(rightPn)) {
					errToRight.put(errPn, rightPn);
				}
			}
		}
	}

	static void addOrModifyExcel() {
		parseOld();
		parseNew();

		createExcel(ERR_TO_RIGHT_PATH);

	}

	static final String[] HEAD_DESC = new String[]{"Error Pn", "Right Pn"};

	static void createExcel(String fileName) {
		try {
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet();
			int rowCnt = 0;
			XSSFRow row = sheet.createRow(rowCnt);
			XSSFCell cell = null;
			for (int i = 0; i < HEAD_DESC.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(HEAD_DESC[i]);
			}
			Set<Map.Entry<String, String>> entries = errToRight.entrySet();
			for (Map.Entry<String, String> entry : entries) {
				rowCnt++;
				String errPn = entry.getKey();
				String rightPn = entry.getValue();
				row = sheet.createRow(rowCnt);
				row.createCell(0).setCellValue(errPn);
				row.createCell(1).setCellValue(rightPn);
			}

			File file = new File(fileName);
			FileOutputStream os = new FileOutputStream(file);
			wb.write(os);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static boolean cellIsNull(Cell cell) {
		return cell == null || "".equals(cell.toString().trim());
	}

	public static String getCellUppercaseString(Cell cell) {
		return cell.toString().toUpperCase().trim();
	}


}
