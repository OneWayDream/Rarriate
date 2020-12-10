package ru.itis.exceptions;

public class UDPFrameFactoryException extends Exception {

    public UDPFrameFactoryException() {
    }

    public UDPFrameFactoryException(String message) {
        super(message);
    }

    public UDPFrameFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public UDPFrameFactoryException(Throwable cause) {
        super(cause);
    }

    public UDPFrameFactoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
