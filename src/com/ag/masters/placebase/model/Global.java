package com.ag.masters.placebase.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.ag.masters.placebase.R;

public class Global {
	// intent request codes
	public static final int IMAGE_CAPTURE = 0;
	public static final int VIDEO_CAPTURE = 1;
	public static final int AUDIO_CAPTURE = 2;
	
	public static final String PREFS = "MyPrefsFile";
	
	public static final String formatDaysForUI(int numDays) {
		String thisInterval;
		switch (numDays) {
		case 0:
			thisInterval = "today";
			break;
		case 1:
			thisInterval = Integer.toString(numDays) + " day ago";
			break;
		default:
			thisInterval = Integer.toString(numDays) + " days ago";
			break;
		}
		return thisInterval;
	}
	
	public static final String formatGeoForUI(double lat, double lng) {
		String s = "(" + Double.toString(lat) + ", " + Double.toString(lng) + ")";
		return s;
	}
	

	public static String readTxt(int res, Context ctx) {
		InputStream inputStream = ctx.getResources().openRawResource(res);
	     
	     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	     
	     int i;
	  try {
	   i = inputStream.read();
	   while (i != -1)
	      {
	       byteArrayOutputStream.write(i);
	       i = inputStream.read();
	      }
	      inputStream.close();
	  } catch (IOException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	  }
	  
	     return byteArrayOutputStream.toString();
	    }
	
	
}

