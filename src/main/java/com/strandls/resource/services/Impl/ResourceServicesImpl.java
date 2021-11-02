/**
 * 
 */
package com.strandls.resource.services.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.resource.dao.LicenseDao;
import com.strandls.resource.dao.ObservationResourceDao;
import com.strandls.resource.dao.ResourceDao;
import com.strandls.resource.dao.SpeciesFieldResourcesDao;
import com.strandls.resource.dao.SpeciesResourceDao;
import com.strandls.resource.dao.UFileDao;
import com.strandls.resource.pojo.License;
import com.strandls.resource.pojo.ObservationResource;
import com.strandls.resource.pojo.Resource;
import com.strandls.resource.pojo.ResourceData;
import com.strandls.resource.pojo.ResourceRating;
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

/**
 * @author Abhishek Rudra
 *
 */
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
		if (resourceIds == null || resourceIds.isEmpty())
			return null;
		List<Resource> resourceList = resourceDao.findByObjectId(resourceIds);
		for (Resource resource : resourceList) {
			try {
				UserIbp userIbp = userService.getUserIbp(resource.getUploaderId().toString());
				observationResourceUsers.add(
						new ResourceData(resource, userIbp, licenseService.getLicenseById(resource.getLicenseId())));
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

					}
				}
			}

			if (objectType.equalsIgnoreCase(Constants.OBSERVATION))
				resourceIds = observationResourceDao.findByObservationId(objectId);
			else if (objectType.equalsIgnoreCase(Constants.SPECIES))
				resourceIds = speciesResourceDao.findBySpeciesId(objectId);
			else if (objectType.equalsIgnoreCase(Constants.SPECIESFIELD))
				resourceIds = speciesFieldResourceDao.findBySpeciesFieldId(objectId);

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
							licenseService.getLicenseById(resource.getLicenseId())));
					speciesPullMap.put(observationId, resourcesDataList);

				} else {
					List<ResourceData> resourcesDataList = new ArrayList<ResourceData>();
					resourcesDataList.add(new ResourceData(resource, userIbp,
							licenseService.getLicenseById(resource.getLicenseId())));
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
				System.out.println("resourceID : " + resourceId);
				System.out.println("speciesID :" + resourcePullingData.getSpeciesId());
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

}
