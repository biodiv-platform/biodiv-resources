/**
 * 
 */
package com.strandls.resource.services.Impl;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.strandls.resource.pojo.MediaGalleryResource;
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
import com.strandls.utility.pojo.TagsMapping;
import com.strandls.utility.pojo.TagsMappingData;

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

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return mediaGalleryShow;
	}

	@Override
	public MediaGalleryShow createMedia(HttpServletRequest request, MediaGalleryCreate mediaGalleryCreate) {
		CommonProfile profile = AuthUtil.getProfileFromRequest(request);
		Long userId = Long.parseLong(profile.getId());

		MediaGallery mediaGallery = mediaGalleryHelper.createMediaGalleryMapping(userId, mediaGalleryCreate);
		mediaGalleryDao.save(mediaGallery);

		if (!(mediaGalleryCreate.getResourcesList().isEmpty())) {
			for (ResourceWithTags resourceDataMediaGallery : mediaGalleryCreate.getResourcesList()) {

				List<Resource> resources = mediaGalleryHelper.createResourceMapping(request, userId,
						resourceDataMediaGallery);

				if (resources == null || resources.isEmpty()) {
					mediaGalleryDao.delete(mediaGallery);
					return null;

				}

				List<Resource> resourceList = createResource(Constants.MEDIAGALLERY, mediaGallery.getId(), resources);

				if (!(resourceDataMediaGallery.getTags().isEmpty())) {
					for (Resource resourceData : resourceList) {

						TagsMapping tagsMapping = new TagsMapping();
						tagsMapping.setObjectId(resourceData.getId());
						tagsMapping.setTags(resourceDataMediaGallery.getTags());

						TagsMappingData tagMappingData = new TagsMappingData();
						tagMappingData.setTagsMapping(tagsMapping);
						tagMappingData.setMailData(null);

						mediaGalleryHelper.createTagsMapping(request, tagMappingData);
					}
				}
			}
		}

		return getMediaByID(mediaGallery.getId());
	}

	@Override
	public ResourceListData getAllResources(Integer limit, Integer offset, String contexts, String mediaTypes,
			String tags, String users) {
		List<Long> tagResourcesId = new ArrayList<>();

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
				for (String context : contextList) {
					tagResourcesId = utilityServiceApi.getResourceIds(tags, context.toLowerCase(), "all");
				}

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

			} catch (Exception e) {
				logger.error(e.getMessage());
			}

			resourceData.setResource(item);
			resourceDataList.add(resourceData);
		}
		return (resourceDataList);

	}

	@Override
	public MediaGalleryShow getMediaByID(Long mId, Integer limit, Integer offSet, String mediaTypes, String tags,
			String users) {
		MediaGalleryShow mediaGalleryShow = new MediaGalleryShow();
		MediaGallery mediaGallery = mediaGalleryDao.findById(mId);

		List<String> mediaTypeList = Arrays.asList(mediaTypes.split(","));
		List<String> userList = Arrays.asList(users.split(","));

		List<Long> usersLong = new ArrayList<>();
		if (!userList.contains("all")) {
			for (String user : userList) {
				usersLong.add(Long.parseLong(user));
			}
		}

		if (mediaGallery != null) {
			List<Long> resourceIds = mediaGalleryResourceDao.findByMediaId(mId);

			List<Long> filteredIds = resourceDao.getResourceIds(null, mediaTypeList, usersLong, resourceIds);

			List<Long> commonResourcesId = filteredIds;

			List<Long> tagResourcesId = new ArrayList<>();

			if (tags != null && !tags.isEmpty() && !tags.equals("all")) {
				try {

					tagResourcesId = utilityServiceApi.getResourceIds(tags, "resource", mId.toString());

					commonResourcesId = resourceIds.stream().filter(tagResourcesId::contains)
							.collect(Collectors.toList());

				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}

			Long totalCount = (long) commonResourcesId.size();

			List<ResourceData> resourceDataList = getResources(limit, offSet, commonResourcesId);

			mediaGalleryShow.setMediaGallery(mediaGallery);
			mediaGalleryShow.setMediaGalleryResource(resourceDataList);
			mediaGalleryShow.setTotalCount(totalCount);
		}

		return mediaGalleryShow;
	}

	@Override
	public String deleteMediaByID(HttpServletRequest request, Long mId) {

		MediaGallery mediaGallerry = mediaGalleryDao.findById(mId);
		mediaGalleryDao.delete(mediaGallerry);

		return null;
	}

	@Override
	public MediaGallery updateMediaGalleryByID(HttpServletRequest request, MediaGallery mediaGalleryData) {
		MediaGallery mediaGallery = mediaGalleryDao.findById(mediaGalleryData.getId());

		mediaGallery.setName(mediaGalleryData.getName());
		mediaGallery.setDescripition(mediaGalleryData.getDescripition());
		mediaGallery.setUpdatedOn(new Date());

		mediaGalleryDao.update(mediaGallery);

		return mediaGallery;
	}

	@Override
	public MediaGallery createBulkResourceMapping(HttpServletRequest request, Long mId,
			MediaGalleryResourceMapData mediaGalleryResourceMapData) {

		MediaGallery mediaGallery = mediaGalleryDao.findById(mId);

		if (mediaGallery != null) {
			List<Resource> resources = resourceDao.findByIds(mediaGalleryResourceMapData.getResourceIds(), -1, -1);
			createResource(Constants.MEDIAGALLERY, mId, resources);
		}

		return mediaGallery;
	}

	@Override
	public List<MediaGallery> getAllMediaGallery() {
		return mediaGalleryDao.findAll();
	}

}
