/** */
package com.strandls.resource.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.resource.pojo.UFile;
import com.strandls.resource.util.AbstractDAO;

import jakarta.inject.Inject;

/**
 * @author Abhishek Rudra
 */
public class UFileDao extends AbstractDAO<UFile, Long> {

	private final Logger logger = LoggerFactory.getLogger(UFileDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected UFileDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public UFile findById(Long id) {
		UFile result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(UFile.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}
}
