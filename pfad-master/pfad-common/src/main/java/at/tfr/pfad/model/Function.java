/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import at.tfr.pfad.Sex;

@Audited(withModifiedFlag = true)
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@JsonIgnoreProperties(ignoreUnknown=true, value = {"handler", "hibernateLazyInitializer"})
public class Function extends BaseEntity implements Presentable, Comparable<Function> {

	public static final String PTA = "P"; // Pfadfinder Trotz Allem
	public static final String ZBV = "ZBV"; // Zur besonderen Verwendung
	public static final String MIT = "MIT"; // Mitarbeiter
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "function_seq")
	@SequenceGenerator(name = "function_seq", sequenceName = "function_seq", allocationSize = 1, initialValue = 1)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;

	@Column
	private String function;

	@Column
	private String key;

	@Column(columnDefinition = "boolean default 'false' not null")
	private boolean exportReg;

	@Column(columnDefinition = "boolean default 'false' not null")
	private boolean free;

	@Column(columnDefinition = "boolean default 'false' not null")
	private boolean leader;

	@Column(columnDefinition = "boolean default 'false' not null")
	private boolean noFunction;

	@Enumerated(EnumType.STRING)
	protected Sex sex;
	
	@XmlID
	public Long getId() {
		return this.id;
	}

	public String getIdStr() {
		return id != null ? id.toString() : "";
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isExportReg() {
		return Boolean.TRUE.equals(exportReg);
	}

	public void setExportReg(boolean exportReg) {
		this.exportReg = Boolean.TRUE.equals(exportReg);
	}

	public boolean isFree() {
		return Boolean.TRUE.equals(free);
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	@Override
	public String getName() {
		return function;
	}

	public boolean isLeader() {
		return Boolean.TRUE.equals(leader);
	}

	public void setLeader(boolean leader) {
		this.leader = leader;
	}

	public boolean isNoFunction() {
		return Boolean.TRUE.equals(noFunction);
	}

	public void setNoFunction(boolean noFunction) {
		this.noFunction = noFunction;
	}

	public Sex getSex() {
		return sex;
	}
	
	public void setSex(Sex sex) {
		this.sex = sex;
	}
	
	@Override
	public String getShortString() {
		String result = "" + function;
		if (key != null && !key.trim().isEmpty())
			result += ", key: " + key;
		return result;
	}
	
	@Override
	public String getLongString() {
		return toString();
	}
	
	@Override
	public int compareTo(Function o) {
		return this.toString().compareTo(o.toString());
	}
	
	@Override
	public String toString() {
		String result = "" + function;
		if (key != null && !key.trim().isEmpty())
			result += ", key: " + key;
		result += ", reg: " + exportReg;
		return result;
	}
}
