package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.form.AbstractForm;
import org.wilson.world.form.DemoForm;
import org.wilson.world.form.Form;
import org.wilson.world.java.JavaExtensionListener;

public class FormManager implements JavaExtensionListener<AbstractForm> {
	private static FormManager instance;
	
	private static int GLOBAL_ID = 1;
	
	private Map<String, Form> forms = new HashMap<String, Form>();
	
	private FormManager() {
		this.loadSystemForms();
		
		ExtManager.getInstance().addJavaExtensionListener(this);
	}
	
	private void loadSystemForms() {
		this.addForm(new DemoForm());
	}
	
	public static FormManager getInstance() {
		if(instance == null) {
			instance = new FormManager();
		}
		
		return instance;
	}
	
	public void addForm(Form form) {
		if(form != null && form.getName() != null) {
			form.setId(GLOBAL_ID++);
			this.forms.put(form.getName(), form);
		}
	}
	
	public void removeForm(Form form) {
		if(form != null && form.getName() != null) {
			this.forms.remove(form.getName());
		}
	}
	
	public List<Form> getForms() {
		return new ArrayList<Form>(this.forms.values());
	}
	
	public Form getForm(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.forms.get(name);
	}
	
	public Form getForm(int id) {
		for(Form form : this.forms.values()) {
			if(form.getId() == id) {
				return form;
			}
		}
		
		return null;
	}

	@Override
	public Class<AbstractForm> getExtensionClass() {
		return AbstractForm.class;
	}

	@Override
	public void created(AbstractForm t) {
		this.addForm(t);
	}

	@Override
	public void removed(AbstractForm t) {
		this.removeForm(t);
	}
}
