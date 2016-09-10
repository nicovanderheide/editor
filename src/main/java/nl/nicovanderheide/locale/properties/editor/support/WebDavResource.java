package nl.nicovanderheide.locale.properties.editor.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.github.sardine.impl.SardineException;

public class WebDavResource implements Resource {
	private final static Logger LOG = LoggerFactory.getLogger(WebDavResource.class);

	private String url;
	private String name;
	private String password;
	private String user;

	public WebDavResource(String url, String user, String password, String name) {
		this.url = url;
		this.name = name;
		this.password = password;
		this.user = user;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		Sardine sardine;
		try {
			sardine = SardineFactory.begin(user, password);
			return sardine.get(url + name);
		} catch (SardineException e) {
			throw new IOException("Unable to get resource from webdav [" + name + "]", e);
		}
	}

	@Override
	public boolean exists() {
		Sardine sardine;
		try {
			sardine = SardineFactory.begin(user, password);
			return sardine.exists(url + name);
		} catch (IOException e) {
			LOG.debug("Unable to get resource from webdav [" + name + "]", e);
			return false;
		}
	}

	@Override
	public boolean isReadable() {
		Sardine sardine;
		try {
			sardine = SardineFactory.begin(user, password);
			return sardine.exists(url + name);
		} catch (IOException e) {
			LOG.debug("Unable to get resource from webdav [" + name + "]", e);
			return false;
		}
	}

	@Override
	public boolean isOpen() {
		return false;
	}

	@Override
	public URL getURL() throws IOException {
		return new URL(url + name);
	}

	@Override
	public URI getURI() throws IOException {
		try {
			return new URI(url + name);
		} catch (URISyntaxException e) {
			throw new IOException("Invalid url", e);
		}
	}

	@Override
	public File getFile() throws IOException {
		return null;
	}

	@Override
	public long contentLength() throws IOException {
		Sardine sardine;
		try {
			sardine = SardineFactory.begin(user, password);
			List<DavResource> resources = sardine.list(url + name);
			if (resources.size() == 1) {
				return resources.get(0).getContentLength();
			}
		} catch (SardineException e) {
			LOG.debug("Unable to get resource from webdav [" + name + "]", e);
		}

		return 0;
	}

	@Override
	public long lastModified() throws IOException {
		Sardine sardine;
		try {
			sardine = SardineFactory.begin(user, password);
			List<DavResource> resources = sardine.list(url + name);
			if (resources.size() == 1) {
				return resources.get(0).getModified().getTime();
			}
		} catch (SardineException e) {
			LOG.debug("Unable to get resource from webdav [" + name + "]", e);
		}

		return 0;
	}

	@Override
	public Resource createRelative(String relativePath) throws IOException {
		String newUrl = "";
		String[] parts = relativePath.split("[/]");
		String[] parts2 = name.split("[/]");
		int back = 0;
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].equals("..")) {
				back++;
			}
		}
		for (int i = 0; i < parts2.length - back; i++) {
			newUrl += parts2[i];
		}
		if (relativePath.contains("/")) {
			newUrl += relativePath.substring(relativePath.lastIndexOf("/") + 1);
		}

		return new WebDavResource(url, user, password, newUrl);
	}

	@Override
	public String getFilename() {
		if (name.contains("/")) {
			return name.substring(name.lastIndexOf("/") + 1);
		} else {
			return name;
		}
	}

	@Override
	public String getDescription() {
		Sardine sardine;
		try {
			sardine = SardineFactory.begin(user, password);
			List<DavResource> resources = sardine.list(url + name);
			if (resources.size() > 1) {
				// resource is a folder
				StringBuilder sb = new StringBuilder();
				sb.append("name[").append(name).append("]");
			} else {
				// resource is a file
				StringBuilder sb = new StringBuilder();
				sb.append("name[").append(name);
				sb.append("]created[").append(resources.get(0).getCreation());
				sb.append("]modified[").append(resources.get(0).getModified());
				sb.append("]content type[").append(resources.get(0).getContentType()).append("]");
			}
		} catch (IOException e) {
			LOG.debug("Unable to get resource from webdav [" + name + "]", e);
		}
		return name;
	}

	public Set<String> list() {
		Set<String> names = new HashSet<>();
		Sardine sardine;
		try {
			sardine = SardineFactory.begin(user, password);
			List<DavResource> resources = sardine.list(url + name);
			
			for (DavResource davResource : resources) {
				names.add(davResource.getName());
			}
			
		} catch (IOException e) {
			LOG.debug("Unable to get resource from webdav [" + name + "]", e);
		}
		return names;
	}
	
	public void removeFile() {
		backup();
		
		Sardine sardine;
		try {
			sardine = SardineFactory.begin(user, password);
			sardine.delete(url + name);
		} catch (IOException e) {
			LOG.error("Unable to delete location: ["+url+name+"]", e);
		}
	}
	
	private static SimpleDateFormat sf = new SimpleDateFormat("yyyymmdd_HHmm");
	
	private void backup() {
		String sourceUrl = url + name;
		
		String destinationUrl = url + "backup/" + sf.format(new Date()) + "."+ name;
		Sardine sardine;
		try {
			sardine = SardineFactory.begin(user, password);
			if (sardine.exists(sourceUrl)) {
				
				if (!sardine.exists(url + "backup/")) {
					sardine.createDirectory(url + "backup/");
				}
				sardine.copy(sourceUrl, destinationUrl);
				LOG.debug("backup: {} to {}", sourceUrl, destinationUrl);
			}
		} catch (IOException e) {
			LOG.error("Unable to backup file: {} to {}", sourceUrl, destinationUrl, e);
		}
	}
	
	public void writePropertiesToFile(InputStream dataStream) {
		backup();
		
		Sardine sardine;
		try {
			sardine = SardineFactory.begin(user, password);
			String resourcePath = url + name;
			
			LOG.debug("exists: ", sardine.exists(resourcePath));
			
			sardine.put(resourcePath, dataStream, "text/plain");
			LOG.debug("put: ", resourcePath);
			
			sardine.shutdown();
		} catch (Exception e) {
			LOG.error("Unable to write datastream to location: [{}]", url+name, e);
		}
	}
}
