package ru.clevertec.sessioninject.exception;

public class SessionNotAvailableException extends RuntimeException{
    public SessionNotAvailableException(String message) {
        super(message);
    }

    public SessionNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
