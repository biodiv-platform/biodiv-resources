package com.strandls.resource.dao;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
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

	public Long getTotalMediaGalleryCount() {
		Session session = sessionFactory.openSession();
		try {
			Long count = (Long) session.createQuery("SELECT COUNT(id) FROM MediaGallery").uniqueResult();
			return count != null ? count : 0L;
		} catch (Exception e) {
			logger.error("Error getting total MediaGallery count: {}", e.getMessage());
			return 0L;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<MediaGallery> findByIds(List<Long> ids) {
		String qry = "from MediaGallery where id IN :ids";
		Session session = sessionFactory.openSession();
		List<MediaGallery> result = new ArrayList<>();
		try {
			Query<MediaGallery> query = session.createQuery(qry);
			query.setParameter("ids", ids);
			result = query.getResultList();

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
