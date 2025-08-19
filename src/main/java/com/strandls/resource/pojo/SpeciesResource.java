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
@Table(name = "species_resource")
@JsonIgnoreProperties(ignoreUnknown = true)
@IdClass(CompositeSpeciesResource.class)
public class SpeciesResource implements Serializable {

	/** */
	private static final long serialVersionUID = -8043072677107314279L;

	private Long resourceId;
	private Long speciesId;

	public SpeciesResource() {
		super();
	}

	public SpeciesResource(Long resourceId, Long speciesId) {
		super();
		this.resourceId = resourceId;
		this.speciesId = speciesId;
	}

	@Id
	@Column(name = "resource_id")
	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	@Id
	@Column(name = "species_resources_id")
	public Long getSpeciesId() {
		return speciesId;
	}

	public void setSpeciesId(Long speciesId) {
		this.speciesId = speciesId;
	}
}
