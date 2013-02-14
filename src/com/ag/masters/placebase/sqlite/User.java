package com.ag.masters.placebase.sqlite;

public class User {
	int _id;
	String _name;
	// last login? 
	
	// Empty Constructor
	public User() {

	}
	// Constructors
	public User(int id, String name) {
		this._id = id;
		this._name = name;
	}
	public User(String name) {
		this._name = name;
	}

	// id
	public int getId() {
		return this._id;
	}
	public void setId(int id) {
		this._id = id;
	}
	// story
	public String getName() {
		return this._name;
	}
	public void setName(String name) {
		this._name = name;
	}
}