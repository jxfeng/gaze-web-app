package com.netapp.atg.video.handler.dydb;

import com.netapp.atg.video.entity.User;
import com.netapp.atg.video.entity.dynamodb.DynamoDBUser;

public class DyUserEntityBuilder {

	public static User build(DynamoDBUser dyUser) {
		
		User user = new User(dyUser.getUserId());
		user.setPassword(dyUser.getPassword());
		user.setFirstName(dyUser.getFirstName());
		user.setLastName(dyUser.getLastName());
		user.setEmail(dyUser.getEmail());
		
		return user;
	}
	
}
