package gaze.video.handler.dydb;

import gaze.video.entity.User;
import gaze.video.entity.dynamodb.DynamoDBUser;
import gaze.video.exception.ApplicationException;
import gaze.video.handler.UserHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DyUserHandler implements UserHandler {

	final AmazonDynamoDBClient client;
	public final static Logger LOG = LoggerFactory.getLogger(DyUserHandler.class);
	
	public DyUserHandler() {
		AmazonDynamoDBClient thisClient = null;
		try {
			thisClient = new AmazonDynamoDBClient(
					new PropertiesCredentials(getClass().getClassLoader().getResourceAsStream("AwsCredentials.properties")));
		} catch(Exception exception) {
			LOG.error("Could not find AwsCredentials.properties file");
			thisClient = null;
		} finally {
			client = thisClient;
		}
	}

	@Override
	public User createNewUser(String userId, String email, String password, User.UserRole role) throws ApplicationException {
		//TODO: Think of what happens if two people create the same user at the same time
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		
		DynamoDBUser dUser = new DynamoDBUser();
		dUser.setUserId(userId);
		dUser.setEmail(email);
		dUser.setPassword(password);
		dUser.setUserRole(role.toString());
		mapper.save(dUser);
		
		User user = DyUserEntityBuilder.build(dUser);
		return user;
	}

	@Override
	public Boolean doesExist(String userId) throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		DynamoDBUser dUser = mapper.load(DynamoDBUser.class, userId);
		if(dUser == null) {
			LOG.info("User " + userId + " was not found in the database");
			return false;
		}
		
		return true;
	}
	
	@Override
	public User getUserDetails(String userId) throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		DynamoDBUser dUser = mapper.load(DynamoDBUser.class, userId);
		if(dUser == null) {
			LOG.error("User " + userId + " was not found in the database");
			throw ApplicationException.NO_SUCH_USER;
		}
		
		User user = DyUserEntityBuilder.build(dUser);
		return user;
	}


	@Override
	public User updateUserDetails(User user) throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		DynamoDBUser dUser = mapper.load(DynamoDBUser.class, user.getUserId());
		if(dUser == null) {
			LOG.error("User " + user.getUserId() + " was not found in the database");
			throw ApplicationException.NO_SUCH_SESSION;
		}
		
		if(user.getFirstName() != null) {
			dUser.setFirstName(user.getFirstName());
		}
		if(user.getLastName() != null) {
			dUser.setLastName(user.getLastName());
		}
		if(user.getPassword() != null) {
			dUser.setPassword(user.getPassword());
		}
		if(user.getEmail() != null) {
			dUser.setEmail(user.getEmail());
		}

		mapper.save(dUser);
		user = DyUserEntityBuilder.build(dUser);
		return user;
		
	}
	
	@Override
	public void deleteUser(String userId) throws ApplicationException {
		throw new RuntimeException("Not implemented yet");
	}



}
