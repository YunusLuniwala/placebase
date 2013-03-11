package com.ag.masters.placebase;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.ImageView;

import com.ag.masters.placebase.model.DatabaseHelper;
import com.ag.masters.placebase.model.Global;
import com.ag.masters.placebase.sqlite.Story;
import com.ag.masters.placebase.sqlite.StoryAudio;
import com.ag.masters.placebase.sqlite.StoryImage;
import com.ag.masters.placebase.sqlite.StoryVideo;
import com.ag.masters.placebase.sqlite.User;

public class ConfirmTrace extends Activity {

	
	/**
	 * 
	 * GRAB the lat, lng, uri, bearing from the image metadata 
	 * (bearing might need to be adjusted to work with MapActivity parameters)
	 * record the timestamp
	 * 
	 * save the story to the database
	 * read the story _id
	 * 
	 * save that to the media type _story
	 * save the media to the db.
	 * 
	 */
	private User user;
    private Story story;
	// only one of these will not be null
	private StoryAudio audio;
	private StoryImage image;
	private StoryVideo video;
	
	DatabaseHelper dbh;
	
	private Handler handler;
	private Runnable runnable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_trace);
		
		/*dbh = new DatabaseHelper(this);
		
		dbh.openDataBase();
		
		// get Story and media parcels
				Bundle data = getIntent().getExtras();
				if (data != null) {
					// get the story object
					Story tempStory = data.getParcelable("story");
					if (tempStory != null) {
						story = tempStory;
					}else {
						throw new RuntimeException("ConfirmTrace: story passed was null");
					}
				}
				// get the story object
				User tempUser = data.getParcelable("user");
				if (tempUser != null) {
					user = tempUser;
				} else {
					// test with default user
					user = new User("ashton", "pass", "0");
					throw new RuntimeException("ConfirmTrace: user passed was null");
				}

				if(story != null) {
					// get the correct media object from parcel 
					// based on Story's media parameter
					switch (story.getMedia()) {
					case Global.IMAGE_CAPTURE:
						StoryImage tempImage = data.getParcelable("media");
						if(tempImage != null) {
							image = tempImage;
							//Log.d("CAPTION", "image caption is : " + image.getCaption());
						} else {
							throw new RuntimeException("SenseActivity: image passed was null");
						}
						break;
					case Global.AUDIO_CAPTURE:
						StoryAudio tempAudio = data.getParcelable("media");
						if(tempAudio != null) {
							audio = tempAudio;
						} else {
							throw new RuntimeException("SenseActivity: audio passed was null");
						}
						break;
					case Global.VIDEO_CAPTURE:
						StoryVideo tempVideo = data.getParcelable("media");
						if(tempVideo != null) {
							video = tempVideo;
						} else {
							throw new RuntimeException("SenseActivity: video passed was null");
						}
						break;
					}
				}
		
		// write Story to database
		dbh.createStory(story);
		// query id of the row
		int newStoryKey;
		newStoryKey = dbh.getStoryId(story.getTimestamp());
		// set foreign key
		// and write image/audio/video data to database
		switch(story.getMedia()) {
		case Global.IMAGE_CAPTURE:
			image.setStory(newStoryKey);
			dbh.createStoryImage(image);
			break;
		case Global.VIDEO_CAPTURE:
			video.setStory(newStoryKey);
			dbh.createStoryVideo(video);
			break;
		case Global.AUDIO_CAPTURE:
			audio.setStory(newStoryKey);
			dbh.createStoryAudio(audio);
			break;
		}
		
		dbh.close();
		*/
		
		ImageView aniView = (ImageView) findViewById(R.id.bg_marker);
		ObjectAnimator fadeOut = ObjectAnimator.ofFloat(aniView, "alpha",0f);
		fadeOut.setDuration(3000);
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.play(fadeOut);
		animatorSet.start();
		// delay for a few seconds before you redirect to map
		runnable = new Runnable() {
			@Override
			public void run() {
				// what you want to run after the delay
				Intent returnToMap = new Intent(ConfirmTrace.this, MapActivity.class);
				//returnToMap.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				returnToMap.putExtra("returnFromStory", true);
				//returnToMap.putExtra("user", user);
				startActivity(returnToMap);
			}
		};		
		
		handler = new Handler();

	}

	@Override
	protected void onResume() {
		super.onResume();
		handler.postDelayed(runnable, 3000);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_confirm_trace, menu);
		return true;
	}

}
