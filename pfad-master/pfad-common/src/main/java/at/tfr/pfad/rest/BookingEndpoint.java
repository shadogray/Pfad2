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

import at.tfr.pfad.model.Booking;
import at.tfr.pfad.svc.BookingDao;
import at.tfr.pfad.svc.BookingService;

/**
 * 
 */
@Stateless
@Path("/bookings")
public class BookingEndpoint extends EndpointBase<Booking> {

	@Inject
	private BookingService bookingSvc;
	
//	@POST
//	@Consumes("application/json")
//	public Response create(Booking entity) {
//		em.persist(entity);
//		return Response.created(
//				UriBuilder.fromResource(BookingEndpoint.class)
//						.path(String.valueOf(entity.getId())).build()).build();
//	}
//
//	@DELETE
//	@Path("/{id:[0-9][0-9]*}")
//	public Response deleteById(@PathParam("id") Long id) {
//		Booking entity = em.find(Booking.class, id);
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
		TypedQuery<Booking> findByIdQuery = em
				.createQuery(
						"SELECT DISTINCT b FROM Booking b LEFT JOIN FETCH b.payments LEFT JOIN FETCH b.member LEFT JOIN FETCH b.activity LEFT JOIN FETCH b.squad WHERE b.id = :entityId ORDER BY b.id",
						Booking.class);
		findByIdQuery.setParameter("entityId", id);
		Booking entity;
		try {
			entity = findByIdQuery.getSingleResult();
		} catch (NoResultException nre) {
			entity = null;
		}
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(bookingSvc.map(entity)).build();
	}

	@GET
	@Produces("application/json")
	public List<BookingDao> listAll(@QueryParam("start") Integer startPosition,
			@QueryParam("max") Integer maxResult) {
		TypedQuery<Booking> findAllQuery = em
				.createQuery(
						"SELECT DISTINCT b FROM Booking b LEFT JOIN FETCH b.payments LEFT JOIN FETCH b.member LEFT JOIN FETCH b.activity LEFT JOIN FETCH b.squad ORDER BY b.id",
						Booking.class);
		if (startPosition != null) {
			findAllQuery.setFirstResult(startPosition);
		}
		if (maxResult != null) {
			findAllQuery.setMaxResults(maxResult);
		}
		final List<Booking> results = findAllQuery.getResultList();
		return bookingSvc.map(results);
	}

//	@PUT
//	@Path("/{id:[0-9][0-9]*}")
//	@Consumes("application/json")
//	public Response update(@PathParam("id") Long id, BookingDao dao) {
//		if (dao == null) {
//			return Response.status(Status.BAD_REQUEST).build();
//		}
//		if (id == null) {
//			return Response.status(Status.BAD_REQUEST).build();
//		}
//		if (!id.equals(dao.getId())) {
//			return Response.status(Status.CONFLICT).entity(dao).build();
//		}
//		if (em.find(Booking.class, id) == null) {
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
