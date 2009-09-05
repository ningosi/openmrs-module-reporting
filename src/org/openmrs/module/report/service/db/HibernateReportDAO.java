/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.report.service.db;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.report.ReportDefinition;
import org.openmrs.module.report.ReportDesign;
import org.openmrs.module.report.renderer.ReportRenderer;
import org.springframework.transaction.annotation.Transactional;

/**
 * ReportService Database Access Interface
 */
@Transactional
public class HibernateReportDAO implements ReportDAO {
	
	//***** PROPERTIES *****
	private SessionFactory sessionFactory;
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @param uuid
	 * @return the ReportDesign with the given uuid
	 */
	@Transactional(readOnly = true)
	public ReportDesign getReportDesignByUuid(String uuid) throws DAOException {
		Query q = sessionFactory.getCurrentSession().createQuery("from ReportDesign r where r.uuid = :uuid");
		return (ReportDesign) q.setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * Get the {@link ReportDesign} with the given id
	 * @param id The Integer ReportDesign id
	 * @return the matching {@link ReportDesign} object
	 * @throws DAOException
	 */
	@Transactional(readOnly = true)
	public ReportDesign getReportDesign(Integer id) throws DAOException {
		return (ReportDesign) sessionFactory.getCurrentSession().get(ReportDesign.class, id);
	}
		
	/**
	 * Return a list of {@link ReportDesign}s for the passed {@link ReportDefinition} and {@link ReportRenderer} class,
	 * optionally including those that are retired
	 * @param includeRetired if true, indicates that retired {@link ReportDesign}s should also be included
	 * @return a List<ReportDesign> object containing all of the {@link ReportDesign}s
	 * @throws DAOException
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<ReportDesign> getReportDesigns(ReportDefinition reportDefinition, Class<? extends ReportRenderer> rendererType, 
											   boolean includeRetired) throws DAOException {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(ReportDesign.class);
		if (reportDefinition != null) {
			crit.add(Expression.eq("reportDefinition", reportDefinition));
		}
		if (rendererType != null) {
			crit.add(Expression.eq("rendererType", rendererType.getName()));
		}
		if (includeRetired == false) {
			crit.add(Expression.eq("retired", false));
		}
		return crit.list();
	}
	
	/**
	 * Save or update the given <code>ReportDesign</code> in the database. If this is a new
	 * ReportDesign, the returned ReportDesign will have a new
	 * {@link ReportDesign#getId()} inserted into it that was generated by the database
	 * 
	 * @param reportDesign The <code>ReportDesign</code> to save or update
	 * @throws DAOException
	 */
	public ReportDesign saveReportDesign(ReportDesign reportDesign) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(reportDesign);
		return reportDesign;
	}
	
	/**
	 * Purges a <code>ReportDesign</code> from the database.
	 * @param reportDesign The <code>ReportDesign</code> to remove from the system
	 * @throws DAOException
	 */
	public void purgeReportDesign(ReportDesign reportDesign) {
		sessionFactory.getCurrentSession().delete(reportDesign);
	}
	
	//***** PROPERTY ACCESS *****
	
	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}

