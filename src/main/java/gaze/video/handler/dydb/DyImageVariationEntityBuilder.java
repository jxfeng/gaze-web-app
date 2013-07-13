package gaze.video.handler.dydb;

import gaze.video.entity.Camera;
import gaze.video.entity.Image;
import gaze.video.entity.ImageVariation;
import gaze.video.entity.dynamodb.DynamoDBCamera;
import gaze.video.entity.dynamodb.DynamoDBImage;
import gaze.video.entity.dynamodb.DynamoDBImageVariation;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.ec2.model.ImageState;

public class DyImageVariationEntityBuilder {

	public static ImageVariation build(DynamoDBImageVariation dyImageBlob) {
		ImageVariation blob = new ImageVariation();
		blob.setBlobResolution(ImageVariation.BlobResolution.valueOf(dyImageBlob.getImageResolution()));
		blob.setBlobSource(ImageVariation.BlobSource.valueOf(dyImageBlob.getBlobSource()));
		blob.setBlobId(dyImageBlob.getBlobId());
		blob.setBlobContentType(dyImageBlob.getBlobContentType());
		blob.setBlobLengthBytes(dyImageBlob.getBlobLengthBytes());
		return blob;
	}

	
}
