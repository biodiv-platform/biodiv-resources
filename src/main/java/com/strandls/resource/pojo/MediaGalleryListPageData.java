package com.strandls.resource.pojo;

import java.util.List;

/**
 * @author Arun
 *
 * 
 */

public class MediaGalleryListPageData {

	private Long totalCount;
	private List<MediaGalleryListTitles> mediaListTitles;

	public MediaGalleryListPageData(Long totalCount, List<MediaGalleryListTitles> mediaListTitles) {
		super();
		this.totalCount = totalCount;
		this.mediaListTitles = mediaListTitles;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	public List<MediaGalleryListTitles> getMediaListTitles() {
		return mediaListTitles;
	}

	public void setMediaListTitles(List<MediaGalleryListTitles> mediaListTitles) {
		this.mediaListTitles = mediaListTitles;
	}

}
