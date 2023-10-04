/**
 * 
 */
package com.strandls.resource.services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.strandls.resource.pojo.License;
import com.strandls.resource.pojo.MediaGallery;
import com.strandls.resource.pojo.MediaGalleryCreate;
import com.strandls.resource.pojo.MediaGalleryListPageData;
import com.strandls.resource.pojo.MediaGalleryResourceData;
import com.strandls.resource.pojo.MediaGalleryResourceMapData;
import com.strandls.resource.pojo.MediaGalleryShow;
import com.strandls.resource.pojo.Resource;
import com.strandls.resource.pojo.ResourceCropInfo;
import com.strandls.resource.pojo.ResourceData;
import com.strandls.resource.pojo.ResourceListData;
import com.strandls.resource.pojo.ResourceRating;
import com.strandls.resource.pojo.ResourceWithTags;
import com.strandls.resource.pojo.SpeciesPull;
import com.strandls.resource.pojo.SpeciesResourcePulling;
import com.strandls.resource.pojo.UFile;
import com.strandls.resource.pojo.UFileCreateData;

/**
 * @author Abhishek Rudra
 *
 */
public interface ResourceServices {

	public List<ResourceData> getResouceURL(String objectType, Long objectId);

	public License getLicenseResouce(Long licenseId);

	public List<Resource> createResource(String objectType, Long objectId, List<Resource> resources);

	public List<Resource> updateResource(String objectType, Long objectId, List<Resource> newResources);

	public List<Resource> updateResourceRating(String objectType, Long objectId, ResourceRating resourceRating);

	public UFile uFileFindById(Long id);

	public UFile createUFile(UFileCreateData ufileCreateData);

	public Boolean removeUFile(Long uFileId);

	public List<SpeciesPull> getresourceMultipleObserId(String objectType, List<Long> objectIds, Long offset);

	public List<ResourceData> speciesResourcesPulling(SpeciesResourcePulling resourcePullingData);

	public Resource getResourceById(Long resourceId);

	public Boolean removeSpeciesFieldMapping(Long speciesFieldId);

	public List<ResourceCropInfo> fetchResourceCropInfo(String resourceIds);

	public ResourceCropInfo updateResourceCropInfo(ResourceCropInfo info);

	public MediaGalleryShow getMediaByID(Long objId);

	public MediaGalleryShow createMedia(HttpServletRequest request, MediaGalleryCreate mediaGalleryCreate);

	public ResourceListData getAllResources(Integer limit, Integer offset, String context, String mediaTypes,
			String tags, String users);

	public String deleteMediaByID(HttpServletRequest request, Long mId);

	public MediaGalleryShow getMediaByID(String mId, Integer max, Integer offSet, String mediaTypes, String tags,
			String users);

	public MediaGalleryShow updateMediaGalleryByID(HttpServletRequest request, Long mediaGalleryId,
			MediaGalleryCreate mediaGallery);

	public List<MediaGallery> createBulkResourceMapping(HttpServletRequest request,
			MediaGalleryResourceMapData mediaGalleryResourceMapData);

	public List<MediaGallery> getAllMediaGallery();

	public String uploadMedia(HttpServletRequest request, List<ResourceWithTags> resourceUpload);

	public MediaGalleryListPageData getMediaGalleryListPageData(Integer max, Integer offSet);

	public Resource updateResourceByID(HttpServletRequest request, ResourceWithTags resourceWithTags);

	public String deleteResourceByID(HttpServletRequest request, Long rId);

	public MediaGalleryResourceData getResourceDataByID(Long rID);

}
