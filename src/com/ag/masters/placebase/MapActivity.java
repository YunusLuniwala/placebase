package com.ag.masters.placebase;

import java.lang.reflect.Method;
import java.util.List;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.ag.masters.placebase.handlers.DateHandler;
import com.ag.masters.placebase.handlers.SDImageLoader;
import com.ag.masters.placebase.model.Global;
import com.ag.masters.placebase.sqlite.DatabaseHelper;
import com.ag.masters.placebase.sqlite.Story;
import com.ag.masters.placebase.sqlite.StoryAudio;
import com.ag.masters.placebase.sqlite.StoryImage;
import com.ag.masters.placebase.sqlite.StoryVideo;
import com.ag.masters.placebase.sqlite.User;
import com.ag.masters.placebase.view.LogoutDialogFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapActivity extends FragmentActivity 
implements OnMarkerClickListener, OnInfoWindowClickListener, OnMapClickListener, LocationSource, LocationListener, SensorEventListener {

	public static final int ZOOM_LEVEL = 16;

	private static final int TARGET_BEARING = 20;
	private static final int TARGET_RANGE = 5;

	private static final String MAP_FRAGMENT_TAG = "map";
	private RelativeLayout mapHolder;
	private SupportMapFragment mMapFragment;
	private GoogleMap mMap;	
	
	private DatabaseHelper dbh;

	private List<Story> allStories;

	private User user;

	// journey mode 
	private static int journeyMode = -1;
	private Location myCurrentLocation = null;	
	private Marker myMarker = null; 			// device marker
	private Canvas myMarkerCanvas;
	// target information
	private Marker targetMarker = null;			// story marker
	private static Location targetLocation;
	private static float bearingToTarget = 0;
	private static float bearingToPerspective = 0; 
	private static float distanceToTarget= 0;
	// the story behind it 
	private Story targetStory;
	
	private CircleOptions myCircleOptions;
	private Circle circle;

	// print out test variables to screen
	private TextView testJourneyMode;
	private TextView testTargetBearing;
	private TextView testBearingToPerspective;
	private TextView testGeoX;
	private TextView testGeoY;
	private TextView testGeoZ;
	private TextView testAccelX;
	private TextView testAccelY;
	private TextView testAccelZ;
	private TextView testAzimuth;

	// device location services (GPS)
	private LocationManager myLocationManager;
	private OnLocationChangedListener myLocationListener;
	private Criteria myCriteria;

	// device dimensions
	private int screenHeight;
	private int screenWidth;

	// Map Camera CancellableCalbacks for animations 
	private CancelableCallback enableAnimation; 

	// device sensor (accelerometer and magnetic field)
	private SensorManager mySensorManager;
	float[] inR = new float[16];
	float[] I = new float[16];
	float[] gravity = new float[3];
	float[] geomag = new float[3];
	float[] orientVals = new float[3];

	/*
	 * time smoothing constant for low-pass filter
	 * 0 ² x ² 1 ; a smaller value basically means more smoothing
	 * See: http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
	 */
	static final float ALPHA = 0.2f;
	protected float[] accelVals;
	protected float[] geomagVals;
	protected float[] lowPassOrientVals;
	double azimuth = 0;
	float bearing = 0; // normalized whole number for raw sensor azimuth input

	// helper classes
	private static DateHandler myDateHandler;
	private static SDImageLoader imageLoader;

	private boolean rotateView = true;
	private boolean firstFix = true;
	private static CameraPosition MYLOCATION;

	// Media Record Buttons
	private ImageButton btnRecordMedia;
	private LinearLayout layoutRecordMedia;
	private boolean isRecordOptionsShowing;
	private boolean isInfoWindowShowing;
	
	// journey Block Views
	LinearLayout journeyBlock;
	Button btnGetMessage;
	ImageButton btnCloseJourneyPanel;
	TextView journeyBearing;
	TextView journeyDistance;
	ImageView compass;
	ImageView alignmentIcon;
	ImageView photo;


	// hard reference to saved URIs to use onActivityResult
	private Uri mCaptureImageUri;
	private Uri mCaptureVideoUri;




	//------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_map);

		dbh = new DatabaseHelper(this);

		imageLoader = new SDImageLoader();

		// get user as SharedPreference
		SharedPreferences settings = getSharedPreferences(Global.PREFS, 0);
		int tempUser = settings.getInt("user", -1);
		
		if (tempUser != -1) {
			// get the user from the database 
			user = dbh.getUser(tempUser);
			Log.v("SHARED PREFS", "current User is: " + user.getName());
			
		} else {
			Log.v("SHARED PREFS", "user was not set");
		}
		
		Bundle data = getIntent().getExtras();
		if (data != null) {

			boolean returnFromStory = data.getBoolean("returnFromStory");
			if (returnFromStory) {
				Log.v(getClass().getSimpleName(), "User returned from authoring story");
				//TODO: something to the marker in the db so the user knows which one is hers
			}
			
			// you've returned from a journey, so hide the block
			int tempJourneyMode = data.getInt("journeyMode");
			if (tempJourneyMode != 0) {
				return;			
			} else {
				journeyMode = tempJourneyMode;
			}
		}

		// set up views
		setUpRecordingBlock();
		setUpJourneyBlock();
		
		
		// define a callback for animateCamera
		// when we have finished an animation, resume rotation
		enableAnimation = new CancelableCallback() {
			@Override
			public void onFinish() {
				rotateView = true;				
			}
			@Override
			public void onCancel() {
				rotateView = true;
			}
		};

		// UI buttons
		addCustomMyLocationButton();

		calculateScreenDimensions();
		myDateHandler = new DateHandler();

		// testPrintouts. Delete when done
		testJourneyMode = (TextView) findViewById(R.id.testJourneyMode); 
		testTargetBearing = (TextView) findViewById(R.id.testTargetBearing);
		testBearingToPerspective = (TextView) findViewById(R.id.journey_perspective);
		testGeoX = (TextView) findViewById(R.id.geoX);
		testGeoY = (TextView) findViewById(R.id.geoY);
		testGeoZ = (TextView) findViewById(R.id.geoZ);
		testAccelX = (TextView) findViewById(R.id.accelX);
		testAccelY = (TextView) findViewById(R.id.accelY);
		testAccelZ = (TextView) findViewById(R.id.accelZ);
		testAzimuth = (TextView) findViewById(R.id.testAzimuth);

		testJourneyMode.setText("0");
		testGeoX.setText("0.00");
		testGeoY.setText("0.00");
		testGeoZ.setText("0.00");
		testAccelX.setText("0.00");
		testAccelY.setText("0.00");
		testAccelZ.setText("0.00");
		testAzimuth.setText("0.00");

		myCriteria = new Criteria();
		myCriteria.setAccuracy(Criteria.ACCURACY_FINE);

		mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		addMapFragment();
		
		
		
        setUpMapIfNeeded();
        
		// initialize the state of media recording
		isRecordOptionsShowing = false;
		// the Info Window is not showing to start
		isInfoWindowShowing = false;

		// define views in journey block
		//journeyBlock = (LinearLayout) findViewById(R.id.journeyblock);
		journeyBearing = (TextView) findViewById(R.id.journey_bearing); // change to an ImageView of the Bearing Icon
		journeyDistance = (TextView) findViewById(R.id.journey_distance);
		btnGetMessage = (Button) findViewById(R.id.journey_claim);
		btnCloseJourneyPanel = (ImageButton) findViewById(R.id.closejourneyblock);
		alignmentIcon = (ImageView) findViewById(R.id.ic_alignment);
		compass = (ImageView) findViewById(R.id.ic_compass);
		photo = (ImageView) findViewById(R.id.ic_info_media);

		// TODO: not the best... a swipe gesture would be better here
		btnCloseJourneyPanel.setLongClickable(true); 
		// set up click listener on close button for journey panel
		btnCloseJourneyPanel.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				journeyMode = 0;
				updateJourneyMode();
			}
		});
		
		// check that we are not in journey mode,
		// and hide the journey block if we are not.
		if(journeyMode != 1) {
			updateJourneyMode();
		}

		// define media buttons and layouts as globals
		//layoutRecordMedia2 = (FrameLayout) findViewById(R.id.recordBtnLayout2);
		btnRecordMedia = (ImageButton) findViewById(R.id.btnRecordMain);
		// http://stackoverflow.com/questions/4969689/android-animation-xml-issues
		btnRecordMedia.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// the recording options are showing. we need to hide them
				if(isRecordOptionsShowing) {
					slideDown((ViewGroup)layoutRecordMedia);
					isRecordOptionsShowing = false;
				}
				else { // the recording options are hidden we need to show them
					slideUp((ViewGroup)layoutRecordMedia);
					isRecordOptionsShowing = true;
				}
			}

		});

		btnGetMessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent retrieveMedia = new Intent(MapActivity.this, RetrieveMedia.class);
				retrieveMedia.putExtra("user", user);
				retrieveMedia.putExtra("story", getStoryFromMarker(targetMarker));
				startActivity(retrieveMedia);
			}
		});

		// set onClickListener for Record buttons
		ImageButton btnVideo = (ImageButton) findViewById(R.id.btnVideo);
		ImageButton btnAudio = (ImageButton) findViewById(R.id.btnAudio);
		ImageButton btnPhoto = (ImageButton) findViewById(R.id.btnPhoto);
		btnVideo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startCaptureVideo();
			}
		});

		btnAudio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startCaptureAudio();
			}
		});

		btnPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startCaptureImage();
			}
		});
		
		// create circle to indicate journey marker
		// https://developers.google.com/maps/documentation/android/reference/com/google/android/gms/maps/model/CircleOptions
		myCircleOptions = new CircleOptions();
		myCircleOptions.fillColor(0x7733b5e5);
		myCircleOptions.radius(5);
		myCircleOptions.strokeWidth(0);
	}


	@Override
	protected void onResume() {

		super.onResume();
		setUpMapIfNeeded();

		myLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		//Register for location updates using a Criteria, and a callback on the specified looper thread.
		myLocationManager.requestLocationUpdates(
				4000L, 		// minTime
				1.0f,		// minDistance
				myCriteria,	// criteria
				this, 		// listener
				null);		// intent

		mMap.setMyLocationEnabled(true);
		// replaces the location source of the my-location layer
		mMap.setLocationSource(this);

		// register sensor listeners
		mySensorManager.registerListener(this, 
				mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
				SensorManager
				.SENSOR_DELAY_NORMAL);
		mySensorManager.registerListener(this, 
				mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 
				SensorManager
				.SENSOR_DELAY_NORMAL);


	}

	@Override
	public void onPause() {
		super.onPause();
		/* Disable the my-location layer (this causes our LocationSource to be automatically deactivated.) */
		mySensorManager.unregisterListener(this);

		myLocationManager.removeUpdates(this);
		mMap.setMyLocationEnabled(false);
		mMap.setLocationSource(null);
		
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbh.close();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		// if you press back while in journey mode, then just exit out of journey mode
		if(journeyMode == 1) {
			journeyMode = 0;
			updateJourneyMode();
		} else {
			super.onBackPressed();
		}
	}

	/** 
	 * Inflate Journey Block viewStub
	 * view_journey_block.xml
	 * 
	 */
	private void setUpJourneyBlock() {
		// inflate journey block with place-based information
		ViewStub journeyStub = (ViewStub) findViewById(R.id.stub_journey);
		journeyBlock = (LinearLayout) journeyStub.inflate();

	}
	
	/** 
	 * Inflate Recording viewStub
	 * view_record_block.xml
	 * 
	 */
	private void setUpRecordingBlock() {
		// inflate view stub for record buttons
		ViewStub recordStub = (ViewStub) findViewById(R.id.stub_record);
		layoutRecordMedia = (LinearLayout) recordStub.inflate();
		layoutRecordMedia.setVisibility(View.INVISIBLE);
		// load content into TextView from a file
		// http://android-er.blogspot.com/2010/07/display-text-file-in-resraw_01.html
		TextView recordingTips = (TextView)findViewById(R.id.recording_tips);
		recordingTips.setText(Global.readTxt(R.raw.tips, this));
	}
	
	/**
	 * use built-in camera to record photos
	 * save files to root
	 */
	private void startCaptureImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(isAvailable(getApplicationContext(), intent)) {

			// Hiyo, holy shitballs. what a pain this was
			// http://stackoverflow.com/questions/4184951/get-path-of-image-from-action-image-capture-intent

			// define the filename
			String filename = Environment.getExternalStorageDirectory().getAbsolutePath() 
					+ String.valueOf(System.currentTimeMillis()) 
					+ ".jpg";

			Log.i("IMAGE INTENT" , filename);

			ContentValues values = new ContentValues();
			values.put(MediaStore.Images.Media.TITLE, filename);
			mCaptureImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

			// must specify the URI where the image will be stored
			// as a global variable to access in onActivityResult
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mCaptureImageUri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivityForResult(intent, Global.IMAGE_CAPTURE); // can add options as a bundle
		}
	}

	/**
	 * use built-in audio recorder
	 * saves files to root
	 * 
	 * see tut: http://www.grokkingandroid.com/recording-audio-using-androids-mediarecorder-framework/
	 */
	private void startCaptureAudio() {
		Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
		if(isAvailable(getApplicationContext(), intent)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivityForResult(intent, Global.AUDIO_CAPTURE);
		}
	}

	/**
	 * use built-in video camera
	 * save files to root
	 */
	private void startCaptureVideo() {
		//http://stackoverflow.com/questions/4990390/android-mediastore-action-video-capture-external-path
		Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
		if(isAvailable(getApplicationContext(), intent)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivityForResult(intent, Global.VIDEO_CAPTURE);
		}
	}


	/**
	 * Handle Results from Recording Activities
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);


		// only start the new activity if the recording was not cancelled
		if (resultCode == RESULT_OK)  {

			// create new story object
			Story story = new Story();
			// create a new media object
			StoryImage image = new StoryImage();
			// create a new Video object
			StoryVideo video = new StoryVideo();
			// create a new media object
			StoryAudio audio = new StoryAudio();

			// create new Intent for video, audio (not photo)
			// so we can add parcels to it.
			Intent startSenses = new Intent(MapActivity.this, PerspectiveActivity.class);

			// also check to see that the request code is OK
			switch(requestCode) {
			case Global.IMAGE_CAPTURE:
				if (resultCode == RESULT_OK) {

					// get filename from the column where we 
					// specified it would be when we defined the intent (startCaptureImage())
					String[] projection = {MediaStore.Images.Media.DATA};
					Cursor cursor = getContentResolver().query(mCaptureImageUri, projection, null, null, null);
					int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					String capturedImageFilePath = cursor.getString(index);
					cursor.close();
					Log.i("IMAGE-CAPTURE", capturedImageFilePath);

					// and store the filepath in StoryImage object
					image.setUri(capturedImageFilePath);

				} else if (resultCode == RESULT_CANCELED){
					Log.i("IMAGE-CAPTURE", "image capture canceled");
				} 

				Intent startCaption = new Intent(MapActivity.this, Caption.class);
				story.setUser(user.getId()); 
				story.setMedia(Global.IMAGE_CAPTURE);
				// pass in StoryImage parcel
				startCaption.putExtra("image", image);
				// pass in Story parcel
				startCaption.putExtra("story", story);
				//startCaption.putExtra("user", user);
				// start SensesActivity
				startCaption.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(startCaption);

				break;

			case Global.VIDEO_CAPTURE:
				if (resultCode == RESULT_OK) {

					Uri videoUri = data.getData();

					String[] projection = {MediaStore.Video.Media.DATA};
					Cursor cursor = getContentResolver().query(videoUri, projection, null, null,null);
					int index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
					cursor.moveToFirst();
					String capturedVideoFilePath = cursor.getString(index);
					cursor.close();
					Log.i("VIDEO-CAPTURE", capturedVideoFilePath);


					// and store the filepath in the StoryVideo object
					video.setUri(capturedVideoFilePath);	

				} else if (resultCode == RESULT_CANCELED){
					Log.i("VIDEO-CAPTURE", "video recording canceled");
				} 

				story.setUser(user.getId());
				story.setMedia(Global.VIDEO_CAPTURE);

				// pass in StoryVideo parcel
				startSenses.putExtra("media", video);
				// pass in Story parcel
				startSenses.putExtra("story", story);
				// start SensesActivity
				//startSenses.putExtra("user", user);
				startSenses.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(startSenses);

				break;

			case Global.AUDIO_CAPTURE:
				if (resultCode == RESULT_OK) {

					// get the URI from the data returned
					Uri audioUri = data.getData();

					String[] projection = {MediaStore.Audio.AudioColumns.DATA};
					Cursor cursor = getContentResolver().query(audioUri, projection, null, null, null);
					int index = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA);
					cursor.moveToFirst();
					// read String from the database column
					String audioFilePath = cursor.getString(index);
					cursor.close();

					// store the filepath in the StoryAudio object
					audio.setUri(audioFilePath);

					Log.i("AUDIO-CAPTURE", audioFilePath);

					story.setUser(user.getId());
					story.setMedia(Global.AUDIO_CAPTURE);

					// pass in StoryAudio parcel
					startSenses.putExtra("media", audio);
					// pass in Story parcel
					startSenses.putExtra("story", story);
					//startSenses.putExtra("user", user);
					// start SensesActivity
					startSenses.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivity(startSenses);

					break;
				} else if (resultCode == RESULT_CANCELED){
					Log.i("AUDIO-CAPTURE", "audio recording canceled");
				}
			}



		}
	}


	/**
	 * Check that an intent is available before using it.
	 * @param context
	 * @param intent
	 * @return true, if there is an intent available to handle the request.
	 * 
	 * see tut: http://www.grokkingandroid.com/checking-intent-availability/
	 */
	public static boolean isAvailable(Context c, Intent i) {
		final PackageManager mgr = c.getPackageManager();
		List<ResolveInfo> list = mgr.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}


	//------------------------------------------------------------------------------------------
	private void addCustomMyLocationButton() {
		//http://stackoverflow.com/questions/14826345/android-maps-api-v2-change-mylocation-icon
		ImageButton btnShowMyLocation = (ImageButton) findViewById(R.id.btnShowMyLocation);

		btnShowMyLocation.setOnClickListener(new OnClickListener() { 

			@Override
			public void onClick(final View v) {
				// stop rotating for a second.
				rotateView = false;

				if(myCurrentLocation != null) {
					mMap.animateCamera(
							CameraUpdateFactory.newLatLngZoom(
									new LatLng(
											myCurrentLocation.getLatitude(), 
											myCurrentLocation.getLongitude()), 
											ZOOM_LEVEL), new CancelableCallback() {
								@Override
								public void onFinish() {
									rotateView = true;				
								}
								@Override
								public void onCancel() {
									rotateView = true;
								}
							});

				}
			}
		});
	}



	private void addMapFragment() {
		// It isn't possible to set a fragment's id programmatically so we set a tag instead and
        // search for it using that.
		mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentByTag(MAP_FRAGMENT_TAG);
		// We only create a fragment if it doesn't already exist.
        if (mMapFragment == null) {
            // To programmatically add the map, we first create a SupportMapFragment.
            mMapFragment = SupportMapFragment.newInstance();

            // Then we add it using a FragmentTransaction.
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.mapHolder, mMapFragment, MAP_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
        
	}
	
	/**
	 * change the height of the map when in journey mode
	 * @param val
	 */
	private void displayJourneyMap(boolean val) {
		if (val == true) {
			RelativeLayout mapHolder = (RelativeLayout) findViewById(R.id.mapHolder);
			RelativeLayout.LayoutParams params = (LayoutParams) mapHolder.getLayoutParams();
			params.height = screenHeight/3 + 110;
			mapHolder.setLayoutParams(params);
			
		} else {
			RelativeLayout mapHolder = (RelativeLayout) findViewById(R.id.mapHolder);
			RelativeLayout.LayoutParams params = (LayoutParams) mapHolder.getLayoutParams();
			params.height = screenHeight;
			mapHolder.setLayoutParams(params);
		}
	}
	//------------------------------------------------------------------------------------------
	// create an marker icon at runtime to show location and type of the story
	// http://stackoverflow.com/questions/11740362/merge-two-bitmaps-in-android?rq=1
	// http://stackoverflow.com/questions/14811579/android-map-api-v2-custom-marker-with-imageview
	private Bitmap createStoryMarkerIcon(int mediaType) {

		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(100,100,Config.ARGB_8888);
			Canvas c = new Canvas(bitmap);
			Resources res = getResources();

			Bitmap mIcon = BitmapFactory.decodeResource(res, R.drawable.ic_map_marker);
			//Bitmap mCompass = BitmapFactory.decodeResource(res, R.drawable.ic_compass);
			Bitmap mMedia;
			switch(mediaType) {
			case Global.IMAGE_CAPTURE:// image
				mMedia = BitmapFactory.decodeResource(res, R.drawable.ic_record_photo);
				break;
			case Global.VIDEO_CAPTURE: // video
				mMedia = BitmapFactory.decodeResource(res, R.drawable.ic_record_video);
				break;
			case Global.AUDIO_CAPTURE: // audio
				mMedia = BitmapFactory.decodeResource(res, R.drawable.ic_record_audio);
				break;
			default:
				mMedia = null;
				break;
			}


			Drawable mIconDrawable = new BitmapDrawable(res, mIcon);
			//Drawable mCompassDrawable = new BitmapDrawable(res, mCompass);
			Drawable mMediaDrawable = new BitmapDrawable(res, mMedia);


			mIconDrawable.setBounds(0,0,100,100);
			//mCompassDrawable.setBounds(25,0,75,75);
			mMediaDrawable.setBounds(15,10,85,80);

			mIconDrawable.draw(c);
			mMediaDrawable.draw(c);
			// rotate the canvas before drawing the orientation.
			// ** may have to convert sensor values from Radians to Degrees before saving them here **
			//rotate (float degrees, float px, float py)
			//c.rotate(bearing, 50, 40);
			//mCompassDrawable.draw(c);


		} catch (Exception e) {

		}
		return bitmap;
	}
	//------------------------------------------------------------------------------------------
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
            mMap = mMapFragment.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				// The Map is verified. It is now safe to manipulate the map.
				setUpMap();
			}
		}
	}

	//------------------------------------------------------------------------------------------
	private void setUpMap() {

		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		mMap.setMyLocationEnabled(true);

		// configure map UI settings
		UiSettings mUiSettings = mMap.getUiSettings();
		mUiSettings.setZoomControlsEnabled(false);
		mUiSettings.setCompassEnabled(false);
		mUiSettings.setTiltGesturesEnabled(true);
		mUiSettings.setMyLocationButtonEnabled(false); //disable myLocation button. we roll our own

		addMarkersToMap();

		// show marker info using our custom info window
		mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		// Set listeners for marker events.  See the bottom of this class for their behavior.
		mMap.setOnMarkerClickListener(this);
		mMap.setOnInfoWindowClickListener(this);
		mMap.setOnMapClickListener(this);

	}

	//------------------------------------------------------------------------------------------	
	private void addMarkersToMap() {


		//dbh.openDataBase();	
		allStories = dbh.getAllStories();

		if(allStories != null) {
			for (int i=0; i<allStories.size(); i++) {
				Story s = allStories.get(i);

				LatLng ll = new LatLng(s.getLat(), s.getLng());
				Bitmap ic = createStoryMarkerIcon(s.getMedia());

				// now, create a marker
				Marker thisMarker = mMap.addMarker(new MarkerOptions()
				.position(ll)
				.icon(BitmapDescriptorFactory.fromBitmap(ic)));

				// and store that marker's ID into the Story instance
				// to pull up the correct information onInfoWindowClick
				String markerId = thisMarker.getId();
				allStories.get(i).setMarkerId(markerId);
			} 
		} else {
			Toast.makeText(this, "no stories in db", Toast.LENGTH_LONG).show();
		}

	}

	//------------------------------------------------------------------------------------------
	private void slideUp(ViewGroup view) {

		view.setVisibility(View.VISIBLE);
		PropertyValuesHolder makeVisible = PropertyValuesHolder.ofFloat("alpha", 0f,1f);
		PropertyValuesHolder slideUpHalfway = PropertyValuesHolder.ofFloat("translationY", screenHeight, (0));

		ObjectAnimator
		.ofPropertyValuesHolder(view, makeVisible, slideUpHalfway)
		.setDuration(400)
		.start();

		
	}
	//------------------------------------------------------------------------------------------
	private void slideDown(ViewGroup view) {

		PropertyValuesHolder makeInvisible = PropertyValuesHolder.ofFloat("alpha", 1f,0f);
		PropertyValuesHolder slideDown = PropertyValuesHolder.ofFloat("translationY", 0, screenHeight);

		ObjectAnimator
		.ofPropertyValuesHolder(view, makeInvisible, slideDown)
		.setDuration(700)
		.start();

	}

	//------------------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_map, menu);
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	//------------------------------------------------------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) { 

		switch(item.getItemId()) {

		case R.id.user_places:
			Intent intent = new Intent(this, UserPlaces.class);
			startActivity(intent);
			return true;	
		case R.id.logout:
		// show dialog.
		DialogFragment newFragment = new LogoutDialogFragment();
		newFragment.show(getFragmentManager(), "logout");
		return true;
		}

		return super.onOptionsItemSelected(item);
	}
	//------------------------------------------------------------------------------------------
	// MARKER RELATED CLICK LISTENERS
	//------------------------------------------------------------------------------------------	
	@Override
	public boolean onMarkerClick(Marker marker) {

		if(journeyMode == 1) {
			journeyMode = 0;
			updateJourneyMode();	
		}
		
		// stop rotating for a second.
		rotateView = false;
		// and set flag that the InfoWindow is Showing
		isInfoWindowShowing = true;
		
		// add a circle to indicate the current marker selected 
		myCircleOptions.center(marker.getPosition());
		if(circle != null) {
			circle.remove();
		}
		circle = mMap.addCircle(myCircleOptions);
		
		return false;
		
	}
	//------------------------------------------------------------------------------------------
	@Override
	public void onInfoWindowClick(Marker marker) {
		
		setMyLocationMarker();

		// store marker as global
		targetMarker = marker;

		// store Location in a global variable
		LatLng target = marker.getPosition();
		targetLocation = new Location("Target");
		targetLocation.setLatitude(target.latitude);
		targetLocation.setLongitude(target.longitude);

		// start rotating again
		rotateView = true;
		
		// set journeyMode to true
		journeyMode = 1;
		isInfoWindowShowing = false;
		
		// TODO: here... move the map!
		
		updateTargetStory(targetMarker);
		enterJourneyMode();
		updateJourneyMode(); // also called in onLocationChanged()

	}

	//------------------------------------------------------------------------------------------
	// MAP CLICK LISTENER
	//------------------------------------------------------------------------------------------
	@Override
	public void onMapClick(LatLng point) {
		
		Log.v("MAP", "MAP CLICKED. isInfoWindowShowing = " + Boolean.toString(isInfoWindowShowing));
		
		// exit strategy: hide the media buttons if they are showing
		if(isRecordOptionsShowing == true) {
			//Toast.makeText(getApplicationContext(), "map was clicked", Toast.LENGTH_LONG).show();
			slideDown((ViewGroup) layoutRecordMedia);
		}
		
		if(isInfoWindowShowing == true) {
			// start rotating again
			rotateView = true;
			if(circle != null) {
				circle.remove();
			}
			updateJourneyMode();
			
		}
		Log.v("MAP", "MAP CLICKED. isInfoWindowShowing = " + Boolean.toString(isInfoWindowShowing));
		
	}

	private void updateTargetStory(Marker marker) {
		getStoryFromMarker(marker);
		targetStory = getStoryFromMarker(marker);
		photo.setImageBitmap(BitmapFactory.decodeFile(targetStory.getPerspectiveUri())); 
	}

	private void enterJourneyMode() {
		// hide journey Block
		 
			// animate the view to show it
			displayJourneyMap(true);
			slideUp(journeyBlock);
			// called when we FIRST enter journey mode
			//journeyBlock.setVisibility(View.VISIBLE); // change for animation?

			targetMarker.hideInfoWindow();


			// create the bounding box that will contain both
			// the target and the device location
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			builder.include(myMarker.getPosition());
			builder.include(targetMarker.getPosition());
			LatLngBounds targetView = builder.build();
			// set boundaries so myLocation and the destination marker are both visible on the screen
			rotateView = false;
			CameraUpdate update = CameraUpdateFactory.newLatLngBounds(targetView, 200);
			mMap.animateCamera(update, enableAnimation);


		
		// set the value of the story's orientation
		testTargetBearing.setText(Float.toString(targetStory.getBearing()));
	}
	
	/** 
	 * While in journey mode
	 * 
	 * Set visibility for the panel
	 * Constantly update values for bearing and distance to target
	 * 
	 */
	private void updateJourneyMode() {

		testJourneyMode.setText(Integer.toString(journeyMode));
		// note: update journeyMode before calling this function
		
		boolean isWithinRange = false;
		boolean isWithinBearing = false;
		boolean isWithinPerspective = false;
		//btnGetMessage.setEnabled(false); // UNCOMMENT AFTER TESTING

		// EXIT JOURNEY MODE
		if (journeyMode != 1) {
			// expand map to full screen
			displayJourneyMap(false);
			// animate the view to hide it
			if(isInfoWindowShowing != true) {
				slideDown(journeyBlock);
				//journeyBlock.setVisibility(View.INVISIBLE); // change for animation?
			}
			if(circle != null) {
				circle.remove();
			}
			removeMyLocationMarker();
			boolean isInfoWindowShowing = false;
		
		// ENTER JOURNEY MODE
		} else {

			// calculate new values for bearing and distance to target
			calculateDistanceToTarget();
			calculateBearingToTarget();
			calculateBearingToPerspective();
			
			// convert the float to int for display
			// update Views with new values for bearing and distance
			journeyDistance.setText(Integer.toString((int)distanceToTarget));
			//journeyBearing.setText(Float.toString(bearingToTarget));
			
			// rotate the compass to reflect device bearing to target
			//compass.setRotation(bearingToTarget);
			compass.setRotation(bearingToPerspective);
			
			
			if (bearingToPerspective >= -(TARGET_BEARING) && bearingToPerspective <= TARGET_BEARING) {
				isWithinPerspective = true;
			}
			
			// if device bearing is within the target range
			if (bearingToTarget >= -(TARGET_BEARING) && bearingToTarget <= TARGET_BEARING) {
				isWithinBearing = true;
			}

			if (distanceToTarget <= TARGET_RANGE) {
				isWithinRange = true;
			}

			// change icon to reflect true or false alignment
			if (isWithinPerspective == true) {
				alignmentIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_align_true));
			} else {
				alignmentIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_align_false));
			}

			if(isWithinRange == true) {
				// make text green
				journeyDistance.setTextColor(Color.GREEN);				
			} else {
				journeyDistance.setTextColor(Color.BLACK);
			}

			if (isWithinRange && isWithinPerspective) {
				btnGetMessage.setEnabled(true);
			}
		}
	}

	/**
	 * Called in Journey mode,
	 * 
	 * update global targetDistance
	 * @return float, the device's distance to the target
	 */
	private float calculateDistanceToTarget() {
		if(myCurrentLocation != null) {
			distanceToTarget = myCurrentLocation.distanceTo(targetLocation);
			// round to a whole number
			distanceToTarget = Math.round(distanceToTarget);
			return distanceToTarget;

		} else return 0;
	}

	/**
	 * Called in Journey mode
	 * 
	 * update global targetBearing
	 * @tutorial http://android-er.blogspot.com/2013/02/get-bearing-between-two-location-using.html
	 * @return float, the device's bearing to the target
	 */
	private float calculateBearingToTarget() {
		if(myCurrentLocation != null) {
			bearingToTarget= myCurrentLocation.bearingTo(targetLocation); // This is insufficient. Need to use actual sensor bearing
			bearingToTarget = bearing - bearingToTarget;
			bearingToTarget = bearingToTarget >= 0 ? bearingToTarget: bearingToTarget + 360;
			// Round to a whole number
			bearingToTarget = Math.round(bearingToTarget);
			// spit out a value from -180 to 0 , and 0 - 180
			bearingToTarget = bearingToTarget >= 180 ? -(360 - bearingToTarget): bearingToTarget;

			return bearingToTarget;
		}
		else return 0;
	}

	/**
	 * 
	 * @return float, the device's bearing to the author's (storied in Story object)
	 */
	private float calculateBearingToPerspective() {
		if(myCurrentLocation != null) {
			// normalize story's perspective values so they range from 0-360
			float storyBearing = targetStory.getBearing();
			storyBearing = storyBearing >= 0 ? storyBearing: storyBearing + 360;
			// and round to a whole number
			storyBearing = Math.round(storyBearing);
			
			bearingToPerspective = bearing - storyBearing;
			// spit out a value from -180 to 0 , and 0 - 180
			bearingToPerspective = bearingToPerspective >= 180 ? -(360 - bearingToPerspective): bearingToPerspective;

			testBearingToPerspective.setText(Float.toString(bearingToPerspective));
			
			return bearingToPerspective;
			
		}
		else return 0;
	}
	
	/**
	 * update global bearing (for device) based onSensorChanged()
	 * @param azimuth
	 */
	private void setDeviceBearing(double azimuth) {

		bearing = (float) azimuth;
		if (!rotateView) {
			//bearing = 0;
		} else {
			// normalize azimuth values so they range from 0-360
			bearing = bearing >= 0 ? bearing: bearing + 360;
			// and round to a whole number
			bearing = Math.round(bearing);
		}
		
		testAzimuth.setText(Double.toString(bearing));

		if(journeyMode == 1) {
			updateJourneyMode();
		}

		if(rotateView) {
			rotateMyCamera();
		}
	}

	//------------------------------------------------------------------------------------------
	private void setMyLocationMarker() {
		// http://androiddev.orkitra.com/?p=3933	

		// remove the old one before you draw another.
		removeMyLocationMarker(); 

		// get current Location
		Location location = mMap.getMyLocation(); 

		// and create myMarker with this icon
		myMarker = mMap.addMarker(new MarkerOptions()
		.position(new LatLng(location.getLatitude(), location.getLongitude()))
		.icon(BitmapDescriptorFactory.fromBitmap(createMyMarkerIcon()))
		.anchor(.5f,.5f));
	}
	//------------------------------------------------------------------------------------------
	private Bitmap createMyMarkerIcon() {
		Bitmap bitmap = null;
		int size = 50;

		try {
			bitmap = Bitmap.createBitmap(size,size,Config.ARGB_8888);
			myMarkerCanvas = new Canvas(bitmap);

			Bitmap mIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_compass);
			Drawable mIconDrawable = new BitmapDrawable(getResources(), mIcon);
			mIconDrawable.setBounds(0,0,size,size);

			//myMarkerCanvas.rotate(targetBearing, size/2, size/2);

			mIconDrawable.draw(myMarkerCanvas);

		} catch (Exception e) {

		}
		return bitmap;
	}
	//------------------------------------------------------------------------------------------
	private void removeMyLocationMarker() {
		if(myMarker != null) {
			// remove the old one before you draw another.
			myMarker.remove(); 
		}
	}
	//------------------------------------------------------------------------------------------
	private void rotateMyCamera() {

		if(mMap.isMyLocationEnabled() && mMap.getMyLocation() != null) {
			if(rotateView && !firstFix) {

				CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(mMap.getCameraPosition().target)      
				.zoom(mMap.getCameraPosition().zoom)               
				.bearing(bearing)                
				.tilt(mMap.getCameraPosition().tilt)                   
				.build();               

				mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 100, null);
			}
		}
	}

	//------------------------------------------------------------------------------------------
	@SuppressLint("NewApi")
	private void calculateScreenDimensions() {
		// find the width and height of the screen
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		try {
			display.getSize(size);
			screenHeight = size.y;
			screenWidth = size.x;
		} catch (NoSuchMethodError e) {
			screenHeight = display.getHeight();
			screenWidth = display.getWidth();
		}
	}

	private Story getStoryFromMarker(Marker marker) {
		// "m0" format 
		
		// We can reliably query the array of story objects based
		// on the key and id being the same (they are added in the order they are read in).
		// No need to store a markerId with the story object
		String markerId = marker.getId();
		String markerNumId = markerId.substring(1);
		Log.v("MARKER", "marker id " + markerNumId);
		Log.v("NUMSTORIES", "num stories " + allStories.size());
		int markerIntId = Integer.parseInt(markerNumId);
		// grab the story object associated with this marker
		Story story = allStories.get(markerIntId);
		return story;
	}
	//------------------------------------------------------------------------------------------
	// LOCATION LISTENER
	// http://android-er.blogspot.com/2013/01/implement-locationsource-and.html
	//------------------------------------------------------------------------------------------
	@Override
	public void onLocationChanged(Location location) {
		
		Log.v("LOCATION", "onLocationChanged: ");
		
		if (myLocationListener != null) {
			myLocationListener.onLocationChanged(location);

			// save device location to global variable.
			myCurrentLocation = mMap.getMyLocation();

			if(firstFix) { // we have gotten the first fix on myLocation
				// set the MyLocation button position to a global variable
				MYLOCATION = new CameraPosition.Builder()
				.target(new LatLng(
						myCurrentLocation.getLatitude(), 
						myCurrentLocation.getLongitude()))
						.zoom(ZOOM_LEVEL)
						.bearing(mMap.getCameraPosition().bearing)
						.tilt(0)
						.build();

				//animate the camera to this place
				/* mMap.animateCamera(CameraUpdateFactory.newCameraPosition(MYLOCATION), new CancelableCallback() {
					@Override
					public void onFinish() {
						rotateView = true;
						// set firstFix to false so it runs only once
						firstFix = false;
					}
					@Override
					public void onCancel() {
						rotateView = true;
						// set firstFix to false so it runs only once
						firstFix = false;
					}
				});		
				 */
				mMap.moveCamera(CameraUpdateFactory.newCameraPosition(MYLOCATION));
				rotateView = true;
				firstFix = false;
			}

			
			Log.v("LOCATION", "My lat: " + myCurrentLocation.getLatitude() + "\n" +"My lo n: " + myCurrentLocation.getLongitude());
			
			
			// if we are journeying. 
			// update myMarker's location
			if (journeyMode == 1) {
				updateJourneyMode();
				setMyLocationMarker();
			}

		}


	}


	//------------------------------------------------------------------------------------------
	@Override
	public void onProviderDisabled(String provider) {
	}
	//------------------------------------------------------------------------------------------
	@Override
	public void onProviderEnabled(String provider) {
	}
	//------------------------------------------------------------------------------------------
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	//------------------------------------------------------------------------------------------
	// LOCATION SOURCE
	//------------------------------------------------------------------------------------------
	@Override
	public void activate(OnLocationChangedListener listener) {
		myLocationListener = listener;
	}
	//------------------------------------------------------------------------------------------
	@Override
	public void deactivate() {
		myLocationListener = null;
	}

	//------------------------------------------------------------------------------------------
	// SENSOR EVENT LISTENER
	//------------------------------------------------------------------------------------------	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}
	//------------------------------------------------------------------------------------------	
	@Override
	public void onSensorChanged(SensorEvent event) {

		// http://stackoverflow.com/questions/4819626/android-phone-orientation-overview-including-compass 
		// http://stackoverflow.com/questions/4020048/finding-orientation-using-getrotationmatrix-and-getorientation?rq=1
		// If the sensor data is unreliable return
		//if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
		//return;

		// Gets the value of the sensor that has been changed
		switch (event.sensor.getType()) {  
		case Sensor.TYPE_ACCELEROMETER:
			accelVals = lowPass(event.values.clone(), accelVals);

			//gravity = event.values.clone();
			gravity = accelVals;

			//test
			testAccelX.setText(Float.toString(gravity[0]));
			testAccelY.setText(Float.toString(gravity[1]));
			testAccelZ.setText(Float.toString(gravity[2]));
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			
			geomagVals = lowPass(event.values.clone(), geomagVals);
			
			//geomag = event.values.clone();
			geomag = geomagVals;
			
			// test
			testGeoX.setText(Float.toString(geomag[0]));
			testGeoY.setText(Float.toString(geomag[1]));
			testGeoZ.setText(Float.toString(geomag[2]));
			break;
		}

		// If gravity and geomag have values then find rotation matrix
		if (gravity != null && geomag != null) {

			// checks that the rotation matrix is found
			boolean success = SensorManager.getRotationMatrix(inR, I, gravity, geomag);
			if (success) {
				SensorManager.getOrientation(inR, orientVals);
				lowPassOrientVals = lowPass(orientVals, lowPassOrientVals);
				azimuth = Math.toDegrees(lowPassOrientVals[0]);
				
				//azimuth = Math.toDegrees(orientVals[0]);
				//pitch = Math.toDegrees(orientVals[1]);
				//roll = Math.toDegrees(orientVals[2]);
				
				// compensate for different screen orientations
				Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
				int compensation = display.getRotation() * 90;                          
				azimuth = azimuth+compensation;

				//testAzimuth.setText(Double.toString(azimuth));
				
				setDeviceBearing(azimuth);

			}
		}

	}	

	/**
	 * @see http://en.wikipedia.org/wiki/Low-pass_filter#Algorithmic_implementation
	 * @see http://developer.android.com/reference/android/hardware/Sensor.html#TYPE_ACCELEROMETER
	 */
	protected float[] lowPass( float[] input, float[] output ) {
		if ( output == null )  {
			return input;
		}

		for ( int i=0; i<input.length; i++ ) {
			output[i] = output[i] + ALPHA * (input[i] - output[i]);
		}
		return output;
	}

	//------------------------------------------------------------------------------------------
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
	//------------------------------------------------------------------------------------------
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	public static Animation expand(final View v, final boolean expand) {
	    try {
	        Method m = v.getClass().getDeclaredMethod("onMeasure", int.class, int.class);
	        m.setAccessible(true);
	        m.invoke(
	            v,
	            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
	            MeasureSpec.makeMeasureSpec(((View)v.getParent()).getMeasuredWidth(), MeasureSpec.AT_MOST)
	        );
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    // get initial height of the view
	    final int initialHeight = v.getMeasuredHeight();
	    
	    if (expand) {
	    	v.getLayoutParams().height = 0;
	    }
	    else {
	    	v.getLayoutParams().height = initialHeight;
	    }
	    v.setVisibility(View.VISIBLE);
	    
	    Animation a = new Animation() {
	    	@Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) {
	            int newHeight = 0;
	            if (expand) {
	            	newHeight = (int) (initialHeight * interpolatedTime);
	            } else {
	            	newHeight = (int) (initialHeight * (1 - interpolatedTime));
	            }
	            v.getLayoutParams().height = newHeight;	            
	            v.requestLayout();
	            
	            if (interpolatedTime == 1 && !expand)
	            	v.setVisibility(View.GONE);
	        }

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };
	    a.setDuration(4000);
	    return a;
	}

	/**
	 * CustomInfoWindowAdapter
	 * 
	 * customizes the look of marker infoWindow
	 */
	class CustomInfoWindowAdapter implements InfoWindowAdapter {

		private final View mWindow;

		/**
		 * Constructor
		 */
		CustomInfoWindowAdapter() {
			// the xml layout file
			mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
		}

		/**
		 * getInfoWindow
		 * 
		 * called for every Marker
		 * 
		 * Use the equals() method on a Marker to check for equals.  Do not use ==.
		 * 
		 * @return View
		 */
		@Override
		public View getInfoWindow(Marker marker) { 

			Story thisStory = getStoryFromMarker(marker);

			// assign views
			ImageView perspective = (ImageView) mWindow.findViewById(R.id.ic_perspective);	// perspective image
			TextView daysAgo = (TextView) mWindow.findViewById(R.id.daysago); 				// days ago				 
			ImageView hear 	= (ImageView) mWindow.findViewById(R.id.ic_sense_hear);			// background set to: R.drawable.btn_sense_bg_false		
			ImageView see 	= (ImageView) mWindow.findViewById(R.id.ic_sense_see);
			ImageView smell = (ImageView) mWindow.findViewById(R.id.ic_sense_smell);
			ImageView taste = (ImageView) mWindow.findViewById(R.id.ic_sense_taste);
			ImageView touch = (ImageView) mWindow.findViewById(R.id.ic_sense_touch);
			ImageView media = (ImageView) mWindow.findViewById(R.id.ic_info_media);

			if (thisStory != null ) {

				switch (thisStory.getMedia()) {
				case Global.IMAGE_CAPTURE:
					media.setImageResource(R.drawable.ic_record_photo);
					break;
				case Global.VIDEO_CAPTURE:
					media.setImageResource(R.drawable.ic_record_video);
					break;
				case Global.AUDIO_CAPTURE:
					media.setImageResource(R.drawable.ic_record_audio);
					break;
				}


				daysAgo.setText(formatInterval(thisStory.getTimestamp()));

				// display background resources for assigned senses
				if(thisStory.getHear() == 1) { 
					hear.setBackgroundResource(R.drawable.btn_sense_bg_true);
				} else {
					hear.setBackgroundResource(R.drawable.btn_sense_bg_false);
				}

				if(thisStory.getSee() == 1) { 
					see.setBackgroundResource(R.drawable.btn_sense_bg_true);
				} else {
					see.setBackgroundResource(R.drawable.btn_sense_bg_false);
				}

				if(thisStory.getSmell() == 1) { 
					smell.setBackgroundResource(R.drawable.btn_sense_bg_true);
				} else {
					smell.setBackgroundResource(R.drawable.btn_sense_bg_false);
				}

				if(thisStory.getTaste() == 1) { 
					taste.setBackgroundResource(R.drawable.btn_sense_bg_true);
				} else {
					taste.setBackgroundResource(R.drawable.btn_sense_bg_false);
				}

				if(thisStory.getTouch() == 1) { 
					touch.setBackgroundResource(R.drawable.btn_sense_bg_true);
				} else {
					touch.setBackgroundResource(R.drawable.btn_sense_bg_false);
				}


				//int perspectiveUri = 0; 
				// this was a resource ID. Will now probably be a string
				if(thisStory.getPerspectiveUri() != null) {
					//Log.v(getClass().getSimpleName(), "Perspective URI is not null");
					Bitmap bitmap = BitmapFactory.decodeFile(thisStory.getPerspectiveUri());
					perspective.setImageBitmap(bitmap);
					//imageLoader.load(thisStory.getPerspectiveUri(), perspective);
				} else {
					// Passing 0 to setImageResource will clear the image view.
					int perspectiveUri = 0;
					perspective.setImageResource(perspectiveUri);
					// Log.v(getClass().getSimpleName(), "Perspective URI is null");
				}




			}

			return mWindow;
		}



		@Override
		public View getInfoContents(Marker marker) {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * format time interval to display in info window
		 * @param timeStamp
		 * @return
		 */
		public String formatInterval(String timeStamp) {

			String thisInterval;

			if(timeStamp != null) {
				// handle String formation based on authored date
				int interval = myDateHandler.getDaysAgo(timeStamp);

				thisInterval = "Posted " + Global.formatDaysForUI(interval);
				
			} else {
				thisInterval = "";
				Toast.makeText(MapActivity.this, "timeStamp is null", Toast.LENGTH_SHORT).show();
			}

			// return String value to populate TextView in InfoWindow
			return thisInterval;

		}

	} 


}
