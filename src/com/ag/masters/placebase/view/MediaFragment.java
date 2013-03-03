package com.ag.masters.placebase.view;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ag.masters.placebase.R;

public class MediaFragment extends Fragment {

	int mediaType;
	
	ImageButton _btnHear;
	ImageButton _btnSee;
	ImageButton _btnSmell;
	ImageButton _btnTaste;
	ImageButton _btnTouch;
	
	
	public MediaFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// inflate the layout
		View v = inflater.inflate(R.layout.view_retrieve_media, container, false);
		
		final ImageButton _btnHear = (ImageButton) v.findViewById(R.id.media_hear);
		final ImageButton _btnSee = (ImageButton) v.findViewById(R.id.media_see);
		final ImageButton _btnSmell = (ImageButton) v.findViewById(R.id.media_smell);
		final ImageButton _btnTaste = (ImageButton) v.findViewById(R.id.media_taste);
		final ImageButton _btnTouch = (ImageButton) v.findViewById(R.id.media_touch);
		
		// subsitute the view stub based on the mediaType
		
        View tv = v.findViewById(R.id.num_comments);
        ((TextView)tv).setText(Integer.toString(getArguments().getInt("mediaType")));
        return v;
	}
	
	/**
	 * GETTERS AND SETTERS
	 * 
	 */
	


	

}
