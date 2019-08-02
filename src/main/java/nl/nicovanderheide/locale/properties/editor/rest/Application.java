package nl.nicovanderheide.locale.properties.editor.rest;

import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.springframework.web.filter.RequestContextFilter;

public class Application extends ResourceConfig {
	 /**
     * Register JAX-RS application components.
     */
    public Application () {
    	property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
    	property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);
    	register(RolesAllowedDynamicFeature.class, 1);
        register(RequestContextFilter.class);
		/* Instead of JacksonFeature.class so we can do our own JsonProcessingException mapping */
        register(JacksonJaxbJsonProvider.class);
        
        register(PropertiesController.class);

		register(JsonProcessingExceptionMapper.class);
		register(RuntimeExceptionMapper.class);
		
	}
}