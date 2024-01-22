/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.view;

import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.Specializes;
import jakarta.interceptor.Interceptor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.SynchronizationType;

@Specializes
@Alternative
@Priority(Interceptor.Priority.APPLICATION)
@ApplicationScoped
public class TestProvider extends at.tfr.pfad.Provider {
	
	//@PersistenceUnit(unitName = "pfadTest")
	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("pfadTest");

	//@PersistenceContext(unitName = "pfadTest")
	//private EntityManager entityManager;

	//@PersistenceContext(unitName = "pfadTest", type = PersistenceContextType.EXTENDED)
	//private EntityManager extendedEntityManager;

	@Produces
	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}
	
	public EntityManager getExtendedEntityManager() {
		return entityManagerFactory.createEntityManager();
	}
	
	@Produces
	@RequestScoped
	public EntityManager getEntityManager() {
		return entityManagerFactory.createEntityManager();
	}
	
	@Produces
	public AuditReader getAuditReader() {
		return AuditReaderFactory.get(entityManagerFactory.createEntityManager(SynchronizationType.UNSYNCHRONIZED).unwrap(Session.class));
	}
}
