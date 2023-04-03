package at.tfr.pfad.action;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.tfr.pfad.DuplicateException;
import at.tfr.pfad.InvalidValueException;
import at.tfr.pfad.PfadException;

@WebServlet(urlPatterns = "action/registration")
public class RegistrationAction implements Servlet {

	@Inject
	private RegistrationHandlerBean regHandler;
	
	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		doService((HttpServletRequest) req, (HttpServletResponse) res);
	}

	public void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			regHandler.service(req);
		} catch (InvalidValueException e) {
			res.sendError(res.SC_BAD_REQUEST, e.getMessage());
		} catch (DuplicateException e) {
			res.sendError(res.SC_CONFLICT, e.getMessage());
		} catch (PfadException e) {
			res.sendError(res.SC_BAD_REQUEST, e.getMessage());
		}
	}

	
	@Override
	public ServletConfig getServletConfig() {
		return null;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
	}

	@Override
	public String getServletInfo() {
		return null;
	}

	@Override
	public void destroy() {
	}

}
