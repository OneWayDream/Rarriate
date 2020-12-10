package ru.itis.exceptions;

public class TCPFrameFactoryException extends Exception {

    public TCPFrameFactoryException() {
    }

    public TCPFrameFactoryException(String message) {
        super(message);
    }

    public TCPFrameFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public TCPFrameFactoryException(Throwable cause) {
        super(cause);
    }

    public TCPFrameFactoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
