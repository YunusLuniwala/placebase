package com.ag.masters.placebase.sqlite;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Story implements Parcelable {
	int _id;
	int _user = 0;				// first created
	int _media = 0;				// first created
	int _hear = 0;				// sense input
	int _see = 0;				// sense input
	int _smell = 0;				// sense input
	int _taste = 0;				// sense input
	int _touch = 0;				// sense input
	double _lat = 0;			// perspective taken
	double _lng = 0;			// perspective taken
	float _bearing = 0;			// perspective taken
	String _timestamp = null;		// perspective taken
	String _perspective_uri = null; // perspective taken
	
	String _markerId;	// created dynamically when markers are added to the map (not in db)

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
	 *  AS A PARCELLABLE ITEM
	 * 
	 * @see http://techdroid.kbeanie.com/2010/06/parcelable-how-to-do-that-in-android.html
	 * @see http://shri.blog.kraya.co.uk/2010/04/26/android-parcel-data-to-pass-between-activities-using-parcelable-classes/
	 *
	 *
	 */

	/**
	 *
	 * Constructor to use when re-constructing object
	 * from a parcel
	 *
	 * @param in a parcel from which to read this object
	 */
	public Story(Parcel in) {
		readFromParcel(in);
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// We just need to write each field into the
		// parcel. When we read from parcel, they
		// will come back in the same order
		Log.v("Writing Story to Parcel", "writeToParcel..." + flags);
		dest.writeInt(_id);
		dest.writeInt(_user);
		dest.writeInt(_media);
		dest.writeInt(_hear);
		dest.writeInt(_see);
		dest.writeInt(_smell);
		dest.writeInt(_taste);
		dest.writeInt(_touch);
		dest.writeDouble(_lat);
		dest.writeDouble(_lng);
		dest.writeFloat(_bearing);
		dest.writeString(_timestamp);
		dest.writeString(_perspective_uri);
	}
	
	/**
	 *
	 * Called from the constructor to create this
	 * object from a parcel.
	 *
	 * @param in parcel from which to re-create object
	 */
	private void readFromParcel(Parcel in) {
		// We just need to read back each
		// field in the order that it was
		// written to the parcel
		_id = in.readInt();
		_user = in.readInt();
		_media = in.readInt();
		_hear = in.readInt();
		_see = in.readInt();
		_smell = in.readInt();
		_taste = in.readInt();
		_touch = in.readInt();
		_lat = in.readDouble();
		_lng = in.readDouble();
		_bearing = in.readFloat();
		_timestamp = in.readString();
		_perspective_uri = in.readString();
	}
	
	/**
    *
    * This field is needed for Android to be able to
    * create new objects, individually or as arrays.
    *
    *
    */
   public static final Parcelable.Creator CREATOR =
   	new Parcelable.Creator() {
           public Story createFromParcel(Parcel in) {
               return new Story(in);
           }

           public Story[] newArray(int size) {
               return new Story[size];
           }
       };
	
}
