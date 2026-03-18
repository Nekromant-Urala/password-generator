package ru.matthew.NauJava.exception;

public class PasswordEntryNotFoundException extends RuntimeException {
    public PasswordEntryNotFoundException(String message) {
        super(message);
    }
}
