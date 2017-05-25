package org.wilson.world.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.java.Jar;
import org.wilson.world.java.JavaFileClassLoader;
import org.wilson.world.java.MemoryDiagnosticListener;
import org.wilson.world.java.MemoryJavaFileObject;
import org.wilson.world.java.RunJavaInfo;
import org.wilson.world.util.ExceptionUtils;
import org.wilson.world.util.FileUtils;
import org.wilson.world.util.IOUtils;

public class JavaManager {
    private static final Logger logger = Logger.getLogger(JavaManager.class);
    
    private static JavaManager instance;
    
    private String defaultContent = null;
    
    private ClassLoader classLoader = null;
    
    private JavaManager() {
        this.classLoader = newClassLoader();
    }
    
    private ClassLoader newClassLoader() {
    	try {
        	String classesDir = this.getJavaClassesDir();
            File file = new File(classesDir);
            URL url = file.toURI().toURL();
            List<URL> urls = new ArrayList<URL>();
            urls.add(url);
			for(File jarFile : this.getJarFiles()) {
				urls.add(jarFile.toURI().toURL());
			}

            return new URLClassLoader(urls.toArray(new URL[urls.size()]), new JavaFileClassLoader(this.getClass().getClassLoader()));
        }
        catch(Exception e) {
        	logger.error(e);
        }
    	
    	return null;
    }
    
    public static JavaManager getInstance() {
        if(instance == null) {
            instance = new JavaManager();
        }
        
        return instance;
    }
    
    public String getJavaClassesDir() {
        return ConfigManager.getInstance().getDataDir() + "classes";
    }
    
    public ClassLoader getClassLoader() {
    	return this.classLoader;
    }
    
    public String getClassName(String source) {
        Pattern p = Pattern.compile("package (.*);");
        Matcher matcher = p.matcher(source);
        if(!matcher.find()) {
            return null;
        }
        
        String packageName = matcher.group(1);
        
        p = Pattern.compile("public class (.*) \\{", Pattern.MULTILINE);
        matcher = p.matcher(source);
        if(!matcher.find()) {
            return null;
        }
        
        String className = matcher.group(1);
        className = className.split(" ")[0].trim();
        String fullName = packageName + "." + className;
        
        return fullName;
    }
    
    private JavaFileObject getJavaFileObject(String source, String className) {
        MemoryJavaFileObject obj = null;
        try {
            obj = new MemoryJavaFileObject(className, source);
        }
        catch(Exception e) {
            logger.error(e);
        }
        
        return obj;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private RunJavaInfo compile(Iterable files) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        RunJavaInfo info = new RunJavaInfo();
        MemoryDiagnosticListener listener = new MemoryDiagnosticListener();
        listener.setRunJavaInfo(info);
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(listener, Locale.ENGLISH, null);

        String classesDir = this.getJavaClassesDir();
        File dir = new File(classesDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        Iterable options = Arrays.asList("-d", classesDir, "-classpath", this.getClasspath());
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, listener, options, null, files);
        Boolean result = task.call();
        info.isSuccessful = result;
        return info;
    }
    
    private String getClasspath() {
    	StringBuilder cp = new StringBuilder();
    	cp.append(System.getProperty("java.class.path"));
    	
    	cp.append(File.pathSeparator).append(this.getJavaClassesDir());
    	
    	//get catalina/lib
    	String catalinaLibDir = this.getCatalinaLibDir();
    	if(catalinaLibDir != null) {
    		File catalinaLibDirFile = new File(catalinaLibDir);
    		for(File jarFile : catalinaLibDirFile.listFiles()) {
    			cp.append(File.pathSeparator).append(jarFile.getAbsolutePath());
    		}
    	}
    	
    	//get WEB-INF/lib
    	String libDir = this.getLibDir();
    	if(libDir != null) {
    		File libDirFile = new File(libDir);
    		for(File jarFile : libDirFile.listFiles()) {
    			cp.append(File.pathSeparator).append(jarFile.getAbsolutePath());
    		}
    	}
    	
    	//get WEB-INF/classes
    	String classesDir = this.getClassesDir();
    	if(classesDir != null) {
    		cp.append(File.pathSeparator).append(classesDir);
    	}
    	
    	//get external jars
    	List<File> jars = this.getJarFiles();
    	if(jars != null && !jars.isEmpty()) {
    		for(File jarFile : jars) {
    			cp.append(File.pathSeparator).append(jarFile.getAbsolutePath());
    		}
    	}
    	
    	return cp.toString();
    }
    
