package org.wilson.world.manager;

import java.io.InputStream;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class ConsoleManager {
    private static final Logger logger = Logger.getLogger(ConsoleManager.class);
    
    private static ConsoleManager instance;
    
    private ConsoleManager() {}
    
    public static ConsoleManager getInstance() {
        if(instance == null) {
            instance = new ConsoleManager();
        }
        return instance;
    }
    
    public String run(String cmd) {
        if(StringUtils.isBlank(cmd)) {
            return null;
        }
        
        Scanner s = null;
        try {
            InputStream is = Runtime.getRuntime().exec(cmd).getInputStream();
            s = new Scanner(is);
            s.useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
        catch(Exception e) {
            logger.error("failed to run command!", e);
            return e.getMessage();
        }
        finally {
            if(s != null) {
                s.close();
            }
        }
    }
}
