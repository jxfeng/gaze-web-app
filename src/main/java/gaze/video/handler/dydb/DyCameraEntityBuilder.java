package gaze.video.handler.dydb;

import gaze.video.entity.Camera;
import gaze.video.entity.dynamodb.DynamoDBCamera;

import java.util.ArrayList;
import java.util.List;


public class DyCameraEntityBuilder {

	public static Camera build(DynamoDBCamera dyCamera) {
		Camera camera = new Camera();
		camera.setUserId(dyCamera.getUserId());
		camera.setCameraId(dyCamera.getCameraId());
		camera.setCameraName(dyCamera.getCameraName());
		camera.setCameraLocation(dyCamera.getCameraLocation());
		camera.setCameraState(dyCamera.getCameraState());
		camera.setLastImageTimestamp(dyCamera.getLastImageTimestamp());
		return camera;
	}
	
	public static List<Camera> buildCameraList(List<DynamoDBCamera> dyCameraList) {
		List<Camera> cameraList = new ArrayList<Camera>();
		if(dyCameraList != null && dyCameraList.size() > 0) {
			for(DynamoDBCamera dyC : dyCameraList) {
				cameraList.add(DyCameraEntityBuilder.build(dyC));
			}
		}
		return cameraList;
	}
	
}
