package com.netapp.atg.video.handler.dydb;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.ec2.model.ImageState;
import com.netapp.atg.video.entity.Camera;
import com.netapp.atg.video.entity.Image;
import com.netapp.atg.video.entity.ImageVariation;
import com.netapp.atg.video.entity.dynamodb.DynamoDBCamera;
import com.netapp.atg.video.entity.dynamodb.DynamoDBImage;
import com.netapp.atg.video.entity.dynamodb.DynamoDBImageVariation;

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
