package gaze.video.handler.dydb;

import gaze.video.entity.Session;
import gaze.video.entity.User;
import gaze.video.entity.dynamodb.DynamoDBSession;
import gaze.video.exception.ApplicationException;
import gaze.video.handler.SessionHandler;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DySessionHandler implements SessionHandler {

	final AmazonDynamoDBClient client;
	public final static Logger LOG = LoggerFactory.getLogger(DySessionHandler.class);
	
	public DySessionHandler() {
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
	public Session getNewSession() throws ApplicationException {
		
		UUID id = UUID.randomUUID();
		DynamoDBSession dSession = new DynamoDBSession();
		dSession.setSessionId(id.toString());
		dSession.setUserId(User.INVALID_USER_ID);
		dSession.setStartTime(System.currentTimeMillis());
		dSession.setLastRequestTime(System.currentTimeMillis());
		dSession.setState(Session.SessionState.ACTIVE.toString());
		
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		mapper.save(dSession);
		
		Session session = DySessionEntityBuilder.build(dSession);
		return session;
	}
	
	@Override
	public Session getNewSession(String userId) throws ApplicationException {
		
		UUID id = UUID.randomUUID();
		DynamoDBSession dSession = new DynamoDBSession();
		dSession.setSessionId(id.toString());
		dSession.setUserId(userId);
		dSession.setStartTime(System.currentTimeMillis());
		dSession.setLastRequestTime(System.currentTimeMillis());
		dSession.setState(Session.SessionState.ACTIVE.toString());
		
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		mapper.save(dSession);
		
		Session session = DySessionEntityBuilder.build(dSession);
		return session;
	}

	@Override
	public Session getSessionDetails(String sessionId) throws ApplicationException {
		
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		DynamoDBSession dSession = mapper.load(DynamoDBSession.class, sessionId);
		if(dSession == null) {
			LOG.error("Session " + sessionId + " was not found in the database");
			throw ApplicationException.NO_SUCH_SESSION;
		}
		Session session = DySessionEntityBuilder.build(dSession);
		
		//Update access time
		dSession.setLastRequestTime(System.currentTimeMillis());
		mapper.save(dSession);
		
		return session;
	}

	@Override
	public void deleteSession(String sessionId) throws ApplicationException {
		
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		DynamoDBSession dSession = mapper.load(DynamoDBSession.class, sessionId);
		if(dSession == null) {
			LOG.error("Session " + sessionId + " was not found in the database");
			throw ApplicationException.NO_SUCH_SESSION;
		}
		mapper.delete(dSession);
	}

}
