package org.wilson.world.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassUtils {
	public static List<Class<?>> getAllInterfaces(Class<?> clazz) {
		if(clazz == null) {
			return Collections.emptyList();
		}
		
		List<Class<?>> interfaces = new ArrayList<Class<?>>();
		for(Class<?> itf : clazz.getInterfaces()) {
			if(!interfaces.contains(itf)) {
				interfaces.add(itf);
			}
		}
		
		Class<?> superClass = clazz.getSuperclass();
		if(superClass != null) {
			List<Class<?>> itfs = getAllInterfaces(superClass);
			for(Class<?> itf : itfs) {
				if(!interfaces.contains(itf)) {
					interfaces.add(itf);
				}
			}
		}
		
		return interfaces;
	}
	
	public static List<Method> getAllMethods(Class<?> clazz) {
		List<Method> methods = new ArrayList<Method>();
		if(clazz == null) {
			return methods;
		}
		
		for(Method method : clazz.getDeclaredMethods()) {
			methods.add(method);
		}
		
		Class<?> superClass = clazz.getSuperclass();
		if(superClass != null) {
			methods.addAll(getAllMethods(superClass));
		}
		
		return methods;
	}
}
