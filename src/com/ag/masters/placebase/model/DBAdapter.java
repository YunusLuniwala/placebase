/**
 * Main DBAdapter class 
 * Responsible for creating all tables in a single db;
 * 
 * @see StackOverFlow answer:
 * http://stackoverflow.com/questions/4063510/multiple-table-sqlite-db-adapters-in-android 
 * 
 * @tutorial
 * For All CRUD Operations in one table (a slightly different approach)
 * http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
 */

package com.ag.masters.placebase.model;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBAdapter {

	// Static variables
	public static final String DATABASE_NAME = "placebase.db"; //$NON-NLS-1$
	public static final int DATABASE_VERSION = 1;
	
	// Prepare Create statements for each table
	private static final String CREATE_TABLE_STORIES = 
			"CREATE TABLE stories (_id INTEGER PRIMARY KEY AUTOINCREMENT, "//$NON-NLS-1$
			+ StoriesDBAdapter.MEDIA + " INT,"//$NON-NLS-1$
			+ StoriesDBAdapter.USER + " INT,"//$NON-NLS-1$
			+ StoriesDBAdapter.HEAR + " INT,"//$NON-NLS-1$
			+ StoriesDBAdapter.SEE + " INT,"//$NON-NLS-1$
			+ StoriesDBAdapter.SMELL + " INT,"//$NON-NLS-1$
			+ StoriesDBAdapter.TASTE + " INT,"//$NON-NLS-1$
			+ StoriesDBAdapter.TOUCH + " INT,"//$NON-NLS-1$
			+ StoriesDBAdapter.PERSPECTIVE + " TEXT,"//$NON-NLS-1$
			+ StoriesDBAdapter.LAT + " REAL,"//$NON-NLS-1$
			+ StoriesDBAdapter.LNG + " REAL,"//$NON-NLS-1$
			+ StoriesDBAdapter.BEARING + " REAL,"//$NON-NLS-1$
			+ StoriesDBAdapter.TIMESTAMP + " TEXT" + ");";//$NON-NLS-1$

	private static final String CREATE_TABLE_VIDEOS = 
			"CREATE TABLE videos (_id INTEGER PRIMARY KEY AUTOINCREMENT, "//$NON-NLS-1$
			+ VideosDBAdapter.STORY + " INT,"//$NON-NLS-1$
			+ VideosDBAdapter.URI + " TEXT" + ");";//$NON-NLS-1$;

	private static final String CREATE_TABLE_AUDIO = 
			"CREATE TABLE audio (_id INTEGER PRIMARY KEY AUTOINCREMENT, "//$NON-NLS-1$
			+ AudioDBAdapter.STORY + " INT,"//$NON-NLS-1$
			+ AudioDBAdapter.URI + " TEXT" + ");";//$NON-NLS-1$;

	private static final String CREATE_TABLE_IMAGES = 
			"CREATE TABLE videos (_id INTEGER PRIMARY KEY AUTOINCREMENT, "//$NON-NLS-1$
			+ ImagesDBAdapter.STORY + " INT,"//$NON-NLS-1$
			+ ImagesDBAdapter.URI + " TEXT,"//$NON-NLS-1$
			+ ImagesDBAdapter.CAPTION + " TEXT" + ");";//$NON-NLS-1$;

	private static final String CREATE_TABLE_USERS = 
			"CREATE TABLE users (_id INTEGER PRIMARY KEY AUTOINCREMENT, "//$NON-NLS-1$
			+ UsersDBAdapter.NAME + " TEXT" + ");";
	
			
	//TODO: flesh out DB table CREATE statements
	/*
	private static final String CREATE_TABLE_ENCOUNTERS = "";
	private static final String CREATE_TABLE_COMMENTS = "";
	*/

	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	/**
	 * Constructor
	 * @param context
	 */
	public DBAdapter(Context context) {
		this.context = context;
		this.DBHelper = new DatabaseHelper(this.context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			/** 
			 * We can only run one SQL query at a time.
			 * Call for every table.
			 * http://androidforbeginners.blogspot.com/2010/01/creating-multiple-sqlite-database.html
			 *  
			 *  
			 *  			db.execSQL(CREATE_TABLE_COMMENTS);
							db.execSQL(CREATE_TABLE_ENCOUNTERS);
			
			 */
			db.execSQL(CREATE_TABLE_STORIES);
			db.execSQL(CREATE_TABLE_VIDEOS);
			db.execSQL(CREATE_TABLE_AUDIO);
			db.execSQL(CREATE_TABLE_IMAGES);
			db.execSQL(CREATE_TABLE_USERS);
			db.execSQL(CREATE_TABLE_USERS);
			
			Log.i("SQL", "Placebase SQL tables created");
		}

		private Context getApplicationContext() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// add any table modifications here
		}

	}
	
	/**
	 * open the database
	 * @returns this
	 * @throws SQLException
	 * return type: DBdapter
	 */
	public DBAdapter open() throws SQLException {
		this.db = this.DBHelper.getWritableDatabase();
		return this;
	}
	/**
     * close the database 
     * return type: void
     */
    public void close() 
    {
        this.DBHelper.close();
    }
}
