package com.bdcom.analyze;

import java.util.ArrayList;
import java.util.List;

public class QrCodeParser {

	public static String src1 = "SN:00239238442 MAC:84:79:73:c5:7b:83\r\n"+
			"SN:00239243337 MAC:84:79:73:c5:dc:c2\r\n"+
			"SN:00239238439 MAC:84:79:73:c5:7b:80\r\n"+
			"SN:00239240468 MAC:84:79:73:c5:83:6d\r\n"+
			"SN:00239243931 MAC:84:79:73:c5:df:14\r\n"+
			"SN:00239242194 MAC:84:79:73:c5:d8:4b\r\n"+
			"SN:00239239488 MAC:84:79:73:c5:7f:99\r\n"+
			"SN:00239239702 MAC:84:79:73:c5:80:6f\r\n"+
			"SN:00239238474 MAC:84:79:73:c5:7b:a3\r\n"+
			"SN:00239238459 MAC:84:79:73:c5:7b:94\r\n"+
			"SN:00239241112 MAC:84:79:73:c5:85:f1\r\n"+
			"SN:00239243878 MAC:84:79:73:c5:de:df\r\n"+
			"SN:00239243779 MAC:84:79:73:c5:de:7c\r\n"+
			"SN:00239237186 MAC:84:79:73:c5:76:9b\r\n"+
			"SN:00239243378 MAC:84:79:73:c5:dc:eb\r\n"+
			"SN:00239244131 MAC:84:79:73:c5:df:dc\r\n"+
			"SN:00239241203 MAC:84:79:73:c5:d4:6c\r\n"+
			"SN:00239242973 MAC:84:79:73:c5:db:56\r\n"+
			"SN:00239241201 MAC:84:79:73:c5:d4:6a\r\n"+
			"SN:00239243664 MAC:84:79:73:c5:de:09";


	public static String src2 = "SN:E20004006313 MAC:84:79:73:d5:8a:f6            SN:E20004006314 MAC:84:79:73:d5:8b:01           SN:E20004006315 MAC:84:79:73:d5:8b:0c           SN:E20004006316 MAC:84:79:73:d5:8b:17           SN:E20004006317 MAC:84:79:73:d5:8b:22";

	public static String src3 = "SN:00246203760 MAC:98:45:62:69:51:39 SN:00246203698 MAC:98:45:62:69:50:fb SN:00246204335 MAC:98:45:62:69:53:78 SN:00246204054 MAC:98:45:62:69:52:5f SN:00246204011 MAC:98:45:62:69:52:34 SN:00246204155 MAC:98:45:62:69:52:c4 SN:00246203759 MAC:98:45:62:69:51:38 SN:00246204001 MAC:98:45:62:69:52:2a SN:00246203888 MAC:98:45:62:69:51:b9 SN:00246203801 MAC:98:45:62:69:51:62 SN:00246203790 MAC:98:45:62:69:51:57 SN:00246204625 MAC:98:45:62:69:54:9a SN:00246204524 MAC:98:45:62:69:54:35 SN:00246204458 MAC:98:45:62:69:53:f3 SN:00246204342 MAC:98:45:62:69:53:7f SN:00246203950 MAC:98:45:62:69:51:f7 SN:00246203745 MAC:98:45:62:69:51:2a SN:00246203692 MAC:98:45:62:69:50:f5 SN:00246203931 MAC:98:45:62:69:51:e4 SN:00246203693 MAC:98:45:62:69:50:f6";

	public static String src4 = "SN：S23093344MAC：84：9：3:7a:77:8dH/W:MFRP0640080330C000";

	public static String src5 = "20013224190";

	public static String src6 = "SN:SLM19010501\r\n\r\n" +
			"SN:SLM19010502\r\n\r\n" +
			"SN:SLM19010503\r\n\r\n" +
			"SN:SLM19010504\r\n\r\n" +
			"SN:SLM19010505\r\n\r\n" +
			"SN:SLM19010506\r\n\r\n" +
			"SN:SLM19010507\r\n\r\n" +
			"SN:SLM19010508\r\n\r\n" +
			"SN:SLM19010509\r\n\r\n" +
			"SN:SLM19010510\r\n\r\n" +
			"SN:SLM19010511\r\n\r\n";

	public static String src7 = "";
	public static String src8 = "";
	public static String src9 = "";
	public static String src10 = "";

	public static String[] testData = new String[10];
	public static void initData(){

		testData[0] = src1;
//		testData[1] = src2;
//		testData[2] = src3;
//		testData[3] = src4;
//		testData[4] = src5;
//		testData[5] = src6;


//		testData[6] = src7;
//		testData[7] = src8;
//		testData[8] = src9;
//		testData[9] = src10;

	}

	public static final String colon1 = ":";
	public static final String colon2 = "：";

	public static final String SNStart = "SN";
	public static final int SNLen = 11 ;
	public static final String MACStart = "MAC";
	public static final int MacLen = "98:45:62:69:50:fb".length() ; //18

