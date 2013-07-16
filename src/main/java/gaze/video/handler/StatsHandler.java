package gaze.video.handler;

import gaze.video.entity.CameraShardStats;
import gaze.video.entity.CameraStats;
import gaze.video.entity.UserStats;
import gaze.video.exception.ApplicationException;

public interface StatsHandler {

	/**
	 * Returns stats for entire account (might be slow)
	 * @param userId
	 * @return
	 * @throws ApplicationException
	 */
	public UserStats getUserStats(String userId) throws ApplicationException;
	
	/**
	 * Gets stats for a given camera
	 * @param userId
	 * @param cameraId
	 * @return
	 * @throws ApplicationException
	 */
	public CameraStats getCameraStats(String userId, String cameraId) throws ApplicationException;
	
	/**
	 * Gets stats for given partition within the camera
	 * @param userId
	 * @param cameraId
	 * @param shardId
	 * @return
	 * @throws ApplicationException
	 */
	public CameraShardStats getCameraShardStats(String userId, String cameraId, Long shardId) throws ApplicationException;
	
}
