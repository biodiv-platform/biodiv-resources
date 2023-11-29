/**
 * 
 */
package com.strandls.resource;

/**
 * @author Abhishek Rudra
 *
 */
public class ApiConstants {

	/**
	 * since all class members are static , private constructor is required
	 */
	private ApiConstants() {
		super();
	}

	// versioning
	public static final String V1 = "/v1";

	// resource controller
	public static final String PING = "/ping";
	public static final String GETPATH = "/getpath";
	public static final String RESOURCE = "/resource";
	public static final String CREATE = "/create";
	public static final String UPDATE = "/update";
	public static final String LICENSE = "/license";
	public static final String RATING = "/rating";
	public static final String ALL = "/all";
	public static final String REMOVE = "/remove";
	public static final String UFILE = "/ufile";
	public static final String BULK = "/bulk";
	public static final String PULLRESOURCE = "/pullResource";
	public static final String SPECIESFIELD = "/specieField";
	public static final String MEDIAGALLERY = "/mediaGallery";
	public static final String UPLOAD = "/upload";
	public static final String SHOW = "/show";
	public static final String LIST = "/list";
	public static final String DELETE = "/delete";
	public static final String BULKRESOURCEMAPPING = "/bulkResourceMapping";
	public static final String EDITPAGE = "/editPage";
	public static final String IMAGE = "/image";

}
