package nl.nicovanderheide.locale.properties.editor.dto;

public class Language implements Comparable<Language> {

	private String key, value;

	public Language(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public int compareTo(Language o) {
		return this.getKey().compareTo(o.getKey());
	}
}
