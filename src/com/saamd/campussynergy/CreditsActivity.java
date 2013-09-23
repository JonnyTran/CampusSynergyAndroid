package com.saamd.campussynergy;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CreditsActivity extends Activity {
	
	
	Button feedBack;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credits);
		
		
		final String[] e_mail = new String[]{"uta.mobi@gmail.com"};
		final String subject = "FEEDBACK (DON'T CHANGE) CSA";
		
		feedBack = (Button) findViewById(R.id.feedBack);		
		feedBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.credits, menu);
		return true;
	}

}
