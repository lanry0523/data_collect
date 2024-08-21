package com.iwhalecloud.data.collect.exception;

public class BikeNotExistException extends Exception{

    public BikeNotExistException() {
        super();
    }

    public BikeNotExistException(String message) {
        super(message);
    }

    public BikeNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public BikeNotExistException(Throwable cause) {
        super(cause);
    }

    protected BikeNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
