package com.heda.schemakollen;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.Application;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class SalaryView extends Activity {

	SQLite_SALARY Salary_DB;
	HEDA_SQLite myDB;
	
	TextView totalTxt, ob50Txt, ob70Txt;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.calculate_view);
	    
	    //Set and open database
	    Salary_DB = new SQLite_SALARY(this);
	    
	    //bra eller dåligt?
	    myDB = schemakollen.mainDB;
	    /*
	    myDB = new HEDA_SQLite(this);
	    myDB.open();
	    */
	    
	    // Set objects
	    totalTxt = (TextView)findViewById(R.id.SalTotal);
	    ob50Txt = (TextView)findViewById(R.id.SalOB50);
	    ob70Txt = (TextView) findViewById(R.id.SalOB70);
	
	    Calendar cal1 = Calendar.getInstance();
	    Calendar cal2 = Calendar.getInstance();
	    /*
	    cal1.set(Calendar.DATE, 1);
	    cal2.set(Calendar.DATE,cal2.getActualMaximum(Calendar.DATE));
	    */
	    cal1.set(Calendar.DATE, 20);
	    cal2.set(Calendar.DATE, 20);
	    cal2.set(Calendar.HOUR, 23);
	    cal2.set(Calendar.MINUTE, 59);
	    
	    myTime totalTime = new myTime();
	    totalTime.setMillies(calculateTotal(cal1.getTime(), cal2.getTime()) );
	    
	    //Calculate tax
	    
	    int tax=calculateTax(10000000f, 29 ,1);
	    ob70Txt.setText(String.valueOf(tax));
	    
	    //totalt antal timmar under perioden
	    totalTxt.setText(String.valueOf(totalTime.getHours())+":"+ String.valueOf(totalTime.getMinutes()));
	    /*
	    ob70Txt.setText(String.valueOf(tabellnr));
	    */

	    
	    
	    myTime from = new myTime();
	    from.setTime(18, 15);
	    myTime to = new myTime();
	    to.setTime(20, 00);
	    
	    myTime OB50 = new myTime();
	    OB50.setMillies(calculateOB(cal1.getTime(), cal2.getTime(), from, to) );
	    
	    ob50Txt.setText(String.valueOf(OB50.getHours())+":"+ String.valueOf(OB50.getMinutes()));
	    
	    /*
	    ob50Txt.setText(String.valueOf(from.getHourMin()));
	    ob70Txt.setText(String.valueOf(to.getHourMin()));
	    */
	    //Toast.makeText(this, String.valueOf(tid.getHourMin()) , Toast.LENGTH_SHORT).show();
	}
	
	private int calculateTax(float income, int tabell, int kolumn){
		//if no tax is found return 0
		int tax=0;
		String typ="30B";
		//open Db and search in cursor
	    Salary_DB.open(); 
	    Cursor c = Salary_DB.fetchTax(tabell, income);
	    //set cursor to first entry
	    c.moveToFirst();
	    //if any data is found
	    if(c.getCount()>0){
	    	typ = c.getString(1);
		    tax = c.getInt(4+kolumn);
	    }
	    //close DB and cursor
	    Salary_DB.close();
	    c.close();
	    //check if its the sum or percentage
	    if(typ.endsWith("B")){
	    	return tax;
	    }
	    else{
	    	tax=(int)(income * (tax/100f));
	    	return tax;
	    }
	}
	
	//get total hours in date range
	private long calculateTotal(Date from, Date to){
		long totalHour=0;
		Cursor c = myDB.fetchRange(from, to);
		c.moveToFirst();
		for(int i=0; i<c.getCount();i++){
			totalHour+=c.getLong(2)-c.getLong(1)-c.getLong(3);
			c.moveToNext();
		}
		c.close();
		return totalHour;
	}
	
	//get total ob in date range
	private long calculateOB(Date from, Date to, myTime OBstartTime, myTime OBendTime){
		long totalHour=0;
		Cursor c = myDB.fetchRange(from, to);
		c.moveToFirst();
		DAY_INFO tempdag;
		for(int i=0; i<c.getCount();i++){
			tempdag = new DAY_INFO();
	    	tempdag.setID(c.getInt(0));
	    	tempdag.setStartTime(new Date(c.getLong(1)));
	    	tempdag.setEndTime(new Date(c.getLong(2)));
	    	tempdag.setRast(c.getLong(3));
	    	tempdag.setActive(true);
			totalHour+=calculateOBDay(tempdag, OBstartTime, OBendTime);
			c.moveToNext();
		}
		c.close();
		return totalHour;
	}
	
	//class to check a specific DAY_INFO for ob
	private long calculateOBDay(DAY_INFO tid, myTime OBstartTime, myTime OBendTime){
		long ob=0;
		//Set up variables
		Calendar OBstart = Calendar.getInstance();
		Calendar OBstop = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		Calendar stop = Calendar.getInstance();
		//set OB date to selected
		OBstart.setTime(tid.getStartTime());
		OBstop.setTime(tid.getStartTime());
		start.setTime(tid.getStartTime());
		stop.setTime(tid.getEndTime());
		//set OB hours and minutes
		OBstart.set(Calendar.HOUR_OF_DAY, OBstartTime.getHours());
		OBstart.set(Calendar.MINUTE, OBstartTime.getMinutes());
		OBstop.set(Calendar.HOUR_OF_DAY, OBendTime.getHours());
		OBstop.set(Calendar.MINUTE, OBendTime.getMinutes());
		
		//calculate OB
		//set time so no time is outside the OB range
		if(start.after(OBstop)){
			return 0;
		}
		if(stop.before(OBstart)){
			return 0;			
		}
		if(start.before(OBstart)){
			start.setTime(OBstart.getTime());			
		}
		if(stop.after(OBstop)){
			stop.setTime(OBstop.getTime());			
		}
		
		ob = stop.getTimeInMillis()-start.getTimeInMillis();
		
		return ob;
	}

}
