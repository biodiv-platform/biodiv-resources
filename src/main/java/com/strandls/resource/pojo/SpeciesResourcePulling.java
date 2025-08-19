/** */
package com.strandls.resource.pojo;

import java.util.List;

/**
 * @author Abhishek Rudra
 */
public class SpeciesResourcePulling {

	private Long speciesId;
	private List<Long> resourcesIds;

	/** */
	public SpeciesResourcePulling() {
		super();
	}

	/**
	 * @param speciesId
	 * @param resourcesIds
	 */
	public SpeciesResourcePulling(Long speciesId, List<Long> resourcesIds) {
		super();
		this.speciesId = speciesId;
		this.resourcesIds = resourcesIds;
	}

	public Long getSpeciesId() {
		return speciesId;
	}

	public void setSpeciesId(Long speciesId) {
		this.speciesId = speciesId;
	}

	public List<Long> getResourcesIds() {
		return resourcesIds;
	}

	public void setResourcesIds(List<Long> resourcesIds) {
		this.resourcesIds = resourcesIds;
	}
}
