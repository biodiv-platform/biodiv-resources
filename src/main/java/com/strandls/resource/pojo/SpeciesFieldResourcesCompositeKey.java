/** */
package com.strandls.resource.pojo;

import java.io.Serializable;

/**
 * @author Abhishek Rudra
 */
public class SpeciesFieldResourcesCompositeKey implements Serializable {

	/** */
	private static final long serialVersionUID = -4656209478700743445L;

	private Long speciesFieldId;
	private Long resourceId;

	public Long getSpeciesFieldId() {
		return speciesFieldId;
	}

	public void setSpeciesFieldId(Long speciesFieldId) {
		this.speciesFieldId = speciesFieldId;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
}
