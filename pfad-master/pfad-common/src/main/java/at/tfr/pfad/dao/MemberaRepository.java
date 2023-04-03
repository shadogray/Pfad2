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
import org.apache.deltaspike.data.api.FullEntityRepository;
import org.apache.deltaspike.data.api.Modifying;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.QueryResult;
import org.apache.deltaspike.data.api.Repository;

import at.tfr.pfad.Pfad;
import at.tfr.pfad.model.Function;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Member_;
import at.tfr.pfad.model.Membera;
import at.tfr.pfad.model.Membera_;
import at.tfr.pfad.model.Squad;
import at.tfr.pfad.model.Squad_;

@Pfad
@Repository
public abstract class MemberaRepository implements FullEntityRepository<Membera, Long> {

	@EntityGraph("fetchAll")
	public Membera fetchBy(Long id) {
		return findBy(id);
	}

	public List<Membera> fetchAll() {
		return fetchAll(0, Integer.MAX_VALUE);
	}
	
	public List<Membera> fetchAll(int max) {
		return fetchAll(0, max);
	}
	
	public List<Membera> fetchAll(int start, int max) {
		List<Long> ids = queryAllIdsIntern()
				.firstResult(start).maxResults(max).getResultList();
		return fetch(ids)
				.orderAsc(Member_.name.getName()).orderAsc(Member_.vorname.getName())
				.orderAsc(Member_.gebJahr.getName()).orderAsc(Member_.gebMonat.getName())
				.getResultList();
	}

	@EntityGraph("fetchAll")
	@Query("select e from Member e where e.id in (?1)")
	protected abstract QueryResult<Membera> fetch(Collection<Long> memberIds);
	
	@EntityGraph("fetchAll")
	@Query("select e from Member e")
	protected abstract QueryResult<Membera> fetchAllIntern();
	
	@EntityGraph("fetchAll")
	public List<Membera> findActive() {
		return criteria().eq(Membera_.aktiv, true).orderAsc(Membera_.name).orderAsc(Membera_.vorname)
				.orderAsc(Membera_.gebJahr).orderAsc(Membera_.gebMonat).getResultList();
	}


	public abstract List<Membera> findByNameEqualIgnoreCaseAndVornameEqualIgnoreCaseAndStrasseEqualIgnoreCaseAndOrtEqualIgnoreCase(String name, String vorname, String strasse, String ort);
	
	@Query(named="Member.withFunction")
	public abstract List<Membera> findByFunction(Function function);
	
	protected QueryResult<Membera> queryAllIntern(int start, int max) {
		return queryAllIntern()
				.firstResult(start)
				.maxResults(max);
	}
	
	@Query("select e from Member e")
	protected abstract QueryResult<Membera> queryAllIntern();
	
	@Query("select e.id from Member e")
	protected abstract QueryResult<Long> queryAllIdsIntern();
	
	@Query(named="Member.distName")
	public abstract List<String> findDistinctName();

	@Query(named="Member.distNameLike")
	public abstract List<String> findDistinctNameLike(String name);

	@Query(named="Member.distVorname")
	public abstract List<String> findDistinctVorname();

	@Query(named="Member.distVornameLike")
	public abstract List<String> findDistinctVornameLike(String vorname);

	@Query(named="Member.distPLZ")
	public abstract List<String> findDistinctPLZ();

	@Query(named="Member.distPLZLike")
	public abstract List<String> findDistinctPLZLike(String plz);

	@Query(named="Member.distOrt")
	public abstract List<String> findDistinctOrt();

	@Query(named="Member.distOrtLike")
	public abstract List<String> findDistinctOrtLike(String ort);

	@Query(named="Member.distStrasse")
	public abstract List<String> findDistinctStrasse();

	@Query(named="Member.distStrasseLike")
	public abstract List<String> findDistinctStrasseLike(String strasse);

	@Query(named="Member.distTitel")
	public abstract List<String> findDistinctTitel();

	@Query(named="Member.distTitelLike")
	public abstract List<String> findDistinctTitelLike(String titel);

	@Query(named="Member.distAnrede")
	public abstract List<String> findDistinctAnrede();

	@Query(named="Member.distAnredeLike")
	public abstract List<String> findDistinctAnredeLike(String anrede);

	@Query(named="Member.distReligion")
	public abstract List<String> findDistinctReligion();

	@Query(named="Member.distReligionLike")
	public abstract List<String> findDistinctReligionLike(String religion);

	@Modifying
	@Query(isNative = true, value = "update MEMBER m set m.status = 'D' where m.id = ?")
	public abstract void deActivate(Long id);

	@Modifying
	@Query(isNative = true, value = "update MEMBER m set m.status = 'A' where m.id = ?")
	public abstract void activate(Long id);
}
