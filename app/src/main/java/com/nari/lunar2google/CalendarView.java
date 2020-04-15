package com.nari.lunar2google;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CalendarView extends Activity {
	static DBHandler dbHandler;
	final static int ACT_EDIT = 0;
	static String chk_date = "";
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TextView hello = (TextView) findViewById(R.id.hello); // TextView 글자 넣기
		long time = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.label_yymmdd));
		Date dd = new Date(time);
		try {
			hello.append(sdf.format(dd) + getResources().getString(R.string.label_lunar2) // "[음)"
					+ padDate(LunarTranser.solarTranse(parse2Date())) + "]");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
						
		final ArrayList<ListData> m_lists = new ArrayList<ListData>();
		final ListView list = (ListView) findViewById(R.id.ListView);
		
		String lunar_date1 = "" ;
		String lunar_date2 = "" ;
		String solar_date = "" ;
		try {
			solar_date = LunarTranser.solarTranse(parse2Date()) ;
		} catch (Exception e) {
			solar_date = parse2Date() ;
		}
		try {
			lunar_date1 = LunarTranser.LunarTranse(parse2Date(), true) ;
		} catch (Exception e) {
			lunar_date1 = parse2Date() ;
		}
		try {
			lunar_date2 = LunarTranser.LunarTranse(parse2Date(), false) ;
		} catch (Exception e) {
			lunar_date2 = parse2Date() ;
		}
		if ("".equals(lunar_date1)) {
			lunar_date1 = parse2Date() ;
		}
		if ("".equals(lunar_date2)) {
			lunar_date2 = parse2Date() ;
		}
		if ("".equals(solar_date)) {
			solar_date = parse2Date() ;
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
					base_date = padDate(base_date)
							+ getResources().getString(R.string.label_lunar2)  // "[음)"
							+ padDate(LunarTranser.solarTranse(base_date)
									+ "]");
				} catch (Exception e) {
					base_date = padDate(base_date) + "[???]";
				}
			} else {
				chk_date = sdf.format(dd).toString().substring(0, 4)
						+ base_date.substring(4, 8);
				if (leap_ty == 1) {
					try {
						base_date = padDate(base_date)
								+ getResources().getString(R.string.label_leap_solar)  // "(윤)[양)"
								+ padDate(LunarTranser.LunarTranse(chk_date,
										true)) + "]";
					} catch (Exception e) {
						base_date = padDate(base_date) + getResources().getString(R.string.label_leap_lunar) ; // "(윤,음)";
					}
				} else {
					try {
						base_date = padDate(base_date)
								+ getResources().getString(R.string.label_solra2)  //"[양)"
								+ padDate(LunarTranser.LunarTranse(chk_date,
										false)) + "]";
					} catch (Exception e) {
						base_date = padDate(base_date) + getResources().getString(R.string.label_lunar2) ; // "(음)";
					}
				}
			}
			String mobil_no = padTelno(cursor.getString(7));
			// 오늘 자료 찾기 -- 양력 음력 구분없이... 
			if (chk_date2.equals(solar_date) || chk_date2.equals(lunar_date1) || chk_date2.equals(lunar_date2) || chk_date2.equals(parse2Date()) ) {
				ListData list_data = new ListData(_id, subject, base_date, name, mobil_no, sync_stat);
				m_lists.add(list_data);
				count++;
			}
		}
		cursor.close();
		dbHandler.close();

		//Toast.makeText(this, "Read Data Count " + String.valueOf(count), Toast.LENGTH_LONG).show() ;
		
		final ListDataAdapter m_adapter = new ListDataAdapter(this,	R.layout.row, m_lists);
		list.setAdapter(m_adapter);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
                                    final int pos, long id) {
				Intent intent = new Intent(CalendarView.this, DeleteData.class);
				intent.putExtra("id", m_lists.get(pos).id);
				startActivityForResult(intent, ACT_EDIT);
			}
		});
	}

	public static String parse2Date() {

		long time = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
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

		public ListDataAdapter(Context context, int textViewResourceId,
                               ArrayList<ListData> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(final int position, View convertView,
                            ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
							tsync_stat.setText(getResources().getString(R.string.label_sync) );
						} else {
							tsync_stat.setText(getResources().getString(R.string.label_no_sync) );
						}
					}

				} catch (Exception e) {

				}

			}
			return v;
		}

	}

	class ListData {

		private String id;
		private String subject;
		private String base_date;
		private String name;
		private String mobilno;
		private int sync_stat;

		public ListData(String id, String subject, String base_date,
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

		public String getSubject() {
			return subject;
		}

		public String getBase_date() {
			return base_date;
		}

		public String getName() {
			return name;
		}

		public String getMobilno() {
			return mobilno;
		}

		public int getSync_stat() {
			return sync_stat;
		}

	}
}
