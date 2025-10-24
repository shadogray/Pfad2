/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad;

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
import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Specializes
@Alternative
@Priority(Interceptor.Priority.APPLICATION)
@ApplicationScoped
public class TestProvider extends at.tfr.pfad.Provider {
	
	//@PersistenceUnit(unitName = "pfadTest")
	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("pfadTest");

	private EntityManagerFactory entityManagerFactoryLarge = Persistence.createEntityManagerFactory("pfadTestLarge");
	private EntityManager entityManagerLarge;

	private boolean large;
	final String BACKUP_SQL = "sql/testPfad2_Backup_20240709.sql";

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

	public EntityManager getExtendedEntityManagerLarge() {
		URL dbFileUrl = getClass().getClassLoader().getResource(BACKUP_SQL);
		if (entityManagerLarge == null) {
			try {
				if (dbFileUrl != null) {
					Path path = Paths.get(dbFileUrl.getFile()).getParent().getParent().resolveSibling("testPfadLarge.mv.db");
					Files.deleteIfExists(path);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			entityManagerLarge = entityManagerFactoryLarge.createEntityManager();
			Session session = entityManagerLarge.unwrap(Session.class);
			try {
				session.getTransaction().begin();
				session.createNativeMutationQuery("RUNSCRIPT FROM '" + dbFileUrl.getFile()+"';")
						.executeUpdate();
				session.getTransaction().commit();
			} catch (Exception e) {
				e.printStackTrace();
				try {
					session.createNativeMutationQuery("SHUTDOWN;").executeUpdate();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				throw new RuntimeException(e);
			}
		}
		return entityManagerLarge;
	}

	@Override
	@Produces
	@RequestScoped
	public EntityManager getEntityManager() {
		EntityManager em;
		if (!large) {
			em = entityManagerFactory.createEntityManager();
		} else {
			em = getExtendedEntityManagerLarge();
		}
		if (!em.getTransaction().isActive()) {
			em.getTransaction().begin();
		}
		return em;
	}
	
	@Override
	@Produces
	public AuditReader getAuditReader() {
		return AuditReaderFactory.get(entityManagerFactory.createEntityManager(SynchronizationType.UNSYNCHRONIZED).unwrap(Session.class));
	}

	public boolean isLarge() {
		return large;
	}

	public void setLarge(boolean large) {
		this.large = large;
	}
}
