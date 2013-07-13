package gaze.video.handler;


import gaze.video.entity.ImageBlob;
import gaze.video.exception.ApplicationException;


public interface ImageBlobHandler {

	public void createImageBlob(String blobId, String encoding, byte[] data) throws ApplicationException;
	
	public ImageBlob getImageBlob(String blobId) throws ApplicationException;
	
}
