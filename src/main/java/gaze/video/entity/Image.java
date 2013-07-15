package gaze.video.entity;

public class Image implements Comparable<Image> {

	private Long   imageTimestamp;
	private ImageState imageState;
	
	public enum ImageState { CREATED, LOADED, DELETED };
	
	public Long getImageTimestamp() {
		return imageTimestamp;
	}
	public void setImageTimestamp(Long imageTimestamp) {
		this.imageTimestamp = imageTimestamp;
	}
	
	public ImageState getImageState() {
		return imageState;
	}
	public void setImageState(ImageState imageState) {
		this.imageState = imageState;
	}
	
	@Override
	public int compareTo(Image other) {
		return (int) (this.imageTimestamp - other.imageTimestamp);
	}
	
}
