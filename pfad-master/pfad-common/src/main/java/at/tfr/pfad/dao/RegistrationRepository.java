/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.dao;

import java.util.Collection;
import java.util.List;

import org.apache.deltaspike.data.api.EntityGraph;
import org.apache.deltaspike.data.api.EntityManagerDelegate;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.QueryResult;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.criteria.Criteria;
import org.apache.deltaspike.data.api.criteria.CriteriaSupport;

import at.tfr.pfad.RegistrationStatus;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Registration;
import at.tfr.pfad.model.Registration_;

@Repository
public abstract class RegistrationRepository implements EntityRepository<Registration, Long>, CriteriaSupport<Registration>, EntityManagerDelegate<Registration> {

	@EntityGraph("fetchAll")
	public Registration fetchBy(Long id) {
		return findBy(id);
	}

	public List<Registration> fetchAll() {
		return fetchAll(0, Integer.MAX_VALUE);
	}
	
	public List<Registration> fetchAll(int max) {
		return fetchAll(0, max);
	}
	
	public List<Registration> fetchAll(int start, int max) {
		List<Long> ids = queryAllIdsIntern()
				.firstResult(start).maxResults(max).getResultList();
		return fetch(ids)
				.orderAsc(Registration_.name).orderAsc(Registration_.vorname)
				.orderAsc(Registration_.gebJahr).orderAsc(Registration_.gebMonat)
				.getResultList();
	}

	protected QueryResult<Registration> queryAllIntern(int start, int max) {
		return queryAllIntern()
				.firstResult(start)
				.maxResults(max);
	}
	
	public List<Registration> getDuplicates(Registration example) {
		return findByNameVornameAndGeburtJahrMonatTag(example.getName(), example.getVorname(), 
				example.getGebJahr(), example.getGebMonat(), example.getGebTag());
	}
	
	@Query(named = "Registration.duplicateCheck")
	public abstract List<Registration> findByNameVornameAndGeburtJahrMonatTag(String name, String vorname, int gebJahr, int gebMonat, int gebTag);
	
	public List<Registration> queryBy(Registration reg) {
		return queryBy(reg);
	}
	
	public List<Registration> queryBy(Registration reg, RegistrationStatus[] stati) {
		return queryBy(reg, stati, null, null);
	}
	
	public List<Registration> queryBy(Registration reg, RegistrationStatus[] stati, Boolean aktiv, Boolean storno) {
		Criteria<Registration, Registration> crit = criteria();
		if (aktiv != null) {
			crit = crit.eq(Registration_.aktiv, aktiv);
		}
		if (storno != null) {
			crit = crit.eq(Registration_.storno, storno);
		}
		if (reg.getGeschlecht() != null) {
			crit = crit.eq(Registration_.geschlecht, reg.getGeschlecht());
		}
		if (reg.getName() != null) {
			crit = crit.likeIgnoreCase(Registration_.name, "%"+reg.getName()+"%");
		}
		if (reg.getVorname() != null) {
			crit = crit.likeIgnoreCase(Registration_.vorname, "%"+reg.getVorname()+"%");
		}
		if (reg.getStrasse() != null) {
			crit = crit.likeIgnoreCase(Registration_.strasse, "%"+reg.getStrasse()+"%");
		}
		if (reg.getOrt() != null) {
			crit = crit.likeIgnoreCase(Registration_.ort, "%"+reg.getOrt()+"%");
		}
		if (reg.getPLZ() != null) {
			crit = crit.likeIgnoreCase(Registration_.plz, "%"+reg.getPLZ()+"%");
		}
		if (reg.getParentName() != null) {
			crit = crit.likeIgnoreCase(Registration_.parentName, "%"+reg.getParentName()+"%");
		}
		if (reg.getParentVorname() != null) {
			crit = crit.likeIgnoreCase(Registration_.parentVorname, "%"+reg.getParentVorname()+"%");
		}
		if (reg.getTelefon() != null) {
			crit = crit.likeIgnoreCase(Registration_.telefon, "%"+reg.getTelefon()+"%");
		}
		if (reg.getEmail() != null) {
			crit = crit.likeIgnoreCase(Registration_.email, "%"+reg.getEmail() +"%");
		}
		if (reg.getGebJahr() > 0) {
			crit = crit.ltOrEq(Registration_.gebJahr, reg.getGebJahr());
		}
		if (reg.getSchoolEntry() > 0) {
			crit = crit.ltOrEq(Registration_.schoolEntry, reg.getSchoolEntry());
		}
		if (stati != null && stati.length > 0) {
			crit = crit.in(Registration_.status, stati);
		}
		return crit.getResultList();
	}
	
	@Query("select e from Registration e")
	protected abstract QueryResult<Registration> queryAllIntern();
	
	@Query("select e.id from Registration e")
	protected abstract QueryResult<Long> queryAllIdsIntern();
	
	@Query("select e from Registration e where e.id in (?1)")
	protected abstract QueryResult<Registration> fetch(Collection<Long> registrationIds);
	
	@Query("select e from Registration e")
	protected abstract QueryResult<Registration> fetchAllIntern();
	
	@Query(named="Registration.distName")
	public abstract List<String> findDistinctName();

	@Query(named="Registration.distVorname")
	public abstract List<String> findDistinctVorname();

	@Query(named="Registration.distPLZ")
	public abstract List<String> findDistinctPLZ();

	@Query(named="Registration.distOrt")
	public abstract List<String> findDistinctOrt();

	@Query(named="Registration.distStrasse")
	public abstract List<String> findDistinctStrasse();

	@Query(named="Registration.distGebJahr")
	public abstract List<Integer> findDistinctGebJahr();

	@Query(named="Registration.distSchoolEntry")
	public abstract List<Integer> findDistinctSchoolEntry();

	@Query(named="Registration.memberOrParent")
	public abstract List<Registration> findByMember(Member member);
}
