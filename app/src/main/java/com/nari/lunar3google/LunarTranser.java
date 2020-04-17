package com.nari.lunar3google;

/* ---------------------------------------------------------------------
 * @(#)AnniversaryOracleDBWrapper.java
 * @Modifier   ray1212
 * @Creator    ray1212
 * @version    1.0
 * @date       2004-05-08
 * ---------------------------------------------------------------------
 * Copyright 1999-2003 by Traniz, Inc.,
 * Information Technology Group, Application Architecture Team,
 * Application Infrastructure Part.
 * 236-1, Seocho-2dong, Seoucho-gu, Seoul, 1362-14, KOREA.
 * All rights reserved.
 * ---------------------------------------------------------------------
 * NOTICE ! You can copy or redistribute this code freely,
 * but you should not remove the information about the copyright notice
 * and the author.
 *
 * For more information on the Traniz.com, please see
 * <http://www.traniz.com/>.
 * ---------------------------------------------------------------------
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class LunarTranser {
    private static final int kk[] = {
        1, 2, 1, 2, 1, 2, 2, 3, 2, 2,
        1, 2, 1, 1, 2, 1, 2, 1, 2, 1,
        2, 2, 1, 2, 2, 0, 1, 1, 2, 1,
        1, 2, 1, 2, 2, 2, 1, 2, 0, 2,
        1, 1, 2, 1, 3, 2, 1, 2, 2, 1,
        2, 2, 2, 1, 1, 2, 1, 1, 2, 1,
        2, 1, 2, 2, 0, 2, 1, 2, 1, 2,
        1, 1, 2, 1, 2, 1, 2, 0, 2, 2,
        1, 2, 3, 2, 1, 1, 2, 1, 2, 1,
        2, 2, 1, 2, 2, 1, 2, 1, 1, 2,
        1, 2, 1, 0, 2, 1, 2, 2, 1, 2,
        1, 2, 1, 2, 1, 2, 0, 1, 2, 3,
        2, 1, 2, 2, 1, 2, 1, 2, 1, 2,
        1, 2, 1, 2, 1, 2, 1, 2, 2, 1,
        2, 2, 0, 1, 1, 2, 1, 1, 2, 3,
        2, 2, 1, 2, 2, 2, 1, 1, 2, 1,
        1, 2, 1, 2, 1, 2, 2, 2, 0, 1,
        2, 1, 2, 1, 1, 2, 1, 2, 1, 2,
        2, 0, 2, 1, 2, 1, 2, 3, 1, 2,
        1, 2, 1, 2, 1, 2, 2, 2, 1, 2,
        1, 1, 2, 1, 2, 1, 2, 0, 1, 2,
        2, 1, 2, 1, 2, 1, 2, 1, 2, 1,
        0, 2, 1, 2, 3, 2, 2, 1, 2, 1,
        2, 1, 2, 1, 2, 1, 2, 1, 2, 1,
        2, 2, 1, 2, 1, 2, 0, 1, 2, 1,
        1, 2, 1, 2, 2, 3, 2, 2, 1, 2,
        1, 2, 1, 1, 2, 1, 2, 1, 2, 2,
        2, 1, 0, 2, 1, 2, 1, 1, 2, 1,
        2, 1, 2, 2, 2, 0, 1, 2, 1, 2,
        1, 3, 2, 1, 1, 2, 2, 1, 2, 2,
        2, 1, 2, 1, 1, 2, 1, 1, 2, 2,
        1, 0, 2, 2, 1, 2, 2, 1, 1, 2,
        1, 2, 1, 2, 0, 1, 2, 2, 1, 4,
        1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
        1, 2, 1, 2, 2, 1, 2, 1, 2, 1,
        0, 2, 1, 1, 2, 2, 1, 2, 1, 2,
        2, 1, 2, 0, 1, 2, 3, 1, 2, 1,
        2, 1, 2, 2, 2, 1, 2, 1, 2, 1,
        1, 2, 1, 2, 1, 2, 2, 2, 1, 0,
        2, 1, 2, 1, 1, 2, 3, 1, 2, 2,
        1, 2, 2, 2, 1, 2, 1, 1, 2, 1,
        1, 2, 2, 1, 2, 0, 2, 2, 1, 2,
        1, 1, 2, 1, 1, 2, 1, 2, 0, 2,
        2, 1, 2, 2, 3, 1, 2, 1, 2, 1,
        1, 2, 2, 1, 2, 2, 1, 2, 1, 2,
        1, 2, 1, 2, 0, 1, 2, 1, 2, 1,
        2, 2, 1, 2, 1, 2, 1, 0, 2, 1,
        3, 2, 1, 2, 2, 1, 2, 2, 1, 2,
        1, 2, 1, 1, 2, 1, 2, 1, 2, 2,
        2, 1, 2, 0, 1, 2, 1, 1, 2, 1,
        2, 3, 2, 2, 1, 2, 2, 1, 2, 1,
        1, 2, 1, 1, 2, 2, 1, 2, 2, 0,
        2, 1, 2, 1, 1, 2, 1, 1, 2, 1,
        2, 2, 0, 2, 1, 2, 2, 1, 3, 2,
        1, 1, 2, 1, 2, 2, 1, 2, 2, 1,
        2, 1, 2, 1, 2, 1, 1, 2, 0, 2,
        1, 2, 1, 2, 2, 1, 2, 1, 2, 1,
        1, 0, 2, 1, 2, 2, 3, 2, 1, 2,
        2, 1, 2, 1, 2, 1, 1, 2, 1, 2,
        1, 2, 2, 1, 2, 2, 1, 0, 2, 1,
        1, 2, 1, 2, 1, 2, 2, 1, 2, 2,
        0, 1, 2, 3, 1, 2, 1, 1, 2, 2,
        1, 2, 2, 2, 1, 2, 1, 1, 2, 1,
        1, 2, 1, 2, 2, 2, 0, 1, 2, 2,
        1, 1, 2, 3, 1, 2, 1, 2, 2, 1,
        2, 2, 2, 1, 1, 2, 1, 1, 2, 1,
        2, 1, 0, 2, 2, 2, 1, 2, 1, 2,
        1, 1, 2, 1, 2, 0, 1, 2, 2, 1,
        2, 4, 1, 2, 1, 2, 1, 1, 2, 1,
        2, 1, 2, 2, 1, 2, 2, 1, 2, 1,
        2, 0, 1, 1, 2, 1, 2, 1, 2, 2,
        1, 2, 2, 1, 0, 2, 1, 1, 4, 1,
        2, 1, 2, 1, 2, 2, 2, 1, 2, 1,
        1, 2, 1, 1, 2, 1, 2, 2, 2, 1,
        0, 2, 2, 1, 1, 2, 1, 1, 4, 1,
        2, 2, 1, 2, 2, 2, 1, 1, 2, 1,
        1, 2, 1, 2, 1, 2, 0, 2, 2, 1,
        2, 1, 2, 1, 1, 2, 1, 2, 1, 0,
        2, 2, 1, 2, 2, 1, 4, 1, 1, 2,
        1, 2, 1, 2, 1, 2, 2, 1, 2, 2,
        1, 2, 1, 1, 2, 0, 1, 2, 1, 2,
        1, 2, 2, 1, 2, 2, 1, 2, 0, 1,
        1, 2, 1, 4, 1, 2, 1, 2, 2, 1,
        2, 2, 1, 1, 2, 1, 1, 2, 1, 2,
        2, 2, 1, 2, 0, 2, 1, 1, 2, 1,
        1, 2, 1, 2, 2, 1, 2, 0, 2, 2,
        3, 1, 2, 1, 1, 2, 1, 2, 1, 2,
        2, 2, 1, 2, 1, 2, 1, 1, 2, 1,
        2, 1, 2, 0, 2, 2, 1, 2, 1, 2,
        1, 3, 2, 1, 2, 1, 2, 2, 1, 2,
        2, 1, 2, 1, 1, 2, 1, 2, 1, 0,
        2, 1, 2, 2, 1, 2, 1, 2, 1, 2,
        1, 2, 0, 1, 2, 1, 2, 1, 4, 2,
        1, 2, 1, 2, 1, 2, 1, 2, 1, 1,
        2, 2, 1, 2, 2, 1, 2, 2, 0, 1,
        1, 2, 1, 1, 2, 1, 2, 2, 1, 2,
        2, 0, 2, 1, 1, 4, 1, 1, 2, 1,
        2, 1, 2, 2, 2, 1, 2, 1, 2, 1,
        1, 2, 1, 2, 1, 2, 2, 0, 2, 1,
        2, 1, 2, 1, 1, 2, 3, 2, 1, 2,
        2, 1, 2, 2, 1, 2, 1, 1, 2, 1,
        2, 1, 2, 0, 1, 2, 2, 1, 2, 1,
        2, 1, 2, 1, 2, 1, 0, 2, 1, 2,
        1, 2, 2, 3, 2, 1, 2, 1, 2, 1,
        2, 1, 2, 1, 2, 1, 2, 2, 1, 2,
        1, 2, 0, 1, 2, 1, 1, 2, 1, 2,
        2, 1, 2, 2, 1, 0, 2, 1, 2, 1,
        3, 2, 1, 2, 1, 2, 2, 2, 1, 2,
        1, 2, 1, 1, 2, 1, 2, 1, 2, 2,
        2, 0, 1, 2, 1, 2, 1, 1, 2, 1,
        1, 2, 2, 1, 0, 2, 2, 2, 3, 2,
        1, 1, 2, 1, 1, 2, 2, 1, 2, 2,
        1, 2, 2, 1, 1, 2, 1, 2, 1, 2,
        0, 1, 2, 2, 1, 2, 1, 2, 3, 2,
        1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
        2, 1, 2, 1, 2, 1, 0, 2, 1, 1,
        2, 2, 1, 2, 1, 2, 2, 1, 2, 0,
        1, 2, 1, 1, 2, 3, 2, 1, 2, 2,
        2, 1, 2, 1, 2, 1, 1, 2, 1, 2,
        1, 2, 2, 2, 1, 0, 2, 1, 2, 1,
        1, 2, 1, 1, 2, 2, 2, 1, 0, 2,
        2, 1, 2, 3, 1, 2, 1, 1, 2, 2,
        1, 2, 2, 2, 1, 2, 1, 1, 2, 1,
        1, 2, 1, 2, 0, 2, 2, 1, 2, 1,
        2, 1, 2, 3, 2, 1, 1, 2, 2, 1,
        2, 2, 1, 2, 1, 2, 1, 2, 1, 1,
        0, 2, 2, 1, 2, 1, 2, 2, 1, 2,
        1, 2, 1, 0, 2, 1, 1, 2, 1, 2,
        4, 1, 2, 2, 1, 2, 1, 2, 1, 1,
        2, 1, 2, 1, 2, 2, 1, 2, 2, 0,
        1, 2, 1, 1, 2, 1, 1, 2, 2, 1,
        2, 2, 0, 2, 1, 2, 1, 3, 2, 1,
        1, 2, 2, 1, 2, 2, 2, 1, 2, 1,
        1, 2, 1, 1, 2, 1, 2, 2, 0, 2,
        1, 2, 2, 1, 1, 2, 1, 1, 2, 3,
        2, 2, 1, 2, 2, 1, 2, 1, 2, 1,
        1, 2, 1, 2, 0, 1, 2, 2, 1, 2,
        2, 1, 2, 1, 2, 1, 1, 0, 2, 1,
        2, 2, 1, 2, 3, 2, 2, 1, 2, 1,
        2, 1, 1, 2, 1, 2, 1, 2, 2, 1,
        2, 2, 1, 0, 2, 1, 1, 2, 1, 2,
        1, 2, 2, 1, 2, 2, 0, 1, 2, 1,
        1, 2, 3, 1, 2, 1, 2, 2, 2, 2,
        1, 2, 1, 1, 2, 1, 1, 2, 1, 2,
        2, 2, 0, 1, 2, 2, 1, 1, 2, 1,
        1, 2, 1, 2, 2, 0, 1, 2, 2, 3,
        2, 1, 2, 1, 1, 2, 1, 2, 1, 2,
        2, 2, 1, 2, 1, 2, 1, 1, 2, 1,
        2, 0, 1, 2, 2, 1, 2, 2, 1, 2,
        3, 2, 1, 1, 2, 1, 2, 1, 2, 2,
        1, 2, 1, 2, 2, 1, 2, 0, 1, 1,
        2, 1, 2, 1, 2, 2, 1, 2, 2, 1,
        0, 2, 1, 1, 2, 1, 3, 2, 2, 1,
        2, 2, 2, 1, 2, 1, 1, 2, 1, 1,
        2, 1, 2, 2, 2, 1, 0, 2, 2, 1,
        1, 2, 1, 1, 2, 1, 2, 2, 1, 0,
        2, 2, 2, 1, 3, 2, 1, 1, 2, 1,
        2, 1, 2, 2, 2, 1, 2, 1, 2, 1,
        1, 2, 1, 2, 1, 0, 2, 2, 1, 2,
        2, 1, 2, 1, 1, 2, 1, 2, 0, 1,
        2, 3, 2, 2, 1, 2, 1, 2, 2, 1,
        1, 2, 1, 2, 1, 2, 1, 2, 2, 1,
        2, 2, 1, 2, 0, 1, 1, 2, 1, 2,
        1, 2, 3, 2, 2, 1, 2, 2, 1, 1,
        2, 1, 1, 2, 1, 2, 2, 2, 1, 2,
        0, 2, 1, 1, 2, 1, 1, 2, 1, 2,
        2, 1, 2, 0, 2, 2, 1, 1, 2, 3,
        1, 2, 1, 2, 1, 2, 2, 2, 1, 2,
        1, 2, 1, 1, 2, 1, 2, 1, 2, 0,
        2, 1, 2, 2, 1, 2, 1, 1, 2, 1,
        2, 1, 0, 2, 1, 2, 4, 2, 1, 2,
        1, 1, 2, 1, 2, 1, 2, 1, 2, 2,
        1, 2, 1, 2, 1, 2, 1, 2, 0, 1,
        2, 1, 2, 1, 2, 1, 2, 2, 3, 2,
        1, 2, 1, 2, 1, 1, 2, 1, 2, 2,
        2, 1, 2, 2, 0, 1, 1, 2, 1, 1,
        2, 1, 2, 2, 1, 2, 2, 0, 2, 1,
        1, 2, 1, 3, 2, 1, 2, 1, 2, 2,
        2, 1, 2, 1, 2, 1, 1, 2, 1, 2,
        1, 2, 2, 0, 2, 1, 2, 1, 2, 1,
        1, 2, 1, 2, 1, 2, 0, 2, 1, 2,
        2, 3, 2, 1, 1, 2, 1, 2, 1, 2,
        1, 2, 2, 1, 2, 1, 2, 1, 2, 1,
        2, 1, 0, 2, 1, 2, 1, 2, 2, 1,
        2, 1, 2, 1, 2, 0, 1, 2, 3, 2,
        1, 2, 1, 2, 2, 1, 2, 1, 2, 1,
        2, 1, 1, 2, 1, 2, 2, 1, 2, 2,
        1, 0, 2, 1, 2, 1, 1, 2, 3, 2,
        1, 2, 2, 2, 1, 2, 1, 2, 1, 1,
        2, 1, 2, 1, 2, 2, 2, 0, 1, 2,
        1, 2, 1, 1, 2, 1, 1, 2, 2, 2,
        0, 1, 2, 2, 1, 2, 3, 1, 2, 1,
        1, 2, 2, 1, 2, 2, 1, 2, 2, 1,
        1, 2, 1, 1, 2, 2, 0, 1, 2, 1,
        2, 2, 1, 2, 1, 2, 1, 2, 1, 0,
        2, 1, 2, 3, 2, 1, 2, 2, 1, 2,
        1, 2, 1, 2, 1, 1, 2, 1, 2, 2,
        1, 2, 2, 1, 2, 0, 1, 2, 1, 1,
        2, 1, 2, 3, 2, 2, 2, 1, 2, 1,
        2, 1, 1, 2, 1, 2, 1, 2, 2, 2,
        1, 0, 2, 1, 2, 1, 1, 2, 1, 1,
        2, 2, 1, 2, 0, 2, 2, 1, 2, 1,
        1, 4, 1, 1, 2, 1, 2, 2, 2, 2,
        1, 2, 1, 1, 2, 1, 1, 2, 1, 2,
        0, 2, 2, 1, 2, 1, 2, 1, 2, 1,
        1, 2, 1, 0, 2, 2, 1, 2, 2, 3,
        2, 1, 2, 1, 2, 1, 1, 2, 1, 2,
        2, 1, 2, 2, 1, 2, 1, 2, 1, 0,
        2, 1, 1, 2, 1, 2, 2, 1, 2, 2,
        1, 2, 0, 1, 2, 3, 1, 2, 1, 2,
        1, 2, 2, 2, 1, 2, 1, 2, 1, 1,
        2, 1, 1, 2, 2, 1, 2, 2, 0
    };
    private final static String yuk[] = {"갑", "을", "병", "정", "무", "기", "경", "신", "임", "계"};
    private final static String gap[] = {"자", "축", "인", "묘", "진", "사", "오", "미", "신", "유", "술", "해"};
    private final static String ddi[] = {"쥐띠", "소띠", "범띠", "토끼띠", "용띠", "뱀띠", "말띠", "양띠", "원숭이띠", "닭띠", "개띠", "돼지띠"};
    private final static String week[] = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};

    private static final int m[] = {
        31, 28, 31, 30, 31, 30, 31, 31, 30, 31,
        30, 31
    };
   

    private static Date init_date;

    private static final String errMsg = "입력값 에러";
  


    /**
     * 음력을 양력으로
     *
     * @param       TranseDay    음력일('yyyyMMdd')
     * @param       leapyes   윤달 여부
     * @return      String    처리결과 양력일 엔티티
     * @throws      java.lang.Exception
     */
    public static String LunarTranse(String TranseDay, boolean leapyes) throws Exception {
        @SuppressWarnings("unused")
		int dt[] = new int[163];
        int lyear = Integer.parseInt(TranseDay.substring(0,4));
        int lmonth = Integer.parseInt(TranseDay.substring(4,6));
        int lday = Integer.parseInt(TranseDay.substring(6,8));

        if(!leapyes && !verifyDate(lyear, lmonth, lday, "solar-"))
        {
            return "";
        }
        if(leapyes && !verifyDate(lyear, lmonth, lday, "solar+"))
        {
            return "";

        }
        int m1 = -1;
        long td = 0L;
        if(lyear != 1881)
        {
            m1 = lyear - 1882;
            for(int i = 0; i <= m1; i++)
            {
                for(int j = 0; j < 13; j++)
                    td = td + (long)kk[i * 13 + j];

                if(kk[i * 13 + 12] == 0)
                    td = td + 336L;
                else
                    td = td + 362L;
            }

        }
        m1++;
        int n2 = lmonth - 1;
        int m2 = -1;
        do
        {
            m2++;
            if(kk[m1 * 13 + m2] > 2)
            {
                td = td + 26L + (long)kk[m1 * 13 + m2];
                n2++;
                continue;
            }
            if(m2 == n2)
                break;
            td = td + 28L + (long)kk[m1 * 13 + m2];
        } while(true);
        if(leapyes)
            td = td + 28L + (long)kk[m1 * 13 + m2];
        td = td + (long)lday + 29L;
        m1 = 1880;
        do
        {
            m1++;
            boolean leap = m1 % 400 == 0 || m1 % 100 != 0 && m1 % 4 == 0;
            if(leap)
                m2 = 366;
            else
                m2 = 365;
            if(td < (long)m2)
                break;
            td = td - (long)m2;
        } while(true);
        int syear = m1;
        m[1] = m2 - 337;
        m1 = 0;
        do
        {
            m1++;
            if(td <= (long)m[m1 - 1])
                break;
            td = td - (long)m[m1 - 1];
        } while(true);
        int smonth = m1;
        int sday = (int)td;
        long y = (long)syear - 1L;
        td = ((y * 365L + y / 4L) - y / 100L) + y / 400L;
        boolean leap = syear % 400 == 0 || syear % 100 != 0 && syear % 4 == 0;
        if(leap)
            m[1] = 29;
        else
            m[1] = 28;
        for(int i = 0; i < smonth - 1; i++)
            td = td + (long)m[i];

        td = td + (long)sday;
        @SuppressWarnings("unused")
		int w = (int)(td % 7L);
        int i = (int)(td % 10L);
        i = (i + 4) % 10;
        int j = (int)(td % 12L);
        j = (j + 2) % 12;
        @SuppressWarnings("unused")
		int k1 = (lyear + 6) % 10;
        @SuppressWarnings("unused")
		int k2 = (lyear + 8) % 12;

        String sValue= String.valueOf(syear);

        if(smonth<10)
            sValue+="0";
         sValue+= String.valueOf(smonth);
         if(sday<10)
            sValue+="0";
         sValue+= String.valueOf(sday);

        String return_value = sValue ;

        return return_value;
    }
    /**
     * 양력을 음력으로
     *
     * @param       TranseDay    양력일('yyyyMMdd')
     * @return      String    처리결과 음력일
     * @throws      java.lang.Exception
     */
    public static String solarTranse(String TranseDay) throws Exception {

        // 2020.04.15 : 음력의 날자도 당해년도 음력 날자를 찾아서 주는 것으로.
        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
        java.sql.Date dd = new java.sql.Date(time);
        TranseDay = sdf.format(dd).substring(0, 4) + TranseDay.substring(4, 8) ;

        int dt[] = new int[163];
        for(int i = 0; i < 163; i++)
        {
            dt[i] = 0;
            for(int j = 0; j < 12; j++)
                switch(kk[i * 13 + j])
                {
                case 1: // '\001'
                case 3: // '\003'
                    dt[i] = dt[i] + 29;
                    break;

                case 2: // '\002'
                case 4: // '\004'
                    dt[i] = dt[i] + 30;
                    break;
                }

            switch(kk[i * 13 + 12])

            {
            case 1: // '\001'
            case 3: // '\003'
                dt[i] = dt[i] + 29;
                break;

            case 2: // '\002'
            case 4: // '\004'
                dt[i] = dt[i] + 30;
                break;
            }
        }
        int syear = Integer.parseInt(TranseDay.substring(0,4));
        int smonth = Integer.parseInt(TranseDay.substring(4,6));
        int sday = Integer.parseInt(TranseDay.substring(6,8));

        long k11 = syear - 1;
        long td2 = ((k11 * 365L + k11 / 4L) - k11 / 100L) + k11 / 400L;
        boolean ll = syear % 400 == 0 || syear % 100 != 0 && syear % 4 == 0;
        if(ll)
            m[1] = 29;
        else
            m[1] = 28;

        if(!verifyDate(syear, smonth, sday, "lunar"))
        {
            throw new Exception("Date Error [" + syear + smonth + sday + "]");

        }
        
        for(int i = 0; i < smonth - 1; i++)
            td2 = td2 + (long)m[i];

        td2 = td2 + (long)sday;


        long td = (td2 - 0xa7a5eL) + 1L;
        long td0 = dt[0];
        int i=0;
        for(i = 0; i < 163; i++)
        {
            if(td <= td0)
                break;
            td0 = td0 + (long)dt[i + 1];
        }

        int lyear = i + 1881;
        td0 = td0 - (long)dt[i];
        td = td - td0;
        int jcount=0;
        if(kk[i * 13 + 12] != 0)
            jcount = 13;
        else
            jcount = 12;
        int m2 = 0;
        int j=0;
        int m1;
        for(j = 0; j < jcount; j++)
        {
            if(kk[i * 13 + j] <= 2)
                m2++;
            if(kk[i * 13 + j] <= 2)
                m1 = kk[i * 13 + j] + 28;
            else
                m1 = kk[i * 13 + j] + 26;
            if(td <= (long)m1)
                break;
            td = td - (long)m1;
        }

        @SuppressWarnings("unused")
		int m0 = j;
        long lmonth = m2;
        int lday = (int)td;
        @SuppressWarnings("unused")
		int w = (int)(td2 % 7L);
        i = (int)((td2 + 4L) % 10L);
        j = (int)((td2 + 2L) % 12L);
        @SuppressWarnings("unused")
		int i1 = (lyear + 6) % 10;
        @SuppressWarnings("unused")
		int j1 = (lyear + 8) % 12;
        String sValue= String.valueOf(lyear);

        if(lmonth<10)
            sValue+="0";
         sValue+= String.valueOf(lmonth);
         if(lday<10)
            sValue+="0";
         sValue+= String.valueOf(lday);
         
        String return_value = sValue ;

        return return_value;
    }

    private static boolean verifyDate(int k, int l, int l1, String s)
    {
        if(k < 1881 || k > 2043 || l < 1 || l > 12)
            return false;
        if(s.equals("lunar") && l1 > m[l - 1])
            return false;
        if(s.equals("solar+"))
        {
            if(kk[(k - 1881) * 13 + 12] < 1)
                return false;
            if(kk[(k - 1881) * 13 + l] < 3)
                return false;
            if(kk[(k - 1881) * 13 + l] + 26 < l1)
                return false;
        }
        if(s.equals("solar-"))
        {
            int j = l - 1;
            for(int i = 1; i <= 12; i++)
                if(kk[((k - 1881) * 13 + i) - 1] > 2)
                    j++;

            if(l1 > kk[(k - 1881) * 13 + j] + 28)
                return false;
        }
        return true;
    }
	public static String[] getYuk() {
		return yuk;
	}
	public static String[] getGap() {
		return gap;
	}
	public static String[] getDdi() {
		return ddi;
	}
	public static String[] getWeek() {
		return week;
	}
	public static void setInit_date(Date init_date) {
		LunarTranser.init_date = init_date;
	}
	public static Date getInit_date() {
		return init_date;
	}
	public static String getErrmsg() {
		return errMsg;
	}   
}