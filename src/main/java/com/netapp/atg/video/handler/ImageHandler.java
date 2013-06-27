package com.netapp.atg.video.handler;

import java.util.List;

import com.netapp.atg.video.entity.Image;
import com.netapp.atg.video.entity.ImageVariation;
import com.netapp.atg.video.exception.ApplicationException;

public interface ImageHandler {
	
	/**
	 * Creates a shard for the image using its timestamp
	 * @param userId
	 * @param cameraId
	 * @param imageTimestamp
	 * @return
	 */
	public String createShard(String userId, String cameraId, Long imageTimestamp);
	
	/**
	 * Gets the shard id using the image timestamp
	 * @param userId
	 * @param cameraId
	 * @param imageTimestamp
	 * @return
	 */
	public String getShardId(String userId, String cameraId, Long imageTimestamp);
	
	/**
	 * Gets the next available shard after the current shard
	 * @param userId
	 * @param cameraId
	 * @param imageTimestamp
	 * @return
	 */
	public Long getNextShardId(String userId, String cameraId, Long imageTimestamp);
	
	/**
	 * Looks up image in the shard, creates or gets it
	 * @param shardId
	 * @param timestamp
	 * @return
	 */
	public Image createImage(String shardId, Long timestamp);
	
	/**
	 * Tells if the image exists in the database or not
	 * @param shardId
	 * @param timestamp
	 * @return
	 */
	public boolean doesImageExist(String shardId, Long timestamp);
	
	/**
	 * Gets all the details of the image
	 * @param shardId
	 * @param timestamp
	 * @return
	 */
	public Image getImage(String shardId, Long timestamp);
	
	/**
	 * Updates the image state to new state (Image must already exist)
	 * @param shardId
	 * @param timestamp
	 * @param newState
	 * @return
	 */
	public Image updateImageState(String shardId, Long timestamp, Image.ImageState newState);
	
	/**
	 * Lists all available images for the given camera
	 * @param shardId
	 * @param startTimestamp
	 * @param limit
	 * @return
	 * @throws ApplicationException
	 */
	public List<Image> listImages(String shardId, Long startTimestamp, Integer limit) throws ApplicationException;
	
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
			String blobContentType, Integer blobLengthBytes, ImageVariation.BlobSource blobSource, ImageVariation.BlobResolution blobResolution);
	
	public ImageVariation getImageVariation(String userId, String cameraId, Long imageTimestamp, ImageVariation.BlobResolution blobResolution);
	
	public List<ImageVariation> listImageVariations(String userId, String cameraId, Long imageTimestamp);
	
	public ImageVariation updateImageBlobState(String userId, String cameraId, Long imageTimestamp, ImageVariation.BlobResolution blobResolution, ImageVariation.BlobState blobState);
	
}
