package com.strandls.resource.pojo;

import java.util.Date;

/**
 * @author Arun
 *
 * 
 */

public class MediaGalleryListTitles {

	private Long id;
	private String name;
	private String description;
	private String reprImage;
	private Date lastUpdated;
	private Date createdOn;
	private Long totalMedia;

	/**
	 * 
	 */
	public MediaGalleryListTitles() {
		super();
	}

	/**
	 * @param id
	 * @param name
	 * @param description
	 * @param reprImage
	 * @param lastUpdated
	 * @param createdOn
	 * @param totalMedia
	 */

	public MediaGalleryListTitles(Long id, String name, String description, String reprImage, Date lastUpdated,
			Date createdOn, Long totalMedia) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.reprImage = reprImage;
		this.lastUpdated = lastUpdated;
		this.createdOn = createdOn;
		this.totalMedia = totalMedia;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReprImage() {
		return reprImage;
	}

	public void setReprImage(String reprImage) {
		this.reprImage = reprImage;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Long getTotalMedia() {
		return totalMedia;
	}

	public void setTotalMedia(Long totalMedia) {
		this.totalMedia = totalMedia;
	}

}
