package org.wilson.world.pagelet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Page {
	private Map<String, String> data = new HashMap<String, String>();
	private String next;
	private String back;
	
	private String css;
	private String html;
	private String clientCode;
	
	private List<String> styles = new ArrayList<String>();
	private List<String> scripts = new ArrayList<String>();
	
	public void set(String name, String value) {
		data.put(name, value);
	}
	
	public String get(String name) {
		return data.get(name);
	}
	
	public Map<String, String> all() {
		return data;
	}
	
	public void setNext(String next) {
		this.next = next;
	}
	
	public String getNext() {
		return this.next;
	}
	
	public void setBack(String back) {
		this.back = back;
	}
	
	public String getBack() {
		return this.back;
	}
	
	public String getClientScript() {
		StringBuilder sb = new StringBuilder();
		for(Entry<String, String> entry : data.entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue();
			sb.append("var ").append(name).append(" = JSON.parse('").append(value).append("');\n");
		}
		
		return sb.toString();
	}
	
	public void setCss(String css) {
		this.css = css;
	}
	
	public String getCss() {
		return this.css;
	}
	
	public void setHtml(String html) {
		this.html = html;
	}
	
	public String getHtml() {
		return this.html;
	}
	
	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}
	
	public String getClientCode() {
		return this.clientCode;
	}
	
	public List<String> getStyles() {
		return this.styles;
	}
	
	public void addStyle(String style) {
		this.styles.add(style);
	}
	
	public void removeStyle(String style) {
		this.styles.remove(style);
	}
	
	public List<String> getScripts() {
		return this.scripts;
	}
	
	public void addScript(String script) {
		this.scripts.add(script);
	}
	
	public void removeScript(String script) {
		this.scripts.remove(script);
	}
}
