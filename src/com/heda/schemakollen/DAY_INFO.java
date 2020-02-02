package com.heda.schemakollen;

import java.util.Calendar;
import java.util.Date;

import android.widget.Toast;

public class DAY_INFO {
	//variables
	private Date startTid;
	private Date slutTid;
	private long rast;
	private boolean active;
	private int id;
	
	//STATICS
	public static final int MIDSOMMARAFTON = 8;
	public static final int MIDSOMMARDAGEN = 9;
	
	public static final int FORSTAMAJ = 10;
	public static final int SWENATIONAL = 11;
	
	public static final int LANGFREDAGEN = 12;
	public static final int PASKAFTON = 13;
	public static final int PASKDAGEN = 14;
	public static final int ANNANDAGPASK = 15;
	
	public static final int KRISTIHIMMEL = 16;
	
	public static final int JULAFTON = 17;
	public static final int JULDAGEN = 18;
	public static final int ANNANDAGJUL = 19;
	public static final int TRETTONDAGEN = 20;
	
	public static final int NYARSAFTON = 21;
	public static final int NYARSDAGEN = 22;
	//public static final int SUNDAY = Calendar.SUNDAY;
	
	
	//active
	public void setActive(boolean inActive){
		active = inActive;
	}
	public boolean isActive(){
		return active;
	}
	
	//Start tid
	public void setStartTime(Date inTime){
		startTid=inTime;
	}
	public Date getStartTime(){
		return startTid;
	}
	public String getStart_String(){
		if(startTid.getMinutes()<10)
			return String.valueOf(startTid.getHours())+":0"+String.valueOf(startTid.getMinutes());
		else
			return String.valueOf(startTid.getHours())+":"+String.valueOf(startTid.getMinutes());
	}
	
	//Slut tid
	public void setEndTime(Date inTime){
		slutTid=inTime;
	}
	public Date getEndTime(){
		return slutTid;
	}
	public String getEnd_String(){
		if(slutTid.getMinutes()<10)
			return String.valueOf(slutTid.getHours())+":0"+String.valueOf(slutTid.getMinutes());
		else
			return String.valueOf(slutTid.getHours())+":"+String.valueOf(slutTid.getMinutes());
	}
	
	//rast
	public void setRast(long inTime){
		rast=inTime;
	}
	public long getRast(){
		return rast;
	}
	public int getRastMin(){
		return (int) (((rast / 1000) / 60) % 60);
	}
	public int getRastHour(){
		return (int) ((rast / 1000) / 3600);
	}
	public String getRast_String(){
		int hour = (int) ((rast / 1000) / 3600);
		int min = (int) (((rast / 1000) / 60) % 60);
		if(min<10)
			return String.valueOf(hour)+":0"+String.valueOf(min);
		else
			return String.valueOf(hour)+":"+String.valueOf(min);
	}
	
	//Veckodag
	public String getDay_String(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(startTid);
		switch (cal.get(Calendar.DAY_OF_WEEK)){
		case Calendar.MONDAY:
			return "Måndag";
		case Calendar.TUESDAY:
    		return "Tisdag";
		case Calendar.WEDNESDAY:
        	return "Onsdag";
		case Calendar.THURSDAY:
        	return "Torsdag";
		case Calendar.FRIDAY:
        	return "Fredag";
		case Calendar.SATURDAY:
        	return "Lördag";
		case Calendar.SUNDAY:
        	return "Söndag";
		}
		return null;
	}
	
	//Datum string
	public String getDate_String(){
		return String.valueOf(startTid.getYear()+1900)+"-"+String.valueOf(startTid.getMonth())+"-"+String.valueOf(startTid.getDate());
	}
	
	
	public void setID(int in_id){
		id=in_id;
	}
	public int getID(){
		return id;
	}
	//total
	
	//strängar
	
	//Helgdagar
	public int isHoliday(){
		return isSweHoliday();
	}
	
