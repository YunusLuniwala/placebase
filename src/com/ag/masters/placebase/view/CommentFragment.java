package com.ag.masters.placebase.view;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
        
        //call adapter to populate the list with all comments
        populateList(); 
        
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
				
				if(body.matches("")) { // if there is no text entered in the field...
					Toast.makeText(getActivity(), "Please write a comment first", Toast.LENGTH_LONG).show();
				} else { // save the comment to the database
				dbh.openDataBase();
				dbh.createComment(userId, storyId, body, timestamp);
				//dbh.close();
				// reset the comment field
				commentField.setText("");
				// close the keyboard
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
				// refresh data in list
				populateList();
				adapter.notifyDataSetChanged();
				
				Log.i("COMMENT", "Story Id: " + Integer.toString(storyId));
				Log.i("COMMENT", "User Id: " + Integer.toString(userId));
				Log.i("COMMENT", "Comment: " + body);
				Log.i("COMMENT", "Written on: " + timestamp);
				
				}
				
			}
		});
	}
	
	private void populateList() {
		// add a list item for every comment in this list
		// get all the comments for this story
		dbh.openDataBase();
		Cursor cursor = dbh.getAllCommentsForStory(story.getId());

		// the desired columns to be bound
		String[] from = new String[] {
				//dbh.COMMENTS_ID,
				DatabaseHelper.COMMENTS_BODY,
				DatabaseHelper.COMMENTS_TIMESTAMP
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
		
		adapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {
				
				if(aColumnIndex == aCursor.getColumnIndex(DatabaseHelper.COMMENTS_TIMESTAMP)) {
					
					TextView textView = (TextView) aView;
					
					String rawDate = aCursor.getString(aColumnIndex);
					int daysPassed = dateHandler.getDaysAgo(rawDate);
					if (daysPassed == 0) {
						textView.setText("posted today");
					} else {
						textView.setText("posted " + dateHandler.getDaysAgo(rawDate) + " days ago");
					}
					return true;
				}
				
				return false;
			}
		});
		
		// assign adapter to ListView
		commentsList.setAdapter(adapter); 
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//cursor.close();
		dbh.close();
	}

}
