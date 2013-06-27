package com.netapp.atg.application;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.netapp.atg.video.resteasyapi.RestEasyAuthenticate;
import com.netapp.atg.video.resteasyapi.RestEasyCamera;
import com.netapp.atg.video.resteasyapi.RestEasyHealthCheck;
import com.netapp.atg.video.resteasyapi.RestEasyImage;
import com.netapp.atg.video.resteasyapi.RestEasyLogout;
import com.netapp.atg.video.resteasyapi.RestEasyUser;

public class RestEasyApplication extends Application {

	private Set<Object> singletons = new HashSet<Object>();
	
	public RestEasyApplication() {
		singletons.add(new RestEasyHealthCheck());
		singletons.add(new RestEasyAuthenticate());
		singletons.add(new RestEasyLogout());
		singletons.add(new RestEasyUser());
		singletons.add(new RestEasyCamera());
		singletons.add(new RestEasyImage());
	}
	
	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
	
}
