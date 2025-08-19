package com.strandls.resource.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * @author Arun
 */
@Entity
@Table(name = "media_gallery_resource")
@JsonIgnoreProperties(ignoreUnknown = true)
@IdClass(CompositeKeyMediaGalleryResource.class)
public class MediaGalleryResource implements Serializable {

	private static final long serialVersionUID = 695923796166182348L;
	private Long mediaGalleryId;
	private Long resourceId;

	/** */
	public MediaGalleryResource() {
		super();
	}

	/**
	 * @param mediaGalleryId
	 * @param resourceId
	 */
	public MediaGalleryResource(Long mediaGalleryId, Long resourceId) {
		super();
		this.mediaGalleryId = mediaGalleryId;
		this.resourceId = resourceId;
	}

	@Id
	@Column(name = "media_gallery_id")
	public Long getMediaGalleryId() {
		return mediaGalleryId;
	}

	public void setMediaGalleryId(Long mediaGalleryId) {
		this.mediaGalleryId = mediaGalleryId;
	}

	@Column(name = "resource_id")
	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
}
