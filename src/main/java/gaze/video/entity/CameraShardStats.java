package gaze.video.entity;

import java.util.Map;

public class CameraShardStats {

	private String userId;
	private String cameraId;
	private Long shardId;
	private Long totalImages;
	private Map<String,Long> numImagesByVariation;
	private Map<String,Long> bytesUsedByVariation;
	
	public CameraShardStats(String userId, String cameraId, Long shardId) {
		this.userId = userId;
		this.cameraId = cameraId;
		this.shardId = shardId;
	}
	
	public String getUserId() {
		return userId;
	}

	public String getCameraId() {
		return cameraId;
	}

	public Long getShardId() {
		return shardId;
	}

	public Long getTotalImages() {
		return totalImages;
	}

	public Map<String, Long> getNumImagesByVariation() {
		return numImagesByVariation;
	}

	public Map<String, Long> getBytesUsedByVariation() {
		return bytesUsedByVariation;
	}

	public CameraShardStats withTotalImages(Long totalImages) {
		this.totalImages = totalImages;
		return this;
	}
	
	public CameraShardStats withNumImagesByVariation(Map<String, Long> numImagesByVariation) {
		this.numImagesByVariation = numImagesByVariation;
		return this;
	}
	
	public CameraShardStats withBytesUsedByVariation(Map<String, Long> bytesUsedByVariation) {
		this.bytesUsedByVariation = bytesUsedByVariation;
		return this;
	}
	
}
