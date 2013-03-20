/**
 * User Places
 * lists all places where the user has left messages
 * 
 * Map Fragment 
 * https://developers.google.com/maps/documentation/android/map#add_a_map_to_an_android_application
 * 
 * ListFragment
 */

package com.ag.masters.placebase;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.ag.masters.placebase.model.DatabaseHelper;
import com.ag.masters.placebase.model.Global;
import com.ag.masters.placebase.sqlite.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

public class UserPlaces extends Activity {
	
	private DatabaseHelper dbh;
	private User user;
	
	private GoogleMap mMap;
	private MapFragment mMapFragment;
	
	private ArrayList userStories;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_places);

		dbh = new DatabaseHelper(this);

		// get user as SharedPreference
		SharedPreferences settings = getSharedPreferences(Global.PREFS, 0);
		int userId = settings.getInt("user", -1);
		if (userId != -1) {
			// get the user from the database 
			user = dbh.getUser(userId);
			Log.v("SHARED PREFS", "current User is: " + user.getName());
		} else {
			Log.v("SHARED PREFS", "user was not set");
		}

		//add the map fragment dynamically
		mMapFragment = MapFragment.newInstance();
		FragmentManager mFragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.user_map, mMapFragment);
		fragmentTransaction.commit();

		setUpMapIfNeeded();
		
		
		// get all items from the database and populate and ArrayList
		userStories = dbh.getAllStoriesForUser(userId);
		Log.v("STORIES RETURNED", Integer.toString(userStories.size()));
		
		displayListView();
		
	}
	
	// http://www.mysamplecode.com/2012/07/android-listview-cursoradapter-sqlite.html
	private void displayListView() {
		

		
		
	}

	
	private void setUpMapIfNeeded() {
		if(mMap == null) {
			mMap = mMapFragment.getMap();
		}
		if(mMap != null) {
			// safe to manipulate it
		}
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setUpMapIfNeeded();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_places, menu);
		return true;
	}

}
