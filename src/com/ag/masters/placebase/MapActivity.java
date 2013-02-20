package com.ag.masters.placebase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
import android.view.Display;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ag.masters.placebase.model.StoriesDBAdapter;
import com.ag.masters.placebase.sqlite.Story;
import com.ag.masters.placebase.sqlite.StoryAudio;
import com.ag.masters.placebase.sqlite.StoryImage;
import com.ag.masters.placebase.sqlite.StoryVideo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity 
implements OnMarkerClickListener, OnInfoWindowClickListener, OnMapClickListener, LocationSource, LocationListener, SensorEventListener {

	//------------------------------------------------------------------------------------------
	// InfoWindowAdapter
	// customizes the look of the marker's info window
	//------------------------------------------------------------------------------------------

	class CustomInfoWindowAdapter implements InfoWindowAdapter {
		// The viewgroup contains an ImageView with id "badge" // TODO: change it.
		// and two TextViews with id "title" and "snippet".
		private final View mWindow;

		//------------------------------------------------------------------------------------------
		CustomInfoWindowAdapter() {
			// the xml layout file for the window bubble // TODO: change this.
			mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
		}

		//------------------------------------------------------------------------------------------
		@Override
		public View getInfoWindow(Marker marker) { // called for every Marker
			// Use the equals() method on a Marker to check for equals.  Do not use ==.

			ImageView perspective = (ImageView) mWindow.findViewById(R.id.ic_perspective);	// perspective image
			TextView titleUi = (TextView) mWindow.findViewById(R.id.title); 				// days ago

			// background in view set to: R.drawable.btn_sense_bg_false						// sense icons 
			ImageView hear = (ImageView) mWindow.findViewById(R.id.ic_sense_hear);
			ImageView see = (ImageView) mWindow.findViewById(R.id.ic_sense_see);
			ImageView smell = (ImageView) mWindow.findViewById(R.id.ic_sense_smell);
			ImageView taste = (ImageView) mWindow.findViewById(R.id.ic_sense_taste);
			ImageView touch = (ImageView) mWindow.findViewById(R.id.ic_sense_touch);


			int perspectiveUri = 0; // this was a resource ID. Will now probably be a string
			/*if(the row has a URI to a perspective image)  {
        	 	//perspectiveUri = thatresourceURI;
        	 	make an image resource from that URI
             } else {
                 // Passing 0 to setImageResource will clear the image view.
                 perspectiveUri = 0;
             }*/
			perspective.setImageResource(perspectiveUri);

			String title = marker.getTitle(); // this you will get from the extended marker object, or a call to the databse...
			if (title != null) {
				titleUi.setText(title);
			} else {
				titleUi.setText("");
			}

			// TODO: iterate through each property of the row. if it equals TRUE
			if(marker.getId().equals("m1")) { 
				hear.setBackgroundResource(R.drawable.btn_sense_bg_true);
			} else {
				hear.setBackgroundResource(R.drawable.btn_sense_bg_false);
			}

			/* if(marker.getSenseSee().equals(true)) {
            	 see.setBackgroundResource(R.drawable.btn_sense_bg_true);
             } else {
            	 see.setBackgroundResource(R.drawable.btn_sense_bg_false);
             } */

			return mWindow;
		}

		@Override
		public View getInfoContents(Marker marker) {
			// TODO Auto-generated method stub
			return null;
		}
	} 
	//------------------------------------------------------------------------------------------
	//------------------------------------------------------------------------------------------
	public static final String MODE = "journeyMode";

	private GoogleMap mMap;	
	private CameraPosition myCameraPosition;

	// journey mode 
	private Marker myMarker = null; // custom marker for myLocation
	private Canvas myMarkerCanvas;
	private Location myCurrentLocation = null;

	private static int journeyMode = -1;
	private static Location targetLocation;
	private static float targetBearing = 0;
	private static float targetDistance = 0;

	// print out test variables to screen
	private TextView testMyLocation;
	private TextView testJourneyMode;
	private TextView testTargetLat;
	private TextView testTargetLng;
	private TextView testTargetBearing;
	private TextView testTargetDistance;
	private TextView testGeoX;
	private TextView testGeoY;
	private TextView testGeoZ;
	private TextView testAccelX;
	private TextView testAccelY;
	private TextView testAccelZ;
	private TextView testAzimuth;
	private TextView testPitch;
	private TextView testRoll;

	private LocationManager myLocationManager;
	private OnLocationChangedListener myLocationListener;
	private Criteria myCriteria;

	private int screenHeight;
	private int screenWidth;

	private SensorManager mySensorManager;
	private boolean sensorRunning;

	float[] inR = new float[16];
	float[] I = new float[16];
	float[] gravity = new float[3];
	float[] geomag = new float[3];
	float[] orientVals = new float[3];

	float bearing = 0; // normalized whole number for raw sensor azimuth input
	double azimuth = 0;
	/*double pitch = 0;
    double roll = 0;*/

	private boolean rotateView = true;
	private boolean firstFix = true;
	private static CameraPosition MYLOCATION;

	// Media Record Btns
	private ImageButton btnRecordMedia;
	private FrameLayout layoutRecordMedia;
	private boolean isRecordOptionsShowing;

	// Animation
	private ObjectAnimator slideUpHalfway;
	private ObjectAnimator slideDown;
	
	// intent request codes
	private static final int IMAGE_CAPTURE = 0;
	private static final int VIDEO_CAPTURE = 1;
	private static final int AUDIO_CAPTURE = 2;

	//------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		

		setContentView(R.layout.activity_map);
		setUpMapIfNeeded();


		// UI buttons
		addCustomMyLocationButton();

		calculateScreenDimensions();

		// testPrintouts. Delete when done
		testMyLocation = (TextView) findViewById(R.id.testMyLocation);
		testTargetBearing = (TextView) findViewById(R.id.testTargetBearing);
		testJourneyMode = (TextView) findViewById(R.id.testJourneyMode);
		testTargetLat = (TextView) findViewById(R.id.testTargetLat);
		testTargetLng = (TextView) findViewById(R.id.testTargetLng);
		testTargetDistance = (TextView) findViewById(R.id.testTargetDistance);
		testGeoX = (TextView) findViewById(R.id.geoX);
		testGeoY = (TextView) findViewById(R.id.geoY);
		testGeoZ = (TextView) findViewById(R.id.geoZ);
		testAccelX = (TextView) findViewById(R.id.accelX);
		testAccelY = (TextView) findViewById(R.id.accelY);
		testAccelZ = (TextView) findViewById(R.id.accelZ);
		testAzimuth = (TextView) findViewById(R.id.testAzimuth);
		testPitch = (TextView) findViewById(R.id.testPitch);
		testRoll = (TextView) findViewById(R.id.testRoll);

		//testGeoX.setText("0.00");
		//testGeoY.setText("0.00");
		//testGeoZ.setText("0.00");
		//testAccelX.setText("0.00");
		//testAccelY.setText("0.00");
		//testAccelZ.setText("0.00");
		testAzimuth.setText("0.00");
		//testPitch.setText("0.00");
		//testRoll.setText("0.00");

		myCriteria = new Criteria();
		myCriteria.setAccuracy(Criteria.ACCURACY_FINE);

		mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		isRecordOptionsShowing = false; // initialize the state of media recording
		
		
		
		
		
		// define journey block and layout
		RelativeLayout journeyBlock = (RelativeLayout) findViewById(R.id.journeyblock);
		
		
		
		// define media buttons and layouts as globals
		layoutRecordMedia = (FrameLayout) findViewById(R.id.recordBtnLayout);
		btnRecordMedia = (ImageButton) findViewById(R.id.btnRecordMain);
		// http://stackoverflow.com/questions/4969689/android-animation-xml-issues
		btnRecordMedia.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// the recording options are showing. we need to hide them
				if(isRecordOptionsShowing) {
					hideMediaButtons();
				}
				else { // the recording options are hidden we need to show them
					showMediaButtons();
				}
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
		
		

		// test
		updateTestValues();
	}
	
	//------------------------------------------------------------------------------------------

	
	
	//------------------------------------------------------------------------------------------
	private void startCaptureImage() {
		// TODO: check that there is a camera
		Intent startCameraActivity = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		// all junk.
		// look more at this...
		///http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder/10229228#10229228
		String filename = "temp.jpg";
		
		File out = Environment.getExternalStorageDirectory();
		out = new File(out, filename);
		startCameraActivity.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out));
		
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, filename);
		Uri mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		
		//StoryFileHandler myStoryFileHandler = new StoryFileHandler();
		//String filename = myStoryFileHandler.createFilename(IMAGE_CAPTURE);
		
		//startCameraActivity.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
		//startCameraActivity.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filename)));
		//startCameraActivity.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(new File(filename).toString()));
		
		startCameraActivity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivityForResult(startCameraActivity, IMAGE_CAPTURE); // can add options as a bundle

	}

	private void startCaptureVideo() {
		// TODO: 
	}

	private void startCaptureAudio() {
		// TODO:
	}

	// handle results from Recording activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Bundle extras = data.getExtras();

		// create the handler object to write files to SD card
		//StoryFileHandler myStoryFileHandler = new StoryFileHandler();
		// create a filename for returned object
		//String filename = myStoryFileHandler.createFilename(requestCode);

		// create new story object
		Story story = new Story();

		// also check to see that the request code is OK
		switch(requestCode) {
		case IMAGE_CAPTURE:
			if (resultCode == RESULT_OK) {
				// create a new Image object
				StoryImage image = new StoryImage();
				// turn URI into string and store in Image object TODO: figure out this URI key
				Uri imageUri = data.getData();
				
				Bitmap bitmap = null;
				try {
					bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				// image.setUri(extras.getString("URI"));
//				myStoryFileHandler.saveStoryImageToFile(filename, bitmap);
				// story.setUser(user); TODO: set this username on a previous login activity, or set a default
				story.setMedia(IMAGE_CAPTURE);
				break;
			}
		case VIDEO_CAPTURE:
			// create a new Video object
			StoryVideo video = new StoryVideo();
			// video.setUri(extras.getString("URI"));
			// story.setUser(user); TODO: set this username on a previous login activity, or set a default
			story.setMedia(VIDEO_CAPTURE);
			break;
		case AUDIO_CAPTURE:
			StoryAudio audio = new StoryAudio();
			// String uri = extras.getString("URI"); // WHAT IS THE KEY???
			// story.setUser(user); TODO: set this username on a previous login activity, or set a default
			story.setMedia(AUDIO_CAPTURE);
			break;
		}

		// start SensesActivity,
		// pass parcellable Story
		// pass parcellable StoryAudio, StoryVideo, or StoryImage too.
	}


	//------------------------------------------------------------------------------------------	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setUpMapIfNeeded();


		myLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		//Register for location updates using a Criteria, and a callback on the specified looper thread.
		myLocationManager.requestLocationUpdates(
				400L, 		// minTime
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


		// test
		updateTestValues();
	}
	//------------------------------------------------------------------------------------------
	@Override
	public void onPause() {
		super.onPause();
		/* Disable the my-location layer (this causes our LocationSource to be automatically deactivated.) */
		mySensorManager.unregisterListener(this);

		myLocationManager.removeUpdates(this);
		mMap.setMyLocationEnabled(false);
		mMap.setLocationSource(null);
	}

	//------------------------------------------------------------------------------------------
	private void addCustomMyLocationButton() {
		//http://stackoverflow.com/questions/14826345/android-maps-api-v2-change-mylocation-icon

		ImageButton btnShowMyLocation = (ImageButton) findViewById(R.id.btnShowMyLocation);

		btnShowMyLocation.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(final View v) {

				rotateView = false;

				Location myLocation = mMap.getMyLocation();

				if(myLocation != null) {

					mMap.animateCamera(CameraUpdateFactory.newCameraPosition(MYLOCATION), new CancelableCallback() {
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


	//------------------------------------------------------------------------------------------
	public List<Story> getallStories() {

		// create a new instance of the database adapter
		StoriesDBAdapter myStoriesDBAdapter = new StoriesDBAdapter(this);

		// Generate List from all stories in the database;
		List<Story> allStories = myStoriesDBAdapter.getAllStories();

		return allStories;
	}

	//------------------------------------------------------------------------------------------	
	private void addMarkersToMap() {
		
		// store all stories as objects from database
		List<Story> stories = getallStories();
		if(stories != null) {
			for (int i=0; i<stories.size(); i++) {
				Story s = stories.get(i);

				LatLng ll = new LatLng(s.getLat(), s.getLng());
				String t = "" + "days old"; // TODO: calculate days old
				Bitmap ic = createIcon(s.getMedia());

				// now, create a marker
				mMap.addMarker(new MarkerOptions()
				.position(ll)
				.icon(BitmapDescriptorFactory.fromBitmap(ic))
				.title(t));
				// and store that marker's ID into the story object from which it came
				// this will link the story in the db to the marker on the map
			}
		}
		
		// *** another function ***String calculateAge(TIMESTAMP) --> days since message was left from timestamp.....?
		
		// dummy LatLngs - should be in database
		Marker melbourne = mMap.addMarker(new MarkerOptions()
		.position(new LatLng(33.784, -84.343636))
		.icon(BitmapDescriptorFactory.fromBitmap(createIcon(2)))
		.title("345 days old"));

		Marker newMelbourne = mMap.addMarker(new MarkerOptions()
		.position(new LatLng(33.7557, -84.32952))
		.icon(BitmapDescriptorFactory.fromBitmap(createIcon(0)))
		.title("6 days old"));

	}

	//------------------------------------------------------------------------------------------
	// create an marker icon at runtime to show location and type of the story
	// http://stackoverflow.com/questions/11740362/merge-two-bitmaps-in-android?rq=1
	// http://stackoverflow.com/questions/14811579/android-map-api-v2-custom-marker-with-imageview
	private Bitmap createIcon(int mediaType) {

		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(100,100,Config.ARGB_8888);
			Canvas c = new Canvas(bitmap);
			Resources res = getResources();

			Bitmap mIcon = BitmapFactory.decodeResource(res, R.drawable.ic_map_marker);
			//Bitmap mCompass = BitmapFactory.decodeResource(res, R.drawable.ic_compass);
			Bitmap mMedia;
			switch(mediaType) {
			case IMAGE_CAPTURE:// image
				mMedia = BitmapFactory.decodeResource(res, R.drawable.ic_record_photo);
				break;
			case VIDEO_CAPTURE: // video
				mMedia = BitmapFactory.decodeResource(res, R.drawable.ic_record_video);
				break;
			case AUDIO_CAPTURE: // audio
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
			mMediaDrawable.setBounds(25,0,75,75);

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
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
					.getMap();
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
	//------------------------------------------------------------------------------------------
	public void showMediaButtons() {
		layoutRecordMedia.setVisibility(View.VISIBLE);
		PropertyValuesHolder makeVisible = PropertyValuesHolder.ofFloat("alpha", 0f,1f);
		PropertyValuesHolder slideUpHalfway = PropertyValuesHolder.ofFloat("translationY", screenHeight, (screenHeight-500));

		ObjectAnimator
		.ofPropertyValuesHolder(layoutRecordMedia, makeVisible, slideUpHalfway)
		.setDuration(200)
		.start();

		isRecordOptionsShowing = true;
	}
	//------------------------------------------------------------------------------------------
	public void hideMediaButtons() {

		PropertyValuesHolder makeInvisible = PropertyValuesHolder.ofFloat("alpha", 1f,0f);
		PropertyValuesHolder slideDown = PropertyValuesHolder.ofFloat("translationY", (screenHeight-500), screenHeight);

		ObjectAnimator
		.ofPropertyValuesHolder(layoutRecordMedia, makeInvisible, slideDown)
		.setDuration(200)
		.start();

		isRecordOptionsShowing = false;
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
		case R.id.saved_spaces:
			Toast.makeText(this, "Saved Spaces", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.user_places:
			Toast.makeText(this, "user places", Toast.LENGTH_SHORT).show();
			return true;	
		}

		return super.onOptionsItemSelected(item);
	}
	//------------------------------------------------------------------------------------------
	// MARKER RELATED CLICK LISTENERS
	//------------------------------------------------------------------------------------------	
	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		return false;
	}
	//------------------------------------------------------------------------------------------
	@Override
	public void onInfoWindowClick(Marker marker) {

		journeyMode = 1;
		// TODO: set this to false (0) from a button within the info window that pops up from below

		// store Location in a globa var
		LatLng target = marker.getPosition();
		targetLocation = new Location("Target");
		targetLocation.setLatitude(target.latitude);
		targetLocation.setLongitude(target.longitude);

		// TODO: BEGIN the journey process: 
		// display partial view with additional information for this place
		// set boundaries so myLocation and the destination marker are both visible on the screen
		// CameraUpdateFactory.newLatLngBounds(LatLbgBounds bounds, int padding (in px));

		//****** PROBABLY NEED TO PUT THIS IN ANOTHER FUNCTION AND STORE THE
		// VALUES FOR THE MARKER'S ENDING LOCATION, so that the bearing can be UPDATED
		// with the user's LAT and LNG .. so put it in onLOCATIONCHANGED() ***********/
		// http://android-er.blogspot.com/2013/02/get-bearing-between-two-location-using.html
		calculateDistanceToTarget();
		calculateBearingToTarget();

		setMyLocationMarker(); // TODO: rotate this shit so it appears to be pointing in the right direction

		// test
		updateTestValues();

	}

	//------------------------------------------------------------------------------------------
	// MAP CLICK LISTENER
	//------------------------------------------------------------------------------------------
	@Override
	public void onMapClick(LatLng point) {
		// exit strategy: hide the media buttons if they are showing
		if(isRecordOptionsShowing == true) {
			//Toast.makeText(getApplicationContext(), "map was clicked", Toast.LENGTH_LONG).show();
			hideMediaButtons();
		}
	}

	//------------------------------------------------------------------------------------------
	// LOCATION LISTENER
	// http://android-er.blogspot.com/2013/01/implement-locationsource-and.html
	//------------------------------------------------------------------------------------------
	@Override
	public void onLocationChanged(Location location) {

		if (myLocationListener != null) {
			myLocationListener.onLocationChanged(location);

			if(firstFix) { // we have gotten the first fix on myLocation

				// set the MyLocation button position to a global variable
				MYLOCATION = new CameraPosition.Builder()
				.target(new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude()))
				.zoom(12)
				.bearing(mMap.getCameraPosition().bearing)
				.tilt(0)
				.build();

				//animate the camera to this place NOT WORKING
				mMap.animateCamera(CameraUpdateFactory.newCameraPosition(MYLOCATION), new CancelableCallback() {
					@Override
					public void onFinish() {
						rotateView = true;
					}

					@Override
					public void onCancel() {
						rotateView = true;
					}
				});
				// set firstFix to false so it runs only once
				firstFix = false;
			}

			// save device location to global variable.
			myCurrentLocation = mMap.getMyLocation();

			// test
			testMyLocation.setText(
					"My lat: " + myCurrentLocation.getLatitude() + "\n" +
							"My lon: " + myCurrentLocation.getLongitude());

			// if we are journeying. update myMarker's location
			if (journeyMode == 1) {
				calculateDistanceToTarget();
				calculateBearingToTarget();

				setMyLocationMarker();
			}

		}
		// test
		updateTestValues();

	}
	//------------------------------------------------------------------------------------------
	private float calculateDistanceToTarget() {
		if(myCurrentLocation != null) {
			targetDistance = myCurrentLocation.distanceTo(targetLocation);
			return targetDistance;
		} else return 0;
	}
	//------------------------------------------------------------------------------------------
	private float calculateBearingToTarget() {
		if(myCurrentLocation != null) {

			targetBearing = myCurrentLocation.bearingTo(targetLocation); // This is insufficient. Need to use actual sensor bearing
			targetBearing = bearing - targetBearing;
			targetBearing = targetBearing >= 0 ? targetBearing: targetBearing + 360;
			// CHANGE THESE NUMBERS SO THAT  THEY READ 0 - !*) depending on 
			// and round them to a whole number
			targetBearing = Math.round(targetBearing);
			targetBearing = targetBearing >= 180 ? -(360 - targetBearing): targetBearing;


			updateTestValues();
			return targetBearing;

		}
		else return 0;
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
			myMarker.remove(); // remove the old one before you draw another.
		}
	}

	//------------------------------------------------------------------------------------------
	private void rotateMyCamera() {

		if(mMap.isMyLocationEnabled() && mMap.getMyLocation() != null && rotateView == true) {

			CameraPosition cameraPosition = new CameraPosition.Builder()
			.target(mMap.getCameraPosition().target)      
			.zoom(mMap.getCameraPosition().zoom)               
			.bearing(bearing)                
			.tilt(mMap.getCameraPosition().tilt)                   
			.build();               

			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 100, null);


		}
	}
	//------------------------------------------------------------------------------------------
	private void updateTestValues() {
		if (myCurrentLocation != null) {
			if (journeyMode >= -1 && journeyMode <= 1) {
				testJourneyMode.setText("journey MODE: " + String.valueOf(journeyMode));
			}
			if (targetLocation != null) {
				testTargetLat.setText("TargetLat: " + targetLocation.getLatitude());
				testTargetLng.setText("TargetLng: " + targetLocation.getLongitude());	
			}
			if(targetDistance != 0) {
				testTargetDistance.setText("TargetDistance: " + targetDistance);
			} 
			if (targetBearing != 0) {
				testTargetBearing.setText("targetBearing: " + targetBearing);
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
		if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
			return;

		// Gets the value of the sensor that has been changed
		switch (event.sensor.getType()) {  
		case Sensor.TYPE_ACCELEROMETER:
			gravity = event.values.clone();
			//test
			/*testAccelX.setText(Float.toString(gravity[0]));
			testAccelY.setText(Float.toString(gravity[1]));
			testAccelZ.setText(Float.toString(gravity[2]));*/
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			geomag = event.values.clone();
			// test
			/*testGeoX.setText(Float.toString(gravity[0]));
			testGeoY.setText(Float.toString(gravity[1]));
			testGeoZ.setText(Float.toString(gravity[2]));*/
			break;
		}

		// If gravity and geomag have values then find rotation matrix
		if (gravity != null && geomag != null) {

			// checks that the rotation matrix is found
			boolean success = SensorManager.getRotationMatrix(inR, I, gravity, geomag);
			if (success) {
				SensorManager.getOrientation(inR, orientVals);
				azimuth = Math.toDegrees(orientVals[0]);
				//pitch = Math.toDegrees(orientVals[1]);
				//roll = Math.toDegrees(orientVals[2]);

				// compensate for different screen orientations
				Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
				int compensation = display.getRotation() * 90;                          
				azimuth = azimuth+compensation;

				setDeviceBearing(azimuth);



			}
		}

	}	
	//------------------------------------------------------------------------------------------	
	private void setDeviceBearing(double azimuth) {
		// normalize azimuth values so they range from 0-360
		bearing = (float) azimuth;
		if (!rotateView) {
			//bearing = 0;
		} else {
			bearing = bearing >= 0 ? bearing: bearing + 360;
			// and round them to a whole number
			bearing = Math.round(bearing);
		}

		if(journeyMode == 1) {
			calculateBearingToTarget();
			//setMyLocationMarker();
		}

		rotateMyCamera();

		// test
		testAzimuth.setText("Bearing: " + bearing);

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




}
