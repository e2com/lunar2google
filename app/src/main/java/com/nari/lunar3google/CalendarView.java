package com.nari.lunar3google;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.nari.lunar3google.util.DBHandler;
import com.nari.lunar3google.util.LunarTranser;
import com.nari.lunar3google.util.StringUtil;
import com.nari.lunar3google.view.ListData;
import com.nari.lunar3google.view.ListDataAdapter;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CalendarView extends Activity {
	static DBHandler dbHandler;
	final static int ACT_EDIT = 0;
	static String chk_date = "";

	String TAG = "CalendarView" ;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainview2);

		EditText hello = findViewById(R.id.editMonth) ;

		long time = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.label_yymmdd));
		Date dd = new Date(time);
		try {
			hello.setText(sdf.format(dd));

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			Log.e(TAG, "Today=" + sdf.format(dd) + '/' + getDateDay(sdf.format(dd), getResources().getString(R.string.label_yymmdd))) ;
			dispMonthly(sdf.format(dd), getResources().getString(R.string.label_yymmdd));
		} catch (Exception e) {
			e.printStackTrace();
		}

		ImageButton btnProv = findViewById(R.id.btnPrevious);
		ImageButton btnNext = findViewById(R.id.btnNext);

		final ArrayList<ListData> m_lists = new ArrayList<ListData>();
		final ListView list = (ListView) findViewById(R.id.ListView);
		
		String lunar_date1 = "" ;
		String lunar_date2 = "" ;
		String solar_date = "" ;
		try {
			solar_date = LunarTranser.solarTranse(StringUtil.parse2Date()) ;
		} catch (Exception e) {
			solar_date = StringUtil.parse2Date() ;
		}
		try {
			lunar_date1 = LunarTranser.LunarTranse(StringUtil.parse2Date(), true) ;
		} catch (Exception e) {
			lunar_date1 = StringUtil.parse2Date() ;
		}
		try {
			lunar_date2 = LunarTranser.LunarTranse(StringUtil.parse2Date(), false) ;
		} catch (Exception e) {
			lunar_date2 = StringUtil.parse2Date() ;
		}
		if ("".equals(lunar_date1)) {
			lunar_date1 = StringUtil.parse2Date() ;
		}
		if ("".equals(lunar_date2)) {
			lunar_date2 = StringUtil.parse2Date() ;
		}
		if ("".equals(solar_date)) {
			solar_date = StringUtil.parse2Date() ;
		}

		dbHandler = DBHandler.open(this);
		Cursor cursor = null;
		//cursor = dbHandler.selectBaseDate(parse2Date());
		cursor = dbHandler.selectAll();
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
				chk_date = sdf.format(dd).toString().substring(0, 4)
						+ base_date.substring(4, 8);
				if (leap_ty == 1) {
					try {
						base_date = StringUtil.padDate(base_date)
								+ getResources().getString(R.string.label_leap_solar)  // "(윤)[양)"
								+ StringUtil.padDate(LunarTranser.LunarTranse(chk_date,
										true)) + "]";
					} catch (Exception e) {
						base_date = StringUtil.padDate(base_date) + getResources().getString(R.string.label_leap_lunar) ; // "(윤,음)";
					}
				} else {
					try {
						base_date = StringUtil.padDate(base_date)
								+ getResources().getString(R.string.label_solra2)  //"[양)"
								+ StringUtil.padDate(LunarTranser.LunarTranse(chk_date,
										false)) + "]";
					} catch (Exception e) {
						base_date = StringUtil.padDate(base_date) + getResources().getString(R.string.label_lunar2) ; // "(음)";
					}
				}
			}
			String mobil_no = StringUtil.padTelno(cursor.getString(7));
			// 오늘 자료 찾기 -- 양력 음력 구분없이... 
			if (chk_date2.equals(solar_date) || chk_date2.equals(lunar_date1) || chk_date2.equals(lunar_date2) || chk_date2.equals(StringUtil.parse2Date()) ) {
				ListData list_data = new ListData(_id, subject, base_date, name, mobil_no, sync_stat);
				m_lists.add(list_data);
				count++;
			}
		}
		cursor.close();
		dbHandler.close();

		//kakaoToast.makeToast(this, "Read Data Count " + String.valueOf(count), Toast.LENGTH_LONG).show() ;
		
		final ListDataAdapter m_adapter = new ListDataAdapter(m_lists);
		list.setAdapter(m_adapter);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
                                    final int pos, long id) {
				Intent intent = new Intent(CalendarView.this, DeleteData.class);
				intent.putExtra("id", m_lists.get(pos).getId());
				startActivityForResult(intent, ACT_EDIT);
			}
		});
	}

	public void dispMonthly(String date, String dateType) throws ParseException {
		TextView labelSunday1 = findViewById(R.id.labelSunday1);
		TextView labelMonday1 = findViewById(R.id.labelMonday1);
		TextView labelTueuday1 = findViewById(R.id.labelTueuday1);
		TextView labelWensday1 = findViewById(R.id.labelWedsday1);
		TextView labelThruday1 = findViewById(R.id.labelThruday1);
		TextView labelFriday1 = findViewById(R.id.labelFriday1);
		TextView labelSatday1 = findViewById(R.id.labelSatday1);

		TextView labelSunday2 = findViewById(R.id.labelSunday2);
		TextView labelMonday2 = findViewById(R.id.labelMonday2);
		TextView labelTueuday2 = findViewById(R.id.labelTueuday2);
		TextView labelWensday2 = findViewById(R.id.labelWedsday2);
		TextView labelThruday2 = findViewById(R.id.labelThruday2);
		TextView labelFriday2 = findViewById(R.id.labelFriday2);
		TextView labelSatday2 = findViewById(R.id.labelSatday2);

		TextView labelSunday3 = findViewById(R.id.labelSunday3);
		TextView labelMonday3 = findViewById(R.id.labelMonday3);
		TextView labelTueuday3 = findViewById(R.id.labelTueuday3);
		TextView labelWensday3 = findViewById(R.id.labelWedsday3);
		TextView labelThruday3 = findViewById(R.id.labelThruday3);
		TextView labelFriday3 = findViewById(R.id.labelFriday3);
		TextView labelSatday3 = findViewById(R.id.labelSatday3);

		TextView labelSunday4 = findViewById(R.id.labelSunday4);
		TextView labelMonday4 = findViewById(R.id.labelMonday4);
		TextView labelTueuday4 = findViewById(R.id.labelTueuday4);
		TextView labelWensday4 = findViewById(R.id.labelWedsday4);
		TextView labelThruday4 = findViewById(R.id.labelThruday4);
		TextView labelFriday4 = findViewById(R.id.labelFriday4);
		TextView labelSatday4 = findViewById(R.id.labelSatday4);

		TextView labelSunday5 = findViewById(R.id.labelSunday5);
		TextView labelMonday5 = findViewById(R.id.labelMonday5);
		TextView labelTueuday5 = findViewById(R.id.labelTueuday5);
		TextView labelWensday5 = findViewById(R.id.labelWedsday5);
		TextView labelThruday5 = findViewById(R.id.labelThruday5);
		TextView labelFriday5 = findViewById(R.id.labelFriday5);
		TextView labelSatday5 = findViewById(R.id.labelSatday5);

		String day = "" ;

		SimpleDateFormat dateFormat = new SimpleDateFormat(dateType) ;
		java.util.Date nDate = dateFormat.parse(date);

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
		dayNum = cal.get(Calendar.DAY_OF_WEEK);

		String[][] weekArray = new String[5][7];
		int iDay = 0;
		int jStart = dayNum - 1; // 요일이 그렇게 오니까
		for(int i=0;i<5;i++) {
			if (i!=0){
				jStart = 0 ;
			}
			for(int j=jStart ; j < 7 ; j++) {
				iDay++;
				if (iDay > endDay) break ;
				weekArray[i][j] = String.valueOf(iDay);
			}
		}

		labelSunday1.setText(weekArray[0][0]);
		labelMonday1.setText(weekArray[0][1]);
		labelTueuday1.setText(weekArray[0][2]);
		labelWensday1.setText(weekArray[0][3]);
		labelThruday1.setText(weekArray[0][4]);
		labelFriday1.setText(weekArray[0][5]);
		labelSatday1.setText(weekArray[0][6]);

		labelSunday2.setText(weekArray[1][0]);
		labelMonday2.setText(weekArray[1][1]);
		labelTueuday2.setText(weekArray[1][2]);
		labelWensday2.setText(weekArray[1][3]);
		labelThruday2.setText(weekArray[1][4]);
		labelFriday2.setText(weekArray[1][5]);
		labelSatday2.setText(weekArray[1][6]);

		labelSunday3.setText(weekArray[2][0]);
		labelMonday3.setText(weekArray[2][1]);
		labelTueuday3.setText(weekArray[2][2]);
		labelWensday3.setText(weekArray[2][3]);
		labelThruday3.setText(weekArray[2][4]);
		labelFriday3.setText(weekArray[2][5]);
		labelSatday3.setText(weekArray[2][6]);

		labelSunday4.setText(weekArray[3][0]);
		labelMonday4.setText(weekArray[3][1]);
		labelTueuday4.setText(weekArray[3][2]);
		labelWensday4.setText(weekArray[3][3]);
		labelThruday4.setText(weekArray[3][4]);
		labelFriday4.setText(weekArray[3][5]);
		labelSatday4.setText(weekArray[3][6]);

		labelSunday5.setText(weekArray[4][0]);
		labelMonday5.setText(weekArray[4][1]);
		labelTueuday5.setText(weekArray[4][2]);
		labelWensday5.setText(weekArray[4][3]);
		labelThruday5.setText(weekArray[4][4]);
		labelFriday5.setText(weekArray[4][5]);
		labelSatday5.setText(weekArray[4][6]);

	}

	/**
	 * 매월 1일의 요일 구하기
	 * @param date
	 * @param dateType
	 * @return
	 * @throws Exception
	 */
	public String getDateDay(String date, String dateType) throws Exception {

		String day = "" ;

		SimpleDateFormat dateFormat = new SimpleDateFormat(dateType) ;
		java.util.Date nDate = dateFormat.parse(date);

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
        dayNum = cal.get(Calendar.DAY_OF_WEEK);

		String fromYMD = year+"/"+month+"/"+startDay;
		String toYMD = year+"/"+month+"/"+endDay;

		Log.e(TAG, "=" + fromYMD + "/" + toYMD) ;

		day = String.valueOf(dayNum);
		return day ;
	}



}
