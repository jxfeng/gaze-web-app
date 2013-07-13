package gaze.video.entity;

public class AppResponse {

	final Integer opCode;
	final String  opMessage;
	
	public static final AppResponse DELETED_OK;
	
	static {
		DELETED_OK = new AppResponse(100000, "Entity deleted");
	}
	
	public AppResponse(Integer errorCode, String errorMessage) {
		this.opCode = errorCode;
		this.opMessage = errorMessage;
	}
	
	public Integer getErrorCode() {
		return opCode;
	}
	
	public String getErrorMessage() {
		return opMessage;
	}
	
}
