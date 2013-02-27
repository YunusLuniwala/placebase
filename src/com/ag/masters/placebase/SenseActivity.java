package com.ag.masters.placebase;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.ag.masters.placebase.handlers.DateHandler;
import com.ag.masters.placebase.sqlite.Story;

public class SenseActivity extends Activity {

	private Story story;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sense);
		
		Bundle data = getIntent().getExtras();
		Story tempStory = data.getParcelable("story");
		if (tempStory != null) {
			story = tempStory;
		}else {
			throw new RuntimeException(
					"SenseActivity: story passed was null");
		}

		
		TextView testDate = (TextView) findViewById(R.id.testDate);
		TextView testInterval = (TextView) findViewById(R.id.testInterval);
		
		// what we would be getting from the database
		String strDate = "2013-2-22 09:13:59";
		testDate.setText(strDate);
		
		DateHandler myDateHandler = new DateHandler();
		int interval = myDateHandler.getDaysAgo(strDate);
		
		String thisInterval;
		if(interval == 0) {
			thisInterval = "today";
		} else if (interval == 1) {
			thisInterval = Integer.toString(interval) + " day ago";
		} else {
			thisInterval = Integer.toString(interval) + " days ago";
		}
		
		thisInterval = Integer.toString(story.getUser());
		testInterval.setText(thisInterval);
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_sense, menu);
		return true;
	}

}
