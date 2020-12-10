package ru.itis.exceptions;

public class AlreadyRegisteredNameException extends Exception {

    public AlreadyRegisteredNameException() {
    }

    public AlreadyRegisteredNameException(String message) {
        super(message);
    }

    public AlreadyRegisteredNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyRegisteredNameException(Throwable cause) {
        super(cause);
    }

    public AlreadyRegisteredNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
