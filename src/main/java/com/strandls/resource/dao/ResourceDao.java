/**
 * 
 */
package com.strandls.resource.dao;

import java.util.List;
import java.util.ArrayList;

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
	public List<Long> getResourceIds(List<String> contexts, List<String> types, List<Long> users, List<Long> ids) {
		String qry = "SELECT R.id FROM Resource R";
		String filters = buildFilters(contexts, types, users, ids);

		if (!filters.isEmpty()) {
			qry += filters;
		}

		List<Long> resourceIds = null;
		Session session = sessionFactory.openSession();
		try {
			Query<Long> query = session.createQuery(qry);

			if (contexts != null && !contexts.isEmpty() && !contexts.contains("all")) {
				query.setParameterList("contexts", contexts);
			}

			if (types != null && !types.isEmpty() && !types.contains("all")) {
				query.setParameterList("types", types);
			}

			if (users != null && !users.isEmpty()) {
				query.setParameterList("users", users);
			}

			if (ids != null) {
				query.setParameterList("ids", ids);
			}

			resourceIds = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}

		return resourceIds;
	}

	private String buildFilters(List<String> contexts, List<String> types, List<Long> users, List<Long> ids) {
		List<String> conditions = new ArrayList<>();

		if (contexts != null && !contexts.isEmpty() && !contexts.contains("all")) {
			conditions.add("R.context IN (:contexts)");
		}

		if (types != null && !types.isEmpty() && !types.contains("all")) {
			conditions.add("R.type IN (:types)");
		}

		if (users != null && !users.isEmpty()) {
			conditions.add("R.uploaderId IN (:users)");
		}

		if (ids != null) {
			conditions.add("R.id IN (:ids)");
		}

		if (!conditions.isEmpty()) {
			return " WHERE " + String.join(" AND ", conditions);
		} else {
			return "";
		}
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
