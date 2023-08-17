package com.strandls.resource.services.Impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.authentication_utility.util.PropertyFileUtil;
import com.strandls.file.api.UploadApi;
import com.strandls.file.model.FilesDTO;
import com.strandls.resource.Headers;
import com.strandls.resource.dao.MediaGalleryResourceDao;
import com.strandls.resource.dao.ResourceDao;
import com.strandls.resource.pojo.MediaGallery;
import com.strandls.resource.pojo.MediaGalleryCreate;
import com.strandls.resource.pojo.MediaGalleryResource;
import com.strandls.resource.pojo.Resource;
import com.strandls.resource.pojo.ResourceData;
import com.strandls.resource.pojo.ResourceWithTags;
import com.strandls.utility.controller.UtilityServiceApi;
import com.strandls.utility.pojo.Tags;
import com.strandls.utility.pojo.TagsMapping;
import com.strandls.utility.pojo.TagsMappingData;

public class MediaGalleryHelper {
	private final Logger logger = LoggerFactory.getLogger(MediaGalleryHelper.class);

	@Inject
	private UploadApi fileUploadService;

	@Inject
	private Headers headers;

	@Inject
	private UtilityServiceApi utilityServices;

	@Inject
	private ResourceDao resourceDao;

	@Inject
	private MediaGalleryResourceDao mediaGalleryResourceDao;

	private Long defaultLanguageId = Long
			.parseLong(PropertyFileUtil.fetchProperty("config.properties", "defaultLanguageId"));

	private Long defaultLicenseId = Long
			.parseLong(PropertyFileUtil.fetchProperty("config.properties", "defaultLicenseId"));

