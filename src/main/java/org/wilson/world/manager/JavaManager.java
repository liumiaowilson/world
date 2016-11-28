package org.wilson.world.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
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
        this.classLoader = new JavaFileClassLoader(this.getClass().getClassLoader());
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
    	
    	return cp.toString();
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
    
    @SuppressWarnings({ "rawtypes", "resource" })
    public Class loadClass(String className) throws Exception {
        String classesDir = this.getJavaClassesDir();
        File file = new File(classesDir);
        URL url = file.toURI().toURL();
        URL[] urls = new URL[] { url };

        ClassLoader loader = new URLClassLoader(urls, this.getClassLoader());

        Class thisClass = loader.loadClass(className);
        
        return thisClass;
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
    private RunJavaInfo runIt(String source) {
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
        	
            Class thisClass = this.loadClass(className);

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
    
    public RunJavaInfo run(String source, boolean clean, boolean forceCompile) {
    	RunJavaInfo info = null;
    	if(forceCompile) {
    		info = this.compile(source, clean);
            if(!info.isSuccessful) {
                return info;
            }
    	}
        
        info = this.runIt(source);
        
        return info;
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
}
