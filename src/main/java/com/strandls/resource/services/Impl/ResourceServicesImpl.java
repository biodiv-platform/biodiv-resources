/**
 * 
 */
package com.strandls.resource.services.Impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.authentication_utility.util.AuthUtil;
import com.strandls.resource.dao.LicenseDao;
import com.strandls.resource.dao.MediaGalleryDao;
import com.strandls.resource.dao.MediaGalleryResourceDao;
import com.strandls.resource.dao.ObservationResourceDao;
import com.strandls.resource.dao.ResourceCropDao;
import com.strandls.resource.dao.ResourceDao;
import com.strandls.resource.dao.SpeciesFieldResourcesDao;
import com.strandls.resource.dao.SpeciesResourceDao;
import com.strandls.resource.dao.UFileDao;
import com.strandls.resource.pojo.License;
import com.strandls.resource.pojo.MediaGallery;
import com.strandls.resource.pojo.MediaGalleryCreate;
import com.strandls.resource.pojo.MediaGalleryListPageData;
import com.strandls.resource.pojo.MediaGalleryListTitles;
import com.strandls.resource.pojo.MediaGalleryResource;
import com.strandls.resource.pojo.MediaGalleryResourceData;
import com.strandls.resource.pojo.MediaGalleryResourceMapData;
import com.strandls.resource.pojo.MediaGalleryShow;
import com.strandls.resource.pojo.ObservationResource;
import com.strandls.resource.pojo.Resource;
import com.strandls.resource.pojo.ResourceCropInfo;
import com.strandls.resource.pojo.ResourceData;
import com.strandls.resource.pojo.ResourceListData;
import com.strandls.resource.pojo.ResourceRating;
import com.strandls.resource.pojo.ResourceWithTags;
import com.strandls.resource.pojo.SpeciesFieldResources;
import com.strandls.resource.pojo.SpeciesPull;
import com.strandls.resource.pojo.SpeciesResource;
import com.strandls.resource.pojo.SpeciesResourcePulling;
import com.strandls.resource.pojo.UFile;
import com.strandls.resource.pojo.UFileCreateData;
import com.strandls.resource.services.LicenseServices;
import com.strandls.resource.services.ResourceServices;
import com.strandls.resource.util.Constants;
import com.strandls.user.ApiException;
import com.strandls.user.controller.UserServiceApi;
import com.strandls.user.pojo.UserIbp;
import com.strandls.utility.controller.UtilityServiceApi;
import com.strandls.utility.pojo.Tags;
import com.strandls.utility.pojo.TagsMappingData;

import net.minidev.json.JSONArray;

/**
 * @author Abhishek Rudra
 *
 */

enum SelectionStatus {
	SELECTED, REJECTED, NOT_CURATED;
}

public class ResourceServicesImpl implements ResourceServices {

	private static final Logger logger = LoggerFactory.getLogger(ResourceServicesImpl.class);

	@Inject
	private ResourceDao resourceDao;

	@Inject
	private LicenseDao licenseDao;

	@Inject
	private UFileDao uFileDao;

	@Inject
	private UserServiceApi userService;

	@Inject
	private ObservationResourceDao observationResourceDao;

	@Inject
	private SpeciesResourceDao speciesResourceDao;

	@Inject
	private SpeciesFieldResourcesDao speciesFieldResourceDao;

	@Inject
	private LicenseServices licenseService;

	@Inject
	private ResourceCropDao resourceCropDao;

	@Inject
	private MediaGalleryHelper mediaGalleryHelper;

	@Inject
	private MediaGalleryDao mediaGalleryDao;

	@Inject
	private UtilityServiceApi utilityServiceApi;

	@Inject
	private MediaGalleryResourceDao mediaGalleryResourceDao;

	private static final String ROLES = "roles";

	private static final String ROLE_ADMIN = "ROLE_ADMIN";

	private static final String RESOURCE = "resource";

