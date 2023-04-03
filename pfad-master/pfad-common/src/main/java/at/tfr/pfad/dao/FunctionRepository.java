package at.tfr.pfad.dao;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.api.criteria.CriteriaSupport;

import at.tfr.pfad.model.Function;

@Repository
public abstract class FunctionRepository implements EntityRepository<Function, Long>, CriteriaSupport<Function> {

	@Inject
	private EntityManager em;
	
	public abstract Function findOptionalById(Long id);

	@SuppressWarnings("unchecked")
	public List<Function> findByIds(Collection<Long> ids) {
		return em.createQuery("select f from Function f where f.id in :ids")
				.setParameter("ids", ids).getResultList();
	}
}
