package org.wilson.world.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ExceptionUtils {
    public static String toString(Throwable e) {
        if(e == null) {
            return null;
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        e.printStackTrace(ps);
        ps.flush();
        ps.close();
        
        return out.toString();
    }
}
