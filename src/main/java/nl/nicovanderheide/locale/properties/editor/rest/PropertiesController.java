package nl.nicovanderheide.locale.properties.editor.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.security.RolesAllowed;
import javax.naming.AuthenticationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang.LocaleUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import nl.nicovanderheide.locale.properties.editor.dto.Language;
import nl.nicovanderheide.locale.properties.editor.dto.Message;
import nl.nicovanderheide.locale.properties.editor.dto.ResponseMessage;
import nl.nicovanderheide.locale.properties.editor.service.impl.InMemoryMessagesService;
import nl.nicovanderheide.locale.properties.editor.support.WebDavResource;
import nl.nicovanderheide.locale.properties.editor.support.ZipCreator;
import nl.nicovanderheide.locale.properties.editor.support.ZipFileEntry;

@Path("message")
public class PropertiesController {
	
	private String getUser() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
	
	@Path("user")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String user() {
		return getUser();
	}
	
	@Path("customers")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<String> customers() {
		return InMemoryMessagesService.getService(getUser()).getCustomers();
	}
	
	@Path("locales")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ROLE_SUPER_ADMIN" })
	public Set<Language> locales() throws AuthenticationException {
		Set<Language> locales = new TreeSet<>();
		for (Object object : LocaleUtils.availableLocaleSet()) {
			String key = object.toString();
			int len= key.length();
			if (len == 2) {
				locales.add(new Language(key, LocaleUtils.toLocale(key).getDisplayName()));
			}
		}
		return locales;
	}
	
	

	@Path("load/{customer}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Set<String> getMessages(@PathParam("customer") String customer) {
		return InMemoryMessagesService.getService(getUser()).getAll(customer);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Set<String> getMessages() {
		return InMemoryMessagesService.getService(getUser()).getAll();
	}

	
	@Path("fill")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Message> getMessagesLocale(@QueryParam("key") String key) {
		return InMemoryMessagesService.getService(getUser()).get(key);
	}

	@Path("add")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ROLE_SUPER_ADMIN" })
	public ResponseMessage add(Message message) throws AuthenticationException {
		InMemoryMessagesService.getService(getUser()).add(message);
		return new ResponseMessage(ResponseMessage.Type.success, "messageAdded");
	}

	@Path("reset")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Message reset(Message message) {
		return InMemoryMessagesService.getService(getUser()).reset(message);
	}
	
	@Path("update")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ROLE_SUPER_ADMIN", "ROLE_ADMIN" })
	public ResponseMessage update(Message message) throws AuthenticationException {
		InMemoryMessagesService.getService(getUser()).update(message);
		return new ResponseMessage(ResponseMessage.Type.success, "messageUpdated");
	}
	
	@Path("delete")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ROLE_SUPER_ADMIN" })
	public ResponseMessage delete(@QueryParam("key") String key) throws AuthenticationException {
		InMemoryMessagesService.getService(getUser()).delete(key);
		return new ResponseMessage(ResponseMessage.Type.success, "messageDeleted");
	}
	
	@Path("addLocale")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ROLE_SUPER_ADMIN" })
	public ResponseMessage addLocale(String locale) throws IOException, AuthenticationException {
		InMemoryMessagesService.getService(getUser()).add(LocaleUtils.toLocale(locale));
		
		return new ResponseMessage(ResponseMessage.Type.success, "localeAdded");
	}
	
	@Path("deleteLocale")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ROLE_SUPER_ADMIN" })
	public ResponseMessage deleteLocale(@QueryParam("locale") String locale) throws AuthenticationException {
		InMemoryMessagesService.getService(getUser()).delete(LocaleUtils.toLocale(locale));
		return new ResponseMessage(ResponseMessage.Type.success, "localeDeleted");
	}
	
	@Path("storeProperties")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "ROLE_SUPER_ADMIN", "ROLE_ADMIN" })
	public ResponseMessage storeProperties() throws IOException, AuthenticationException {
		InMemoryMessagesService.getService(getUser()).storeProperties();
		return new ResponseMessage(ResponseMessage.Type.success, "propertiesStored");
	}
	
	@Path("searchValue")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Set<String> searchValue(String value) {
		return InMemoryMessagesService.getService(getUser()).searchValue(value);
	}
	
	@GET
	@Path("export")
	@Produces("application/zip")
	@RolesAllowed({ "ROLE_SUPER_ADMIN", "ROLE_ADMIN" })
	public Response export() throws IOException {
		final List<ZipFileEntry> files = new ArrayList<>();
		final ZipCreator zc = new ZipCreator();
		
		for (WebDavResource resource : InMemoryMessagesService.getService(getUser()).downloadProperties()) {
			files.add(new ZipFileEntry(resource.getFilename(), resource.getInputStream()));
		}

		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				zc.createZip(files, output);
			}
		};

		return Response.ok(stream)
				.header("X-Frame-Options", "SAMEORIGIN")
				.header("content-disposition", "attachment; filename = \"" + InMemoryMessagesService.getService(getUser()).getCustomer() + ".zip\"")
				.build();
	}
}
