package ru.itis.exceptions;

public class FrameException extends Exception {

    public FrameException() {
    }

    public FrameException(String message) {
        super(message);
    }

    public FrameException(String message, Throwable cause) {
        super(message, cause);
    }

    public FrameException(Throwable cause) {
        super(cause);
    }

    public FrameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
