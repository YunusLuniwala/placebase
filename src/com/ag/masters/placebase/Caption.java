package com.ag.masters.placebase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ag.masters.placebase.handlers.SDImageLoader;
import com.ag.masters.placebase.sqlite.Story;
import com.ag.masters.placebase.sqlite.StoryImage;

public class Caption extends Activity {

	Story story;
	StoryImage image;
	
	EditText caption;
	ImageView photoView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_caption);
		
		/* catch Story & StoryImage from recording */
		Bundle data = getIntent().getExtras();
		if (data != null) {

			Story tempStory = data.getParcelable("story");
			if (tempStory != null) {
				story = tempStory; // global var
			}else {
				throw new RuntimeException("SenseActivity: story passed was null");
			}

			StoryImage tempImage = data.getParcelable("image");
			if (tempImage != null) {
				image = tempImage; // global var
			}else {
				throw new RuntimeException("SenseActivity: image passed was null");
			}
		}

		/* UI buttons*/
		Button btnDone = (Button) findViewById(R.id.btn_caption_done);
		ImageButton close = (ImageButton) findViewById(R.id.btn_close_caption);
		
		/* UI containers */
		caption = (EditText) findViewById(R.id.edit_caption);
		photoView = (ImageView) findViewById(R.id.recent_photo);
		
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Caption.this, MapActivity.class);
				startActivity(intent);
			}
		});
		
		btnDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				// save caption to image object
				String mCaption = caption.getText().toString();
				image.setCaption(mCaption);
				
				Intent intent = new Intent(Caption.this, SenseActivity.class);
				// add parcels to Intent 
				intent.putExtra("image", image);
				intent.putExtra("story", story);
				// launch senses activity
				startActivity(intent);
			}
		});

		populatePhotoView();		
		
	}
	
	/**
	 * display the recent photo in the photoView widget
	 */
	private void populatePhotoView() {
		SDImageLoader imageLoader = new SDImageLoader();
		imageLoader.load(image.getUri(), photoView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_caption, menu);
		return true;
	}

}
