package gaze.video.entity;

public class ImageVariation {

	private BlobVariation blobVariation;
	private BlobSource blobSource;
	private String blobId;
	private String blobContentType;
	private BlobState blobState;
	private Integer blobLengthBytes;
	
	public enum BlobVariation {ORIGINAL, RT_FEATURE_DETECTED};
	public enum BlobSource {AMAZON_S3, AMAZON_S3_RRS};
	public enum BlobState {CREATED, LOADED};
	
	public BlobVariation getBlobVariation() {
		return blobVariation;
	}
	public void setBlobVariation(BlobVariation blobVariation) {
		this.blobVariation = blobVariation;
	}
	
	public BlobSource getBlobSource() {
		return blobSource;
	}
	public void setBlobSource(BlobSource blobSource) {
		this.blobSource = blobSource;
	}
	
	public String getBlobId() {
		return blobId;
	}
	public void setBlobId(String blobId) {
		this.blobId = blobId;
	}
	
	public String getBlobContentType() {
		return blobContentType;
	}
	public void setBlobContentType(String blobContentType) {
		this.blobContentType = blobContentType;
	}
	public BlobState getBlobState() {
		return blobState;
	}
	public void setBlobState(BlobState blobState) {
		this.blobState = blobState;
	}
	
	public Integer getBlobLengthBytes() {
		return blobLengthBytes;
	}
	public void setBlobLengthBytes(Integer blobLengthBytes) {
		this.blobLengthBytes = blobLengthBytes;
	}
	
}
