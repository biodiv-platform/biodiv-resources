/**
 * 
 */
package com.strandls.resource.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import com.strandls.resource.pojo.Resource;
import com.strandls.resource.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 */
public class ResourceDao extends AbstractDAO<Resource, Long> {

	private static final Logger logger = LoggerFactory.getLogger(ResourceDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected ResourceDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public Resource findById(Long id) {
		Session session = sessionFactory.openSession();
		Resource entity = null;
		try {
			entity = session.get(Resource.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return entity;
	}

	@SuppressWarnings("unchecked")
	public List<Resource> findByObjectId(List<Long> objectIds) {

		String qry = "from Resource R where R.id in(:objectIds) order by rating DESC ";

		List<Resource> result = null;
		Session session = sessionFactory.openSession();
		try {
			Query<Resource> query = session.createQuery(qry);
			query.setParameter("objectIds", objectIds);

			result = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Long> getResourceIds(List<String> contexts, List<String> types) {
		String qry = "SELECT R.id FROM Resource R WHERE 1=1";

		if (contexts != null && !contexts.isEmpty() && !contexts.contains("all")) {
			qry += " AND R.context IN (:contexts)";
		}

		if (types != null && !types.isEmpty() && !types.contains("all")) {
			qry += " AND R.type IN (:types)";
		}

		List<Long> resourceIds = null;
		Session session = sessionFactory.openSession();
		try {
			Query<Long> query = session.createQuery(qry);

			if (contexts != null && !contexts.isEmpty()) {
				query.setParameterList("contexts", contexts);
			}

			if (types != null && !types.isEmpty()) {
				query.setParameterList("types", types);
			}

			resourceIds = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}

		return resourceIds;
	}

	@SuppressWarnings("unchecked")
	public List<Resource> findByIds(List<Long> ids, int limit, int offset) {
		List<Resource> entities = null;
		try (Session session = sessionFactory.openSession()) {
			String qry = "FROM Resource WHERE id IN :ids";
			Query<Resource> query = session.createQuery(qry);
			query.setParameterList("ids", ids);

			if (limit > 0 && offset >= 0) {
				query.setFirstResult(offset).setMaxResults(limit);
			}

			entities = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return entities;
	}

}
