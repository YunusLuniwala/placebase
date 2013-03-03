package com.ag.masters.placebase.sqlite;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Encounter implements Parcelable{
	int _id;
	int _story;
	int _user;

	int _hear;
	int _see;
	int _taste;
	int _smell;
	int _touch;
	
	// empty constructor
	public Encounter() {
	}
	// Constructors
	public Encounter(int id, int story, int user, int hear, int see, int taste, int smell, int touch) {
		this._id = id;
		this._story = story;
		this._user = user;
		this._hear = hear;
		this._see = see;
		this._taste = taste;
		this._smell = smell;
		this._touch = touch;
	}

	public Encounter(int id, int story, int user) {
		this._id = id;
		this._story = story;
		this._user = user;
	}
	public Encounter(int story, int user) {
		this._story = story;
		this._user = user;
	}	
	// ID
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
	public Encounter(Parcel in) {
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
		Log.v("Writing Encounter to Parcel", "writeToParcel..." + flags);
		dest.writeInt(_id);
		dest.writeInt(_story);
		dest.writeInt(_user);
		dest.writeInt(_hear);
		dest.writeInt(_see);
		dest.writeInt(_smell);
		dest.writeInt(_taste);
		dest.writeInt(_touch);
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
		_hear = in.readInt();
		_see = in.readInt();
		_smell = in.readInt();
		_taste = in.readInt();
		_touch = in.readInt();
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
           public Encounter createFromParcel(Parcel in) {
               return new Encounter(in);
           }

           public Encounter[] newArray(int size) {
               return new Encounter[size];
           }
       };

}
