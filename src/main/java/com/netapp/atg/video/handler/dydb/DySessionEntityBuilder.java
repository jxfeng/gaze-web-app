package com.netapp.atg.video.handler.dydb;

import com.netapp.atg.video.entity.Session;
import com.netapp.atg.video.entity.dynamodb.DynamoDBSession;

public class DySessionEntityBuilder {

	public static Session build(DynamoDBSession dySession) {
		Session session = new Session(dySession.getSessionId(), 
										dySession.getUserId(), 
										dySession.getStartTime(),
										dySession.getLastRequestTime()
										);
		return session;
	}
	
}
