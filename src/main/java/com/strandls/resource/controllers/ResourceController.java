package com.strandls.resource.controllers;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.strandls.authentication_utility.filter.ValidateUser;
import com.strandls.resource.ApiConstants;
import com.strandls.resource.pojo.License;
import com.strandls.resource.pojo.MediaGallery;
import com.strandls.resource.pojo.MediaGalleryCreate;
import com.strandls.resource.pojo.MediaGalleryListPageData;
import com.strandls.resource.pojo.MediaGalleryResourceData;
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
import com.strandls.resource.services.ResourceServices;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Tag(name = "Resource Services", description = "APIs for resource microservice")
@Path(ApiConstants.V1 + ApiConstants.RESOURCE)
@Produces(MediaType.APPLICATION_JSON)
public class ResourceController {

	@Inject
	private ResourceServices service;

	@GET
	@Path(ApiConstants.PING)
	@Produces(MediaType.TEXT_PLAIN)
	@Operation(summary = "Dummy API Ping", description = "Checks validity of war file at deployment", responses = @ApiResponse(responseCode = "200", description = "Pong", content = @Content(schema = @Schema(implementation = String.class))))
	public String getPong() {
		return "PONG";
	}

	@GET
	@Path(ApiConstants.GETPATH + "/{objectType}/{objectId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Operation(summary = "Find Media Resource by Observation ID", responses = {
			@ApiResponse(responseCode = "200", description = "List of resource data", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResourceData.class)))),
			@ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response getImageResource(
			@Parameter(description = "Type of object", required = true) @PathParam("objectType") String objectType,
			@Parameter(description = "Object ID", required = true) @PathParam("objectId") String objectId) {
		try {
			Long objId = Long.parseLong(objectId);
			List<ResourceData> resource = service.getResouceURL(objectType, objId);
			return Response.status(Response.Status.OK).entity(resource).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	@POST
	@Path(ApiConstants.CREATE + "/{objectType}/{objectId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@ValidateUser
	@Operation(summary = "Create Resources against a objectId", requestBody = @RequestBody(required = true, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Resource.class)))), responses = {
			@ApiResponse(responseCode = "201", description = "All resources created", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Resource.class)))),
			@ApiResponse(responseCode = "206", description = "Some resources could not be created", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Resource.class)))),
			@ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response createResource(@Context HttpServletRequest request,
			@Parameter(description = "Type of object", required = true) @PathParam("objectType") String objectType,
			@Parameter(description = "Object ID", required = true) @PathParam("objectId") String objectId,
			List<Resource> resources) {
		try {
			Long id = Long.parseLong(objectId);
			List<Resource> result = service.createResource(objectType, id, resources);
			if (result.isEmpty())
				return Response.status(Response.Status.CREATED).entity(null).build();
			return Response.status(206).entity(result).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + "/{objectType}/{objectId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@ValidateUser
	@Operation(summary = "Update Resources against a objectId", requestBody = @RequestBody(required = true, content = @Content(array = @ArraySchema(schema = @Schema(implementation = Resource.class)))), responses = {
			@ApiResponse(responseCode = "200", description = "Resources updated", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Resource.class)))),
			@ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response updateResources(@Context HttpServletRequest request,
			@Parameter(description = "Type of object", required = true) @PathParam("objectType") String objectType,
			@Parameter(description = "Object ID", required = true) @PathParam("objectId") String objectId,
			List<Resource> resources) {
		try {
			Long objId = Long.parseLong(objectId);
			List<Resource> result = service.updateResource(objectType, objId, resources);
			return Response.status(Response.Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + ApiConstants.RATING + "/{objectType}/{objectId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@ValidateUser
	@Operation(summary = "Update the rating of the resource", requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = ResourceRating.class))), responses = {
			@ApiResponse(responseCode = "200", description = "Resources with updated rating", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Resource.class)))),
			@ApiResponse(responseCode = "400", description = "Unable to update the rating", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response updateRating(@Context HttpServletRequest request,
			@Parameter(description = "Type of object", required = true) @PathParam("objectType") String objectType,
			@Parameter(description = "Object ID", required = true) @PathParam("objectId") String objectId,
			ResourceRating resourceRating) {
		try {
			Long objId = Long.parseLong(objectId);
			List<Resource> result = service.updateResourceRating(objectType, objId, resourceRating);
			return Response.status(Response.Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.LICENSE + "/{licenseId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Operation(summary = "Find Media Resource of License by ID", responses = {
			@ApiResponse(responseCode = "200", description = "License found", content = @Content(schema = @Schema(implementation = License.class))),
			@ApiResponse(responseCode = "404", description = "License not found", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response getLicenseResource(
			@Parameter(description = "ID for License Resource", required = true) @PathParam("licenseId") String licenseId) {
		try {
			Long id = Long.parseLong(licenseId);
			License license = service.getLicenseResouce(id);
			if (license != null)
				return Response.status(Response.Status.OK).entity(license).build();
			else
				return Response.status(Response.Status.NOT_FOUND).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path(ApiConstants.UFILE + "/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Operation(summary = "Finds ufile by id", responses = {
			@ApiResponse(responseCode = "200", description = "UFile found", content = @Content(schema = @Schema(implementation = UFile.class))),
			@ApiResponse(responseCode = "400", description = "Unable to find the ufile data", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response getUFilePath(@Parameter(description = "UFile id", required = true) @PathParam("id") String id) {
		try {
			Long ufileId = Long.parseLong(id);
			UFile result = service.uFileFindById(ufileId);
			return Response.status(Response.Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.UFILE)
	@Consumes(MediaType.APPLICATION_JSON)
	@ValidateUser
	@Operation(summary = "Create the Ufile object", requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = UFileCreateData.class))), responses = {
			@ApiResponse(responseCode = "200", description = "UFile created", content = @Content(schema = @Schema(implementation = UFile.class))),
			@ApiResponse(responseCode = "406", description = "Data missing", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "Unable to create the ufile", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response createUFile(@Context HttpServletRequest request, UFileCreateData ufileCreateData) {
		try {
			UFile result = service.createUFile(ufileCreateData);
			if (result != null)
				return Response.status(Response.Status.OK).entity(result).build();
			return Response.status(Status.NOT_ACCEPTABLE).entity("Data missing").build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path(ApiConstants.REMOVE + ApiConstants.UFILE + "/{uFileId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@ValidateUser
	@Operation(summary = "Remove the ufile", responses = {
			@ApiResponse(responseCode = "200", description = "Boolean deleted", content = @Content(schema = @Schema(implementation = Boolean.class))),
			@ApiResponse(responseCode = "406", description = "Data missing", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "Unable to delete the ufile", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response removeUFile(@Context HttpServletRequest request,
			@Parameter(description = "UFile id", required = true) @PathParam("uFileId") String uFileId) {
		try {
			Long ufileId = Long.parseLong(uFileId);
			Boolean result = service.removeUFile(ufileId);
			if (result != null)
				return Response.status(Response.Status.OK).entity(result).build();
			return Response.status(Response.Status.NOT_ACCEPTABLE).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.BULK + ApiConstants.GETPATH + "/{objectType}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get multiple resources", requestBody = @RequestBody(required = true, description = "List of object IDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Long.class)))), responses = {
			@ApiResponse(responseCode = "200", description = "Multiple resources", content = @Content(array = @ArraySchema(schema = @Schema(implementation = SpeciesPull.class)))),
			@ApiResponse(responseCode = "400", description = "unable to fetch resource", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response getBulkResources(
			@Parameter(description = "Type of the observation", required = true) @PathParam("objectType") String objectType,
			@Parameter(description = "Offset", example = "0") @DefaultValue("0") @QueryParam("offset") String offset,
			List<Long> objectIds) {
		try {
			Long offSet = Long.parseLong(offset);
			List<SpeciesPull> result = service.getresourceMultipleObserId(objectType, objectIds, offSet);
			return Response.status(Response.Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.PULLRESOURCE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@Operation(summary = "pull resources for species", requestBody = @RequestBody(required = true, description = "Species resource pulling criteria", content = @Content(schema = @Schema(implementation = SpeciesResourcePulling.class))), responses = {
			@ApiResponse(responseCode = "200", description = "Multiple resources", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResourceData.class)))),
			@ApiResponse(responseCode = "400", description = "unable to pull resource", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response pullResource(@Context HttpServletRequest request, SpeciesResourcePulling resourcePulling) {
		try {
			List<ResourceData> result = service.speciesResourcesPulling(resourcePulling);
			return Response.status(Response.Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.GETPATH + "/{resourceId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "fetch resource by Id", responses = {
			@ApiResponse(responseCode = "200", description = "Resource found", content = @Content(schema = @Schema(implementation = Resource.class))),
			@ApiResponse(responseCode = "400", description = "unable to fetch resource", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response getResourceDataById(
			@Parameter(description = "Resource ID", required = true) @PathParam("resourceId") String resourceId) {
		try {
			Long rId = Long.parseLong(resourceId);
			Resource result = service.getResourceById(rId);
			return Response.status(Response.Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path(ApiConstants.REMOVE + ApiConstants.SPECIESFIELD + "/{sfId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@Operation(summary = "remove speciesField mapping", responses = {
			@ApiResponse(responseCode = "200", description = "Mapping deleted", content = @Content(schema = @Schema(implementation = Boolean.class))),
			@ApiResponse(responseCode = "400", description = "unable to fetch resource", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response removeSFMapping(@Context HttpServletRequest request,
			@Parameter(description = "Species Field Mapping ID", required = true) @PathParam("sfId") String sfId) {
		try {
			Long speciesFieldId = Long.parseLong(sfId);
			Boolean result = service.removeSpeciesFieldMapping(speciesFieldId);
			return Response.status(Response.Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/cropInfo/{resourceId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Get crop details of resources", responses = {
			@ApiResponse(responseCode = "200", description = "Crop info details", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResourceCropInfo.class)))),
			@ApiResponse(responseCode = "400", description = "unable to fetch resource", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response getResourcesCropInfo(
			@Parameter(description = "Comma-separated resource IDs", required = true) @PathParam("resourceId") String resourceIds) {
		try {
			List<ResourceCropInfo> result = service.fetchResourceCropInfo(resourceIds);
			return Response.status(Response.Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path("/cropInfo/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@Operation(summary = "update crop details of resources", requestBody = @RequestBody(required = true, description = "Crop Info details update", content = @Content(schema = @Schema(implementation = ResourceCropInfo.class))), responses = {
			@ApiResponse(responseCode = "200", description = "Crop info updated", content = @Content(schema = @Schema(implementation = ResourceCropInfo.class))),
			@ApiResponse(responseCode = "400", description = "unable to fetch resource", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response updateResourcesCropInfo(@Context HttpServletRequest request, ResourceCropInfo resourceCropInfo) {
		try {
			ResourceCropInfo result = service.updateResourceCropInfo(resourceCropInfo);
			return Response.status(Response.Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.MEDIAGALLERY + ApiConstants.EDITPAGE + "/{mId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Find Media Resource by ID", responses = {
			@ApiResponse(responseCode = "200", description = "Media Gallery", content = @Content(schema = @Schema(implementation = MediaGalleryShow.class))),
			@ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response getMedia(
			@Parameter(description = "ID for Resource", required = true) @PathParam("mId") String mId) {
		try {
			Long objId = Long.parseLong(mId);
			MediaGalleryShow mediaGallery = service.getMediaByID(objId);
			return Response.status(Status.OK).entity(mediaGallery).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@POST
	@Path(ApiConstants.MEDIAGALLERY + ApiConstants.CREATE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@Operation(summary = "Create a Media Gallery", requestBody = @RequestBody(required = true, description = "Media Gallery Create object", content = @Content(schema = @Schema(implementation = MediaGalleryCreate.class))), responses = {
			@ApiResponse(responseCode = "200", description = "Created/show media gallery", content = @Content(schema = @Schema(implementation = MediaGalleryShow.class))),
			@ApiResponse(responseCode = "406", description = "Data missing", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "unable to create the media gallery", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response createMedia(@Context HttpServletRequest request, MediaGalleryCreate mediaGalleryCreate) {
		try {
			MediaGalleryShow result = service.createMedia(request, mediaGalleryCreate);
			if (result != null)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_ACCEPTABLE).entity("Data missing").build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.MEDIAGALLERY + ApiConstants.UPLOAD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@Operation(summary = "Upload resource list to media gallery", requestBody = @RequestBody(required = true, description = "List of resources to upload", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResourceWithTags.class)))), responses = {
			@ApiResponse(responseCode = "200", description = "Upload result", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "406", description = "Error Uploading", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "unable to create the ufile", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response uploadResourceMediaGallery(@Context HttpServletRequest request,
			List<ResourceWithTags> resourceUpload) {
		try {
			String result = service.uploadMedia(request, resourceUpload);
			if (result != null)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_ACCEPTABLE).entity("Error Uploading").build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.MEDIAGALLERY + ApiConstants.SHOW)
	@Consumes(MediaType.TEXT_PLAIN)
	@Operation(summary = "Find Media Resource by ID", description = "Returns Media Gallery page with filtering and pagination", responses = {
			@ApiResponse(responseCode = "200", description = "Media Gallery result", content = @Content(schema = @Schema(implementation = MediaGalleryShow.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response getMediaGallery(
			@Parameter(description = "Offset", example = "0") @DefaultValue("0") @QueryParam("offset") String offset,
			@Parameter(description = "Limit", example = "12") @DefaultValue("12") @QueryParam("limit") String limit,
			@Parameter(description = "Resource type", example = "all") @DefaultValue("all") @QueryParam("type") String type,
			@Parameter(description = "Tags filter", example = "all") @DefaultValue("all") @QueryParam("tags") String tags,
			@Parameter(description = "User(s) filter", example = "all") @DefaultValue("all") @QueryParam("user") String users,
			@Parameter(description = "Media Gallery IDs filter", example = "all") @DefaultValue("all") @QueryParam("mId") String mIds) {
		try {
			Integer max = Integer.parseInt(limit);
			Integer offSet = Integer.parseInt(offset);
			MediaGalleryShow mediaGallery = service.getMediaByID(mIds, max, offSet, type, tags, users);
			return Response.status(Status.OK).entity(mediaGallery).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path(ApiConstants.MEDIAGALLERY + ApiConstants.LIST)
	@Consumes(MediaType.TEXT_PLAIN)
	@Operation(summary = "Find Media Gallery (paginated list)", description = "Returns paginated list of media gallery items", responses = {
			@ApiResponse(responseCode = "200", description = "Media Gallery List page", content = @Content(schema = @Schema(implementation = MediaGalleryListPageData.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response getMediaGalleryList(
			@Parameter(description = "Offset", example = "0") @DefaultValue("0") @QueryParam("offset") String offset,
			@Parameter(description = "Limit", example = "12") @DefaultValue("12") @QueryParam("limit") String limit) {
		try {
			Integer max = Integer.parseInt(limit);
			Integer offSet = Integer.parseInt(offset);
			MediaGalleryListPageData mediaGalleryListPageData = service.getMediaGalleryListPageData(max, offSet);
			return Response.status(Status.OK).entity(mediaGalleryListPageData).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path(ApiConstants.MEDIAGALLERY + ApiConstants.ALL)
	@Consumes(MediaType.TEXT_PLAIN)
	@Operation(summary = "Find All Media Resource", description = "Returns Media Gallery list", responses = {
			@ApiResponse(responseCode = "200", description = "List of all MediaGallery", content = @Content(array = @ArraySchema(schema = @Schema(implementation = MediaGallery.class)))),
			@ApiResponse(responseCode = "400", description = "Unable to fetch the data", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response getAllMediaGallery() {
		try {
			List<MediaGallery> mediaGallery = service.getAllMediaGallery();
			return Response.status(Status.OK).entity(mediaGallery).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@POST
	@Path(ApiConstants.ALL)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Find All Media Resource", description = "Returns List of Media using advanced/paginated search", responses = {
			@ApiResponse(responseCode = "200", description = "Results list", content = @Content(schema = @Schema(implementation = ResourceListData.class))),
			@ApiResponse(responseCode = "400", description = "Unable to fetch the data", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response getAllResources(@Context HttpServletRequest request,
			@Parameter(description = "Offset", example = "0") @DefaultValue("0") @QueryParam("offset") String offset,
			@Parameter(description = "Limit", example = "12") @DefaultValue("12") @QueryParam("limit") String limit,
			@Parameter(description = "Context", example = "all") @DefaultValue("all") @QueryParam("context") String context,
			@Parameter(description = "Resource type", example = "all") @DefaultValue("all") @QueryParam("type") String type,
			@Parameter(description = "Tags", example = "all") @DefaultValue("all") @QueryParam("tags") String tags,
			@Parameter(description = "User", example = "all") @DefaultValue("all") @QueryParam("user") String users,
			@Parameter(description = "Bulk posting?", example = "false") @DefaultValue("false") @QueryParam("isBulkPosting") Boolean isBulkPosting,
			@Parameter(description = "Select all?", example = "false") @DefaultValue("false") @QueryParam("selectAll") Boolean selectAll,
			@Parameter(description = "Unselected IDs") @QueryParam("unSelected") String unSelectedIds,
			@Parameter(description = "Resource IDs") @QueryParam("resourceIds") String resourceIds,
			@Parameter(description = "Media gallery IDs") @QueryParam("mediaGalleryIds") String mediaGalleryIds) {
		try {
			Integer max = Integer.parseInt(limit);
			Integer offSet = Integer.parseInt(offset);
			ResourceListData resultList = service.getAllResources(max, offSet, context, type, tags, users, request,
					isBulkPosting, selectAll, unSelectedIds, resourceIds, mediaGalleryIds);
			return Response.status(Status.OK).entity(resultList).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@DELETE
	@Path(ApiConstants.MEDIAGALLERY + ApiConstants.DELETE + "/{mId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@Operation(summary = "Delete MediaGallery by ID", description = "Deletes a media gallery by its ID", responses = {
			@ApiResponse(responseCode = "200", description = "Deleted. Returns status message.", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response deleteMedia(@Context HttpServletRequest request,
			@Parameter(description = "ID for Resource", required = true) @PathParam("mId") String mediaGalleryId) {
		try {
			Long mId = Long.parseLong(mediaGalleryId);
			String result = service.deleteMediaByID(request, mId);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@PUT
	@Path(ApiConstants.MEDIAGALLERY + ApiConstants.UPDATE + "/{mId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@Operation(summary = "Update Media Gallery", description = "Updates a media gallery and returns the updated object", requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = MediaGalleryCreate.class))), responses = {
			@ApiResponse(responseCode = "200", description = "Returns updated Media Gallery", content = @Content(schema = @Schema(implementation = MediaGalleryShow.class))),
			@ApiResponse(responseCode = "400", description = "Unable to update Media Gallery", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response updateMediaGallery(@Context HttpServletRequest request,
			@Parameter(description = "ID for Resource", required = true) @PathParam("mId") String mediaGalleryId,
			MediaGalleryCreate mediaGallery) {
		try {
			Long mId = Long.parseLong(mediaGalleryId);
			MediaGalleryShow updatedMediaGallery = service.updateMediaGalleryByID(request, mId, mediaGallery);
			return Response.status(Status.OK).entity(updatedMediaGallery).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path("/{rId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Find Resource by ID", description = "Returns a MediaGalleryResourceData object for a given resource ID", responses = {
			@ApiResponse(responseCode = "200", description = "Media Gallery Resource Data", content = @Content(schema = @Schema(implementation = MediaGalleryResourceData.class))),
			@ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response getResource(
			@Parameter(description = "ID for Resource", required = true) @PathParam("rId") String resourceId) {
		try {
			Long rID = Long.parseLong(resourceId);
			MediaGalleryResourceData mediaGallery = service.getResourceDataByID(rID);
			return Response.status(Status.OK).entity(mediaGallery).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@Operation(summary = "Update Resource", description = "Updates a resource and returns updated resource", requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = ResourceWithTags.class))), responses = {
			@ApiResponse(responseCode = "200", description = "Resource updated", content = @Content(schema = @Schema(implementation = Resource.class))),
			@ApiResponse(responseCode = "400", description = "Unable to update Resource", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response updateResource(@Context HttpServletRequest request, ResourceWithTags resourceWithTags) {
		try {
			Resource updatedMediaGallery = service.updateResourceByID(request, resourceWithTags);
			return Response.status(Status.OK).entity(updatedMediaGallery).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@DELETE
	@Path(ApiConstants.DELETE + "/{rId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@Operation(summary = "Delete resource by ID", description = "Deletes a resource by its ID", responses = {
			@ApiResponse(responseCode = "200", description = "Resource deleted. Returns status message.", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "Invalid ID", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response deleteResource(@Context HttpServletRequest request,
			@Parameter(description = "ID for Resource", required = true) @PathParam("rId") String resourceId) {
		try {
			Long rId = Long.parseLong(resourceId);
			String result = service.deleteResourceByID(request, rId);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path(ApiConstants.IMAGE + "/{rId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Operation(summary = "Get the image resource with custom height & width by url", description = "Returns the image stream for the resource, can resize/convert based on query", responses = {
			@ApiResponse(responseCode = "200", description = "Image stream (binary)"),
			@ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response getImage(@Context HttpServletRequest request, @PathParam("rId") String resourceId,
			@QueryParam("w") Integer width, @QueryParam("h") Integer height,
			@DefaultValue("webp") @QueryParam("fm") String format, @DefaultValue("") @QueryParam("fit") String fit,
			@DefaultValue("false") @QueryParam("preserve") String preserve) throws UnsupportedEncodingException {
		Long rId = Long.parseLong(resourceId);
		String hAccept = request.getHeader(HttpHeaders.ACCEPT);
		boolean preserveFormat = Boolean.parseBoolean(preserve);
		boolean isWebpRequested = hAccept.contains("webp") && format.equalsIgnoreCase("webp");
		boolean isFormatNotWebp = !format.equalsIgnoreCase("webp");
		String userRequestedFormat;
		if (isWebpRequested && format.equalsIgnoreCase("webp")) {
			userRequestedFormat = "webp";
		} else if (isFormatNotWebp) {
			userRequestedFormat = format;
		} else {
			userRequestedFormat = "jpg";
		}
		return service.getImage(request, rId, width, height, userRequestedFormat, fit, preserveFormat);
	}
}
