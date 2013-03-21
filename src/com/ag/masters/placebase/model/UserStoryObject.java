/**
 * Convenience Object
 * Object to simplify the construction of List Items for UserPlaces.class
 * 
 * Has no parallel structure in the database
 * combines info from Story, encounter and comment tables
 */

package com.ag.masters.placebase.model;

public class UserStoryObject {

	// story
	int _storyId;
	int _media = 0;				
	double _lat = 0;			
	double _lng = 0;			
	String _timestamp = null;		
	String _perspective_uri = null; 
	// comments
	int _comments = 0;
	// encounters
	int _encounters = 0;
	// any new activity
	boolean _new = false;

	public UserStoryObject() {
	}

	public UserStoryObject(int id, int media, double lat, double lng, String timestamp, String perspective_uri ) {
		this._storyId = id;
		this._media = media;
		this._lat = lat;
		this._lng = lng;
		this._timestamp = timestamp;
		this._perspective_uri = perspective_uri;
	}

	public UserStoryObject(int id, int media, double lat, double lng, String timestamp, String perspective_uri, int comments, int encounters, boolean isNew) {
		this._storyId = id;
		this._media = media;
		this._lat = lat;
		this._lng = lng;
		this._timestamp = timestamp;
		this._perspective_uri = perspective_uri;
		this._comments = comments;
		this._encounters = encounters;
		this._new = isNew;
	}
	
	
	public int getId() {
		return _storyId;
	}
	public int getMedia() {
		return _media;
	}
	public double getLat() {
		return _lat;
	}
	public double getLng() {
		return _lng;
	}
	public String getTimestamp() {
		return _timestamp;
	}
	public String getPerspective() {
		return _perspective_uri;
	}
	

	public void setComments(int num) {
		_comments = num;
	}
	public int getComments() {
		return _comments;
	}
	public void setEncounters(int num) {
		_encounters = num;
	}
	public int getEncounters() {
		return _encounters;
	}
	public void setNew() {
		_new = true;
	}
	public boolean getNew() {
		return _new;
	}
	
}


