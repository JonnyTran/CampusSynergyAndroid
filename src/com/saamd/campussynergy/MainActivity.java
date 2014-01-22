package com.saamd.campussynergy;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;














import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptionsCreator;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MainActivity extends Activity {
	public static final boolean debug = false;  // Setting this to true will cause debug messages to appear with tag USER
	
	private final LatLng HOME_LOCATION = new LatLng(32.72898611111111, -97.11500833333332);		//HOME LOCATION	
	private ArrayList<Event> eventList = new ArrayList<Event>();		//this arraylist holds all the events
	private Hashtable<String, Building> buildingList =  new Hashtable<String, Building>();
	private GoogleMap map;
	Dialog dialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this code initializes parse databases 
		Parse.initialize(this, getString(R.string.ParseAuthCode_1), getString(R.string.ParseAuthCode_2));
		setContentView(R.layout.activity_main);		//sets the layout to the activity_main.xml
		popupMenu();		//this method runs the popup menu that displays the campusSynergy logo
		
		
		Button leButton = (Button) findViewById(R.id.btn_List);
		leButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, EventList.class);
				i.putExtra("ArrayList<Event>", eventList);
				startActivity(i);
			}
		});
		
		Button infoButton = (Button) findViewById(R.id.info);
		infoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, CreditsActivity.class);
				startActivity(i);
			}
		});		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		// CameraUpdate update = CameraUpdateFactory.newLatLng(HOME_LOCATION);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(HOME_LOCATION, 16);
		map.animateCamera(update);
		
		//isOnline method checks to see if the device has Internet connectivity or not
		if (!isOnline()) {
			// No Internet connection  :: we display a toast to indicate the situation
			Context context = getApplicationContext();
			CharSequence text = "No Internet Connection. No Events!!";
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}

		// Retrieving data and adds markers
		ParseQuery query = new ParseQuery("campus_synergy");
		// query.whereNotEqualTo("bldName", "");
		//this runs in the background and fires a callback when data is retrieved
		query.findInBackground(new FindCallback() {

			public void done(List<ParseObject> scoreList, ParseException e) {
				try {
					if (e == null) {
						Log.d("score", "Retrieved " + scoreList.size() + " scores");
						for (int i = 0; i < scoreList.size(); i++) {
							Date date;
							Calendar eventDate = null;
							date = scoreList.get(i).getDate("date");	//gets event date
							Calendar currentDate = Calendar.getInstance();
							//converting date to calendar
							DateFormat format=new SimpleDateFormat("yyyy/mm/dd hh:MM");
							format.format(date);
							eventDate = format.getCalendar();
							eventDate.add(Calendar.HOUR_OF_DAY, (int) scoreList.get(i).getDouble("duration"));
							
							if(eventDate.compareTo(currentDate) < 0)
							{
								//so the event date/time is already passed
								//we delete the object from dataBase
								//scoreList.get(i).deleteInBackground();
								//This could introduce a huge security risk
							}
							else
							{
								// event date is more than or equal to current date/time
								// so we add the event to the eventList
								String title, buildingName, pointDescription, publisher;
								
								title = scoreList.get(i).getString("title");		//title
								buildingName = scoreList.get(i).getString("bldName");		//building name
								pointDescription = scoreList.get(i).getString("longDescription");	//description
														//date
								publisher = scoreList.get(i).getString("publisher");
								
								ParseGeoPoint pointLocation = new ParseGeoPoint();
								//pointLocation = scoreList.get(i).getParseGeoPoint("location");
								//LatLng location = new LatLng(pointLocation.getLatitude(), pointLocation.getLongitude());

								MarkerOptions markerOptions = new MarkerOptions();
								// the following line adds the marker to the map object
								//map.addMarker(markerOptions.position(location).title(title).snippet(buildingName.toString()));
								//the following code adds the event to the event list
								eventList.add(new Event(title,buildingName, pointDescription, scoreList.get(i).getString("roomString"), scoreList.get(i).getDouble("duration"), date, publisher));
								
								//add all the building to the building list
								Building bld = new Building(buildingName);
								buildingList.put(buildingName ,bld);
								
								highlightBuilding(buildingName);
							}
							
							
							// Log.d("test", "eventList size = " + eventList.size());
						}

					} else {
						Log.d("score", "Error: " + e.getMessage());		//tracing code
					}
				} catch (Exception e1) {
					Log.d("Error", "Not a valid database data Exception: " + e1);		//debuging code
				}
				

				//addMarkerListenerToMap();		//this method adds an onclick listener to each marker that has data
				
				fetchBuildingDataFromXML();
				
				addPolygonListenerToMap();
				
				//Sorting events based on date
				Collections.sort(eventList);
				
				
			}
		});

	}
	
	private void fetchBuildingDataFromXML(){
		
		ArrayList<Double> list = null;
		XMLParser parser = new XMLParser();
		
		try {
			for (String itm : buildingList.keySet())
			{
				Building bld = buildingList.get(itm);
				list = parser.getCoordinatesOfBuilding(MainActivity.this, bld.getName());
				//Log.d("USER", "DEBUG" + list.toString());
				//size is guaranteed to be even number
				for (int i=0; i< list.size(); i+=2)
				{
					bld.addLatitude( list.get(i) );
					bld.addLongitude( list.get(i+1) );
				}
				bld.prepObject();	//sorts the lists
			}

		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			Log.d("USER", "ERROR 1: XmlPullParserException");
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			Log.d("USER", "ERROR 2: IOException");
		}catch (Exception e3) {
			// TODO Auto-generated catch block
			Log.d("USER", "ERROR 3: Generic Exception: " + e3.toString());
		}
	}
	
	
	private void addPolygonListenerToMap() {
		map.setOnMapClickListener(new OnMapClickListener() {
			
			public void onMapClick(LatLng location) {
				// TODO Auto-generated method stub
				String building = whatBuildingWasClicked(location.latitude, location.longitude) ;
				if(building != null) {
					Log.d("USER" , building);
					
					ArrayList<Event> events = new ArrayList<Event>();
					for(Event even : eventList)
					{
						Log.d("USER" , even.getBuildingName() + "  -  ");
						if(even.getBuildingName().equals(building))
						{
							events.add(even);
						}
					}
					if(!events.isEmpty())
					{
						Intent i = new Intent(MainActivity.this, EventList.class);
						i.putExtra("ArrayList<Event>", events);
						startActivity(i);
					}
				}
			}
		});
	}
	
	//currently not used
	private void addMarkerListenerToMap() {
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) { // TODO

				Event tmp = findEventByTitleAndBuilding(marker.getTitle(), marker.getSnippet());
				if (tmp != null) {
					
					Intent i = new Intent(MainActivity.this, EventDisplay.class);
					i.putExtra("Event", tmp);
					startActivity(i);
					
				} else {
					Log.d("Error", "addListenerToMap : Line 147");
				}

			}
		});
	}
	
	//currently not used
	private Event findEventBySnippet(String description) {
		for (Event i : eventList) {
			if ((i.getDescription()).equals(description)) {
				return i;
			}
		}
		return null;

	}

	private Event findEventByTitleAndBuilding(String title, String buildingName) {
		for (Event i : eventList) {
			if ((i.getBuildingName()).equals(buildingName) && i.getTitle().equals(title)) {
				return i;
			}
		}
		return null;

	}
	
	//currently not used
	private void addMarker(final String buildingName, final String description, final String room, final String startingTime, final String duration) {
		// Setting a custom info window adapter for the google map
		map.setInfoWindowAdapter(new InfoWindowAdapter() {

			// Use default InfoWindow frame
			@Override
			public View getInfoWindow(Marker arg0) {
				return null;
			}

			// Defines the contents of the InfoWindow
			@Override
			public View getInfoContents(Marker arg0) {

				// Getting view from the layout file info_window_layout
				View v = getLayoutInflater().inflate(R.layout.marker_info_window_layout, null);
				// linking to xml widgets
				TextView textView_building = (TextView) v.findViewById(R.id.textView_building);
				TextView textView_description = (TextView) v.findViewById(R.id.textView_description);
				TextView textView_roomNumber = (TextView) v.findViewById(R.id.textView_roomNumber);
				TextView textView_startingTime = (TextView) v.findViewById(R.id.textView_startingTime);
				TextView textView_duration = (TextView) v.findViewById(R.id.textView_duration);
				// Returning the view containing InfoWindow contents
				textView_building.setText(buildingName.toString());
				textView_description.setText(description.toString());
				textView_roomNumber.setText(room.toString());
				textView_startingTime.setText(startingTime.toString());
				textView_duration.setText(duration.toString());

				return v;

			}
		});
	}

	private void popupMenu() {
		View view = LayoutInflater.from(this).inflate(R.layout.popup, null);
		dialog = new Dialog(MainActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		//dialog.getWindow().setLayout(384, 512);
		dialog.show();
		Button ok = (Button) view.findViewById(R.id.button_popup_ok);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.hide();
			}
		});
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}

	public void onClick_add(View V) {
		// Class newClass =
		// Class.forName("com.saamd.campussynergy.AddActivity");
		Intent i = new Intent(MainActivity.this, AddActivity.class);
		startActivity(i);
	}
	//not used anymore
	public void showEventList(){
		View view = LayoutInflater.from(this).inflate(R.layout.event_list_item, null);
		dialog = new Dialog(MainActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_OPTIONS_PANEL);
		dialog.setContentView(view);
		//dialog.getWindow().setLayout( 600 , 700 );
		dialog.show();
		
		ArrayList<String> stringArrayList = new ArrayList<String>();
		for (Event x : eventList){
			stringArrayList.add(x.getTitle());
		}
		
		/*TabHost tabHost= (TabHost) view.findViewById(R.id.tabhost);
		tabHost.setup();

		TabSpec spec1=tabHost.newTabSpec("Tab 1");
		spec1.setContent(R.id.tab1);
		spec1.setIndicator("Tab 1");

		TabSpec spec2=tabHost.newTabSpec("Tab 2");
		spec2.setIndicator("Tab 2");
		spec2.setContent(R.id.tab2);
		
		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		*/
		ListView list1 = (ListView) view.findViewById(R.id.eventList);
		ArrayAdapter<String> arrayAdapter =      
		         new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, stringArrayList);
		         list1.setAdapter(arrayAdapter);
		//dummy comment
		//2nd dummy
	}
	
	public String whatBuildingWasClicked(Double lat, Double lng){
		
		for (String itm : buildingList.keySet())
		{	
			Building build = buildingList.get(itm);
			if(lat > build.getMinLatitude() && lat < build.getMaxLatitude() && lng > build.getMinLongitude() && lng < build.getMaxLongitude())
			{
				return build.getName();		//return building name
			}
		}
		return null;		//base case: if no building was clicked
	}
	
	
	/**
	 * Highlights a building
	 * @param buildingName: make sure building names are correct!!
	 */
	public void highlightBuilding(String buildingName)
	{
		// XML node keys
		ArrayList<Double> list = null;
		XMLParser parser = new XMLParser();
		try {
			list = parser.getCoordinatesOfBuilding(MainActivity.this, buildingName);
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			Log.d("USER", "ERROR 1: XmlPullParserException");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			Log.d("USER", "ERROR 2: IOException");
		}
		
		
		PolygonOptions rectOptions = new PolygonOptions();
		try{
			for (int i=0; i<list.size(); i+=2)
			{
				rectOptions.add( new LatLng(list.get(i), list.get(i+1)) );
			}
			
			Polygon polygon = map.addPolygon(rectOptions);
			polygon.setStrokeColor(Color.RED);
			polygon.setStrokeWidth(1);
	        polygon.setFillColor( Color.argb(100, 225, 0, 120) );
		} catch(Exception e0){
			Log.d("USER", "ERROR 0: Exception: BAD BUILDING NAME!");
		}
	}
}
