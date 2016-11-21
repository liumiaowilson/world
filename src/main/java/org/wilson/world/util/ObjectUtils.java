package org.wilson.world.util;

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
}
