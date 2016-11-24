package org.wilson.world.util;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.java.JavaObject;
import org.wilson.world.manager.JavaObjectManager;

public class ObjectUtils {
	public static String detectType(Object obj) {
		if(obj == null) {
			return null;
		}
		
		if(obj instanceof String) {
			String str = (String)obj;
			VarType type = detectType(str);
			return type.name();
		}
		else {
			return obj.getClass().getCanonicalName();
		}
	}
	
	/**
	 * Detect type from str, such as "1", "msg", "true"
	 * 
	 * @param str
	 * @return
	 */
	public static VarType detectType(String str) {
		if("true".equals(str) || "false".equals(str)) {
			return VarType.Boolean;
		}
		else if(isInteger(str)) {
			return VarType.Integer;
		}
		else {
			return VarType.String;
		}
	}
	
	private static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Object newInstance(String className) {
		if(StringUtils.isBlank(className)) {
			return null;
		}
		
		Object ret = null;
		try {
			Class clazz = Class.forName(className);
			ret = clazz.newInstance();
		}
		catch(Exception e) {
		}
		if(ret != null) {
			return ret;
		}
		
		JavaObject javaObject = JavaObjectManager.getInstance().getJavaObjectByClassName(className);
		if(javaObject != null) {
			ret = javaObject.object;
		}
		
		return ret;
	}
}
