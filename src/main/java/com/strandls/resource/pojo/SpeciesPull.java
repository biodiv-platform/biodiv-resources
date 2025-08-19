/** */
package com.strandls.resource.pojo;

import java.util.List;

/**
 * @author Abhishek Rudra
 */
public class SpeciesPull {
	private Long observationId;
	private List<ResourceData> resourceData;

	/** */
	public SpeciesPull() {
		super();
	}

	/**
	 * @param observationId
	 * @param resourceData
	 */
	public SpeciesPull(Long observationId, List<ResourceData> resourceData) {
		super();
		this.observationId = observationId;
		this.resourceData = resourceData;
	}

	public Long getObservationId() {
		return observationId;
	}

	public void setObservationId(Long observationId) {
		this.observationId = observationId;
	}

	public List<ResourceData> getResourceData() {
		return resourceData;
	}

	public void setResourceData(List<ResourceData> resourceData) {
		this.resourceData = resourceData;
	}
}
