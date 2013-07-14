package gaze.video.handler.dydb;


import gaze.video.entity.Session;
import gaze.video.entity.dynamodb.DynamoDBSession;
import gaze.video.entity.dynamodb.DynamoDBUser;
import gaze.video.exception.ApplicationException;
import gaze.video.handler.SessionAuthenticator;
import gaze.video.handler.SessionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DySessionAuthenticator implements SessionAuthenticator {

	final AmazonDynamoDBClient client;
	public final static Logger LOG = LoggerFactory.getLogger(DySessionAuthenticator.class);
	
	public DySessionAuthenticator() {
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
	public Session authenticate(String userId, String password) throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		
		//Check userId and password
		//HACK: Keeping password in plain-text for now
		DynamoDBUser dUser = mapper.load(DynamoDBUser.class, userId);
		if(dUser == null) {
			LOG.error("User " + userId + " was not found in the database");
			throw ApplicationException.NO_SUCH_USER;
		} else {
			if(!dUser.getPassword().equals(password)) {
				LOG.error("User " + userId + " password didn't match");
				throw ApplicationException.USER_INVALID_PASSWORD;
			}
		}
		
		//User is now authenticated, create a session for him
		SessionHandler sessionHandler = new DySessionHandler();
		Session session = sessionHandler.getNewSession(userId);
		return session;
	}

	@Override
	public Session getSession(String sessionId) throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		
		//Find the session in the database
		DynamoDBSession dSession = mapper.load(DynamoDBSession.class, sessionId);
		if(dSession == null) {
			LOG.error("Session " + sessionId + " was not found in the database");
			throw ApplicationException.NO_SUCH_SESSION;
		}
		Session session = DySessionEntityBuilder.build(dSession);
		
		//Update access time in the database
		dSession.setLastRequestTime(System.currentTimeMillis());
		mapper.save(dSession);
		
		return session;
	}

	@Override
	public Boolean isSessionValid(Session session) throws ApplicationException {
		if(session.getState() != Session.SessionState.ACTIVE) {
			LOG.info("Session id:" + session.getSessionId() + " timed/logged out");
			return false;
		}
		return true;
	}
	
	@Override
	public Boolean logoutSession(String sessionId) throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		
		//Find the session in the database
		DynamoDBSession dSession = mapper.load(DynamoDBSession.class, sessionId);
		if(dSession == null) {
			LOG.error("Session " + sessionId + " was not found in the database");
			return false;
		}
		
		//Mark the session as logged out
		dSession.setState(Session.SessionState.LOGGED_OUT.toString());
		mapper.save(dSession);
		
		return true;
	}



}
