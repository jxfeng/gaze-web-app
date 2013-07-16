package gaze.video.handler.dydb;

import gaze.video.entity.Image;
import gaze.video.entity.dynamodb.DynamoDBImage;

import java.util.ArrayList;
import java.util.List;

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
