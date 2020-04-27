package com.nari.lunar3google;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.nari.lunar3google.R.id;
import com.nari.lunar3google.util.DBHandler;
import com.nari.lunar3google.util.LunarTranser;
import com.nari.lunar3google.util.StringUtil;
import com.nari.lunar3google.view.ListData;
import com.nari.lunar3google.view.ListDataAdapter;
import com.nari.lunar3google.view.ListTitleAdapter;
import com.nari.lunar3google.view.ListTitleData;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarMonth extends AppCompatActivity {

    DBHandler dbHandler;
    final int ACT_EDIT = 0;
    String chk_date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_month);
        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        ImageButton btnPrev2 = findViewById(R.id.btnPrevious2) ;
        ImageButton btnNext2 = findViewById(R.id.btnNext2) ;
        EditText editText2 = findViewById(id.editMonth2) ;
        ListView dataView = findViewById(id.dataView) ;
        GridView dataMonth = findViewById(id.gridMonth);

        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.label_yymmdd));
        Date dd = new Date(time);
        editText2.setText(sdf.format(dd));

        final ArrayList<ListData> mList = new ArrayList<ListData>() ;
        dbHandler = DBHandler.open(this);
        Cursor cursor = null;
        cursor = dbHandler.selectAll();
        int count = 0;
        while (cursor.moveToNext()) {
            String _id = cursor.getString(0);
            String subject = cursor.getString(1);
            String base_date = cursor.getString(2);
            String chk_date2 = cursor.getString(2);
            int lunar_ty = cursor.getInt(3);
            int leap_ty = cursor.getInt(4);
            int sync_stat = cursor.getInt(5);
            String name = cursor.getString(6);
            if (lunar_ty == 2) {
                try {
                    base_date = StringUtil.padDate(base_date)
                            + getResources().getString(R.string.label_lunar2)  // "[음)"
                            + StringUtil.padDate(LunarTranser.solarTranse(base_date)
                            + "]");
                } catch (Exception e) {
                    base_date = StringUtil.padDate(base_date) + "[???]";
                }
            } else {
                chk_date = sdf.format(dd).toString().substring(0, 4)
                        + base_date.substring(4, 8);
                if (leap_ty == 1) {
                    try {
                        base_date = StringUtil.padDate(base_date)
                                + getResources().getString(R.string.label_leap_solar)  // "(윤)[양)"
                                + StringUtil.padDate(LunarTranser.LunarTranse(chk_date,
                                true)) + "]";
                    } catch (Exception e) {
                        base_date = StringUtil.padDate(base_date) + getResources().getString(R.string.label_leap_lunar); // "(윤,음)";
                    }
                } else {
                    try {
                        base_date = StringUtil.padDate(base_date)
                                + getResources().getString(R.string.label_solra2)  //"[양)"
                                + StringUtil.padDate(LunarTranser.LunarTranse(chk_date,
                                false)) + "]";
                    } catch (Exception e) {
                        base_date = StringUtil.padDate(base_date) + getResources().getString(R.string.label_lunar2); // "(음)";
                    }
                }
            }
            String mobil_no = StringUtil.padTelno(cursor.getString(7));
            ListData list_data = new ListData(_id, subject, base_date, name, mobil_no, sync_stat);
            mList.add(list_data);
            count++;
        }
        cursor.close();
        dbHandler.close();

        ListDataAdapter listDataAdapter = new ListDataAdapter(mList);
        dataView.setAdapter(listDataAdapter);

        dataView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CalendarMonth.this, DeleteData.class);
                intent.putExtra("id", mList.get(position).getId());
                startActivityForResult(intent, ACT_EDIT);
            }
        });

        java.util.Date nDate = null;
        try {
            nDate = sdf.parse(sdf.format(dd));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance() ;
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK) ;

        String sYear = String.valueOf(cal.get(Calendar.YEAR)) ;
        String sMonth = String.valueOf(cal.get(Calendar.MONTH)+1) ;

        // 메월 1일 설정
        cal.set(Integer.parseInt(sYear), Integer.parseInt(sMonth)-1, Integer.parseInt("1"));
        int year = cal.get ( cal.YEAR );
        int month = cal.get ( cal.MONTH )+1 ;
        int startDay = cal.get(cal.DAY_OF_MONTH);
        int endDay = cal.getActualMaximum(cal.DAY_OF_MONTH);
        dayNum = cal.get(Calendar.DAY_OF_WEEK) - 1;

        ArrayList<ListTitleData> mMonth = new ArrayList<ListTitleData>();
        String[] mTitle = { getString(R.string.labelSun), getString(R.string.labelMon) , getString(R.string.labelTue), getString(R.string.labelWed), getString(R.string.labelThr), getString(R.string.labelFri), getString(R.string.labelSat)};
        for(int idx = 0 ; idx < mTitle.length ; idx++) {
            ListTitleData listTitleData = new ListTitleData(mTitle[idx]);
            mMonth.add(listTitleData);
        }
        int iDay = 0 ;
        int iPos = 0 ;
        while(true) {
            String sValue = "" ;
            if (iDay == endDay) break ;
            if (iPos < dayNum) {
                sValue = "" ;
            } else {
                iDay++; // 날자 넣기
                sValue = String.valueOf(iDay) ;
            }
            ListTitleData listTitleData = new ListTitleData(sValue);
            mMonth.add(listTitleData);
            iPos++; // 위치 잡기
        }
        ListTitleAdapter listTitleAdapter = new ListTitleAdapter(mMonth, year, month);
        dataMonth.setAdapter(listTitleAdapter);

    }

}
