package ru.clevertec.sessioninject.exception;

public class CreateObjectException extends RuntimeException{
    public CreateObjectException(String message) {
        super(message);
    }

    public CreateObjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
