package gaze.video.entity;

public class CameraShard {

	private String userId;
	private String cameraId;
	private Long   shardId;
	private Long   shardBeginTimestamp;
	private Long   shardEndTimestamp;
	
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
	
	public Long getShardId() {
		return shardId;
	}
	
	public void setShardId(Long shardId) {
		this.shardId = shardId;
	}
	
	public Long getShardEndTimestamp() {
		return shardEndTimestamp;
	}
	
	public void setShardEndTimestamp(Long shardEndTimestamp) {
		this.shardEndTimestamp = shardEndTimestamp;
	}
	
	public Long getShardBeginTimestamp() {
		return shardBeginTimestamp;
	}
	
	public void setShardBeginTimestamp(Long shardBeginTimestamp) {
		this.shardBeginTimestamp = shardBeginTimestamp;
	}
	
}
