package com.nari.lunar2google;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * admob 광고 추가
 * 앱 ID: ca-app-pub-5706840078904135~7939326835
 * 광고 단위 ID: ca-app-pub-5706840078904135/7570896839
 */
public class LunarGugul extends AppCompatActivity {
	static DBHandler dbHandler;
	final static int ACT_EDIT = 0;
	static String chk_date = "";
	@SuppressWarnings("unused")
	private Calendar am;
	/** Called when the activity is first created. */

	String TAG = "LunarGugul";
	ArrayList<String> chkPermission = new ArrayList<String>() ;
	final static int PERMISSION_REQUEST_CODE = 1000 ;
	TextView hello ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		hello = findViewById(R.id.hello); // TextView 글자 넣기

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(LunarGugul.this, EntryData.class);
				intent.putExtra("TextIn", "test");
				startActivityForResult(intent, ACT_EDIT);
			}
		});

		MobileAds.initialize(this, new OnInitializationCompleteListener() {
			@Override
			public void onInitializationComplete(InitializationStatus initializationStatus) {
			}
		});

		// adMob 광고 추가 2017.04.23
        //MobileAds.initialize(getApplicationContext(), "ca-app-pub-5706840078904135~7939326835");
        AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adReqeust = new AdRequest.Builder().build();
		mAdView.loadAd(adReqeust);
        // end adMob

	}

	@Override
	public void onResume() {
		super.onResume();

		if (checkFunction("READ_CALENDAR")) {
			onRefreshData() ;
		}

	}

	public boolean onRefreshData() {
		boolean bResult = false ;

		long time = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.label_yymmdd));
		Date dd = new Date(time);
		try {
			hello.append(sdf.format(dd) + getResources().getString(R.string.label_lunar2)
					+ padDate(LunarTranser.solarTranse(parse2Date())) + "]");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		Uri calendars = null;

		if (android.os.Build.VERSION.SDK_INT == 7) {
			calendars = Uri.parse("content://calendar/calendars");
		} else {
			calendars = Uri.parse("content://com.android.calendar/calendars");
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

		try {
			if (Cursor_calendars.moveToFirst()) {
				boolean chk_google = false;
				int[] _id = new int[Cursor_calendars.getCount()];
				String[] calendars_name = new String[Cursor_calendars.getCount()];
				String[] _sync_account_type = new String[Cursor_calendars.getCount()];
				//String[] timezone = new String[Cursor_calendars.getCount()];

				for (int i = 0; i < calendars_name.length; i++) {
					_id[i] = Cursor_calendars.getInt(0);
					calendars_name[i] = Cursor_calendars.getString(1);
					_sync_account_type[i] = Cursor_calendars.getString(2);
					//timezone[i]=Cursor_calendars.getString(3);
					Log.d(TAG, "[" + _id[i] + "][" + calendars_name[i] + "]") ;
					if (calendars_name[i].indexOf("@gmail.com") > 0) {
						chk_google = true;
						SharedPreferences pref = getSharedPreferences("lunar2Gugul", 0) ;
						SharedPreferences.Editor edit = pref.edit() ;
						edit.putString("CalendarID", String.valueOf(_id[i])) ;
						edit.commit() ;
						break ; // 2017.04.05 한개만 찾으면 되기 때문에
					}
					Cursor_calendars.moveToNext();
				}
				Cursor_calendars.close();

				if (!chk_google) {

					DialogInterface.OnClickListener mClickLeft = new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// finish() ;
						}
					};

					DialogInterface.OnClickListener mClickRight = new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					};

					new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.label_notify))
							.setMessage(getResources().getString(R.string.label_mesg_not_sync))
							.setPositiveButton(getResources().getString(R.string.label_btn_continue), mClickLeft)
							.setNegativeButton(getResources().getString(R.string.label_cancel_btn), mClickRight).show();
				}

			}
		} catch (Exception e) {

			DialogInterface.OnClickListener mClickLeft = new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					finish() ;
				}
			};

			DialogInterface.OnClickListener mClickRight = new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			};

			new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.label_notify))
					.setMessage(getResources().getString(R.string.label_mesg_not_sync))
					.setPositiveButton(getResources().getString(R.string.label_btn_continue), mClickLeft)
					.setNegativeButton(getResources().getString(R.string.label_cancel_btn), mClickRight).show();
		}

		doDisplayData();

		return bResult ;
	}

	public boolean checkFunction(String option){
		boolean bResult = true ;
		chkPermission.clear();

		int permissioninfo = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) ;
		if (permissioninfo != PackageManager.PERMISSION_GRANTED) {
			chkPermission.add(Manifest.permission.WRITE_CALENDAR);
		}
		if ("READ_CALENDAR".equals(option)) {
			permissioninfo = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
			if (permissioninfo != PackageManager.PERMISSION_GRANTED) {
				chkPermission.add(Manifest.permission.READ_CALENDAR);
			}
		}
		permissioninfo = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) ;
		if (permissioninfo != PackageManager.PERMISSION_GRANTED) {
			chkPermission.add(Manifest.permission.SEND_SMS);
		}
		permissioninfo = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) ;
		if (permissioninfo != PackageManager.PERMISSION_GRANTED) {
			chkPermission.add(Manifest.permission.READ_SMS);
		}
		permissioninfo = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) ;
		if (permissioninfo != PackageManager.PERMISSION_GRANTED) {
			chkPermission.add(Manifest.permission.ACCESS_WIFI_STATE);
		}
		permissioninfo = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) ;
		if (permissioninfo != PackageManager.PERMISSION_GRANTED) {
			chkPermission.add(Manifest.permission.INTERNET);
		}
		/*
		* manifest 에 설정 추가 필요 android 10 부터 : 임시로
		*          android:requestLegacyExternalStorage="true"
		* */
		if ("WRITE_EXTERNAL_STORAGE".equals(option)) {
			permissioninfo = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
			if (permissioninfo != PackageManager.PERMISSION_GRANTED) {
				chkPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			}
		}
		if ("READ_EXTERNAL_STORAGE".equals(option)) {
			permissioninfo = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
			if (permissioninfo != PackageManager.PERMISSION_GRANTED) {
				chkPermission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
			}
		}
		permissioninfo = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) ;
		if (permissioninfo != PackageManager.PERMISSION_GRANTED) {
			chkPermission.add(Manifest.permission.ACCESS_NETWORK_STATE);
		}
		if (chkPermission.size() > 0) {
			String strArray[] = new String[chkPermission.size()] ;
			strArray = chkPermission.toArray(strArray) ;
			ActivityCompat.requestPermissions(this, strArray, PERMISSION_REQUEST_CODE);
		}

		return bResult ;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_menu_append:
			Intent intent = new Intent(this, EntryData.class);
			intent.putExtra("TextIn", "test");
			startActivityForResult(intent, ACT_EDIT);
			return true;
		case R.id.action_menu_sync_schedule:
			// Toast.makeText(this, "일정에 일괄 등록합니다.", Toast.LENGTH_SHORT).show()
			// ;
			Intent intent2 = new Intent(this, CalendarWrite.class);
			intent2.putExtra("Textin", "CalendarWrite");
			startActivityForResult(intent2, ACT_EDIT);
			return true;
		case R.id.action_menu_about:
			new AlertDialog.Builder(this)
					.setTitle(getResources().getString(R.string.app_name))
					.setMessage(
							getResources().getString(R.string.label_mesg_1) +
							"\n http://6k2emg.blog.me \n" + getResources().getString(R.string.label_mesg_2) + "\n" +
                                    getResources().getString(R.string.label_mesg_3) + " \n " +
                                    getResources().getString(R.string.label_mesg_4) + ": www.androidicons.com")
					.setIcon(R.drawable.ic_menu_wizard).setCancelable(false)
					.setNegativeButton(getResources().getString(R.string.label_close), null).show();
			
			return true;
		case R.id.action_menu_get_schedule:
			Intent intent4 = new Intent(this, CalendarRead.class);
			intent4.putExtra("Textin", "CalendarRead");
			startActivityForResult(intent4, ACT_EDIT);
			return true;
		case R.id.action_menu_today:
			Intent intent5 = new Intent(this, CalendarView.class);
			intent5.putExtra("Textin", "CalendarView");
			startActivityForResult(intent5, ACT_EDIT);
			return true;
		case R.id.action_menu_backup:
			if (checkFunction("WRITE_EXTERNAL_STORAGE")) {
				if (isExternalStorageAvail()) {
					new ExportDatabaseTask().execute();
					SystemClock.sleep(1000);
				} else {
					Toast.makeText(LunarGugul.this,
							getResources().getString(R.string.label_not_used_backup), Toast.LENGTH_SHORT)
							.show();
				}
			}
			/*  외부파일에 기록하는 것은 나중에 db layout 이 변동되면 그걸 해소하기 위한 방법으로 다가
			try {
				FileOutputStream fos = openFileOutput("lunarPlan.txt", Context.MODE_PRIVATE ) ;
				dbHandler = DBHandler.open(this);
				Cursor cursor = null;
				cursor = dbHandler.selectAll();
				while (cursor.moveToNext()) {
					String _id = cursor.getString(0);
					String subject = cursor.getString(1);
					String base_date = cursor.getString(2);
					int lunar_ty = cursor.getInt(3);
					int leap_ty = cursor.getInt(4);
					int sync_stat = cursor.getInt(5);
					String name = cursor.getString(6);
					String mobil_no = padTelno(cursor.getString(7));
					String TextData = _id + ";" + subject + ";" + base_date + ";" + String.valueOf(lunar_ty) + ";" + String.valueOf(leap_ty) + ";" + String.valueOf(sync_stat) + ";" + name + ";" + mobil_no + ";";
					fos.write(TextData.getBytes());
				}
				fos.close() ;
				cursor.close();
				dbHandler.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			return true;

		case R.id.action_menu_restore:
			if (checkFunction("READ_EXTERNAL_STORAGE")) {
				if (isExternalStorageAvail()) {
					new ImportDatabaseTask().execute();
					SystemClock.sleep(500);
				} else {
					Toast.makeText(LunarGugul.this,
							getResources().getString(R.string.label_not_used_backup), Toast.LENGTH_SHORT)
							.show();
				}
			}
			/* 외부파일에 기록하는 것은 나중에 db layout 이 변동되면 그걸 해소하기 위한 방법으로 다가
			try {
				FileInputStream fis = openFileInput("lunarPlan.txt") ;
				byte[] data = new byte[fis.available()];
				String TextData = "" ;
				while(fis.read(data) != -1) {
					TextData = new String(data) ;
					new AlertDialog.Builder(this)
					.setTitle("구글로 간 음력")
					.setMessage(TextData)
					.setIcon(R.drawable.ic_menu_wizard).setCancelable(false)
					.setNegativeButton("닫기", null).show();
				}
				fis.close() ;
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			
			return true;
		case R.id.action_menu_set_alarm_time:
			Intent intent8 = new Intent(this, LunarOption.class);
			intent8.putExtra("Textin", "LunarOption");
			startActivityForResult(intent8, ACT_EDIT);

		case R.id.action_menu_test_alarm:
			
			AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
			Intent intent1 ;
			PendingIntent sender ;
			
			intent1 = new Intent(LunarGugul.this, AlarmReceiver.class) ;
			sender = PendingIntent.getBroadcast(this, 0, intent1, 0) ;
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis()) ;
			calendar.add(Calendar.SECOND, 10);
			
			am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender) ;
			
			Log.i("TAG", "TTT") ;
			/* 2011.04.23 요긴 안드로이드 표준의 SMS 기록 및 출력 방법ㅇㅁ...
			Calendar time = null ;
			ContentValues val = new ContentValues();
			val.put("address","011-289-7213");
			val.put("date",new Long(time.getTimeInMillis()));
			val.put("read", 1);
			val.put("status",-1);
			val.put("type",2);
			val.put("body", "문자간다!!");
			Uri inserted = getContentResolver().insert(Uri.parse("content://sms"), val);
			if (inserted != null) {
				Toast.makeText(LunarGugul.this,"sms Inserted", Toast.LENGTH_LONG).show() ;
			} else {
				Toast.makeText(LunarGugul.this,"sms Inserted failed", Toast.LENGTH_LONG).show() ;
			}
			// sms 가지고 오기...~ 
			ContentResolver r = getContentResolver() ;
			Cursor result = r.query(Uri.parse("content://sms"), null, null, null, null) ;
			
			startManagingCursor(result) ;
			String body = null ;
			String number = null ;
			
			if (result.moveToFirst()) {
				body = result.getString(result.getColumnIndexOrThrow("body")).toString() ;
				number = result.getString(result.getColumnIndexOrThrow("address")).toString();
				Toast.makeText(this, body + "[" + number + "]", Toast.LENGTH_LONG).show() ;
			}
			while (result.moveToNext()) {
				body = result.getString(result.getColumnIndexOrThrow("body")).toString() ;
				number = result.getString(result.getColumnIndexOrThrow("address")).toString();
				Toast.makeText(this, body + "[" + number + "]", Toast.LENGTH_LONG).show() ;
			}
			result.close() ;
			*/
			Log.i("TAG", "TTT") ;
			
			return true ;
		}
		return false;
	}

	/**
	 * 외장 MicroSD Card 사용 가능 한가?
	 * @return
	 */
	private boolean isExternalStorageAvail() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/*

    /**
     * 백업 하기
     */
	private class ExportDatabaseTask extends AsyncTask<Void, Void, Boolean> {
		private final ProgressDialog dialog = new ProgressDialog(LunarGugul.this);

		// can use UI thread here
		@Override
		protected void onPreExecute() {
			dialog.setMessage(getString(R.string.msgBackup));
			dialog.show();
		}

		// automatically done on worker thread (separate from UI thread)
		@Override
		protected Boolean doInBackground(final Void... args) {

			String strPath = Environment.getExternalStorageDirectory().getAbsolutePath() ;
			Log.e(TAG, "filePath=" + strPath) ;

			File dbFile = new File(Environment.getDataDirectory() + "/data/com.nari.lunar2google/databases/myLunarPlan");
			File exportDir = new File(Environment.getExternalStorageDirectory(), "lunar2google");
			Log.e(TAG, "BACKUP DIR=" + exportDir.getAbsolutePath());
			if (!exportDir.exists()) {
				exportDir.mkdirs();
			}
			File file = new File(exportDir, dbFile.getName() + ".sqlite"); // 2019.05.29 확장자를 지정하다.

			try {
				file.createNewFile();
				FileUtil.copyFile(dbFile, file);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "BACKUP ERROR=" + e.toString()) ;
				return false;
			}
		}

		// can use UI thread here
		@Override
		protected void onPostExecute(final Boolean success) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			if (success) {
				Toast.makeText(LunarGugul.this, getString(R.string.msgBackupCompiled), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(LunarGugul.this, getString(R.string.msgNotBackup), Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 *
	 */
	private class ImportDatabaseTask extends AsyncTask<Void, Void, String> {
		private final ProgressDialog dialog = new ProgressDialog(LunarGugul.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage(getResources().getString(R.string.label_restore));
			dialog.show();
		}

		// could pass the params used here in AsyncTask<String, Void, String> - but not being re-used
		@Override
		protected String doInBackground(final Void... args) {

			File dbBackupFile = new File( Environment.getExternalStorageDirectory() + "/lunar2google/myLunarPlan.sqlite");
			Log.e(TAG, "RESTORE FILE=" +dbBackupFile.getAbsolutePath());
			if (!dbBackupFile.exists()) {
				return getResources().getString(R.string.mesg_restore_err);
			} else if (!dbBackupFile.canRead()) {
				return getResources().getString(R.string.mesg_restore_err);
			}

			File dbFile = new File(Environment.getDataDirectory() + "/data/com.nari.lunar2google/databases/myLunarPlan");
			if (dbFile.exists()) {
				dbFile.delete();
			}

			try {
				dbFile.createNewFile();
				FileUtil.copyFile(dbBackupFile, dbFile);
				return null;
			} catch (IOException e) {
				Log.e(TAG, "RESTORE ERROR=" + e.toString()) ;
				return e.toString() ;
			}
		}

		@Override
		protected void onPostExecute(final String errMsg) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			if (errMsg == null) {
				Toast.makeText(LunarGugul.this, getResources().getString(R.string.mesg_restore_ok), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(LunarGugul.this, getResources().getString(R.string.mesg_restore_err) + errMsg, Toast.LENGTH_SHORT).show();
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		doDisplayData();
    }

	/**
	 * 등록된 데이터 조회 처리
	 */
	public void doDisplayData() {
		final ArrayList<ListData> m_lists = new ArrayList<ListData>();
		ListView list = (ListView) findViewById(R.id.ListView);

		long time = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.label_yymmdd));
		Date dd = new Date(time);

		dbHandler = DBHandler.open(this);
		Cursor cursor = null;
		cursor = dbHandler.selectAll();
		int count = 0;
		while (cursor.moveToNext()) {
			String _id = cursor.getString(0);
			String subject = cursor.getString(1);
			String base_date = cursor.getString(2);
			int lunar_ty = cursor.getInt(3);
			int leap_ty = cursor.getInt(4);
			int sync_stat = cursor.getInt(5);
			if (lunar_ty == 2) {
				try {
					base_date = padDate(base_date)
							+ getResources().getString(R.string.label_lunar2)
							+ padDate(LunarTranser.solarTranse(base_date)
							+ "]");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				chk_date = sdf.format(dd).toString().substring(0, 4)
						+ base_date.substring(4, 8);
				if (leap_ty == 1) {
					try {
						base_date = padDate(base_date)
								+ getResources().getString(R.string.label_leap_solar) // "(윤)[양)"
								+ padDate(LunarTranser.LunarTranse(chk_date,
								true)) + "]";
					} catch (Exception e) {
						base_date = padDate(base_date) + getResources().getString(R.string.label_leap_lunar); // "(윤,음)";
					}
				} else {
					try {
						base_date = padDate(base_date)
								+ getResources().getString(R.string.label_solra2)  // "[양)"
								+ padDate(LunarTranser.LunarTranse(chk_date,
								false)) + "]";
					} catch (Exception e) {
						base_date = padDate(base_date) + getResources().getString(R.string.label_lunar2); // "(음)";
					}
				}
			}

			String name = cursor.getString(6);
			String mobil_no = padTelno(cursor.getString(7));
			ListData l1 = new ListData(_id, subject, base_date, name, mobil_no,
					sync_stat);
			m_lists.add(l1);
			count++;
		}
		cursor.close();
		dbHandler.close();

		ListDataAdapter m_adapter = new ListDataAdapter(this, R.layout.row, m_lists);
		m_adapter.notifyDataSetChanged();
		list.setAdapter(m_adapter);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v,
									final int pos, long id) {
				Intent intent = new Intent(LunarGugul.this, DeleteData.class);
				intent.putExtra("id", m_lists.get(pos).id);
				startActivityForResult(intent, ACT_EDIT);

			}

		});

	}

	public static String parse2Date() {

		long time = System.currentTimeMillis();
		@SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date dd = new Date(time);
		return sdf.format(dd);

	}

	public static String pad(int c) {
		String return_value = "";
		if (c >= 10) {
			return_value = String.valueOf(c);
		} else {
			return_value = "0" + String.valueOf(c);
		}
		return return_value;
	}

	public static String padDate(String iDate) {
		String return_value = "";
		try {
			return_value = iDate.substring(0, 4) + "-" + iDate.substring(4, 6)
					+ "-" + iDate.substring(6, 8);
		} catch (Exception e) {
			return_value = iDate;
		}
		return return_value;
	}
	
	public static String padTelno(String iTelno) {
		String return_value = "" ;
		try {
			if (iTelno.length() < 11) {
				return_value = iTelno.substring(0, 3) + "-" + iTelno.substring(3, 6) + "-" + iTelno.substring(6, 10);
			} else {
				return_value = iTelno.substring(0, 3) + "-" + iTelno.substring(3, 7) + "-" + iTelno.substring(7, 11);
			}
		} catch (Exception e) {
			return_value = iTelno ;
		}
		
		return return_value ;
	}

	private class ListDataAdapter extends ArrayAdapter<ListData> {

		private ArrayList<ListData> items;

		ListDataAdapter(Context context, int textViewResourceId,
                        ArrayList<ListData> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@SuppressLint("InflateParams")
        @Override
		public View getView(final int position, View convertView,
                            ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert vi != null;
                v = vi.inflate(R.layout.row, null);
			}
			ListData p = items.get(position);
			if (p != null) {

				try {
					TextView tsubject = (TextView) v
							.findViewById(R.id.tSubject);
					TextView tbase_date = (TextView) v
							.findViewById(R.id.tBasedate);
					TextView tname = (TextView) v.findViewById(R.id.tName);
					TextView tmobilno = (TextView) v
							.findViewById(R.id.tMobilno);
					TextView tsync_stat = (TextView) v
							.findViewById(R.id.tSync_stat);

					if (tsubject != null) {
						tsubject.setText(p.getSubject());
					}
					if (tbase_date != null) {
						tbase_date.setText(p.getBase_date());
					}
					if (tname != null) {
						tname.setText(p.getName());
					}
					if (tmobilno != null) {
						tmobilno.setText(p.getMobilno());
					}
					if (tsync_stat != null) {
						if (p.getSync_stat() == 1) {
							tsync_stat.setText(getResources().getString(R.string.label_sync));
						} else {
							tsync_stat.setText(getResources().getString(R.string.label_no_sync));
						}
					}

				} catch (Exception ignored) {

				}

			}
			return v;
		}

	}

	static class ListData {

		private String id;
		private String subject;
		private String base_date;
		private String name;
		private String mobilno;
		private int sync_stat;

		ListData(String id, String subject, String base_date,
                 String name, String mobilno, int sync_stat) {
			this.id = id;
			this.subject = subject;
			this.base_date = base_date;
			this.name = name;
			this.mobilno = mobilno;
			this.sync_stat = sync_stat;
		}

		public String getId() {
			return id;
		}

		String getSubject() {
			return subject;
		}

		String getBase_date() {
			return base_date;
		}

		public String getName() {
			return name;
		}

		String getMobilno() {
			return mobilno;
		}

		int getSync_stat() {
			return sync_stat;
		}

	}

}