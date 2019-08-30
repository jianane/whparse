import org.apache.poi.ss.usermodel.Cell;

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

	public static boolean isContainChinese(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		return m.find();
	}

	public static void main(String[] args){
	    String test = "asdfsadf；只";
	    String test1 = "asdfsadf";
	    test.compareTo(test1);
		System.out.println(isContainChinese(test));;


	}
}
