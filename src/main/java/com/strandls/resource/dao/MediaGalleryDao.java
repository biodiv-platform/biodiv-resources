package com.strandls.resource.dao;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.resource.pojo.MediaGallery;
import com.strandls.resource.util.AbstractDAO;

public class MediaGalleryDao extends AbstractDAO<MediaGallery, Long> {

	private static final Logger logger = LoggerFactory.getLogger(MediaGalleryDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected MediaGalleryDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public MediaGallery findById(Long id) {
		Session session = sessionFactory.openSession();
		MediaGallery entity = null;
		try {
			entity = session.get(MediaGallery.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return entity;

	}

}
