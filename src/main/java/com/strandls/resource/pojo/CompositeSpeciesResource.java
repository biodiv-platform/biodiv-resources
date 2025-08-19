/** */
package com.strandls.resource.pojo;

import java.io.Serializable;

/**
 * @author Abhishek Rudra
 */
public class CompositeSpeciesResource implements Serializable {

	/** */
	private static final long serialVersionUID = -354670334110821649L;

	private Long resourceId;
	private Long speciesId;

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public Long getSpeciesId() {
		return speciesId;
	}

	public void setSpeciesId(Long speciesId) {
		this.speciesId = speciesId;
	}
}
