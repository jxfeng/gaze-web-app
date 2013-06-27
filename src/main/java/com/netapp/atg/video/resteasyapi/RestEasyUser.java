package com.netapp.atg.video.resteasyapi;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.netapp.atg.application.ApplicationSettings;
import com.netapp.atg.video.entity.AppError;
import com.netapp.atg.video.entity.CreateUser;
import com.netapp.atg.video.entity.Session;
import com.netapp.atg.video.entity.User;
import com.netapp.atg.video.exception.ApplicationException;
import com.netapp.atg.video.handler.SessionAuthenticator;
import com.netapp.atg.video.handler.UserHandler;
import com.netapp.atg.video.handler.dydb.DySessionAuthenticator;
import com.netapp.atg.video.handler.dydb.DyUserHandler;

@Path("/api/user")
public class RestEasyUser {

	private static final Logger LOG = LoggerFactory.getLogger(RestEasyUser.class);
	
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response createNewUser(String jsonInput) {
		try {
			Gson gson = new Gson();
			CreateUser info;
			
			UserHandler userHandler = new DyUserHandler();
			
			//Expect a JSON document as input
			try {
				info = gson.fromJson(jsonInput, CreateUser.class);
			} catch(Exception e) {
				LOG.error("Could not construct CreateUser from JSON input: " + jsonInput);
				throw ApplicationException.USER_INVALID_CREATE;
			}
			
			String userId = info.getUserHandle();
			String emailAddress = info.getEmailAddress();
			String password = info.getPassword();
			
			//Check if account already exists
			if(userHandler.doesExist(userId)) {
				throw ApplicationException.USER_ALREADY_EXISTS;
			}
			
			//Create new user account
			User user = userHandler.createNewUser(userId, emailAddress, password);
			String msg = new Gson().toJson(user);
			return Response.status(Status.OK).entity(msg).build();
			
		} catch(ApplicationException exception) {
			LOG.error(exception.getErrorCode().toString(), exception.getCause());
			AppError error = new AppError(exception.getErrorCode(), exception.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(error)).build();
		}
	}
	
	@GET
	@Path("/{userId}")
	@Produces("application/json")
	public Response getUserDetails(@HeaderParam(ApplicationSettings.SESSION_HTTP_HEADER) String sessionId,
			@PathParam("userId") String userId) {
		try {
			Gson gson = new Gson();
			SessionAuthenticator authenticator = new DySessionAuthenticator();
			UserHandler userHandler = new DyUserHandler();
			
			//Make sure session is valid
			if(sessionId == null) {
				LOG.error("No session id provided in the request");
				throw ApplicationException.SESSION_INVALID_ID;
			}
			Session session = authenticator.getSession(sessionId);
			String sessionUserId = session.getUserId();
			
			//Get info on the user
			if(userId != null) {
				if(userId.equals(sessionUserId)) {
					User user = userHandler.getUserDetails(userId);
					return Response.status(Status.OK).entity(gson.toJson(user)).build();
				} else {
					throw ApplicationException.USER_INVALID_USER_ID;
				}
			} else {
				throw ApplicationException.USER_INVALID_USER_ID;
			}
			
		} catch(ApplicationException exception) {
			LOG.error(exception.getErrorCode().toString(), exception.getCause());
			AppError error = new AppError(exception.getErrorCode(), exception.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(error)).build();
		}
	}
	
}
