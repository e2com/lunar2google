package com.nari.lunar3google.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nari.lunar3google.R;

import java.util.ArrayList;

public class ListTitleAdapter extends BaseAdapter {

    ArrayList<ListTitleData> textTitle  ;
    ArrayList<ListData> listData ;

    public ListTitleAdapter(ArrayList<ListTitleData> pTitle, ArrayList<ListData> pListData) {
        super();
        textTitle = pTitle ;
        listData = pListData ;
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
        ArrayList<ListData> mList = new ArrayList<ListData>() ;
        for(int i=0; i < listData.size();i++) {
            //ListData list_data = new ListData(_id, subject, base_date, name, mobil_no, sync_stat);
            ListData listData1 = new ListData(listData.get(i).getId(),
                                              listData.get(i).getSubject(),
                                              listData.get(i).getBase_date(),
                                              listData.get(i).getName(),
                                              listData.get(i).getMobilno(),
                                              listData.get(i).getSync_stat());
            mList.add(listData1);
        }
        ListDataAdapter listDataAdapter = new ListDataAdapter(mList);
        lvList.setAdapter(listDataAdapter);

        return v;
    }
}
