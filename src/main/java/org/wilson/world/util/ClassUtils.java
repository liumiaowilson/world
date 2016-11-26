package org.wilson.world.util;

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
}
