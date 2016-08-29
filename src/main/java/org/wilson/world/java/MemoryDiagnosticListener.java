package org.wilson.world.java;

import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

public class MemoryDiagnosticListener implements DiagnosticListener<JavaFileObject> {
    private RunJavaInfo info;
    
    public void setRunJavaInfo(RunJavaInfo info) {
        this.info = info;
    }
    
    public RunJavaInfo getRunJavaInfo() {
        return this.info;
    }
    
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        this.info.isSuccessful = false;
        this.info.lineNumber = diagnostic.getLineNumber();
        this.info.code = diagnostic.getCode();
        this.info.message = diagnostic.getMessage(Locale.ENGLISH);
    }
}