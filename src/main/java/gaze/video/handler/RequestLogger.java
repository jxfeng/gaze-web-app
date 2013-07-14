package gaze.video.handler;

import gaze.video.entity.Session;

public interface RequestLogger {

	/**
	 * Generates a UUID to represent a request
	 * @return
	 */
	public String getNewRequestId();
	
	/**
	 * Logs the beginning of a processing of a API request
	 * @param session
	 * @param requestId
	 * @param requestName
	 */
	public void logRequestBegin(Session session, String requestId, String requestName);
	
	/**
	 * Logs the ending of a processing of a API request
	 * @param session
	 * @param requestId
	 * @param requestName
	 */
	public void logRequestEnd(Session session, String requestId, String requestName);
	
}
