package com.ag.masters.placebase;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.ag.masters.placebase.model.Global;
import com.ag.masters.placebase.sqlite.Story;
import com.ag.masters.placebase.sqlite.StoryAudio;
import com.ag.masters.placebase.sqlite.StoryImage;
import com.ag.masters.placebase.sqlite.StoryVideo;
import com.ag.masters.placebase.sqlite.User;

public class SenseActivity extends Activity {

	private User user;
	private Story story;
	// only one of these will not be null
	private StoryAudio audio;
	private StoryImage image;
	private StoryVideo video;
	
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sense);
		context = this;
		
		Bundle data = getIntent().getExtras();

		if (data != null) {

			// get the story object
			Story tempStory = data.getParcelable("story");
			if (tempStory != null) {
				// and save into a global var
				story = tempStory;
			}else {
				throw new RuntimeException("SenseActivity: story passed was null");
			}

			// get the user object
			User tempUser = data.getParcelable("user");
			if (tempUser != null) {
				// and save into a global var
				user = tempUser;
			}else {
				throw new RuntimeException("SenseActivity: user passed was null");
			}
		}

		if(story != null) {
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
		}

		
		// test
		//story = new Story();

		// close btn
		ImageButton close = (ImageButton) findViewById(R.id.btn_close_caption);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SenseActivity.this, MapActivity.class);
				startActivity(intent);
			}
		});
		
		// record perspective button
		ImageButton record = (ImageButton) findViewById(R.id.btn_toPerspective);
		Button record2 = (Button) findViewById(R.id.btn_toPerspective2);
		record.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SenseActivity.this, PerspectiveActivity.class);
				intent.putExtra("story", story);
				intent.putExtra("user", user);
				
				switch(story.getMedia()) {
				case Global.IMAGE_CAPTURE:
					intent.putExtra("media", image);
					break;
				case Global.VIDEO_CAPTURE:
					intent.putExtra("media", video);
					break;
				case Global.AUDIO_CAPTURE:
					intent.putExtra("media", audio);
					break;
				}
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
				
			}
		});
		
		record2.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SenseActivity.this, PerspectiveActivity.class);
				intent.putExtra("story", story);
				intent.putExtra("user", user);
				
				switch(story.getMedia()) {
				case Global.IMAGE_CAPTURE:
					intent.putExtra("media", image);
					break;
				case Global.VIDEO_CAPTURE:
					intent.putExtra("media", video);
					break;
				case Global.AUDIO_CAPTURE:
					intent.putExtra("media", audio);
					break;
				}
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
				
			}
		});

		// add Click Listeners to sense Buttons
		setUpSenseBtns();
		
	}

	/**
	 * set on Click Listeners and add attributes to story element
	 * when the sense Buttons are clicked
	 */
	private void setUpSenseBtns() {
		final ImageButton btnHear = (ImageButton) findViewById(R.id.btn_record_hear);
		final ImageButton btnSee = (ImageButton) findViewById(R.id.btn_record_see);
		final ImageButton btnSmell = (ImageButton) findViewById(R.id.btn_record_smell);
		final ImageButton btnTaste = (ImageButton) findViewById(R.id.btn_record_taste);
		final ImageButton btnTouch = (ImageButton) findViewById(R.id.btn_record_touch);
		
		List<ImageButton> imageButtons = new ArrayList<ImageButton>();
		imageButtons.add(btnHear);
		imageButtons.add(btnSee);
		imageButtons.add(btnSmell);
		imageButtons.add(btnTaste);
		imageButtons.add(btnTouch);
		
		for(int i = 0; i < imageButtons.size(); i ++) {
			imageButtons.get(i).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(v == btnHear) {
						if(story.getHear() == 0) {
							btnHear.setBackgroundResource(R.drawable.btn_sense_bg_true);
							story.setHear(1);
						} else {
							btnHear.setBackgroundResource(R.drawable.btn_sense_bg_false);
							story.setHear(0);
						}
						Log.i("SENSE ACTIVITY", "Hear btn clicked");
					} else if (v == btnSee) {
						if(story.getSee() == 0) {
							btnSee.setBackgroundResource(R.drawable.btn_sense_bg_true);
							story.setSee(1);
						} else {
							btnSee.setBackgroundResource(R.drawable.btn_sense_bg_false);
							story.setSee(0);
						}
						Log.i("SENSE ACTIVITY", "See btn clicked");
					} else if  (v == btnSmell) {
						if(story.getSmell() == 0) {
							btnSmell.setBackgroundResource(R.drawable.btn_sense_bg_true);
							story.setSmell(1);
						} else {
							btnSmell.setBackgroundResource(R.drawable.btn_sense_bg_false);
							story.setSmell(0);
						}
						Log.i("SENSE ACTIVITY", "Smell btn clicked");
					} else if  (v == btnTaste) {
						if(story.getTaste() == 0) {
							btnTaste.setBackgroundResource(R.drawable.btn_sense_bg_true);
							story.setTaste(1);
						} else {
							btnTaste.setBackgroundResource(R.drawable.btn_sense_bg_false);
							story.setTaste(0);
						}
						Log.i("SENSE ACTIVITY", "Taste btn clicked");
					} else if (v == btnTouch) {
						if(story.getTouch() == 0) {
							btnTouch.setBackgroundResource(R.drawable.btn_sense_bg_true);
							story.setTouch(1);
						} else {
							btnTouch.setBackgroundResource(R.drawable.btn_sense_bg_false);
							story.setTouch(0);
						}
						Log.i("SENSE ACTIVITY", "Touch btn clicked");
					}
					
				}
			});
		}
		
		
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

}
