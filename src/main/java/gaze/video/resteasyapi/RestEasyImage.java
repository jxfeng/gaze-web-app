package gaze.video.resteasyapi;

import gaze.application.ApplicationSettings;
import gaze.video.entity.AppError;
import gaze.video.entity.Camera;
import gaze.video.entity.CameraShard;
import gaze.video.entity.Image;
import gaze.video.entity.ImageBlob;
import gaze.video.entity.ImageVariation;
import gaze.video.entity.Session;
import gaze.video.entity.ImageVariation.BlobVariation;
import gaze.video.exception.ApplicationException;
import gaze.video.handler.CameraHandler;
import gaze.video.handler.ImageBlobHandler;
import gaze.video.handler.ImageHandler;
import gaze.video.handler.SessionAuthenticator;
import gaze.video.handler.dydb.DyCameraHandler;
import gaze.video.handler.dydb.DyImageHandler;
import gaze.video.handler.dydb.DySessionAuthenticator;
import gaze.video.handler.s3.S3ImageBlobHandler;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

@Path("/api/image")
public class RestEasyImage {

	private static final Logger LOG = LoggerFactory.getLogger(RestEasyImage.class);
	
	@POST
	@Path("/{cameraId}/{imageTS}")
	@Produces("application/json")
	@Consumes("multipart/form-data")
	public Response createNewImage(@HeaderParam(ApplicationSettings.SESSION_HTTP_HEADER) String sessionId,
			@PathParam("cameraId") String cameraId,  @PathParam("imageTS") Long imageTS, @QueryParam("variation") String variation, MultipartFormDataInput input) {
		try {
			
			SessionAuthenticator authenticator = new DySessionAuthenticator();
			ImageHandler imageHandler = new DyImageHandler();
			ImageBlobHandler blobHandler = new S3ImageBlobHandler();
			CameraHandler cameraHandler = new DyCameraHandler();
			
			//Check if sessionId was passed in
			if(sessionId == null) {
				LOG.error("No session id provided in the request");
				throw ApplicationException.SESSION_INVALID_ID;
			}
			
			//Make sure session is valid
			Session session = authenticator.getSession(sessionId);
			if(!authenticator.isSessionValid(session)) {
				LOG.error("Session id:" + session.getSessionId() + " expired");
				throw ApplicationException.SESSION_EXPIRED;
			}
			
			//Extract user from session
			String userId = session.getUserId();
			
			//TODO: Make sure user is active
			//TODO: Throw out really big requests
			//TODO: If shard has been archived then new posts aren't allowed
			
			if(cameraId == null) {
				LOG.error("No camera id provided when uploading new image");
				throw ApplicationException.CAMERA_INVALID_CAMERA_ID;
			}
			
			if(imageTS == null) {
				LOG.error("No image id provided when uploading new image");
				throw ApplicationException.IMAGE_INVALID_IMAGE_ID;
			}
			
			String blobContentType = null;
			byte blobContents[] = null;
			
			//Process the input
			//TODO: Handle error cases, handles only 1 upload
			Map<String, List<InputPart>> form = input.getFormDataMap();
			List<InputPart> parts = form.get("uploaded-image");
			for(InputPart part : parts) {
				try {
					MultivaluedMap<String, String> header = part.getHeaders();
					for(String key : header.keySet()) {
						LOG.info("Header has key: " + key + " and values: " + header.get(key));
					}
					if(header.containsKey("Content-Type")) {
						blobContentType = header.get("Content-Type").get(0).toString();
						blobContentType = blobContentType.replace('[', ' ').replace(']', ' ').trim();
						LOG.info("Uploaded image has type " + blobContentType);
					}
					
					//TODO: Get filename for debugging
					//TODO: Should I extract the extension?
					
					//Get file contents
					InputStream stream = part.getBody(InputStream.class, null);
					blobContents = IOUtils.toByteArray(stream);
					break;
				} catch(Exception e) {
					LOG.error("Got an error while receiving the uploaded image");
					LOG.error(e.getLocalizedMessage());
					throw ApplicationException.IMAGE_INVALID_BLOB;
				}
			}
			
			assert(blobContentType != null && blobContentType.length() > 0);
			assert(blobContents != null && blobContents.length > 0);
			
			//Create/get a shard to put the image
			CameraShard shard = imageHandler.getShard(userId, cameraId, imageTS);
			if(shard == null) {
				LOG.error("Could not create/get shard for userId: " + userId + " cameraId: " + cameraId + " imagets:" + imageTS);
				throw ApplicationException.CAMERA_INVALID_INPUT;
			}
			assert(shard != null);
			
			//Process the variation
			BlobVariation blobVariation = BlobVariation.ORIGINAL;
			variation = (variation != null) ? variation : BlobVariation.ORIGINAL.toString();
			for(BlobVariation bv : BlobVariation.values()) {
				if(variation.equals(bv.toString())) {
					blobVariation = bv;
				}
			}
			LOG.info("Loading image variation userId: " + userId + " cameraId: " + cameraId + " imagets:" + imageTS + " variation: " + blobVariation);
			
			//Get image state
			//If ORIGINAL, then create image entry
			Image image;
			if(blobVariation == BlobVariation.ORIGINAL) {
				image = imageHandler.createImage(shard, imageTS);
				if(image.getImageState() == Image.ImageState.LOADED) {
					LOG.info("Image camera: " + cameraId + " image: " + imageTS + " is fully loaded");
					return Response.status(Status.OK).entity(new Gson().toJson(image)).build();
				}
			} 
			//Image must already exist and be LOADED
			else {
				image = imageHandler.getImage(shard, imageTS);
				if(image.getImageState() != Image.ImageState.LOADED) {
					LOG.info("Cannot load variation for userId: " + userId + " cameraId: " + cameraId + " imagets:" + imageTS + " until image is LOADED");
					throw ApplicationException.IMAGE_NOT_IN_VALID_STATE;
				}
			}

			//CREATED: create blobId, load into S3, update state
			if((blobVariation == BlobVariation.ORIGINAL && image.getImageState() == Image.ImageState.CREATED) || 
					(blobVariation != BlobVariation.ORIGINAL && image.getImageState() == Image.ImageState.LOADED)) {
				
				//See if metadata for blob already exists
				List<ImageVariation> blobs = imageHandler.listImageVariations(userId, cameraId, imageTS);
				ImageVariation originalBlob = null;
				if(blobs != null && blobs.size() > 0) {
					for(ImageVariation b : blobs) {
						if(b.getBlobVariation() == blobVariation) {
							LOG.info("Found existing variation userId: " + userId + " cameraId: " + cameraId + " imagets:" + imageTS + " variation: " + blobVariation);
							originalBlob = b;
							break;
						}
					}
				}
				
				//There is no blob yet, so create one
				if(originalBlob == null) {
					originalBlob = imageHandler.createImageBlob(
							userId, cameraId, imageTS, 
							blobContentType,
							blobContents.length,
							ImageVariation.BlobSource.AMAZON_S3, 
							blobVariation
							);
					LOG.info("Created variation userId: " + userId + " cameraId: " + cameraId + " imagets:" + imageTS + " variation: " + blobVariation);
				}
				
				//Load into S3 (no other choice for now)
				if(originalBlob.getBlobSource() == ImageVariation.BlobSource.AMAZON_S3) {
					blobHandler.createImageBlob(originalBlob.getBlobId(), blobContentType, blobContents);
					LOG.info("Created blob variation userId: " + userId + " cameraId: " + cameraId + " imagets:" + imageTS + " variation: " + blobVariation);
				} else {
					LOG.error("Invalid blob source given; should always be S3");
					throw ApplicationException.BLOB_INVALID_SOURCE;
				}
				
				//Record updated state
				ImageVariation updatedBlob = imageHandler.updateImageBlobState(userId, cameraId, imageTS, 
						blobVariation, ImageVariation.BlobState.LOADED);
				
				//If ORIGINAL, update image state to LOADED
				if(blobVariation == BlobVariation.ORIGINAL) {
					Image updatedImage = imageHandler.updateImageState(shard, imageTS, Image.ImageState.LOADED);
					LOG.info("Image userId: " + userId + " cameraId: " + cameraId + " imagets: " + imageTS + " is now fully loaded");
					return Response.status(Status.OK).entity(new Gson().toJson(updatedImage)).build();
				}
				
				return Response.status(Status.OK).entity(new Gson().toJson(image)).build();

			} 

			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			
		} catch(ApplicationException exception) {
			LOG.error(exception.getErrorCode().toString(), exception.getCause());
			AppError error = new AppError(exception.getErrorCode(), exception.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(error)).build();
		}
	}
	
