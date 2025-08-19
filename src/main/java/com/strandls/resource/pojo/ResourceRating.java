package com.strandls.resource.pojo;

public class ResourceRating {

	private Long resourceId;
	private Integer rating;

	/** */
	public ResourceRating() {
		super();
	}

	/**
	 * @param resourceId
	 * @param rating
	 */
	public ResourceRating(Long resourceId, Integer rating) {
		super();
		this.resourceId = resourceId;
		this.rating = rating;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}
}
