package com.ag.masters.placebase.sqlite;

public class Audio {
	int _id;
	int _story;
	String _uri;
	
	
	// empty Constructor
	public Audio() {
		
	}
	// Constructors
	public Audio(int id, int story, String uri) {
		this._id = id;
		this._story = story;
		this._uri = uri;
	}
	
	public Audio(int story, String uri) {
		this._story = story;
		this._uri = uri;
	}
	
	public Audio(String uri) {
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
}	