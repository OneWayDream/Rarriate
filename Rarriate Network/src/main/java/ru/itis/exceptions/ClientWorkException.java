package ru.itis.exceptions;

public class ClientWorkException extends IllegalStateException {
    public ClientWorkException() {
    }

    public ClientWorkException(String s) {
        super(s);
    }

    public ClientWorkException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientWorkException(Throwable cause) {
        super(cause);
    }
}
