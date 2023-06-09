package com.strandls.resource.pojo;

import java.util.List;

import com.strandls.user.pojo.UserIbp;
import com.strandls.utility.pojo.Tags;

/**
 * @author Arun
 *
 */

public class MediaGalleryShow {

	private MediaGallery mediaGallery;
	private List<Tags> tags;
	private UserIbp authorInfo;

	public MediaGallery getMediaGallery() {
		return mediaGallery;
	}

	public void setMediaGallery(MediaGallery mediaGallery) {
		this.mediaGallery = mediaGallery;
	}

	public List<Tags> getTags() {
		return tags;
	}

	public void setTags(List<Tags> tags) {
		this.tags = tags;
	}

	public UserIbp getAuthorInfo() {
		return authorInfo;
	}

	public void setAuthorInfo(UserIbp authorInfo) {
		this.authorInfo = authorInfo;
	}

}
