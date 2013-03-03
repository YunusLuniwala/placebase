package com.ag.masters.placebase.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ag.masters.placebase.MapActivity;
import com.ag.masters.placebase.R;
import com.ag.masters.placebase.handlers.DateHandler;
import com.ag.masters.placebase.model.DatabaseHelper;
import com.ag.masters.placebase.model.Global;
import com.ag.masters.placebase.sqlite.Encounter;
import com.ag.masters.placebase.sqlite.Story;
import com.ag.masters.placebase.sqlite.User;

public class MediaFragment extends Fragment  {

	DatabaseHelper dbh;

	int mediaType;
	
	ImageButton _btnHear;
	ImageButton _btnSee;
	ImageButton _btnSmell;
	ImageButton _btnTaste;
	ImageButton _btnTouch;
	
	Story story;
	User user;
	Encounter encounter;
	
	public MediaFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		dbh = new DatabaseHelper(getActivity());
		
		// save parcel objects in global vars
		story = getArguments().getParcelable("story");
		user = getArguments().getParcelable("user");
		encounter = getArguments().getParcelable("encounter");
		
		// inflate the layout
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
				// save senses to the database? or do this onDestory so we don't have to repeat it for other "back" operations?
				Intent intent = new Intent(getActivity(), MapActivity.class);
				getActivity().startActivity(intent);
			}
		});
		
		// Fields to populate from Story data  
        TextView numComments = (TextView) v.findViewById(R.id.num_comments);
        numComments.setText(Integer.toString(getArguments().getInt("mediaType"))); // temporary
        
        TextView numEncounters = (TextView) v.findViewById(R.id.num_encounters);
        numComments.setText(Integer.toString(4)); // temporary
        
        TextView metaTime = (TextView) v.findViewById(R.id.meta_timestamp);
        metaTime.setText(setTimestampAndAuthor()); 
        
        TextView metaGeo = (TextView) v.findViewById(R.id.meta_geo);
        metaGeo.setText(setGeo());
		
		List<ImageButton> imageButtons = new ArrayList<ImageButton>();
		imageButtons.add(_btnHear);
		imageButtons.add(_btnSee);
		imageButtons.add(_btnSmell);
		imageButtons.add(_btnTaste);
		imageButtons.add(_btnTouch);
		
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
							story.setSee(0);
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
		// subsitute the view stub based on the mediaType
		switch(mediaType) {
		case (Global.AUDIO_CAPTURE):
			View audioStub = ((ViewStub) v.findViewById(R.id.audio_stub)).inflate();
			break;
		case (Global.IMAGE_CAPTURE):
			View imageStub = ((ViewStub) v.findViewById(R.id.image_stub)).inflate();
			break;
		case (Global.VIDEO_CAPTURE):
			View videoStub = ((ViewStub) v.findViewById(R.id.video_stub)).inflate();
			break;
		}
        
        // return the view
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

	

}
