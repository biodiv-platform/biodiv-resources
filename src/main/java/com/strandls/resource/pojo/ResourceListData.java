package com.strandls.resource.pojo;

import java.util.List;

/**
 * 
 * @author Arun
 *
 */
public class ResourceListData {

	private List<ResourceData> resourceDataList;
	private Long totalCount;

	public ResourceListData() {
		super();
	}

	public ResourceListData(List<ResourceData> resourceDataList, Long totalCount) {
		super();
		this.resourceDataList = resourceDataList;
		this.totalCount = totalCount;
	}

	public List<ResourceData> getResourceDataList() {
		return resourceDataList;
	}

	public void setResourceDataList(List<ResourceData> resourceDataList) {
		this.resourceDataList = resourceDataList;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

}
