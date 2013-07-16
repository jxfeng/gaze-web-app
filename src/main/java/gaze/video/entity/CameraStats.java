package gaze.video.entity;

import java.util.List;
import java.util.Map;

public class CameraStats {

	private String userId;
	private String cameraId;

	private Long totalImages;
	private Map<String,Long> numImagesByVariation;
	private Map<String,Long> bytesUsedByVariation;
	
	private List<CameraShardStats> shardStats;
	private Long numShards;
	
	public CameraStats(String userId, String cameraId) {
		this.userId = userId;
		this.cameraId = cameraId;
	}
	
	public CameraStats withTotalImages(Long totalImages) {
		this.totalImages = totalImages;
		return this;
	}
	
	public String getUserId() {
		return userId;
	}

	public String getCameraId() {
		return cameraId;
	}

	public Long getTotalImages() {
		return totalImages;
	}

	public Long getNumShards() {
		return numShards;
	}
	
	public Map<String, Long> getNumImagesByVariation() {
		return numImagesByVariation;
	}

	public Map<String, Long> getBytesUsedByVariation() {
		return bytesUsedByVariation;
	}

	public List<CameraShardStats> getShardStats() {
		return shardStats;
	}

	public CameraStats withNumImagesByVariation(Map<String, Long> numImagesByVariation) {
		this.numImagesByVariation = numImagesByVariation;
		return this;
	}
	
	public CameraStats withBytesUsedByVariation(Map<String, Long> bytesUsedByVariation) {
		this.bytesUsedByVariation = bytesUsedByVariation;
		return this;
	}
	
	public CameraStats withShardStats(List<CameraShardStats> shardStats) {
		this.shardStats = shardStats;
		this.numShards = (shardStats != null) ? shardStats.size() : 0L;
		return this;
	}
	
}
