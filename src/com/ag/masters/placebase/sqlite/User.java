package com.ag.masters.placebase.sqlite;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class User implements Parcelable{
	int _id;
	String _name;
	String _password;
	String _date;
	
	// Empty Constructor
	public User() {

	}
	// Constructors
	public User(int id, String name, String password, String date) {
		this._id = id;
		this._name = name;
		this._password = password;
		this._date = date;
	}
	public User(String name) {
		this._name = name;
	}
	public User(String name, String password, String date) {
		this._name = name;
		this._password = password;
		this._date = date;
	}
	

	// id
	public int getId() {
		return this._id;
	}
	public void setId(int id) {
		this._id = id;
	}
	// name
	public String getName() {
		return this._name;
	}
	public void setName(String name) {
		this._name = name;
	}
	// password
	public String getPassword() {
		return this._password;
	}
	public void setPassword(String password) {
		this._password = password;
	}
	//date
	public String getDate() {
		return this._date;
	}
	public void setDate(String date) {
		this._date = date;
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
	public User(Parcel in) {
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
		dest.writeString(_name);
		dest.writeString(_password);
		dest.writeString(_date);
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
		_name = in.readString();
		_password = in.readString();
		_date = in.readString();
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
           public User createFromParcel(Parcel in) {
               return new User(in);
           }

           public User[] newArray(int size) {
               return new User[size];
           }
       };
}