	@GET
	@Path("/{cameraId}/list")
	@Produces("application/json")
	public Response listImages(@HeaderParam(ApplicationSettings.SESSION_HTTP_HEADER) String sessionId,
		@PathParam("cameraId") String cameraId, @QueryParam("since") Long since, @QueryParam("reverse") Boolean reverse, @QueryParam("limit") Integer limit) {
			
		try {
			SessionAuthenticator authenticator = new DySessionAuthenticator();
			ImageHandler imageHandler = new DyImageHandler();
			
			//Check if sessionId was passed in
			if(sessionId == null) {
				LOG.error("No session id provided in the request");
				throw ApplicationException.SESSION_INVALID_ID;
			}
			
			//Make sure session is valid
			Session session = authenticator.getSession(sessionId);
			if(!authenticator.isSessionValid(session)) {
				throw ApplicationException.SESSION_EXPIRED;
			}
			
			//Extract user from session
			String userId = session.getUserId();
			
			//Get optional parameters
			limit = (limit != null) ? limit : 10;
			since = (since !=null && since > 0) ? since : 0;
			reverse = (reverse != null) ? reverse : false;
			
			//Get list
			CameraShard shard = imageHandler.getShard(userId, cameraId, since);
			List<Image> images = imageHandler.listImages(shard, since, reverse, limit);
			
			//Too little images -- see if other shards exist
			if(images == null || images.size() < limit) {
				if(!reverse) {
					Long endTimestamp = shard.getShardEndTimestamp();
					while(images == null || images.size() < limit) {
						CameraShard nextShard = imageHandler.getNextShard(userId, cameraId, endTimestamp);
						if(nextShard == null) {
							LOG.info("No shard after userId: " + shard.getUserId() + " cameraId:" + shard.getCameraId() + " shardId: " + shard.getShardId());
							break;
						}
						List<Image> images2 = imageHandler.listImages(nextShard, since, reverse, limit);
						if(images2 != null && images2.size() > 0) {
							for(Image i : images2) {
								images.add(i);
								if(images.size() >= limit) {
									break;
								}
							}
						}
						endTimestamp = nextShard.getShardEndTimestamp();
					}
				}
				//Reverse iterator
				else {
					Long startTimestamp = shard.getShardBeginTimestamp();
					while(images == null || images.size() < limit) {
						CameraShard prevShard = imageHandler.getPreviousShard(userId, cameraId, startTimestamp);
						if(prevShard == null) {
							LOG.info("No shard before userId: " + shard.getUserId() + " cameraId:" + shard.getCameraId() + " shardId: " + shard.getShardId());
							break;
						}
						List<Image> images2 = imageHandler.listImages(prevShard, since, reverse, limit);
						if(images2 != null && images2.size() > 0) {
							for(Image i : images2) {
								images.add(i);
								if(images.size() >= limit) {
									break;
								}
							}
						}
						startTimestamp = prevShard.getShardBeginTimestamp();
					}
				}
			}
			
			//Sort the final list
			Collections.sort(images);
			
			//Cursor - opaque to me but will be interpreted by the underlying implementation
			return Response.status(Status.OK).entity(new Gson().toJson(images)).build();
			
		} catch(ApplicationException exception) {
			LOG.error(exception.getErrorCode().toString(), exception.getCause());
			AppError error = new AppError(exception.getErrorCode(), exception.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(error)).build();
		}
	}
	
