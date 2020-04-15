package com.nari.lunar2google;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class CalendarWrite extends Activity {
	DBHandler dbHandler ;
	int google_index = 0;
	String TAG = "CalendarWrite";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.calendarread);
        
        Uri calendars = null ;
		
		if (android.os.Build.VERSION.SDK_INT == 7) {
        	calendars = Uri.parse("content://calendar/calendars") ;
        } else {
        	calendars = Uri.parse("content://com.android.calendar/calendars") ;
        }

		String[] projection_calendars = null ;
		Cursor Cursor_calendars = null ;
		if (android.os.Build.VERSION.SDK_INT < 14) {
			projection_calendars = new String[] { "_id", "name", "_sync_account_type" };
			Cursor_calendars = getContentResolver().query(calendars,	projection_calendars, "selected=1", null, null);
		} else {
			projection_calendars = new String[] {"_id",  "name", "account_type"} ;
			Cursor_calendars = getContentResolver().query(calendars, projection_calendars, "visible=1", null, null) ;
		}
        
        if(Cursor_calendars.moveToFirst()) {
        	boolean chk_google = false ;
        	int[] _id                   = new int[Cursor_calendars.getCount()];
        	String[] calendars_name     = new String[Cursor_calendars.getCount()];
        	String[] _sync_account_type  = new String[Cursor_calendars.getCount()];
        	
        	for (int i = 0 ; i < calendars_name.length ; i++) {
        		_id[i] = Cursor_calendars.getInt(0);
        		calendars_name[i] = Cursor_calendars.getString(1);
        		_sync_account_type[i] = Cursor_calendars.getString(2);
        		if (calendars_name[i].indexOf("@gmail.com") > 0) {
        			chk_google = true ;
        			google_index = _id[i] ;
                    break;
        		}
        		Cursor_calendars.moveToNext() ;
        	}
        	Cursor_calendars.close();
			Log.d(TAG, "선택된 메일 :" + google_index + "><" + calendars_name[google_index] + ">");
			
			if (!chk_google) {

				DialogInterface.OnClickListener mClickLeft =
					new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							finish() ;
						}
					};
					
	        	DialogInterface.OnClickListener mClickRight =
					new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							finish() ;
						}
					};

				new AlertDialog.Builder(this)
		    	.setTitle(getResources().getString(R.string.label_notify) )
		    	.setMessage(getResources().getString(R.string.label_mesg_not_sync) )
		    	.setPositiveButton(getResources().getString(R.string.label_btn_continue) , mClickLeft)
		    	.setNegativeButton(getResources().getString(R.string.label_cancel_btn) , mClickRight)
		    	.show() ;
			}				
        }
            	
    	DialogInterface.OnClickListener mClickLeft =
			new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					ArrayList<String> arGeneral = new ArrayList<String>() ;
			        dbHandler = DBHandler.open(CalendarWrite.this)  ;
			        
			        long time = System.currentTimeMillis();
					SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.label_yymmdd) );
					Date dd = new Date(time);
					String timezone = "Asia/Seoul" ;
					
			        Calendar calStTime = Calendar.getInstance();
			        Calendar calEndTime = Calendar.getInstance();
			        calStTime.setTimeZone(TimeZone.getTimeZone(timezone)) ;
			        calEndTime.setTimeZone(TimeZone.getTimeZone(timezone)) ;
			        
			        SharedPreferences pref = getSharedPreferences("lunar2Gugul", 0) ;
			        String AlramSetTime = pref.getString("Time", "0800") ;
					int startHour = Integer.valueOf(AlramSetTime.substring(0, 2)) ;
			        int startMinute = Integer.valueOf(AlramSetTime.substring(2, 4)) ;
			        int endHour = startHour ;
			        int endMinute = startMinute ;
					int year = 0 ;
					int month = 0 ;
					int day = 0 ;
					
			        Cursor cursor = null ;
					cursor = dbHandler.selectNotSync() ;
					int count = 0 ;
					while (cursor.moveToNext()) {
						String _id = cursor.getString(0) ;
						String subject = cursor.getString(1) ;
						String base_date = cursor.getString(2) ;
						int lunar_ty = cursor.getInt(3) ;
						int leap_ty = cursor.getInt(4) ;
						String name = cursor.getString(6) ;
						String mobil_no = cursor.getString(7) ;
						
						for(int nextYear=0;nextYear < 10 ; nextYear++) { // 앞으로 10년 동안 반복해서 기록하기... 2011.07.29 
							int chk_yy = Integer.valueOf( sdf.format(dd).toString().substring(0, 4) ) + nextYear ;
							String chk_date = String.valueOf(chk_yy) + base_date.substring(4, 8) ;
							if (lunar_ty == 1) {
								// 음력기록하기
								if (leap_ty == 1) { // 윤달 음력
									try {
										year = Integer.valueOf( LunarTranser.LunarTranse(chk_date, true).substring(0, 4) ) ;
										month = Integer.valueOf( LunarTranser.LunarTranse(chk_date, true).substring(4, 6) ) ;
										day = Integer.valueOf( LunarTranser.LunarTranse(chk_date, true).substring(6, 8) ) ;
									
									} catch (Exception e) {
										year = Integer.valueOf(chk_date.substring(0, 4)) ;
										month = Integer.valueOf(chk_date.substring(4, 6)) ;
										day = Integer.valueOf(chk_date.substring(6, 8)) ;
									}
								} else { // 그냥 음력
									try {
										year = Integer.valueOf( LunarTranser.LunarTranse(chk_date, false).substring(0, 4) ) ;
										month = Integer.valueOf( LunarTranser.LunarTranse(chk_date, false).substring(4, 6) ) ;
										day = Integer.valueOf( LunarTranser.LunarTranse(chk_date, false).substring(6, 8) ) ;
									} catch (Exception e) {
										year = Integer.valueOf(chk_date.substring(0, 4)) ;
										month = Integer.valueOf(chk_date.substring(4, 6)) ;
										day = Integer.valueOf(chk_date.substring(6, 8)) ;
									}
								}
							} else {
								// 양력도 기록하기
								year = Integer.valueOf(chk_date.substring(0, 4)) ;
								month = Integer.valueOf(chk_date.substring(4, 6)) ;
								day = Integer.valueOf(chk_date.substring(6, 8)) ;
							}
							
							month = month - 1 ; // 달력에 기록되는 월은 0 ~ 11까지로 되어 있는디 이유는 모름...
							
							chk_date = pad(year) + pad(month) + pad(day) ;
							
							calStTime.set(Calendar.YEAR, year);
							calStTime.set(Calendar.MONTH, month);
							calStTime.set(Calendar.DATE, day);
							calStTime.set(Calendar.HOUR_OF_DAY, startHour);
							calStTime.set(Calendar.MINUTE, startMinute);
							calEndTime.set(Calendar.YEAR, year);
							calEndTime.set(Calendar.MONTH, month);
							calEndTime.set(Calendar.DATE, day);
							calEndTime.set(Calendar.HOUR_OF_DAY, endHour);
							calEndTime.set(Calendar.MINUTE, endMinute);
							
							ContentValues cv = new ContentValues();
							cv.put("calendar_id", google_index); // 2017.03.30 계정 이름에 @gmail.com 이 있는 것 ... 계정이 2개이면 처음꺼 : 내폰에 등록한 처음 계정
							cv.put("title", subject); 
							long startTime = calStTime.getTimeInMillis(); 
							long endTime = calEndTime.getTimeInMillis(); 
							cv.put("dtstart", startTime); // start time <string dd-mm-yyyy tt:mm:ss> 
							cv.put("dtend", endTime); // end time <string dd-mm-yyyy tt:mm:ss> 
							cv.put("selfAttendeeStatus", 0) ;
							cv.put("allDay", 0); // 종일 일정 아님... 종일일정으로 하는 경우 양력은 전일자로 들어감...
							//cv.put("visibility", 0) ; // ICS 4.0 에는 이 항목이 없는 것 같다... kdy 2012.05.07
							//cv.put("transparency", 0) ; // ICS 4.0 에는 이 항목이 없는 것 같다... kdy 2012.05.07
							cv.put("hasAlarm", 0); // 0 for false, 1 for true
							cv.put("eventTimezone", timezone ) ; // timezone 이 설정이 되지 않으면 오류가 난다... 일정에서 수정할 때...
							cv.put("hasExtendedProperties", 0) ;
							cv.put("eventStatus", 1); 
							cv.put("hasAttendeeData", 0) ;
							cv.put("guestsCanModify", 0) ;
							cv.put("guestsCanInviteOthers", 1) ;
							cv.put("guestsCanSeeGuests", 1) ;
							cv.put("deleted", 0) ;
							cv.put("eventLocation", mobil_no); 
							cv.put("description", name) ;
							
							if(android.os.Build.VERSION.SDK_INT == 7){ 
								@SuppressWarnings("unused")
                                Uri newevent = getContentResolver().insert(Uri.parse("content://calendar/events"), cv);
							}else{ 
								@SuppressWarnings("unused")
                                Uri newevent = getContentResolver().insert(Uri.parse("content://com.android.calendar/events"), cv);
							}

							Log.d(TAG, "[" + google_index + "][" + subject + "]") ;
	
							long rc = dbHandler.updateSyncStat(_id,	1) ;
							if(rc <= 0){
								// Toast.makeText(CalendarWrite.this, "Calendar Write 에러", Toast.LENGTH_LONG).show();
								setResult(RESULT_CANCELED);
							}else{ 
								//AlarmSet(base_date, lunar_ty, leap_ty) ; // 알람설정
								//Toast.makeText(CalendarWrite.this, "Calendar Write 기록중...\n 잠시만 기달려주세요...", Toast.LENGTH_LONG).show();
								setResult(RESULT_OK);
							}
						}
						arGeneral.add(subject + "[" + padDate(base_date) + "] " + getResources().getString(R.string.label_msg_write_calendar)) ;
						count++ ;
					}
					cursor.close() ;
					dbHandler.close() ;
					
					ArrayAdapter<String> Adapter ;
			        Adapter = new ArrayAdapter<String>( CalendarWrite.this, android.R.layout.simple_list_item_1, arGeneral) ;
			        ListView list = (ListView)findViewById(R.id.ListView) ;
			        list.setAdapter(Adapter) ;

			        new AlertDialog.Builder(CalendarWrite.this)
			        .setTitle(getResources().getString(R.string.label_write_calendar))
			        .setMessage(getResources().getString(R.string.label_msg_write_calendar))
			        .setPositiveButton(getResources().getString(R.string.label_close), new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							finish() ;
							
						}
					}) ;
				}
			};
			
		DialogInterface.OnClickListener mClickRight=
			new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					ArrayList<String> arGeneral = new ArrayList<String>() ;
			        dbHandler = DBHandler.open(CalendarWrite.this)  ;
			        				
			        Cursor cursor = null ;
					cursor = dbHandler.selectSync() ;
					int count = 0 ;
					while (cursor.moveToNext()) {
						String _id = cursor.getString(0) ;
						String subject = cursor.getString(1) ;
						String base_date = cursor.getString(2) ;
						
						if(android.os.Build.VERSION.SDK_INT == 7){ 
							@SuppressWarnings("unused")
							int newevent = getContentResolver().delete(Uri.parse("content://calendar/events"), "title=" + "'" + subject + "'", null);
						}else{ 
							@SuppressWarnings("unused")
							//String DelId = "content://com.android.calendar/events/" + _id ;
							//int newevent = getContentResolver().delete(Uri.parse(DelId),null,null) ;
							int newevent = getContentResolver().delete(Uri.parse("content://com.android.calendar/events"),  "title=" + "'" + subject + "'", null);
						} 
						
						long rc = dbHandler.updateSyncStat(_id,	0) ;
						if(rc <= 0){
							//Toast.makeText(CalendarWrite.this, "Calendar 제거 에러", Toast.LENGTH_LONG).show();
							setResult(RESULT_CANCELED);
						}else{ 
							//Toast.makeText(CalendarWrite.this, "Calendar 제거중...\n 잠시만 기달려주세요...", Toast.LENGTH_LONG).show();
							setResult(RESULT_OK);
						}
						
						arGeneral.add(subject + "[" + padDate(base_date) + "] " + getResources().getString(R.string.label_msg_delete_calendar)) ;
						count++ ;
					}
					cursor.close() ;
					dbHandler.close() ;
					
					ArrayAdapter<String> Adapter ;
			        Adapter = new ArrayAdapter<String>( CalendarWrite.this, android.R.layout.simple_list_item_1, arGeneral) ;
			        ListView list = (ListView)findViewById(R.id.ListView) ;
			        list.setAdapter(Adapter) ;
			        	
			        new AlertDialog.Builder(CalendarWrite.this)
			        .setTitle(getResources().getString(R.string.label_delete_calendar))
			        .setMessage(getResources().getString(R.string.label_msg_delete_calendar))
			        .setPositiveButton(getResources().getString(R.string.label_close), new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							finish() ;
							
						}
					}) ;
				}
			};
		
    	new AlertDialog.Builder(this)
    	.setTitle(getResources().getString(R.string.label_sync_calendar))
    	.setMessage(getResources().getString(R.string.label_sync_start))
    	.setPositiveButton(getResources().getString(R.string.label_write_calendar), mClickLeft)
    	.setNegativeButton(getResources().getString(R.string.label_delete_calendar), mClickRight)
    	.show() ;
    	
    }
    
    public void AlarmSet(String base_date, int lunar_ty, int leap_ty) {
    	
    	long time = System.currentTimeMillis();
    	Date dd = new Date(time);
        Calendar calStTime = Calendar.getInstance();
        
    	// 알림설정 start	
		final AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent intent1 ;
		final PendingIntent sender ;
		intent1 = new Intent(this, AlarmReceiver.class) ;
		sender = PendingIntent.getBroadcast(this, 0, intent1, 0) ;
        // 알림설정 end
		SharedPreferences pref = getSharedPreferences("lunar2Gugul", 0) ;
        String AlramSetTime = pref.getString("Time", "0800") ;
		int startHour = Integer.valueOf(AlramSetTime.substring(0, 2)) ;
        int startMinute = Integer.valueOf(AlramSetTime.substring(2, 4)) ;
		int year = 0 ;
		int month = 0 ;
		int day = 0 ;
		SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.label_yymmdd));
		String chk_date = sdf.format(dd).toString().substring(0, 4) + base_date.substring(4, 8) ;
		if (lunar_ty == 1) {
			// 음력기록하기
			if (leap_ty == 1) { // 윤달 음력
				try {
					year = Integer.valueOf( LunarTranser.LunarTranse(chk_date, true).substring(0, 4) ) ;
					month = Integer.valueOf( LunarTranser.LunarTranse(chk_date, true).substring(4, 6) ) ;
					day = Integer.valueOf( LunarTranser.LunarTranse(chk_date, true).substring(6, 8) ) ;
				
				} catch (Exception e) {
					year = Integer.valueOf(chk_date.substring(0, 4)) ;
					month = Integer.valueOf(chk_date.substring(4, 6)) ;
					day = Integer.valueOf(chk_date.substring(6, 8)) ;
				}
			} else { // 그냥 음력
				try {
					year = Integer.valueOf( LunarTranser.LunarTranse(chk_date, false).substring(0, 4) ) ;
					month = Integer.valueOf( LunarTranser.LunarTranse(chk_date, false).substring(4, 6) ) ;
					day = Integer.valueOf( LunarTranser.LunarTranse(chk_date, false).substring(6, 8) ) ;
				} catch (Exception e) {
					year = Integer.valueOf(chk_date.substring(0, 4)) ;
					month = Integer.valueOf(chk_date.substring(4, 6)) ;
					day = Integer.valueOf(chk_date.substring(6, 8)) ;
				}
			}
		} else {
			// 양력도 기록하기
			year = Integer.valueOf(chk_date.substring(0, 4)) ;
			month = Integer.valueOf(chk_date.substring(4, 6)) ;
			day = Integer.valueOf(chk_date.substring(6, 8)) ;
		}
		
		month = month - 1 ; // 달력에 기록되는 월은 0 ~ 11까지로 되어 있는디 이유는 모름...
		
		chk_date = pad(year) + pad(month) + pad(day) ;
		
		calStTime.set(Calendar.YEAR, year);
		calStTime.set(Calendar.MONTH, month);
		calStTime.set(Calendar.DATE, day);
		calStTime.set(Calendar.HOUR_OF_DAY, startHour);
		calStTime.set(Calendar.MINUTE, startMinute);
		// 알림설정 
		am.set(AlarmManager.RTC_WAKEUP, calStTime.getTimeInMillis(), sender) ;
    	
    }
    
    public static String padDate(String iDate) {
		String return_value = "" ;
		try {
			return_value = iDate.substring(0, 4) + "-" + iDate.substring(4, 6) + "-" + iDate.substring(6, 8);
		} catch (Exception e) {
			return_value = iDate ;
		}
		return return_value ;
	}
    
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

}
