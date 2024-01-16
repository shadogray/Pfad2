package at.tfr.pfad.action;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
