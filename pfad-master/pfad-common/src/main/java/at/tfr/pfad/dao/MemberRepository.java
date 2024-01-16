package at.tfr.pfad.dao;

import java.util.Collection;
import java.util.List;

import jakarta.inject.Inject;

import org.apache.deltaspike.data.api.AbstractFullEntityRepository;
import org.apache.deltaspike.data.api.EntityGraph;
import org.apache.deltaspike.data.api.Modifying;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.QueryResult;
import org.apache.deltaspike.data.api.Repository;

import at.tfr.pfad.Pfad;
import at.tfr.pfad.model.Function;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Member_;
import at.tfr.pfad.model.Squad;
import at.tfr.pfad.model.Squad_;

@Repository
public abstract class MemberRepository extends AbstractFullEntityRepository<Member,Long> {

	@Inject
	@Pfad
	private MemberaRepository a;

	public MemberaRepository a() {
		return a;
	}
	
	@EntityGraph("fetchAll")
	public Member fetchBy(Long id) {
		return findBy(id);
	}

	public List<Member> fetchAll() {
		return fetchAll(0, Integer.MAX_VALUE);
	}
	
	public List<Member> fetchAll(int max) {
		return fetchAll(0, max);
	}
	
	public List<Member> fetchAll(int start, int max) {
		List<Long> ids = queryAllIdsIntern()
				.firstResult(start).maxResults(max).getResultList();
		return fetch(ids)
				.orderAsc(Member_.name.getName()).orderAsc(Member_.vorname.getName())
				.orderAsc(Member_.gebJahr.getName()).orderAsc(Member_.gebMonat.getName())
				.getResultList();
	}

	@EntityGraph("fetchAll")
	@Query("select e from Member e where e.id in (?1)")
	protected abstract QueryResult<Member> fetch(Collection<Long> memberIds);
	
	@EntityGraph("fetchAll")
	@Query("select e from Member e")
	protected abstract QueryResult<Member> fetchAllIntern();
	
	@EntityGraph("fetchAll")
	public List<Member> findActive() {
		return criteria().eq(Member_.aktiv, true).orderAsc(Member_.name).orderAsc(Member_.vorname)
				.orderAsc(Member_.gebJahr).orderAsc(Member_.gebMonat).getResultList();
	}


	public abstract List<Member> findByNameEqualIgnoreCaseAndVornameEqualIgnoreCaseAndStrasseEqualIgnoreCaseAndOrtEqualIgnoreCase(String name, String vorname, String strasse, String ort);
	
	@Query(named="Member.withFunction")
	public abstract List<Member> findByFunction(Function function);
	
	protected QueryResult<Member> queryAllIntern(int start, int max) {
		return queryAllIntern()
				.firstResult(start)
				.maxResults(max);
	}
	
	@Query("select e from Member e")
	protected abstract QueryResult<Member> queryAllIntern();
	
	@Query("select e.id from Member e")
	protected abstract QueryResult<Long> queryAllIdsIntern();
	
	public List<Member> findAccessible(Member member) {
		return criteria()
				.join(Member_.trupp,
						where(Squad.class).join(Squad_.leaderFemale, where(Member.class).eq(Member_.id, member.getId())))
				.getResultList();
	}
	

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
