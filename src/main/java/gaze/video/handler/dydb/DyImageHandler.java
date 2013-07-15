package gaze.video.handler.dydb;

import gaze.video.entity.CameraShard;
import gaze.video.entity.Image;
import gaze.video.entity.ImageVariation;
import gaze.video.entity.Image.ImageState;
import gaze.video.entity.ImageVariation.BlobVariation;
import gaze.video.entity.ImageVariation.BlobSource;
import gaze.video.entity.dynamodb.DynamoDBCamera;
import gaze.video.entity.dynamodb.DynamoDBCameraShard;
import gaze.video.entity.dynamodb.DynamoDBImage;
import gaze.video.entity.dynamodb.DynamoDBImageVariation;
import gaze.video.exception.ApplicationException;
import gaze.video.handler.ImageHandler;

import java.util.ArrayList;
import java.util.Collections;
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



public class DyImageHandler implements ImageHandler {

	final AmazonDynamoDBClient client;
	public final static Logger LOG = LoggerFactory.getLogger(DyImageHandler.class);
	
	public DyImageHandler() {
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
	public CameraShard getShard(String userId, String cameraId, Long imageTimestamp)  throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		Long shardId = getShardId(imageTimestamp);
		String cameraKey = generateCameraKey(userId, cameraId);
		DynamoDBCameraShard dShard = mapper.load(DynamoDBCameraShard.class, cameraKey, shardId);
		
		//Return existing
		if(dShard != null) {
			LOG.debug("Shard for userId: " + userId + " cameraId: " + cameraId + " shardId: " + shardId + " already exists");
			return DyCameraShardEntityBuilder.build(dShard);
		} 
		
		//Create new and return that
		else {
			LOG.debug("Created shard for userId: " + userId + " cameraId: " + cameraId + " shardId: " + shardId + " already exists");
			dShard = new DynamoDBCameraShard();
			dShard.setUserId(userId);
			dShard.setCameraId(cameraId);
			dShard.setCameraKey(cameraKey);
			dShard.setShardId(shardId);
			dShard.setShardComplete(false);
			dShard.setShardKey(getShardKey(userId, cameraId, imageTimestamp));
			mapper.save(dShard);
			return DyCameraShardEntityBuilder.build(dShard);
		}
	}
	
	@Override
	public CameraShard getPreviousShard(String userId, String cameraId, Long imageTimestamp)  throws ApplicationException {
		List<CameraShard> shardList = listShards(userId, cameraId, imageTimestamp, true, 1);
		if(shardList != null && shardList.size() > 0) {
			return shardList.get(0);
		}
		return null;
	}
	
	@Override
	public CameraShard getNextShard(String userId, String cameraId, Long imageTimestamp)  throws ApplicationException {
		List<CameraShard> shardList = listShards(userId, cameraId, imageTimestamp, false, 1);
		if(shardList != null && shardList.size() > 0) {
			return shardList.get(0);
		}
		return null;
	}
	
	@Override
	public List<CameraShard> listShards(String userId, String cameraId, Long fromTimestamp, Boolean reverse, Integer limit)  throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		Long shardId = getShardId(fromTimestamp);
		String cameraKey = generateCameraKey(userId, cameraId);
		
		//Match hash key
		Map<String, Condition> keyConditions = new HashMap<String, Condition>();
		Condition hashKeyCondition = new Condition()
	    									.withComparisonOperator(ComparisonOperator.EQ.toString())
	    									.withAttributeValueList(new AttributeValue().withS(cameraKey));
		keyConditions.put("cameraKey", hashKeyCondition);
		
		//Iterator over range key
		if(fromTimestamp != null) {
			if(!reverse) {
				LOG.info("Forward iterator on camera shard userId: " + userId + " cameraId: " + cameraId + " from: " + shardId);
				Condition rangeKeyCondition = new Condition()
					.withComparisonOperator(ComparisonOperator.GT.toString())
					.withAttributeValueList(new AttributeValue().withN(shardId.toString()));
				keyConditions.put("shardId", rangeKeyCondition);
			} else {
				LOG.info("Reverse iterator on camera shard userId: " + userId + " cameraId: " + cameraId + " from: " + shardId);
				Condition rangeKeyCondition = new Condition()
				.withComparisonOperator(ComparisonOperator.LT.toString())
				.withAttributeValueList(new AttributeValue().withN(shardId.toString()));
				keyConditions.put("shardId", rangeKeyCondition);
			}
		}
		
		//Issue the query - the mapper sucks for now!
		QueryRequest query = new QueryRequest()
									.withTableName("CameraShard")
									.withKeyConditions(keyConditions)
									.withConsistentRead(false)
									.withScanIndexForward(!reverse)
									.withLimit(limit);
		
