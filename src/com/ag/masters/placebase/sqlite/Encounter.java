package com.ag.masters.placebase.sqlite;

public class Encounter {
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

}
