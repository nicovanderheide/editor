package nl.nicovanderheide.locale.properties.editor.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import nl.nicovanderheide.locale.properties.editor.dto.Message;
import nl.nicovanderheide.locale.properties.editor.service.MessagesService;
import nl.nicovanderheide.locale.properties.editor.support.LoadedProperties;
import nl.nicovanderheide.locale.properties.editor.support.Time;
import nl.nicovanderheide.locale.properties.editor.support.WebDavReloadableResourceBundleMessageSource;
import nl.nicovanderheide.locale.properties.editor.support.WebDavResource;
import nl.nicovanderheide.locale.properties.editor.support.WebDavResourceLoader;

/**
 * Simple Set InMemoryKeyValueService.
 */
public class InMemoryMessagesService implements MessagesService {
	
	private static Map<String, InMemoryMessagesService> serviceMap = new HashMap<>();
	
	public static InMemoryMessagesService getService(String user) {
		if (!serviceMap.containsKey(user)) {
			serviceMap.put(user, new InMemoryMessagesService());
		}
		return serviceMap.get(user);
	}
	
	public static void cleanService(String user) {
		if (serviceMap.containsKey(user)) {
			serviceMap.remove(user);
		}
	}
	
	
	private WebDavResourceLoader webDavResourceLoader;
	private WebDavReloadableResourceBundleMessageSource ms;
	
	private void loadCustomer(String customer) {
		this.customer = customer;
		ms = new WebDavReloadableResourceBundleMessageSource();
		ms.setBasenames(new String[] {  "classpath:messages", "classpath:labels",  });
		ms.setDefaultEncoding("UTF-8");
		ms.setUseCodeAsDefaultMessage(true);
		ms.setAlwaysUseMessageFormat(true);
		ms.setFallbackToSystemLocale(false);
		ms.setCacheSeconds(1);
		
		ms.setResourceLoader(new DefaultResourceLoader());
		
		
		webDavResourceLoader = new WebDavResourceLoader();
		webDavResourceLoader.setUser(LoadedProperties.getProperty("webdav.user","root"));
		webDavResourceLoader.setPassword(LoadedProperties.getProperty("webdav.password","root"));
		webDavResourceLoader.setWebdavUrl(LoadedProperties.getProperty("webdav.url", "http://localhost:18080/repository/default/") + customer);
		
		
		ms.setWebDavResourceLoader(webDavResourceLoader);
		LOG.debug("switchMessageSource: {}", customer);
	}
	

	private final static Logger LOG = LoggerFactory.getLogger(InMemoryMessagesService.class);

	private Set<String> findLocaleSet() {
		LOG.trace("findLocaleSet: {}", webDavResourceLoader.getWebdavUrl());
		Set<String> languageTags = new HashSet<>();
		languageTags.add("default");
		LOG.trace("added: default");

		WebDavResource resource = webDavResourceLoader.getResource(".");
		for (String file : resource.list()) {
			if (file.endsWith("properties")) {
				try {
					if (file.contains("_")) {
						String languageTag = file.substring(file.indexOf("_") + 1, file.indexOf("."));
						languageTags.add(languageTag);
						LOG.debug("added: {}", languageTag);
					} else {
						fileBasename = file.substring(0, file.indexOf("."));
					}
				} catch (IndexOutOfBoundsException e) {/* ignore this error */
				}
			}
		}

		return languageTags;

	}

	private String fileBasename = null;
	private Map<Locale, Map<String, String>> map = new HashMap<>();
	private String customer;

	public  Properties getProperties() {
		return getProperties(LocaleContextHolder.getLocale());
	}

	public  Properties getProperties(Locale locale) {
		return ((WebDavReloadableResourceBundleMessageSource)ms).getMergedProperties(locale).getProperties();
	}
	
	private Locale locale(String languageTag) {
		Locale locale = null;
		try {
			locale = LocaleUtils.toLocale(languageTag);
		} catch (Exception e) {
			locale = Message.DEFAULT_LOCALE;
		}
		return locale;
	}
	
