package com.ag.masters.placebase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class Login extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	
		Button login = (Button) findViewById(R.id.btn_confirmlogin);
		Button signup = (Button) findViewById(R.id.btn_new_user);
		
		login.setOnClickListener(new View.OnClickListener() {
	 			public void onClick(View v) {
	 				// TODO: check in an AccountActivity if the name is recognized
	 				Intent startCreateAccount = new Intent(v.getContext(), MapActivity.class);
	 				startCreateAccount.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	 				startActivity(startCreateAccount);
	 				
	 				// eventually start map, and welcome the new user by name.
	 				// ideally, cannot return to this page unless you sign out.
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
