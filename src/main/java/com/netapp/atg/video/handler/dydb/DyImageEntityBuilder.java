package com.netapp.atg.video.handler.dydb;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.ec2.model.ImageState;
import com.netapp.atg.video.entity.Camera;
import com.netapp.atg.video.entity.Image;
import com.netapp.atg.video.entity.dynamodb.DynamoDBCamera;
import com.netapp.atg.video.entity.dynamodb.DynamoDBImage;

public class DyImageEntityBuilder {

	public static Image build(DynamoDBImage dyImage) {
		Image image = new Image();
		image.setImageState(Image.ImageState.valueOf(dyImage.getImageState()));
		image.setImageTimestamp(dyImage.getImageTimestamp());
		return image;
	}
	
	public static List<Image> buildImageList(List<DynamoDBImage> dyImageList) {
		List<Image> imageList = new ArrayList<Image>();
		if(dyImageList != null && dyImageList.size() > 0) {
			for(DynamoDBImage dyImage : dyImageList) {
				imageList.add(build(dyImage));
			}
		}
		return imageList;
	}
	
}
