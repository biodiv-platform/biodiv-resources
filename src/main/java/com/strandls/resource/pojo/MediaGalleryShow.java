package com.strandls.resource.pojo;

import java.util.List;

/**
 * @author Arun
 *
 */

public class MediaGalleryShow {

	private MediaGallery mediaGallery;
	private List<ResourceData> mediaGalleryResource;
	private long totalCount;

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

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

}
