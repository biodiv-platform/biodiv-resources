/** */
package com.strandls.resource.pojo;

/**
 * @author Abhishek Rudra
 */
public class UFileCreateData {

	private String mimeType;
	private String path;
	private String size;
	private Integer weight;

	/** */
	public UFileCreateData() {
		super();
	}

	/**
	 * @param mimeType
	 * @param path
	 * @param size
	 * @param weight
	 */
	public UFileCreateData(String mimeType, String path, String size, Integer weight) {
		super();
		this.mimeType = mimeType;
		this.path = path;
		this.size = size;
		this.weight = weight;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}
}
