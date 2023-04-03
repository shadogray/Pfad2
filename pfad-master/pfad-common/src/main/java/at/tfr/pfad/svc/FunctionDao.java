package at.tfr.pfad.svc;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FunctionDao extends BaseDao {

	private Long id;
	private int version;
	private String function;
	private String key;
	private boolean exportReg;
	private boolean free;

	@XmlID
	public Long getId() {
		return this.id;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof FunctionDao)) {
			return false;
		}
		FunctionDao other = (FunctionDao) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
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
		return exportReg;
	}

	public void setExportReg(boolean exportReg) {
		this.exportReg = exportReg;
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	@Override
	public String getName() {
		return function;
	}
	
	@Override
	public int compareTo(BaseDao o) {
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
