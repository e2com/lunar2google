package com.nari.lunar2google;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;

public class LunarOption extends Activity {

	private static final String TAG = "LunarOption" ;
	TimePicker timeEntry ;

	public void onCreate(Bundle savedInstanceState) {
	    	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lunaroption);
		SharedPreferences pref = getSharedPreferences("lunar2Gugul", 0) ;
		final String time = pref.getString("Time", "0800") ;
		String googleId = pref.getString("CalendarID","") ;
		String googleName = pref.getString("CalendarName","") ;
		String googleType = pref.getString("CalendarType","") ;

		Log.e(TAG, "googleId=" + googleId) ;
		Log.e(TAG, "googleName=" + googleName) ;
		Log.e(TAG, "googleType=" + googleType) ;

		CalendarIdRef calendarIdRef = new CalendarIdRef();
		final ArrayList<String> googleIds = calendarIdRef.CalendarIdRef(LunarOption.this);

		Spinner spinner = findViewById(R.id.googleId) ;
		spinner.setAdapter(new ArrayAdapter<>(LunarOption.this, android.R.layout.simple_spinner_dropdown_item, googleIds));
		spinner.setSelection(Integer.parseInt(googleId));
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				SharedPreferences pref = getSharedPreferences("lunar2Gugul", 0) ;
				SharedPreferences.Editor edit = pref.edit() ;

				edit.putString("CalendarID", String.valueOf(position)) ;
				edit.putString("CalendarName", googleIds.get(position)) ;

				edit.commit() ;
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		timeEntry = (TimePicker) findViewById(R.id.AlramTime) ;
	        try {
	        	timeEntry.setHour(Integer.parseInt(time.substring(0, 2))) ;
	        	timeEntry.setMinute(Integer.parseInt(time.substring(2, 4))) ;
	        } catch(Exception e) {
	        	Toast.makeText(this, time, Toast.LENGTH_LONG).show() ;
	        }

	        Button save_btn = (Button) findViewById(R.id.save_btn) ;
	        save_btn.setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					finish() ;
				}
	        	
	        }) ;
	        
	 }
	 
	 public void onPause() {
		 super.onPause() ;
		 
		 SharedPreferences pref = getSharedPreferences("lunar2Gugul", 0) ;
		 SharedPreferences.Editor edit = pref.edit() ;
		 
		 String time = pad(timeEntry.getHour()) + pad(timeEntry.getMinute()) ;
		 edit.putString("Time", time) ;
		 edit.commit() ;
		 
	 }
	 
	 private static String pad(int c) {
	        if (c >= 10)
	            return String.valueOf(c);
	        else
	            return "0" + String.valueOf(c);
	 }
}
