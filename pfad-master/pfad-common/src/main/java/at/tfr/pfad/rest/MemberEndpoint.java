package at.tfr.pfad.rest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.inject.Inject;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.joda.time.DateTime;

import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Member_;
import at.tfr.pfad.model.Membera;
import at.tfr.pfad.dao.MemberA;
import at.tfr.pfad.svc.MemberDao;
import at.tfr.pfad.svc.MemberMapper;
import at.tfr.pfad.svc.MemberService;

/**
 * 
 */
@Stateless
@Path("/members")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MemberEndpoint extends EndpointBase<Member> {

	List<Integer> gebTag = IntStream.range(1,31).boxed().collect(Collectors.toList());
	static List<Integer> gebJahr = IntStream.range(1900, new DateTime().getYear()).boxed().collect(Collectors.toList());
	static {
		Collections.reverse(gebJahr);
	}
	 // ATTENTION: Pfad GebMonat starts with "1"!!
	static List<Monat> gebMonat = new GregorianCalendar().getDisplayNames(Calendar.MONTH, Calendar.LONG_FORMAT, Locale.GERMAN).
			entrySet().stream().map(e -> new Monat(e.getKey(),e.getValue()+1)).sorted().collect(Collectors.toList());

	
	@Inject
	private MemberService memberSvc;
	@Inject
	private MemberMapper mm;

	@POST
	public Response create(MemberDao dao) {
		MemberDao saved = memberSvc.save(dao);
		return Response.created(
				UriBuilder.fromResource(MemberEndpoint.class)
						.path(String.valueOf(saved.getId())).build()).build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") Long id) {
		Member entity = em.find(Member.class, id);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		em.remove(entity);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	public Response findById(@PathParam("id") Long id) {
		MemberDao dao = memberSvc.findBy(id);
		if (dao == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(dao).build();
	}

	@GET
	public List<MemberDao> listAll(@QueryParam("start") Integer startPosition,
			@QueryParam("max") Integer maxResult) {
		return memberSvc.map(memberRepo.fetchAll(startPosition != null ? startPosition : 0, 
				maxResult != null ? maxResult : Integer.MAX_VALUE));
	}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	public Response update(@PathParam("id") Long id, MemberDao dao) {
		if (dao == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (id == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		if (!id.equals(dao.getId())) {
			return Response.status(Status.CONFLICT).entity(dao).build();
		}
		if (em.find(Member.class, id) == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		try {
			dao = memberSvc.update(dao);
		} catch (OptimisticLockException e) {
			return Response.status(Response.Status.CONFLICT)
					.entity(e.getEntity()).build();
		}

		return Response.ok(dao).build();
	}
	
	@GET
	@Path("/filtered")
	public List<MemberDao> filtered(@QueryParam("filter") String filter, @QueryParam("truppId") Long truppId) {
		return memberSvc.filtered(filter, truppId);
	}
	
	@SuppressWarnings("unchecked")
	@POST
	@Path("/query")
	public List<MemberDao> queryByExample(Member example) {
		return memberSvc.map(memberRepo.findBy(example, 0, 10, MemberA.name, MemberA.vorname, 
				MemberA.geschlecht, MemberA.email, 
				MemberA.aktiv, MemberA.aktivExtern, 
				MemberA.bvKey, MemberA.truppId, 
				MemberA.gebJahr, MemberA.gebMonat, MemberA.gebTag, 
				MemberA.strasse, MemberA.ort, MemberA.plz, 
				MemberA.rolle, 
				MemberA.free, MemberA.support, MemberA.trail, MemberA.gilde));
	}

	@GET
	@Path("/{id:[0-9]+}/siblings")
	public List<MemberDao> findSiblingsById(@PathParam("id") Long id) {
		Member member = memberRepo.fetchBy(id);
		return memberSvc.map(member.getSiblings());
	}

	@GET
	@Path("/{id:[0-9]+}/parents")
	public List<MemberDao> findParentsById(@PathParam("id") Long id) {
		Member member = memberRepo.fetchBy(id);
		return memberSvc.map(member.getParents());
	}
	
	@GET
	@Path("/distinct")
	public List<String> distinct(@QueryParam("property") String property, @QueryParam("filter") String filter) {
		String name = ("findDistinct"+property).toLowerCase();
		Optional<Method> mOpt = Stream.of(memberRepo.getClass().getDeclaredMethods())
				.filter(m -> m.getName().toLowerCase().equals(name)).findFirst();
		if (mOpt.isPresent()) {
			try {
				return ((List<String>)mOpt.get().invoke(memberRepo))
						.stream().filter(s -> s != null && s.toLowerCase().contains(filter.toLowerCase()))
						.sorted().collect(Collectors.toList());
			} catch (Exception e) {
				log.info("failure to invoke for property="+property+", filter="+filter, e);
			}
		}
		return new ArrayList<>();
	}
	
	@GET
	@Path("/gebJahr")
	public List<Integer> gebJahr() {
		return gebJahr;
	}

	@GET
	@Path("/gebMonat")
	public List<Monat> gebMonat() {
		return gebMonat;
	}

	@GET
	@Path("/gebTag")
	public List<Integer> gebTag() {
		return gebTag;
	}
	
	public static class Monat implements Comparable<Monat>{
		private String name;
		private Integer index;
		
		public Monat() {
		}
		
		public Monat(String name, Integer index) {
			this.name = name;
			this.index = index;
		}

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getIndex() {
			return index;
		}
		public void setIndex(Integer index) {
			this.index = index;
		}
		
		@Override
		public int compareTo(Monat o) {
			return index.compareTo(o.index);
		}

		@Override
		public String toString() {
			return "Monat [name=" + name + ", index=" + index + "]";
		}
	}
}
