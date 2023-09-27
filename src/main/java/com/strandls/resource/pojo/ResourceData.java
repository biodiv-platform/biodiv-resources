/**
 * 
 */
package com.strandls.resource.pojo;

import java.util.List;

import com.strandls.user.pojo.UserIbp;
import com.strandls.utility.pojo.Tags;

import io.swagger.annotations.ApiModel;

/**
 * @author Abhishek Rudra
 *
 */

@ApiModel
public class ResourceData {

	private Resource resource;
	private UserIbp userIbp;
	private License license;
	private List<Tags> tags;

	/**
	 * 
	 */
	public ResourceData() {
		super();
	}

	/**
	 * @param resource
	 * @param userIbp
	 * @param license
	 * @param tags
	 */
	public ResourceData(Resource resource, UserIbp userIbp, License license, List<Tags> tags) {
		super();
		this.resource = resource;
		this.userIbp = userIbp;
		this.license = license;
		this.tags = tags;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public UserIbp getUserIbp() {
		return userIbp;
	}

	public void setUserIbp(UserIbp userIbp) {
		this.userIbp = userIbp;
	}

	public License getLicense() {
		return license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	public List<Tags> getTags() {
		return tags;
	}

	public void setTags(List<Tags> tags) {
		this.tags = tags;
	}

}
