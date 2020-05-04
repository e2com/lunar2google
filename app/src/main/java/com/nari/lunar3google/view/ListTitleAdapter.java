package com.nari.lunar3google.view;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nari.lunar3google.R;
import com.nari.lunar3google.util.DBHandler;
import com.nari.lunar3google.util.LunarTranser;
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

        Log.i(TAG, "ListTitleAdapter=" + year + "-" + month) ;
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
    public View getView(int position, final View convertView, ViewGroup parent) {

        Context context = parent.getContext() ;
        View v = convertView ;
        if (v == null) {
            LayoutInflater lv = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = lv.inflate(R.layout.content_dayview, null);
        }
        TextView textDay = v.findViewById(R.id.textDay);
        textDay.setText(this.textTitle.get(position).getTitle());
        TextView textList = v.findViewById(R.id.textList) ;

        String dataList = "" ;
        int iCnt = 0 ;
        if (isNumber(textTitle.get(position).getTitle())) {
            String pYMD = String.valueOf(year) + String.valueOf(StringUtil.pad(month)) + StringUtil.pad(Integer.parseInt(textTitle.get(position).getTitle()));
            Log.i(TAG, "pYMD=" + pYMD) ;
            DBHandler dbHandler = DBHandler.open(context);
            Cursor rs = dbHandler.selectBaseDate(pYMD);
            listData.clear();
            while (rs.moveToNext()) {
                dataList = rs.getString(rs.getColumnIndex("subject"));
                iCnt++;
            }
            dbHandler.close();
        }
        /* 1건 이상의 경우만 표시 */
        if (iCnt > 1) {
            textList.setText("(" + iCnt + ")" + dataList);
        } else {
            textList.setText(dataList);
        }

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
