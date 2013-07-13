package gaze.video.handler.dydb;

import gaze.video.entity.Camera;
import gaze.video.entity.CameraState;
import gaze.video.entity.dynamodb.DynamoDBCamera;
import gaze.video.exception.ApplicationException;
import gaze.video.handler.CameraHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;



public class DyCameraHandler implements CameraHandler {

	final AmazonDynamoDBClient client;
	public final static Logger LOG = LoggerFactory.getLogger(DyCameraHandler.class);
	
	public DyCameraHandler() {
		AmazonDynamoDBClient thisClient = null;
		try {
			thisClient = new AmazonDynamoDBClient(
					new PropertiesCredentials(getClass().getClassLoader().getResourceAsStream("AwsCredentials.properties")));
		} catch(Exception exception) {
			LOG.error("Could not find AwsCredentials.properties file");
			thisClient = null;
		} finally {
			client = thisClient;
		}
	}

	@Override
	public Camera createNewCamera(String userId, String cameraId) throws ApplicationException {
		DynamoDBCamera dCamera = new DynamoDBCamera();
		dCamera.setUserId(userId);
		dCamera.setCameraId(cameraId);
		dCamera.setCameraState(CameraState.CREATED.toString());
		
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		mapper.save(dCamera);
		
		Camera camera = DyCameraEntityBuilder.build(dCamera);
		return camera;
	}

	@Override
	public Boolean doesExist(String userId, String cameraId) throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		DynamoDBCamera dCamera = mapper.load(DynamoDBCamera.class, userId, cameraId);
		if(dCamera == null) {
			LOG.info("Camera " + userId + "-" + cameraId + " was not found in the database");
			return false;
		}
		return true;
	}
	

	@Override
	public List<Camera> listCameras(String userId, String startCameraKey, Integer limit) throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		
		//Fix the limit to be sane [0,100]
		limit = Math.max(1, Math.min(limit, 100));
		
		//Match hash key
		Map<String, Condition> keyConditions = new HashMap<String, Condition>();
		Condition hashKeyCondition = new Condition()
	    									.withComparisonOperator(ComparisonOperator.EQ.toString())
	    									.withAttributeValueList(new AttributeValue().withS(userId));
		keyConditions.put("userId", hashKeyCondition);	
		
		//Issue the query - the mapper sucks for now!
		QueryRequest query = new QueryRequest()
									.withTableName("Camera")
									.withKeyConditions(keyConditions)
									.withConsistentRead(false)
									.withLimit(limit);
		//From where?
		if(startCameraKey != null) {
			Map<String, AttributeValue> startKey = new HashMap<String, AttributeValue>();
			startKey.put("userId", new AttributeValue(userId));
			startKey.put("cameraId", new AttributeValue(startCameraKey));
			query = query.withExclusiveStartKey(startKey);
		}
		
		//Process result
		QueryResult result = client.query(query);
		if(result != null) {
			List<DynamoDBCamera> cameraList = mapper.marshallIntoObjects(DynamoDBCamera.class, result.getItems());
			return DyCameraEntityBuilder.buildCameraList(cameraList);
		} else {
			LOG.error("Camera query failed for userId: " + userId + " startKey: " + startCameraKey + " limit: " + limit);
			throw ApplicationException.CAMERA_QUERY_FAILED;
		}
	}

	@Override
	public Camera getCameraDetails(String userId, String cameraId) throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		DynamoDBCamera dCamera = mapper.load(DynamoDBCamera.class, userId, cameraId);
		if(dCamera == null) {
			LOG.info("Camera " + userId + "-" + cameraId + " was not found in the database");
			throw ApplicationException.CAMERA_INVALID_CAMERA_ID;
		}
		Camera camera = DyCameraEntityBuilder.build(dCamera);
		return camera;
	}
	

	@Override
	public Camera updateLatestImageTimestamp(String userId, String cameraId, Long imageTimestamp) throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		DynamoDBCamera dCamera = mapper.load(DynamoDBCamera.class, userId, cameraId);
		if(dCamera == null) {
			LOG.info("Camera " + userId + "-" + cameraId + " was not found in the database");
			throw ApplicationException.CAMERA_INVALID_CAMERA_ID;
		}
		dCamera.setLastImageTimestamp(imageTimestamp);
		mapper.save(dCamera);
		Camera camera = DyCameraEntityBuilder.build(dCamera);
		return camera;
	}

	@Override
	public void deleteCamera(String userId, String cameraId) throws ApplicationException {
		// TODO Auto-generated method stub
		
	}





}
