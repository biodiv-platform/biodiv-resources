/**
 * 
 */
package com.strandls.resource.dao;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * @author Abhishek Rudra
 *
 */
public class ResourceDaoModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(ResourceDao.class).in(Scopes.SINGLETON);
		bind(LicenseDao.class).in(Scopes.SINGLETON);
		bind(ObservationResourceDao.class).in(Scopes.SINGLETON);
		bind(SpeciesResourceDao.class).in(Scopes.SINGLETON);
		bind(SpeciesFieldResourcesDao.class).in(Scopes.SINGLETON);
		bind(UFileDao.class).in(Scopes.SINGLETON);
		bind(ResourceCropDao.class).in(Scopes.SINGLETON);
		bind(MediaGalleryDao.class).in(Scopes.SINGLETON);

	}
}
