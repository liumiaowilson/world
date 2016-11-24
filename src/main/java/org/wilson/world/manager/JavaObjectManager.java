package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.java.ActiveObject;
import org.wilson.world.java.JavaClass;
import org.wilson.world.java.JavaClassListener;
import org.wilson.world.java.JavaObject;

public class JavaObjectManager implements JavaClassListener {
	private static final Logger logger = Logger.getLogger(JavaObjectManager.class);
	
	private static JavaObjectManager instance;
	
	private Map<Integer, JavaObject> objects = new HashMap<Integer, JavaObject>();
	private Map<String, JavaObject> cls2ObjMap = new HashMap<String, JavaObject>();
	
	private JavaObjectManager() {
		JavaClassManager.getInstance().addJavaClassListener(this);
	}
	
	public static JavaObjectManager getInstance() {
		if(instance == null) {
			instance = new JavaObjectManager();
		}
		
		return instance;
	}
	
	@SuppressWarnings("rawtypes")
	private Object newInstance(Class clazz) {
		if(clazz == null) {
			return null;
		}
		
		try {
			Object obj = clazz.newInstance();
			return obj;
		}
		catch(Exception e) {
			logger.error(e);
		}
		
		return null;
	}

	@Override
	public void created(JavaClass javaClass) {
		if(javaClass != null) {
			if(!ActiveObject.class.isAssignableFrom(javaClass.clazz)) {
				//we only load active objects
				return;
			}
			
			Object obj = this.newInstance(javaClass.clazz);
			if(obj == null) {
				logger.warn("Failed to create java object for class [" + javaClass.name + "]");
				return;
			}
			
			JavaObject javaObject = new JavaObject();
			javaObject.id = javaClass.id;
			String name = javaClass.clazz.getSimpleName();
			name = name.substring(0, 1).toLowerCase() + name.substring(1) + "AO";
			javaObject.name = name;
			javaObject.object = obj;
			this.objects.put(javaObject.id, javaObject);
			
			this.cls2ObjMap.put(javaClass.name, javaObject);
		}
	}

	@Override
	public void removed(JavaClass javaClass) {
		if(javaClass != null) {
			this.objects.remove(javaClass.id);
			
			this.cls2ObjMap.remove(javaClass.name);
		}
	}
	
	public List<JavaObject> getJavaObjects() {
		List<JavaObject> javaObjects = new ArrayList<JavaObject>();
		for(JavaObject javaObject : this.objects.values()) {
			javaObject = this.loadJavaObject(javaObject);
			javaObjects.add(javaObject);
		}
		
		return javaObjects;
	}
	
	public JavaObject getJavaObject(int id) {
		JavaObject javaObject = this.objects.get(id);
		if(javaObject != null) {
			javaObject = this.loadJavaObject(javaObject);
		}
		
		return javaObject;
	}
	
	@SuppressWarnings("rawtypes")
	private JavaObject loadJavaObject(JavaObject javaObject) {
		if(javaObject == null) {
			return null;
		}
		
		Class clazz = javaObject.object.getClass();
		Class [] interfaces = clazz.getInterfaces();
		StringBuilder sb = new StringBuilder();
		for(Class itf : interfaces) {
			if(ActiveObject.class != itf) {
				sb.append(itf.getCanonicalName()).append(" ");
			}
		}
		javaObject.interfaces = sb.toString();
		
		return javaObject;
	}
	
	public JavaObject getJavaObject(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		for(JavaObject javaObject : this.objects.values()) {
			if(name.equals(javaObject.name)) {
				return javaObject;
			}
		}
		
		return null;
	}
	
	public JavaObject getJavaObjectByClassName(String className) {
		if(StringUtils.isBlank(className)) {
			return null;
		}
		
		return this.cls2ObjMap.get(className);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JavaObject getJavaObjectOfClass(Class clazz) {
		if(clazz == null) {
			return null;
		}
		
		for(JavaObject javaObject : this.objects.values()) {
			if(clazz.isAssignableFrom(javaObject.object.getClass())) {
				return javaObject;
			}
		}
		
		return null;
	}
}
