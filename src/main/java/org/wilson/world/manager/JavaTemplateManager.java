package org.wilson.world.manager;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.java.JavaExtensionPoint;
import org.wilson.world.java.JavaTemplate;
import org.wilson.world.lifecycle.ManagerLifecycle;

public class JavaTemplateManager implements ManagerLifecycle {
	private static final Logger logger = Logger.getLogger(JavaTemplateManager.class);
	
	private static JavaTemplateManager instance;
	
	private Map<String, JavaTemplate> templates = new HashMap<String, JavaTemplate>();
	
	private JavaTemplateManager() {
	}
	
	private void loadJavaTemplates() {
		logger.info("Load java templates...");
		
		List<JavaExtensionPoint> eps = ExtManager.getInstance().getJavaExtensionPoints();
		for(JavaExtensionPoint ep : eps) {
			JavaTemplate template = new JavaTemplate();
			template.name = ep.name;
			template.description = ep.description;
			Class<?> clazz = ep.clazz;
			String className = clazz.getCanonicalName();
			if(clazz.isInterface()) {
				template.interfaceClassNames.add(className);
			}
			else if(Modifier.isAbstract(clazz.getModifiers())) {
				template.parentClassName = className;
			}
			else {
				logger.warn("Java extension point should be either interface or abstract class for [" + className + "]");
				continue;
			}
			
			template.packageName = this.generateExtPackageName(className);
			//wait to be filled by user input
			template.className = null;
			//used to test the code
			template.hasMainMethod = true;
			
			this.templates.put(template.name, template);
		}
	}
	
	private String generateExtPackageName(String className) {
		String extClassName = className;
		int pos1 = extClassName.indexOf(".");
		int pos2 = extClassName.lastIndexOf(".");
		extClassName = "ext" + extClassName.substring(pos1, pos2);
		return extClassName;
	}
	
	public static JavaTemplateManager getInstance() {
		if(instance == null) {
			instance = new JavaTemplateManager();
		}
		
		return instance;
	}
	
	public List<JavaTemplate> getJavaTemplates() {
		return new ArrayList<JavaTemplate>(this.templates.values());
	}
	
	public JavaTemplate getJavaTemplate(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.templates.get(name);
	}
	
	private String getImportedPackageName(Class<?> clazz) {
		if(clazz == null) {
			return null;
		}
		
		if(clazz.isPrimitive()) {
			return null;
		}
		else if(clazz.isArray()) {
			clazz = clazz.getComponentType();
			return this.getImportedPackageName(clazz);
		}
		else {
			String packageName = clazz.getPackage().getName();
			if(packageName.equals("java.lang")) {
				return null;
			}
			else {
				return packageName;
			}
		}
	}
	
