package com.ag.masters.placebase.view;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ag.masters.placebase.R;
import com.ag.masters.placebase.RetrieveMedia;
import com.ag.masters.placebase.handlers.DateHandler;
import com.ag.masters.placebase.model.DatabaseHelper;
import com.ag.masters.placebase.sqlite.Encounter;
import com.ag.masters.placebase.sqlite.Story;
import com.ag.masters.placebase.sqlite.User;



public class CommentFragment extends Fragment {

	Story story;
	User user;
	Encounter encounter;
	
	DatabaseHelper dbh;
	Cursor cursor;
	
	DateHandler dateHandler;
	
	Button submit;
	EditText commentField;
	ListView commentsList;
	
	private SimpleCursorAdapter adapter;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dbh = new DatabaseHelper(getActivity());
		dateHandler = new DateHandler();
		
		// get parcel from FragmentActivity
		FragmentActivity fa = (RetrieveMedia)getActivity();
		Intent launchingIntent = fa.getIntent();
		Bundle data = launchingIntent.getExtras();

		story = data.getParcelable("story");
		user = data.getParcelable("user");
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// define views
		View v = inflater.inflate(R.layout.view_retrieve_comments, container, false);
		submit = (Button) v.findViewById(R.id.btn_submitComment);
		commentField = (EditText) v.findViewById(R.id.commentField);
        commentsList = (ListView) v.findViewById(R.id.list);
        
		// get all the comments for this story
		dbh.openDataBase();
		Cursor cursor = dbh.getAllCommentsForStory(story.getId());
		
		// the desired columns to be bound
		String[] from = new String[] {
			//dbh.COMMENTS_ID,
			dbh.COMMENTS_BODY,
			dbh.COMMENTS_TIMESTAMP
		};
		// the XML defined view that the data will be bound to
		int[] to = new int[] {
			R.id.commentBody,
			R.id.commentTimestamp
		};
		// create the adapter using the cursor pointing to the desired data and layout information
		adapter = new SimpleCursorAdapter(
				getActivity(), 
				R.layout.listitem_comment, 
				cursor, 
				from, 
				to, 
				0);
		// assign adapter to ListView
		commentsList.setAdapter(adapter); 
		
		
		// IMPORTANT close the cursor and the database

		
		
		
		
        populateList(); // call adapter to populate the list with all comments
        initSubmitButtonListener();

        return v;
	}
	
	private void initSubmitButtonListener() {
		
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				int storyId = story.getId();
				int userId = user.getId();
				String timestamp = dateHandler.getCurrentTimeAsString();
				String body = commentField.getText().toString();
				
				Log.i("COMMENT", "Story Id: " + Integer.toString(storyId));
				Log.i("COMMENT", "User Id: " + Integer.toString(userId));
				Log.i("COMMENT", "Comment: " + body);
				Log.i("COMMENT", "Written on: " + timestamp);
				
				// save the comment to the database
				dbh.openDataBase();
				dbh.createComment(userId, storyId, body, timestamp);
				//dbh.close();
				
				clearText();
				
				LinearLayout commentsList = (LinearLayout) v.findViewById(R.id.comments);
				
			}
		});
	}
	
	private void populateList() {
		// add a list item for every comment in this list
		
	}
	
	private void clearText() {
		commentField.setText("");
	
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//cursor.close();
		dbh.close();
	}
	
}
