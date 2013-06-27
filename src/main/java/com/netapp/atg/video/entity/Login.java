package com.netapp.atg.video.entity;

public class Login {

	final String userId;
	final String password;
	
	public Login(String userId, String password) {
		this.userId = userId;
		this.password = password;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getPassword() {
		return password;
	}
	
}