	public MediaGallery createMediaGalleryMapping(Long userId, MediaGalleryCreate mediaGalleryCreate) {
		try {
			MediaGallery mediaGallery = new MediaGallery();
			mediaGallery.setAuthorId(userId);
			mediaGallery.setCreatedOn(new Date());
			mediaGallery.setUpdatedOn(new Date());
			mediaGallery.setName(mediaGalleryCreate.getName());
			mediaGallery.setDescription(mediaGalleryCreate.getDescription());

			return mediaGallery;

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	public List<Resource> createResourceMapping(HttpServletRequest request, Long userId,
			List<ResourceWithTags> resourceDataList, Long objectId) {

		List<Resource> resources = new ArrayList<>();

		try {

			List<String> fileList = new ArrayList<>();
			for (ResourceWithTags resourceData : resourceDataList) {

				if (resourceData.getPath() != null && resourceData.getPath().trim().length() > 0) {
					fileList.add(resourceData.getPath());
				}
			}

			Map<String, Object> fileMap = new HashMap<>();
			if (!fileList.isEmpty()) {
				fileUploadService = headers.addFileUploadHeader(fileUploadService,
						request.getHeader(HttpHeaders.AUTHORIZATION));

				FilesDTO filesDTO = new FilesDTO();
				filesDTO.setFiles(fileList);
				filesDTO.setFolder("resources");
				filesDTO.setModule("resource");
				fileMap = fileUploadService.moveFiles(filesDTO);
			}

			for (ResourceWithTags resourceData : resourceDataList) {
				Resource resource = new Resource();

				if (resourceData.getCaption() != null) {
					resource.setDescription(
							(resourceData.getCaption().trim().length() != 0) ? resourceData.getCaption().trim() : null);
				}

				if (resourceData.getPath() != null) {
					if (fileMap != null && !fileMap.isEmpty() && fileMap.containsKey(resourceData.getPath())) {
						System.out.println(fileMap);
						Map<String, String> files = (Map<String, String>) fileMap.get(resourceData.getPath());
						System.out.println(files);
						String relativePath = files.get("name").toString();
						resource.setFileName(relativePath);
					} else if (resourceData.getPath().startsWith("/ibpmu")) {
						continue;
					} else {
						resource.setFileName(resourceData.getPath());
					}
				}

				resource.setMimeType(null);

				if (resourceData.getType().startsWith("image") || resourceData.getType().equalsIgnoreCase("image")) {
					resource.setType("IMAGE");
				} else if (resourceData.getType().startsWith("audio")
						|| resourceData.getType().equalsIgnoreCase("audio")) {
					resource.setType("AUDIO");
				} else if (resourceData.getType().startsWith("video")
						|| resourceData.getType().equalsIgnoreCase("video")) {
					resource.setType("VIDEO");
				}

				if (resourceData.getPath() == null) {
					resource.setFileName(resource.getType().substring(0, 1).toLowerCase());
				}

				resource.setUrl(resourceData.getUrl());
				resource.setRating(resourceData.getRating());
//				resource.setUploadTime(new Date());
				resource.setUploaderId(userId);
				resource.setContext("RESOURCE");

				if (resourceData.getLanguageId() != null) {
					resource.setLanguageId(resourceData.getLanguageId());
				} else {
					resource.setLanguageId(defaultLanguageId);
				}

				if (resourceData.getLicenseId() != null) {
					resource.setLicenseId(resourceData.getLicenseId());
				} else {
					resource.setLicenseId(defaultLicenseId);
				}

				resource.setContributor(resourceData.getContributor());

				resources.add(resource);

				resource = resourceDao.save(resource);

				if (resource != null) {
					// To create a new Media Gallery
					if (objectId != null) {
						MediaGalleryResource entity = new MediaGalleryResource(objectId, resource.getId());
						mediaGalleryResourceDao.save(entity);

					}

					// To upload images in a media Gallery
					List<Long> mIds = resourceData.getmId();
					if (mIds != null && !mIds.isEmpty()) {
						for (Long mId : resourceData.getmId()) {
							MediaGalleryResource entity = new MediaGalleryResource(mId, resource.getId());
							mediaGalleryResourceDao.save(entity);
						}
					}

					// To update tags
					if (!resourceData.getTags().isEmpty()) {
						TagsMappingData tagMappingData = createTagsMappingData(resource.getId(),
								resourceData.getTags());
						createTagsMapping(request, tagMappingData);
					}
				}

			}

			return resources;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return Collections.emptyList();
	}

	public List<ResourceWithTags> getResourcesWithTags(List<ResourceData> resourceDataList) {
		return resourceDataList.stream().filter(resourceData -> resourceData.getResource().getId() == null)
				.map(resourceData -> {
					Resource resource = resourceData.getResource();
					return new ResourceWithTags(resource.getFileName(), resource.getUrl(), resource.getType(),
							resource.getDescription(), resource.getRating(), resource.getLicenseId(),
							resource.getContext(), resource.getLanguageId(), resource.getContributor(),
							resourceData.getTags(), null);
				}).collect(Collectors.toList());
	}

	public TagsMappingData createTagsMappingData(Long objectId, List<Tags> tags) {
		TagsMapping tagsMapping = new TagsMapping();
		tagsMapping.setObjectId(objectId);
		tagsMapping.setTags(tags);

		TagsMappingData tagMappingData = new TagsMappingData();
		tagMappingData.setTagsMapping(tagsMapping);
		tagMappingData.setMailData(null);

		return tagMappingData;
	}

	public void createTagsMapping(HttpServletRequest request, TagsMappingData tagsMappingData) {

		utilityServices = headers.addUtilityHeaders(utilityServices, request.getHeader(HttpHeaders.AUTHORIZATION));
		try {
			utilityServices.createTags("resource", tagsMappingData);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	public void updateTagsMapping(HttpServletRequest request, TagsMappingData tagsMappingData) {

		utilityServices = headers.addUtilityHeaders(utilityServices, request.getHeader(HttpHeaders.AUTHORIZATION));
		try {
			utilityServices.updateTags("resource", tagsMappingData);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

}
