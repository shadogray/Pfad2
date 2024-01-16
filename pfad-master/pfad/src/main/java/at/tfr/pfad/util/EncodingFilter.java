package at.tfr.pfad.util;

import java.io.IOException;

import jakarta.faces.annotation.FacesConfig;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

@WebFilter(urlPatterns="*")
public class EncodingFilter implements Filter {

	private static final String ENCODING = "encoding";
	private String encoding = "UTF-8";
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (StringUtils.isNotBlank(filterConfig.getInitParameter(ENCODING))) {
			encoding = filterConfig.getInitParameter(ENCODING);
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;
		
		if (StringUtils.isBlank(req.getCharacterEncoding())) {
			req.setCharacterEncoding(encoding);
		}
		
		chain.doFilter(request, response);
		
		if (StringUtils.isBlank(res.getCharacterEncoding())) {
			res.setCharacterEncoding(encoding);
		}
	}

	@Override
	public void destroy() {
	}
	
}
