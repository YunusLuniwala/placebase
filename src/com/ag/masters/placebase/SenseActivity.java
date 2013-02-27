package com.ag.masters.placebase;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.ag.masters.placebase.handlers.DateHandler;
import com.ag.masters.placebase.model.Global;
import com.ag.masters.placebase.sqlite.Story;
import com.ag.masters.placebase.sqlite.StoryAudio;
import com.ag.masters.placebase.sqlite.StoryImage;
import com.ag.masters.placebase.sqlite.StoryVideo;

public class SenseActivity extends Activity {

	private Story story;
	
	// only one of these will not be null
	private StoryAudio audio;
	private StoryImage image;
	private StoryVideo video;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sense);
		
		Bundle data = getIntent().getExtras();
		
		// get the story object
		Story tempStory = data.getParcelable("story");
		if (tempStory != null) {
			// and save into a global var
			story = tempStory;
		}else {
			throw new RuntimeException("SenseActivity: story passed was null");
		}
		
		// get the correct media object from parcel 
		// based on Story's media parameter
		switch (story.getMedia()) {
			case Global.IMAGE_CAPTURE:
				StoryImage tempImage = data.getParcelable("image");
				if(tempImage != null) {
					image = tempImage;
				} else {
					throw new RuntimeException("SenseActivity: image passed was null");
				}
				break;
			case Global.AUDIO_CAPTURE:
				StoryAudio tempAudio = data.getParcelable("audio");
				if(tempAudio != null) {
					audio = tempAudio;
				} else {
					throw new RuntimeException("SenseActivity: audio passed was null");
				}
				break;
			case Global.VIDEO_CAPTURE:
				StoryVideo tempVideo = data.getParcelable("video");
				if(tempVideo != null) {
					video = tempVideo;
				} else {
					throw new RuntimeException("SenseActivity: video passed was null");
				}
				break;
		}

		

		TextView testDate = (TextView) findViewById(R.id.title_record_sense);
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
		
		testInterval.setText(thisInterval);
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_sense, menu);
		return true;
	}

}
