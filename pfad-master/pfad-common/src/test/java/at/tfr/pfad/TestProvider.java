/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.interceptor.Interceptor;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.SynchronizationType;

import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

@Alternative
@Specializes
@Priority(Interceptor.Priority.APPLICATION + 100)
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
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		return em;
	}
	
	@Produces
	public AuditReader getAuditReader() {
		return AuditReaderFactory.get(entityManagerFactory.createEntityManager(SynchronizationType.UNSYNCHRONIZED).unwrap(Session.class));
	}
}
