package nl.nicovanderheide.locale.properties.editor.support;

import java.util.Properties;

public class LoadedProperties {
	private static Properties properties = new Properties();
	
	public static String getProperty(String key, String defaultValue) {
		Object object = properties.get(key);
		return (object!=null ? object : defaultValue).toString();
	}

	public static void addProperty(String key, String value) {
		properties.put(key, value);
	}
}