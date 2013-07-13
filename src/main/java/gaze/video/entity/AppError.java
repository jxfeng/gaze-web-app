package gaze.video.entity;

public class AppError {

	final Integer errorCode;
	final String  errorMessage;
	
	public static final AppError UNKNOWN_ERROR;
	
	static {
		UNKNOWN_ERROR = new AppError(100000, "Unknown error");
	}
	
	public AppError(Integer errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	public Integer getErrorCode() {
		return errorCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
}
