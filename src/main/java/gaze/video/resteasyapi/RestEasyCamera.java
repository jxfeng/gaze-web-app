package gaze.video.resteasyapi;

import gaze.application.ApplicationSettings;
import gaze.video.entity.AppError;
import gaze.video.entity.Camera;
import gaze.video.entity.CameraShard;
import gaze.video.entity.Session;
import gaze.video.entity.User;
import gaze.video.exception.ApplicationException;
import gaze.video.handler.CameraHandler;
import gaze.video.handler.ImageHandler;
import gaze.video.handler.SessionAuthenticator;
import gaze.video.handler.dydb.DyCameraHandler;
import gaze.video.handler.dydb.DyImageHandler;
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
	@Consumes("application/json")
	@Produces("application/json")
	public Response createNewCamera(@HeaderParam(ApplicationSettings.SESSION_HTTP_HEADER) String sessionId,
			@PathParam("cameraId") String cameraId, String jsonInput) {
		try {
			
			SessionAuthenticator authenticator = new DySessionAuthenticator();
			CameraHandler cameraHandler = new DyCameraHandler();
			Camera camera = null;
			
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
			
			//Extract user from session
			String userId = session.getUserId();
			
			//Process user input
			if(jsonInput != null) {
				try {
					camera = (new Gson()).fromJson(jsonInput, Camera.class);
				} catch(Exception e) {
					LOG.error("Could not construct Camera from JSON input: " + jsonInput);
					throw ApplicationException.CAMERA_INVALID_INPUT;
				}
			}
			boolean cameraExists = cameraHandler.doesExist(userId, cameraId);
			
			//Check if camera already exists
			if(cameraExists && jsonInput == null) {
				LOG.error("No data provided to update camera details");
				throw ApplicationException.CAMERA_ALREADY_EXISTS;
			}
			
			//Camera exists so just update details
			if(cameraExists) {
				camera.setUserId(userId);
				camera.setCameraId(cameraId);
				camera = cameraHandler.updateCamera(camera);
				return Response.status(Status.OK).entity(new Gson().toJson(camera)).build();
			}
			
			//Create new camera
			camera.setUserId(userId);
			camera.setCameraId(cameraId);
			camera = cameraHandler.createNewCamera(camera);
			return Response.status(Status.OK).entity(new Gson().toJson(camera)).build();
			
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
	@Path("/{cameraId}/list-shards")
	@Produces("application/json")
	public Response listCameraShards(@HeaderParam(ApplicationSettings.SESSION_HTTP_HEADER) String sessionId,
			@PathParam("cameraId") String cameraId, @QueryParam("from") Long fromTS, @QueryParam("reverse") Boolean reverse, @QueryParam("limit") Integer limit) {
		try {
			Gson gson = new Gson();
			SessionAuthenticator authenticator = new DySessionAuthenticator();
			CameraHandler cameraHandler = new DyCameraHandler();
			ImageHandler imageHandler = new DyImageHandler();
			
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
			String sessionUserId = session.getUserId();
			
			//Check arguments
			fromTS = (fromTS != null && fromTS > 0) ? fromTS : 0;
			reverse = (reverse != null) ? reverse : false;
			limit = (limit != null) ? Math.min(limit, 100) : 100;
			
			//Get info on the camera
			if(sessionUserId != null) {
				if(cameraId != null) {
					List<CameraShard> shardList = imageHandler.listShards(sessionUserId, cameraId, fromTS, reverse, limit);
					return Response.status(Status.OK).entity(gson.toJson(shardList)).build();
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
	
	@POST
	@Path("/{cameraId}/commit/{imageTs}")
	@Produces("application/json")
	public Response updateImageCommit(@HeaderParam(ApplicationSettings.SESSION_HTTP_HEADER) String sessionId,
			@PathParam("cameraId") String cameraId, @PathParam("imageTs") Long imageTs) {
		try {
			Gson gson = new Gson();
			SessionAuthenticator authenticator = new DySessionAuthenticator();
			CameraHandler cameraHandler = new DyCameraHandler();
			
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
			String sessionUserId = session.getUserId();
			
			//Check if timestamp was passed in
			if(imageTs == null || imageTs < 0) {
				LOG.error("Invalid timestamp passed in for commit");
				throw ApplicationException.CAMERA_INVALID_COMMIT_TS;
			}
			
			//Get info on the camera
			if(sessionUserId != null) {
				if(cameraId != null) {
					Camera camera = cameraHandler.getCameraDetails(sessionUserId, cameraId);
					camera = cameraHandler.updateLatestImageTimestamp(sessionUserId, cameraId, imageTs);
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
			@QueryParam("start") String startKey, @QueryParam("limit") Integer limit, @QueryParam("reverse") Boolean reverse) {
		try {

			SessionAuthenticator authenticator = new DySessionAuthenticator();
			CameraHandler cameraHandler = new DyCameraHandler();
			
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
			String sessionUserId = session.getUserId();
			
			//Get optional parameters
			limit = (limit != null) ? limit : 10;
			reverse = (reverse != null) ? reverse : false; 
			
			//Get list
			List<Camera> cameras = cameraHandler.listCameras(sessionUserId, startKey, reverse, limit);
			
			//Cursor - opaque to me but will be interpreted by the underlying implementation
			return Response.status(Status.OK).entity(new Gson().toJson(cameras)).build();
			
		} catch(ApplicationException exception) {
			LOG.error(exception.getErrorCode().toString(), exception.getCause());
			AppError error = new AppError(exception.getErrorCode(), exception.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(error)).build();
		}
	}
	
}
