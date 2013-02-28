package com.ag.masters.placebase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ag.masters.placebase.handlers.DateHandler;
import com.ag.masters.placebase.handlers.UserActivity;
import com.ag.masters.placebase.sqlite.User;

public class NewUser extends Activity {
	
	Button btn_start;
	EditText username;
	EditText password;
	
	private static int ADD_ACCOUNT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_user);
		
		username = (EditText) findViewById(R.id.new_user_name);
		password = (EditText) findViewById(R.id.new_user_password);
		btn_start = (Button) findViewById(R.id.btn_new_user);
		btn_start.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// get the entered user information
				String enteredName = username.getText().toString();
				String enteredPwd = password.getText().toString();
				
				
				DateHandler dateHandler = new DateHandler();
				// set Last login to current time
				String date = dateHandler.getCurrentTimeAsString();
				
				// first check if there are illegal characters in
				// the entered information
				Intent startAddAccount = new Intent(v.getContext(),UserActivity.class);
				
				//bundle up the information
				Bundle userInfo = new Bundle();
				userInfo.putInt("requestCode", ADD_ACCOUNT);
				userInfo.putString("username",enteredName);
				userInfo.putString("password",enteredPwd);
				userInfo.putString("date", date);
				
				//give the bundle to the intent
				startAddAccount.putExtras(userInfo);
				
				startActivityForResult(startAddAccount,ADD_ACCOUNT);
			}
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode==ADD_ACCOUNT){
			switch(resultCode){
				case RESULT_OK:
					// start setup people activity
					// Get the user name from the intent
					String name = data.getStringExtra("username");
					User user = data.getParcelableExtra("user");
				    if(name.equals(null)){
				    	String error = "Sorry, this username is invalid. Please try another.";
				    	Toast loginError = Toast.makeText(this , error , Toast.LENGTH_LONG);
				    	loginError.show();
				    	return;
				    } else if(name.equals("0")){
				    	String takenError = "Sorry, that username has already been taken. Please enter a new username.";
				    	Toast nameTaken = Toast.makeText(this , takenError, Toast.LENGTH_LONG);
				    	nameTaken.show();
				    	return;
				    }
				    else{
				    	Intent goToMap = new Intent(this, MapActivity.class);
				    	// pass the user parcel to the map activity
				    	//goToMap.putExtra("user", user);
				    	startActivity(goToMap);
				    }
					break;
				case RESULT_CANCELED:
					// display error
					Bundle failInfo = getIntent().getExtras();
					String fail = failInfo.getString("fail");
					Toast failLogin = Toast.makeText(this , fail, Toast.LENGTH_LONG);
			    	failLogin.show();
					break;
				default:
					String noInfo = "Couldn't save your information. Please try again!";
					Toast noInformation = Toast.makeText(this , noInfo, Toast.LENGTH_LONG);
			    	noInformation.show();	
			} //end switch
		} // end if requestCode
	} // end onActivityResult

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_new_user, menu);
		return true;
	}

}
