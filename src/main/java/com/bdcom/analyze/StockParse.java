package com.bdcom.analyze;

import com.bdcom.bean.Stock;
import com.bdcom.util.MyUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Jianan
 * @Date
 */
public class StockParse {

	public static Map<String, Stock> pnToStock = new HashMap<String, Stock>();
	public static Set<String> virtualPnSet = new HashSet<String>();

	public static Map<String, Stock> parseStock(){
		Workbook wb = MyUtil.readExcel("C:/Users/Administrator/Desktop/whscan/stock/现存量查___0190828.9.41.xlsx");
		Cell cell;
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			Sheet sheet = wb.getSheetAt(i);
			for (int j = 1; j <= sheet.getLastRowNum(); j++) {
				Row row = sheet.getRow(j);



				cell = row.getCell(4);
				if (cellIsNull(cell)) {
					continue;
				}
				String pn = getCellString(cell).toUpperCase();

				cell = row.getCell(2);
				if (cellIsNull(cell)) {
					continue;
				}
				String kc = getCellString(cell);
				if ("虚拟库存".equals(kc)) {
					virtualPnSet.add(pn);
				}

				cell = row.getCell(5);
				String pName = "";
				if (!cellIsNull(cell)) {
					pName = getCellString(cell);
				}

				cell = row.getCell(8);
				if (cellIsNull(cell)) {
					continue;
				}
				String price = getCellString(cell);
				Stock stock = new Stock();
				stock.setPn(pn);
				stock.setpName(pName);
				stock.setUnitPrice(price);
				Stock stock1 = pnToStock.get(pn);
				if (stock1 != null && !stock1.getUnitPrice().equals(price)) {
					System.out.println(pn);
				}
				pnToStock.put(pn, stock);
			}
		}
		return pnToStock;
	}

	public static boolean cellIsNull(Cell cell){
		return cell == null || "".equals(cell.toString().trim());
	}
	public static String getCellString(Cell cell){
		return cell.toString().trim();
	}

	public static void main(String[] args){
		parseStock();
		for (String s : virtualPnSet) {
			System.out.println(s);
		}
		System.out.println(virtualPnSet.size());
	}

}
