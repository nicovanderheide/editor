package nl.nicovanderheide.locale.properties.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

public class UploadConfigurationTest {

	private static Logger LOG = LoggerFactory.getLogger(UploadConfigurationTest.class);

	private static final String TEMPLATE_URL = "%s/%s";

	private static String url, user, password;
	
	private static FilenameFilter TEMPLATE_FILTER = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith("properties");
		}
	};
	
	@Test
	public void uploadConfiguration() {

		LOG.info("UploadConfiguration start");
		LOG.info("");
		
		url = System.getProperty("webdav.url", "http://localhost:18080/repository/default/");
		user = System.getProperty("webdav.user", "admin");
		password = System.getProperty("webdav.password", "admin");

		try {
			Sardine webdavResource = SardineFactory.begin(user, password);
			LOG.info("Start cleanup");
			for (DavResource resource : webdavResource.list(url)) {
				if (webdavResource.exists(url + resource.getName())) {
					webdavResource.delete(url + resource.getName());
					LOG.info("\tRemoved: {}", resource.getName());
				}
			}
			LOG.info("Cleanup of {} finished", url);
			LOG.info("");
						
			uploadFiles(webdavResource, new File("install").listFiles());
			
			LOG.info("UploadConfiguration end");
		} catch (IOException e) {
			LOG.error("Unable to get resource", e);
		}
	}
	
	private static void uploadFiles(Sardine webdavResource, File... files) throws IOException {
		for (File directory : files) {
			if (directory.isDirectory()) {
				LOG.info(directory.getAbsolutePath());
				int templates = 0;
				for (File template : directory.listFiles(TEMPLATE_FILTER)) {
					if (templates == 0) {
						webdavResource.createDirectory(url + directory.getName());
						LOG.info("Created directory: {}", directory.getName());
					}
					String templateUrl = String.format(TEMPLATE_URL, directory.getName(), template.getName());
					webdavResource.put(url + templateUrl, new FileInputStream(template));
					LOG.info("\tUploaded: {}", templateUrl);
					templates++;
				}
				if (templates > 0) {
					LOG.info("Uploaded: {} templates for {}", templates, directory.getName());
					LOG.info("");
				}
			} else if (directory.getName().endsWith("properties") || directory.getName().endsWith("xslt")) {
				webdavResource.put(url + directory.getName(), new FileInputStream(directory));
				LOG.info("Uploaded: {}", directory.getName());
				LOG.info("");
			}
		}
	}
}