	/**
	 * Generate java code
	 * 
	 * @param template
	 * @return
	 */
	public String generateCode(JavaTemplate template) {
		if(template == null) {
			return null;
		}
		
		//analyze the class
		List<String> importPackageNames = new ArrayList<String>();
		List<Class<?>> superClasses = new ArrayList<Class<?>>();
		List<Method> methods = new ArrayList<Method>();
		Class<?> parentClass = null;
		List<Class<?>> interfaceClasses = new ArrayList<Class<?>>();
		
		if(StringUtils.isNotBlank(template.parentClassName)) {
			try {
				Class<?> clazz = Class.forName(template.parentClassName);
				parentClass = clazz;
				superClasses.add(parentClass);
			}
			catch(Exception e) {
				logger.warn("Failed to load class [" + template.parentClassName + "]");
			}
		}
		
		if(!template.interfaceClassNames.isEmpty()) {
			for(String interfaceClassName : template.interfaceClassNames) {
				try {
					Class<?> clazz = Class.forName(interfaceClassName);
					interfaceClasses.add(clazz);
					superClasses.add(clazz);
				}
				catch(Exception e) {
					logger.warn("Failed to load class [" + interfaceClassName + "]");
				}
			}
		}
		
		for(Class<?> superClass : superClasses) {
			String packageName = this.getImportedPackageName(superClass);
			if(packageName != null && !importPackageNames.contains(packageName)) {
				importPackageNames.add(packageName);
			}
			
			Method [] superClassMethods = superClass.getMethods();
			for(Method superClassMethod : superClassMethods) {
				if(Modifier.isAbstract(superClassMethod.getModifiers())) {
					methods.add(superClassMethod);
				}
				
				Class<?> [] parameterTypes = superClassMethod.getParameterTypes();
				for(Class<?> parameterType : parameterTypes) {
					packageName = this.getImportedPackageName(parameterType);
					if(packageName != null && !importPackageNames.contains(packageName)) {
						importPackageNames.add(packageName);
					}
				}
				
				Class<?> [] exceptionTypes = superClassMethod.getExceptionTypes();
				for(Class<?> exceptionType : exceptionTypes) {
					packageName = this.getImportedPackageName(exceptionType);
					if(packageName != null && !importPackageNames.contains(packageName)) {
						importPackageNames.add(packageName);
					}
				}
				
				Class<?> returnType = superClassMethod.getReturnType();
				packageName = this.getImportedPackageName(returnType);
				if(packageName != null && !importPackageNames.contains(packageName)) {
					importPackageNames.add(packageName);
				}
			}
		}
		
		StringBuilder sb = new StringBuilder();
		
		//generate package
		sb.append("package ").append(template.packageName).append(";\n");
		sb.append("\n");
		
		//generate imports
		for(String importPackageName : importPackageNames) {
			sb.append("import ").append(importPackageName).append(".*;\n");
		}
		sb.append("\n");
		
		//generate comment
		sb.append("/**\n");
		sb.append("* ").append(template.description).append("\n");
		sb.append("*/\n");
		
		//generate class begin
		String className = template.className;
		if(StringUtils.isBlank(className)) {
			className = parentClass.getSimpleName() + "Impl";
		}
		sb.append("public class ").append(className);
		if(parentClass != null) {
			sb.append(" extends ").append(parentClass.getSimpleName());
		}
		if(!interfaceClasses.isEmpty()) {
			sb.append(" implements ");
			for(int i = 0; i < interfaceClasses.size(); i++) {
				sb.append(interfaceClasses.get(i).getSimpleName());
				if(i != interfaceClasses.size() - 1) {
					sb.append(",");
				}
			}
		}
		sb.append(" {\n");
		sb.append("\n");
		
		//generate unimplemented methods
		for(Method method : methods) {
			sb.append("    ");
			String modifiers = this.getModifiers(method);
			if(modifiers != null) {
				sb.append(modifiers);
			}
			
			Class<?> returnType = method.getReturnType();
			String returnTypeClassName = this.getClassName(returnType);
			sb.append(" ").append(returnTypeClassName);
			
			sb.append(" ").append(method.getName());
			
			sb.append("(");
			Class<?> [] parameterTypes = method.getParameterTypes();
			for(int i = 0; i < parameterTypes.length; i++) {
				Class<?> parameterType = parameterTypes[i];
				String parameterTypeClassName = this.getClassName(parameterType);
				sb.append(parameterTypeClassName).append(" p").append(i);
				if(i != parameterTypes.length - 1) {
					sb.append(", ");
				}
			}
			sb.append(")");
			
			Class<?> [] exceptionTypes = method.getExceptionTypes();
			if(exceptionTypes != null && exceptionTypes.length > 0) {
				sb.append(" throws ");
				for(int i = 0; i < exceptionTypes.length; i++) {
					sb.append(exceptionTypes[i].getName());
					if(i != exceptionTypes.length - 1) {
						sb.append(",");
					}
				}
			}
			
			sb.append(" {\n");
			
			if(void.class != returnType) {
				String defaultValue = this.getDefaultValueAsString(returnType);
				sb.append("        return ").append(defaultValue).append(";\n");
			}
			
			sb.append("    }\n");
			sb.append("\n");
		}
		
		//generate main method
		if(template.hasMainMethod) {
			sb.append("    public static void main(String [] args) {\n");
			sb.append("        System.out.println(\"Hello World\");\n");
			sb.append("    }\n");
			sb.append("\n");
		}
		
		//generate class end
		sb.append("}\n");
		
		return sb.toString();
	}
	
	private String getClassName(Class<?> clazz) {
		if(clazz == null) {
			return null;
		}
		
		if(clazz.isArray()) {
			clazz = clazz.getComponentType();
			return this.getClassName(clazz) + " []";
		}
		else {
			return clazz.getSimpleName();
		}
	}
	
	private String getModifiers(Method method) {
		if(method == null) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		
		int modifiers = method.getModifiers();
		if(Modifier.isPublic(modifiers)) {
			sb.append("public");
		}
		else if(Modifier.isProtected(modifiers)) {
			sb.append("protected");
		}
		else if(Modifier.isPrivate(modifiers)) {
			sb.append("private");
		}
		
		if(Modifier.isStatic(modifiers)) {
			sb.append(" static");
		}
		
		if(Modifier.isSynchronized(modifiers)) {
			sb.append(" synchronized");
		}
		
		return sb.toString();
	}
	
	public String toClassName(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		boolean started = false;
		List<Character> chars = new ArrayList<Character>(name.length());
		for(int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if(!started) {
				if(Character.isJavaIdentifierStart(c)) {
					c = Character.toUpperCase(c);
					chars.add(c);
					started = true;
				}
			}
			else {
				if(Character.isJavaIdentifierPart(c)) {
					chars.add(c);
				}
			}
		}
		
		if(chars.isEmpty()) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		for(char c : chars) {
			sb.append(c);
		}
		
		return sb.toString();
	}

	@Override
	public void start() {
		this.loadJavaTemplates();
	}

	@Override
	public void shutdown() {
	}
	
	private String getDefaultValueAsString(Class<?> clazz) {
		if(clazz == null) {
			return null;
		}
		
		if(byte.class == clazz) {
			return "0";
		}
		else if(short.class == clazz) {
			return "0";
		}
		else if(int.class == clazz) {
			return "0";
		}
		else if(long.class == clazz) {
			return "0";
		}
		else if(float.class == clazz) {
			return "0";
		}
		else if(double.class == clazz) {
			return "0";
		}
		else if(boolean.class == clazz) {
			return "false";
		}
		else if(char.class == clazz) {
			return "0";
		}
		else {
			return "null";
		}
	}
}
