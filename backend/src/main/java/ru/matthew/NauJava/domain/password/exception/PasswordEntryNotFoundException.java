package ru.matthew.NauJava.domain.password.exception;

public class PasswordEntryNotFoundException extends RuntimeException {
    public PasswordEntryNotFoundException(String message) {
        super(message);
    }
}
