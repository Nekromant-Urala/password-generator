package ru.matthew.NauJava.repository.exception;

public class PasswordEntryNotFoundException extends RuntimeException {
    public PasswordEntryNotFoundException(String message) {
        super(message);
    }
}
