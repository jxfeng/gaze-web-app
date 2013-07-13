package gaze.video.handler.s3;

import gaze.video.entity.ImageBlob;
import gaze.video.exception.ApplicationException;
import gaze.video.handler.ImageBlobHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.sun.corba.se.impl.ior.ByteBuffer;

public class S3ImageBlobHandler implements ImageBlobHandler {

	final AmazonS3Client client;
	final String BUCKET_NAME = "gokulapptestbucket";
	public final static Logger LOG = LoggerFactory.getLogger(S3ImageBlobHandler.class);
	
	public S3ImageBlobHandler() {
		AmazonS3Client thisClient = null;
		try {
			thisClient = new AmazonS3Client(
					new PropertiesCredentials(getClass().getClassLoader().getResourceAsStream("AwsCredentials.properties")));
			
			//Make sure bucket exists
			if(!thisClient.doesBucketExist(BUCKET_NAME)) {
				LOG.error("S3 bucket to store images doesn't exist");
				//TODO: Throw exception
			}
			//TODO: Check bucket permissions
			
		} catch(Exception exception) {
			LOG.error("Could not find AwsCredentials.properties file");
			thisClient = null;
		} finally {
			client = thisClient;
		}
	}

	@Override
	public void createImageBlob(String blobId, String encoding, byte[] data) throws ApplicationException {

		//Check if blob exists
		ObjectMetadata existingObjectMetadata;
		boolean blobAlreadyExists = false;
		try {
			existingObjectMetadata = client.getObjectMetadata(BUCKET_NAME, blobId);
			LOG.info("Blob " + blobId + " already exists");
			blobAlreadyExists = true;
		} catch(AmazonS3Exception exception) {

		}

		//Add the blob
		try {
			//Fill out the blob's metadata
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentEncoding(encoding);
			metadata.setContentLength(data.length);
			//metadata.setContentMD5(md5Base64);

			//Put into S3
			PutObjectResult result = client.putObject(BUCKET_NAME, blobId, new ByteArrayInputStream(data), metadata);
		} catch(Exception e) {
			LOG.error("Got an exception while uploading into S3");
			throw ApplicationException.IMAGE_INVALID_IMAGE_ID; //TODO: Fix this
		}

	}
	
	@Override
	public ImageBlob getImageBlob(String blobId) throws ApplicationException {
		try {
			S3Object sBlob = client.getObject(BUCKET_NAME, blobId);
			
			//The blob doesn't exist
			if(sBlob == null) {
				LOG.info("S3 did not have blob with blobId: " + blobId);
				throw ApplicationException.BLOB_INVALID_BLOB_ID;
			}
			
			//Convert to my blob
			ImageBlob blob = S3ImageBlobEntityBuilder.build(sBlob);
			return blob;
		} catch(AmazonS3Exception exception) {
			LOG.error("S3 failed while getting blob with blobId: " + blobId);
			throw ApplicationException.BLOB_FETCH_FAILED;
		}
	}

}
