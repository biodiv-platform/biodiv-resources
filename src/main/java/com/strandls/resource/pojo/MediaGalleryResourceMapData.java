package com.strandls.resource.pojo;

import java.util.List;

/**
 * @author Arun
 *
 */
public class MediaGalleryResourceMapData {

	public List<Long> resourceIds;
	public List<Long> mediaGalleryIds;

	/**
	 * 
	 */
	public MediaGalleryResourceMapData() {
		super();
	}

	/**
	 * @param resourceIds
	 */

	public MediaGalleryResourceMapData(List<Long> resourceIds, List<Long> mediaGalleryIds) {
		super();
		this.resourceIds = resourceIds;
		this.mediaGalleryIds = mediaGalleryIds;

	}

	public List<Long> getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(List<Long> resourceIds) {
		this.resourceIds = resourceIds;
	}

	public List<Long> getMediaGalleryIds() {
		return mediaGalleryIds;
	}

	public void setMediaGalleryIds(List<Long> mediaGalleryIds) {
		this.mediaGalleryIds = mediaGalleryIds;
	}

}
