package ru.itis.exceptions;

import lombok.Getter;

public class IncorrectFCSException extends Exception {

    protected Object messageID;

    public IncorrectFCSException() {
    }

    public IncorrectFCSException(Object messageID){
        this.messageID = messageID;
    }

    public IncorrectFCSException(String message) {
        super(message);
    }

    public IncorrectFCSException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectFCSException(Throwable cause) {
        super(cause);
    }

    public IncorrectFCSException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public Object getMessageID(){
        return messageID;
    }
}
