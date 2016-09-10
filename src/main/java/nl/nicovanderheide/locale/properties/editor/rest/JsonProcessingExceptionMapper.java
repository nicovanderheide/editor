package nl.nicovanderheide.locale.properties.editor.rest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class JsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException> {

	@Autowired
	HttpServletRequest request;

	private static final Logger log = LoggerFactory.getLogger(JsonProcessingExceptionMapper.class);

	private static final String errorLogger = "ip: %s, method: %s, url: %s, query: %s";
	
	@Override
	public Response toResponse(JsonProcessingException exception) {
		String requestInfo = String.format(errorLogger, 
				request.getRemoteAddr(),
				request.getMethod(),
				(null != request.getRequestURL() ? request.getRequestURL() : "n/a"),
				(null != request.getQueryString() ? request.getQueryString() : "n/a"));

		log.error("JSON processing exception occurred, Request information: " + requestInfo, exception);

		Map<String, String> errors = new HashMap<>();
		errors.put("errors", "invalid.json");

		return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
	}
}
