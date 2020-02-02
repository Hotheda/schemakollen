package com.heda.schemakollen;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
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

public class avtalSettings extends Activity {
	
	//constants
	private static final int ID_START = 1;
	private static final int ID_END = 2;
	private static final boolean TIME_FORMAT_24H = true;
	
	//adapter class (sets view data to list)
	public class editOBadapter extends ArrayAdapter<AVTAL.OB>{
		private ArrayList<AVTAL.OB> items;
		
		Button btn_Start, btn_End, btn_OBday;
		TextView txt_OBsum;
		
		public editOBadapter(Context context, int textViewResourceId, ArrayList<AVTAL.OB> items) {
			super(context, textViewResourceId, items);
			this.items=items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
	        View v = convertView;
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.avtalsettingoblist, null);
	        }
	        final AVTAL.OB info = items.get(position);
	        final int pos = position;
	        
	        ImageButton btnDel = (ImageButton)v.findViewById(R.id.avtalEdit_imageBtn);
    	    btnDel.setBackgroundResource(android.R.drawable.ic_menu_delete);
	        
	        if (info != null) {
	        	//Set view objects
	        	//Button btn_Start, btn_End, btn_Rast;
	    	    btn_Start = (Button)v.findViewById(R.id.avtalEditObStart_btn);
	    	    btn_End = (Button)v.findViewById(R.id.avtalEditObEnd_btn);
	    	    txt_OBsum = (TextView)v.findViewById(R.id.avtalEditOBsum_txt);
	    	    btn_OBday = (Button)v.findViewById(R.id.avtalObDays);
	    	    
	        	//Set object data
        		btn_Start.setText(info.start.getString());
        		btn_End.setText(info.end.getString());
        		txt_OBsum.setText(String.valueOf(info.ob_sum));
	        }
	        
	        btnDel.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
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
		    
		    //set button to show diffrent days to use OB on
		    btn_OBday.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					final CharSequence[] Days = avtalSettings.this.getResources().getStringArray(R.array.dayStrings); //{"Red", "Green", "Blue"};
					
					AlertDialog.Builder builder = new AlertDialog.Builder(avtalSettings.this);
					builder.setTitle("Pick a color");
					final boolean[] selected = new boolean[Days.length];
					for(int i=0;i<info.day.size();i++){ 
						selected[info.day.get(i)] = true;
					}
					//info.day.clear();
					builder.setMultiChoiceItems(Days,selected,new DialogInterface.OnMultiChoiceClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which, boolean isChecked) {
							if(isChecked){
								info.day.add(which);
								Toast.makeText(avtalSettings.this, "Add"+Days[which], Toast.LENGTH_SHORT).show();
							}
							else{
								for(int i=0;i<info.day.size();i++)
									if(info.day.get(i)==which)
									{
										info.day.remove(i);
										Toast.makeText(avtalSettings.this, "Removing"+Days[which], Toast.LENGTH_SHORT).show();
									}
							}
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}
		    });
		    
	        return v;
		}
		
		protected void getTimeFromPicker(final int id, final int position) {
			TimePickerDialog.OnTimeSetListener TimeListener = new OnTimeSetListener(){
				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					//save to dayinfo
					myTime d;
					switch(id){
					//set start time values				
					case ID_START:
						//Set values to dayinfo
						d = new myTime(ActiveAvtal.obs.get(position).start.getMillies());
						d.setTime(hourOfDay, minute);
						ActiveAvtal.obs.get(position).start.setMillies(d.getMillies());
						break;
					//set end time values
					case ID_END:
						//Set values to dayinfo
						d = new myTime(ActiveAvtal.obs.get(position).end.getMillies());
						d.setTime(hourOfDay, minute);
						ActiveAvtal.obs.get(position).end.setMillies(d.getMillies());
						break;
					}
					//Update adapter
					adapter.notifyDataSetChanged();
				}
			};
			
			//Create a TimePicker dialog
			int h = 0, min = 0;
			switch(id){
			case ID_START:
				h = ActiveAvtal.obs.get(position).start.getHours();
				min = ActiveAvtal.obs.get(position).start.getMinutes();
				break;
			case ID_END:
				h = ActiveAvtal.obs.get(position).end.getHours();
				min = ActiveAvtal.obs.get(position).end.getMinutes();
				break;
			}
			TimePickerDialog TimeDiag = new TimePickerDialog(avtalSettings.this, TimeListener, h, min, TIME_FORMAT_24H );
			TimeDiag.show();
		}
	}
	
	private ListView list;
	private Button addOBbtn, editAvtal;
	private AVTAL ActiveAvtal;
	private ArrayList<AVTAL> allAvtal = new ArrayList<AVTAL>();
	private editOBadapter adapter;
	
	private SQLite_SALARY salaryDB;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.avtalsettings);
	    
	    salaryDB = new SQLite_SALARY(this);
	    ActiveAvtal = new AVTAL();
	    ActiveAvtal = readAvtalFromDB();
	    /*
	    if(ActiveAvtal==null)
	    	ActiveAvtal = new AVTAL();
	    	*/
	    
	    list = (ListView)findViewById(R.id.avtalObList);
	    addOBbtn = (Button)findViewById(R.id.avtalAddOB_btn);
	    editAvtal = (Button)findViewById(R.id.avtalSave_btn);
	    
	    adapter = new editOBadapter(this, R.layout.avtalsettingoblist, ActiveAvtal.obs);
	    
	    list.setAdapter(adapter);
	    
	    addOBbtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				ActiveAvtal.createNewOB(0, 0, 0, "1", 1);
				adapter.notifyDataSetChanged();
			}
	    });
	    editAvtal.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ActiveAvtal.name="Handels";
				editAvtalInDB(ActiveAvtal);
			}
	    });
	}
	
	private AVTAL readAvtalFromDB(){
		AVTAL avtal = new AVTAL();
		salaryDB.open();
		Cursor c = salaryDB.fetchAvtal("Handels");
		avtal = salaryDB.getAvtal(c);
		if(c.getCount()==0)
			avtal=null;
		//close all
		salaryDB.close();		
		c.close();
		return avtal;
	}
	
	private void editAvtalInDB(AVTAL avtal){
		salaryDB.open();
		salaryDB.addAvtal(avtal);
		salaryDB.close();
	}

}
