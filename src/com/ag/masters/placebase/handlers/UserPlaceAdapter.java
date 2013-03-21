/**
 * extend the ArrayAdapter to bind story objects to a list array
 * for user places
 */

package com.ag.masters.placebase.handlers;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ag.masters.placebase.R;
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
			
			row.setTag(holder);

		} else {
			holder = (ViewHolder)row.getTag();
		}

		// cache the current object
		UserStoryObject uso = stories.get(position);
		
		// set values
		int daysAgo = handler.getDaysAgo(uso.getTimestamp());
		if(daysAgo > 0) {
			String s = Integer.toString(daysAgo) + " days ago";
			holder.daysTxt.setText(s);		
		} else {
			holder.daysTxt.setText("today");
		}
		

		Bitmap bitmap = BitmapFactory.decodeFile(uso.getPerspective());
		holder.img.setImageBitmap(bitmap);
		
		
		holder.geoTxt.setText("(" + Double.toString(uso.getLat()) + ", " + Double.toString(uso.getLng()) + ")");
		holder.encounterTxt.setText(Integer.toString(uso.getEncounters()));
		holder.commentTxt.setText(Integer.toString(uso.getComments()));
		
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
		
	}





}


