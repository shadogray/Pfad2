package at.tfr.pfad.dao;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;

import at.tfr.pfad.ScoutRole;
import at.tfr.pfad.Sex;
import at.tfr.pfad.model.Member;

//@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
//@StaticMetamodel(Member.class)
public abstract class MemberA {

	public static volatile SingularAttribute<Member, Integer> gebMonat;
	public static volatile SingularAttribute<Member, String> gruppenSchluessel;
	public static volatile SingularAttribute<Member, Sex> geschlecht;
	public static volatile SingularAttribute<Member, Boolean> dead;
	public static volatile SingularAttribute<Member, Boolean> aktivExtern;
	public static volatile SingularAttribute<Member, Boolean> trail;
	public static volatile SingularAttribute<Member, String> titel;
	public static volatile SingularAttribute<Member, Boolean> infoMail;
	public static volatile SingularAttribute<Member, String> telefon;
	public static volatile SingularAttribute<Member, String> changedBy;
	public static volatile SingularAttribute<Member, Boolean> gilde;
	public static volatile SingularAttribute<Member, Long> id;
	public static volatile SingularAttribute<Member, Boolean> free;
	public static volatile SingularAttribute<Member, String> email;
	public static volatile SingularAttribute<Member, Boolean> altER;
	public static volatile SingularAttribute<Member, Integer> gebJahr;
	public static volatile SingularAttribute<Member, String> strasse;
	public static volatile SingularAttribute<Member, Date> created;
	public static volatile SingularAttribute<Member, String> vorname;
	public static volatile SingularAttribute<Member, String> anrede;
	public static volatile SingularAttribute<Member, Integer> version;
	public static volatile SingularAttribute<Member, String> religion;
	public static volatile SingularAttribute<Member, String> bvKey;
	public static volatile SingularAttribute<Member, String> ort;
	public static volatile SingularAttribute<Member, Long> personenKey;
	public static volatile SingularAttribute<Member, ScoutRole> rolle;
	public static volatile SingularAttribute<Member, Long> truppId;
	public static volatile SingularAttribute<Member, String> createdBy;
	public static volatile SingularAttribute<Member, Integer> gebTag;
	public static volatile SingularAttribute<Member, String> name;
	public static volatile SingularAttribute<Member, Long> VollzahlerId;
	public static volatile SingularAttribute<Member, Boolean> support;
	public static volatile SingularAttribute<Member, Boolean> aktiv;
	public static volatile SingularAttribute<Member, String> plz;
	public static volatile SingularAttribute<Member, Date> changed;
	public static volatile SingularAttribute<Member, String> status;

	public static final String GEB_MONAT = "gebMonat";
	public static final String GRUPPEN_SCHLUESSEL = "gruppenSchluessel";
	public static final String GESCHLECHT = "geschlecht";
	public static final String DEAD = "dead";
	public static final String AKTIV_EXTERN = "aktivExtern";
	public static final String TRAIL = "trail";
	public static final String TITEL = "titel";
	public static final String INFO_MAIL = "infoMail";
	public static final String TELEFON = "telefon";
	public static final String CHANGED_BY = "changedBy";
	public static final String GILDE = "gilde";
	public static final String ID = "id";
	public static final String FREE = "free";
	public static final String EMAIL = "email";
	public static final String ALT_ER = "altER";
	public static final String GEB_JAHR = "gebJahr";
	public static final String STRASSE = "strasse";
	public static final String CREATED = "created";
	public static final String VORNAME = "vorname";
	public static final String ANREDE = "anrede";
	public static final String VERSION = "version";
	public static final String RELIGION = "religion";
	public static final String BV_KEY = "bvKey";
	public static final String ORT = "ort";
	public static final String PERSONEN_KEY = "personenKey";
	public static final String ROLLE = "rolle";
	public static final String TRUPP_ID = "truppId";
	public static final String CREATED_BY = "createdBy";
	public static final String GEB_TAG = "gebTag";
	public static final String NAME = "name";
	public static final String VOLLZAHLER_ID = "VollzahlerId";
	public static final String SUPPORT = "support";
	public static final String AKTIV = "aktiv";
	public static final String PLZ = "plz";
	public static final String CHANGED = "changed";
	public static final String STATUS = "status";

}

