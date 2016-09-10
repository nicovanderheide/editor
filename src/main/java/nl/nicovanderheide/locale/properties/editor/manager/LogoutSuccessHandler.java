package nl.nicovanderheide.locale.properties.editor.manager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import nl.nicovanderheide.locale.properties.editor.service.impl.InMemoryMessagesService;

public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

	   // Just for setting the default target URL
	   public LogoutSuccessHandler(String defaultTargetURL) {
	        this.setDefaultTargetUrl(defaultTargetURL);
	   }

	   @Override
	   public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
	        super.onLogoutSuccess(request, response, authentication);
	        if (null != authentication) {
	        	InMemoryMessagesService.cleanService(authentication.getName());
	        }
	   }
	}
