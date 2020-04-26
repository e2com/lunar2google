package com.nari.lunar3google.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ServicesManager extends Service implements Runnable {
	private int mStartId ;
	private Handler mHandler ;
	private boolean mRunning ;
	private static final int TIMER_PERIOD = 3 * 1000 ;
	private static final int COUNT = 10 ;
	private int mCounter ;
	
	public void onCreate() {
		Log.e(">>>", "Service Created.") ;
		Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show() ;
		super.onCreate();
		mHandler = new Handler() ;
		mRunning = false ;
	}

	public void onStart(Intent intent, int startId) {
		Log.e(">>>", "Service startId = " + startId) ;
		Toast.makeText(this, "Service startId = " + startId, Toast.LENGTH_LONG).show() ;
		super.onStart(intent, startId) ;
		mStartId = startId ;
		mCounter = COUNT ;
		if (!mRunning) {
			mHandler.postDelayed(this, TIMER_PERIOD);
			mRunning = true ;
		}
	}

	public void onDestroy() {
		mRunning = false ;
		super.onDestroy() ;
	}
	public void run() {
		if (!mRunning) {
			Log.e(">>>", "run after destory") ;
			Toast.makeText(this, "run after destory", Toast.LENGTH_LONG).show() ;
			return ;
		} else if (-mCounter <= 0 ) {
			Log.e("MyServices", "stop Service id = " + mStartId) ;
			stopSelf(mStartId) ;
		} else {
			Log.e("MyServices", "mCounter : " + mCounter) ;
			mHandler.postDelayed(this, TIMER_PERIOD) ;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
