package com.netapp.atg.video.handler;


import com.netapp.atg.video.entity.ImageBlob;
import com.netapp.atg.video.exception.ApplicationException;

public interface ImageBlobHandler {

	public void createImageBlob(String blobId, String encoding, byte[] data) throws ApplicationException;
	
	public ImageBlob getImageBlob(String blobId) throws ApplicationException;
	
}
