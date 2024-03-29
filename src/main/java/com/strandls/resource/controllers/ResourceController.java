/**
 * 
 */
package com.strandls.resource.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.StreamingOutput;

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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author Abhishek Rudra
 *
 */

@Api("Resource Services")
@Path(ApiConstants.V1 + ApiConstants.RESOURCE)
public class ResourceController {

	@Inject
	private ResourceServices service;

	@ApiOperation(value = "Dummy API Ping", notes = "Checks validity of war file at deployment", response = String.class)

	@GET
	@Path(ApiConstants.PING)
	@Produces(MediaType.TEXT_PLAIN)
	public String getPong() {
		return "PONG";
	}

	@GET
	@Path(ApiConstants.GETPATH + "/{objectType}/{objectId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find Media Reource by Observation ID", notes = "Returns Path of the Resources", response = ResourceData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID", response = String.class) })

	public Response getImageResource(
			@ApiParam(value = "ID Observation for Resource", required = true) @PathParam("objectType") String objectType,
			@PathParam("objectId") String objectId) {
		try {

			Long objId = Long.parseLong(objectId);
			List<ResourceData> resource = service.getResouceURL(objectType, objId);
			return Response.status(Status.OK).entity(resource).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@POST
	@Path(ApiConstants.CREATE + "/{objectType}/{objectId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser
	@ApiOperation(value = "Create Resources against a objectId", notes = "Returns list of uncreated resources", response = Resource.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID", response = String.class) })

	public Response createResource(@Context HttpServletRequest request, @PathParam("objectType") String objectType,
			@PathParam("objectId") String objectId, @ApiParam(name = "resources") List<Resource> resources) {
		try {
			Long id = Long.parseLong(objectId);
			List<Resource> result = service.createResource(objectType, id, resources);
			if (result.isEmpty())
				return Response.status(Status.CREATED).entity(null).build();
			return Response.status(206).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + "/{objectType}/{objectId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser
	@ApiOperation(value = "Update Resources against a objectId", notes = "Returns list of uncreated resources", response = Resource.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID", response = String.class) })

	public Response updateResources(@Context HttpServletRequest request, @PathParam("objectType") String objectType,
			@PathParam("objectId") String objectId, @ApiParam(name = "resources") List<Resource> resources) {
		try {
			Long objId = Long.parseLong(objectId);
			List<Resource> result = service.updateResource(objectType, objId, resources);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + ApiConstants.RATING + "/{objectType}/{objectId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "update the rating of the resource", notes = "Returns all the resource", response = Resource.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to update the rating", response = String.class) })

	public Response updateRating(@Context HttpServletRequest request, @PathParam("objectType") String objectType,
			@PathParam("objectId") String objectId, @ApiParam(name = "resourceRating") ResourceRating resourceRating) {
		try {
			Long objId = Long.parseLong(objectId);
			List<Resource> result = service.updateResourceRating(objectType, objId, resourceRating);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

	}

	@GET
	@Path(ApiConstants.LICENSE + "/{licenseId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find Media Reource of License by ID", notes = "Returns Path of the license", response = License.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "License not found", response = String.class),
			@ApiResponse(code = 400, message = "Invalid ID", response = String.class) })

	public Response getLicenseResource(
			@ApiParam(value = "ID for License Resource", required = true) @PathParam("licenseId") String licenseId) {
		try {
			Long id = Long.parseLong(licenseId);
			License license = service.getLicenseResouce(id);

			if (license != null)
				return Response.status(Status.OK).entity(license).build();
			else
				return Response.status(Status.NOT_FOUND).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path(ApiConstants.UFILE + "/{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "finds ufile by id", notes = "Return the ufile data as per id", response = UFile.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to find the ufile data", response = String.class) })

	public Response getUFilePath(@PathParam("id") String id) {
		try {
			Long ufileId = Long.parseLong(id);
			UFile result = service.uFileFindById(ufileId);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.UFILE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "create the Ufile object", notes = "return the ufile object on completion", response = UFile.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to create the ufile", response = String.class) })

	public Response createUFile(@Context HttpServletRequest request,
			@ApiParam(name = "ufileCreateData") UFileCreateData ufileCreateData) {
		try {
			UFile result = service.createUFile(ufileCreateData);
			if (result != null)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_ACCEPTABLE).entity("Data missing").build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path(ApiConstants.REMOVE + ApiConstants.UFILE + "/{uFileId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Remove the ufile", notes = "returns the booelan for deletion", response = Boolean.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to delete the ufile", response = String.class) })

	public Response removeUFile(@Context HttpServletRequest request, @PathParam("uFileId") String uFileId) {
		try {
			Long ufileId = Long.parseLong(uFileId);
			Boolean result = service.removeUFile(ufileId);
			if (result != null)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_ACCEPTABLE).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.BULK + ApiConstants.GETPATH + "/{objectType}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "get multiple resources", notes = "returns multiple resources", response = SpeciesPull.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch resource", response = String.class) })

	public Response getBulkResources(@PathParam("objectType") String objectType,
			@DefaultValue("0") @QueryParam("offset") String offset,
			@ApiParam(name = "objectIds") List<Long> objectIds) {
		try {

			Long offSet = Long.parseLong(offset);
			List<SpeciesPull> result = service.getresourceMultipleObserId(objectType, objectIds, offSet);
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.PULLRESOURCE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "pull resources for speciess", notes = "returns multiple resources", response = ResourceData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to pull resource", response = String.class) })

	public Response pullResource(@Context HttpServletRequest request,
			@ApiParam(name = "resourcePulling") SpeciesResourcePulling resourcePulling) {
		try {
			List<ResourceData> result = service.speciesResourcesPulling(resourcePulling);
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.GETPATH + "/{resourceId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "fetch resource by Id", notes = "returns  resources", response = Resource.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch resource", response = String.class) })

	public Response getResourceDataById(@PathParam("resourceId") String resourceId) {
		try {
			Long rId = Long.parseLong(resourceId);
			Resource result = service.getResourceById(rId);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path(ApiConstants.REMOVE + ApiConstants.SPECIESFIELD + "/{sfId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "remove speciesField mapping", notes = "returns boolean", response = Boolean.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch resource", response = String.class) })

	public Response removeSFMapping(@Context HttpServletRequest request, @PathParam("sfId") String sfId) {
		try {
			Long speciesFieldId = Long.parseLong(sfId);
			Boolean result = service.removeSpeciesFieldMapping(speciesFieldId);
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/cropInfo/{resourceId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "get crop details of resources", notes = "returns something", response = ResourceCropInfo.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch resource", response = String.class) })
	public Response getResourcesCropInfo(@PathParam("resourceId") String resourceIds) {
		try {
			List<ResourceCropInfo> result = service.fetchResourceCropInfo(resourceIds);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

	}

	@PUT
	@Path("/cropInfo/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@ApiOperation(value = "update crop details of resources", notes = "returns updated crop information", response = ResourceCropInfo.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch resource", response = String.class) })

	public Response updateResourcesCropInfo(@Context HttpServletRequest request,
			@ApiParam(name = "resourcesCropInfo") ResourceCropInfo resourceCropInfo) {
		try {
			ResourceCropInfo result = service.updateResourceCropInfo(resourceCropInfo);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.MEDIAGALLERY + ApiConstants.EDITPAGE + "/{mId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find Media Resource by  ID", notes = "Returns Media", response = ResourceData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID", response = String.class) })

	public Response getMedia(@ApiParam(value = "ID  for Resource", required = true) @PathParam("mId") String mId) {
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

	@ApiOperation(value = "create the Ufile object", notes = "return the ufile object on completion", response = UFile.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to create the ufile", response = String.class) })

	public Response createMedia(@Context HttpServletRequest request,
			@ApiParam(name = "ufileCreateData") MediaGalleryCreate mediaGalleryCreate) {
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

	@ApiOperation(value = "create the Ufile object", notes = "return the ufile object on completion", response = UFile.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to create the ufile", response = String.class) })

	public Response uploadResourceMediaGallery(@Context HttpServletRequest request,
			@ApiParam(name = "ufileCreateData") List<ResourceWithTags> resourceUpload) {
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
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find Media Resource by  ID", notes = "Returns Media Gallery", response = ResourceData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID", response = String.class) })

	public Response getMediaGallery(@DefaultValue("0") @QueryParam("offset") String offset,
			@DefaultValue("12") @QueryParam("limit") String limit, @DefaultValue("all") @QueryParam("type") String type,
			@DefaultValue("all") @QueryParam("tags") String tags, @DefaultValue("all") @QueryParam("user") String users,
			@DefaultValue("all") @QueryParam("mId") String mIds) {
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
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find Media Resource by  ID", notes = "Returns Media Gallery", response = ResourceData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID", response = String.class) })

	public Response getMediaGalleryList(@DefaultValue("0") @QueryParam("offset") String offset,
			@DefaultValue("12") @QueryParam("limit") String limit) {
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
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Find All Media Resource", notes = "Returns Media Gallery", response = ResourceData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

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

	@ApiOperation(value = "Find All Media Resource ", notes = "Returns List of Media", response = ResourceData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response getAllResources(@Context HttpServletRequest request,
			@DefaultValue("0") @QueryParam("offset") String offset,
			@DefaultValue("12") @QueryParam("limit") String limit,
			@DefaultValue("all") @QueryParam("context") String context,
			@DefaultValue("all") @QueryParam("type") String type, @DefaultValue("all") @QueryParam("tags") String tags,
			@DefaultValue("all") @QueryParam("user") String users,
			@DefaultValue("false") @QueryParam("isBulkPosting") Boolean isBulkPosting,
			@DefaultValue("false") @QueryParam("selectAll") Boolean selectAll,
			@QueryParam("unSelected") String unSelectedIds, @QueryParam("resourceIds") String resourceIds,
			@QueryParam("mediaGalleryIds") String mediaGalleryIds) {
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

	@ApiOperation(value = "Delete MediaGallery by  ID", notes = "Returns Media", response = ResourceData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID", response = String.class) })

	public Response deleteMedia(@Context HttpServletRequest request,
			@ApiParam(value = "ID  for Resource", required = true) @PathParam("mId") String mediaGalleryId) {
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

	@ApiOperation(value = "Update Media Gallery", notes = "Returns Media", response = ResourceData.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Unable TO update Media Gallery", response = String.class) })

	public Response updateMediaGallery(@Context HttpServletRequest request,
			@ApiParam(value = "ID  for Resource", required = true) @PathParam("mId") String mediaGalleryId,
			@ApiParam(name = "mediaGallery") MediaGalleryCreate mediaGallery) {
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

	@ApiOperation(value = "Find Resource by  ID", notes = "Returns Resource", response = MediaGalleryResourceData.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID", response = String.class) })

	public Response getResource(
			@ApiParam(value = "ID  for Resource", required = true) @PathParam("rId") String resourceId) {
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

	@ApiOperation(value = "Update Resource", notes = "Returns Resource", response = ResourceWithTags.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable To update Resource", response = String.class) })

	public Response updateResource(@Context HttpServletRequest request,
			@ApiParam(name = "resourceWithTags") ResourceWithTags resourceWithTags) {
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

	@ApiOperation(value = "Delete resource by  ID", notes = "Returns Media", response = Resource.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID", response = String.class) })

	public Response deleteResource(@Context HttpServletRequest request,
			@ApiParam(value = "ID  for Resource", required = true) @PathParam("rId") String resourceId) {
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
	@ApiOperation(value = "Get the image resource with custom height & width by url", response = StreamingOutput.class)
	public Response getImage(@Context HttpServletRequest request, @PathParam("rId") String resourceId,
			@QueryParam("w") Integer width, @QueryParam("h") Integer height,
			@DefaultValue("webp") @QueryParam("fm") String format, @DefaultValue("") @QueryParam("fit") String fit,
			@DefaultValue("false") @QueryParam("preserve") String presereve) throws UnsupportedEncodingException {

		Long rId = Long.parseLong(resourceId);
		String hAccept = request.getHeader(HttpHeaders.ACCEPT);
		boolean preserveFormat = Boolean.parseBoolean(presereve);
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
