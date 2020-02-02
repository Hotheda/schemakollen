package com.heda.schemakollen;

public class myTime {
	private long millis;
	//constructor
	myTime(){
		millis=0;
	}
	myTime(long in_millis){
		millis=in_millis;
	}
	public void setMillies(long inMillis){
		millis=inMillis;
	}
	public void setTime(int h, int min){
		millis=h*60*60*1000;
		millis+=min*60*1000;
	}
	public long getMillies(){
		return millis;
	}
	public int getHours(){
		return (int) ((millis/1000) / 3600);
	}
	public int getMinutes(){
		return (int) (((millis/1000) / 60) % 60);
	}
	public float getHourMin(){
		return (((millis/1000f) / 60f) / 60f);
	}
	public String getString(){
		if(getMinutes()<10)
			return String.valueOf(getHours())+":0"+String.valueOf(getMinutes());
		else
			return String.valueOf(getHours())+":"+String.valueOf(getMinutes());
	}
}