	@Override
	public Set<String> getAll(String customer) {

		loadCustomer(customer);

		map = new HashMap<>();
		Set<String> languages = findLocaleSet();
		for (String languageTag : languages) {
			Locale locale= locale(languageTag);

			for (Entry<Object, Object> entry : getProperties(locale).entrySet()) {
				if (!map.containsKey(locale)) {
					map.put(locale, new HashMap<String, String>());
				}
				map.get(locale).put(entry.getKey().toString(), entry.getValue().toString());
			}
		}

		return getAll();
	}

	@Override
	public Set<String> getAll() {
		Set<String> keys = new TreeSet<>();
		for (String languageTag : findLocaleSet()) {
			Locale locale = locale(languageTag);
			for (String key : map.get(locale).keySet()) {
				if (!keys.contains(key)) {
					keys.add(key);
				}
			}
		}
		
		
		LOG.debug("get all keys");
		return keys;
	}

	@Override
	public Set<String> searchValue(String value) {
		long begin = System.currentTimeMillis();
		Set<String> keys = new TreeSet<>();
		
		for (Locale locale : map.keySet()) {
			for (Entry<String, String> entry : map.get(locale).entrySet()) {
				if (entry.getValue().toLowerCase().contains(value.toLowerCase())) {
					keys.add(entry.getKey());
				}
			}
		}
		
		LOG.debug("get all keys containing the value {} in any language found in {}", value, Time.takenBetween(begin));
		return keys;
	}
	
	@Override
	public Set<String> getCustomers() {

		WebDavResourceLoader webDavResourceLoader = new WebDavResourceLoader();
		webDavResourceLoader.setUser(LoadedProperties.getProperty("webdav.user", "root"));
		webDavResourceLoader.setPassword(LoadedProperties.getProperty("webdav.password", "root"));
		webDavResourceLoader.setWebdavUrl(LoadedProperties.getProperty("webdav.url", "http://127.0.0.1:18080/jackrabbit/repository/default/files/"));

		Set<String> customers = new HashSet<>();
		for (String customer : webDavResourceLoader.getResource(".").list()) {
			for (String allowedName : LoadedProperties.getProperty("customer.names","klantnaam").split("[;, ]")) {
				if (allowedName.equals(customer)) {
					customers.add(customer);
				}
			}
		}
		LOG.debug("get all customers");
		return customers;
	}

	@Override
	public Collection<Message> get(final String key) {
		Set<Message> messages = new TreeSet<>();
		for (Locale locale : map.keySet()) {
			messages.add(new Message(key, map.get(locale).get(key), locale));
		}
		LOG.debug("get all translations for: {}", key);
		return messages;
	}

	@Override
	public void update(Message message) {
		map.get(message.getLocale()).put(message.getKey(), message.getValue());
		LOG.debug("update: {}: {}={}", message.getLocale(), message.getKey(), message.getValue());
	}

	@Override
	public void add(Message message) {
		if (!map.get(message.getLocale()).containsKey(message.getKey())) {
			map.get(message.getLocale()).put(message.getKey(), message.getValue());
			LOG.info("add: {}={}", message.getKey(), message.getValue());
		} else {
			LOG.info("not added: {}={}", message.getKey(), message.getValue());
		}
	}

	@Override
	public void delete(String key) {
		for (Locale locale : map.keySet()) {
			map.get(locale).remove(key);
			LOG.info("delete: {}: {}", locale, key);
		}
	}

	@Override
	public void add(Locale locale) throws IOException {
		if (map.containsKey(locale) == false) {
			map.put(locale, new HashMap<String, String>());
			Map<String, String> clone = map.get(Message.DEFAULT_LOCALE);
			for (Entry<String, String> entry : clone.entrySet()) {
				map.get(locale).put(entry.getKey(), entry.getValue() + " [translate]");
			}
			LOG.info("add locale: {}", locale);
			writeLocaleFile(locale);
		}
	}

	@Override
	public void delete(Locale locale) {
		String fileName = fileBasename + (locale != null && locale.toString().length() != 0 ? "_" + locale.toString() : "") + ".properties";
		getWebDavResourceLoader().getResource(fileName).removeFile();
		LOG.info("removed: {}", fileName);
	}

