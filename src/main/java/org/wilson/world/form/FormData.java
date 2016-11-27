package org.wilson.world.form;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class FormData {
	private Map<String, Object> data = new HashMap<String, Object>();
	
	public void put(String name, Object value) {
		this.data.put(name, value);
	}
	
	public Object get(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.data.get(name);
	}
	
	public boolean hasInput(String name) {
		if(StringUtils.isBlank(name)) {
			return false;
		}
		
		return this.data.containsKey(name);
	}
	
	public String getString(String name) {
		Object value = this.get(name);
		return String.valueOf(value);
	}
	
	public int getInt(String name) {
		Object value = this.get(name);
		if(value instanceof Integer) {
			return (Integer)value;
		}
		else if(value instanceof String) {
			try {
				return Integer.parseInt((String)value);
			}
			catch(Exception e) {
				return 0;
			}
		}
		else {
			return 0;
		}
	}
	
	public long getLong(String name) {
		Object value = this.get(name);
		if(value instanceof Long) {
			return (Long)value;
		}
		else if(value instanceof String) {
			try {
				return Long.parseLong((String)value);
			}
			catch(Exception e) {
				return 0;
			}
		}
		else {
			return 0;
		}
	}
	
	public double getDouble(String name) {
		Object value = this.get(name);
		if(value instanceof Double) {
			return (Double)value;
		}
		else if(value instanceof String) {
			try {
				return Double.parseDouble((String)value);
			}
			catch(Exception e) {
				return 0;
			}
		}
		else {
			return 0;
		}
	}
	
	public boolean getBoolean(String name) {
		Object value = this.get(name);
		if(value instanceof Boolean) {
			return (Boolean)value;
		}
		else if(value instanceof String) {
			return Boolean.valueOf((String)value);
		}
		else {
			return false;
		}
	}
	
	public File getFile(String name) {
		Object value = this.get(name);
		if(value instanceof File) {
			return (File)value;
		}
		else if(value instanceof String) {
			File file = new File((String)value);
			if(file.exists()) {
				return file;
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
}
