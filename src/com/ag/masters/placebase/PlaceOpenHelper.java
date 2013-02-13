package com.ag.masters.placebase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlaceOpenHelper extends SQLiteOpenHelper {
	
	// DATABASE
	public static final String DATABASE_NAME = "placebase.db";
	public static final int DATABASE_VERSION = 1;
	
	// TABLES
	private static final String PLACE_TABLE_NAME = "places";
	// add rows and columns
	public static final String PLAVE_COLUMN_ID = "_id";
	
	private static final String PLACE_TABLE_CREATE = "";
	
	PlaceOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// called when the database is created for the first time.
		// This is where the the creation of tables and initial population of tables should happen
		db.execSQL(PLACE_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
