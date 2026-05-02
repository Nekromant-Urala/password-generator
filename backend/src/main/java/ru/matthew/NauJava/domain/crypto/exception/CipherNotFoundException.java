package ru.matthew.NauJava.domain.crypto.exception;

public class CipherNotFoundException extends RuntimeException {
    public CipherNotFoundException(String message) {
        super(message);
    }
}
