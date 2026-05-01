package ru.matthew.NauJava.domain.password.exception;

public class PasswordEntryAlreadyExistsException extends RuntimeException {
    public PasswordEntryAlreadyExistsException(String message) {
        super(message);
    }
}
