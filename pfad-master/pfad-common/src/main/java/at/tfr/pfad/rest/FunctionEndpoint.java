package at.tfr.pfad.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import at.tfr.pfad.model.Function;
import at.tfr.pfad.svc.FunctionDao;
import at.tfr.pfad.svc.FunctionMapper;

/**
 * 
 */
@Stateless
@Path("/functions")
public class FunctionEndpoint extends EndpointBase<Function> {

	@Inject
	private FunctionMapper fm;
	
//	@POST
//	@Consumes("application/json")
//	public Response create(FunctionDao dao) {
//		em.persist(entity);
//		return Response.created(
//				UriBuilder.fromResource(FunctionEndpoint.class)
//						.path(String.valueOf(entity.getId())).build()).build();
//	}
//
//	@DELETE
//	@Path("/{id:[0-9][0-9]*}")
//	public Response deleteById(@PathParam("id") Long id) {
//		Function entity = em.find(Function.class, id);
//		if (entity == null) {
//			return Response.status(Status.NOT_FOUND).build();
//		}
//		em.remove(entity);
//		return Response.noContent().build();
//	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public Response findById(@PathParam("id") Long id) {
		TypedQuery<Function> findByIdQuery = em
				.createQuery(
						"SELECT DISTINCT f FROM Function f WHERE f.id = :entityId ORDER BY f.id",
						Function.class);
		findByIdQuery.setParameter("entityId", id);
		Function entity;
		try {
			entity = findByIdQuery.getSingleResult();
		} catch (NoResultException nre) {
			entity = null;
		}
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(fm.functionToDao(entity)).build();
	}

	@GET
	@Produces("application/json")
	public List<FunctionDao> listAll(@QueryParam("start") Integer startPosition,
			@QueryParam("max") Integer maxResult) {
		TypedQuery<Function> findAllQuery = em.createQuery(
				"SELECT DISTINCT f FROM Function f ORDER BY f.function, f.key, f.id",
				Function.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		final List<Function> results = findAllQuery.getResultList();
		return results.stream().map(f -> fm.functionToDao(f)).collect(Collectors.toList());
	}

//	@PUT
//	@Path("/{id:[0-9][0-9]*}")
//	@Consumes("application/json")
//	public Response update(@PathParam("id") Long id, Function entity) {
//		if (entity == null) {
//			return Response.status(Status.BAD_REQUEST).build();
//		}
//		if (id == null) {
//			return Response.status(Status.BAD_REQUEST).build();
//		}
//		if (!id.equals(entity.getId())) {
//			return Response.status(Status.CONFLICT).entity(entity).build();
//		}
//		if (em.find(Function.class, id) == null) {
//			return Response.status(Status.NOT_FOUND).build();
//		}
//		try {
//			entity = em.merge(entity);
//		} catch (OptimisticLockException e) {
//			return Response.status(Response.Status.CONFLICT)
//					.entity(e.getEntity()).build();
//		}
//
//		return Response.noContent().build();
//	}
}
