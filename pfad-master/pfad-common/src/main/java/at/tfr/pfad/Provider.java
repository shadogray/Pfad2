/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnit;
import javax.persistence.SynchronizationType;

import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.jboss.logging.Logger;

@ApplicationScoped
public class Provider {
	
	private Logger log = Logger.getLogger(getClass());

	@PersistenceUnit(unitName = "pfad")
	private EntityManagerFactory entityManagerFactory;

	@PersistenceContext(unitName = "pfad")
	private EntityManager entityManager;

	@PersistenceContext(unitName = "pfad", type = PersistenceContextType.EXTENDED)
	private EntityManager extendedEntityManager;
	
	public EntityManager getExtendedEntityManager() {
		return extendedEntityManager;
	}
	
	@Produces
	@RequestScoped
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@Produces
	@RequestScoped
	public AuditReader getAuditReader() {
		return AuditReaderFactory.get(entityManagerFactory.createEntityManager(SynchronizationType.UNSYNCHRONIZED).unwrap(Session.class));
	}
}
