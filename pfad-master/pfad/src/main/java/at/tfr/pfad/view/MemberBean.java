/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.inject.Instance;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.event.PhaseId;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.omnifaces.util.Faces;

import at.tfr.pfad.ScoutRole;
import at.tfr.pfad.Sex;
import at.tfr.pfad.model.Configuration;
import at.tfr.pfad.model.DelMember;
import at.tfr.pfad.model.Function;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Member_;
import at.tfr.pfad.model.Registration;
import at.tfr.pfad.model.Squad;
import at.tfr.pfad.processing.MemberValidator;
import at.tfr.pfad.svc.MemberDao;
import at.tfr.pfad.util.CollectionUtil;
import at.tfr.pfad.util.ValidationResult;
import at.tfr.pfad.view.ViewUtils.Month;

/**
 * Backing bean for Member entities.
 * This class provides CRUD functionality for all Member entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ViewScoped
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class MemberBean extends BaseBean<Member> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Instance<MemberValidator> memberValidator;
	private Boolean exampleActive;
	private Boolean exampleFree;
	private Boolean exampleGilde;
	private Boolean exampleSupport;
	private Boolean exampleInfoMail;
	private List<Function> exampleFunctions;
	private List<Squad> exampleTrupps;
	private List<Squad> leaderOf;
	private List<Squad> assistantOf;
	private Registration registration;
	private Collection<ValidationResult> validationResults = new ArrayList<>();

	/*
	 * support creating and retrieving Member entities
	 */

	public List<Squad> getLeaderOf() {
		return leaderOf;
	}
	public List<Squad> getAssistantOf() {
		return assistantOf;
	}
	public Registration getRegistration() {
		return registration;
	}
	
	public String create() {
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (this.id == null) {
			this.member = getMemberExample();
		} else {
			if (this.member == null || !this.member.getId().equals(id)) {
				this.member = findById(getId());
				if (this.member.getTrupp() != null) this.member.getTrupp().getId();
				this.member.getSiblings().size();
				this.member.getParents().size();
				this.member.getFunktionen().size();
				if (this.member.getVollzahler() != null) {
					this.member.getVollzahler().getId();
					filteredMembers.add(member.getVollzahler());
				}
			}
			this.leaderOf = squadRepo.findByLeaderFemaleEqualOrLeaderMaleEqual(member);
			this.assistantOf = squadRepo.findByAssistant(member);
			this.validationResults = validateMember(member, false);
			this.registration = registrationRepo.findByMember(member).stream().findFirst().orElse(null);
		}
	}

	public Member findById(Long id) {
		return memberRepo.fetchBy(id);
	}

	/*
	 * support updating and deleting Member entities
	 */
	
	public Set<Function> getMemberFunctions() {
		return new TreeSet<Function>(member.getFunktionen());
	}
	
	public void setMemberFunctions(Set<Function> functions) {
		CollectionUtil.synchronize(member.getFunktionen(), functions);
		updateMemberOnChange();
	}
	
	public void updateMemberOnChange() {
		if (PhaseId.INVOKE_APPLICATION.equals(Faces.getCurrentPhaseId())) {
			member.setAktiv(
					member.getTrupp() != null 
					|| member.getFunktionen().stream().anyMatch(f -> f.isExportReg())
					|| !squadRepo.findByResponsible(member).isEmpty());
		}
	}

	public String update() {

		try {
			if (!isUpdateAllowed()) {
				throw new SecurityException("Update denied for: "+sessionBean.getUser());
			}
			
			log.info("updated " + member + " by " + sessionContext.getCallerPrincipal());
	
			validateMember(member, true);
			
			if (id == null) {
				entityManager.persist(member);
				id = member.getId();
			} else {
				member = entityManager.merge(member);
			}
			
			// add BV-Key after persist!!
			if (StringUtils.isEmpty(member.getBVKey())) {
				member.setBVKey(Configuration.BADEN_KEYPFX + member.getId());
			}

			if (member.getSiblings() != null && !member.getSiblings().isEmpty()) {
				List<Member> siblings = new ArrayList<>(member.getSiblings());
				for (Member sibling : siblings) {
					sibling = memberRepo.findBy(sibling.getId());
					if (sibling.equals(member) || sibling.getSiblings().contains(member)) {
						throw new IllegalArgumentException("Cannot add Parent as Child: parent="+member+", childToAdd: "+memberToAdd);
					}
					member.getSiblings().add(sibling);
				}
			}
			
			memberRepo.flush();
			
			if (member.getId() != null) {
				return "view?faces-redirect=true&id=" + member.getId();
			}
			return "search?faces-redirect=true";

		} catch (Exception e) {
			log.info("update: id="+id+", member="+member+" : "+e, e);
			error(e.getMessage());
			return null;
		}
	}

	private Collection<ValidationResult> validateMember(Member member, boolean toMessages) {
		Collection<Member> leaders = squadRepo.findLeaders();
		List<ValidationResult> vr = memberValidator.get().validate(member, leaders);
		if (!vr.isEmpty() && toMessages) {
			warn(vr.toString());
		}
		return vr;
	}

	public boolean isUpdateAllowed() {
		return isAdmin() || isGruppe() || isVorstand() || (isLeiter() && !isRegistrationEnd());
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String delete() {

		if (!isDeleteAllowed())
			throw new SecurityException("only admins may delete entry");

		log.info("deleted " + member + " by " + sessionContext.getCallerPrincipal());

		try {
			Member deletableEntity = findById(getId());
			Hibernate.initialize(deletableEntity.getTrupp());
			Hibernate.initialize(deletableEntity.getVollzahler());
			Hibernate.initialize(deletableEntity.getSiblings());

			// cleanup of relationships:
			deletableEntity.getMailMessages().stream().forEach(mm -> mm.setMember(null));

			this.entityManager.remove(deletableEntity);
			this.entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			log.info("update: "+e, e);
			error(e.getMessage());
			return null;
		}
	}

	public Collection<ValidationResult> getValidationResults() {
		return validationResults;
	}
	
	/*
	 * support searching Member entities with pagination
	 */

	private List<Member> pageItems;
	private DataModel<MemberDao> dataModel;

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public Member getExample() {
		return getMemberExample();
	}

	public void setExample(Member example) {
		setMemberExample(example);
	}
	
	public String search() {
		this.page = 0;
		return FacesContext.getCurrentInstance().getViewRoot().getViewId()+"?faces-redirect=true&includeViewParams=true";
	}

	public void paginate() {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Member> root = countCriteria.from(Member.class);
		countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Member> criteria = builder.createQuery(Member.class);
		root = criteria.from(Member.class);
		root.fetch(Member_.trupp, JoinType.LEFT);
		TypedQuery<Member> query = this.entityManager.createQuery(
				criteria.select(root).where(getSearchPredicates(root)).distinct(true).orderBy(builder.asc(root.get(Member_.name)),
						builder.asc(root.get(Member_.vorname)), builder.asc(root.get(Member_.id))));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
		this.pageItems = query.getResultList();
		dataModel = new ListDataModel<>(pageItems.stream().map(m -> memberMapper.memberToDao(m)).collect(Collectors.toList()));
	}

	private Predicate[] getSearchPredicates(Root<Member> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String BVKey = getMemberExample().getBVKey();
		if (BVKey != null && !"".equals(BVKey)) {
			predicatesList
					.add(builder.like(builder.lower(root.get(Member_.bvKey)), '%' + BVKey.toLowerCase() + '%'));
		}
		long PersonenKey = getMemberExample().getPersonenKey();
		if (PersonenKey != 0) {
			predicatesList.add(builder.equal(root.get(Member_.personenKey), PersonenKey));
		}
		String Titel = getMemberExample().getTitel();
		if (Titel != null && !"".equals(Titel)) {
			predicatesList
					.add(builder.like(builder.lower(root.get(Member_.titel)), '%' + Titel.toLowerCase() + '%'));
		}
		String Name = getMemberExample().getName();
		if (Name != null && !"".equals(Name)) {
			predicatesList.add(builder.like(builder.lower(root.get(Member_.name)), '%' + Name.toLowerCase() + '%'));
		}

		String Vorname = getMemberExample().getVorname();
		if (Vorname != null && !"".equals(Vorname)) {
			predicatesList
					.add(builder.like(builder.lower(root.get(Member_.vorname)), '%' + Vorname.toLowerCase() + '%'));
		}
		String Telefon = getMemberExample().getTelefon();
		if (Telefon != null && !"".equals(Telefon)) {
			predicatesList
					.add(builder.like(builder.lower(root.get(Member_.telefon)), '%' + Telefon.toLowerCase() + '%'));
		}
		String Email = getMemberExample().getEmail();
		if (Email != null && !"".equals(Email)) {
			predicatesList
					.add(builder.like(builder.lower(root.get(Member_.email)), '%' + Email.toLowerCase() + '%'));
		}
		String Strasse = getMemberExample().getStrasse();
		if (Strasse != null && !"".equals(Strasse)) {
			predicatesList
					.add(builder.like(builder.lower(root.get(Member_.strasse)), '%' + Strasse.toLowerCase() + '%'));
		}
		if (getMemberExample().getGeschlecht() != null) {
			predicatesList.add(builder.equal(root.get(Member_.geschlecht), getMemberExample().getGeschlecht()));
		}
		if (exampleActive != null) {
			predicatesList.add(builder.equal(root.get(Member_.aktiv), exampleActive));
		}
		if (exampleFree != null) {
			predicatesList.add(builder.equal(root.get(Member_.free), exampleFree));
		}
		if (exampleGilde != null) {
			predicatesList.add(builder.equal(root.get(Member_.gilde), exampleGilde));
		}
		if (exampleSupport != null) {
			predicatesList.add(builder.equal(root.get(Member_.support), exampleSupport));
		}
		if (exampleInfoMail != null) {
			predicatesList.add(builder.equal(root.get(Member_.infoMail), exampleInfoMail));
		}

		Squad trupp = getMemberExample().getTrupp();
		if (trupp != null) {
			predicatesList.add(builder.equal(root.get(Member_.trupp), trupp));
		}
		
		if (exampleTrupps != null && !exampleTrupps.isEmpty()) {
			predicatesList.add(root.get(Member_.trupp).in(exampleTrupps));
		}

		if (getExampleFunctions() != null && !getExampleFunctions().isEmpty()) {
			predicatesList.add(root.join(Member_.funktionen).in(
					getExampleFunctions().stream().filter(f-> f!=null && f.getId() != null).collect(Collectors.toList())));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Member> getPageItems() {
		return pageItems;
	}
	
	public DataModel<MemberDao> getDataModel() {
		return dataModel;
	}

	public long getCount() {
		return this.count;
	}

	public Boolean getExampleActive() {
		return exampleActive;
	}
	
	public void setExampleActive(Boolean exampleActive) {
		this.exampleActive = exampleActive;
	}
	
	public Boolean getExampleFree() {
		return exampleFree;
	}
	
	public void setExampleFree(Boolean exampleFree) {
		this.exampleFree = exampleFree;
	}
	
	public Boolean getExampleSupport() {
		return exampleSupport;
	}
	
	public void setExampleGilde(Boolean exampleGilde) {
		this.exampleGilde = exampleGilde;
	}
	
	public Boolean getExampleGilde() {
		return exampleGilde;
	}
	
	public void setExampleSupport(Boolean exampleSupport) {
		this.exampleSupport = exampleSupport;
	}
	
	public Boolean getExampleInfoMail() {
		return exampleInfoMail;
	}
	
	public void setExampleInfoMail(Boolean exampleInfoMail) {
		this.exampleInfoMail = exampleInfoMail;
	}
	
	public List<Function> getExampleFunctions() {
		return exampleFunctions;
	}
	
	public void setExampleFunctions(List<Function> exampleFunctions) {
		this.exampleFunctions = exampleFunctions;
	}
	
	public List<Squad> getExampleTrupps() {
		return exampleTrupps;
	}
	
	public void setExampleTrupps(List<Squad> exampleTrupps) {
		this.exampleTrupps = exampleTrupps;
	}
	
	/*
	 * support listing and POSTing back Member entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Member> getAll() {

		CriteriaQuery<Member> criteria = this.entityManager.getCriteriaBuilder().createQuery(Member.class);
		return this.entityManager.createQuery(criteria.select(criteria.from(Member.class))).getResultList().stream()
				.sorted().collect(Collectors.toList());
	}

	public List<Member> getSelectableLeaders() {

		CriteriaQuery<Member> criteria = this.entityManager.getCriteriaBuilder().createQuery(Member.class);
		return this.entityManager.createQuery(criteria.select(criteria.from(Member.class))).getResultList().stream()
				.filter(m -> m.isAktiv()).sorted().collect(Collectors.toList());
	}

	public List<Member> getActive() {

		CriteriaQuery<Member> criteria = this.entityManager.getCriteriaBuilder().createQuery(Member.class);
		List<Member> resultList = this.entityManager.createQuery(criteria.select(criteria.from(Member.class))).getResultList();
		if (isAdmin()) {
			return resultList;
		}
		return resultList.stream()
				.filter(m -> m.isAktiv() || m.isAktivExtern()).sorted().collect(Collectors.toList());
	}

	public List<Member> getActiveNoVollzahler() {

		CriteriaQuery<Member> criteria = this.entityManager.getCriteriaBuilder().createQuery(Member.class);
		List<Member> resultList = this.entityManager.createQuery(criteria.select(criteria.from(Member.class))).getResultList();
		if (isAdmin()) {
			return resultList;
		}
		return resultList.stream()
				.filter(m -> (m.isAktiv() || m.isAktivExtern()) && m.getVollzahler() == null).sorted().collect(Collectors.toList());
	}

	public List<Member> filteredNoVollzahler(final String filter) {
		return members.filtered(filter).stream().filter(m -> !m.equals(member) && !member.equals(m.getVollzahler())).collect(Collectors.toList());
	}
	
	public List<Member> filteredNoParents(final String filter) {
		return members.filtered(filter).stream().filter(m -> !m.equals(member) && !m.getParents().contains(member)).collect(Collectors.toList());
	}
	
	public Converter getConverter() {

		final MemberBean ejbProxy = this.sessionContext.getBusinessObject(MemberBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {
				if (StringUtils.isBlank(value))
					return null;
				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value instanceof Member) 
					return ((Member)value).getId() != null ? ""+((Member)value).getId() : null;
				return ""+(value != null ? value : "");
			}
		};
	}

	public Converter getListConverter() {
		return new Converter() {
			
			final MemberBean ejbProxy = sessionContext.getBusinessObject(MemberBean.class);
			
			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value instanceof Collection) {
					return ((Collection<Member>)value).stream().filter(o->o != null)
							.filter(f->f.getId() != null).map(f->f.getId().toString()).collect(Collectors.joining(","));
				}
				return "";
			}
			
			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {
				if (StringUtils.isNotBlank(value)) {
					return Stream.of(value.split(","))
							.map(id->ejbProxy.findById(Long.valueOf(id)))
							.filter(o->o != null).collect(Collectors.toList());
				}
				return new ArrayList<>();
			}
		};
	}

	/*
	 * support adding children to bidirectional, one-to-many tables
	 */

	private Member add = new Member();

	public Member getAdd() {
		return this.add;
	}

	public Member getAdded() {
		Member added = this.add;
		this.add = new Member();
		return added;
	}

	public List<String> getDistinctName() {
		return memberRepo.findDistinctName();
	}

	public List<String> getDistinctNameLike(String name) {
		return memberRepo.findDistinctNameLike(("%"+name+"%").toLowerCase());
	}

	public List<String> getDistinctVorname() {
		return memberRepo.findDistinctVorname();
	}

	public List<String> getDistinctVornameLike(String vorname) {
		return memberRepo.findDistinctVornameLike(("%"+vorname+"%").toLowerCase());
	}

	public List<String> getDistinctPLZ() {
		return memberRepo.findDistinctPLZ();
	}

	public List<String> getDistinctPLZLike(String plz) {
		return memberRepo.findDistinctPLZLike(("%"+plz+"%").toLowerCase());
	}

	public List<String> getDistinctOrt() {
		return memberRepo.findDistinctOrt();
	}

	public List<String> getDistinctOrtLike(String ort) {
		return memberRepo.findDistinctOrtLike(("%"+ort+"%").toLowerCase());
	}

	public List<String> getDistinctStrasse() {
		return memberRepo.findDistinctStrasse();
	}

	public List<String> getDistinctStrasseLike(String strasse) {
		return memberRepo.findDistinctStrasseLike(("%"+strasse+"%").toLowerCase());
	}

	public List<String> getDistinctTitel() {
		return memberRepo.findDistinctTitel();
	}

	public List<String> getDistinctTitelLike(String titel) {
		return memberRepo.findDistinctTitelLike(("%"+titel+"%").toLowerCase());
	}

	public List<String> getDistinctAnrede() {
		return memberRepo.findDistinctAnrede();
	}

	public List<String> getDistinctAnredeLike(String anrede) {
		return memberRepo.findDistinctAnredeLike(("%"+anrede+"%").toLowerCase());
	}

	public List<String> getDistinctReligion() {
		return memberRepo.findDistinctReligion();
	}
	
	public List<String> getDistinctReligionLike(String religion) {
		return memberRepo.findDistinctReligionLike(("%"+religion+"%").toLowerCase());
	}
	
	public String logout() {
		try {
			((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).logout();
		} catch (Exception e) {}
		return "index?faces-redirect=true";
	}
	
	public List<ScoutRole> getScoutRoles() {
		return Arrays.asList(ScoutRole.values());
	}
	
	public List<Sex> getSexes() {
		return Arrays.asList(Sex.values());
	}

	public List<Month> getMonths() {
		return Arrays.asList(Month.values());
	}
	
	public void deActivate(Member m) {
		memberRepo.detach(m);
		memberRepo.deActivate(m.getId());
		member = memberRepo.findBy(m.getId());
	}
}
