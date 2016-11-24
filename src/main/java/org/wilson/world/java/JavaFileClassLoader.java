package org.wilson.world.java;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.manager.JavaManager;

public class JavaFileClassLoader extends ClassLoader {
	private static final Logger logger = Logger.getLogger(JavaFileClassLoader.class);
	
	public JavaFileClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	private byte[] loadClassData(String name) throws IOException {
        // Opening the file
        InputStream stream = new FileInputStream(name);
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
		
		String file = name.replace('.', File.separatorChar) + ".class";
		file = JavaManager.getInstance().getJavaClassesDir() + File.separator + file;
		if(!new File(file).exists()) {
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

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> clazz = this.getClass(name);
		if(clazz != null) {
			return clazz;
		}
		else {
			return super.loadClass(name);
		}
	}
}
