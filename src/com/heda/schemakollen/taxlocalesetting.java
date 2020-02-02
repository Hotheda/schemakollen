package com.heda.schemakollen;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class taxlocalesetting extends Activity {

	//settings and editor
	private SharedPreferences userSettings;
	private SharedPreferences.Editor SettingsEdit;
	
	//objects
	private Spinner localeSpin;
	private Spinner taxTableSpin;
	private Spinner taxKolumnSpin;
	private TextView editTax;
	
	//variables
	ArrayAdapter<String> tabellAdapter;
	private final String[] tabell = {"29","30","31","32","33","34","35","36","37","Set %"};
	private final String[] locale = {"SV", "Other"};
	private final String[] kolumn = {"Kolumn 1", "Kolumn 2", "Kolumn 3" ,"Kolumn 4" ,"Kolumn 5" ,"Kolumn 6" };

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.taxlocalesetting);

		//set id's
		localeSpin = (Spinner) findViewById(R.id.tax_locale_spinner);
		taxTableSpin = (Spinner) findViewById(R.id.tax_table_spinner);
		taxKolumnSpin = (Spinner) findViewById(R.id.tax_kolumn_spinner);
		editTax = (TextView) findViewById(R.id.tax_edit);
		 
		//settings store in shared preferences
		userSettings = this.getSharedPreferences("userSettings", 0);
		SettingsEdit = userSettings.edit();
	    //int radioNum = Settings.getInt("background", 0);
		
		
		//locale picker
	    ArrayAdapter<String> localeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locale);
	    localeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    localeSpin.setAdapter(localeAdapter);
	    String selectedLocale = userSettings.getString("locale", "SV");
		for(int i=0;i<locale.length;i++){
			if(locale[i].equals(selectedLocale))
				localeSpin.setSelection(i);
		}
	    
	    //set text in textview
	    editTax.setText(String.valueOf(userSettings.getFloat("skatteprocent", 0)));
	    
	    //skattetabell picker
	    tabellAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tabell);
	    tabellAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    taxTableSpin.setAdapter(tabellAdapter);
	    //set default value from settings
	    String selectedTable = String.valueOf(userSettings.getInt("skattetabell", 29));
	    
	    //fillTabell();
	    if(selectedTable.equals("0")){
	    	taxTableSpin.setSelection(tabell.length-1);
	    }else{
			for(int i=0;i<tabell.length;i++){
				if(tabell[i].equals(selectedTable))
					taxTableSpin.setSelection(i);
			}
	    }
	    
	    //skattekolumn picker
	    ArrayAdapter<String> kolumnAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, kolumn);
	    kolumnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    taxKolumnSpin.setAdapter(kolumnAdapter);
	    //set default value from settings
	    taxKolumnSpin.setSelection(userSettings.getInt("skattekolumn", 1)-1);
	    
	    
	    //spinner för locale
	    localeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// Set locale
				SettingsEdit.putString("locale", locale[position]);
				SettingsEdit.commit();
				if(!locale[position].equals("SV"))
					taxTableSpin.setSelection(tabell.length-1);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				//Do nothing
			}
	    });
	    
	    
	    //spinner för skattetabell
	    taxTableSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long listID) {
				if(tabell[position].endsWith("%")){
					//show the current edit method
					editTax.setVisibility(TextView.VISIBLE);
					taxKolumnSpin.setVisibility(Spinner.INVISIBLE);
					SettingsEdit.putInt("skattetabell",0);
					SettingsEdit.commit();
				}
				else{
					//show the current edit method
					editTax.setVisibility(TextView.INVISIBLE);
					taxKolumnSpin.setVisibility(Spinner.VISIBLE);
					SettingsEdit.putInt("skattetabell", Integer.parseInt(tabell[position]));
					SettingsEdit.commit();
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// Do nothing
			}
	    });
	    
	    taxKolumnSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				//save to settings
				SettingsEdit.putInt("skattekolumn",position+1);
				SettingsEdit.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Do nothing
			}
		});
	    
	    editTax.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				SettingsEdit.putFloat("skatteprocent",Float.parseFloat(editTax.getText().toString()));
				SettingsEdit.commit();
				return false;
			}
	    });
	    /*
	    editTax.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// Save text as float value
				SettingsEdit.putFloat("skatteprocent",Float.parseFloat(editTax.getText().toString()));
				SettingsEdit.commit();
			}
	    });*/
	    
	}

/*
	private void fillTabell() {
		if(userSettings.getString("locale", "SV").equals("SV")){
			tabell.clear();
			tabell.add("29");
			tabell.add("30");
			tabell.add("31");
			tabell.add("32");
			tabell.add("33");
			tabell.add("34");
			tabell.add("35");
			tabell.add("36");
			tabell.add("37");
			tabell.add("Set %");
			tabellAdapter.notifyDataSetChanged();
		}
		else{
			tabell.clear();
			tabell.add("Set %");
			tabellAdapter.notifyDataSetChanged();
		}
	}
	*/
}
