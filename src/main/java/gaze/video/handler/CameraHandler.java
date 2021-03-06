package gaze.video.handler;

import gaze.video.entity.Camera;
import gaze.video.exception.ApplicationException;

import java.util.List;


public interface CameraHandler {

	public Camera createNewCamera(Camera camera) throws ApplicationException;
	
	public Camera updateCamera(Camera camera) throws ApplicationException;

	public Boolean doesExist(String userId, String cameraId) throws ApplicationException;
	
	public List<Camera> listCameras(String userId, String fromCameraId, Boolean reverse, Integer limit) throws ApplicationException;

	public Camera getCameraDetails(String userId, String cameraId) throws ApplicationException;
	
	public Camera updateLatestImageTimestamp(String userId, String cameraId, Long imageTimestamp) throws ApplicationException;

	public void deleteCamera(String userId, String cameraId) throws ApplicationException;
	
}
