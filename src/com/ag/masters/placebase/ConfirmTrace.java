package com.ag.masters.placebase;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ConfirmTrace extends Activity {

	
	/**
	 * TODO: this is where you will return after recording the perspective photo
	 * 
	 * GRAB the lat, lng, uri, bearing from the image metadata 
	 * (bearing might need to be adjusted to work with MapActivity parameteres)
	 * record the timestamp
	 * 
	 * save the story to the database
	 * read the story _id
	 * 
	 * save that to the media type _story
	 * save the media to the db.
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_trace);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_confirm_trace, menu);
		return true;
	}

}
