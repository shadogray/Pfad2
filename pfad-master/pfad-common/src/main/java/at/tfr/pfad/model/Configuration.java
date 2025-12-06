/*
 * Copyright 2015 Thomas Frühbeck, fruehbeck(at)aon(dot)at.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package at.tfr.pfad.model;

import at.tfr.pfad.ConfigurationType;
import at.tfr.pfad.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true, value = {"handler", "hibernateLazyInitializer"})
public class Configuration extends BaseEntity implements Comparable<Configuration>, Cloneable {

	public static final String BADEN_KEY = "BAD";
	public static final String BADEN_KEYPFX = "3-BAD-";
	public static final String REGEND_KEY = "RegistrationEnd";
	public static final String BADEN_IBANS = "BadenIBANs";
	public static final Pattern DOWNLOAD_PATTERN = Pattern.compile("^.+?\\.download\\.(.+)$");
    public static final String STORAGE_PATH = "storage.path";
    public static final String STORAGE_PATH_DEFAULT = "/opt/pfad/files";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "configuration_seq")
	@SequenceGenerator(name = "configuration_seq", sequenceName = "configuration_seq", allocationSize = 1, initialValue = 1)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;

	@Length(max = 64)
	@Column(length = 64)
	private String ckey;

	@Length(max = 4096)
	@Column(length = 4096)
	private String cvalue;

	@Column
	@Enumerated(EnumType.STRING)
	private Role role;
	
	@Column
	private String owners;

	@Column(nullable = false, columnDefinition = "varchar2(16) default 'simple'")
	@Enumerated(EnumType.STRING)
	private ConfigurationType type;
	
	@Column(length = 4096)
	private String description;
	
	@Column(length = 1024)
	private String headers;
	
	@Transient
	private String uiName;
	
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

	public Role getRole() {
		return role != null ? role : Role.none;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getOwners() {
		return owners;
	}
	
	public void setOwners(String owners) {
		this.owners = owners;
	}
	
	public ConfigurationType getType() {
		return type != null ? type : ConfigurationType.simple;
	}

	public void setType(ConfigurationType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHeaders() {
		return headers;
	}

	public void setHeaders(String headers) {
		this.headers = headers;
	}
	
	public String[] toHeaders(List<String> alias) {
		String[] result = new String[alias.size()];
		String[] vals = (""+headers).split(";");
		for (int i=0; i < alias.size(); i++) {
			if (vals.length > i && StringUtils.isNotBlank(vals[i]) && alias.get(i).matches("\\d+"))
				result[i] = vals[i].trim();
			else
				result[i] = alias.get(i);
		}
		return result;
	}

	public String getCkey() {
		return ckey;
	}

	public void setCkey(String ckey) {
		this.ckey = ckey;
	}

	public Configuration withCkey(String ckey) {
		setCkey(ckey);
		return this;
	}
	
	public String getCvalue() {
		if ((""+ckey).toLowerCase().contains("password")) 
			return "********";
		return cvalue;
	}

	public void setCvalue(String cvalue) {
		this.cvalue = cvalue;
	}

	public Configuration withCValue(String cvalue) {
		setCvalue(cvalue);
		return this;
	}
	
	public String getCvalueIntern() {
		return cvalue;
	}

	public String getUiName() {
		return uiName;
	}
	
	public void setUiName(String uiName) {
		this.uiName = uiName;
	}
	
	public Configuration withUiName(String uiName) {
		setUiName(uiName);
		return this;
	}
		
	public boolean isDownload() {
		return ckey != null && DOWNLOAD_PATTERN.matcher(ckey).matches();
	}
	
	public String getDownloadName() {
		if (isDownload()) {
			Matcher matcher = DOWNLOAD_PATTERN.matcher(ckey);
			if (matcher.matches() && matcher.groupCount() >= 1)
				return matcher.group(1);
		}
		return ckey;
	}
	
	public boolean isNative() {
		return ConfigurationType.nativeQuery.equals(type);
	}
	
	public void setNative(boolean nativeQuery) {
		type = nativeQuery ? ConfigurationType.nativeQuery : ConfigurationType.query;
	}
	
	@Override
	public int compareTo(Configuration o) {
		if (this.id != null && o.id != null) 
			return this.id.compareTo(o.id);
		return this.toString().compareTo(o.toString());
	}
	
	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		if (ckey != null && !ckey.trim().isEmpty())
			result += "ckey: " + ckey;
		if (cvalue != null && !cvalue.trim().isEmpty())
			result += ", cvalue: " + cvalue;
		return result;
	}
	
	public String toTitle() {
		return ckey + ": " + (description != null ? description + "\n" : "") + "\nAbfrage: \n" + cvalue;
	}

	public Configuration copy() {
		try {
			return (Configuration) clone();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}