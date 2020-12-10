package ru.itis.exceptions;

public class ImageFileNotFoundException extends Exception{
    public ImageFileNotFoundException() {
    }

    public ImageFileNotFoundException(String message) {
        super(message);
    }

    public ImageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageFileNotFoundException(Throwable cause) {
        super(cause);
    }

    public ImageFileNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
