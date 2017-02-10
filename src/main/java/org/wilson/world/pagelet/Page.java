package org.wilson.world.pagelet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Page {
	private Map<String, String> data = new HashMap<String, String>();
	private String next;
	
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
	
	public String getClientScript() {
		StringBuilder sb = new StringBuilder();
		for(Entry<String, String> entry : data.entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue();
			sb.append("var ").append(name).append(" = JSON.parse('").append(value).append("');\n");
		}
		
		return sb.toString();
	}
}