	//Swedish holidays
    public int isSweHoliday()
    {
    	Calendar easterday = Calendar.getInstance();
    	Calendar KristiHimmel = Calendar.getInstance();
    	Calendar Midsommardagen = Calendar.getInstance();
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(startTid);
    	//set int values
        int year = cal.get(Calendar.YEAR);
        int month= cal.get(Calendar.MONTH)+1;
        int day  = cal.get(Calendar.DATE);
        //påskdagen
        easterday.setTime(EasterDay(year));
        //kristihimmel
        KristiHimmel.setTime(easterday.getTime());
        KristiHimmel.add(Calendar.DATE, 39);
        //midsommardagen
        Midsommardagen.set(year, Calendar.JUNE, 20);
        for (int i = 0; i < 7; i++)
        {
            if (Midsommardagen.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
                Midsommardagen.add(Calendar.DATE, 1);
        }

        
        if (cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)/*Söndag*/
        	return Calendar.SUNDAY;
        if(month == 12 && 25 == day)/*juldagen*/
        	return JULDAGEN;
        if(month == 12 && 26 == day)/*annandag jul*/
        	return ANNANDAGJUL;
        if(month == 1 && 1 == day) /*nyårsdagen*/
        	return NYARSDAGEN;
        if(month == 1 && 6 == day)/*trettondagen*/
        	return TRETTONDAGEN;
        if(month == 5 && 1 == day)/*förstan maj*/
        	return FORSTAMAJ;
        if(month == 6 && 6 == day)/*Nationnaldagen*/
        	return SWENATIONAL;
        if(month == easterday.get(Calendar.MONTH)+1 && easterday.get(Calendar.DATE) == day)/*Påskdagen*/
        	return PASKDAGEN;
        if(month == easterday.get(Calendar.MONTH)+1 && (easterday.get(Calendar.DATE) - 2) == day)/*långfredag*/
        	return LANGFREDAGEN;
        if(month == easterday.get(Calendar.MONTH)+1 && (easterday.get(Calendar.DATE) + 1) == day)/*annandag Påsk*/
        	return ANNANDAGPASK;
        if(month == KristiHimmel.get(Calendar.MONTH)+1 && (KristiHimmel.get(Calendar.DATE)) == day)/*KristiHimmel*/
        	return KRISTIHIMMEL;
        if(month == Midsommardagen.get(Calendar.MONTH)+1 && (Midsommardagen.get(Calendar.DATE)) == day)/*Midsommardagen*/
        	return MIDSOMMARDAGEN;
        	
        //if not a holiday
        return 0;
    }
    
    //afton (räknas som lördag i bl.a. handels
    public int isAfton(){
    	//set some static days
    	Calendar cal = Calendar.getInstance();
    	Calendar easterday = Calendar.getInstance();
    	Calendar Midsommardagen = Calendar.getInstance();
    	//set time depending on date
    	cal.setTime(startTid);
    	//calculate static days
    	easterday.setTime(EasterDay(cal.get(Calendar.YEAR)));
        //midsommardagen
        Midsommardagen.set(Calendar.YEAR, Calendar.JUNE, 20);
        for (int i = 0; i < 7; i++)
        {
            if (Midsommardagen.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
                Midsommardagen.add(Calendar.DATE, 1);
        }
    	
    	
    	if(cal.get(Calendar.MONTH) == Calendar.DECEMBER && cal.get(Calendar.DATE)==24) /*Julafton*/
    		return JULAFTON;
    	if(cal.get(Calendar.MONTH) == Calendar.DECEMBER && cal.get(Calendar.DATE)==31) /*Nyårs aftton*/
    		return NYARSAFTON;
    	if(cal.get(Calendar.MONTH) == easterday.get(Calendar.MONTH) && (easterday.get(Calendar.DATE) - 1) == cal.get(Calendar.DATE))/*påsk afton*/
    		return PASKAFTON;
    	if(cal.get(Calendar.MONTH) == Midsommardagen.get(Calendar.MONTH) && (Midsommardagen.get(Calendar.DATE) - 1) == cal.get(Calendar.DATE))/*midsommar afton*/
    		return MIDSOMMARAFTON;
    	
    	return 0;
    }
	
	//påskdagen
    private Date EasterDay(int year)
    {
        // Räkna ut vilken dag påskdagen infaller det angivna året (4 siffror, t.ex. 2001) 
        int day = 0;
        int month = 0;
        int A, b, C, D, E;

        A = year % 19;
        b = year % 4;
        C = year % 7;
        D = (19 * A + 24) % 30;
        E = ((2 * b) + (4 * C) + (6 * D) + 5) % 7;
        day = 22 + D + E;
        if (day == 57) day = day - 7;
        if (day == 56 && D == 28 && E == 6 && A > 10) day = day - 7;

        if (day > 31)
        {
            day = day - 31;
            month = 4;
        }
        else
            month = 3;
        //set a new calendar value and return it as date
        Calendar easterDay = Calendar.getInstance();
        easterDay.set(year, month-1, day);
        
        return easterDay.getTime();
    }
	
}
