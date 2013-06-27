package com.netapp.atg.video.entity.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="CameraShard")
public class DynamoDBCameraShard {
	
	private String cameraKey;
	private Long shardDate;
	private String shardId;
	private Boolean shardComplete;
	//TODO: Keep track of resizer status here
	
	@DynamoDBHashKey(attributeName="cameraKey")
	public String getCameraKey() {
		return cameraKey;
	}
	public void setCameraKey(String cameraKey) {
		this.cameraKey = cameraKey;
	}
	
	@DynamoDBRangeKey(attributeName="shardDate")
	public Long getShardDate() {
		return shardDate;
	}
	public void setShardDate(Long shardDate) {
		this.shardDate = shardDate;
	}
	
	@DynamoDBAttribute(attributeName="shardId")
	public String getShardId() {
		return shardId;
	}
	public void setShardId(String shardId) {
		this.shardId = shardId;
	}

	@DynamoDBAttribute(attributeName="shardComplete")
	public Boolean getShardComplete() {
		return shardComplete;
	}
	public void setShardComplete(Boolean shardComplete) {
		this.shardComplete = shardComplete;
	}

	
	
}
