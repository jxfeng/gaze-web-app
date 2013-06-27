package com.netapp.atg.video.handler;

import com.netapp.atg.video.entity.Session;
import com.netapp.atg.video.exception.ApplicationException;

public interface SessionAuthenticator {

	/**
	 * Login into the system using userid and password
	 * @param uderId
	 * @param password
	 * @return
	 * @throws ApplicationException
	 */
	public Session authenticate(String userId, String password) throws ApplicationException;
	
	/**
	 * Gets the session info from the database
	 * @param sessionId
	 * @return
	 */
	public Session getSession(String sessionId) throws ApplicationException;
	
	/**
	 * Checks if the session is still valid
	 * @param session
	 * @return
	 */
	public Boolean isSessionValid(Session session) throws ApplicationException;
	
	/**
	 * Logs out the session if found, returns true. Else returns false.
	 * @param sessionId
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean logoutSession(String sessionId) throws ApplicationException;
	
}
