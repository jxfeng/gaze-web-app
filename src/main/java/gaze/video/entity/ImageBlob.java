package gaze.video.entity;

public class ImageBlob {

	private String blobId;
	private String blobContentType;
	private byte[] blobContents;
	
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
	
	public Integer getBlobLengthBytes() {
		if(blobContents == null) {
			return 0;
		}
		return blobContents.length;
	}
	
	public byte[] getBlobContents() {
		return blobContents;
	}
	public void setBlobContents(byte[] blobContents) {
		this.blobContents = blobContents;
	}

	
}
