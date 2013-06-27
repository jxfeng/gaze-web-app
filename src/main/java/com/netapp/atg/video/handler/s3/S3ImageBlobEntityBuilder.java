package com.netapp.atg.video.handler.s3;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.model.S3Object;
import com.netapp.atg.video.entity.ImageBlob;
import com.netapp.atg.video.exception.ApplicationException;

public class S3ImageBlobEntityBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(S3ImageBlobEntityBuilder.class);
	
	public static ImageBlob build(S3Object object) throws ApplicationException {
		
		try {
			ImageBlob blob = new ImageBlob();
			blob.setBlobId(object.getKey());
			blob.setBlobContentType(object.getObjectMetadata().getContentType());
			blob.setBlobContents(IOUtils.toByteArray(object.getObjectContent()));
			return blob;
		} catch(Exception e) {
			LOG.error("Could not convert blob from S3 for blobId: " + object.getKey());
			throw ApplicationException.BLOB_COULD_NOT_CONVERT;
		}
	}
	
}
