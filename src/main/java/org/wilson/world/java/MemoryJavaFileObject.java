package org.wilson.world.java;

import java.io.IOException;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class MemoryJavaFileObject extends SimpleJavaFileObject {
    private String contents = null;

    public MemoryJavaFileObject(String className, String contents) throws Exception {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.contents = contents;
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return contents;
    }
}