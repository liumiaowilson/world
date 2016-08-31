package org.wilson.world.java;

public class RunJavaInfo {
    public String className;
    
    public boolean isSuccessful;
    
    //below for success
    public String log;
    
    //below for failure
    public long lineNumber;
    
    public String code;
    
    public String message;
    
    public String getMessage() {
        if(this.isSuccessful) {
            return this.log;
        }
        else {
            if(this.lineNumber == 0) {
                return this.message + " from class [" + this.className + "]";
            }
            else {
                return this.message + " at line " + this.lineNumber + " from class [" + this.className + "]";
            }
        }
    }
}
