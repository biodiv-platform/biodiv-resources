package com.strandls.resource.pojo;

import java.util.List;

/**
 * @author Arun
 *
 */

public class MediaGalleryShow {

	private MediaGallery mediaGallery;
	private List<ResourceData> mediaGalleryResource;

	public MediaGallery getMediaGallery() {
		return mediaGallery;
	}

	public void setMediaGallery(MediaGallery mediaGallery) {
		this.mediaGallery = mediaGallery;
	}

	public List<ResourceData> getMediaGalleryResource() {
		return mediaGalleryResource;
	}

	public void setMediaGalleryResource(List<ResourceData> mediaGalleryResource) {
		this.mediaGalleryResource = mediaGalleryResource;
	}

}
