/**
 * Table-specific DBAdapter class
 * Videos
 * 
 * responsible for pulling from the database anything related to this type
 * 
 * @see StackOverFlow answer:
 * http://stackoverflow.com/questions/4063510/multiple-table-sqlite-db-adapters-in-android
 */
package com.ag.masters.placebase.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ImagesDBAdapter {
	// table name
		private static final String DATABASE_TABLE = "images";
		// columns
		public static final String ROW_ID = "_id";
		public static final String STORY = "story";
		public static final String URI = "uri";
		public static final String CAPTION = "caption";

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

		public ImagesDBAdapter(Context context) {
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
		public ImagesDBAdapter open() throws SQLException {
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
		 * @return rowId or -1 if failed
		 */
		public long createImage(int story, int uri, String caption) {
			ContentValues initialValues = new ContentValues();
			initialValues.put(STORY, story);
			initialValues.put(URI, uri);
			initialValues.put(CAPTION, caption);
			return this.mDb.insert(DATABASE_TABLE, null, initialValues);
		}

		/**
		 * Delete record
		 * 
		 * @param rowId
		 * @return true if deleted, false otherwise
		 */
		public boolean deleteImage(long rowId) {
			return this.mDb.delete(DATABASE_TABLE, ROW_ID + "=" + rowId, null) > 0;
		}

		/**
		 * Return a Cursor positioned at the ONE record that matches the given ID
		 * @param rowId
		 * @return
		 * @throws SQLException
		 */
		public Cursor getImage(long rowId) throws SQLException {
			Cursor mCursor = 
					this.mDb.query(true, DATABASE_TABLE, new String[] {}, ROW_ID + "=" + rowId, null, null, null, null, null);
			if(mCursor != null) {
				mCursor.moveToFirst();
			}
			return mCursor;
		}

		/**
		 * Update record
		 * 
		 * 
		 * @return true if successfully updated, false otherwise
		 */
		public boolean updateImage(long rowId, int story, String uri, String caption) {
			ContentValues args = new ContentValues();
			args.put(STORY, story);
			args.put(URI, uri);
			args.put(CAPTION, caption);
			return this.mDb.update(DATABASE_TABLE, args, ROW_ID + "=" + rowId, null) > 0;
		}

}