	@GET
	@Path("/{cameraId}/{imageTS}")
	@Produces("application/json")
	public Response getImageDetails(@HeaderParam(ApplicationSettings.SESSION_HTTP_HEADER) String sessionId,
			@PathParam("cameraId") String cameraId, @PathParam("imageTS") Long imageTS) {
		
		try {
			SessionAuthenticator authenticator = new DySessionAuthenticator();
			ImageHandler imageHandler = new DyImageHandler();
			
			//Make sure session is valid
			if(sessionId == null) {
				LOG.error("No session id provided in the request");
				throw ApplicationException.SESSION_INVALID_ID;
			}
			Session session = authenticator.getSession(sessionId);
			String userId = session.getUserId();

			CameraShard shard = imageHandler.getShard(userId, cameraId, imageTS);
			if(shard != null && imageHandler.doesImageExist(shard, imageTS)) {
				Image img = imageHandler.getImage(shard, imageTS);
				if(img != null) {
					return Response.status(Status.OK).entity(new Gson().toJson(img)).build();
				} else {
					return Response.status(Status.NOT_FOUND).build();
				}
			} else {
				return Response.status(Status.NOT_FOUND).build();
			}
			
		} catch(ApplicationException exception) {
			LOG.error(exception.getErrorCode().toString(), exception.getCause());
			AppError error = new AppError(exception.getErrorCode(), exception.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(error)).build();
		}
	}
	
