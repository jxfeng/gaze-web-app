package gaze.video.entity.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="ImageVariation")
public class DynamoDBImageVariation {

	private String imageKey;
	private String imageResolution;
	private String blobSource;
	private String blobId;
	private String blobContentType;
	private Integer blobLengthBytes;
	private String blobState;
	
	@DynamoDBHashKey(attributeName="imageKey")
	public String getImageKey() {
		return imageKey;
	}
	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}
	
	@DynamoDBRangeKey(attributeName="imageResolution")
	public String getImageResolution() {
		return imageResolution;
	}
	public void setImageResolution(String imageResolution) {
		this.imageResolution = imageResolution;
	}
	
	@DynamoDBAttribute(attributeName="blobSource")
	public String getBlobSource() {
		return blobSource;
	}
	public void setBlobSource(String blobSource) {
		this.blobSource = blobSource;
	}
	
	@DynamoDBAttribute(attributeName="blobId")
	public String getBlobId() {
		return blobId;
	}
	public void setBlobId(String blobId) {
		this.blobId = blobId;
	}
	
	@DynamoDBAttribute(attributeName="blobContentType")
	public String getBlobContentType() {
		return blobContentType;
	}
	public void setBlobContentType(String blobContentType) {
		this.blobContentType = blobContentType;
	}
	
	@DynamoDBAttribute(attributeName="blobLengthBytes")
	public Integer getBlobLengthBytes() {
		return blobLengthBytes;
	}
	public void setBlobLengthBytes(Integer blobLengthBytes) {
		this.blobLengthBytes = blobLengthBytes;
	}
	
	@DynamoDBAttribute(attributeName="blobState")
	public String getBlobState() {
		return blobState;
	}
	public void setBlobState(String blobState) {
		this.blobState = blobState;
	}
	

}
