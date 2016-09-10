package nl.nicovanderheide.locale.properties.editor.support;

import org.springframework.core.io.ResourceLoader;

public class WebDavResourceLoader implements ResourceLoader {

	private static String webdavUrl = null;
	private static String user = null;
	private static String password = null;

	@Override
	public WebDavResource getResource(String location) {
		if (location.contains(":")) {
			return new WebDavResource(webdavUrl, user, password, location.substring(location.indexOf(":") + 1));
		} else {
			return new WebDavResource(webdavUrl, user, password, location);
		}
	}

	@Override
	public ClassLoader getClassLoader() {
		return WebDavResourceLoader.class.getClassLoader();
	}

	public String getWebdavUrl() {
		return webdavUrl;
	}

	public void setWebdavUrl(String webdavUrl) {
		if (webdavUrl.endsWith("/")) {
			WebDavResourceLoader.webdavUrl = webdavUrl;
		} else {
			WebDavResourceLoader.webdavUrl = webdavUrl + "/";
		}
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		WebDavResourceLoader.user = user;
	}

	public String getPassword() {
		return "******";
	}

	public void setPassword(String password) {
		WebDavResourceLoader.password = password;
	}
}