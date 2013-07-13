package gaze.video.handler.dydb;

import gaze.video.entity.Image;
import gaze.video.entity.ImageVariation;
import gaze.video.entity.Image.ImageState;
import gaze.video.entity.ImageVariation.BlobResolution;
import gaze.video.entity.ImageVariation.BlobSource;
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
	public String createShard(String userId, String cameraId, Long imageTimestamp) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		Long shardDate = generateShardDate(imageTimestamp);
		String cameraKey = generateCameraKey(userId, cameraId);
		DynamoDBCameraShard dShard = mapper.load(DynamoDBCameraShard.class, cameraKey, shardDate);
		
		if(dShard != null) {
			LOG.info("Shard for camera" + cameraKey + " date: " + shardDate + " found");
			return dShard.getShardId();
		} else {
			LOG.info("Created shard for camera" + cameraKey + " date: " + shardDate);
			dShard = new DynamoDBCameraShard();
			dShard.setCameraKey(cameraKey);
			dShard.setShardDate(shardDate);
			dShard.setShardComplete(false);
			dShard.setShardId(generateShardId(userId, cameraId, imageTimestamp));
			mapper.save(dShard);
			return dShard.getShardId();
		}
	}

	@Override
	public String getShardId(String userId, String cameraId, Long imageTimestamp) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		Long shardDate = generateShardDate(imageTimestamp);
		String cameraKey = generateCameraKey(userId, cameraId);
		DynamoDBCameraShard dShard = mapper.load(DynamoDBCameraShard.class, cameraKey, shardDate);
		if(dShard == null) {
			LOG.info("Shard for camera" + cameraId + " date: " + shardDate + " not found");
			return null;
		}
		return dShard.getShardId();
	}
	
	@Override
	public Long getNextShardId(String userId, String cameraId, Long imageTimestamp) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		Long shardDate = generateShardDate(imageTimestamp);
		String cameraKey = generateCameraKey(userId, cameraId);
		
		//Match hash key
		Map<String, Condition> keyConditions = new HashMap<String, Condition>();
		Condition hashKeyCondition = new Condition()
	    									.withComparisonOperator(ComparisonOperator.EQ.toString())
	    									.withAttributeValueList(new AttributeValue().withS(cameraKey));
		keyConditions.put("cameraKey", hashKeyCondition);	
		
		//Issue the query - the mapper sucks for now!
		QueryRequest query = new QueryRequest()
									.withTableName("CameraShard")
									.withKeyConditions(keyConditions)
									.withConsistentRead(false)
									.withLimit(10);
		//From where?
		if(imageTimestamp != null) { 
			Map<String, AttributeValue> startKey = new HashMap<String, AttributeValue>();
			startKey.put("cameraKey", new AttributeValue(cameraKey));
			startKey.put("shardDate", new AttributeValue(shardDate.toString()));
			query = query.withExclusiveStartKey(startKey);
		}
		
		//Process result
		//TODO: Check that string sorting behaves correctly, the dates should be in order
		QueryResult result = client.query(query);
		if(result != null) {
			List<DynamoDBCameraShard> dShards = mapper.marshallIntoObjects(DynamoDBCameraShard.class, result.getItems());
			List<Long> dates = new ArrayList<Long>();
			for(DynamoDBCameraShard shard : dShards) {
				dates.add(shard.getShardDate());
			}
			Collections.sort(dates);
			return dates.get(0);
		}
		return null;
	}

	@Override
	public Image createImage(String shardId, Long timestamp) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		String imageKey = generateImageKey(shardId, timestamp);
		DynamoDBImage dImage = mapper.load(DynamoDBImage.class, shardId, imageKey);
		if(dImage != null) {
			LOG.info("Found image shard:" + shardId + " imageKey:" + imageKey);
			return DyImageEntityBuilder.build(dImage);
		} else {
			dImage = new DynamoDBImage();
			dImage.setCameraShardId(shardId);
			dImage.setImageKey(imageKey);
			dImage.setImageState(Image.ImageState.CREATED.toString());
			dImage.setImageTimestamp(timestamp);
			mapper.save(dImage);
			return DyImageEntityBuilder.build(dImage);
		}
	}

	@Override
	public boolean doesImageExist(String shardId, Long timestamp) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		String imageKey = generateImageKey(shardId, timestamp);
		DynamoDBImage dImage = mapper.load(DynamoDBImage.class, shardId, imageKey);
		if(dImage != null) {
			LOG.info("Found image shard:" + shardId + " imageKey:" + imageKey);
			return true;
		}
		return false;
	}

	@Override
	public Image getImage(String shardId, Long timestamp) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		String imageKey = generateImageKey(shardId, timestamp);
		DynamoDBImage dImage = mapper.load(DynamoDBImage.class, shardId, imageKey);
		if(dImage != null) {
			LOG.info("Found image shard:" + shardId + " imageKey:" + imageKey);
			return DyImageEntityBuilder.build(dImage);
		}
		return null;
	}

	@Override
	public Image updateImageState(String shardId, Long timestamp, ImageState newState) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		String imageKey = generateImageKey(shardId, timestamp);
		DynamoDBImage dImage = mapper.load(DynamoDBImage.class, shardId, imageKey);
		if(dImage != null) {
			LOG.info("Found image shard:" + shardId + " imageKey:" + imageKey);
			dImage.setImageState(newState.toString());
			mapper.save(dImage);
			return DyImageEntityBuilder.build(dImage);
		}
		return null;
	}

	@Override
	public List<Image> listImages(String shardId, Long startImageTimestamp, Integer limit) throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		limit = Math.max(1, Math.min(limit, 100));
		
		//Match hash key
		Map<String, Condition> keyConditions = new HashMap<String, Condition>();
		Condition hashKeyCondition = new Condition()
	    									.withComparisonOperator(ComparisonOperator.EQ.toString())
	    									.withAttributeValueList(new AttributeValue().withS(shardId));
		keyConditions.put("cameraShardId", hashKeyCondition);	
		
		//Issue the query - the mapper sucks for now!
		QueryRequest query = new QueryRequest()
									.withTableName("Image")
									.withKeyConditions(keyConditions)
									.withConsistentRead(false)
									.withLimit(limit);
		//From where?
		if(startImageTimestamp != null) {
			Map<String, AttributeValue> startKey = new HashMap<String, AttributeValue>();
			startKey.put("cameraShardId", new AttributeValue(shardId));
			startKey.put("imageKey", new AttributeValue(generateImageKey(shardId, startImageTimestamp)));
			query = query.withExclusiveStartKey(startKey);
		}
		
		//Process result
		QueryResult result = client.query(query);
		if(result != null) {
			List<DynamoDBImage> imageList = mapper.marshallIntoObjects(DynamoDBImage.class, result.getItems());
			LOG.info("Found " + imageList.size() + " images in shard " + shardId);
			return DyImageEntityBuilder.buildImageList(imageList);
		} else {
			LOG.error("Image query failed for shardId: " + shardId + " startImageTimestamp: " + startImageTimestamp + " limit: " + limit);
			throw ApplicationException.IMAGE_QUERY_FAILED;
		}
	}

	@Override
	public ImageVariation createImageBlob(String userId, String cameraId, Long imageTimestamp, String blobContentType, Integer blobLengthBytes, BlobSource blobSource, BlobResolution blobResolution) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		String imageKey = generateImageKey(generateShardId(userId, cameraId, imageTimestamp), imageTimestamp);
		String blobResolutionString = blobResolution.toString();
		DynamoDBImageVariation dImageBlob = mapper.load(DynamoDBImageVariation.class, imageKey, blobResolutionString);
		if(dImageBlob != null) {
			LOG.info("Found image blob: " + dImageBlob);
			return DyImageVariationEntityBuilder.build(dImageBlob);
		} else {
			dImageBlob = new DynamoDBImageVariation();
			dImageBlob.setImageKey(imageKey);
			dImageBlob.setImageResolution(blobResolutionString);
			dImageBlob.setBlobSource(blobSource.toString());
			dImageBlob.setBlobId(generateBlobId(generateShardId(userId, cameraId, imageTimestamp), imageTimestamp, blobResolution));
			dImageBlob.setBlobContentType(blobContentType);
			dImageBlob.setBlobLengthBytes(blobLengthBytes);
			mapper.save(dImageBlob);
			return DyImageVariationEntityBuilder.build(dImageBlob);
		}
	}
	
	@Override
	public ImageVariation getImageVariation(String userId, String cameraId, Long imageTimestamp, ImageVariation.BlobResolution blobResolution) {
		
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		String imageKey = generateImageKey(generateShardId(userId, cameraId, imageTimestamp), imageTimestamp);
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
	public List<ImageVariation> listImageVariations(String userId, String cameraId, Long imageTimestamp) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ImageVariation updateImageBlobState(String userId, String cameraId, Long imageTimestamp, BlobResolution blobResolution, ImageVariation.BlobState blobState) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		String imageKey = generateImageKey(generateShardId(userId, cameraId, imageTimestamp), imageTimestamp);
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
	
	private Long generateShardDate(Long timestamp) {
		return timestamp/1000000000;
	}
	
	private String generateCameraKey(String userId, String cameraId) {
		return "user-" + userId + "-camera-" + cameraId;
	}
	
	private String generateShardId(String userId, String cameraId, Long timestamp) {
		return "user-" + userId + "-camera-" + cameraId + "-shard-" + generateShardDate(timestamp);
	}
	
	private String generateImageKey(String shardKey, Long timestamp) {
		return shardKey + "-imagets-" + timestamp;
	}

	private String generateBlobId(String shardKey, Long timestamp, BlobResolution blobResolution) {
		return shardKey + "-imagets-" + timestamp + "-resolution-" + blobResolution.toString();
	}


}