    private List<File> getJarFiles() {
    	List<File> files = new ArrayList<File>();
    	for(Jar jar : JarManager.getInstance().getJars()) {
    		File jarFile = new File(JarManager.getInstance().getJarPath(jar));
    		files.add(jarFile);
    	}
    	
    	return files;
    }
    
    private String getLibDir() {
    	String dir = this.getWebInfDir();
    	if(dir != null) {
    		return dir + "/lib";
    	}
    	else {
    		return null;
    	}
    }
    
    private String getClassesDir() {
    	String dir = this.getWebInfDir();
    	if(dir != null) {
    		return dir + "/classes";
    	}
    	else {
    		return null;
    	}
    }
    
    private String getCatalinaLibDir() {
    	String catalinaHome = System.getProperty("catalina.home");
    	if(catalinaHome == null) {
    		return null;
    	}
    	
    	return catalinaHome + "/lib";
    }
    
    private String getWebInfDir() {
    	String catalinaHome = System.getProperty("catalina.home");
    	if(catalinaHome == null) {
    		return null;
    	}
    	
    	//Presumption here that the app is installed in ROOT!
    	File dir = new File(catalinaHome + "/webapps/ROOT/WEB-INF");
    	if(dir.exists() && dir.isDirectory()) {
    		return dir.getAbsolutePath();
    	}
    	else {
    		dir = new File(catalinaHome + "/work/Catalina/localhost/_/WEB-INF");
    		if(dir.exists() && dir.isDirectory()) {
    			return dir.getAbsolutePath();
    		}
    		else {
    			return null;
    		}
    	}
    }
    
    @SuppressWarnings({ "rawtypes" })
    public Class loadClass(String className, boolean newClassLoader) throws Exception {
    	ClassLoader cl = newClassLoader ? this.newClassLoader() : this.classLoader;
        return cl.loadClass(className);
    }
    
    @SuppressWarnings("rawtypes")
	public Class loadClass(String className) throws Exception {
    	return this.loadClass(className, false);
    }
    
    public boolean hasClass(String className) {
    	if(StringUtils.isBlank(className)) {
    		return false;
    	}
    	
    	String classesDir = this.getJavaClassesDir();
    	String path = className.replace(".", "/");
    	String fileName = classesDir + "/" + path + ".class";
    	File file = new File(fileName);
    	return file.exists();
    }
    
    @SuppressWarnings({ "rawtypes",  "unchecked" })
    private RunJavaInfo runIt(String source, boolean newClassLoader) {
        RunJavaInfo info = new RunJavaInfo();
        try {
        	String className = this.getClassName(source);
        	if(StringUtils.isBlank(className)) {
        		info.isSuccessful = false;
        		info.message = "Failed to get class name";
        		return info;
        	}
        	
        	if(!this.hasClass(className)) {
        		info = this.compile(source, false);
        		if(!info.isSuccessful) {
        			return info;
        		}
        	}
        	
            Class thisClass = this.loadClass(className, newClassLoader);

            Class [] params = { String [].class };
            Object [] paramsObj = { new String[0] };
            Object instance = thisClass.newInstance();
            Method mainMethod = thisClass.getDeclaredMethod("main", params);

            PrintStream oldOut = System.out;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream newOut = new PrintStream(out);
            try {
                System.setOut(newOut);
                mainMethod.invoke(instance, paramsObj);
                newOut.flush();
                newOut.close();
            }
            finally {
                System.setOut(oldOut);
            }
            
            String log = out.toString();
            info.isSuccessful = true;
            info.log = log;
            return info;
        }
        catch(Exception e) {
            logger.error(e);
            info.isSuccessful = false;
            info.message = ExceptionUtils.toString(e);
        }
        
        return info;
    }
    
    public String validate(String source) {
        RunJavaInfo info = this.compile(source);
        if(!info.isSuccessful) {
            if(info.lineNumber == 0) {
                return info.message;
            }
            else {
                return info.message + " at line " + info.lineNumber;
            }
        }
        
        return null;
    }
    
