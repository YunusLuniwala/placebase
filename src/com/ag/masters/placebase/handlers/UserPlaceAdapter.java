/**
 * UserPlaceAdapter.class
 * 
 * extends the ArrayAdapter to bind story objects to a list array
 * for user places
 * 
 * formats and populates the fields
 * 
 * sends callback from this adapter to its class using a CustomOnClickListener
 * http://milesburton.com/Android_-_Building_a_ListView_with_an_OnClick_Position
 * 
 */

package com.ag.masters.placebase.handlers;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ag.masters.placebase.R;
import com.ag.masters.placebase.model.Global;
import com.ag.masters.placebase.model.UserStoryObject;
import com.ag.masters.placebase.sqlite.DatabaseHelper;


public class UserPlaceAdapter extends BaseAdapter {
	
	private ArrayList<UserStoryObject> stories;

	private LayoutInflater inflater;
	private int layoutResourceId;
	
	DateHandler handler;
	DatabaseHelper dbh;
	Context mContext;
	
	private OnCustomClickListener mCallback; //this is our activity
	
	public UserPlaceAdapter(Context context, ArrayList<UserStoryObject> data, int layout, OnCustomClickListener callback) {
		
		stories = data;
		inflater = LayoutInflater.from(context);
		layoutResourceId = layout;
		mContext = context;
		
		handler = new DateHandler();
		this.mCallback = callback;

	}

	@Override
	public int getCount() {
		return stories.size();
	}
	
	@Override
	public Object getItem(int position) { // Returns the UserStoryObject
		return stories.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void setData(ArrayList<UserStoryObject> newStories) {
		stories = newStories;
	}
	
	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		
		View row = convertView;
		ViewHolder holder = null;
		
		if (row == null) {
			row = inflater.inflate(layoutResourceId, null);
			
			// assign views to holder
			holder = new ViewHolder();
			holder.img = (ImageView) row.findViewById(R.id.imgIcon);
			holder.geoTxt = (TextView) row.findViewById(R.id.geo_txt);
			holder.daysTxt = (TextView) row.findViewById(R.id.days_txt);
			holder.encounterTxt = (TextView) row.findViewById(R.id.num_user_encounters);
			holder.commentTxt = (TextView) row.findViewById(R.id.num_user_comments);
			holder.mediaIcon = (ImageView) row.findViewById(R.id.user_media_icon);
			holder.delete = (ImageButton) row.findViewById(R.id.btn_delete_story);
			row.setTag(holder);
		} else {
			holder = (ViewHolder)row.getTag();
		}

		
		// cache the current object
		final UserStoryObject uso = stories.get(position);
		
		// make list item and button clickable
		// http://stackoverflow.com/questions/6116583/android-listview-custom-adapter-imagebutton
		holder.delete.setFocusable(false);
		holder.delete.setFocusableInTouchMode(false);
		
		// set custom on Click Listener to the delete button--callback to activity
		holder.delete.setOnClickListener(new CustomOnClickListener(mCallback, position));
		
		
		// SET VALUES
		// timestamp
		int daysAgo = handler.getDaysAgo(uso.getTimestamp());
		String s = Global.formatDaysForUI(daysAgo);
		holder.daysTxt.setText(s);
		// perspective image
		Bitmap bitmap = BitmapFactory.decodeFile(uso.getPerspective());
		holder.img.setImageBitmap(bitmap);
		// geocoordinates
		String geo = Global.formatGeoForUI(uso.getLat(), uso.getLng());
		holder.geoTxt.setText(geo);
		// encounters
		holder.encounterTxt.setText(Integer.toString(uso.getEncounters()));
		// comments
		holder.commentTxt.setText(Integer.toString(uso.getComments()));
		// media type
		int mediaType = uso.getMedia();
		Drawable drawable = null;
		Resources res = parent.getResources();
		if(mediaType == Global.IMAGE_CAPTURE) {
			drawable = res.getDrawable(R.drawable.ic_record_photo);
		} else if (mediaType == Global.VIDEO_CAPTURE) {
			drawable = res.getDrawable(R.drawable.ic_record_video);
		} else if (mediaType == Global.AUDIO_CAPTURE) {
			drawable = res.getDrawable(R.drawable.ic_record_audio);
		}
		holder.mediaIcon.setImageDrawable(drawable);
		
		return row;
	}
	
	/**
	 * Cache the layout views for better performance
	 */
	static class ViewHolder {
		ImageView img;
		TextView geoTxt;
		TextView daysTxt;
		TextView encounterTxt;
		TextView commentTxt;
		ImageView mediaIcon;
		ImageButton delete;
		
	}





}


