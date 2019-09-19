package com.bdcom.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
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


	public static String getTodayDate(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String dateString = sdf.format(date);

		return dateString;
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

	public static void main(String[] args){
	    System.out.println(getTodayDate());
	}

}
