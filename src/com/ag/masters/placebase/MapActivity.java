package com.ag.masters.placebase;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CameraPosition.Builder;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity 
	implements OnMarkerClickListener, OnInfoWindowClickListener, LocationSource, LocationListener {

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
         
             
             
             
             if(marker.getId().equals("m1")) { // TODO: change to the .hear property of the row. if it equals TRUE..
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
	
	private Marker myMarker = null; // custom marker for myLocation
	
	boolean journeyMode; 	// whether we display graphics and behvaiors specific to journey mode
	
	private LocationManager myLocationManager;
	private OnLocationChangedListener myLocationListener;
	private Criteria myCriteria;
	
	// print out test variables to screen
	private TextView testMyLocation;
	// TODO: add device bearing test
	
	//------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		setContentView(R.layout.activity_map);
		setUpMapIfNeeded();
		
		// UI buttons
		addCustomMyLocationButton();
		
		// testPrintouts. Delete when done
		testMyLocation = (TextView) findViewById(R.id.testMyLocation);
		
		myCriteria = new Criteria();
		myCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		myLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		
	}
	
	//------------------------------------------------------------------------------------------	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setUpMapIfNeeded();
		
		//Register for location updates using a Criteria, and a callback on the specified looper thread.
		myLocationManager.requestLocationUpdates(
				400L, 		// minTime
				1.0f,		// minDistance
				myCriteria,	// criteria
				this, 		// listener
				null);		// intent
		
		// replaces the location source of the my-location layer
		mMap.setLocationSource(this);
		
		if(journeyMode == true) {
			setMyLocationMarker();
			Toast.makeText(this, "JOURNEY MODE", Toast.LENGTH_LONG).show();
		}

	}
	//------------------------------------------------------------------------------------------
	@Override
	public void onPause() {
		/* Disable the my-location layer (this causes our LocationSource to be automatically deactivated.) */
		mMap.setMyLocationEnabled(false);
		mMap.setLocationSource(null);
		
		myLocationManager.removeUpdates(this);
		super.onPause();
	}
	
	//------------------------------------------------------------------------------------------
	private void addCustomMyLocationButton() {
		//http://stackoverflow.com/questions/14826345/android-maps-api-v2-change-mylocation-icon
		
		ImageButton btnShowMyLocation = (ImageButton) findViewById(R.id.btnShowMyLocation);

		btnShowMyLocation.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(final View v) {
				Location myLocation = mMap.getMyLocation();
				if(myLocation != null) {
					LatLng target = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
					CameraPosition position = mMap.getCameraPosition();
					
					Builder builder = new CameraPosition.Builder();
					builder.zoom(15);
					builder.target(target);
					mMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
				
				}
				
			}
		});
	}
	
	//------------------------------------------------------------------------------------------	
	private void addMarkersToMap() {
	// get rows from database
		// dummy LatLngs - should be in database
				final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
				
				// for each row in database
					// float bearing = BEARING;
					// float lat = LAT;
					// float lng = LNG;
					// int numEncounters = ENCOUNTERS;
				
					// Bitmap icon = createIcon(bearing);
					// *** another function ***String calculateAge(TIMESTAMP) --> days since message was left from timestamp.....?
				
				Marker melbourne = mMap.addMarker(new MarkerOptions()
				//.position(new LatLng(lat, lng))  
				.position(new LatLng(34, -86))
				//.icon(BitmapDescriptorFactory.fromBitmap(createIcon(bearing)))
				.icon(BitmapDescriptorFactory.fromBitmap(createIcon(300f)))
				//.title(numEncounters.toString() + "encounters")
				.title("345 days old")
				//.snippet(calculateAge());
				.snippet("0 encounters"));
				
				Marker newMelbourne = mMap.addMarker(new MarkerOptions()
				//.position(new LatLng(lat, lng))  
				.position(new LatLng(30, -90))
				//.icon(BitmapDescriptorFactory.fromBitmap(createIcon(bearing)))
				.icon(BitmapDescriptorFactory.fromBitmap(createIcon(40.5f)))
				//.title(numEncounters.toString() + "encounters")
				.title("6 days old")
				//.snippet(calculateAge());
				.snippet("20 encounters"));
		
	}

	//------------------------------------------------------------------------------------------
	// create an marker icon at runtime to show location and orientation of the story
	// http://stackoverflow.com/questions/11740362/merge-two-bitmaps-in-android?rq=1
	// http://stackoverflow.com/questions/14811579/android-map-api-v2-custom-marker-with-imageview
	private Bitmap createIcon(float bearing) {
		
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(100,100,Config.ARGB_8888);
			Canvas c = new Canvas(bitmap);
			Resources res = getResources();
			
			Bitmap mIcon = BitmapFactory.decodeResource(res, R.drawable.ic_map_marker);
			Bitmap mCompass = BitmapFactory.decodeResource(res, R.drawable.ic_compass);
			
			Drawable mIconDrawable = new BitmapDrawable(res, mIcon);
			Drawable mCompassDrawable = new BitmapDrawable(res, mCompass);
			
			mIconDrawable.setBounds(0,0,100,100);
			mCompassDrawable.setBounds(25,0,75,75);
			
			
			mIconDrawable.draw(c);
			// rotate the canvas before drawing the orientation.
			// ** may have to convert sensor values from Radians to Degrees before saving them here **
			//rotate (float degrees, float px, float py)
			c.rotate(bearing, 50, 40);
			mCompassDrawable.draw(c);
			
			
		} catch (Exception e) {
			
		}
		return bitmap;
	}

	//------------------------------------------------------------------------------------------
	private Bitmap createMyMarkerIcon() {
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(50,50,Config.ARGB_8888);
			Canvas c = new Canvas(bitmap);		
			
			Bitmap mIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_compass);
			Drawable mIconDrawable = new BitmapDrawable(getResources(), mIcon);
			mIconDrawable.setBounds(0,0,50,50);
			mIconDrawable.draw(c);
			
			// TODO: rotate so that the shit is pointing to the marker we are journeying to.
			

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
		mUiSettings.setCompassEnabled(true);
		mUiSettings.setTiltGesturesEnabled(true);
		mUiSettings.setMyLocationButtonEnabled(false); //disable myLocation button. we roll our own

		addMarkersToMap();
		
		// show marker info using our custom info window
		mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		// Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        
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
		
		journeyMode = true;
		// TODO: set this to false from a button within the info window that pops up from below
		
		setMyLocationMarker(); // maybe only show this if you are in journey mode
		// TODO: BEGIN the journey process: display partial view with additional information for this place
		// set boundaries so myLocation and the destination marker are both visible on the screen
		//CameraUpdateFactory.newLatLngBounds(LatLbgBounds bounds, int padding (in px));
		
	}
