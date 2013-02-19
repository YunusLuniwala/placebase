package com.ag.masters.placebase.sqlite;

public class Story {
	int _id;		
	double _lat;		// perspective taken
	double _lng;		// perspective taken
	float _bearing;		// perspective taken
	int _media;			// first created
	int _hear;			// sense input
	int _see;			// sense input
	int _smell;			// sense input
	int _taste;			// sense input
	int _touch;			// sense input
	String _timestamp;	// perspective taken
	String _perspective_uri; // or perhaps the URI? perspective taken
	int _user;			// first created
	
	String _markerId;	// created dynamically when markers are added to the map

	// Empty constructor
	public Story() {
	}
	
	public Story(int user, int media) { // likely how its first created
		this._user = user;
		this._media = media;
	}
	
	// constructor
	public Story(int id, double lat, double lng, float bearing, int media, int hear, int see, int taste, int smell, int touch, String timestamp, String perspective_uri, int user) {
		this._id = id;
		this._lat = lat;
		this._lng = lng;
		this._bearing = bearing;
		this._media = media;
		this._hear = hear;
		this._see = see;
		this._taste = taste;
		this._smell = smell;
		this._touch = touch;
		this._timestamp = timestamp;
		this._perspective_uri = perspective_uri;
		this._user = user;
	}

	// constructor
	public Story(double lat, double lng, float bearing, int media, int hear, int see, int taste, int smell, int touch, String timestamp, String perspective_uri, int user) {
		this._lat = lat;
		this._lng = lng;
		this._bearing = bearing;
		this._media = media;
		this._hear = hear;
		this._see = see;
		this._taste = taste;
		this._smell = smell;
		this._touch = touch;
		this._timestamp = timestamp;
		this._perspective_uri = perspective_uri;
		this._user = user;
	}

	// constructor
	public Story(int id, double lat, double lng, float bearing, int media, int user) {
		this._id = id;
		this._lat = lat;
		this._lng = lng;
		this._bearing = bearing;
		this._media = media;
		this._user = user;
	}
	// constructor
	public Story(double lat, double lng, float bearing, int media, int user) {
		this._lat = lat;
		this._lng = lng;
		this._bearing = bearing;
		this._media = media;
		this._user = user;
	}
	// constructor
	public Story(double lat, double lng) {
		this._lat = lat;
		this._lng = lng;
	}

	// ID
	public int getId() {
		return this._id;
	}

	public void setId(int id) {
		this._id = id;
	}
	// latitude
	public double getLat() {
		return this._lat;
	}

	public void setLat(double lat) {
		this._lat = lat;
	}
	//longitude
	public double getLng() {
		return this._lng;
	}

	public void setLng(double lng) {
		this._lng = lng;
	}
	// bearing
	public float getBearing() {
		return this._bearing;
	}
	public void setBearing(float bearing) {
		this._bearing = bearing;
	}
	// media type
	public int getMedia() {
		return this._media;
	}
	public void setMedia(int media) {
		this._media = media;
	}
	// hear
	public int getHear() {
		return this._hear;
	}
	public void setHear(int hear) {
		this._hear = hear;
	}
	// see
	public int getSee() {
		return this._see;
	}
	public void setSee(int see) {
		this._see = see;
	}
	// taste
	public int getTaste() {
		return this._taste;
	}
	public void setTaste(int taste) {
		this._taste = taste;
	}
	// smell
	public int getSmell() {
		return this._smell;
	}
	public void setSmell(int smell) {
		this._smell = smell;
	}
	// touch
	public int getTouch() {
		return this._touch;
	}
	public void setTouch(int touch) {
		this._touch = touch;
	}
	// timestamp
	public String getTimestamp() {
		return this._timestamp;
	}
	public void setTimestamp(String timestamp) {
		this._timestamp = timestamp;
	}
	// perspective_uri
	public String getPerspectiveUri() {
		return this._perspective_uri;
	}
	public void setPerspectiveUri(String perspective_uri) {
		this._perspective_uri = perspective_uri;
	}
	//user
	public int getUser() {
		return this._user;
	}
	public void setUser(int user) {
		this._user = user;
	}
	// marker item
	public void setMarkerId(String markerId) {
		this._markerId = markerId;
	}
	public String getMarkerId() {
		return this._markerId;
	}
	/**
	 * TODO: AS A PARCELLABLE ITEM
	 * 
	 * @see http://techdroid.kbeanie.com/2010/06/parcelable-how-to-do-that-in-android.html
	 * @see http://shri.blog.kraya.co.uk/2010/04/26/android-parcel-data-to-pass-between-activities-using-parcelable-classes/
	 *
	 *
	 */
}
