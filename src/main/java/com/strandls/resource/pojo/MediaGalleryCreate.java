package com.strandls.resource.pojo;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.strandls.utility.pojo.Tags;

/**
 * @author Arun
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class MediaGalleryCreate {

	private Boolean isTruncated;
	private Date createdOn;
	private Date updatedOn;

//	-----Location Data--------
	private Double latitude;
	private Double longitude;

	private ResourceDataMediaGallery resources;

	private String title;
	private String description;

	private List<Tags> tags;

	public Boolean getIsTruncated() {
		return isTruncated;
	}

	public void setIsTruncated(Boolean isTruncated) {
		this.isTruncated = isTruncated;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public List<Tags> getTags() {
		return tags;
	}

	public void setTags(List<Tags> tags) {
		this.tags = tags;
	}

	public ResourceDataMediaGallery getResources() {
		return resources;
	}

	public void setResources(ResourceDataMediaGallery resources) {
		this.resources = resources;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