	@GET
	@Path("/{cameraId}/{imageTS}/variations")
	@Produces("application/json")
	public Response listVariations(@HeaderParam(ApplicationSettings.SESSION_HTTP_HEADER) String sessionId,
			@PathParam("cameraId") String cameraId, @PathParam("imageTS") Long imageTS) {
		try {
			SessionAuthenticator authenticator = new DySessionAuthenticator();
			ImageHandler imageHandler = new DyImageHandler();

			
			//Make sure session is valid
			if(sessionId == null) {
				LOG.error("No session id provided in the request");
				throw ApplicationException.SESSION_INVALID_ID;
			}
			Session session = authenticator.getSession(sessionId);
			String userId = session.getUserId();
			
			//List the variations I have
			List<ImageVariation> variations = imageHandler.listImageVariations(userId, cameraId, imageTS);
			if(variations == null || variations.size() == 0) {
				LOG.info("No variations found for image userId: " + userId + " cameraId: " + cameraId + " ts:" + imageTS);
				return Response.status(Status.NOT_FOUND).build();
			}

			//TODO: Pick out the valid variations (LOADED ones)
			
			return Response.status(Status.OK).entity(new Gson().toJson(variations)).build();
			
		} catch(ApplicationException exception) {
			LOG.error(exception.getErrorCode().toString(), exception.getCause());
			AppError error = new AppError(exception.getErrorCode(), exception.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(error)).build();
		}
			
	}
	
	@GET
	@Path("/{cameraId}/{imageTS}/blob.jpg")
	@Produces("image/jpeg")
	public Response getBlob(@HeaderParam(ApplicationSettings.SESSION_HTTP_HEADER) String sessionId,
			@PathParam("cameraId") String cameraId, @PathParam("imageTS") Long imageTS, @QueryParam("variation") String variation) {
		try {
			SessionAuthenticator authenticator = new DySessionAuthenticator();
			ImageHandler imageHandler = new DyImageHandler();
			ImageBlobHandler blobHandler = new S3ImageBlobHandler();
			
			//Make sure session is valid
			if(sessionId == null) {
				LOG.error("No session id provided in the request");
				throw ApplicationException.SESSION_INVALID_ID;
			}
			Session session = authenticator.getSession(sessionId);
			String userId = session.getUserId();

			//Validate the variation asked for, must be one of the predefined ones
			//If not, returns original
			ImageVariation.BlobVariation blobResolution = ImageVariation.BlobVariation.ORIGINAL;
			for(BlobVariation res : ImageVariation.BlobVariation.values()) {
				if(res.toString().equals(variation)) {
					blobResolution = res;
					break;
				}
			}

			CameraShard shard = imageHandler.getShard(userId, cameraId, imageTS);
			if(shard != null && imageHandler.doesImageExist(shard, imageTS)) {
				//Get the blob handle
				ImageVariation imgVariation = imageHandler.getImageVariation(userId, cameraId, imageTS, blobResolution);
				assert(imgVariation.getBlobSource() == ImageVariation.BlobSource.AMAZON_S3);
				
				//Fetch from S3
				ImageBlob blob = blobHandler.getImageBlob(imgVariation.getBlobId());
				return Response.status(Status.OK).entity(blob.getBlobContents()).build();
			} else {
				LOG.error("Did not find blob for userId: " + userId, " cameraId: " + cameraId + " ts:" + imageTS);
				return Response.status(Status.NOT_FOUND).build();
			}
			
		} catch(ApplicationException exception) {
			LOG.error(exception.getErrorCode().toString(), exception.getCause());
			AppError error = new AppError(exception.getErrorCode(), exception.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new Gson().toJson(error)).build();
		}
			
	}	
	
}
