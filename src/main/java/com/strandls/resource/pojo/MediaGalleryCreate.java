package com.strandls.resource.pojo;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Arun
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class MediaGalleryCreate {

	private String name;
	private String description;

	private Boolean isDeleted;
	private Date createdOn;
	private Date updatedOn;

	private List<ResourceWithTags> resourcesList;

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

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public List<ResourceWithTags> getResourcesList() {
		return resourcesList;
	}

	public void setResourcesList(List<ResourceWithTags> resourcesList) {
		this.resourcesList = resourcesList;
	}

}
