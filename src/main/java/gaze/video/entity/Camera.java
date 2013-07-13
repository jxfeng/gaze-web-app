package gaze.video.entity;

public class Camera {

	private String userId;
	private String cameraId;
	private String cameraName;
	private String cameraLocation;
	private String cameraState;
	private Long   lastImageTimestamp;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getCameraId() {
		return cameraId;
	}
	public void setCameraId(String cameraId) {
		this.cameraId = cameraId;
	}
	
	public String getCameraName() {
		return cameraName;
	}
	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
	}
	
	public String getCameraLocation() {
		return cameraLocation;
	}
	public void setCameraLocation(String cameraLocation) {
		this.cameraLocation = cameraLocation;
	}
	
	public String getCameraState() {
		return cameraState;
	}
	public void setCameraState(String cameraState) {
		this.cameraState = cameraState;
	}
	
	public Long getLastImageTimestamp() {
		return lastImageTimestamp;
	}
	public void setLastImageTimestamp(Long lastImageTimestamp) {
		this.lastImageTimestamp = lastImageTimestamp;
	}
	
}
