package com.nari.lunar3google.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class ServiceActivty extends Service {
	boolean mQuit ;

	public void onCreate() {
		super.onCreate();
		
	}
	
	public void onDestroy() {
		super.onDestroy() ;
		
		Toast.makeText(this, "Service onDestroy", Toast.LENGTH_LONG).show() ;
		mQuit = true ;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		mQuit = false ;
		Toast.makeText(this, "Service onStartCommand", Toast.LENGTH_LONG).show() ;
		return startId;
		
	}
	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

}
