package com.strandls.resource.controllers;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * @author Abhishek Rudra
 */
public class ResourceControllerModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(ResourceController.class).in(Scopes.SINGLETON);
		bind(LicenseController.class).in(Scopes.SINGLETON);
	}
}
