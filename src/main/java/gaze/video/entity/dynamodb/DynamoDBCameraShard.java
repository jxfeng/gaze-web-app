package gaze.video.entity.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="CameraShard")
public class DynamoDBCameraShard {
	
	private String cameraKey;
	private Long shardId;
	private String userId;
	private String cameraId;
	private String shardKey;
	private Boolean shardComplete;
	//TODO: Keep track of resizer status here
	
	@DynamoDBHashKey(attributeName="cameraKey")
	public String getCameraKey() {
		return cameraKey;
	}
	public void setCameraKey(String cameraKey) {
		this.cameraKey = cameraKey;
	}
	
	@DynamoDBRangeKey(attributeName="shardId")
	public Long getShardId() {
		return shardId;
	}
	public void setShardId(Long shardId) {
		this.shardId = shardId;
	}
	
	@DynamoDBAttribute(attributeName="shardKey")
	public String getShardKey() {
		return shardKey;
	}
	public void setShardKey(String shardKey) {
		this.shardKey = shardKey;
	}
	
	@DynamoDBAttribute(attributeName="userId")
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@DynamoDBAttribute(attributeName="cameraId")
	public String getCameraId() {
		return cameraId;
	}
	public void setCameraId(String cameraId) {
		this.cameraId = cameraId;
	}

	@DynamoDBAttribute(attributeName="shardComplete")
	public Boolean getShardComplete() {
		return shardComplete;
	}
	public void setShardComplete(Boolean shardComplete) {
		this.shardComplete = shardComplete;
	}

}
