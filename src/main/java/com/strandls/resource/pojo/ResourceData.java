package com.strandls.resource.pojo;

import java.util.List;

import com.strandls.user.pojo.UserIbp;
import com.strandls.utility.pojo.Tags;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Contains detailed info about a resource, its owner, license and tags")
public class ResourceData {

	@Schema(description = "The resource details")
	private Resource resource;

	@Schema(description = "The user who owns or uploaded the resource")
	private UserIbp userIbp;

	@Schema(description = "The license associated with this resource")
	private License license;

	@ArraySchema(schema = @Schema(implementation = Tags.class))
	private List<Tags> tags;

	public ResourceData() {
		super();
	}

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
