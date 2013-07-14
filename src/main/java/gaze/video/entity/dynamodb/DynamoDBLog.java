package gaze.video.entity.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;


@DynamoDBTable(tableName="Log")
public class DynamoDBLog {

	private String sessionId;
	private String requestId;
	private Long   requestTimestamp;
	private String requestName;
	
	@DynamoDBHashKey(attributeName="sessionId")
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@DynamoDBRangeKey(attributeName="requestId")
	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	@DynamoDBAttribute(attributeName="requestTimestamp")
	public Long getRequestTimestamp() {
		return requestTimestamp;
	}

	public void setRequestTimestamp(Long requestTimestamp) {
		this.requestTimestamp = requestTimestamp;
	}
	
	@DynamoDBAttribute(attributeName="requestName")
	public String getRequestName() {
		return requestName;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

}
