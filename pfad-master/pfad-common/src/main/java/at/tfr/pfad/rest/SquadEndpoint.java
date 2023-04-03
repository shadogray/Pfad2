package at.tfr.pfad.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import at.tfr.pfad.model.Squad;
import at.tfr.pfad.svc.MemberDao;
import at.tfr.pfad.svc.MemberService;
import at.tfr.pfad.svc.SquadDao;
import at.tfr.pfad.svc.SquadMapper;
import at.tfr.pfad.svc.SquadService;

/**
 * 
 */
@Stateless
@Path("/squads")
@Produces("application/json")
@Consumes("application/json")
public class SquadEndpoint extends EndpointBase<Squad> {

	@Inject
	private SquadService squadSvc;
	@Inject
	private MemberService memberSvc;
	@Inject
	private SquadMapper sm;

	@POST
	public Response create(SquadDao dao) {
		Squad entity = new Squad();
		sm.updateSquad(dao, entity);
		em.persist(entity);
		return Response.created(
				UriBuilder.fromResource(SquadEndpoint.class)
						.path(String.valueOf(entity.getId())).build()).build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") Long id) {
		Squad entity = em.find(Squad.class, id);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		em.remove(entity);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	public Response findById(@PathParam("id") Long id) {
		SquadDao dao;
		try {
			dao = squadSvc.findBy(id);
		} catch (NoResultException nre) {
			dao = null;
		}
		if (dao == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(dao).build();
	}

	@GET
	public List<SquadDao> listAll(@QueryParam("start") Integer startPosition,
			@QueryParam("max") Integer maxResult) {
		return squadSvc.findAll();
	}

	@GET
	@Path("/{id:[0-9]+}/assistants")
	public List<MemberDao> listAssistants(@PathParam("id") Long id) {
		return memberSvc.map(squadRepo.findBy(id).getAssistants());
	}

	@GET
	@Path("/{id:[0-9]+}/scouts")
	public List<MemberDao> listScouts(@PathParam("id") Long id) {
		return memberSvc.map(squadRepo.findBy(id).getScouts());
	}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	public Response update(@PathParam("id") Long id, SquadDao dao) {
		if (dao == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (id == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (!id.equals(dao.getId())) {
			return Response.status(Status.CONFLICT).entity(dao).build();
		}
		if (em.find(Squad.class, id) == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		try {
			dao = squadSvc.update(dao);
		} catch (OptimisticLockException e) {
			return Response.status(Response.Status.CONFLICT)
					.entity(dao).build();
		}

		return Response.noContent().build();
	}
}
