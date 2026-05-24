package ru.matthew.NauJava.domain.audit.exception;

public class NotFoundAuditEventException extends RuntimeException {
    public NotFoundAuditEventException(String message) {
        super(message);
    }
}
