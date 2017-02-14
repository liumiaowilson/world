package org.wilson.world.manager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.java.ActiveObject;
import org.wilson.world.java.JavaClass;
import org.wilson.world.java.JavaClassListener;
import org.wilson.world.java.JavaObject;
import org.wilson.world.java.JavaObjectInfo;
import org.wilson.world.java.JavaObjectListener;
import org.wilson.world.java.Script;
import org.wilson.world.java.Scriptable;
import org.wilson.world.model.JavaFile;

public class JavaObjectManager implements JavaClassListener {
	private static final Logger logger = Logger.getLogger(JavaObjectManager.class);
	
	private static JavaObjectManager instance;
	
	private Map<Integer, JavaObject> objects = new HashMap<Integer, JavaObject>();
	private Map<String, JavaObject> cls2ObjMap = new HashMap<String, JavaObject>();
	private Map<String, JavaObject> namedObjects = new HashMap<String, JavaObject>();
	
	private List<JavaObjectListener> listeners = new ArrayList<JavaObjectListener>();
	
	private JavaObjectManager() {
		JavaClassManager.getInstance().addJavaClassListener(this);
	}
	
	public static JavaObjectManager getInstance() {
		if(instance == null) {
			instance = new JavaObjectManager();
		}
		
		return instance;
	}
	
	public void addJavaObjectListener(JavaObjectListener listener) {
		if(listener != null) {
			this.listeners.add(listener);
		}
	}
	
	public void removeJavaObjectListener(JavaObjectListener listener) {
		if(listener != null) {
			this.listeners.remove(listener);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object newInstance(Class clazz) {
		if(clazz == null) {
			return null;
		}
		
		try {
			Object obj = clazz.newInstance();
			return obj;
		}
		catch(Exception e) {
			logger.debug("Failed to create obj with constructor");
			
			try {
				Method m = clazz.getMethod("getInstance");
				if(!m.isAccessible()) {
					m.setAccessible(true);
				}
				
				return m.invoke(null);
			} catch (Exception e1) {
				logger.debug("Failed to create obj with getInstance");
			}
		}
		
		logger.error("Failed to create obj for [" + clazz + "]");
		return null;
	}

	@Override
	public void created(JavaClass javaClass) {
		if(javaClass != null) {
			Object obj = this.newInstance(javaClass.clazz);
			if(obj == null) {
				logger.warn("Failed to create java object for class [" + javaClass.name + "]");
				return;
			}
			
			if(obj instanceof Scriptable) {
				Scriptable scriptable = (Scriptable) obj;
				JavaFile file = JavaFileManager.getInstance().getJavaFile(javaClass.id, false);
				if(file != null && file.script != null) {
					Script script = ScriptManager.getInstance().eval(file.script);
					scriptable.setScript(script);
				}
			}
			
			JavaObject javaObject = new JavaObject();
			javaObject.id = javaClass.id;
			String name = "obj#" + javaObject.id;
			if(obj instanceof ActiveObject) {
				name = ((ActiveObject)obj).getName();
			}
			javaObject.name = name;
			javaObject.object = obj;
			javaObject = this.loadJavaObject(javaObject);
			
			this.objects.put(javaObject.id, javaObject);
			this.cls2ObjMap.put(javaClass.name, javaObject);
			this.namedObjects.put(javaObject.name, javaObject);
			
			for(JavaObjectListener listener : this.listeners) {
				listener.created(javaObject);
			}
		}
	}

	@Override
	public void removed(JavaClass javaClass) {
		if(javaClass != null) {
			JavaObject javaObject = this.objects.remove(javaClass.id);
			
			if(javaObject != null) {
				this.cls2ObjMap.remove(javaClass.name);
				this.namedObjects.remove(javaObject.name);
			}
			
			for(JavaObjectListener listener : this.listeners) {
				listener.removed(javaObject);
			}
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
	
	public List<JavaObjectInfo> getJavaObjectInfos() {
		List<JavaObjectInfo> infos = new ArrayList<JavaObjectInfo>();
		for(JavaObject obj : this.getJavaObjects()) {
			JavaObjectInfo info = new JavaObjectInfo();
			info.id = obj.id;
			info.name = obj.name;
			info.interfaces = obj.interfaces;
			info.className = obj.object.getClass().getCanonicalName();
			infos.add(info);
		}
		
		return infos;
	}
	
	public JavaObject getJavaObject(int id) {
		JavaObject javaObject = this.objects.get(id);
		if(javaObject != null) {
			javaObject = this.loadJavaObject(javaObject);
		}
		
		return javaObject;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JavaObject loadJavaObject(JavaObject javaObject) {
		if(javaObject == null) {
			return null;
		}
		
		Class clazz = javaObject.object.getClass();
		List<Class> interfaces = ClassUtils.getAllInterfaces(clazz);
		StringBuilder sb = new StringBuilder();
		for(Class itf : interfaces) {
			sb.append(itf.getCanonicalName()).append(" ");
		}
		javaObject.interfaces = sb.toString();
		
		return javaObject;
	}
	
	public JavaObject getJavaObject(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		JavaObject javaObject = this.namedObjects.get(name);
		if(javaObject != null) {
			javaObject = this.loadJavaObject(javaObject);
		}
		
		return javaObject;
	}
	
	public JavaObject getJavaObjectByClassName(String className) {
		if(StringUtils.isBlank(className)) {
			return null;
		}
		
		JavaObject javaObject = this.cls2ObjMap.get(className);
		if(javaObject != null) {
			javaObject = this.loadJavaObject(javaObject);
		}
		
		return javaObject;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public JavaObject getJavaObjectOfClass(Class clazz) {
		List<JavaObject> javaObjects = this.getJavaObjectsOfClass(clazz);
		if(javaObjects.isEmpty()) {
			return null;
		}
		else {
			return javaObjects.get(0);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<JavaObject> getJavaObjectsOfClass(Class clazz) {
		if(clazz == null) {
			return null;
		}
		
		List<JavaObject> javaObjects = new ArrayList<JavaObject>();
		for(JavaObject javaObject : this.objects.values()) {
			if(clazz.isAssignableFrom(javaObject.object.getClass())) {
				javaObject = this.loadJavaObject(javaObject);
				javaObjects.add(javaObject);
			}
		}
		
		return javaObjects;
	}
}
