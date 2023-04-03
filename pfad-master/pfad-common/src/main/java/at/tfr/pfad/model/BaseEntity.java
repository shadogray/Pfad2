package at.tfr.pfad.model;

import java.io.Serializable;

public abstract class BaseEntity implements PrimaryKeyHolder, Serializable {

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BaseEntity)) {
			return super.equals(obj);
		}
		
		BaseEntity other = (BaseEntity) obj;
		Long myId = getId();
		Long othId = other.getId();
		
		if (myId != null && othId != null) {
			return myId.equals(othId);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : super.hashCode();
	}

}
