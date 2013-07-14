package gaze.video.entity;

public class User {

	public static final String INVALID_USER_ID = "0000";
	
	final String userId;
	String password;
	String firstName;
	String lastName;
	String email;
	UserState state;
	UserRole role;

	public enum UserRole { NORMAL, ADMIN };
	
	public User(String userId) {
		this.userId = userId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String emailAddress) {
		this.email = emailAddress;
	}
	
	public UserState getState() {
		return state;
	}
	
	void setState(UserState userState) {
		this.state = userState;
	}
	
	
	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}
	
}
