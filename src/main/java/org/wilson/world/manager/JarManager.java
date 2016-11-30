package org.wilson.world.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.java.Jar;

public class JarManager {
	public static final String DIR = "jars/";
	
	private static JarManager instance;
	
	private static int GLOBAL_ID = 1;
	
	private Map<Integer, Jar> jars = new HashMap<Integer, Jar>();
	
	private JarManager() {
		this.ensureJarDir();
		
		this.loadJars();
	}
	
	private void ensureJarDir() {
		File dir = new File(this.getJarDir());
		if(!dir.exists()) {
			dir.mkdirs();
		}
	}
	
	private void loadJars() {
		File dir = new File(this.getJarDir());
		for(File jarFile : dir.listFiles()) {
			this.loadJar(jarFile);
		}
	}
	
	private void loadJar(File jarFile) {
		if(jarFile == null || !jarFile.isFile() || !jarFile.getName().endsWith(".jar")) {
			return;
		}
		
		this.addJar(jarFile.getName());
	}
	
	public static JarManager getInstance() {
		if(instance == null) {
			instance = new JarManager();
		}
		
		return instance;
	}
	
	public String getJarDir() {
		return ConfigManager.getInstance().getDataDir() + DIR;
	}
	
	public void addJar(String name) {
		if(StringUtils.isBlank(name)) {
			return;
		}
		
		File jarFile = new File(this.getJarDir() + name);
		if(!jarFile.exists()) {
			return;
		}
		
		Jar jar = new Jar();
		jar.id = GLOBAL_ID++;
		jar.name = name;
		this.jars.put(jar.id, jar);
	}
	
	public Jar getJar(int id) {
		return this.jars.get(id);
	}
	
	public Jar getJar(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		for(Jar jar : this.jars.values()) {
			if(name.equals(jar.name)) {
				return jar;
			}
		}
		
		return null;
	}
	
	public List<Jar> getJars() {
		return new ArrayList<Jar>(this.jars.values());
	}
	
	public void deleteJar(int id) {
		Jar jar = this.getJar(id);
		if(jar == null) {
			return;
		}
		
		File jarFile = new File(this.getJarPath(jar));
		if(jarFile.exists()) {
			jarFile.delete();
		}
		
		this.jars.remove(id);
	}
	
	public String getJarPath(Jar jar) {
		if(jar == null) {
			return null;
		}
		
		return this.getJarDir() + jar.name;
	}
}
