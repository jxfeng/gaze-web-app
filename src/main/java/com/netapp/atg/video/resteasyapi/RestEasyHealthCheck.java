package com.netapp.atg.video.resteasyapi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.netapp.atg.video.entity.Health;

@Path("/healthcheck")
public class RestEasyHealthCheck {

	private static final Logger LOG = LoggerFactory.getLogger(RestEasyHealthCheck.class);
	
	@GET
	@Produces("application/json")
	public Response healthCheck() {
		
		try {
			Health health = new Health();
			health.setSystemTimeMs(System.currentTimeMillis());
			health.setApplicationStatus(Boolean.TRUE);
			String data = new Gson().toJson(health);
			Response response = Response.status(Status.OK).entity(data).build();
			return response;
		} catch(Exception e) {
			LOG.error("Health check failed due to unknown error");
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
	}
	
}
