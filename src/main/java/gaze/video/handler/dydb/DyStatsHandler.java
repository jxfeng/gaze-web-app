package gaze.video.handler.dydb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import gaze.video.entity.CameraShardStats;
import gaze.video.entity.CameraStats;
import gaze.video.entity.ImageVariation.BlobVariation;
import gaze.video.entity.UserStats;
import gaze.video.entity.dynamodb.DynamoDBCamera;
import gaze.video.entity.dynamodb.DynamoDBCameraShard;
import gaze.video.entity.dynamodb.DynamoDBImage;
import gaze.video.entity.dynamodb.DynamoDBImageVariation;
import gaze.video.exception.ApplicationException;
import gaze.video.handler.StatsHandler;

public class DyStatsHandler implements StatsHandler {

	final AmazonDynamoDBClient client;
	public final static Logger LOG = LoggerFactory.getLogger(DyStatsHandler.class);
	
	public DyStatsHandler() {
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
	public UserStats getUserStats(String userId) throws ApplicationException {

		DynamoDBMapper mapper = new DynamoDBMapper(client);
		List<CameraStats> cameraStats = new ArrayList<CameraStats>();
		long numImages = 0L;
		Map<String, Long> numImagesPerVariation = new HashMap<String, Long>();
		Map<String, Long> spaceUsedPerVariation = new HashMap<String, Long>();
		
		for(BlobVariation var : BlobVariation.values()) {
			numImagesPerVariation.put(var.toString(), 0L);
			spaceUsedPerVariation.put(var.toString(), 0L);
		}
		
		//Get list of cameras
		DynamoDBCamera dyCamera = new DynamoDBCamera();
		dyCamera.setUserId(userId);
		DynamoDBQueryExpression<DynamoDBCamera> queryExpression = new DynamoDBQueryExpression<DynamoDBCamera>()
				.withHashKeyValues(dyCamera);
		
		//Go through each camera and collect stats
		List<DynamoDBCamera> cameraList = mapper.query(DynamoDBCamera.class, queryExpression);
		if(cameraList != null) {
			for(DynamoDBCamera cam : cameraList) {
				CameraStats stat = getCameraStats(userId, cam.getCameraId());
				if(stat != null) {
					cameraStats.add(stat);
					numImages += stat.getTotalImages();
					if(stat.getNumImagesByVariation() != null) {
						for(String key : numImagesPerVariation.keySet()) {
							Long ni = numImagesPerVariation.get(key);
							numImagesPerVariation.put(key, ni + stat.getNumImagesByVariation().get(key));
						}
					}
					if(stat.getBytesUsedByVariation() != null) {
						for(String key : spaceUsedPerVariation.keySet()) {
							Long ni = spaceUsedPerVariation.get(key);
							spaceUsedPerVariation.put(key, ni + stat.getBytesUsedByVariation().get(key));
						}
					}
				}
			}
		}
		
		//Build result
		UserStats stats = new UserStats(userId).withTotalImages(numImages).withBytesUsedByVariation(spaceUsedPerVariation)
				.withNumImagesByVariation(numImagesPerVariation).withCameraStats(cameraStats);
		return stats;
		
	}
	
	@Override
	public CameraStats getCameraStats(String userId, String cameraId) throws ApplicationException {

		DynamoDBMapper mapper = new DynamoDBMapper(client);
		List<CameraShardStats> shardStats = new ArrayList<CameraShardStats>();
		long numImages = 0L;
		Map<String, Long> numImagesPerVariation = new HashMap<String, Long>();
		Map<String, Long> spaceUsedPerVariation = new HashMap<String, Long>();
		
		for(BlobVariation var : BlobVariation.values()) {
			numImagesPerVariation.put(var.toString(), 0L);
			spaceUsedPerVariation.put(var.toString(), 0L);
		}
		
		//Get list of shards
		String hashKey = DyConfiguration.generateCameraKey(userId, cameraId);
		DynamoDBCameraShard dyShard = new DynamoDBCameraShard();
		dyShard.setCameraKey(hashKey);
		DynamoDBQueryExpression<DynamoDBCameraShard> queryExpression = new DynamoDBQueryExpression<DynamoDBCameraShard>()
				.withHashKeyValues(dyShard);
		
		//Go through each shard and collect stats
		List<DynamoDBCameraShard> shardList = mapper.query(DynamoDBCameraShard.class, queryExpression);
		if(shardList != null) {
			for(DynamoDBCameraShard shard : shardList) {
				CameraShardStats stat = getCameraShardStats(userId, cameraId, shard.getShardId());
				if(stat != null) {
					shardStats.add(stat);
					numImages += stat.getTotalImages();
					if(stat.getNumImagesByVariation() != null) {
						for(String key : numImagesPerVariation.keySet()) {
							Long ni = numImagesPerVariation.get(key);
							numImagesPerVariation.put(key, ni + stat.getNumImagesByVariation().get(key));
						}
					}
					if(stat.getBytesUsedByVariation() != null) {
						for(String key : spaceUsedPerVariation.keySet()) {
							Long ni = spaceUsedPerVariation.get(key);
							spaceUsedPerVariation.put(key, ni + stat.getBytesUsedByVariation().get(key));
						}
					}
				}
			}
		}
		
		//Build result
		CameraStats stats = new CameraStats(userId, cameraId).withShardStats(shardStats)
				.withTotalImages(numImages).withBytesUsedByVariation(spaceUsedPerVariation).withNumImagesByVariation(numImagesPerVariation);
		return stats;	
	}
	
	public CameraShardStats getCameraShardStats(String userId, String cameraId, Long shardId) throws ApplicationException {
		
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		
		//Initialize counters
		long numImages = 0L;
		Map<String, Long> numImagesPerVariation = new HashMap<String, Long>();
		Map<String, Long> spaceUsedPerVariation = new HashMap<String, Long>();
		
		for(BlobVariation var : BlobVariation.values()) {
			numImagesPerVariation.put(var.toString(), 0L);
			spaceUsedPerVariation.put(var.toString(), 0L);
		}
		
		//Setup hash key
		String hashKey = DyConfiguration.getShardKeyById(userId, cameraId, shardId);
		DynamoDBImage dyImage = new DynamoDBImage();
		dyImage.setShardKey(hashKey);
		LOG.info("Looking for shard shardId:" + shardId + " shardKey: " + hashKey);
		
		//Go through list of images
		DynamoDBQueryExpression<DynamoDBImage> query = new DynamoDBQueryExpression<DynamoDBImage>().withHashKeyValues(dyImage);
		List<DynamoDBImage> imageList = mapper.query(DynamoDBImage.class, query);
		if(imageList != null) {
			for(DynamoDBImage img : imageList) {
				numImages++;
				LOG.info("Image " + img.getImageKey());
				for(BlobVariation var : BlobVariation.values()) {
					DynamoDBImageVariation imgVar = mapper.load(DynamoDBImageVariation.class, img.getImageKey(), var.toString());
					if(imgVar != null) {
						Long ni = numImagesPerVariation.get(var.toString());
						numImagesPerVariation.put(var.toString(), ni + 1);
						Long si = spaceUsedPerVariation.get(var.toString());
						spaceUsedPerVariation.put(var.toString(), si + imgVar.getBlobLengthBytes());
					}
					else {
						LOG.error("Could not fetch stats for image variation imageKey:" + img.getImageKey() + " variation: " + var);
					}
				}
			}
			
			//Make stats object
			CameraShardStats stats = new CameraShardStats(userId, cameraId, shardId)
			.withTotalImages(numImages).withBytesUsedByVariation(spaceUsedPerVariation)
			.withNumImagesByVariation(numImagesPerVariation);
			return stats;
		} else {
			LOG.error("imageList is null");
		}

		LOG.error("Could not get stats for shard userId:" + userId + " cameraId: " + cameraId + " shardId: " + shardId);
		return null;
		
	}

	
	
}
