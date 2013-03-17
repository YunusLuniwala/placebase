/**
 * Save the perspective and assemble the last of the story and media objects 
 * to store in the database during confirmation screen to follow
 * 
 * https://code.google.com/p/apics/source/browse/trunk/EE546-Project3/src/android/com/app/aPics.java?r=109
 */
package com.ag.masters.placebase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ag.masters.placebase.handlers.DateHandler;
import com.ag.masters.placebase.model.Global;
import com.ag.masters.placebase.sqlite.Story;
import com.ag.masters.placebase.sqlite.StoryAudio;
import com.ag.masters.placebase.sqlite.StoryImage;
import com.ag.masters.placebase.sqlite.StoryVideo;
import com.ag.masters.placebase.sqlite.User;
import com.ag.masters.placebase.view.CameraView;

public class PerspectiveActivity extends Activity implements 
	OnClickListener, Camera.PictureCallback, Camera.ShutterCallback, LocationListener, SensorEventListener{

	private CameraView cameraView;
    private LocationManager myLocationManager;
    private ImageButton pictureButton;
    private View overlay;
    
	// device sensor (accelerometer and magnetic field)
	private SensorManager mySensorManager;
	float[] inR = new float[16];
	float[] I = new float[16];
	float[] gravity = new float[3];
	float[] geomag = new float[3];
	float[] orientVals = new float[3];
	double azimuth = 0;
	
	private User user;
    private Story story;
	// only one of these will not be null
	private StoryAudio audio;
	private StoryImage image;
	private StoryVideo video;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_perspective);
		
		// get Story and media parcels
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
				StoryImage tempImage = data.getParcelable("media");
				if(tempImage != null) {
					image = tempImage;
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

		// close btn
		ImageButton close = (ImageButton) findViewById(R.id.btn_close_caption);
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PerspectiveActivity.this, MapActivity.class);
				startActivity(intent);
			}
		});

		
		cameraView = (CameraView) this.findViewById(R.id.view_preview);
		pictureButton = (ImageButton) this.findViewById(R.id.btn_takePhoto);
		pictureButton.setOnClickListener(this);
		//pictureButton.setEnabled(false); // start the button disabled until we have a location fix
		
		 LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 overlay = inflater.inflate(R.layout.location_progress, null);
		 overlay.setVisibility(View.INVISIBLE);
		 FrameLayout container = (FrameLayout) findViewById(R.id.perspective_layout);
		 container.addView(overlay);
		
		mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        myLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		// register sensor listeners
		mySensorManager.registerListener(this, 
				mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
				SensorManager
				.SENSOR_DELAY_NORMAL);
		mySensorManager.registerListener(this, 
				mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 
				SensorManager
				.SENSOR_DELAY_NORMAL);
		// register location listener
		myLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER,
				//LocationManager.GPS_PROVIDER,
				0,
				0,
				(LocationListener) this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		myLocationManager.removeUpdates((LocationListener) this);
		mySensorManager.unregisterListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_perspective, menu);
		return true;
	}

	/** 
	 * shutter button clicked
	 */
	@Override
	public void onClick(View v) {
		cameraView.takePicture(this, null, this);
	}

	
	// From the Camera.PictureCallback
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		
		// save the current time to the story object
		DateHandler myDateHandler = new DateHandler();
		story.setTimestamp(myDateHandler.getCurrentTimeAsString());
		
		// save the azimuth to the story
		story.setBearing((float) azimuth);
		Log.v(getClass().getSimpleName(), "bearing set to " + Float.toString(story.getBearing()));
		
		// Set up content Values to save with the Image on the SD card		
		ContentValues values = new ContentValues();
		
		//GPS 
		Location location = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location != null) {
			
			values.put(Images.Media.LATITUDE, location.getLatitude());
			// save latitude data to Story
			story.setLat(location.getLatitude());
			Log.v(getClass().getSimpleName(), "latitude set to " + Double.toString(story.getLat()));
			
			values.put(Images.Media.LONGITUDE, location.getLongitude());
			// save longitude data to Story
			story.setLng(location.getLongitude());
			Log.v(getClass().getSimpleName(), "longitude set to " + Double.toString(story.getLng()));
			
		} else {
			Log.v(getClass().getSimpleName(), "GPS is null");
		}
		

		//Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
		// TODO resize this image and save it to the database instead of the raw data.
		
		
		
		// set up the target to save the image
	    Uri uri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
	    OutputStream imageFileOS;
	    try {
	     imageFileOS = getContentResolver().openOutputStream(uri);
	     // save the image
	     imageFileOS.write(data);
	     imageFileOS.flush();
	     imageFileOS.close();
	     Log.v(getClass().getSimpleName(), "Image saved: " + uri.toString());
	    } catch (FileNotFoundException e) {
	     // TODO Auto-generated catch block
	     e.printStackTrace();
	    } catch (IOException e) {
	     // TODO Auto-generated catch block
	     e.printStackTrace();
	    }
	    
	    // grab the filepath of this new file to associate with the story file
	    String[] projection = {MediaStore.Images.Media.DATA};
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String capturedImageFilePath = cursor.getString(index);
		cursor.close();
		Log.i("IMAGE CAPTURE", capturedImageFilePath);
		// Store the filepath in Story object
		story.setPerspectiveUri(capturedImageFilePath);
		
		// start new Activity to confirm capture
		Intent intent = new Intent(this, ConfirmTrace.class);
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
	
	// From Camera.ShutterCallback
	@Override
	public void onShutter() {
		// play sound
		Log.i(getClass().getSimpleName(), "SHUTTER CALLBACK");
	}


	@Override
	public void onLocationChanged(Location location) {
		// disallow the user from saving a photo until the location
		// has been set and is accurate within 10 meters, if using GPS
		if(location.getProvider() == LocationManager.GPS_PROVIDER) {
			if(location.getAccuracy() < 10) {
				pictureButton.setEnabled(true);
				overlay.setVisibility(View.INVISIBLE);
			} else {
				pictureButton.setEnabled(false);
				overlay.setVisibility(View.VISIBLE);
			}
		}

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

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
					gravity = event.values.clone();
					break;
				case Sensor.TYPE_MAGNETIC_FIELD:
					geomag = event.values.clone();
					break;
				}
				// If gravity and geomag have values then find rotation matrix
				if (gravity != null && geomag != null) {

					// checks that the rotation matrix is found
					boolean success = SensorManager.getRotationMatrix(inR, I, gravity, geomag);
					if (success) {
						SensorManager.getOrientation(inR, orientVals);
						azimuth = Math.toDegrees(orientVals[0]);
						
						// compensate for different screen orientations
						Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
						int compensation = display.getRotation() * 90;                          
						azimuth = azimuth+compensation;

						//TODO: you might need this to get readings that align correctly with the users
						//setDeviceBearing(azimuth);

					}
				}
		
	}


}
