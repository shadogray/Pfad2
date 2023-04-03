package at.tfr.pfad.rest;

import java.util.List;

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

import at.tfr.pfad.model.Payment;
import at.tfr.pfad.svc.PaymentDao;
import at.tfr.pfad.svc.PaymentService;

/**
 * 
 */
@Stateless
@Path("/payments")
public class PaymentEndpoint extends EndpointBase<Payment> {

	@Inject
	private PaymentService paymentSvc;
	
//	@POST
//	@Consumes("application/json")
//	public Response create(Payment entity) {
//		em.persist(entity);
//		return Response.created(
//				UriBuilder.fromResource(PaymentEndpoint.class)
//						.path(String.valueOf(entity.getId())).build()).build();
//	}
//
//	@DELETE
//	@Path("/{id:[0-9][0-9]*}")
//	public Response deleteById(@PathParam("id") Long id) {
//		Payment entity = em.find(Payment.class, id);
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
		TypedQuery<Payment> findByIdQuery = em
				.createQuery(
						"SELECT DISTINCT p FROM Payment p LEFT JOIN FETCH p.payer LEFT JOIN FETCH p.bookings WHERE p.id = :entityId ORDER BY p.id",
						Payment.class);
		findByIdQuery.setParameter("entityId", id);
		Payment entity;
		try {
			entity = findByIdQuery.getSingleResult();
		} catch (NoResultException nre) {
			entity = null;
		}
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(paymentSvc.map(entity)).build();
	}

	@GET
	@Produces("application/json")
	public List<PaymentDao> listAll(@QueryParam("start") Integer startPosition,
			@QueryParam("max") Integer maxResult) {
		TypedQuery<Payment> findAllQuery = em
				.createQuery(
						"SELECT DISTINCT p FROM Payment p LEFT JOIN FETCH p.payer LEFT JOIN FETCH p.bookings ORDER BY p.id",
						Payment.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		final List<Payment> results = findAllQuery.getResultList();
		return paymentSvc.map(results);
	}

//	@PUT
//	@Path("/{id:[0-9][0-9]*}")
//	@Consumes("application/json")
//	public Response update(@PathParam("id") Long id, Payment entity) {
//		if (entity == null) {
//			return Response.status(Status.BAD_REQUEST).build();
//		}
//		if (id == null) {
//			return Response.status(Status.BAD_REQUEST).build();
//		}
//		if (!id.equals(entity.getId())) {
//			return Response.status(Status.CONFLICT).entity(entity).build();
//		}
//		if (em.find(Payment.class, id) == null) {
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
