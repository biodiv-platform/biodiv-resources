/** */
package com.strandls.resource.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.resource.pojo.License;
import com.strandls.resource.util.AbstractDAO;

import jakarta.inject.Inject;

/**
 * @author Abhishek Rudra
 */
public class LicenseDao extends AbstractDAO<License, Long> {

	private static final Logger logger = LoggerFactory.getLogger(LicenseDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected LicenseDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public License findById(Long id) {

		Session session = sessionFactory.openSession();
		License entity = null;
		try {
			entity = session.get(License.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return entity;
	}

	@SuppressWarnings("unchecked")
	public List<License> findByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty())
			return new ArrayList<License>();

		Session session = sessionFactory.openSession();
		List<License> result = new ArrayList<License>();
		String qry = "from License where id IN (:ids)";
		try {
			Query<License> query = session.createQuery(qry);
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
