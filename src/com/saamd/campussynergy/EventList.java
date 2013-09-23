package com.saamd.campussynergy;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class EventList extends Activity  {

	ArrayList<Event> eventList;
	ListView list;
	
	
	//@SuppressWarnings("unchecked")
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_list);
		list = (ListView) findViewById(R.id.eventList);	//connect java to xml
		
		eventList = (ArrayList<Event>) getIntent().getSerializableExtra("ArrayList<Event>");
		ArrayList<String> stringArrayList = new ArrayList<String>();
		
		for (Event x : eventList){
			stringArrayList.add(x.getTitle());
		}
		
		ArrayAdapter<String> arrayAdapter =      
		         new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, stringArrayList);
		
		list.setAdapter(arrayAdapter);
		//*
		list.setOnItemClickListener(new OnItemClickListener() {
			 
			@Override
		        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		            // TODO Auto-generated method stub
					Intent i = new Intent(EventList.this, EventDisplay.class);
					i.putExtra("Event", eventList.get(position) );
					startActivity(i);
		        }

		});
		
		/**/
	}
}
