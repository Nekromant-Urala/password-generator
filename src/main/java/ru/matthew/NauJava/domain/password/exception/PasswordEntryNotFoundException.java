package ru.matthew.NauJava.domain.password.exception;

public class PasswordEntryNotFoundException extends RuntimeException {

    private static final String STANDARD_MESSAGE = "Запись с таким id: '%d' не была найдена.";

    public PasswordEntryNotFoundException(long id) {
        super(STANDARD_MESSAGE.formatted(id));
    }

    public PasswordEntryNotFoundException(String message) {
        super(message);
    }
}