    public RunJavaInfo compile(String source) {
        return this.compile(source, true);
    }

    public void clean(String source) {
        if(StringUtils.isBlank(source)) {
            return;
        }

        String className = this.getClassName(source);
        if(StringUtils.isBlank(className)) {
            return;
        }

        File file = this.getClassFile(className);
        if(file != null) {
            FileUtils.delete(file);
        }
    }
    
    public RunJavaInfo compile(String source, boolean clean) {
        RunJavaInfo info = new RunJavaInfo();
        if(StringUtils.isBlank(source)) {
            info.isSuccessful = false;
            info.message = "No source found to run";
            return info;
        }
        
        String className = this.getClassName(source);
        if(StringUtils.isBlank(className)) {
            info.isSuccessful = false;
            info.message = "No class found to run";
            return info;
        }
        
        JavaFileObject fileObj = this.getJavaFileObject(source, className);
        if(fileObj == null) {
            info.isSuccessful = false;
            info.message = "No java file found to run";
            return info;
        }
        
        if(clean) {
        	File file = this.getClassFile(className);
            FileUtils.delete(file);
        }
        
        info = this.compile(Arrays.asList(fileObj));
        info.className = className;
        return info;
    }
    
    public RunJavaInfo compile(List<String> sources) {
    	return this.compile(sources, true);
    }
    
    public RunJavaInfo compile(List<String> sources, boolean clean) {
        RunJavaInfo info = new RunJavaInfo();
        List<JavaFileObject> fileObjects = new ArrayList<JavaFileObject>();
        List<String> classNames = new ArrayList<String>();
        for(String source : sources) {
        	if(StringUtils.isBlank(source)) {
                info.isSuccessful = false;
                info.message = "No source found to run";
                return info;
            }
        	
            String className = this.getClassName(source);
            if(StringUtils.isBlank(className)) {
                info.isSuccessful = false;
                info.message = "No class found to run";
                return info;
            }
            
            JavaFileObject fileObj = this.getJavaFileObject(source, className);
            if(fileObj == null) {
                info.isSuccessful = false;
                info.className = className;
                info.message = "No java file found to run";
                return info;
            }
            
            fileObjects.add(fileObj);
            classNames.add(className);
        }
        
        if(clean) {
            for(String className : classNames) {
            	File file = new File(className);
            	FileUtils.delete(file);
            }
        }
        
        info = this.compile(fileObjects);
        return info;
    }
    
    public RunJavaInfo run(String source, boolean clean, boolean forceCompile, boolean newClassLoader) {
    	RunJavaInfo info = null;
    	if(forceCompile) {
    		info = this.compile(source, clean);
            if(!info.isSuccessful) {
                return info;
            }
    	}
        
        info = this.runIt(source, newClassLoader);
        
        return info;
    }
    
    public RunJavaInfo run(String source, boolean clean, boolean forceCompile) {
    	return this.run(source, clean, forceCompile, false);
    }

    public RunJavaInfo run(String source, boolean clean) {
        return run(source, clean, true);
    }
    
    public RunJavaInfo run(String source) {
        return run(source, true);
    }
    
    public String getDefaultJavaContent() throws IOException {
        if(defaultContent == null) {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("Demo.java");
            defaultContent = IOUtils.toString(is);
        }
        return this.defaultContent;
    }
    
    public File getClassFile(String className) {
    	if(StringUtils.isBlank(className)) {
    		return null;
    	}
    	
    	String file = className.replace('.', File.separatorChar) + ".class";
		file = JavaManager.getInstance().getJavaClassesDir() + File.separator + file;
		return new File(file);
    }

	/**
	 * Create an instance from the given type
	 * 
	 * @param type
	 * @return
	 */
	public Object createInstance(String type) throws Exception {
		if(StringUtils.isBlank(type)) {
			return null;
		}
		
		Class<?> clazz = this.getClassLoader().loadClass(type);
		if(clazz == null) {
			return null;
		}
		
		try {
			Method getInstance = clazz.getDeclaredMethod("getInstance");
			if(Modifier.isStatic(getInstance.getModifiers())) {
				if(!getInstance.isAccessible()) {
					getInstance.setAccessible(true);
				}
				return getInstance.invoke(null);
			}
		}
		catch(Exception e) {
		}
		
		return clazz.newInstance();
	}
}
