package com.nari.lunar3google.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nari.lunar3google.CalendarView;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intent2 = new Intent(context, CalendarView.class);
		intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) ;
		context.startActivity(intent2) ;
	}
}
