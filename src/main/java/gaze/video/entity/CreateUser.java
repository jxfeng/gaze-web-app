package gaze.video.entity;

public class CreateUser {

	final String userHandle;
	final String emailAddress;
	final String password;
	
	public CreateUser(String userHandle, String emailAddress, String password) {
		this.userHandle = userHandle;
		this.emailAddress = emailAddress;
		this.password = password;
	}
	
	public String getUserHandle() {
		return userHandle;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}
	
	public String getPassword() {
		return password;
	}
	
}
