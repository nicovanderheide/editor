package nl.nicovanderheide.locale.properties.editor.support;

import java.io.InputStream;

public class ZipFileEntry {

	private String name;
	private InputStream is;

	public ZipFileEntry(String name, InputStream is) {
		super();
		this.name = name;
		this.is = is;
	}

	public String getName() {
		return name;
	}

	public InputStream getIs() {
		return is;
	}
}