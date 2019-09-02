import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jianan
 * @Date
 */
public class MyUtil {
	public static boolean cellIsNull(Cell cell){
		return cell == null || "".equals(cell.toString().trim());
	}
	public static String getCellUppercaseString(Cell cell){
		return cell.toString().toUpperCase().trim();
	}
	public static String getCellString(Cell cell){
		return cell.toString().toUpperCase().trim();
	}
	public static String getStringTypeCell(Cell cell){
		cell.setCellType(CellType.STRING);
		return cell.toString().toUpperCase().trim();
	}

	public static boolean isContainChinese(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		return m.find();
	}


	static String getTodayDate(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String dateString = sdf.format(date);

		return dateString;
	}

	public static void main(String[] args){
	    System.out.println(getTodayDate());
	}

}
