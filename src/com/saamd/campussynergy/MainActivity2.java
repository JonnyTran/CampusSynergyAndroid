package com.saamd.campussynergy;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity2 extends Activity {
	
	public static int ADD_EVENT_REQUEST_CODE = 92;	//WHY 92, WHY NOT?
	
	public static final String PREFS_NAME = "MyPrefsFile";
	private SharedPreferences settings;
	
    
    private static String TAG = "debug";
	
    //this is used in the subClass
    private static Activity context;
	
	//current view stuff
	private static String selectedViewName;
	private static View selectedView;
    
    
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
    
	
	private CharSequence mDrawerTitle;
    private CharSequence mTitle;
	private String[] viewTitles;
	
	
	//used to check if the user is logged in
	private String restoredUsername;
	private String restoredPassword;
	
	
	//data Structures
	private static ArrayList<Event> eventList;		//this arraylist holds all the events
	
	private Hashtable<String, Building> buildingList =  new Hashtable<String, Building>();
	
	
	//map stuff
	public static GoogleMap map;
	private final static LatLng HOME_LOCATION = new LatLng(32.731, -97.1145);		//HOME LOCATION	
	
	private ViewFragment mapFragment;
	
	
	
    
    
    //the following variables are created to preserve the views and prevent to inflater from making a duplicate view
    static View mapView = null;
    static View listView = null;
    static View addEventView = null;
    static View aboutUsView = null;
	
	
	//UI eventList
	private static ListView list;
	
	
	BroadcastReceiver newEventReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(AddActivity.NEW_EVENT_INTENT))
			{
				Event event = (Event) intent.getSerializableExtra("Event");
				//Toast.makeText(context, "New event was added", Toast.LENGTH_SHORT).show();
				eventList.add( event ); // catches the new event and adds it to the list
				//add all the building to the building list
				Building bld = new Building(event.getBuildingName());
				
				//if the new building is not highlighted already
				if (! buildingList.containsKey(event.getBuildingName()))
				{
					// we add the building to the hashtable
					buildingList.put(event.getBuildingName() ,bld);
					//adding stuff to the map fragment
					fetchBuildingDataFromXML();		//update the retrieved coordinates
					highlightBuildings();			//re highlight the buildings
					addPolygonListenerToMap();		// add Polygon listeners again
				}

			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_activity2);
		
		//set the context
		context = MainActivity2.this;
		

		//retrieve data from shared preferences
		settings = getSharedPreferences(PREFS_NAME, 0);
		restoredUsername = settings.getString("username", " ");		//getString("VAR_KEY", "DEFAULT_VALUE")
		restoredPassword = settings.getString("password", " ");
		int restoredMonth = settings.getInt("lastLoginMonth", -1);	//this is used as a flag to terminate the app from asking the users to login once every month
		
		// BETA CODE :: this ensures that the user gets logged out every month once
		checkIfLoginExpired(restoredMonth);

		
		
		// initialize parse code
		Parse.initialize(this, getString(R.string.ParseAuthCode_1), getString(R.string.ParseAuthCode_2));
		
		
		
		//check for Internet and download events
		if (!isOnline()) {
			// No Internet connection  :: we display a toast to indicate the situation
			Context context = getApplicationContext();
			CharSequence text = "No Internet Connection. No Events!!";
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
		
		//Query Parse
		queryParse();
		
		//initialize data structures
		
		//register the NEW_EVENT receiver
		IntentFilter filter = new IntentFilter(AddActivity.NEW_EVENT_INTENT);
		registerReceiver(newEventReceiver, filter);
		
		
		//link up UI
		
		
		
		//initialize UI
		
		
		
		
		/*
		 * all code after this point is dealing with the side drawer
		 */
		mTitle = mDrawerTitle = getTitle();
		
		//Retrieving the view title from the strings file
		viewTitles = getResources().getStringArray(R.array.app_views);
		//linking XML and java
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		// Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, viewTitles));
        
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
        
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
        
	}
	

	
	
	private void queryParse() {

		//initialize the array list
		eventList = new ArrayList<Event>();
		
		
		ParseQuery query = new ParseQuery("campus_synergy");
		// query.whereNotEqualTo("bldName", "");
		//this runs in the background and fires a callback when data is retrieved
		query.findInBackground(new FindCallback() {

			public void done(List<ParseObject> scoreList, ParseException e) {
				try {
					if (e == null) {
						Log.d(TAG, "Retrieved " + scoreList.size() + " scores");
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
								// so we just ignore it, the cloud code will take care of expired events
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
								
								//the following code adds the event to the event list
								eventList.add(new Event(title,buildingName, pointDescription, scoreList.get(i).getString("roomString"), scoreList.get(i).getDouble("duration"), date, publisher));
								
								//add all the building to the building list
								Building bld = new Building(buildingName);
								buildingList.put(buildingName ,bld);
								
								
								//Log.d(TAG, "Building name: " + buildingName);
							}
						}

					} else {
						Log.d(TAG, "Error: " + e.getMessage());		//tracing code
					}
				} catch (Exception e1) {
					Log.d(TAG, "Not a valid database data Exception: " + e1);		//Debugging code
				}
		
						
			//Sorting events based on date
			Collections.sort(eventList);
			
			
			//we read the XML file here
			fetchBuildingDataFromXML();	//this populates the coordinates for each building in the buildingsList
			
			//adding stuff to the map fragment
			highlightBuildings();
			addPolygonListenerToMap();
			
			
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
				list = parser.getCoordinatesOfBuilding(MainActivity2.this, bld.getName());
				//Log.d(TAG, "DEBUG" + list.toString());
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
			Log.d(TAG, "ERROR 1: XmlPullParserException");
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			Log.d(TAG, "ERROR 2: IOException");
		}catch (Exception e3) {
			// TODO Auto-generated catch block
			Log.d(TAG, "ERROR 3: Generic Exception: " + e3.toString());
		}
	}
	



	/*  This function is BETA and it should be replaced  */
	private void checkIfLoginExpired(int restoredMonth) {
		//BETA CODE ONLY: It checks if it worked this month and it deletes passwords/username if month!=restoredMonth
		int month = Calendar.getInstance().get(Calendar.MONTH);
		Log.d(TAG, "Current month: " + month + "restoredMonth: " + restoredMonth);
		if(month != restoredMonth )
		{
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("username", " ");
			editor.putString("password", " ");
			editor.putInt("lastLoginMonth", -1);	//sets the month to bad value
			editor.commit();
		}
	}

	/* checks if there is an Internet connection */
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
	
	
	/* checks if the user is logged in or not */
	public boolean isUserLoggedIn()
	{
		if (restoredUsername.equals(" ") || restoredPassword.equals(" ")) {
			// No password or username are saved, we open the login activity
			return false;
		}
		return true;
	}
	
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        
        inflater.inflate(R.menu.topright_menu, menu);
        
        /*
        //check if the user is logged in or not
        if ( isUserLoggedIn() )
        {
        	//if user is logged in
        	inflater.inflate(R.menu.logout_menu, menu);
        } else
        {
        	//if user is not logged in
            inflater.inflate(R.menu.login_menu, menu);
        }
		*/
        
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        
        menu.findItem(R.id.menu_add).setVisible(!drawerOpen);
        
        /*
        //check if the user is logged in or not
        if (isUserLoggedIn())
        {
        	//if user is logged in
        	menu.findItem(R.id.action_logout).setVisible(!drawerOpen);
        }
        else 
        {
            //if user is not logged in
            menu.findItem(R.id.action_login).setVisible(!drawerOpen);
        }
        */
        
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        
        	case R.id.menu_add:
	        	Intent intent = new Intent( context , AddActivity.class);
				startActivity(intent);
	        	break;
        	case R.id.menu_refresh:
        		queryParse();	//updates the events
        		
	        	break;
        	case R.id.menu_home:
        		updateMapToHomeLocation();
	        	break;
	        	
	        	
	        	
        	case R.id.action_login:
        	
	            // login here
	        	Log.d(TAG , "logging in");
	        	break;
        	
        	case R.id.action_logout:
        	
	        	// logout here
	        	Log.d(TAG , "logging out");
	        	break;
        	
        }
        //default always happens
        return super.onOptionsItemSelected(item);
    }
	
	
	/** Swaps fragments in the main content view */
	public void selectItem(int position) {
		// Create a new fragment and specify the view to show based on position
	    Fragment mapFragment = new ViewFragment();
	    Bundle args = new Bundle();
	    args.putInt(ViewFragment.ARG_VIEW_NUMBER, position);
	    mapFragment.setArguments(args);

	    // Insert the fragment by replacing any existing fragment
	    FragmentManager fragmentManager = getFragmentManager();
	    fragmentManager.beginTransaction()
	                   .replace(R.id.content_frame, mapFragment)
	                   .commit();

	    // Highlight the selected item, update the title, and close the drawer
	    mDrawerList.setItemChecked(position, true);
	    setTitle(viewTitles[position]);
	    mDrawerLayout.closeDrawer(mDrawerList);
	}
	
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}
	
	
	
	
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
    
    
    
    
    
    private void addPolygonListenerToMap() {
		map.setOnMapClickListener(new OnMapClickListener() {
			
			public void onMapClick(LatLng location) {
				// TODO Auto-generated method stub
				String building = whatBuildingWasClicked(location.latitude, location.longitude) ;
				if(building != null) {
					Log.d(TAG , building + " was clicked!");
					
					ArrayList<Event> events = new ArrayList<Event>();
					for(Event even : eventList)
					{
						Log.d(TAG , even.getBuildingName() + "  -  ");
						if(even.getBuildingName().equals(building))
						{
							events.add(even);
						}
					}
					if(!events.isEmpty())
					{
						Intent i = new Intent(MainActivity2.this, EventList.class);
						i.putExtra("ArrayList<Event>", events);
						startActivity(i);
					}
				}
			}
		});
	}
    
	
	private String whatBuildingWasClicked(Double lat, Double lng){
		
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
    
	
	private void highlightBuildings()
	{	
		map.clear();	//CLEARS UP THE MAP BEFORE WE ADD STUFF TO IT
		if(buildingList.isEmpty())
			Log.d(TAG, "buildingList is empty");
		for (String itm : buildingList.keySet())
		{	
			Building building = buildingList.get(itm);
			highlightBuilding(building.getName());
		}
		
	}
	
	/*
	 * Highlights a building
	 * @param buildingName: make sure building names are correct!!
	 */
	private void highlightBuilding(String buildingName)
	{
		// XML node keys
		ArrayList<Double> list = null;
		XMLParser parser = new XMLParser();
		try {
			list = parser.getCoordinatesOfBuilding(context, buildingName);

		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			Log.d(TAG, "ERROR 1: XmlPullParserException");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			Log.d(TAG, "ERROR 2: IOException");
		}

		
		
		PolygonOptions rectOptions = new PolygonOptions();
		try{
			for (int i=0; i<list.size(); i+=2)
			{
				rectOptions.add( new LatLng(list.get(i), list.get(i+1)) );
			}
			
			Polygon polygon = map.addPolygon(rectOptions);
			polygon.setStrokeColor(Color.RED);
			polygon.setStrokeWidth(2);
	        polygon.setFillColor( Color.argb(100, 225, 0, 120) );
		} catch(Exception e0){
			Log.d(TAG, "ERROR 0: Exception: BAD BUILDING NAME!");
		}
	}
    
    
	private void updateMapToHomeLocation()
	{
		// CameraUpdate update = CameraUpdateFactory.newLatLng(HOME_LOCATION);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(HOME_LOCATION, 16);
		map.animateCamera(update);
	}
    

    @Override
    protected void onResume() {
		  	
    	//this is absolutely a hack, it fixes a bug where the view disappears
		/*
    	if( selectedView.equals(aboutUsView) )
		{
			selectItem(2);
		} else if( selectedView.equals(listView))
		{
			selectItem(1);
		} else {
			selectItem(0);
		}
		*/
    	super.onResume();
    }
    

    
	
	/**
     * Fragment that appears in the "content_frame", shows a View
     */
    public static class ViewFragment extends Fragment {
        public static final String ARG_VIEW_NUMBER = "view_number";
        
        
        ArrayAdapter<Event> arrayAdapter;
        
        
        public ViewFragment() {
            // Empty constructor required for fragment subclasses
        }
        
        /*	this method sets up the map	*/
        public  void setupTheMapView()
    	{
    		//setting up the map
    		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
    		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    		// CameraUpdate update = CameraUpdateFactory.newLatLng(HOME_LOCATION);
    		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(HOME_LOCATION, 16);
    		map.animateCamera(update);
    		
    	}
        
        
        
        
        public void setUpArrayAdapter()
        {
        	arrayAdapter = new MyListAdapter();
        }
        
    	private void setUpEventListView()
        {
        	list = (ListView) listView.findViewById(R.id.eventList);	//connect java to xml
        	ArrayList<String> stringArrayList = new ArrayList<String>();
    		
        	setUpArrayAdapter();
        	
    		list.setAdapter(arrayAdapter);
    		//*
    		
    		list.setOnItemClickListener(new OnItemClickListener() {
      			 
    			@Override
    		        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
    		            
    					Intent i = new Intent(context, EventDisplay.class);
    					i.putExtra("Event", eventList.get(position) );
    					startActivity(i);
    		        }

    		});
    		
        }
    	
    	private void setUpAboutUs()
    	{
    		final String[] e_mail = new String[]{"uta.mobi@gmail.com"};
    		final String subject = "FEEDBACK (DON'T CHANGE) CSA";
    		
    		Button btn_openWebsite = (Button) aboutUsView.findViewById(R.id.button_oen_mobis_website);
    		btn_openWebsite.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse("http://idappthat.mobi/"));
					startActivity(i);
				}
			});
    		
    		Button btn_report = (Button) aboutUsView.findViewById(R.id.button_feedBack);
    		btn_report.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_SEND);
					
					i.setType("plain/text");
					i.putExtra(Intent.EXTRA_SUBJECT, subject);
					i.putExtra(Intent.EXTRA_EMAIL, e_mail);
					i.putExtra(Intent.EXTRA_TEXT, "CAMPUS SYNERGY FEEDBACK\n\n");
					startActivity(Intent.createChooser(i, "SEND FEEDBACK"));					
				}
			});
    	}
        
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = null;
            int i = getArguments().getInt(ARG_VIEW_NUMBER);
            String currentView = getResources().getStringArray(R.array.app_views)[i];
            
            
			if (currentView.equals("Map View")) {
				if(mapView == null){
					//inflate a new view
					rootView = inflater.inflate(R.layout.map_layout, container, false);
					mapView = rootView;
					
					//the following function sets up the map UI
					setupTheMapView();
					
				} else
				{
					//use the preserved view
					rootView = mapView;
				}
				
				
				
				
			} else if (currentView.equals("List View")) {
				if(listView == null)
				{
					//inflate a new view
					rootView = inflater.inflate(R.layout.activity_event_list, container,false);
					listView = rootView;
				} else
				{
					//use the preserved view
					rootView = listView;
					
				}
				//sets up the list view
				setUpEventListView();
				
				
			} else if (currentView.equals("Add Event")) {
				if(addEventView == null)
				{
					
					Intent intent = new Intent( context , AddActivity.class);
					startActivity(intent);
					
					//inflate a new view
					//rootView = inflater.inflate(R.layout.add_layout, container,false);
					//addEventView = rootView;
				} else
				{
					//use the preserved view
					rootView = addEventView;
				}
				
			} else if (currentView.equals("About Us")) {
				if(aboutUsView == null)
				{
					//inflate a new view
					rootView = inflater.inflate(R.layout.activity_credits, container, false);
					aboutUsView = rootView;
					setUpAboutUs();
				} else
				{
					//use the preserved view
					rootView = aboutUsView;
				}
			}
            
            getActivity().setTitle(currentView);
            //this is where the magic happen
            
            //we update the selected view
            selectedView = rootView;
            
            return rootView;
        }
        
        
        private class MyListAdapter extends ArrayAdapter<Event>
    	{
    		public MyListAdapter()
    		{
    			super(context, R.layout.event_list_item, eventList);
    		}

    		@Override
    		public View getView(int position, View convertView, ViewGroup parent) {
    			//making sure that we have a view (the first item will not have a view passed in, this makes sure we create one)
    			View itemView = convertView;
    			if (itemView == null){
    				itemView = context.getLayoutInflater().inflate(R.layout.event_list_item, parent, false);
    			}
    			
    			//finding the coupon that we're displaying
    			Event currentEvent = eventList.get(position);
    			
    			
    			//fill the view (filling in the items' elements)
    			TextView title, time, building;
    			
    			//linking java to XML
    			title = (TextView) itemView.findViewById(R.id.textView_list_item_title);
    			time = (TextView) itemView.findViewById(R.id.textView_list_item_time);
    			building = (TextView) itemView.findViewById(R.id.textView_list_item_building);
    			
    			
    			//filling in the data for each coupon
    			try{
    				
    				SimpleDateFormat format1 = new SimpleDateFormat("    hh:mm a\nMM/dd/yyy");
    				String eventDate = format1.format( currentEvent.getDate() );  
    				
    				title.setText(currentEvent.getTitle());
    				time.setText(eventDate);
    				building.setText(currentEvent.getBuildingName());
    			} catch(Exception e)
    			{
    				Log.d("DEBUG", "Database not populated correctly" + e.toString());
    			}
    						
    			return itemView;
    			
    		}
    		
    		
    	}
        
        
        
    }
    
    
    
    

}
