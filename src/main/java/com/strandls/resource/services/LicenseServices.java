package com.strandls.resource.services;

import java.util.List;

import com.strandls.resource.pojo.License;

public interface LicenseServices {

	public List<License> getAllLicenses();

	public License getLicenseById(Long licenseId);
}
