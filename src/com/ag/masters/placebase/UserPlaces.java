/**
 * User Places
 * lists all places where the user has left messages
 * 
 * Map Fragment 
 * https://developers.google.com/maps/documentation/android/map#add_a_map_to_an_android_application
 * 
 * ListFragment
 *  http://www.mysamplecode.com/2012/07/android-listview-cursoradapter-sqlite.html
 *  http://geekswithblogs.net/bosuch/archive/2011/01/31/android---create-a-custom-multi-line-listview-bound-to-an.aspx
 *  
 *  Handles callback from the Adapter Class with a CustomOnClickListener
 *  http://milesburton.com/Android_-_Building_a_ListView_with_an_OnClick_Position
 */

package com.ag.masters.placebase;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.ag.masters.placebase.handlers.DateHandler;
import com.ag.masters.placebase.handlers.OnCustomClickListener;
import com.ag.masters.placebase.handlers.UserPlaceAdapter;
import com.ag.masters.placebase.model.DatabaseHelper;
import com.ag.masters.placebase.model.Global;
import com.ag.masters.placebase.model.UserStoryObject;
import com.ag.masters.placebase.sqlite.Story;
import com.ag.masters.placebase.sqlite.User;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class UserPlaces extends Activity implements OnCustomClickListener {
	
	private DatabaseHelper dbh;
	private User user;
	private int userId;
	
	private GoogleMap mMap;
	private MapFragment mMapFragment;
	Marker marker;
	
	private ListView lv;
	private UserPlaceAdapter adapter;
	private LinearLayout emptyText;
	
	private ArrayList<UserStoryObject> stories;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_places);

		dbh = new DatabaseHelper(this);
		dbh.openDataBase();
		// get user as SharedPreference
		SharedPreferences settings = getSharedPreferences(Global.PREFS, 0);
		userId = settings.getInt("user", -1);
		if (userId != -1) {
			// get the user from the database 
			user = dbh.getUser(userId);
			Log.v("SHARED PREFS", "current User is: " + user.getName());
		} else {
			Log.v("SHARED PREFS", "user was not set");
		}

		emptyText = (LinearLayout) findViewById(R.id.empty_text);
		
		//add the map fragment dynamically
		mMapFragment = MapFragment.newInstance();
		FragmentManager mFragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.user_map, mMapFragment);
		fragmentTransaction.commit();
		// set up the map when it's ready to be operated on
		setUpMapIfNeeded();
		
		// assemble information from database
		stories = assembleData();
		
		if(stories.size() == 0) {
			emptyText.setVisibility(View.VISIBLE);
		}
		
		lv = (ListView) findViewById(R.id.user_list);	
		// associate the custom adapter with the listview
		adapter = new UserPlaceAdapter(this, stories, R.layout.listview_user_item_row, this);
		//lv.setAdapter(new UserPlaceAdapter(this, stories, R.layout.listview_user_item_row, this));
		lv.setAdapter(adapter);
				
		setRowListeners();
		
	}

	/**
	 * create the object data structure
	 * @return
	 */
	ArrayList<UserStoryObject> assembleData() {
		
		ArrayList<UserStoryObject>so = new ArrayList<UserStoryObject>();
		
		// get all items from the database and populate and ArrayList
		ArrayList<Story> userStories = dbh.getAllStoriesForUser(userId);
		
		for (int i = 0; i < userStories.size(); i++) {
			Story s = userStories.get(i);
			int id = s.getId();
			
			int numEncounters = dbh.getEncounterCountForStory(id);
			int numComments = dbh.getCommentCount(id);
			
			Log.v("STORIES RETURNED", "num encounters: " + Integer.toString(numEncounters));
			Log.v("STORIES RETURNED", "num comments: " + Integer.toString(numComments));
			
			// get all comments for this story 
			ArrayList<String> times = dbh.getCommentTimestampArrayForStory(id);
			
			boolean isNew = false;
			if(times.size() > 0) {
				isNew = checkCommentIsNewerThan(times, 3);
			}

			UserStoryObject uso = new UserStoryObject(
					s.getId(), s.getMedia(), s.getLat(), s.getLng(), s.getTimestamp(), s.getPerspectiveUri(), 
					numComments, 
					numEncounters,
					isNew);
					
			
			so.add(uso);
		}
		
		Log.v("STORIES RETURNED", "Stories saved in UserStoryObject array: " + Integer.toString(so.size()));
		
		return so;
		
	}
	
	/**
	 * check if a row should be marked as having new activity
	 * @param dates
	 * @param num
	 * @return
	 */
	private boolean checkCommentIsNewerThan(ArrayList<String> dates, int num) {
		DateHandler handler = new DateHandler();
		
		for (int i = 0; i < dates .size(); i++) {
			String date = dates.get(i);
			int numDays = handler.getDaysAgo(date);
			if (numDays <= num) {
				return true;
			}
		}
		return false;
	}


	public void setRowListeners() {
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				// get the object at the clicked position
				Object o = lv.getItemAtPosition(position);
				// cast it back to a UserStoryObject 
				UserStoryObject fullObject = (UserStoryObject) o;

				moveMapToLocation(fullObject.getLat(),fullObject.getLng());
				
				//Toast.makeText(UserPlaces.this, "You have chosen : " + " " + fullObject.getLat(), Toast.LENGTH_SHORT).show();
				//Toast.makeText(UserPlaces.this, "You have chosen : " + " " + v.getId(), Toast.LENGTH_SHORT).show();
			}

		});
		
	}
	
	// move the map to the story's geocoordinates on click
	private void moveMapToLocation(double lat, double lng) {
		//Toast.makeText(UserPlaces.this, "Move to : " + " " + lat + ", " + lng, Toast.LENGTH_SHORT).show();
		
		removeMarkers(); // remove any markers that are already there
		
		// create LatLng object
		LatLng pos = new LatLng(lat, lng);
		// create the cameraUpdate
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, 18);
		// and move the map
		mMap.moveCamera(cameraUpdate);
		// add a marker
		marker = mMap.addMarker(new MarkerOptions()
			.position(pos));
		
	}
	
	private void removeMarkers() {
		if(marker != null) {
			marker.remove();
		}
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		dbh.close();
	}

	@Override
	public void OnCustomClick(View aView, int position) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "CALLBACK is good", Toast.LENGTH_SHORT).show();
		// delete the record from the database
		UserStoryObject uso = stories.get(position);
		dbh.deleteStory(uso.getId());
		// refresh the dataset
		stories = assembleData();
		adapter.setData(stories);
		// tell adapter to draw the view
		adapter.notifyDataSetChanged();
		
		if(stories.size() == 0) {
			emptyText.setVisibility(View.VISIBLE);
		}
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(this, MapActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		startActivity(intent);
	}

}
