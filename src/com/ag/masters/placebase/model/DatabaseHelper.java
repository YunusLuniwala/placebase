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

import com.ag.masters.placebase.sqlite.Story;
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

		if(dbExist){
			//do nothing - database already exist
		}else{

			//By calling this method an empty database will be created into the default system path
			//of your application so we will be able to overwrite that database with our database.
			this.getReadableDatabase();

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
	}

	@Override
	public synchronized void close() {
		if(myDataBase != null)
			myDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 
	 * STORIES TABLE
	 * All CRUD operations
	 * 
	 */
	
	
	 /**
     * Get all stories in the database 
     * 
     * @return List<Story> 
     */
    public List<Story> getAllStories() throws SQLException {
		List<Story>allStories = new ArrayList<Story>();
		
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_STORIES;
		
		// open the database
		this.openDataBase();
		Cursor cursor = myDataBase.rawQuery(selectQuery, null);
		
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
		
		// close db connection
		cursor.close();
		this.close();
		
		// return story list
		return allStories;
	}



    
    
    
    
	/**
	 * 
	 * IMAGE TABLE
	 * All CRUD operations
	 * 
	 */
    
	/**
	 * 
	 * VIDEO TABLE
	 * All CRUD operations
	 * 
	 */

	/**
	 * 
	 * AUDIO TABLE
	 * All CRUD operations
	 * 
	 */
    
    /**
	 * 
	 * USER TABLE
	 * All CRUD operations
	 * 
	 */
    

    
    /**
     * Add new user
     * @param username
     * @return
     * 
     */
    public void addUser(User user) {
    	this.openDataBase(); // setValues of myDataBase
    	myDataBase = this.getWritableDatabase(); // and get writable version of db
    	ContentValues values = new ContentValues();
    	values.put(USERS_NAME, user.getName());
    	values.put(USERS_PASSWORD, user.getPassword());
    	values.put(USERS_DATE, user.getDate());
    	
    	// insert a new row
    	myDataBase.insert(TABLE_USERS, null, values);
    	myDataBase.close();
    }
    
    /**
     * Return a single user via username
     * @param username
     * @return User
     */
	public User getUser(String username) {
		this.openDataBase();
		User user;
		Cursor cursor = myDataBase.query(TABLE_USERS, new String[] {USERS_ID, USERS_NAME, USERS_PASSWORD, USERS_DATE}, USERS_NAME + "=?", 
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
		myDataBase.close();
		return user;
	}
    

}
