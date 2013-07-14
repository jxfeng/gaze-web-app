package gaze.video.entity.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="Session")
public class DynamoDBSession {
	
	private String sessionId;
	private String userId;
	private Long startTime;
	private Long lastRequestTime;
	private String state;
	
	@DynamoDBHashKey(attributeName="sessionId")
	public String getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@DynamoDBAttribute(attributeName="userId")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@DynamoDBAttribute(attributeName="startTime")
	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	
	@DynamoDBAttribute(attributeName="lastRequestTime")
	public Long getLastRequestTime() {
		return lastRequestTime;
	}

	public void setLastRequestTime(Long lastRequestTime) {
		this.lastRequestTime = lastRequestTime;
	}
	
	@DynamoDBAttribute(attributeName="state")
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}

}
