package gaze.video.entity.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="Image")
public class DynamoDBImage {

	private String cameraShardId;
	private String imageKey;
	private Long   imageTimestamp;
	private String imageState;
	
	@DynamoDBHashKey(attributeName="cameraShardId")
	public String getCameraShardId() {
		return cameraShardId;
	}
	public void setCameraShardId(String cameraShardId) {
		this.cameraShardId = cameraShardId;
	}
	
	@DynamoDBRangeKey(attributeName="imageKey")
	public String getImageKey() {
		return imageKey;
	}
	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}
	
	@DynamoDBAttribute(attributeName="imageTimestamp")
	public Long getImageTimestamp() {
		return imageTimestamp;
	}
	public void setImageTimestamp(Long imageTimestamp) {
		this.imageTimestamp = imageTimestamp;
	}
	
	@DynamoDBAttribute(attributeName="imageState")
	public String getImageState() {
		return imageState;
	}
	public void setImageState(String imageState) {
		this.imageState = imageState;
	}

}