	public static List<String> parse1(String srcStr ){

		List<String> snList = new ArrayList<String>();

		String[] lineSplit = (srcStr).split("\r\n");
		if( lineSplit.length == 1 ){
			lineSplit = srcStr.split("\n");
		}

		for(String str : lineSplit  ){

			if(  isBlank(str) ){
				//为空白字符不处理
			}else{
				String[] split = str.split(" ");
				int sLen = split.length ;
				for(  String sss : split ){
					sss = sss.toUpperCase();
					if(  isBlank(sss) ){
						//为空白字符不处理
					}else{
//						System.out.print(  sss + "===" );

						// 以SN:开头的
						if(  sss.startsWith( "SN" + colon1) ||  sss.startsWith( "SN"  + colon2 ) ||  sss.startsWith( "SN;" ) ){
							String tmpSn = null;
							tmpSn = sss.substring(3);
							int macIndex = tmpSn.indexOf("MAC");
							if(  macIndex!=-1 ){
								tmpSn= tmpSn.substring( 0 , macIndex);
							}
							if( tmpSn!=null && tmpSn.length()>2 ){
								snList.add(tmpSn.trim());
							}
						}
						else if(  sss.startsWith( "S/N:")){
							String tmpSn = null;
							tmpSn = sss.substring(4);
							int macIndex = tmpSn.indexOf("MAC");
							if(  macIndex!=-1 ){
								tmpSn= tmpSn.substring( 0 , macIndex);
							}
							if( tmpSn!=null && tmpSn.length()>2 ){
								snList.add(tmpSn.trim());
							}
						}
						else if( sss.startsWith( "MAC" + colon1) || sss.startsWith( "MAC"  + colon2 )  ) {

						} else if( sss.startsWith( "H/W" + colon1) || sss.startsWith( "H/W"  + colon2 )   ){

						}
						else if( sss.startsWith( "MFRP" + colon1) || sss.startsWith( "MFRP"  + colon2 )   ){

						}else if( sss.startsWith( "MFRP") ){

						}
						else{
							sss = sss.trim();
							int length = sss.length();
							if( length>=5 && length<=35 && sss.indexOf(":") < 0){
								snList.add( sss.trim() );
							} else {

							}
						}

					}
				}//for
//				System.out.println(    );
//				System.out.println(  "======================"   );
			}

		}

//		for( String sn :  snList  ){
//			System.out.print(   sn + " , "  );
//		}
//		System.out.println();
//		System.out.println(  snList.size() + "=============" );
		return snList;

	}
	public static List<String> parse(String srcStr ){

		List<String> snList = new ArrayList<String>();

		String[] lineSplit = (srcStr).split("\r\n");
		if( lineSplit.length == 1 ){
			lineSplit = srcStr.split("\n");
		}

		for(String str : lineSplit  ){

			if(  isBlank(str) ){
				//为空白字符不处理
			}else{
				String[] split = str.split(" ");
				int sLen = split.length ;
				for(  String sss : split ){
					sss = sss.toUpperCase();
					if(  isBlank(sss) ){
						//为空白字符不处理
					}else{
//						System.out.print(  sss + "===" );

						// 以SN:开头的
						if(  sss.startsWith( "SN" + colon1) ||  sss.startsWith( "SN"  + colon2 )  ||  sss.startsWith( "SN;" )   ){
							String tmpSn = null;
							tmpSn = sss.substring(3);
							int macIndex = tmpSn.indexOf("MAC");
							if(  macIndex!=-1 ){
								tmpSn= tmpSn.substring( 0 , macIndex);
							}
							if( tmpSn!=null && tmpSn.length()>2 ){
								snList.add(tmpSn.trim());
							}
						}else if(  sss.startsWith( "S/N:") ){
							String tmpSn = null;
							tmpSn = sss.substring(4);
							snList.add(tmpSn.trim());

						}else if( sss.startsWith( "MAC" + colon1) || sss.startsWith( "MAC"  + colon2 )  ) {

						} else if( sss.startsWith( "H/W" + colon1) || sss.startsWith( "H/W"  + colon2 )   ){

						}else if( sss.startsWith( "MFRP" + colon1) || sss.startsWith( "MFRP"  + colon2 )   ){

						}else if( sss.startsWith( "MFRP") ){

						}else{
							int length = sss.length();
							if(  sss.contains(colon1) || sss.contains(colon2) ){
								continue;
							}
							if( length>=5 && length<=35 ){
								snList.add( sss.trim() );
							} else {

							}
						}

					}
				}//for
//				System.out.println(    );
//				System.out.println(  "======================"   );
			}

		}

//		for( String sn :  snList  ){
//			System.out.print(   sn + " , "  );
//		}
//		System.out.println();
//		System.out.println(  snList.size() + "=============" );
		return snList;

	}


	public static void main(String[] args) {
		System.out.println(":".indexOf(":") >= 0);
//		initData();

//		for( int i=0 ; i<testData.length ;i++ ){
//			String tmpStr = testData[i];
//			if( tmpStr!=null && tmpStr.length()>0 ){
//				parse(tmpStr);
//			}
//		}

		parse(src1);
		System.out.println(  "~~~~~~~~~~~~~~~~~~~换了一种格式src1~~~~~~" );
		parse(src2);
		System.out.println(  "~~~~~~~~~~~~~~~~~~~换了一种格式src2~~~~~~" );
		parse(src3);
		System.out.println(  "~~~~~~~~~~~~~~~~~~~换了一种格式src3~~~~~~" );
		parse(src4);
		System.out.println(  "~~~~~~~~~~~~~~~~~~~换了一种格式src4~~~~~~" );
		parse(src5);
		System.out.println(  "~~~~~~~~~~~~~~~~~~~换了一种格式src5~~~~~~" );
		parse(src6);
		System.out.println(  "~~~~~~~~~~~~~~~~~~~换了一种格式src6~~~~~~" );
	}



	public static boolean isBlank(CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}


}
