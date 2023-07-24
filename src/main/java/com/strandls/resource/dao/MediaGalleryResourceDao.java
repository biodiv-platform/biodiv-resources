package com.strandls.resource.dao;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.resource.pojo.MediaGalleryResource;
import com.strandls.resource.util.AbstractDAO;

public class MediaGalleryResourceDao extends AbstractDAO<MediaGalleryResource, Long> {

	private final Logger logger = LoggerFactory.getLogger(MediaGalleryResourceDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected MediaGalleryResourceDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public MediaGalleryResource findById(Long id) {
		Session session = sessionFactory.openSession();
		MediaGalleryResource entity = null;
		try {
			entity = session.get(MediaGalleryResource.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return entity;
	}

	@SuppressWarnings("unchecked")
	public List<Long> findByMediaId(Long mediaGalleryId) {
		List<MediaGalleryResource> mediaGalleryResourceList = null;
		String qry = "from MediaGalleryResource where  mediaGalleryId = :mediaGalleryId";
		Session session = sessionFactory.openSession();
		List<Long> result = new ArrayList<>();
		try {
			Query<MediaGalleryResource> query = session.createQuery(qry);
			query.setParameter("mediaGalleryId", mediaGalleryId);
			mediaGalleryResourceList = query.getResultList();
			if (mediaGalleryResourceList != null && !mediaGalleryResourceList.isEmpty()) {
				for (MediaGalleryResource mediaGalleryResource : mediaGalleryResourceList)
					result.add(mediaGalleryResource.getResourceId());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Long> findByMediaIds(List<Long> mediaGalleryIds) {
		List<MediaGalleryResource> mediaGalleryResourceList = null;
		String qry = "from MediaGalleryResource where mediaGalleryId IN :mediaGalleryIds";
		Session session = sessionFactory.openSession();
		List<Long> result = new ArrayList<>();
		try {
			Query<MediaGalleryResource> query = session.createQuery(qry);
			query.setParameter("mediaGalleryIds", mediaGalleryIds);
			mediaGalleryResourceList = query.getResultList();
			if (mediaGalleryResourceList != null && !mediaGalleryResourceList.isEmpty()) {
				for (MediaGalleryResource mediaGalleryResource : mediaGalleryResourceList) {
					result.add(mediaGalleryResource.getResourceId());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public MediaGalleryResource findByPair(Long mediaGalleryId, Long resourceId) {
		String qry = "from MediaGalleryResource where mediaGalleryId = :mediaGalleryId and resourceId = :resourceId";
		Session session = sessionFactory.openSession();
		MediaGalleryResource result = null;
		try {
			Query<MediaGalleryResource> query = session.createQuery(qry);
			query.setParameter("mediaGalleryId", mediaGalleryId);
			query.setParameter("resourceId", resourceId);
			result = query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
