package gaze.video.resteasyapi;

import gaze.application.ApplicationSettings;
import gaze.video.entity.AppError;
import gaze.video.entity.CreateUser;
import gaze.video.entity.Session;
import gaze.video.entity.User;
import gaze.video.exception.ApplicationException;
import gaze.video.handler.SessionAuthenticator;
import gaze.video.handler.UserHandler;
import gaze.video.handler.dydb.DySessionAuthenticator;
import gaze.video.handler.dydb.DyUserHandler;

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

@Path("/api/user")
public class RestEasyUser {

	private static final Logger LOG = LoggerFactory.getLogger(RestEasyUser.class);
	
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response createNewUser(String jsonInput) {
		try {

			CreateUser info;			
			UserHandler userHandler = new DyUserHandler();

			try {
				info = (new Gson()).fromJson(jsonInput, CreateUser.class);
			} catch(Exception e) {
				LOG.error("Could not construct CreateUser from JSON input: " + jsonInput);
				throw ApplicationException.USER_INVALID_CREATE;
			}
			
			String userId = info.getUserHandle();
			String emailAddress = info.getEmailAddress();
			String password = info.getPassword();
			
			//Check if account already exists
			if(userHandler.doesExist(userId)) {
				LOG.error("User id " + userId + " already exists in the database");
				throw ApplicationException.USER_ALREADY_EXISTS;
			}
			
			//Create new user account
			User user = userHandler.createNewUser(userId, emailAddress, password, User.UserRole.NORMAL);
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
			
			//Check if sessionId was passed in
			if(sessionId == null) {
				LOG.error("No session id provided in the request");
				throw ApplicationException.SESSION_INVALID_ID;
			}
			
			//Make sure session is valid
			Session session = authenticator.getSession(sessionId);
			if(!authenticator.isSessionValid(session)) {
				throw ApplicationException.SESSION_EXPIRED;
			}
			
			//Get info on the user
			String sessionUserId = session.getUserId();
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
	
	@POST
	@Path("/{userId}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response updateUser(@HeaderParam(ApplicationSettings.SESSION_HTTP_HEADER) String sessionId,
			@PathParam("userId") String userId, String jsonInput) {
		try {

			User user;			
			UserHandler userHandler = new DyUserHandler();
			SessionAuthenticator authenticator = new DySessionAuthenticator();

			try {
				user = (new Gson()).fromJson(jsonInput, User.class);
			} catch(Exception e) {
				LOG.error("Could not construct User from JSON input: " + jsonInput);
				throw ApplicationException.USER_INVALID_CREATE;
			}

			//Check if sessionId was passed in
			if(sessionId == null) {
				LOG.error("No session id provided in the request");
				throw ApplicationException.SESSION_INVALID_ID;
			}
			
			//Make sure session is valid
			Session session = authenticator.getSession(sessionId);
			if(!authenticator.isSessionValid(session)) {
				throw ApplicationException.SESSION_EXPIRED;
			}
			
			//Make sure session edits his own account
			if(!session.getUserId().equals(userId) || (user.getUserId() != userId)) {
				LOG.error("Cannot edit somebody else's account");
				throw ApplicationException.USER_PERMISSION_MISMATCH;
			}
			assert(userId == user.getUserId());
			
			//Check if user exists
			if(!userHandler.doesExist(userId)) {
				LOG.error("No such user");
				throw ApplicationException.NO_SUCH_USER;
			}
			
			//Update user details
			user = userHandler.updateUserDetails(user);
			String msg = new Gson().toJson(user);
			
			return Response.status(Status.OK).entity(msg).build();
			
		} catch(ApplicationException exception) {
			LOG.error(exception.getErrorCode().toString(), exception.getCause());
			AppError error = new AppError(exception.getErrorCode(), exception.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(error)).build();
		}
	}
	
}
