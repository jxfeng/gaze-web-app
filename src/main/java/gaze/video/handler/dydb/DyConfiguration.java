package gaze.video.handler.dydb;

import gaze.video.entity.CameraShard;
import gaze.video.entity.ImageVariation.BlobVariation;

public class DyConfiguration {

	public static final Long SHARD_NUM_IMAGES = 10000000L;
	
	public static Long getShardId(Long timestamp) {
		return timestamp/SHARD_NUM_IMAGES;
	}
	
	public static String generateCameraKey(String userId, String cameraId) {
		return "user-" + userId + "-camera-" + cameraId;
	}
	
	public static String getShardKey(CameraShard shard) {
		return getShardKey(shard.getUserId(), shard.getCameraId(), shard.getShardBeginTimestamp());
	}
	
	public static String getShardKey(String userId, String cameraId, Long timestamp) {
		return "user-" + userId + "-camera-" + cameraId + "-shard-" + String.format("%010d", getShardId(timestamp));
	}
	
	public static String getShardKeyById(String userId, String cameraId, Long shardId) {
		return "user-" + userId + "-camera-" + cameraId + "-shard-" + String.format("%010d", shardId);
	}
	
	public static String getImageKey(String shardKey, Long timestamp) {
		return shardKey + "-imagets-" + String.format("%020d", timestamp);
	}

	public static String generateBlobId(String shardKey, Long timestamp, BlobVariation blobResolution) {
		return shardKey + "-imagets-" + String.format("%020d", timestamp) + "-resolution-" + blobResolution.toString();
	}
	
	public static Long getStartTimestamp(Long shardId) {
		Long startTimestamp = shardId * SHARD_NUM_IMAGES;
		return startTimestamp;
	}
	
	public static Long getEndTimestamp(Long shardId) {
		Long endTimestamp = ((shardId + 1) * DyConfiguration.SHARD_NUM_IMAGES) - 1;
		return endTimestamp;
	}
	
	
}
