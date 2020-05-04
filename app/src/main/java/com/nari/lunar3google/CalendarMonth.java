package com.nari.lunar3google;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nari.lunar3google.R.id;
import com.nari.lunar3google.service.AlarmReceiver;
import com.nari.lunar3google.util.BackPressCloseHandler;
import com.nari.lunar3google.util.DBHandler;
import com.nari.lunar3google.util.FileUtil;
import com.nari.lunar3google.util.LunarTranser;
import com.nari.lunar3google.util.StringUtil;
import com.nari.lunar3google.util.kakaoToast;
import com.nari.lunar3google.view.ListData;
import com.nari.lunar3google.view.ListDataAdapter;
import com.nari.lunar3google.view.ListTitleAdapter;
import com.nari.lunar3google.view.ListTitleData;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CalendarMonth extends AppCompatActivity{

    DBHandler dbHandler;
    final int ACT_EDIT = 0;
    String chk_date = "";
    String TAG = "CalendarMonth" ;

    ArrayList<String> chkPermission = new ArrayList<String>() ;
    final static int PERMISSION_REQUEST_CODE = 1000 ;
    private BackPressCloseHandler backPressCloseHandler;

    EditText editText2 ;
    ListView dataView ;
    GridView dataMonth ;
    String paramDate ;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfToday = new SimpleDateFormat("yyyyMMdd"); // 해당월의 정보 취득으로 변경 2020.05.04

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_month);
        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        backPressCloseHandler = new BackPressCloseHandler(this);

        ImageButton btnPrev2 = findViewById(R.id.btnPrevious2) ;
        ImageButton btnNext2 = findViewById(R.id.btnNext2) ;
        editText2 = findViewById(id.editMonth2) ;

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = "" ;
                switch (v.getId()) {
                    case R.id.btnPrevious2:
                        param = "P" ;
                        break ;
                    case id.btnNext2:
                        param = "N" ;
                        break ;
                    case R.id.editMonth2:
                        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                paramDate = StringUtil.pad(year) + "-" + StringUtil.pad(month + 1) + "-" + StringUtil.pad(dayOfMonth) ;
                                editText2.setText(paramDate.substring(0, 7));
                                if (getDayList(paramDate)) {
                                    Log.e(TAG, paramDate + ":" + paramDate) ;
                                };
                                doRefresh(paramDate) ;
                            }
                        };
                        Log.e(TAG, "year=" + paramDate.substring(0, 4)) ;
                        Log.e(TAG, "month=" + paramDate.substring(5, 7)) ;
                        Log.e(TAG, "day=" + paramDate.substring(8, 10)) ;
                        DatePickerDialog datePickerDialog = new DatePickerDialog(CalendarMonth.this, listener, Integer.parseInt(paramDate.substring(0, 4)), Integer.parseInt(paramDate.substring(5, 7)) - 1, Integer.parseInt(paramDate.substring(8, 10)));
                        datePickerDialog.show();
                        break ;
                }
                Log.e(TAG, param) ;
                paramDate = StringUtil.addMonth(paramDate, param) + "-01" ; // yyyy-MM-dd 형식으로만 전달해야 함.
                if (getDayList(paramDate)) {
                    Log.e(TAG, paramDate + ":" + paramDate) ;
                };
                doRefresh(paramDate) ;

            }
        };
        btnPrev2.setOnClickListener(onClickListener);
        btnNext2.setOnClickListener(onClickListener);
        editText2.setOnClickListener(onClickListener);

        dataView = findViewById(id.dataView) ;
        dataMonth = findViewById(id.gridMonth);
        dataMonth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView textDay = view.findViewById(R.id.textDay);
                String tDay = textDay.getText().toString() ;
                Log.e(TAG, "position=" + position + " id=" + id + " tDay=" + tDay) ;

                if (StringUtil.isNumber(tDay)) {
                    tDay = paramDate.substring(0, 8) + StringUtil.pad(Integer.parseInt(tDay));
                    if (getDayList(tDay)) {
                        Log.e(TAG, paramDate + ":" + tDay);
                    }
                    ;
                }
            }
        });

        long time = System.currentTimeMillis();
        Date dd = new Date(time);
        editText2.setText(sdf.format(dd));
        paramDate = sdf.format(dd) ;

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
                // kakaoToast.makeToast(this, "일정에 일괄 등록합니다.", Toast.LENGTH_SHORT).show()
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
/*            case R.id.action_menu_get_schedule:
                Intent intent4 = new Intent(this, CalendarMonth.class);
                intent4.putExtra("Textin", "CalendarMonth");
                startActivityForResult(intent4, ACT_EDIT);*/

	/* 한동안은 가져오는 건 하지 말자...
				Intent intent4 = new Intent(this, CalendarRead.class);
				intent4.putExtra("Textin", "CalendarRead");
				startActivityForResult(intent4, ACT_EDIT);

                return true;
*/
            case R.id.action_menu_today:
                Intent intent5 = new Intent(this, CalendarView.class);
                intent5.putExtra("Textin", "CalendarView");
                startActivityForResult(intent5, ACT_EDIT);
                return true;
            case R.id.action_menu_backup:
                if (checkFunction("WRITE_EXTERNAL_STORAGE")) {
                    if (isExternalStorageAvail()) {
                        new CalendarMonth.ExportDatabaseTask().execute();
                        SystemClock.sleep(1000);
                    } else {
                        kakaoToast.makeToast(CalendarMonth.this,
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
                        new CalendarMonth.ImportDatabaseTask().execute();
                        SystemClock.sleep(500);
                    } else {
                        kakaoToast.makeToast(CalendarMonth.this,
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

                intent1 = new Intent(CalendarMonth.this, AlarmReceiver.class) ;
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
					kakaoToast.makeToast(CalendarMonth.this,"sms Inserted", Toast.LENGTH_LONG).show() ;
				} else {
					kakaoToast.makeToast(CalendarMonth.this,"sms Inserted failed", Toast.LENGTH_LONG).show() ;
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
					kakaoToast.makeToast(this, body + "[" + number + "]", Toast.LENGTH_LONG).show() ;
				}
				while (result.moveToNext()) {
					body = result.getString(result.getColumnIndexOrThrow("body")).toString() ;
					number = result.getString(result.getColumnIndexOrThrow("address")).toString();
					kakaoToast.makeToast(this, body + "[" + number + "]", Toast.LENGTH_LONG).show() ;
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
        private final ProgressDialog dialog = new ProgressDialog(CalendarMonth.this);

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
                kakaoToast.makeToast(CalendarMonth.this, getString(R.string.msgBackupCompiled), Toast.LENGTH_SHORT).show();
            } else {
                kakaoToast.makeToast(CalendarMonth.this, getString(R.string.msgNotBackup), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     *
     */
    private class ImportDatabaseTask extends AsyncTask<Void, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(CalendarMonth.this);

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
                kakaoToast.makeToast(CalendarMonth.this, getResources().getString(R.string.mesg_restore_ok), Toast.LENGTH_SHORT).show();
            } else {
                kakaoToast.makeToast(CalendarMonth.this, getResources().getString(R.string.mesg_restore_err) + errMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 뒤로 가기 버튼으로 종료를 하고자 할 때
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // 이것 막고 새로 만들걸루 대체
        backPressCloseHandler.onBackPressed();

        doRefresh(paramDate);
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
    protected void onResume() {
        super.onResume();

        if (checkFunction("WRITE_CALENDAR")) {
            doRefresh(paramDate) ;
        }

    }

    public void doRefresh(String baseMonth) {

        editText2.setText(baseMonth.substring(0, 7));



        Log.e(TAG, baseMonth) ;

        java.util.Date nDate = null;
        try {
            nDate = sdf.parse(baseMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance() ;
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK) ;

        String sYear = String.valueOf(cal.get(Calendar.YEAR)) ;
        String sMonth = String.valueOf(cal.get(Calendar.MONTH)+1) ;

        // 메월 1일 설정
        cal.set(Integer.parseInt(sYear), Integer.parseInt(sMonth)-1, Integer.parseInt("1"));
        int year = cal.get ( cal.YEAR );
        int month = cal.get ( cal.MONTH )+1 ;
        int startDay = cal.get(cal.DAY_OF_MONTH);
        int endDay = cal.getActualMaximum(cal.DAY_OF_MONTH);
        dayNum = cal.get(Calendar.DAY_OF_WEEK) - 1;

        ArrayList<ListTitleData> mMonth = new ArrayList<ListTitleData>();
        String[] mTitle = { getString(R.string.labelSun), getString(R.string.labelMon) , getString(R.string.labelTue), getString(R.string.labelWed), getString(R.string.labelThr), getString(R.string.labelFri), getString(R.string.labelSat)};
        for(int idx = 0 ; idx < mTitle.length ; idx++) {
            ListTitleData listTitleData = new ListTitleData(mTitle[idx]);
            mMonth.add(listTitleData);
        }
        int iDay = 0 ;
        int iPos = 0 ;
        while(true) {
            String sValue = "" ;
            if (iDay == endDay) break ;
            if (iPos < dayNum) {
                sValue = "" ;
            } else {
                iDay++; // 날자 넣기
                sValue = String.valueOf(iDay) ;
            }
            ListTitleData listTitleData = new ListTitleData(sValue);
            mMonth.add(listTitleData);
            iPos++; // 위치 잡기
        }
        ListTitleAdapter listTitleAdapter = new ListTitleAdapter(mMonth, year, month);
        dataMonth.setAdapter(listTitleAdapter);

    }

    public boolean getDayList(String baseDate) {
        boolean bResult = false ;

        final ArrayList<ListData> mList = new ArrayList<ListData>() ;
        dbHandler = DBHandler.open(this);
        Cursor cursor = null;
        cursor = dbHandler.selectBaseDate(baseDate.replaceAll("-", ""));
        int count = 0;
        while (cursor.moveToNext()) {
            String _id = cursor.getString(0);
            String subject = cursor.getString(1);
            String base_date = cursor.getString(2);
            String chk_date2 = cursor.getString(2);
            int lunar_ty = cursor.getInt(3);
            int leap_ty = cursor.getInt(4);
            int sync_stat = cursor.getInt(5);
            String name = cursor.getString(6);
            if (lunar_ty == 2) {
                try {
                    base_date = StringUtil.padDate(base_date)
                            + getResources().getString(R.string.label_lunar2)  // "[음)"
                            + StringUtil.padDate(LunarTranser.solarTranse(base_date)
                            + "]");
                } catch (Exception e) {
                    base_date = StringUtil.padDate(base_date) + "[???]";
                }
            } else {
                chk_date = baseDate.substring(0, 4)
                        + base_date.substring(4, 8);
                if (leap_ty == 1) {
                    try {
                        base_date = StringUtil.padDate(base_date)
                                + getResources().getString(R.string.label_leap_solar)  // "(윤)[양)"
                                + StringUtil.padDate(LunarTranser.LunarTranse(chk_date,
                                true)) + "]";
                    } catch (Exception e) {
                        base_date = StringUtil.padDate(base_date) + getResources().getString(R.string.label_leap_lunar); // "(윤,음)";
                    }
                } else {
                    try {
                        base_date = StringUtil.padDate(base_date)
                                + getResources().getString(R.string.label_solra2)  //"[양)"
                                + StringUtil.padDate(LunarTranser.LunarTranse(chk_date,
                                false)) + "]";
                    } catch (Exception e) {
                        base_date = StringUtil.padDate(base_date) + getResources().getString(R.string.label_lunar2); // "(음)";
                    }
                }
            }
            String mobil_no = StringUtil.padTelno(cursor.getString(7));
            ListData list_data = new ListData(_id, subject, base_date, name, mobil_no, sync_stat);
            mList.add(list_data);
            count++;
            bResult = true ;
        }
        cursor.close();
        dbHandler.close();

        ListDataAdapter listDataAdapter = new ListDataAdapter(mList);
        dataView.setAdapter(listDataAdapter);

        dataView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CalendarMonth.this, DeleteData.class);
                intent.putExtra("id", mList.get(position).getId());
                startActivityForResult(intent, ACT_EDIT);
            }
        });

        return bResult ;
    }
}
