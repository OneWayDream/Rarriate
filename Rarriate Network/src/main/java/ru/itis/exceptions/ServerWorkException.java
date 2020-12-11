package ru.itis.exceptions;

public class ServerWorkException extends IllegalStateException {

    public ServerWorkException() {
    }

    public ServerWorkException(String message) {
        super(message);
    }

    public ServerWorkException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerWorkException(Throwable cause) {
        super(cause);
    }
}
