package com.heda.schemakollen;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;

public class AVTAL {
	//the ob array class
	public class OB{
		//constructor
		OB(){
			day = new ArrayList<Integer>();
			start = new myTime();
			end = new myTime();
			percent=true;
			ob_sum=0;
			type = "";
		}
		public String dayString(){
			String retStr = "";
			for(int i=0;i<day.size();i++){
				retStr.concat(String.valueOf(getDayFromint(day.get(i)))); //
			}
			return retStr;
		}
		public String type;
		public myTime start;
		public myTime end;
		public ArrayList <Integer> day;
		public Boolean percent;
		public float ob_sum;
	}
	//constructor
	AVTAL(){
		obs = new ArrayList<OB>();
		misc = new ArrayList<OB>();
		name ="";
	}
	
	public String name;
	public ArrayList<OB> obs;
	public ArrayList<OB> misc;
	
	/*
	public OB misc;
	public OB SickDay;
	public OB Vacation;
	public OB Overtime;
	*/

	public void createNewOB(long start, long end, float OBsum, String days, int percent){
		//create a new object
		OB new_ob = new OB();
		//fill object data
		new_ob.type="OB";
		new_ob.start.setMillies(start);
		new_ob.end.setMillies(end);
		//loop the string and extract days
		new_ob.day.clear();
		new_ob.ob_sum=OBsum;
		if(percent==1)
			new_ob.percent=true;
		else
			new_ob.percent=false;
		for(int i=0;i<days.length();i++){
			new_ob.day.add(getDayFromChar(days.toUpperCase().charAt(i)));
		}
		obs.add(new_ob);
	}
	
	public void createNewMisc(String type, long start, long end, float OBsum, String days, int percent){
		//create a new object
		OB new_ob = new OB();
		//fill object data
		new_ob.type=type;
		new_ob.start.setMillies(start);
		new_ob.end.setMillies(end);
		//loop the string and extract days
		new_ob.day.clear();
		new_ob.ob_sum=OBsum;
		if(percent==1)
			new_ob.percent=true;
		else
			new_ob.percent=false;
		for(int i=0;i<days.length();i++){
			new_ob.day.add(getDayFromChar(days.toUpperCase().charAt(i)));
		}
		misc.add(new_ob);
	}
	
	
	// method to parse the day string
	//transform char to int
	private int getDayFromChar(char c){
		switch (c){
		case '1':
			return Calendar.SUNDAY;
		case '2':
			return Calendar.MONDAY;
		case '3':
			return Calendar.TUESDAY;
		case '4':
			return Calendar.WEDNESDAY;
		case '5':
			return Calendar.THURSDAY;
		case '6':
			return Calendar.FRIDAY;
		case '7':
			return Calendar.SATURDAY;
		case 'A':
			return DAY_INFO.ANNANDAGJUL;
		case 'B':
			return DAY_INFO.ANNANDAGPASK;
		case 'C':
			return DAY_INFO.FORSTAMAJ;
		case 'D':
			return DAY_INFO.JULAFTON;
		case 'E':
			return DAY_INFO.JULDAGEN;
		case 'F':
			return DAY_INFO.KRISTIHIMMEL;
		case 'G':
			return DAY_INFO.LANGFREDAGEN;
		case 'H':
			return DAY_INFO.MIDSOMMARAFTON;
		case 'I':
			return DAY_INFO.MIDSOMMARDAGEN;
		case 'J':
			return DAY_INFO.NYARSAFTON;
		case 'K':
			return DAY_INFO.NYARSDAGEN;
		case 'L':
			return DAY_INFO.PASKAFTON;
		case 'M':
			return DAY_INFO.PASKDAGEN;
		case 'N':
			return DAY_INFO.SWENATIONAL;
		case 'O':
			return DAY_INFO.TRETTONDAGEN;
		}
		return 0;
	}
	
	//transform int to char
	private char getDayFromint(int i){
		switch (i){
		case Calendar.SUNDAY:
			return '1';
		case Calendar.MONDAY:
			return '2';
		case Calendar.TUESDAY:
			return '3';
		case Calendar.WEDNESDAY:
			return '4';
		case Calendar.THURSDAY:
			return '5';
		case Calendar.FRIDAY:
			return '6';
		case Calendar.SATURDAY:
			return '7';
		case DAY_INFO.ANNANDAGJUL:
			return 'A';
		case DAY_INFO.ANNANDAGPASK:
			return 'B';
		case DAY_INFO.FORSTAMAJ:
			return 'C';
		case DAY_INFO.JULAFTON:
			return 'D';
		case DAY_INFO.JULDAGEN:
			return 'E';
		case DAY_INFO.KRISTIHIMMEL:
			return 'F';
		case DAY_INFO.LANGFREDAGEN:
			return 'G';
		case DAY_INFO.MIDSOMMARAFTON:
			return 'H';
		case DAY_INFO.MIDSOMMARDAGEN:
			return 'I';
		case DAY_INFO.NYARSAFTON:
			return 'J';
		case DAY_INFO.NYARSDAGEN:
			return 'K';
		case DAY_INFO.PASKAFTON:
			return 'L';
		case DAY_INFO.PASKDAGEN:
			return 'M';
		case DAY_INFO.SWENATIONAL:
			return 'N';
		case DAY_INFO.TRETTONDAGEN:
			return 'O';
		}
		return 0;
	}
}
