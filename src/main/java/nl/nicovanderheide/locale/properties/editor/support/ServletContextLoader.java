package nl.nicovanderheide.locale.properties.editor.support;

import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component
public class ServletContextLoader  implements ServletContextAware {
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		@SuppressWarnings("unchecked")
		Enumeration<String> parameters = servletContext.getInitParameterNames();
		while (parameters.hasMoreElements()) {
			String param = parameters.nextElement();

			LoadedProperties.addProperty(param, servletContext.getInitParameter(param));
		}
	}
}
