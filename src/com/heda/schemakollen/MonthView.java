package com.heda.schemakollen;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.PathShape;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MonthView extends Activity {
	
	private static final int SHAPE_TYPE=1;
	
	//adapter class (sets view data to list)
	public class monthlistadapter extends ArrayAdapter<DAY_INFO>{
		private ArrayList<DAY_INFO> items;
		
		public monthlistadapter(Context context, int textViewResourceId, ArrayList<DAY_INFO> items) {
			super(context, textViewResourceId, items);
			this.items=items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
	        View v = convertView;
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.month_list, null);
	        }
	        DAY_INFO info = items.get(position);
	        if (info != null) {
	        	//Set view objects
	        	TextView txtDate;
	        	ImageView img;
	        	
	        	txtDate = (TextView)v.findViewById(R.id.monthListText);
	        	img = (ImageView)v.findViewById(R.id.monthListImage);
	        	
	        	//get screen size
	        	int imgSize = getWindowManager().getDefaultDisplay().getWidth() / 7;
	        	
	        	//Get values from DB
	        	Cursor c=null;
		    	DAY_INFO tempdag;
		    	//store values in array
		    	ArrayList<DAY_INFO> daylist = new ArrayList<DAY_INFO>();
		    	c = myDB.fetchDaySorted(info.getStartTime());
				//if cursor exists and contain data
		    	if(c!=null && c.getCount()>0){
					//overwrite data with data from db
				    startManagingCursor(c);
				    c.moveToFirst();
				    for(int i=0;i<c.getCount();i++){
					    tempdag=new DAY_INFO();
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
	        	
	        	if(daylist.size()>0){ //info.getStartTime().getMonth()==5){
	        		
	        		int start=0;
	        		int stop=0;
	        		if(SHAPE_TYPE==1){
		        		//Loop and draw all values from DB
		        		Path p = new Path();
		        		for(int i=0;i<daylist.size();i++){
		        			start=daylist.get(i).getStartTime().getHours();
		        			stop=daylist.get(i).getEndTime().getHours();
			        		p.moveTo(start, 8);
			        		p.lineTo(stop, 8);
			        		p.lineTo(stop, 10);
			        		p.lineTo(start, 10);
			        		p.lineTo(start, 8);
		        		}
		        		
		        		ShapeDrawable rect = new ShapeDrawable(new PathShape(p,24,10));
		        		rect.setIntrinsicHeight(imgSize);
		        		rect.setIntrinsicWidth(imgSize);
		        		rect.getPaint().setColor(Color.RED);
	
		        		img.setImageDrawable(rect);
	        		}
	        		if(SHAPE_TYPE==2){
		        		//Arc shape
		        		
		        		//Set 0 to strait up
		        		start -= 90;
		        		
		        		//Draw background
		        		ShapeDrawable oval = new ShapeDrawable(new OvalShape());
		        		oval.setIntrinsicHeight(imgSize);
		        		oval.setIntrinsicWidth(imgSize);
		        		oval.getPaint().setColor(Color.DKGRAY);
		        		
		        		//loop and set arcs from DB
		        		for(int i=0;i<daylist.size();i++){
		        			//set time in 360 degrees start and stop how many degrees
			        		start= daylist.get(i).getStartTime().getHours()*15;
			        		stop = (daylist.get(i).getEndTime().getHours()*15)-start;
			        		//Set 0 to strait up
			        		start -= 90;
		        		}
		        		
		        		ShapeDrawable arc = new ShapeDrawable(new ArcShape(start, stop));
		        		
		        		
		        		arc.setIntrinsicHeight(imgSize);
		        		arc.setIntrinsicWidth(imgSize);
		        		arc.getPaint().setColor(Color.RED);
		        		
		        		//v.setBackgroundDrawable(oval);
		        		img.setImageDrawable(arc);
	        		}
	        	}
	        	
	        	img.getWidth();
	        	//int newSize = img.getWidth();
	        	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imgSize, imgSize);
	        	img.setLayoutParams(params);
	        	
	        	//If its a holyday (Red)
	        	if(info.isHoliday()!=0){
	        		txtDate.setTextColor(Color.RED);
	        	}
	        	else{
	        		txtDate.setTextColor(Color.WHITE);
	        	}
	        		
	        	txtDate.setText(String.valueOf(info.getStartTime().getDate()));
	        	
	        	//veckodag (söndag = 0)
	        	
	        }
	        return v;
		}
	}
	
	TextView monthTxt;
	GridView grid;
	monthlistadapter adapter;
	private ArrayList<DAY_INFO> DayInfo = new ArrayList<DAY_INFO>();
	private HEDA_SQLite myDB=null; //database
	Calendar dateToShow; //a date in month to show

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
		if(myDB!=null)
			myDB.close();
		super.onDestroy();
	}*/
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.month_view);
	    
	    //Get current date (using month and year
	    dateToShow = Calendar.getInstance();
	
	    //Open database
	    myDB = new HEDA_SQLite(this);
	    myDB.open();
	    
	    monthTxt = (TextView)findViewById(R.id.monthDateTxt);
	    grid = (GridView)findViewById(R.id.monthGridView);
	    adapter = new monthlistadapter(this, R.layout.month_list, DayInfo);
	    showMonth(dateToShow.getTime());
	    grid.setAdapter(adapter);
	    
	    /////////////////////////////////////////
	    final GestureDetector gestureDetector = new GestureDetector(new MyGestureDetector());
	    grid.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(gestureDetector.onTouchEvent(event))
					return true;
				return false;
			}
	    });
	    
	}

	private void showMonth(Date Day) {
		//clear list
		DayInfo.clear();
		
		//Set text of month
		setMonthTxt();
		
		//set first day of month
		Day.setHours(0);
		Day.setMinutes(0);
		Calendar cal = Calendar.getInstance();
		cal.setTime(Day);
		cal.set(Calendar.DATE,1);
		
		DAY_INFO tempdag;
		
		//get first day of calendarview
		if(cal.get(Calendar.DAY_OF_WEEK)!=Calendar.MONDAY){
			while(cal.get(Calendar.DAY_OF_WEEK)!=Calendar.MONDAY)
				cal.add(Calendar.DATE, -1);
		}
		long startday = cal.getTimeInMillis();
		
		//get last day of caledar view
		cal.add(Calendar.DATE, 28);
		cal.set(Calendar.DATE, 28);
	    while(cal.get(Calendar.DAY_OF_WEEK)!=Calendar.MONDAY){
			cal.add(Calendar.DATE, 1);
	    }
	    long endday = cal.getTimeInMillis();
		
		cal.setTimeInMillis(startday);
		//loop all days
	    while(cal.getTimeInMillis() < endday){
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
	    adapter.notifyDataSetChanged();
	}
    private void setMonthTxt() {
		// Set text in textbox (year and month)
    	String[] month = getResources().getStringArray(R.array.monthStrings);
    	monthTxt.setText(month[dateToShow.get(Calendar.MONTH)]+"  "+String.valueOf(dateToShow.get(Calendar.YEAR)));
	}

    
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
    				//show next month
    				//list.startAnimation(AnimationUtils.makeOutAnimation(WeekView.this, false));
    				grid.startAnimation(AnimationUtils.makeInAnimation(MonthView.this,false));
    				dateToShow.add(Calendar.MONTH, 1);
    			}  else if (e2.getX() - e1.getX() > SWIPE_DISTANCE && Math.abs(velocityX) > SWIPE_VELOCITY) {
    				//show previous month
    				//list.startAnimation(AnimationUtils.makeOutAnimation(WeekView.this, true)); 
    				grid.startAnimation(AnimationUtils.makeInAnimation(MonthView.this, true));
    				dateToShow.add(Calendar.MONTH, -1);
    			}
    			//Toast.makeText(MonthView.this, String.valueOf(dateToShow.get(Calendar.MONTH)), Toast.LENGTH_SHORT).show();
    			showMonth(dateToShow.getTime());
    		} catch (Exception e) {                
    			// nothing            
    		}            
    		return false;
    	}
    }
}
