package com.ag.masters.placebase.handlers;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;

import com.ag.masters.placebase.model.DatabaseHelper;
import com.ag.masters.placebase.sqlite.User;


/**
 * 
 * UserActivity
 * 
 * Handles all of the interactions between the
 * user table, and NewUser/Login activities
 * 
 * Should be called from startActivityForResult()
 * Accepts request code parameters, and based upon that parameter
 * determines whether to Add Account or checkAndFetchAccount
 * 
 *
 */

public class UserActivity extends Activity{
	
	private static int ADD_ACCOUNT = 1;
	private static int GET_ACCOUNT = 2;
	private static String BAD_PASSWORD = "bad";
	
	private static DatabaseHelper dbh;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//dbh = startDatabase();
		dbh = new DatabaseHelper(this);
		
		// Retrieve information from the passed Bundle to
		// get user information and display it
		Bundle data = getIntent().getExtras();
		if (data == null) {
			return;
		} else {
			int requestCode = data.getInt("requestCode");
			String username = data.getString("username");
			String password = data.getString("password");
			String date = data.getString("date");
			
			if(requestCode == ADD_ACCOUNT){
				if (username != null && password != null) {
					// fetch the account that was created
					String name = createUser(username, password, date);
					
					// start an intent to return to create account activity
					Intent accountCreated = new Intent();
					
					//pass the account back to the original activity
					User justCreated = checkAndFetchUser(username,password);
					
					// attach integer to intent					
					accountCreated.putExtra("username", name);
					accountCreated.putExtra("user", justCreated);
										
					// return to original activity
					setResult(RESULT_OK, accountCreated);
					finish();
				} else{
					Intent accountNotCreated = new Intent();
					setResult(RESULT_CANCELED, accountNotCreated);
					finish();
				}
			} else if(requestCode == GET_ACCOUNT){
				if (username !=null && password !=null){
					//check if the user/password combo exists in db
					User userCheck = checkAndFetchUser(username,password);
					if(userCheck != null){
						Log.d("AccountActivity", "fetched name: " + userCheck.getName());
						if(userCheck.getPassword() != BAD_PASSWORD){
							// if the password is correct, and username is correct
							// start activity with the correct password
							Log.d("AccountActivity" , "UserCheck's username: " + userCheck.getName());
							
							Intent userFetched = new Intent();
							userFetched.putExtra("username", userCheck.getName());
							userFetched.putExtra("user", userCheck);
							setResult(RESULT_OK, userFetched);
							finish();
						} else{
							// if the account returns a bad password, start
							// an intent to the main activity
							String e = "Account password doesn't match. Please enter another password.";
							Intent accountNotFetched = new Intent();
							accountNotFetched.putExtra("Fail", e);
							setResult(RESULT_CANCELED, accountNotFetched);
							finish();
						}
					} else{
						// if the account returns null, start
						// an intent to the main activity
						String e = "Account doesn't exist.";
						Intent accountNotFetched = new Intent();
						accountNotFetched.putExtra("Fail", e);
						setResult(RESULT_CANCELED, accountNotFetched);
						finish();
					}
				}
			}
		}
	}

	/**
	 * Queries the database for the requested account If the name doesn't exist
	 * then write the account into the database. If name matches another name,
	 * then return "0";
	 * 
	 * @param username
	 * @param password
	 * @return String username
	 */
	public String createUser(String username, String password, String date) {
		//dbh = startDatabase();
		final String NAME_EXISTS = "0";
		
		String name = null;
		
		User accountExists = dbh.getUser(username);

		if (accountExists.getName() != null) {
			Log.d("Name is : ", NAME_EXISTS);
			dbh.close();
			return NAME_EXISTS;
		} else {
			name = username;
			Log.d("Insert: ", "Inserting ..");
			dbh.addUser(new User(name, password, date));
			dbh.close();
		}
		return name;
		
	}
	
	
	/**
	 * 
	 * Method checks for existence of the user. If the 
	 * user exists in the database, then return User type.
	 * If only username exists, but password was wrong, return
	 * BAD_PASSWORD int. Otherwise, return null.
	 * 
	 * @param username
	 * @param password
	 * @return User
	 */
	public User checkAndFetchUser(String username, String password){
		//dbh = startDatabase();
		User userExists = dbh.getUser(username);
		String n = userExists.getName();
		String p = userExists.getPassword();
		
		// if name is not null and equals username
		if(n != null){
			//log that name is in db
			Log.d("Name exists in database: ", username);
			
			//check if password matches
			if(p.equals(password)){
				//return the whole account
				return userExists;
			} else {
				//return bad password
				userExists.setPassword(BAD_PASSWORD);
				return userExists;
			}
		} 
		dbh.close();
		return userExists=null;
	}
	
	/**
	 * Convenience method for creating a database helper
	 * or initializing the database
	 * 
	 * @return AccountDatabaseHelper
	 */
	/*public DatabaseHelper startDatabase(){
		dbh = new DatabaseHelper(this);
		
		try {
			dbh.createDataBase();
		} catch (IOException ioe) {
			throw new Error(
					"Unable to create database, or db has been created already");
		}
		// OPEN THE DATABASE
		try {
			//dbh.close();
			dbh.openDataBase();
		} catch (SQLException sqle) {
			throw sqle;
		}
		
		return dbh;
	}
	*/
	
	@Override
	protected void onPause(){
		super.onPause();
		//dbh.close();
	}
	
}
