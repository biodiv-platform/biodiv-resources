/** */
package com.strandls.resource.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.resource.pojo.SpeciesResource;
import com.strandls.resource.util.AbstractDAO;

import jakarta.inject.Inject;

/**
 * @author Abhishek Rudra
 */
public class SpeciesResourceDao extends AbstractDAO<SpeciesResource, Long> {

	private final Logger logger = LoggerFactory.getLogger(SpeciesResourceDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected SpeciesResourceDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public SpeciesResource findById(Long id) {
		SpeciesResource result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(SpeciesResource.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Long> findBySpeciesId(Long speciesId) {
		List<SpeciesResource> speciesResourceList = null;
		String qry = "from SpeciesResource where  speciesId = :speciesId";
		Session session = sessionFactory.openSession();
		List<Long> result = new ArrayList<Long>();
		try {
			Query<SpeciesResource> query = session.createQuery(qry);
			query.setParameter("speciesId", speciesId);
			speciesResourceList = query.getResultList();
			if (speciesResourceList != null && !speciesResourceList.isEmpty()) {
				for (SpeciesResource speciesResource : speciesResourceList)
					result.add(speciesResource.getResourceId());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	public int mergeResourcesToTarget(List<Long> sourceSpeciesIds, Long targetSpeciesId) {
		if (sourceSpeciesIds == null || sourceSpeciesIds.isEmpty()) {
			logger.warn("No source species IDs provided for merge");
			return 0;
		}

		Session session = sessionFactory.openSession();
		Transaction tx = null;
		int updatedCount = 0;

		try {
			tx = session.beginTransaction();
			logger.info("Merging resources from {} source IDs to target {}", sourceSpeciesIds.size(), targetSpeciesId);

			String qry = "UPDATE SpeciesResource SET speciesId = :targetSpeciesId WHERE speciesId IN (:sourceSpeciesIds)";
			updatedCount = session.createQuery(qry).setParameter("targetSpeciesId", targetSpeciesId)
					.setParameter("sourceSpeciesIds", sourceSpeciesIds).executeUpdate();

			tx.commit();
			logger.info("Successfully merged {} resources to target {}", updatedCount, targetSpeciesId);

		} catch (Exception e) {
			if (tx != null) {
				try {
					tx.rollback();
				} catch (Exception rollbackEx) {
					logger.error("Error during rollback", rollbackEx);
				}
			}
			logger.error("Failed to merge resources to target {}: {}", targetSpeciesId, e.getMessage(), e);
			throw new RuntimeException("mergeResourcesToTarget failed for target " + targetSpeciesId, e);
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}

		return updatedCount;
	}

	@SuppressWarnings("unchecked")
	public SpeciesResource findByPair(Long speciesId, Long resourceId) {
		String qry = "from SpeciesResource where speciesId = :speciesId and resourceId = :resourceId";
		Session session = sessionFactory.openSession();
		SpeciesResource result = null;
		try {
			Query<SpeciesResource> query = session.createQuery(qry);
			query.setParameter("speciesId", speciesId);
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
