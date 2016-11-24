package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.java.JavaClass;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.JavaFile;

public class JavaClassManager implements ManagerLifecycle {
	private static Logger logger = Logger.getLogger(JavaClassManager.class);
	
	private static JavaClassManager instance;
	
	private Map<Integer, JavaClass> classes = new HashMap<Integer, JavaClass>();
	
	private JavaClassManager() {
	}
	
	public static JavaClassManager getInstance() {
		if(instance == null) {
			instance = new JavaClassManager();
		}
		
		return instance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start() {
		Cache<Integer, JavaFile> cache = ((CachedDAO<JavaFile>)JavaFileManager.getInstance().getDAO()).getCache();
		cache.addCacheListener(new CacheListener<JavaFile>(){

			@SuppressWarnings("rawtypes")
			@Override
			public void cachePut(JavaFile old, JavaFile v) {
				if(old != null) {
					cacheDeleted(old);
				}
				
				String className = JavaFileManager.getInstance().getClassName(v);
				if(className != null) {
					if(!JavaManager.getInstance().hasClass(className)) {
						JavaManager.getInstance().compile(v.source, false);
					}
					try {
						Class clazz = JavaManager.getInstance().loadClass(className);
						if(clazz != null) {
							JavaClass javaClass = new JavaClass();
							javaClass.id = v.id;
							javaClass.name = className;
							javaClass.clazz = clazz;
							classes.put(javaClass.id, javaClass);
						}
					}
					catch(Exception e) {
						logger.error(e);
					}
				}
				else {
					logger.warn("Failed to find class name for java file [" + v.name + "]");
				}
			}

			@Override
			public void cacheDeleted(JavaFile v) {
				classes.remove(v.id);
			}

			@Override
			public void cacheLoaded(List<JavaFile> all) {
				for(JavaFile file : all) {
					cachePut(null, file);
				}
			}

			@Override
			public void cacheLoading(List<JavaFile> old) {
				classes.clear();
			}
			
		});
		
		cache.notifyLoaded();
	}

	@Override
	public void shutdown() {
	}
	
	public JavaClass getJavaClass(int id) {
		return this.classes.get(id);
	}
	
	public List<JavaClass> getJavaClasses() {
		return new ArrayList<JavaClass>(this.classes.values());
	}
	
	public JavaClass getJavaClass(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		for(JavaClass javaClass : this.classes.values()) {
			if(name.equals(javaClass.name)) {
				return javaClass;
			}
		}
		
		return null;
	}
}
