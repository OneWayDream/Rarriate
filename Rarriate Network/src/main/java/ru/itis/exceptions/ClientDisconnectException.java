package ru.itis.exceptions;

import java.nio.channels.SelectionKey;

public class ClientDisconnectException extends Exception {

    protected SelectionKey selectionKey;

    public ClientDisconnectException() {
    }

    public ClientDisconnectException(SelectionKey selectionKey){
        this.selectionKey = selectionKey;
    }

    public ClientDisconnectException(String message) {
        super(message);
    }

    public ClientDisconnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientDisconnectException(Throwable cause) {
        super(cause);
    }

    public ClientDisconnectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SelectionKey getSelectionKey(){
        return selectionKey;
    }
}
