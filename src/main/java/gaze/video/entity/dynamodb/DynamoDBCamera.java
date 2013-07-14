package gaze.video.entity.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;


@DynamoDBTable(tableName="Camera")
public class DynamoDBCamera {

	private String userId;
	private String cameraId;
	private String cameraName;
	private String cameraLocation;
	private String cameraState;
	private Long   lastImageTimestamp;

	@DynamoDBHashKey(attributeName="userId")
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@DynamoDBRangeKey(attributeName="cameraId")
	public String getCameraId() {
		return cameraId;
	}
	public void setCameraId(String cameraId) {
		this.cameraId = cameraId;
	}
	
	@DynamoDBAttribute(attributeName="cameraName")
	public String getCameraName() {
		return cameraName;
	}
	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
	}
	
	@DynamoDBAttribute(attributeName="cameraLocation")
	public String getCameraLocation() {
		return cameraLocation;
	}
	public void setCameraLocation(String cameraLocation) {
		this.cameraLocation = cameraLocation;
	}
	
	@DynamoDBAttribute(attributeName="cameraState")
	public String getCameraState() {
		return cameraState;
	}
	public void setCameraState(String cameraState) {
		this.cameraState = cameraState;
	}
	
	@DynamoDBAttribute(attributeName="lastImageTimestamp")
	public Long getLastImageTimestamp() {
		return lastImageTimestamp;
	}
	public void setLastImageTimestamp(Long lastImageTimestamp) {
		this.lastImageTimestamp = lastImageTimestamp;
	}
	

}
