package com.heda.schemakollen;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.heda.schemakollen.AVTAL.OB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class SQLite_SALARY {
	private static SQLiteDatabase sqliteDB;
	private SQLiteDBHelper DBHelper;
	private final Context myContext;
	
	//DB string names
	private static final String KEY_ID = "id";
	private static final String KEY_TYPE = "TYP";
	private static final String KEY_TABELL = "TABELL";
	private static final String KEY_MIN = "MIN";
	private static final String KEY_MAX = "MAX";
	private static final String KEY_KOL1 = "KOL1";
	private static final String KEY_KOL2 = "KOL2";
	private static final String KEY_KOL3 = "KOL3";
	private static final String KEY_KOL4 = "KOL4";
	private static final String KEY_KOL5 = "KOL5";
	private static final String KEY_KOL6 = "KOL6";
	
	private static final String DB_NAME = "salary2011.db";
	private static final String DB_TABLE = "SKATT";
	private static final int DB_VERSION = 1;
	
	/*
	//String to create a table in the database
	private static final String SQL_CREATE_TABLE =
		"CREATE TABLE "+DB_TABLE+" ("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+KEY_START+", "+KEY_END+", "+KEY_RAST+")";
	*/
	
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
			//db.execSQL(SQL_CREATE_TABLE);
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
	}
	
	//Original db class
	//Constructor
	public SQLite_SALARY(Context in_context){
		this.myContext=in_context;
		if(!checkDB()){
			try {
				Toast.makeText(myContext, "Building DB", Toast.LENGTH_SHORT).show();
				copyDB();
			} catch (IOException e) { 
				Toast.makeText(myContext, "IO Error", Toast.LENGTH_SHORT).show();
			}
		}
		else
			Toast.makeText(myContext, "DB OK", Toast.LENGTH_SHORT).show();
	}
	
    private boolean checkDB(){
    	//create a dummy DB
    	SQLiteDatabase dummyDB = null;
    	try{
    		String myPath = "/data/data/com.heda.schemakollen/databases/" + DB_NAME;
    		dummyDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	}catch(SQLiteException e){
    		//database does't exist yet.
    	}
    	if(dummyDB != null){
    		dummyDB.close();
    	}
    	return dummyDB != null ? true : false;
    }

	
	//open DB or throws exception if not
	public SQLite_SALARY open() throws SQLException{
		DBHelper = new SQLiteDBHelper(myContext);
		sqliteDB = DBHelper.getReadableDatabase();
		return this;
	}
	
	//Close DB
	public void close(){
		DBHelper.close();
	}
	/*
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
	*/
	public Cursor fetchAllNotes(){ 
		return sqliteDB.rawQuery("SELECT * FROM "+DB_TABLE, null);
	}
	
	public Cursor fetchTax(int tabell, float income){
		String retStr = "SELECT * FROM "+DB_TABLE+" WHERE "+KEY_MIN+" <= "+String.valueOf(income)+" and "+KEY_MAX+" >= "+String.valueOf(income)+" AND "+KEY_TABELL+" == "+String.valueOf(tabell);
		return sqliteDB.rawQuery(retStr , null);
	}
	
	/*
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
	*/
	/*
	public void delete(long in_id){
		sqliteDB.delete(DB_TABLE, KEY_ID+"="+in_id, null);
	}*/
	
	private void copyDB() throws IOException{
		//Open the DB in the apk
		InputStream infile = myContext.getResources().openRawResource(com.heda.schemakollen.R.raw.month2011);
		//Create a new db file
		OutputStream outfile = new FileOutputStream("/data/data/com.heda.schemakollen/databases/"+DB_NAME);
		
		byte[] buffer = new byte[1024];
    	int length;
    	while ((length = infile.read(buffer))>0){
    		outfile.write(buffer, 0, length);
    	}

    	//close streams
    	outfile.flush();
    	outfile.close();
    	infile.close();		
	}
	
	
	/*	Tabell AVTAL, här hanterar vi alla avtal med 
	 * 	KEY_ID, KEY_AVTAL, KEY_TYPE, KEY_ 
	 */
	private static final String DB_TABLE_AVTAL = "AVTAL";
	private static final String KEY_AVTAL_NAME = "NAME";
	private static final String KEY_AVTAL_TYPE = "TYP";
	private static final String KEY_AVTAL_START = "START";
	private static final String KEY_AVTAL_END = "END";
	private static final String KEY_AVTAL_DAY = "DAY";
	private static final String KEY_AVTAL_PERCENT = "PERCENT";
	private static final String KEY_AVTAL_OBSUM = "OBSUM";
	
	public Cursor fetchAvtal(String name){
		String retStr = "SELECT * FROM "+DB_TABLE_AVTAL+" WHERE "+KEY_AVTAL_NAME+" = '"+name+"'";
		return sqliteDB.rawQuery(retStr , null);
	}
	
	/*
	public Cursor deleteAvtal(String name){
		String retStr = "SELECT * FROM "+DB_TABLE_AVTAL+" WHERE "+KEY_AVTAL_NAME+" = "+name;
		return sqliteDB.rawQuery(retStr , null);
	}*/
	
	public AVTAL getAvtal(Cursor c){
		AVTAL avtal = new AVTAL();
		c.moveToFirst();
		avtal.name = c.getString(1);
		for(int i=0;i<c.getCount();i++){
			avtal.createNewOB(c.getLong(3), c.getLong(4), c.getFloat(5), c.getString(6), c.getInt(7));
			c.moveToNext();
		}
		/*
		createNoteAvtal(avtal.name, avtal.Overtime);
		createNoteAvtal(avtal.name, avtal.SickDay);
		createNoteAvtal(avtal.name, avtal.Vacation);
		createNoteAvtal(avtal.name, avtal.misc);
		for(int i=0;i<avtal.obs.size();i++)
			createNoteAvtal(avtal.name, avtal.obs.get(i));
		*/
		return avtal;
	}
	
	public void addAvtal(AVTAL avtal){
		deleteAvtal(avtal.name);
		for(int i=0;i<avtal.misc.size();i++)
			createNoteAvtal(avtal.name, avtal.misc.get(i));
		for(int i=0;i<avtal.obs.size();i++)
			createNoteAvtal(avtal.name, avtal.obs.get(i));
	}
	
	public void deleteAvtal(String avtal){
		sqliteDB.delete(DB_TABLE_AVTAL, KEY_AVTAL_NAME+" = '"+avtal+"'", null);
	}
	
	//Create new db entry
	public long createNoteAvtal(String name, AVTAL.OB ob){
		//make a input context
		ContentValues CVal = new ContentValues();
		//Set values
		CVal.put(KEY_AVTAL_NAME, name);
		CVal.put(KEY_AVTAL_TYPE, ob.type);
		CVal.put(KEY_AVTAL_START, ob.start.getMillies());
		CVal.put(KEY_AVTAL_END, ob.end.getMillies());
		CVal.put(KEY_AVTAL_DAY, ob.dayString());
		if(ob.percent)
			CVal.put(KEY_AVTAL_PERCENT, 1);
		else
			CVal.put(KEY_AVTAL_PERCENT, 0);
		CVal.put(KEY_AVTAL_OBSUM, ob.ob_sum);
		 
		//save to DB
		return sqliteDB.insert(DB_TABLE_AVTAL, null, CVal);
	}
}
