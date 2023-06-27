package com.strandls.resource.pojo;

import java.io.Serializable;

/**
 * @author Arun
 *
 */

public class CompositeKeyMediaGalleryResource implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2021737483395261002L;

	private Long mediaGalleryId;
	private Long resourceId;

	public Long getMediaGalleryId() {
		return mediaGalleryId;
	}

	public void setMediaGalleryId(Long mediaGalleryId) {
		this.mediaGalleryId = mediaGalleryId;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

}
