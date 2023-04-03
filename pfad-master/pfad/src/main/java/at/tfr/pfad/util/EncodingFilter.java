package at.tfr.pfad.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
