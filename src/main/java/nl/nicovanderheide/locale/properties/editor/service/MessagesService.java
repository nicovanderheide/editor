package nl.nicovanderheide.locale.properties.editor.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import nl.nicovanderheide.locale.properties.editor.dto.Message;

/**
 * Service to handle MessagesService.
 */
public interface MessagesService {

	public Set<String> getCustomers();

	public Set<String> getAll(String customer);

	public Set<String> getAll();

	public Collection<Message> get(String key);

	public void add(Message message);

	public Message reset(Message message);
	
	public void update(Message message);

	public void delete(String key);
	
	public void storeProperties() throws IOException;

	public void add(Locale locale) throws IOException;

	public void delete(Locale locale);

	public Set<String> searchValue(String value);
}