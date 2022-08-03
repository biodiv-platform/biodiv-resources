package com.strandls.resource.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.resource.pojo.ResourceCropInfo;
import com.strandls.resource.util.AbstractDAO;

public class ResourceCropDao extends AbstractDAO<ResourceCropInfo, Long> {
	private static final Logger logger = LoggerFactory.getLogger(ResourceCropDao.class);

	@Inject
	protected ResourceCropDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public ResourceCropInfo findById(Long id) {
		Session session = sessionFactory.openSession();
		ResourceCropInfo entity = null;
		try {
			entity = session.get(ResourceCropInfo.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return entity;

	}

	@SuppressWarnings("unchecked")
	public List<ResourceCropInfo> findByResourceIds(List<Long> resourceIds) {

		String qry = "from ResourceCropInfo R where R.id in(:resourceIds)";

		List<ResourceCropInfo> result = null;
		Session session = sessionFactory.openSession();
		try {
			Query<ResourceCropInfo> query = session.createQuery(qry);
			query.setParameter("resourceIds", resourceIds);

			result = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}

		return result;
	}

}
