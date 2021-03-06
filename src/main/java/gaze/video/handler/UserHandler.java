package gaze.video.handler;

import gaze.video.entity.User;
import gaze.video.exception.ApplicationException;


public interface UserHandler {

	/**
	 * Creates a new user and return its details
	 * @return
	 */
	public User createNewUser(String userId, String email, String password, User.UserRole role) throws ApplicationException;
	
	/**
	 * Checks if user already exists in the database
	 * @param userId
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean doesExist(String userId) throws ApplicationException;
	
	/**
	 * Get details of the user
	 * @param userId
	 * @return
	 */
	public User getUserDetails(String userId) throws ApplicationException;
	
	/**
	 * Updates user details
	 * @param user
	 * @return
	 * @throws ApplicationException
	 */
	public User updateUserDetails(User user) throws ApplicationException;
	
	
	/**
	 * Delete the user
	 * @param userId
	 */
	public void deleteUser(String userId) throws ApplicationException;
	
}
