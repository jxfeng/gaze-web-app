package com.netapp.atg.video.exception;

public class ApplicationException extends Exception {

	private static final long serialVersionUID = -8263706466262464545L;
	
	public static final ApplicationException NO_SUCH_SESSION;
	public static final ApplicationException SESSION_INVALID_ARGUMENTS;
	public static final ApplicationException SESSION_INVALID_ID;
	
	public static final ApplicationException NO_SUCH_USER;
	public static final ApplicationException USER_INVALID_PASSWORD;
	public static final ApplicationException USER_INVALID_LOGIN;
	public static final ApplicationException USER_INVALID_USER_ID;
	public static final ApplicationException USER_INVALID_CREATE;
	public static final ApplicationException USER_ALREADY_EXISTS;
	public static final ApplicationException USER_PERMISSION_MISMATCH;

	public static final ApplicationException NO_SUCH_CAMERA;
	public static final ApplicationException CAMERA_INVALID_CAMERA_ID;
	public static final ApplicationException CAMERA_ALREADY_EXISTS;
	public static final ApplicationException CAMERA_QUERY_FAILED;
	
	public static final ApplicationException NO_SUCH_IMAGE;
	public static final ApplicationException IMAGE_INVALID_CAMERA_ID;
	public static final ApplicationException IMAGE_ALREADY_EXISTS;
	public static final ApplicationException IMAGE_INVALID_IMAGE_ID;
	public static final ApplicationException IMAGE_QUERY_FAILED;
	
	public static final ApplicationException NO_SUCH_BLOB;
	public static final ApplicationException BLOB_INVALID_SOURCE;
	public static final ApplicationException BLOB_INVALID_BLOB_ID;
	public static final ApplicationException BLOB_COULD_NOT_CONVERT;
	public static final ApplicationException BLOB_FETCH_FAILED;
	
	static {
		//SESSION - 5000
		NO_SUCH_SESSION = new ApplicationException(5001, "No such session");
		SESSION_INVALID_ARGUMENTS = new ApplicationException(5002, "Invalid arguments to session start request");
		SESSION_INVALID_ID = new ApplicationException(5003, "Invalid or missing session id");
		//USER - 6000
		NO_SUCH_USER = new ApplicationException(6001, "No such user");
		USER_INVALID_PASSWORD = new ApplicationException(6002, "Invalid password");
		USER_INVALID_LOGIN = new ApplicationException(6003, "Invalid login");
		USER_INVALID_USER_ID = new ApplicationException(6004, "Invalid user id");
		USER_INVALID_CREATE = new ApplicationException(6005, "Invalid create parameters");
		USER_ALREADY_EXISTS = new ApplicationException(6006, "User id already exists");
		USER_PERMISSION_MISMATCH = new ApplicationException(6007, "Cannot get details of another user");
		//CAMERA - 7000
		NO_SUCH_CAMERA = new ApplicationException(7001, "No such camera");
		CAMERA_INVALID_CAMERA_ID = new ApplicationException(7002, "Invalid camera id");
		CAMERA_ALREADY_EXISTS = new ApplicationException(7003, "Camera id already exists");
		CAMERA_QUERY_FAILED = new ApplicationException(7004, "Camera query failed");
		//IMAGE - 8000
		NO_SUCH_IMAGE = new ApplicationException(8001, "No such image");
		IMAGE_INVALID_CAMERA_ID = new ApplicationException(8002, "Invalid camera id");
		IMAGE_ALREADY_EXISTS = new ApplicationException(8003, "Image id already exists");
		IMAGE_INVALID_IMAGE_ID = new ApplicationException(8004, "Invalid image id");
		IMAGE_QUERY_FAILED = new ApplicationException(8005, "Image query failed");
		//BLOB - 9000
		NO_SUCH_BLOB = new ApplicationException(9001, "No such blob");
		BLOB_INVALID_SOURCE = new ApplicationException(9002, "Invalid source of blob");
		BLOB_INVALID_BLOB_ID = new ApplicationException(9003, "Invalid blob id");
		BLOB_COULD_NOT_CONVERT = new ApplicationException(9004, "Could not convert blob contents from S3");
		BLOB_FETCH_FAILED = new ApplicationException(9005, "Could not fetch blob from S3");
	}
	
	private final Integer errorCode;
	
	public ApplicationException(Integer errorCode, String errorMessage) {
		super(errorMessage);
		
		//Record my error code
		this.errorCode = errorCode;
		
	}
	
	public Integer getErrorCode() {
		return errorCode;
	}

}
