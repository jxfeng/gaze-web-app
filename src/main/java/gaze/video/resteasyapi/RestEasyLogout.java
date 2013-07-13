package gaze.video.resteasyapi;

import gaze.application.ApplicationSettings;
import gaze.video.entity.AppError;
import gaze.video.exception.ApplicationException;
import gaze.video.handler.SessionAuthenticator;
import gaze.video.handler.dydb.DySessionAuthenticator;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

@Path("/api/logout")
public class RestEasyLogout {

	private static final Logger LOG = LoggerFactory.getLogger(RestEasyLogout.class);
	private final SessionAuthenticator authenticator;
	
	public RestEasyLogout() {
		authenticator = new DySessionAuthenticator();
	}
	
	@POST
	@Produces("application/json")
	public Response logoutUser(@HeaderParam(ApplicationSettings.SESSION_HTTP_HEADER) String sessionId) {
		try {
			if(sessionId != null) {
				authenticator.logoutSession(sessionId);
				return Response.status(Status.OK).build();
			} else {
				return Response.status(Status.NOT_FOUND).build();
			}
			
		} catch(ApplicationException exception) {
			LOG.error(exception.getErrorCode().toString(), exception.getCause());
			AppError error = new AppError(exception.getErrorCode(), exception.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(error)).build();
		}
	}
	
}
