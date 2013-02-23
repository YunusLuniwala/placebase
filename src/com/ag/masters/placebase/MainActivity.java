package com.ag.masters.placebase;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ag.masters.placebase.model.DatabaseHelper;

public class MainActivity extends Activity {

	DatabaseHelper mDatabaseHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// set up database on splash screen
		
		//DBAdapter db = new DBAdapter(this);
		
		mDatabaseHelper = new DatabaseHelper(this);
		try {
			mDatabaseHelper.createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}
		try {
			mDatabaseHelper.openDataBase();
		}catch(SQLException sqle){
			throw sqle;
		}
		
		Button startBtn = (Button) findViewById(R.id.startapp_btn);
		startBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent beginMap = new Intent(MainActivity.this, MapActivity.class);
				startActivity(beginMap);
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	

}
