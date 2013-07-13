package gaze.video.handler.dydb;

import gaze.video.entity.Session;
import gaze.video.entity.User;
import gaze.video.entity.dynamodb.DynamoDBSession;
import gaze.video.entity.dynamodb.DynamoDBUser;
import gaze.video.exception.ApplicationException;
import gaze.video.handler.UserHandler;

import java.util.UUID;

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
	public User createNewUser(String userId, String email, String password) throws ApplicationException {
		//TODO: Need to figure out duplicate emails, or reset by email address
		DynamoDBUser dUser = new DynamoDBUser();
		dUser.setUserId(userId);
		dUser.setEmail(email);
		dUser.setPassword(password);
		
		DynamoDBMapper mapper = new DynamoDBMapper(client);
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
		//TODO: Need to make sure the user checks his own account
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		DynamoDBUser dUser = mapper.load(DynamoDBUser.class, userId);
		if(dUser == null) {
			LOG.error("User " + userId + " was not found in the database");
			throw ApplicationException.NO_SUCH_SESSION;
		}
		
		User user = DyUserEntityBuilder.build(dUser);
		return user;
	}

	@Override
	public void deleteUser(String userId) throws ApplicationException {
		// TODO Auto-generated method stub
		
	}


}
