package com.nari.lunar2google;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "myLunarPlan" ;
	private static final int DB_Ver = 1 ;
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_Ver);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table lunarPlan(" +
			"_id integer primary key autoincrement, " +
			"subject text," +
			"base_date text," +
			"lunar_ty integer," +
			"leap_ty integer," +
			"sync_stat integer," +
			"name text," +
			"mobil_no text)";
		db.execSQL(sql) ;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists lunarPlan") ;
		onCreate(db) ;
	}
	
}
