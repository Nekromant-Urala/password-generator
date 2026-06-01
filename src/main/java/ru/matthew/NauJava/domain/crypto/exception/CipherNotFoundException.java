package ru.matthew.NauJava.domain.crypto.exception;

public class CipherNotFoundException extends RuntimeException {
    public CipherNotFoundException(String message) {
        super(message);
    }

    public CipherNotFoundException(Throwable cause) {
        super(cause);
    }

    public CipherNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