//------------------------------------------------------------------------------------------
// LOCATION LISTENER
// http://android-er.blogspot.com/2013/01/implement-locationsource-and.html
//------------------------------------------------------------------------------------------

	@Override
	public void onLocationChanged(Location location) {
		
		//Toast.makeText(getBaseContext(), "Moved to "+location.toString(), Toast.LENGTH_LONG).show();
		
		if (myLocationListener != null) {
			myLocationListener.onLocationChanged(location);
			
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			
			testMyLocation.setText(
					"lat: " + lat + "\n" +
					"lon: " + lng);
			
			// animate to user's location when the location changes.
			// AG: don't do this until you are journeying.
			LatLng latlng= new LatLng(location.getLatitude(), location.getLongitude());
			
			// mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
			if(journeyMode) {
				setMyLocationMarker(); // maybe only show this if you are in journey mode
			}
		}
		
	}

	//------------------------------------------------------------------------------------------
	private void setMyLocationMarker() {
	//http://androiddev.orkitra.com/?p=3933	
		// add custom marker location based on Location Changed 
		removeMyLocationMarker();
		Location location = mMap.getMyLocation();
		Bitmap bmp = createMyMarkerIcon();
		myMarker = mMap.addMarker(new MarkerOptions()
			.position(new LatLng(location.getLatitude(), location.getLongitude()))
			.icon(BitmapDescriptorFactory.fromBitmap(bmp))
			.anchor(.5f,.5f));
	}
	//------------------------------------------------------------------------------------------
	private void removeMyLocationMarker() {
		if(myMarker != null) {
			myMarker.remove(); // remove the old one before you draw another.
		}
	}
	//------------------------------------------------------------------------------------------
	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

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

	@Override
	public void deactivate() {
		myLocationListener = null;
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}
}
