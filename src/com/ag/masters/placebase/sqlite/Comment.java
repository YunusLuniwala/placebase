package com.ag.masters.placebase.sqlite;

public class Comment {
	int _id;
	int _story;
	int _user;
	String _body;
	long _timestamp;

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
	public long getTimestamp() {
		return this._timestamp;
	}
	public void setTimestamp(long timestamp) {
		this._timestamp = timestamp;
	}

}
