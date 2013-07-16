package gaze.video.resteasyapi;

import gaze.application.ApplicationSettings;
import gaze.video.entity.AppError;
import gaze.video.entity.CameraShardStats;
import gaze.video.entity.CameraStats;
import gaze.video.entity.Session;
import gaze.video.entity.UserStats;
import gaze.video.exception.ApplicationException;
import gaze.video.handler.SessionAuthenticator;
import gaze.video.handler.StatsHandler;
import gaze.video.handler.dydb.DySessionAuthenticator;
import gaze.video.handler.dydb.DyStatsHandler;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

@Path("/api/stats")
public class RestEasyStats {

	private static final Logger LOG = LoggerFactory.getLogger(RestEasyStats.class);
	private final SessionAuthenticator authenticator;
	
	public RestEasyStats() {
		authenticator = new DySessionAuthenticator();
	}
	
	
	@GET
	@Produces("application/json")
	public Response getUserStats(@HeaderParam(ApplicationSettings.SESSION_HTTP_HEADER) String sessionId) {
		try {
			
			StatsHandler statsHandler = new DyStatsHandler();
			
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
			
			//Send response
			UserStats stats = statsHandler.getUserStats(userId);
			if(stats == null) {
				LOG.error("Could not get stats for user userId:" + userId);
				return Response.status(Status.NOT_FOUND).build();
			}
			
			return Response.status(Status.OK).entity(new Gson().toJson(stats)).build();
			
			
		} catch(ApplicationException exception) {
			LOG.error(exception.getErrorCode().toString(), exception.getCause());
			AppError error = new AppError(exception.getErrorCode(), exception.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(error)).build();
		}
	}
	
	@GET
	@Path("/camera/{cameraId}")
	@Produces("application/json")
	public Response getShardStats(@HeaderParam(ApplicationSettings.SESSION_HTTP_HEADER) String sessionId, 
			@PathParam("cameraId") String cameraId) {
		try {
			
			StatsHandler statsHandler = new DyStatsHandler();
			
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
			
			//Get other parameters
			if(cameraId != null) {
				LOG.error("CameraId not provided to getShardStats()");
				throw ApplicationException.CAMERA_INVALID_CAMERA_ID;
			}
			
			//Send response
			CameraStats stats = statsHandler.getCameraStats(userId, cameraId);
			if(stats == null) {
				LOG.error("Could not get stats for camera userId:" + userId + " cameraId: " + cameraId);
				return Response.status(Status.NOT_FOUND).build();
			}
			
			return Response.status(Status.OK).entity(new Gson().toJson(stats)).build();
			
			
		} catch(ApplicationException exception) {
			LOG.error(exception.getErrorCode().toString(), exception.getCause());
			AppError error = new AppError(exception.getErrorCode(), exception.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(error)).build();
		}
	}
	
	@GET
	@Path("/shard/{cameraId}/{shardId}")
	@Produces("application/json")
	public Response getShardStats(@HeaderParam(ApplicationSettings.SESSION_HTTP_HEADER) String sessionId, 
			@PathParam("cameraId") String cameraId, @PathParam("shardId") Long shardId) {
		try {
			
			StatsHandler statsHandler = new DyStatsHandler();
			
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
			
			//Get other parameters
			if(cameraId != null) {
				LOG.error("CameraId not provided to getShardStats()");
				throw ApplicationException.CAMERA_INVALID_CAMERA_ID;
			}
			
			if(shardId != null) {
				LOG.error("ShardId not provided to getShardStats()");
				throw ApplicationException.SHARD_INVALID_SHARD_ID;
			}
			
			//Send response
			CameraShardStats stats = statsHandler.getCameraShardStats(userId, cameraId, shardId);
			if(stats == null) {
				LOG.error("Could not get stats for shard userId:" + userId + " cameraId: " + cameraId + " shardId: " + shardId);
				return Response.status(Status.NOT_FOUND).build();
			}
			
			return Response.status(Status.OK).entity(new Gson().toJson(stats)).build();
			
			
		} catch(ApplicationException exception) {
			LOG.error(exception.getErrorCode().toString(), exception.getCause());
			AppError error = new AppError(exception.getErrorCode(), exception.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(error)).build();
		}
	}
	
}
