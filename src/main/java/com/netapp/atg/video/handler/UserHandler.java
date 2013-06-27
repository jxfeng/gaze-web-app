package com.netapp.atg.video.handler;

import com.netapp.atg.video.entity.User;
import com.netapp.atg.video.exception.ApplicationException;

public interface UserHandler {

	/**
	 * Creates a new user and return its details
	 * @return
	 */
	public User createNewUser(String userId, String email, String password) throws ApplicationException;
	
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
	 * Delete the user
	 * @param userId
	 */
	public void deleteUser(String userId) throws ApplicationException;
	
}