	@Override
	public List<ResourceData> getResouceURL(String objectType, Long objectId) {
		List<ResourceData> observationResourceUsers = new ArrayList<ResourceData>();
		List<Long> resourceIds = null;
		if (objectType.equalsIgnoreCase(Constants.OBSERVATION))
			resourceIds = observationResourceDao.findByObservationId(objectId);
		else if (objectType.equalsIgnoreCase(Constants.SPECIES))
			resourceIds = speciesResourceDao.findBySpeciesId(objectId);
		else if (objectType.equalsIgnoreCase(Constants.SPECIESFIELD))
			resourceIds = speciesFieldResourceDao.findBySpeciesFieldId(objectId);
		else if (objectType.equalsIgnoreCase(Constants.MEDIAGALLERY))
			resourceIds = mediaGalleryResourceDao.findByMediaId(objectId);

		if (resourceIds == null || resourceIds.isEmpty())
			return null;
		List<Resource> resourceList = resourceDao.findByObjectId(resourceIds);
		for (Resource resource : resourceList) {
			try {
				UserIbp userIbp = userService.getUserIbp(resource.getUploaderId().toString());

				List<Tags> tags = null;
				try {
					tags = utilityServiceApi.getTags(Constants.RESOURCE, resource.getId().toString());
				} catch (Exception e) {
					logger.error(e.getMessage());
				}

				observationResourceUsers.add(new ResourceData(resource, userIbp,
						licenseService.getLicenseById(resource.getLicenseId()), tags));
			} catch (ApiException e) {
				logger.error(e.getMessage());
			}

		}
		return observationResourceUsers;
	}

	@Override
	public License getLicenseResouce(Long licenseId) {
		return licenseDao.findById(licenseId);
	}

	@Override
	public List<Resource> createResource(String objectType, Long objectId, List<Resource> resources) {
		for (Resource resource : resources) {
			Resource result = resourceDao.save(resource);
			if (result != null) {
				logger.debug("Resource Created with ID :" + result.getId());

				if (objectType.equalsIgnoreCase(Constants.OBSERVATION)) {
					ObservationResource entity = new ObservationResource(objectId, result.getId());
					ObservationResource mappingResult = observationResourceDao.save(entity);
					logger.debug("Observation Resource Mapping Created: ", mappingResult.getObservationId(), " and ",
							mappingResult.getResourceId());
				} else if (objectType.equalsIgnoreCase(Constants.SPECIES)) {
					SpeciesResource entity = new SpeciesResource(result.getId(), objectId);
					SpeciesResource mappingResult = speciesResourceDao.save(entity);
					logger.debug("Species Resource Mapping Created: " + mappingResult.getSpeciesId() + " and "
							+ mappingResult.getResourceId());

				} else if (objectType.equalsIgnoreCase(Constants.SPECIESFIELD)) {
					SpeciesFieldResources entity = new SpeciesFieldResources(objectId, resource.getId());
					SpeciesFieldResources mappingResult = speciesFieldResourceDao.save(entity);
					logger.debug("Species Resource Mapping Created: " + mappingResult.getSpeciesFieldId() + " and "
							+ mappingResult.getResourceId());
				} else if (objectType.equalsIgnoreCase(Constants.MEDIAGALLERY)) {
					MediaGalleryResource entity = new MediaGalleryResource(objectId, resource.getId());
					MediaGalleryResource mappingResult = mediaGalleryResourceDao.save(entity);
					logger.debug("Media Gallery Resource Mapping Created: " + mappingResult.getMediaGalleryId()
							+ " and " + mappingResult.getResourceId());
				}

			}

		}
		List<Long> resourceIds = null;
		if (objectType.equalsIgnoreCase(Constants.OBSERVATION))
			resourceIds = observationResourceDao.findByObservationId(objectId);
		else if (objectType.equalsIgnoreCase(Constants.SPECIES))
			resourceIds = speciesResourceDao.findBySpeciesId(objectId);
		else if (objectType.equalsIgnoreCase(Constants.SPECIESFIELD))
			resourceIds = speciesFieldResourceDao.findBySpeciesFieldId(objectId);
		else if (objectType.equalsIgnoreCase(Constants.MEDIAGALLERY))
			resourceIds = mediaGalleryResourceDao.findByMediaId(objectId);
		resources = resourceDao.findByObjectId(resourceIds);
		return resources;

	}

