package com.netapp.atg.video.resteasyapi;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.netapp.atg.video.entity.AppError;
import com.netapp.atg.video.entity.Login;
import com.netapp.atg.video.entity.Session;
import com.netapp.atg.video.exception.ApplicationException;
import com.netapp.atg.video.handler.SessionAuthenticator;
import com.netapp.atg.video.handler.dydb.DySessionAuthenticator;

@Path("/api/authenticate")
public class RestEasyAuthenticate {

	private static final Logger LOG = LoggerFactory.getLogger(RestEasyAuthenticate.class);
	private final SessionAuthenticator authenticator;
	
	public RestEasyAuthenticate() {
		authenticator = new DySessionAuthenticator();
	}
	
	@POST
	@Produces("application/json")
	@Consumes("application/json")
	public Response authenticateUser(String jsonInput) {
		try {
			Gson gson = new Gson();
			Login info;
			
			//Expect a JSON document as input
			try {
				info = gson.fromJson(jsonInput, Login.class);
			} catch(Exception e) {
				LOG.error("Could not construct LoginInfo from JSON input: " + jsonInput);
				throw ApplicationException.USER_INVALID_LOGIN;
			}
			
			String userId = info.getUserId();
			String password = info.getPassword();
			Session session = authenticator.authenticate(userId, password);
			String reply = new Gson().toJson(session);
			return Response.status(Status.OK).entity(reply).build();
			
		} catch(ApplicationException exception) {
			LOG.error(exception.getErrorCode().toString(), exception.getCause());
			AppError error = new AppError(exception.getErrorCode(), exception.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(error)).build();
		}
	}
	
}
