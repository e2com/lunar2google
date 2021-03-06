package com.nari.lunar3google.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHandler {

	private DBHelper helper;
	private SQLiteDatabase db;
	
	public DBHandler(Context ctx) {
		helper = new DBHelper(ctx);
		db = helper.getWritableDatabase(); 
	}
	public static DBHandler open(Context ctx) throws SQLException {
		DBHandler handler = new DBHandler(ctx);
		return handler;
	}
	
	public void close(){
		helper.close();
	}
	
	public long insert(String subject, String base_date, int lunar_ty, int leap_ty, int sync_stat, String name, String mobil_no){
		ContentValues values = new ContentValues();
		values.put("subject", subject) ;
		values.put("base_date", base_date);
		values.put("lunar_ty", lunar_ty);
		values.put("leap_ty", leap_ty) ;
		values.put("sync_stat", sync_stat);
		values.put("name", name);
		values.put("mobil_no", mobil_no);
		
		long result = db.insert("lunarPlan", null, values);
		
		return result;
	}
	
	public Cursor selectAll(){
		//Cursor cursor = db.query(true, "lunarPlan", 
		//	new String[]{"_id", "subject", "base_date", "lunar_ty", "sync_stat", "name", "mobil_no"}, 
		//	null, null, null, null, null, null);
		String sql = "select * from lunarPlan order by _id desc" ;
		Cursor cursor = db.rawQuery(sql, null) ;
		return cursor;
	}
	
	public Cursor selectNotSync(){
		// 달력중에서 sync 되지 않은 것들만 조회 하기.
		String sql = "select * from lunarPlan where sync_stat != '1'" ;
		Cursor cursor = db.rawQuery(sql, null) ;
		return cursor;
	}
	
	public Cursor selectSync(){
		// 달력중에서 sync 되지 않은 것들만 조회 하기.
		String sql = "select * from lunarPlan where sync_stat = '1'" ;
		Cursor cursor = db.rawQuery(sql, null) ;
		return cursor;
	}
	
	public Cursor selectId(String id){
		//Cursor cursor = db.query(true, "lunarPlan", 
		//		new String[]{"_id", "subject", "base_date", "lunar_ty", "sync_stat", "name", "mobil_no"}, 
		//		"_id=" + id, null, null, null, null, null);
		String sql = "select * from lunarPlan where _id = " + id;
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor != null){
			cursor.moveToFirst();
		}
		return cursor;
	}

	/**
	 *
	 * @param BaseDate : 양력날자
	 * @return : 양력/음력에 등록된 일정
	 */
	public Cursor selectBaseDate(String BaseDate){
		String lunar_date1 = "" ;
		try {
			lunar_date1 = LunarTranser.solarTranse(BaseDate) ;
		} catch (Exception e) {
			lunar_date1 = BaseDate ;
		}
		if ("".equals(lunar_date1)) {
			lunar_date1 = BaseDate ;
		}
		Log.d(">>>", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;
		Log.d(">>>", BaseDate + "," + lunar_date1) ;
		// 왜 이렇게는 검색이 한개만 되는 것일까 ???
		String sql = "select * from lunarPlan where (base_date like '%" + BaseDate.substring(4, 8) +
				"' and lunar_ty = '2') or (base_date like '%" + lunar_date1.substring(4, 8) + "' and lunar_ty = '1' ) " +
				" order by base_date" ;
		Log.d(">>>", sql) ;
		Log.d(">>>", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}

	/**
	 * 해당월의 정보만 취득
	 * @param BaseDate
	 * @return
	 */
	public Cursor selectMonth(String BaseDate){
		String lunar_date1 = "" ;
		String lunar_date2 = "" ;
		try {
			lunar_date1 = LunarTranser.LunarTranse(BaseDate, true) ;
		} catch (Exception e) {
			lunar_date1 = BaseDate ;
		}
		try {
			lunar_date2 = LunarTranser.LunarTranse(BaseDate, false) ;
		} catch (Exception e) {
			lunar_date2 = BaseDate ;
		}
		if ("".equals(lunar_date1)) {
			lunar_date1 = BaseDate ;
		}
		if ("".equals(lunar_date2)) {
			lunar_date2 = BaseDate ;
		}
		Log.d(">>>", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;
		Log.d(">>>", BaseDate + "," + lunar_date1 + "," + lunar_date2) ;
		// 왜 이렇게는 검색이 한개만 되는 것일까 ???
		String sql = "select * from lunarPlan where (base_date between '%" + BaseDate.substring(4, 6) +	"01' and '%" + BaseDate.substring(4, 6)	+ "31') "
				+ " or (base_date between '%" + lunar_date1.substring(4, 6) + "01' and '%" + lunar_date1.substring(4, 6) + "31' and lunar_ty = '1' and leap_ty = '1') "
				+ " or (base_date like '%" + lunar_date2.substring(4, 6) + "01' and '%" + lunar_date2.substring(4, 6) + "31' and lunar_ty = '1' and leap_ty = '0') order by base_date" ;

		Log.d(">>>", sql) ;
		Log.d(">>>", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}
	
	public Cursor selectlunar_date(String base_date){
		String sql="select * from lunarPlan where base_date like ?";
		Cursor cursor = db.rawQuery(sql, new String[]{base_date + "%"});
		return cursor;
	}

	public Cursor selectName(String name){
		String sql="select * from lunarPlan where name like ?";
		Cursor cursor = db.rawQuery(sql, new String[]{name + "%"});
		return cursor;
	}

	public Cursor selectSubject(String subject){
		String sql="select * from lunarPlan where subject like ?";
		Cursor cursor = db.rawQuery(sql, new String[]{subject + "%"});
		return cursor;
	}
	
	public long delete(String id){
		//String sql = "delete from person where id=" + id;
		//db.execSQL(sql);
		
		long result = db.delete("lunarPlan", "_id=" +id, null);
		return result;
	}
	
	public long update(String id, String subject, String base_date, int lunar_ty, int leap_ty, int sync_stat, String name, String mobil_no){
		ContentValues values = new ContentValues();
		values.put("subject", subject) ;
		values.put("base_date", base_date);
		values.put("lunar_ty", lunar_ty);
		values.put("leap_ty", leap_ty) ;
		values.put("sync_stat", sync_stat);
		values.put("name", name);
		values.put("mobil_no", mobil_no);
		long result = db.update("lunarPlan", values, "_id=" + id , null);
		return result;
	}	

	public long updateSyncStat(String id, int sync_stat){
		ContentValues values = new ContentValues();
		values.put("sync_stat", sync_stat);
		long result = db.update("lunarPlan", values, "_id=" + id , null);
		return result;
	}
}
