package org.wilson.world.exception;

public class DataException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DataException(String msg) {
        super(msg);
    }
}
