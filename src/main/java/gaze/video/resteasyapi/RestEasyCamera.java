package gaze.video.resteasyapi;

import gaze.application.ApplicationSettings;
import gaze.video.entity.AppError;
import gaze.video.entity.Camera;
import gaze.video.entity.Session;
import gaze.video.exception.ApplicationException;
import gaze.video.handler.CameraHandler;
import gaze.video.handler.SessionAuthenticator;
import gaze.video.handler.dydb.DyCameraHandler;
import gaze.video.handler.dydb.DySessionAuthenticator;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

@Path("/api/camera")
public class RestEasyCamera {

	private static final Logger LOG = LoggerFactory.getLogger(RestEasyCamera.class);
	
	@POST
	@Path("/{cameraId}")
	@Produces("application/json")
	public Response createNewCamera(@HeaderParam(ApplicationSettings.SESSION_HTTP_HEADER) String sessionId,
			@PathParam("cameraId") String cameraId) {
		try {
			
			SessionAuthenticator authenticator = new DySessionAuthenticator();
			CameraHandler cameraHandler = new DyCameraHandler();
			
			//Make sure session is valid
			if(sessionId == null) {
				LOG.error("No session id provided in the request");
				throw ApplicationException.SESSION_INVALID_ID;
			}
			Session session = authenticator.getSession(sessionId);
			String userId = session.getUserId();
			
			//TODO: Make sure user is active
			
			//Check if camera already exists
			if(cameraHandler.doesExist(userId, cameraId)) {
				throw ApplicationException.CAMERA_ALREADY_EXISTS;
			}
			
			//Create new camera
			Camera camera = cameraHandler.createNewCamera(userId, cameraId);
			String msg = new Gson().toJson(camera);
			return Response.status(Status.OK).entity(msg).build();
			
		} catch(ApplicationException exception) {
			LOG.error(exception.getErrorCode().toString(), exception.getCause());
			AppError error = new AppError(exception.getErrorCode(), exception.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(error)).build();
		}
	}
	
	@GET
	@Path("/{cameraId}")
	@Produces("application/json")
	public Response getCameraDetails(@HeaderParam(ApplicationSettings.SESSION_HTTP_HEADER) String sessionId,
			@PathParam("cameraId") String cameraId) {
		try {
			Gson gson = new Gson();
			SessionAuthenticator authenticator = new DySessionAuthenticator();
			CameraHandler cameraHandler = new DyCameraHandler();
			
			//Make sure session is valid
			if(sessionId == null) {
				LOG.error("No session id provided in the request");
				throw ApplicationException.SESSION_INVALID_ID;
			}
			Session session = authenticator.getSession(sessionId);
			String sessionUserId = session.getUserId();
			
			//Get info on the camera
			if(sessionUserId != null) {
				if(cameraId != null) {
					Camera camera = cameraHandler.getCameraDetails(sessionUserId, cameraId);
					return Response.status(Status.OK).entity(gson.toJson(camera)).build();
				} else {
					throw ApplicationException.CAMERA_INVALID_CAMERA_ID;
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
	
	
	@GET
	@Path("/list")
	@Produces("application/json")
	public Response listCameras(@HeaderParam(ApplicationSettings.SESSION_HTTP_HEADER) String sessionId,
			@QueryParam("start") String startKey, @QueryParam("limit") Integer limit) {
		try {
			Gson gson = new Gson();
			SessionAuthenticator authenticator = new DySessionAuthenticator();
			CameraHandler cameraHandler = new DyCameraHandler();
			
			//Make sure session is valid
			if(sessionId == null) {
				LOG.error("No session id provided in the request");
				throw ApplicationException.SESSION_INVALID_ID;
			}
			Session session = authenticator.getSession(sessionId);
			String sessionUserId = session.getUserId();
			
			//Get optional parameters
			limit = (limit != null) ? limit : 10;
			
			//Get list
			List<Camera> cameras = cameraHandler.listCameras(sessionUserId, startKey, limit);
			
			//Cursor - opaque to me but will be interpreted by the underlying implementation
			return Response.status(Status.OK).entity(new Gson().toJson(cameras)).build();
			
		} catch(ApplicationException exception) {
			LOG.error(exception.getErrorCode().toString(), exception.getCause());
			AppError error = new AppError(exception.getErrorCode(), exception.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(error)).build();
		}
	}
	
}
