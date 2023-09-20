package ru.practicum.exception;

public class ObjectNotAvailableException extends RuntimeException {
    public ObjectNotAvailableException(String message) {
        super(message);
    }
}
