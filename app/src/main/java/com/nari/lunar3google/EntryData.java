package com.nari.lunar3google;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.nari.lunar3google.service.AlarmReceiver;
import com.nari.lunar3google.util.DBHandler;
import com.nari.lunar3google.util.LunarTranser;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class EntryData extends Activity {
	String TAG = "EntryData>>>" ;
	String subject ;
	String base_date ;
	String base_time ;
	String name ;
	String mobilno ;
	int lunar_ty ;
	int leap_ty ;
	int sync_stat = 0 ;
    EditText entry_sYear;
    EditText entry_sMon;
    EditText entry_sDay;
    EditText entry_sHour;
    EditText entry_sMin;
	DBHandler dbHandler ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
		setContentView(R.layout.entrydata) ;
		long time = System.currentTimeMillis();
		Date now = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SharedPreferences pref = getSharedPreferences("lunar2Gugul", 0) ;
        String AlramSetTime = pref.getString("Time", "0800") ;

		dbHandler = DBHandler.open(this)  ;
		final EditText entrySubject = (EditText) findViewById(R.id.entrySubject) ;
		final EditText entryName = (EditText) findViewById(R.id.entryName) ;
		final EditText entryMobilNo = (EditText) findViewById(R.id.entryMobilNo) ;
		entry_sYear = (EditText) findViewById(R.id.entry_sYear);
		entry_sMon = (EditText) findViewById(R.id.entry_sMon);
		entry_sDay = (EditText) findViewById(R.id.entry_sDay);
		entry_sHour = (EditText) findViewById(R.id.entry_sHour);
		entry_sMin = (EditText) findViewById(R.id.entry_sMin);
		final CheckBox entryleap_ty = (CheckBox) findViewById(R.id.chk_lunar) ;
		
		/* 2015.05.04 초기화 날자는 오늘 날자로 설정 */
		entry_sYear.setText(String.valueOf(sdf.format(now).substring(0, 4)));
		entry_sMon.setText(String.valueOf(sdf.format(now).substring(4, 6)));
		entry_sDay.setText(String.valueOf(sdf.format(now).substring(6, 8)));
		entry_sHour.setText(AlramSetTime.substring(0, 2));
		entry_sMin.setText(AlramSetTime.substring(2, 4));
		
		lunar_ty = 1 ; // 음력으로 설정
		leap_ty = 0 ;  // 윤달 아님으로 설정
		
		entryleap_ty.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton chkBox, boolean chk_stat) {
				if (chk_stat) {
					leap_ty = 1 ;
				} else {
					leap_ty = 0 ;
				}
			}
		});
		
		Button save_btn = (Button) findViewById(R.id.save_btn) ;
		RadioGroup RadioGroup01 = (RadioGroup) findViewById(R.id.RadioGroup01) ;
		RadioGroup01.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (group.getId() == R.id.RadioGroup01) {
					switch(checkedId) {
					case R.id.selectLunar:
						lunar_ty = 1 ;
						entryleap_ty.setEnabled(true) ;
						break ;
					case R.id.selectSolra:
						lunar_ty = 2 ;
						entryleap_ty.setEnabled(false) ;
						break ;
					}
				}
				
			}
		});
		
		save_btn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				subject = entrySubject.getText().toString() ;
				name = entryName.getText().toString() ;
				mobilno = entryMobilNo.getText().toString() ;
				
				Log.d(TAG, entry_sYear.getText().toString()) ;
				Log.d(TAG, entry_sMon.getText().toString()) ;
				Log.d(TAG, entry_sDay.getText().toString()) ;
				Log.d(TAG, entry_sHour.getText().toString()) ;
				Log.d(TAG, entry_sMin.getText().toString()) ;

				base_date = pad(Integer.parseInt(entry_sYear.getText().toString())) + pad(Integer.parseInt(entry_sMon.getText().toString())) + pad(Integer.parseInt(entry_sDay.getText().toString())) ;
				base_time = pad(Integer.parseInt(entry_sHour.getText().toString())) + pad(Integer.parseInt(entry_sMin.getText().toString())) ;
				sync_stat = 1 ;
				Log.d(TAG, subject + base_date + lunar_ty + leap_ty + sync_stat + name + mobilno) ;
				long rc = dbHandler.insert(subject, base_date, lunar_ty, leap_ty, sync_stat, name, mobilno) ;
				if(rc <= 0){
					//Toast.makeText(EntryData.this, "에러났다", 2000).show();
					//setResult(RESULT_CANCELED);
				}else{
					//AlarmSet(base_date, base_time, lunar_ty, leap_ty) ; // 알람설정
					CalendarWrite(subject, base_date, base_time, lunar_ty, leap_ty, sync_stat, name, mobilno) ; // 일정 동기화 처리
					//Toast.makeText(EntryData.this, "저장완료", 2000).show();
					//setResult(RESULT_OK);
				}
				finish() ;
			}
		});
		
		findViewById(R.id.btn_sYear_up).setOnClickListener(mClickListener) ;		
		findViewById(R.id.btn_sMon_up).setOnClickListener(mClickListener) ;
		findViewById(R.id.btn_sDay_up).setOnClickListener(mClickListener) ;
		findViewById(R.id.btn_sYear_down).setOnClickListener(mClickListener) ;		
		findViewById(R.id.btn_sMon_down).setOnClickListener(mClickListener) ;
		findViewById(R.id.btn_sDay_down).setOnClickListener(mClickListener) ;		
		findViewById(R.id.btn_sHour_up).setOnClickListener(mClickListener) ;	
		findViewById(R.id.btn_sMin_up).setOnClickListener(mClickListener) ;
		findViewById(R.id.btn_sHour_down).setOnClickListener(mClickListener) ;	
		findViewById(R.id.btn_sMin_down).setOnClickListener(mClickListener) ;		
		
	}
	
	Button.OnClickListener mClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			int sYear = Integer.parseInt(entry_sYear.getText().toString()) ;
			int sMonth = Integer.parseInt(entry_sMon.getText().toString()) ;
			int sDay = Integer.parseInt(entry_sDay.getText().toString()) ;
			int sHour= Integer.parseInt(entry_sHour.getText().toString()) ;
			int sMin = Integer.parseInt(entry_sMin.getText().toString()) ;
			
			switch (v.getId()) {
			case R.id.btn_sYear_up:
				sYear++ ;
				if (sYear > 2999) sYear = 2999 ;
				break;
			case R.id.btn_sYear_down:
				sYear--;
				if (sYear < 1810) sYear = 1810 ;
				break ;
			case R.id.btn_sMon_up:
				sMonth++ ;
				if (sMonth > 12) sMonth = 1 ;
				break ;
			case R.id.btn_sMon_down:
				sMonth--;
				if(sMonth < 1) sMonth = 12;
				break;
			case R.id.btn_sDay_up:
				sDay++ ;
				if (sDay > 31) sDay = 1;
				break;
			case R.id.btn_sDay_down:
				sDay--;
				if (sDay < 1) sDay = 31 ;
				break;
			case R.id.btn_sHour_up:
				sHour++;
				if (sHour>24) sHour = 0 ;
				break;
			case R.id.btn_sHour_down:
				sHour--;
				if (sHour<0) sHour = 24 ;
				break ;
			case R.id.btn_sMin_up:
				sMin++;
				if (sMin>59) sMin=0 ;
				break ;
			case R.id.btn_sMin_down:
				sMin--;
				if (sMin<0) sMin = 59;
				break;				
			}
			
			entry_sYear.setText(String.valueOf(sYear));
			entry_sMon.setText(pad(sMonth));
			entry_sDay.setText(pad(sDay));
			entry_sHour.setText(pad(sHour));
			entry_sMin.setText(pad(sMin));
			
		}
	};
	
	public void CalendarWrite(String subject, String base_date, String base_time, int lunar_ty, int leap_ty, int sync_stat, String name, String mobil_no) {
		long time = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.label_yymmdd));
		Date dd = new Date(time);
		String timezone = "Asia/Seoul" ;

		SharedPreferences pref = getSharedPreferences("lunar2Gugul", 0) ;

        Calendar calStTime = Calendar.getInstance();
        Calendar calEndTime = Calendar.getInstance();
        calStTime.setTimeZone(TimeZone.getTimeZone(timezone)) ;
        calEndTime.setTimeZone(TimeZone.getTimeZone(timezone)) ;

		int startHour = Integer.parseInt(base_time.substring(0, 2).toString()) ;
        int startMinute = Integer.parseInt(base_time.substring(2, 4).toString()) ;
        int endHour = startHour ;
        int endMinute = startMinute ;
		int year = 0 ;
		int month = 0 ;
		int day = 0 ;
		Log.d(">>>",timezone) ;
		
		for(int nextYear=-1;nextYear<10;nextYear++) { // 앞으로 10년 동안 반복해서 기록하기... 2011.07.29 
			int chk_yy = Integer.parseInt( sdf.format(dd).toString().substring(0, 4) ) + nextYear ;
			String chk_date = String.valueOf(chk_yy) + base_date.substring(4, 8) ;
			if (lunar_ty == 1) {
				// 음력기록하기
				if (leap_ty == 1) { // 윤달 음력
					try {
						year = Integer.parseInt( LunarTranser.LunarTranse(chk_date, true).substring(0, 4) ) ;
						month = Integer.parseInt( LunarTranser.LunarTranse(chk_date, true).substring(4, 6) ) ;
						day = Integer.parseInt( LunarTranser.LunarTranse(chk_date, true).substring(6, 8) ) ;
					
					} catch (Exception e) {
						year = Integer.parseInt(chk_date.substring(0, 4)) ;
						month = Integer.parseInt(chk_date.substring(4, 6)) ;
						day = Integer.parseInt(chk_date.substring(6, 8)) ;
					}
				} else { // 그냥 음력
					try {
						year = Integer.parseInt( LunarTranser.LunarTranse(chk_date, false).substring(0, 4) ) ;
						month = Integer.parseInt( LunarTranser.LunarTranse(chk_date, false).substring(4, 6) ) ;
						day = Integer.parseInt( LunarTranser.LunarTranse(chk_date, false).substring(6, 8) ) ;
					} catch (Exception e) {
						year = Integer.parseInt(chk_date.substring(0, 4)) ;
						month = Integer.parseInt(chk_date.substring(4, 6)) ;
						day = Integer.parseInt(chk_date.substring(6, 8)) ;
					}
				}
			} else {
				// 양력도 기록하기
				year = Integer.parseInt(chk_date.substring(0, 4)) ;
				month = Integer.parseInt(chk_date.substring(4, 6)) ;
				day = Integer.parseInt(chk_date.substring(6, 8)) ;
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
			cv.put("calendar_id", Integer.parseInt(pref.getString("CalendarID", "3"))); // 2017.04.05 시작할때 구해 놓은 ID 을 찾아서
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
		}
		
	}
	
	public void onDestroy(Bundle savedInstanceState) {
		try {
			dbHandler.close() ;
		} finally {
			
		}
	}
	
	public void AlarmSet(String base_date, String base_time, int lunar_ty, int leap_ty) {
	    	
	    	long time = System.currentTimeMillis();
	    	Date dd = new Date(time);
	        Calendar calStTime = Calendar.getInstance();
	        
	    	// 알림설정 start	
			final AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
			Intent intent1 ;
			final PendingIntent sender ;
			intent1 = new Intent(this, AlarmReceiver.class) ;
			sender = PendingIntent.getBroadcast(this, 0, intent1, 0) ;
			SharedPreferences pref = getSharedPreferences("lunar2Gugul", 0) ;
	        String AlramSetTime = pref.getString("Time", "0800") ;
	        // 알림설정 end
			int startHour = Integer.parseInt(base_time.substring(0, 2).toString()) ;
	        int startMinute = Integer.parseInt(base_time.substring(2, 4).toString()) ;
			int year = 0 ;
			int month = 0 ;
			int day = 0 ;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일(E)");
			String chk_date = sdf.format(dd).toString().substring(0, 4) + base_date.substring(4, 8) ;
			if (lunar_ty == 1) {
				// 음력기록하기
				if (leap_ty == 1) { // 윤달 음력
					try {
						year = Integer.parseInt( LunarTranser.LunarTranse(chk_date, true).substring(0, 4) ) ;
						month = Integer.parseInt( LunarTranser.LunarTranse(chk_date, true).substring(4, 6) ) ;
						day = Integer.parseInt( LunarTranser.LunarTranse(chk_date, true).substring(6, 8) ) ;
					
					} catch (Exception e) {
						year = Integer.parseInt(chk_date.substring(0, 4)) ;
						month = Integer.parseInt(chk_date.substring(4, 6)) ;
						day = Integer.parseInt(chk_date.substring(6, 8)) ;
					}
				} else { // 그냥 음력
					try {
						year = Integer.parseInt( LunarTranser.LunarTranse(chk_date, false).substring(0, 4) ) ;
						month = Integer.parseInt( LunarTranser.LunarTranse(chk_date, false).substring(4, 6) ) ;
						day = Integer.parseInt( LunarTranser.LunarTranse(chk_date, false).substring(6, 8) ) ;
					} catch (Exception e) {
						year = Integer.parseInt(chk_date.substring(0, 4)) ;
						month = Integer.parseInt(chk_date.substring(4, 6)) ;
						day = Integer.parseInt(chk_date.substring(6, 8)) ;
					}
				}
			} else {
				// 양력도 기록하기
				year = Integer.parseInt(chk_date.substring(0, 4)) ;
				month = Integer.parseInt(chk_date.substring(4, 6)) ;
				day = Integer.parseInt(chk_date.substring(6, 8)) ;
			}
			
			month = month - 1 ; // 달력에 기록되는 월은 0 ~ 11까지로 되어 있는디 이유는 모름...
			
			chk_date = pad(year) + pad(month) + pad(day) ;

			Log.i(">>>", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;
			Log.i("chk_date",chk_date) ;
			Log.i(">>>", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;
			
			calStTime.set(Calendar.YEAR, year);
			calStTime.set(Calendar.MONTH, month);
			calStTime.set(Calendar.DATE, day);
			calStTime.set(Calendar.HOUR_OF_DAY, startHour);
			calStTime.set(Calendar.MINUTE, startMinute);
			// 알림설정 
			am.set(AlarmManager.RTC_WAKEUP, calStTime.getTimeInMillis(), sender) ;
	    	
	}
	    
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
    
    
	
}
