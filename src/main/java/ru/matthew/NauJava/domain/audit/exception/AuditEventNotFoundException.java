package ru.matthew.NauJava.domain.audit.exception;

public class AuditEventNotFoundException extends RuntimeException {
    public AuditEventNotFoundException(String message) {
        super(message);
    }
}
