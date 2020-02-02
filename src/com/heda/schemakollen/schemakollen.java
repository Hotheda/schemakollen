package com.heda.schemakollen;

import com.heda.schemakollen.appwidget.UpdateService;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.TabHost;

//Main activity to run the app
public class schemakollen extends TabActivity {
    /** Called when the activity is first created. */
	
	//bra eller dåligt?
	public static HEDA_SQLite mainDB;
	/*
	@Override
	public void onDestroy(){
		if(mainDB!=null)
			mainDB.close();
		super.onDestroy();
	}
	*/
	@Override
	public void onPause(){
		//updateWidget();
		if(mainDB!=null)
			mainDB.close();
		super.onPause();
	}
	@Override
	public void onResume(){
		super.onResume();
		mainDB.open();
	}
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Open main database
        mainDB = new HEDA_SQLite(this);
        //mainDB.open();
        
        //Some reusable variables
       	TabHost tabHost = getTabHost();
       	Intent intent;
        TabHost.TabSpec spec;
        
        //Tabb 2 (Vecko översikt)
        intent = new Intent().setClass(this, WeekView.class);
        spec = tabHost.newTabSpec("week").setIndicator("Week").setContent(intent);
        tabHost.addTab(spec);
        //tabb 1
        intent = new Intent().setClass(this, MonthView.class);
        spec = tabHost.newTabSpec("month").setIndicator("Month").setContent(intent);
        tabHost.addTab(spec);
        //Tabb 3 (Löneuträkning)
        intent = new Intent().setClass(this, SalaryView.class);
        spec = tabHost.newTabSpec("salary").setIndicator("Salary").setContent(intent);
        tabHost.addTab(spec);
    }
    
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu){
		//create options menu
		super.onCreateOptionsMenu(menu);
		//menu install apk
		menu.add("Settings")
		.setIcon(android.R.drawable.ic_menu_preferences)
		.setOnMenuItemClickListener(new OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				Intent file_browser = new Intent(schemakollen.this, settings.class);
				startActivityForResult(file_browser, 3);
				return false;
			}
		});
		return true;
	}
	
	//run the update widget intent
	private void updateWidget() {
		this.startService(new Intent(this, UpdateService.class));		
	}
}