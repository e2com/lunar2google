package com.nari.lunar3google;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CalendarRead extends Activity {

    String TAG = "CalendarRead";
	DBHandler dbHandler ;
    int[] _id ;
    String[] calendars_name ;
    boolean[] loadTY ;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendarread);
        
        final ArrayList<String> arGeneral = new ArrayList<String>() ;
        dbHandler = DBHandler.open(this)  ;

		DialogInterface.OnClickListener mClickLeft =
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
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
			        	_id                   = new int[Cursor_calendars.getCount()];
			        	calendars_name        = new String[Cursor_calendars.getCount()];
			        	loadTY                = new boolean[Cursor_calendars.getCount()];

			        	for (int i = 0 ; i < calendars_name.length ; i++) {
			        		_id[i] = Cursor_calendars.getInt(0);
			        		calendars_name[i] = Cursor_calendars.getString(1);
                            if (calendars_name[i].indexOf("@gmail.com") > 0) loadTY[i] = true ;
                            if (calendars_name[i].indexOf("calendar") > 0) loadTY[i] = true ;
							Log.d(">>>", ">" + loadTY[i] + "<" + calendars_name[i]);
			        		Cursor_calendars.moveToNext() ;
			        	}
			        	Cursor_calendars.close();
			        }
			        
			        if (android.os.Build.VERSION.SDK_INT == 7) {
			            calendars = Uri.parse("content://calendar/events") ;
			           } else {
			            calendars = Uri.parse("content://com.android.calendar/events") ;
			           }
			           
			           String[] projection = new String[] {
			        		   "calendar_id",
			        		   //"htmlUri",
			        		   "title",
			        		   "eventLocation",
			        		   "description",
			        		   //"eventStatus",
			        		   //"selfAttendeeStatus",
			        		   //"commentsUri",
			        		   "dtstart",
			        		   //"dtend",
			        		   //"eventTimezone",
			        		   //"duration",
			        		   //"allDay",
			        		   //"visibility", // ICS 4.0 에는 이 항목이 없는 것 같다... kdy 2012.05.07
			        		   //"transparency", // ICS 4.0 에는 이 항목이 없는 것 같다... kdy 2012.05.07
			        		   //"hasAlarm",
			        		   //"hasExtendedProperties",
			        		   "rrule",
			        		   //"rdate",
			        		   //"exrule",
			        		   //"exdate",
			        		   //"originalEvent",
			        		   //"originalInstanceTime",
			        		   //"originalAllDay",
			        		   //"lastDate",
			        		   //"hasAttendeeData",
			        		   //"guestsCanModify",
			        		   //"guestsCanInviteOthers",
			        		   //"guestsCanSeeGuests",
			        		   //"organizer",
			        		   //"deleted"
			        		   //"dtstart2",
			        		   //"dtend2",
			        		   //"eventTimezone2",
			        		   //"syncAdapterData"
			        		   } ;
			           Cursor managedCursor = null ; //getContentResolver().query(calendars, projection, "selected=1", null, null) ;
			           if (android.os.Build.VERSION.SDK_INT < 14) {
			        	   managedCursor = getContentResolver().query(calendars, projection, "selected=1", null, null) ;
						} else {
							managedCursor = getContentResolver().query(calendars, projection, "visible=1", null, null) ;
						}
			           if(managedCursor.moveToFirst()) {

			        	   int[] calendar_id               = new int[managedCursor.getCount()];
			        	   //String[] htmlUri                = new String[managedCursor.getCount()];
			        	   String[] title                  = new String[managedCursor.getCount()];
			        	   String[] eventLocation          = new String[managedCursor.getCount()];
			        	   String[] description            = new String[managedCursor.getCount()];
			        	   //int[] eventStatus               = new int[managedCursor.getCount()];
			        	   //int[] selfAttendeeStatus        = new int[managedCursor.getCount()];
			        	   //String[] commentsUri            = new String[managedCursor.getCount()];
			        	   String[] dtstart                = new String[managedCursor.getCount()];
			        	   //String[] dtend                  = new String[managedCursor.getCount()];
			        	   //String[] eventTimezone          = new String[managedCursor.getCount()];
			        	   //String[] duration               = new String[managedCursor.getCount()];
			        	   //int[] allDay                    = new int[managedCursor.getCount()];
			        	   //int[] visibility                = new int[managedCursor.getCount()];
			        	   //int[] transparency              = new int[managedCursor.getCount()];
			        	   //int[] hasAlarm                  = new int[managedCursor.getCount()];
			        	   //int[] hasExtendedProperties     = new int[managedCursor.getCount()];
			        	   String[] rrule                  = new String[managedCursor.getCount()];
			        	   //String[] rdate                  = new String[managedCursor.getCount()];
			        	   //String[] exrule                 = new String[managedCursor.getCount()];
			        	   //String[] exdate                 = new String[managedCursor.getCount()];
			        	   //String[] originalEvent          = new String[managedCursor.getCount()];
			        	   //int[] originalInstanceTime      = new int[managedCursor.getCount()];
			        	   //int[] originalAllDay            = new int[managedCursor.getCount()];
			        	   //String[] lastDate               = new String[managedCursor.getCount()];
			        	   //int[] hasAttendeeData           = new int[managedCursor.getCount()];
			        	   //int[] guestsCanModify           = new int[managedCursor.getCount()];
			        	   //int[] guestsCanInviteOthers     = new int[managedCursor.getCount()];
			        	   //int[] guestsCanSeeGuests        = new int[managedCursor.getCount()];
			        	   //String[] organizer              = new String[managedCursor.getCount()];
			        	   //int[] deleted                   = new int[managedCursor.getCount()];
			        	   
			        	   String view_c = "" ;
			        	   for (int i = 0 ; i < title.length ; i++) {
			            	calendar_id[i] = managedCursor.getInt(0);
			            	//htmlUri[i] = managedCursor.getString(1);
			            	title[i] = managedCursor.getString(1);
			            	eventLocation[i] = managedCursor.getString(2);
			            	description[i] = managedCursor.getString(3);
			            	//eventStatus[i] = managedCursor.getInt(5);
			            	//selfAttendeeStatus[i] = managedCursor.getInt(6);
			            	//commentsUri[i] = managedCursor.getString(7);
			            	dtstart[i] = parse2Date( managedCursor.getLong(4) );
			            	//dtend[i] = parse2Date( managedCursor.getLong(9) );
			            	//eventTimezone[i] = managedCursor.getString(10);
			            	//duration[i] = managedCursor.getString(11);
			            	//allDay[i] = managedCursor.getInt(12);
			            	//visibility[i] = managedCursor.getInt(13);
			            	//transparency[i] = managedCursor.getInt(14);
			            	//hasAlarm[i] = managedCursor.getInt(13);
			            	//hasExtendedProperties[i] = managedCursor.getInt(14);
			            	rrule[i] = managedCursor.getString(5);
			            	//rdate[i] = managedCursor.getString(16);
			            	//exrule[i] = managedCursor.getString(17);
			            	//exdate[i] = managedCursor.getString(18);
			            	//originalEvent[i] = managedCursor.getString(19);
			            	//originalInstanceTime[i] = managedCursor.getInt(20);
			            	//originalAllDay[i] = managedCursor.getInt(21);
			            	//lastDate[i] = parse2Date( managedCursor.getInt(22) );
			            	//hasAttendeeData[i] = managedCursor.getInt(23);
			            	//guestsCanModify[i] = managedCursor.getInt(24);
			            	//guestsCanInviteOthers[i] = managedCursor.getInt(25);
			            	//guestsCanSeeGuests[i] = managedCursor.getInt(26);
			            	//organizer[i] = managedCursor.getString(27);
			            	//deleted[i] = managedCursor.getInt(28);

                            Log.d(TAG, "<" + calendars_name[calendar_id[i]] + "><" + loadTY[calendar_id[i]] + "><" + dtstart[i] + "><" + rrule[i] + "><" + title[i] + "><" + dtstart[i] + "><" + description[i] + "><" + eventLocation[i] + ">");
                            if (loadTY[calendar_id[i]])
                            try {
			            		view_c = ByDate(rrule[i]) ;

                                Log.d(TAG, "view_c=[" + view_c + "]");
			            		
			            		long rc = 0 ;
			            		if (dtstart[i].substring(4, 8).equals(view_c.substring(4, 8))) {
			            			rc = dbHandler.insert(title[i], dtstart[i], 2, 0, 1, description[i], eventLocation[i]) ;
			            		} else { 
			            			//rc = dbHandler.insert(title[i], view_c, 1, 0, 0, description[i], eventLocation[i]) ;
			            		}
			            		if(rc <= 0){
			    					//Toast.makeText(CalendarRead.this, "에러났다", Toast.LENGTH_LONG).show();
			    					setResult(RESULT_CANCELED);
			    				} else { 
			    					//Toast.makeText(this, "저장완료", 2000).show();
			    					setResult(RESULT_OK);
			    				}
			            		
			            	} catch (Exception e) {
			            		Log.d(TAG, "<" + title[i] + "><" + dtstart[i] + "><" + description[i] + "><" + eventLocation[i] + ">");
			            		long rc = dbHandler.insert(title[i], dtstart[i], 2, 0, 1, description[i], eventLocation[i]) ;
			    				if(rc <= 0){
			    					//Toast.makeText(CalendarRead.this, "에러났다", Toast.LENGTH_LONG).show();
			    					setResult(RESULT_CANCELED);
			    				}else{ 
			    					//Toast.makeText(this, "저장완료", 2000).show();
			    					setResult(RESULT_OK);
			    				}	
			            	}
			            	
			             managedCursor.moveToNext() ;
			            }
			            managedCursor.close();
			           }
			        
			        arGeneral.add( getResources().getString(R.string.label_msg_get_calendar)) ;
			        
			        ArrayAdapter<String> Adapter ;
			        Adapter = new ArrayAdapter<String>( CalendarRead.this, android.R.layout.simple_list_item_1, arGeneral) ;
			        ListView list = (ListView)findViewById(R.id.ListView) ;
			        list.setAdapter(Adapter) ;
					
				}
			};

		DialogInterface.OnClickListener mClickRight =
				new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						finish() ;
					}
				};

		new AlertDialog.Builder(this)
		    	.setTitle(getResources().getString(R.string.label_msg_get_calendar))
		    	.setMessage(getResources().getString(R.string.label_msg_read_calendar))
		    	.setPositiveButton(getResources().getString(R.string.label_save_btn), mClickLeft)
		    	.setNegativeButton(getResources().getString(R.string.label_cancel_btn), mClickRight)
		    	.show() ;
		        
    }
    
    //@SuppressWarnings("null")
	public static String ByDate(String rrule) {
    	
    	String[] Gubun_data = new String[10] ;
    	String[] Value_data = new String[10] ;
    	String return_value ;
    	int wDay = 0 ;
    	int wMonth = 0 ;
    	int s_pos = 0 ;
		int l_pos = 0 ;
		int d_cnt = 0 ;
		
		for (int jj = 0 ; jj < rrule.length(); jj++) {
			//arGeneral.add(rrule[i].substring(jj, jj+1));
			if (rrule.substring(jj, jj+1).equals("=")) {
				l_pos = jj ;
				Gubun_data[d_cnt] = rrule.substring(s_pos, l_pos) ;
			}
			
			if (rrule.substring(jj, jj+1).equals(";")) {
				Value_data[d_cnt] = rrule.substring(l_pos + 1, jj) ;
				s_pos = jj + 1;
				d_cnt++ ;
			}
		}
		Value_data[d_cnt] = rrule.substring(l_pos + 1, rrule.length()) ;
		
		for (int jj = 0 ; jj <= d_cnt ; jj++) {
			if (Gubun_data[jj].toString().equals("BYMONTHDAY")) {
				wDay = Integer.valueOf(Value_data[jj]) ;
			}
			if (Gubun_data[jj].toString().equals("BYMONTH")) {
				wMonth = Integer.valueOf(Value_data[jj]) ;
			}
		}

		return_value = "1902" + pad(wMonth) + pad(wDay) ; 
		if (return_value.substring(4, 8).equals("0000")) {
			return_value = "" ;
		}
		return return_value ;
		
    }
    
	public static String parseDate(long time) {
    	 
		//long time = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dd = new Date(time);
		return sdf.format(dd);
 
	}// parseDate

	public static String parse2Date(long time) {
   	 
		//long time = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date dd = new Date(time);
		return sdf.format(dd);
 
	}// parse2Date
	
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
    
}
