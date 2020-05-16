package com.nari.lunar3google.util;

import android.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StringUtil {

    private static String TAG = "StringUtil" ;

    public StringUtil() {

    }

    /**
     * 날자형식 만들기
     * @param iDate
     * @return
     */
    public static String padDate(String iDate) {
        String return_value = "";
        try {
            return_value = iDate.substring(0, 4) + "-" + iDate.substring(4, 6)
                    + "-" + iDate.substring(6, 8);
        } catch (Exception e) {
            return_value = iDate;
        }
        return return_value;
    }

    /**
     * 전화번호 형식 만들기
     * @param iTelno
     * @return
     */
    public static String padTelno(String iTelno) {
        String return_value = "" ;
        try {
            if (iTelno.length() < 11) {
                return_value = iTelno.substring(0, 3) + "-" + iTelno.substring(3, 6) + "-" + iTelno.substring(6, 10);
            } else {
                return_value = iTelno.substring(0, 3) + "-" + iTelno.substring(3, 7) + "-" + iTelno.substring(7, 11);
            }
        } catch (Exception e) {
            return_value = iTelno ;
        }

        return return_value ;
    }

    /**
     * 연월일 산출
     * @return
     */
    public static String parse2Date() {

        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dd = new Date(time);
        return sdf.format(dd);

    }

    /**
     * 두자리 숫자인가 ???
     * @param c
     * @return
     */
    public static String pad(int c) {
        String return_value = "";
        if (c >= 10) {
            return_value = String.valueOf(c);
        } else {
            return_value = "0" + String.valueOf(c);
        }
        return return_value;
    }

    /**
     * 월 계산 함수
     * @param currMonth
     * @param param
     * @return
     */
    public static String addMonth(String currMonth, String param) {
        String strToday = "" ;

        Log.e(TAG, ">>>" + currMonth + "<<<" + param) ;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar cal = Calendar.getInstance() ;
        String[] currYm = currMonth.split("-") ;
        Log.e(TAG, "=" + currYm[0] + "/" + currYm[1]) ;

        cal.set(Integer.parseInt(currYm[0]), Integer.parseInt(currYm[1])-1, 1);
        Log.e(TAG, "sdf Date=" + sdf.format(new java.util.Date(cal.getTimeInMillis())));
        if ("P".equals(param)) {
            cal.add(cal.MONTH, -1);
        } else {
            cal.add(cal.MONTH, +1);
        }
        strToday = sdf.format(new java.util.Date(cal.getTimeInMillis()));

        Log.e(TAG, "return Value =" + strToday) ;
        return strToday ;
    }

    /**
     *  숫자인가 ?
     * @param iString
     * @return
     */
    public static boolean isNumber(String iString) {
        boolean bResult = false ;
        int isNumber = 0 ;
        try {
            isNumber = Integer.parseInt(iString) ;
            bResult = true ;
        } catch (Exception e) {

        }
        //Log.d(TAG, "[" + isNumber + "]") ;
        return bResult ;
    }

    /**
     * 오늘 날자 리턴
     * @return
     */
    public static String getToday() {
        String strToday = "" ;

        long now = System.currentTimeMillis();
        java.util.Date date = new java.util.Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        strToday = sdf.format(date);

        return strToday ;
    }


}
