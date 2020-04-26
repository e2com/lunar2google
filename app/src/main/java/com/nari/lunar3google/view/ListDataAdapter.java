package com.nari.lunar3google.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nari.lunar3google.R;

import java.util.ArrayList;

public class ListDataAdapter extends BaseAdapter {

    private ArrayList<ListData> items = new ArrayList<ListData>();

    public ListDataAdapter(ArrayList<ListData> items) {
        super();
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        Context context = parent.getContext() ;
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row, null);
        }
        ListData p = items.get(position);
        if (p != null) {

            try {
                TextView tsubject = (TextView) v
                        .findViewById(R.id.tSubject);
                TextView tbase_date = (TextView) v
                        .findViewById(R.id.tBasedate);
                TextView tname = (TextView) v.findViewById(R.id.tName);
                TextView tmobilno = (TextView) v
                        .findViewById(R.id.tMobilno);
                TextView tsync_stat = (TextView) v
                        .findViewById(R.id.tSync_stat);

                if (tsubject != null) {
                    tsubject.setText(p.getSubject());
                }
                if (tbase_date != null) {
                    tbase_date.setText(p.getBase_date());
                }
                if (tname != null) {
                    tname.setText(p.getName());
                }
                if (tmobilno != null) {
                    tmobilno.setText(p.getMobilno());
                }
                if (tsync_stat != null) {
                    if (p.getSync_stat() == 1) {
                        tsync_stat.setText(context.getResources().getString(R.string.label_sync) );
                    } else {
                        tsync_stat.setText(context.getResources().getString(R.string.label_no_sync) );
                    }
                }

            } catch (Exception e) {

            }

        }
        return v;
    }

}