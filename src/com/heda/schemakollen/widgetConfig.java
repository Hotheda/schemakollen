package com.heda.schemakollen;

import com.heda.schemakollen.appwidget.UpdateService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class widgetConfig extends Activity {

	//Shared preferences
	SharedPreferences widgetPrefs;
	SharedPreferences.Editor SettingsEdit;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.widgetconfig_view);
	    
	    //set objects
	    RadioGroup backRadio = (RadioGroup)findViewById(R.id.WidgetradioGroupBack);
	    Spinner textColorSpinner = (Spinner)findViewById(R.id.widgetColorSpinner);
	    
	    // RADIO BUTTONS
	    // Background type and color
	    //Load saved settings
	    widgetPrefs = widgetConfig.this.getSharedPreferences("widgetPrefs", 0);
	    int radioNum = widgetPrefs.getInt("background", 0);
	    
	    //Set editor
	    SettingsEdit = widgetPrefs.edit();
	    
		//Set selected values to background
		switch(radioNum){
		case 0:
			((RadioButton)findViewById(R.id.widgetrRadioback0)).setChecked(true);
			break;
		case 1:
			((RadioButton)findViewById(R.id.widgetrRadioback1)).setChecked(true);
			break;
		case 2:
			((RadioButton)findViewById(R.id.widgetrRadioback2)).setChecked(true);
			break;
		}
		
		//Set listener
	    backRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				//Set preferences
				switch(checkedId){
				case R.id.widgetrRadioback0:
					SettingsEdit.putInt("background", 0);
					break;
				case R.id.widgetrRadioback1:
					SettingsEdit.putInt("background", 1);
					break;
				case R.id.widgetrRadioback2:
					SettingsEdit.putInt("background", 2);
					break;
				}
				//Commit changes
				SettingsEdit.commit();
				updateWidget();
			}
		});
	    final String[] colors = {"Black", "Blue", "Gray", "Green", "Red", "White", "Yellow"};
	    final int[] colorval = {Color.BLACK, Color.BLUE, Color.GRAY, Color.GREEN, Color.RED, Color.WHITE, Color.YELLOW};
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colors);
	    //Spinner to set text color
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    textColorSpinner.setAdapter(adapter);
	    
	    //Set selected values to text color
		int selectedcolor = widgetPrefs.getInt("textColor", Color.WHITE);
		for(int i=0;i<colorval.length;i++){
			if(colorval[i]==selectedcolor)
				textColorSpinner.setSelection(i);
		}
	    
	    textColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
				//Set prefs
				SettingsEdit.putInt("textColor", colorval[pos]);
				//Commit changes
				SettingsEdit.commit();
				updateWidget();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
	
			}
		});
	}
	//Update widget
	private void updateWidget() {
		this.startService(new Intent(this, UpdateService.class));		
	}
}
