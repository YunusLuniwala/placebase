package com.ag.masters.placebase.model;



import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ag.masters.placebase.sqlite.Encounter;
import com.ag.masters.placebase.sqlite.Story;
import com.ag.masters.placebase.sqlite.StoryAudio;
import com.ag.masters.placebase.sqlite.StoryImage;
import com.ag.masters.placebase.sqlite.StoryVideo;
import com.ag.masters.placebase.sqlite.User;

/**
 * 
 * General DatabaseHelper to access databases.
 * Returns a database helper, and attempts to create a database from
 * existing resources in Assets
 * 
 * @see Tutorial by Fluxa for creating own SQLite database in Android using pre-existing tables
 * 		http://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/
 * 
 * Consolidated to combat this error:
 * http://stackoverflow.com/questions/14215204/java-android-sqlite-no-such-table-exists
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	//The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/com.ag.masters.placebase/databases/";
	private static String DB_NAME = "placebase";
	private static int DB_VERSION = 1;

	// Encounter Table info
	private static final String TABLE_STORIES = "stories";
		//column names
		private static final String STORIES_ID = "_id";
		private static final String STORIES_USER = "user";
		private static final String STORIES_MEDIA = "media";
		private static final String STORIES_HEAR = "hear";
		private static final String STORIES_SEE = "see";
		private static final String STORIES_SMELL = "smell";
		private static final String STORIES_TASTE = "taste";
		private static final String STORIES_TOUCH = "touch";
		private static final String STORIES_LAT = "lat";
		private static final String STORIES_LNG = "lng";
		private static final String STORIES_BEARING = "bearing";
		private static final String STORIES_TIMESTAMP = "timestamp";
		private static final String STORIES_PERSPECTIVE = "perspective";
		
	// User Table info
	private static final String TABLE_USERS = "users";
		//column names
		private static final String USERS_ID = "_id";
		private static final String USERS_NAME = "name";
		private static final String USERS_PASSWORD = "password";
		private static final String USERS_DATE = "date";

	//Image Table info
	private static final String TABLE_PHOTOS = "photos";
		//column names
		private static final String PHOTOS_ID = "_id";
		private static final String PHOTOS_STORY = "story";
		private static final String PHOTOS_URI = "uri";
		private static final String PHOTOS_CAPTION = "caption";

	//Video Table info
	private static final String TABLE_VIDEOS = "videos";
		//column names
		private static final String VIDEOS_ID = "_id";
		private static final String VIDEOS_STORY = "story";
		private static final String VIDEOS_URI = "uri";

	//Audio Table info
	private static final String TABLE_AUDIO = "audio";
		//column names
		private static final String AUDIO_ID = "_id";
		private static final String AUDIO_STORY = "story";
		private static final String AUDIO_URI = "uri";
		
	//Comment Table info
	private static final String TABLE_COMMENTS = "comment";
		//column names
		private static final String COMMENTS_ID = "_id";
		private static final String COMMENTS_STORY = "story";
		private static final String COMMENTS_USER = "user";
		private static final String COMMENTS_BODY = "body";
		private static final String COMMENTS_TIMESTAMP = "timestamp";
			
	// Encounter Table info
	private static final String TABLE_ENCOUNTERS = "encounters";
		//column names
		private static final String ENCOUNTERS_ID = "_id";
		private static final String ENCOUNTERS_STORY = "story";
		private static final String ENCOUNTERS_USER = "user";
		private static final String ENCOUNTERS_HEAR = "hear";
		private static final String ENCOUNTERS_SEE = "see";
		private static final String ENCOUNTERS_SMELL = "smell";
		private static final String ENCOUNTERS_TASTE = "taste";
		private static final String ENCOUNTERS_TOUCH = "touch";

	private SQLiteDatabase myDataBase; 
	private final Context myContext;
	
	/**
	 * Constructor
	 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	 * @param context
	 */
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.myContext = context;
	}


	/**
	 * Creates a empty database on the system and rewrites it with your own database.
	 * */
	public void createDataBase() throws IOException{

		boolean dbExist = checkDataBase();
		SQLiteDatabase db_Read = null;
		
		if(dbExist){
			//do nothing - database already exist
		}else{

			//By calling this method an empty database will be created into the default system path
			//of your application so we will be able to overwrite that database with our database.
			db_Read = this.getReadableDatabase();
			db_Read.close();

			try {

				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase(){

		SQLiteDatabase checkDB = null;

		try{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

		}catch(SQLiteException e){

			//database does't exist yet.

		}

		if(checkDB != null){
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}


	/**
	 * Copies your database from your local assets-folder to the just created empty database in the
	 * system folder, from where it can be accessed and handled.
	 * This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException{

		//Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		
		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;
		
		//Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		
		//transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}
		//Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void openDataBase() throws SQLException{
		//Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		myDataBase.close(); // really?
		
	}
	
    public SQLiteDatabase getDatabase(){
    	return myDataBase;
    }
    
	@Override
	public synchronized void close() {
		if(myDataBase != null)
			myDataBase.close();
		super.close();
	}
	


	@Override
	public void onCreate(SQLiteDatabase db) {
		// table pre created
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//table

	}
	
	/**
	 * 
	 * STORIES TABLE
	 * All CRUD operations
	 * 
	 */
	
	// get all stories
    public List<Story> getAllStories() throws SQLException {
		List<Story>allStories = new ArrayList<Story>();
		
		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_STORIES;
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if(cursor.moveToFirst()) {
			do {
				Story story = new Story();
				story.setId(Integer.parseInt(cursor.getString(0)));
				story.setUser(cursor.getInt(1));
				story.setMedia(cursor.getInt(2));
				story.setHear(cursor.getInt(3));
				story.setSee(cursor.getInt(4));
				story.setSmell(cursor.getInt(5));
				story.setTaste(cursor.getInt(6));
				story.setTouch(cursor.getInt(7));
				story.setLat(cursor.getDouble(8));
				story.setLng(cursor.getDouble(9));
				story.setBearing(cursor.getFloat(10));
				story.setTimestamp(cursor.getString(11));
				story.setPerspectiveUri(cursor.getString(12));
				
				// add story to the list
				allStories.add(story);
				
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		db.close();
		
		return allStories;
	}

    // create new story
    public void createStory(Story story) {
    	SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(STORIES_USER, story.getUser()); 
		values.put(STORIES_MEDIA, story.getMedia()); 
		values.put(STORIES_HEAR, story.getHear());
		values.put(STORIES_SEE, story.getSee());
		values.put(STORIES_SMELL, story.getSmell());
		values.put(STORIES_TASTE, story.getTaste());
		values.put(STORIES_TOUCH, story.getTouch());
		values.put(STORIES_LAT, story.getLat());
		values.put(STORIES_LNG, story.getLng());
		values.put(STORIES_BEARING, story.getBearing());
		values.put(STORIES_TIMESTAMP, story.getTimestamp());
		values.put(STORIES_PERSPECTIVE, story.getPerspectiveUri());
		
		// insert a new row
		db.insert(TABLE_STORIES, null, values);
		db.close();
    }

    
    /** 
     * Return the story id from a timestamp
     * @param timestamp
     * @return Story _id
     */
    public int getStoryId(String timestamp) {
    	SQLiteDatabase db = this.getReadableDatabase();
    	int storyId;
		Cursor cursor = db.query(TABLE_STORIES, new String[] {STORIES_ID, STORIES_TIMESTAMP}, STORIES_TIMESTAMP + "=?", 
				new String[]{String.valueOf(timestamp)}, null, null, null);
		if (cursor.moveToFirst()) {
			storyId = cursor.getInt(0);
		} else {
			storyId = -1; // fail to return story id
		}
		cursor.close();
		db.close();
		return storyId;
    }
    
    
	/**
	 * 
	 * IMAGE TABLE
	 * All CRUD operations
	 * 
	 */
    
    // create one StoryImage
    public void createStoryImage(StoryImage image) {
    	SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
	    values.put(PHOTOS_STORY, image.getStory()); 
	    values.put(PHOTOS_URI, image.getUri());
	    values.put(PHOTOS_CAPTION, image.getCaption());

	    Log.d("Inserting...", "Inserting into " + TABLE_PHOTOS);
	    db.insert(TABLE_PHOTOS, null, values);

	    db.close();
    }

    // return one StoryImage from Story _id
    public StoryImage getStoryImage(int storyId) {
    	SQLiteDatabase db = this.getReadableDatabase();

    	Cursor cursor = db.query(TABLE_PHOTOS, null , PHOTOS_STORY + "=? ", 
    			new String []{ String.valueOf(storyId)}, null, null, null);

    	if(cursor.moveToFirst()){
    		StoryImage image = new StoryImage(
    				Integer.parseInt(cursor.getString(0)),
    				cursor.getInt(1),
    				cursor.getString(2),
    				cursor.getString(3));
    		cursor.close();
    		db.close();
    		return image;
    	} else {
    		cursor.close();
    		db.close();
    		return null;
    	}	
    }
    
	/**
	 * 
	 * VIDEO TABLE
	 * All CRUD operations
	 * 
	 */
    
    // create one Video
    public void createStoryVideo(StoryVideo video) {
    	SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
	    values.put(VIDEOS_STORY, video.getStory()); 
	    values.put(VIDEOS_URI, video.getUri());
	    
	    Log.d("Inserting...", "Inserting into " + TABLE_VIDEOS);
	    db.insert(TABLE_VIDEOS, null, values);
		
		db.close();
    }
    
    
    // return one Video from Story _id
    public StoryVideo getStoryVideo(int storyId) {
    	SQLiteDatabase db = this.getReadableDatabase();
    	
    	Cursor cursor = db.query(TABLE_VIDEOS, null , VIDEOS_STORY + "=? ", 
				new String []{ String.valueOf(storyId)}, null, null, null);
    	
    	if(cursor.moveToFirst()){
    	StoryVideo video = new StoryVideo(
    			Integer.parseInt(cursor.getString(0)),
    			cursor.getInt(1),
    			cursor.getString(2));
    		cursor.close();
    		db.close();
    		return video;
    	} else {
    		cursor.close();
        	db.close();
        	return null;
    	}	
    }

	/**
	 * 
	 * AUDIO TABLE
	 * All CRUD operations
	 * 
	 */
    // create one StoryAudio row
    public void createStoryAudio(StoryAudio audio) {
    	SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
	    values.put(AUDIO_STORY, audio.getStory()); 
	    values.put(AUDIO_URI, audio.getUri());
	    
	    Log.d("Inserting...", "Inserting into " + TABLE_AUDIO);
	    db.insert(TABLE_AUDIO, null, values);

	    db.close();
    }

    // return one StoryAudio from Story _id
    public StoryAudio getStoryAudio(int storyId) {
    	SQLiteDatabase db = this.getReadableDatabase();

    	Cursor cursor = db.query(TABLE_AUDIO, null , AUDIO_STORY + "=? ", 
    			new String []{ String.valueOf(storyId)}, null, null, null);

    	if(cursor.moveToFirst()){
    		StoryAudio audio = new StoryAudio(
    				Integer.parseInt(cursor.getString(0)),
    				cursor.getInt(1),
    				cursor.getString(2));
    		cursor.close();
    		db.close();
    		return audio;
    	} else {
    		cursor.close();
    		db.close();
    		return null;
    	}	
    }


    /**
 	 * 
 	 * ENCOUNTER TABLE
 	 * All CRUD operations
 	 * 
 	 */   
    
  /**
   * Returns a prior Encounter if the user has one at this place
   * @param storyId
   * @param userId
   * @return
   */
   public Encounter getUsersPriorEncounter(int storyId, int userId) {
	   
	   Encounter pE;
	 
	   SQLiteDatabase db = this.getReadableDatabase();
	   
	   Cursor cursor = db.query(TABLE_ENCOUNTERS, null, 
			   ENCOUNTERS_USER + "=? AND " + ENCOUNTERS_STORY + "=?", new String[]{String.valueOf(userId), String.valueOf(storyId)}, null, null, null);
		if (cursor.moveToFirst()) {
			pE = new Encounter(Integer.parseInt(cursor.getString(0)), cursor.getInt(1), cursor.getInt(2),
					cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), 
					cursor.getInt(7));
			Log.i("Previous Encounter Fetched: ", "Previous History User: " + pE.getUser());
		} else {
			Log.i("ENCOUNTER DB", "user has no prior encounter here");
			 cursor.close();
			 db.close();
			 return null;
		}

		cursor.close();
		db.close();
		return pE;
   }

   // update Encounter
   public void updateEncounter(Encounter e) {
	   SQLiteDatabase db = this.getWritableDatabase();

	   ContentValues values = new ContentValues();
	   values.put(ENCOUNTERS_STORY, e.getStory());
	   values.put(ENCOUNTERS_HEAR, e.getHear());
	   values.put(ENCOUNTERS_SEE, e.getSee());
	   values.put(ENCOUNTERS_SMELL, e.getSmell());
	   values.put(ENCOUNTERS_TASTE, e.getTaste());
	   values.put(ENCOUNTERS_TOUCH, e.getTouch());

	   //update row
	   db.update(TABLE_ENCOUNTERS, values, ENCOUNTERS_USER + " = ?", 
			   new String[] { String.valueOf(e.getUser())});
	   db.close();
   }

   // Add new Encounter
   public void createEncounter(Encounter e) {
	   SQLiteDatabase db = this.getWritableDatabase();
	   
	   ContentValues values = new ContentValues();
	   values.put(ENCOUNTERS_USER, e.getUser());
	   values.put(ENCOUNTERS_STORY, e.getStory());
	   values.put(ENCOUNTERS_HEAR, e.getHear());
	   values.put(ENCOUNTERS_SEE, e.getSee());
	   values.put(ENCOUNTERS_SMELL, e.getSmell());
	   values.put(ENCOUNTERS_TASTE, e.getTaste());
	   values.put(ENCOUNTERS_TOUCH, e.getTouch());

	   // insert a new row
	   db.insert(TABLE_ENCOUNTERS, null, values);
	   db.close();
   }

   // return number of Encounter for a Story
   public int getEncounterCountForStory(int storyId){

	   int num = 0;

	   SQLiteDatabase db = this.getReadableDatabase();
	   Cursor cursor = db.query(TABLE_ENCOUNTERS, null, ENCOUNTERS_STORY  + "=?", 
			   new String []{ String.valueOf(storyId)}, null, null, null);

	   num = cursor.getCount();
	   cursor.close();
	   db.close();
	    
       return num;
	}

    
   /**
	 * 
	 * USER TABLE
	 * All CRUD operations
	 * 
	 */
   // new User
    public void addUser(User user) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	ContentValues values = new ContentValues();
    	values.put(USERS_NAME, user.getName());
    	values.put(USERS_PASSWORD, user.getPassword());
    	values.put(USERS_DATE, user.getDate());
    	
    	// insert a new row
    	db.insert(TABLE_USERS, null, values);
    	db.close();
    }
    
    // get username from userId
    public String getUsernameOfAuthor(int userId) {
    	String username;
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.query(TABLE_USERS, new String[] {USERS_ID, USERS_NAME}, USERS_ID + "=?", 
				new String[]{String.valueOf(userId)}, null, null, null);
		if (cursor.moveToFirst()) {
			username = cursor.getString(1);
		} else {
			username = null; // fail to return story id
		}
    	
    	cursor.close();
    	db.close();
    	return username;
    }
    
    // return User object from name
	public User getUser(String username) {
		SQLiteDatabase db = this.getWritableDatabase();
		User user;
		Cursor cursor = db.query(TABLE_USERS, new String[] {USERS_ID, USERS_NAME, USERS_PASSWORD, USERS_DATE}, USERS_NAME + "=?", 
				new String[]{String.valueOf(username)}, null, null, null);
		if (cursor.moveToFirst()) {
			user = new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3));
		} else {
			user = new User();
			user.setId(0);
			user.setName(null);
			user.setPassword(null);
			user.setDate("0");
		}
		cursor.close();
		db.close();
		return user;
	}
	
	// update User's login date
	public int updateUserLoginDate(User user) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(USERS_DATE, user.getDate());
		
		//db.close();
		Log.i("DATABASE", "user login date updated");
		return db.update(TABLE_USERS, values, USERS_NAME + "=?", new String[]{String.valueOf(user.getDate())});
	}
    

}
