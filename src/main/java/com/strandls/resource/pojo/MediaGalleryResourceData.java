package com.strandls.resource.pojo;

import java.util.List;

public class MediaGalleryResourceData {

	private ResourceData resourceData;
	private List<MediaGallery> mediaGallery;

	public MediaGalleryResourceData() {
		super();
	}

	public MediaGalleryResourceData(ResourceData resourceData, List<MediaGallery> mediaGallery) {
		super();
		this.resourceData = resourceData;
		this.mediaGallery = mediaGallery;
	}

	public ResourceData getResourceData() {
		return resourceData;
	}

	public void setResourceData(ResourceData resourceData) {
		this.resourceData = resourceData;
	}

	public List<MediaGallery> getMediaGallery() {
		return mediaGallery;
	}

	public void setMediaGallery(List<MediaGallery> mediaGallery) {
		this.mediaGallery = mediaGallery;
	}

}
