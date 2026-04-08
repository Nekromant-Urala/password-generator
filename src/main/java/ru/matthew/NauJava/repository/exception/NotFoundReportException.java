package ru.matthew.NauJava.repository.exception;

public class NotFoundReportException extends RuntimeException {
    public NotFoundReportException(String message) {
        super(message);
    }
}
