package com.strandls.resource.pojo;

import java.util.List;

/**
 * @author Arun
 *
 */
public class MediaGalleryResourceMapData {

	public List<Long> resourceIds;

	/**
	 * 
	 */
	public MediaGalleryResourceMapData() {
		super();
	}

	/**
	 * @param resourceIds
	 */

	public MediaGalleryResourceMapData(List<Long> resourceIds) {
		super();
		this.resourceIds = resourceIds;
	}

	public List<Long> getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(List<Long> resourceIds) {
		this.resourceIds = resourceIds;
	}

}
