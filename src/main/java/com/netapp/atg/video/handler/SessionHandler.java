package com.netapp.atg.video.handler;

import com.netapp.atg.video.entity.Session;
import com.netapp.atg.video.exception.ApplicationException;

public interface SessionHandler {

	/**
	 * Creates a new session and return its details
	 * @return
	 */
	public Session getNewSession() throws ApplicationException;
	
	/**
	 * Create a new session for this particular user
	 * @param userId
	 * @return
	 * @throws ApplicationException
	 */
	public Session getNewSession(String userId) throws ApplicationException;
	
	/**
	 * Get details of the session
	 * @param sessionId
	 * @return
	 */
	public Session getSessionDetails(String sessionId) throws ApplicationException;
	
	/**
	 * Delete the session
	 * @param sessionId
	 */
	public void deleteSession(String sessionId) throws ApplicationException;
	
}
