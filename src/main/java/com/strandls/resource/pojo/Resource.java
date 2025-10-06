package com.strandls.resource.pojo;

import java.io.Serializable;
import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Schema(description = "Resource entity representing a file/image/video or other media asset")
@Entity
@Table(name = "resource")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resource implements Serializable {

	private static final long serialVersionUID = -4877757562860141754L;

	@Schema(description = "Unique identifier for the resource", example = "12345")
	private Long id;

	@Schema(description = "Description of the resource", example = "Close-up of a butterfly")
	private String description;

	@Schema(description = "Original file name", example = "butterfly.jpg")
	private String fileName;

	@Schema(description = "MIME type of the resource", example = "image/jpeg")
	private String mimeType;

	@Schema(description = "Resource type (e.g., 'image', 'audio', 'video', 'document')", example = "image")
	private String type;

	@Schema(description = "URL to access the resource", example = "https://domain.org/resources/12345.jpg")
	private String url;

	@Schema(description = "User-given (or calculated) rating for the resource", example = "4")
	private Integer rating;

	@Schema(description = "Date/time when the resource was uploaded", example = "2024-02-15")
	private Date uploadTime;

	@Schema(description = "ID of the user who uploaded", example = "42")
	private Long uploaderId;

	@Schema(description = "Resource context (e.g., module or feature)", example = "observation")
	private String context;

	@Schema(description = "Language id of this resource, if relevant", example = "1")
	private Long languageId;

	@Schema(description = "License id associated with this resource", example = "5")
	private Long licenseId;

	@Schema(description = "Contributor/collection/credit string", example = "John Doe")
	private String contributor;

	public Resource() {
	}

	public Resource(Long id, String description, String fileName, String mimeType, String type, String url,
			Integer rating, Date uploadTime, Long uploaderId, String context, Long languageId, Long licenseId,
			String contributor) {
		this.id = id;
		this.description = description;
		this.fileName = fileName;
		this.mimeType = mimeType;
		this.type = type;
		this.url = url;
		this.rating = rating;
		this.uploadTime = uploadTime;
		this.uploaderId = uploaderId;
		this.context = context;
		this.languageId = languageId;
		this.licenseId = licenseId;
		this.contributor = contributor;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resource_id_generator")
	@SequenceGenerator(name = "resource_id_generator", sequenceName = "resource_id_seq", allocationSize = 1)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "file_name")
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Column(name = "mime_type")
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Column(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "rating")
	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	@Column(name = "upload_time")
	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	@Column(name = "uploader_id")
	public Long getUploaderId() {
		return uploaderId;
	}

	public void setUploaderId(Long uploaderId) {
		this.uploaderId = uploaderId;
	}

	@Column(name = "context")
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	@Column(name = "language_id")
	public Long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

	@Column(name = "license_id")
	public Long getLicenseId() {
		return licenseId;
	}

	public void setLicenseId(Long licenseId) {
		this.licenseId = licenseId;
	}

	@Column(name = "contributor")
	public String getContributor() {
		return contributor;
	}

	public void setContributor(String contributor) {
		this.contributor = contributor;
	}
}
