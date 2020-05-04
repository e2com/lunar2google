package com.nari.lunar3google;

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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.nari.lunar3google.service.AlarmReceiver;
import com.nari.lunar3google.util.DBHandler;
import com.nari.lunar3google.util.LunarTranser;
import com.nari.lunar3google.util.kakaoToast;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

@SuppressWarnings("deprecation")
public class DeleteData extends Activity {
	String TAG = "DeleteData>>>" ;
	DBHandler dbHandler ;
	int uLunar_ty ;
	String uSubject ;
	String uBase_date ;
	String uBase_time ;
	String uName ;
	String uMobilno ;
	int uLeap_ty ;
	int uSync_stat = 0 ;
    EditText entry_sYear;
    EditText entry_sMon;
    EditText entry_sDay;
    EditText entry_sHour;
    EditText entry_sMin;
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
		setContentView(R.layout.deletedata) ;
		
		Intent intent = getIntent() ;
		final String id = intent.getStringExtra("id") ;

		final EditText deleteSubject = (EditText) findViewById(R.id.deleteSubject) ;
		final EditText deleteName = (EditText) findViewById(R.id.deleteName) ;
		final EditText deleteMobilNo = (EditText) findViewById(R.id.deleteMobilNo) ;
		entry_sYear = (EditText) findViewById(R.id.entry_sYear);
		entry_sMon = (EditText) findViewById(R.id.entry_sMon);
		entry_sDay = (EditText) findViewById(R.id.entry_sDay);
		entry_sHour = (EditText) findViewById(R.id.entry_sHour);
		entry_sMin = (EditText) findViewById(R.id.entry_sMin);
		final CheckBox deleteleap_ty = (CheckBox) findViewById(R.id.chk_lunar) ;
	
		dbHandler = DBHandler.open(this);	
		Cursor cursor = null ;
		cursor = dbHandler.selectId(id) ;
		
		final String subject = cursor.getString(1) ;
		final String base_date = cursor.getString(2) ;
		final int lunar_ty = cursor.getInt(3) ;
		final int leap_ty = cursor.getInt(4) ;
		final int sync_stat = cursor.getInt(5) ;
		final String name = cursor.getString(6) ;
		final String mobil_no = cursor.getString(7) ;
		
		if (leap_ty == 1) {
			deleteleap_ty.setChecked(true) ;
			uLeap_ty = 1 ;
		} else {
			deleteleap_ty.setChecked(false) ;
			uLeap_ty = 0 ;
		}
		
		try {
			Log.d(TAG, subject) ;
			Log.d(TAG, base_date) ;
			Log.d(TAG, String.valueOf(lunar_ty)) ;
			Log.d(TAG, String.valueOf(leap_ty)) ;
			Log.d(TAG, String.valueOf(sync_stat)) ;
			Log.d(TAG, name) ;
			Log.d(TAG, mobil_no) ;
			
		} catch (Exception e) {
			
			Log.d(TAG, e.toString());
			
		}
		
		deleteSubject.setText(subject) ;
		try  {
			entry_sYear.setText(base_date.substring(0, 4));
			entry_sMon.setText(base_date.substring(4, 6));
			entry_sDay.setText(base_date.substring(6, 8));
			
		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}
		
		SharedPreferences pref = getSharedPreferences("lunar2Gugul", 0) ;
        String AlramSetTime = pref.getString("Time", "0800") ;
        int googleId = Integer.parseInt(pref.getString("CalendarID", "3")) ;
        Log.d(TAG, "googleId=" + googleId);
        entry_sHour.setText(AlramSetTime.substring(0, 2));
        entry_sMin.setText(AlramSetTime.substring(2, 4));
		
		if (lunar_ty == 2) {
			RadioButton selectSolar = (RadioButton) findViewById(R.id.selectSolra) ;
			selectSolar.setChecked(true) ;
			uLunar_ty = 2 ;
		} else {
			RadioButton selectLunar = (RadioButton) findViewById(R.id.selectLunar) ;
			selectLunar.setChecked(true) ;
			uLunar_ty = 1 ;
		}
		deleteName.setText(name) ;
		deleteMobilNo.setText(mobil_no) ;

