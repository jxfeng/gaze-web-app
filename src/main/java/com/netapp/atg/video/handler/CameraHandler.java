package com.netapp.atg.video.handler;

import java.util.List;

import com.netapp.atg.video.entity.Camera;
import com.netapp.atg.video.exception.ApplicationException;

public interface CameraHandler {

	public Camera createNewCamera(String userId, String cameraId) throws ApplicationException;

	public Boolean doesExist(String userId, String cameraId) throws ApplicationException;
	
	public List<Camera> listCameras(String userId, String fromCameraId, Integer limit) throws ApplicationException;

	public Camera getCameraDetails(String userId, String cameraId) throws ApplicationException;
	
	public Camera updateLatestImageTimestamp(String userId, String cameraId, Long imageTimestamp) throws ApplicationException;

	public void deleteCamera(String userId, String cameraId) throws ApplicationException;
	
}
