package com.saamd.campussynergy;

import java.text.SimpleDateFormat;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
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

		title = (TextView) findViewById(R.id.title);
		description = (TextView) findViewById(R.id.description);
		publisher = (TextView) findViewById(R.id.publisher);
		
		
		SimpleDateFormat format1 = new SimpleDateFormat("hh:mm a  MM/dd/yyy");
		String eventDate = format1.format( event.getDate() );  
		
		
		title.setText(event.getTitle() + "\n");
		description.setText(event.getDescription() + "\n\n" 
				+ "Located in: " + event.getBuildingName() + "\n" 
				+ "Room: " + event.getRoomNumber() + "\n"
				+ "This event starts at "+ eventDate + " and it takes " + event.getDuration() + " hours to finish.\n");
		publisher.setText("\n\nPublished by: " + event.getPublisher());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.event_display, menu);
		return true;
	}

}
