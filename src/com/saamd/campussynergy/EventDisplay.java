package com.saamd.campussynergy;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.parse.ParseInstallation;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class EventDisplay extends Activity {

	private Event event;
	private TextView title, description, publisher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_display);

		event = (Event) getIntent().getSerializableExtra("Event"); // catches
																	// passed
																	// event
																	// through
																	// intent

		description = (TextView) findViewById(R.id.description);
		publisher = (TextView) findViewById(R.id.publisher);
		
		
		ImageButton btnAddCalendar = (ImageButton) findViewById(R.id.button_add_to_calendar);
		btnAddCalendar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar cal = Calendar.getInstance();   
				cal.setTime( event.getDate() );
				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setType("vnd.android.cursor.item/event");
				intent.putExtra("beginTime", cal.getTimeInMillis());
				cal.add(Calendar.HOUR_OF_DAY , (int) event.getDuration() );
				intent.putExtra("endTime",  cal.getTimeInMillis() );
				intent.putExtra("title", event.getTitle());
				intent.putExtra("description", event.getDescription() );
				intent.putExtra("eventLocation", event.getBuildingName() + ", " + "UTA" + " Room " + event.getRoomNumber());
				startActivity(intent);
			}
		});
		

		
		
		SimpleDateFormat format1 = new SimpleDateFormat("hh:mm a  MM/dd/yyy");
		String eventDate = format1.format( event.getDate() );  
		
		setTitle(event.getTitle());		//set the title
		
		
		
		description.setText(event.getDescription() + "\n\n" 
				+ "Located in: " + event.getBuildingName() + "\n" 
				+ "Room: " + event.getRoomNumber() + "\n"
				+ "This event starts at "+ eventDate + " and it takes " + event.getDuration() + " hours to finish.\n");
		publisher.setText("\n\nPublished by: " + event.getPublisher());
		
	}

}
