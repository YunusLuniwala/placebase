/**
 * Table-specific DBAdapter class
 * Stories
 * 
 * responsible for pulling from the database anything related to this type
 * 
 * @see StackOverFlow answer:
 * http://stackoverflow.com/questions/4063510/multiple-table-sqlite-db-adapters-in-android
 */

package com.ag.masters.placebase.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ag.masters.placebase.sqlite.Story;

public class StoriesDBAdapter {

	// table name
	private static final String DATABASE_TABLE = "stories";
	// columns
	public static final String ROW_ID = "_id";
	public static final String LAT = "lat";
	public static final String LNG = "lng";
	public static final String BEARING = "bearing";
	public static final String MEDIA = "media";
	public static final String HEAR = "hear";
	public static final String SEE = "see";
	public static final String SMELL = "smell";
	public static final String TASTE = "taste";
	public static final String TOUCH = "touch";
	public static final String TIMESTAMP = "timestamp";
	public static final String PERSPECTIVE = "perspective_uri";
	public static final String USER = "user";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private final Context mContext;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DBAdapter.DATABASE_NAME, null, DBAdapter.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {		
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

	/**
	 * Constructor
	 * takes the context to allow the database to be opened/created
	 * 
	 * @param context
	 * the Context within which to work
	 */
	public StoriesDBAdapter(Context context) {
		this.mContext = context;
	}
	
	/**
	 * Open the database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to signal the failure.
	 * 
	 * @return this (self reference, allowing this to be chained in an initialization call)
	 * @throws SQLException (if the database can neither be opened or created)
	 *
	 */
	public StoriesDBAdapter open() throws SQLException {
		this.mDbHelper = new DatabaseHelper(this.mContext);
		this.mDb = this.mDbHelper.getWritableDatabase();
		return this;
	}
	
	/**
     * close 
     * return type: void
     */
    public void close() {
        this.mDbHelper.close();
    }
    
    
    /************************* CRUD OPERATIONS ****************************/
    
    /**
     * Create new record. If successfully created, return the new
     * rowId for that record. Otherwise, return a -1 to indicate failure.
     * 
     * @param lat
     * @param lng
     * @param bearing
     * @param media
     * @param hear
     * @param see
     * @param smell
     * @param taste
     * @param touch
     * @param timestamp
     * @param perspective_uri
     * @param user
	 * 
	 * @return rowId or -1 if failed
     */
    public long createStory(double lat, double lng, float bearing, int media, 
    		int hear, int see, int smell, int taste, int touch,
    		String timestamp, String perspective_uri, int user) {
    	
    	ContentValues initialValues = new ContentValues();
    	initialValues.put(LAT, lat);
    	initialValues.put(LNG, lng);
    	initialValues.put(BEARING, bearing);
    	initialValues.put(MEDIA, media);
    	initialValues.put(HEAR, hear);
    	initialValues.put(SEE, see);
    	initialValues.put(SMELL, smell);
    	initialValues.put(TASTE, taste);
    	initialValues.put(TOUCH, touch);
    	initialValues.put(TIMESTAMP, timestamp);
    	initialValues.put(PERSPECTIVE, perspective_uri);
    	initialValues.put(USER, user);
    	
    	return this.mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    /**
     * Delete single story
     * 
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteStory(long rowId) {
    	return this.mDb.delete(DATABASE_TABLE, ROW_ID + "=" + rowId, null) > 0;
    }
    
    /**
     * Return a Cursor positioned at the ONE record that matches the given ID
     * @param rowId
     * @return
     * @throws SQLException
     */
    public Cursor getStory(long rowId) throws SQLException {
    	
    	Cursor mCursor = 
    			this.mDb.query(true, DATABASE_TABLE, new String[] {}, ROW_ID + "=" + rowId, null, null, null, null, null);
    			if(mCursor != null) {
    				mCursor.moveToFirst();
    			}
    	return mCursor;
    }
    
    /**
     * Update a single story with all parameters
     * 
     * @param rowId
     * @param lat
     * @param lng
     * @param bearing
     * @param media
     * @param hear
     * @param see
     * @param smell
     * @param taste
     * @param touch
     * @param timestamp
     * @param perspective_uri
     * @param user
     * 
     * @return true if successfully updated, false otherwise
     */
    public boolean updateStory(long rowId, double lat, double lng, float bearing, int media, 
    		int hear, int see, int smell, int taste, int touch,
    		String timestamp, String perspective_uri, int user ) {
    		ContentValues args = new ContentValues();
    		args.put(LAT, lat);
    		args.put(LNG, lng);
    		args.put(BEARING, bearing);
    		args.put(MEDIA, media);
    		args.put(HEAR, hear);
    		args.put(SEE, see);
    		args.put(SMELL, smell);
    		args.put(TASTE, taste);
    		args.put(TOUCH, touch);
    		args.put(TIMESTAMP, timestamp);
    		args.put(PERSPECTIVE, perspective_uri);
    		args.put(USER, user);
    	return this.mDb.update(DATABASE_TABLE, args, ROW_ID + "=" + rowId, null) > 0;
    }
    
    
    
    /**
     * Get all stories in the database 
     * and store them in a List of Story objects
     * @return List<Story> 
     */
    public List<Story> getAllStories() throws SQLException {
		List<Story>allStories = new ArrayList<Story>();
		
		// Select All Query
		String selectQuery = "SELECT * FROM " + DATABASE_TABLE;
		
		// open the database
		this.open();
		Cursor cursor = mDb.rawQuery(selectQuery, null);
		
		if(cursor.moveToFirst()) {
			do {
				Story story = new Story();
				story.setId(Integer.parseInt(cursor.getString(0)));
				story.setLat(cursor.getDouble(1));
				story.setLng(cursor.getDouble(2));
				story.setBearing(cursor.getFloat(3));
				story.setMedia(cursor.getInt(4));
				story.setHear(cursor.getInt(5));
				story.setSee(cursor.getInt(6));
				story.setSmell(cursor.getInt(7));
				story.setTaste(cursor.getInt(8));
				story.setTouch(cursor.getInt(9));
				story.setTimestamp(cursor.getString(10));
				story.setPerspectiveUri(cursor.getString(11));
				story.setUser(cursor.getInt(12));
				
				// add story to the list
				allStories.add(story);
				
			} while (cursor.moveToNext());
		}
		
		// close db connection
		cursor.close();
		this.close();
		
		// return story list
		return allStories;
	}
    
    /**
     * Get a count of all stories
     * @return int
     */
    public int getStoryCount() {
    	
    	// Select All Query
    	String selectQuery = "SELECT * FROM " + DATABASE_TABLE;
    			
    	// open the database
    	this.open();
    	Cursor cursor = mDb.rawQuery(selectQuery, null);
    	
    	// close db connection
    	cursor.close();
    	this.close();
    	
    	// return count
    	return cursor.getCount();
    }





}
