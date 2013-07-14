package gaze.video.entity;

public class Session {

	final String sessionId;
	final String userId;
	final Long startTime;
	final Long lastRequestTime;
	final SessionState state;
	
	public enum SessionState { ACTIVE, TIMED_OUT, LOGGED_OUT };
	
	public static final Session NO_SESSION;
	static {
		NO_SESSION = new Session("0000", "0000", 0L, 0L, SessionState.ACTIVE);
	}
	
	public Session(String sessionId, String userId, Long startTime, Long lastRequestTime, SessionState state) {
		this.sessionId = sessionId;
		this.userId = userId;
		this.startTime = startTime;
		this.lastRequestTime = lastRequestTime;
		this.state = state;
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
	
	public SessionState getState() {
		return state;
	}
	
}
