package at.tfr.pfad.view;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import at.tfr.pfad.util.SessionBean;

@Named
@RequestScoped
public class LoginBean {
	
	@Inject
	private SessionBean sessionBean;
	
	private String username;
	private String password;

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String login() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		try {
			
			request.login(this.username, this.password);
			sessionBean.init();
			
		} catch (ServletException e) {
			context.addMessage(null, new FacesMessage("Login failed."));
			return "login";
		}
		return "/pfad/index?faces-redirect=true";
	}

	public String logout() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		try {
			request.getSession().invalidate();
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage("Logout failed."));
		}
		return "/pfad/index?faces-redirect=true";
	}
}