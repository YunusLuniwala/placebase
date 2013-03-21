/**
 * UserPlaceAdapter.class
 * 
 * extends the ArrayAdapter to bind story objects to a list array
 * for user places
 * 
 * formats and populates the fields
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ag.masters.placebase.R;
import com.ag.masters.placebase.model.Global;
import com.ag.masters.placebase.model.UserStoryObject;


public class UserPlaceAdapter extends BaseAdapter {
	
	private static ArrayList<UserStoryObject> stories;

	private LayoutInflater inflater;
	private int layoutResourceId;
	
	DateHandler handler;


	public UserPlaceAdapter(Context context, ArrayList<UserStoryObject> data, int layout) {
		
		stories = data;
		inflater = LayoutInflater.from(context);
		layoutResourceId = layout;
		
		handler = new DateHandler();

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
	
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

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
		//holder.delete.setClickable(true);
		
		holder.delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.v("BUTTON", "button clicked for pos " + Integer.toString(uso.getId()));
				deleteItem(uso.getId());
			}
		});
		
		// SET VALUES
		// timestamp
		int daysAgo = handler.getDaysAgo(uso.getTimestamp());
		if(daysAgo > 0) {
			String s = Integer.toString(daysAgo) + " days ago";
			holder.daysTxt.setText(s);		
		} else {
			holder.daysTxt.setText("today");
		}
		// perspective image
		Bitmap bitmap = BitmapFactory.decodeFile(uso.getPerspective());
		holder.img.setImageBitmap(bitmap);
		// geocoordinates
		holder.geoTxt.setText("(" + Double.toString(uso.getLat()) + ", " + Double.toString(uso.getLng()) + ")");
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
	
	private void deleteItem(int id) {
		// run database function to delete the item
		
		// and remove it from the list (or refresh the list, or hide the row or whatever)
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


