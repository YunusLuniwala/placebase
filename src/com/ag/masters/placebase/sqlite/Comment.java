package com.ag.masters.placebase.sqlite;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Comment implements Parcelable {
	int _id;
	int _story;
	int _user;
	String _body;
	String _timestamp;

	// id
	public int getId() {
		return this._id;
	}
	public void setId(int id) {
		this._id = id;
	}
	// story
	public int getStory() {
		return this._story;
	}
	public void setStory(int story) {
		this._story = story;
	}
	//user
	public int getUser() {
		return this._user;
	}
	public void setUser(int user) {
		this._user = user;
	}
	//uri
	public String getBody() {
		return this._body;
	}
	public void setBody(String body) {
		this._body = body;
	}
	// timestamp
	public String getTimestamp() {
		return this._timestamp;
	}
	public void setTimestamp(String timestamp) {
		this._timestamp = timestamp;
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
	public Comment(Parcel in) {
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
		Log.v("Writing Comment to Parcel", "writeToParcel..." + flags);
		dest.writeInt(_id);
		dest.writeInt(_story);
		dest.writeInt(_user);
		dest.writeString(_body);
		dest.writeString(_timestamp);
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
		_story = in.readInt();
		_body = in.readString();
		_timestamp = in.readString();
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
           public Comment createFromParcel(Parcel in) {
               return new Comment(in);
           }

           public Comment[] newArray(int size) {
               return new Comment[size];
           }
       };

       
}
