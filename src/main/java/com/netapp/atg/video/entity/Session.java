package com.netapp.atg.video.entity;

public class Session {

	final String sessionId;
	final String userId;
	final Long startTime;
	final Long lastRequestTime;
	
	public Session(String sessionId, String userId, Long startTime, Long lastRequestTime) {
		this.sessionId = sessionId;
		this.userId = userId;
		this.startTime = startTime;
		this.lastRequestTime = lastRequestTime;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public Long getStartTime() {
		return startTime;
	}
	
	public Long getLastRequestTime() {
		return lastRequestTime;
	}
}
