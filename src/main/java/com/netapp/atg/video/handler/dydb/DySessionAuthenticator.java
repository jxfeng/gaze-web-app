package com.netapp.atg.video.handler.dydb;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.netapp.atg.application.ApplicationSettings;
import com.netapp.atg.video.entity.Session;
import com.netapp.atg.video.entity.dynamodb.DynamoDBSession;
import com.netapp.atg.video.entity.dynamodb.DynamoDBUser;
import com.netapp.atg.video.exception.ApplicationException;
import com.netapp.atg.video.handler.SessionAuthenticator;
import com.netapp.atg.video.handler.SessionHandler;

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
		
		//Check password (Hack: keeping in plain text for now)
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
	public Boolean isSessionValid(Session session) throws ApplicationException {
		if((System.currentTimeMillis() - session.getLastRequestTime()) > ApplicationSettings.SESSION_TIMEOUT_MS) {
			LOG.info("Session id:" + session.getSessionId() + " timed out");
			return false;
		}
		return true;
	}
	
	@Override
	public Boolean logoutSession(String sessionId) throws ApplicationException {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		DynamoDBSession dSession = mapper.load(DynamoDBSession.class, sessionId);
		if(dSession == null) {
			LOG.error("Session " + sessionId + " was not found in the database");
			return false;
		}
		//Delete the session
		mapper.delete(dSession);
		return true;
	}



}
