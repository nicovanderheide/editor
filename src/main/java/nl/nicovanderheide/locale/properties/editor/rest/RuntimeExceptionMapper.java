package nl.nicovanderheide.locale.properties.editor.rest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RuntimeExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<RuntimeException> {

	@Autowired
	HttpServletRequest request;

	private static final Logger log = LoggerFactory.getLogger(RuntimeExceptionMapper.class);

	private static final String errorLogger = "ip: %s, method: %s, url: %s, query: %s";

	@Override
	public Response toResponse(RuntimeException exception) {
		String requestInfo = String.format(errorLogger, request.getRemoteAddr(), request.getMethod(), (null != request.getRequestURL() ? request.getRequestURL() : "n/a"),
				(null != request.getQueryString() ? request.getQueryString() : "n/a"));

		log.error("JSON processing exception occurred, Request information: " + requestInfo, exception);

		Map<String, String> errors = new HashMap<>();
		errors.put("errors", "unknown.exception");

		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errors).build();
	}

}