		RadioGroup RadioGroup01 = (RadioGroup) findViewById(R.id.RadioGroup01) ;
		RadioGroup01.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (group.getId() == R.id.RadioGroup01) {
					switch(checkedId) {
					case R.id.selectLunar:
						uLunar_ty = 1 ;
						deleteleap_ty.setEnabled(true);
						break ;
					case R.id.selectSolra:
						uLunar_ty = 2 ;
						deleteleap_ty.setEnabled(false);
						break ;
					}
				}
				
			}
		});
		
		deleteleap_ty.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton chkBox, boolean chk_stat) {
				if (chk_stat) {
					uLeap_ty = 1 ;
				} else {
					uLeap_ty = 0 ;
				}
			}
		});
		
		Button update_btn = (Button) findViewById(R.id.update_btn) ;
		update_btn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				uSubject = deleteSubject.getText().toString() ;
				uBase_date = pad(Integer.parseInt(entry_sYear.getText().toString())) + pad(Integer.parseInt(entry_sMon.getText().toString())) + pad(Integer.parseInt(entry_sDay.getText().toString())) ;
				uBase_time = pad(Integer.parseInt(entry_sHour.getText().toString())) + pad(Integer.parseInt(entry_sMin.getText().toString())) ;
				uName = deleteName.getText().toString() ;
				uMobilno = deleteMobilNo.getText().toString() ;
				uSync_stat = 1 ; // 2011.05.01 부터는 수정할때도 동기화를 시도함...
				long rc = dbHandler.update(id, uSubject, uBase_date, uLunar_ty, uLeap_ty, uSync_stat, uName, uMobilno) ;
				//dbHandler.close() ;
				if(rc <= 0){
					//kakaoToast.makeToast(DeleteData.this, "에러났다", 2000).show();
					//setResult(RESULT_CANCELED);
				}else{ 
					//AlarmSet(base_date, lunar_ty, leap_ty) ; // 알람설정
					CalendarDelete(id, subject) ;
					CalendarWrite(uSubject, uBase_date, uBase_time, uLunar_ty, uLeap_ty, uSync_stat, uName, uMobilno) ;
					//kakaoToast.makeToast(DeleteData.this, "수정완료", 2000).show();
					//setResult(RESULT_OK);
					
				}
				finish();
				
			}
		});		

		Button delete_btn = (Button) findViewById(R.id.delete_btn) ;
		delete_btn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				DialogInterface.OnClickListener mClickLeft =
					new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							
							long rc = dbHandler.delete(id) ;
							//dbHandler.close() ;
							if(rc <= 0){
								//kakaoToast.makeToast(DeleteData.this, "에러났다", 2000).show();
								//setResult(RESULT_CANCELED);
							}else{ 
								//kakaoToast.makeToast(DeleteData.this, "삭제완료", 2000).show();
								CalendarDelete(id, subject) ;
								//setResult(RESULT_OK);
								
							}
							finish();
							
						}
					};
					
	        	DialogInterface.OnClickListener mClickRight =
					new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							finish() ;
						}
					};
					
				new AlertDialog.Builder(DeleteData.this)
		    	.setTitle(getResources().getString(R.string.label_confirm_delete))
		    	.setMessage(getResources().getString(R.string.label_msg_delete))
		    	.setPositiveButton(getResources().getString(R.string.label_btn_continue), mClickLeft)
		    	.setNegativeButton(getResources().getString(R.string.label_cancel_btn), mClickRight)
		    	.show() ;
			}
		});	
		
		Button sendSMS_btn = (Button) findViewById(R.id.sendSMS_btn) ;
		sendSMS_btn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				DialogInterface.OnClickListener mClickLeft =
					new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							
							try {
								uSubject = deleteSubject.getText().toString() ;
								//uBase_date = pad(Integer.parseInt(entry_sYear.toString())) + pad(Integer.parseInt(entry_sMon.toString())) + pad(Integer.parseInt(entry_sDay.toString())) ;
								//uBase_time = pad(Integer.parseInt(entry_sHour.toString())) + pad(Integer.parseInt(entry_sMin.toString())) ;
								uName = deleteName.getText().toString() ;
								uMobilno = deleteMobilNo.getText().toString() ;
								//uSync_stat = sync_stat ;
							
								if (uMobilno.length() > 9) {
									//SmsManager sms = SmsManager.getDefault();
									//sms.sendTextMessage(uMobilno, null, "<" + uName + ">" + uSubject , null, null);
								    
									// SMS 발송 권한을 사용할 수 없기 때문에 기본 문자앱으로 전달만
							        Uri uri = Uri.parse("smsto:" + uMobilno);
									Intent it = new Intent(Intent.ACTION_SENDTO, uri);
									it.putExtra("sms_body", "<" + uName + ">" + uSubject);
									startActivity(it);

									kakaoToast.makeToast(getBaseContext(), getResources().getString(R.string.label_completed_SMS),	Toast.LENGTH_LONG).show() ;
								} else {
									kakaoToast.makeToast(getBaseContext(), getResources().getString(R.string.label_not_found_phone),	Toast.LENGTH_LONG).show() ;
								}
							} catch (Exception e) {
								kakaoToast.makeToast(getBaseContext(), getResources().getString(R.string.label_invalid_phone_no),	Toast.LENGTH_LONG).show() ;
							}
							finish() ;
							
						}
					};
					
	        	DialogInterface.OnClickListener mClickRight =
					new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							finish() ;
						}
					};
				
				new AlertDialog.Builder(DeleteData.this)
		    	.setTitle(getResources().getString(R.string.label_menu_sms))
		    	.setMessage(getResources().getString(R.string.label_msg_pay_sms))
		    	.setPositiveButton(getResources().getString(R.string.label_btn_continue), mClickLeft)
		    	.setNegativeButton(getResources().getString(R.string.label_cancel_btn), mClickRight)
		    	.show() ;
			}
		});

		Button cancel_btn = (Button) findViewById(R.id.cancel_btn) ;
		cancel_btn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				//dbHandler.close() ;
				finish();
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbHandler.close();
	}

	/**
	 * 문자 보내기를 직접 하지 않도록 수정 2020.04.19
	 * @param message
	 * @param attachment
	 */
	public void composeMmsMessage(String message, Uri attachment) {

		Log.e(TAG, "composeMmsMessage===" +  message) ;
/*		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra("sms_body", message);
		intent.putExtra(Intent.EXTRA_STREAM, attachment);
		if (intent.resolveActivity(getPackageManager()) != null) {
			Log.e(TAG, "startActivity...");
			startActivity(intent);
		}*/

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setData(Uri.parse("smsto:"));  // This ensures only SMS apps respond
		intent.putExtra("sms_body", message);
		intent.putExtra(Intent.EXTRA_STREAM, attachment);
		startActivity(intent);

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
		SharedPreferences pref = getSharedPreferences("lunar2Gugul", 0) ;
        String AlramSetTime = pref.getString("Time", "0800") ;
		int startHour = Integer.parseInt(AlramSetTime.substring(0, 2)) ;
        int startMinute = Integer.parseInt(AlramSetTime.substring(2, 4)) ;
		int year = 0 ;
		int month = 0 ;
		int day = 0 ;
		SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.label_yymmdd));
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
			} else { // 그냥 음력r
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

	public void CalendarWrite(String subject, String base_date, String base_time, int lunar_ty, int leap_ty, int sync_stat, String name, String mobil_no) {
		long time = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.label_yymmdd));
		Date dd = new Date(time);
		String timezone = "Asia/Seoul" ;

		SharedPreferences pref = getSharedPreferences("lunar2Gugul", 0) ;
		int googleId = Integer.parseInt(pref.getString("CalendarID", "3")) ;
		
        Calendar calStTime = Calendar.getInstance();
        Calendar calEndTime = Calendar.getInstance();
        calStTime.setTimeZone(TimeZone.getTimeZone(timezone)) ;
        calEndTime.setTimeZone(TimeZone.getTimeZone(timezone)) ;
        
		int startHour = Integer.parseInt(base_time.substring(0, 2).toString()) ;
        int startMinute = Integer.parseInt(base_time.substring(2, 4).toString()) ;
		int year = 0 ;
		int month = 0 ;
		int day = 0 ;
		Log.d(TAG, timezone) ;
		Log.d(TAG, "googleId=" + googleId ) ;
		
		for(int nextYear=0;nextYear<10;nextYear++) { // 앞으로 10년 동안 반복해서 기록하기... 2011.07.29 
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

			Log.d(TAG, chk_date + " Write ...") ;

			calStTime.set(Calendar.YEAR, year);
			calStTime.set(Calendar.MONTH, month);
			calStTime.set(Calendar.DATE, day);
			calStTime.set(Calendar.HOUR_OF_DAY, startHour);
			calStTime.set(Calendar.MINUTE, startMinute);
			calEndTime.set(Calendar.YEAR, year);
			calEndTime.set(Calendar.MONTH, month);
			calEndTime.set(Calendar.DATE, day);
			calEndTime.set(Calendar.HOUR_OF_DAY, startHour);
			calEndTime.set(Calendar.MINUTE, startMinute);
			
			ContentValues cv = new ContentValues();
			cv.put("calendar_id", googleId); // 2017.04.05 시작할때 구해 놓은 ID 을 찾아서
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
		Log.d(TAG,"============================================================================================== write end") ;

	}
	
	@SuppressWarnings("unused")
	public void CalendarDelete(String _id, String subject) {
		if(android.os.Build.VERSION.SDK_INT == 7){ 
			int newevent = getContentResolver().delete(Uri.parse("content://calendar/events"), "title=" + "'" + subject + "'", null);
		}else{ 
			//String DelId = "content://com.android.calendar/events/" + _id ;
			//int newevent = getContentResolver().delete(Uri.parse(DelId),null,null) ;
			int newevent = getContentResolver().delete(Uri.parse("content://com.android.calendar/events"),  "title=" + "'" + subject + "'", null);
		}
		Log.d(TAG,"============================================================================================== delete end") ;
	}
	
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
	
}
