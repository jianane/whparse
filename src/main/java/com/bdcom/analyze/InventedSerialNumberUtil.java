package com.bdcom.analyze;

import java.util.*;

/**
 * @author Declan
 * @date 2019/08/31 17:36
 */
public class InventedSerialNumberUtil {


    private static Map<String, String> kuCunZuZhi2KuDaiMa = new HashMap<String, String>();


    private static List<String> randomStr = new ArrayList<String>();
    private static int index = 0;

    static {
        kuCunZuZhi2KuDaiMa.put("01", "A");
        kuCunZuZhi2KuDaiMa.put("0101", "B");
        kuCunZuZhi2KuDaiMa.put("010101", "C");
        kuCunZuZhi2KuDaiMa.put("010102", "D");
        kuCunZuZhi2KuDaiMa.put("0102", "E");

        for (int i = 1; i < 1000000; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            String str = String.valueOf(i);
            for (int j = 0; j < 6-str.length(); j++) {
                stringBuilder.append("0");
            }
            stringBuilder.append(str);
            randomStr.add(stringBuilder.toString());
        }
    }


    /**
     * 获取虚假号
     *  假序号规则拟定义如下，为避免重号引入了年月日 （共13位）。目前公司正常产品用的序号是11位。
     *  假序号规则：库代码(3位代号固定）+ 年（采用年份最后两位数表示）+ 月（1~9ABC共一位）+ 日（01~31两位标识）+	流水号（5位整数数字表示）
     *  好处是一目了然，易识别掌握。弊端就是：比正常的号长了。

     * @param kuWeiZuZhi 库位组织
     * @param kuBie 库别
     * @param kuWei 库位
     * @return
     */
    public static String getInventedSerialNumber(String kuWeiZuZhi, String kuBie, String kuWei){

        StringBuilder retSerialNumberBuilder = new StringBuilder();
        retSerialNumberBuilder.append(getKuDaiMa(kuWeiZuZhi, kuBie, kuWei));
        retSerialNumberBuilder.append(getDateStr());
        retSerialNumberBuilder.append(get5RandomNumber());
        return retSerialNumberBuilder.toString();
    }
    /**
     * 获取虚假号
     *  假序号规则拟定义如下，为避免重号引入了年月日 （共13位）。目前公司正常产品用的序号是11位。
     *  假序号规则：库代码(3位代号固定）+ 年（采用年份最后两位数表示）+ 月（1~9ABC共一位）+ 日（01~31两位标识）+	流水号（5位整数数字表示）
     *  好处是一目了然，易识别掌握。弊端就是：比正常的号长了。

     * @param kuWeiZuZhi 库位组织
     * @param kuBie 库别
     * @param kuWei 库位
     * @param startIndex 5位流水号(00001~99999)的起始索引
     * @return
     */
    public static String getInventedSerialNumber(String kuWeiZuZhi, String kuBie, String kuWei, int startIndex){
        if(startIndex>0 && index==0){
            index = startIndex;
        }
        StringBuilder retSerialNumberBuilder = new StringBuilder();
        retSerialNumberBuilder.append(getKuDaiMa(kuWeiZuZhi, kuBie, kuWei));
        retSerialNumberBuilder.append(getDateStr());
        retSerialNumberBuilder.append(get5RandomNumber());
        return retSerialNumberBuilder.toString();
    }


    /**
     * 获取当前所以的值
     * @return
     */
    public static int getCurrentIndex(){
        return index+1;
    }

    private static String getKuDaiMa(String kuWeiZuZhi, String kuBie, String kuWei){
        StringBuilder kuBieStrBuilder = new StringBuilder();
        if(!kuCunZuZhi2KuDaiMa.containsKey(kuWeiZuZhi)){
            throw new RuntimeException("库存组织找不到");
        }
        kuBieStrBuilder.append(kuCunZuZhi2KuDaiMa.get(kuWeiZuZhi));
        if(kuBie==null || kuBie.equals("") || kuBie.length()>3 || kuBie.length()<2){
            throw new RuntimeException("传入的库别错误："+kuBie);
        }
        if(kuBie.length()<3){
            kuBie = "0" + kuBie;
        }
        kuBieStrBuilder.append(kuBie);
        return kuBieStrBuilder.toString();
    }


    private static String getDateStr(){
        StringBuilder stringBuilder = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DATE);

        stringBuilder.append(String.valueOf(year).substring(2));
        if(month>9){
            String strMonth = month==10 ? "A" : month==11 ? "B" : "C";
            stringBuilder.append(strMonth);
        }else{
            stringBuilder.append(String.valueOf(month));
        }

        String strDay = day>9 ? String.valueOf(day) : "0"+day;
        stringBuilder.append(strDay);

        return stringBuilder.toString();
    }


    private static String get5RandomNumber(){
        String random = randomStr.get(index);
        index++;
        return random;
    }





    public static void main(String[] args) {
        String kuCunZuZhi = "010102";
        String kuBie = "66";
        String kuWei = "01";
        String inventedSerialNumber = getInventedSerialNumber(kuCunZuZhi, kuBie, kuWei, 2);
        String inventedSerialNumber1 = getInventedSerialNumber(kuCunZuZhi, kuBie, kuWei, 2);
        String inventedSerialNumber2 = getInventedSerialNumber(kuCunZuZhi, kuBie, kuWei, 2);
        String inventedSerialNumber3 = getInventedSerialNumber(kuCunZuZhi, kuBie, kuWei, 2);
        System.out.println(inventedSerialNumber);
        System.out.println(inventedSerialNumber1);
        System.out.println(inventedSerialNumber2);
        System.out.println(inventedSerialNumber3);
        System.out.println(getCurrentIndex());

    }




}
