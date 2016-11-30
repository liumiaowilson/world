package org.wilson.world.java;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.manager.JarManager;
import org.wilson.world.manager.JavaManager;

public class JavaFileClassLoader extends ClassLoader {
	private static final Logger logger = Logger.getLogger(JavaFileClassLoader.class);
	
	public JavaFileClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	private byte[] loadClassData(File file) throws IOException {
        // Opening the file
        InputStream stream = new FileInputStream(file);
        int size = stream.available();
        byte buff[] = new byte[size];
        DataInputStream in = new DataInputStream(stream);
        // Reading the binary data
        in.readFully(buff);
        in.close();
        
        return buff;
    }
	
	private Class<?> getClass(String name)  {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		File file = JavaManager.getInstance().getClassFile(name);
		if(!file.exists()) {
			return null;
		}
		
		byte [] b = null;
		try {
			b = this.loadClassData(file);
			Class<?> c = defineClass(name, b, 0, b.length);
			resolveClass(c);
			
			return c;
		}
		catch(Exception e) {
			logger.error(e);
		}
		
		return null;
	}
	
	private Class<?> loadClassFromExternalJars(String name) {
		URLClassLoader cl = null;
		try {
			List<URL> urls = new ArrayList<URL>();
			for(Jar jar : JarManager.getInstance().getJars()) {
				File jarFile = new File(JarManager.getInstance().getJarPath(jar));
				urls.add(jarFile.toURI().toURL());
			}
			
			cl = new URLClassLoader(urls.toArray(new URL[0]), this);
			return cl.loadClass(name);
		}
		catch(Exception e) {
			logger.error(e);
			return null;
		}
		finally {
			if(cl != null) {
				try {
					cl.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> clazz = this.getClass(name);
		if(clazz != null) {
			return clazz;
		}
		else {
			clazz = this.loadClassFromExternalJars(name);
			if(clazz != null) {
				return clazz;
			}
			
			return super.loadClass(name);
		}
	}
}
