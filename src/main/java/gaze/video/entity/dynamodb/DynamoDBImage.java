package gaze.video.entity.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="Image")
public class DynamoDBImage {

	private String shardKey;
	private String imageKey;
	private Long   imageTimestamp;
	private String imageState;
	
	@DynamoDBHashKey(attributeName="shardKey")
	public String getShardKey() {
		return shardKey;
	}
	public void setShardKey(String shardKey) {
		this.shardKey = shardKey;
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
