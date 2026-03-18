package ru.matthew.NauJava.exception;

public class ServiceDataNotFoundException extends RuntimeException {
    public ServiceDataNotFoundException(String message) {
        super(message);
    }
}
