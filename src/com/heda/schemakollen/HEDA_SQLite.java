package com.heda.schemakollen;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HEDA_SQLite{
	private static SQLiteDatabase sqliteDB;
	private SQLiteDBHelper DBHelper;
	private final Context myContext;
	
	//DB string names
	private static final String KEY_ID = "id";
	private static final String KEY_START = "start";
	private static final String KEY_END = "slut";
	private static final String KEY_RAST = "rast";
	private static final String DB_NAME = "dbschema.db";
	private static final String DB_TABLE = "tider";
	private static final int DB_VERSION = 1;
	
	//String to create a table in the database
	private static final String SQL_CREATE_TABLE =
		"CREATE TABLE "+DB_TABLE+" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+KEY_START+", "+KEY_END+", "+KEY_RAST+")";
	
	//SQL lite helper class
	private class SQLiteDBHelper extends SQLiteOpenHelper{
		public SQLiteDBHelper(Context context){ //, String TableName, CursorFactory cursor_factory, int DBversion) {
			//Set cursor to null?
			//super(context, TableName, cursor_factory, DBversion);
			// new constructor
			super(context, DB_NAME, null, DB_VERSION);
		}
	
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_TABLE);
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
	}
	
	//Original db class
	//Constructor
	public HEDA_SQLite(Context in_context){
		this.myContext=in_context;
	}
	
	//open DB or throws exception if not
	public HEDA_SQLite open() throws SQLException{
		DBHelper = new SQLiteDBHelper(myContext);
		sqliteDB = DBHelper.getWritableDatabase();
		return this;
	}
	
	//Close DB
	public void close(){
		DBHelper.close();
	}
	
	//Create new db entry
	public long createNote(Date in_start, Date in_end, long in_rast){
		//make a input context
		ContentValues CVal = new ContentValues();
		//Set values
		CVal.put(KEY_START, in_start.getTime());
		CVal.put(KEY_END, in_end.getTime());
		CVal.put(KEY_RAST, in_rast);
		//save to DB
		return sqliteDB.insert(DB_TABLE, null, CVal);
	}
	
	public Cursor fetchAllNotes(){ 
		return sqliteDB.rawQuery("SELECT * FROM "+DB_TABLE, null);
	}
	
	public Cursor fetchRange(Date from, Date to){
		String retStr = "SELECT * FROM "+DB_TABLE+" WHERE "+KEY_START+" >= "+from.getTime()+" and "+KEY_START+" <= "+to.getTime();
		return sqliteDB.rawQuery(retStr , null);
	}
	
	//Sort by KEY_START
	public Cursor fetchRangeSorted(Date from, Date to){
		String[] columns = new String[]{KEY_ID, KEY_START, KEY_END, KEY_RAST};
		String SortRetString = KEY_START+" >= "+from.getTime()+" and "+KEY_START+" <= "+to.getTime();
		return sqliteDB.query(DB_TABLE, columns, SortRetString , null, null, null, KEY_START);
	}
	
	public Cursor fetchDay(Date date){
		Date from = new Date(date.getYear(),date.getMonth(),date.getDate());
		Date to =  new Date(date.getYear(),date.getMonth(),date.getDate(),23,59);
		String retStr = "SELECT * FROM "+DB_TABLE+" WHERE "+KEY_START+" >= "+from.getTime()+" and "+KEY_START+" <= "+to.getTime();
		return sqliteDB.rawQuery(retStr , null);
	}
	
	public Cursor fetchDaySorted(Date date){
		Date from = new Date(date.getYear(),date.getMonth(),date.getDate());
		Date to =  new Date(date.getYear(),date.getMonth(),date.getDate(),23,59);
		String[] columns = new String[]{KEY_ID, KEY_START, KEY_END, KEY_RAST};
		String SortRetString = KEY_START+" >= "+from.getTime()+" and "+KEY_START+" <= "+to.getTime();
		return sqliteDB.query(DB_TABLE, columns, SortRetString , null, null, null, KEY_START);
	}
	
	public void delete(long in_id){
		sqliteDB.delete(DB_TABLE, KEY_ID+"="+in_id, null);
	}
	
}