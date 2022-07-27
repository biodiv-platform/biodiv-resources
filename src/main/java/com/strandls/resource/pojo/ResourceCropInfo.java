package com.strandls.resource.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;

@ApiModel
@Entity
@Table(name = "resource_crop_info")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceCropInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8928280656369809809L;

	private Long id;

	private String selectionStatus;

	private Long x;

	private Long y;

	private Long width;

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
