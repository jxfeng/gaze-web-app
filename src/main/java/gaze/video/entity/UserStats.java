package gaze.video.entity;

import java.util.List;
import java.util.Map;

public class UserStats {

	private String userId;
	private Long totalImages;
	private Map<String,Long> numImagesByVariation;
	private Map<String,Long> bytesUsedByVariation;
	
	private Long numCameras;
	private List<CameraStats> cameraStats;
	
	public UserStats(String userId) {
		this.userId = userId;
	}
	
	public UserStats withTotalImages(Long totalImages) {
		this.totalImages = totalImages;
		return this;
	}
	
	public String getUserId() {
		return userId;
	}

	public Long getTotalImages() {
		return totalImages;
	}

	public Long getNumCameras() {
		return numCameras;
	}
	
	public Map<String, Long> getNumImagesByVariation() {
		return numImagesByVariation;
	}

	public Map<String, Long> getBytesUsedByVariation() {
		return bytesUsedByVariation;
	}

	public List<CameraStats> getCameraStats() {
		return cameraStats;
	}

	public UserStats withNumImagesByVariation(Map<String, Long> numImagesByVariation) {
		this.numImagesByVariation = numImagesByVariation;
		return this;
	}
	
	public UserStats withBytesUsedByVariation(Map<String, Long> bytesUsedByVariation) {
		this.bytesUsedByVariation = bytesUsedByVariation;
		return this;
	}
	
	public UserStats withCameraStats(List<CameraStats> cameraStats) {
		this.cameraStats = cameraStats;
		this.numCameras = (cameraStats != null) ? cameraStats.size() : 0L;
		return this;
	}
	
}
