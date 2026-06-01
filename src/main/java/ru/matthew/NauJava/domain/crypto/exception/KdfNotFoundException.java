package ru.matthew.NauJava.domain.crypto.exception;

public class KdfNotFoundException extends RuntimeException {
    public KdfNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public KdfNotFoundException(String message) {
        super(message);
    }
}
