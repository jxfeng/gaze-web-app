package gaze.video.handler;

import gaze.video.entity.CameraShard;
import gaze.video.entity.Image;
import gaze.video.entity.ImageVariation;
import gaze.video.exception.ApplicationException;

import java.util.List;


public interface ImageHandler {
	
	/**
	 * Creates a shard for the image using its timestamp
	 * @param userId
	 * @param cameraId
	 * @param imageTimestamp
	 * @return
	 */
	public CameraShard getShard(String userId, String cameraId, Long imageTimestamp) throws ApplicationException;
	
	/**
	 * Gets the previous available shard before the current shard
	 * @param userId
	 * @param cameraId
	 * @param imageTimestamp
	 * @return
	 */
	public CameraShard getPreviousShard(String userId, String cameraId, Long imageTimestamp) throws ApplicationException;
	
	/**
	 * Gets the next available shard after the current shard
	 * @param userId
	 * @param cameraId
	 * @param imageTimestamp
	 * @return
	 */
	public CameraShard getNextShard(String userId, String cameraId, Long imageTimestamp) throws ApplicationException;
	
	/**
	 * Lists available shards
	 * @param userId
	 * @param cameraId
	 * @param fromTimestamp
	 * @param reverse
	 * @param limit
	 * @return
	 */
	public List<CameraShard> listShards(String userId, String cameraId, Long fromTimestamp, Boolean reverse, Integer limit) throws ApplicationException;
	
	/**
	 * Looks up image in the shard, creates or gets it
	 * @param shardId
	 * @param timestamp
	 * @return
	 */
	public Image createImage(CameraShard shard, Long timestamp) throws ApplicationException;
	
	/**
	 * Tells if the image exists in the database or not
	 * @param shardId
	 * @param timestamp
	 * @return
	 */
	public boolean doesImageExist(CameraShard shard, Long timestamp) throws ApplicationException;
	
	/**
	 * Gets all the details of the image
	 * @param shardId
	 * @param timestamp
	 * @return
	 */
	public Image getImage(CameraShard shard, Long timestamp) throws ApplicationException;
	
	/**
	 * Updates the image state to new state (Image must already exist)
	 * @param shardId
	 * @param timestamp
	 * @param newState
	 * @return
	 */
	public Image updateImageState(CameraShard shard, Long timestamp, Image.ImageState newState) throws ApplicationException;
	
	/**
	 * Lists all available images for the given camera
	 * @param shardId
	 * @param startTimestamp
	 * @param reverse
	 * @param limit
	 * @return
	 * @throws ApplicationException
	 */
	public List<Image> listImages(CameraShard shard, Long startTimestamp, Boolean reverse, Integer limit) throws ApplicationException;
	
	/**
	 * Creates or gets the blob
	 * @param userId
	 * @param cameraId
	 * @param imageTimestamp
	 * @param blobSource
	 * @param blobResolution
	 * @return
	 */
	public ImageVariation createImageBlob(String userId, String cameraId, Long imageTimestamp, 
			String blobContentType, Integer blobLengthBytes, ImageVariation.BlobSource blobSource, ImageVariation.BlobVariation blobResolution) throws ApplicationException;
	
	public ImageVariation getImageVariation(String userId, String cameraId, Long imageTimestamp, ImageVariation.BlobVariation blobResolution) throws ApplicationException;
	
	public List<ImageVariation> listImageVariations(String userId, String cameraId, Long imageTimestamp) throws ApplicationException;
	
	public ImageVariation updateImageBlobState(String userId, String cameraId, Long imageTimestamp, ImageVariation.BlobVariation blobResolution, ImageVariation.BlobState blobState) throws ApplicationException;
	
}
