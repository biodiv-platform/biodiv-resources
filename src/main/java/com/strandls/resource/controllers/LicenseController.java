package com.strandls.resource.controllers;

import java.util.List;

import com.strandls.resource.ApiConstants;
import com.strandls.resource.pojo.License;
import com.strandls.resource.services.LicenseServices;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Tag(name = "License Controller", description = "API for fetching licenses")
@Path(ApiConstants.V1 + ApiConstants.LICENSE)
@Produces(MediaType.APPLICATION_JSON)
public class LicenseController {

	@Inject
	LicenseServices licenseServices;

	@GET
	@Path(ApiConstants.ALL)
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Fetch all licenses", description = "Returns all licenses", responses = {
			@ApiResponse(responseCode = "200", description = "List of all licenses", content = @Content(array = @ArraySchema(schema = @Schema(implementation = License.class)))),
			@ApiResponse(responseCode = "400", description = "Could not fetch licenses", content = @Content(schema = @Schema(implementation = String.class))) })
	public Response getAllLicenses() {
		try {
			List<License> licenses = licenseServices.getAllLicenses();
			return Response.status(Status.OK).entity(licenses).build();
		} catch (Exception ex) {
			return Response.status(Status.BAD_REQUEST).entity(ex.getMessage()).build();
		}
	}
}
