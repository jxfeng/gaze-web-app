package gaze.application;

import gaze.video.resteasyapi.RestEasyAuthenticate;
import gaze.video.resteasyapi.RestEasyCamera;
import gaze.video.resteasyapi.RestEasyHealthCheck;
import gaze.video.resteasyapi.RestEasyImage;
import gaze.video.resteasyapi.RestEasyLogout;
import gaze.video.resteasyapi.RestEasyStats;
import gaze.video.resteasyapi.RestEasyUser;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;


public class RestEasyApplication extends Application {

	private Set<Object> singletons = new HashSet<Object>();
	
	public RestEasyApplication() {
		singletons.add(new RestEasyHealthCheck());
		singletons.add(new RestEasyAuthenticate());
		singletons.add(new RestEasyLogout());
		singletons.add(new RestEasyUser());
		singletons.add(new RestEasyCamera());
		singletons.add(new RestEasyImage());
		singletons.add(new RestEasyStats());
	}
	
	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
	
}
