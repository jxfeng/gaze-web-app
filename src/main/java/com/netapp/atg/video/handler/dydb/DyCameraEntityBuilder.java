package com.netapp.atg.video.handler.dydb;

import java.util.ArrayList;
import java.util.List;

import com.netapp.atg.video.entity.Camera;
import com.netapp.atg.video.entity.dynamodb.DynamoDBCamera;

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