	@Override
	public List<Resource> updateResource(String objectType, Long objectId, List<Resource> newResources) {

		try {
			List<Resource> resourceList = new ArrayList<Resource>();
			int flag = 0;
			List<Long> resourceIds = null;
			if (objectType.equalsIgnoreCase(Constants.OBSERVATION))
				resourceIds = observationResourceDao.findByObservationId(objectId);
			else if (objectType.equalsIgnoreCase(Constants.SPECIES))
				resourceIds = speciesResourceDao.findBySpeciesId(objectId);
			else if (objectType.equalsIgnoreCase(Constants.SPECIESFIELD))
				resourceIds = speciesFieldResourceDao.findBySpeciesFieldId(objectId);
			else if (objectType.equalsIgnoreCase(Constants.MEDIAGALLERY))
				resourceIds = mediaGalleryResourceDao.findByMediaId(objectId);

			if (resourceIds == null || resourceIds.isEmpty())
//				resources are getting created for the first time
				return createResource(objectType, objectId, newResources);

			List<Resource> oldResourcesList = resourceDao.findByObjectId(resourceIds);
			for (Resource resource : newResources) {
				flag = 0;
				for (Resource oldResource : oldResourcesList) {
					if (oldResource.getFileName().equals(resource.getFileName())) {
						flag = 1;
						resource.setId(oldResource.getId());
						resource.setContext(oldResource.getContext());
						resource.setUploaderId(oldResource.getUploaderId());
						resourceDao.update(resource);
						break;
					}
				}
				if (flag == 0) {
					resource = resourceDao.save(resource);

					if (objectType.equalsIgnoreCase(Constants.OBSERVATION)) {
						ObservationResource entity = new ObservationResource(objectId, resource.getId());
						ObservationResource mappingResult = observationResourceDao.save(entity);
						logger.debug("Observation Resource Mapping Created: " + mappingResult.getObservationId()
								+ " and " + mappingResult.getResourceId());
					} else if (objectType.equalsIgnoreCase(Constants.SPECIES)) {
						SpeciesResource entity = new SpeciesResource(resource.getId(), objectId);
						SpeciesResource mappingResult = speciesResourceDao.save(entity);
						logger.debug("Species Resource Mapping Created: " + mappingResult.getSpeciesId() + " and "
								+ mappingResult.getResourceId());

					} else if (objectType.equalsIgnoreCase(Constants.SPECIESFIELD)) {
						SpeciesFieldResources entity = new SpeciesFieldResources(objectId, resource.getId());
						SpeciesFieldResources mappingResult = speciesFieldResourceDao.save(entity);
						logger.debug("Species Resource Mapping Created: " + mappingResult.getSpeciesFieldId() + " and "
								+ mappingResult.getResourceId());
					} else if (objectType.equalsIgnoreCase(Constants.MEDIAGALLERY)) {
						MediaGalleryResource entity = new MediaGalleryResource(objectId, resource.getId());
						MediaGalleryResource mappingResult = mediaGalleryResourceDao.save(entity);
						logger.debug("Media Gallery Resource Mapping Created: " + mappingResult.getMediaGalleryId()
								+ " and " + mappingResult.getResourceId());
					}
				}
			}
			for (Resource oldResource : oldResourcesList) {
				flag = 0;
				for (Resource resource : newResources) {
					if (oldResource.getFileName().equals(resource.getFileName())) {
						flag = 1;
					}
				}
				if (flag == 0) {
					if (objectType.equalsIgnoreCase(Constants.OBSERVATION)) {
						ObservationResource observationResource = observationResourceDao.findByPair(objectId,
								oldResource.getId());
						observationResourceDao.delete(observationResource);
					} else if (objectType.equalsIgnoreCase(Constants.SPECIES)) {
						SpeciesResource speciesResource = speciesResourceDao.findByPair(objectId, oldResource.getId());
						speciesResourceDao.delete(speciesResource);
					} else if (objectType.equalsIgnoreCase(Constants.SPECIESFIELD)) {
						SpeciesFieldResources speciesFieldResource = speciesFieldResourceDao.findByPair(objectId,
								oldResource.getId());
						speciesFieldResourceDao.delete(speciesFieldResource);
					} else if (objectType.equalsIgnoreCase(Constants.MEDIAGALLERY)) {
						MediaGalleryResource mediaGalleryResource = mediaGalleryResourceDao.findByPair(objectId,
								oldResource.getId());
						mediaGalleryResourceDao.delete(mediaGalleryResource);
					}
				}
			}

			if (objectType.equalsIgnoreCase(Constants.OBSERVATION))
				resourceIds = observationResourceDao.findByObservationId(objectId);
			else if (objectType.equalsIgnoreCase(Constants.SPECIES))
				resourceIds = speciesResourceDao.findBySpeciesId(objectId);
			else if (objectType.equalsIgnoreCase(Constants.SPECIESFIELD))
				resourceIds = speciesFieldResourceDao.findBySpeciesFieldId(objectId);
			else if (objectType.equalsIgnoreCase(Constants.MEDIAGALLERY))
				resourceIds = mediaGalleryResourceDao.findByMediaId(objectId);

			resourceList = resourceDao.findByObjectId(resourceIds);
			return resourceList;

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	@Override
	public List<Resource> updateResourceRating(String objectType, Long objectId, ResourceRating resourceRating) {
		Resource resource = resourceDao.findById(resourceRating.getResourceId());
		resource.setRating(resourceRating.getRating());
		resourceDao.update(resource);
		List<Long> resourceIds = null;
		if (objectType.equalsIgnoreCase(Constants.OBSERVATION))
			resourceIds = observationResourceDao.findByObservationId(objectId);
		else if (objectType.equalsIgnoreCase(Constants.SPECIES))
			resourceIds = speciesResourceDao.findBySpeciesId(objectId);
		else if (objectType.equalsIgnoreCase(Constants.SPECIESFIELD))
			resourceIds = speciesFieldResourceDao.findBySpeciesFieldId(objectId);
		else if (objectType.equalsIgnoreCase(Constants.MEDIAGALLERY))
			resourceIds = mediaGalleryResourceDao.findByMediaId(objectId);

		return resourceDao.findByObjectId(resourceIds);
	}

	@Override
	public UFile uFileFindById(Long id) {
		return uFileDao.findById(id);
	}

	@Override
	public UFile createUFile(UFileCreateData ufileCreateData) {
		UFile ufile = new UFile(null, ufileCreateData.getMimeType(), ufileCreateData.getPath(),
				ufileCreateData.getSize(), (ufileCreateData.getWeight() > 0) ? ufileCreateData.getWeight() : 0);

		ufile = uFileDao.save(ufile);
		return ufile;
	}

	@Override
	public Boolean removeUFile(Long uFileId) {
		try {
			UFile uFile = uFileDao.findById(uFileId);
			if (uFile != null) {
				uFile = uFileDao.delete(uFile);

				if (uFile != null)
					return true;
				return false;

			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public List<SpeciesPull> getresourceMultipleObserId(String objectType, List<Long> objectIds, Long offset) {

		try {

			int lowerLimit = (int) (0 + offset);
			int upperLimit = (int) (10 + offset);
			List<SpeciesPull> result = new ArrayList<SpeciesPull>();
			List<Long> idList = new ArrayList<Long>();
			Map<Long, List<Long>> observationResMap = new HashMap<Long, List<Long>>();
			Map<Long, List<ResourceData>> speciesPullMap = new HashMap<Long, List<ResourceData>>();
			for (Long objectId : objectIds) {

				List<Long> resourceIds = observationResourceDao.findByObservationId(objectId);
				if (resourceIds != null && !resourceIds.isEmpty()) {
					idList.addAll(resourceIds);
					observationResMap.put(objectId, resourceIds);
				}

				if (idList.size() >= upperLimit)
					break;
			}
			if (idList.size() < lowerLimit)
				return null;

			upperLimit = (idList.size() < upperLimit) ? idList.size() : upperLimit;

			for (int i = lowerLimit; i < upperLimit; i++) {
				Resource resource = resourceDao.findById(idList.get(i));
				UserIbp userIbp = userService.getUserIbp(resource.getUploaderId().toString());

				Long observationId = null;
				for (Entry<Long, List<Long>> entry : observationResMap.entrySet()) {
					if (entry.getValue().contains(idList.get(i))) {
						observationId = entry.getKey();
						break;
					}
				}

				if (speciesPullMap.containsKey(observationId)) {
					List<ResourceData> resourcesDataList = speciesPullMap.get(observationId);
					resourcesDataList.add(new ResourceData(resource, userIbp,
							licenseService.getLicenseById(resource.getLicenseId()), null));
					speciesPullMap.put(observationId, resourcesDataList);

				} else {
					List<ResourceData> resourcesDataList = new ArrayList<ResourceData>();
					resourcesDataList.add(new ResourceData(resource, userIbp,
							licenseService.getLicenseById(resource.getLicenseId()), null));
					speciesPullMap.put(observationId, resourcesDataList);
				}

			}

			if (!speciesPullMap.isEmpty()) {
				for (Entry<Long, List<ResourceData>> entry : speciesPullMap.entrySet()) {
					result.add(new SpeciesPull(entry.getKey(), entry.getValue()));
				}
			}

			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	@Override
	public List<ResourceData> speciesResourcesPulling(SpeciesResourcePulling resourcePullingData) {

		try {
			for (Long resourceId : resourcePullingData.getResourcesIds()) {
				SpeciesResource entity = new SpeciesResource(resourceId, resourcePullingData.getSpeciesId());
				speciesResourceDao.save(entity);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return getResouceURL("species", resourcePullingData.getSpeciesId());
	}

	@Override
	public Resource getResourceById(Long resourceId) {
		Resource result = resourceDao.findById(resourceId);
		return result;
	}

	@Override
	public Boolean removeSpeciesFieldMapping(Long speciesFieldId) {
		try {
			List<Long> resourcesList = speciesFieldResourceDao.findBySpeciesFieldId(speciesFieldId);
			if (resourcesList != null && !resourcesList.isEmpty()) {
				for (Long resourceId : resourcesList) {
					SpeciesFieldResources speciesFieldResource = new SpeciesFieldResources(speciesFieldId, resourceId);
					speciesFieldResourceDao.delete(speciesFieldResource);
				}
			}

			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;

	}

	@Override
	public List<ResourceCropInfo> fetchResourceCropInfo(String resourceIds) {
		try {
			List<Long> listOfResourceIds = Stream.of(resourceIds.split(",")).map(Long::parseLong)
					.collect(Collectors.toList());
			List<ResourceCropInfo> resourcesCropInfo = resourceCropDao.findByResourceIds(listOfResourceIds);
			return resourcesCropInfo;

		} catch (Exception e) {
			logger.error(e.getMessage());

		}
		return null;

	}

	private Boolean validCropStatus(String status) {
		for (SelectionStatus selectionStatus : SelectionStatus.values()) {
			if (selectionStatus.name().equalsIgnoreCase(status.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	public ResourceCropInfo updateResourceCropInfo(ResourceCropInfo info) {
		ResourceCropInfo result;

		try {
			if (info.getSelectionStatus() == null || validCropStatus(info.getSelectionStatus())) {
				ResourceCropInfo resource = resourceCropDao.findById(info.getId());
				if (resource == null) {
					result = resourceCropDao.save(info);
				} else {
					result = resourceCropDao.update(info);
				}
				return result;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public MediaGalleryShow getMediaByID(Long objId) {
		MediaGalleryShow mediaGalleryShow = new MediaGalleryShow();
		MediaGallery mediaGallerry = mediaGalleryDao.findById(objId);

		try {
			List<ResourceData> mediaGalleryResource = getResouceURL(Constants.MEDIAGALLERY, objId);
			mediaGalleryShow.setMediaGallery(mediaGallerry);
			mediaGalleryShow.setMediaGalleryResource(mediaGalleryResource);
			mediaGalleryShow.setTotalCount(mediaGalleryResource.size());

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return mediaGalleryShow;
	}

	@Override
	public MediaGalleryShow createMedia(HttpServletRequest request, MediaGalleryCreate mediaGalleryCreate) {
		CommonProfile profile = AuthUtil.getProfileFromRequest(request);

		JSONArray roles = (JSONArray) profile.getAttribute(ROLES);

		if (!roles.contains(ROLE_ADMIN)) {
			return null;
		}

		Long userId = Long.parseLong(profile.getId());
		MediaGallery mediaGallery = new MediaGallery();

		try {
			mediaGallery = mediaGalleryHelper.createMediaGalleryMapping(userId, mediaGalleryCreate);
			mediaGalleryDao.save(mediaGallery);

			if (mediaGallery == null) {
				return null;
			}

			List<ResourceWithTags> resourceList = mediaGalleryCreate.getResourcesList();

			mediaGalleryHelper.createResourceMapping(request, userId, resourceList, mediaGallery.getId());

			return getMediaByID(mediaGallery.getId());

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	@Override
	public String uploadMedia(HttpServletRequest request, List<ResourceWithTags> resourceUpload) {
		CommonProfile profile = AuthUtil.getProfileFromRequest(request);
		Long userId = Long.parseLong(profile.getId());

		if (resourceUpload.isEmpty()) {
			return null;
		}

		mediaGalleryHelper.createResourceMapping(request, userId, resourceUpload, null);

		return "Resource Uploaded Successfully";
	}

	@Override
	public ResourceListData getAllResources(Integer limit, Integer offset, String contexts, String mediaTypes,
			String tags, String users) {

		List<String> mediaTypeList = Arrays.asList(mediaTypes.split(","));
		List<String> contextList = Arrays.asList(contexts.split(","));
		List<String> userList = Arrays.asList(users.split(","));
		ResourceListData resourceListData = new ResourceListData();

		List<Long> usersLong = new ArrayList<>();
		if (!userList.contains("all")) {
			for (String user : userList) {
				usersLong.add(Long.parseLong(user));
			}
		}

		List<Long> resourcesId = resourceDao.getResourceIds(contextList, mediaTypeList, usersLong, null);
		List<Long> commonResourcesId = resourcesId;

		if (tags != null && !tags.isEmpty() && !tags.equals("all")) {
			try {

				List<Long> tagResourcesId = utilityServiceApi.getResourceIds(tags, contexts.toLowerCase(), "all");

				commonResourcesId = resourcesId.stream().filter(tagResourcesId::contains).collect(Collectors.toList());

			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		Long totalCount = (long) commonResourcesId.size();

		List<ResourceData> resourceDataList = getResources(limit, offset, commonResourcesId);
		resourceListData.setResourceDataList(resourceDataList);
		resourceListData.setTotalCount(totalCount);

		return (resourceListData);
	}

	public List<ResourceData> getResources(Integer limit, Integer offset, List<Long> resourcesIds) {
		List<Resource> resources = resourceDao.findByIds(resourcesIds, limit, offset);
		List<ResourceData> resourceDataList = new ArrayList<>();

		for (Resource item : resources) {
			ResourceData resourceData = new ResourceData();
			try {
				resourceData.setUserIbp(userService.getUserIbp(item.getUploaderId().toString()));
				resourceData.setTags(utilityServiceApi.getTags(RESOURCE, item.getId().toString()));
				resourceData.setLicense(licenseService.getLicenseById(item.getLicenseId()));

			} catch (Exception e) {
				logger.error(e.getMessage());
			}

			resourceData.setResource(item);
			resourceDataList.add(resourceData);
		}
		return (resourceDataList);

	}

	@Override
	public MediaGalleryShow getMediaByID(String mIds, Integer limit, Integer offSet, String mediaTypes, String tags,
			String users) {
		MediaGalleryShow mediaGalleryShow = new MediaGalleryShow();
		MediaGallery mediaGallery = new MediaGallery();

		List<String> mediaTypeList = Arrays.asList(mediaTypes.split(","));
		List<String> userList = Arrays.asList(users.split(","));
		List<String> mIdList = Arrays.asList(mIds.split(","));

		List<Long> usersLong = new ArrayList<>();
		if (!userList.contains("all")) {
			for (String user : userList) {
				usersLong.add(Long.parseLong(user));
			}
		}

		List<Long> mIdsLong = new ArrayList<>();

		if (!mIdList.contains("all") && !mIdList.isEmpty()) {
			for (String mId : mIdList) {
				mIdsLong.add(Long.parseLong(mId));
			}
			if ((mIdsLong.size() == 1)) {
				mediaGallery = mediaGalleryDao.findById(mIdsLong.get(0));
			}
		} else {
			mediaGallery.setName("All media gallery");
			mediaGallery.setDescription("This is all media Gallery");

		}

		List<Long> resourceIds = mediaGalleryResourceDao.findByMediaIds(mIdsLong);

		List<Long> filteredIds = resourceDao.getResourceIds(null, mediaTypeList, usersLong, resourceIds);

		List<Long> commonResourcesId = filteredIds;

		List<Long> tagResourcesId;

		if (tags != null && !tags.isEmpty() && !tags.equals("all")) {
			try {

				tagResourcesId = utilityServiceApi.getResourceIds(tags, RESOURCE, "all");

				commonResourcesId = filteredIds.stream().filter(tagResourcesId::contains).collect(Collectors.toList());

			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		Long totalCount = (long) commonResourcesId.size();

		List<ResourceData> resourceDataList = getResources(limit, offSet, commonResourcesId);

		mediaGalleryShow.setMediaGallery(mediaGallery);
		mediaGalleryShow.setMediaGalleryResource(resourceDataList);
		mediaGalleryShow.setTotalCount(totalCount);

		return mediaGalleryShow;
	}

	@Override
	public String deleteMediaByID(HttpServletRequest request, Long mId) {
		try {

			CommonProfile profile = AuthUtil.getProfileFromRequest(request);

			JSONArray roles = (JSONArray) profile.getAttribute(ROLES);

			if (!roles.contains(ROLE_ADMIN)) {
				return null;
			}
			MediaGallery mediaGallery = mediaGalleryDao.findById(mId);

			List<Resource> resources = new ArrayList<>();
			updateResource(Constants.MEDIAGALLERY, mId, resources);

			mediaGalleryDao.delete(mediaGallery);

			return "Media Gallery Deleted Sucessfully";

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public MediaGalleryShow updateMediaGalleryByID(HttpServletRequest request, Long mId,
			MediaGalleryCreate mediaGalleryData) {

		CommonProfile profile = AuthUtil.getProfileFromRequest(request);

		JSONArray roles = (JSONArray) profile.getAttribute(ROLES);

		Long userId = Long.parseLong(profile.getId());

		if (!roles.contains(ROLE_ADMIN)) {
			return null;
		}

		MediaGallery mediaGallery = mediaGalleryDao.findById(mId);

		mediaGallery.setName(mediaGalleryData.getName());
		mediaGallery.setDescription(mediaGalleryData.getDescription());
		mediaGallery.setUpdatedOn(new Date());
		mediaGalleryDao.update(mediaGallery);

		List<ResourceWithTags> resourceWithTags = mediaGalleryData.getResourcesList();

		// existing resource
		List<ResourceWithTags> resourcesWithId = resourceWithTags.stream().filter(rwt -> rwt.getId() != null)
				.collect(Collectors.toList());

		List<Resource> oldResource = mediaGalleryHelper.convertResourceWithTagsList(resourcesWithId);
		updateResource(Constants.MEDIAGALLERY, mId, oldResource);

		for (ResourceWithTags resource : resourcesWithId) {
			TagsMappingData tagsMapping = mediaGalleryHelper.createTagsMappingData(resource.getId(),
					resource.getTags());
			mediaGalleryHelper.updateTagsMapping(request, tagsMapping);

		}

		// new resource
		List<ResourceWithTags> newResource = resourceWithTags.stream().filter(rwt -> rwt.getId() == null)
				.collect(Collectors.toList());

		mediaGalleryHelper.createResourceMapping(request, userId, newResource, mediaGallery.getId());

		return getMediaByID(mId);
	}

	@Override
	public List<MediaGallery> createBulkResourceMapping(HttpServletRequest request,
			MediaGalleryResourceMapData mediaGalleryResourceMapData, Boolean selectAll, String unSelectedIds) {

		List<Long> mIdList = mediaGalleryResourceMapData.getMediaGalleryIds();

		if (mIdList.isEmpty()) {
			return Collections.emptyList();
		}

		List<Long> unSelectedResourceIds = Arrays.stream(unSelectedIds.split(",")).map(Long::parseLong)
				.collect(Collectors.toList());

		List<Resource> resources;
		// Check the conditions to determine how to fetch resources
		if (Boolean.TRUE.equals(selectAll) && unSelectedResourceIds.isEmpty()) {
			resources = resourceDao.findAll();
		} else {
			resources = resourceDao.findAll().stream()
					.filter(resource -> !unSelectedResourceIds.contains(resource.getId())).collect(Collectors.toList());
		}

		if (!Boolean.TRUE.equals(selectAll)) {
			resources = resourceDao.findByIds(mediaGalleryResourceMapData.getResourceIds(), -1, -1);
		}

		List<MediaGallery> mediaGalleryList = new ArrayList<>();

		for (Long mId : mIdList) {
			MediaGallery mediaGallery = mediaGalleryDao.findById(mId);

			if (mediaGallery != null) {

				List<Long> existingResourceIds = getResourceIdsForMediaGallery(mId);

				for (Resource resource : resources) {
					if (!existingResourceIds.contains(resource.getId())) {
						MediaGalleryResource entity = new MediaGalleryResource(mId, resource.getId());
						mediaGalleryResourceDao.save(entity);
					}
				}
				mediaGalleryList.add(mediaGallery);
			}
		}

		return mediaGalleryList;
	}

	private List<Long> getResourceIdsForMediaGallery(Long mId) {
		return getResouceURL(Constants.MEDIAGALLERY, mId).stream()
				.filter(resourceData -> resourceData != null && resourceData.getResource() != null)
				.map(resourceData -> resourceData.getResource().getId()).collect(Collectors.toList());
	}

	@Override
	public List<MediaGallery> getAllMediaGallery() {
		return mediaGalleryDao.findAll();
	}

	@Override
	public MediaGalleryListPageData getMediaGalleryListPageData(Integer max, Integer offSet) {
		List<MediaGallery> mediaGalleryList = mediaGalleryDao.findAll(max, offSet);
		List<MediaGalleryListTitles> mediaGalleryListTitles = new ArrayList<>();

		for (MediaGallery mediaGallery : mediaGalleryList) {

			MediaGalleryListTitles mediaGalleryListItem = new MediaGalleryListTitles();

			List<Long> resourcesIds = mediaGalleryResourceDao.findByMediaId(mediaGallery.getId());

			mediaGalleryListItem.setId(mediaGallery.getId());
			mediaGalleryListItem.setName(mediaGallery.getName());
			mediaGalleryListItem.setDescription(mediaGallery.getDescription());
			mediaGalleryListItem.setLastUpdated(mediaGallery.getUpdatedOn());
			mediaGalleryListItem.setCreatedOn(mediaGallery.getCreatedOn());
			mediaGalleryListItem.setReprImage(getReprImage(resourcesIds));
			mediaGalleryListItem.setTotalMedia((long) resourcesIds.size());

			mediaGalleryListTitles.add(mediaGalleryListItem);

		}

		return new MediaGalleryListPageData(mediaGalleryDao.getTotalMediaGalleryCount(), mediaGalleryListTitles);
	}

	public String getReprImage(List<Long> resourcesIds) {

		List<Resource> resources = resourceDao.findByIds(resourcesIds, -1, -1);

		for (Resource resource : resources) {
			if (resource.getType() != null && resource.getType().equals("IMAGE")) {
				return resource.getFileName();
			}
		}

		return null;

	}

	@Override
	public Resource updateResourceByID(HttpServletRequest request, ResourceWithTags resourceWithTags) {
		CommonProfile profile = AuthUtil.getProfileFromRequest(request);
		JSONArray roles = (JSONArray) profile.getAttribute(ROLES);
		Long userId = Long.parseLong(profile.getId());

		Resource resource = resourceDao.findById(resourceWithTags.getId());

		if (!roles.contains(ROLE_ADMIN) && !resource.getUploaderId().equals(userId)) {
			return resource;
		}

		updateResourceDetails(resource, resourceWithTags);

		updateMediaGallery(resourceWithTags, resource);

		TagsMappingData tagsMapping = mediaGalleryHelper.createTagsMappingData(resourceWithTags.getId(),
				resourceWithTags.getTags());
		mediaGalleryHelper.updateTagsMapping(request, tagsMapping);

		return resource;
	}

	private void updateResourceDetails(Resource resource, ResourceWithTags resourceWithTags) {
		resource.setDescription(resourceWithTags.getCaption());
		resource.setContributor(resourceWithTags.getContributor());
		resource.setRating(resourceWithTags.getRating());
		resource.setLicenseId(resourceWithTags.getLicenseId());
		resourceDao.update(resource);
	}

	private void updateMediaGallery(ResourceWithTags resourceWithTags, Resource resource) {
		List<Long> newMediaGalleryIds = resourceWithTags.getmId();

		List<Long> existingMediaGalleryIds = getResourceDataByID(resourceWithTags.getId()).getMediaGallery().stream()
				.map(MediaGallery::getId).collect(Collectors.toList());

		List<Long> removedIds = existingMediaGalleryIds.stream().filter(id -> !newMediaGalleryIds.contains(id))
				.collect(Collectors.toList());

		List<Long> addedIds = newMediaGalleryIds.stream().filter(id -> !existingMediaGalleryIds.contains(id))
				.collect(Collectors.toList());

		for (Long mId : addedIds) {
			MediaGalleryResource entity = new MediaGalleryResource(mId, resource.getId());
			mediaGalleryResourceDao.save(entity);
		}

		for (Long mId : removedIds) {
			MediaGalleryResource entity = new MediaGalleryResource(mId, resource.getId());
			mediaGalleryResourceDao.delete(entity);
		}
	}

	@Override
	public String deleteResourceByID(HttpServletRequest request, Long rId) {
		try {

			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			JSONArray roles = (JSONArray) profile.getAttribute(ROLES);
			Long userId = Long.parseLong(profile.getId());
			Resource resource = resourceDao.findById(rId);

			if (roles.contains(ROLE_ADMIN) || resource.getUploaderId().equals(userId)) {
				List<MediaGalleryResource> mediaGalleryResource = mediaGalleryResourceDao.findByResourceId(rId);
				for (MediaGalleryResource item : mediaGalleryResource) {
					mediaGalleryResourceDao.delete(item);
				}
				resourceDao.delete(resource);

				return "Resource Deleted Sucessfully";

			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	@Override
	public MediaGalleryResourceData getResourceDataByID(Long rID) {
		Resource resource = resourceDao.findById(rID);
		if (resource == null) {
			return null;
		}
		try {
			ResourceData resourceData = new ResourceData();
			MediaGalleryResourceData mediaGalleryResourceData = new MediaGalleryResourceData();

			resourceData.setResource(resource);
			resourceData.setUserIbp(userService.getUserIbp(resource.getUploaderId().toString()));
			resourceData.setTags(utilityServiceApi.getTags(RESOURCE, resource.getId().toString()));
			resourceData.setLicense(licenseService.getLicenseById(resource.getLicenseId()));
			mediaGalleryResourceData.setResourceData(resourceData);

			List<MediaGalleryResource> mediaGalleryResources = mediaGalleryResourceDao.findByResourceId(rID);

			List<Long> mId = mediaGalleryResources.stream().map(MediaGalleryResource::getMediaGalleryId)
					.collect(Collectors.toList());

			List<MediaGallery> mediaGallery = mediaGalleryDao.findByIds(mId);

			mediaGalleryResourceData.setMediaGallery(mediaGallery);

			return mediaGalleryResourceData;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

}
