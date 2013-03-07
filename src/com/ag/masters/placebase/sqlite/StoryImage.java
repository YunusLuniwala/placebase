package com.ag.masters.placebase.sqlite;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class StoryImage implements Parcelable{
	int _id;
	int _story;
	String _uri;
	String _caption;
	
	// empty Constructor
	public StoryImage() {
		
	}
	// Constructors
	public StoryImage(int id, int story, String uri, String caption) {
		this._id = id;
		this._story = story;
		this._uri = uri;
	}
	
	public StoryImage(int story, String uri, String caption) {
		this._story = story;
		this._uri = uri;
	}
	
	public StoryImage(String uri, String caption) {
		this._uri = uri;
	}
	
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
	//uri
	public String getUri() {
		return this._uri;
	}
	public void setUri(String uri) {
		this._uri = uri;
	}
	//caption
	public String getCaption() {
		return this._caption;
	}
	public void setCaption(String caption) {
		this._caption = caption;
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
	public StoryImage(Parcel in) {
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
		Log.v("Writing Image to Parcel", "writeToParcel..." + flags);
		dest.writeInt(_id);
		dest.writeInt(_story);
		dest.writeString(_uri);
		dest.writeString(_caption);
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
		_story = in.readInt();
		_uri = in.readString();
		_caption = in.readString();
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
           public StoryImage createFromParcel(Parcel in) {
               return new StoryImage(in);
           }

           public StoryImage[] newArray(int size) {
               return new StoryImage[size];
           }
       };

	
}
