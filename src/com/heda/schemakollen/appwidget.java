package com.heda.schemakollen;


import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

public class appwidget extends  AppWidgetProvider{
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	context.startService(new Intent(context, UpdateService.class));
    }
    
    @Override
    public void onEnabled(Context context){
    	super.onEnabled(context); 
    	/*
    	SharedPreferences widgetPrefs;
    	widgetPrefs = context.getSharedPreferences("widgetPrefs", 0);
    	SharedPreferences.Editor SettingsEdit = widgetPrefs.edit();
		SettingsEdit.putInt("background", 1);
		SettingsEdit.commit();
		*/
    }
    
    public static class UpdateService extends Service {
    	@Override
    	public void onStart(Intent intent, int startId) {
    		// Build the widget update for today
    		RemoteViews updateViews = buildUpdate(this);
    		// Push update for this widget to the home screen
    		ComponentName thisWidget = new ComponentName(this, appwidget.class);
    		AppWidgetManager manager = AppWidgetManager.getInstance(this);
    		manager.updateAppWidget(thisWidget, updateViews);
    	}        

    	//build widget
    	public RemoteViews buildUpdate(Context context) {
    		HEDA_SQLite myDB=null;
    		myDB = new HEDA_SQLite(this);
    	    myDB.open();
    	    Calendar cal = Calendar.getInstance();
    	    cal.add(Calendar.DATE, 7); 
    	    Cursor c = null;
    	    //c = myDB.fetchDay(cal.getTime());
    	    c = myDB.fetchRangeSorted(new Date(), cal.getTime());
    		//startManagingCursor(c);
		    c.moveToFirst();
    		
    		//widget stuff
    		RemoteViews updateViews = null;
    		updateViews = new RemoteViews(context.getPackageName(), R.layout.appwidget);
    		DAY_INFO tempdag = new DAY_INFO();
    		Intent intent = new Intent(context, schemakollen.class);
    		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
    		//updateViews.setOnClickPendingIntent(R.id.WidgetTextTime, pendingIntent);
    		updateViews.setOnClickPendingIntent(R.id.widgetlayout, pendingIntent);
	    	String tempTxt = "";
	    	if(c!=null && c.getCount()>0){
				//overwrite data with data from db
    			//Set max size of days to 2
    			int size =2;
    			if(c.getCount()<2)
    				size = c.getCount();
    			//loop and get days 
		    	for(int i=0;i<size;i++){
			    	tempdag.setStartTime(new Date(c.getLong(1)));
			    	tempdag.setEndTime(new Date(c.getLong(2)));
			    	tempdag.setRast(c.getLong(3));
			    	tempdag.setActive(true);
			    	//tempTxt.concat(tempdag.getDay_String()+"\n"+tempdag.getStart_String()+"-"+tempdag.getEnd_String());
			    	tempTxt=tempTxt+tempdag.getDay_String()+"\n"+tempdag.getStart_String()+"-"+tempdag.getEnd_String()+"\n";
			    	c.moveToNext();
		    	}
		    	//Set text in textview
		    	updateViews.setTextViewText(R.id.WidgetTextTime, tempTxt);
	    		//updateViews.setTextViewText(R.id.WidgetTextTime, tempdag.getDay_String()+"\n"+tempdag.getStart_String()+"-"+tempdag.getEnd_String());
	    	}
	    	else{ 
	    		updateViews.setTextViewText(R.id.WidgetTextTime, "Ledig");
	    	}
	    	/*
	    	//set background
	    	Drawable bmp = getResources().getDrawable(R.drawable.widget);
	    	Paint p = new Paint(Color.RED);
	    	ColorFilter filter = new LightingColorFilter(Color.RED, 1);
	    	bmp.setColorFilter(filter);
	    	*/
	    	SharedPreferences prefs = context.getSharedPreferences("widgetPrefs", 0);
	    	int back = prefs.getInt("background", 0);
	    	switch(back){
	    	case 0:
	    		updateViews.setImageViewResource(R.id.widgetbmp, 0 );
	    		break;
	    	case 1:
	    		updateViews.setImageViewResource(R.id.widgetbmp, R.drawable.widget );
	    		break;
	    	case 2:
	    		updateViews.setImageViewResource(R.id.widgetbmp, R.drawable.widgetdark );
	    		break;
	    	}
	    	
	    	//Set text color
	    	updateViews.setTextColor(R.id.WidgetTextTime, prefs.getInt("textColor", Color.WHITE));
	    	
	    	//close all
	    	c.close();
	    	myDB.close();

    		return updateViews;
		}      
    	@Override  
    	public IBinder onBind(Intent intent) {    
    		// We don't need to bind to this service     
    		return null;    
    	}
    	
    }
}
