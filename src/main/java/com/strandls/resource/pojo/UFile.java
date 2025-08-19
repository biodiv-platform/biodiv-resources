/** */
package com.strandls.resource.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author Abhishek Rudra
 */
@Entity
@Table(name = "ufile")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UFile {

	private Long id;
	private String mimeType;
	private String path;
	private String size;
	private Integer weight;

	/** */
	public UFile() {
		super();
	}

	/**
	 * @param id
	 * @param mimeType
	 * @param path
	 * @param size
	 * @param weight
	 */
	public UFile(Long id, String mimeType, String path, String size, Integer weight) {
		super();
		this.id = id;
		this.mimeType = mimeType;
		this.path = path;
		this.size = size;
		this.weight = weight;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "mimetype")
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Column(name = "path")
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Column(name = "size")
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	@Column(name = "weight")
	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}
}