		//Process result
		QueryResult result = client.query(query);
		if(result != null) {
			List<DynamoDBCameraShard> shardList = mapper.marshallIntoObjects(DynamoDBCameraShard.class, result.getItems());
			return DyCameraShardEntityBuilder.buildShardList(shardList);
		} else {
			LOG.info("Failed iterator on camera shard userId: " + userId + " cameraId: " + cameraId + " from: " + shardId);
			throw ApplicationException.SHARD_QUERY_FAILED;
		}
		
	}

	@Override
	public Image createImage(CameraShard shard, Long timestamp)  throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		String shardKey = getShardKey(shard);
		String imageKey = getImageKey(shardKey, timestamp);
		DynamoDBImage dImage = mapper.load(DynamoDBImage.class, shardKey, imageKey);
		
		//Image entry already exists
		if(dImage != null) {
			LOG.info("Found image in shard with shardKey:" + shardKey + " imageKey:" + imageKey);
			return DyImageEntityBuilder.build(dImage);
		} 
		
		//Create new image entry
		else {
			LOG.info("Created new image in shard with shardKey:" + shardKey + " imageKey:" + imageKey);
			dImage = new DynamoDBImage();
			dImage.setShardKey(shardKey);
			dImage.setImageKey(imageKey);
			dImage.setImageState(Image.ImageState.CREATED.toString());
			dImage.setImageTimestamp(timestamp);
			mapper.save(dImage);
			return DyImageEntityBuilder.build(dImage);
		}
	}

	@Override
	public boolean doesImageExist(CameraShard shard, Long timestamp)  throws ApplicationException{
		return (getImage(shard, timestamp) != null);
	}

	@Override
	public Image getImage(CameraShard shard, Long timestamp)  throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		String shardKey = getShardKey(shard);
		String imageKey = getImageKey(shardKey, timestamp);
		
		DynamoDBImage dImage = mapper.load(DynamoDBImage.class, shardKey, imageKey);
		if(dImage != null) {
			LOG.debug("Found image ts " + timestamp + " in shard shardKey:" + shardKey + " imageKey:" + imageKey);
			return DyImageEntityBuilder.build(dImage);
		}
		
		LOG.debug("Did not find image ts " + timestamp + " in shard shardKey:" + shardKey + " imageKey:" + imageKey);
		return null;
	}

	@Override
	public Image updateImageState(CameraShard shard, Long timestamp, ImageState newState)  throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		String shardKey = getShardKey(shard);
		String imageKey = getImageKey(shardKey, timestamp);
		
		DynamoDBImage dImage = mapper.load(DynamoDBImage.class, shardKey, imageKey);
		if(dImage != null) {
			LOG.debug("Updated image ts " + timestamp + " in shard shardKey:" + shardKey + " imageKey:" + imageKey + " to state:" + newState);
			dImage.setImageState(newState.toString());
			mapper.save(dImage);
			return DyImageEntityBuilder.build(dImage);
		}
		
		LOG.debug("Could not update image ts " + timestamp + " in shard shardKey:" + shardKey + " imageKey:" + imageKey + " to state:" + newState);
		return null;
	}

	@Override
	public List<Image> listImages(CameraShard shard, Long since, Boolean reverse, Integer limit) throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		String shardKey = getShardKey(shard);

		//Fix input arguments
		limit = (limit != null) ? Math.max(1, Math.min(limit, 100)) : 10;
		reverse = (reverse == null) ? false : reverse;

		//Match hash key
		Map<String, Condition> keyConditions = new HashMap<String, Condition>();
		Condition hashKeyCondition = new Condition()
		.withComparisonOperator(ComparisonOperator.EQ.toString())
		.withAttributeValueList(new AttributeValue().withS(shardKey));
		keyConditions.put("shardKey", hashKeyCondition);	

		//Am I given a start key?
		if(since != null) {
			if(!reverse) {
				Condition rangeKeyCondition = new Condition()
				.withComparisonOperator(ComparisonOperator.GT.toString())
				.withAttributeValueList(new AttributeValue().withS(getImageKey(shardKey, since)));
				keyConditions.put("imageKey", rangeKeyCondition);
			} else {
				Condition rangeKeyCondition = new Condition()
				.withComparisonOperator(ComparisonOperator.LT.toString())
				.withAttributeValueList(new AttributeValue().withS(getImageKey(shardKey, since)));
				keyConditions.put("imageKey", rangeKeyCondition);
			}
		}

		//Issue the query - the mapper sucks for now!
		QueryRequest query = new QueryRequest()
		.withTableName("Image")
		.withKeyConditions(keyConditions)
		.withScanIndexForward(!reverse)
		.withConsistentRead(false)
		.withLimit(limit);

		//Process result
		QueryResult result = client.query(query);
		if(result != null) {
			List<DynamoDBImage> imageList = mapper.marshallIntoObjects(DynamoDBImage.class, result.getItems());
			LOG.info("Found " + imageList.size() + " images in shard " + shardKey);
			return DyImageEntityBuilder.buildImageList(imageList);
		} else {
			LOG.error("Image query failed for shardKey: " + shardKey + " since: " + since + " limit: " + limit);
			throw ApplicationException.IMAGE_QUERY_FAILED;
		}
	}

	@Override
	public ImageVariation createImageBlob(String userId, String cameraId, Long imageTimestamp, String blobContentType, 
			Integer blobLengthBytes, BlobSource blobSource, BlobVariation blobResolution)  throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		String imageKey = getImageKey(getShardKey(userId, cameraId, imageTimestamp), imageTimestamp);
		String blobResolutionString = blobResolution.toString();
		DynamoDBImageVariation dImageBlob = mapper.load(DynamoDBImageVariation.class, imageKey, blobResolutionString);
		if(dImageBlob != null) {
			LOG.info("Found image blob: " + dImageBlob);
			return DyImageVariationEntityBuilder.build(dImageBlob);
		} else {
			dImageBlob = new DynamoDBImageVariation();
			dImageBlob.setImageKey(imageKey);
			dImageBlob.setImageVariation(blobResolutionString);
			dImageBlob.setBlobSource(blobSource.toString());
			dImageBlob.setBlobId(generateBlobId(getShardKey(userId, cameraId, imageTimestamp), imageTimestamp, blobResolution));
			dImageBlob.setBlobContentType(blobContentType);
			dImageBlob.setBlobLengthBytes(blobLengthBytes);
			mapper.save(dImageBlob);
			return DyImageVariationEntityBuilder.build(dImageBlob);
		}
	}
	
	@Override
	public ImageVariation getImageVariation(String userId, String cameraId, Long imageTimestamp, 
			ImageVariation.BlobVariation blobResolution)  throws ApplicationException {
		
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		String imageKey = getImageKey(getShardKey(userId, cameraId, imageTimestamp), imageTimestamp);
		String blobResolutionString = blobResolution.toString();
		DynamoDBImageVariation dImageBlob = mapper.load(DynamoDBImageVariation.class, imageKey, blobResolutionString);
		if(dImageBlob != null) {
			LOG.info("Found image blob: " + dImageBlob);
			return DyImageVariationEntityBuilder.build(dImageBlob);
		}
		
		LOG.info("Did not find blob for imageTS: " + imageTimestamp);
		return null;
	}

	@Override
	public List<ImageVariation> listImageVariations(String userId, String cameraId, Long imageTimestamp)  throws ApplicationException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ImageVariation updateImageBlobState(String userId, String cameraId, Long imageTimestamp, 
			BlobVariation blobResolution, ImageVariation.BlobState blobState)  throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		String imageKey = getImageKey(getShardKey(userId, cameraId, imageTimestamp), imageTimestamp);
		String blobResolutionString = blobResolution.toString();
		DynamoDBImageVariation dImageBlob = mapper.load(DynamoDBImageVariation.class, imageKey, blobResolutionString);
		if(dImageBlob != null) {
			LOG.info("Found image blob: " + dImageBlob);
			dImageBlob.setBlobState(blobState.toString());
			mapper.save(dImageBlob);
			return DyImageVariationEntityBuilder.build(dImageBlob);
		}
		return null;
	}
	
	@Override
	public Long getStartTimestamp(Long shardId) {
		Long startTimestamp = shardId * 1000000000;
		return startTimestamp;
	}
	
	@Override
	public Long getEndTimestamp(Long shardId) {
		Long endTimestamp = ((shardId + 1) * 1000000000) - 1;
		return endTimestamp;
	}
	
	private Long getShardId(Long timestamp) {
		return timestamp/1000000000;
	}
	
	private String generateCameraKey(String userId, String cameraId) {
		return "user-" + userId + "-camera-" + cameraId;
	}
	
	private String getShardKey(CameraShard shard) {
		return getShardKey(shard.getUserId(), shard.getCameraId(), shard.getShardBeginTimestamp());
	}
	
	private String getShardKey(String userId, String cameraId, Long timestamp) {
		return "user-" + userId + "-camera-" + cameraId + "-shard-" + String.format("%010d", getShardId(timestamp));
	}
	
	private String getImageKey(String shardKey, Long timestamp) {
		return shardKey + "-imagets-" + String.format("%020d", timestamp);
	}

	private String generateBlobId(String shardKey, Long timestamp, BlobVariation blobResolution) {
		return shardKey + "-imagets-" + String.format("%020d", timestamp) + "-resolution-" + blobResolution.toString();
	}


}
