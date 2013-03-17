package com.ag.masters.placebase.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ag.masters.placebase.R;

public class Funnel extends ViewGroup{
	
	public Funnel(Context context) {
		super(context);
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater.inflate(R.layout.progressbar, this, true);
		
		if(this.getClass().getSimpleName() == "Caption") {
			Log.v("dfd", "we are at the caption screen");
		}
		
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		for(int i = 0 ; i < getChildCount() ; i++){
            getChildAt(i).layout(l, t, r, b);
        }
		
	}

}
