package ru.matthew.NauJava.domain.password.exception;

public class PasswordEntryDecodeException extends RuntimeException {
    public PasswordEntryDecodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordEntryDecodeException(String message) {
        super(message);
    }
}
