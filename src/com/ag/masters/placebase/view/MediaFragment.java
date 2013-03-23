package com.ag.masters.placebase.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ag.masters.placebase.MapActivity;
import com.ag.masters.placebase.R;
import com.ag.masters.placebase.RetrieveMedia;
import com.ag.masters.placebase.handlers.DateHandler;
import com.ag.masters.placebase.handlers.SDImageLoader;
import com.ag.masters.placebase.model.Global;
import com.ag.masters.placebase.sqlite.DatabaseHelper;
import com.ag.masters.placebase.sqlite.Encounter;
import com.ag.masters.placebase.sqlite.Story;
import com.ag.masters.placebase.sqlite.StoryAudio;
import com.ag.masters.placebase.sqlite.StoryImage;
import com.ag.masters.placebase.sqlite.StoryVideo;
import com.ag.masters.placebase.sqlite.User;

public class MediaFragment extends Fragment implements OnPreparedListener, MediaController.MediaPlayerControl {

	DatabaseHelper dbh;

	int mediaType;

	MediaPlayer mp; 
	MediaController mc;
	
	boolean pausing = false;

	ImageButton _btnHear;
	ImageButton _btnSee;
	ImageButton _btnSmell;
	ImageButton _btnTaste;
	ImageButton _btnTouch;

	Story story;
	User user;
	Encounter encounter;
	
	// only one of these will not be null
	StoryImage image;
	StoryVideo video;
	StoryAudio audio;

	// encounter shtuff
	Encounter priorEncounter;
	int numEncountersInDb;
	int numEncountersActual;
	boolean usersFirstEncounter = true;
	// comment shtuff
	int numComments;

	
	
	
	public MediaFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// get parcel from FragmentActivity
		FragmentActivity fa = (RetrieveMedia)getActivity();
		
		Intent launchingIntent = fa.getIntent();
		Bundle data = launchingIntent.getExtras();

		story = data.getParcelable("story");
		user = data.getParcelable("user");
		// key to query specific media object
		mediaType = story.getMedia();
		
		// initialize encounter object
		encounter = new Encounter(story.getId(), user.getId());
		priorEncounter = null;
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		dbh = new DatabaseHelper(getActivity());

		// get prior encounters
		dbh.openDataBase();
		// get stored encounter count for this story
		if(story.getId() != 0) { // check first that we have a story object
			numEncountersInDb = dbh.getEncounterCountForStory(story.getId());
			Log.d("ENCOUNTERS" , "number of encounters saved in database: " + numEncountersInDb);
			// check if this is user's first encounter
			priorEncounter = dbh.getUsersPriorEncounter(story.getId(), user.getId());
			
			// get the number of comments for this story 
			numComments = dbh.getCommentCount(story.getId());
			Log.d("COMMENTS" , "number of comments saved in database: " + numComments);
			
		}
		dbh.close();

		// if this is the first time, add one to the number returned from the db.
		if(priorEncounter == null) {
			numEncountersActual = numEncountersInDb + 1; 
			// +1 is this user : )
			// hold off on actually writing this to the DB 
			// until we have sense data and can do it all onDestory TODO
			// But set values so buttons don't freak out later
			encounter.setHear(0);
			encounter.setSee(0);
			encounter.setSmell(0);
			encounter.setTaste(0);
			encounter.setTouch(0);
		} else {
			numEncountersActual = numEncountersInDb;
			//populate Encounter object with user's prior history here
			encounter = priorEncounter;
			usersFirstEncounter = false;
		}

		// inflate the xml layout
		View v = inflater.inflate(R.layout.view_retrieve_media, container, false);

		// sense buttons
		_btnHear = (ImageButton) v.findViewById(R.id.media_hear);
		_btnSee = (ImageButton) v.findViewById(R.id.media_see);
		_btnSmell = (ImageButton) v.findViewById(R.id.media_smell);
		_btnTaste = (ImageButton) v.findViewById(R.id.media_taste);
		_btnTouch = (ImageButton) v.findViewById(R.id.media_touch);

