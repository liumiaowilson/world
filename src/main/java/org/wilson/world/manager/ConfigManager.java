package org.wilson.world.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.wilson.world.console.ConfigBackupHandler;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;
import org.wilson.world.util.ErrorAppender;

public class ConfigManager implements EventListener {
    private static final Logger logger = Logger.getLogger(ConfigManager.class);
    
    private static ConfigManager instance;
    
    public static final String CONFIG_OVERRIDE_FILE_NAME = "config.override.properties";
    private Properties props = null;
    
    private ErrorAppender appender = new ErrorAppender();
    
    private ConfigManager() {
        this.loadConfig();
        
        EventManager.getInstance().registerListener(EventType.ConfigOverrideUploaded, this);
        
        this.setProxy();
        
        BackupManager.getInstance().addBackupHandler(new ConfigBackupHandler());
    }
    
    private void setProxy() {
        String proxy_host = this.getConfig("system.proxy_host");
        String proxy_port = this.getConfig("system.proxy_port");
        if(!StringUtils.isBlank(proxy_host) && !StringUtils.isBlank(proxy_port)) {
            System.setProperty("http.proxyHost", proxy_host);
            System.setProperty("http.proxyPort", proxy_port);
        }
    }
    
    private void loadConfig() {
        try {
            props = new Properties();
            String url = null;
            if(isOpenShiftApp()) {
                url = "/world.openshift.properties";
            }
            else {
                url = "/world.dev.properties";
            }
            InputStream in = getClass().getResourceAsStream(url);
            props.load(in);
            in.close();
        }
        catch(Exception e) {
            logger.error("failed to load config file", e);
        }
        
        try {
            InputStream in = this.loadOverrideConfig();
            if(in != null) {
                props.load(in);
                in.close();
            }
        }
        catch(Exception e) {
            logger.error("failed to load config override", e);;
        }
        
        String loglevel = this.getConfig("log.level", "DEBUG");
        LogManager.getRootLogger().setLevel(Level.toLevel(loglevel));
        LogManager.getRootLogger().addAppender(this.appender);
    }
    
    public static ConfigManager getInstance() {
        if(instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    public boolean isOpenShiftApp() {
        String name = System.getenv("OPENSHIFT_GEAR_NAME");
        return name != null;
    }
    
    public List<String> getConfigNames() {
        List<String> names = new ArrayList<String>();
        for(String n : this.props.stringPropertyNames()) {
            names.add(n);
        }
        return names;
    }
    
    public String getConfig(String name, String defaultValue) {
        String value = this.props.getProperty(name);
        if(value == null) {
            value = defaultValue;
        }
        return value;
    }
    
    public String getConfig(String name) {
        return this.getConfig(name, null);
    }
    
    public int getConfigAsInt(String name, int defaultValue) {
        String value = this.getConfig(name);
        if(value == null) {
            return defaultValue;
        }
        int ret = defaultValue;
        try {
            ret = Integer.parseInt(value);
        }
        catch(Exception e) {
            logger.error(e);
        }
        return ret;
    }
    
    public int getConfigAsInt(String name) {
        return this.getConfigAsInt(name, 0);
    }
    
    public long getConfigAsLong(String name, long defaultValue) {
        String value = this.getConfig(name);
        if(value == null) {
            return defaultValue;
        }
        long ret = defaultValue;
        try {
            ret = Long.parseLong(value);
        }
        catch(Exception e) {
            logger.error(e);
        }
        return ret;
    }
    
    public long getConfigAsLong(String name) {
        return this.getConfigAsLong(name, 0);
    }
    
    public boolean getConfigAsBoolean(String name) {
        return this.getConfigAsBoolean(name, false);
    }
    
    public boolean getConfigAsBoolean(String name, boolean defaultValue) {
        String value = this.getConfig(name);
        if(value == null) {
            return defaultValue;
        }
        if("true".equalsIgnoreCase(value)) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public boolean isInDebugMode() {
        return this.getConfigAsBoolean("mode.debug");
    }
    
    public InputStream loadOverrideConfig() throws Exception {
        String path = this.getConfigOverrideFilePath();
        
        File file = new File(path);
        if(logger.isDebugEnabled()) {
            logger.debug("load override config from: " + file.getAbsolutePath());
        }
        if(!file.exists()) {
            return null;
        }
        
        return new FileInputStream(file);
    }
    
    public List<String> getOverrideConfigContent() {
        String path = this.getConfigOverrideFilePath();
        
        File file = new File(path);
        if(!file.exists()) {
            return null;
        }
        
        try {
            Path p = FileSystems.getDefault().getPath(path);
            List<String> lines = Files.readAllLines(p, Charset.defaultCharset());
            return lines;
        }
        catch(Exception e) {
            logger.error("failed to get override config content!", e);
            return null;
        }
    }
    
    public String getDataDir() {
        if(this.isOpenShiftApp()) {
            return System.getenv("OPENSHIFT_DATA_DIR");
        }
        else {
            return "";
        }
    }
    
    public String getTmpDataDir() {
    	String dir = this.getDataDir();
    	if(StringUtils.isNotBlank(dir)) {
    		return dir + "/tmp";
    	}
    	else {
    		return "tmp";
    	}
    }
    
    public String getUploadDataDir() {
    	String dir = this.getDataDir();
    	if(StringUtils.isNotBlank(dir)) {
    		return dir + "/upload";
    	}
    	else {
    		return "upload";
    	}
    }
    
    public String getConfigOverrideFilePath() {
        String path = null;
        if(this.isOpenShiftApp()) {
            path = System.getenv("OPENSHIFT_DATA_DIR") + CONFIG_OVERRIDE_FILE_NAME;
        }
        else {
            path = CONFIG_OVERRIDE_FILE_NAME;
        }
        return path;
    }

    @Override
    public void handle(Event event) {
        logger.info("Reload config");
        
        this.loadConfig();
    }
    
    public boolean isPreloadOnStartup() {
        return this.getConfigAsBoolean("startup.preload", true);
    }

    @Override
    public boolean isAsync() {
        return false;
    }
    
    public boolean isInMemoryMode() {
        return this.getConfigAsBoolean("dao.mode.isMemory", false);
    }
    
    public boolean isInOffLineMode() {
        return this.getConfigAsBoolean("system.mode.isOffLine", false);
    }
    
    public void setInMemoryModeTemporarily(boolean flag) {
        this.props.put("dao.mode.isMemory", String.valueOf(flag));
    }
    
    public void saveOverrideConfig(String content) {
        if(StringUtils.isBlank(content)) {
            return;
        }
        
        try {
            File file = new File(this.getConfigOverrideFilePath());
            file.delete();
            Files.write(Paths.get(this.getConfigOverrideFilePath()), content.getBytes(), StandardOpenOption.WRITE);
        } catch (IOException e) {
            logger.error(e);
        }
    }
    
    public TimeZone getUserDefaultTimeZone() {
        String tzStr = this.getConfig("user.default.timezone", "Asia/Shanghai");
        TimeZone tz = TimeZone.getTimeZone(tzStr);
        return tz;
    }
}
