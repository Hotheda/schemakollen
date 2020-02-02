package com.heda.schemakollen;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class settings extends Activity {
	
	private class SETTING{
		Drawable icon;
		String name;
		Intent classname;
	}
	
	//adapter class (sets view data to list)
	public class settingsadapter extends ArrayAdapter<SETTING>{
		private ArrayList<SETTING> items;
		
		public settingsadapter(Context context, int textViewResourceId, ArrayList<SETTING> items) {
			super(context, textViewResourceId, items);
			this.items=items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
	        View v = convertView;
	        if (v == null) {
	            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = vi.inflate(R.layout.settings_list, null);
	        }
	        SETTING info = items.get(position);
	        TextView text = (TextView)v.findViewById(R.id.settings_name);
	        ImageView icon = (ImageView)v.findViewById(R.id.settings_icon);
	        
	        icon.setImageDrawable(info.icon);
	        text.setText(info.name);

	        return v;
		}
	}
	
	ListView list;
	settingsadapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		list = (ListView)findViewById(R.id.settingslist);
		
		final ArrayList<SETTING> settings = new ArrayList<SETTING>();
		
		//Tax settings
		settings.add(new SETTING());
		settings.get(0).name = "Tax settings";
		settings.get(0).classname = new Intent(this, taxlocalesetting.class);
		settings.get(0).icon = getResources().getDrawable(android.R.drawable.ic_menu_manage);
		//Widget settings
		settings.add(new SETTING());
		settings.get(1).name = "Widget Settings";
		settings.get(1).classname = new Intent(this, widgetConfig.class);
		settings.get(1).icon = getResources().getDrawable(android.R.drawable.ic_menu_preferences);
		//Avtal settings
		settings.add(new SETTING());
		settings.get(2).name = "Avtal settings";
		settings.get(2).classname = new Intent(this, avtalSettings.class);
		settings.get(2).icon = getResources().getDrawable(android.R.drawable.ic_menu_preferences);
		
		
		adapter = new settingsadapter(this, R.layout.settings_list, settings);
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long list_id) {
				startActivity(settings.get(position).classname);
			}
		});
	}

}
