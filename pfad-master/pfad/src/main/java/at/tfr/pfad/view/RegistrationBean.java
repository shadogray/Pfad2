package at.tfr.pfad.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.omnifaces.util.Faces;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.event.RowEditEvent;

import at.tfr.pfad.RegistrationStatus;
import at.tfr.pfad.dao.RegistrationRepository;
import at.tfr.pfad.model.Member;
import at.tfr.pfad.model.Registration;
import at.tfr.pfad.view.convert.TriStateConverter;

@Named
@ViewScoped
@Stateful
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class RegistrationBean extends BaseBean {

	private Logger log = Logger.getLogger(getClass());
	
	@Inject
	private RegistrationRepository regRepo;
	private Registration example = new Registration();
	private RegistrationStatus[] filterStati;
	private List<Integer> distinctGebJahr = new ArrayList<>();
	private List<Integer> distinctSchoolEntry = new ArrayList<>();
	private List<Registration> dataModel;
	private Long id;
	private Registration registration;
	private Boolean storno, aktiv;
	private boolean all;
	private TriStateConverter triStateConverter = new TriStateConverter();
	private Map<String,Integer> rowVals = new LinkedHashMap<>();
	private List<Entry<String,Integer>> rowDm;

	@PostConstruct
	public void init() {
		distinctGebJahr = regRepo.findDistinctGebJahr();
		distinctSchoolEntry = regRepo.findDistinctSchoolEntry();
		filterStati = new RegistrationStatus[] 
				{ RegistrationStatus.ZusageGes, RegistrationStatus.AbsageGes}; 
		rowVals.put("20", new Integer(20));
		rowVals.put("50", new Integer(50));
		rowVals.put("Alle", new Integer(10000));
		rowDm = new ArrayList<>(rowVals.entrySet());
		paginate();
	}
	
	@PreDestroy
	private void destroy() {
		log.debug("destroyed: id="+id+", filter="+Arrays.toString(filterStati));
	}
	
	public void retrieve() {
		if ((sessionBean.isAdmin() || sessionBean.isRegistrierung() || sessionBean.isAnmeldung()) && id == null) {
			registration = new Registration();
			registration.setAktiv(true);
			registration.setStatus(RegistrationStatus.Erstellt);
		} else {
			registration = regRepo.findBy(id);
		}
	}
	
	public String show(Long id) {
		return "create?faces-redirect=true&includeViewParams=true&id="+id;
	}

	public String paginateRedirect() {
		paginate();
		return Faces.getViewId()+"?faces-redirect=true&includeViewParams=true";
	}
	
	public void paginate() {
		// Belli: sollen alle sehen können
		dataModel = regRepo.queryBy(example, filterStati, aktiv, storno);
	}

	public void update() {
		if (isUpdateAllowed()) {
			registration = updateRegistration(registration);
			id = registration.getId();
			info("Anmeldung aktualisert..");
		} else {
			error("Keine Berechtigung zur Änderung für Benutzer: " + sessionBean.getUser() + "!");
		}
	}

	public Registration updateRegistration(Registration reg) {
		Member child = reg.getMember();
		if (child != null) {
			if (StringUtils.isBlank(child.getName()) || !child.getName().equals(reg.getName()))
				child.setName(reg.getName());
			if (StringUtils.isBlank(child.getVorname()) || !child.getVorname().equals(reg.getVorname()))
				child.setVorname(reg.getVorname());
			if (StringUtils.isBlank(child.getStrasse()) || !child.getStrasse().equals(reg.getStrasse()))
				child.setStrasse(reg.getStrasse());
			if (StringUtils.isBlank(child.getOrt()) || !child.getOrt().equals(reg.getOrt()))
				child.setOrt(reg.getOrt());
			if (StringUtils.isBlank(child.getPLZ()) || !child.getPLZ().equals(reg.getPLZ()))
				child.setPLZ(reg.getPLZ());
			if (StringUtils.isBlank(child.getEmail()) || !child.getEmail().equals(reg.getEmail()))
				child.setEmail(reg.getEmail());
			if (StringUtils.isBlank(child.getTelefon()) || !child.getTelefon().equals(reg.getTelefon()))
				child.setTelefon(reg.getTelefon());
			if (child.getGebTag() == 0 || child.getGebTag() != reg.getGebTag())
				child.setGebTag(reg.getGebTag());
			if (child.getGebMonat() == 0 || child.getGebMonat() != reg.getGebMonat())
				child.setGebMonat(reg.getGebMonat());
			if (child.getGebJahr() == 0 || child.getGebJahr() != reg.getGebJahr())
				child.setGebJahr(reg.getGebJahr());
		}
		Member parent = reg.getParent();
		if (parent != null) {
			if (StringUtils.isBlank(parent.getName()) || !parent.getName().equals(reg.getParentName()))
				parent.setName(reg.getParentName());
			if (StringUtils.isBlank(parent.getVorname()) || !parent.getVorname().equals(reg.getParentVorname()))
				parent.setVorname(reg.getParentVorname());
			if (StringUtils.isBlank(parent.getStrasse()) || !parent.getStrasse().equals(reg.getStrasse()))
				parent.setStrasse(reg.getStrasse());
			if (StringUtils.isBlank(parent.getOrt()) || !parent.getOrt().equals(reg.getOrt()))
				parent.setOrt(reg.getOrt());
			if (StringUtils.isBlank(parent.getPLZ()) || !parent.getPLZ().equals(reg.getPLZ()))
				parent.setPLZ(reg.getPLZ());
			if (StringUtils.isBlank(parent.getEmail()) || !parent.getEmail().equals(reg.getEmail()))
				parent.setEmail(reg.getEmail());
			if (StringUtils.isBlank(parent.getTelefon()) || !parent.getTelefon().equals(reg.getTelefon()))
				parent.setTelefon(reg.getTelefon());
		}
		return regRepo.saveAndFlush(reg);
	}

	public void onRowEdit(RowEditEvent event) {
		if (!isUpdateAllowed() || !(event.getObject() instanceof Registration)) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anmeldung NICHT geändert!!", "Keine Berechtigung");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		} else {
			Registration extReg = (Registration) event.getObject();
			Registration reg = regRepo.findBy(extReg.getId());
			reg.setName(extReg.getName());
			reg.setVorname(extReg.getVorname());
			reg.setGeschlecht(extReg.getGeschlecht());
			reg.setGebJahr(extReg.getGebJahr());
			reg.setGebMonat(extReg.getGebMonat());
			reg.setGebTag(extReg.getGebTag());
			reg.setSchoolEntry(extReg.getSchoolEntry());
			reg.setEmail(extReg.getEmail());
			reg.setTelefon(extReg.getTelefon());

			reg.setStrasse(extReg.getStrasse());
			reg.setOrt(extReg.getOrt());
			reg.setPLZ(extReg.getPLZ());
			reg.setParentName(extReg.getParentName());
			reg.setParentVorname(extReg.getParentVorname());
			reg.setStatus(extReg.getStatus());
			reg.setAktiv(extReg.isAktiv());
			reg.setStorno(extReg.isStorno());
			
			updateRegistration(reg);
			info("Anmeldung geändert: " + reg.toFullString());
		}
	}

	public void updateStatus(AjaxBehaviorEvent event) {
		try {
			Object attrRegId = event.getComponent().getAttributes().get("regId");
			if (attrRegId != null) {
				Long regId = Long.parseLong(attrRegId.toString());
				Registration reg = regRepo.findBy(regId);
				RegistrationStatus status = (RegistrationStatus)((SelectOneMenu)event.getComponent()).getValue();
				reg.setStatus(status);
				regRepo.flush();
				info("Anmeldung geändert: " + reg + ": Status: " + status);
			}
		} catch (Exception e) {
			warn("Fehler: " + e);
		}
	}
	
	public void onRowCancel(RowEditEvent event) {
		info("Änderung Abgebrochen: " + event.getObject());
	}
	
	public void convertToMember() {
		registration = regRepo.findBy(id);
		try {
			if (registration.getMember() != null) {
				throw new ValidationException("Conversion already done: " + registration.getMember());
			}
			
			Member parent = registration.getParent();
			if (parent == null) {
				List<Member> members = memberRepo.
						findByNameEqualIgnoreCaseAndVornameEqualIgnoreCaseAndStrasseEqualIgnoreCaseAndOrtEqualIgnoreCase(
								registration.getParentName(), 
						registration.getParentVorname(), registration.getStrasse(), registration.getOrt());
				if (members.size() == 1) {
					parent = members.get(0);
				} else {
					parent = new Member();
					parent.setAktiv(false);
					parent.setName(registration.getParentName());
					parent.setVorname(registration.getParentVorname());
					parent.setStrasse(registration.getStrasse());
					parent.setPLZ(registration.getPLZ());
					parent.setOrt(registration.getOrt());
					parent.setEmail(registration.getEmail());
					parent.setTelefon(registration.getTelefon());
					parent = memberRepo.saveAndFlush(parent);
				}
			}
			
			Member member = new Member();
			member.setAnrede(registration.getAnrede());
			member.setName(registration.getName());
			member.setVorname(registration.getVorname());
			member.setGeschlecht(registration.getGeschlecht());
			
			member.setGebJahr(registration.getGebJahr());
			member.setGebMonat(registration.getGebMonat());
			member.setGebTag(registration.getGebTag());
			
			member.setStrasse(registration.getStrasse());
			member.setPLZ(registration.getPLZ());
			member.setOrt(registration.getOrt());
			member.setEmail(registration.getEmail());
			member.setTelefon(registration.getTelefon());
			
			member.setAktiv(true);
			
			member = memberRepo.saveAndFlush(member);
			
			parent.getSiblings().add(member);
			member.addParent(parent);
			
			memberRepo.flush();
			
			registration.setParent(parent);
			registration.setMember(member);
			registration.setAktiv(false);
			registration.setStatus(RegistrationStatus.Mitglied);
			regRepo.saveAndFlush(registration);
			
			info("In Mitglied umgewandelt: " + member);
			
		} catch (Exception e) {
			log.info("Cannot convert: " + registration + " : " + e, e);
			error("Anmeldung NICHT geändert!! Fehler: " + e.getLocalizedMessage());
		}
	}

	public boolean isUpdateAllowed() {
		return sessionBean.isAdmin() || sessionBean.isGruppe() || sessionBean.isRegistrierung()
				|| sessionBean.isAnmeldung();
	}

	public ListDataModel<Registration> getDataModel() {
		return new ListDataModel<Registration>(dataModel);
	}

	public Registration getExample() {
		return example;
	}

	public void setExample(Registration example) {
		this.example = example;
	}
	
	public RegistrationStatus[] getFilterStati() {
		return filterStati;
	}
	
	public void setFilterStati(RegistrationStatus[] filterStati) {
		this.filterStati = filterStati;
	}

	public List<Integer> getDistinctGebJahr() {
		return distinctGebJahr;
	}
	
	public List<Integer> getDistinctSchoolEntry() {
		return distinctSchoolEntry;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Registration getRegistration() {
		return registration;
	}

	public void setRegistration(Registration registration) {
		this.registration = registration;
	}

	public boolean isAll() {
		return all;
	}

	public void setAll(boolean all) {
		this.all = all;
	}
	
	public String getStorno() {
		return triStateConverter.getAsString(null, null, storno);
	}
	
	public void setStorno(String storno) {
		this.storno = (Boolean)triStateConverter.getAsObject(null, null, storno);
	}
	
	public void toggleStorno() {
		storno = (storno == null ? Boolean.TRUE : Boolean.TRUE.equals(storno) ? Boolean.FALSE : null);
		paginate();
	}
	
	public String getAktiv() {
		return triStateConverter.getAsString(null, null, aktiv);
	}
	
	public void setAktiv(String aktiv) {
		this.aktiv = (Boolean)triStateConverter.getAsObject(null, null, aktiv);
	}

	public void toggleAktiv() {
		aktiv = (aktiv == null ? Boolean.TRUE : Boolean.TRUE.equals(aktiv) ? Boolean.FALSE : null);
		paginate();
	}
	
	public List<Entry<String,Integer>> getRowVals() {
		return rowDm;
	}
	
	public List<RegistrationStatus> getStati() {
		return Arrays.asList(RegistrationStatus.values());
	}
}
