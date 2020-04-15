package com.nari.lunar2google;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class LunarOption extends Activity {
	TimePicker timeEntry ;
	public void onCreate(Bundle savedInstanceState) {
	    	
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.lunaroption);
	        SharedPreferences pref = getSharedPreferences("lunar2Gugul", 0) ;
	        String time = pref.getString("Time", "0800") ;
	        
	        timeEntry = (TimePicker) findViewById(R.id.AlramTime) ;
	        try {
	        	timeEntry.setCurrentHour(Integer.valueOf(time.substring(0, 2))) ;
	        	timeEntry.setCurrentMinute(Integer.valueOf(time.substring(2, 4))) ;
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
		 
		 String time = pad(timeEntry.getCurrentHour()) + pad(timeEntry.getCurrentMinute()) ;
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
