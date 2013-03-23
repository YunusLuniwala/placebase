package com.ag.masters.placebase.model;

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


}

