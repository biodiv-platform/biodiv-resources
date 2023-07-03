package com.strandls.resource.pojo;

import java.util.List;

import com.strandls.utility.pojo.Tags;

public class ResourceDataMediaGallery {

	private String path;
	private String url;
	private String type;
	private String caption;
	private Integer rating;
	private Long licenseId;
	private String context;
	private Long languageId;
	private String contributor;

	private List<Tags> tags;

	/**
	 * 
	 */
	public ResourceDataMediaGallery() {
		super();
	}

	/**
	 * @param path
	 * @param url
	 * @param type
	 * @param caption
	 * @param licenseId
	 * @param context
	 * @param languageId
	 * @param contributor
	 * @param tags
	 */
	public ResourceDataMediaGallery(String path, String url, String type, String caption, Integer rating,
			Long licenseId, String context, Long languageId, String contributor, List<Tags> tags) {
		super();
		this.path = path;
		this.url = url;
		this.type = type;
		this.caption = caption;
		this.rating = rating;
		this.licenseId = licenseId;
		this.context = context;
		this.languageId = languageId;
		this.contributor = contributor;
		this.tags = tags;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Long getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(Long licenseId) {
		this.licenseId = licenseId;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public Long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

	public String getContributor() {
		return contributor;
	}

	public void setContributor(String contributor) {
		this.contributor = contributor;
	}

	public List<Tags> getTags() {
		return tags;
	}

	public void setTags(List<Tags> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "ResourceDataMedia [path=" + path + ", url=" + url + ", type=" + type + ", caption=" + caption
				+ ", licenseId=" + licenseId + ", context=" + context + ", languageId=" + languageId + ", contributor="
				+ contributor + "]" + ", tags=" + tags;
	}

}
