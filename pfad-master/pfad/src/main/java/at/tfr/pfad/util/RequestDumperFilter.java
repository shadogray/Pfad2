package at.tfr.pfad.util;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@WebFilter(urlPatterns="*")
public class RequestDumperFilter implements Filter {
	
	private Logger log = Logger.getLogger(getClass());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;
		
		try {
			if (log.isDebugEnabled()) {
				req.getParameterMap().entrySet().forEach(e -> {
					log.debug("Request Param: " + e.getKey() + " : " 
							+ Stream.of(e.getValue()).map(s -> StringUtils.abbreviate(s, 30)).collect(Collectors.joining()));
				});
			}
		} catch (Throwable t) {
			log.warn("cannot handle request: " + t);
		}

        try {
            chain.doFilter(request, response);
        } catch (Throwable t) {
            log.warn("cannot handle request: " + ((HttpServletRequest)request).getRequestURI(), t);
        }
	}

	@Override
	public void destroy() {
	}
}
