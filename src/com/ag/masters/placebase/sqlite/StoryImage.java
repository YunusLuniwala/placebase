package com.ag.masters.placebase.sqlite;

public class StoryImage {
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
}
