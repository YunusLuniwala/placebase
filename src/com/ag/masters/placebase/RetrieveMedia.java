package com.ag.masters.placebase;
/**
 * Implements horizontal view paging
 * with PageAdapter and the ActionBar API
 * 
 * http://android-developers.blogspot.com/2011/08/horizontal-view-swiping-with-viewpager.html
 * http://developer.android.com/training/implementing-navigation/lateral.html#tabs
 * http://mobile.tutsplus.com/tutorials/android/android-user-interface-design-horizontal-view-paging/
 * 
 */
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

import com.ag.masters.placebase.sqlite.DatabaseHelper;
import com.ag.masters.placebase.sqlite.Encounter;
import com.ag.masters.placebase.sqlite.Story;
import com.ag.masters.placebase.sqlite.User;
import com.ag.masters.placebase.view.CommentFragment;
import com.ag.masters.placebase.view.MediaFragment;

public class RetrieveMedia extends FragmentActivity implements
		ActionBar.TabListener {
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	// store references to the Fragments in the 
	// main class when they are created
	private MediaFragment mMediaFragment;
	private CommentFragment mCommentFragment;
	
	DatabaseHelper dbh;
	// passed in from MapActivity
	public Story story;
	public User user;
	// determine which viewStub to sub
	private int mediaType = -1;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_retrieve_media);
		
		dbh = new DatabaseHelper(this);
		
		// unpack parcel
		Bundle data = getIntent().getExtras();
		if (data != null) {
			// get the STORY object
			Story tempStory = data.getParcelable("story");
			if (tempStory != null) {
				story = tempStory;
				// set the media type	
				mediaType = story.getMedia();
			}else {
				throw new RuntimeException("RetrieveMedia: story passed was null");
			}
			// get the USER object
			User tempUser = data.getParcelable("user");
			if (tempUser != null) {
				user = tempUser;
			}else {
				throw new RuntimeException("RetrieveMedia: user passed was null");
			}
		}
		
		// Set up the action bar with tabs
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		//Remove title bar (and comment out menu)
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);

		// Create the adapter that will return a fragment for each of the two Fragments
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					/** 
					 * when the view is swiped....
					 */
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
						
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Do not show a menu on media retrieval
		//getMenuInflater().inflate(R.menu.activity_retrieve_image, menu);
		return true;
	}

	/** 
	 * when A tab is selected....
	 */
	@Override
	public void onTabSelected(ActionBar.Tab tab,FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
		switchToSelected(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,FragmentTransaction fragmentTransaction) {
	}

	
	/**
	 * called whenever the fragment is swipped or tabbed to
	 * TODO: save encounter object for selected sense buttons when the user pages away
	 * 
	 * @param adapter position
	 */
	private void switchToSelected(int pos) {
		if (pos == 0) { 
			switchToMedia();
		} else if (pos == 1){ 
			switchToComments();
		}
	}
	
	private void switchToMedia() {
		
	}
	
	private void switchToComments() {
		
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// CALLED ONLY ONCE. DO SETUP HERE!
			// Return the Media or Comments fragment
			// pass mediaType as arg to set ViewStub for the particular media 
			if (position == 0) {
				Fragment fragment = new MediaFragment();
				mMediaFragment = (MediaFragment) fragment;
				return mMediaFragment;
			} else if (position == 1) {
				Fragment fragment = new CommentFragment();
				mCommentFragment = (CommentFragment) fragment;
				return mCommentFragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.story).toUpperCase();
			case 1:
				return getString(R.string.comments).toUpperCase();
			}
			return null;
		}
	}


}
