/** */
package com.strandls.resource.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.resource.pojo.SpeciesFieldResources;
import com.strandls.resource.util.AbstractDAO;

import jakarta.inject.Inject;

/**
 * @author Abhishek Rudra
 */
public class SpeciesFieldResourcesDao extends AbstractDAO<SpeciesFieldResources, Long> {

	private final Logger logger = LoggerFactory.getLogger(SpeciesFieldResourcesDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected SpeciesFieldResourcesDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public SpeciesFieldResources findById(Long id) {
		SpeciesFieldResources result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(SpeciesFieldResources.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Long> findBySpeciesFieldId(Long speciesFieldId) {
		String qry = "from  SpeciesFieldResources where speciesFieldId = :speciesFieldId";
		Session session = sessionFactory.openSession();
		List<Long> result = new ArrayList<Long>();
		List<SpeciesFieldResources> sfResourceList = null;
		try {
			Query<SpeciesFieldResources> query = session.createQuery(qry);
			query.setParameter("speciesFieldId", speciesFieldId);
			sfResourceList = query.getResultList();
			if (sfResourceList != null && !sfResourceList.isEmpty()) {
				for (SpeciesFieldResources sfResource : sfResourceList) {
					result.add(sfResource.getResourceId());
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
	public SpeciesFieldResources findByPair(Long speciesFieldId, Long resourceId) {
		String qry = "from SpeciesFieldResources where  speciesFieldId = :speciesFieldId and resourceId = :resourcesId";
		Session session = sessionFactory.openSession();
		SpeciesFieldResources result = null;
		try {
			Query<SpeciesFieldResources> query = session.createQuery(qry);
			query.setParameter("speciesFieldId", speciesFieldId);
			query.setParameter("resourcesId", resourceId);
			result = query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}
}
