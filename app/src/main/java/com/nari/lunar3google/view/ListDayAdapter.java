package com.nari.lunar3google.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nari.lunar3google.R;

import java.util.ArrayList;

public class ListDayAdapter extends BaseAdapter {

    ArrayList<ListDayData> listDayData = new ArrayList<ListDayData>();
    public ListDayAdapter(ArrayList<ListTitleData> pListDayData) {
        for(int i=0; i < pListDayData.size() ; i++) {
            ListDayData listDayData1 = new ListDayData(pListDayData.get(i).getTitle());
            listDayData.add(listDayData1);
        }
    }

    @Override
    public int getCount() {
        return listDayData.size();
    }

    @Override
    public Object getItem(int position) {
        return listDayData.get(position);
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
            v = lv.inflate(R.layout.content_title, null);
        }
        TextView textView = v.findViewById(R.id.textTitle);
        textView.setText(listDayData.get(position).getTextDayTile());
        return v;
    }
}
