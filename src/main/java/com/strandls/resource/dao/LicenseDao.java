/** */
package com.strandls.resource.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
}
