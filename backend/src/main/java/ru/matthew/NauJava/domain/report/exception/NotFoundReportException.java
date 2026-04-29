package ru.matthew.NauJava.domain.report.exception;

public class NotFoundReportException extends RuntimeException {
    public NotFoundReportException(String message) {
        super(message);
    }
}
