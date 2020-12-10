package ru.itis.exceptions;

public class SoundFileNotFoundException extends Exception {
    public SoundFileNotFoundException() {
    }

    public SoundFileNotFoundException(String message) {
        super(message);
    }

    public SoundFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoundFileNotFoundException(Throwable cause) {
        super(cause);
    }

    public SoundFileNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
