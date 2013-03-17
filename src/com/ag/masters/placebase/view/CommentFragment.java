package com.ag.masters.placebase.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ag.masters.placebase.R;
import com.ag.masters.placebase.model.DatabaseHelper;
import com.ag.masters.placebase.sqlite.Encounter;
import com.ag.masters.placebase.sqlite.Story;
import com.ag.masters.placebase.sqlite.User;



public class CommentFragment extends Fragment {

	Story story;
	User user;
	Encounter encounter;
	
	DatabaseHelper dbh;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Create a new TextView and set its text to the fragment's section
		// number argument value.
		
		View v = inflater.inflate(R.layout.view_retrieve_comments, container, false);
        return v;
	}
	
	
}
