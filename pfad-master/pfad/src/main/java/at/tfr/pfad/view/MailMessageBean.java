/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.view;

import at.tfr.pfad.Role;
import at.tfr.pfad.dao.MailMessageRepository;
import at.tfr.pfad.model.MailMessage;
import at.tfr.pfad.model.MailMessage_;
import at.tfr.pfad.model.Member_;
import at.tfr.pfad.model.Registration_;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.Stateful;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Backing bean for MailMessage entities.
 * This class provides CRUD functionality for all MailMessage entities. It
 * focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt>
 * for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ViewScoped
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class MailMessageBean extends BaseBean<MailMessage,MailMessage> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private MailMessageRepository configRepo;

	/*
	 * Support creating and retrieving MailMessage entities
	 */

	private Long id;
	private List<MailMessage> allConfigs;

	@PostConstruct
	public void init() {
		allConfigs = configRepo.findAll();
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private MailMessage MailMessage;
	private String memberName;
	private String registrationName;

	public MailMessage getMailMessage() {
		return this.MailMessage;
	}

	public void setMailMessage(MailMessage MailMessage) {
		this.MailMessage = MailMessage;
	}

	public String create() {
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.id == null) {
			this.MailMessage = this.example;
		} else {
			this.MailMessage = findById(getId());
		}
		entity = MailMessage;
	}

	public MailMessage findById(Long id) {

		return entityManager.find(MailMessage.class, id);
	}

	/*
	 * Support updating and deleting MailMessage entities
	 */

	@Override
	public boolean isUpdateAllowed() {
		return isAdmin() || isGruppe() || isVorstand();
	}

	public String update() {

		try {
			if (!isUpdateAllowed()) {
				throw new SecurityException("Update denied for: "+sessionBean.getUser());
			}
			validator.validate(MailMessage);

			if (this.id == null) {
				entityManager.persist(this.MailMessage);
				entityManager.flush();
				return "search?faces-redirect=true";
			} else {
				MailMessage = entityManager.merge(MailMessage);
				entityManager.flush();
				return "view?faces-redirect=true&id=" + this.MailMessage.getId();
			}
		} catch (Exception e) {
			log.info("update: "+e, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String delete() {

		if (!isDeleteAllowed())
			throw new SecurityException("only admins may delete entry");

		try {
			MailMessage deletableEntity = findById(getId());

			entityManager.remove(deletableEntity);
			entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			log.info("update: "+e, e);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching MailMessage entities with pagination
	 */

	private List<MailMessage> pageItems;

	private MailMessage example = new MailMessage();

	public MailMessage getExample() {
		return this.example;
	}

	public void setExample(MailMessage example) {
		this.example = example;
	}
	
	public String getMemberName() {
		return memberName;
	}
	
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getRegistrationName() {
		return registrationName;
	}
	
	public void setRegistrationName(String registrationName) {
		this.registrationName = registrationName;
	}
	
	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<MailMessage> root = countCriteria.from(MailMessage.class);
		countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
		this.count = entityManager.createQuery(countCriteria).getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<MailMessage> criteria = builder.createQuery(MailMessage.class);
		root = criteria.from(MailMessage.class);
		TypedQuery<MailMessage> query = entityManager
				.createQuery(criteria.select(root).where(getSearchPredicates(root))
						.orderBy(builder.desc(root.get(MailMessage_.id))));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<MailMessage> root) {

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String receiver = this.example.getReceiver();
		if (StringUtils.isNotBlank(receiver)) {
			predicatesList.add(builder.like(builder.lower(root.get(MailMessage_.receiver)), '%' + receiver.toLowerCase() + '%'));
		}
		String subject = this.example.getSubject();
		if (StringUtils.isNotBlank(subject)) {
			predicatesList.add(builder.like(builder.lower(root.get(MailMessage_.subject)), '%' + subject.toLowerCase() + '%'));
		}
		String text = this.example.getText();
		if (StringUtils.isNotBlank(text)) {
			predicatesList
					.add(builder.like(builder.lower(root.get(MailMessage_.text)), '%' + text.toLowerCase() + '%'));
		}
		String createdBy = this.example.getCreatedBy();
		if (StringUtils.isNotBlank(createdBy)) {
			predicatesList
					.add(builder.like(builder.lower(root.get(MailMessage_.createdBy)), '%' + createdBy.toLowerCase() + '%'));
		}
		String sender = this.example.getSender();
		if (StringUtils.isNotBlank(sender)) {
			predicatesList
					.add(builder.like(builder.lower(root.get(MailMessage_.sender)), '%' + sender.toLowerCase() + '%'));
		}
		if (StringUtils.isNotBlank(memberName)) {
			Predicate name = builder.like(builder.lower(root.get(MailMessage_.member).get(Member_.name)), '%' + memberName.toLowerCase() + '%');
			Predicate vorname = builder.like(builder.lower(root.get(MailMessage_.member).get(Member_.vorname)), '%' + memberName.toLowerCase() + '%');
			predicatesList.add(builder.or(name, vorname));
		}
		if (StringUtils.isNotBlank(registrationName)) {
			Predicate name = builder.like(builder.lower(root.get(MailMessage_.registration).get(Registration_.name)), '%' + registrationName.toLowerCase() + '%');
			Predicate vorname = builder.like(builder.lower(root.get(MailMessage_.registration).get(Registration_.vorname)), '%' + registrationName.toLowerCase() + '%');
			predicatesList.add(builder.or(name, vorname));
		}
		
		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<MailMessage> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back MailMessage entities (e.g. from inside
	 * an HtmlSelectOneMenu)
	 */

	public List<MailMessage> getAll() {

		CriteriaQuery<MailMessage> criteria = entityManager.getCriteriaBuilder()
				.createQuery(MailMessage.class);
		return entityManager.createQuery(criteria.select(criteria.from(MailMessage.class))).getResultList();
	}

	public Converter getConverter() {

		final MailMessageBean ejbProxy = this.sessionContext.getBusinessObject(MailMessageBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {
				if (StringUtils.isBlank(value))
					return null;
				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value instanceof MailMessage) 
					return ""+((MailMessage)value).getId();
				return ""+(value != null ? value : "");
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private MailMessage add = new MailMessage();

	public MailMessage getAdd() {
		return this.add;
	}

	public MailMessage getAdded() {
		MailMessage added = this.add;
		this.add = new MailMessage();
		return added;
	}
	
	public List<Role> getRoles() {
		return Arrays.asList(Role.values());
	}
	
	public List<MailMessage> getAllConfigs() {
		return allConfigs;
	}
}
