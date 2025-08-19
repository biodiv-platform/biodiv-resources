package com.strandls.resource;

import com.strandls.file.api.UploadApi;
import com.strandls.utility.controller.UtilityServiceApi;

import jakarta.ws.rs.core.HttpHeaders;

/**
 * @author Arun
 */
public class Headers {

	public UploadApi addFileUploadHeader(UploadApi uploadService, String authHeader) {
		uploadService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return uploadService;
	}

	public UtilityServiceApi addUtilityHeaders(UtilityServiceApi utilityServices, String authHeader) {
		utilityServices.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return utilityServices;
	}
}
