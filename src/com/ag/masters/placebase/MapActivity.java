package com.ag.masters.placebase;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity 
	implements OnMarkerClickListener, OnInfoWindowClickListener {

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
        	
        	ImageView perspective = (ImageView) mWindow.findViewById(R.id.ic_perspective);
            TextView titleUi = (TextView) mWindow.findViewById(R.id.title);
            // sense icons 
            // background in view set to: R.drawable.btn_sense_bg_false
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
    
    
    
	private GoogleMap mMap;
	private ImageButton btnRecordMain;
	
//------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		setUpMapIfNeeded();
		
		btnRecordMain = (ImageButton) findViewById(R.id.btnRecordMain);
		btnRecordMain.setOnClickListener(new OnClickListener() { // TODO
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
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
				.position(new LatLng(-0, 0))
				//.icon(BitmapDescriptorFactory.fromBitmap(createIcon(bearing)))
				.icon(BitmapDescriptorFactory.fromBitmap(createIcon(300f)))
				//.title(numEncounters.toString() + "encounters")
				.title("345 days old")
				//.snippet(calculateAge());
				.snippet("0 encounters"));
				
				Marker newMelbourne = mMap.addMarker(new MarkerOptions()
				//.position(new LatLng(lat, lng))  
				.position(new LatLng(-40, 20))
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
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setUpMapIfNeeded();
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
		
		mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		
		// configure map UI settings
		UiSettings mUiSettings = mMap.getUiSettings();
		mUiSettings.setZoomControlsEnabled(false);
		mUiSettings.setCompassEnabled(true);
		mUiSettings.setTiltGesturesEnabled(true);
		
		addMarkersToMap();
		
		mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		// Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
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
		// TODO Auto-generated method stub
	}
//------------------------------------------------------------------------------------------	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_map, menu);
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
}
