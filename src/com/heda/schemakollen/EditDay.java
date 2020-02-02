package com.heda.schemakollen;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.heda.schemakollen.WeekView.weeklistadapter;
import com.heda.schemakollen.appwidget.UpdateService;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditDay extends Activity{
	//constants
	private static final int ID_START = 1;
	private static final int ID_END = 2;
	private static final int ID_RAST = 3;
	
	private static final String S_ACTIVE = "ACTIVE";
	private static final String S_RAST = "RAST";
	private static final String S_END = "END_TIME";
	private static final String S_START = "START_TIME";
	private static final String S_LIST_ID = "LIST_ID";
	
	
	//adapter class (sets view data to list)
	public class editlistadapter extends ArrayAdapter<DAY_INFO>{
		private ArrayList<DAY_INFO> items;
		
		Button btn_Start, btn_End, btn_Rast;
		
		public editlistadapter(Context context, int textViewResourceId, ArrayList<DAY_INFO> items) {
			super(context, textViewResourceId, items);
			this.items=items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
	        View v = convertView;
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.editday_item, null);
	        }
	        DAY_INFO info = items.get(position);
	        final int pos = position;
	        
	        ImageButton btnDel = (ImageButton)v.findViewById(R.id.Edit_imageBtn);
    	    btnDel.setBackgroundResource(android.R.drawable.ic_menu_delete);
	        
	        if (info != null) {
	        	//Set view objects
	        	//Button btn_Start, btn_End, btn_Rast;
	    	    btn_Start = (Button)v.findViewById(R.id.editStart_btn);
	    	    btn_End = (Button)v.findViewById(R.id.editEnd_btn);
	    	    btn_Rast = (Button)v.findViewById(R.id.editRast_btn);
	    	    
	    	    
	        	
	        	//Set object data
        		btn_Start.setText(info.getStart_String());
        		btn_End.setText(info.getEnd_String());
        		btn_Rast.setText(info.getRast_String());
	        }
	        
	        btnDel.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					//Delete from DB
					if(DayInfo.get(pos).getID()!=-1)
						myDB.delete(DayInfo.get(pos).getID());
					// Delete day
					items.remove(pos);
					adapter.notifyDataSetChanged();
				}
	        });
		    //Add OnClickLister to start time
		    btn_Start.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					getTimeFromPicker(ID_START, pos); //passed by ref so value is stored
				}
		    });
		    //Add OnClickLister to end time
		    btn_End.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					getTimeFromPicker(ID_END, pos); //passed by ref so value is stored
				}
		    });
		    //Add OnClickLister to end time
		    btn_Rast.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					getTimeFromPicker(ID_RAST, pos); //passed by ref so value is stored
				}
		    });
	        
	        return v;
		}
		
		protected void getTimeFromPicker(final int id, final int position) {
			TimePickerDialog.OnTimeSetListener TimeListener = new OnTimeSetListener(){
				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					//save to dayinfo
					Date d;
					switch(id){
					//set start time values				
					case ID_START:
						//Set values to dayinfo
						d = new Date(DayInfo.get(position).getStartTime().getTime());
						d.setHours(hourOfDay);
						d.setMinutes(minute);
						DayInfo.get(position).setStartTime(d);
						if(DayInfo.get(position).getStartTime().after(DayInfo.get(position).getEndTime())){
							DayInfo.get(position).setEndTime(DayInfo.get(position).getStartTime());
						}							
						break;
					//set end time values
					case ID_END:
						d = new Date(DayInfo.get(position).getEndTime().getTime());
						d.setHours(hourOfDay);
						d.setMinutes(minute);
						if(d.after(DayInfo.get(position).getStartTime())){						
							DayInfo.get(position).setEndTime(d);
						}else{
							Toast.makeText(EditDay.this, "Felaktig tid", Toast.LENGTH_SHORT).show();
						}
						break;
					//set rast values
					case ID_RAST:
						DayInfo.get(position).setRast( ( (hourOfDay*60) + minute )*1000*60);
						break;
					}
					//Update values in list
					//Delete from DB
					if(DayInfo.get(position).getID()!=-1)
						myDB.delete(DayInfo.get(position).getID());
					//Add new value to DB
					DayInfo.get(position).setID((int)myDB.createNote(DayInfo.get(position).getStartTime(), DayInfo.get(position).getEndTime(), DayInfo.get(position).getRast()));
					//Update adapter
					adapter.notifyDataSetChanged();
				}
			};
			
			//Create a TimePicker dialog
			int h = 0, min = 0;
			switch(id){
			case ID_START:
				h = DayInfo.get(position).getStartTime().getHours();
				min = DayInfo.get(position).getStartTime().getMinutes();
				break;
			case ID_END:
				h = DayInfo.get(position).getEndTime().getHours();
				min = DayInfo.get(position).getEndTime().getMinutes();
				break;
			case ID_RAST:
				h = DayInfo.get(position).getRastHour();
				min = DayInfo.get(position).getRastMin();
				break;
			}
			TimePickerDialog TimeDiag = new TimePickerDialog(EditDay.this, TimeListener, h, min, TIME_FORMAT_24H );
			TimeDiag.show();
		}
	}
	
	//var
	private TextView txt_TitleDate, txt_TitleWeekday;
	private Button btn_New;
	private static final boolean TIME_FORMAT_24H = true;
	private DAY_INFO activeDay = new DAY_INFO();
	//Database
	private HEDA_SQLite myDB=null;
	private ListView list;
	private editlistadapter adapter;
	
	private ArrayList<DAY_INFO> DayInfo = new ArrayList<DAY_INFO>();

	/*
	@Override
	public void onResume(){
		super.onResume();
		myDB = new HEDA_SQLite(this);
	    myDB.open();
	}*/
	/*
	@Override
	public void onPause(){
		updateWidget();
		super.onPause();		
	}*/
	//close the DB
	/*
	@Override
	public void onDestroy(){
		updateWidget();
		super.onDestroy();
	}*/
	//run the update widget intent
	private void updateWidget() {
		this.startService(new Intent(this, UpdateService.class));		
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
    	setContentView(R.layout.editday_view);
	    //get objects
    	txt_TitleWeekday = (TextView)findViewById(R.id.txt_EditTitleWeekday);
	    txt_TitleDate = (TextView)findViewById(R.id.txt_EditTitleDate);
	    btn_New = (Button)findViewById(R.id.EditNew_btn);
	    list = (ListView)findViewById(R.id.editday_list);

	        
	    //get values from intent
	    Intent i = getIntent();
	    activeDay.setStartTime(new Date(i.getLongExtra(S_START, 0)));
	    //Set title
	    txt_TitleDate.setText(activeDay.getDate_String());
	    txt_TitleWeekday.setText(activeDay.getDay_String());
	    
	    //open database
	    myDB = new HEDA_SQLite(this);
	    myDB.open();
	    
	    //Create the adapter
	    adapter = new editlistadapter(this, R.layout.editday_item, DayInfo);
	    //Get data from DB
	    getDayData();
	    //Set adapter to list
	    list.setAdapter(adapter);
	    
	    
	    btn_New.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DAY_INFO tempday=new DAY_INFO();
				tempday.setID(-1);
				tempday.setStartTime(new Date(activeDay.getStartTime().getTime()));
				tempday.setEndTime(new Date(activeDay.getStartTime().getTime()));
				DayInfo.add(tempday);
				adapter.notifyDataSetChanged();
			}
	    });
	}

	/*
	private void setText(int id, int h, int min){
		switch(id){
		//set start time text
		case ID_START:
			if(min<10)
				btn_Start.setText(String.valueOf(h)+":0"+String.valueOf(min));
			else
				btn_Start.setText(String.valueOf(h)+":"+String.valueOf(min));
			break;
		//set end time text
		case ID_END:
			if(min<10)
				btn_End.setText(String.valueOf(h)+":0"+String.valueOf(min));
			else
				btn_End.setText(String.valueOf(h)+":"+String.valueOf(min));
			break;
		//set rast text
		case ID_RAST:
			if(min<10)
				btn_Rast.setText(String.valueOf(h)+":0"+String.valueOf(min));
			else
				btn_Rast.setText(String.valueOf(h)+":"+String.valueOf(min));
			break;
		}
	}*/
	
	private void getDayData(){
		//clear array
		DayInfo.clear();
		//Load cursor
		Cursor c = myDB.fetchDaySorted(activeDay.getStartTime());
		//Loop and get data
		if(c!=null && c.getCount()>0){
			//reset cursor
			c.moveToFirst();
			//set tempday
			DAY_INFO tempDay;
			for(int i=0;i<c.getCount();i++){
				tempDay = new DAY_INFO();
				tempDay.setID(c.getInt(0));
		    	tempDay.setStartTime(new Date(c.getLong(1)));
		    	tempDay.setEndTime(new Date(c.getLong(2)));
		    	tempDay.setRast(c.getLong(3));
		    	tempDay.setActive(true);
				DayInfo.add(tempDay);
				c.moveToNext();
			}
		}
		else{
			//Create empty day to edit
		}
		c.close();
		//update list
		adapter.notifyDataSetChanged();
		//update widget (if exists)
		//updateWidget();
	}
}
