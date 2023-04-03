package at.tfr.pfad.view;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.apache.deltaspike.core.util.StringUtils;

@Named
@ViewScoped
public class PfadUI implements Serializable {

	private String menuSelect;

	public String getMenuSelect() {
		return menuSelect;
	}

	public void setMenuSelect(final String menuSelect) {
		this.menuSelect = menuSelect;
	}
	
	public String isMenuSelected(final String menuSelect) {
		return StringUtils.isNotEmpty(this.menuSelect) && this.menuSelect.equals(menuSelect) ? "menu-select" : "";
	}
	
}
