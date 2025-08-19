/** */
package com.strandls.resource.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * @author Abhishek Rudra
 */
@Entity
@Table(name = "observation_resource")
@JsonIgnoreProperties(ignoreUnknown = true)
@IdClass(CompositeKeyObservationResource.class)
public class ObservationResource implements Serializable {

	/** */
	private static final long serialVersionUID = -7970642734279869311L;

	private Long observationId;
	private Long resourceId;

	/** */
	public ObservationResource() {
		super();
	}

	/**
	 * @param observationId
	 * @param resourceId
	 */
	public ObservationResource(Long observationId, Long resourceId) {
		super();
		this.observationId = observationId;
		this.resourceId = resourceId;
	}

	@Id
	@Column(name = "observation_id")
	public Long getObservationId() {
		return observationId;
	}

	public void setObservationId(Long observationId) {
		this.observationId = observationId;
	}

	@Column(name = "resource_id")
	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
}
