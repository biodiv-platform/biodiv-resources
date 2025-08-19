package com.strandls.resource.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Schema(description = "Crop information for a resource image. Contains selection state and crop rectangle.")
@Entity
@Table(name = "resource_crop_info")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceCropInfo implements Serializable {
	private static final long serialVersionUID = -8928280656369809809L;

	@Schema(description = "Unique identifier for the resource crop info (resource id)", example = "123")
	private Long id;

	@Schema(description = "Crop selection status (e.g. 'CROPPED', 'ORIGINAL')", example = "CROPPED")
	private String selectionStatus;

	@Schema(description = "X coordinate of the crop rectangle (top-left)", example = "15")
	private Long x;

	@Schema(description = "Y coordinate of the crop rectangle (top-left)", example = "25")
	private Long y;

	@Schema(description = "Width of the crop rectangle in pixels", example = "512")
	private Long width;

	@Schema(description = "Height of the crop rectangle in pixels", example = "256")
	private Long height;

	public ResourceCropInfo() {
		super();
	}

	public ResourceCropInfo(Long id, String selectionStatus, Long x, Long y, Long width, Long height) {
		super();
		this.id = id;
		this.selectionStatus = selectionStatus;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Id
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "selection_status")
	public String getSelectionStatus() {
		return selectionStatus;
	}

	public void setSelectionStatus(String selectionStatus) {
		this.selectionStatus = selectionStatus;
	}

	@Column(name = "x")
	public Long getX() {
		return x;
	}

	public void setX(Long x) {
		this.x = x;
	}

	@Column(name = "y")
	public Long getY() {
		return y;
	}

	public void setY(Long y) {
		this.y = y;
	}

	@Column(name = "width")
	public Long getWidth() {
		return width;
	}

	public void setWidth(Long width) {
		this.width = width;
	}

	@Column(name = "height")
	public Long getHeight() {
		return height;
	}

	public void setHeight(Long height) {
		this.height = height;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
