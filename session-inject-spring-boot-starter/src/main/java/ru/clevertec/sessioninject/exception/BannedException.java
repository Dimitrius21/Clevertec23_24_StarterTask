package ru.clevertec.sessioninject.exception;

public class BannedException extends RuntimeException{
    public BannedException(String message) {
        super(message);
    }

    public BannedException(String message, Throwable cause) {
        super(message, cause);
    }
}
