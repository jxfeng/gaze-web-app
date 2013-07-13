package gaze.video.entity;

public class Health {

	private Boolean applicationStatus;
	private Long    systemTimeMs;
	
	public Boolean getApplicationStatus() {
		return applicationStatus;
	}
	
	public void setApplicationStatus(Boolean status) {
		this.applicationStatus = status;
	}
	
	public Long getSystemTimeMs() {
		return systemTimeMs;
	}
	
	public void setSystemTimeMs(Long systemTimeMs) {
		this.systemTimeMs = systemTimeMs;
	}
	
}
