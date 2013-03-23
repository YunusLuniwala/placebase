package com.ag.masters.placebase;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ag.masters.placebase.model.Global;
import com.ag.masters.placebase.sqlite.DatabaseHelper;

public class StartActivity extends Activity {

	DatabaseHelper mDatabaseHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		/**
		 * Create database if it does not exist
		 * (moved to AccountActivity)
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
		
		// if there is a user logged in
		// skip straight to the map screen
		SharedPreferences settings = getSharedPreferences(Global.PREFS, 0);
		int tempUser = settings.getInt("user", -1);
		if(tempUser != -1) {
			Intent intent = new Intent(this, MapActivity.class);
			startActivity(intent);
			finish();
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
				loginActivity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(loginActivity);
				finish();
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
				newUserActivity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(newUserActivity);
				finish();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}


}
