package gaze.video.handler;

import gaze.video.entity.Session;
import gaze.video.exception.ApplicationException;


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
