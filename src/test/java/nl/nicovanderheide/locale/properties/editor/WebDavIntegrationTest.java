package nl.nicovanderheide.locale.properties.editor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.LocaleUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.io.DefaultResourceLoader;

import nl.nicovanderheide.locale.properties.editor.dto.Message;
import nl.nicovanderheide.locale.properties.editor.support.WebDavReloadableResourceBundleMessageSource;
import nl.nicovanderheide.locale.properties.editor.support.WebDavResource;
import nl.nicovanderheide.locale.properties.editor.support.WebDavResourceLoader;

public class WebDavIntegrationTest {

	private final static Logger LOG = LoggerFactory.getLogger(WebDavIntegrationTest.class);
	
	@Test
	public void testGetCustomers() {
		WebDavResourceLoader webDavResourceLoader = new WebDavResourceLoader();
		webDavResourceLoader.setUser("admin");
		webDavResourceLoader.setPassword("admin");
		webDavResourceLoader.setWebdavUrl("http://localhost:18080/repository/default/");
		
		WebDavResource resource =(WebDavResource)webDavResourceLoader.getResource(".");
		for (String name : resource.list()) {
			LOG.info(name);		
		}
	}
	
	
	private Set<String> findProperties(WebDavResourceLoader webDavResourceLoader) {
		Set<String> languageTags = new HashSet<>();
		languageTags.add("_DEFAULT");
		WebDavResource resource = webDavResourceLoader.getResource(".");
		for (String file : resource.list()) {
			try {
				String languageTag = file.substring(file.indexOf("_")+1, file.indexOf("."));
				languageTags.add(languageTag);

			} catch (Exception e) {
				LOG.trace(e.getMessage());
			}
		}
		return languageTags;

	}
	
	@Test
	public void testGetMessages() {
		WebDavResourceLoader webDavResourceLoader = new WebDavResourceLoader();
		webDavResourceLoader.setUser("admin");
		webDavResourceLoader.setPassword("admin");
		webDavResourceLoader.setWebdavUrl("http://localhost:18080/repository/default/");
		
		
		WebDavReloadableResourceBundleMessageSource source = new WebDavReloadableResourceBundleMessageSource();
		source.setBasenames(new String[] { "classpath:messages", "classpath:labels",  });
		source.setDefaultEncoding("UTF-8");
		source.setUseCodeAsDefaultMessage(false);
		source.setResourceLoader(new DefaultResourceLoader());
		
		source.setWebDavResourceLoader(webDavResourceLoader);
		
		
		
		Map<Locale, Set<Message>> map = new HashMap<>();
		for (String languageTag : findProperties(webDavResourceLoader)) {
			Locale locale = null;
			try {
				locale = LocaleUtils.toLocale(languageTag);
			} catch (IllegalArgumentException e) {
				locale = new Locale("_default");
			}
			
			for (Entry<Object, Object> entry : getProperties(source, locale).entrySet()) {
				if (!map.containsKey(locale)) {
					map.put(locale, new HashSet<Message>() );
				}
				map.get(locale).add(new Message(entry.getKey().toString(), entry.getValue().toString()));
			}
		}
		
		for (Locale locale : map.keySet()) {
			LOG.info("["+locale.toString()+"]");
			for (Message message : map.get(locale)) {
				LOG.info("{}={}", message.getKey(), message.getValue());
			}
			LOG.info("");
		}
	}
	
	private Properties getProperties(MessageSource ms, Locale locale) {
		return ((WebDavReloadableResourceBundleMessageSource)ms).getMergedProperties(locale).getProperties();
	}
	
}