		// back to map btn
		ImageButton _btnToMap = (ImageButton) v.findViewById(R.id.btn_to_map);
		_btnToMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MapActivity.class);
				intent.putExtra("user", user);
				// mimick the back button
				getActivity().onBackPressed();
			}
		});

		// comments/ encounter counts and story attributes
		TextView numComments = (TextView) v.findViewById(R.id.num_comments);
		numComments.setText(Integer.toString(this.numComments));
		
		TextView numEncounters = (TextView) v.findViewById(R.id.num_encounters);
		numEncounters.setText(Integer.toString(numEncountersActual));

		TextView metaTime = (TextView) v.findViewById(R.id.meta_timestamp);
		metaTime.setText(setTimestampAndAuthor()); 

		TextView metaGeo = (TextView) v.findViewById(R.id.meta_geo);
		metaGeo.setText(setGeo());
		
		ImageView storyHear = (ImageView) v.findViewById(R.id.story_hear);
		ImageView storySee = (ImageView) v.findViewById(R.id.story_see);
		ImageView storySmell = (ImageView) v.findViewById(R.id.story_smell);
		ImageView storyTaste = (ImageView) v.findViewById(R.id.story_taste);
		ImageView storyTouch = (ImageView) v.findViewById(R.id.story_touch);

		if(story.getHear() == 1) {
			storyHear.setVisibility(View.VISIBLE);
		}
		if(story.getSee() == 1) {
			storySee.setVisibility(View.VISIBLE);
		}
		if(story.getSmell() == 1) {
			storySmell.setVisibility(View.VISIBLE);
		}
		if(story.getTaste() == 1) {
			storyTaste.setVisibility(View.VISIBLE);
		}
		if(story.getTouch() == 1) {
			storyTouch.setVisibility(View.VISIBLE);
		}

		// pre-populate sense buttons based on user's prior encounter
		if(encounter.getHear() == 1) {
			_btnHear.setBackgroundResource(R.drawable.btn_sense_bg_true);
		}
		if(encounter.getSee() == 1) {
			_btnSee.setBackgroundResource(R.drawable.btn_sense_bg_true);
		}
		if(encounter.getSmell() == 1) {
			_btnSmell.setBackgroundResource(R.drawable.btn_sense_bg_true);
		}
		if(encounter.getTaste() == 1) {
			_btnTaste.setBackgroundResource(R.drawable.btn_sense_bg_true);
		}
		if(encounter.getTouch() == 1) {
			_btnTouch.setBackgroundResource(R.drawable.btn_sense_bg_true);
		}

		// user input
		List<ImageButton> imageButtons = new ArrayList<ImageButton>();
		imageButtons.add(_btnHear);
		imageButtons.add(_btnSee);
		imageButtons.add(_btnSmell);
		imageButtons.add(_btnTaste);
		imageButtons.add(_btnTouch);

		// add on click listeners
		for(int i = 0; i < imageButtons.size(); i ++) {
			imageButtons.get(i).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(v == _btnHear) {
						if(encounter.getHear() == 0) {
							_btnHear.setBackgroundResource(R.drawable.btn_sense_bg_true);
							encounter.setHear(1);
						} else {
							_btnHear.setBackgroundResource(R.drawable.btn_sense_bg_false);
							encounter.setHear(0);
						}
						Log.i("SENSE ACTIVITY", "Hear btn clicked");
					} else if (v == _btnSee) {
						if(encounter.getSee() == 0) {
							_btnSee.setBackgroundResource(R.drawable.btn_sense_bg_true);
							encounter.setSee(1);
						} else {
							_btnSee.setBackgroundResource(R.drawable.btn_sense_bg_false);
							encounter.setSee(0);
						}
						Log.i("SENSE ACTIVITY", "See btn clicked");
					} else if  (v == _btnSmell) {
						if(encounter.getSmell() == 0) {
							_btnSmell.setBackgroundResource(R.drawable.btn_sense_bg_true);
							encounter.setSmell(1);
						} else {
							_btnSmell.setBackgroundResource(R.drawable.btn_sense_bg_false);
							encounter.setSmell(0);
						}
						Log.i("SENSE ACTIVITY", "Smell btn clicked");
					} else if  (v == _btnTaste) {
						if(encounter.getTaste() == 0) {
							_btnTaste.setBackgroundResource(R.drawable.btn_sense_bg_true);
							encounter.setTaste(1);
						} else {
							_btnTaste.setBackgroundResource(R.drawable.btn_sense_bg_false);
							encounter.setTaste(0);
						}
						Log.i("SENSE ACTIVITY", "Taste btn clicked");
					} else if (v == _btnTouch) {
						if(encounter.getTouch() == 0) {
							_btnTouch.setBackgroundResource(R.drawable.btn_sense_bg_true);
							encounter.setTouch(1);
						} else {
							_btnTouch.setBackgroundResource(R.drawable.btn_sense_bg_false);
							encounter.setTouch(0);
						}
						Log.i("SENSE ACTIVITY", "Touch btn clicked");
					}

				}
			});

		}

		// open the DB, cuz you're making calls to to retrieve
		// media connected to the Story object
		dbh.openDataBase();
		// populate the viewStub based on the mediaType
		switch(mediaType) { 
		// AUDIO
		case (Global.AUDIO_CAPTURE):
			View audioStub = ((ViewStub) v.findViewById(R.id.audio_stub)).inflate();
			LinearLayout audioView = (LinearLayout) v.findViewById(R.id.inflated_audio);
			
			final ImageButton btnPlay = (ImageButton) v.findViewById(R.id.btn_play);
			btnPlay.setImageDrawable((getResources().getDrawable(R.drawable.ic_media_pause)));
			
			//ImageButton btnPause = (ImageButton) v.findViewById(R.id.btn_pause);
			//progress = (SeekBar) v.findViewById(R.id.progress);

			// get audio from database
			audio = dbh.getStoryAudio(story.getId());
			
			final String audioPath = audio.getUri();
			//Log.v("AUDIO PATH", audioPath);
			
			// create a media player to play back audio file
			mp = new MediaPlayer();
			mp.setOnPreparedListener(this);
			
			// create a media Controller 
			// mc = new MediaController(getActivity());
			

		try {
			mp.setDataSource(audioPath);
			mp.prepare();
			mp.start();
			//mc.setMediaPlayer((MediaPlayerControl) mp);
			//mc.show();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		btnPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(audioPath != null) {
					if (pausing) {
						pausing = false;
						mp.start();
						btnPlay.setImageDrawable((getResources().getDrawable(R.drawable.ic_media_pause)));
					} else {
						pausing = true;
						mp.stop();
						btnPlay.setImageDrawable((getResources().getDrawable(R.drawable.ic_media_play)));
					}
				}
			}
		});
		

		break;

		case (Global.IMAGE_CAPTURE):
			// inflate the image view stub
			View imageStub = ((ViewStub) v.findViewById(R.id.image_stub)).inflate();

		// get image from DB
		image = dbh.getStoryImage(story.getId());
		String imagePath = image.getUri();
		String imageCaption = image.getCaption();
		Log.d("IMAGE VIEWER", "image path: " + imagePath);

		// load image
		ImageView frame = (ImageView) v.findViewById(R.id.media_image);
		SDImageLoader loader = new SDImageLoader();
		loader.load(image.getUri(), frame);

		//load caption
		TextView caption = (TextView) v.findViewById(R.id.view_caption);
		Log.d("CAPTION", "image caption is : " + imageCaption);
		caption.setText(imageCaption);

		break;

		case (Global.VIDEO_CAPTURE):

			// inflate the video view stub
			View videoStub = ((ViewStub) v.findViewById(R.id.video_stub)).inflate();

		//populate the story object from DB call
		video = dbh.getStoryVideo(story.getId());
		String path = video.getUri();
		Log.d("VIDEO PLAYER", "video path: " + path);

		// set up the video view and media controller
		VideoView videoView = (VideoView) v.findViewById(R.id.video_view);
		videoView.setMediaController(new MediaController(getActivity()));

		// set path and start playing
		videoView.setVideoPath(path);
		videoView.start();
		videoView.requestFocus();

		break;
		}

		// close the db
		dbh.close();

		// return the entire view
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}

	/**
	 * save the Encounter to the DB when the activity is Paused
	 */
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// write encounter to the database
		dbh.openDataBase();
		if(usersFirstEncounter) {
			// write new
			dbh.createEncounter(encounter);
			Log.v("DESTROY MEDIA FRAGMENT", "writing user's FIRST encounter to DB");
		} else {
			// update
			dbh.updateEncounter(encounter);
			Log.v("DESTROY MEDIA FRAGMENT", "writing user's SUBSEQUENT encounter to DB");
		}
		dbh.close();
	}
	
	/**
	 * Assembles a String with the Time and Author name 
	 * pulled from the Story object
	 */
	private String setTimestampAndAuthor() {

		dbh.openDataBase();
		String author = dbh.getUsernameOfAuthor(story.getUser());
		dbh.close();

		String timeAgo = formatInterval(story.getTimestamp());
		String text = timeAgo + " by " + author;

		return text;

	}

	/**
	 * Assembles a String with the Geographic coordinates
	 * pulled from the Story object
	 * @return
	 */
	private String setGeo() {
		String lat = Double.toString(story.getLat());
		String lng = Double.toString(story.getLng());
		String text = "(" + lat + ", " + lng + ")";
		return text;
	}

	/**
	 * format time interval to display in info window
	 * @param timeStamp
	 * @return
	 */
	public String formatInterval(String timeStamp) {

		DateHandler myDateHandler = new DateHandler();
		String thisInterval;

		if(timeStamp != null) {
			// handle String formation based on authored date
			int interval = myDateHandler.getDaysAgo(timeStamp);

			switch (interval) {
			case 0:
				thisInterval = "today";
				break;
			case 1:
				thisInterval = Integer.toString(interval) + " day ago";
				break;
			default:
				thisInterval = Integer.toString(interval) + " days ago";
				break;
			}
		} else {
			thisInterval = "";
			Toast.makeText(getActivity(), "timeStamp is null", Toast.LENGTH_SHORT).show();
		}

		// return String value to populate TextView in InfoWindow
		return thisInterval;

	}

	/* @Override
	public void run() {

		// TODO Auto-generated method stub
		int currentPosition = 0;
		if(mediaType == Global.AUDIO_CAPTURE) {
			int total = mp.getDuration();
			Log.d("MEDIA PLAYER", "total duration is: " + Integer.toString(total));
			progress.setMax(total);
			while (mp != null && currentPosition < total) {
				try {
					Thread.sleep(1000);
					currentPosition = mp.getCurrentPosition();
				} catch (InterruptedException e) {
					return;
				} catch (Exception e) {
					return;
				}
				progress.setProgress(currentPosition);
			}
		}

	}
	 */


	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(mediaType == Global.AUDIO_CAPTURE) {
			mp.stop();
			mp.release();
		}
	}

	//	
//	@Override
//	  public boolean onTouchEvent(MotionEvent event) {
//	    //the MediaController will hide after 3 seconds - tap the screen to make it appear again
//	    mc.show();
//	    return false;
//	  }
	

	@Override
	public boolean canPause() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canSeekBackward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return mp.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		return mp.getDuration();
	}

	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return mp.isPlaying();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		mp.pause();
	}

	@Override
	public void seekTo(int pos) {
		// TODO Auto-generated method stub
		mp.seekTo(pos);

	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		mp.start();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}



}
