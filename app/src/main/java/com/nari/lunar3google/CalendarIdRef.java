package com.nari.lunar3google;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;

public class CalendarIdRef {

    String TAG = "CalendarIdRef" ;

    public HashMap<String, Integer> CalendarIdRef(Context context) {
        HashMap<String, Integer> googleIds = new HashMap<String, Integer>();
        Uri calendars = null;

        if (android.os.Build.VERSION.SDK_INT == 7) {
            calendars = Uri.parse("content://calendar/calendars");
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendars");
        }

        String[] projection_calendars = null ;
        Cursor Cursor_calendars = null ;
        if (android.os.Build.VERSION.SDK_INT < 14) {
            projection_calendars = new String[] { "_id", "name", "_sync_account_type" };
            Cursor_calendars = context.getContentResolver().query(calendars,	projection_calendars, "selected=1", null, null);
        } else {
            projection_calendars = new String[] {"_id",  "name", "account_type"} ;
            Cursor_calendars = context.getContentResolver().query(calendars, projection_calendars, "visible=1", null, null) ;
        }

        try {
            if (Cursor_calendars.moveToFirst()) {
                boolean chk_google = false;
                int[] _id = new int[Cursor_calendars.getCount()];
                String[] calendars_name = new String[Cursor_calendars.getCount()];
                String[] _sync_account_type = new String[Cursor_calendars.getCount()];
                //String[] timezone = new String[Cursor_calendars.getCount()];

                for (int i = 0; i < calendars_name.length; i++) {
                    _id[i] = Cursor_calendars.getInt(0);
                    calendars_name[i] = Cursor_calendars.getString(1);
                    _sync_account_type[i] = Cursor_calendars.getString(2);
                    //timezone[i]=Cursor_calendars.getString(3);
                    Log.d(TAG, "[" + _id[i] + "][" + calendars_name[i] + "]") ;
                    if (calendars_name[i].indexOf("@gmail.com") > 0) {
                        chk_google = true;
                        SharedPreferences pref = context.getSharedPreferences("lunar2Gugul", 0) ;
                        SharedPreferences.Editor edit = pref.edit() ;
                        edit.putString("CalendarID", String.valueOf(_id[i])) ;
                        edit.putString("CalendarName", calendars_name[i]);
                        edit.putString("CalendarType", _sync_account_type[i]) ;
                        edit.commit() ;
                        if (calendars_name[i] != null) {
                            googleIds.put(calendars_name[i], _id[i]);
                        }
                        Log.e(TAG, "calendars_name=" + calendars_name[i])  ;
                    }
                    Cursor_calendars.moveToNext();
                }
                Cursor_calendars.close();

                if (!chk_google) {

                    DialogInterface.OnClickListener mClickLeft = new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // finish() ;
                        }
                    };

                    DialogInterface.OnClickListener mClickRight = new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                           // finish();
                        }
                    };

                    new AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.label_notify))
                            .setMessage(context.getResources().getString(R.string.label_mesg_not_sync))
                            .setPositiveButton(context.getResources().getString(R.string.label_btn_continue), mClickLeft)
                            .setNegativeButton(context.getResources().getString(R.string.label_cancel_btn), mClickRight).show();
                }

            }
        } catch (Exception e) {

            e.printStackTrace();

            DialogInterface.OnClickListener mClickLeft = new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                   // finish() ;
                }
            };

            DialogInterface.OnClickListener mClickRight = new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                   // finish();
                }
            };

            new AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.label_notify))
                    .setMessage(context.getResources().getString(R.string.label_mesg_not_sync))
                    .setPositiveButton(context.getResources().getString(R.string.label_btn_continue), mClickLeft)
                    .setNegativeButton(context.getResources().getString(R.string.label_cancel_btn), mClickRight).show();
        }

        return googleIds ;
    }

}
