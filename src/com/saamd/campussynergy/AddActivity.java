package com.saamd.campussynergy;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import org.xmlpull.v1.XmlPullParserException;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddActivity extends Activity {

	public static final String PREFS_NAME = "MyPrefsFile";

	private ArrayList<String> buildings = new ArrayList<String>();
	Dialog dialog;

	private SharedPreferences settings;
	
	private Spinner dropDownSpinner;
	private EditText descriptionText;
	private EditText titleText;
	private Button durationBtn;
	
	private Button roomNumberBtn;
	private static Button startingTimeBtn;		//being static is required
	private static Button dateBtn;				//being static is required	
	private static Date eventDate;

	private static boolean flag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//retrieve data from shared preferences
		settings = getSharedPreferences(PREFS_NAME, 0);
		String restoredUsername = settings.getString("username", " ");
		String restoredPassword = settings.getString("password", " ");
		int restoredMonth = settings.getInt("lastLoginMonth", -1);	//this is used as a flag to terminate the app from asking the users to login once every month
		
		//BETA CODE ONLY: It checks if it worked this month and it deletes passwords/username if month!=restoredMonth
		int month = Calendar.getInstance().get(Calendar.MONTH);
		
		Log.d("USER", "Current month: " + month + "restoredMonth: " + restoredMonth);
		
		
		if(month != restoredMonth )
		{
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("username", " ");
			editor.putString("password", " ");
			editor.putInt("lastLoginMonth", -1);	//sets the month to bad value
			editor.commit();
		}
		
		
		
		// checks to see if username and password already exists.
		if (restoredUsername.equals(" ") || restoredPassword.equals(" ")) {
			// No password or username are saved, we open the login activity
			Intent i = new Intent(AddActivity.this, LoginActivity.class);
			startActivity(i);
			finish();
		} else {

			setContentView(R.layout.add_layout);

			// Initialize Variables, connecting xml to java
			titleText = (EditText) findViewById(R.id.editText_eventTitle);
			titleText.setSingleLine();
			dropDownSpinner = (Spinner) findViewById(R.id.spinner_buildingName);
			descriptionText = (EditText) findViewById(R.id.editText_eventDescription);
			durationBtn = (Button) findViewById(R.id.button_duration);
			roomNumberBtn = (Button) findViewById(R.id.button_roomNumber);
			dateBtn = (Button) findViewById(R.id.button_date);
			setDateButtonText(null); 	// null sets it to the current date
			startingTimeBtn = (Button) findViewById(R.id.button_startingTime);
			setStartingTimeButtonText(null);	// null sets it to the current time
			eventDate = new Date();
			
			Parse.initialize(this, getString(R.string.ParseAuthCode_1), getString(R.string.ParseAuthCode_2));
			setSpinner();
			/*
			ParseQuery query = new ParseQuery("geo_data");
			query.findInBackground(new FindCallback() {
				public void done(List<ParseObject> scoreList, ParseException e) {
					if (e == null) {
						Log.d("score", "Retrieved " + scoreList.size()
								+ " scores");
						for (int i = 0; i < scoreList.size(); i++) {

							Building building = new Building();

							ParseGeoPoint pointLocation = new ParseGeoPoint();
							pointLocation = scoreList.get(i).getParseGeoPoint(
									"location");

							building.setLatitude(pointLocation.getLatitude());
							building.setLongitude(pointLocation.getLongitude());

							building.setName(scoreList.get(i).getString(
									"bldName"));

							buildings.add(building);

							if (i + 1 == scoreList.size()) {
								Collections.sort(buildings);
								setSpinner();
							}
						}
					} else {
						Log.d("score", "Error: " + e.getMessage());
					}
				}
			});
			/**/

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	private void setSpinner() {
		// this chuck of code converts an ArrayList of strings to a string
		// array.
		
		XMLParser xm = new XMLParser();
		try {
			buildings = xm.getBuildingList(AddActivity.this);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int size = buildings.size();		
		String[] items = new String[size];
		for (int i = 0; i < size; i++) {
			items[i] = new String();
			items[i] = buildings.get(i);
			//Log.d("USER", " " + items[i]);
		}

		// sets the spinner items to the items array

		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				this, android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dropDownSpinner.setAdapter(adapter);
	}

	private String getBuildingByName(String name) {
		for (String i : buildings) {
			if (i.equals(name)) {
				return i;
			}
		}
		return null;
	}

	public void onClick_submit(View V) {
		boolean pass = true;
		if (titleText.getText().toString().equals(""))
			pass = false;
		if(descriptionText.getText().toString().equals(""))
			pass =false;
			
		if (isOnline() && pass) {
			// Internet connection available
			// Parse.initialize(this,
			// "QuoI3WPv5g9LyP4awzhZEH8FvRKIgWgFEdFJSTmB",
			// "DsDAvLDiDSLQ9VFOLRte3Ck7Yk1MmJONfeUWjZ5V");

			ParseObject campus_synergy = new ParseObject("campus_synergy");

			campus_synergy.put("title", titleText.getText().toString());
			campus_synergy.put("bldName", dropDownSpinner.getSelectedItem().toString());
			campus_synergy.put("longDescription", descriptionText.getText().toString());

			// Reformatting the string from hh:mm to hh.mm

			String holder = startingTimeBtn.getText().toString();
			String array[] = holder.split(":");
			String number = array[0] + "." + array[1];
			System.out.println("number: " + number + " Holder is: " + holder);
			campus_synergy.put("timeStart", Double.parseDouble(number));
			/**/
			campus_synergy.put("date", eventDate);
			
			campus_synergy.put("duration",
					Integer.parseInt(durationBtn.getText().toString()));
			campus_synergy.put("room",
					Double.parseDouble(roomNumberBtn.getText().toString()));

			campus_synergy.put("publisher", settings.getString("publisherName", " "));
			// campus_synergy.saveInBackground();

			campus_synergy.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException arg0) {
					// TODO Auto-generated method stub
					Context context = getApplicationContext();
					CharSequence text = "Data Submitted successfully! Thanks";
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(context, text, duration);
					toast.show();

					Intent i = new Intent(AddActivity.this, MainActivity.class);
					startActivity(i);
					finish();
				}
			});
		} else {
			// No Internet connection
			Context context = getApplicationContext();
			CharSequence text;
			
			if(pass)	text = "No Internet Connection!!";
			else text = "Check your input!!";
			
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}

		/**/
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}

	@Deprecated
	public void onClick_startingTimeBtn(View v) {

		View view = LayoutInflater.from(this).inflate(R.layout.time_picker,
				null);
		dialog = new Dialog(AddActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		dialog.getWindow().setLayout(384, 450);
		dialog.show();
		Button ok = (Button) view.findViewById(R.id.button_ok);
		final TimePicker timePicker = (TimePicker) view
				.findViewById(R.id.timePicker_dialog);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				int hour = timePicker.getCurrentHour();
				int minute = timePicker.getCurrentMinute();
				String time = hour + ":" + minute;
				startingTimeBtn.setText(time);
				dialog.hide();
			}
		});
	}

	public void onClick_durationBtn(View v) {

		View view = LayoutInflater.from(this).inflate(R.layout.number_picker,
				null);
		dialog = new Dialog(AddActivity.this);
		dialog.setTitle("Set Duration");
		//dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		//dialog.getWindow().setLayout(384, 400);
		dialog.show();
		Button ok = (Button) view.findViewById(R.id.button_ok);
		final NumberPicker numberPicker = (NumberPicker) view
				.findViewById(R.id.numberPicker1);
		String[] nums = new String[25];
		for (int i = 0; i < nums.length; i++)
			nums[i] = Integer.toString(i);
		numberPicker.setMinValue(0);
		numberPicker.setMaxValue(nums.length - 1);
		numberPicker.setWrapSelectorWheel(false);
		numberPicker.setDisplayedValues(nums);
		numberPicker.setValue(1);

		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				int hour = numberPicker.getValue();
				String time = hour + "";
				durationBtn.setText(time);
				dialog.hide();
			}
		});
	}

	public void onClick_roomNumberBtn(View V) {
		View view = LayoutInflater.from(this).inflate(R.layout.number_picker,
				null);
		dialog = new Dialog(AddActivity.this);
		dialog.setTitle("Set Room");
		//dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// dialog.requestWindowFeature()
		dialog.setContentView(view);
		//dialog.getWindow().setLayout((int) (ScreenManager.getWidth(this) * 0.8), (int) (ScreenManager.getHeight(this) * 0.5));
		dialog.show();

		// View Widgets
		Button ok = (Button) view.findViewById(R.id.button_ok);
		final NumberPicker numberPicker = (NumberPicker) view
				.findViewById(R.id.numberPicker1);

		int size = 801; // elements of the picker
		String[] nums = new String[size];
		for (int i = 0; i < size; i++)
			nums[i] = Integer.toString(i + 100);
		numberPicker.setMinValue(100);
		numberPicker.setMaxValue(size - 1);
		numberPicker.setWrapSelectorWheel(true);
		numberPicker.setDisplayedValues(nums);
		numberPicker.setValue(100);

		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				int number = numberPicker.getValue();
				roomNumberBtn.setText(Integer.toString(number));
				dialog.hide();
				//finish();
			}
		});
	}

	/** Date picker Methods+ Inner Classes**/
	
	// nulll sets it to the current date
	public static void setDateButtonText(Calendar cal) {
		// setting the calendar button text to the current date
		if (cal == null) {
			final Calendar c = Calendar.getInstance();
			dateBtn.setText(c.get(Calendar.MONTH) + 1 + " / "
					+ c.get(Calendar.DAY_OF_MONTH) + " / "
					+ c.get(Calendar.YEAR));
		} else {
			dateBtn.setText(cal.get(Calendar.MONTH) + 1 + " / "
					+ cal.get(Calendar.DAY_OF_MONTH) + " / "
					+ cal.get(Calendar.YEAR));
		}

	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
		
	}

	// date Picker
	@SuppressLint("ValidFragment")
	private static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		Calendar c = Calendar.getInstance();

		@SuppressWarnings("deprecation")
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			// final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			
			//this is the date variable to be saved in parse DB: setting it to default
			eventDate.setDate(day);
			eventDate.setMonth(month);
			eventDate.setDate(day);
			
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@SuppressWarnings("deprecation")
		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			// setting dateBtn to selected date
			c.set(year, month, day); // sets the selected date to calendar c
			
			eventDate.setDate(day);
			eventDate.setMonth(month);
			eventDate.setDate(day);
			
			setDateButtonText(c);
		}
	}
	
	/** Date picker Methods+ Inner Classes**/
	
	/** Time picker Methods + Inner Classes**/

	public static void setStartingTimeButtonText(Calendar cal) {
		// setting the calendar button text to the current date
		SimpleDateFormat format1 = new SimpleDateFormat("hh:mm a");
		if (cal == null) {
			final Calendar c = Calendar.getInstance();
			Date date = c.getTime();
			String eventTime = format1.format( date );
			startingTimeBtn.setText(eventTime);
		} else {
			
			Date date = cal.getTime();  
			String eventTime = format1.format( date );
			startingTimeBtn.setText(eventTime);
		}

	}
	
	
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getFragmentManager(), "timePicker");
	}
	
	
	@SuppressLint("ValidFragment")
	private static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
		
		Calendar c = Calendar.getInstance();
		@SuppressWarnings("deprecation")
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			
			// Use the current time as the default values for the picker
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			eventDate.setHours(hour);
			eventDate.setMinutes(minute);
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		@SuppressWarnings("deprecation")
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Do something with the time chosen by the user
			c.set(0,0,0,hourOfDay,minute); // sets the selected date to calendar c
			eventDate.setHours(hourOfDay);
			eventDate.setMinutes(minute);
			setStartingTimeButtonText(c);
		}
	}
	
	/** Time picker Methods + Inner Classes**/
	
	/**	Number picker classes	**/
	
	
	
}