	@Override
	public void storeProperties() throws IOException {
		for (Locale locale : map.keySet()) {
			writeLocaleFile(locale);
		}
	}

	@Override
	public Message reset(Message message) {
		message.setValue(map.get(message.getLocale()).get(message.getKey()));
		LOG.debug("reset: {}: {}={}", message.getLocale(), message.getKey(), message.getValue());
		return message;
	}

	private void writeLocaleFile(Locale locale) throws IOException {
		long begin = System.currentTimeMillis();
		String fileName = fileBasename + (locale != null && !locale.toString().contains("default") ? "_" + locale.toString() : "") + ".properties";

		StringBuilder comments = new StringBuilder();
		comments.append(String.format("  %s written.\r\n", fileName));

		boolean changed = false;
		Resource resource = getWebDavResourceLoader().getResource(fileName);
		boolean exists = resource.exists();
		
		Properties oldProperties = new SortedProperties();
		oldProperties.load(getWebDavResourceLoader().getResource(fileBasename + ".properties").getInputStream());
		Properties newProperties = new SortedProperties();
		for (Entry<String, String> entry : map.get(locale).entrySet()) {
			if (exists && (null != oldProperties.getProperty(entry.getKey()) && !entry.getValue().equalsIgnoreCase(oldProperties.getProperty(entry.getKey())))) {
				if (changed == false) {
					comments.append("\r\n");
					comments.append("  The following changes were recorded:");
					comments.append("\r\n");
				}
				comments.append(String.format("    %s changed from: '%s' to '%s'\r\n", entry.getKey(), oldProperties.getProperty(entry.getKey()), entry.getValue()));
				changed = true;
			}
			newProperties.put(entry.getKey(), entry.getValue());
		}

		for (Object key : newProperties.keySet()) {
			if (!oldProperties.containsKey(key)) {
				changed = true;
			}
		}
		for (Object key : oldProperties.keySet()) {
			if (!newProperties.containsKey(key)) {
				changed = true;
			}
		}

		if (changed || !exists) {
			StringWriter writer = new StringWriter();
			newProperties.store(writer, comments.toString());

			getWebDavResourceLoader().getResource(fileName).writePropertiesToFile(new ByteArrayInputStream(writer.toString().getBytes("UTF-8")));
			LOG.info(	"all-changes: {} stored  in {}", fileName, Time.takenBetween(begin));
		} else {
			LOG.debug(	"not-changed: {} checked in {}", fileName, Time.takenBetween(begin));
		}
	}

	public WebDavResourceLoader getWebDavResourceLoader() {
		return webDavResourceLoader;
	}

	public void setWebDavResourceLoader(WebDavResourceLoader webDavResourceLoader) {
		this.webDavResourceLoader = webDavResourceLoader;
	}

	public class SortedProperties extends Properties {
		private static final long serialVersionUID = -8201897245408022900L;

		
		public synchronized Enumeration<Object> keys() {
			Enumeration<Object> keysEnum = super.keys();
			Set<Object> keySet = new TreeSet<>();
			while (keysEnum.hasMoreElements()) {
				keySet.add(keysEnum.nextElement());
			}
			return Collections.enumeration(keySet);
		}

	}
	
	public Set<WebDavResource> downloadProperties() throws IOException {
		Set<WebDavResource> resources= new HashSet<WebDavResource>();
		for (Locale locale : map.keySet()) {
			resources.add(downloadLocaleFile(locale));
		}
		return resources;
	}
	
	private WebDavResource downloadLocaleFile(Locale locale) {
		String fileName = fileBasename + (locale != null && !locale.toString().contains("default") ? "_" + locale.toString() : "") + ".properties";
		boolean exists = getWebDavResourceLoader().getResource(fileName).exists();
		if (exists) {
			 return getWebDavResourceLoader().getResource(fileName);
		}
		return null;
	}
	
	public String getCustomer() {
		return customer;
	}
}
