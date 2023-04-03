package at.tfr.pfad.svc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import at.tfr.pfad.model.PrimaryKeyHolder;

@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
public class BaseDao implements PrimaryKeyHolder, Serializable, Comparable<BaseDao> {

	protected Long id;
	protected String name;
	protected String shortName;
	protected String longName;
	protected String details;
	protected List<Contact> contacts = new ArrayList<>();
	
	@XmlID
	public Long getId() {
		return id;
	};
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getDetails() {
		return details;
	}
	
	public void setDetails(String details) {
		this.details = details;
	}
	
	public List<Contact> getContacts() {
		return contacts;
	}
	
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	
	@Override
	public int compareTo(BaseDao o) {
		return getCompareString().compareTo(o.getCompareString());
	}

	public String getCompareString() {
		return "" + name + id;
	}

	@Override
	public String toString() {
		return "BaseDao [id=" + id + ", name=" + name + ", shortName=" + shortName + "]";
	}
}
