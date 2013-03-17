package com.ag.masters.placebase;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ag.masters.placebase.handlers.DateHandler;
import com.ag.masters.placebase.handlers.UserActivity;
import com.ag.masters.placebase.model.DatabaseHelper;
import com.ag.masters.placebase.sqlite.User;

public class Login extends Activity {

	Button btn_login;
	EditText username;
	EditText password;
	
	private static int GET_ACCOUNT = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	
		
		
		username = (EditText) findViewById(R.id.form_username);
		password = (EditText) findViewById(R.id.form_password);
		btn_login = (Button) findViewById(R.id.btn_confirmlogin);
		btn_login.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// get the entered user information
				String enteredName = username.getText().toString();
				String enteredPwd = password.getText().toString();
				
				// first check if there are illegal characters in
				// the entered information
				Intent startAddAccount = new Intent(v.getContext(),UserActivity.class);
				
				//bundle up the information
				Bundle userInfo = new Bundle();
				userInfo.putInt("requestCode", GET_ACCOUNT);
				userInfo.putString("username",enteredName);
				userInfo.putString("password",enteredPwd);
				
				//give the bundle to the intent
				startAddAccount.putExtras(userInfo);
				
				startActivityForResult(startAddAccount,GET_ACCOUNT);
				
			}
		});	
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode==GET_ACCOUNT){
			switch(resultCode){
				case RESULT_OK:
					// start setup people activity
					// Get the user name from the intent
					String name = data.getStringExtra("username");
					User user = data.getParcelableExtra("user");
				    if(name.equals(null)){
				    	String error = "Sorry, we couldn't log you in! Please try again.";
				    	Toast loginError = Toast.makeText(Login.this , error , Toast.LENGTH_LONG);
				    	loginError.show();
				    } else{
				    	//rest date of last login on date object
				    	DateHandler dateHandler = new DateHandler();
				    	String date = dateHandler.getCurrentTimeAsString();
				    	user.setDate(date);
				    	
				    	Intent goToMap= new Intent(this,MapActivity.class);
				    	
				    	// add the account object
				    	goToMap.putExtra("user", user);
				    	startActivity(goToMap);
				    	finish();
				    }
					break;
				case RESULT_CANCELED:
					String fail = data.getStringExtra("Fail");
					Toast failLogin = Toast.makeText(Login.this , fail, Toast.LENGTH_LONG);
			    	failLogin.show();
					break;
				default:
					String noInfo = "Couldn't get your information. Please try again!";
					Toast noInformation = Toast.makeText(Login.this , noInfo, Toast.LENGTH_LONG);
			    	noInformation.show();
			} //end switch
		} // end if requestCode
	} // end onActivityResult

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent backToSplash = new Intent(this, StartActivity.class);
		startActivity(backToSplash);
	}
	
	
}
