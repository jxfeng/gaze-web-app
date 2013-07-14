package gaze.video.handler.dydb;

import gaze.video.entity.Session;
import gaze.video.entity.dynamodb.DynamoDBSession;


public class DySessionEntityBuilder {

	public static Session build(DynamoDBSession dySession) {
		Session.SessionState state = Session.SessionState.valueOf(dySession.getState());
		Session session = new Session(dySession.getSessionId(), 
										dySession.getUserId(), 
										dySession.getStartTime(),
										dySession.getLastRequestTime(),
										state
										);
		return session;
	}
	
}
