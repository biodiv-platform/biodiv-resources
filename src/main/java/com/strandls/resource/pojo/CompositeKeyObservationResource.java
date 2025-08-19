/** */
package com.strandls.resource.pojo;

import java.io.Serializable;

/**
 * @author Abhishek Rudra
 */
public class CompositeKeyObservationResource implements Serializable {

	/** */
	private static final long serialVersionUID = -7433935834067243630L;

	private Long observationId;
	private Long resourceId;

	public Long getObservationId() {
		return observationId;
	}

	public void setObservationId(Long observationId) {
		this.observationId = observationId;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
}
