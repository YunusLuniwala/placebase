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

public class StartActivity extends Activity {

	DatabaseHelper mDatabaseHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		/**
		 * Create database if it does not exist
		 */
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
		
		/**
		 * LOGIN
		 */
		Button btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Intent beginMap = new Intent(StartActivity.this, MapActivity.class);
				// startActivity(beginMap);
				Intent loginActivity = new Intent(StartActivity.this, Login.class);
				startActivity(loginActivity);
			}
		});
		
		/**
		 * NEW USER
		 */
		Button newAccount = (Button) findViewById(R.id.btn_newuser);
		newAccount.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent newUserActivity = new Intent(StartActivity.this, NewUser.class);
				startActivity(newUserActivity);
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
