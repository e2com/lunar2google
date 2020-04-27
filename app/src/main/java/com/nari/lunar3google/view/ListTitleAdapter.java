package com.nari.lunar3google.view;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nari.lunar3google.R;
import com.nari.lunar3google.util.DBHandler;
import com.nari.lunar3google.util.StringUtil;

import java.util.ArrayList;

public class ListTitleAdapter extends BaseAdapter {

    ArrayList<ListTitleData> textTitle  ;
    ArrayList<ListTitleData> listData = new ArrayList<ListTitleData>() ;
    int year = 0 ;
    int month = 0 ;
    String TAG = "ListTitleAdapter";

    public ListTitleAdapter(ArrayList<ListTitleData> pTitle, int pYear, int pMonth) {
        super();
        textTitle = pTitle ;
        year = pYear ;
        month = pMonth ;

        Log.e(TAG, "ListTitleAdapter=" + year + "-" + month) ;
    }

    @Override
    public int getCount() {
        return textTitle.size();
    }

    @Override
    public Object getItem(int position) {
        return textTitle.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext() ;
        View v = convertView ;
        if (v == null) {
            LayoutInflater lv = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = lv.inflate(R.layout.content_dayview, null);
        }
        TextView textDay = v.findViewById(R.id.textDay);
        textDay.setText(this.textTitle.get(position).getTitle());
        ListView lvList = v.findViewById(R.id.ListViewDay);
        if (isNumber(textTitle.get(position).getTitle())) {
            String pYMD = String.valueOf(year) + String.valueOf(StringUtil.pad(month)) + StringUtil.pad(Integer.parseInt(textTitle.get(position).getTitle()));
            Log.e(TAG, "pYMD=" + pYMD) ;
            DBHandler dbHandler = DBHandler.open(context);
            Cursor rs = dbHandler.selectBaseDate(pYMD);
            listData.clear();
            while (rs.moveToNext()) {
                ListTitleData listTitleData = new ListTitleData(rs.getString(rs.getColumnIndex("subject")));
                listData.add(listTitleData);
                Log.e(TAG, "Today Plan=" + rs.getString(rs.getColumnIndex("subject")));
            }
            dbHandler.close();
        }
        ListDayAdapter listDayAdapter = new ListDayAdapter(listData);
        lvList.setAdapter(listDayAdapter);
        return v;
    }

    public boolean isNumber(String iString) {
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
}
