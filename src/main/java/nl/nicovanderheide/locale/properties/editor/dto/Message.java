package nl.nicovanderheide.locale.properties.editor.dto;

import java.util.Locale;

public class Message implements Comparable<Message>{
	public static final Locale DEFAULT_LOCALE = new Locale("_default");
	
	public Message(){
		
	}
	public Message(String key, String value) {
		setKey(key);
		setValue(value);
		setUnchangedValue(value);
	}
	
	public Message(String key, String value, Locale locale) {
		setKey(key);
		setValue(value);
		setUnchangedValue(value);
		setLocale(locale);
	}

	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	private String unchangedValue;
	
	private String value;
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public Locale getLocale() {
		return (locale == null || locale.toString().contains("DEFAULT") || locale.toString().contains("default") ? DEFAULT_LOCALE :locale);
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	private Locale locale = null;

	@Override
	public String toString() {
		return "Message [key=" + key + ", value=" + value + ", locale=" + locale + "]";
	}
	@Override
	public int compareTo(Message o) {
		int returnValue = this.getLocale().toString().compareTo(o.getLocale().toString());
		
		if (returnValue==0) {
			returnValue = this.getKey().compareTo(o.getKey());
		}
		
		return returnValue;
	}
	public String getUnchangedValue() {
		return unchangedValue;
	}
	public void setUnchangedValue(String unchangedValue) {
		this.unchangedValue = unchangedValue;
	}
	
	
}
