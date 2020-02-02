package com.heda.schemakollen;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import com.heda.schemakollen.appwidget.UpdateService;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class WeekView extends Activity{
	
	//adapter class (sets view data to list)
	public class weeklistadapter extends ArrayAdapter<DAY_INFO>{
		private ArrayList<DAY_INFO> items;
		
		public weeklistadapter(Context context, int textViewResourceId, ArrayList<DAY_INFO> items) {
			super(context, textViewResourceId, items);
			this.items=items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
	        View v = convertView;
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.week_list, null);
	        }
	        DAY_INFO info = items.get(position);
	    	Cursor c=null;
	    	//get all times from this day
	    	DAY_INFO tempdag;
	    	ArrayList<DAY_INFO> daylist = new ArrayList<DAY_INFO>();
	    	c = myDB.fetchDaySorted(info.getStartTime());
			//if cursor exists and contain data	    	
	    	if(c!=null && c.getCount()>0){
				//overwrite data with data from db
			    startManagingCursor(c);
			    c.moveToFirst();
			    
			    for(int i=0;i<c.getCount();i++){
			    	tempdag = new DAY_INFO();
			    	tempdag.setID(c.getInt(0));
			    	tempdag.setStartTime(new Date(c.getLong(1)));
			    	tempdag.setEndTime(new Date(c.getLong(2)));
			    	tempdag.setRast(c.getLong(3));
			    	tempdag.setActive(true);
			    	daylist.add(tempdag);
			    	c.moveToNext();
			    }
			}
	    	c.close();
	    	
	        if (info != null) {
	        	//Set view objects
	        	TextView txtWorktime, txtRast, txtTotal, txtWeekday, txtDate;
	        	txtWeekday = (TextView)v.findViewById(R.id.weeklist_weekday);
	        	txtDate = (TextView)v.findViewById(R.id.weeklist_date);
	        	txtWorktime = (TextView)v.findViewById(R.id.weeklist_worktime);
	        	txtRast = (TextView)v.findViewById(R.id.weeklist_rast);
	        	txtTotal = (TextView)v.findViewById(R.id.weeklist_total);
	        	
	        	//Set object data
	        	if(daylist.size()>0){
	        		String workTime="";
	        		for(int i=0;i<daylist.size();i++){
	        			if(daylist.get(i).getRast()==0)
	        				workTime=workTime+daylist.get(i).getStart_String()+" - "+daylist.get(i).getEnd_String();
	        			else
	        				workTime=workTime+daylist.get(i).getStart_String()+" - "+daylist.get(i).getEnd_String()+" ("+daylist.get(i).getRast_String()+")";
	        			if(i<daylist.size()-1)
	        				workTime+="\n";
	        		}
	        		txtWorktime.setText(workTime);
	        		txtRast.setText("");
	        		txtTotal.setText("");
	        		/*
	        		txtWorktime.setText(info.getStart_String()+" - "+info.getEnd_String());
	        		txtRast.setText("Rast: "+info.getRast_String()); //fullösning?
	        		txtTotal.setText("");*/
	        		v.setBackgroundColor(Color.rgb(0, 0, 55));
	        	}
	        	else{
	        		txtWorktime.setText("Ledig");
	        		txtRast.setText("");
	        		txtTotal.setText("");
	        		v.setBackgroundColor(Color.BLACK);
	        	}
	        	//Set textcolors
	        	//Red if holiday
	        	if(info.isHoliday()!=0){
	        		txtDate.setTextColor(Color.RED);
	        		txtWeekday.setTextColor(Color.RED);
	        	}
	        	else{
	        		txtDate.setTextColor(Color.WHITE);
	        		txtWeekday.setTextColor(Color.WHITE);
	        	}
	        	
	        	//Visa datum
	        	txtDate.setText(String.valueOf(info.getStartTime().getDate())+"/"+String.valueOf(info.getStartTime().getMonth()+1));
	        	//visa veckodag
	        	txtWeekday.setText(info.getDay_String());
	        		        	
	        }
	        return v;
		}
	}

	/** Called when the activity is first created. */
	//Statics
	private static final int R_EDITDAY = 0;
	private static final String S_ACTIVE = "ACTIVE";
	private static final String S_RAST = "RAST";
	private static final String S_END = "END_TIME";
	private static final String S_START = "START_TIME";
	private static final String S_LIST_ID = "LIST_ID";
	//private static final String S_DELETE = "DELETE";
	
	//variables
	private TextView weekNumTxt, weekYearTxt;
	private weeklistadapter adapter;
	private ListView list;
	private ArrayList<DAY_INFO> DayInfo = new ArrayList<DAY_INFO>();
	
	private HEDA_SQLite myDB=null; //database
		
	/*
	@Override
	public void onResume(){
		super.onResume();
		myDB = new HEDA_SQLite(this);
	    myDB.open();
	}
	@Override
	public void onPause(){
		myDB.close();
		super.onPause();
	}*/
	
	/*
	@Override
	public void onDestroy(){
		updateWidget();
		if(myDB!=null)
			myDB.close();
		super.onDestroy();
	}
	*/
	/*
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
	    setContentView(R.layout.week_view);
	    list = (ListView) findViewById(R.id.week_list);
	    
	    weekNumTxt = (TextView)findViewById(R.id.weekNum_txt);
	    weekYearTxt = (TextView)findViewById(R.id.weekYear_txt);
	}*/
	
	//main (on create)
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    //setContentView(R.layout.week_view);
	    setContentView(R.layout.week_view);
	    list = (ListView) findViewById(R.id.week_list);
	    
	    weekNumTxt = (TextView)findViewById(R.id.weekNum_txt);
	    weekYearTxt = (TextView)findViewById(R.id.weekYear_txt);
	    
	    //Open database
	    myDB = new HEDA_SQLite(this);
	    myDB.open();
	    
	    adapter = new weeklistadapter(this, R.layout.week_list, DayInfo);
	    
	    showWeek(FirstDayOfWeek(new Date()));
	    
	    list.setAdapter(adapter);
	    list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long list_id) {
				//Open edit_day.java and send intent values
				Intent EditIntent = new Intent(WeekView.this, EditDay.class);
				//Set day to edit
				EditIntent.putExtra(S_START, DayInfo.get(position).getStartTime().getTime());
				//Start edit activity
				startActivityForResult(EditIntent, R_EDITDAY);
			}
	    });
	    list.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long list_id) {
		    	//build dialog to confirm removal of day
				AlertDialog.Builder builder = new AlertDialog.Builder(WeekView.this);
				builder.setTitle(DayInfo.get(position).getDate_String());
				builder.setMessage("Vill du ta bort dag?");
				builder.setCancelable(true);
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//Delete all data on this day
						Cursor c = myDB.fetchDay(DayInfo.get(position).getStartTime());
						if(c!=null && c.getCount()>0){
				    		c.moveToFirst();
				    		//delete all DB values for this day
				    		for(int i=0;i<c.getCount();i++){
				    			int dbID = c.getInt(0);
				    			myDB.delete(dbID);
				    			if(!c.isLast())
				    				c.moveToNext();
				    		}
				    		c.close();
				    	}
						//refresh list
						refreshList();
					}
				});
				builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Do nothing if cancel
					}
				});
				
				AlertDialog dialog = builder.create();
				dialog.show();

				return false;
			}
	    });
	    /////////////////////////////////////////
	    //Swipe gesture builder
	    class MyGestureDetector extends SimpleOnGestureListener {
	    	@Override  
	    	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {            
	    		final ViewConfiguration vc = ViewConfiguration.get(getBaseContext());
	    		final int SWIPE_DISTANCE = vc.getScaledTouchSlop();
	    		final int SWIPE_VELOCITY = vc.getScaledMinimumFlingVelocity();
	    		try{
	    			// right to left swipe                
	    			if(e1.getX() - e2.getX() > SWIPE_DISTANCE && Math.abs(velocityX) > SWIPE_VELOCITY) {
	    				//list.startAnimation(AnimationUtils.makeOutAnimation(WeekView.this, false));
	    				list.startAnimation(AnimationUtils.makeInAnimation(WeekView.this,false));
	    				showNextWeek();
	    			}  else if (e2.getX() - e1.getX() > SWIPE_DISTANCE && Math.abs(velocityX) > SWIPE_VELOCITY) {
	    				//list.startAnimation(AnimationUtils.makeOutAnimation(WeekView.this, true)); 
	    				list.startAnimation(AnimationUtils.makeInAnimation(WeekView.this, true));
	    				showPreviousWeek();	
	    			}            
	    		} catch (Exception e) {                
	    			// nothing            
	    		}            
	    		return false;
	    	}
	    }
	    /////////////////////////////////////////
	    final GestureDetector gestureDetector = new GestureDetector(new MyGestureDetector());
	    list.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(gestureDetector.onTouchEvent(event))
					return true;
				return false;
			}
	    });
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch(requestCode){
			case R_EDITDAY:
				if(resultCode == RESULT_OK){
					
				}
				//update list
				showWeek(FirstDayOfWeek(DayInfo.get(0).getStartTime()));
				break;
		}
	}
	
	private Date FirstDayOfWeek(Date ActiveWeek){
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(ActiveWeek);
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		int year = cal.get(Calendar.YEAR); 
		cal.clear();
		cal.set(Calendar.WEEK_OF_YEAR, week);
		cal.set(Calendar.YEAR, year);
		return cal.getTime();
	}
	
	private Date FirstDayNextWeek(Date ActiveWeek){
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(ActiveWeek);
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		int year = cal.get(Calendar.YEAR); 
		cal.clear();
		cal.set(Calendar.WEEK_OF_YEAR, week+1);
		cal.set(Calendar.YEAR, year);
		return cal.getTime();
	}
	private Date FirstDayPreviousWeek(Date ActiveWeek){
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(ActiveWeek);
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		int year = cal.get(Calendar.YEAR); 
		cal.clear();
		cal.set(Calendar.WEEK_OF_YEAR, week-1);
		cal.set(Calendar.YEAR, year);
		return cal.getTime();
	}
	private void showNextWeek(){
		Date first = FirstDayNextWeek(DayInfo.get(0).getStartTime());
		showWeek(first);
	}
	
	private void showPreviousWeek(){
		Date first = FirstDayPreviousWeek(DayInfo.get(0).getStartTime());
		showWeek(first);
	}
	
	private void refreshList() {
		Date first = DayInfo.get(0).getStartTime();
		showWeek(first);
	}
	
	private void showWeek(Date firstDayOfWeek){
		Date first = firstDayOfWeek;
		DayInfo.clear();
	    DAY_INFO tempdag;
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(first);
	    
	    for(int i=first.getDate();i<first.getDate()+7;i++){
	    	tempdag=new DAY_INFO();

	    	tempdag.setActive(false);
	    	tempdag.setStartTime(cal.getTime());
	    	tempdag.setEndTime(cal.getTime());
	    	tempdag.setRast(0*1000*60); //1800000); // 30 min
	    	
			//add day to list
			DayInfo.add(tempdag);
			//next day
			cal.add(Calendar.DATE, 1);
	    }
	    
	    //Show weeknumber and total hours
	    Calendar weekCal = Calendar.getInstance(new Locale("sv","SWEDEN"));
	    weekCal.setTime(firstDayOfWeek);
	    //Set title text
	    weekYearTxt.setText(String.valueOf(weekCal.get(Calendar.YEAR)));
	    weekNumTxt.setText("v."+String.valueOf(weekCal.get(Calendar.WEEK_OF_YEAR))+" - ("+TotalHoursWeek(firstDayOfWeek)+" timmar)");
	    
	    adapter.notifyDataSetChanged();
	}
	
	private String TotalHoursWeek(Date firstDay){
		firstDay.setHours(0);
		firstDay.setMinutes(0);
		Calendar cal = Calendar.getInstance();
		cal.setTime(firstDay);
		cal.add(Calendar.DATE, 7);
		Date secondDay = new Date(cal.getTimeInMillis());
		
		Cursor c = myDB.fetchRange(firstDay, secondDay);
		c.moveToFirst();
		int timeInMillis=0;
		
		for(int i=0;i<c.getCount();i++){
			//1=från ; 2=till; 3=rast
	    	timeInMillis+=c.getLong(2)-c.getLong(1)-c.getLong(3);
	    	c.moveToNext();
		}
		//return hours and minutes
		int hour = (int) ((timeInMillis / 1000) / 3600);
		int min = (int) (((timeInMillis / 1000) / 60) % 60);
		c.close();
		if(min<10)
			return String.valueOf(hour)+":0"+String.valueOf(min);
		else
			return String.valueOf(hour)+":"+String.valueOf(min);		
	}
}
