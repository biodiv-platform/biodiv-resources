package com.strandls.resource.pojo;

import java.util.List;

/**
 * 
 * @author Arun
 *
 */
public class ResourceListData {

	private List<ResourceData> resourceDataList;
	private long totalCount;

	public ResourceListData(List<ResourceData> resourceDataList, long totalCount) {
		super();
		this.setResourceDataList(resourceDataList);
		this.setTotalCount(totalCount);

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
