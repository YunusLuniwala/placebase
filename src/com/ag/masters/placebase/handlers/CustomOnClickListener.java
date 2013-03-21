package com.ag.masters.placebase.handlers;

import android.view.View;
import android.view.View.OnClickListener;

public class CustomOnClickListener implements OnClickListener {

	private int position;
	private OnCustomClickListener callback; // interface
	
	// Pass in the callback (this will be the activity) and the row position
	public CustomOnClickListener(OnCustomClickListener callback, int pos) {
		position = pos;
		this.callback = callback;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		callback.OnCustomClick(v, position);
	}

}
