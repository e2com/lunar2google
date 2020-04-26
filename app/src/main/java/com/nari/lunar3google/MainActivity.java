package com.nari.lunar3google;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.nari.lunar3google.service.AlarmReceiver;
import com.nari.lunar3google.util.BackPressCloseHandler;
import com.nari.lunar3google.util.DBHandler;
import com.nari.lunar3google.util.FileUtil;
import com.nari.lunar3google.util.LunarTranser;
import com.nari.lunar3google.view.ListData;
import com.nari.lunar3google.view.ListDataAdapter;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * admob 광고 추가
 */
public class MainActivity extends AppCompatActivity {

    private static final int RESOLVE_HINT = 100;
    static DBHandler dbHandler;
    final static int ACT_EDIT = 0;
    static String chk_date = "";
    @SuppressWarnings("unused")
    private Calendar am;
    /** Called when the activity is first created. */

    String TAG = "MainActivity";
    ArrayList<String> chkPermission = new ArrayList<String>() ;
    final static int PERMISSION_REQUEST_CODE = 1000 ;
    TextView hello ;

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        hello = findViewById(R.id.hello); // TextView 글자 넣기
        backPressCloseHandler = new BackPressCloseHandler(this);

        FloatingActionButton fab = findViewById(R.id.fab3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick... fab ") ;
                Intent intent = new Intent(MainActivity.this, EntryData.class);
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
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adReqeust = new AdRequest.Builder().build();
        mAdView.loadAd(adReqeust);
        // end adMob

        if (checkFunction("WRITE_CALENDAR")) {
            if (!onRefreshData()) {

                // finish();
            }
        }

        doDisplayData();
    }

    /**
     * 뒤로 가기 버튼으로 종료를 하고자 할 때
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // 이것 막고 새로 만들걸루 대체
        backPressCloseHandler.onBackPressed();

        doDisplayData() ;
    }

    // Obtain the phone number from the result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        doDisplayData();

    }

    /**
     * 화면에 데이터 조회하기
     * @return
     */
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

        // 카렌더 등록할 계정이 있는지 확인함.
        SharedPreferences pref = getSharedPreferences("lunar2Gugul", 0) ;
        String googleId = pref.getString("CalendarID","") ;
        if ("".equals(googleId)) {
            if (checkFunction("READ_CALENDAR")) {
                CalendarIdRef calendarIdRef = new CalendarIdRef();
                HashMap<String, Integer> googleIds = calendarIdRef.CalendarIdRef(MainActivity.this);
                Log.e(TAG, "googleIds.size()=" + googleIds.size()) ;
                // 구글 계정이 없으니 그만
                if (googleIds.size() < 1) {

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

                    new AlertDialog.Builder(MainActivity.this).setTitle(getResources().getString(R.string.label_notify))
                            .setMessage(getResources().getString(R.string.label_mesg_not_sync))
                            .setPositiveButton(getResources().getString(R.string.label_btn_continue), mClickLeft)
                            .setNegativeButton(getResources().getString(R.string.label_cancel_btn), mClickRight).show();

                    //finish();
                } else {
                    bResult = true ;
                }
            }
        }

        if (bResult) {
            doDisplayData();
        }

        return bResult ;
    }

    public boolean checkFunction(String option){
        boolean bResult = false ;
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
        } else {
            bResult = true ;
        }

        return bResult ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
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

                Intent intent4 = new Intent(this, CalendarMonth.class);
                intent4.putExtra("Textin", "CalendarMonth");
                startActivityForResult(intent4, ACT_EDIT);

	/* 한동안은 가져오는 건 하지 말자...
				Intent intent4 = new Intent(this, CalendarRead.class);
				intent4.putExtra("Textin", "CalendarRead");
				startActivityForResult(intent4, ACT_EDIT);*/

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
                        Toast.makeText(MainActivity.this,
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
                        Toast.makeText(MainActivity.this,
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

                intent1 = new Intent(MainActivity.this, AlarmReceiver.class) ;
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
					Toast.makeText(MainActivity.this,"sms Inserted", Toast.LENGTH_LONG).show() ;
				} else {
					Toast.makeText(MainActivity.this,"sms Inserted failed", Toast.LENGTH_LONG).show() ;
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

    /**
     * 백업 하기
     */
    private class ExportDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

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

            File dbFile = new File(Environment.getDataDirectory() + "/data/com.nari.lunar3google/databases/myLunarPlan");
            File exportDir = new File(Environment.getExternalStorageDirectory(), "lunar3google");
            Log.e(TAG, "BACKUP DIR=" + exportDir.getAbsolutePath());
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File file = new File(exportDir, dbFile.getName() + ".sqlite"); // 2019.05.29 확장자를 지정하다.

            try {
                file.createNewFile();
                FileUtil.copyFile(dbFile, file);
                Log.e(TAG, "BACKUP Compiled" ) ;
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
                Toast.makeText(MainActivity.this, getString(R.string.msgBackupCompiled), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.msgNotBackup), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     *
     */
    private class ImportDatabaseTask extends AsyncTask<Void, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.label_restore));
            dialog.show();
        }

        // could pass the params used here in AsyncTask<String, Void, String> - but not being re-used
        @Override
        protected String doInBackground(final Void... args) {

            File dbBackupFile = new File( Environment.getExternalStorageDirectory() + "/lunar3google/myLunarPlan.sqlite");
            Log.e(TAG, "RESTORE FILE=" +dbBackupFile.getAbsolutePath());
            if (!dbBackupFile.exists()) {
                return getResources().getString(R.string.mesg_restore_err);
            } else if (!dbBackupFile.canRead()) {
                return getResources().getString(R.string.mesg_restore_err);
            }

            File dbFile = new File(Environment.getDataDirectory() + "/data/com.nari.lunar3google/databases/myLunarPlan");
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
                Toast.makeText(MainActivity.this, getResources().getString(R.string.mesg_restore_ok), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.mesg_restore_err) + errMsg, Toast.LENGTH_SHORT).show();
            }
        }
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

        ListDataAdapter m_adapter = new ListDataAdapter(m_lists);
        m_adapter.notifyDataSetChanged();
        list.setAdapter(m_adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v,
                                    final int pos, long id) {
                Intent intent = new Intent(MainActivity.this, DeleteData.class);
                intent.putExtra("id", m_lists.get(pos).getId());
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
        int idx = iTelno.indexOf("-") ;
        if (idx < 0) {
            try {
                if (iTelno.length() < 11) {
                    return_value = iTelno.substring(0, 3) + "-" + iTelno.substring(3, 6) + "-" + iTelno.substring(6, 10);
                } else {
                    return_value = iTelno.substring(0, 3) + "-" + iTelno.substring(3, 7) + "-" + iTelno.substring(7, 11);
                }
            } catch (Exception e) {
                return_value = iTelno ;
            }
        } else {
            return_value = iTelno ;
        }
        return return_value ;
    }
}
